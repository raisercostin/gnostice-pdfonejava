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
import java.nio.*;
import java.nio.charset.Charset;

public class PdfString extends PdfObject
{
    private String s;

    protected boolean isHex;
    
    PdfString()
    {
        s = new String();
        isHex = false;
    }
    
    public PdfString(String s)
    {
        this.s = s;
        this.isHex = false;
    }

    public PdfString(String s, boolean isHex)
    {
        this.s = s;
        this.isHex = isHex;
    }
    
    public int hashCode()
    {
        return s.hashCode();
    }

    public Object clone()
    {
        PdfString clone = (PdfString) super.clone();
        clone.s = new String(this.s);
        
        return clone;
    }

    public boolean equals(Object obj)
    {
        if ((obj == null) || ( !(obj instanceof PdfString)))
        {
            return false;
        }
        if (s == null && ((PdfString) obj).s == null) 
        {
            return true;
        }
        return s.equals( ((PdfString) obj).s);
    }

    public synchronized String getString()
    {
        return s;
    }

    protected static String escape(String s)
    {
        if (s == null || s.equals(""))
        {
            return s;
        }
        StringBuffer sb = new StringBuffer();
        char ch;
        int i = -1;
        int len = s.length();
        while (++i < len)
        {
            ch = s.charAt(i);
            switch (ch)
            {
                case '\\':
                    sb.append("\\" + ch);
                    break;
                case '(':
                    sb.append("\\" + ch);
                    break;
                case ')':
                    sb.append("\\" + ch);
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;    
                default:
                    sb.append(ch);
                    break;
            }
        }
        
        return sb.toString();
    }
    
    public static PdfString parse(CharBuffer cb) throws PdfException
    {
        char ch = cb.get();

        if (ch == Usable.PDF_LITERALSTRINGSTART)
        {
            return readLiteral(cb);
        }
        else if (ch == Usable.PDF_HEXSTRINGSTART)
        {
            return readHex(cb);
        }
        else
        {
            throw new PdfBadFileException(
                "String should start with '(' or '<'.");
        }
    }

    private static CharBuffer unescape(CharBuffer cb) throws IOException
    {
        if (cb == null || cb.capacity() == 0)
        {
            return cb;
        }
        ByteBuffer bb = Charset.forName("ISO-8859-1").encode(cb);
        PdfByteOutputStream baos = new PdfByteOutputStream();
        
        boolean stringBroken = false;
        char ch;
        int bb_len = bb.capacity();
        
        for (int i = 0; i < bb_len; ++i)
        {
            ch = (char) bb.get(i);
            if (ch == '\\')
            {
                ch = (char) bb.get(i + 1);
                switch (ch)
                {
                    case 't':
                        ch = '\t';
                        break;
                    case 'r':
                        ch = '\r';
                        break;
                    case 'n':
                        ch = '\n';
                        break;
                    case 'b':
                        ch = '\b';
                        break;
                    case 'f':
                        ch = '\f';
                        break;
                    case '\r':
                        char ch1 = 0;
                        try {
                            //check if '/r' is followed by '\n'
                            ch1 = (char) bb.get(i + 2);
                            //this will throw IndexOutOfBoundsException
                            //if there is no char after '\r'
                            //hence shut this exception
                        }
                        catch (IndexOutOfBoundsException iobe) { /*shut it*/ }
                        i += ch1 == '\n' ? 1 : 0;
                        stringBroken = true;
                        break;
                    case '\n':
                        stringBroken = true;
                        break;
                    default:    
                        break;
                }
                ++i;
            }
            if (!stringBroken)
            {
                baos.write(ch);
            }
            else
            {
                stringBroken = false;
            }
        }
        
        bb = ((ByteBuffer) ByteBuffer.wrap(baos.getBuffer()).limit(
            baos.size())).slice();
        cb = Charset.forName("ISO-8859-1").decode(bb);
        
        return cb; 
    }
    
    public static PdfString parse(CharBuffer cb, PdfEncryption e)
        throws IOException, PdfException
    {
        byte[] ba = null;
        ByteBuffer bb = null;
        cb = unescape(cb);
        int limit = cb.capacity();
        char ch = cb.get();
        if (ch == Usable.PDF_LITERALSTRINGSTART)
        {
            ba = new byte[limit - 2];
            for (int i = 1; i <= limit - 2; ++i)
            {
                ba[i - 1] = (byte) cb.get(i);
            }
            e.encryptRC4(ba);
        }
        else if (ch == Usable.PDF_HEXSTRINGSTART)
        {
            String s = cb.toString().substring(0, limit - 2);
            ba = new byte[(limit - 2) / 2];
            for (int i = 0, j = 0, len = s.length(); i < len; i += 2, ++j)
            {
                ba[j] = (byte) Integer.parseInt(
                    s.substring(i, i + 2), 16);
            }
            e.encryptRC4(ba);
            //s = escape(fromBytes(ba));
            //ba = toBytes(s);
        }
        else
        {
            throw new PdfBadFileException(
                "String should start with '(' or '<'.");
        }

        bb = ByteBuffer.wrap(ba);
        cb = Charset.forName("ISO-8859-1").decode(bb);
        //cb = unescape(cb);
        if (cb.capacity() == 0)
        {
            return new PdfString("");
        }
        if (cb.get(0) == 254 && cb.get(1) == 255)
        {
            byte[] nba = new byte[ba.length - 2];
            System.arraycopy(ba, 2, nba, 0, nba.length);
            return new PdfTextString(PdfTextString.fromBytes(nba));
        }

        return new PdfString(cb.toString());
    }

    protected static PdfString readLiteral(CharBuffer cb)
        throws PdfException
    {
        char ch = 0;
        String s = new String();
        String code;
        int nested = 1;
        do
        {
            ch = cb.get();
            switch (ch)
            {
                case '\\': //escape sequence
                    ch = cb.get();
                    switch (ch)
                    {
                        case 'n':
                            s += '\n';
                            break;
                        case 'r':
                            s += '\r';
                            break;
                        case 'f':
                            s += '\f';
                            break;
                        case 't':
                            s += '\t';
                            break;
                        case 'b':
                            s += '\b';
                            break;
                        case '\\':
                            s += '\\';
                            break;
                        case '(':
                            s += '(';
                            break;
                        case ')':
                            s += ')';
                            break;
                        case '\r':
                        case '\n':
                            do
                            {
                                ch = cb.get();
                            } while (ch == '\r' || ch == '\n');
                            cb.position(cb.position() - 1);
                            break;
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                            code = new String();
                            do
                            {
                                code += ch;
                                ch = cb.get();
                            } while (code.length() < 3 && Character.isDigit(ch));
                            if (code.length() < 3)
                                code = "0" + code;
                            try
                            {
                                s += (char) Integer.parseInt(code, 8);
                            }
                            catch (NumberFormatException nfe)
                            {
                                throw new PdfException(
                                        "Method readLiteral encountered improper octal code.");
                            }
                            cb.position(cb.position() - 1);
                            break;
                        default:
                            s += ch;
                            break;
                    }
                    break;
                case '(':
                    ++nested;
                    s += '(';
                    break;
                case ')':
                    --nested;
                    if (nested != 0)
                        s += ')';
                    break;
                case '\r':
                case '\n':
                    do
                    {
                        ch = cb.get();
                        s += '\n';
                    } while (ch == '\r' || ch == '\n');
                    cb.position(cb.position() - 1);
                    break;
                default:
                    s += ch;
                    break;
            }
        } while (nested > 0);

        if (s.equals(""))
        {
            return new PdfString(s);
        }
        if (s.charAt(0) == 254 && s.charAt(1) == 255)
        {
            return new PdfTextString(toBytes(s.substring(2)));
        }

        return new PdfString(s);
    }

    private static PdfString readHex(CharBuffer cb) throws PdfException
    {
        char ch;
        String s = new String();
        StringBuffer sb = new StringBuffer();
        boolean done = false;
        boolean isTextString = false;

        do
        {
            ch = cb.get();
            switch (ch)
            {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                    s += ch;
                    break;
                case 0:
                case Usable.PDF_TAB:
                case Usable.PDF_NEWLINE:
                case Usable.PDF_FORMFEED:
                case Usable.PDF_CARRIAGE:
                case Usable.PDF_SP:
                    continue;
                case '>':
                    done = true;
                    break;
                default:
                    throw new PdfException("Invalid character in Hex string.");
            }
        } while ( !done);

        if ((s.length() % 2) != 0)
        {
            s += '0';
        }

        if (s.length() >= 4 && s.substring(0, 4).equalsIgnoreCase("feff"))
        {
            s = s.substring(4);
            isTextString = true;
            for (int i = 0, len = s.length(); i < len; i += 4)
            {
                sb.append((char) Integer.parseInt(s.substring(i, i + 4),
                    16));
            }
        }
        else
        {
            for (int i = 0, len = s.length(); i < len; i += 2)
            {
                sb.append((char) Integer.parseInt(s.substring(i, i + 2),
                    16));
            }
        }
        
        return isTextString ? new PdfTextString(sb.toString(), true)
            : new PdfString(sb.toString(), true);
    } 

    private static String toHex(String s)
    {
        if (s.equals(""))
        {
            return s;
        }
        
        int len = s.length();
        ByteBuffer bb = ByteBuffer.allocate(len);
        for (int i = 0; i < len; i++)
        {
             bb.put((byte) s.charAt(i));
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

    protected static String toLiteral(String s)
    {
        return s == null ? Usable.PDF_LITERALSTRINGSTART + "" 
                + Usable.PDF_LITERALSTRINGEND : 
            Usable.PDF_LITERALSTRINGSTART + s
                + Usable.PDF_LITERALSTRINGEND;
    }
    
    protected int write(PdfWriter w) throws IOException
    {
        if (s == null)
        {
            return 0;
        }
        
        String tempS = this.s;
        DataOutputStream dataOpStream = w.getDataOutputStream();
        int byteCount = 2; // for ( and )
        
        if (w.encryptDocument)
        {
            ByteBuffer bb = encode(tempS);
            w.encryptor.setHashKey(w.currentObjNumber,
                w.currentGenNumber);
            w.encryptor.setKey();
            byte[] ba = PdfEncryption.encryptRC4(bb, w.encryptor);
            tempS = fromBytes(ba);
        }

        if (isHex)
        {
            dataOpStream.writeByte(Usable.PDF_HEXSTRINGSTART);
            tempS = PdfString.toHex(tempS);
            dataOpStream.writeBytes(tempS);
            byteCount += tempS.length();
            dataOpStream.writeByte(Usable.PDF_HEXSTRINGEND);
        }
        else
        {
            tempS = PdfString.escape(tempS);
            dataOpStream.writeByte(Usable.PDF_LITERALSTRINGSTART);
            dataOpStream.writeBytes(tempS);
            byteCount += tempS.length();
            dataOpStream.writeByte(Usable.PDF_LITERALSTRINGEND);
        }

        return byteCount;
    }

    
    public static ByteBuffer encode(String s)
    {
        if (s == null || s.equals(""))
        {
            return null;
        }
        int len = s.length();
        int bufferSize = len;
        ByteBuffer bb = ByteBuffer.allocate(bufferSize);
        for (int i = 0; i < len; i++)
        {
            bb.put((byte) s.charAt(i));
        }
       
        return bb;
    }
    
    public static final byte[] toBytes(String text)
    {
        int len = text.length();
        byte b[] = new byte[len];
        for (int k = 0; k < len; ++k)
        {
            b[k] = (byte)text.charAt(k);
        }
        return b;
    }

    public static String fromBytes(byte bytes[])
    {
        char c[] = new char[bytes.length];
        for (int k = 0; k < bytes.length; ++k)
        {
            c[k] = (char)(bytes[k] & 0xff);
        }
        return new String(c);
    }
}