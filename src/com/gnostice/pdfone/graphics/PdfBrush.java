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

public class PdfBrush implements Cloneable
{
    public static final int PATTERN_HORIZONTAL = 1;
    
    public static final int PATTERN_VERTICAL = 2;
    
    public static final int PATTERN_FORWARD_DIAGONAL = 3;
    
    public static final int PATTERN_BACKWARD_DIAGONAL = 4;
    
    public static final int PATTERN_CROSS = 5;
    
    public static final int PATTERN_DIAGONAL_CROSS = 6;
    
    public static final int PATTERN_SOLID = 7;
    
    public Color fillColor;
    
    public int brushPattern; 
    
    public PdfBrush()
    {
        fillColor = Color.BLACK;
        brushPattern = PATTERN_SOLID;
    }
    
    public Object clone()
    {
        PdfBrush newBrush = null;
        try
        {
            newBrush = (PdfBrush) super.clone();
            if (this.fillColor != null)
            {
                newBrush.fillColor = new Color(fillColor.getRed(),
                    fillColor.getGreen(), fillColor.getRed());
            }
        }
        catch (CloneNotSupportedException cnse)
        {
        }

        return newBrush;
    }
}
