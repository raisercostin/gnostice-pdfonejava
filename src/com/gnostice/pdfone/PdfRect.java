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

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * This class represents a rectangle or rectangular area on a page. A
 * <code>PdfRect</code> object is specified by x-y coordinates of
 * its top-left corner, its width, and its height. These
 * specifications of a rectangle are applied in a literal sense. As a
 * result, <b>the actual position and size of a rectangle object on a
 * page is wholly dependent on whatever measurement unit that is
 * currently applicable</b>.
 * <p>
 * For example, on a page where the measurement unit is inch, the
 * height and width of a rectangle will be applied in inches,
 * literally. On a page with pixel as the measurement unit, the
 * rectangle's position and size will be applied in pixels, literally.
 * As this can cause serious shifts in position, care has to be taken
 * when reusing a rectangle in different places or in different
 * situations.
 * </p>
 * <p>
 * Some classes may have methods that specify a <code>PdfRect</code>
 * object and a particular measurement unit as arguments. In these 
 * cases, the position and size of the rectangle are applied in the 
 * specified measurement unit. When no measurement unit is specified 
 * or cannot be determined, the height, width, and x-y coordinates 
 * of its top-left corner will be applied in points, or or points as 
 * in "one inch equals 72 <i>points</i> equals 96 pixels."
 * </p>
 * 
 * @since 1.0
 * @version 1.0
 */
public class PdfRect implements Cloneable
{
	protected double x;
	
	protected double y;
	
	protected double width;
	
	protected double height;
	
	/**
     * Constructs a <code>PdfRect</code> object at position 
     * (<code>x</code>, <code>y</code>) with specified width and 
     * height.
     * 
     * @param x
     *            x-coordinate of the top-left corner of the 
     *            rectangle
     * @param y
     *            y-coordinate of the top-left corner of the 
     *            rectangle
     * @param width
     *            width of the rectangle
     * @param height
     *            height of the rectangle
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfRect.ExampleSyntax.htm#PdfRect_double_double_double_double">example</a>.
     */
	public PdfRect(double x, double y, double width, double height)
	{
	    this.x = x;
	    this.y = y;
	    this.width = Math.abs(width);
	    this.height = Math.abs(height);
	}
	
	/**
     * Constructs a <code>PdfRect</code> object with specified 
     * <code>Rectangle</code> object.
     * 
     * @param rect
     *            a rectangle
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfRect.ExampleSyntax.htm#PdfRect_Rectangle">example</a>.
     */
	public PdfRect(Rectangle rect)
	{
	    this.x = rect.getX();
	    this.y = rect.getY();
	    this.height = Math.abs(rect.getHeight());
	    this.width = Math.abs(rect.getWidth());
	}
	
    /**
     * Constructs a <code>PdfRect</code> object with specified
     * <code>Rectangle2D</code> object.
     * 
     * @param rect a rectangle
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfRect.ExampleSyntax.htm#PdfRect_Rectangle2D">example</a>.
     */
    public PdfRect(Rectangle2D rect)
    {
        this.x = rect.getX();
        this.y = rect.getY();
        this.height = Math.abs(rect.getHeight());
        this.width = Math.abs(rect.getWidth());
    }
    
    public PdfRect(PdfArray array, double pageHeight)
    {
        double x1 = 0, y1 = 0, x2 = 0, y2 = 0;
        ArrayList arrList = (ArrayList) array.getList();
        
        if (arrList.get(0) instanceof PdfNumber)
        {
            x1 = ((PdfNumber)arrList.get(0)).getVal();
        }

        if (arrList.get(1) instanceof PdfNumber)
        {
            y1 = ((PdfNumber)arrList.get(1)).getVal();
        }

        if (arrList.get(2) instanceof PdfNumber)
        {
            x2 = ((PdfNumber)arrList.get(2)).getVal();
        }

        if (arrList.get(3) instanceof PdfNumber)
        {
            y2 = ((PdfNumber)arrList.get(3)).getVal();
        }

        x = Math.min(x1, x2);
        width = Math.abs(x1 - x2);
        y = pageHeight - Math.max(y1, y2);
        height = Math.abs(y1 - y2);
    }

    
    /**
     * Returns height of this rectangle.
     * 
     * @return height of the rectangle
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfRect.ExampleSyntax.htm#METHODS">example</a>.
     */
    public double height()
    {
        return height;
    }

    /**
     * Returns width of this rectangle.
     * 
     * @return width of the rectangle
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfRect.ExampleSyntax.htm#METHODS">example</a>.
     */
    public double width()
    {
        return width;
    }

    /**
     * Returns x-coordinate of top-left corner of this rectangle.
     * 
     * @return x-coordinate of top-left corner of the rectangle
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfRect.ExampleSyntax.htm#METHODS">example</a>.
     */
    public double getX()
    {
        return x;
    }

    /**
     * Returns y-coordinate of top-left corner of this rectangle.
     * 
     * @return y-coordinate of top-left corner of the rectangle 
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfRect.ExampleSyntax.htm#METHODS">example</a>.
     */
    public double getY()
    {
        return y;
    }
    
    /**
     * Returns distance from top of page to bottom of this rectangle.
     * 
     * @return distance from top of page to bottom of the rectangle
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfRect.ExampleSyntax.htm#METHODS">example</a>.
     */
    public double bottom()
    {
        return (y + height);
    }

    /**
     * Returns distance from left side of page to left side of this
     * rectangle.
     * 
     * @return distance from left side of page to left side of the
     *         rectangle
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfRect.ExampleSyntax.htm#METHODS">example</a>.
     */
    public double left()
    {
        return x;
    }

    /**
     * Returns distance from left side of page to right side of this
     * rectangle.
     * 
     * @return distance from left side of page to right side of this
     *         rectangle
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfRect.ExampleSyntax.htm#METHODS">example</a>.
     */
    public double right()
    {
        return (x + width);
    }

    /**
     * Returns distance from top side of page to top side of this
     * rectangle.
     * 
     * @return distance from top side of page to top side of the
     *         rectangle
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfRect.ExampleSyntax.htm#METHODS">example</a>.
     */
    public double top()
    {
        return y;
    }

    public Object clone()
    {
        try
        {
            PdfRect clone = (PdfRect) super.clone(); 
            return clone;
        }
        catch (CloneNotSupportedException e)
        {
            return null; //never
        }
    }

    public String toString()
    {
        return "[x = " + x + " y = " + y + " w = "
            + width + " h = " + height + "]";
    }
}