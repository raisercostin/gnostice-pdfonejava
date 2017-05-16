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

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;

/**
 * @author amol
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
final class PdfImagePng extends PdfImage
{
    private FileImageInputStream fiis;
    
    PdfImagePng(FileImageInputStream fiis) throws IOException,
        PdfException
    {
        super();
        this.fiis = fiis;
        //streamDict.setValue(PdfImageJpeg.FILTER, PdfImageJpeg.DCT);
        fiis.seek(0);
        read();
    }
    
    private void read() throws IOException, PdfException
    {
        BufferedImage image = ImageIO.read(fiis);
        ColorModel cm = image.getColorModel();
        ColorSpace cs = cm.getColorSpace();

        int clrspc = cs.getType();
        switch (clrspc)
        {
            case ColorSpace.TYPE_RGB:
                this.colorSpace = CS_DEVICE_RGB;
            	break;
            case ColorSpace.TYPE_GRAY:
                this.colorSpace = CS_DEVICE_GRAY;
            	break;
            default:
                throw new PdfException("ColorSpace '"
                    + cs.getName(clrspc) + "' not supported.");
        }

        this.bitsPerComp = (int) (cm.getPixelSize() / cm
            .getNumComponents());
        this.width = image.getWidth();
        this.height = image.getHeight();
        
        DataBuffer db = image.getData().getDataBuffer();
        if (!(db instanceof DataBufferByte))
        {
            throw new PdfException("Invalid PNG image data.");
        }

        byte[][] bankData = ((DataBufferByte) image.getData()
            .getDataBuffer()).getBankData();
        PdfByteOutputStream baos = new PdfByteOutputStream();
        for (int i = 0; i < bankData.length; ++i)
        {
            baos.write(bankData[i]);
        }
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ImageIO.write(image, "jpg", baos);
        this.streamBuffer = ((ByteBuffer) ByteBuffer.wrap(
            baos.getBuffer()).limit(baos.size())).slice();
    }
}
