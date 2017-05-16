/**
****************************************************
*  Java based PDF creation and manipulation Library      
****************************************************
*
*  Project Title: Gnostice PDFOne Java
*  Copyright © 2002-2008 Gnostice Information Technologies Private Limited, Bangalore, India
*  http://www.gnostice.com
*
*  This file is part of PDFOne Java Library.
*
*  This program is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.

*  You should have received a copy of the GNU General Public License
*  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*
*/

package com.gnostice.pdfone.readers;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

import com.gnostice.pdfone.PdfByteOutputStream;
import com.gnostice.pdfone.PdfDict;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfInteger;
import com.gnostice.pdfone.PdfLong;
import com.gnostice.pdfone.PdfName;
import com.gnostice.pdfone.PdfNull;
import com.gnostice.pdfone.PdfStream;
import com.gnostice.pdfone.Usable;

final class TtfGenerator
{
    /* constants for composite glyf checking */
    private static final int ARG_1_AND_2_ARE_WORDS = 1;

    private static final int WE_HAVE_A_SCALE = 8;

    private static final int MORE_COMPONENTS = 32;

    private static final int WE_HAVE_AN_X_AND_Y_SCALE = 64;

    private static final int WE_HAVE_A_TWO_BY_TWO = 128;

    static final int entrySelectors[] = { 0, 0, 1, 1, 2, 2, 2, 2, 3,
        3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4 };
    
    private Hashtable glyphsUsed;
    
    private ArrayList usedGlyphsList;
    
    private long glyfTableOffset;
    
    private int[] locaTable;
    
    private int[] locaTableTemp;
    
    private PdfByteOutputStream locaTableSubset;
    
    private byte[] glyfTableSubset;
    
    private int initialGlyfTableSize;

    private int initialLocaTableSize;

    private RandomAccessFile raf;
    
    private PdfByteOutputStream pbos;
    
    private Hashtable tableInfo;
    
    private boolean isLocaFormatShort;
    
    private boolean isType0;
    
    private String[] requiredTables;
    
    private TtfGenerator(String filename, Hashtable glyphsUsed,
        Hashtable tableInfo, int[] locatable,
        boolean isLocaFormatShort, boolean isType0)
        throws IOException
    {
        this.glyphsUsed = glyphsUsed;
        this.tableInfo = tableInfo;
        this.locaTable = locatable;
        this.isLocaFormatShort = isLocaFormatShort;
        this.isType0 = isType0;
        this.usedGlyphsList = new ArrayList(glyphsUsed.keySet());
        this.raf = new RandomAccessFile(filename, "r");
        
        pbos = new PdfByteOutputStream();
    }
    
    private void initializeTables()
    {
        this.requiredTables = isType0 ? TtfReader.cidRequiredTables
            : TtfReader.ttfRequiredTables;
    }
    
    private PdfStream getStream(boolean isSubset) throws IOException,
        PdfException
    {
        initializeTables();
        
        try
        {
            if (isSubset)
            {
                locaTableSubset = new PdfByteOutputStream();
                readLocaTableSize();
                readGlyphs();
                createNewGlyfTable();
                createNewLocaTable();
                writeSubset();
            }
            else
            {
                writeFullFont();
            }

            HashMap hm = new HashMap();
            hm.put(new PdfName(Usable.PDF_LENGTH_1), new PdfLong(pbos
                .size()));
            ByteBuffer bb = ((ByteBuffer) ByteBuffer.wrap(
                pbos.getBuffer()).limit(pbos.size())).slice();

            return new PdfStream(new PdfDict(hm), bb);
        }
        finally
        {
            this.raf.close();
        }
    }
    
    private void createNewLocaTable()
    {
        if (isLocaFormatShort)
        {
            for (int i = 0; i < locaTableTemp.length; ++i)
            {
                locaTableSubset.writeShort(locaTableTemp[i] / 2);
            }
        }
        else
        {
            for (int i = 0; i < locaTableTemp.length; ++i)
            {
                locaTableSubset.writeInt(locaTableTemp[i]);
            }
        }
        int padCount = ((initialLocaTableSize + 3) & ( ~3))
            - initialLocaTableSize;
        for (int j = 0; j < padCount; ++j)
        {
            locaTableSubset.write(0);
        }
    }

    private void createNewGlyfTable() throws IOException
    {
        int sortedGlyfIndices[] = new int[usedGlyphsList.size()];
        locaTableTemp = new int[locaTable.length];
        for (int i = 0; i < sortedGlyfIndices.length; ++i)
        {
            sortedGlyfIndices[i] = ((PdfInteger) usedGlyphsList
                .get(i)).getInt();
        }
        Arrays.sort(sortedGlyfIndices);
        
        int noOfBytes = 0;
        for (int i = 0; i < sortedGlyfIndices.length; ++i) 
        {
            int glyphIndex = sortedGlyfIndices[i];
            noOfBytes += locaTable[glyphIndex + 1] - locaTable[glyphIndex];
        }
        
        initialGlyfTableSize = noOfBytes;
        noOfBytes = (noOfBytes + 3) & (~3);
        glyfTableSubset = new byte[noOfBytes];
        
        for (int i = 0, glyphTableIndex = 0, glyphListIndex = 0;
            i < locaTableTemp.length; ++i)
        {
            locaTableTemp[i] = glyphTableIndex;
            if (glyphListIndex < sortedGlyfIndices.length
                && sortedGlyfIndices[glyphListIndex] == i)
            {
                ++glyphListIndex;
                locaTableTemp[i] = glyphTableIndex;
                int start = locaTable[i];
                int bytesToRead = locaTable[i + 1] - start;
                if (bytesToRead > 0)
                {
                    raf.seek(glyfTableOffset + start);
                    raf.readFully(glyfTableSubset, glyphTableIndex,
                        bytesToRead);
                    glyphTableIndex += bytesToRead;
                }
            }
        }
    }

    private int checksum(byte ba[])
    {
        int byte0 = 0;
        int byte1 = 0;
        int byte2 = 0;
        int byte3 = 0;
        int index = 0;
        for (int i = 0, limit = ba.length / 4; i < limit; ++i)
        {
            byte3 += (int) ba[index++] & 0xff;
            byte2 += (int) ba[index++] & 0xff;
            byte1 += (int) ba[index++] & 0xff;
            byte0 += (int) ba[index++] & 0xff;
        }

        return byte0 + (byte1 << 8) + (byte2 << 16) + (byte3 << 24);
    }

    private void readGlyphs() throws IOException, PdfException
    {
        long[] la = (long[]) tableInfo.get(TtfReader.GLYF);
        if (la == null)
        {
            throw new PdfException(
                "GLYF table not present in font file.");
        }
        if (!glyphsUsed.containsKey(PdfInteger.DUMMY))
        {
            glyphsUsed.put(PdfInteger.DUMMY, PdfNull.DUMMY);
            usedGlyphsList.add(PdfInteger.DUMMY);
        }
        glyfTableOffset = la[0];

        for (int i = 0, limit = usedGlyphsList.size(); i < limit; ++i)
        {
            readCompositeGlyph(((PdfInteger) usedGlyphsList.get(i))
                .getInt());
        }
    }

    protected void readCompositeGlyph(int glyph) throws IOException
    {
        int start = locaTable[glyph];
        if (start == locaTable[glyph + 1])
        {
            return;
        }
        
        raf.seek(glyfTableOffset + start);
        int numberOfContours = raf.readShort();
        if (numberOfContours >= 0)
        {
            return;
        }
        
        raf.skipBytes(8);
        while(true)
        {
            int flags = raf.readUnsignedShort();
            PdfInteger compositeGlyph = new PdfInteger(raf
                .readUnsignedShort());
            if ( !glyphsUsed.containsKey(compositeGlyph))
            {
                glyphsUsed.put(compositeGlyph, PdfNull.DUMMY);
                usedGlyphsList.add(compositeGlyph);
            }
            if ((flags & MORE_COMPONENTS) == 0)
            {
                return;
            }
            int bytesToSkip = ((flags & ARG_1_AND_2_ARE_WORDS) != 0) ? 4
                : 2;
            if ((flags & WE_HAVE_A_SCALE) != 0)
            {
                bytesToSkip += 2;
            }
            else if ((flags & WE_HAVE_AN_X_AND_Y_SCALE) != 0)
            {
                bytesToSkip += 4;
            }
            if ((flags & WE_HAVE_A_TWO_BY_TWO) != 0)
            {
                bytesToSkip += 8;
            }
            
            raf.skipBytes(bytesToSkip);
        }
    }

    private void readLocaTableSize() throws IOException, PdfException
    {
        long[] la = (long[]) tableInfo.get(TtfReader.LOCA);
        if (la == null)
        {
            throw new PdfException(
                "LOCA table not present in font file.");
        }
        initialLocaTableSize = (int) la[1];
    }

    private void writeFullFont() throws IOException
    {
        int countTables = 0;
        int len = 0;
        long[] la;
        for (int i = 0; i < requiredTables.length; ++i)
        {
            String name = requiredTables[i];
            la = (long[]) tableInfo.get(name);
            if (la != null)
            {
                ++countTables;
            }
        }

        int ref = 16 * countTables + 12;
        pbos.writeInt(0x00010000);
        pbos.writeShort(countTables);
        int selector = entrySelectors[countTables];
        pbos.writeShort((1 << selector) * 16);
        pbos.writeShort(selector);
        pbos.writeShort((countTables - (1 << selector)) * 16);
        
        for (int i = 0; i < requiredTables.length; ++i)
        {
            String name = requiredTables[i];
            la = (long[]) tableInfo.get(name);
            if (la != null)
            {
                pbos.writeString(name, 0);
                pbos.writeInt((int) la[2]);
                len = (int) la[1];
                pbos.writeInt(ref);
                pbos.writeInt(len);
                ref += (len + 3) & ( ~3);
            }
        }
        for (int i = 0; i < requiredTables.length; ++i)
        {
            String name = requiredTables[i];
            la = (long[]) tableInfo.get(name);
            if (la != null)
            {
                len = (int) la[1];
                raf.seek(la[0]);
                byte[] ba = new byte[len];
                raf.readFully(ba, 0, ba.length);
                pbos.write(ba);
                int padCount = ((len + 3) & (~3)) - len;
                for (int j = 0; j < padCount; ++j)
                {
                    pbos.write(0);
                }
            }
        }
    }
    
    private void writeSubset() throws IOException
    {
        int countTables = 2;
        int len = 0;
        long[] la;
        for (int i = 0; i < requiredTables.length; ++i)
        {
            String name = requiredTables[i];
            if (name.equals(TtfReader.GLYF) || name.equals(TtfReader.LOCA))
            {
                continue;
            }
            la = (long[]) tableInfo.get(name);
            if (la != null)
            {
                ++countTables;
            }
        }
        
        int ref = 16 * countTables + 12;
        pbos.writeInt(0x00010000);
        pbos.writeShort(countTables);
        int selector = entrySelectors[countTables];
        pbos.writeShort((1 << selector) * 16);
        pbos.writeShort(selector);
        pbos.writeShort((countTables - (1 << selector)) * 16);
        
        for (int i = 0; i < requiredTables.length; ++i)
        {
            String name = requiredTables[i];
            la = (long[]) tableInfo.get(name);
            if (la != null)
            {
                pbos.writeString(name, 0);
                if (name.equals(TtfReader.GLYF))
                {
                    pbos.writeInt(checksum(glyfTableSubset));
                    len = initialGlyfTableSize;
                }
                else if (name.equals(TtfReader.LOCA))
                {
                    pbos.writeInt(checksum(locaTableSubset.getBuffer()));
                    len = initialLocaTableSize;
                }
                else
                {
                    pbos.writeInt((int) la[2]);
                    len = (int) la[1];
                }
                pbos.writeInt(ref);
                pbos.writeInt(len);
                ref += (len + 3) & ( ~3);
            }
        }
        
        for (int i = 0; i < requiredTables.length; ++i)
        {
            String name = requiredTables[i];
            la = (long[]) tableInfo.get(name);
            if (la != null)
            {    
                if (name.equals(TtfReader.GLYF))
                {
                    pbos.write(glyfTableSubset, 0, 
                        glyfTableSubset.length);
                }
                else if (name.equals(TtfReader.LOCA))
                {
                    pbos.write(locaTableSubset.getBuffer(), 0,
                        locaTableSubset.size());
                }
                else
                {
                    len = (int) la[1];
                    raf.seek(la[0]);
                    byte[] ba = new byte[len];
                    raf.readFully(ba, 0, ba.length);
                    pbos.write(ba);
                    int padCount = ((len + 3) & (~3)) - len;
                    for (int j = 0; j < padCount; ++j)
                    {
                        pbos.write(0);
                    }
                }
            }
        }
    }

    static PdfStream createStream(String filename,
        Hashtable glyphsUsed, Hashtable tableInfo, int[] locatable,
        boolean isLocaFormatShort, boolean isSubset, boolean isType0)
        throws IOException, PdfException
    {
        return new TtfGenerator(filename, glyphsUsed, tableInfo,
            locatable, isLocaFormatShort, isType0).getStream(isSubset);
    }
}
