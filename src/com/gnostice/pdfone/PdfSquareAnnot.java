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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PdfSquareAnnot extends PdfProAnnot
{
    Color interiorColor;

    boolean cloudy;

    int cloudIntensity;
    
    /*PdfRect diffRect;*/

    public Object clone()
    {
        PdfSquareAnnot clone = (PdfSquareAnnot) super.clone();
        if (this.interiorColor != null)
        {
            clone.interiorColor = new Color(interiorColor.getRed(),
                interiorColor.getGreen(), interiorColor.getBlue());
        }
        
        return clone;
    }

    public PdfSquareAnnot()
    {
        interiorColor = Color.WHITE;
        this.annotType = ANNOT_TYPE_SQUARE;
    }

    public PdfSquareAnnot(PdfRect r, String subject, String contents,
        String title)
    {
        super(r, subject, contents, title);
        interiorColor = Color.WHITE;
        this.annotType = ANNOT_TYPE_SQUARE;
    }

    public PdfSquareAnnot(PdfRect r, String subject, String contents,
        String title, Color c)
    {
        super(r, subject, contents, title, c);
        interiorColor = Color.WHITE;
        this.annotType = ANNOT_TYPE_SQUARE;
    }
    
    public PdfSquareAnnot(PdfRect r, String subject, String contents,
        String title, int flags)
    {
        super(r, subject, contents, title, flags);
        interiorColor = Color.WHITE;
        this.annotType = ANNOT_TYPE_SQUARE;
    }
    
    public PdfSquareAnnot(PdfRect r, String subject, String contents,
        String title, int flags, Color c)
    {
        super(r, subject, contents, title, flags, c);
        interiorColor = Color.WHITE;
        this.annotType = ANNOT_TYPE_SQUARE;
    }
    
    public PdfSquareAnnot(PdfRect r, int flags)
    {
        super(r, flags);
        interiorColor = Color.WHITE;
        this.annotType = ANNOT_TYPE_SQUARE;
    }
    
    public PdfSquareAnnot(PdfRect r, int flags, Color c)
    {
        super(r, flags, c);
        this.annotType = ANNOT_TYPE_SQUARE;
        interiorColor = Color.WHITE;
    }
    
    public PdfSquareAnnot(PdfRect r, Color c)
    {
        super(r, c);
        interiorColor = Color.WHITE;
        this.annotType = ANNOT_TYPE_SQUARE;
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

    public synchronized Color getInteriorColor()
    {
        return interiorColor;
    }

    public synchronized void setInteriorColor(Color interiorColor)
    {
        this.interiorColor = interiorColor;
    }

    public synchronized boolean isCloudy()
    {
        return cloudy;
    }

    public synchronized void setCloudy(boolean cloudy)
    {
        this.cloudy = cloudy;
    }

    /*public synchronized PdfRect getDiffRect()
    {
        return diffRect;
    }

    public synchronized void setDiffRect(PdfRect diffRect)
    {
        this.diffRect = diffRect;
    }*/

    protected void set(PdfStdDocument originDoc, PdfStdDocument d)
        throws IOException, PdfException
    {
        super.set(originDoc, d);
        if (d.getVersion() == PdfDocument.VERSION_1_5)
        {
            HashMap annotMap = (HashMap)dict.getMap();
            HashMap hmBE = new HashMap();
            if (this.isCloudy())
            {
                hmBE.put(new PdfName(Usable.PDF_S), new PdfName("C"));
                hmBE.put(new PdfName(Usable.PDF_I), new PdfInteger(this
                    .getCloudIntensity()));
                annotMap.put(new PdfName(Usable.PDF_BE), new PdfDict(hmBE));
            }
            /*if (this.diffRect != null)
            {
                double[] da = new double[] { diffRect.left(),
                    diffRect.right(), diffRect.top(),
                    diffRect.bottom() };
                annotMap.put(new PdfName("RD"), new PdfArray(da));
            }*/
        }
    }
    
    protected PdfAnnot encode(PdfStdPage p) throws PdfException
    {
        super.encode(p);
        /*PdfRect prevDiffRect = this.diffRect;*/
        /*if (this.diffRect!= null)
        {
            diffRect = p.updatePageSettings(this.diffRect);
        }*/

        HashMap annotMap = (HashMap)dict.getMap();
        annotMap.put(new PdfName(Usable.PDF_SUBTYPE), new PdfName(
            Usable.PDF_SQUAREANNOT));

        ArrayList arrIntColor = new ArrayList(4);
        arrIntColor.add(new PdfFloat(interiorColor.getRed() / 255f));
        arrIntColor.add(new PdfFloat(interiorColor.getGreen() / 255f));
        arrIntColor.add(new PdfFloat(interiorColor.getBlue() / 255f));
        annotMap.put(new PdfName(Usable.PDF_IC), new PdfArray(
            arrIntColor));

        /*this.diffRect = prevDiffRect;*/

        return this;
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
            if (name.equals(PDF_IC))
            {
                unknownAttributes.remove(key);
                ArrayList arrColor = (ArrayList) ((PdfArray) value)
                    .getList();
                double r = 0, g = 0, b = 0;

                if (arrColor.get(0) instanceof PdfNumber)
                {
                    r = ((PdfNumber) arrColor.get(0)).getVal();
                }
                if (arrColor.get(1) instanceof PdfNumber)
                {
                    g = ((PdfNumber) arrColor.get(1)).getVal();
                }
                if (arrColor.get(2) instanceof PdfNumber)
                {
                    b = ((PdfNumber) arrColor.get(2)).getVal();
                }
                Color col = new Color((float) r, (float) g, (float) b);
                setInteriorColor(col);
            }
            else if (name.equals(PDF_BE))
            {
                unknownAttributes.remove(key);
                if (value instanceof PdfDict)
                {
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
}