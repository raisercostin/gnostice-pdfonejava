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
import com.gnostice.pdfone.PdfByteOutputStream;
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
public final class PdfAscii85Filter
{
    private static PdfAscii85Filter instance;
    
    private static final PdfName FILTER = new PdfName(
        Usable.PDF_FILTER);

    private static final PdfName ASCII85 = new PdfName(
        Usable.PDF_ASCII85);
    
    private PdfAscii85Filter()
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
            d.getMap().put(FILTER, ASCII85);
        }
        else
        {
            if (filter instanceof PdfArray)
            {
                ((PdfArray) filter).getList().add(0, ASCII85);
            }
            else if (filter instanceof PdfName)
            {
                ArrayList l = new ArrayList();
                l.add(ASCII85);
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
        int capacity = bb.capacity();
        long base256 = 0;
        int bytesRead = 0;
        byte[] encChar = new byte[5];
        PdfByteOutputStream outBytes = new PdfByteOutputStream();

        for (int i = 0; i < capacity; i++)
        {
            bytesRead = 0;
            base256 = 0;

            base256 = (long) ((bb.get(i) & 0xff) * Math.pow(256, 3));
            bytesRead++;
            i++;
            if (i < capacity)
            {
                base256 += (long) ((bb.get(i) & 0xff) * 256 * 256);
                bytesRead++;
                i++;
                if (i < capacity)
                {
                    base256 += (bb.get(i) & 0xff) * 256;
                    bytesRead++;
                    i++;
                    if (i < capacity)
                    {
                        base256 += (bb.get(i) & 0xff);
                        bytesRead++;
                    }
                }
            }

            if (base256 == 0 && bytesRead == 4)
            {
                outBytes.write((int) 'z');
                continue;
            }
            for (int j = 0; j < encChar.length; j++)
            {
                encChar[j] = 33;
            }
            if (bytesRead != 4)
            {
                for (int t = 1; t <= 4 - bytesRead; t++)
                {
                    base256 /= 85;
                }
            }

            int index = bytesRead;
            while (index >= 0)
            {
                encChar[index] += (byte) (base256 % 85);
                base256 /= 85;
                index--;
            }
            for (index = 0; index <= bytesRead; index++)
            {
                outBytes.write(encChar[index]);
            }
        }

        outBytes.write((int) '~');
        outBytes.write((int) '>');
        
        return ((ByteBuffer) ByteBuffer.wrap(outBytes.getBuffer())
            .limit(outBytes.size())).slice();
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

    public static ByteBuffer decode(ByteBuffer bb)
        throws PdfBadFileException
    {
        if (bb == null)
        {
            return null;
        }
        CharBuffer cb = CharBuffer.allocate(bb.capacity());

        try
        {
            for (int i = 0; i < bb.capacity(); i++)
            {
                cb.put((char) bb.get(i));
            }
        }
        catch (Exception e)
        {
            throw new PdfBadFileException("Invalid character"
                + " in stream buffer. Cannot use ASCII-85 filter.");
        }

        cb.position(0);
        if ((cb.charAt(cb.capacity() - 2) != '~')
            && (cb.charAt(cb.capacity() - 1) != '>'))
        {
            throw new PdfBadFileException(
                "Invalid End of Data marker" + " in stream buffer."
                    + " Must be '~>' for ASCII-85 filter.");
        }

        long base85 = 0;
        int capacity = cb.capacity() - 2; /* skip '~>' */
        int count = 0;
        char char1 = 0;
        PdfByteOutputStream outBytes = new PdfByteOutputStream();
        byte[] ba = new byte[4];
        long[] pow = new long[5];

        pow[0] = (long) Math.pow(85, 4);
        pow[1] = (long) Math.pow(85, 3);
        pow[2] = (long) Math.pow(85, 2);
        pow[3] = 85;
        pow[4] = 1;

        while (cb.position() + 1 <= capacity)
        {
            do
            {
                char1 = cb.get();
            } while (char1 == Usable.PDF_NEWLINE || char1 == Usable.PDF_CARRIAGE
                || char1 == Usable.PDF_TAB || char1 == Usable.PDF_SP
                || char1 == Usable.PDF_FORMFEED);

            if (char1 == 'z')
            {
                if (count != 0)
                {
                    throw new PdfBadFileException(
                        "'z' found at inappopriate"
                            + " position. Can't use ASCII-Hex filter.");
                }
                else
                {
                    for (int i = 0; i < ba.length; i++)
                    {
                        outBytes.write(0);
                    }
                }
            }
            else if (char1 < '!' || char1 > 'u')
            {
                throw new PdfBadFileException(
                    "Invalid character found."
                        + " Can't use ASCII-Hex filter.");
            }
            else
            {
                base85 += (long) ((byte) (char1 - 33) * pow[count]);
                count++;
                if (count == 5)
                {
                    long temp = base85;
                    int t = 3;
                    
                    ba[0] = ba[1] = ba[2] = ba[3] = 0;
                    while (temp != 0)
                    {
                        ba[t] += (byte) (temp % 256);
                        temp /= 256;
                        t--;
                    }
                    for (int i = 0; i < ba.length; i++)
                    {
                        outBytes.write(ba[i]);
                    }

                    count = 0;
                    base85 = 0;
                }
            }
        }

        if (count == 1)
        {
            throw new PdfBadFileException("Single final character."
                + " Can't use ASCII-Hex filter.");
        }
        else if (count > 1)
        {
            long temp = base85;
            int t = 3;
            
            ba[0] = ba[1] = ba[2] = ba[3] = 0;
            while (temp != 0)
            {
                ba[t] += (byte) (temp % 256);
                temp /= 256;
                t--;
            }
            for (int i = count - 1; i <= 3; i++)
            {
                if (ba[i] != 0)
                {
                    ba[count - 2]++;
                    break;
                }
            }
            for (int i = 0; i < count - 1; i++)
            {
                outBytes.write(ba[i]);
            }
        }

        return ((ByteBuffer) ByteBuffer.wrap(outBytes.getBuffer())
            .limit(outBytes.size())).slice();
    }
    
    synchronized static PdfAscii85Filter getInstance()
    {
        if (instance == null)
        {
            instance = new PdfAscii85Filter();
        }
        
        return instance;
    }
}