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

import java.awt.Color;

import com.gnostice.pdfone.fonts.PdfFont;
import com.gnostice.pdfone.graphics.PdfBrush;
import com.gnostice.pdfone.graphics.PdfPen;

/**
 * @author arjun
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class PdfCell 
{
    protected double x;

    protected double y;

    protected double width;

    protected double height;

    protected int rowSpan;

    protected int colSpan;

    protected Color  backgroundColor;

	protected int alignment;
	
	protected String text;
	
	protected PdfImage image;
	
    protected PdfFormField formfield;
    
	protected double cellLeftMargin;
	
	protected double cellTopMargin;
	
	protected double cellRightMargin;
	
	protected double cellBottomMargin;
	
    protected PdfBrush brush;

    protected PdfPen  pen;
    
    protected PdfFont font;
    
    public PdfCell(int rowSpan, int colSpan)
    {
        this.height = 72;
        this.width = 72;
        this.rowSpan = rowSpan;
        this.colSpan = colSpan;
        this.alignment = PdfTextFormatter.LEFT;
    }

    public PdfCell(int rowSpan, int colSpan, Color backgroundColor,
        int alignment)
    {
        this.height = 72;
        this.width = 72;
        this.rowSpan = rowSpan;
        this.colSpan = colSpan;
        this.backgroundColor = backgroundColor;
        this.alignment = alignment;
    }

    /*public void setX(double x)
    {
        this.x = x;
    }

    public void setY(double y)
    {
        this.y = y;
    }*/

    public void setWidth(double width)
    {
        this.width = width;
    }

    public void setHeight(double height)
    {
        this.height = height;
    }

    public void setRowSpan(int rowSpan)
    {
        this.rowSpan = rowSpan;
    }

    public void setColSpan(int colSpan)
    {
        this.colSpan = colSpan;
    }

    public int getColSpan()
    {
        return colSpan;
    }

    public double getHeight()
    {
        return height;
    }

    public int getRowSpan()
    {
        return rowSpan;
    }

    public double getWidth()
    {
        return width;
    }

   /* public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }*/
    
    public int getAlignment()
    {
        return alignment;
    }
    
    public void setAlignment(int alignment)
    {
        this.alignment = alignment;
    }
    
    public Color getBackgroundColor()
    {
        return backgroundColor;
    }
    
    public void setBackgroundColor(Color backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }
    
    public Object clone()
	{
		PdfCell c = new PdfCell( this.rowSpan ,this.colSpan);
		c.x = this.x;
		c.y = this.y;
		c.width = this.width;
		c.height = this.height;
		return c;
	}

}
