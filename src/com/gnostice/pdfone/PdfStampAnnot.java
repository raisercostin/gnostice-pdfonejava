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

public class PdfStampAnnot extends PdfProAnnot
{
    public static final String APPROVED = "Approved";

    public static final String ASIS = "AsIs";

    public static final String CONFIDENTIAL = "Confidential";

    public static final String DEPARTMENTAL = "Departmental";

    public static final String DRAFT = "Draft";

    public static final String EXPERIMENTAL = "Experimental";

    public static final String EXPIRED = "Expired";

    public static final String FINAL = "Final";

    public static final String FOR_COMMENT = "ForComment";

    public static final String FOR_PUBLIC_RELEASE = "ForPublicRelease";

    public static final String NOT_APPROVED = "NotApproved";

    public static final String NOT_FOR_PUBLIC_RELEASE = "NotForPublicRelease";

    public static final String SOLD = "Sold";

    public static final String TOP_SECRET = "TopSecret";
    
    private String stamp;
    
    protected Object clone()
    {
        return (PdfStampAnnot) super.clone();
    }

    public PdfStampAnnot()
    {
        stamp = DRAFT;
        this.annotType = ANNOT_TYPE_STAMP;
    }
    
    public PdfStampAnnot(PdfRect r, String subject, String contents,
        String title)
    {
        super(r, subject, contents, title);
        stamp = DRAFT;
        this.annotType = ANNOT_TYPE_STAMP;
    }

    public PdfStampAnnot(PdfRect r, String subject, String contents,
        String title, Color c)
    {
        super(r, subject, contents, title, c);
        stamp = DRAFT;
        this.annotType = ANNOT_TYPE_STAMP;
    }
    
    public PdfStampAnnot(PdfRect r, String subject, String contents,
        String title, int flags)
    {
        super(r, subject, contents, title, flags);
        stamp = DRAFT;
        this.annotType = ANNOT_TYPE_STAMP;
    }
    
    public PdfStampAnnot(PdfRect r, String subject, String contents,
        String title, int flags, Color c)
    {
        super(r, subject, contents, title, flags, c);
        stamp = DRAFT;
        this.annotType = ANNOT_TYPE_STAMP;
    }
    
    public PdfStampAnnot(PdfRect r, int flags)
    {
        super(r, flags);
        stamp = DRAFT;
        this.annotType = ANNOT_TYPE_STAMP;
    }
    
    public PdfStampAnnot(PdfRect r, int flags, Color c)
    {
        super(r, flags, c);
        stamp = DRAFT;
        this.annotType = ANNOT_TYPE_STAMP;
    }
    
    public PdfStampAnnot(PdfRect r, Color c)
    {
        super(r, c);
        stamp = DRAFT;
        this.annotType = ANNOT_TYPE_STAMP;
    }

    public synchronized String getStamp()
    {
        return stamp;
    }

    public synchronized void setStamp(String stamp)
    {
        this.stamp = stamp;
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
                unknownAttributes.remove(key);
                String n = ((PdfName) value).getString();
                setStamp(DRAFT);
                if (n.equals("Approved"))
                {
                    setStamp(APPROVED);
                }
                else if (n.equals("AsIs"))
                {
                    setStamp(ASIS);
                }
                else if (n.equals("Confidential"))
                {
                    setStamp(CONFIDENTIAL);
                }
                else if (n.equals("Departmental"))
                {
                    setStamp(DEPARTMENTAL);
                }
                else if (n.equals("Experimental"))
                {
                    setStamp(EXPERIMENTAL);
                }
                else if (n.equals("Expired"))
                {
                    setStamp(EXPIRED);
                }
                else if (n.equals("Final"))
                {
                    setStamp(FINAL);
                }
                else if (n.equals("ForComment"))
                {
                    setStamp(FOR_COMMENT);
                }
                else if (n.equals("ForPublicRelease"))
                {
                    setStamp(FOR_PUBLIC_RELEASE);
                }
                else if (n.equals("NotApproved"))
                {
                    setStamp(NOT_APPROVED);
                }
                else if (n.equals("NotForPublicRelease"))
                {
                    setStamp(NOT_FOR_PUBLIC_RELEASE);
                }
                else if (n.equals("Sold"))
                {
                    setStamp(SOLD);
                }
                else if (n.equals("TopSecret"))
                {
                    setStamp(TOP_SECRET);
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

        HashMap annotMap = (HashMap)dict.getMap();
        annotMap.put(new PdfName(Usable.PDF_SUBTYPE), new PdfName(
            Usable.PDF_STAMPANNOT));
        annotMap.put(new PdfName(Usable.PDF_NAME), new PdfName(
            this.stamp));
        
        return this;
    }
}
