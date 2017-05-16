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
import java.util.HashMap;

public class PdfCircleAnnot extends PdfSquareAnnot
{
    public Object clone()
    {
        return (PdfCircleAnnot) super.clone();
    }

    public PdfCircleAnnot()
    {
        super();
        this.annotType = ANNOT_TYPE_CIRCLE;
    }
    
    public PdfCircleAnnot(PdfRect r, String subject, String contents,
        String title)
    {
        super(r, subject, contents, title);
        this.annotType = ANNOT_TYPE_CIRCLE;
    }

    public PdfCircleAnnot(PdfRect r, String subject, String contents,
        String title, Color c)
    {
        super(r, subject, contents, title, c);
        this.annotType = ANNOT_TYPE_CIRCLE;
    }
    
    public PdfCircleAnnot(PdfRect r, String subject, String contents,
        String title, int flags)
    {
        super(r, subject, contents, title, flags);
        this.annotType = ANNOT_TYPE_CIRCLE;
    }
    
    public PdfCircleAnnot(PdfRect r, String subject, String contents,
        String title, int flags, Color c)
    {
        super(r, subject, contents, title, flags, c);
        this.annotType = ANNOT_TYPE_CIRCLE;
    }
    
    public PdfCircleAnnot(PdfRect r, int flags)
    {
        super(r, flags);
        this.annotType = ANNOT_TYPE_CIRCLE;
    }
    
    public PdfCircleAnnot(PdfRect r, int flags, Color c)
    {
        super(r, flags, c);
        this.annotType = ANNOT_TYPE_CIRCLE;
    }
    
    public PdfCircleAnnot(PdfRect r, Color c)
    {
        super(r, c);
        this.annotType = ANNOT_TYPE_CIRCLE;
    }

    protected PdfAnnot encode(PdfStdPage p) throws PdfException
    {
        super.encode(p);

        HashMap annotMap = (HashMap)dict.getMap();
        annotMap.put(new PdfName(Usable.PDF_SUBTYPE), new PdfName(
            Usable.PDF_CIRCLEANNOT));

        return this;
    }
}