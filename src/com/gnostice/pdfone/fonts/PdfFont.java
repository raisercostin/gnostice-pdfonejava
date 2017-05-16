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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;

import com.gnostice.pdfone.PdfArray;
import com.gnostice.pdfone.PdfDict;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfName;
import com.gnostice.pdfone.Usable;
import com.gnostice.pdfone.encodings.PdfEncodings;
import com.gnostice.pdfone.readers.TtcReader;

/**
 * This class represents a font. When creating a non-standard font, 
 * it can be specified that the font file needs to embedded in the
 * document. A "font subset" representing only the glyphs used in the
 * document can also be embedded in a document in place of the entire
 * font file.
 * 
 * @version 1.0
 * @since 1.0
 */
public abstract class PdfFont implements Usable, Cloneable
{
    protected static HashMap builtInFonts;
    
    public static final PdfName FONT_DESCRIPTOR = new PdfName(
        PDF_FDESCRIPTOR);

    public static final int TYPE_1 = 1;

    public static final int TRUE_TYPE = 2;

    public static final int CJK_TYPE = 3;

    public static final int CID_TYPE_2 = 4;

    public static final int CID_TYPE_0 = 5;

    /**
     * Style for normal type.
     */
    public static final int PLAIN = 1;

    /**
     * Style for bold type.
     */
    public static final int BOLD = 2;

    /**
     * Style for italic type.
     */
    public static final int ITALIC = 4;

    /**
     * Style for underline decoration.
     */
    public static final int UNDERLINE = 8;

    /**
     * Style for stroked text rendering.
     */
    public static final int STROKE = 16;

    /**
     * Style for stroked and filled text rendering.
     */
    public static final int STROKE_AND_FILL = 32;

    /**
     * Constant for embedding entire font in the document.
     */
    public static final byte EMBED_FULL = 1;
    
    /**
     * Constant for embedding font subset in the document.
     */
    public static final byte EMBED_SUBSET = 2;
    
    protected String name;
    
    protected byte embedType;
    
    protected int size;

    protected int style; /* bold, italic, underline */

    protected int type;

    protected int encoding;

    protected int strokeWidth;

    protected Color color;

    protected Color strokeColor;
    
    protected int underlinePosition;
    
    protected int underlineThickness;
    
    protected PdfDict fontDescriptor;
    
    protected int[] widths;
    
    protected int flags;
    
    protected PdfArray fontBBox;
    
    protected int ascent;
    
    protected int descent;
    
    protected int capHeight;
    
    protected int avgWidth;
    
    protected int maxWidth;
    
    protected int stemH;
    
    protected int stemV;
    
    protected int italicangle;
    
    protected int firstChar;
    
    protected int lastChar;
    
    /* For subset embedding */
    protected int[] charCodesUsed;
    
    protected String subsetPrefix; 
    
    static class Sort implements Comparator
    {
        public int compare(Object arg0, Object arg1)
        {
            int val1 = ((int[]) arg0)[0];
            int val2 = ((int[]) arg1)[0];
            return val1 < val2 ? -1 : val1 == val2 ? 0 : 1;
        }
    }

    static
    {
        builtInFonts = new HashMap();
        builtInFonts.put("COURIER", new Integer(1));
        builtInFonts.put("COURIERNEW", new Integer(2));
        builtInFonts.put("COURIER-BOLD", new Integer(3));
        builtInFonts.put("COURIER-OBLIQUE", new Integer(4));
        builtInFonts.put("COURIER-BOLDOBLIQUE", new Integer(5));
        builtInFonts.put("HELVETICA", new Integer(6));
        builtInFonts.put("ARIAL", new Integer(7));
        builtInFonts.put("HELVETICA-BOLD", new Integer(8));
        builtInFonts.put("HELVETICA-OBLIQUE", new Integer(9));
        builtInFonts.put("HELVETICA-BOLDOBLIQUE", new Integer(10));
        builtInFonts.put("TIMES-ROMAN", new Integer(11));
        builtInFonts.put("TIMESNEWROMAN", new Integer(12));
        builtInFonts.put("TIMES-BOLD", new Integer(13));
        builtInFonts.put("TIMES-ITALIC", new Integer(14));
        builtInFonts.put("TIMES-BOLDITALIC", new Integer(15));
        builtInFonts.put("SYMBOL", new Integer(16));
        builtInFonts.put("ZAPFDINGBATS", new Integer(17));
    }
    
    public Object clone()
    {
        PdfFont clone = null;
        try
        {
            clone = (PdfFont) super.clone();
            clone.color = new Color(color.getRed(), color.getGreen(),
                color.getBlue());
            clone.strokeColor = new Color(strokeColor.getRed(),
                strokeColor.getGreen(), strokeColor.getBlue());
        }
        catch (CloneNotSupportedException cnse)
        {
        }

        return clone;
    }

    private static String toPdfName(String name)
    {
        String s = new String();
		boolean escape = false;
		
		for (int i = 0, limit = name.length(); i < limit; i++)
		{
			char ch = name.charAt(i);
			
			switch (ch)
			{
				case PDF_TAB :
				case PDF_NEWLINE :
				case PDF_FORMFEED :
				case PDF_CARRIAGE :
				case PDF_SP :
				case PDF_NAMESTART :
				case PDF_HEXSTRINGSTART :
				case PDF_HEXSTRINGEND :
				case PDF_ARRAYSTART :
				case PDF_ARRAYEND :
				case PDF_LITERALSTRINGSTART :
				case PDF_LITERALSTRINGEND :
				case '#' :
				case '%' :
				case '\b' :
				case '{' :
				case '}' :
					escape = true;
					break;
				default :
					escape = ((ch < 33) || (ch > 126)); //as per PDF spec.
					break;
			}
			
			if (escape)
			{
			    continue;
			}
			else s += ch;
		}
        return s;
    }

    /**
     * Returns a new <code>PdfFont</code> object created for the
     * specified font with specified size and character encoding.
     * 
     * @param name
     *            name of the standard font or the pathname of the
     *            font file
     * @param size
     *            (expressed in page measurement units) size of the
     *            font
     * @param encoding
     *            constant specifying the font's character encoding
     * @return 
     *            a new <code>PdfFont</code> object
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfEncodings
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#create_String_int_int">example</a>.
     */
    public static PdfFont create(String name, int size, int encoding)
        throws IOException, PdfException
    {
        name = toPdfName(name);
        if (builtInFonts.get(name.toUpperCase()) != null)
        {
            return new PdfType1Font(name, size, encoding);
        }
        else
        {
            File file = new File(name);
            String fileName = file.getName();
            if (fileName.toUpperCase().endsWith(".OTF")
                || fileName.toUpperCase().endsWith(".TTF"))
            {
                return encoding == PdfEncodings.CP1252 ? new PdfTrueTypeFont(
                    name, size, encoding)
                    : new PdfCIDType2Font(name, size, encoding);
            }
            else if (fileName.toUpperCase().indexOf(".TTC") >= 0)
            {
                TtcReader t = TtcReader.fileReader(name);
                long offset = t.getOffset();
                name = t.getFileName();
                t.dispose();
 
                return encoding == PdfEncodings.CP1252 ? new PdfTrueTypeFont(
                    name, size, encoding, offset)
                    : new PdfCIDType2Font(name, size, encoding, offset);
            }
            else
            {
                throw new PdfException("Unsupported font file or format.");
            }
        }
    }

    /**
     * Returns a new <code>PdfFont</code> object created for the
     * specified font with specified style, size, and character
     * encoding.
     * 
     * @param name
     *            name of the standard font or the pathname of the
     *            font file
     * @param style
     *            constant or combined value of constants specifying
     *            the style of the font
     * @param size
     *            (expressed in page measurement units) size of the
     *            font
     * @param encoding
     *            constant specifying the font's character encoding
     * @return 
     *            a new <code>PdfFont</code> object
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfEncodings
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#create_String_int_int_int">example</a>.
     */
    public static PdfFont create(String name, int style, int size,
        int encoding) throws IOException, PdfException
    {
        name = toPdfName(name);
        if (builtInFonts.get(name.toUpperCase()) != null)
        {
            return new PdfType1Font(name, style, size, encoding);
        }
        else
        {
            File file = new File(name);
            String fileName = file.getName();
            if (fileName.toUpperCase().endsWith(".OTF")
                || fileName.toUpperCase().endsWith(".TTF"))
            {
                return encoding == PdfEncodings.CP1252 ? new PdfTrueTypeFont(
                    name, style, size, encoding)
                    : new PdfCIDType2Font(name, style, size, encoding);
            }
            else if (fileName.toUpperCase().indexOf(".TTC") >= 0)
            {
                TtcReader t = TtcReader.fileReader(name);
                long offset = t.getOffset();
                name = t.getFileName();
                t.dispose();

                return encoding == PdfEncodings.CP1252 ? new PdfTrueTypeFont(
                    name, style, size, encoding, offset)
                    : new PdfCIDType2Font(name, style, size, encoding, offset);
            }
            else
            {
                throw new PdfException("Unsupported font file or format.");
            }
        }
    }
    
    /**
     * Returns a new <code>PdfFont</code> object created for the
     * specified font with specified size, character encoding, and
     * embedding method.
     * 
     * @param name
     *            name of the built-in font or pathname of the font
     *            file
     * @param size
     *            size of the font
     * @param encoding
     *            constant specifying the font's character encoding
     * @param embedType
     *            constant specifying how the font needs to be 
     *            embedded
     * @return 
     *            a new <code>PdfFont</code> object
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfEncodings
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#create_String_int_int_byte">example</a>.
     */
    public static PdfFont create(String name, int size, int encoding,
        byte embedType) throws IOException, PdfException
    {
        name = toPdfName(name);
        if (builtInFonts.get(name.toUpperCase()) != null)
        {
            return new PdfType1Font(name, size, encoding);
        }
        else
        {
            File file = new File(name);
            String fileName = file.getName();
            if (fileName.toUpperCase().endsWith(".OTF")
                || fileName.toUpperCase().endsWith(".TTF"))
            {
                return encoding == PdfEncodings.CP1252 ? new PdfTrueTypeFont(
                    name, size, encoding, embedType)
                    : new PdfCIDType2Font(name, size, encoding, embedType);
            }
            else if (fileName.toUpperCase().indexOf(".TTC") >= 0)
            {
                TtcReader t = TtcReader.fileReader(name);
                long offset = t.getOffset();
                name = t.getFileName();
                t.dispose();

                return encoding == PdfEncodings.CP1252 ? new PdfTrueTypeFont(
                    name, size, encoding, embedType, offset)
                    : new PdfCIDType2Font(name, size, encoding, embedType, offset);
            }
            else
            {
                throw new PdfException("Unsupported font file or format.");
            }
        }
    }

    /**
     * Returns a new PdfFont object created for the specified font
     * with specified style, size, character encoding, and embedding
     * method.
     * 
     * @param name
     *            name of the standard font or the pathname of the
     *            font file
     * @param style
     *            constant or combined value of constants specifying
     *            the style of the font
     * @param size
     *            (expressed in page measurement units) size of the
     *            font
     * @param encoding
     *            constant specifying the font's character encoding
     * @param embedType
     *            constant specifying how the font needs to be 
     *            embedded
     * @return 1.0
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfEncodings
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#create_String_int_int_int_byte">example</a>.
     */
    public static PdfFont create(String name, int style, int size,
        int encoding, byte embedType) throws IOException, PdfException
    {
        name = toPdfName(name);
        if (builtInFonts.get(name.toUpperCase()) != null)
        {
            return new PdfType1Font(name, style, size, encoding);
        }
        else
        {
            File file = new File(name);
            String fileName = file.getName();
            if (fileName.toUpperCase().endsWith(".OTF")
                || fileName.toUpperCase().endsWith(".TTF"))
            {
                return encoding == PdfEncodings.CP1252 ? new PdfTrueTypeFont(
                    name, style, size, encoding, embedType)
                    : new PdfCIDType2Font(name, style, size, encoding,
                        embedType);
            }
            else if (fileName.toUpperCase().indexOf(".TTC") >= 0)
            {
                TtcReader t = TtcReader.fileReader(name);
                long offset = t.getOffset();
                name = t.getFileName();
                t.dispose();

                return encoding == PdfEncodings.CP1252 ? new PdfTrueTypeFont(
                    name, style, size, encoding, embedType, offset)
                    : new PdfCIDType2Font(name, style, size, encoding,
                        embedType, offset);
            }
            else
            {
                throw new PdfException("Unsupported font file or format.");
            }
        }
    }

    protected PdfFont(String name, int size, int encoding)
    {
        this.name = name;
        this.size = size < 0 ? -size : size;
        this.style = PLAIN;
        this.encoding = encoding;
        this.color = Color.BLACK;
        this.strokeColor = Color.BLACK;
        charCodesUsed = new int[256];
    }

    protected PdfFont(String name, int style, int size, int encoding)
    {
        this.name = name;
        this.size = size < 0 ? -size : size;
        this.style = style;
        this.encoding = encoding;
        this.color = Color.BLACK;
        this.strokeColor = Color.BLACK;
        charCodesUsed = new int[256];
    }

    protected PdfFont(String name, int size, int encoding, byte embedType)
    {
        this.name = name;
        this.size = size < 0 ? -size : size;
        this.style = PLAIN;
        this.encoding = encoding;
        this.color = Color.BLACK;
        this.strokeColor = Color.BLACK;
        this.embedType = embedType;
        charCodesUsed = new int[256];
    }

    protected PdfFont(String name, int style, int size, int encoding,
        byte embedType)
    {
        this.name = name;
        this.size = size < 0 ? -size : size;
        this.style = style;
        this.encoding = encoding;
        this.color = Color.BLACK;
        this.strokeColor = Color.BLACK;
        this.embedType = embedType;
        charCodesUsed = new int[256];
    }

    /**
     * Returns color of this font.
     * 
     * @return color of the font.
     * @since 1.0
     * @see #setColor(Color)
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#getColor">example</a>. 
     */
    public synchronized Color getColor()
    {
        return color;
    }

    /**
     * Specifies color for this font.
     * 
     * @param color color for the font 
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#setColor">example</a>. 
     */
    public synchronized void setColor(Color color)
    {
        this.color = color;
    }

    /**
     * Returns color used to stroke this font's characters.
     * 
     * @return color used to stroke the font's characters
     * @since 1.0
     * @see #setStrokeColor(Color)
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#getStrokeColor">example</a>.
     */
    public synchronized Color getStrokeColor()
    {
        return strokeColor;
    }

    /**
     * Specifies color that needs to be used to stroke this font's
     * characters.
     * 
     * @param strokeColor
     *            color that needs to be used to stroke the font's
     *            characters
     * @since 1.0
     * @see #getStrokeColor()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#setStrokeColor">example</a>.
     */
    public synchronized void setStrokeColor(Color strokeColor)
    {
        if ((style & STROKE) == STROKE
            || (style & STROKE_AND_FILL) == STROKE_AND_FILL)
        {
            this.strokeColor = strokeColor;
        }
    }

    /**
     * Returns name of this font.
     * 
     * @return name of the font
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#getName">example</a>.
     */
    public synchronized String getName()
    {
        return name;
    }

    /**
     * Returns user-defined size for this font.
     * 
     * @return user-defined size for the font
     * @since 1.0
     * @see #setSize(int)
     */
    public synchronized int getSize()
    {
        return size;
    }

    /**
     * Specifies a size for this font.
     * 
     * @param size size for the font
     * @since 1.0
     * @see #getSize()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#setSize">example</a>.
     */
    public synchronized void setSize(int size)
    {
        this.size = size;
    }

    public synchronized int getType()
    {
        return type;
    }

    /**
     * Returns constant or combined value of constants representing
     * this font's styles.
     * 
     * @return constant or combined value of constants representing 
     *         the font's styles
     * @since 1.0
     * @see #setStyle(int)
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#getStyle">example</a>.
     */
    public synchronized int getStyle()
    {
        return style;
    }

    /**
     * Returns width of strokes used by this font's characters.
     * 
     * @return width of strokes used by the font's characters
     * @see #setStrokeWidth(int)
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#getStrokeWidth">example</a>.
     * @since 1.0
     */
    public synchronized int getStrokeWidth()
    {
        return strokeWidth;
    }

    /**
     * Specifies width of strokes that need to be used by this font's
     * characters.
     * 
     * @param strokeWidth
     *            width of strokes that need to be used by the font's
     *            characters
     * @since 1.0
     * @see #getStrokeWidth()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#setStrokeWidth">example</a>.
     */
    public synchronized void setStrokeWidth(int strokeWidth)
    {
        if ((style & STROKE) == STROKE
            || (style & STROKE_AND_FILL) == STROKE_AND_FILL)
        {
            this.strokeWidth = strokeWidth;
        }
    }

    /**
     * Returns constant representing this font's character encoding.
     * 
     * @return constant representing the font's character encoding
     * @since 1.0
     * @see PdfEncodings
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#getEncoding">example</a>.
     */
    public int getEncoding()
    {
        return encoding;
    }

    public synchronized int getAscent()
    {
        return ascent;
    }

    public synchronized int getAvgWidth()
    {
        return avgWidth;
    }

    public synchronized int getCapHeight()
    {
        return capHeight;
    }

    public synchronized int getDescent()
    {
        return descent;
    }

    /**
     * Returns constant or combined value of constants representing
     * styles used by this font.
     * 
     * @return constant or combined value of constants representing
     *         styles used by the font
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#getFlags">example</a>.
     */
    public synchronized int getFlags()
    {
        return flags;
    }

    public synchronized PdfArray getFontBBox()
    {
        return fontBBox;
    }

    /**
     * Returns slope of the dominant vertical strokes of this font.
     * 
     * @return (expressed in degrees and measured counterclockwise)
     *         slope of the dominant vertical strokes of the font
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#getItalicangle">example</a>.
     */
    public synchronized int getItalicangle()
    {
        return italicangle;
    }

    public synchronized int getMaxWidth()
    {
        return maxWidth;
    }

    public synchronized int getStemH()
    {
        return stemH;
    }

    public synchronized int getStemV()
    {
        return stemV;
    }

    /**
     * Returns constant representing this font's embedding method.
     * 
     * @return constant representing the font's embedding method
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#getEmbedType">example</a>.
     */
    public synchronized byte getEmbedType()
    {
        return embedType;
    }

    public synchronized int getFirstChar()
    {
        return firstChar;
    }

    public synchronized int getLastChar()
    {
        return lastChar;
    }

    public synchronized byte[] updateGlyphList(String text)
        throws PdfException, IOException
    {
        byte[] ba = PdfEncodings.getBytes(text, this.encoding);
        if (ba != null)
        {
            for (int i = 0; i < ba.length; ++i)
            {
                charCodesUsed[((int) ba[i]) & 0xff] = 1;
            }
        }

        return ba;
    }
    
    /**
     * Excludes trailing white space characters in specified text
     * based on <code>excludeEndSpaces</code> and returns width of
     * resultant text in this font in specified measurement unit.
     * 
     * @param text
     *            piece of text 
     * @param mu
     *            measurement unit in which the width is to be
     *            expressed
     * @param excludeEndSpaces
     *            whether to exclude trailing white space characters 
     * @return 
     *            (expressed in specified measurement unit) width of 
     *            the resultant text
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#getWidth_String_int_boolean">example</a>.
     */
    public double getWidth(String text, int mu,
        boolean excludeEndSpaces)
    {
        int index = text.length();
        if (index != 0)
        {
            if (excludeEndSpaces)
            {
                char c;
                do
                {
                    if (--index == -1)
                    {
                        break;
                    }
                    c = text.charAt(index);
                    
                } while (Character.isWhitespace(c));
                
                return getWidth(text.substring(0, index + 1), mu);
            }
            else
            {
                return getWidth(text, mu);
            }
        }
        
        return 0;
    }

    /**
     * Specifies this font's styles.
     * 
     * @param style
     *            constant or combined value of constants specifying
     *            this font's styles.
     * @since 1.0
     * @see #getStyle()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#setStyle">example</a>.
     */
    public abstract void setStyle(int style);
    
    /**
     * Returns height of this font as specified in its font file.
     * 
     * @return height of the font as specified in its font file
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#getHeight">example</a>.
     */
    public abstract double getHeight();
    
    /**
     * Returns an array of glyph widths of all characters in this
     * font, arranged in the order of their character codes.
     * 
     * @return an array of glyph widths of all characters in the font,
     *         arranged in the order of their character codes
     * @since 1.0
     */
    public abstract int[] getWidths();

    /**
     * Returns width of character <code>c</code>, as mentioned in
     * this font's file.
     * 
     * @param c
     *            single character
     * @return    
     *            width of this character as mentioned in the font's
     *            file
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#getWidth_char">example</a>.
     */
    public abstract int getWidth(char c);
    
    /**
     * Returns sum of widths of all characters in <code>text</code>, 
     * as mentioned in this font's file.
     * 
     * @param text
     *            piece of text
     * @return 
     *            sum of widths of all characters 
     *            <code>text</code>, as mentioned in this font's file
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#getWidth_String">example</a>.
     */
    public abstract int getWidth(String text);
    
    /**
     * Returns width of character <code>c</code> in this font in
     * specified measurement unit.
     * 
     * @param c
     *            single character
     * @param mu
     *            measurement unit in which the width needs to be
     *            expressed
     * @return 
     *            (expressed in specified measurement unit) width of 
     *            the character in the font
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#getWidth_char_int">example</a>.
     */
    public abstract double getWidth(char c, int mu);
 
    /**
     * Returns width of <code>text</code> in the font in specified 
     * measurement unit.
     * 
     * @param text
     *            piece of text
     * @param mu
     *            measurement unit in which the width needs to be
     *            expressed
     * @return 
     *            (expressed in specified measurement unit) width of 
     *            the text in the font
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfFont.ExampleSyntax.htm#getWidth_String_int">example</a>.
     */
    public abstract double getWidth(String text, int mu);

    public abstract int getUnderlinePosition();
    
    public abstract int getUnderlineThickness();
    
    public abstract void getData(HashMap hm) throws IOException,
        PdfException;
}
