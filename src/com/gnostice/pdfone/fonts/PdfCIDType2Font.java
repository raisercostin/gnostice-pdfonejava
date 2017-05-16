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

package com.gnostice.pdfone.fonts;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

import com.gnostice.pdfone.PdfArray;
import com.gnostice.pdfone.PdfDict;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfInteger;
import com.gnostice.pdfone.PdfName;
import com.gnostice.pdfone.PdfStream;
import com.gnostice.pdfone.PdfString;

final class PdfCIDType2Font extends PdfTrueTypeFont
{
    private Hashtable charCodesUsed;
    
    private Object[] sortedCharCodes;
    
    private static String toCMapHex(int n)
    {
        String s = Integer.toHexString(n);
        return "<0000".substring(0, 5 - s.length()) + s + ">";
    }

    PdfCIDType2Font(String path, int size, int encoding,
        byte embedType) throws IOException, PdfException
    {
        super(path, size, encoding, embedType);
        this.type = CID_TYPE_2;
        this.charCodesUsed = new Hashtable();
    }

    PdfCIDType2Font(String path, int style, int size, int encoding,
        byte embedType) throws IOException, PdfException
    {
        super(path, style, size, encoding, embedType);
        this.type = CID_TYPE_2;
        this.charCodesUsed = new Hashtable();
    }

    PdfCIDType2Font(String path, int style, int size, int encoding)
        throws IOException, PdfException
    {
        super(path, style, size, encoding);
        this.type = CID_TYPE_2;
        this.charCodesUsed = new Hashtable();
    }

    PdfCIDType2Font(String path, int size, int encoding)
        throws IOException, PdfException
    {
        super(path, size, encoding);
        this.type = CID_TYPE_2;
        this.charCodesUsed = new Hashtable();
    }

    PdfCIDType2Font(String path, int size, int encoding,
        byte embedType, long offset) throws IOException, PdfException
    {
        super(path, size, encoding, embedType, offset);
        this.type = CID_TYPE_2;
        this.charCodesUsed = new Hashtable();
    }

    PdfCIDType2Font(String path, int style, int size, int encoding,
        byte embedType, long offset) throws IOException, PdfException
    {
        super(path, style, size, encoding, embedType, offset);
        this.type = CID_TYPE_2;
        this.charCodesUsed = new Hashtable();
    }

    PdfCIDType2Font(String path, int style, int size, int encoding,
        long offset) throws IOException, PdfException
    {
        super(path, style, size, encoding, offset);
        this.type = CID_TYPE_2;
        this.charCodesUsed = new Hashtable();
    }

    PdfCIDType2Font(String path, int size, int encoding, long offset)
        throws IOException, PdfException
    {
        super(path, size, encoding, offset);
        this.type = CID_TYPE_2;
        this.charCodesUsed = new Hashtable();
    }

    public ArrayList prepareWidths()
    {
        ArrayList outer = new ArrayList();
        ArrayList inner = new ArrayList();
        int prevCharCode = -1;
        int[] charMetrics = null;
        
        for (int i = 0; i < sortedCharCodes.length; ++i)
        {
            charMetrics = (int[]) sortedCharCodes[i];
            if (charMetrics[1] != 1000)
            {
                int charCode = charMetrics[0];
                if (charCode == prevCharCode + 1)
                {
                    inner.add(new PdfInteger(charMetrics[1]));
                }
                else
                {
                    if (inner.size() != 0)
                    {
                        PdfArray arr = new PdfArray(inner);
                        outer.add(arr);
                        inner = new ArrayList();
                    }
                    outer.add(new PdfInteger(charCode));
                    inner.add(new PdfInteger(charMetrics[1]));
                }
                prevCharCode = charCode;
            }
        }
        if (inner.size() != 0)
        {
            PdfArray arr = new PdfArray(inner);
            outer.add(arr);
        }
        
        return outer;
    }

    private void sortCharCodes()
    {
        sortedCharCodes = charCodesUsed.values().toArray();
        Arrays.sort(sortedCharCodes, new PdfFont.Sort());
    }
    
    public synchronized PdfDict getDescendantDict()
    {
        sortCharCodes();
        
        HashMap hm = new HashMap();
        hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_FONT));
        hm.put(new PdfName(PDF_SUBTYPE), new PdfName(
            PDF_CIDFONT_TYPE2));
        hm.put(new PdfName(PDF_BASEFONT), new PdfName(
            getBaseFontName()));

        //this is hard coded...needs workaround
        hm.put(new PdfName(PDF_CID_TO_GID_MAP), new PdfName(
            "Identity"));
        
        HashMap nhm = new HashMap();
        nhm.put(new PdfName("Supplement"), PdfInteger.DUMMY);
        nhm.put(new PdfName("Ordering"), new PdfString("Identity"));
        nhm.put(new PdfName("Registry"), new PdfString("Adobe"));
        hm.put(new PdfName(PDF_CIDSYSTEM_INFO), new PdfDict(nhm));
        
        //this should be conditional for horizontal and vertical mode
        hm.put(new PdfName(PDF_DW), new PdfInteger(1000));
        ArrayList list = prepareWidths();
        if (list.size() != 0)
        {
            hm.put(new PdfName(PDF_W), new PdfArray(list));
        }
        
        return new PdfDict(hm);
    }
    
    public synchronized PdfStream getToUnicodeCMAP()
    {
        if (sortedCharCodes.length != 0)
        {
            StringBuffer sb = new StringBuffer(
                "/CIDInit /ProcSet findresource begin\n"
                    + "12 dict begin\n"
                    + "begincmap\n"
                    + "/CIDSystemInfo\n"
                    + "<</Registry(Adobe)/Ordering(UCS)/Supplement 0>> def\n"
                    + "/CMapName /Adobe-Identity-UCS def\n"
                    + "/CMapType 2 def\n"
                    + "1 begincodespacerange\n"
                    + toCMapHex(((int[]) sortedCharCodes[0])[0])
                    + toCMapHex(((int[]) sortedCharCodes[
                        sortedCharCodes.length - 1])[0])
                    + "\n" + "endcodespacerange\n");
            for (int i = 0, limit = 0; i < sortedCharCodes.length; ++i)
            {
                if (limit == 0)
                {
                    if (i != 0)
                    {
                        sb.append("endbfrange\n");
                    }
                    limit = Math.min(100, sortedCharCodes.length - i);
                    sb.append(limit).append(" beginbfrange\n");
                }
                --limit;
                int charMetrics[] = (int[]) sortedCharCodes[i];
                String cmapHex = toCMapHex(charMetrics[0]);
                sb.append(cmapHex).append(cmapHex).append(
                    toCMapHex(charMetrics[2])).append("\n");
            }
            sb.append("endbfrange\n" + "endcmap\n"
                + "CMapName currentdict /CMap defineresource pop\n"
                + "end end\n");
            byte[] ba = sb.toString().getBytes();
            
            return new PdfStream(new PdfDict(new HashMap()),
                ByteBuffer.wrap(ba));
        }
        
        return null;
    }
    
    public synchronized byte[] updateGlyphList(String text)
        throws IOException, PdfException
    {
        String encoding = "UnicodeBigUnmarked";
        int limit = text.length();
        int charMetrics[] = null;
        char ca[] = new char[limit];
        byte[] ba = null;

        int index = 0;
        char ch = 0;
        int charCode = 0;
        try
        {
            for (int i = 0; i < limit; ++i)
            {
                ch = text.charAt(i);
                charMetrics = t.getMacOrWinCharMetrics(ch);
                if (charMetrics != null)
                {
                    charCode = charMetrics[0];
                    PdfInteger key = new PdfInteger(charCode);
                    if ( !charCodesUsed.containsKey(key))
                    {
                        charCodesUsed.put(key, new int[] { charCode,
                            charMetrics[1], ch });
                    }
                    ca[index++] = (char) charCode;
                }
            }
            String s = new String(ca, 0, index);
            ba = s.getBytes(encoding);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new PdfException(
                "Specified encoding does not support "
                    + "some char code in text.");
        }

        return ba;
    }

    public PdfStream createStream(byte embedType) throws IOException,
        PdfException
    {
        if (embedType == 0)
        {
            embedType = EMBED_SUBSET;
        }
        
        return t.createStream(embedType, charCodesUsed);
    }
    
    public void getData(HashMap hm) throws IOException, PdfException
    {
        hm.put(new PdfName(PDF_SUBTYPE), new PdfName(PDF_TYPE0));
        hm.put(new PdfName(PDF_BASEFONT), new PdfName(
            getBaseFontName()));

        // this is hard coded here..needs workaround
        hm.put(new PdfName(PDF_ENCODING), new PdfName("Identity-H"));

        PdfDict descendantFont = getDescendantDict();
        if (descendantFont != null)
        {
            hm.put(new PdfName(PDF_DESCENDANT), descendantFont);
            PdfStream stm = createStream(getEmbedType());
            if (stm != null)
            {
                PdfDict fontDescriptor = getFontDescriptor();
                fontDescriptor.getMap().put(
                    new PdfName(PDF_FONTNAME),
                    new PdfName(getBaseFontName()));
                descendantFont.getMap().put(
                    new PdfName(RUBICON_EMBEDDED), stm);
                descendantFont.getMap().put(
                    new PdfName(PDF_FDESCRIPTOR), fontDescriptor);
            }

        }

        PdfStream toUniStm = getToUnicodeCMAP();
        if (toUniStm != null)
        {
            hm.put(new PdfName(PDF_TOUNICODE), toUniStm);
        }
    }

    public Object clone()
    {
        PdfCIDType2Font clone = (PdfCIDType2Font) super.clone();
        clone.charCodesUsed = (Hashtable) this.charCodesUsed.clone();

        return clone;
    }
}
