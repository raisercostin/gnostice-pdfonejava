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
import java.util.List;

import com.gnostice.pdfone.readers.PdfCharSequenceReader;

/* 
 * DOC COMMENT: setValue and getValue will be ignored
 * adding appearnaces will be ignored
 */

abstract class PdfFormChoiceField extends PdfFormField
{
    static final int FLAG_LISTBOX = 0;

    static final int FLAG_COMBOBOX = 1 << 17;

    private boolean inheritedOptions;
    
    private boolean inheritedTopIndex;
    
    private boolean inheritedSelectedIndex;
    
    private boolean inheritedValues;
    
    int topIndex;
    /* Used for ListBoxes only */

    ArrayList items;
    
    ArrayList itemExportValues;
    
    ArrayList selectionValues;
    
    ArrayList selectionIndices;
    
    boolean autoAdjustTextHeight;
    
    PdfFormChoiceField()
    {
        super();
        this.level = LEVEL_TERMINAL;
        this.type = TYPE_LISTBOX;
        items = new ArrayList();
        itemExportValues = new ArrayList();
        selectionValues = new ArrayList(); 
        selectionIndices = new ArrayList();
    }

    PdfFormChoiceField(int fieldFlag)
    {
        super();
        this.level = LEVEL_TERMINAL;
        this.type = TYPE_LISTBOX;
        this.fieldFlag = fieldFlag;
        items = new ArrayList();
        itemExportValues = new ArrayList();
        selectionValues = new ArrayList();
        selectionIndices = new ArrayList();
    }

    PdfFormChoiceField(String name, int fieldFlags)
    {
        super(TYPE_LISTBOX, name, fieldFlags);
        this.level = LEVEL_TERMINAL;
        items = new ArrayList();
        itemExportValues = new ArrayList();
        selectionValues = new ArrayList();
        selectionIndices = new ArrayList();
    }

    PdfFormChoiceField(String name, String alternateName,
        String mappingName, int fieldFlags)
    {
        super(TYPE_LISTBOX, name, alternateName, mappingName,
            fieldFlags);
        this.level = LEVEL_TERMINAL;
        items = new ArrayList();
        itemExportValues = new ArrayList();
        selectionValues = new ArrayList();
        selectionIndices = new ArrayList();
    }

    PdfFormChoiceField(PdfRect r, String name, int fieldFlags)
    {
        setRect(r);
        this.name = name;
        this.fieldFlag = fieldFlags;
        this.level = LEVEL_TERMINAL;
        this.type = TYPE_LISTBOX;
        items = new ArrayList();
        itemExportValues = new ArrayList();
        selectionValues = new ArrayList();
        selectionIndices = new ArrayList();
    }

    PdfFormChoiceField(PdfRect r, String name, String alternateName,
        String mappingName, int fieldFlags)
    {
        setRect(r);
        this.name = name;
        this.altName = alternateName;
        this.mappingName = mappingName;
        this.fieldFlag = fieldFlags;
        this.level = LEVEL_TERMINAL;
        this.type = TYPE_LISTBOX;
        items = new ArrayList();
        itemExportValues = new ArrayList();
        selectionValues = new ArrayList();
        selectionIndices = new ArrayList();
    }

    PdfFormChoiceField(String name, int fieldFlags,
        Color borderColor, Color backgroundColor)
    {
        super(TYPE_LISTBOX, name, fieldFlags, borderColor,
            backgroundColor);
        this.level = LEVEL_TERMINAL;
        items = new ArrayList();
        itemExportValues = new ArrayList();
        selectionValues = new ArrayList();
        selectionIndices = new ArrayList();
    }

    PdfFormChoiceField(String name, String alternateName,
        String mappingName, int fieldFlags, Color borderColor,
        Color backgroundColor)
    {
        super(TYPE_LISTBOX, name, alternateName, mappingName,
            fieldFlags, borderColor, backgroundColor);
        this.level = LEVEL_TERMINAL;
        items = new ArrayList();
        itemExportValues = new ArrayList();
        selectionValues = new ArrayList();
        selectionIndices = new ArrayList();
    }

    PdfFormChoiceField(PdfRect r, String name, int fieldFlags,
        Color borderColor, Color backgroundColor)
    {
        super(TYPE_LISTBOX, name, fieldFlags, borderColor,
            backgroundColor);
        setRect(r);
        this.level = LEVEL_TERMINAL;
        items = new ArrayList();
        itemExportValues = new ArrayList();
        selectionValues = new ArrayList();
        selectionIndices = new ArrayList();
    }

    PdfFormChoiceField(PdfRect r, String name, String alternateName,
        String mappingName, int fieldFlags, Color borderColor,
        Color backgroundColor)
    {
        super(TYPE_LISTBOX, name, alternateName, mappingName,
            fieldFlags, borderColor, backgroundColor);
        setRect(r);
        this.level = LEVEL_TERMINAL;
        items = new ArrayList();
        itemExportValues = new ArrayList();
        selectionValues = new ArrayList();
        selectionIndices = new ArrayList();
    }
    
    static PdfFormChoiceField getInstance(int flag)
    {
        if ((flag & FLAG_COMBOBOX) != 0)
        {
            return new PdfFormComboBox();
        }
        else
        {
            return new PdfFormListBox();
        }
    }
    
    private void inheritProperties(PdfStdPage page)
        throws IOException, PdfException
    {
        PdfDict dict = parent.dict;
        PdfObject inherited;

        if (!inheritedValues)
        {
            inherited = page.originDoc.reader.getObject(dict
                .getValue(NAME_VALUE));
            if (inherited != null && !(inherited instanceof PdfNull))
            {
                if (inherited instanceof PdfString)
                {
                    this.selectionValues.add((PdfString) inherited);
                }
                else if (inherited instanceof PdfArray)
                {
                    List l = ((PdfArray) inherited).getList();
                    PdfObject obj;
                    for (int i = 0; i < l.size(); i++)
                    {
                        obj = (PdfObject) l.get(i);
                        if (obj instanceof PdfString)
                        {
                            this.selectionValues.add((PdfString) obj);
                        }
                    }
                }
            }
        }
        if ( !inheritedOptions)
        {
            inherited = page.originDoc.reader.getObject(dict
                .getValue(new PdfName(PDF_OPT)));
            if (inherited != null && !(inherited instanceof PdfNull))
            {
                List l = ((PdfArray) inherited).getList();
                for (int i = 0; i < l.size(); i++)
                {
                    PdfObject obj = (PdfObject) l.get(i);
                    if (obj instanceof PdfString)
                    {
                        this.addItem(((PdfString) obj).getString());
                    }
                    else if (obj instanceof PdfArray)
                    {
                        List l1 = ((PdfArray) obj).getList();
                        PdfString expVal = (PdfString) l1.get(0);
                        if (expVal == null)
                        {
                            expVal = new PdfString("");
                        }
                        PdfString displayVal = (PdfString) l1.get(1);
                        if (displayVal == null)
                        {
                            displayVal = new PdfString("");
                        }
                        this.addItem(displayVal.getString(), expVal
                            .getString());
                    }
                }
            }
        }
        if ( !inheritedSelectedIndex)
        {
            inherited = page.originDoc.reader.getObject(dict
                .getValue(new PdfName(PDF_I)));
            if (inherited != null && !(inherited instanceof PdfNull))
            {
                List l = ((PdfArray) inherited).getList();
                for (int i = 0; i < l.size(); i++)
                {
                    PdfObject obj = (PdfObject) l.get(i);
                    if (obj instanceof PdfInteger)
                    {
                        this.selectionIndices.add((PdfInteger) obj);
                    }
                }
            }
        }
        if (!inheritedTopIndex)
        {
            inherited = page.originDoc.reader.getObject(dict
                .getValue(new PdfName("TI")));
            if (inherited != null && !(inherited instanceof PdfNull))
            {
                this.topIndex = ((PdfNumber) inherited).getInt();
            }
        }
        if (this.alignment == ALIGNMENT_NONE)
        {
            this.alignment = this.parent.alignment;
        }
        if ( !this.hasDA)
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
            value = page.originDoc.reader.getObject(currObj);
            if (value instanceof PdfNull)
            {
                continue;
            }
            name = ((PdfName) key).getString();

            if (name.equals(PDF_H))
            {
                unknownAttributes.remove(key);
                if (((PdfName) value).getString().equals("N"))
                {
                    this.setHighlightMode(HIGHLIGHT_MODE_NONE);
                }
                else if (((PdfName) value).getString().equals("I"))
                {
                    this.setHighlightMode(HIGHLIGHT_MODE_INVERT);
                }
                else if (((PdfName) value).getString().equals("O"))
                {
                    this.setHighlightMode(HIGHLIGHT_MODE_OUTLINE);
                }
                else if (((PdfName) value).getString().equals("P"))
                {
                    this.setHighlightMode(HIGHLIGHT_MODE_PUSH);
                }
                else
                {
                    this.setHighlightMode(HIGHLIGHT_MODE_NONE);
                }
            }
            if (name.equals(PDF_OPT))
            {
                unknownAttributes.remove(key);
                inheritedOptions = true;
                List l = ((PdfArray) value).getList();
                for (int i = 0; i < l.size(); i++)
                {
                    PdfObject obj = (PdfObject) l.get(i);
                    if (obj instanceof PdfString)
                    {
                        this.addItem(((PdfString) obj).getString());
                    }
                    else if (obj instanceof PdfArray)
                    {
                        List l1 = ((PdfArray) obj).getList();
                        PdfString expVal = (PdfString) l1.get(0);
                        if (expVal == null)
                        {
                            expVal = new PdfString("");
                        }
                        PdfString displayVal = (PdfString) l1.get(1);
                        if (displayVal == null)
                        {
                            displayVal = new PdfString("");
                        }
                        this.addItem(displayVal.getString(), expVal
                            .getString());
                    }
                }
            }
            if (name.equals("TI"))
            {
                unknownAttributes.remove(key);
                inheritedTopIndex = true;
                this.topIndex = ((PdfNumber) value).getInt();
            }
            if (name.equals(PDF_I))
            {
                unknownAttributes.remove(key);
                inheritedSelectedIndex = true;
                List l = ((PdfArray) value).getList();
                for (int i = 0; i < l.size(); i++)
                {
                    PdfObject obj = (PdfObject) l.get(i);
                    if (obj instanceof PdfInteger)
                    {
                        this.selectionIndices.add((PdfInteger) obj);
                    }
                }
            }
            if (name.equals(PDF_V))
            {
                unknownAttributes.remove(key);
                inheritedValues = true;
                if (value instanceof PdfString)
                {
                    this.selectionValues.add((PdfString) value);
                }
                else if (value instanceof PdfArray)
                {
                    List l = ((PdfArray) value).getList();
                    PdfObject obj;
                    for (int i = 0; i < l.size(); i++)
                    {
                        obj = (PdfObject) l.get(i);
                        if (obj instanceof PdfString)
                        {
                            this.selectionValues.add((PdfString) obj);
                        }
                    }
                }
            }
            else if (name.equals(PDF_MK))
            {
                unknownAttributes.remove(key);
                HashMap mkMap = (HashMap) ((PdfDict) value).getMap();
                String mk_name;
                PdfObject mk_key, mk_value, mk_currObj;
                for (Iterator iter1 = mkMap.keySet().iterator(); iter1
                    .hasNext();)
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
                if ( !knownAttributes.containsKey(name))
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
    
    void setExportValues()
    {
        exportValues = new ArrayList();
        ArrayList values = null;
        for (int i = 0, limit = items.size(); i < limit; ++i)
        {
            values = new ArrayList();
            values.add(itemExportValues.get(i));
            values.add(items.get(i));
            exportValues.add(new PdfArray(values));
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
        invokeAnnotEncode(page);
        super.set(page, d);
        
        HashMap annotMap = (HashMap)dict.getMap();
        annotMap.put(new PdfName(Usable.PDF_SUBTYPE), new PdfName(
            Usable.PDF_WIDGET));
        annotMap.put(new PdfName(PDF_MK),
            new PdfDict(prepareMKDict()));

        setExportValues();

        if (!selectionValues.isEmpty())
        {
            if (selectionValues.size() > 1)
            {
                setMultiselect(true);
            }
            this.dict.dictMap.put(new PdfName(PDF_V), new PdfArray(
                selectionValues));
            this.dict.dictMap.put(new PdfName(PDF_I), new PdfArray(
                selectionIndices));
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

    public synchronized void addItem(String item)
    {
        if (item == null)
        {
            item = "";
        }
        items.add(new PdfTextString(item));
        itemExportValues.add(new PdfTextString(""));
    }

    public synchronized void addItem(String item, String exportValue)
    {
        if (item == null)
        {
            item = "";
        }
        if (exportValue == null)
        {
            exportValue = "";
        }
        items.add(new PdfTextString(item));
        itemExportValues.add(new PdfTextString(exportValue));
    }

    public synchronized void addItem(String item, boolean keepSelected)
    {
        if (item == null)
        {
            item = "";
        }
        PdfTextString pdfItem = new PdfTextString(item);
        items.add(pdfItem);
        itemExportValues.add(new PdfTextString(""));

        if (keepSelected)
        {
            selectionValues.add(pdfItem);
            selectionIndices.add(new PdfInteger(items.size() - 1));
        }
    }

    public synchronized void addItem(String item, String exportValue,
        boolean keepSelected)
    {
        if (item == null)
        {
            item = "";
        }
        if (exportValue == null)
        {
            exportValue = "";
        }

        PdfTextString pdfItem = new PdfTextString(item);
        items.add(pdfItem);
        itemExportValues.add(new PdfTextString(exportValue));
        if (keepSelected)
        {
            selectionValues.add(pdfItem);
            selectionIndices.add(new PdfInteger(items.size() - 1));
        }
    }
    
    public synchronized boolean isMultiselect()
    {
        if (this.fieldFlag == -1)
        {
            return false;
        }
        return (fieldFlag & FLAG_CHOICE_MULTISELECT) == FLAG_CHOICE_MULTISELECT;
    }

    public synchronized void setMultiselect(boolean multiselect)
    {
        if (multiselect)
        {
            fieldFlag = (fieldFlag == -1 ? FLAG_CHOICE_MULTISELECT
                : (fieldFlag | FLAG_CHOICE_MULTISELECT));
        }
        else
        {
            fieldFlag = Math.max(0, fieldFlag);
            fieldFlag &= 0xffdfffff;
        }
    }

    public synchronized boolean isCommitOnSelectionChange()
    {
        if (this.fieldFlag == -1)
        {
            return false;
        }
        return (fieldFlag & FLAG_CHOICE_COMMIT_ON_SEL_CHANGE) == 
            FLAG_CHOICE_COMMIT_ON_SEL_CHANGE;
    }

    public synchronized void setCommitOnSelectionChange(
        boolean commitOnSelectionChange)
    {
        if (commitOnSelectionChange)
        {
            fieldFlag = (fieldFlag == -1 ? FLAG_CHOICE_COMMIT_ON_SEL_CHANGE
                : (fieldFlag | FLAG_CHOICE_COMMIT_ON_SEL_CHANGE));
        }
        else
        {
            fieldFlag = Math.max(0, fieldFlag);
            fieldFlag &= 0xfbffffff;
        }
    }

    /*
     * DOC COMMENT: Sets the selected values for this field. List of
     * String objects to be selected. If multiselect is not allowed
     * then only first in the list is selected.
     */
    public synchronized void setValues(List values)
    {
        if (parent == null)
        {
            selectionValues = new ArrayList();
            selectionIndices = new ArrayList();
            if (!isMultiselect())
            {
                PdfString setVal = new PdfTextString((String) values
                    .get(0));
                int setIndex = items.indexOf(setVal);
                if (setIndex >= 0)
                {
                    selectionValues.add(setVal);
                    selectionIndices.add(new PdfInteger(setIndex));
                }
            }
            else
            {
                for (int i = 0, limit = values.size(); i < limit; ++i)
                {
                    PdfString setVal = new PdfTextString((String) values
                        .get(i));
                    int setIndex = items.indexOf(setVal);
                    if (setIndex >= 0)
                    {
                        selectionValues.add(setVal);
                        selectionIndices.add(new PdfInteger(setIndex));
                    }
                }
            }
        }
        else
        {
            int index = 0;
            PdfFormField sibling = (PdfFormField) parent.kids
                .get(index);
            while ( !sibling.getFullyQualifiedName().equals(
                this.getFullyQualifiedName()))
            {
                sibling = (PdfFormField) parent.kids.get(++index);
            }

            PdfFormChoiceField chSibling = (PdfFormChoiceField) sibling;
            chSibling.selectionValues = new ArrayList();
            chSibling.selectionIndices = new ArrayList();
            if (!isMultiselect())
            {
                PdfString setVal = new PdfTextString((String) values
                    .get(0));
                int setIndex = chSibling.items.indexOf(setVal);
                if (setIndex >= 0)
                {
                    chSibling.selectionValues.add(setVal);
                    chSibling.selectionIndices.add(new PdfInteger(
                        setIndex));
                }
            }
            else
            {
                for (int i = 0, limit = values.size(); i < limit; ++i)
                {
                    PdfString setVal = new PdfTextString((String) values
                        .get(i));
                    int setIndex = chSibling.items.indexOf(setVal);
                    if (setIndex >= 0)
                    {
                        chSibling.selectionValues.add(setVal);
                        chSibling.selectionIndices
                            .add(new PdfInteger(setIndex));
                    }
                }
            }
        }
    }

    /*
     * DOC COMMENT: Sets the selected value for this field. Selects
     * only this value, erasing all other selected ones.
     */
    public synchronized void setValue(String value)
    {
        if (parent == null)
        {
            selectionValues = new ArrayList();
            selectionIndices = new ArrayList();
            PdfString setVal = new PdfTextString(value);
            int setIndex = items.indexOf(setVal);
            if (setIndex >= 0)
            {
                selectionValues.add(setVal);
                selectionIndices.add(new PdfInteger(setIndex));
            }
        }
        else
        {
            int index = 0;
            PdfFormField sibling = (PdfFormField) parent.kids
                .get(index);
            while ( !sibling.getFullyQualifiedName().equals(
                this.getFullyQualifiedName()))
            {
                sibling = (PdfFormField) parent.kids.get(++index);
            }

            PdfFormChoiceField chSibling = (PdfFormChoiceField) sibling;
            chSibling.selectionValues = new ArrayList();
            chSibling.selectionIndices = new ArrayList();
            PdfString setVal = new PdfTextString(value);
            int setIndex = chSibling.items.indexOf(setVal);
            if (setIndex >= 0)
            {
                chSibling.selectionValues.add(setVal);
                chSibling.selectionIndices.add(new PdfInteger(setIndex));
            }
        }
    }

    /*
     * DOC COMMENT : This methods gets the first selected item in the
     * list
     */
    public synchronized String getValue()
    {
        if (selectionValues.size() == 0)
        {
            return "";
        }
        if (parent == null)
        {
            return ((PdfString) selectionValues.get(0)).getString();
        }
        else
        {
            int index = 0;
            PdfFormField sibling = (PdfFormField) parent.kids
                .get(index);
            while ( !sibling.getFullyQualifiedName().equals(
                this.getFullyQualifiedName()))
            {
                sibling = (PdfFormField) parent.kids.get(++index);
            }

            return ((PdfString) ((PdfFormChoiceField) sibling).selectionValues
                .get(0)).getString();
        }
    }

    public synchronized List getDisplayValues()
    {
        List retList = new ArrayList();
        if (items != null)
        {
            for (int i = 0, limit = items.size(); i < limit; ++i)
            {
                retList.add(((PdfString) items.get(i)).getString());
            }
        }

        return retList;
    }
    
    public synchronized List getExportValues()
    {
        List retList = new ArrayList();
        if (itemExportValues != null)
        {
            for (int i = 0, limit = itemExportValues.size(); i < limit; ++i)
            {
                retList.add(((PdfString) itemExportValues.get(i))
                    .getString());
            }
        }
        
        return retList;
    }
    
    public synchronized List getSelectedValues()
    {
        List retList = new ArrayList();
        if (selectionValues != null)
        {
            for (int i = 0, limit = selectionValues.size(); i < limit; ++i)
            {
                retList.add(((PdfString) selectionValues.get(i))
                    .getString());
            }
        }
        
        return retList;
    }
}
