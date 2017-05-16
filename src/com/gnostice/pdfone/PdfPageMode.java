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
 * This interface defines constants for specifying how document 
 * should be displayed.
 * 
 * @since 1.0
 * @version 1.0
 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfBookmark.ExampleSyntax.htm#PdfDocument_getPageMode">example</a>.
 */
public interface PdfPageMode
{
    /**
     * Constant to prevent display of document outline and thumbnail images.   
     */
    public static final int USENONE = 0;

    /**
     * Constant to display document outline.  
     */
    public static final int USEOUTLINES = 1;

    /**
     * Constant to display thumnail images.
     */
    public static final int USETHUMBS = 2;

    /**
     * Constant to display document in fullscreen mode.
     */
    public static final int FULLSCREEN = 3;

    /**
     * Constant to display option content group panel.  
     */
    public static final int USEOC = 4;

    /**
     * Constant to display Attachments panel.
     */
    public static final int USEATTACHMENTS = 5;
}