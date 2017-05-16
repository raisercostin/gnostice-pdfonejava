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
import java.util.HashMap;

import com.gnostice.pdfone.PdfArray;
import com.gnostice.pdfone.PdfDict;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfInteger;
import com.gnostice.pdfone.PdfMeasurement;
import com.gnostice.pdfone.PdfName;
import com.gnostice.pdfone.PdfStream;
import com.gnostice.pdfone.Usable;
import com.gnostice.pdfone.readers.TtfReader;

class PdfTrueTypeFont extends PdfFont
{
    private static final PdfName FLAGS = new PdfName(Usable.PDF_FLAGS);
    
    private static final PdfName FONT_BBOX = new PdfName(
        Usable.PDF_FONTBBOX);
    
    private static final PdfName ASCENT = new PdfName(
        Usable.PDF_ASCENT);
    
    private static final PdfName DESCENT = new PdfName(
        Usable.PDF_DESCENT);
    
    private static final PdfName CAPHEIGHT = new PdfName(
        Usable.PDF_CAPHEIGHT);
    
    private static final PdfName AVGWIDTH = new PdfName(
        Usable.PDF_AVGWIDTH);
    
    private static final PdfName MAXWIDTH = new PdfName(
        Usable.PDF_MAXWIDTH);

    private static final PdfName STEMH = new PdfName(
        Usable.PDF_HORIZ_STEM);

    private static final PdfName STEMV = new PdfName(
        Usable.PDF_VERT_STEM);
    
    private static final PdfName ITALIC_ANGLE = new PdfName(
        Usable.PDF_ITALANGLE);

    private static final String TTF_BOLD = "Bold";

    private static final String TTF_ITALIC = "Italic";

    private static final String TTF_SEPARATOR = ",";
    
    TtfReader t;
    
//    boolean isSymbolic;
    
    PdfTrueTypeFont(String path, int size, int encoding)
        throws IOException, PdfException
    {
        super(null, size, encoding);
        t = TtfReader.fileReader(path, 0);
        this.type = TRUE_TYPE;
        this.style = t.getFontStyle();
        this.name = t.getFontBaseName();
        this.ascent = t.getFontAscent();
        this.avgWidth = t.getFontAvgWidth();
        this.capHeight = t.getFontCapHeight();
        this.descent = t.getFontDescent();
        this.flags = t.getFontFlags(); 
        this.fontBBox = new PdfArray(t.getFontBBox());
        this.italicangle = (int) t.getFontItalicAngle();
        this.maxWidth = t.getFontMaxwidth();
        
        setFontDescriptor();
    }
    
    PdfTrueTypeFont(String path, int style, int size, int encoding)
        throws IOException, PdfException
    {
        super(null, style, size, encoding);
        t = TtfReader.fileReader(path, 0);
        this.type = TRUE_TYPE;
        
        this.style = t.getFontStyle() | (style & 0xfffffff9); 
        /* ignore BOLD and ITALIC styles */
        
        this.name = t.getFontBaseName();
        this.ascent = t.getFontAscent();
        this.avgWidth = t.getFontAvgWidth();
        this.capHeight = t.getFontCapHeight();
        this.descent = t.getFontDescent();
        this.flags = t.getFontFlags(); 
        this.fontBBox = new PdfArray(t.getFontBBox());
        this.italicangle = (int) t.getFontItalicAngle();
        this.maxWidth = t.getFontMaxwidth();
        
        setFontDescriptor();
    }

    PdfTrueTypeFont(String path, int size, int encoding, byte embedType)
        throws IOException, PdfException
    {
        super(null, size, encoding, embedType);
        t = TtfReader.fileReader(path, 0);
        this.type = TRUE_TYPE;
        this.style = t.getFontStyle();
        this.name = t.getFontBaseName();
        this.ascent = t.getFontAscent();
        this.avgWidth = t.getFontAvgWidth();
        this.capHeight = t.getFontCapHeight();
        this.descent = t.getFontDescent();
        this.flags = t.getFontFlags();
        this.fontBBox = new PdfArray(t.getFontBBox());
        this.italicangle = (int) t.getFontItalicAngle();
        this.maxWidth = t.getFontMaxwidth();
        
        setFontDescriptor();
        if (embedType == PdfFont.EMBED_SUBSET)
        {
            createSubsetPrefix(); 
        }
    }

    PdfTrueTypeFont(String path, int style, int size, int encoding,
        byte embedType) throws IOException, PdfException
    {
        super(null, style, size, encoding, embedType);
        t = TtfReader.fileReader(path, 0);
        this.type = TRUE_TYPE;

        this.style = t.getFontStyle() | (style & 0xfffffff9);
        /* ignore BOLD and ITALIC styles */

        this.name = t.getFontBaseName();
        this.ascent = t.getFontAscent();
        this.avgWidth = t.getFontAvgWidth();
        this.capHeight = t.getFontCapHeight();
        this.descent = t.getFontDescent();
        this.fontBBox = new PdfArray(t.getFontBBox());
        this.italicangle = (int) t.getFontItalicAngle();
        this.maxWidth = t.getFontMaxwidth();
        
        setFontDescriptor();
        if (embedType == PdfFont.EMBED_SUBSET)
        {
            createSubsetPrefix(); 
        }
    }

    PdfTrueTypeFont(String path, int size, int encoding, long offset)
        throws IOException, PdfException
    {
        super(null, size, encoding);
        t = TtfReader.fileReader(path, offset);
        this.type = TRUE_TYPE;
        this.style = t.getFontStyle();
        this.name = t.getFontBaseName();
        this.ascent = t.getFontAscent();
        this.avgWidth = t.getFontAvgWidth();
        this.capHeight = t.getFontCapHeight();
        this.descent = t.getFontDescent();
        this.flags = t.getFontFlags();
        this.fontBBox = new PdfArray(t.getFontBBox());
        this.italicangle = (int) t.getFontItalicAngle();
        this.maxWidth = t.getFontMaxwidth();

        setFontDescriptor();
    }

    PdfTrueTypeFont(String path, int style, int size, int encoding,
        long offset) throws IOException, PdfException
    {
        super(null, style, size, encoding);
        t = TtfReader.fileReader(path, offset);
        this.type = TRUE_TYPE;

        this.style = t.getFontStyle() | (style & 0xfffffff9);
        /* ignore BOLD and ITALIC styles */

        this.name = t.getFontBaseName();
        this.ascent = t.getFontAscent();
        this.avgWidth = t.getFontAvgWidth();
        this.capHeight = t.getFontCapHeight();
        this.descent = t.getFontDescent();
        this.flags = t.getFontFlags();
        this.fontBBox = new PdfArray(t.getFontBBox());
        this.italicangle = (int) t.getFontItalicAngle();
        this.maxWidth = t.getFontMaxwidth();

        setFontDescriptor();
    }

    PdfTrueTypeFont(String path, int size, int encoding,
        byte embedType, long offset) throws IOException, PdfException
    {
        super(null, size, encoding, embedType);
        t = TtfReader.fileReader(path, offset);
        this.type = TRUE_TYPE;
        this.style = t.getFontStyle();
        this.name = t.getFontBaseName();
        this.ascent = t.getFontAscent();
        this.avgWidth = t.getFontAvgWidth();
        this.capHeight = t.getFontCapHeight();
        this.descent = t.getFontDescent();
        this.flags = t.getFontFlags();
        this.fontBBox = new PdfArray(t.getFontBBox());
        this.italicangle = (int) t.getFontItalicAngle();
        this.maxWidth = t.getFontMaxwidth();

        setFontDescriptor();
        if (embedType == PdfFont.EMBED_SUBSET)
        {
            createSubsetPrefix();
        }
    }

    PdfTrueTypeFont(String path, int style, int size, int encoding,
        byte embedType, long offset) throws IOException, PdfException
    {
        super(null, style, size, encoding, embedType);
        t = TtfReader.fileReader(path, offset);
        this.type = TRUE_TYPE;

        this.style = t.getFontStyle() | (style & 0xfffffff9);
        /* ignore BOLD and ITALIC styles */

        this.name = t.getFontBaseName();
        this.ascent = t.getFontAscent();
        this.avgWidth = t.getFontAvgWidth();
        this.capHeight = t.getFontCapHeight();
        this.descent = t.getFontDescent();
        this.fontBBox = new PdfArray(t.getFontBBox());
        this.italicangle = (int) t.getFontItalicAngle();
        this.maxWidth = t.getFontMaxwidth();

        setFontDescriptor();
        if (embedType == PdfFont.EMBED_SUBSET)
        {
            createSubsetPrefix();
        }
    }

    private void createSubsetPrefix()
    {
        subsetPrefix = "RBCNJV+";
    }
    
    private void setFontDescriptor()
    {
        HashMap hm = new HashMap();
        
        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(PDF_FDESCRIPTOR));
        hm.put(FLAGS, new PdfInteger(flags));
        hm.put(FONT_BBOX, fontBBox);
        hm.put(ASCENT, new PdfInteger(ascent));
        hm.put(DESCENT, new PdfInteger(descent));
        hm.put(CAPHEIGHT, new PdfInteger(capHeight));
        hm.put(AVGWIDTH, new PdfInteger(avgWidth));
        hm.put(MAXWIDTH, new PdfInteger(maxWidth));
        hm.put(STEMH, new PdfInteger(78));
        hm.put(STEMV, new PdfInteger(78));
        hm.put(ITALIC_ANGLE, new PdfInteger(Math.round(italicangle)));
        
        this.fontDescriptor = new PdfDict(hm);
    }
    
    public synchronized void setStyle(int style)
    {
        if ((this.style & STROKE_AND_FILL) == STROKE_AND_FILL
            && (style & STROKE) == STROKE)
        {
            this.style &= 0xffffffdf;
            /* erase STROKE_AND_FILL style */                
        }
        if ((this.style & STROKE) == STROKE
            && (style & STROKE_AND_FILL) == STROKE_AND_FILL) 
        {
            this.style &= 0xffffffef; 
            /* erase STROKE style */                
        }

        this.style |= (style & 0xfffffff9);
        /* ignore BOLD and ITALIC styles */
    }

    public String getBaseFontName()
    {
        String s = this.name;
        
        switch (style)
        {
            case PdfFont.BOLD | PdfFont.ITALIC:
                s += TTF_SEPARATOR + TTF_BOLD + TTF_ITALIC;
                break;

            case PdfFont.BOLD:
                s += TTF_SEPARATOR + TTF_BOLD;
                break;
            
            case PdfFont.ITALIC:
                s += TTF_SEPARATOR + TTF_ITALIC;
                break;
            
            default:
                break;
        }
        
        return subsetPrefix != null ? subsetPrefix + s : s;
    }

    public PdfDict getFontDescriptor()
    {
        return this.fontDescriptor;
    }

    public int[] getWidths()
    {
        return this.widths;
    }

    public int getWidth(char c)
    {
        /*Since we are not creating the widths array beforehand,
        we have to call t.getGlyphWidthFromCMAP(int c) directly.
        So encoding consideration will come into picture here.
        i.e. what int c to pass to t.getGlyphWidthFromCMAP will be
        encoding dependent*/

        return ( !Character.isSpaceChar(c) && Character
            .isWhitespace(c)) ? 0 : t.getGlyphWidthFromCMAP(c);
        
        /*return 1000;*/ /*default width*/
    }

    public int getWidth(String text)
    {
        int total = 0;
        
        for (int i = 0, limit = text.length(); i < limit; i++)
        {
            total += getWidth(text.charAt(i));
        }
        return total;
    }

    public double getWidth(char c, int mu)
    {
        return PdfMeasurement.convertToMeasurementUnit(mu,
            getWidth(c) * 0.001f * this.size);
    }

    public double getWidth(String text, int mu)
    {
        double total = 0;
        
        for (int i = 0, limit = text.length(); i < limit; i++)
        {
            total += getWidth(text.charAt(i), mu);
        }
        return total;
    }

    public int getUnderlinePosition()
    {
        return 100; //0.1 points
    }

    public int getUnderlineThickness()
    {
        return 50; //0.05 points
    }

    public int getFirstChar()
    {
        return firstChar;
    }

    public int getLastChar()
    {
        return lastChar;
    }

    public double getHeight()
    {
        double height = t.getFontHeight(); //this is in pixels
        double retVal = height == 0 ? size : height * size;
        
        return PdfMeasurement.convertToMeasurementUnit(
            PdfMeasurement.MU_PIXELS, retVal);
    }

    public PdfStream createStream(byte embedType) throws IOException,
        PdfException
    {
        return embedType != 0 ? t.createStream(embedType,
            charCodesUsed, firstChar, lastChar) : null;
    }

    public void setWidths()
    {
        for (firstChar = 0; firstChar < 256; ++firstChar)
        {
            if (charCodesUsed[firstChar] != 0)
                break;
        }
        for (lastChar = 255; lastChar >= firstChar; --lastChar)
        {
            if (charCodesUsed[lastChar] != 0)
                break;
        }
        if (firstChar > 255)
        {
            firstChar = 255;
            lastChar = 255;
        }
        
        this.widths = t.getFontWidths(firstChar, lastChar);
    }
    
    public void getData(HashMap hm) throws IOException, PdfException
    {
        setWidths();
        
        PdfDict fontDescriptor = getFontDescriptor(); 
        fontDescriptor.getMap().put(new PdfName(
            PDF_FONTNAME), new PdfName(getBaseFontName()));

        hm.put(new PdfName(PDF_SUBTYPE), new PdfName(
            PDF_TRUETYPE));
        hm.put(new PdfName(PDF_BASEFONT),
            new PdfName(getBaseFontName()));
        hm.put(new PdfName(PDF_FIRSTCHAR),
            new PdfInteger(getFirstChar()));
        hm.put(new PdfName(PDF_LASTCHAR),
            new PdfInteger(getLastChar()));
        hm.put(new PdfName(PDF_ENCODING),
            new PdfName(PDF_WINANSIENCODING));
        
        hm.put(new PdfName(PDF_FDESCRIPTOR),
            fontDescriptor);
        hm.put(new PdfName(PDF_WIDTHS), new PdfArray(
            getWidths()));
        
        PdfStream stm = createStream(getEmbedType());
        if (stm != null)
        {
            hm.put(new PdfName(RUBICON_EMBEDDED), stm);
        }
    }

    public Object clone()
    {
        return (PdfTrueTypeFont) super.clone();
    }
    
    protected void finalize() throws Throwable
    {
        t.dispose();
    }
}
