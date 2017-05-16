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
 * This interface defines constants for specifying page layout
 * to be used when document is displayed.
 * 
 * @since 1.0
 * @version 1.0
 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#PageLayout">example</a>.
 */
public interface PdfPageLayout
{
    /**
     * Constant for displaying one page at a time.
     */
    int SINGLE_PAGE = 0;
    
    /**
     * Constant for displaying pages in a single column.
     */
    int ONE_COLUMN = 1;
    
    /**
     * Constant for displaying pages in two columns, with odd-numbered
     * pages on the left-side column.
     */
    int TWO_COLUMN_LEFT = 2;
    
    /**
     * Constant for displaying pages in two columns, with odd-numbered
     * pages on the right-side column.  
     */
    int TWO_COLUMN_RIGHT = 3;
    
    /**
     * Constant for displaying two pages at a time, with odd-numbered
     * pages on the left. Applicable to PDF version 1.5.
     */
    int TWO_PAGE_LEFT = 4;
    
    /**
     * Constant for displaying two pages at a time, with odd-numbered
     * pages on the right. Applicable to PDF version 1.5.
     */
    int TWO_PAGE_RIGHT = 5;
}
