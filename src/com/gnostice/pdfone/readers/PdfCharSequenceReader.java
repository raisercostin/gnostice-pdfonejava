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

package com.gnostice.pdfone.readers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfCharSequenceReader
{
    static String EXPR_COMMENT = "%[^\\r\\n(?:\\r\\n)]*"
        + "(?:[\\r\\n(?:\\r\\n)])";

    static String EXPR_WHITESP = "(?:(?:\\s*)|(?:\\x00*)|(?:"
        + EXPR_COMMENT + "*))";

    public static boolean isAutoAdjustTextHeightForField(
        CharSequence cs)
    {
        int textSize = 1;
        if (cs != null)
        {
            Pattern pat = Pattern.compile(EXPR_WHITESP
                + "(\\+|\\-)?((\\d+?)+)" + EXPR_WHITESP + "Tf");
            Matcher mat = pat.matcher(cs);
            if (mat.find())
            {
                textSize = Integer.parseInt(mat.group(3));
            }
        }
        
        return textSize == 0;
    }
}
