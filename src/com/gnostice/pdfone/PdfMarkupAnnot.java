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

public class PdfMarkupAnnot extends PdfProAnnot
{
    private double[] quadPoints;

    public static final byte STYLE_HIGHLIGHT = 0;
    
    public static final byte STYLE_UNDERLINE = 1;
    
    public static final byte STYLE_SQUIGGLY = 2;
    
    public static final byte STYLE_STRIKEOUT = 3;

    private int style;
    
    public PdfMarkupAnnot()
    {
        this.quadPoints = new double[8];
        this.style = STYLE_HIGHLIGHT;
        this.annotType = ANNOT_TYPE_MARKUP;
    }

    public PdfMarkupAnnot(PdfRect r, Color c)
    {
        super(r, c);
        this.quadPoints = new double[8];
        this.style = STYLE_HIGHLIGHT;
        this.annotType = ANNOT_TYPE_MARKUP;
    }

    public PdfMarkupAnnot(PdfRect r, int flags, Color c)
    {
        super(r, flags, c);
        this.quadPoints = new double[8];
        this.style = STYLE_HIGHLIGHT;
        this.annotType = ANNOT_TYPE_MARKUP;
    }

    public PdfMarkupAnnot(PdfRect r, int flags)
    {
        super(r, flags);
        this.quadPoints = new double[8];
        this.style = STYLE_HIGHLIGHT;
        this.annotType = ANNOT_TYPE_MARKUP;
    }

    public PdfMarkupAnnot(PdfRect r, String subject, String contents,
        String title, Color c)
    {
        super(r, subject, contents, title, c);
        this.quadPoints = new double[8];
        this.style = STYLE_HIGHLIGHT;
        this.annotType = ANNOT_TYPE_MARKUP;
    }

    public PdfMarkupAnnot(PdfRect r, String subject, String contents,
        String title, int flags, Color c)
    {
        super(r, subject, contents, title, flags, c);
        this.quadPoints = new double[8];
        this.style = STYLE_HIGHLIGHT;
        this.annotType = ANNOT_TYPE_MARKUP;
    }

    public PdfMarkupAnnot(PdfRect r, String subject, String contents,
        String title, int flags, Color c, double[] quadPoints,
        int style)
    {
        super(r, subject, contents, title, flags, c);
        this.quadPoints = quadPoints;
        this.style = style;
        this.annotType = ANNOT_TYPE_MARKUP;
    }

    public PdfMarkupAnnot(PdfRect r, String subject, String contents,
        String title, int flags)
    {
        super(r, subject, contents, title, flags);
        this.quadPoints = new double[8];
        this.annotType = ANNOT_TYPE_MARKUP;
        this.style = STYLE_HIGHLIGHT;
    }

    public PdfMarkupAnnot(PdfRect r, String subject, String contents,
        String title)
    {
        super(r, subject, contents, title);
        this.quadPoints = new double[8];
        this.style = STYLE_HIGHLIGHT;
        this.annotType = ANNOT_TYPE_MARKUP;
    }

    public PdfMarkupAnnot(String content, int style,
        double[] quadPoints, Color color)
    {
        this.quadPoints = quadPoints;
        this.contents = content;
        this.style = style;
        this.color = color;
        this.annotType = ANNOT_TYPE_MARKUP;
    }

    public Object clone()
    {
        PdfMarkupAnnot clone = (PdfMarkupAnnot) super.clone();
        if (this.quadPoints != null)
        {
            clone.quadPoints = new double[quadPoints.length];
            System.arraycopy(quadPoints, 0, clone.quadPoints, 0,
                quadPoints.length);
        }

        return clone;
    }

    public synchronized double[] getQuadPoints()
    {
        return quadPoints;
    }

    /**Always in page MU */
    public synchronized void setQuadPoints(double[] quadPoints)
    {
        this.quadPoints = quadPoints;
    }

    public synchronized int getStyle()
    {
        return style;
    }

    public synchronized void setStyle(int style)
    {
        this.style = style;
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
            if (name.equals(PDF_QUADPOINTS))
            {
                if (value instanceof PdfArray)
                {
                    unknownAttributes.remove(key);
                    ArrayList arrQuadPoints = (ArrayList) ((PdfArray) value)
                        .getList();
                    int size = arrQuadPoints.size();
                    PdfObject obj = null;
                    double val = 0;
                    double ver[] = new double[size];

                    for (int j = 0; j < size; ++j)
                    {
                        obj = (PdfObject) arrQuadPoints.get(j);
                        if (obj instanceof PdfNumber)
                        {
                            val = ((PdfNumber) obj).getVal();
                            ver[j] = j % 2 == 0 ? val
                                : page.pageHeight - val;
                        }
                    }
                    setQuadPoints(ver);
                }
            }
            else if (name.equals(PDF_SUBTYPE))
            {
                unknownAttributes.remove(key);
                String style = ((PdfName) value).getString();
                int sty = 0;
                if (style.equals("Underline"))
                {
                    sty = 1;
                }
                else if (style.equals("Squiggly"))
                {
                    sty = 2;
                }
                else if (style.equals("StrikeOut"))
                {
                    sty = 3;
                }
                setStyle(sty);
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
        
        double pageHeight = p.pageHeight;
        HashMap annotMap = (HashMap)dict.getMap();
        String sty;
        switch (this.getStyle())
        {
            case STYLE_HIGHLIGHT:
                sty = "Highlight";
                break;

            case STYLE_UNDERLINE:
                sty = "Underline";
                break;

            case STYLE_SQUIGGLY:
                sty = "Squiggly";
                break;

            case STYLE_STRIKEOUT:
                sty = "StrikeOut";
                break;

            default :
                sty = "Highlight";
                break;
        }
        
        annotMap.put(new PdfName(Usable.PDF_SUBTYPE),
            new PdfName(sty));

        double quadpoints[] = this.getQuadPoints();
        if (quadpoints != null)
        {
            if (quadpoints.length % 8 != 0)
            {
                throw new PdfException("Invalid quad points count.");
            }
            ArrayList arrQuadPoints = new ArrayList();
            double[] newQuads = new double[quadpoints.length];
            for (int i = 0; i < quadpoints.length; i += 2)
            {
                PdfPoint point = p.updatePageSettings(new PdfPoint(
                    quadpoints[i], quadpoints[i + 1]));
                newQuads[i] = point.x;
                newQuads[i + 1] = point.y;
            }
        
            for (int i = 0; i < newQuads.length; i++)
            {
                if ((i % 2) > 0)
                {
                    arrQuadPoints.add(new PdfFloat(
                        (float) (pageHeight - newQuads[i])));
                }
                else
                {
                    arrQuadPoints.add(new PdfFloat(
                        (float) newQuads[i]));
                }
            }
            annotMap.put(new PdfName(Usable.PDF_QUADPOINTS),
                new PdfArray(arrQuadPoints));
        }
        
        return this;
    }
}