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
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class PdfRunLengthFilter
{
    private static PdfRunLengthFilter instance;
    
    private static final PdfName FILTER = new PdfName(
        Usable.PDF_FILTER);

    private static final PdfName RUNLENGTH = new PdfName(
        Usable.PDF_RUNLENGTH);
    
    private PdfRunLengthFilter()
    {
        //private constructor
    }
    
    private static long getRunLength(long startPos, ByteBuffer bb,
        boolean repeatByte)
    {
        long size = bb.capacity();
        if (startPos == size)
        {
            return 0;
        }
        boolean samePattern = true;
        long runLength = 0;
        bb.position((int) startPos);
        repeatByte = false;
        byte[] byte1 = new byte[1];
        byte[] byte2 = new byte[1];

        while (samePattern)
        {
            if (bb.position() == size)
            {
                break;
            }
            if (runLength == 0)
            {
                bb.get(byte1, 0, 1);
                runLength++;
            }
            else if (runLength == 1)
            {
                bb.get(byte2, 0, 1);
                runLength++;
                repeatByte = (byte2[0] == byte1[0]);
            }
            else
            {
                if (repeatByte)
                {
                    byte1[0] = byte2[0];
                    bb.get(byte2, 0, 1);
                    if (byte2[0] == byte1[0])
                    {
                        runLength++;
                    }
                    else
                    {
                        samePattern = false;
                    }
                }
                else
                {
                    byte1[0] = byte2[0];
                    bb.get(byte2, 0, 1);
                    if (byte2[0] != byte1[0])
                    {
                        runLength++;
                    }
                    else
                    {
                        runLength--;
                        samePattern = false;
                    }
                }
            }
        }
        bb.position((int) startPos);
        return runLength;
    }

    public static void encode(PdfStream s) throws PdfException
    {
        s.setBuffer(encode(s.getBuffer()));
        PdfDict d = s.getDictionary();
        PdfObject filter = d.getValue(FILTER);
        if (filter == null)
        {
            d.getMap().put(FILTER, RUNLENGTH);
        }
        else
        {
            if (filter instanceof PdfArray)
            {
                ((PdfArray) filter).getList().add(0, RUNLENGTH);
            }
            else if (filter instanceof PdfName)
            {
                ArrayList l = new ArrayList();
                l.add(RUNLENGTH);
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
        long curPos = 0, runLength, len;
        boolean isRepeat = false;
        PdfByteOutputStream outBytes = new PdfByteOutputStream();

        bb.position(0);
        while (curPos != bb.capacity())
        {
            runLength = getRunLength(curPos, bb, isRepeat);
            if ( !isRepeat)
            {
                len = runLength;
                while (len > 128)
                {
                    byte[] bytes = new byte[128];
                    bb.get(bytes, 0, 128);
                    outBytes.write(127);
                    for (int i = 0; i < 128; i++)
                    {
                        outBytes.write(bytes[i]);
                    }
                    len -= 128;
                }
                if (len != 0)
                {
                    outBytes.write((int) ((len - 1) & 0xff));
                    byte[] bytes = new byte[(int) len];
                    bb.get(bytes, 0, (int) (len & 0xff));
                    for (int i = 0; i < (int) (len & 0xff); i++)
                    {
                        outBytes.write(bytes[i]);
                    }
                }
            }
            else
            {
                len = runLength;
                byte[] oneByte = new byte[1];
                bb.get(oneByte, 0, 1);
                while (len >= 128)
                {
                    outBytes.write(129);
                    outBytes.write(oneByte[0]);
                    len -= 128;
                }
                if (len != 0)
                {
                    outBytes.write((int) ((257 - len) & 0x0ff));
                    outBytes.write(oneByte[0]);
                }
                bb.position(bb.position() + (int) runLength - 1);
            }
            curPos = bb.position();
        }

        outBytes.write(128); /* EOD marker */

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
        if (bb.get(bb.capacity() - 1) != (byte) 128)
        {
            throw new PdfBadFileException(
                "Invalid End of Data marker."
                    + " Can't use Run Length filter.");
        }
        long size = bb.capacity();
        byte[] oneByte = new byte[1];
        PdfByteOutputStream outBytes = new PdfByteOutputStream();

        bb.position(0);
        while (bb.position() != size - 1)
        {
            byte[] runLength = new byte[1];

            bb.get(runLength, 0, 1);
            if (runLength[0] > 128)
            {
                bb.get(oneByte, 0, 1);
                for (int i = 0; i < (257 - runLength[0]); i++)
                {
                    outBytes.write(oneByte[0]);
                }
            }
            else if (runLength[0] < 128)
            {
                byte[] bytes = new byte[runLength[0] + 1];
                bb.get(bytes, 0, (runLength[0] + 1));
                for (int i = 0; i < (runLength[0] + 1); i++)
                {
                    outBytes.write(bytes[i]);
                }
            }
            else
            {
                throw new PdfBadFileException(
                    "Unexpected End of Data marker. Can't use Run Length filter.");
            }
        }

        return ((ByteBuffer) ByteBuffer.wrap(outBytes.getBuffer())
            .limit(outBytes.size())).slice();
    }
    
    synchronized static PdfRunLengthFilter getInstance()
    {
        if (instance == null)
        {
            instance = new PdfRunLengthFilter();
        }
        
        return instance;
    }
}