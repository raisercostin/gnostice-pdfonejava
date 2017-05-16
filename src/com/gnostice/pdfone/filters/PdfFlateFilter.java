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

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import com.gnostice.pdfone.PdfArray;
import com.gnostice.pdfone.PdfBadFileException;
import com.gnostice.pdfone.PdfByteOutputStream;
import com.gnostice.pdfone.PdfDict;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfInteger;
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
public final class PdfFlateFilter
{
    private static PdfFlateFilter instance;
    
    private static final PdfName FILTER = new PdfName(
        Usable.PDF_FILTER);

    private static final PdfName FLATE = new PdfName(Usable.PDF_FLATE);
    
    public static final int DEFLATED = 8;

    public static final int BEST_SPEED = 1;

    public static final int BEST_COMPRESSION = 9;

    public static final int DEFAULT_COMPRESSION = -1;

    public static final int FILTERED = 1;

    public static final int HUFFMAN_ONLY = 2;

    public static final int DEFAULT_STRATEGY = 0;

    private PdfFlateFilter()
    {
        //private constructor
    }
    
    public static void encode(PdfStream s, int level) throws PdfException
    {
        s.setBuffer(encode(s.getBuffer(), level));
        PdfDict d = s.getDictionary();
        PdfObject filter = d.getValue(FILTER);
        if (filter == null)
        {
            d.getMap().put(FILTER, FLATE);
        }
        else
        {
            if (filter instanceof PdfArray)
            {
                ((PdfArray) filter).getList().add(0, FLATE);
            }
            else if (filter instanceof PdfName)
            {
                ArrayList l = new ArrayList();
                l.add(FLATE);
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
    
    public static ByteBuffer encode(ByteBuffer bb, int level)
    {
        if (bb == null)
        {
            return null;
        }
        Deflater compressor = new Deflater();
        byte[] ba = new byte[bb.capacity()];
        PdfByteOutputStream outBytes = new PdfByteOutputStream();

        bb.position(0);
        bb.get(ba);
        compressor.setLevel(level);
        compressor.setInput(ba);
        compressor.finish();

        byte[] buf = new byte[1024];
        while ( !compressor.finished())
        {
            int len = compressor.deflate(buf);
            outBytes.write(buf, 0, len);
        }

        return ((ByteBuffer) ByteBuffer.wrap(outBytes.getBuffer())
            .limit(outBytes.size())).slice();
    }

    public static ByteBuffer encode(String s, int level)
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

        return encode(bb, level);
    }

    public static ByteBuffer decode(ByteBuffer bb)
        throws PdfBadFileException
    {
        if (bb == null)
        {
            return null;
        }
        Inflater decompressor = new Inflater();
        byte[] ba = new byte[bb.capacity()];
        PdfByteOutputStream outBytes = new PdfByteOutputStream();

        bb.position(0);
        bb.get(ba);
        decompressor.setInput(ba);

        byte[] buf = new byte[1024];
        while ( !decompressor.finished())
        {
            try {
            int len = decompressor.inflate(buf);
            outBytes.write(buf, 0, len);
            }
            catch (DataFormatException dfe)
            {
                throw new PdfBadFileException(
                    "Compressed data format is invalid."
                        + " Can't use Flate filter.");
            }
        }

        return ((ByteBuffer) ByteBuffer.wrap(outBytes.getBuffer())
            .limit(outBytes.size())).slice();
    }
    
    public static ByteBuffer dePredict(ByteBuffer bb, PdfDict dp)
        throws PdfException
    {
        if (dp == null)
        {
            return bb;
        }
        PdfByteOutputStream baos = new PdfByteOutputStream();
        bb.position(0);
        int predictor = 0, colomns = 1/*, bpc = 8*/;
        
        try {
            PdfObject obj = dp.getValue(new PdfName(
                Usable.PDF_PREDICTOR));
            if (obj != null)
            {
                predictor = ((PdfInteger) obj).getInt();
            }
            if (predictor == 1 || (predictor >=3 && predictor < 10))
            {
                return bb;
            }
            obj = dp.getValue(new PdfName(Usable.PDF_COLOMNS));
            if (obj != null)
            {
                colomns = ((PdfInteger) obj).getInt();                
            }
            
            if (predictor == 2) /* TIFF prediction. Consider bpc */
            {
                obj = dp.getValue(new PdfName(
                    Usable.PDF_BITS_PER_COMPONENT));
                if (obj != null)
                {
//                    bpc = ((PdfInteger) obj).getInt();
                }
            }
            else /* (predictor >= 10) => PNG prediction */
            {
                int[] temp = new int[colomns];
                int[] prior = new int[colomns];
                Arrays.fill(prior, 0);
                while (true)
                {
                    byte[] currRow = readRow(bb, colomns);
                    /* currRow.length should be colomns + 1 */
                    if (currRow == null)
                    {
                        break;
                    }
                    int algoTag = currRow[0];
                    switch (algoTag)
                    {
                        case 0:
                            baos.write(currRow, 1, colomns);
                            System.arraycopy(currRow, 1, prior, 0, colomns);
                            break;
                        case 1: /* sub */
                            for (int i = 1; i < currRow.length; ++i)
                            {
                                temp[i - 1] = (i == 1) ? currRow[i]
                                    : currRow[i] + /*currRow[i - 1]*/temp[i - 2];
                                baos.write((temp[i - 1] % 256) & 0xff);
                            }
                            prior = temp;
                            break;
                        case 2: /* up */
                            for (int i = 1; i < currRow.length; ++i)
                            {
                                temp[i - 1] = ((currRow[i] + prior[i - 1])
                                    % 256) & 0xff; 
                                baos.write(temp[i - 1]);
                            }
                            prior = temp;
                            break;
                        case 3: /* average */
                            for (int i = 1; i < currRow.length; ++i)
                            {
                                temp[i - 1] = ((currRow[i] + (int) Math
                                    .floor((prior[i - 1] + i == 1 ? 0
                                        : temp[i - 2]) / 2)) % 256) & 0xff;
                                baos.write(temp[i - 1]);
                            }
                            prior = temp;
                            break;
                        case 4: /* paeth */
                            int a, b, c;
                            for (int i = 1; i < currRow.length; ++i)
                            {
                                a = i == 1 ? 0 : temp[i - 2];
                                b = prior[i - 1];
                                c = i == 1 ? 0 : prior[i - 2];
                                temp[i - 1] = currRow[i]
                                    + paeth(a & 0xff, b & 0xff, c & 0xff);
                                baos.write(temp[i - 1]);
                            }
                            prior = temp;
                            break;
                        default:
                            throw new PdfBadFileException(
                            	"Improper PNG algo tag in stream.");
                    }
                }
            }
        }
        catch (ClassCastException cce)
        {
            throw new PdfBadFileException(
                "Invalid entry present in '/DecodeParms'");
        }
        return ((ByteBuffer) ByteBuffer.wrap(baos.getBuffer())
            .limit(baos.size())).slice();
    }
    
    static byte[] readRow(ByteBuffer bb, int colomns)
    {
        try {
            byte[] currRow = new byte[colomns + 1];
            bb.get(currRow, 0, colomns + 1);
            return currRow;
        }
        catch (BufferUnderflowException bufe)
        {
            return null;
        }
    }
    
    static int paeth(int a, int b, int c)
    {
        int p = (a + b) - c;
        int pa = Math.abs(p - a);
        int pb = Math.abs(p - b);
        int pc = Math.abs(p - c);
        if ((pa <= pb) && (pa <= pc))
        {
            return a;
        }
        else if (pb <= pc)
        {
            return b;
        }
        else
        {
            return c;
        }
    }
    
    synchronized static PdfFlateFilter getInstance()
    {
        if (instance == null)
        {
            instance = new PdfFlateFilter();
        }
        
        return instance;
    }
}