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

package com.gnostice.pdfone.encodings;

import java.lang.reflect.Method;

public class PdfEncodings
{
    public static final int WINANSI = 0;

    public static final int CP1252 = 0;
    
    public static final int LATIN_1 = 0;
    
    public static final int UTF_16BE = 1; 

    private static String[] encodings = new String[] { 
        "Cp1252" };

    public static byte[] getBytes(String s, int encoding)
    {
        byte[] retVal = null;
        String name = PdfEncodings.class.getPackage().getName()
            + "." + encodings[encoding];
        
        try
        {
            Method m = Class.forName(name).getMethod("getBytes",
                new Class[] { String.class });
            retVal = (byte[]) m.invoke(null, new Object[]{s});
        }
        catch (Exception e) { }
        
        return retVal;
    }

    public static char[] getChars(byte[] bytes, int encoding)
    {
        char[] retVal = null;
        String name = PdfEncodings.class.getPackage().getName()
            + "." + encodings[encoding];
        
        try
        {
            Method m = Class.forName(name).getMethod("getChars",
                new Class[] { byte[].class });
            retVal = (char[]) m.invoke(null, new Object[]{bytes});
        }
        catch (Exception e) { /*Should not be thrown ever*/ }
        
        return retVal;
    }
}
