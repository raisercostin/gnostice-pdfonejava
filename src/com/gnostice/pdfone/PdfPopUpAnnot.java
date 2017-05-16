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

public class PdfPopUpAnnot extends PdfProAnnot
{
    boolean open;
    
    PdfAnnot parent;
    
    public Object clone()
    {
        PdfPopUpAnnot clone = (PdfPopUpAnnot) super.clone();
        
        /* Cloning parent may lead to stack overflow */
        /*if (parent != null)
        {
            clone.parent = (PdfAnnot) parent.clone();
        }*/
        
        return clone;
    }

    public PdfPopUpAnnot()
    {
        this.annotType = ANNOT_TYPE_POPUP;
    }
    
    public PdfPopUpAnnot(PdfRect r, String subject, String contents,
        String title)
    {
        super(r, subject, contents, title);
        this.annotType = ANNOT_TYPE_POPUP;
    }

    public PdfPopUpAnnot(PdfRect r, String subject, String contents,
        String title, Color c)
    {
        super(r, subject, contents, title, c);
        this.annotType = ANNOT_TYPE_POPUP;
    }
    
    public PdfPopUpAnnot(PdfRect r, String subject, String contents,
        String title, int flags)
    {
        super(r, subject, contents, title, flags);
        this.annotType = ANNOT_TYPE_POPUP;
    }
    
    public PdfPopUpAnnot(PdfRect r, String subject, String contents,
        String title, int flags, Color c)
    {
        super(r, subject, contents, title, flags, c);
        this.annotType = ANNOT_TYPE_POPUP;
    }
    
    public PdfPopUpAnnot(PdfRect r, int flags)
    {
        super(r, flags);
        this.annotType = ANNOT_TYPE_POPUP;
    }
    
    public PdfPopUpAnnot(PdfRect r, int flags, Color c)
    {
        super(r, flags, c);
        this.annotType = ANNOT_TYPE_POPUP;
    }
    
    public PdfPopUpAnnot(PdfRect r, Color c)
    {
        super(r, c);
        this.annotType = ANNOT_TYPE_POPUP;
    }
    
    public synchronized PdfAnnot getParent()
    {
        return parent;
    }

    public synchronized void setParent(PdfAnnot parent)
    {
        this.parent = parent;
    }

    public synchronized boolean isOpen()
    {
        return open;
    }

    public synchronized void setOpen(boolean open)
    {
        this.open = open;
    }

    public synchronized PdfPopUpAnnot getPopup()
    {
        return null;
    }

    public synchronized void setPopup(PdfPopUpAnnot popup,
        boolean overridePopUpProperties)
    {
        return;
    }

    protected void set(PdfStdDocument originDoc, PdfStdDocument d)
        throws IOException, PdfException
    {
        super.set(originDoc, d);
        this.dict.objNumber = d.objectRun++;
        if (this.parent != null)
        {
            dict.getMap().put(new PdfName(Usable.PDF_PARENT),
                new PdfIndirectReference(parent.dict.objNumber, 0));
        }
    }

    protected PdfAnnot encode(PdfStdPage p) throws PdfException
    {
        super.encode(p);
        
        HashMap annotMap = (HashMap)dict.getMap();
        annotMap.put(new PdfName(Usable.PDF_SUBTYPE), new PdfName(
            Usable.PDF_POPUP));
        
        return this;
    }
}
