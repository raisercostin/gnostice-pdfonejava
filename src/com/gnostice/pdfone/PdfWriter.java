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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Hashtable;

/**
 * This class is used in conjunction with the
 * <code>PdfDocument</code> class to create new PDF documents.
 * <code>PdfWriter</code> does not have a default constructor and
 * instead has methods to create objects with:
 * <ul>
 * <li>a pathname of a file,</li>
 * <li>a {@link java.io.File} object,</li>
 * <li>a {@link java.io.ByteArrayOutputStream} object,</li>
 * <li>a {@link  java.io.FileOutputStream} object, or</li>
 * <li>an {@link java.io.OutputStream} object</li>
 * </ul>
 * for its output stream.
 * 
 * @see PdfDocument
 * @since 1.0
 * @version 1.0
 */
public final class PdfWriter implements Usable
{
    protected static final DecimalFormatSymbols DFS = 
        new DecimalFormatSymbols();
    
    protected static final DecimalFormat DF = new DecimalFormat(
    	"#.#####");

    boolean decompressStreams;
    
    protected DataOutputStream dataOpStream;

    protected File file;
    
    protected boolean inUse;
    
    protected PdfEncryption encryptor;
    
    protected boolean encryptDocument;
    
    protected int currentObjNumber;
    
    protected int currentGenNumber;
    
    protected Hashtable writtenObjs;
    
    static String formatFloat(double n)
    {
        synchronized(PdfWriter.class)
        {
            DFS.setDecimalSeparator('.');
            DF.setDecimalFormatSymbols(DFS);
            return DF.format(n);
        }
    }
    
    /**
     * Returns a new <code>PdfWriter</code> object created with
     * specified {@link java.io.ByteArrayOutputStream} object.
     * 
     * @param baos
     *            {@link java.io.ByteArrayOutputStream} object with
     *            which the new <code>PdfWriter</code> is to be
     *            created
     * @return a new <code>PdfWriter</code> object
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfWriter.ExampleSyntax.htm#memoryWriter_ByteArrayOutputStream">example</a>.
     */
    public static PdfWriter memoryWriter(ByteArrayOutputStream baos)
    {
        return new PdfWriter(baos);
    }
    
    /**
     * Returns a new <code>PdfWriter</code> object created with
     * specified {@link java.io.File} object.
     * 
     * @param pdfFile
     *            {@link java.io.File} object with which the new
     *            <code>PdfWriter</code> is to be created
     * @return a new <code>PdfWriter</code> object
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfWriter.ExampleSyntax.htm#fileWriter_File">example</a>.
     */
    public static PdfWriter fileWriter(File pdfFile)
        throws IOException
    {
        return new PdfWriter(pdfFile);
    }
    
    /**
     * Returns a new <code>PdfWriter</code> object created with
     * file specified by its pathname.
     * 
     * @param fileName
     *            pathname of the file with which the new
     *            <code>PdfWriter</code> object is to be created
     * @return a new <code>PdfWriter</code> object
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfWriter.ExampleSyntax.htm#fileWriter_String">example</a>.
     */
    public static PdfWriter fileWriter(String fileName)
        throws IOException
    {
        return new PdfWriter(new File(fileName));
    }

    /**
     * Returns a new <code>PdfWriter</code> object created with
     * specified {@link java.io.FileOutputStream} object.
     * 
     * @param fos
     *            {@link java.io.FileOutputStream} object with which
     *            the new <code>PdfWriter</code> object is to be
     *            created
     * @return a new <code>PdfWriter</code> object
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfWriter.ExampleSyntax.htm#fileStreamWriter_FileOutputStream">example</a>.
     */
    public static PdfWriter fileStreamWriter(FileOutputStream fos)
    {
        return new PdfWriter(new BufferedOutputStream(fos));
    }
    
    /**
     * Returns a new <code>PdfWriter</code> created with specified
     * {@link java.io.OutputStream} object.
     * 
     * @param os
     *            {@link java.io.OutputStream} object with which the
     *            new <code>PdfWriter</code> object is to be created
     * @return a new <code>PdfWriter</code> object
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfWriter.ExampleSyntax.htm#streamWriter_OutputStream">example</a>.
     */
    public static PdfWriter streamWriter(OutputStream os)
    {
        return new PdfWriter(os);
    }
    
    private PdfWriter(File pdfFile) throws IOException
    {
        dataOpStream = new DataOutputStream(new BufferedOutputStream(
            new FileOutputStream(pdfFile)));
        file = pdfFile;
        encryptDocument = false;
    }

    private PdfWriter(OutputStream outputStream)
    {
        dataOpStream = new DataOutputStream(outputStream);
        encryptDocument = false;
    }

    /**
     * Closes all I/O streams associated this <code>PdfWriter</code>.
     * 
     * @throws IOException
     *             if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfWriter.ExampleSyntax.htm#dispose">example</a>.
     */
    public synchronized void dispose() throws IOException
    {
        if (dataOpStream != null)
        {
            dataOpStream.flush();
            dataOpStream.close();
            dataOpStream = null;
        }
        
        inUse = false;
    }

    protected synchronized DataOutputStream getDataOutputStream()
    {
        return dataOpStream;
    }

    synchronized int writePDFHeader(String version)
        throws IOException
    {
        String pdfHeader = PDF_HEADER + version;
        pdfHeader += PDF_BINARYDATA;
        dataOpStream.writeBytes(pdfHeader);
        return pdfHeader.length();
    }

    synchronized int writePDFObject(PdfObject pdfObj)
        throws IOException
    {
        return pdfObj.write(this);
    }

    synchronized int writeIndirectObject(PdfObject pdfObj)
        throws IOException
    {
        if (pdfObj.objNumber < 0)
        {
            return 0;
        }
        if (writtenObjs == null)
        {
            writtenObjs = new Hashtable();
        }
        if (writtenObjs.get(new PdfInteger(pdfObj.objNumber)) != null)
        {
            return 0;
        }
        writtenObjs.put(new PdfInteger(pdfObj.objNumber),
            new PdfInteger(pdfObj.objNumber));
        
        String objStartLabel/*, objEndLabel*/;
        int byteCount = 0;
        
        currentObjNumber = pdfObj.getObjectNumber();
        currentGenNumber = pdfObj.getGenerationNumber();
        if (pdfObj.getObjectNumber() >= 0)
        {
            objStartLabel = Integer.toString(pdfObj
                .getObjectNumber())
                + PDF_SP
                + Integer.toString(pdfObj.getGenerationNumber())
                + PDF_SP + PDF_OBJ + PDF_LF;
            dataOpStream.writeBytes(objStartLabel);
            byteCount += objStartLabel.length();
        }
        
        byteCount += writePDFObject(pdfObj);
        
        if (pdfObj.getObjectNumber() >= 0)
        {
            dataOpStream.writeBytes(PDF_LF + PDF_ENDOBJ + PDF_LF); //"\nendobj\n");
            byteCount += 10;
        }
        return byteCount;
    }

    //This will write the Cross Reference Table and also the
    // associated trailor
    // dictionary
    synchronized int writeXrefTable(PdfCrossRefTable xRefTable,
        long startXRef, boolean flush) throws IOException
    {
        return xRefTable.write(this, startXRef, flush);
    }
    
    synchronized int writeCrossRefStream(
        PdfCrossReferenceStream xRefStream, long startXRef)
        throws IOException
    {
        int bytesWritten = writeIndirectObject(xRefStream);
        
        String startOffset = PDF_LF + PDF_STARTXREF + PDF_LF
            + Long.toString(startXRef) + PDF_LF + PDF_EOF + PDF_LF;
        dataOpStream.writeBytes(startOffset);
        dataOpStream.flush();
        
        return bytesWritten;
    }
    
    synchronized File getFile()
    {
        return file;
    }
}