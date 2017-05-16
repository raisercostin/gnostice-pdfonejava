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

class CodePage
{
    static byte[] bytes;
    
    static char[] unicodeMap;
    
    private static int index(char ch)
    {
        for (int i = 0; i < unicodeMap.length; ++i)
        {
            if (unicodeMap[i] == ch)
            {
                return i;
            }
        }

        return -1;
    }

    private static int index(short b)
    {
        for (int i = 0; i < bytes.length; ++i)
        {
            if (bytes[i] == b)
            {
                return i;
            }
        }

        return -1;
    }

    public static byte[] getBytes(String s)
    {
        int limit = s.length();
        byte[] retVal = new byte[limit];
        for (int i = 0, index; i < limit; ++i)
        {
            index = index(s.charAt(i));
            retVal[i] = index != -1 ? bytes[index] : -1;
        }

        return retVal;
    }

    public static char[] getChars(byte[] ba)
    {
        char[] retVal = new char[ba.length];
        for (int i = 0, index; i < ba.length; ++i)
        {
            index = index(ba[i]);
            retVal[i] = (char) (index != -1 ? unicodeMap[index]
                : -1);
        }

        return retVal;
    }
}
