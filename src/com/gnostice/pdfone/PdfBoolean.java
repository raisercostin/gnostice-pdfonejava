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

public class PdfBoolean extends PdfObject
{
    private boolean bool;

    public static final PdfBoolean TRUE = new PdfBoolean(true);

    public static final PdfBoolean FALSE = new PdfBoolean(false);
    
    public PdfBoolean(boolean bool)
    {
        this.bool = bool;
    }

    public Object clone()
    {
        return (PdfBoolean) super.clone();
    }

    public boolean equals(Object obj)
    {
        if ((obj == null) || ( !(obj instanceof PdfBoolean)))
        {
            return false;
        }
        return (this.bool == ((PdfBoolean) obj).bool);
    }

    public boolean getBooleanValue()
    {
        return this.bool;
    }

    protected int write(PdfWriter writer) throws IOException
    {
        DataOutputStream dataOpStream = writer.getDataOutputStream();
        int byteCount = 1;
        dataOpStream.writeByte(Usable.PDF_SP);

        if (bool)
        {
            dataOpStream.writeBytes(Usable.PDF_TRUE);
            return byteCount + 4;
        }
        else
        {
            dataOpStream.writeBytes(Usable.PDF_FALSE);
            return byteCount + 5;
        }
    }
}