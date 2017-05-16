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

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.HashMap;

import javax.imageio.stream.FileImageInputStream;


/**
 * This class represents an image that can be used in a document or page.
 * <code>PdfImage</code> currently supports Joint Photographic Experts 
 * Group (JPEG), bitmap (BMP), and Portable Network Graphics (PNG) 
 * image formats.
 * <p>
 * Methods of this class can:
 * </p>
 * <ol>
 *  <li>create an instance based on an image file</li>
 *  <li>retrieve original height and width of an image</li>
 *  <li>retrieve height and width currently set for the object</li>
 *  <li>specify height and width for the object</li>
 *  <li>retrieve number of bits used to store value of each component in
 * colorspace for this image</li>
 * </ol>
 * <p>
 * After creating an object of this class, you can use methods such as
 * <code>drawImage</code> of {@link PdfPage} or {@link PdfDocument} 
 * instances to add images. <b>The object's height and width will be 
 * interpreted in terms of current measurement unit of the page or 
 * document.</b> See  
 * <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfImage.ExampleSyntax.htm#intro">example</a>.
 * </p>
 * 
 * @since 1.0
 * @version 1.0
 */
public abstract class PdfImage extends PdfStream
{
    private static final PdfName TYPE = new PdfName(Usable.PDF_TYPE);
    
    private static final PdfName XOBJ = new PdfName(Usable.PDF_XOBJECT);
    
    private static final PdfName SUBTYPE = new PdfName(Usable.PDF_SUBTYPE);
    
    private static final PdfName IMAGE = new PdfName(Usable.PDF_IMAGE);
    
    private static final int BMP_START_VALUE = 19778;
    
    private static final int JPEG_START_VALUE = 55551;
    
    private static final int PNG_START_VALUE = 20617;
    
    protected static final PdfName CS_DEVICE_GRAY = new PdfName(
        Usable.PDF_DEVICE_GRAY);

    protected static final PdfName CS_DEVICE_RGB = new PdfName(
        Usable.PDF_DEVICE_RGB);

    protected static final PdfName CS_INDEXED = new PdfName(
        Usable.PDF_INDEXED);

    protected boolean isIndexed = false;
    
    protected boolean isRotated = false;
    
    protected int type;
    
    protected int bitsPerComp; //Bits per component
    
    protected float width; //actual image width

    protected float height; //actual image height

    protected float scaledWidth; //user image width

    protected float scaledHeight; //user image height

    protected float rotation;

    protected PdfObject colorSpace;
    
    protected PdfArray matrix;
    
    protected PdfStream lookUpStream; //for indexed images
    
//    protected int compression; //compression format
    
//    public static final class CompressionFormat
//    {
//        public static final int JPEG = 1;
//        
//        public static final int FLATE = 2;
//        
//        public static final int CCITT = 4;
//    }
    private float tempScaledWidth; //user image width

    private float tempScaledHeight; //user image height

    private float tempRotation;
    
    PdfImage()
    {
        super();
        this.scaledWidth = -1;
        this.scaledHeight = -1;
        this.rotation = 0;
        HashMap hm = new HashMap();
        hm.put(TYPE, XOBJ);
        hm.put(SUBTYPE, IMAGE);
        streamDict = new PdfDict(hm);
//        compression = CompressionFormat.JPEG;
    }

    /**
	 * Creates a <code>PdfImage</code> object based on image file 
	 * specified by <code>path</code>.
	 * 
	 * @param path
	 *            relative or fully qualified path and filename of 
	 *            the image
	 * @return a new <code>PdfImage</code> object
	 * @exception IOException
	 *            if an I/O error occurs.
	 * @exception PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfImage.ExampleSyntax.htm#create">example</a>.
	 */
    public static PdfImage create(String path)
        throws IOException, PdfException
    {
        if (path == null)
        {
            throw new PdfException(
                "Illegal argument to PdfImage.create() [path == null].");
        }
        FileImageInputStream fiis = null;
        try
        {
            fiis = new FileImageInputStream(new File(path));
            fiis.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            int i = fiis.readUnsignedShort();
            if (i == BMP_START_VALUE)
            {
                return new PdfImageBmp(fiis);
            }
            else if (i == JPEG_START_VALUE)
            {
                fiis = new FileImageInputStream(new File(path));
                return new PdfImageJpeg(fiis);
            }
            else if (i == PNG_START_VALUE)
            {
                fiis = new FileImageInputStream(new File(path));
                return new PdfImagePng(fiis);
            }
            else
            {
                throw new PdfException("Corrupt Image file.");                
            }
        }
        catch (FileNotFoundException fnfe)
        {
            throw new PdfException(
                "Invalid image path. Cannot draw image.");
        }
        catch (EOFException eofe)
        {
            throw new PdfException("Insufficient image data.");
        }
//        finally
//        {
//            if (fiis != null)
//            {
//                fiis.close();
//            }
//        }
    }

    /**
	 * Specifies width for this object.
	 * 
	 * @param width
	 *            width in current measurement unit
	 * @since 1.0            
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfImage.ExampleSyntax.htm#setHeight_Width">example</a>.
	 */
    public synchronized void setWidth(double width)
    {
        scaledWidth = (float) (width < 0 ? -width : width); 
    }
    
    /**
	 * Retrieves width currently set for this object.
	 * 
	 * @return width in current measurement unit
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfImage.ExampleSyntax.htm#getHeight_Width">example</a>.
	 */
    public synchronized float getWidth()
    {
        if(scaledWidth == -1)
        {    
            return 0;
        }  
        return scaledWidth;
    }
    
    /**
	 * Specifies height for this object.
	 * 
	 * @param height
	 *            height in current measurement unit
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfImage.ExampleSyntax.htm#setHeight_Width">example</a>.
	 */
    public synchronized void setHeight(double height)
    {
        scaledHeight = (float) (height < 0 ? -height : height);
    }
    
    /**
	 * Retrieves height currently set for this object.
	 * 
	 * @return height in current measurement unit
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfImage.ExampleSyntax.htm#getHeight_Width">example</a>.
	 */
    public synchronized float getHeight()
    {
        if(scaledHeight == -1)
        {    
            return 0;
        }   
        return scaledHeight;
    }
    
    /**
	 * Specifies rotation angle for this object. The default rotation 
	 * angle for any object is 0.0 degrees.
	 * 
	 * @param r
	 *            angle of rotation in degrees (Made with reference to 
	 *            center of image. Applied in anti-clockwise 
	 *            direction.)
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfImage.ExampleSyntax.htm#setRotation">example</a>.
	 */
    public synchronized void setRotation(double r)
    {
        rotation = (float) r;
        isRotated = true;
    }
    
    /**
	 * Retrieves rotation angle currently set for this object.
	 * 
	 * @return 
	 *            angle of rotation in degrees (Made with reference to 
	 *            center of image. Applied in anti-clockwise 
	 *            direction.) 
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfImage.ExampleSyntax.htm#getRotation">example</a>.
	 */
    public synchronized float getRotation()
    {
        return rotation;
    }

    /**
	 * Returns number of bits used to store value of each component of
	 * colorspace for this image.
	 * 
	 * @return 
	 * 			  number of bits used to store each component of 
	 * 			  colorspace
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfImage.ExampleSyntax.htm#bitsPerComponent">example</a>.
	 */
    public int bitsPerComponent()
    {
        return bitsPerComp;
    }

    /*public*/ int type()
    {
        return type;
    }

    /*public synchronized float scaledWidth()
    {
        return scaledWidth;
    }*/

    /*public synchronized float scaledHeight()
    {
        return scaledHeight;
    }*/

    public PdfObject getColorSpace()
    {
        return colorSpace;
    }

    /**
	 * Retrieves original width of image. Original width is as in the 
	 * image file.
	 * 
	 * @return width of image in pixels
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfImage.ExampleSyntax.htm#width">example</a>.
	 */
    public synchronized float width()
    {
        return width;
    }

    /**
	 * Retrieves original height of image. Original height is as in 
	 * the image file.
	 * 
	 * @return height of image in pixels
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfImage.ExampleSyntax.htm#height">example</a>.
	 */
    public synchronized float height()
    {
        return height;
    }
    
//    public int getCompressionFormat()
//    {
//        return compression;
//    }
//    
//    public void setCompressionFormat(int cf)
//    {
//        this.compression = cf;
//    }

    public int hashCode()
    {
        this.streamBuffer.position(0);
        return this.streamBuffer.hashCode();
    }
    
    protected void store()
    {
        this.tempRotation = this.rotation;
        this.tempScaledWidth = this.scaledWidth ;
        this.tempScaledHeight = this.scaledHeight; 
    }
    
    protected void reStore()
    {
        this.rotation = this.tempRotation;
        this.scaledWidth = this.tempScaledWidth;
        this.scaledHeight = this.tempScaledHeight; 
    }
}
