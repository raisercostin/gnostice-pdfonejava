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

public class PdfInteger extends PdfObject implements PdfNumber
{
	static final int DUMMY_VAL = 0;
	
	public static final PdfInteger DUMMY = new PdfInteger(0);
	
    private int intVal;

	public PdfInteger(int intVal)
	{
		this.intVal = intVal;
	}

    public Object clone()
    {
        return (PdfInteger) super.clone();
    }
    
	public boolean equals(Object obj)
	{
		if ((obj == null) || (!(obj instanceof PdfInteger)))
		{
			return false;
		}
		return (intVal == ((PdfInteger) obj).intVal);
	}

    public int hashCode()
    {
        return intVal;
    }
    
	public int getInt()
	{
		return intVal;
	}

	public long getLong()
	{
		return (long) intVal;
	}

	public float getFloat()
	{
		return (float) intVal;
	}

	protected int write(PdfWriter writer) throws IOException
	{
		DataOutputStream dataOpStream = writer.getDataOutputStream();

		String s = Integer.toString(intVal);
		s = Usable.PDF_SP + s;
		dataOpStream.writeBytes(s);
		return s.length();
	}
    
    public double getVal()
    {
        return intVal;
    }
}