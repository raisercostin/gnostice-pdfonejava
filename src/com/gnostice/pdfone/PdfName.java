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

public class PdfName extends PdfObject 
{
	private String name;

    public Object clone()
    {
        PdfName clone = (PdfName) super.clone();
        clone.name = new String(name);
        
        return clone;
    }

    public PdfName(String name)
	{
		this.name = name;
	}

	protected static String parse(String name) throws PdfException
	{
//		if (name.length() < 1)
//			throw new PdfBadFileException(
//				"Name length is less than one charater.");
	    if (name.length() < 1)
	    {
	        return "";
	    }

		int length = name.length(), index = 0;
		String s = new String();

		do 
		{
			char ch = name.charAt(index);
			switch(ch)
			{
				case '#' :
					if (( index + 2 ) >= length)
						throw new PdfBadFileException(
							"'#' not followed by two hex digits" +
							" in Name object.");
					else 
					try {
					s += (char)Integer.parseInt(
						name.substring(index + 1, index + 3), 16);	
					}
					catch (NumberFormatException nfe)
					{
						throw new PdfBadFileException(
							"Improper hex char code in Name object.");
					}
					index += 3;
					break;
				default :
					s += ch;
					++index;
					break;
			}
		} while (index < length);
		return s;
	}

	//Since PdfName object are used as "key" in Hashmap (PdfDict)
	//method hashCode() is required
	public int hashCode()
	{
		return name.hashCode();
	}
	
	public boolean equals(Object obj)
	{
		if ((obj == null) || (!(obj instanceof PdfName)))
		{
			return false;
		}
		return name.equals(((PdfName)obj).name);
	}

	public String getString()
	{
		return this.name;
	}

	public String getName()
	{
		return "/" + this.name;
	}
	
	protected int write(PdfWriter writer) throws IOException
	{
		DataOutputStream dataOpStream = writer.getDataOutputStream();
		int byteCount = 0; //for '/'

		String s = new String("/");
		String hexString;
		boolean escape = false;
		
		for (int i = 0; i < this.name.length(); i++)
		{
			char ch = this.name.charAt(i);
			
			switch (ch)
			{
				case Usable.PDF_TAB :
				case Usable.PDF_NEWLINE :
				case Usable.PDF_FORMFEED :
				case Usable.PDF_CARRIAGE :
				case Usable.PDF_SP :
				case Usable.PDF_NAMESTART :
				case Usable.PDF_HEXSTRINGSTART :
				case Usable.PDF_HEXSTRINGEND :
				case Usable.PDF_ARRAYSTART :
				case Usable.PDF_ARRAYEND :
				case Usable.PDF_LITERALSTRINGSTART :
				case Usable.PDF_LITERALSTRINGEND :
				case '#' :
				case '%' :
				case '\b' :
				case '{' :
				case '}' :
					escape = true;
					break;
				default :
					escape = ((ch < 33) || (ch > 126)); //as per PDF spec.
					break;
			}
			
			if (escape)
			{
				hexString = Integer.toHexString(ch);
				if (hexString.length() < 2)
					hexString = '0' + hexString;
				s += '#';
				s += hexString;
			}
			else s += ch;
		}
		
		dataOpStream.writeBytes(s);
		byteCount += s.length();
		return byteCount;
	}
}