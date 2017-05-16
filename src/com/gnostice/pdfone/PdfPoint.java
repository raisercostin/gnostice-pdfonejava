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

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * This class represents a point on a page. Its position is expressed
 * in x-y coordinates. However, these coordinates are applied in a
 * literal sense. As a result, <b>the actual position of a
 * <code>PdfPoint</code> object on a page is wholly dependent on
 * whatever measurement unit that is currently applicable</b>.
 * <p>
 * For example, on a page where the measurement unit is inch, the x-y
 * coordinates of a point will be applied in inches, literally. On a
 * page with pixel as the measurement unit, coordinates of the same
 * point will applied in pixels, literally. As this can cause serious
 * shifts in position, care has to be taken when reusing a point in
 * different places or in different situations.
 * </p>
 * <p>
 * Some classes may have methods that specify a <code>PdfPoint</code>
 * object and a particular measurement unit as arguments. In these
 * cases, the coordinates of the <code>PdfPoint</code> object are
 * applied in the specified measurement unit. When no measurement unit
 * is specified or can be determined, the coordinates are applied in
 * default as points, or <i>points</i> as in "one inch equals 72
 * <i>points</i> equals 96 pixels."
 * </p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class PdfPoint
{
    protected double x;
    
    protected double y;

    /**
     * Constructs a point at position 
     * (<code>x</code>, <code>y</code>).
     * 
     * @param x
     *            x-coordinate of the point
     * @param y
     *            y-coordinate of the point
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPoint.ExampleSyntax.htm#PdfPoint_double_double">example</a>.
     */
    public PdfPoint(double x, double y)
    {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Constructs a point at the same position as specified
     * <code>PdfPoint</code> object. 
     * 
     * @param p a point
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPoint.ExampleSyntax.htm#PdfPoint_PdfPoint">example</a>.
     */
    public PdfPoint(PdfPoint p)
    {
        this.x = p.x;
        this.y = p.y;
    }
    
    /**
     * Constructs a point at the same position as specified
     * <code>Point</code> object.
     * 
     * @param p
     *            a point
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPoint.ExampleSyntax.htm#PdfPoint_Point">example</a>.
     */
    public PdfPoint(Point p)
    {
        this.x = p.x;
        this.y = p.y;
    }
    
    /**
     * Constructs a point at the same position as specified
     * <code>Point2D</code> object.
     * 
     * @param p a point
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPoint.ExampleSyntax.htm#PdfPoint_Point2D">example</a>.
     */
    public PdfPoint(Point2D p)
    {
        this.x = p.getX();
        this.y = p.getY();
    }
    
    /**
     * Returns x-coordinate of this point.
     * 
     * @return x-coordinate of the point
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPoint.ExampleSyntax.htm#SETTERS_AND_GETTERS">example</a>.
     */
    public synchronized double getX()
    {
        return x;
    }

    /**
     * Specifies new x-coordinate for this point. 
     * 
     * @param x new x-coordinate for the point
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPoint.ExampleSyntax.htm#SETTERS_AND_GETTERS">example</a>.
     */
    public synchronized void setX(double x)
    {
        this.x = x;
    }

    /**
     * Returns y-coordinate of this point.
     * 
     * @return y-coordinate of the point
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPoint.ExampleSyntax.htm#SETTERS_AND_GETTERS">example</a>.
     */    
    public synchronized double getY()
    {
        return y;
    }

    /**
     * Specifies new y-coordinate for this point.
     * 
     * @param y new y-coordinate for the point
     * @since
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPoint.ExampleSyntax.htm#SETTERS_AND_GETTERS">example</a>.
     */
    public synchronized void setY(double y)
    {
        this.y = y;
    }

    public Object clone() throws CloneNotSupportedException
    {
        return new PdfPoint(this.x, this.y);
    }

    public String toString()
    {
        return "x = " + x + " y = " + y;
    }
}
