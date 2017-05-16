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

final class PdfTextString extends PdfString 
{
    private byte[] ba;
    
    PdfTextString(String s)
    {
        super(s);
        this.ba = toByteArray(s);
    }

    PdfTextString(String s, boolean isHex)
    {
        super(s, isHex);
        this.ba = toByteArray(s);
    }
    
    PdfTextString(byte[] ba)
    {
        super();
        this.ba = ba;
    }
    
    public int hashCode()
    {
        return super.getString().hashCode();
    }

    public Object clone()
    {
        PdfTextString clone = (PdfTextString) super.clone();
        if (this.ba != null)
        {
            byte[] nba = new byte[ba.length];
            for (int i = 0; i < ba.length; ++i)
            {
                nba[i] = ba[i];
            }
            clone.ba = nba;
        }
        
        return clone;
    }
    
    private static byte[] escape(byte[] ba) throws IOException
    {
        if (ba == null || ba.length == 0)
        {
            return ba;
        }
        PdfByteOutputStream baos = new PdfByteOutputStream();
        char ch;
        int i = -1;
        int len = ba.length;
        while (++i < len)
        {
            ch = (char) ba[i];
            switch (ch)
            {
                case '\\':
                    baos.write(("\\" + ch).getBytes());
                    break;
                case '(':
                    baos.write(("\\" + ch).getBytes());
                    break;
                case ')':
                    baos.write(("\\" + ch).getBytes());
                    break;
                case '\t':
                    baos.write("\\t".getBytes());
                    break;
                case '\r':
                    baos.write("\\r".getBytes());
                    break;
                case '\n':
                    baos.write("\\n".getBytes());
                    break;
                case '\b':
                    baos.write("\\b".getBytes());
                    break;
                case '\f':
                    baos.write("\\f".getBytes());
                    break;    
                default:
                    baos.write(ch);
                    break;
            }
        }
        ba = baos.toByteArray();
        
        return ba;
    }

    protected static String toHex(String s)
    {
        if (s.equals(""))
        {
            return s;
        }
        
        int len = s.length();
        ByteBuffer bb = ByteBuffer.allocate(len * 2);
        for (int i = 0; i < len; i++)
        {
            bb.putChar(s.charAt(i));
        }

        StringBuffer sb = new StringBuffer();

        len = bb.capacity();
        for (int i = 0; i < len; i++)
        {
            int val = bb.get(i);
            sb.append(Integer.toHexString((val >> 4) & 0x0f));
            sb.append(Integer.toHexString(val & 0x0f));
        }
        
        return sb.toString();
    }
    
    protected int write(PdfWriter w) throws IOException
    {
        if (super.getString() == null)
        {
            return 0;
        }
        String tempS = super.getString();
        DataOutputStream dataOpStream = w.getDataOutputStream();
        int byteCount = 2; // for ( and )
        
        if (w.encryptDocument)
        {
            ByteBuffer bb = encode(tempS, true);
            w.encryptor.setHashKey(w.currentObjNumber,
                w.currentGenNumber);
            w.encryptor.setKey();
            byte[] ba = PdfEncryption.encryptRC4(bb, w.encryptor);
            if (isHex)
            {
                tempS = fromBytes(ba);
                dataOpStream.writeByte(Usable.PDF_HEXSTRINGSTART);
                tempS = PdfTextString.toHex(tempS);
                dataOpStream.writeBytes(tempS);
                byteCount += tempS.length();
                dataOpStream.writeByte(Usable.PDF_HEXSTRINGEND);
            }
            else
            {
                dataOpStream.writeByte(Usable.PDF_LITERALSTRINGSTART);
                ba = PdfTextString.escape(ba);
                dataOpStream.write(ba);
                byteCount += ba.length;
                dataOpStream.writeByte(Usable.PDF_LITERALSTRINGEND);
            }
        }
        else
        {
            if (isHex)
            {
                dataOpStream.writeByte(Usable.PDF_HEXSTRINGSTART);
                tempS = PdfTextString.toHex(tempS);
                dataOpStream.writeBytes("feff" + tempS);
                byteCount += tempS.length() + 4; //for feff
                dataOpStream.writeByte(Usable.PDF_HEXSTRINGEND);
            }
            else
            {
                if (this.ba == null)
                {
                    ByteBuffer bb = PdfTextString
                        .encode(tempS, false);
                    if (bb == null)
                    {
                        this.ba = new byte[0];
                    }
                    else
                    {
                        this.ba = new byte[bb.capacity()];
                        bb.position(0);
                        bb.get(this.ba);
                    }
                }
                dataOpStream.writeByte(Usable.PDF_LITERALSTRINGSTART);
                dataOpStream.writeByte('\u00fe');
                dataOpStream.writeByte('\u00ff');
                this.ba = PdfTextString.escape(this.ba);
                dataOpStream.write(this.ba);
                byteCount += this.ba.length;
                byteCount += 2; //feff
                dataOpStream.writeByte(Usable.PDF_LITERALSTRINGEND);
            }
        }
        
        return byteCount;
    }
    
    public static ByteBuffer encode(String s, boolean append)
    {
        if (s == null || s.equals(""))
        {
            return ByteBuffer.allocate(0);
        }
        int len = s.length();
        int bufferSize = append ? 2 * len + 2 : 2 * len;
        ByteBuffer bb = ByteBuffer.allocate(bufferSize);
        if (append)
        {
            bb.putChar('\ufeff');
        }
        for (int i = 0; i < len; i++)
        {
            bb.putChar(s.charAt(i));
        }
       
        return bb;
    }
    
    public static final byte[] toByteArray(String text)
    {
        ByteBuffer bb = encode(text, false);
        byte ba[] = new byte[bb.capacity()];
        
        bb.position(0);
        bb.get(ba);
        
        return ba;
    }

    public static String fromBytes(byte bytes[])
    {
        char c[] = new char[bytes.length / 2];
        for (int k = 0, j = 0; k < bytes.length; k += 2, ++j)
        {
            c[j] = (char) (((bytes[k] << 8) & 0xffff) 
                | (bytes[k + 1] & 0xff));
        }
        return new String(c);
    }

    public synchronized String getString()
    {
        return fromBytes(this.ba);
    }
}
