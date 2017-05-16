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

import java.io.IOException;
import java.nio.ByteBuffer;

import com.gnostice.pdfone.filters.PdfFlateFilter;

final class PdfCrossReferenceStream extends PdfStream
{
    static final PdfName TYPE = new PdfName(Usable.PDF_TYPE);
    
    static final PdfName XREF = new PdfName(Usable.PDF_XREFSTREAM);
    
    static final PdfName WIDTHS = new PdfName(Usable.PDF_W);
    
    int[] widths;
    
    PdfByteOutputStream baos;
    
    static int readData(int size, ByteBuffer bb)
    {
        /* Assumption : 0 < size <= 4 */
        if (size <= 0)
        {
            return 0;
        }
        switch (size)
        {
            case 1:
                return bb.get() & 0xff;
            case 2:
                byte msb = bb.get();
                byte lsb = bb.get();
                return ((msb << 8) & 0xffff) | (lsb & 0xff);
            case 3:
                byte b1 = bb.get();
                byte b2 = bb.get();
                byte b3 = bb.get();
                return ((b1 << 16) & 0xffffff)
                	| ((b2 << 8) & 0xffff)
                    | (b3 & 0xff);
            case 4:
                byte db1 = bb.get();
                byte db2 = bb.get();
                byte db3 = bb.get();
                byte db4 = bb.get();
                return ((db1 << 24) & 0xffffffff)
                    | ((db2 << 16) & 0xffffff)
                    | ((db3 << 8) & 0xffff) | (db4 & 0xff);
            default:
                return -1;
        }
    }

    PdfCrossReferenceStream() throws IOException 
    {
        super();
        baos = new PdfByteOutputStream();
    }

    PdfCrossReferenceStream(PdfDict d, ByteBuffer bb)
        throws IOException
    {
        super(d, bb);
        baos = new PdfByteOutputStream();
    }
    
    synchronized void initialize() throws PdfException
    {
        streamDict.getMap().put(TYPE, XREF);
        streamDict.getMap().put(WIDTHS, new PdfArray(widths));
        streamBuffer = ((ByteBuffer) ByteBuffer
            .wrap(baos.getBuffer()).limit(baos.size())).slice();
        PdfFlateFilter.encode(this, PdfFlateFilter.BEST_COMPRESSION);
    }
}
