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

public class PdfTextAnnot extends PdfProAnnot
{
    public static final int ICON_COMMENT = 0;
    
    public static final int ICON_KEY = 1;
    
    public static final int ICON_NOTE = 2;
    
    public static final int ICON_HELP = 3;
    
    public static final int ICON_NEWPARAGRAPH = 4;
    
    public static final int ICON_PARAGRAPH = 5;
    
    public static final int ICON_INSERT = 6;

    private int iconName;

    private boolean open;

    public Object clone()
    {
        return (PdfTextAnnot) super.clone();
    }

    public PdfTextAnnot()
    {
        iconName = ICON_NOTE;
        this.annotType = ANNOT_TYPE_TEXT;
    }

    public PdfTextAnnot(double x, double y, String subject,
        String contents, String title, int flags, Color c,
        int iconName, boolean open)
    {
        super(new PdfRect(x, y, 0, 0), subject, contents, title,
            flags, c);
        iconName = ICON_NOTE;
        this.annotType = ANNOT_TYPE_TEXT;
    }

    public PdfTextAnnot(double x, double y, int iconName, boolean open)
    {
        setRect(x, y, 0, 0);
        this.iconName = iconName;
        this.open = open;
        this.annotType = ANNOT_TYPE_TEXT;
    }

    public PdfTextAnnot(double x, double y, String subject,
        String contents, String title)
    {
        super(new PdfRect(x, y, 0, 0), subject, contents, title);
        iconName = ICON_NOTE;
        this.annotType = ANNOT_TYPE_TEXT;
    }

    public PdfTextAnnot(double x, double y, String subject,
        String contents, String title, Color c)
    {
        super(new PdfRect(x, y, 0, 0), subject, contents, title, c);
        iconName = ICON_NOTE;
        this.annotType = ANNOT_TYPE_TEXT;
    }

    public PdfTextAnnot(double x, double y, String subject,
        String contents, String title, int flags)
    {
        super(new PdfRect(x, y, 0, 0), subject, contents, title,
            flags);
        iconName = ICON_NOTE;
        this.annotType = ANNOT_TYPE_TEXT;
    }

    public PdfTextAnnot(double x, double y, String subject,
        String contents, String title, int flags, Color c)
    {
        super(new PdfRect(x, y, 0, 0), subject, contents, title,
            flags, c);
        iconName = ICON_NOTE;
        this.annotType = ANNOT_TYPE_TEXT;
    }

    public PdfTextAnnot(double x, double y, int flags)
    {
        super(new PdfRect(x, y, 0, 0), flags);
        iconName = ICON_NOTE;
        this.annotType = ANNOT_TYPE_TEXT;
    }

    public PdfTextAnnot(double x, double y, int flags, Color c)
    {
        super(new PdfRect(x, y, 0, 0), flags, c);
        iconName = ICON_NOTE;
        this.annotType = ANNOT_TYPE_TEXT;
    }

    public PdfTextAnnot(double x, double y, Color c)
    {
        super(new PdfRect(x, y, 0, 0), c);
        iconName = ICON_NOTE;
        this.annotType = ANNOT_TYPE_TEXT;
    }

    public synchronized boolean isOpen()
    {
        return open;
    }

    public synchronized void setOpen(boolean open)
    {
        this.open = open;
    }

    public synchronized int getIconName()
    {
        return iconName;
    }

    public synchronized void setIconName(int name)
    {
        this.iconName = name;
    }
    
    public synchronized void setLocation(double x, double y)
    {
        setRect(x, y, 0, 0);
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
            if (name.equals(PDF_OPEN))
            {
                unknownAttributes.remove(key);
                setOpen(((PdfBoolean) value).getBooleanValue());
            }
            else if (name.equals(PDF_NAME))
            {
                unknownAttributes.remove(key);
                String n = ((PdfName) value).getString();
                setIconName(2);
                if (n.equals("Comment"))
                {
                    setIconName(0);
                }
                else if (n.equals("Key"))
                {
                    setIconName(1);
                }
                else if (n.equals("Note"))
                {
                    setIconName(2);
                }
                else if (n.equals("Help"))
                {
                    setIconName(3);
                }
                else if (n.equals("NewParagraph"))
                {
                    setIconName(4);
                }
                else if (n.equals("Paragraph"))
                {
                    setIconName(5);
                }
                else if (n.equals("Insert"))
                {
                    setIconName(6);
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
            Usable.PDF_TEXTANNOT));

        String s;
        switch (iconName)
        {
            case PdfTextAnnot.ICON_COMMENT:
                s = "Comment";
                break;

            case PdfTextAnnot.ICON_KEY:
                s = "Key";
                break;

            case PdfTextAnnot.ICON_NOTE:
                s = "Note";
                break;

            case PdfTextAnnot.ICON_HELP:
                s = "Help";
                break;

            case PdfTextAnnot.ICON_NEWPARAGRAPH:
                s = "NewParagraph";
                break;

            case PdfTextAnnot.ICON_PARAGRAPH:
                s = "Paragraph";
                break;

            case PdfTextAnnot.ICON_INSERT:
                s = "Insert";
                break;

            default:
                s = "Note";
        }
        annotMap.put(new PdfName(Usable.PDF_NAME), new PdfName(s));
        annotMap.put(new PdfName(Usable.PDF_OPEN), new PdfBoolean(
            this.isOpen()));

        return this;
    }
}
