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

public class PdfFormComboBox extends PdfFormChoiceField
{
    public static final int FLAG_EDITABLE = 1 << 18;
    
    PdfFormComboBox()
    {
        super(PdfFormChoiceField.FLAG_COMBOBOX);
        this.type = TYPE_COMBOBOX;
    }

    public PdfFormComboBox(String name, int fieldFlags)
    {
        super(name, fieldFlags | PdfFormChoiceField.FLAG_COMBOBOX);
        this.type = TYPE_COMBOBOX;
    }

    public PdfFormComboBox(String name, String alternateName,
        String mappingName, int fieldFlags)
    {
        super(name, alternateName, mappingName, fieldFlags
            | PdfFormChoiceField.FLAG_COMBOBOX);
        this.type = TYPE_COMBOBOX;
    }

    public PdfFormComboBox(String name, String alternateName,
        String mappingName)
    {
        super(name, alternateName, mappingName,
            PdfFormChoiceField.FLAG_COMBOBOX);
        this.type = TYPE_COMBOBOX;
    }

    public PdfFormComboBox(String name)
    {
        super(name, PdfFormChoiceField.FLAG_COMBOBOX);
        this.type = TYPE_COMBOBOX;
    }

    /*
     * DOC COMMENT: This C'tor should not be used for widgets without
     * parent. Since in PDF, name is mandatory for all widgets. Name should
     * be present in some dict in the hierarchy.
     */
    public PdfFormComboBox(PdfRect rect)
    {
        super(PdfFormChoiceField.FLAG_COMBOBOX);
        this.type = TYPE_COMBOBOX;
        setRect(rect);
    }

    public PdfFormComboBox(PdfRect r, String name, int fieldFlags)
    {
        super(r, name, fieldFlags | PdfFormChoiceField.FLAG_COMBOBOX);
        this.type = TYPE_COMBOBOX;
    }

    public PdfFormComboBox(PdfRect r, String name)
    {
        super(r, name, PdfFormChoiceField.FLAG_COMBOBOX);
        this.type = TYPE_COMBOBOX;
    }

    public PdfFormComboBox(PdfRect r, String name,
        String alternateName, String mappingName, int fieldFlags)
    {
        super(r, name, alternateName, mappingName, fieldFlags
            | PdfFormChoiceField.FLAG_COMBOBOX);
        this.type = TYPE_COMBOBOX;
    }

    public PdfFormComboBox(PdfRect r, String name,
        String alternateName, String mappingName)
    {
        super(r, name, alternateName, mappingName,
            PdfFormChoiceField.FLAG_COMBOBOX);
        this.type = TYPE_COMBOBOX;
    }

    public PdfFormComboBox(String name, int fieldFlags,
        Color borderColor, Color backgroundColor)
    {
        super(name, fieldFlags | PdfFormChoiceField.FLAG_COMBOBOX,
            borderColor, backgroundColor);
        this.type = TYPE_COMBOBOX;
    }

    public PdfFormComboBox(String name, String alternateName,
        String mappingName, int fieldFlags, Color borderColor,
        Color backgroundColor)
    {
        super(name, alternateName, mappingName, fieldFlags
            | PdfFormChoiceField.FLAG_COMBOBOX, borderColor,
            backgroundColor);
        this.type = TYPE_COMBOBOX;
    }

    public PdfFormComboBox(String name, String alternateName,
        String mappingName, Color borderColor, Color backgroundColor)
    {
        super(name, alternateName, mappingName,
            PdfFormChoiceField.FLAG_COMBOBOX, borderColor,
            backgroundColor);
        this.type = TYPE_COMBOBOX;
    }

    public PdfFormComboBox(String name, Color borderColor,
        Color backgroundColor)
    {
        super(name, PdfFormChoiceField.FLAG_COMBOBOX, borderColor,
            backgroundColor);
        this.type = TYPE_COMBOBOX;
    }

    public PdfFormComboBox(PdfRect r, String name, int fieldFlags,
        Color borderColor, Color backgroundColor)
    {
        super(r, name, fieldFlags | PdfFormChoiceField.FLAG_COMBOBOX,
            borderColor, backgroundColor);
        this.type = TYPE_COMBOBOX;
    }

    public PdfFormComboBox(PdfRect r, String name, Color borderColor,
        Color backgroundColor)
    {
        super(r, name, PdfFormChoiceField.FLAG_COMBOBOX, borderColor,
            backgroundColor);
        this.type = TYPE_COMBOBOX;
    }

    public PdfFormComboBox(PdfRect r, String name,
        String alternateName, String mappingName, int fieldFlags,
        Color borderColor, Color backgroundColor)
    {
        super(r, name, alternateName, mappingName, fieldFlags
            | PdfFormChoiceField.FLAG_COMBOBOX, borderColor,
            backgroundColor);
        this.type = TYPE_COMBOBOX;
    }

    public PdfFormComboBox(PdfRect r, String name,
        String alternateName, String mappingName, Color borderColor,
        Color backgroundColor)
    {
        super(r, name, alternateName, mappingName,
            PdfFormChoiceField.FLAG_COMBOBOX, borderColor,
            backgroundColor);
        this.type = TYPE_COMBOBOX;
    }

    public synchronized boolean isEditable()
    {
        if (this.fieldFlag == -1)
        {
            return false;
        }
        return (fieldFlag & FLAG_EDITABLE) == FLAG_EDITABLE;
    }

    public synchronized void setEditable(boolean editable)
    {
        if (editable)
        {
            fieldFlag = (fieldFlag == -1 ? FLAG_EDITABLE
                : (fieldFlag | FLAG_EDITABLE));
        }
        else
        {
            fieldFlag = Math.max(0, fieldFlag);
            fieldFlag &= 0xfffbffff;
        }
    }
}
