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

public class PdfIndirectReference extends PdfObject
{
    public PdfIndirectReference(int objNumber, int genNumber)
    {
        this.objNumber = objNumber;
        this.genNumber = genNumber;
    }

    public Object clone()
    {
        return (PdfIndirectReference) super.clone();
    }


    public boolean equals(Object obj)
    {
        if ((obj == null)
            || ( !(obj instanceof PdfIndirectReference)))
        {
            return false;
        }
        PdfIndirectReference indRefObj = (PdfIndirectReference) obj;
        
        return ((this.objNumber == indRefObj.objNumber)
            && (this.genNumber == indRefObj.genNumber));
    }

    protected int write(PdfWriter writer) throws IOException
    {
        DataOutputStream dataOpStream = writer.getDataOutputStream();
        int byteCount = 0;
        if (this.objNumber <= 0)
        {
            dataOpStream.writeBytes(Usable.PDF_SP + Usable.PDF_NULL
                + Usable.PDF_SP);
            
            return byteCount + 6;
        }

        String s = Integer.toString(this.objNumber);
        s = Usable.PDF_SP + s;
        dataOpStream.writeBytes(s);
        //dataOpStream.write(this.objNumber);
        byteCount += s.length();

        dataOpStream.writeByte(Usable.PDF_SP);
        byteCount++;

        s = Integer.toString(this.genNumber);
        dataOpStream.writeBytes(s);
        byteCount += s.length();

        dataOpStream.writeBytes(Usable.PDF_SP + "R");
        return byteCount + 2;
    }
}