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
 * @author atanu
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public interface PdfPreferences
{
    public static final int HIDE_TOOLBAR = 1;

    public static final int HIDE_MENUBAR = 2;

    public static final int HIDE_WINDOWUI = 4;

    public static final int FIT_WINDOW = 8;

    public static final int CENTER_WINDOW = 16;

    public static final int DISPLAY_DOC_TITLE = 32;
    
    public static final class Direction
    {
        public static final int LEFT_TO_RIGHT = 64;

        public static final int RIGHT_TO_LEFT = 128;
    }
    
    public static final class NonFullScreenPageMode
    {
        public static final int NONE = 256;

        public static final int OUTLINES = 512;

        public static final int THUMBS = 1024;

        public static final int OC = 2048;
    }
}