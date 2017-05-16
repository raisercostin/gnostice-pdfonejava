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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.gnostice.pdfone.fonts.PdfFont;

public class PdfDict extends PdfObject
{
    Map dictMap;

    protected PdfFont getValueAsFont(Object key)
    {
        Object obj = dictMap.get(key);

        return (obj instanceof PdfFont) ? (PdfFont) obj : null;
    }

    protected PdfImage getValueAsImage(Object key)
    {
        Object obj = dictMap.get(key);

        return (obj instanceof PdfImage) ? (PdfImage) obj : null;
    }
    
    public PdfDict(Map m)
    {
        if (m instanceof HashMap)
        {
            dictMap = m;
        }
        else
        {
            dictMap = new HashMap(m);
        }
    }

    public Object clone()
    {
        PdfDict clone = (PdfDict) super.clone();
        HashMap hm = new HashMap();
        for (Iterator iter = dictMap.keySet().iterator(); iter
            .hasNext();)
        {
            PdfObject key = (PdfObject) iter.next();
            PdfObject val = (PdfObject) dictMap.get(key);
            hm.put(key.clone(), val.clone());
        }
        clone.dictMap = hm;
        
        return clone;
    }

    public boolean equals(Object obj)
    {
        if ((obj == null) || ( !(obj instanceof PdfDict)))
        {
            return false;
        }
        return dictMap.equals(((PdfDict) obj).dictMap);
    }

    public synchronized Map getMap()
    {
        return dictMap;
    }

    public synchronized PdfObject getValue(Object key)
    {
        return (PdfObject) dictMap.get(key);
    }

    public synchronized void setValue(PdfObject key, PdfObject value)
    {
        dictMap.put(key, value);
    }

    protected int write(PdfWriter writer) throws IOException
    {
        DataOutputStream dataOpStream = writer.getDataOutputStream();
        int byteCount = 4; // for << and >>

        dataOpStream.writeBytes(Usable.PDF_DICTSTART);
        //dataOpStream.writeBytes(Usable.PDF_LF);
        //byteCount += 2;

        for (Iterator iter = dictMap.keySet().iterator(); iter
            .hasNext();)
        {
            PdfName key = (PdfName) iter.next();
            PdfObject value = (PdfObject) dictMap.get(key);
            byteCount += key.write(writer);
            //dataOpStream.writeByte(Usable.PDF_SP);
            //byteCount++;
           	byteCount += value.write(writer);
            //dataOpStream.writeBytes(Usable.PDF_LF);
            //byteCount += 2;
        }

        dataOpStream.writeBytes(Usable.PDF_DICTEND);
        //dataOpStream.writeBytes(Usable.PDF_LF);
        //byteCount += 2;
        return byteCount;
    }
}