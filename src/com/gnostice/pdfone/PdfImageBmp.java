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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.stream.FileImageInputStream;

/**
 * @author amol
 * 
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
final class PdfImageBmp extends PdfImage
{
    long bmpFileSize;

    long bmpOffset;

    boolean isTopDown;

    int planes;

    long compression;

    long sizeImage;

    long xPels;

    long yPels;

    long clrUsed;

    long clrImp;

    FileImageInputStream fiis;

    byte[] palette;

    int bitCount; //bits per pixel in BMP file
    
    int type; //this bmp type

    long redMask;
    
    long greenMask;
    
    long blueMask; 
    
    long alphaMask;

    private static final int BMP_HEADER_SIZE = 14;
    
    private static final int BMP_RGBTRIPLE_ENTRIES = 3;
    
    private static final int BMP_RGBQUAD_ENTRIES = 4;
    
    private static final int BI_RGB = 0;

    private static final int BI_RLE8 = 1;

    private static final int BI_RLE4 = 2;

    private static final int BI_BF = 3;

    private static final int BMP_TYPE_2_1BIT = 0;

    private static final int BMP_TYPE_2_4BIT = 1;

    private static final int BMP_TYPE_2_8BIT = 2;

    private static final int BMP_TYPE_2_24BIT = 3;

    private static final int BMP_TYPE_3_1BIT = 4;

    private static final int BMP_TYPE_3_4BIT = 5;

    private static final int BMP_TYPE_3_8BIT = 6;

    private static final int BMP_TYPE_3_24BIT = 7;

    private static final int BMP_TYPE_3_NT_16BIT = 8;

    private static final int BMP_TYPE_3_NT_32BIT = 9;

    private static final int BMP_TYPE_4_1BIT = 10;

    private static final int BMP_TYPE_4_4BIT = 11;

    private static final int BMP_TYPE_4_8BIT = 12;

    private static final int BMP_TYPE_4_16BIT = 13;

    private static final int BMP_TYPE_4_24BIT = 14;

    private static final int BMP_TYPE_4_32BIT = 15;

    PdfImageBmp(FileImageInputStream fiis) throws IOException,
        PdfException
    {
        super();
        this.isTopDown = false;
        this.fiis = fiis;
        parse();
        parseStream();
    }

    private void getBmpCoreInfo(long structSize) throws IOException
    {
        switch (bitCount)
        {
            case 1:
                type = BMP_TYPE_2_1BIT;
                break;
            case 4:
                type = BMP_TYPE_2_4BIT;
                break;
            case 8:
                type = BMP_TYPE_2_8BIT;
                break;
            case 24:
                type = BMP_TYPE_2_24BIT;
                break;
        }
        int palSize = (int) (bmpOffset - structSize - BMP_HEADER_SIZE);
        if (bmpOffset == structSize)
        {
            switch (type)
            {
                case BMP_TYPE_2_1BIT:
                    palSize = 2 * BMP_RGBTRIPLE_ENTRIES;
                    break;
                case BMP_TYPE_2_4BIT:
                    palSize = 16 * BMP_RGBTRIPLE_ENTRIES;
                    break;
                case BMP_TYPE_2_8BIT:
                    palSize = 256 * BMP_RGBTRIPLE_ENTRIES;
                    break;
                case BMP_TYPE_2_24BIT:
                    palSize = 0;
                    break;
            }
            bmpOffset = structSize + palSize;
        }
        palette = new byte[palSize];
        fiis.read(palette, 0, palSize);
    }

    private void getBmpInfoHeader(long structSize)
        throws IOException, PdfException
    {
        switch ((int) compression)
        {
            case BI_RGB: /* fall through */
    
            case BI_RLE8: /* fall through */
    
            case BI_RLE4:
                switch (bitCount)
                {
                    case 1:
                        type = BMP_TYPE_3_1BIT;
                        break;
                    case 4:
                        type = BMP_TYPE_3_4BIT;
                        break;
                    case 8:
                        type = BMP_TYPE_3_8BIT;
                        break;
                    case 16:
                        type = BMP_TYPE_3_NT_16BIT;
                        redMask = 0x7c00;
                        greenMask = 0x3e0;
                        blueMask = 0x1f;
                        break;
                    case 24:
                        type = BMP_TYPE_3_24BIT;
                        break;
                    case 32:
                        type = BMP_TYPE_3_NT_32BIT;
                        redMask = 0x00ff0000;
                        greenMask = 0x0000ff00;
                        blueMask = 0x000000ff;
                        break;
                }
                int palSize = (int) (bmpOffset - structSize - BMP_HEADER_SIZE);
                if (bmpOffset == structSize)
                {
                    switch (type)
                    {
                        case BMP_TYPE_3_1BIT:
                            palSize = (int) (clrUsed == 0 ? 2
                                : clrUsed) * 4;
                            break;
                        case BMP_TYPE_3_4BIT:
                            palSize = (int) (clrUsed == 0 ? 16
                                : clrUsed) * 4;
                            break;
                        case BMP_TYPE_3_8BIT:
                            palSize = (int) (clrUsed == 0 ? 256
                                : clrUsed) * 4;
                            break;
                        default:
                            palSize = 0;
                            break;
                    }
                    bmpOffset = structSize + palSize;
                }
                palette = new byte[palSize];
                fiis.read(palette, 0, palSize);
                break;
            case BI_BF:
                type = (bitCount == 16) ? BMP_TYPE_3_NT_16BIT
                    : BMP_TYPE_3_NT_32BIT;
                redMask = fiis.readUnsignedInt();
                greenMask = fiis.readUnsignedInt();
                blueMask = fiis.readUnsignedInt();
                if (clrUsed != 0)
                {
                    palSize = (int) clrUsed * 4;
                    palette = new byte[palSize];
                    fiis.read(palette, 0, palSize);
                }
                break;
            default:
                throw new PdfException(
                    "Invalid compression specified in BMP file.");
        }
    }

    private void getBmpV4Header(long structSize) throws IOException
    {
        redMask = fiis.readUnsignedInt();
        greenMask = fiis.readUnsignedInt();
        blueMask = fiis.readUnsignedInt();
        alphaMask = fiis.readUnsignedInt();
        
//        long v4ColorSpace = fiis.readUnsignedInt();
        
        /*CIEXYZTRIPLE Structure*/
        //        int cieRedX = fiis.readInt();
        //        int cieRedY = fiis.readInt();
        //        int cieRedZ = fiis.readInt();
        //        int cieGreenX = fiis.readInt();
        //        int cieGreenY = fiis.readInt();
        //        int cieGreenZ = fiis.readInt();
        //        int cieBlueX = fiis.readInt();
        //        int cieBlueY = fiis.readInt();
        //        int cieBlueZ = fiis.readInt();
        //        
        //        long gammaRed = fiis.readUnsignedInt();
        //        long gammaGreen = fiis.readUnsignedInt();
        //        long gammaBlue = fiis.readUnsignedInt();
        
        switch (bitCount)
        {
            case 1:
                type = BMP_TYPE_4_1BIT;
                break;
            case 4:
                type = BMP_TYPE_4_4BIT;
                break;
            case 8:
                type = BMP_TYPE_4_8BIT;
                break;
            case 16:
                type = BMP_TYPE_4_16BIT;
                if ((int) compression == BI_RGB)
                {
                    redMask = 0x7c00;
                    greenMask = 0x3e0;
                    blueMask = 0x1f;
                }
                break;
            case 24:
                type = BMP_TYPE_4_24BIT;
                break;
            case 32:
                type = BMP_TYPE_4_32BIT;
                if ((int) compression == BI_RGB)
                {
                    redMask = 0x00ff0000;
                    greenMask = 0x0000ff00;
                    blueMask = 0x000000ff;
                }
                break;
        }
        
        int palSize = (int) (bmpOffset - structSize - BMP_HEADER_SIZE);
        if (bmpOffset == structSize)
        {
            switch (type)
            {
                case BMP_TYPE_4_1BIT:
                    palSize = (int) (clrUsed == 0 ? 2
                        : clrUsed) * BMP_RGBQUAD_ENTRIES;
                    break;
                case BMP_TYPE_4_4BIT:
                    palSize = (int) (clrUsed == 0 ? 16
                        : clrUsed) * BMP_RGBQUAD_ENTRIES;
                    break;
                case BMP_TYPE_4_8BIT:
                    palSize = (int) (clrUsed == 0 ? 256
                        : clrUsed) * BMP_RGBQUAD_ENTRIES;
                    break;
                default:
                    palSize = 0;
                break;
            }
            bmpOffset = structSize + palSize;
        }
        palette = new byte[palSize];
        fiis.read(palette, 0, palSize);
    }

    private void parseHeader() throws IOException, PdfException
    {
        /* reading bmpFileSize */
        bmpFileSize = fiis.readUnsignedInt();

        /* reading two reserved words */
        fiis.readUnsignedInt();

        /* reading bmpOffset */
        bmpOffset = fiis.readUnsignedInt();
    }

    private void parse() throws IOException, PdfException
    {
        parseHeader();

        long structSize = fiis.readUnsignedInt();
        if (0 == bmpOffset)
        {
            bmpOffset = structSize;
        }
        if (12 < structSize)
        {
            width = fiis.readInt();
            height = fiis.readInt();
        }
        else
        {
            width = fiis.readUnsignedShort();
            height = fiis.readUnsignedShort();
        }
        isTopDown = (height < 0);
        height = Math.abs(height);
        planes = fiis.readUnsignedShort();
        bitCount = fiis.readUnsignedShort();

        if (12 == structSize)
        /* we have to parse BITMAPCOREINFOHEADER here */
        {
            getBmpCoreInfo(structSize);
        }
        else
        {
            compression = fiis.readUnsignedInt();
            sizeImage = fiis.readUnsignedInt();
            xPels = fiis.readInt();
            yPels = fiis.readInt();
            clrUsed = fiis.readUnsignedInt();
            clrImp = fiis.readUnsignedInt();

            if (40 == structSize)
            /* we have to parse BITMAPINFOHEADER here */
            {
                getBmpInfoHeader(structSize);
            }
            else if (108 == structSize)
            /* we have to parse BMPV4HEADER here */
            {
                getBmpV4Header(structSize);
            }
            else
            {
                throw new PdfException(
                    "BMP version not supported.");
            }
        }
    }

    /* This reads the bitmap array based on the image type*/
    private void parseStream() throws IOException, PdfException
    {
        switch (type)
        {
            case BMP_TYPE_2_1BIT:
                readBmpBufferAs1Bit(3);
                break;
            case BMP_TYPE_2_4BIT:
                readBmpBufferAs4Bit(3);
                break;
            case BMP_TYPE_2_8BIT:
                readBmpBufferAs8Bit(3);
                break;
            case BMP_TYPE_2_24BIT:
                readBmpBufferAs24Bit();
                break;
            case BMP_TYPE_3_1BIT:
                readBmpBufferAs1Bit(4);
                break;
            case BMP_TYPE_3_4BIT:
                switch ((int) compression)
                {
                    case BI_RGB:
                        readBmpBufferAs4Bit(4);
                        break;
                    case BI_RLE4:
                        readRLE4EncodedBmpBuffer();
                        break;
                    default:
                        throw new PdfException(
                            "Invalid or unsupported compression " + 
                            "encountered in BMP.");
                }
                break;
            case BMP_TYPE_3_8BIT:
                switch ((int) compression)
                {
                    case BI_RGB:
                        readBmpBufferAs8Bit(4);
                        break;
                    case BI_RLE8:
                        readRLE8EncodedBmpBuffer();
                        break;
                    default:
                        throw new PdfException(
                            "Invalid or unsupported compression " + 
                            "encountered in BMP.");
                }
                break;
            case BMP_TYPE_3_24BIT:
                readBmpBufferAs24Bit();
                break;
            case BMP_TYPE_3_NT_16BIT:
                readBmpBufferAs16Or32Bit(false);
                break;
            case BMP_TYPE_3_NT_32BIT:
                readBmpBufferAs16Or32Bit(true);
                break;
            case BMP_TYPE_4_1BIT:
                readBmpBufferAs1Bit(4);
                break;
            case BMP_TYPE_4_4BIT:
                switch ((int) compression)
                {
                    case BI_RGB:
                        readBmpBufferAs4Bit(4);
                        break;
                    case BI_RLE4:
                        readRLE4EncodedBmpBuffer();
                        break;
                    default:
                        throw new PdfException(
                            "Invalid or unsupported compression " + 
                            "encountered in BMP.");
                }
                break;
            case BMP_TYPE_4_8BIT:
                switch ((int) compression)
                {
                    case BI_RGB:
                        readBmpBufferAs8Bit(4);
                        break;
                    case BI_RLE8:
                        readRLE8EncodedBmpBuffer();
                        break;
                    default:
                        throw new PdfException(
                            "Invalid or unsupported compression " + 
                            "encountered in BMP.");
                }
                break;
            case BMP_TYPE_4_16BIT:
                readBmpBufferAs16Or32Bit(false);
                break;
            case BMP_TYPE_4_24BIT:
                readBmpBufferAs24Bit();
                break;
            case BMP_TYPE_4_32BIT:
                readBmpBufferAs16Or32Bit(true);
                break;
        }
    }

    private void fillLookUpStream(int paletteWidth)
    {
        ArrayList l = new ArrayList();
        l.add(CS_INDEXED);
        l.add(CS_DEVICE_RGB);
        if (palette != null)
        {
            int palEntries = palette.length / paletteWidth;
            byte[] ba = new byte[palEntries * BMP_RGBTRIPLE_ENTRIES];
            for (int i = 0; i < palEntries; ++i)
            {
                int s = i * paletteWidth;
                int d = i * BMP_RGBTRIPLE_ENTRIES;
                ba[d + 2] = palette[s++];
                ba[d + 1] = palette[s++];
                ba[d] = palette[s];
            }
            lookUpStream = new PdfStream(new PdfDict(new HashMap()),
                ByteBuffer.wrap(ba));
            l.add(new PdfInteger((ba.length / 3) - 1));
            l.add(lookUpStream);
        }
        colorSpace = new PdfArray(l);
    }

    /* Read the bitmap buffer as 1 bit index into the palette */
    private void readBmpBufferAs1Bit(int paletteWidth)
        throws IOException, PdfException
    {
        this.isIndexed = true;
        this.bitsPerComp = bitCount;
        int bytesPerLine = (int) Math.ceil(width / 8);
        int paddedBytes = bytesPerLine % 4 == 0 ? 0
            : 4 - (bytesPerLine % 4); //4 bytes per word 
        int bufSize = (bytesPerLine + paddedBytes) * (int) height;
        byte[] buffer = new byte[bufSize];
        byte[] temp = new byte[bufSize];

        for (int bytesRead = 0; bytesRead < bufSize; )
        {
            bytesRead += fiis.read(temp, bytesRead,
                bufSize - bytesRead);
        }
        if (!isTopDown)
        {
            for (int i = 0; i < height; i++)
            {
                System.arraycopy(temp, bufSize - (i + 1)
                    * (bytesPerLine + paddedBytes), buffer,
                    i * bytesPerLine, bytesPerLine);
            }
        }
        else //if top - down
        {
            for (int i = 0; i < height; i++)
            {
                System.arraycopy(temp, i
                    * (bytesPerLine + paddedBytes), buffer,
                    i * bytesPerLine, bytesPerLine);
            }
        }
        streamBuffer = ByteBuffer.wrap(buffer);
        fillLookUpStream(paletteWidth);
    }

    /* Read the bitmap buffer as 4 bit index into the palette */
    private void readBmpBufferAs4Bit(int paletteWidth)
        throws IOException, PdfException
    {
        this.isIndexed = true;
        this.bitsPerComp = bitCount;
        int bytesPerLine = (int) Math.ceil((double) width * 4 / 8);
        int paddedBytes = bytesPerLine % 4 == 0 ? 0
            : 4 - (bytesPerLine % 4); //4 bytes per word
        int bufSize = (bytesPerLine + paddedBytes) * (int) height;
        byte[] buffer = new byte[bufSize];
        byte[] temp = new byte[bufSize];

        for (int bytesRead = 0; bytesRead < bufSize; )
        {
            bytesRead += fiis.read(temp, bytesRead,
                bufSize - bytesRead);
        }
        if (!isTopDown)
        {
            for (int i = 0; i < height; i++)
            {
                System.arraycopy(temp, bufSize - (i + 1)
                    * (bytesPerLine + paddedBytes), buffer,
                    i * bytesPerLine, bytesPerLine);
            }
        }
        else //if top - down
        {
            for (int i = 0; i < height; i++)
            {
                System.arraycopy(temp, i
                    * (bytesPerLine + paddedBytes), buffer,
                    i * bytesPerLine, bytesPerLine);
            }
        }
        streamBuffer = ByteBuffer.wrap(buffer);
        fillLookUpStream(paletteWidth);
    }

    /* Read the bitmap buffer as 8 bit index into the palette */
    private void readBmpBufferAs8Bit(int paletteWidth)
        throws IOException, PdfException
    {
        this.isIndexed = true;
        this.bitsPerComp = bitCount;
        int bitsPerLine = (int) width * 8;
        int paddedBytes = 0;
        if (bitsPerLine % 32 != 0)
        {
            paddedBytes = (bitsPerLine / 32 + 1) * 32 - bitsPerLine;
            paddedBytes = (int) Math.ceil(paddedBytes / 8);
        }
        int bufSize = (int) ((width + paddedBytes) * height);
        byte[] buffer = new byte[bufSize];
        byte[] temp = new byte[bufSize];

        for (int bytesRead = 0; bytesRead < bufSize; )
        {
            bytesRead += fiis.read(temp, bytesRead,
                bufSize - bytesRead);
        }
        if (!isTopDown)
        {
            for (int i = 0; i < height; i++)
            {
                System.arraycopy(temp, (int) (bufSize - (i + 1)
                    * (width + paddedBytes)), buffer, (int) (i * width),
                    (int) width);
            }
        }
        else
        {
            for (int i = 0; i < height; i++)
            {
                System.arraycopy(temp, i * ((int) width + paddedBytes),
                    buffer, i * (int) width, (int) width);
            }
        }
        streamBuffer = ByteBuffer.wrap(buffer);
        fillLookUpStream(paletteWidth);
    }

    /* Read the bitmap buffer as 16 or 32 bit BMP data */
    private void readBmpBufferAs16Or32Bit(boolean is32)
        throws IOException, PdfException
    {
        this.bitsPerComp = 8;
        int index = 0;
        int v = 0;
        int bitsPerLine = (int) width * 16;
        int paddedBytes = 0;
        if (bitsPerLine % 32 != 0)
        {
            paddedBytes = (bitsPerLine / 32 + 1) * 32 - bitsPerLine;
            paddedBytes = (int) Math.ceil(paddedBytes / 8);
        }
        byte[] buffer = new byte[(int) (width * height * 3)];
        
        int red_mask = getMask((int) redMask);
        int red_shift = getShift((int) redMask);
        int red_factor = red_mask + 1;
        int green_mask = getMask((int) greenMask);
        int green_shift = getShift((int) greenMask);
        int green_factor = green_mask + 1;
        int blue_mask = getMask((int) blueMask);
        int blue_shift = getShift((int) blueMask);
        int blue_factor = blue_mask + 1;
    
        if (!isTopDown)
        {
            //int max = (int) (width * height - 1);
            for (int i = (int) height - 1; i >= 0; --i)
            {
                index = (int) width * 3 * i;
                for (int j = 0; j < width; ++j)
                {
                    v = is32 ? (int) fiis.readUnsignedInt() : fiis
                        .readUnsignedShort(); 
                    buffer[index++] = (byte) 
                    	(((v >>> red_shift) & red_mask) * 256 / red_factor);
                    buffer[index++] = (byte)
                    	(((v >>> green_shift) & green_mask) * 256 / green_factor);
                    buffer[index++] = (byte)
                    	(((v >>> blue_shift) & blue_mask) * 256 / blue_factor);
                }
            }
        }
        else
        {
            for (int i = 0; i < height; ++i)
            {
                for (int j = 0; j < width; ++j)
                {
                    v = is32 ? (int) fiis.readUnsignedInt() : fiis
                        .readUnsignedShort(); 
                    buffer[index++] = (byte)
                    	(((v >>> red_shift) & red_mask) * 256 / red_factor);
                    buffer[index++] = (byte)
                    	(((v >>> green_shift) & green_mask) * 256 / green_factor);
                    buffer[index++] = (byte)
                    	(((v >>> blue_shift) & blue_mask) * 256 / blue_factor);
                }
            }
        }
        for (int i = 0; i < paddedBytes; ++i)
        {
            fiis.read();
        }
        colorSpace = CS_DEVICE_RGB;
        streamBuffer = ByteBuffer.wrap(buffer);
    }

    /* Read the bitmap buffer as 24 bit BMP data */
    private void readBmpBufferAs24Bit() throws IOException,
        PdfException
    {
        this.isIndexed = true;
        this.bitsPerComp = 8;
        int br = 0;
        int bitsPerLine = (int) width * 24;
        int paddedBytes = 0;
        if (bitsPerLine % 32 != 0)
        {
            paddedBytes = (bitsPerLine / 32 + 1) * 32 - bitsPerLine;
            paddedBytes = (int) Math.ceil(paddedBytes / 8);
        }
        byte[] buffer = new byte[(int) (width * height * 3)];
        int bmpSize = (int) (((width * 3 + 3) / 4 * 4) * height);
        byte[] temp = new byte[bmpSize];
        int index = 0;
        int count = -paddedBytes;

        for (int bytesRead = 0; bytesRead < bmpSize; bytesRead += br)
        {
            br = fiis.read(temp, bytesRead, bmpSize - bytesRead);
            if (br < 0) break; //EOF
        }
        if (!isTopDown)
        {
            int limit = (int) (width * height * 3 - 1);
            for (int i = 0; i < height; ++i)
            {
                index = (int) (limit - (i + 1) * width * 3 + 1);
                count += paddedBytes;
                for (int j = 0; j < width; ++j, index += 3)
                {
                    buffer[index + 2] = temp[count++];
                    buffer[index + 1] = temp[count++];
                    buffer[index] = temp[count++];
                }
            }
        }
        else
        {
            for (int i = 0; i < height; ++i)
            {
                count += paddedBytes;
                for (int j = 0; j < width; ++j, index += 3)
                {
                    buffer[index + 2] = temp[count++];
                    buffer[index + 1] = temp[count++];
                    buffer[index] = temp[count++];
                }
            }
        }
        colorSpace = CS_DEVICE_RGB;
        streamBuffer = ByteBuffer.wrap(buffer);
    }

    private void readRLE4EncodedBmpBuffer() throws IOException, PdfException
    {
        int bufSize = (int) sizeImage;
        if (bufSize == 0)
        {
            bufSize = (int) (bmpFileSize - bmpOffset);
        }
        byte[] temp = new byte[bufSize];

        for (int bytesRead = 0; bytesRead < bufSize; )
        {
            bytesRead += fiis.read(temp, bytesRead, bufSize - bytesRead);
        }

        //Decompress
        temp = decodeRLEEncodedBuffer(false, temp);
        
        //do this with System.arraycopy()
        if (!isTopDown)
        {
            byte inverted[] = temp;
            temp = new byte[(int) (width * height)];
            int l = 0, index, lineEnd;
            for (int i = (int) height - 1; i >= 0; i--)
            {
                index = i * (int) width;
                lineEnd = l + (int) width;
                while (l != lineEnd)
                {
                    temp[l++] = inverted[index++];
                }
            }
        }

        byte[] buffer = new byte[(int) ((int) ((width + 1) / 2) * height)];
        int ptr = 0;
        int sh = 0;
        for (int i = 0; i < height; ++i, sh += (int) ((width + 1) / 2))
        {
            for (int j = 0; j < width; ++j)
            {
                if ((j & 1) == 0)
                    buffer[sh + j / 2] = (byte) (buffer[ptr++] << 4);
                else
                    buffer[sh + j / 2] |= (byte) (buffer[ptr++] & 0x0f);
            }
        }
        streamBuffer = ByteBuffer.wrap(buffer);
        fillLookUpStream(4);
    }

    private void readRLE8EncodedBmpBuffer() throws IOException, PdfException
    {
        int bufSize = (int) sizeImage;
        if (bufSize == 0)
        {
            bufSize = (int) (bmpFileSize - bmpOffset);
        }
        byte[] buffer = new byte[bufSize];

        for (int bytesRead = 0; bytesRead < bufSize; )
        {
            bytesRead += fiis.read(buffer, bytesRead, bufSize
                - bytesRead);
        }

        //Decompress
        buffer = decodeRLEEncodedBuffer(true, buffer);
        
        int newBufSize = (int) (width * height);
        if ( !isTopDown)
        {
            byte[] temp = new byte[buffer.length];
            int bytesPerLine = (int) width;
            for (int i = 0; i < height; i++)
            {
                System.arraycopy(buffer, newBufSize - (i + 1)
                    * (bytesPerLine), temp, i * bytesPerLine,
                    bytesPerLine);
            }
            buffer = temp;
        }

        streamBuffer = ByteBuffer.wrap(buffer);
        fillLookUpStream(4);
    }

    private byte[] decodeRLEEncodedBuffer(boolean is8, byte values[])
    {
        byte val[] = new byte[(int) (width * height)];
        try
        {
            int ptr = 0;
            int x = 0;
            int q = 0;
            for (int y = 0; y < height && ptr < values.length;)
            {
                int count = values[ptr++] & 0xff;
                if (count != 0)
                {
                    // encoded mode
                    int bt = values[ptr++] & 0xff;
                    if (is8)
                    {
                        for (int i = count; i != 0; --i)
                        {
                            val[q++] = (byte) bt;
                        }
                    }
                    else
                    {
                        for (int i = 0; i < count; ++i)
                        {
                            val[q++] = (byte) ((i & 1) == 1 ? (bt & 0x0f)
                                : ((bt >>> 4) & 0x0f));
                        }
                    }
                    x += count;
                }
                else
                {
                    // escape mode
                    count = values[ptr++] & 0xff;
                    if (count == 1)
                        break;
                    switch (count)
                    {
                        case 0:
                            x = 0;
                            ++y;
                            q = y * (int) width;
                            break;
                        case 2:
                            // delta mode
                            x += values[ptr++] & 0xff;
                            y += values[ptr++] & 0xff;
                            q = y * (int) width + x;
                            break;
                        default:
                            // absolute mode
                            if (is8)
                            {
                                for (int i = count; i != 0; --i)
                                    val[q++] = (byte) (values[ptr++] & 0xff);
                            }
                            else
                            {
                                int bt = 0;
                                for (int i = 0; i < count; ++i)
                                {
                                    if ((i & 1) == 0)
                                        bt = values[ptr++] & 0xff;
                                    val[q++] = (byte) ((i & 1) == 1 ? (bt & 0x0f)
                                        : ((bt >>> 4) & 0x0f));
                                }
                            }
                            x += count;
                            // read pad byte
                            if (is8)
                            {
                                if ((count & 1) == 1)
                                    ++ptr;
                            }
                            else
                            {
                                if ((count & 3) == 1
                                    || (count & 3) == 2)
                                    ++ptr;
                            }
                            break;
                    }
                }
            }
        }
        catch (Exception e)
        {
            //empty on purpose
        }

        return val;
    }

    private int getMask(int val)
    {
        for (int shift = 0; shift < 32; ++shift, val >>>= 1)
        {
            if ((val & 1) == 1) break;
        }
        return val;
    }

    private int getShift(int val)
    {
        int shift = 0;
        for (; shift < 32; ++shift, val >>>= 1)
        {
            if ((val & 1) == 1) break;
        }
        return shift;
    }
}