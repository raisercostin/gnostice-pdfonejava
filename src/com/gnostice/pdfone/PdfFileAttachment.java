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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

import com.gnostice.pdfone.filters.PdfFlateFilter;

final class PdfFileAttachment implements Cloneable
{
    static final int WINDOWS = 1;
    
    static final int UNIX = 2;
    
    static final int MACOS = 3;
    
    int platID;
    
    String filepath;
    
    /* boolean embed; */
    
    boolean isUrl;
    
    boolean isVolatile;
    
    boolean isAbsolutePath;
    
    PdfStream fileStream;
    
    public Object clone()
    {
        Object obj = null;
        try
        {
            obj = super.clone();
            PdfFileAttachment clone = (PdfFileAttachment) obj;
            if (this.fileStream != null)
            {
                clone.fileStream = (PdfStream) this.fileStream
                    .clone();
            }
        }
        catch (CloneNotSupportedException e)
        {
            //never
        }  
        
        return obj; 
    }

    boolean isAbsolute()
    {
        if (filepath == null)
        {
            return false;
        }
        switch (File.separatorChar)
        {
            case '\\':
                this.platID = WINDOWS;
                return filepath.indexOf(':') != -1;
                
            case '/':
                this.platID = UNIX;
                return filepath.charAt(0) == '/';
                
            case ':':
                this.platID = MACOS;
                return true;
                
            default:
                return false;
        }
    }
    
    PdfFileAttachment()
    {
        this.platID = -1;
    }
    
    PdfFileAttachment(String filePath/*, boolean embed*/, boolean isUrl,
        boolean isVolatile)
    {
        this.platID = -1;
        this.filepath = filePath;
        /* this.embed = embed; */
        this.isUrl = isUrl;
        this.isVolatile = isVolatile;
        this.isAbsolutePath = isAbsolute();
    }

/*    synchronized boolean isEmbed()
    {
        return embed;
    }

    synchronized void setEmbed(boolean embed)
    {
        this.embed = embed;
    }
*/
    synchronized int getPlatID()
    {
        return platID;
    }

    synchronized String getFilepath()
    {
        return filepath;
    }

    synchronized void setFilepath(String filepath)
    {
        this.filepath = filepath;
    }

    synchronized boolean isAbsolutePath()
    {
        return isAbsolutePath;
    }

    synchronized boolean isUrl()
    {
        return isUrl;
    }

    synchronized void setUrl(boolean isUrl)
    {
        this.isUrl = isUrl;
    }

    synchronized boolean isVolatile()
    {
        return isVolatile;
    }

    synchronized void setVolatile(boolean isVolatile)
    {
        this.isVolatile = isVolatile;
    }

    synchronized PdfString createPdfFilepath()
    {
        String s = "";
        String separator = "/";
        String regex = File.separator;
        if (regex.equals("\\"))
        {
            regex = "\\\\";
        }
        String[] sa = null;
        switch (platID)
        {
            case WINDOWS:
                sa = this.filepath.split(regex);
                if (isAbsolutePath)
                {
                    for (int i = 0; i < sa.length; ++i)
                    {
                        if (sa[i].equals("")) continue;
                        if (sa[i].indexOf(separator) != -1
                            && sa[i].indexOf(':') == -1) 
                        {
                            int index = sa[i].indexOf(separator); 
                            sa[i] = sa[i].substring(0, index)
                                + "\\"
                                + sa[i].substring(index, sa[i]
                                    .length());
                        }
                        if (sa[i].endsWith(":"))
                        {
                            s += separator + sa[i].substring(0,
                                    sa[i].length() - 1);
                        }
                        else
                        {
                            s += separator + sa[i];
                        }
                    }
                }
                else
                {
                    s += separator;
                    for (int i = 0; i < sa.length; ++i)
                    {
                        if (sa[i].equals("")) continue;
                        if (sa[i].indexOf(separator) != -1
                            && sa[i].indexOf(':') == -1) 
                        {
                            int index = sa[i].indexOf(separator); 
                            sa[i] = sa[i].substring(0, index)
                                + "\\"
                                + sa[i].substring(index, sa[i]
                                    .length());
                        }
                        s += separator + sa[i];
                    }
                }
                break;
                
            case UNIX:
                s = this.filepath;
                break;
                
            case MACOS:
                sa = this.filepath.split(regex);
                for (int i = 0; i < sa.length; ++i)
                {
                    if (sa[i].equals("")) continue;
                    if (sa[i].indexOf(separator) != -1)
                    {
                        int index = sa[i].indexOf(separator); 
                        sa[i] = sa[i].substring(0, index)
                            + "\\"
                            + sa[i].substring(index, sa[i]
                                .length());
                    }
                    s += separator + sa[i];
                }

                break;
                
            default:
                break;
        }
        
        return new PdfString(s);
        //return new PdfTextString(s, true);
    }
    
    synchronized PdfStream createFileStream() throws IOException,
        PdfException
    {
        if(filepath == null || filepath == "")
        {
            throw  new PdfException("Filepath not set");
        }
        FileInputStream fis = new FileInputStream(filepath);
        FileChannel fc = fis.getChannel();
        ByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc
            .size());
        fileStream = new PdfStream(new PdfDict(new HashMap()), bb);
        PdfFlateFilter.encode(fileStream,
            PdfFlateFilter.BEST_COMPRESSION);
        fileStream.getDictionary().getMap().put(
            new PdfName(Usable.PDF_TYPE),
            new PdfName(Usable.PDF_EMBEDDEDFILE));

        return fileStream;
    }
    
    synchronized PdfDict createFileSpecDict()
    {
        HashMap hm = new HashMap();
        hm.put(new PdfName(Usable.PDF_TYPE),
            new PdfName(Usable.PDF_F));
            //new PdfName("FileSpec")); /* see implementation note 45 in Appendix H in PDF Reference */
        
        hm.put(new PdfName(Usable.PDF_F), createPdfFilepath());
        if (isUrl())
        {
            hm.put(new PdfName(Usable.PDF_FS), new PdfName(
                Usable.PDF_URL));
            hm.put(new PdfName(Usable.PDF_F), new PdfString(
                filepath));
        }
        else if (isVolatile())
        {
            hm.put(new PdfName(Usable.PDF_V), PdfBoolean.TRUE);
        }
        
        return new PdfDict(hm);
    }
}
