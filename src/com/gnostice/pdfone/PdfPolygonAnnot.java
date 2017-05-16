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

public class PdfPolygonAnnot extends PdfPolylineAnnot
{
    private int cloudIntensity;
    
    private boolean cloudy;

    public Object clone()
    {
        return (PdfPolygonAnnot) super.clone();
    }

    public PdfPolygonAnnot()
    {
        super();
        this.annotType = ANNOT_TYPE_POLYGON;
    }

    public PdfPolygonAnnot(PdfRect r, String subject,
        String contents, String title, int flags, Color c,
        boolean isCloudy, int cloudIntensity)
    {
        super(r, subject, contents, title, flags, c);
        if (isCloudy)
        {
            this.cloudy = isCloudy;
            this.cloudIntensity = cloudIntensity;
        }
        this.annotType = ANNOT_TYPE_POLYGON;
    }

    public PdfPolygonAnnot(PdfRect r, String subject, String contents,
        String title)
    {
        super(r, subject, contents, title);
        this.annotType = ANNOT_TYPE_POLYGON;
    }

    public PdfPolygonAnnot(PdfRect r, String subject, String contents,
        String title, Color c)
    {
        super(r, subject, contents, title, c);
        this.annotType = ANNOT_TYPE_POLYGON;
    }
    
    public PdfPolygonAnnot(PdfRect r, String subject, String contents,
        String title, int flags)
    {
        super(r, subject, contents, title, flags);
        this.annotType = ANNOT_TYPE_POLYGON;
    }
    
    public PdfPolygonAnnot(PdfRect r, String subject, String contents,
        String title, int flags, Color c)
    {
        super(r, subject, contents, title, flags, c);
        this.annotType = ANNOT_TYPE_POLYGON;
    }
    
    public PdfPolygonAnnot(PdfRect r, int flags)
    {
        super(r, flags);
        this.annotType = ANNOT_TYPE_POLYGON;
    }
    
    public PdfPolygonAnnot(PdfRect r, int flags, Color c)
    {
        super(r, flags, c);
        this.annotType = ANNOT_TYPE_POLYGON;
    }
    
    public PdfPolygonAnnot(PdfRect r, Color c)
    {
        super(r, c);
        this.annotType = ANNOT_TYPE_POLYGON;
    }
    
    public synchronized boolean isCloudy()
    {
        return cloudy;
    }

    public synchronized void setCloudy(boolean cloudy)
    {
        this.cloudy = cloudy;
    }

    public synchronized int getCloudIntensity()
    {
        return cloudIntensity;
    }

    public synchronized void setCloudIntensity(int cloudIntensity)
    {
        if (cloudIntensity >= 0 && cloudIntensity <= 2)
        {
            this.cloudIntensity = cloudIntensity;
        }
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
            if (name.equals(PDF_BE))
            {
                if (value instanceof PdfDict)
                {
                    unknownAttributes.remove(key);
                    Iterator be = ((PdfDict) value).getMap().keySet()
                        .iterator();
                    while (be.hasNext())
                    {
                        PdfObject beKey = (PdfObject) be.next();
                        PdfObject beValue = (PdfObject) ((PdfDict) value)
                            .getMap().get(beKey);
                        beValue = page.originDoc.reader
                            .getObject(beValue);

                        String bename = ((PdfName) beKey).getString();
                        if (bename.equals(PDF_S))
                        {
                            if (((PdfName) beValue).getString()
                                .equals("C"))
                            {
                                setCloudy(true);
                            }
                        }
                        else if (bename.equals(PDF_I))
                        {
                            if (beValue instanceof PdfNumber)
                            {
                                setCloudIntensity((int) ((PdfNumber) beValue)
                                    .getVal());
                            }
                        }
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
        
        HashMap annotMap = (HashMap)dict.getMap();
        annotMap.put(new PdfName(Usable.PDF_SUBTYPE), new PdfName(
            Usable.PDF_POLYGONANNOT));

        HashMap hmBE = new HashMap();
        if (this.isCloudy())
        {
            hmBE.put(new PdfName(Usable.PDF_S), new PdfName("C"));
            hmBE.put(new PdfName(Usable.PDF_I), new PdfInteger(this
                .getCloudIntensity()));
            annotMap.put(new PdfName(Usable.PDF_BE), new PdfDict(hmBE));
        }
        annotMap.remove(new PdfName(Usable.PDF_LE));

        return this;
    }
}