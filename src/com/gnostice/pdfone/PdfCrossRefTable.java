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
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

/**
 * @author amol
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

class PdfCrossRefTable
{
    long[] byteOffsetArray;

    PdfDict trailerDict;
    
    long[] offsetArraySort;
    
    protected synchronized int write(PdfWriter writer,
        long startXRef, boolean flush) throws IOException
    {
        DataOutputStream dataOpStream = writer.getDataOutputStream();
        dataOpStream.writeBytes(Usable.PDF_XREF + Usable.PDF_LF);
        int byteCount = 6;
        boolean isEncrypt = writer.encryptDocument;
        
        String noOfObj = Integer.toString(byteOffsetArray.length);

        //this has to be modified to handle an arbitrary start object
        // number not "0" always
        dataOpStream.writeBytes("0" + Usable.PDF_SP + noOfObj + Usable.PDF_LF);

        //try Long.toString(startXRef)
        byteCount += noOfObj.length() + 4; //"0" , space and LF;

        for (int i = 0; i < byteOffsetArray.length; i++)
        {
            if (byteOffsetArray[i] == -1)
            {
                continue;
            }
            String offset = Long.toString(byteOffsetArray[i]);
            for (int j = offset.length(); j < 10; j++)
            {
                dataOpStream.write('0');
            }
            dataOpStream.writeBytes(offset + Usable.PDF_SP);
   
            String generation = Integer.toString(i == 0 ? 65535 : 0);
            for (int j = generation.length(); j < 5; j++)
            {
                dataOpStream.write('0');
            }
            dataOpStream.writeBytes(generation + Usable.PDF_SP);
            dataOpStream.write(i == 0 ? 'f' : 'n');
            dataOpStream.writeBytes(Usable.PDF_LF);
            byteCount += 20;
        }

        dataOpStream.writeBytes(Usable.PDF_TRAILER + Usable.PDF_LF);
        byteCount += 9;
        if (isEncrypt)
        {
            writer.encryptDocument = false;
        }
        byteCount += trailerDict.write(writer);
        if (isEncrypt)
        {
            writer.encryptDocument = true;
        }
        String startOffset = Usable.PDF_LF + Usable.PDF_STARTXREF + Usable.PDF_LF
                + Long.toString(startXRef) + Usable.PDF_LF + Usable.PDF_EOF + Usable.PDF_LF;
        dataOpStream.writeBytes(startOffset);

        if (flush)
        {
           dataOpStream.flush();
        }
        
        return byteCount += startOffset.length();
    }

    PdfCrossRefTable(long[] offset)
    {
        /* This is always called in writing mode */
        byteOffsetArray = offset;
    }

    PdfCrossRefTable(ArrayList offsetList)
    {
        /* This is always called in reading mode */
        
        Object[] offsetArray = offsetList.toArray();

        int limit = offsetList.size();
        this.byteOffsetArray = new long[limit];
        this.offsetArraySort = new long[limit];

        for (int i = 0; i < limit; ++i)
        {
            byteOffsetArray[i] = ((Long) offsetArray[i]).longValue();
            offsetArraySort[i] = byteOffsetArray[i];
        }
        
        Arrays.sort(offsetArraySort);
    }
    
    PdfCrossRefTable(Vector offsetList)
    {
        /* This is always called in reading mode */
        
        offsetList.set(0, new Long(0));
        
        Object[] offsetArray = offsetList.toArray();

        int limit = offsetList.size();
        this.byteOffsetArray = new long[limit];
        this.offsetArraySort = new long[limit];

        for (int i = 0; i < limit; ++i)
        {
            byteOffsetArray[i] = ((Long) offsetArray[i]).longValue();
            offsetArraySort[i] = byteOffsetArray[i];
        }
                
        Arrays.sort(offsetArraySort);
    }

    protected void set(int objectRun, int root, int Info,
        int Encrypt, PdfArray fileID)
    {
        HashMap hm = new HashMap();

        hm.put(new PdfName(Usable.PDF_SIZE), new PdfInteger(objectRun));
        hm.put(new PdfName(Usable.PDF_INFO), new PdfIndirectReference(Info,
            0));
        if (root != -1)
        {
            hm.put(new PdfName(Usable.PDF_ROOT), new PdfIndirectReference(
                root, 0));
        }
        if (Encrypt != -1)
        {
            hm.put(new PdfName(Usable.PDF_ENCRYPT),
                new PdfIndirectReference(Encrypt, 0));
        }
        hm.put(new PdfName(Usable.PDF_ID), fileID);

        trailerDict = new PdfDict(hm);
    }

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public String toString()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            PdfWriter writer = PdfWriter.memoryWriter(baos);
            write(writer, 0, false);
            writer.dispose();
            baos.close();
        }
        catch (IOException e)
        {
            return null;
        }
        return baos.toString();
    }
}