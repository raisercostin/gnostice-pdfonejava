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

public abstract class PdfObject implements Cloneable
{
	protected abstract int write(PdfWriter writer)
        throws IOException;

	protected int objNumber;
    
    protected int genNumber;

	public String toString()
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			PdfWriter writer = PdfWriter.memoryWriter(baos);
			write(writer);
			writer.dispose();
			baos.close();
		}
		catch (IOException e)
		{
			return null;
		}
		return baos.toString();
	}

	public Object clone()
	{
		try {
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			return null; // this should never happen
		}
	}

	public synchronized int getObjectNumber()
	{
		return objNumber;
	}

	public synchronized void setObjectNumber(int objNumber)
	{
		this.objNumber = objNumber;
	}

	public synchronized int getGenerationNumber()
	{
		return genNumber;
	}

	public synchronized void setGenerationNumber(int genNumber)
	{
		this.genNumber = genNumber;
	}
}