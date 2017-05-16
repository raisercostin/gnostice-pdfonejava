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

package com.gnostice.pdfone;

/**
 * This class is used to retrieve and 
 * specify settings related to text formatting. A single instance of 
 * the class can be associated with multiple <code>PdfPage</code> 
 * objects. An object of this class is returned when 
 * <code>getTextFormatter</code> method is used on a {@link PdfPage} 
 * object. Formatting settings specified on the 
 * <code>PdfTextFormatter</code> object will affect subsequent calls 
 * to <code>writeText</code> method for the page.
 * <p>
 * The text formatting attributes supported by this class are as 
 * follows:
 * </p>
 * <ul>
 *  <li>alignment of text to
 *   <ul>
 *    <li>left margin,</li>
 *    <li>right margin,</li>
 *    <li>both left and right margins, and</li>
 *    <li>center.</li>
 *   </ul>
 *  </li> 
 *  <li>wrapping of text</li>
 *  <li>position of first line inside a rectangle</li>
 *  <li>alignment of last line to both left and right margins</li>
 * </ul>
 * <p>
 * An object of the <code>PdfTextFormatter</code> class has the 
 * following settings as default:
 * </p>
 * 
 * <ul>
 *  <li>text wraps</li>
 *  <li>text is aligned to left margin</li>
 *  <li>first line is positioned at 0 measurement units inside rectangles</li>
 *  <li>last line is not aligned to both margins (that is aligned only to the
 *  left margin)</li>
 * </ul>
 * 
 * @version 1.0
 * @since 1.0
 */

public class PdfTextFormatter implements Cloneable 
{
    /**
     * Constant to wrap text.
     */
    public static final boolean WRAP = true;
    
    /**
     * Constant to prevent text wrapping.
     */
    public static final boolean NO_WRAP = false;
    
    /**
     * Constant to align text to left margin.
     */
    public static final int LEFT = 1;

    /**
     * Constant to align text to right margin.
     */
    public static final int RIGHT = 2;

    /**
     * Constant to align text to center.
     */
    public static final int CENTER = 4;

    /**
     * Constant to align text to both left and right margins.
     */
    public static final int JUSTIFIED = 8;

    public static final int HORIZONTAL = 1;

    public static final int VERTICAL = 2;

    public static final int LEFT_TO_RIGHT = 1;

    public static final int RIGHT_TO_LEFT = 2;

    private boolean wrap;
    
    private boolean justifyLastLine;

    private double charSpacing;

    private double wordSpacing;

    private double lineSpacing;

    private int alignment;

    private int writingMode;

    private int direction;

    private double rotation;

    private double scaling;

    private double rise;
    
    private double firstLinePosition;
    
     /**
     * Zero-argument default constructor.
     */
    public PdfTextFormatter()
    {
        this.wrap = true;
        this.alignment = LEFT;
        this.rotation = 0;
        this.writingMode = HORIZONTAL;
        this.firstLinePosition = 0;
        this.justifyLastLine = false;
    }

    public Object clone()
    {
        PdfTextFormatter clone = null;
        try
        {
            clone = (PdfTextFormatter) super.clone();
        }
        catch (CloneNotSupportedException cnse)
        {

        }

        return clone; 
    }

    /**
     * Retrieves text alignment.
     * 
     * @since 1.0
     * @return constant identifying text alignment
     * @see #setAlignment
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfTextFormatter.ExampleSyntax.htm#getAlignment">example</a>.
     */
    public synchronized int getAlignment()
    {
        return alignment;
    }

    /**
	 * Specifies text alignment.
	 * 
	 * @since 1.0
	 * @param alignment
	 *            constant specifying text alignment
	 * @see #getAlignment
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfTextFormatter.ExampleSyntax.htm#setAlignment">example</a>.
	 */
    public synchronized void setAlignment(int alignment)
    {
        this.alignment = alignment;
    }

    public synchronized double getCharSpacing()
    {
        return charSpacing;
    }

    public synchronized void setCharSpacing(double charSpacing)
    {
        this.charSpacing = charSpacing;
    }

    public synchronized int getDirection()
    {
        return direction;
    }

    public synchronized void setDirection(int direction)
    {
        this.direction = direction;
    }

    public synchronized double getLineSpacing()
    {
        return lineSpacing;
    }

    public synchronized void setLineSpacing(double lineSpacing)
    {
        this.lineSpacing = lineSpacing;
    }

    public synchronized double getRise()
    {
        return rise;
    }

    public synchronized void setRise(double rise)
    {
        this.rise = rise;
    }

    synchronized double getRotation()
    {
        return rotation;
    }

    synchronized void setRotation(double rotation)
    {
        this.rotation = rotation;
    }

    public synchronized double getScaling()
    {
        return scaling;
    }

    public synchronized void setScaling(double scaling)
    {
        this.scaling = scaling;
    }

    public synchronized double getWordSpacing()
    {
        return wordSpacing;
    }

    public synchronized void setWordSpacing(double wordSpacing)
    {
        this.wordSpacing = wordSpacing;
    }

    /**
	 * Retrieves whether text is set to wrap.
	 * 
	 * @since 1.0
	 * @return constant identifying whether text is set to wrap 
	 * @see #setWrap
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfTextFormatter.ExampleSyntax.htm#isWrap">example</a>.
	 */
    public synchronized boolean isWrap()
    {
        return wrap;
    }

    /**
     * Specifies whether text should wrap.
     * 
     * @since 1.0
     * @param wrap
     *            constant specifying whether text needs to wrapped 
     * @see #isWrap
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfTextFormatter.ExampleSyntax.htm#setWrap">example</a>.
     */
    public synchronized void setWrap(boolean wrap)
    {
        this.wrap = wrap;
    }

    public synchronized int getWritingMode()
    {
        return writingMode;
    }

    public synchronized void setWritingMode(int writingMode)
    {
        this.writingMode = writingMode;
    }
    
    /**
     * Retrieves default position where first line of text is set to
     * be written inside rectangles.
     * 
     * @since 1.0
     * @return default position inside rectangles where the first line
     *         of text is set to begin (Expressed in current
     *         measurement unit)
     * @see #setFirstLinePosition
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfTextFormatter.ExampleSyntax.htm#getFirstLinePosition">example</a>.
     */
    public synchronized double getFirstLinePosition()
    {
        return firstLinePosition;
    }
    
    /**
     * Specifies default position where first line of text should be
     * written inside rectangles. <code>firstLinePosition</code>
     * must be less than the total width of the rectangle. Else, the
     * first line defaults to the first position on the second line.
     * 
     * @since 1.0
     * @param firstLinePosition
     *            default position inside rectangles where the first
     *            line of text should begin (Applied in current
     *            measurement unit)
     * @see #getFirstLinePosition
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfTextFormatter.ExampleSyntax.htm#setFirstLinePosition">example</a>.
     */
    public synchronized void setFirstLinePosition(
        double firstLinePosition)
    {
        this.firstLinePosition = firstLinePosition;
    }
    
    /**
	 * Retrieves whether last line is aligned to both left and right 
	 * margins.
	 * 
	 * @since 1.0
	 * @return <code>true</code> if last line is aligned to both left 
	 * 		   and right margins. 
	 * 		   <code>false</code> if otherwise.
	 * @see #setJustifyLastLine
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfTextFormatter.ExampleSyntax.htm#isJustifyLastLine">example</a>.
	 */
    public synchronized boolean isJustifyLastLine()
    {
        return justifyLastLine;
    }
    
    /**
	 * Specifies whether last line should be aligned to both left and 
	 * right margins. When text is aligned to both margins, it can 
	 * suffer from poor readability. When there is not enough text on 
	 * the last line, words might be written far apart from each other.
	 * 
	 * @since 1.0
	 * @param justifyLastLine
	 *            <code>true</code> aligns last line text to both left and
	 *            right margins.
	 *            <code>false</code> defaults the alignment to the left 
	 *            margin.
	 * 
	 * @see #isJustifyLastLine
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfTextFormatter.ExampleSyntax.htm#setJustifyLastLine">example</a>.
	 */
    public synchronized void setJustifyLastLine(
        boolean justifyLastLine)
    {
        this.justifyLastLine = justifyLastLine;
    }
}
