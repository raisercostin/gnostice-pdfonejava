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

package com.gnostice.pdfone.graphics;

import java.awt.Color;

public class PdfPen implements Cloneable
{
    public static final double DEFAULT_WIDTH = 0.1;

    public static final int DEFAULT_MITERLIMIT = 10;
    
    public static final int CAPSTYLE_BUTT = 0;
    
    public static final int CAPSTYLE_ROUND = 1;
    
    public static final int CAPSTYLE_PROJECTING_SQUARE = 2;
    
    public static final int JOINSTYLE_MITER = 0;
    
    public static final int JOINSTYLE_ROUND = 1;
    
    public static final int JOINSTYLE_BEVEL = 2;
    
    public double dashLength;
    
    public double dashGap;
    
    public double dashPhase;
    
    public double width;
    
    public int capStyle;
    
    public int joinStyle;
    
    public double miterLimit;
    
    public Color strokeColor;
    
    public PdfPen()
    {
        dashLength = 0;
        dashGap = 0;
        dashPhase = 0;
        width = DEFAULT_WIDTH; //0 width is not recommended
        capStyle = CAPSTYLE_BUTT; //butt style
        joinStyle = JOINSTYLE_MITER; //miter join
        miterLimit = DEFAULT_MITERLIMIT;
        strokeColor = Color.BLACK;
    }
   
    public Object clone()
    {
        PdfPen clone = null;
        try
        {
            clone = (PdfPen) super.clone();
            if (this.strokeColor != null)
            {
                clone.strokeColor = new Color(strokeColor.getRed(),
                    strokeColor.getGreen(), strokeColor.getBlue());
            }
        }
        catch (CloneNotSupportedException cnse)
        {
        }

        return clone;
    }
}
