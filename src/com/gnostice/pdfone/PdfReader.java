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

import java.io.*;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.gnostice.pdfone.filters.PdfFilter;

/**
 * This class is used in conjunction with the 
 * <code>PdfDocument</code> class to read from a file and possibly 
 * make changes and save it all to another file. For this purpose, 
 * a <code>PdfReader</code> object needs two components - an input 
 * stream and an output stream. For this reason, 
 * <code>PdfReader</code> does not have a default constructor. 
 * Instead, a <code>PdfReader</code> object is created by explicitly 
 * specifying its input stream and/or its output stream. The class 
 * has several methods to achieve this. Once a <code>PdfReader</code>
 * object has been created, it is used to in creating a 
 * <code>PdfDocument</code> that can be set to read from an existing 
 * file or stream.
 * 
 * @see PdfDocument
 * @since 1.0
 * @version 1.0
 */

public final class PdfReader implements Usable
{
    protected Method onPassword;
    
    protected Method onPageRead;
    
    private static final PdfName TRAILER_SIZE = new PdfName(PDF_SIZE);

    private static final PdfName TRAILER_PREV = new PdfName(PDF_PREV);

    private static final String EXPR_COMMENT = 
        "%[^\\r\\n(?:\\r\\n)]*(?:[\\r\\n(?:\\r\\n)])";
    
    private static final String EXPR_WHITESP = 
    	"(?:(?:\\s*)|(?:\\x00*)|(?:" + EXPR_COMMENT + "*))";
    
    private static final Pattern PAT_STARTXREF = Pattern.compile(
        "(?:(" + PDF_STARTXREF + ")" + EXPR_WHITESP
            + "(\\d+))|(?:(\\d+)" + EXPR_WHITESP + "("
            + PDF_STARTXREF + "))", Pattern.CASE_INSENSITIVE);

    private static final Pattern PAT_TRAILERSTART = Pattern
        .compile(PDF_TRAILER + EXPR_WHITESP + "(" + PDF_DICTSTART
            + ")" + EXPR_WHITESP);

    private static final Pattern PAT_TRAILEREND = Pattern
        .compile(EXPR_WHITESP + "(" + PDF_DICTEND + ")"
            + EXPR_WHITESP);
    
    private static final Pattern PAT_EOF = Pattern.compile("("
        + EXPR_WHITESP + PDF_EOF + EXPR_WHITESP + ")" + "|" + "("
        + EXPR_WHITESP + "%%EO" + EXPR_WHITESP + ")" + "|" + "("
        + EXPR_WHITESP + "%%E" + EXPR_WHITESP + ")",
        Pattern.CASE_INSENSITIVE);

    private static final Pattern PAT_STRINGEND = Pattern
        .compile("((\\))|(>[^>]??))" + EXPR_WHITESP);
    		
    private static final Pattern PAT_XREFENTRY = Pattern.compile(
        //Group: 1 -> 2, 3, 4
        EXPR_WHITESP + "((\\d{10}?)" + EXPR_WHITESP + "\\s(\\d{5}?)"
            + EXPR_WHITESP + "\\s([fn]{1}?)" + EXPR_WHITESP 
            + "?)" + "|" +
        //Group: 5 -> 6, 7
         EXPR_WHITESP + "((\\d+)" + EXPR_WHITESP + "+\\s(\\d+)"
            + EXPR_WHITESP + "+)", Pattern.CASE_INSENSITIVE);
    
    /*
     * pattern to parse PDF objects. Object can be recognized by
     * checking for pattern's group no. that matched
     */
    private static final Pattern PAT_PDFOBJ = Pattern.compile(
    	/* array start Group: 1 */
    	EXPR_WHITESP + "(\\"  + PDF_ARRAYSTART  + ")"
            + EXPR_WHITESP + "|" +

		/* array end Group: 2 */
		EXPR_WHITESP + "(\\" + PDF_ARRAYEND + ")"
			+ EXPR_WHITESP + "|" + 

		/* boolean Group:3 [true = Group:4 false = Group:5] */
		EXPR_WHITESP + "((" + PDF_TRUE + ")|(" + PDF_FALSE + "))"
			+ EXPR_WHITESP + "|" + 

		/* dict start Group: 6 */
		EXPR_WHITESP + "(" + PDF_DICTSTART + ")"
			+ EXPR_WHITESP + "|" + 
		
		/* string start Group: 7 [' <' = Group: 8 '(' = Group:9] */
		EXPR_WHITESP + "((<[^<]??)|(\\())" + "|" + 

		/* string end Group:10 [')' = Group: 11 '>' = Group: 12] */
		"((\\))|(>[^>]))" + EXPR_WHITESP + "|" + 

		/* Reference [objNo = Group:14 genNo = Group:15] */
		EXPR_WHITESP + "((\\d+)" + EXPR_WHITESP + "+(\\d+)"
			+ EXPR_WHITESP + "+R{1})"  + EXPR_WHITESP + "|"  +

		/* float [sign = Group: 16 value = Group: 17] */
		EXPR_WHITESP + "(\\+|\\-)?((\\d*\\.\\d+)|(\\d+\\.))"
			+ EXPR_WHITESP + "|" +   

		/* integer [sign = Group: 20 value = Group: 21] */
		/* To be modified to parse only digits without decimal point */
		/* check for integers with multiple digits */
		EXPR_WHITESP + "(\\+|\\-)?((\\d+?)+)" + EXPR_WHITESP + "|" +  

		//name Group: 23
		EXPR_WHITESP + "/"
			+ "([^\\x00\\t\\n\\f\\r \\(\\)<>\\[\\]\\{\\}/%]*)"
			+ EXPR_WHITESP + "|" + 
		
		/* stream Group: 24 */ 
		EXPR_WHITESP + "(" + PDF_DICTEND + ")" + EXPR_WHITESP + "("
            + PDF_STREAM + "((\\r\\n)|\\r|\\n)?" + ")*" + "|" + 
		
		/* null Group: 28 */
		EXPR_WHITESP + "(" + PDF_NULL + ")" + EXPR_WHITESP
		    , Pattern.CASE_INSENSITIVE);

    
	private static final Pattern PAT_OBJSTART = Pattern.compile(
        EXPR_WHITESP + "*(\\d+)" + EXPR_WHITESP + "+(\\d*)"
            + EXPR_WHITESP + "+(obj)", Pattern.CASE_INSENSITIVE);
	
	private static final Pattern PAT_STREAMEND = Pattern.compile(
        "(((\\r\\n)|\\r|\\n)?" + PDF_ENDSTREAM + EXPR_WHITESP + ")",
//      "(((\\r\\n)|\\r|\\n)*" + PDF_ENDSTREAM + EXPR_WHITESP + ")",
        Pattern.CASE_INSENSITIVE); 

    static final int READ_OUTLINES = 1;
    
    static final int READ_ANNOTS = 2;
    
    private InputStream is;
    
    private OutputStream os;

    private FileChannel fc;

    private ByteBuffer bb;

    private CharBuffer cb;

    private String outFilePath;
    
    private Vector byteOffsetArray;

    private Vector entryUsageArray;

    private Vector freeObjList;

    PdfCrossRefTable xrt;
    
	//prev and size values change conforming to the
	//current trailer dict being read
    private long prev;
	
	private long size;
	
	private int stringLen;
	
	private long greatestXRef;
	
	private long previousXRef;
	
    private Matcher mat_pdfObj;
    
    private Matcher mat_xrefentry;
    
    PdfObject encryptDict;

    PdfObject infoDict;

    PdfObject catalogDict;

    private PdfObject fileIDIndRef;
    /*if ID value in trailer is Ind Ref*/
    
    String fileID;
    
    private long xrefStmOffset;
    /*for hybrid reference files*/
    
    private Hashtable objectStmId;

    private ArrayList streamedObjsHash;
    
    int properties;
    
    PdfEncryption decryptor;
    
    private int currentObjNo;
    
    private int currentGenNo;
    
    private class PdfContainerEnd extends PdfObject
	{
    	public int code = 0; //1:']', 2:'>>', 3:'stream'

        public PdfContainerEnd(int code)
        {
            this.code = code;
        }

        protected int write(PdfWriter w)
        {
            return 0;
        }
	}
    
    /*private Cons'tor called only by static fileReader method*/
    private PdfReader(int properties)
    {
        /*raf = null;*/
        fc = null;
        bb = null;
        cb = null;
        catalogDict = null;
        encryptDict = null;
        infoDict = null;
        prev = size = -1;
        xrefStmOffset = -1;
        stringLen = 0;
        greatestXRef = 0;
        this.properties = properties;
        fileID = null;
        byteOffsetArray = null;
        freeObjList = null;
        xrt = null;
    }

    private static String validateExtension(String path)
    {
        String extension = ".pdf";
        int index = path.lastIndexOf('.');
        if ((index != -1)
            && ( !path.substring(index, path.length())
                .equalsIgnoreCase(extension))) 
        {
            path = path.substring(0, index) + extension;
        }
        else
        {
            path += extension;
        }
        
        return path;
    }

    /**
     * Closes all input/output (I/O) streams associated with this
     * <code>PdfReader</code>.
     * 
     * @throws IOException
     *            An input/output error has occurred.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfReader.ExampleSyntax.htm#dispose">example</a>.
     */
    public void dispose() throws IOException
    {
    	if (is != null)
    	{
    		is.close();
    		is = null;
    	}
        if (os != null)
        {
            os.close();
            os = null;
        } 
    }
    
    /**
     * Returns a new <code>PdfReader</code> object created with file
     * specified by pathname <code>inFilePath</code> as its input 
     * stream.
     * 
     * @param inFilePath
     *            pathname of the file that needs to be used as the 
     *            input stream
     * @return 
     *            a new <code>PdfReader</code> object
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfReader.ExampleSyntax.htm#fileReader_String">example</a>.
     */
    public static PdfReader fileReader(String inFilePath)
        throws IOException
    {
        synchronized (PdfReader.class)
        {
            if ( !inFilePath.endsWith(".pdf")
                && !inFilePath.endsWith(".PDF"))
            {
                inFilePath = validateExtension(inFilePath);
            }
            PdfReader reader = new PdfReader(READ_OUTLINES
                | READ_ANNOTS);
            reader.fc = new RandomAccessFile(inFilePath, "r")
                .getChannel();

            if (reader.fc.size() < 1024) /*buffer entire file*/
            {
                reader.bb = reader.fc.map(
                    FileChannel.MapMode.READ_ONLY, 0, reader.fc
                        .size());
            }
            else /*buffer only last 1024 bytes of file*/
            {
                reader.bb = reader.fc.map(
                    FileChannel.MapMode.READ_ONLY,
                    reader.fc.size() - 1024, 1024);
            }
            reader.cb = Charset.forName("ISO-8859-1").decode(
                reader.bb);

            return reader;
        }
    }

    /**
     * Returns a new <code>PdfReader</code> object created with
     * specified {@link java.io.File} object as its input stream.
     * 
     * @param inFile
     *            {@link java.io.File} object of the input stream
     * @return 
     *            a new <code>PdfReader</code> object
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfReader.ExampleSyntax.htm#fileReader_File">example</a>.
     */
    public static PdfReader fileReader(File inFile)
        throws IOException, PdfException
    {
        if (inFile == null || !inFile.isFile())
        {
            throw new PdfException(
                "Illegal argument to fileReader." +
                " [inFile == null || !inFile.isFile()]");
        }
        synchronized (PdfReader.class)
        {
            String inFileName = inFile.getName();
            if (!inFileName.endsWith(".pdf")
                && !inFileName.endsWith(".PDF"))
            {
                inFileName = validateExtension(inFileName);
            }
            PdfReader reader = new PdfReader(READ_OUTLINES
                | READ_ANNOTS);
            reader.fc = new RandomAccessFile(inFileName, "r")
                .getChannel();

            if (reader.fc.size() < 1024) /*buffer entire file*/
            {
                reader.bb = reader.fc.map(
                    FileChannel.MapMode.READ_ONLY, 0, reader.fc
                        .size());
            }
            else /*buffer only last 1024 bytes of file*/
            {
                reader.bb = reader.fc.map(
                    FileChannel.MapMode.READ_ONLY,
                    reader.fc.size() - 1024, 1024);
            }
            reader.cb = Charset.forName("ISO-8859-1").decode(
                reader.bb);

            return reader;
        }
    }

    /**
     * Returns a new <code>PdfReader</code> object created with the
     * specified {@link java.io.FileInputStream} object as its input
     * stream.
     * 
     * @param fis
     *            {@link java.io.FileInputStream} object of the input
     *            stream 
     * @return 
     *            a new <code>PdfReader</code> object
     * @throws IOException
     *            if an input/output exception has occurred.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfReader.ExampleSyntax.htm#fileStreamReader_FileInputStream">example</a>.
     */
    public static PdfReader fileStreamReader(FileInputStream fis)
        throws IOException, PdfException
    {
        if (fis == null)
        {
            throw new PdfException(
                "Illegal argument to fileReader." +
                " [fis == null]");
        }
        synchronized (PdfReader.class)
        {
            PdfReader reader = new PdfReader(READ_OUTLINES
                | READ_ANNOTS);
            reader.is = new BufferedInputStream(fis);
            reader.fc = fis.getChannel();

            if (reader.fc.size() < 1024) /*buffer entire file*/
            {
                reader.bb = reader.fc.map(
                    FileChannel.MapMode.READ_ONLY, 0, reader.fc
                        .size());
            }
            else /*buffer only last 1024 bytes of file*/
            {
                reader.bb = reader.fc.map(
                    FileChannel.MapMode.READ_ONLY,
                    reader.fc.size() - 1024, 1024);
            }
            reader.cb = Charset.forName("ISO-8859-1").decode(
                reader.bb);

            return reader;
        }
    }
    
    /*public static PdfReader memoryReader(byte[] ba)
        throws IOException, PdfException
    {
        if (ba == null || ba.length == 0)
        {
            throw new PdfException(
                "Illegal argument to fileReader." +
                " [bais == null || ba.length == 0]");
        }
        synchronized (PdfReader.class)
        {
            PdfReader reader = new PdfReader(READ_OUTLINES
                | READ_ANNOTS);
            reader.is = new ByteArrayInputStream(ba);
            reader.bb = ByteBuffer.wrap(ba);
            reader.bb.position(ba.length - 1024);
            reader.bb.limit((int) (reader.bb.position() + 1024));
            ByteBuffer bb = reader.bb.slice();
            reader.cb = Charset.forName("ISO-8859-1").decode(bb);

            return reader;
        }
    }*/
    
    /**
     * Returns a new <code>PdfReader</code> object created with file
     * specified by pathname <code>inFilePath</code> as its input 
     * stream and file specified by pathname <code>outFilePath</code>
     * as its output stream.
     * 
     * @param inFilePath
     *            pathname of the file that needs to be used as the 
     *            input stream
     * @param outFilePath
     *            pathname of the file that needs to be used as the 
     *            output stream
     * @return    
     *            a new <code>PdfReader</code> object
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfReader.ExampleSyntax.htm#fileReader_String_String">example</a>.
     */
    public static PdfReader fileReader(String inFilePath,
        String outFilePath) throws IOException
    {
        synchronized (PdfReader.class)
        {
            if ( !inFilePath.endsWith(".pdf")
                && !inFilePath.endsWith(".PDF"))
            {
                inFilePath = validateExtension(inFilePath);
            }
            PdfReader reader = new PdfReader(READ_OUTLINES
                | READ_ANNOTS);
            reader.fc = new RandomAccessFile(inFilePath, "r")
                .getChannel();
            reader.os = new FileOutputStream(outFilePath);
            reader.outFilePath = outFilePath;
            if ( !outFilePath.endsWith(".pdf")
                && !outFilePath.endsWith(".PDF"))
            {
                reader.outFilePath = validateExtension(outFilePath);
            }

            if (reader.fc.size() < 1024) /* buffer entire file */
            {
                reader.bb = reader.fc.map(
                    FileChannel.MapMode.READ_ONLY, 0, reader.fc
                        .size());
            }
            else
            /* buffer only last 1024 bytes of file */
            {
                reader.bb = reader.fc.map(
                    FileChannel.MapMode.READ_ONLY,
                    reader.fc.size() - 1024, 1024);
            }
            reader.cb = Charset.forName("ISO-8859-1").decode(
                reader.bb);

            return reader;
        }
    }

    /**
     * Returns a new <code>PdfReader</code> object created with
     * specified {@link java.io.File} object as its input stream and 
     * file specified by pathname <code>outFilePath</code> as its 
     * output stream.
     * 
     * @param inFile
     *            {@link java.io.File} object of the input stream
     * @param outFilePath
     *            pathname of the file that needs to be used as the 
     *            output stream
     * @return 
     *            a new <code>PdfReader</code> object
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfReader.ExampleSyntax.htm#fileReader_File_String">example</a>.
     */
    public static PdfReader fileReader(File inFile, String outFilePath)
        throws IOException, PdfException
    {
        if (inFile == null || !inFile.isFile())
        {
            throw new PdfException("Illegal argument to fileReader."
                + " [inFile == null || !inFile.isFile()]");
        }
        synchronized (PdfReader.class)
        {
            String inFileName = inFile.getName();
            if ( !inFileName.endsWith(".pdf")
                && !inFileName.endsWith(".PDF"))
            {
                inFileName = validateExtension(inFileName);
            }
            PdfReader reader = new PdfReader(READ_OUTLINES
                | READ_ANNOTS);
            reader.fc = new RandomAccessFile(inFileName, "r")
                .getChannel();
            reader.os = new FileOutputStream(outFilePath);
            reader.outFilePath = outFilePath;
            if ( !outFilePath.endsWith(".pdf")
                && !outFilePath.endsWith(".PDF"))
            {
                reader.outFilePath = validateExtension(outFilePath);
            }

            if (reader.fc.size() < 1024) /* buffer entire file */
            {
                reader.bb = reader.fc.map(
                    FileChannel.MapMode.READ_ONLY, 0, reader.fc
                        .size());
            }
            else
            /* buffer only last 1024 bytes of file */
            {
                reader.bb = reader.fc.map(
                    FileChannel.MapMode.READ_ONLY,
                    reader.fc.size() - 1024, 1024);
            }
            reader.cb = Charset.forName("ISO-8859-1").decode(
                reader.bb);

            return reader;
        }
    }

    /**
     * Returns a new <code>PdfReader</code> object created with
     * specified {@link FileInputStream} object as its input stream
     * and file specified by pathname <code>outFilePath</code> as 
     * its output stream.
     * 
     * @param fis
     *            {@link java.io.FileInputStream} object of the input 
     *            stream
     * @param outFilePath
     *            pathname of the file that needs to be used as the 
     *            output stream
     * @return 
     *            a new <code>PdfReader</code> object
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfReader.ExampleSyntax.htm#fileStreamReader_FileInputStream_String">example</a>.
     */
    public static PdfReader fileStreamReader(FileInputStream fis,
        String outFilePath) throws IOException, PdfException
    {
        if (fis == null)
        {
            throw new PdfException("Illegal argument to fileReader."
                + " [fis == null]");
        }
        synchronized (PdfReader.class)
        {
            PdfReader reader = new PdfReader(READ_OUTLINES
                | READ_ANNOTS);
            reader.is = new BufferedInputStream(fis);
            reader.fc = fis.getChannel();
            reader.os = new FileOutputStream(outFilePath);
            reader.outFilePath = outFilePath;
            if ( !outFilePath.endsWith(".pdf")
                && !outFilePath.endsWith(".PDF"))
            {
                reader.outFilePath = validateExtension(outFilePath);
            }

            if (reader.fc.size() < 1024) /* buffer entire file */
            {
                reader.bb = reader.fc.map(
                    FileChannel.MapMode.READ_ONLY, 0, reader.fc
                        .size());
            }
            else
            /* buffer only last 1024 bytes of file */
            {
                reader.bb = reader.fc.map(
                    FileChannel.MapMode.READ_ONLY,
                    reader.fc.size() - 1024, 1024);
            }
            reader.cb = Charset.forName("ISO-8859-1").decode(
                reader.bb);

            return reader;
        }
    }

    /*public static PdfReader memoryReader(byte[] ba, String outFilePath)
        throws IOException, PdfException
    {
        if (ba == null || ba.length == 0)
        {
            throw new PdfException("Illegal argument to fileReader."
                + " [bais == null || ba.length == 0]");
        }
        synchronized (PdfReader.class)
        {
            PdfReader reader = new PdfReader(READ_OUTLINES
                | READ_ANNOTS);
            reader.is = new ByteArrayInputStream(ba);
            reader.outFilePath = outFilePath;
            if ( !outFilePath.endsWith(".pdf")
                && !outFilePath.endsWith(".PDF"))
            {
                reader.outFilePath = validateExtension(outFilePath);
            }
            reader.bb = ByteBuffer.wrap(ba);
            reader.bb.position(ba.length - 1024);
            reader.bb.limit((int) (reader.bb.position() + 1024));
            ByteBuffer bb = reader.bb.slice();
            reader.cb = Charset.forName("ISO-8859-1").decode(bb);

            return reader;
        }
    }*/

    /**
     * Returns a new <code>PdfReader</code> object created with file
     * specified by pathname <code>inFilePath</code> as its input 
     * stream and specified {@link java.io.OutputStream} object as 
     * its output stream.
     * 
     * @param inFilePath
     *            pathname of the file that needs to be used as the
     *            input stream
     * @param os
     *            {@link java.io.OutputStream} object of the output
     *            stream
     * @return 
     *            a new <code>PdfReader</code> object
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfReader.ExampleSyntax.htm#fileReader_String_OutputStream">example</a>.
     */
    public static PdfReader fileReader(String inFilePath,
        OutputStream os) throws IOException
    {
        synchronized (PdfReader.class)
        {
            if ( !inFilePath.endsWith(".pdf")
                && !inFilePath.endsWith(".PDF"))
            {
                inFilePath = validateExtension(inFilePath);
            }
            PdfReader reader = new PdfReader(READ_OUTLINES
                | READ_ANNOTS);
            reader.fc = new RandomAccessFile(inFilePath, "r")
                .getChannel();
            reader.os = os;
            
            if (reader.fc.size() < 1024) /* buffer entire file */
            {
                reader.bb = reader.fc.map(
                    FileChannel.MapMode.READ_ONLY, 0, reader.fc
                        .size());
            }
            else
            /* buffer only last 1024 bytes of file */
            {
                reader.bb = reader.fc.map(
                    FileChannel.MapMode.READ_ONLY,
                    reader.fc.size() - 1024, 1024);
            }
            reader.cb = Charset.forName("ISO-8859-1").decode(
                reader.bb);

            return reader;
        }
    }

    /**
     * Returns a new <code>PdfReader</code> object created with
     * specified {@link java.io.File} object as its input stream and
     * specified {@link java.io.OutputStream} object as its output
     * stream.
     * 
     * @param inFile
     *            {@link java.io.File} object of the input stream
     * @param os
     *            {@link java.io.OutputStream} object of the output 
     *            stream
     * @return    
     *            a new <code>PdfReader</code> object
     * @throws IOException
     *            if an input/output exception had occurred.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfReader.ExampleSyntax.htm#fileReader_File_OutputStream">example</a>.
     */
    public static PdfReader fileReader(File inFile, OutputStream os)
        throws IOException, PdfException
    {
        if (inFile == null || !inFile.isFile())
        {
            throw new PdfException("Illegal argument to fileReader."
                + " [inFile == null || !inFile.isFile()]");
        }
        synchronized (PdfReader.class)
        {
            String inFileName = inFile.getName();
            if ( !inFileName.endsWith(".pdf")
                && !inFileName.endsWith(".PDF"))
            {
                inFileName = validateExtension(inFileName);
            }
            PdfReader reader = new PdfReader(READ_OUTLINES
                | READ_ANNOTS);
            reader.fc = new RandomAccessFile(inFileName, "r")
                .getChannel();
            reader.os = os;
            
            if (reader.fc.size() < 1024) /* buffer entire file */
            {
                reader.bb = reader.fc.map(
                    FileChannel.MapMode.READ_ONLY, 0, reader.fc
                        .size());
            }
            else
            /* buffer only last 1024 bytes of file */
            {
                reader.bb = reader.fc.map(
                    FileChannel.MapMode.READ_ONLY,
                    reader.fc.size() - 1024, 1024);
            }
            reader.cb = Charset.forName("ISO-8859-1").decode(
                reader.bb);

            return reader;
        }
    }

    /**
     * Returns a new <code>PdfReader</code> object created with
     * specified {@link FileInputStream} object as its input stream
     * and specified {@link java.io.OutputStream} object as its 
     * output stream.
     * 
     * @param fis
     *            {@link java.io.FileInputStream} object of the input
     *            stream
     * @param os
     *            {@link java.io.OutputStream} object of the output
     *            stream
     * @return 
     *            a new <code>PdfReader</code> object
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfReader.ExampleSyntax.htm#fileStreamReader_FileInputStream_OutputStream">example</a>.
     */
    public static PdfReader fileStreamReader(FileInputStream fis,
        OutputStream os) throws IOException, PdfException
    {
        if (fis == null)
        {
            throw new PdfException("Illegal argument to fileReader."
                + " [fis == null]");
        }
        synchronized (PdfReader.class)
        {
            PdfReader reader = new PdfReader(READ_OUTLINES
                | READ_ANNOTS);
            reader.is = new BufferedInputStream(fis);
            reader.fc = fis.getChannel();
            reader.os = os;
            
            if (reader.fc.size() < 1024) /* buffer entire file */
            {
                reader.bb = reader.fc.map(
                    FileChannel.MapMode.READ_ONLY, 0, reader.fc
                        .size());
            }
            else
            /* buffer only last 1024 bytes of file */
            {
                reader.bb = reader.fc.map(
                    FileChannel.MapMode.READ_ONLY,
                    reader.fc.size() - 1024, 1024);
            }
            reader.cb = Charset.forName("ISO-8859-1").decode(
                reader.bb);

            return reader;
        }
    }

    /*public static PdfReader memoryReader(byte[] ba, OutputStream os)
        throws IOException, PdfException
    {
        if (ba == null || ba.length == 0)
        {
            throw new PdfException("Illegal argument to fileReader."
                + " [bais == null || ba.length == 0]");
        }
        synchronized (PdfReader.class)
        {
            PdfReader reader = new PdfReader(READ_OUTLINES
                | READ_ANNOTS);
            reader.is = new ByteArrayInputStream(ba);
            reader.os = os;
            reader.bb = ByteBuffer.wrap(ba);
            reader.bb.position(ba.length - 1024);
            reader.bb.limit((int) (reader.bb.position() + 1024));
            ByteBuffer bb = reader.bb.slice();
            reader.cb = Charset.forName("ISO-8859-1").decode(bb);

            return reader;
        }
    }*/

    static PdfReader fileReader(String inFilePath, int properties)
        throws IOException
    {
        synchronized (PdfReader.class)
        {
            if ( !inFilePath.endsWith(".pdf")
                && !inFilePath.endsWith(".PDF"))
            {
                inFilePath = validateExtension(inFilePath);
            }
            PdfReader reader = new PdfReader(properties);
            reader.fc = new RandomAccessFile(inFilePath, "r")
                .getChannel();
            
            if (reader.fc.size() < 1024) /* buffer entire file */
            {
                reader.bb = reader.fc.map(
                    FileChannel.MapMode.READ_ONLY, 0, reader.fc
                        .size());
            }
            else
            /* buffer only last 1024 bytes of file */
            {
                reader.bb = reader.fc.map(
                    FileChannel.MapMode.READ_ONLY,
                    reader.fc.size() - 1024, 1024);
            }
            reader.cb = Charset.forName("ISO-8859-1").decode(
                reader.bb);

            return reader;
        }
    }

    private PdfString parseString(int start) throws IOException,
        PdfException
    {
    	char ch = this.cb.get(start);
    	CharBuffer cb = null;
    	if (ch == PDF_HEXSTRINGSTART)
    	{
    		Matcher mat_stringend = PAT_STRINGEND.matcher(this.cb);
    		if (mat_stringend.find(start)
                && mat_stringend.group(3) != null)
    		{
    			cb = CharBuffer.wrap(this.cb.subSequence(start,
//                    mat_stringend.end()));
                    mat_stringend.end(3)));
    			stringLen = cb.length() - 1;
    		}
    		else
    		{
    		    throw new PdfBadFileException(
                    "Hex string end '>' not encountered" +
                    " in method parseString().");
    		}
    	}
    	else if (ch == PDF_LITERALSTRINGSTART)
    	{
    		int done = 1;
    		int index = start + 1;
    		do
    		{
    			try {
    			ch = this.cb.get(index);
    			if (ch == '\\')
    			{
    				if (this.cb.get(index + 1) == '\\'
                            || this.cb.get(index + 1) == '('
                            || this.cb.get(index + 1) == ')')
    				{
    				  index += 1;
    				}
    			}
    			else if (ch == '(')
    			{
    			    ++done;
    			}
    			else if (ch == ')')
    			{
    			    --done;
    			}
    			
    			++index;
    			}
    			catch (IndexOutOfBoundsException iob)
    			{
    				throw new PdfBadFileException(
                        "Literal string end not encountered" +
                        " in method parseString().");
    			}
    		} while (done > 0);
    
    		cb = CharBuffer.wrap(this.cb.subSequence(start, index));
    		stringLen = cb.length() - 1;
    	}
    	if (encryptDict != null && decryptor != null)
        {
            decryptor.setHashKey(currentObjNo, currentGenNo);
            decryptor.setKey();
            return PdfString.parse(cb, decryptor);
        }
    	else
    	{    
    	    return PdfString.parse(cb);      
    	}
    }

    private PdfDict parseTrailer(long offset) throws IOException,
        PdfException
    {
       PdfDict trailer = null;
    
       mat_pdfObj = PAT_PDFOBJ.matcher(cb);
       Matcher mat_trailerstart = PAT_TRAILERSTART.matcher(cb);
       Matcher mat_eof = PAT_EOF.matcher(cb);
       Matcher mat_trailerend = PAT_TRAILEREND.matcher(cb);
    
       if (mat_trailerstart.find())
       {
       	  if (mat_trailerend.find(mat_trailerstart.start()))
          {
          	PdfObject obj = readPdfObject(mat_trailerstart.start(1));
          	if (obj instanceof PdfDict)
          	{
          	    trailer = (PdfDict)obj;
          	}
          	else
          	{          	    
          	    throw new PdfBadFileException(
                    "Invalid trailer object (must be a dictionary).");
          	}
          }
          else
          {
             if (mat_eof.find(mat_trailerstart.start()))
             {
             	throw new PdfBadFileException(
                    "Trailer End not found.");
             }
             else //pick some more chunk
             {
                 long bytesToRead = new Double(Runtime.getRuntime()
                     .freeMemory() / 1.2).longValue();
                 long chunkSize = 1024 > bytesToRead ? bytesToRead : 1024; 
                 chunkSize = Math.min(chunkSize, fc.size() - offset
                      - bb.capacity());
                 bb = fc.map(FileChannel.MapMode.READ_ONLY,
                        offset, bb.capacity() + chunkSize);
                 cb = Charset.forName("ISO-8859-1").decode(bb);

                 trailer = parseTrailer(offset);
             }
          }
       }
       else
       {
           long bytesToRead = new Double(Runtime.getRuntime()
                .freeMemory() / 1.2).longValue();
           long chunkSize = 1024 > bytesToRead ? bytesToRead : 1024; 
           chunkSize = Math.min(chunkSize, fc.size() - offset
                - bb.capacity());
           bb = fc.map(FileChannel.MapMode.READ_ONLY, offset
                + bb.capacity(), chunkSize);
           cb = Charset.forName("ISO-8859-1").decode(bb);

           trailer = parseTrailer(offset + bb.capacity());
       }
    
       return trailer;
    }

    private synchronized PdfObject readPdfObject(int startIndex)
        throws IOException, PdfException
    {
    	mat_pdfObj = PAT_PDFOBJ.matcher(cb);
    	//System.out.println(mat_pdfObj.groupCount());
    	if (mat_pdfObj.find(startIndex))
    	{
    		if (stringLen > 0)
   		    {
                stringLen = 0; //reset the offset
            }
    		/* array start Group: 1 */
    		if (mat_pdfObj.group(1) != null)
    		{
    			ArrayList arrList = new ArrayList();
    			PdfObject obj;
    			boolean done = false;
    			
    			while(!done)
    			{
    				obj = readPdfObject(mat_pdfObj.end() + stringLen);
    				if (obj == null)
    				{
    					throw new PdfException(
    						"Method readPdfObject returned null " +
							"while reading Array Object.");
    				}
    				else if (obj instanceof PdfContainerEnd)
    				{
    					if (((PdfContainerEnd)obj).code != 1)
    					{
    						throw new PdfBadFileException(
                                "Array End not encountered.");
    					}
    					else 
    					{
    						done = true;
    						return new PdfArray(arrList);
    					}
    				}
    				else arrList.add(obj);
    			}
    		}
    		
    		/* array end Group: 2 */
    		else if (mat_pdfObj.group(2) != null)
    		{
    			return (new PdfContainerEnd(1));
    		}
    		
    		/* boolean Group:3 [true = Group:4 false = Group:5] */    		
    		else if (mat_pdfObj.group(3) != null)
    		{
    			if (mat_pdfObj.group(4) != null)
    			{
    				return /*new PdfBoolean(true)*/
                        PdfBoolean.TRUE;
    			}
    			else if (mat_pdfObj.group(5) != null)
    			{
    				return /*new PdfBoolean(false)*/
                        PdfBoolean.FALSE;
    			}
    		}
    		
    		/* dict start Group: 6 */
    		else if (mat_pdfObj.group(6) != null)
    		{
    			Map m = new HashMap();
    			PdfObject key, value;
        		boolean dictDone = false;
        		boolean streamDictDone = false;
        		while(!dictDone && !streamDictDone)
        		{
        			key = readPdfObject(mat_pdfObj.end() + stringLen);
        			if (key == null)
        			{
        				throw new PdfException(
                            "Method readPdfObject returned null" +
                            " while reading key for Dictionary Object.");
        			}
        			else if (key instanceof PdfContainerEnd)
        			{
        				if (((PdfContainerEnd)key).code == 2)
        				{
        					dictDone = true;
        					return new PdfDict(m);
        				}
        				else if (((PdfContainerEnd)key).code == 3)
            			{
            				streamDictDone = true;
                            boolean streamIsProper = false;
                            int streamSize = 0;
                            int startLimit = mat_pdfObj.end(25);
                            PdfObject streamLen = (PdfObject) m
                                .get(new PdfName(PDF_LENGTH));
                            while (bb.get(startLimit) == (byte) PDF_SP
                                || bb.get(startLimit) == (byte) PDF_TAB)
                            {
                                startLimit++; 
                            }
                            if (bb.get(startLimit) == (byte) PDF_CARRIAGE)
                            {
                                startLimit++;
                            }
                            if (bb.get(startLimit) == (byte) PDF_NEWLINE)
                            {
                                startLimit++;
                            }
                            CharBuffer tempCb = this.cb;
                            ByteBuffer tempBb = this.bb;
                            streamLen = getObject(streamLen);
                            this.cb = tempCb;
                            this.bb = tempBb;
                            
                            if (streamLen != null)
                            {
                                int pos = ((PdfInteger) streamLen)
                                    .getInt();
                                int end = startLimit + pos;
                                end = end < bb.capacity() ? end : bb
                                    .capacity()
                                    - "endstream".length();
                                while (bb.get(end) == (byte) PDF_SP
                                    || bb.get(end) == (byte) PDF_TAB
                                    || bb.get(end) == (byte) PDF_NEWLINE
                                    || bb.get(end) == (byte) PDF_CARRIAGE)
                                {
                                    end++;
                                }
                                if (end < bb.capacity() - 9 && cb.subSequence(end, end + 9)
                                    .toString().equalsIgnoreCase(
                                        PDF_ENDSTREAM))
                                {
                                    streamSize = pos;
                                    streamIsProper = true;
                                }
                            }
                            
                            if (!streamIsProper)
                            {
                                Matcher m_strm = PAT_STREAMEND.matcher(cb);
                                if (!m_strm.find(startIndex))
                                {
                                    throw new PdfException(
                                        "stream end not found.");
                                }
                                int endLimit = m_strm.start(1); 
                                streamSize = endLimit - startLimit;
                            }
                            PdfByteOutputStream baos = new PdfByteOutputStream();
            				for (int i = startLimit; i < startLimit + streamSize; ++i)
            				{
            				    baos.write(bb.get(i));
            				}
            				if (encryptDict != null && !(encryptDict instanceof PdfIndirectReference))
            				{
                                decryptor.setHashKey(currentObjNo,
                                    currentGenNo);
                                decryptor.setKey();
                                PdfEncryption.decryptRC4(baos
                                    .getBuffer(), 0, baos.size(),
                                    decryptor);
            				}
            				PdfObject stmfilter = (PdfObject) m
                                .get(new PdfName(PDF_FILTER));
            				PdfObject stmdecode = (PdfObject) m
            					.get(new PdfName(PDF_DECODEPARMS));
            				if (stmdecode == null)
            				{
            				    stmdecode = (PdfObject) m
            				    	.get(new PdfName(PDF_DP));
            				}
            				
                            ByteBuffer bb = ((ByteBuffer) ByteBuffer
                                .wrap(baos.getBuffer()).limit(
                                    baos.size())).slice();
                            PdfStream stm = new PdfStream(new PdfDict(m), bb);
            				stm.filters = getObject(stmfilter);
            				stm.decodeParms = getObject(stmdecode);
            				if (stm.filters != null)
                            {
                                if (stm.filters instanceof PdfArray)
                                {
                                    int i = 0;
                                    ArrayList l = (ArrayList) ((PdfArray) stm.filters)
                                        .getList();
                                    for (Iterator iter = l.iterator(); iter
                                        .hasNext(); i++)
                                    {
                                        l.set(i, getObject((PdfObject) iter
                                              .next()));
                                    }
                                }
                                if (stm.decodeParms != null
                                    && stm.decodeParms instanceof PdfArray)
                                {
                                    int i = 0;
                                    ArrayList l = (ArrayList) ((PdfArray) stm.decodeParms)
                                        .getList();
                                    for (Iterator iter = l.iterator(); iter
                                        .hasNext(); i++)
                                    {
                                        l.set(i, getObject((PdfObject) iter
                                            .next()));
                                    }
                                }
                            }
            				
            				return stm;
            			}
        			}
        			value = readPdfObject(mat_pdfObj.end());
        			if (value == null)
        			{
        				throw new PdfException(
        					"Method readPdfObject returned null while " +
        					"reading value for Dictionary Object.");
        			}
        			else
        			{
        			    m.put(key, value);
        			}
        		}
    		}

    		/* string start Group: 7 [' <' = Group: 8 '(' = Group: 9] */
    		else if (mat_pdfObj.group(7) != null)
    		{
    		    return parseString(mat_pdfObj.end() - 1);
    		}
    		
    		/* Reference Group:13 [objNo = Group:14 genNo = Group:15] */ 
    		else if (mat_pdfObj.group(13) != null)
    		{
    			int objNo = 0 , genNo = 0;
    			try {
    			objNo = Integer.parseInt(mat_pdfObj.group(14)
                        .trim());
    			genNo = Integer.parseInt(mat_pdfObj.group(15)
                        .trim());
    			}
    			catch (NumberFormatException nfe)
				{
    				throw new PdfBadFileException(
    					"Method readPdfObject encountered improper " +  
    					"integer entry while reading Reference Object.");
				}
    			
    			return new PdfIndirectReference(objNo, genNo);
    		}
    		
    		/* float [sign = Group: 16 value = Group: 17] */
    		else if (mat_pdfObj.group(17) != null)
    		{
    			String floatVal = mat_pdfObj.group(17);
    			try {
    			if (mat_pdfObj.group(16) != null && 
    				mat_pdfObj.group(16).compareTo("-") == 0)
    			{
    			    return new PdfFloat(Float.parseFloat("-"
                            + floatVal.trim()));
    			}
    			else
    			{
    			    return new PdfFloat(Float.parseFloat(floatVal
                            .trim()));
    			}
    			}
    			catch (NumberFormatException nfe)
				{
    				throw new PdfBadFileException(
        				"Method readPdfObject encountered invalid " +  
        				"float value while reading Float Object.");
				}
    		}
    		
    		/* integer [sign = Group: 20 value = Group: 21] */
    		else if (mat_pdfObj.group(21) != null)
    		{
    			String intVal = mat_pdfObj.group(21);
    			try {
    			if (mat_pdfObj.group(20) != null && 
    				mat_pdfObj.group(20).compareTo("-") == 0)
    			{
    			    return new PdfInteger(Integer.parseInt("-"
                            + intVal.trim()));
    			}
    			else
    			{
    			    return new PdfInteger(Integer.parseInt(intVal
                            .trim()));
    			}
    			}
    			catch (NumberFormatException nfe)
				{
                    try
                    {
                        return new PdfLong(Long.parseLong(intVal
                            .trim()));
                    }
                    catch (NumberFormatException nfe1)
                    {
                        throw new PdfBadFileException(
                            "Method readPdfObject encountered invalid "
                                + "integer value while reading Integer Object.");
                    }
				}
    		}
    		
    		/* name Group: 23 */
    		else if (mat_pdfObj.group(23) != null)
    		{
    			String s = mat_pdfObj.group(23);
    			return new PdfName(PdfName.parse(s.trim()));
    		}
    		
    		/* dict end Group: 24 stream end Group: 25 */
    		else if (mat_pdfObj.group(24) != null)
    		{
    			if (mat_pdfObj.group(25) != null)
    			{
    			    /* code 3 for endstream */
    			    return (new PdfContainerEnd(3));
    			}
    			else
    			{
    			    /* code 2 for dict ">>" */
    			    return (new PdfContainerEnd(2));
    			}
    		}

    		/* null Group: 28 */
    		else if (mat_pdfObj.group(28) != null)
    		{
    			return new PdfNull();
    		}
    	}
    	else
    	{
    		throw new PdfBadFileException("Object not recognizable.");
    	}
    	
    	return null;
    }
    
	private synchronized void readTrailer(PdfDict trailer)
        throws IOException, PdfException
	{
	    Object obj = trailer.getValue(TRAILER_PREV);
	    if (obj != null)
	    { 
	        if ( !(obj instanceof PdfLong)
	            && !(obj instanceof PdfInteger))
	        {
	            throw new PdfBadFileException(
	                "Valid /Prev entry not present" +
	            " in trailer dictionary.");
	        }
	        else if (obj instanceof PdfInteger)
	        {
	            prev = ((PdfInteger)obj).getLong();
	        }
	        else
	        {
	            prev = ((PdfLong)obj).getLong();
	        }
	    }
	    
	    obj = trailer.getValue(TRAILER_SIZE);
	    if (obj == null) 
	    { 
	        size = -1;
	    }
	    else if ( !(obj instanceof PdfLong)
	        && !(obj instanceof PdfInteger))
	    {
	        throw new PdfBadFileException(
	            "Valid /Size entry not present" +
	        " in trailer dictionary.");
	    }
	    else if (obj instanceof PdfInteger)
	    {
	        size = ((PdfInteger)obj).getLong();
	    }
	    else
	    {
	        size = ((PdfLong)obj).getLong();
	    }
	    
	    if (catalogDict == null)
	    {
	        obj = trailer.getValue(new PdfName(PDF_ROOT));
	        if (obj != null)
	        {
	            if (obj instanceof PdfIndirectReference)
	            {
	                catalogDict = (PdfIndirectReference)obj;
	            }
	            else if (obj instanceof PdfDict)
	            {
	                catalogDict = new PdfCatalog((PdfDict)obj);
	            }
	            else
	            {
	                throw new PdfBadFileException(
	                    "Valid /Root entry not present" +
	                " in trailer dictionary.");
	            }
	        }
	    }
	    
	    if (encryptDict == null)
	    {
	        obj = trailer.getValue(new PdfName(PDF_ENCRYPT));
	        if (obj != null)
	        {
	            if (obj instanceof PdfDict)
	            {	
	                encryptDict = (PdfDict)obj;
	            }
	            else if (obj instanceof PdfIndirectReference)
	            {
	                encryptDict = (PdfIndirectReference)obj;
	            }
	            else
	            {
	                throw new PdfBadFileException(
	                    "Valid /Encrypt entry not present" +
	                " in trailer dictionary.");
	            }
	        }
	    }
	    
	    if (infoDict == null)
	    {
	        obj = trailer.getValue(new PdfName(PDF_INFO));
	        if (obj != null)
	        {
	            if (obj instanceof PdfDict)
	            {
	                infoDict = (PdfDict)obj;
	            }
	            else if (obj instanceof PdfIndirectReference)
	            {
	                infoDict = (PdfIndirectReference)obj;
	            }
	            else
	            {
	                throw new PdfBadFileException(
	                    "Valid /Info entry not present" +
	                " in trailer dictionary.");
	            }
	        }
	    }
	    
	    if (fileID == null)
	    {
	        obj = trailer.getValue(new PdfName(PDF_ID));
	        if (obj != null)
	        {
	            if (obj instanceof PdfArray)
	            {
	                PdfObject o = (PdfObject) ((PdfArray) obj)
                        .getList().get(0);
	                if (o != null && (o instanceof PdfString))
	                {
	                    fileID = ((PdfString)o).getString();				
	                }
	                else
	                {
	                    throw new PdfBadFileException(
	                    "File ID is not a string.");
	                }
	            }
	            else if (obj instanceof PdfIndirectReference)
	            {
	                fileIDIndRef = (PdfIndirectReference) obj;
	            }
	            else
	            {
	                throw new PdfBadFileException(
                        "Invalid ID value in trailer.");
	            }
	        }
	    }
	    
	    obj = trailer.getValue(new PdfName(PDF_XREFSTMOFFSET));
	    if (obj != null)
	    {
	        if ( !(obj instanceof PdfLong)
	            && !(obj instanceof PdfInteger))
	        {
	            throw new PdfBadFileException(
	                "Valid /XRefStm entry not present" +
	            " in trailer dictionary.");
	        }
	        else if (obj instanceof PdfInteger)
	        {
	            xrefStmOffset = ((PdfInteger)obj).getLong();
	        }
	        else
	        {
	            xrefStmOffset = ((PdfLong)obj).getLong();
	        }
	    }
	}

    private synchronized void readTrailer(long offset)
        throws IOException, PdfException
    {
    	readTrailer(parseTrailer(offset));
    }

    synchronized long readStartXref() throws IOException,
        PdfException
    {
       Matcher mat = PAT_STARTXREF.matcher(cb);
       int startIndex = 0;
       
       while (mat.find())
       {
           startIndex = mat.start();
       }
       
       if (mat.find(startIndex))
       {
          if (mat.group(2) != null)
          {
              greatestXRef = Long.parseLong(mat.group(2).trim());
              return greatestXRef;
          }
          else if (mat.group(3) != null)
          {
              greatestXRef = Long.parseLong(mat.group(3).trim()); 
              return greatestXRef;
          }
          else
          {
              throw new PdfBadFileException(
                    "Invalid 'startxref' value.");
          }
       }
       else throw new PdfBadFileException(
       		"startxref not found.");
    }
    
    private synchronized void readCrossRefTable(long offset,
        boolean parseXRefStream) throws IOException, PdfException
	{
    	readTrailer(offset);
    	
    	if (byteOffsetArray == null && size > 0)
    	{
    	    byteOffsetArray = new Vector((int) size);
    	    entryUsageArray = new Vector((int) size);
    	    freeObjList = new Vector((int) size);
            for (int i = 0; i < (int)size; i++)
            {	
            	entryUsageArray.add(new Boolean(false));
                byteOffsetArray.add(new Long(-1));
            }
    	}
       
        long bytesToRead = size * 30;
        bb = fc.map(FileChannel.MapMode.READ_ONLY, offset,
           		(fc.size() - offset) > bytesToRead ? 
           		    bytesToRead : fc.size() - offset);
        cb = Charset.forName("ISO-8859-1").decode(bb);
        mat_xrefentry = PAT_XREFENTRY.matcher(cb);

        long lastEntryRead = 0;
        while(lastEntryRead < size-1)
        {
        	if (mat_xrefentry.find() &&
        		mat_xrefentry.group(5) != null)
        	{	
       			long startObjNo = Long.parseLong(mat_xrefentry
                    .group(6));
       			long noOfEntries = Long.parseLong(mat_xrefentry
                    .group(7));
       			if (noOfEntries <= 0)
       			{
       			    break;
       			}
       			lastEntryRead =  startObjNo + noOfEntries - 1;
       			int j = (int)startObjNo;
       			for (int i = 0; i < (int)noOfEntries; i++, j++)
       			{
       				if (j < size
                            && mat_xrefentry.find())
                            if (
                            /*&& */mat_xrefentry.group(1) != null
                            && !mat_xrefentry.group(4).equals("f")
                            && ((freeObjList != null)
                            && !freeObjList.contains(new Integer(j)))
                            && ((Boolean) entryUsageArray.get(j))
                                .booleanValue() == false)
       				{
       					byteOffsetArray.set(j, new Long(Long
                                .parseLong(mat_xrefentry.group(2))));
       					entryUsageArray.set(j, new Boolean(true));
       				}
       				else if (mat_xrefentry.group(4) != null
                            && mat_xrefentry.group(4).equals("f"))
       				{		
       					if (freeObjList == null)
       					{
       						freeObjList = new Vector();
       					}
       					freeObjList.add(new Integer(j));
       				}
       			}
       	   }
       	   else break;
        }//of while
        
        if (/*parseXRefStream*/xrefStmOffset != -1)
        {
            bb = fc.map(FileChannel.MapMode.READ_ONLY,
                    xrefStmOffset,
                    (fc.size() - xrefStmOffset) >> 2 > bytesToRead
                    ? bytesToRead : (fc.size() - xrefStmOffset) >> 2);
            cb = Charset.forName("ISO-8859-1").decode(bb);
            parseCrossRefStream();
            xrefStmOffset = -1;
        }
        else if (prev > 0)
        {
            if (prev > greatestXRef)
            {
                greatestXRef = prev;
            }
            readCrossRefInfo(prev);
        }
	}
    
    private synchronized void readCrossRefStream(PdfStream stm)
        throws IOException, PdfException
    {
        stm.streamBuffer.position(0);
        int limit = -1; //for index array
        try {
            PdfObject obj_index = stm.streamDict.getValue(new PdfName(
                PDF_INDEX));
            int[] index;
            if (obj_index == null)
            {
                index = new int[] {0, (int) size};
                limit = 2;
            }
            else
            {
                Object[] objArr = ((ArrayList) (((PdfArray) obj_index))
                    .getList()).toArray();
                limit = objArr.length;
                index = new int[limit];
                for (int i = 0; i < limit; ++i)
                {
                    index[i] = ((PdfInteger) objArr[i]).getInt();
                }
            }
            ArrayList widthList = (ArrayList) ((PdfArray) stm.streamDict
                .getValue(PdfCrossReferenceStream.WIDTHS)).getList();
            int[] widths = new int[] {
                ((PdfInteger) widthList.get(0)).getInt(),
                ((PdfInteger) widthList.get(1)).getInt(),
                ((PdfInteger) widthList.get(2)).getInt() };

        	if (byteOffsetArray == null && size > 0)
        	{
        	    byteOffsetArray = new Vector((int) size/* - 1*/);
        	    entryUsageArray = new Vector((int) size/* - 1*/);
        	    freeObjList = new Vector((int) size - 1);
        	    for (int i = 0; i < (int)size; i++)
                {	
                	entryUsageArray.add(new Boolean(false));
                    byteOffsetArray.add(new Long(-1));
//                    generationArray.add(new Integer(-1));
                }
        	}

        	long lastEntryRead = 0;
        	int arrIndex = -1;
        	while(lastEntryRead < size-1 && arrIndex < limit)
            {
                int startObjNo = index[++arrIndex];
                int noOfEntries = index[++arrIndex];
                lastEntryRead =  startObjNo + noOfEntries - 1;
                int j = (int)startObjNo;
                for (int i = 0; i < (int)noOfEntries; i++, j++)
                {
                    if (j < size)
                    {
                        int type = PdfCrossReferenceStream.readData(
                            widths[0], stm.streamBuffer);
                        switch (type)
                        {
                            case 0:
                                freeObjList.add(new Integer(j));
                                PdfCrossReferenceStream.readData(
                                    widths[1], stm.streamBuffer);
                                PdfCrossReferenceStream.readData(
                                    widths[2], stm.streamBuffer);
                                break;
                            case 1:
                                long offset = PdfCrossReferenceStream
                                    .readData(widths[1],
                                        stm.streamBuffer);
                                /*int gen = */PdfCrossReferenceStream
                                    .readData(widths[2],
                                        stm.streamBuffer);
                                if (((freeObjList != null) && !freeObjList
                                    .contains(new Integer(j)))
                                    && ((Boolean) entryUsageArray
                                        .get(j)).booleanValue() == false)
                                 {
                                     byteOffsetArray.set(j, new Long(offset));
                                     entryUsageArray.set(j, new Boolean(true));
                                 }
                                 break;
                             case 2:
                                 PdfInteger objNo = new PdfInteger(
                                     PdfCrossReferenceStream.readData(
                                        widths[1], stm.streamBuffer));
                                 if (objectStmId == null)
                                 {
                                     objectStmId = new Hashtable();
                                 }
                                 if (!objectStmId.containsKey(objNo))
                                 {
                                     objectStmId.put(objNo, PdfNull.DUMMY);
                                 }
                                 PdfCrossReferenceStream.readData(
                                    widths[2], stm.streamBuffer);
                                 break;
                             default:
                                 throw new PdfBadFileException(
                                    "Improper cross reference stream entry.");
                        }
                    }
                }
            }//of while
            if (prev > 0)
            {
                if (prev > greatestXRef)
                {
                    greatestXRef = prev;
                }
                readCrossRefInfo(prev);
            }
        }
        catch (ClassCastException cce)
        {
            throw new PdfBadFileException(
                "Improper entry in cross reference stream.");
        }
        catch (IndexOutOfBoundsException iobe)
        {
            throw new PdfBadFileException(
                "Either Incomplete '/W' entry or stream data" +
                " in cross reference stream.");
        }
    }
    
    private synchronized void parseCrossRefStream()
        throws IOException, PdfException
    {
        PdfObject stm = readIndirectObject(((PdfInteger)
            readPdfObject(0)).getInt());
        if (stm instanceof PdfStream)
        {
            PdfStream stream = (PdfStream) stm;
            readTrailer(stream.streamDict);
            PdfFilter.decompress(stream);
            readCrossRefStream(stream);
        }
        else
        {
            throw new PdfBadFileException(
                "Cross reference stream is not proper.");
        }
    }
    
    synchronized void readCrossRefInfo(long offset)
        throws IOException, PdfException
    {
        if (prev != -1) prev = -1; //for recursive calls
        Runtime.getRuntime().gc();
        long bytesToRead = new Double(
             Runtime.getRuntime().freeMemory() / 1.2).longValue();
        //Utilizes nearly 83% of free Memory if required
        //(i.e. for huge files only)
        long newOffset = Math.max(0, offset - 50);
     
        if (newOffset >= fc.size())
        {
        	throw new PdfBadFileException("Trailer not present.");
        }
        
        long chunkSize = previousXRef - offset;
        previousXRef = offset;
        if (chunkSize <= 0)
        {
            chunkSize = offset - newOffset + 570 > fc.size()
                - newOffset ? fc.size() - newOffset : offset
                - newOffset + 570;
        }
        else
        {
            chunkSize += offset - newOffset;
        }
        
        bb = fc.map(FileChannel.MapMode.READ_ONLY, newOffset,
            	 chunkSize > bytesToRead ? bytesToRead
            	    : chunkSize);
        cb = Charset.forName("ISO-8859-1").decode(bb);
        //for hybrid reference files
        Pattern pat_xrefStm = Pattern.compile(PDF_NAMESTART
            + EXPR_WHITESP + PDF_XREFSTMOFFSET);
        //for files 1.4 or below
        Pattern pat_xrefTbl = Pattern.compile("[^start]" + PDF_XREF);
        //for files 1.5 or above
        Pattern pat_typeStm = Pattern.compile(PDF_NAMESTART
            + EXPR_WHITESP + PDF_TYPE + EXPR_WHITESP + PDF_NAMESTART
            + EXPR_WHITESP + PDF_XREFSTREAM);
        
        Matcher mat_xrefStm = pat_xrefStm.matcher(cb);
        Matcher mat_xrefTbl = pat_xrefTbl.matcher(cb);
        Matcher mat_typeStm = pat_typeStm.matcher(cb);
        
        if (mat_xrefStm.find())
        {
            bb = fc.map(FileChannel.MapMode.READ_ONLY, offset,
            	(fc.size() - offset) > bytesToRead ? bytesToRead
            	    : fc.size() - offset);
            cb = Charset.forName("ISO-8859-1").decode(bb);
            readCrossRefTable(offset, true);
        }
        else if (mat_xrefTbl.find())
        {
            bb = fc.map(FileChannel.MapMode.READ_ONLY, newOffset,
            	(fc.size() - newOffset) > bytesToRead ? bytesToRead
            	    : fc.size() - newOffset);
            cb = Charset.forName("ISO-8859-1").decode(bb);
            readCrossRefTable(/* offset */newOffset
                + mat_xrefTbl.start(), false);
        }
        else if (mat_typeStm.find())
        {
            bb = fc.map(FileChannel.MapMode.READ_ONLY, offset,
            	(fc.size() - offset) > bytesToRead ? bytesToRead
            	    : fc.size() - offset);
            cb = Charset.forName("ISO-8859-1").decode(bb);
            parseCrossRefStream(); 
            // no tolerance given for cross reference stream
        }
        else
        {
            throw new PdfBadFileException(
                "cross reference data could not be" +
                " located in file.");
        }
    }
    
    private ArrayList getStreamedObjects(PdfObjectStream os)
        throws IOException, PdfException
    {
        ByteBuffer bb = (ByteBuffer) os.stream.streamBuffer.position(0);
        CharBuffer cb = Charset.forName("ISO-8859-1").decode(bb);
        CharSequence cs = cb.subSequence(0, os.firstObjOffset);
        Pattern pat = Pattern.compile("\\d++");
        Matcher mat = pat.matcher(cs);
        
        ArrayList objList = new ArrayList();
        PdfObject obj;
        int objNo;
        while (mat.find())
        {
            objNo = Integer.parseInt(mat.group());
            this.cb = os.readObject(objNo);
            obj = readPdfObject(0);
            obj.objNumber = objNo;
            objList.add(obj);
            mat.find(); //this is the offset
        } 
        
        return objList;
    }
    
    private void bufferObjects() throws IOException, PdfException
    {
//        Hashtable clone = (Hashtable) objectStmId.clone();
//        objectStmId = new Hashtable();
        streamedObjsHash = new ArrayList();
        ArrayList list;
        PdfInteger objNo;
        PdfObjectStream os;
        PdfObject obj;
        Hashtable currentHash;
        for (Iterator iter = objectStmId.keySet().iterator(); iter
            .hasNext();)
        {
            currentHash = new Hashtable();
            objNo = (PdfInteger) iter.next();
            os = readObjectStream(objNo.getInt());
            list = getStreamedObjects(os);
            for (int i = 0, limit = list.size(); i < limit; ++i)
            {
                obj = (PdfObject) list.get(i);
//                System.out.println(obj.objNumber);
                currentHash.put(new PdfInteger(obj.objNumber),
                    obj);
            }
            streamedObjsHash.add(currentHash);
        }
    }
    
    synchronized void initialize() throws IOException, PdfException
    {
        xrt = new PdfCrossRefTable(byteOffsetArray);
        if (fileIDIndRef != null)
        {
            fileIDIndRef = getObject(fileIDIndRef);
            if (fileIDIndRef instanceof PdfArray)
            {
                PdfObject o = (PdfObject) ((PdfArray) fileIDIndRef)
                    .getList().get(0);
                if (o != null && (o instanceof PdfString))
                {
                    fileID = ((PdfString)o).getString();                
                }
                else
                {
                    throw new PdfBadFileException(
                        "File ID is not a string.");
                }
            }
            else
            {
                throw new PdfBadFileException(
                    "Invalid ID value in trailer.");
            }
        }
        if (objectStmId != null)
        {
            bufferObjects();            
        }
    }

    private PdfObjectStream readObjectStream(int objNo)
        throws IOException, PdfException
    {
       try {
           PdfStream stream = (PdfStream) dereferObject(objNo);
           PdfFilter.decompress(stream);
           PdfObject first = stream.streamDict.getValue(new PdfName(
                PDF_FIRST));
           first = getObject(first);
           return new PdfObjectStream(stream, ((PdfInteger) first)
                .getInt()); 
       }
       catch (ClassCastException cce)
       {
           throw new PdfException("Object stream is not a Stream.");
       }
    }
    
    PdfObject getObject(PdfObject obj) throws IOException,
        PdfException
    {
        PdfObject nObj = obj;
        if (obj instanceof PdfIndirectReference)
        {
            int objNo = obj.objNumber;
            nObj = dereferObject(objNo);
        }
        
        return nObj;
    }
    
    synchronized PdfObject dereferObject(int objNo)
        throws IOException, PdfException
    {
        PdfObject obj = null;
        PdfInteger intObj = new PdfInteger(objNo);
//        if (objectStmId != null && objectStmId.containsKey(intObj))
//        {
//            return (PdfObject) objectStmId.get(new PdfInteger(objNo));
//        }
        if (streamedObjsHash != null)
        {
            for (int i = 0, limit = streamedObjsHash.size(); i < limit; ++i)
            {
                Hashtable currentHash = (Hashtable) streamedObjsHash
                    .get(i);
                if (currentHash.containsKey(intObj))
                {
                    return (PdfObject) currentHash
                        .get(new PdfInteger(objNo));
                }
            }
        }
        
        long objSize = getObjectSize(objNo);
        if (objSize <= 0)
        {
            return new PdfNull();
        }
        long newOffset = Math.max(0,
            xrt.byteOffsetArray[objNo] - 10);
        
        /*we should calculate free memory hre
         before loading the entire object.
         What in case of a huge stream object?*/
        
        bb = fc.map(FileChannel.MapMode.READ_ONLY, newOffset,
            xrt.byteOffsetArray[objNo] - newOffset + objSize);
        cb =  Charset.forName("ISO-8859-1").decode(bb);
        obj = readIndirectObject(objNo);
        if (obj instanceof PdfIndirectReference)
        {
            obj = dereferObject(((PdfIndirectReference) obj)
                .objNumber);
        }
        
        return obj;
    }
    
    private long getObjectSize(int objNo) throws PdfException 
    {
        int index = -1;
        try
        {
            index = Arrays.binarySearch(xrt.offsetArraySort,
                xrt.byteOffsetArray[objNo]);
            return (index == xrt.offsetArraySort.length - 1) ?
                greatestXRef - xrt.offsetArraySort[index]
                : xrt.offsetArraySort[index + 1]
                    - xrt.offsetArraySort[index];   
        }
        catch (IndexOutOfBoundsException iob)
        {
            throw new PdfException("Invalid object no.");
        }
    }
    
    private PdfObject readIndirectObject(int objNo)
        throws IOException, PdfException
    {
        Matcher mat_objstart = PAT_OBJSTART.matcher(cb);
        int objLabel = -2;

        while (objLabel != objNo && mat_objstart.find())
        {
            objLabel = Integer.parseInt(mat_objstart.group(1));
        } 
        
        if (objLabel == objNo)
        {
            currentGenNo = Integer.parseInt(mat_objstart.group(2));
            currentObjNo = objNo;
            PdfObject obj = readPdfObject(mat_objstart.end(3));
            obj.genNumber = currentGenNo;
            obj.objNumber = objNo;
            return obj;
        }
        else
        {
            /* throw new PdfBadFileException("Object '" + objNo
                + "' not present in file."); */
            return new PdfNull();
        }
    }
    
    /**
     * Sets an <i>onPassword</i> event handler for this <code>PdfReader</code>.
     * The handler is a <b>static</b> method in the class where the <code>PdfReader</code> is instantiated. 
     * Its signature should always be <code>public static void &lt;event-handler-name&gt;(PdfDocument d, StringBuffer pwd, boolean[] flag)</code>.
     * This method is used to provide a password to read an encrypted <code>PdfDocument</code> object.
     * It is invoked by the <code>PdfDocument</code> with this <code>PdfReader</code>.   
     * 
<pre>
    public class EncryptedDocLoader
    {
        public static void myOnPasswordHandler(PdfDocument d,
            StringBuffer pwd, boolean[] flag)
        {
            pwd.append("password");
            flag[0] = true;
        }
        
        public static void main(String[] args) throws PdfException,
            IOException
        {
            try
            {
                PdfReader reader = PdfReader.fileReader("input.pdf",
                                                        "output.pdf");
                Method onPasswordHandler = EncryptedDocLoader.class
                    .getDeclaredMethod("myOnPasswordHandler",
                        new Class[] { PdfDocument.class,
                            StringBuffer.class, boolean[].class });
    
                reader.<span style="color: purple;">setOnPassword(onPasswordHandler)</span>;
                PdfDocument encryptedDoc = new PdfDocument(reader);
    
                encryptedDoc.writeText("I successfully opened an " +
                        "encrypted document progamatically!!");
    
                encryptedDoc.write();
                reader.dispose();
            }
            catch (SecurityException e)
            {
                e.printStackTrace();
            }
            catch (NoSuchMethodException e)
            {
                e.printStackTrace();
            }
        }
    } 
</pre>  
     *  
     * 
     * @param m handler for <i>onPassword</i> event 
     * @since 1.0
     */
    public void setOnPassword(Method m)
    {
        onPassword = m;
    }

    public void setOnPageRead(Method m)
    {
        onPageRead = m;
    }

    /**
     * Retrieves {@link java.io.OutputStream} object currently set as
     * output stream for this <code>PdfReader</code>.
     * 
     * @return {@link java.io.OutputStream} object currently set as
     *         the output stream
     * @since 1.0
     */
    public synchronized OutputStream getOutputStream()
    {
        return os;
    }
    
    /**
     * Sets specified {@link java.io.OutputStream} object as output
     * stream for this <code>PdfReader</code>.
     * 
     * @param os
     *            {@link java.io.OutputStream} object that needs to 
     *            be set as the output stream
     * @since 1.0
     */
    public synchronized void setOutputStream(OutputStream os)
    {
        outFilePath = null;
        this.os = os;
    }
    
    /**
     * Retrieves pathname of the file currently set as output stream
     * for this <code>PdfReader</code>.
     * 
     * @return pathname of the file currently set as the output
     *         stream
     * @since 1.0
     */
    public synchronized String getOutFilePath()
    {
        return outFilePath;
    }
    
    /**
     * Sets file specified by pathname <code>outFilePath</code> as 
     * output stream for this <code>PdfReader</code>.
     * 
     * @param outFilePath
     *            pathname of the file that needs to be set as the 
     *            output stream
     * @since 1.0
     */
    public synchronized void setOutFilePath(String outFilePath)
    {
        this.os = null;
        this.outFilePath = outFilePath;
    }
}