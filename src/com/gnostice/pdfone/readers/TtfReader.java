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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfInteger;
import com.gnostice.pdfone.PdfNull;
import com.gnostice.pdfone.PdfStream;
import com.gnostice.pdfone.fonts.PdfFont;

/**
 * @author amol
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class TtfReader
{
    final static String CMAP = "cmap";
    
    final static String HEAD = "head";
    
    final static String LOCA = "loca";
    
    final static String GLYF = "glyf";
    
    private final static String HHEA = "hhea";
    
    private final static String HMTX = "hmtx";
    
    private final static String MAXP = "maxp";
    
    private final static String NAME = "name";
    
    private final static String POST = "post";
    
    private final static String OS2 = "OS/2";
    
    static final String[] ttfRequiredTables = { CMAP, "cvt ", "fpgm",
        GLYF, HEAD, HHEA, HMTX, LOCA, MAXP, "prep" };

    static final String[] cidRequiredTables = { "cvt ", "fpgm",
        GLYF, HEAD, HHEA, HMTX, LOCA, MAXP, "prep" };

    private TtfOS2Table os2;
    
    private TtfHeadTable head;
    
    private TtfHHeaTable hhea;
    
    private Hashtable tableInfo;
    
    private RandomAccessFile raf;
    
    protected Hashtable macEncodingTable;
    
    protected Hashtable winUniEncodingTable;
    
    protected boolean isSymbolicFont;

    private int[] widths;
    
    private int[] locaTable;
    
    private boolean isLocaFormatShort;
    
    private String fileName;
    
    private long isFixedPitch;
    
    private short underlinePosition;
    
    private short underlineThickness;
    
    private double italicAngle;
    
    private TtfReader()
    {
        //private constructor
    }
    
    public static TtfReader fileReader(String fileName, long offset)
        throws IOException, PdfException
    {
        TtfReader t = new TtfReader();
        t.fileName = fileName;
        t.raf = new RandomAccessFile(fileName, "r");
        t.readTableDir(offset);
        t.readTables();
        
        return t;
    }
    
    private void readTableDir(long offset) throws IOException,
        UnsupportedEncodingException
    {
        raf.seek(offset);
        raf.skipBytes(4); //version skipped
        
        int noOfTables = raf.readUnsignedShort();
        tableInfo = new Hashtable();
        
        raf.skipBytes(6);
        
        byte[] ba = new byte[4];
        String tag, charset = "ISO-8859-1";
        
        for (int i = 0; i < noOfTables; ++i)
        {
            raf.read(ba);
            tag = new String(ba, charset);
            
            long[] la = new long[3];
            la[2] = ((long)raf.readInt()) & 0xffffffff;//checksum
            la[0] = ((long)raf.readInt()) & 0xffffffff;//offset
            la[1] = ((long)raf.readInt()) & 0xffffffff;//length
            tableInfo.put(tag, la);
        }
    }
    
    private void readTables() throws IOException, PdfException
    {
        readHeadTable();
        readHHeaTable();
        readLocaTable();
        readOS2Table();
        readGlyphWidths();
        readCmapTable();
        readPostTable();
    }
    
    private void readHeadTable() throws IOException, PdfException
    {
        head = new TtfHeadTable();
        long[] la = (long[]) tableInfo.get(HEAD);
        if (la == null)
        {
            throw new PdfException(
                "HEAD table not present in font file.");
        }
        raf.seek(la[0]);
        
        /*skip fields from version through magicNumber*/
        raf.seek(raf.getFilePointer() + 16 /*bytes*/);
        
        head.flags = raf.readUnsignedShort();
        head.unitsPerEm = raf.readUnsignedShort();
        
        /*skip creation and modification dates*/
        raf.seek(raf.getFilePointer() + 16 /*bytes*/);
        
        head.xMin = raf.readShort();
        head.yMin = raf.readShort();
        head.xMax = raf.readShort();
        head.yMax = raf.readShort();
        head.macStyle = raf.readUnsignedShort();
        
        raf.skipBytes(4);
        isLocaFormatShort = (raf.readUnsignedShort() == 0);
    }

    private void readLocaTable() throws IOException, PdfException
    {
        long[] la = (long[]) tableInfo.get(LOCA);
        if (la == null)
        {
            return;
        }
        raf.seek(la[0]);
        if (isLocaFormatShort)
        {
            int entries = (int) la[1] / 2;
            locaTable = new int[entries];
            for (int i = 0; i < entries; ++i)
            {
                locaTable[i] = raf.readUnsignedShort() * 2;
            }
        }
        else
        {
            int entries = (int) la[1] / 4;
            locaTable = new int[entries];
            for (int i = 0; i < entries; ++i)
            {
                locaTable[i] = raf.readInt();
            }
        }
    }

    private void readOS2Table() throws IOException, PdfException
    {
        os2 = new TtfOS2Table();
        long[] la = (long[]) tableInfo.get(OS2);
        if (la == null)
        {
            throw new PdfException(
                "OS/2 table not present in font file.");
        }
        raf.seek(la[0]);

        os2.version = raf.readUnsignedShort();
        os2.avgCharWidth = raf.readShort();
        
        /*skip fields usWeightClass & usWidthClass*/
        raf.skipBytes(4);
        os2.fsType = raf.readUnsignedShort();
        
        /*skip fields from ySubscriptXSize through fsSelection*/
        raf.seek(raf.getFilePointer() + 54 /*bytes*/);
        
        os2.firstCharIndex = raf.readUnsignedShort();
        os2.lastCharIndex = raf.readUnsignedShort();
        os2.typoAscender = raf.readShort();
        os2.typoDescender = raf.readShort();
        /*skip sTypoLineGap*/
        raf.skipBytes(2);
        
        os2.usWinAscent = raf.readUnsignedShort();
        os2.usWinDescent = raf.readUnsignedShort();

        if (os2.version > 1) /*2 || 3*/
        {
            /*skip fields from ulCodePageRange1 through sxHeight*/
            raf.seek(raf.getFilePointer() + 10 /*bytes*/);
            os2.sCapHeight = raf.readShort();
        }
        else /*0 || 1*/
        {
            os2.sCapHeight = (short) Math.abs(0.7 * head.unitsPerEm);
        }
    }

    private void readHHeaTable() throws IOException, PdfException
    {
        hhea = new TtfHHeaTable();
        long[] la = (long[]) tableInfo.get(HHEA);
        if (la == null)
        {
            throw new PdfException(
                "HHea table not present in font file.");
        }
        raf.seek(la[0]);
        
        /*skip fields from version through LineGap*/
        raf.seek(raf.getFilePointer() + 10 /*bytes*/);
        
        hhea.advanceWidthMax = raf.readUnsignedShort();
    
        /*skip fields from minLeftSideBearing through xMaxExtent*/
        raf.seek(raf.getFilePointer() + 6 /*bytes*/);
        
        hhea.caretSlopeRise = raf.readShort();
        hhea.caretSlopeRun = raf.readShort();
    
        /*skip fields from caretOffset through metricDataFormat*/
        raf.seek(raf.getFilePointer() + 12 /*bytes*/);
            
        hhea.numberOfHMetrics = raf.readUnsignedShort();
    }

    private int[] readGlyphWidths() throws IOException, PdfException
    {
        long[] hmtx_la = (long[]) tableInfo.get(HMTX);
        if (hmtx_la == null)
        {
            throw new PdfException(
                "HMTX table not present in font file.");
        }
        long hmtxTableLength = hmtx_la[1];  
        long[] maxp_la = (long[]) tableInfo.get(MAXP);
        if (maxp_la == null)
        {
            raf.seek(hmtx_la[0]);
            for (int i = 0; i < hhea.numberOfHMetrics; ++i)
            {
                widths[i] = (raf.readUnsignedShort() * 1000)
                    / head.unitsPerEm;
                raf.readShort();
            }
        }
        else
        {
            raf.seek(maxp_la[0] + 4); /*for version*/
            int numGlyphs = raf.readUnsignedShort();
            hmtxTableLength -= Math.abs(numGlyphs
                - hhea.numberOfHMetrics) * 2;
            long limit = hmtx_la[0] + hmtxTableLength;
            widths = new int[hhea.numberOfHMetrics];
            raf.seek(hmtx_la[0]);
            if (hmtxTableLength == 4) //monospaced font
            {
                int w = raf.readUnsignedShort();
                for (int i = 0; i < hhea.numberOfHMetrics; ++i)
                {
                    widths[i] = (w * 1000) / head.unitsPerEm;
                }
            }
            else
            {
                for (int i = 0; raf.getFilePointer() < limit; ++i)
                {
                    widths[i] = (raf.readUnsignedShort() * 1000)
                        / head.unitsPerEm;
                    raf.readShort();
                }
            }
        }
        
        return widths;
    }

    private void readCmapTable() throws IOException, PdfException
    {
        long[] la = (long[]) tableInfo.get(CMAP);
        if (la == null)
        {
            throw new PdfException(
                "cmap table not present in font file.");
        }
        raf.seek(la[0] + 2); /* skip version */
    
        int tableCount = raf.readUnsignedShort();
        
        long macEncTblOff = 0;
        long winUniEncTblOff = 0;
        long winSymEncTblOff = 0;
        for (int i = 0; i < tableCount; ++i)
        {
            int platId = raf.readUnsignedShort();
            int platSpecId = raf.readUnsignedShort();
            long offset = ((long)raf.readInt()) & 0xffffffff;
            if (platId == 1 && platSpecId == 0)
            {
                macEncTblOff = offset;
            }
            if (platId == 3 && platSpecId == 0)
            {
                isSymbolicFont = true;
                winSymEncTblOff = offset;
            }
            else if (platId == 3 && platSpecId == 1)
            {
                winUniEncTblOff = offset;
            }
        }
    
        if (macEncTblOff > 0)
        {
            raf.seek(la[0] + macEncTblOff);
            int format = raf.readUnsignedShort();
            switch (format)
            {
                case 0:
                    macEncodingTable = readCmapTable_Format_0();
                    break;
                case 4:
                    macEncodingTable = readCmapTable_Format_4();
                    break;
                case 6:
                    macEncodingTable = readCmapTable_Format_6();
                    break;
            }
        }
        
        if (winUniEncTblOff > 0)
        {
            raf.seek(la[0] + winUniEncTblOff);
            int format = raf.readUnsignedShort();
            if (format == 4)
            {
                winUniEncodingTable = readCmapTable_Format_4();
            }
        }
    
        if (winSymEncTblOff > 0)
        {
            raf.seek(la[0] + winSymEncTblOff);
            int format = raf.readUnsignedShort();
            if (format == 4)
            {
                macEncodingTable = readCmapTable_Format_4();
            }
        }
    }

    private Hashtable readCmapTable_Format_0() throws IOException
    {
        Hashtable t = new Hashtable();
        
        raf.skipBytes(4);
        /* skip length & version */
        int glyphIndex; 
        for (int i = 0; i < 256; ++i)
        {
            glyphIndex = raf.readUnsignedByte();
            t.put(new Integer(i), new int[] { glyphIndex,
                getGlyphWidthFromHMTX(glyphIndex) });
        }
        
        return t;
    }

    private Hashtable readCmapTable_Format_4() throws IOException
    {
        Hashtable t = new Hashtable();
        
        int length = raf.readUnsignedShort() / 2;
        /*length of the table*/
        
        raf.skipBytes(2); /* skip version */
        
        int segCount = raf.readUnsignedShort() / 2;
        /* header have this entry stored as segCount * 2 */
        
        raf.skipBytes(6); /* skip searchRange - rangeShift */
        
        int endCount[] = new int[segCount];
        for (int i = 0; i < segCount; ++i)
        {
            endCount[i] = raf.readUnsignedShort();
        }
        
        raf.skipBytes(2); /* skip reservedPad */
        
        int startCount[] = new int[segCount];
        for (int i = 0; i < segCount; ++i)
        {
            startCount[i] = raf.readUnsignedShort();
        }
    
        int idDelta[] = new int[segCount];
        for (int i = 0; i < segCount; ++i)
        {
            idDelta[i] = raf.readUnsignedShort();
            /* Delta for this char in this segment */
        }
    
        int idRangeOffset[] = new int[segCount];
        for (int i = 0; i < segCount; ++i)
        {
            idRangeOffset[i] = raf.readUnsignedShort();
            /* offset into glyphIdArray */
        }
    
        int glyphIdArray[] = new int[length - (8 + 4 * segCount)];
        for (int i = 0; i < glyphIdArray.length; ++i)
        {
            glyphIdArray[i] = raf.readUnsignedShort();
        }
        
        for (int i = 0; i < segCount; ++i)
        {
            int glyphIndex;
            for (int j = startCount[i]; j <= endCount[i]
                && j != 0xffff; ++j)
            {
                if (idRangeOffset[i] == 0)
                {
                    glyphIndex = (idDelta[i] + j) & 0xffff;
                    /* modulo 65536 */
                }
                else
                {
                    int segIndex = i + idRangeOffset[i] / 2
                        - segCount + j - startCount[i];
                    if (segIndex >= glyphIdArray.length)
                    {
                        continue;
                    }
                    glyphIndex = (idDelta[i] + glyphIdArray[segIndex])
                        & 0xffff; /* modulo 65536 */
                }
                /*System.out.println(j & (isSymbolicFont ? 0xff : 0xffff));*/
                t.put(new Integer(j
                    & (isSymbolicFont ? 0xff : 0xffff)), new int[] {
                    glyphIndex, getGlyphWidthFromHMTX(glyphIndex) });
            }
        }
        
        return t;
    }

    private Hashtable readCmapTable_Format_6() throws IOException
    {
        Hashtable h = new Hashtable();
        raf.skipBytes(4); /* skip length & version */
        
        int firstCode = raf.readUnsignedShort();
        int entryCount = raf.readUnsignedShort();
        int glyphIndex;
        for (int i = 0; i < entryCount; ++i)
        {
            glyphIndex = raf.readUnsignedShort();
            h.put(new Integer(i + firstCode), new int[] { glyphIndex,
                getGlyphWidthFromHMTX(glyphIndex) });
        }
        
        return h;
    }

    private void readPostTable() throws IOException
    {
        long[] la = (long[]) tableInfo.get(POST);
        if (la == null)
        {
            italicAngle = -Math.toDegrees(Math.atan2(
                hhea.caretSlopeRun, hhea.caretSlopeRise));
        }
        short mantissa = 0;
        int fraction = 0;
        raf.seek(la[0] + 4);
        mantissa = raf.readShort();
        fraction = raf.readUnsignedShort();
        italicAngle = (double) mantissa + (double) fraction / 16384.0;
        underlinePosition = raf.readShort();
        underlineThickness = raf.readShort();
        isFixedPitch = ((long)raf.readInt()) & 0xffffffff;
    }
    
    private int getGlyphWidthFromHMTX(int glyph)
    {
        if (glyph >= widths.length)
        {
            glyph = widths.length - 1;
        }
        
        return widths[glyph];
    }

    public int getGlyphWidthFromCMAP(int c)
    {
        Hashtable map = winUniEncodingTable == null ? macEncodingTable
            : winUniEncodingTable;
        if (map == null)
        {
            return /*0*/getGlyphWidthFromHMTX(0);
        }
        int[] glyphIndexAndWidth = (int[]) map.get(new Integer(c));
    
        return glyphIndexAndWidth == null ? /*0*/getGlyphWidthFromHMTX(0)
            : glyphIndexAndWidth[1];
    }

    public double getFontItalicAngle()
    {
        return italicAngle;
    }
    
    public String getFontBaseName() throws IOException, PdfException
    {
        long[] la = (long[]) tableInfo.get(NAME);
        if (la == null)
        {
            throw new PdfException(
                "Name table not present in font file.");
        }
        raf.seek(la[0] + 2);
        int count = raf.readUnsignedShort();
        int stringOffset = raf.readUnsignedShort();
        for (int k = 0; k < count; ++k)
        {
            int platformID = raf.readUnsignedShort();
            raf.skipBytes(4); /* skip encodingID & languageID */
            int nameID = raf.readUnsignedShort();
            int length = raf.readUnsignedShort();
            int offset = raf.readUnsignedShort();
            if (nameID == 6)
            {
                raf.seek(la[0] + stringOffset + offset);
                if (platformID == 0 || platformID == 3)
                {
                    StringBuffer sb = new StringBuffer();
                    int limit = length / 2;
                    for (int j = 0; j < limit; ++j)
                    {
                        sb.append(raf.readChar());
                    }

                    return sb.toString();
                }
                else
                {
                    byte[] ba = new byte[length];
                    raf.read(ba);
                    return new String(ba, "ISO-8859-1");
                }
            }
        }

        File file = new File(fileName);

        return file.getName().replace(' ', '-');
    }

    public int[] getFontWidths(int firstChar, int lastChar)
    {
        int[] nwidths = new int[lastChar - firstChar + 1];
        for (int i = 0, j = firstChar; i < nwidths.length; ++i, ++j)
        {
            nwidths[i] = getGlyphWidthFromCMAP(j);
        }

        return nwidths;
    }
    
    public int[] getFontBBox()
    {
        return new int[] { head.xMin * 1000 / head.unitsPerEm,
            head.yMin * 1000 / head.unitsPerEm,
            head.xMax * 1000 / head.unitsPerEm,
            head.yMax * 1000 / head.unitsPerEm };
    }

    public int getFontStyle()
    {
        int retVal = 0;
        if ((head.macStyle & 1) == 1)
        {
            retVal |= PdfFont.BOLD;
        }
        if ((head.macStyle & 2) == 2)
        {
            retVal |= PdfFont.ITALIC;
        }
        
        return retVal;
    }

    public int getFontFlags()
    {
        int flags = 0;
        if (isFixedPitch != 0)
        {
            flags |= 1;
        }
        flags |= isSymbolicFont ? 4 : 32;
        if ((head.macStyle & 2) != 0)
        {
            flags |= 64;
        }
        if ((head.macStyle & 1) != 0)
        {
            flags |= 262144;
        }

        return flags;
    }
    
    public int getFontAscent()
    {
        return os2.typoAscender * 1000 / head.unitsPerEm;
    }
    
    public int getFontDescent()
    {
        return os2.typoDescender * 1000 / head.unitsPerEm;
    }
    
    public int getFontCapHeight()
    {
        return os2.sCapHeight * 1000 / head.unitsPerEm;
    }
    
    public double getFontHeight()
    {
        return (os2.usWinAscent + os2.usWinDescent) 
            / head.unitsPerEm;
    }
    
    public int getFontAvgWidth()
    {
        return os2.avgCharWidth * 1000 / head.unitsPerEm;
    }
    
    public short getFontUnderlinePosition()
    {
        return underlinePosition;
    }

    public short getFontUnderlineThickness()
    {
        return underlineThickness;
    }

    public int getFontMaxwidth()
    {
        return hhea.advanceWidthMax * 1000 / head.unitsPerEm;
    }
    
    public PdfStream createStream(byte embedType,
        Hashtable charCodesUsed) throws IOException, PdfException
    {
        if (os2.fsType == 2)
        {
            return null; /* no embedding allowed */
        }

        return TtfGenerator.createStream(fileName, charCodesUsed,
            tableInfo, locaTable, isLocaFormatShort,
            embedType == PdfFont.EMBED_SUBSET, true);
    }

    public PdfStream createStream(byte embedType,
        int[] charCodesUsed, int firstChar, int lastChar)
        throws IOException, PdfException
    {
        if (os2.fsType == 2)
        {
            return null; /* no embedding allowed */
        }

        return TtfGenerator.createStream(fileName, getGlyfIndices(
            charCodesUsed, firstChar, lastChar), tableInfo,
            locaTable, isLocaFormatShort,
            embedType == PdfFont.EMBED_SUBSET, false);
    }
    
    private Hashtable getGlyfIndices(int[] charCodes, int firstChar,
        int lastChar)
    {
        int charMetrics[];
        Hashtable glyphs = new Hashtable();
        for (int i = firstChar; i <= lastChar; ++i)
        {
            if (charCodes[i] != 0)
            {
                charMetrics = getMacOrWinCharMetrics(i);
                if (charMetrics != null)
                {
                    glyphs.put(new PdfInteger(charMetrics[0])
                        , PdfNull.DUMMY);
                }
            }
        }

        return glyphs;
    }
    
    public int[] getMacOrWinCharMetrics(int charCode)
    {
        if (winUniEncodingTable != null)
        {
            return (int[]) winUniEncodingTable.get(new Integer(
                charCode));
        }
        if (macEncodingTable != null)
        {
            return (int[]) macEncodingTable
                .get(new Integer(charCode));
        }
        
        return null;
    }
    
    public boolean isCompositeGlyf(int charCode) throws IOException
    {
        if (locaTable != null)
        {
            Hashtable map = winUniEncodingTable == null ? macEncodingTable
                : winUniEncodingTable;
            if (map == null)
            {
                return false;
            }
            int[] glyphIndexAndWidth = (int[]) map.get(new Integer(
                charCode));
            if (glyphIndexAndWidth != null)
            {
                int glyphIndex = glyphIndexAndWidth[0];
                int start = locaTable[glyphIndex];
                if (start != locaTable[glyphIndex + 1])
                {
                    long[] la = (long[]) tableInfo.get(GLYF);
                    if (la != null)
                    {
                        long glyfTableOffset = la[0];
                        raf.seek(glyfTableOffset + start);
                        int numberOfContours = raf.readShort();

                        return (numberOfContours < 0);
                    }
                }
            }
        }

        return false;
    }

    public void dispose() throws IOException
    {
        if (raf != null)
        {
            raf.close();
        }
    }
}
