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

import com.gnostice.pdfone.encodings.PdfEncodings;

/**
 * @author amol
 * 
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public final class PdfByteOutputStream extends ByteArrayOutputStream
{
    public PdfByteOutputStream()
    {
        super();
    }

    public PdfByteOutputStream(int size)
    {
        super(size);
    }

    public synchronized byte[] getBuffer()
    {
        return buf;
    }
    
    public void writeShort(int n) {
        write((byte)(n >> 8));
        write((byte)(n));
    }

    public void writeInt(int n) {
        write((byte)(n >> 24));
        write((byte)(n >> 16));
        write((byte)(n >> 8));
        write((byte)(n));
    }

    public void writeString(String s, int encoding)
    {
        byte[] bytes = PdfEncodings.getBytes(s, encoding);
        byte[] ba = new byte[bytes.length];
        for (int i = 0; i < bytes.length; ++i)
            ba[i] = (byte) bytes[i];
        write(ba, 0, bytes.length);
    }
}
