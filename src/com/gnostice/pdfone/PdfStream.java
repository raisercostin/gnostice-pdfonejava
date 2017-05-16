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

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import com.gnostice.pdfone.filters.PdfFilter;

public class PdfStream extends PdfObject
{
    protected PdfObject filters;
    
    protected PdfObject decodeParms;
    
    protected ByteBuffer streamBuffer;

    protected PdfDict streamDict;
    
    PdfStream()
    {
        filters = null;
        decodeParms = null;
        streamBuffer = null;
        streamDict = null;
    }
    
    public PdfStream(PdfDict d, ByteBuffer bb)
    {
        filters = null;
        decodeParms = null;
        streamDict = new PdfDict(new HashMap(d.getMap()));
        streamBuffer = ByteBuffer.allocateDirect(bb.capacity());
        streamBuffer.put(bb);
        streamBuffer.position(0);
    }

    public Object clone()
    {
        PdfStream clone = (PdfStream) super.clone();
        ByteBuffer bb = null;
        PdfDict d = null;
        
        if (streamBuffer != null)
        {
            streamBuffer.position(0);
            bb = ByteBuffer.allocate(streamBuffer.capacity());
            bb.put(streamBuffer);
            bb.position(0);
            streamBuffer.position(0);
        }
        if (streamDict != null)
        {
            d = (PdfDict) streamDict.clone();
        }

        clone.streamBuffer = bb;
        clone.streamDict = d;

        return clone;
    }

    public synchronized ByteBuffer getBuffer()
    {
        ByteBuffer bb = streamBuffer.asReadOnlyBuffer();
        bb.position(0);
        bb.limit(bb.capacity());
        return bb;
    }

    public synchronized PdfDict getDictionary()
    {
        return streamDict;
    }

    public synchronized void setBuffer(ByteBuffer bb)
    {
        bb.position(0);
        bb.limit(bb.capacity());
        streamBuffer = bb.asReadOnlyBuffer();
    }
    
    protected int write(PdfWriter w) throws IOException
    {
        if (w.decompressStreams && PdfFilter.allFiltersSupported(this))
        {
            try {
            PdfFilter.decompress(this);
            getDictionary().getMap().remove(
                    new PdfName(Usable.PDF_FILTER));
            }
            catch (PdfException pe)
            {
                /*will never be thrown since we
                are checking for supported filters
                in the beginning*/
            }
        }
        
        DataOutputStream dataOpStream = w.getDataOutputStream();
        ByteBuffer bb = streamBuffer;
        int bbcap = bb.capacity();
        streamDict.getMap().put(new PdfName(Usable.PDF_LENGTH),
            new PdfInteger(bbcap));
        int count = streamDict.write(w);
        dataOpStream.writeBytes(Usable.PDF_LF + Usable.PDF_STREAM
            + Usable.PDF_LF);
        count += 10;

        if (w.encryptDocument)
        {
            byte[] ba = new byte[bbcap];
            bb.get(ba);
            PdfEncryption encrypto = w.encryptor;
            encrypto.setHashKey(w.currentObjNumber,
                w.currentGenNumber);
            encrypto.setKey();
            encrypto.encryptRC4(ba);
            dataOpStream.write(ba);
        }
        else
        {
            for (int i = 0; i < bbcap; ++i)
                dataOpStream.write(bb.get());
        }
        count += bbcap;
        bb.position(0);
        dataOpStream.writeBytes(Usable.PDF_LF + Usable.PDF_ENDSTREAM);
        return count + 11;
    }

    public boolean equals(Object obj)
    {
        synchronized (this)
        {
            if ((obj == null) || ( !(obj instanceof PdfStream)))
            {
                return false;
            }
            PdfStream s = (PdfStream) obj;
            return ((streamDict.equals(s.streamDict)) && (streamBuffer
                .equals(s.streamBuffer)));
        }
    }
    
    public synchronized PdfObject getFilters()
    {
        return filters;
    }

    public synchronized PdfObject getDecodeParms()
    {
        return decodeParms;
    }
}