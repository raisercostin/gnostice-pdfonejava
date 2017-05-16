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

package com.gnostice.pdfone.filters;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;

import com.gnostice.pdfone.PdfArray;
import com.gnostice.pdfone.PdfBadFileException;
import com.gnostice.pdfone.PdfDict;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfName;
import com.gnostice.pdfone.PdfObject;
import com.gnostice.pdfone.PdfStream;
import com.gnostice.pdfone.Usable;

/**
 * @author amol
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class PdfAsciiHexFilter	
{
    private static PdfAsciiHexFilter instance;
    
    private static final char EOD = '>'; /* End Of Data */
    
    private static final PdfName FILTER = new PdfName(Usable.PDF_FILTER);
    
    private static final PdfName ASCIIHEX = new PdfName(Usable.PDF_ASCIIHEX);
    
    private PdfAsciiHexFilter()
    {
        //private constructor
    }

    public static void encode(PdfStream s) throws PdfException
    {
        s.setBuffer(encode(s.getBuffer()));
        PdfDict d = s.getDictionary();
        PdfObject filter = d.getValue(FILTER);
        if (filter == null)
        {
            d.getMap().put(FILTER, ASCIIHEX);
        }
        else
        {
            if (filter instanceof PdfArray)
            {
                ((PdfArray) filter).getList().add(0, ASCIIHEX);
            }
            else if (filter instanceof PdfName)
            {
                ArrayList l = new ArrayList();
                l.add(ASCIIHEX);
                l.add(filter);
                d.getMap().put(FILTER, new PdfArray(l));
            }
            else
            {
                throw new PdfBadFileException(
                    "Improper '/Filter' entry.");
            }
        }
    }
    
    public static ByteBuffer encode(ByteBuffer bb)
    {
        if (bb == null)
        {
            return null;
        }
        ByteBuffer nbb = ByteBuffer.allocate(2 * bb.capacity() + 1);
        byte val;

        for (int i = 0; i < bb.capacity(); i++)
        {
            val = bb.get(i);
            nbb.put((byte) Integer.toString((val >> 4) & 0x0f, 16).charAt(0))
                    .put((byte) Integer.toString((val & 0x0f), 16).charAt(0));
        }
        nbb.put((byte) EOD);

        return nbb;
    }

    public static ByteBuffer encode(String s)
    {
        if (s == null || s.equals(""))
        {
            return null;
        }
        ByteBuffer bb = ByteBuffer.allocate(2 * s.length());

        for (int i = 0; i < s.length(); i++)
        {
            bb.putChar(s.charAt(i));
        }

        return encode(bb);
    }

    public static ByteBuffer decode(ByteBuffer bb) throws PdfBadFileException
    {
        if (bb == null)
        {
            return null;
        }
        CharBuffer cb = CharBuffer.allocate(bb.capacity());

        try {
        for (int i = 0; i < bb.capacity(); i++)
        {
            cb.put((char) bb.get(i));
        }
        }
        catch (Exception e)
        {
            throw new PdfBadFileException("Invalid character"
                    + " in stream buffer. Cannot use ASCII Hex filter.");
        }

        cb.position(0);
        if (cb.charAt(cb.capacity() - 1) != EOD)
        {
            throw new PdfBadFileException("Invalid End of Data marker"
                    + " in stream buffer."
                    + " Must be '>' for ASCII Hex filter.");
        }

        boolean done = false;
        char char1 = 0, char2 = 0;
        ByteBuffer nbb = ByteBuffer.allocate(cb.capacity() / 2);

        do
        {
            do
            {
                char1 = cb.get();
            } while (char1 == Usable.PDF_NEWLINE || char1 == Usable.PDF_CARRIAGE
                    || char1 == Usable.PDF_TAB || char1 == Usable.PDF_SP
                    || char1 == Usable.PDF_FORMFEED);
            switch (char1)
            {
                case EOD:
                    done = true;
                    break;
                case '0': /* fall through */
                case '1': /* fall through */
                case '2': /* fall through */
                case '3': /* fall through */
                case '4': /* fall through */
                case '5': /* fall through */
                case '6': /* fall through */
                case '7': /* fall through */
                case '8': /* fall through */
                case '9': /* fall through */
                case 'a': /* fall through */
                case 'b': /* fall through */
                case 'c': /* fall through */
                case 'd': /* fall through */
                case 'e': /* fall through */
                case 'f': /* fall through */
                case 'A': /* fall through */
                case 'B': /* fall through */
                case 'C': /* fall through */
                case 'D': /* fall through */
                case 'E': /* fall through */
                case 'F':
                    do
                    {
                        char2 = cb.get();
                    } while (char2 == Usable.PDF_NEWLINE || char2 == Usable.PDF_CARRIAGE
                            || char2 == Usable.PDF_TAB || char2 == Usable.PDF_SP
                            || char2 == Usable.PDF_FORMFEED);
                    switch (char2)
                    {
                        case EOD:
                            done = true;
                            break;
                        case '0': /* fall through */
                        case '1': /* fall through */
                        case '2': /* fall through */
                        case '3': /* fall through */
                        case '4': /* fall through */
                        case '5': /* fall through */
                        case '6': /* fall through */
                        case '7': /* fall through */
                        case '8': /* fall through */
                        case '9': /* fall through */
                        case 'a': /* fall through */
                        case 'b': /* fall through */
                        case 'c': /* fall through */
                        case 'd': /* fall through */
                        case 'e': /* fall through */
                        case 'f': /* fall through */
                        case 'A': /* fall through */
                        case 'B': /* fall through */
                        case 'C': /* fall through */
                        case 'D': /* fall through */
                        case 'E': /* fall through */
                        case 'F':
                            byte msb = (byte) (Character.digit(char1, 16) << 4);
                            byte lsb = (byte) Character.digit(char2, 16);
                            nbb.put((byte) (msb | lsb));
                            break;
                        default:
                            throw new PdfBadFileException("Invalid character"
                                + " in stream buffer. Cannot use ASCII Hex filter.");
                    }
                    break;
                default:
                    throw new PdfBadFileException("Invalid character"
                        + " in stream buffer. Cannot use ASCII Hex filter.");
            }
        } while ( !done);

        if (char2 == EOD) /* odd number of bytes, assume last byte to be 0 */
        {
            char2 = '0';
            byte msb = (byte) (Character.digit(char1, 16) << 4);
            byte lsb = (byte) Character.digit(char2, 16);
            nbb.put((byte) (msb | lsb));
        }
        return nbb;
    }
    
    synchronized static PdfAsciiHexFilter getInstance()
    {
        if (instance == null)
        {
            instance = new PdfAsciiHexFilter();
        }
        
        return instance;
    }
}