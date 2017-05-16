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

import com.gnostice.pdfone.encodings.PdfEncodings;
import com.gnostice.pdfone.fonts.PdfFont;
import com.gnostice.pdfone.graphics.PdfBrush;
import com.gnostice.pdfone.graphics.PdfPen;
import com.gnostice.pdfone.readers.PdfCharSequenceReader;

public class PdfFormTextField extends PdfFormField
{
    public static final int FLAG_MULTILINE = 1 << 12;
    
    public static final int FLAG_PASSWORD = 1 << 13;
    
    public static final int FLAG_FILESELECT = 1 << 20;
    
    public static final int FLAG_NO_SCROLL = 1 << 23;
    
    public static final int FLAG_COMB = 1 << 24;
    
    public static final int FLAG_RICH_TEXT = 1 << 25;
    
    boolean autoAdjustTextHeight;
    
    PdfFormTextField()
    {
        super();
        this.level = LEVEL_TERMINAL;
        this.type = TYPE_TEXTFIELD;
    }
    
    public PdfFormTextField(String name, int fieldFlags)
    {
        super(TYPE_TEXTFIELD, name, fieldFlags);
        this.level = LEVEL_TERMINAL;
    }

    public PdfFormTextField(String name, String alternateName,
        String mappingName, int fieldFlags)
    {
        super(TYPE_TEXTFIELD, name, alternateName, mappingName, fieldFlags);
        this.level = LEVEL_TERMINAL;
    }

    public PdfFormTextField(String name, String alternateName,
        String mappingName)
    {
        super(TYPE_TEXTFIELD, name, alternateName, mappingName);
        this.level = LEVEL_TERMINAL;
    }

    public PdfFormTextField(String name)
    {
        super(TYPE_TEXTFIELD, name);
        this.level = LEVEL_TERMINAL;
    }

    public PdfFormTextField(PdfRect rect)
    {
        super(TYPE_TEXTFIELD);
        setRect(rect);
        this.level = LEVEL_TERMINAL;
    }

    public PdfFormTextField(PdfRect r, String name, int fieldFlags)
    {
        setRect(r);
        this.name = name;
        this.fieldFlag = fieldFlags;
        this.level = LEVEL_TERMINAL;
        this.type = TYPE_TEXTFIELD;
    }

    public PdfFormTextField(PdfRect r, String name)
    {
        setRect(r);
        this.name = name;
        this.level = LEVEL_TERMINAL;
        this.type = TYPE_TEXTFIELD;
    }

    public PdfFormTextField(PdfRect r, String name,
        String alternateName, String mappingName, int fieldFlags)
    {
        setRect(r);
        this.name = name;
        this.altName = alternateName;
        this.mappingName = mappingName;
        this.fieldFlag = fieldFlags;
        this.level = LEVEL_TERMINAL;
        this.type = TYPE_TEXTFIELD;
    }

    public PdfFormTextField(PdfRect r, String name,
        String alternateName, String mappingName)
    {
        setRect(r);
        this.name = name;
        this.altName = alternateName;
        this.mappingName = mappingName;
        this.level = LEVEL_TERMINAL;
        this.type = TYPE_TEXTFIELD;
    }

    public PdfFormTextField(String name, int fieldFlags,
        Color borderColor, Color backgroundColor)
    {
        super(TYPE_TEXTFIELD, name, fieldFlags, borderColor,
            backgroundColor);
        this.level = LEVEL_TERMINAL;
    }

    public PdfFormTextField(String name, String alternateName,
        String mappingName, int fieldFlags, Color borderColor,
        Color backgroundColor)
    {
        super(TYPE_TEXTFIELD, name, alternateName, mappingName,
            fieldFlags, borderColor, backgroundColor);
        this.level = LEVEL_TERMINAL;
    }

    public PdfFormTextField(String name, String alternateName,
        String mappingName, Color borderColor, Color backgroundColor)
    {
        super(TYPE_TEXTFIELD, name, alternateName, mappingName,
            borderColor, backgroundColor);
        this.level = LEVEL_TERMINAL;
    }

    public PdfFormTextField(String name, Color borderColor,
        Color backgroundColor)
    {
        super(TYPE_TEXTFIELD, name, borderColor, backgroundColor);
        this.level = LEVEL_TERMINAL;
    }

    public PdfFormTextField(PdfRect r, String name, int fieldFlags,
        Color borderColor, Color backgroundColor)
    {
        super(TYPE_TEXTFIELD, name, fieldFlags, borderColor,
            backgroundColor);
        setRect(r);
        this.level = LEVEL_TERMINAL;
    }

    public PdfFormTextField(PdfRect r, String name,
        Color borderColor, Color backgroundColor)
    {
        super(TYPE_TEXTFIELD, name, borderColor, backgroundColor);
        setRect(r);
        this.level = LEVEL_TERMINAL;
    }

    public PdfFormTextField(PdfRect r, String name,
        String alternateName, String mappingName, int fieldFlags,
        Color borderColor, Color backgroundColor)
    {
        super(TYPE_TEXTFIELD, name, alternateName, mappingName,
            fieldFlags, borderColor, backgroundColor);
        setRect(r);
        this.level = LEVEL_TERMINAL;
    }

    public PdfFormTextField(PdfRect r, String name,
        String alternateName, String mappingName, Color borderColor,
        Color backgroundColor)
    {
        super(TYPE_TEXTFIELD, name, alternateName, mappingName,
            borderColor, backgroundColor);
        setRect(r);
        this.level = LEVEL_TERMINAL;
    }

    public synchronized boolean isMultiline()
    {
        if (this.fieldFlag == -1)
        {
            return false;
        }

        return (fieldFlag & FLAG_MULTILINE) == FLAG_MULTILINE;
    }

    public synchronized void setMultiline(boolean multiline)
    {
        if (multiline)
        {
            fieldFlag = (fieldFlag == -1 ? FLAG_MULTILINE
                : (fieldFlag | FLAG_MULTILINE));
        }
        else
        {
            fieldFlag = Math.max(0, fieldFlag);
            fieldFlag &= 0xffffefff;
        }
    }

    public synchronized boolean isPasswordField()
    {
        if (this.fieldFlag == -1)
        {
            return false;
        }
        return (fieldFlag & FLAG_PASSWORD) == FLAG_PASSWORD;
    }

    public synchronized void setAsPasswordField(boolean passwordField)
    {
        if (passwordField)
        {
            fieldFlag = (fieldFlag == -1 ? FLAG_PASSWORD
                : (fieldFlag | FLAG_PASSWORD));
        }
        else
        {
            fieldFlag = Math.max(0, fieldFlag);
            fieldFlag &= 0xffffdfff;
        }
    }

    public synchronized boolean isFileSelect()
    {
        if (this.fieldFlag == -1)
        {
            return false;
        }
        return (fieldFlag & FLAG_FILESELECT) == FLAG_FILESELECT;
    }

    public synchronized void setAsFileSelectField(boolean fileSelect)
    {
        if (fileSelect)
        {
            fieldFlag = (fieldFlag == -1 ? FLAG_FILESELECT
                : (fieldFlag | FLAG_FILESELECT));
        }
        else
        {
            fieldFlag = Math.max(0, fieldFlag);
            fieldFlag &= 0xffefffff;
        }
    }

    public synchronized boolean isNoScroll()
    {
        if (this.fieldFlag == -1)
        {
            return false;
        }
        return (fieldFlag & FLAG_NO_SCROLL) == FLAG_NO_SCROLL;
    }

    public synchronized void setNoScroll(boolean noScroll)
    {
        if (noScroll)
        {
            fieldFlag = (fieldFlag == -1 ? FLAG_NO_SCROLL
                : (fieldFlag | FLAG_NO_SCROLL));
        }
        else
        {
            fieldFlag = Math.max(0, fieldFlag);
            fieldFlag &= 0xff7fffff;
        }
    }

    public synchronized boolean isComb()
    {
        if (this.fieldFlag == -1)
        {
            return false;
        }
        return (fieldFlag & FLAG_COMB) == FLAG_COMB;
    }

    public synchronized void setComb(boolean comb)
    {
        if (comb)
        {
            fieldFlag = (fieldFlag == -1 ? FLAG_COMB
                : (fieldFlag | FLAG_COMB));
        }
        else
        {
            fieldFlag = Math.max(0, fieldFlag);
            fieldFlag &= 0xfeffffff;
        }
    }

    public synchronized boolean isRichText()
    {
        if (this.fieldFlag == -1)
        {
            return false;
        }
        return (fieldFlag & FLAG_RICH_TEXT) == FLAG_RICH_TEXT;
    }

    public synchronized void setRichText(boolean richText)
    {
        if (richText)
        {
            fieldFlag = (fieldFlag == -1 ? FLAG_RICH_TEXT
                : (fieldFlag | FLAG_RICH_TEXT));
        }
        else
        {
            fieldFlag = Math.max(0, fieldFlag);
            fieldFlag &= 0xfdffffff;
        }
    }
    
    public synchronized boolean isAutoAdjustTextHeight()
    {
        return autoAdjustTextHeight;
    }

    public synchronized void setAutoAdjustTextHeight(
        boolean autoAdjustFieldTextHeight)
    {
        this.autoAdjustTextHeight = autoAdjustFieldTextHeight;
    }

    public synchronized int getAlignment()
    {
        return alignment;
    }

    public synchronized void setAlignment(int alignment)
    {
        this.alignment = alignment;
    }

    public synchronized int getMaxlen()
    {
        return maxlen;
    }

    public synchronized void setMaxlen(int maxlen)
    {
        this.maxlen = maxlen;
    }

    public synchronized String getValue()
    {
        if (parent == null)
        {
            return value;
        }
        else
        {
            int index = 0;
            PdfFormField sibling = (PdfFormField) parent.kids
                .get(index);
            while ( !sibling.getFullyQualifiedName().equals(
                this.getFullyQualifiedName()))
            {
                sibling = (PdfFormField) parent.kids.get(index++);
            }

            return sibling.value;
        }
    }

    public synchronized void setValue(String value)
    {
        this.value = value;
        
        if (parent != null)
        {
            int index = 0;
            PdfFormField sibling = (PdfFormField) parent.kids
                .get(index);
            while ( !sibling.getFullyQualifiedName().equals(
                this.getFullyQualifiedName()))
            {
                sibling = (PdfFormField) parent.kids.get(index++);
            }

            sibling.value = value;
        }
    }

    private void inheritProperties(PdfStdPage page)
        throws IOException, PdfException
    {
        PdfDict dict = parent.dict;
        PdfObject inherited;
        
        if (this.value == null)
        {
            inherited = page.originDoc.reader.getObject(dict
                .getValue(NAME_VALUE));
            if (inherited != null && !(inherited instanceof PdfNull))
            {
                this.value = ((PdfString) inherited).getString();
            }
        }
        if (this.defaultValue == null)
        {
            inherited = page.originDoc.reader.getObject(dict
                .getValue(NAME_DEFAULTVALUE));
            if (inherited != null && !(inherited instanceof PdfNull))
            {
                this.defaultValue = ((PdfString) inherited).getString();
            }
        }
        if (this.alignment == ALIGNMENT_NONE)
        {
            this.alignment = this.parent.alignment;
        }
        if (this.maxlen == -1)
        {
            this.maxlen = parent.maxlen;
        }
        if (!this.hasDA)
        {
            if (this.parent.hasDA)
            {
                this.setAutoAdjustTextHeight(PdfCharSequenceReader
                    .isAutoAdjustTextHeightForField(this.parent.da));
            }
            else
            {
                this.setAutoAdjustTextHeight(PdfCharSequenceReader
                    .isAutoAdjustTextHeightForField(((PdfProDocument)
                        page.originDoc).getFormDA()));
            }
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
            value = page.originDoc.reader
                .getObject(currObj);
            if (value instanceof PdfNull)
            {
                continue;
            }
            name = ((PdfName) key).getString();
            if (name.equals(PDF_V))
            {
                unknownAttributes.remove(key);
                
                /*
                 * Here /V entry could even be a stream. It could be a
                 * sequence of bytes to be interpreted according to the
                 * /Differences entry in its fonts /Encoding entry. We
                 * need to have a separate method to handle all these
                 * cases.
                 */ 
                
                this.value = ((PdfString) value).getString();
            } 
            else if (name.equals(PDF_DV))
            {
                unknownAttributes.remove(key);
                this.defaultValue = ((PdfString) value).getString();
            } 
            else if (name.equals(PDF_MK))
            {
                unknownAttributes.remove(key);
                HashMap mkMap = (HashMap) ((PdfDict) value).getMap();
                String mk_name;
                PdfObject mk_key, mk_value, mk_currObj;
                for (Iterator iter1 = mkMap.keySet().iterator(); iter1.hasNext();)
                {
                    mk_key = (PdfObject) iter1.next();
                     
                    mk_currObj = (PdfObject) mkMap.get(mk_key);
                    mk_value = page.originDoc.reader
                        .getObject(mk_currObj);
                    if (mk_value instanceof PdfNull)
                    {
                        continue;
                    }
                    mk_name = ((PdfName) mk_key).getString();
                    if (mk_name.equals(PDF_R))
                    {
                        this.rotation = ((PdfNumber) mk_value)
                            .getInt();
                    }
                    else if (mk_name.equals(PDF_BC))
                    {
                        this.borderColor = PdfArray
                            .getColor((PdfArray) mk_value);
                    }
                    else if (mk_name.equals(PDF_BG))
                    {
                        this.backgroundColor = PdfArray
                            .getColor((PdfArray) mk_value);
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
        
        if (this.parent != null)
        {
            inheritProperties(page);
        }
    }

    ArrayList getAllDescendantWidgets()
    {
        ArrayList retVal = new ArrayList();
        retVal.add(this);
        
        return retVal ;
    }

    protected void set(PdfStdPage page, PdfStdDocument d)
        throws IOException, PdfException
    {
        HashMap annotMap = (HashMap)dict.getMap();
        annotMap.put(new PdfName(Usable.PDF_SUBTYPE), new PdfName(
            Usable.PDF_WIDGET));
        annotMap.put(new PdfName(PDF_MK),
            new PdfDict(prepareMKDict()));

        if (this.fieldFlag != -1)
        {
            annotMap.put(new PdfName(PDF_FIELD_FLAG),
                new PdfInteger(this.fieldFlag));
        }
        if (this.value != null)
        {
            if ( !(this.mode == PdfDocument.READING_MODE))
            {
                double w1 = PdfMeasurement.convertToPdfUnit(
                    page.measurementUnit, this.rect.width);
                double h1 = PdfMeasurement.convertToPdfUnit(
                    page.measurementUnit, this.rect.height);

                createAppearance(page, new PdfRect(0, 0, w1, h1));
            }
            
            if (this.font == null)
            {
                annotMap.put(new PdfName(PDF_V), new PdfString(value,
                    true));
            }
            else
            {
                try {
                    annotMap.put(new PdfName(PDF_V), new PdfString(
                        PdfString.escape(new String(font
                            .updateGlyphList(value), "Cp1252"))));
                }
                catch (IOException ioe) { }
            }
        }
        if (this.defaultValue != null)
        {
            /* Code to reflect defuaultValue in app stream here */
            
            if (this.font == null)
            {
                annotMap.put(new PdfName(PDF_DV), new PdfString(defaultValue,
                    true));
            }
            else
            {
                try {
                    annotMap.put(new PdfName(PDF_DV), new PdfString(
                    PdfString.escape(new String(
                        font.updateGlyphList(defaultValue), "Cp1252"))));
                }
                catch (IOException ioe) { }
            }
        }

        if (this.font != null)
        {
            // Prepare DA
            String defaultFontName = PDF_NAMESTART
                + PDF_FONTNAMEPREFIX + this.font.getName();
            Color fontColor = this.font.getColor();
            String color = PdfWriter
                .formatFloat(fontColor.getRed() / 255f)
                + PDF_SP;
            color += PdfWriter
                .formatFloat(fontColor.getGreen() / 255f)
                + PDF_SP;
            color += PdfWriter
                .formatFloat(fontColor.getBlue() / 255f)
                + " rg ";
            String fontName = defaultFontName + PDF_SP
                + (autoAdjustTextHeight ? "0" : font.getSize() + "")
                + PDF_SP + PDF_TEXTFONT;

            String da = color + fontName;

            this.dict.dictMap.put(new PdfName(PDF_DA), new PdfString(
                da));
        }
        
        invokeAnnotEncode(page);
        super.set(page, d);
    }

    void write(PdfStdDocument d) throws IOException, PdfException
    {
        writeAppearances(d);
        super.write(d);
    }
    
    private void createAppearance(PdfStdPage page, PdfRect r)
        throws IOException, PdfException
    {
        if (normalAppearance == null)
        {
            PdfPen pen = new PdfPen();
            PdfBrush brush = new PdfBrush();
            pen.strokeColor = this.borderColor;
            pen.width = this.borderWidth;
            brush.fillColor = this.backgroundColor;
            double avlWidth = Math.max(0, r.width - 2 * pen.width);
            double avlHeight = Math.max(0, r.height - 2 * pen.width);
            
            double fontSize = this.isMultiline() ? avlHeight / 4 : 
                this.isAutoAdjustTextHeight() ? 0 : 12; 
            PdfFont font = this.font == null ? PdfFont.create("Arial",
                (int) fontSize, PdfEncodings.CP1252) : this.font;

            normalAppearance = new PdfAppearanceStream(r);

            if (this.borderColor != null
                || this.backgroundColor != null)
            {
                normalAppearance.drawRect(r,
                    this.borderColor == null ? null : pen,
                    this.backgroundColor == null ? null : brush);
            }

            PdfTextFormatter tf = new PdfTextFormatter();
            switch (this.alignment)
            {
                case ALIGNMENT_CENTER:
                    tf.setAlignment(PdfTextFormatter.CENTER);
                    break;
                case ALIGNMENT_RIGHT:
                    tf.setAlignment(PdfTextFormatter.RIGHT);
                    break;
                default:
                    tf.setAlignment(PdfTextFormatter.LEFT);
                    break;
            }
            normalAppearance.writeText(this.value, font, new PdfRect(
                pen.width, this.isMultiline() ? pen.width
                    : (r.height - font.getHeight()) / 2, avlWidth,
                avlHeight), tf);
        }
    }
}
