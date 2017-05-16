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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author amol
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
final class PdfObjectStream
{
    static final PdfName TYPE = new PdfName(Usable.PDF_TYPE);
    
    static final PdfName OBJSTM = new PdfName(Usable.PDF_OBJSTREAM);
    
    static final PdfName OBJCOUNT = new PdfName(Usable.PDF_NO_COMP_OBJ);
    
    PdfStream stream;
    
    PdfByteOutputStream offsets; 
    
    int objectCount;
    
    int firstObjOffset;
    
    PdfObjectStream()
    {
        stream = new PdfStream();
        offsets = new PdfByteOutputStream();
    }

    PdfObjectStream(PdfDict d, ByteBuffer bb)
    {
        stream = new PdfStream(d, bb);
        offsets = new PdfByteOutputStream();
    }
    
    PdfObjectStream (PdfStream stream, int first)
    {
        this.stream = stream;
        this.firstObjOffset = first;
        offsets = null;
    }
    
    CharBuffer readObject(int objNo) throws PdfException
    {
        ByteBuffer bb = (ByteBuffer) stream.streamBuffer.position(0);
        CharBuffer cb = Charset.forName("ISO-8859-1").decode(bb);
        CharSequence cs = cb.subSequence(0, firstObjOffset);
        Pattern pat = Pattern.compile("\\d++");
        Matcher mat = pat.matcher(cs);
        int objLabel = 0x80000000;//Integer.MIN_VALUE

        while (objLabel != objNo && mat.find())
        {
            objLabel = Integer.parseInt(mat.group());
            mat.find(); //this is the offset
        } 
        
        if (objLabel == objNo)
        {
            int obj2Label = 0, obj2Offset;
            try {
                //mat.find();
                int objOffset = Integer.parseInt(mat.group());
                if (mat.find())
                {
                    obj2Label = Integer.parseInt(mat.group());
                    mat.find();
                    obj2Offset = Integer.parseInt(mat.group());
                 
                    return CharBuffer.wrap(cb
                        .subSequence(objOffset + firstObjOffset,
                            obj2Offset + firstObjOffset));
                }
                else //this is last
                {
                    return CharBuffer.wrap(cb.subSequence(objOffset
                        + firstObjOffset, cb.length()));
                }
            }
            catch (IllegalStateException ise)
            {
                throw new PdfBadFileException(
                    "Offset for object " + obj2Label
                    + " not specified in object stream.");
            }
        }
        else
        {
            throw new PdfBadFileException("Object '" + objNo
                + "' not present in specified object stream.");
        }
    }
    
    synchronized void initialize(ByteArrayOutputStream baos)
        throws IOException, PdfException
    {
        HashMap hm = new HashMap();
        hm.put(TYPE, OBJSTM);
        hm.put(PdfBookmarkTree.FIRST,
            new PdfInteger(offsets.size()));
        hm.put(OBJCOUNT, new PdfInteger(objectCount)); 
        stream.streamDict = new PdfDict(hm);
        
        baos.writeTo(offsets);
        stream.streamBuffer = ((ByteBuffer) ByteBuffer.wrap(
            offsets.getBuffer()).limit(offsets.size())).slice();
    }
}
