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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author atanu
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
final class PdfDate
{
    static String CurrentDate()
    {
        Calendar now = Calendar.getInstance();
        Date today = now.getTime();

        SimpleDateFormat formatter = new SimpleDateFormat(
            Usable.PDF_DATE_FORMAT);
        String dateNewFormat = formatter.format(today);

        StringBuffer strBuff = new StringBuffer(Usable.PDF_DATE
            + dateNewFormat);
        strBuff.insert(strBuff.length() - 2
            , Usable.PDF_SINGLE_QUOTES);
        strBuff.append(Usable.PDF_SINGLE_QUOTES);

        return (strBuff.toString());
    }
}