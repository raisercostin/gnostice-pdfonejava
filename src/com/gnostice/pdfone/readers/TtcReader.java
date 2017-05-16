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

package com.gnostice.pdfone.readers;

import java.io.IOException;
import java.io.RandomAccessFile;

import com.gnostice.pdfone.PdfException;

public final class TtcReader
{
    private String fileName;
    
    private RandomAccessFile raf;
    
    private int index;
    
    private long[] offsets;
    
    private TtcReader()
    {
        //private constructor
    }
    
    private static int getSpecifiedIndex(String fileName)
    {
        int start = fileName.indexOf(".TTC");
        String indexString = fileName.substring(start + 4);
        
        return Integer.parseInt(indexString);
    }
    
    public static TtcReader fileReader(String fileName)
        throws IOException, PdfException
    {
        TtcReader t = new TtcReader();
        t.index = getSpecifiedIndex(fileName.toUpperCase());
        t.fileName = fileName.substring(0, fileName.toUpperCase()
            .indexOf(".TTC") + 4);
        t.raf = new RandomAccessFile(t.fileName, "r");
        t.readOffsets();
        return t;
    }
    
    private void readOffsets() throws IOException, PdfException
    {
        raf.skipBytes(8); /* skip tag and version */
        int noOfFonts = raf.readInt();
        offsets = new long[noOfFonts];
        if (index <= noOfFonts)
        {
            for (int i = 0; i < noOfFonts; ++i)
            {
                offsets[i] = ((long) raf.readInt()) & 0xffffffff;
            }
        }
        else
        {
            throw new PdfException("Invalid font index for TTC file.");
        }
    }
    
    public long getOffset()
    {
        return offsets[index - 1];
    }
    
    public String getFileName()
    {
        return this.fileName;
    }
    
    public void dispose() throws IOException
    {
        if (raf != null)
        {
            raf.close();
        }
    }
}
