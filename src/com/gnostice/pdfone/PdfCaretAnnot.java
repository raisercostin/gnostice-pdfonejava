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

public class PdfCaretAnnot extends PdfProAnnot
{
    public static final int SYMBOL_NONE = 0;

    public static final int SYMBOL_PARAGRAPH = 1;
    
    private int symbol;

    private PdfRect diffRect;
    
    public Object clone()
    {
        PdfCaretAnnot clone = (PdfCaretAnnot) super.clone();
        if (this.diffRect != null)
        {
            clone.diffRect = (PdfRect) this.diffRect.clone();
        }

        return clone;
    }

    public PdfCaretAnnot()
    {
        this.annotType = ANNOT_TYPE_CARET;
    }
    
    public PdfCaretAnnot(PdfRect r, String subject, String contents,
        String title, int flags, Color c, int symbol)
    {
        super(r, subject, contents, title, flags, c);
        this.symbol = symbol;
        this.annotType = ANNOT_TYPE_CARET;
    }

    public PdfCaretAnnot(PdfRect r, String subject, String contents,
        String title)
    {
        super(r, subject, contents, title);
        this.annotType = ANNOT_TYPE_CARET;
    }

    public PdfCaretAnnot(PdfRect r, String subject, String contents,
        String title, Color c)
    {
        super(r, subject, contents, title, c);
        this.annotType = ANNOT_TYPE_CARET;
    }
    
    public PdfCaretAnnot(PdfRect r, String subject, String contents,
        String title, int flags)
    {
        super(r, subject, contents, title, flags);
        this.annotType = ANNOT_TYPE_CARET;
    }
    
    public PdfCaretAnnot(PdfRect r, String subject, String contents,
        String title, int flags, Color c)
    {
        super(r, subject, contents, title, flags, c);
        this.annotType = ANNOT_TYPE_CARET;
    }
    
    public PdfCaretAnnot(PdfRect r, int flags)
    {
        super(r, flags);
        this.annotType = ANNOT_TYPE_CARET;
    }
    
    public PdfCaretAnnot(PdfRect r, int flags, Color c)
    {
        super(r, flags, c);
        this.annotType = ANNOT_TYPE_CARET;
    }
    
    public PdfCaretAnnot(PdfRect r, Color c)
    {
        super(r, c);
        this.annotType = ANNOT_TYPE_CARET;
    }
    
    public int getSymbol()
    {
        return symbol;
    }

    public void setSymbol(int symbol)
    {
        this.symbol = symbol;
    }

    public synchronized PdfRect getDiffRect()
    {
        return diffRect;
    }

    public synchronized void setDiffRect(PdfRect diffRect)
    {
        this.diffRect = diffRect;
    }
    
    public synchronized double getBottomRD()
    {
        return diffRect.bottom();
    }

    public synchronized void setBottomRD(float bottomRD)
    {
        this.diffRect.height = bottomRD - diffRect.y;
    }

    public synchronized double getLeftRD()
    {
        return this.diffRect.left();
    }

    public synchronized void setLeftRD(float leftRD)
    {
        this.diffRect.x = leftRD;
    }

    public synchronized double getRightRD()
    {
        return this.diffRect.right();
    }

    public synchronized void setRightRD(float rightRD)
    {
        this.diffRect.width = rightRD - this.diffRect.x;
    }

    public synchronized double getTopRD()
    {
        return this.diffRect.top();
    }

    public synchronized void setTopRD(float topRD)
    {
        this.diffRect.y = topRD;
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
            if (name.equals("Sy"))
            {
                if (value instanceof PdfName)
                {
                    unknownAttributes.remove(key);
                    String sym = ((PdfName) value).getString();
                    if (sym.equals("P"))
                    {
                        setSymbol(PdfCaretAnnot.SYMBOL_PARAGRAPH);
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
        
        PdfRect prevDiffRect = this.diffRect;
        if (this.diffRect != null)
        {
            this.diffRect = p.updatePageSettings(this.diffRect);
            if(this.diffRect == null)
            {
                throw new PdfException("Invalid Annot DiffRect");
            }
        }

        HashMap annotMap = (HashMap)dict.getMap();
        annotMap.put(new PdfName(Usable.PDF_SUBTYPE), new PdfName(
            Usable.PDF_CARETANNOT));

        double pageHeight = p.pageHeight;

        if (this.diffRect != null)
        {
            double rectHeight = PdfMeasurement.convertToPdfUnit(
                p.measurementUnit, rect.height);
            double[] da = new double[] { diffRect.left() - left,
                top + rectHeight - (pageHeight - diffRect.top()) ,
                right - diffRect.right(), 
                bottom + rectHeight - (pageHeight - diffRect.bottom()) };
            annotMap.put(new PdfName("RD"), new PdfArray(da));
        }
        
        PdfName sy = new PdfName("Sy");
        switch (this.getSymbol())
        {
            case SYMBOL_NONE:
                annotMap.put(sy, new PdfName("None"));
                break;

            case SYMBOL_PARAGRAPH:
                annotMap.put(sy, new PdfName("P"));
                break;

            default:
                annotMap.put(sy, new PdfName("None"));
                break;
        }
        
        this.diffRect = prevDiffRect;
        
        return this;
    }

    protected void set(PdfStdDocument originDoc, PdfStdDocument d)
        throws IOException, PdfException
    {
        super.set(originDoc, d);
        if (d.getVersion().equals(PdfDocument.VERSION_1_4))
        {
            int objNo = this.dict.objNumber;
            this.dict = new PdfDict(new HashMap());
            /* remove everything */
            this.dict.objNumber = objNo;
        }
    }
}