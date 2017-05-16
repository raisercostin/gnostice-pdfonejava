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

public class PdfFormListBox extends PdfFormChoiceField
{
    PdfFormListBox()
    {
        super(PdfFormChoiceField.FLAG_LISTBOX);
    }

    public PdfFormListBox(String name, int fieldFlags)
    {
        super(name, fieldFlags | PdfFormChoiceField.FLAG_LISTBOX);
    }

    public PdfFormListBox(String name, String alternateName,
        String mappingName, int fieldFlags)
    {
        super(name, alternateName, mappingName, fieldFlags
            | PdfFormChoiceField.FLAG_LISTBOX);
    }

    public PdfFormListBox(String name, String alternateName,
        String mappingName)
    {
        super(name, alternateName, mappingName,
            PdfFormChoiceField.FLAG_LISTBOX);
    }

    public PdfFormListBox(String name)
    {
        super(name, PdfFormChoiceField.FLAG_LISTBOX);
    }

    /*
     * DOC COMMENT: This C'tor should not be used for widgets without
     * parent. Since in PDF, name is mandatory for all widgets. Name
     * should be present in some dict in the hierarchy.
     */
    public PdfFormListBox(PdfRect rect)
    {
        super(PdfFormChoiceField.FLAG_LISTBOX);
        setRect(rect);
    }

    public PdfFormListBox(PdfRect r, String name, int fieldFlags)
    {
        super(r, name, fieldFlags | PdfFormChoiceField.FLAG_LISTBOX);
    }

    public PdfFormListBox(PdfRect r, String name)
    {
        super(r, name, PdfFormChoiceField.FLAG_LISTBOX);
    }

    public PdfFormListBox(PdfRect r, String name,
        String alternateName, String mappingName, int fieldFlags)
    {
        super(r, name, alternateName, mappingName, fieldFlags
            | PdfFormChoiceField.FLAG_LISTBOX);
    }

    public PdfFormListBox(PdfRect r, String name,
        String alternateName, String mappingName)
    {
        super(r, name, alternateName, mappingName,
            PdfFormChoiceField.FLAG_LISTBOX);
    }

    public PdfFormListBox(String name, int fieldFlags,
        Color borderColor, Color backgroundColor)
    {
        super(name, fieldFlags | PdfFormChoiceField.FLAG_LISTBOX,
            borderColor, backgroundColor);
    }

    public PdfFormListBox(String name, String alternateName,
        String mappingName, int fieldFlags, Color borderColor,
        Color backgroundColor)
    {
        super(name, alternateName, mappingName, fieldFlags
            | PdfFormChoiceField.FLAG_LISTBOX, borderColor,
            backgroundColor);
    }

    public PdfFormListBox(String name, String alternateName,
        String mappingName, Color borderColor, Color backgroundColor)
    {
        super(name, alternateName, mappingName,
            PdfFormChoiceField.FLAG_LISTBOX, borderColor,
            backgroundColor);
    }

    public PdfFormListBox(String name, Color borderColor,
        Color backgroundColor)
    {
        super(name, PdfFormChoiceField.FLAG_LISTBOX, borderColor,
            backgroundColor);
    }

    public PdfFormListBox(PdfRect r, String name, int fieldFlags,
        Color borderColor, Color backgroundColor)
    {
        super(r, name, fieldFlags | PdfFormChoiceField.FLAG_LISTBOX,
            borderColor, backgroundColor);
    }

    public PdfFormListBox(PdfRect r, String name, Color borderColor,
        Color backgroundColor)
    {
        super(r, name, PdfFormChoiceField.FLAG_LISTBOX, borderColor,
            backgroundColor);
    }

    public PdfFormListBox(PdfRect r, String name,
        String alternateName, String mappingName, int fieldFlags,
        Color borderColor, Color backgroundColor)
    {
        super(r, name, alternateName, mappingName, fieldFlags
            | PdfFormChoiceField.FLAG_LISTBOX, borderColor,
            backgroundColor);
    }

    public PdfFormListBox(PdfRect r, String name,
        String alternateName, String mappingName, Color borderColor,
        Color backgroundColor)
    {
        super(r, name, alternateName, mappingName,
            PdfFormChoiceField.FLAG_LISTBOX, borderColor,
            backgroundColor);
    }

    protected void set(PdfStdDocument originDoc, PdfStdDocument d)
        throws IOException, PdfException
    {
        super.set(originDoc, d);
        this.dict.dictMap.put(new PdfName("TI"), new PdfInteger(
            topIndex));
    }
        
    public synchronized int getTopIndex()
    {
        return topIndex;
    }

    public synchronized void setTopIndex(int topIndex)
    {
        this.topIndex = topIndex;
    }

}
