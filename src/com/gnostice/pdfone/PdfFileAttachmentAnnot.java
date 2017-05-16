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

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class PdfFileAttachmentAnnot extends PdfProAnnot
{
    public static final int ICON_GRAPH = 0; 

    public static final int ICON_PAPERCLIP = 1;
    
    public static final int ICON_PUSHPIN = 2; 
    
    public static final int ICON_TAG = 3; 
    
    private PdfFileAttachment fa;
    
    private int icon;
    
    PdfStream fStream;
    
    protected PdfDict fsDict;

    public Object clone()
    {
        PdfFileAttachmentAnnot clone = (PdfFileAttachmentAnnot) super
            .clone();
        clone.fa = this.fa == null ? null
            : (PdfFileAttachment) this.fa.clone();
        clone.fStream = this.fStream == null ? null
            : (PdfStream) this.fStream.clone();
        clone.fsDict = this.fsDict == null ? null : (PdfDict) fsDict
            .clone();

        return clone;
    }

    public PdfFileAttachmentAnnot()
    {
        this.fa = new PdfFileAttachment();
        this.annotType = ANNOT_TYPE_FILE_ATTACHMENT;
    }
    
    public PdfFileAttachmentAnnot(double x, double y, String subject,
        String contents, String title, int flags, Color c,
        String filePath,/* boolean embed, */boolean isUrl,
        boolean isVolatile, int icon)
    {
        super(new PdfRect(x, y, 0, 0), subject, contents, title,
            flags, c);
        this.fa = new PdfFileAttachment(filePath/* , embed */,
            isUrl, isVolatile);
        this.icon = icon;
        this.annotType = ANNOT_TYPE_FILE_ATTACHMENT;
    }

    public PdfFileAttachmentAnnot(double x, double y, String subject,
        String contents, String title)
    {
        super(new PdfRect(x, y, 0, 0), subject, contents, title);
        this.fa = new PdfFileAttachment();
        this.annotType = ANNOT_TYPE_FILE_ATTACHMENT;
    }

    public PdfFileAttachmentAnnot(double x, double y, String subject,
        String contents, String title, Color c)
    {
        super(new PdfRect(x, y, 0, 0), subject, contents, title, c);
        this.fa = new PdfFileAttachment();
        this.annotType = ANNOT_TYPE_FILE_ATTACHMENT;
    }

    public PdfFileAttachmentAnnot(double x, double y, String subject,
        String contents, String title, int flags)
    {
        super(new PdfRect(x, y, 0, 0), subject, contents, title,
            flags);
        this.fa = new PdfFileAttachment();
        this.annotType = ANNOT_TYPE_FILE_ATTACHMENT;
    }

    public PdfFileAttachmentAnnot(double x, double y, String subject,
        String contents, String title, int flags, Color c)
    {
        super(new PdfRect(x, y, 0, 0), subject, contents, title,
            flags, c);
        this.fa = new PdfFileAttachment();
        this.annotType = ANNOT_TYPE_FILE_ATTACHMENT;
    }

    public PdfFileAttachmentAnnot(double x, double y, int flags)
    {
        super(new PdfRect(x, y, 0, 0), flags);
        this.fa = new PdfFileAttachment();
        this.annotType = ANNOT_TYPE_FILE_ATTACHMENT;
    }

    public PdfFileAttachmentAnnot(double x, double y, int flags,
        Color c)
    {
        super(new PdfRect(x, y, 0, 0), flags, c);
        this.fa = new PdfFileAttachment();
        this.annotType = ANNOT_TYPE_FILE_ATTACHMENT;
    }

    public PdfFileAttachmentAnnot(double x, double y, Color c)
    {
        super(new PdfRect(x, y, 0, 0), c);
        this.fa = new PdfFileAttachment();
        this.annotType = ANNOT_TYPE_FILE_ATTACHMENT;
    }

    public PdfFileAttachmentAnnot(String filePath,/* boolean embed, */
        boolean isUrl, boolean isVolatile, int icon)
    {
        this.fa = new PdfFileAttachment(filePath/* , embed */,
            isUrl, isVolatile);
        this.icon = icon;
        this.annotType = ANNOT_TYPE_FILE_ATTACHMENT;
    }

    /* Returns null in reading mode */
    public synchronized String getFilePath()
    {
        return fa.filepath;
    }

    public synchronized void setFilePath(String filePath)
    {
        this.fa.filepath = filePath;
        this.fa.isAbsolutePath = this.fa.isAbsolute();
        if (unknownAttributes != null)
        {
            unknownAttributes.remove(new PdfName(PDF_FS));
        }
    }

    public synchronized int getIcon()
    {
        return icon;
    }

    public synchronized void setIcon(int icon)
    {
        this.icon = icon;
    }

    void applyPropertiesFrom(PdfDict annotDict, PdfStdPage page)
        throws IOException, PdfException
    {
        super.applyPropertiesFrom(annotDict, page);
        HashMap annotMap = (HashMap) annotDict.getMap();
        Iterator iter = annotMap.keySet().iterator();
        String name;
        PdfObject key, value, currObj;

        while (iter.hasNext())
        {
            key = (PdfObject) iter.next();
            currObj = (PdfObject) annotMap.get(key);
            value = page.originDoc.reader.getObject(currObj);
            if (value instanceof PdfNull)
            {
                continue;
            }
            name = ((PdfName) key).getString();
            if (name.equals(PDF_NAME))
            {
                if (value instanceof PdfName)
                {
                    unknownAttributes.remove(key);
                    String sym = ((PdfName) value).getString();
                    if (sym.equals("Graph"))
                    {
                        setIcon(ICON_GRAPH);
                    }
                    if (sym.equals("PushPin"))
                    {
                        setIcon(ICON_PUSHPIN);
                    }
                    if (sym.equals("Tag"))
                    {
                        setIcon(ICON_TAG);
                    }
                    else
                    {
                        setIcon(ICON_PAPERCLIP);
                    }
                }
            }
            else
            {
                if (! knownAttributes.containsKey(name))
                {
                    unknownAttributes.put(key,
                        value.objNumber == 0 ? value : currObj);
                }
            }
        }
    }

    protected PdfAnnot encode(PdfStdPage p) throws PdfException
    {
        super.encode(p);
        
        HashMap annotMap = (HashMap) dict.getMap();
        annotMap.put(new PdfName(Usable.PDF_SUBTYPE), new PdfName(
            Usable.PDF_FILEATTACHMENTANNOT));
        
        String iconName;
        switch (this.getIcon())
        {
            case ICON_GRAPH:
                iconName = "Graph";
                break;

            case ICON_PAPERCLIP:
                iconName = "Paperclip";
                break;

            case ICON_PUSHPIN:
                iconName = "PushPin";
                break;

            case ICON_TAG:
                iconName = "Tag";
                break;

            default:
                iconName = "Paperclip";
        }
        annotMap.put(new PdfName(Usable.PDF_NAME), new PdfName(
            iconName));

        return this;
    }

    protected void set(PdfStdDocument originDoc, PdfStdDocument d)
        throws IOException, PdfException
    {
        super.set(originDoc, d);
        if ((unknownAttributes == null)
            || ( !unknownAttributes.containsKey(new PdfName(PDF_FS))))
        {
            fsDict = fa.createFileSpecDict();
            fsDict.objNumber = d.objectRun++;
            if (/* fa.isEmbed() && */!fa.isUrl())
            {
                fStream = fa.createFileStream();
                fStream.objNumber = d.objectRun++;
                HashMap hm = new HashMap();
                hm.put(new PdfName(Usable.PDF_F),
                    new PdfIndirectReference(fStream.objNumber, 0));
                fsDict.getMap().put(new PdfName(Usable.PDF_EF),
                    new PdfDict(hm));
            }
        }
    }

    protected void write(PdfStdDocument d) throws IOException,
        PdfException
    {
        super.write(d);
        int index = 0;
        HashMap annotMap = (HashMap) dict.getMap();
        if ((unknownAttributes == null)
            || ( !unknownAttributes.containsKey(new PdfName(PDF_FS))))
        {

            annotMap.put(new PdfName(PDF_FS),
                new PdfIndirectReference(fsDict.objNumber, 0));
            index = fsDict.getObjectNumber();
            d.offset[index] = d.bytesWritten;
            d.bytesWritten += d.writer.writeIndirectObject(fsDict);
        }

        if (fStream != null)
        {
            index = fStream.getObjectNumber();
            d.offset[index] = d.bytesWritten;
            d.bytesWritten += d.writer.writeIndirectObject(fStream);
        }
    }
}