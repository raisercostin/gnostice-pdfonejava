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
import java.util.Map;

import com.gnostice.pdfone.fonts.PdfFont;

/*
 * DOC COMMENT: For fields without parent, type and name entries are
 * mandatory
 */
/*
 * DOC COMMENT: For fields with parent having name, if name entry is
 * not present or same name is presnet they reflect value changes.
 */
public class PdfFormField extends PdfProAnnot
{
    static final int LEVEL_TERMINAL = 0; //for widget only
    
    static final int LEVEL_INTERMEDIATE = 1;

    static final PdfName NAME_VALUE = new PdfName(PDF_V);
    
    static final PdfName NAME_DEFAULTVALUE = new PdfName(PDF_DV);

    public static final int BUTTON_STATE_OFF = 0;
    
    public static final int BUTTON_STATE_ON = 1;

    /* To inherit from ancestors */
    public static final int FLAG_NONE = -1;
    
    public static final int FLAG_READONLY = 1;
    
    public static final int FLAG_REQUIRED = 2;
    
    public static final int FLAG_NO_EXPORT = 4;

    public static final int FLAG_CHOICE_MULTISELECT = 1 << 21;
    
    public static final int FLAG_NO_SPELLCHECK = 1 << 22;
    
    public static final int FLAG_CHOICE_COMMIT_ON_SEL_CHANGE = 1 << 26;

    public static final int FLAG_RADIO_IN_UNISON = 1 << 25;

    public static final int FLAG_RADIO_NO_TOGGLE_TO_OFF = 1 << 14;

    public static final int HIGHLIGHT_MODE_NONE = 1;
    
    public static final int HIGHLIGHT_MODE_INVERT = 2;
    
    public static final int HIGHLIGHT_MODE_OUTLINE = 3;

    public static final int HIGHLIGHT_MODE_PUSH = 4;
    
    public static final int HIGHLIGHT_MODE_TOGGLE = 5;

    public static final int ALIGNMENT_LEFT = 0;
    
    public static final int ALIGNMENT_CENTER = 1;

    public static final int ALIGNMENT_RIGHT = 2;
    
    /* To inherit from ancestors */
    static final int TYPE_NONE = 0;
    
    static final int ALIGNMENT_NONE = -1;
    
    public static final int TYPE_PUSHBUTTON = 1;

    public static final int TYPE_TEXTFIELD = 2;

    public static final int TYPE_RADIOGROUP = 3;

    public static final int TYPE_CHECKGROUP = 4;

    public static final int TYPE_LISTBOX = 5;

    public static final int TYPE_COMBOBOX = 6;

    /* To be implemented */
    static final int TYPE_SIGNATURE = 7;
    
    PdfFormField parent;
    
    ArrayList kids;
    
    int level; //widget or intermediate?
    
    int type; 

    int alignment;
    
    int highlightMode;
    
    int fieldFlag = -1;

    boolean hasDA;
    
    String da;
    
    String name;

    String altName;
    
    String mappingName;
    
    String defaultValue;

    String value;
    
    PdfFont font;
    
    int rotation;
    
    /* Will be used only for text fields */
    int maxlen = -1;
    
    Color borderColor;
    
    Color backgroundColor;
    
    ArrayList exportValues;

    ArrayList inputFocusActionList;
    
    ArrayList inputBlurActionList;
    
    ArrayList keyStrokeJavaScriptList;
    
    ArrayList beforeFormatJavaScriptList;
    
    ArrayList valueChangeJavaScriptList;
    
    ArrayList recalculateJavaScriptList;
    
    static
    {
        knownAttributes.put(PDF_KIDS, PdfNull.DUMMY);
        knownAttributes.put(PDF_T, PdfNull.DUMMY);
        knownAttributes.put(PDF_TU, PdfNull.DUMMY);
        knownAttributes.put(PDF_TM, PdfNull.DUMMY);
        knownAttributes.put(PDF_FIELD_FLAG, PdfNull.DUMMY);
        knownAttributes.put(PDF_V, PdfNull.DUMMY);
        knownAttributes.put(PDF_DV, PdfNull.DUMMY);
    }

    PdfFormField()
    {
        super();
        this.level = LEVEL_INTERMEDIATE;
        this.alignment = ALIGNMENT_NONE;
    }

    public PdfFormField(int type, String name)
    {
        this.level = LEVEL_INTERMEDIATE;
        this.type = type;
        this.name = name;
        this.alignment = ALIGNMENT_NONE;
    }
    
    public PdfFormField(int type, String name, int fieldflags)
    {
        this.level = LEVEL_INTERMEDIATE;
        this.type = type;
        this.name = name;
        this.fieldFlag = fieldflags;
        this.alignment = ALIGNMENT_NONE;
    }

    public PdfFormField(int type, String name, String alternateName,
        String mappingName, int fieldFlags)
    {
        this.level = LEVEL_INTERMEDIATE;
        this.type = type;
        this.name = name;
        this.altName = alternateName;
        this.mappingName = mappingName;
        this.fieldFlag = fieldFlags;
        this.alignment = ALIGNMENT_NONE;
    }

    public PdfFormField(int type, String name, String alternateName,
        String mappingName)
    {
        this.level = LEVEL_INTERMEDIATE;
        this.type = type;
        this.name = name;
        this.altName = alternateName;
        this.mappingName = mappingName;
        this.flags = -1;
        this.alignment = ALIGNMENT_NONE;
    }

    public PdfFormField(String name)
    {
        this.level = LEVEL_INTERMEDIATE;
        this.type = TYPE_NONE;
        this.name = name;
        this.alignment = ALIGNMENT_NONE;
    }
    
    public PdfFormField(String name, int fieldflags)
    {
        this.level = LEVEL_INTERMEDIATE;
        this.type = TYPE_NONE;
        this.name = name;
        this.fieldFlag = fieldflags;
        this.alignment = ALIGNMENT_NONE;
    }

    public PdfFormField(String name, String alternateName,
        String mappingName, int fieldFlags)
    {
        this.level = LEVEL_INTERMEDIATE;
        this.type = TYPE_NONE;
        this.name = name;
        this.altName = alternateName;
        this.mappingName = mappingName;
        this.fieldFlag = fieldFlags;
        this.alignment = ALIGNMENT_NONE;
    }

    public PdfFormField(String name, String alternateName,
        String mappingName)
    {
        this.level = LEVEL_INTERMEDIATE;
        this.type = TYPE_NONE;
        this.name = name;
        this.altName = alternateName;
        this.mappingName = mappingName;
        this.flags = -1;
        this.alignment = ALIGNMENT_NONE;
    }

    //These C'tors should not be exposed to user 
    PdfFormField(int type)
    {
        this.level = LEVEL_INTERMEDIATE;
        this.type = type;
        this.alignment = ALIGNMENT_NONE;
    }

    PdfFormField(int type, String name, Color borderColor,
        Color backgroundColor)
    {
        this.level = LEVEL_INTERMEDIATE;
        this.type = type;
        this.name = name;
        this.borderColor = borderColor;
        this.backgroundColor = backgroundColor;
        this.alignment = ALIGNMENT_NONE;
    }

    PdfFormField(int type, String name, int fieldflags,
        Color borderColor, Color backgroundColor)
    {
        this.level = LEVEL_INTERMEDIATE;
        this.type = type;
        this.name = name;
        this.borderColor = borderColor;
        this.backgroundColor = backgroundColor;
        this.fieldFlag = fieldflags;
        this.alignment = ALIGNMENT_NONE;
    }

    PdfFormField(int type, String name, String alternateName,
        String mappingName, int fieldFlags, Color borderColor,
        Color backgroundColor)
    {
        this.level = LEVEL_INTERMEDIATE;
        this.type = type;
        this.name = name;
        this.altName = alternateName;
        this.mappingName = mappingName;
        this.borderColor = borderColor;
        this.backgroundColor = backgroundColor;
        this.fieldFlag = fieldFlags;
        this.alignment = ALIGNMENT_NONE;
    }

    PdfFormField(int type, String name, String alternateName,
        String mappingName, Color borderColor, Color backgroundColor)
    {
        this.level = LEVEL_INTERMEDIATE;
        this.type = type;
        this.name = name;
        this.altName = alternateName;
        this.mappingName = mappingName;
        this.borderColor = borderColor;
        this.backgroundColor = backgroundColor;
        this.flags = -1;
        this.alignment = ALIGNMENT_NONE;
    }

    ArrayList getAllDescendantWidgets()
    {
        ArrayList retVal = new ArrayList();
        PdfFormField descendant;
        ArrayList moreDescendants;
        
        if (kids != null)
        {
            for (int i = 0, limit = kids.size(); i < limit; ++i)
            {
                descendant = (PdfFormField) kids.get(i);
                if (descendant.level == LEVEL_TERMINAL
                    && !retVal.contains(descendant))
                {
                    retVal.add(descendant);
                }
                else
                {
                    moreDescendants = descendant
                        .getAllDescendantWidgets();
                    if (moreDescendants != null)
                    {
                        retVal.addAll(moreDescendants);
                    }
                }
            }
        }

        return retVal;
    }
    
    protected HashMap prepareMKDict()
    {
        HashMap hm = new HashMap();
        hm.put(new PdfName(PDF_R), new PdfInteger(rotation));
        hm.put(new PdfName(PDF_BC), normalizeColor(borderColor));
        hm.put(new PdfName(PDF_BG), normalizeColor(backgroundColor));
        
        return hm;
    }
    
    //should not be public
    synchronized int getHighlightMode()
    {
        return highlightMode;
    }

    synchronized void setHighlightMode(int highlightMode)
    {
        this.highlightMode = highlightMode;
    }

    public synchronized int getRotation()
    {
        return rotation;
    }

    public synchronized void setRotation(int rotation)
    {
        this.rotation = rotation;
    }

    public synchronized PdfFont getFont()
    {
        return font;
    }

    /* DOC COMMENT: Only fonts with CP1252 encoding should be used */ 
    public synchronized void setFont(PdfFont font)
    {
        this.font = font == null ? null : (PdfFont) font.clone();
    }

    public synchronized String getMappingName()
    {
        return mappingName;
    }

    public synchronized void setMappingName(String mappingName)
    {
        this.mappingName = mappingName;
    }

    /* Alternate name is shown as tooltip text */
    public synchronized String getAlternateName()
    {
        return altName;
    }

    public synchronized void setAlternateName(String alternateName)
    {
        this.altName = alternateName;
    }

    public synchronized String getName()
    {
        return (name == null && parent != null) ? parent
            .getName() : (name == null ? "" : name);
    }

    public synchronized void setName(String name)
    {
        this.name = name;
    }
    
    public String getFullyQualifiedName()
    {
        return parent == null ? (name == null ? "" : name) : parent
            .getFullyQualifiedName()
            + "." + (name == null ? "" : name);
    }

    public final synchronized int getFieldFlags()
    {
        return fieldFlag;
    }

    /*
     * DOC COMMENT: Should be called only on root level fields since
     * flag is not overriden by kids in PDF
     */
    public final synchronized void setFieldFlags(int fieldFlags)
    {
        if (this.fieldFlag < 0)
        {
            this.fieldFlag = fieldFlags;
        }
        else
        {
            this.fieldFlag ^= fieldFlags;
        }
    }

    public synchronized String getValue()
    {
        return value;
    }

    public synchronized String getDefaultValue()
    {
        return defaultValue;
    }

    /* setValue will behave differently in reading and writing modes */
    public synchronized void setValue(String value)
    {
        this.value = value;
    }

    /* setDefaultValue will behave differently in reading and writing modes */
    public synchronized void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public synchronized boolean isRadioInUnison()
    {
        if (this.fieldFlag == -1)
        {
            if (this.parent != null)
            {
                return parent.isRadioInUnison();
            }
            
            return false;
        }
        
        return (fieldFlag & FLAG_RADIO_IN_UNISON) == FLAG_RADIO_IN_UNISON;
    }

    public synchronized void setRadioInUnison(
        boolean radioInUnison)
    {
        if (radioInUnison)
        {
            fieldFlag = (fieldFlag == -1 ? FLAG_RADIO_IN_UNISON
                : (fieldFlag | FLAG_RADIO_IN_UNISON));
        }
        else
        {
            fieldFlag = Math.max(0, fieldFlag);
            fieldFlag &= 0xfdffffff;
        }
    }

    public synchronized boolean isRadioNoToggleToOff()
    {
        if (this.fieldFlag == -1)
        {
            if (this.parent != null)
            {
                return parent.isRadioNoToggleToOff();
            }
            
            return false;
        }

        return (fieldFlag & FLAG_RADIO_NO_TOGGLE_TO_OFF) == 
            FLAG_RADIO_NO_TOGGLE_TO_OFF;
    }

    public synchronized void setRadioNoToggleToOff(
        boolean radioToggleToOff)
    {
        if (radioToggleToOff)
        {
            fieldFlag = (fieldFlag == -1 ? (1 << 14)
                : (fieldFlag | (1 << 14)));
        }
        else
        {
            fieldFlag = Math.max(0, fieldFlag);
            fieldFlag &= 0xffffefff;
        }
    }

    public synchronized boolean isNoExport()
    {
        if (this.fieldFlag == -1)
        {
            if (this.parent != null)
            {
                return parent.isNoExport();
            }
            
            return false;
        }

        return (fieldFlag & FLAG_NO_EXPORT) == FLAG_NO_EXPORT;
    }
    
    public synchronized void setNoExport(boolean noExport)
    {
        if (noExport)
        {
            fieldFlag = (fieldFlag == -1 ? FLAG_NO_EXPORT
                : (fieldFlag | FLAG_NO_EXPORT));
        }
        else
        {
            fieldFlag = Math.max(0, fieldFlag);
            fieldFlag &= 0xfffffffb;
        }
    }

    public synchronized boolean isReadOnly()
    {
        if (this.fieldFlag == -1)
        {
            if (this.parent != null)
            {
                return parent.isReadOnly();
            }
            
            return false;
        }

        return (fieldFlag & FLAG_READONLY) == FLAG_READONLY;
    }
    
    public synchronized void setReadOnly(boolean readOnly)
    {
        if (readOnly)
        {
            fieldFlag = (fieldFlag == -1 ? FLAG_READONLY
                : (fieldFlag | FLAG_READONLY));
        }
        else
        {
            fieldFlag = Math.max(0, fieldFlag);
            fieldFlag &= 0xfffffffe;
        }
    }
    
    public synchronized boolean isRequired()
    {
        if (this.fieldFlag == -1)
        {
            if (this.parent != null)
            {
                return parent.isRequired();
            }
            
            return false;
        }
        return (fieldFlag & FLAG_REQUIRED) == FLAG_REQUIRED;
    }
    
    public synchronized void setRequired(boolean required)
    {
        if (required)
        {
            fieldFlag = (fieldFlag == -1 ? FLAG_REQUIRED
                : (fieldFlag | FLAG_REQUIRED));
        }
        else
        {
            fieldFlag = Math.max(0, fieldFlag);
            fieldFlag &= 0xfffffffd;
        }
    }

    public synchronized boolean isNoSpellCheck()
    {
        if (this.fieldFlag == -1)
        {
            if (this.parent != null)
            {
                return parent.isNoSpellCheck();
            }
            
            return false;
        }

        return (fieldFlag & FLAG_NO_SPELLCHECK) == FLAG_NO_SPELLCHECK;
    }

    public synchronized void setNoSpellCheck(boolean noSpellCheck)
    {
        if (noSpellCheck)
        {
            fieldFlag = (fieldFlag == -1 ? FLAG_NO_SPELLCHECK
                : (fieldFlag | FLAG_NO_SPELLCHECK));
        }
        else
        {
            fieldFlag = Math.max(0, fieldFlag);
            fieldFlag &= 0xffbfffff;
        }
    }

    public synchronized Color getBackgroundColor()
    {
        return backgroundColor;
    }

    public synchronized void setBackgroundColor(Color backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    public synchronized Color getBorderColor()
    {
        return borderColor;
    }

    public synchronized int getType()
    {
        return type;
    }

    public synchronized void setBorderColor(Color borderColor)
    {
        this.borderColor = borderColor;
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
    
    void invokeAnnotEncode(PdfStdPage p) throws PdfException
    {
        super.encode(p);
    }
    
    void writeAppearances(PdfStdDocument d) throws IOException,
        PdfException
    {
        super.write(d);
    }
    
    //Called only by check boxes and radio buttons
    void setExportValues()
    {
        exportValues = new ArrayList();
        ArrayList widgets = getAllDescendantWidgets();
        String value;
        for (int i = 0, limit = widgets.size(); i < limit; ++i)
        {
            value = ((PdfFormButtonField) widgets.get(i)).value;
            exportValues.add(new PdfTextString(
                value == null ? "" : value, true));
        }
    }
    
    //Called only by check boxes and radio buttons
    void applyExportValueTo(PdfFormField radioOrCheck, PdfStdPage page)
        throws IOException, PdfException
    {
        if (exportValues == null)
        {
            PdfName name_opt = new PdfName(PDF_OPT);
            PdfObject inherited = page.originDoc.reader
                .getObject(dict.getValue(name_opt));
            if (inherited != null && !(inherited instanceof PdfNull))
            {
                exportValues = (ArrayList) ((PdfArray) inherited)
                    .getList();
            }
            else
            {
                exportValues = new ArrayList();
            }
        }
        int index = kids.indexOf(radioOrCheck);
        if (index != -1)
        {
            radioOrCheck.value = ((PdfString) exportValues
                .get(index)).getString();
        }
    }


    /*private static void pullupFlag(PdfFormField parent)
    {
        PdfFormField child;
        if (parent.kids != null)
        {
            for (int i = 0, limit = parent.kids.size(); i < limit; ++i)
            {
                child = (PdfFormField) parent.kids.get(i);
                pullupFlag(child);
                if (child.fieldFlag != FLAG_NONE)
                {
                    parent.fieldFlag |= child.fieldFlag;
                }
            }
        }
    }*/

    void applyPropertiesFrom(PdfDict annotDict, PdfStdPage page)
        throws IOException, PdfException
    {
        annotDict.dictMap.remove(new PdfName(PDF_FT));
        page.originDoc.catalog.removeFieldsEntry(annotDict.objNumber,
            annotDict.genNumber, page.originDoc);
        
        super.applyPropertiesFrom(annotDict, page);
        Map annotMap = annotDict.getMap();
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
            if (name.equals(PDF_T))
            {
                unknownAttributes.remove(key);
                this.name = ((PdfString) value).getString();
            }
            else if (name.equals(PDF_TM))
            {
                unknownAttributes.remove(key);
                this.mappingName = ((PdfString) value).getString();                
            }
            else if (name.equals(PDF_TU)) 
            {
                unknownAttributes.remove(key);
                this.altName = ((PdfString) value).getString();                
            }
            else if (name.equals(PDF_FIELD_FLAG))
            {
                unknownAttributes.remove(key);
                this.fieldFlag = ((PdfNumber) value).getInt();
            }
            else if (name.equals(PDF_MAXLEN))
            {
                unknownAttributes.remove(key);
                this.maxlen = ((PdfNumber) value).getInt();
            }
            else if (name.equals(PDF_Q))
            {
                unknownAttributes.remove(key);
                int alignment = ((PdfNumber) value).getInt();
                switch (alignment)
                {
                    case 1:
                        this.alignment = ALIGNMENT_CENTER;
                        break;
                    case 2:
                        this.alignment = ALIGNMENT_RIGHT;
                        break;
                    default: 
                        this.alignment = ALIGNMENT_LEFT;
                        break;
                }
            } 
            else if (name.equals(PDF_DA))
            {
                unknownAttributes.remove(key);
                da = ((PdfString) value).getString();
                hasDA = true;
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

    private void setActionList(ArrayList actionList,
        PdfStdDocument d, PdfName key)
    {
        PdfDict actionDict = null;
        if (actionList != null)
        {
            int size = actionList.size();
            for (int i = size - 1; i >= 0; i--)
            {
                actionDict = (PdfDict) actionList.get(i);
                
                PdfName fields_key = new PdfName(PDF_FIELDS);
                if (actionDict.dictMap.containsKey(fields_key))
                {
                    ArrayList indRefs = new ArrayList();
                    ArrayList fields = (ArrayList) actionDict.dictMap
                        .get(fields_key);
                    for (int j = 0, limit = fields.size(); j < limit; ++j)
                    {
                        indRefs.add(new PdfIndirectReference(
                            ((PdfFormField) fields.get(i)).dict.objNumber, 0));
                    }
                    actionDict.dictMap.put(new PdfName(PDF_FIELDS),
                        new PdfArray(indRefs));
                }
                
                if (i != size - 1)
                {
                    actionDict.getMap().put(new PdfName(PDF_NEXT),
                        new PdfIndirectReference(d.objectRun - 1, 0));
                }
                actionDict.setObjectNumber(d.objectRun++);
            }

            this.actionDict.getMap().put(key,
                new PdfIndirectReference(d.objectRun - 1, 0));
        }
    }
    
    void setActions(PdfStdDocument d)
    {
        setActionList(mouseEntryActionList, d, new PdfName(PDF_E));
        setActionList(mouseExitActionList, d, new PdfName(PDF_X));
        setActionList(mouseDownActionList, d, new PdfName(PDF_D));
        setActionList(mouseUpActionList, d, new PdfName(PDF_U));

        setActionList(keyStrokeJavaScriptList, d, new PdfName(
            PDF_KEYSTROKE));
        setActionList(beforeFormatJavaScriptList, d, new PdfName(
            PDF_BEFOREFORMAT));
        setActionList(recalculateJavaScriptList, d, new PdfName(
            PDF_RECALCULATE));
        setActionList(valueChangeJavaScriptList, d, new PdfName(
            PDF_VALUECHANGE));

        setActionList(inputFocusActionList, d, new PdfName(PDF_FO));
        setActionList(inputBlurActionList, d, new PdfName(PDF_BL));

        setActionList(pageOpenActionList, d,
            new PdfName(PDF_PAGEOPEN));
        setActionList(pageCloseActionList, d, new PdfName(
            PDF_PAGECLOSE));
        setActionList(pageVisibleActionList, d, new PdfName(
            PDF_PAGEVISIBLE));
        setActionList(pageInvisibleActionList, d, new PdfName(
            PDF_PAGEINVISIBLE));
    }

    protected void set(PdfStdPage page, PdfStdDocument d)
        throws IOException, PdfException
    {
        super.set(page.originDoc, d); //Appearances
        
        this.dict.setObjectNumber(d.objectRun++);
        
        HashMap fieldMap = (HashMap) this.dict.getMap();
        if (this.type == TYPE_NONE && this.kids != null)
        {
            this.type = ((PdfFormField) this.kids.get(0)).type;
        }
        if (this.type != TYPE_NONE)
        {
            String fType = "";
            switch (this.type)
            {
                case TYPE_PUSHBUTTON:
                case TYPE_RADIOGROUP:
                case TYPE_CHECKGROUP:
                    fType = PDF_BTN;
                    break;
                case TYPE_LISTBOX:
                case TYPE_COMBOBOX:
                    fType = PDF_CH;
                    break;
                case TYPE_TEXTFIELD:
                    fType = PDF_TX;
                    break;
                case TYPE_SIGNATURE:
                    fType = "Sig";
                    break;
            }
            fieldMap.put(new PdfName(PDF_FT), new PdfName(fType));
        }
        if (this.level != LEVEL_TERMINAL)
        {
            fieldMap.put(new PdfName(PDF_T), new PdfTextString(
                this.name == null ? "" : this.name, true));
        }
        else if (name != null)
        {
            fieldMap.put(new PdfName(PDF_T), new PdfTextString(
                this.name, true));
        }
        if (this.altName != null)
        {
            fieldMap.put(new PdfName(PDF_TU), new PdfTextString(
                this.altName, true));
        }
        if (this.mappingName != null)
        {
            fieldMap.put(new PdfName(PDF_TM), new PdfTextString(
                this.mappingName, true));
        }
        if (this.fieldFlag != -1)
        {
            fieldMap.put(new PdfName(PDF_FIELD_FLAG), new PdfInteger(
                this.fieldFlag));
        }
        if (maxlen != -1)
        {
            fieldMap.put(new PdfName(PDF_MAXLEN), new PdfInteger(
                maxlen));
        }
        if (this.alignment != ALIGNMENT_NONE)
        {
            fieldMap.put(new PdfName(PDF_Q),
                new PdfInteger(alignment));
        }

        if (this.font != null)
        {
            ((PdfProDocument) d).addDefaultFormFont(this.font);
        }
        if (this.actionDict != null)
        {
            setActions(d);
            fieldMap.put(new PdfName(PDF_AA), actionDict);
        }

        if (this.kids != null)
        {
            int size = this.kids.size();
            for (int i = 0; i < size; ++i)
            {
                ((PdfFormField) kids.get(i)).set(page, d);
            }
        }
    }
    
    private void writeActionList(ArrayList actionList,
        PdfStdDocument d) throws IOException
    {
        if (actionList != null)
        {
            PdfDict openActionDict;
            for (int i = 0, limit = actionList.size(); i < limit; i++)
            {
                openActionDict = (PdfDict) actionList.get(i);
                int index = openActionDict.getObjectNumber();
                d.offset[index] = d.bytesWritten;
                d.bytesWritten += d.writer
                    .writeIndirectObject(openActionDict);
            }
        }
    }
    
    void writeActions(PdfStdDocument d) throws IOException
    {
        if (actionDict == null)
        {
            return;
        }
        
        writeActionList(mouseEntryActionList, d);
        writeActionList(mouseExitActionList, d);
        writeActionList(mouseDownActionList, d);
        writeActionList(mouseUpActionList, d);
        
        writeActionList(keyStrokeJavaScriptList, d);
        writeActionList(beforeFormatJavaScriptList, d);
        writeActionList(recalculateJavaScriptList, d);
        writeActionList(valueChangeJavaScriptList, d);
        writeActionList(inputFocusActionList, d);
        writeActionList(inputBlurActionList, d);

        writeActionList(pageOpenActionList, d);
        writeActionList(pageCloseActionList, d);
        writeActionList(pageVisibleActionList, d);
        writeActionList(pageInvisibleActionList, d);
    }
    
    void write(PdfStdDocument d) throws IOException, PdfException
    {
        writeActions(d);
        
        PdfFormField child;
        HashMap fieldMap = (HashMap) this.dict.getMap();
        if (this.kids != null)
        {
            ArrayList kidsIndRefList = new ArrayList();
            for (int i = 0, limit = kids.size(); i < limit; ++i)
            {
                child = (PdfFormField) kids.get(i);
                kidsIndRefList.add(new PdfIndirectReference(
                    (child).dict.objNumber, 0));
                child.write(d);
            }
            fieldMap.put(new PdfName(PDF_KIDS), new PdfArray(
                kidsIndRefList));
        }
        if (this.parent != null)
        {
            fieldMap.put(new PdfName(PDF_PARENT),
                new PdfIndirectReference(
                    ((PdfFormField) parent).dict.objNumber, 0));
        }
        if (exportValues != null)
        {
            this.dict.dictMap.put(new PdfName("Opt"), new PdfArray(
                exportValues));
        }
        
        int index = this.dict.getObjectNumber();
        d.offset[index] = d.bytesWritten;
        d.bytesWritten += d.writer.writeIndirectObject(this.dict);
    }

    public final synchronized void addChildField(PdfFormField child)
    {
        if (kids == null)
        {
            kids = new ArrayList();
        }
        kids.add(child);
        child.parent = this;
    }

    public synchronized List getChildList()
    {
        return kids == null ? new ArrayList() : kids;
    }

    public synchronized int getChildCount()
    {
        return kids == null ? 0 : kids.size();
    }
    
    /* DOC COMMENT : Recommended only for radio groups */
    public int[] getRadioGroupSelectedIndices()
    {
        ArrayList retList = new ArrayList();
        if (kids != null)
        {
            PdfFormField kid;
            for (int i = 0, limit = kids.size(); i < limit; ++i)
            {
                kid = (PdfFormField) kids.get(i);
                if ((kid.level == LEVEL_TERMINAL)
                    && (kid.type == TYPE_RADIOGROUP)
                    && (((PdfFormRadioButton) kid).getState() == BUTTON_STATE_ON))
                {
                    retList.add(new PdfInteger(i + 1));
                }
            }
        }
        Object[] objArr = retList.toArray();
        int[] retArr = new int[objArr.length];
        for (int i = 0; i < objArr.length; ++i)
        {
            retArr[i] = ((PdfInteger) objArr[i]).getInt();
        }

        return retArr;
    }

    /* DOC COMMENT : Recommended only for radio groups */
    public List getRadioGroupSelectedValues()
    {
        ArrayList retList = new ArrayList();
        if (kids != null)
        {
            PdfFormField kid;
            for (int i = 0, limit = kids.size(); i < limit; ++i)
            {
                kid = (PdfFormField) kids.get(i);
                if ((kid.level == LEVEL_TERMINAL)
                    && (kid.type == TYPE_RADIOGROUP))
                {
                    PdfFormRadioButton rb = (PdfFormRadioButton) kid;
                    if (rb.getState() == BUTTON_STATE_ON)
                    {
                        retList.add(new String(rb.getValue()));
                    }
                }
            }
        }

        return retList;
    }

    /* DOC COMMENT : Recommended only for check groups */
    public int[] getCheckGroupSelectedIndices()
    {
        ArrayList retList = new ArrayList();
        if (kids != null)
        {
            PdfFormField kid;
            for (int i = 0, limit = kids.size(); i < limit; ++i)
            {
                kid = (PdfFormField) kids.get(i);
                if ((kid.level == LEVEL_TERMINAL)
                    && (kid.type == TYPE_RADIOGROUP)
                    && (((PdfFormCheckBox) kid).getState() == BUTTON_STATE_ON))
                {
                    retList.add(new PdfInteger(i + 1));
                }
            }
        }
        Object[] objArr = retList.toArray();
        int[] retArr = new int[objArr.length];
        for (int i = 0; i < objArr.length; ++i)
        {
            retArr[i] = ((PdfInteger) objArr[i]).getInt();
        }

        return retArr;
    }

    /* DOC COMMENT : Recommended only for check groups */
    public List getCheckGroupSelectedValues()
    {
        ArrayList retList = new ArrayList();
        if (kids != null)
        {
            PdfFormField kid;
            for (int i = 0, limit = kids.size(); i < limit; ++i)
            {
                kid = (PdfFormField) kids.get(i);
                if ((kid.level == LEVEL_TERMINAL)
                    && (kid.type == TYPE_CHECKGROUP))
                {
                    PdfFormCheckBox cb = (PdfFormCheckBox) kid;
                    if (cb.getState() == BUTTON_STATE_ON)
                    {
                        retList.add(new String(cb.getValue()));
                    }
                }
            }
        }

        return retList;
    }

    public synchronized void addActionFormReset(int eventType)
        throws PdfException
    {
        if (actionDict == null)
        {
            actionDict = new PdfDict(new HashMap());
        }

        HashMap hm = new HashMap();
        hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_ACTION));
        hm.put(new PdfName(PDF_S), new PdfName(PDF_RESET_FORM));

        switch (eventType)
        {
            case PdfAction.PdfEvent.ON_MOUSE_ENTER:
                if (mouseEntryActionList == null)
                {
                    mouseEntryActionList = new ArrayList();
                }
                mouseEntryActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_MOUSE_EXIT:
                if (mouseExitActionList == null)
                {
                    mouseExitActionList = new ArrayList();
                }
                mouseExitActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_MOUSE_DOWN:
                if (mouseDownActionList == null)
                {
                    mouseDownActionList = new ArrayList();
                }
                mouseDownActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_MOUSE_UP:
                if (mouseUpActionList == null)
                {
                    mouseUpActionList = new ArrayList();
                }
                mouseUpActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_INPUT_FOCUS:
                if (inputFocusActionList == null)
                {
                    inputFocusActionList = new ArrayList();
                }
                inputFocusActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_INPUT_BLURRED:
                if (inputBlurActionList == null)
                {
                    inputBlurActionList = new ArrayList();
                }
                inputBlurActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_CLOSE:
                if (pageCloseActionList == null)
                {
                    pageCloseActionList = new ArrayList();
                }
                pageCloseActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_OPEN:
                if (pageOpenActionList == null)
                {
                    pageOpenActionList = new ArrayList();
                }
                pageOpenActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_VISIBLE:
                if (pageVisibleActionList == null)
                {
                    pageVisibleActionList = new ArrayList();
                }
                pageVisibleActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_INVISIBLE:
                if (pageInvisibleActionList == null)
                {
                    pageInvisibleActionList = new ArrayList();
                }
                pageInvisibleActionList.add(new PdfDict(hm));
                break;

            default:
                break;
        }
    }

    public synchronized void addActionFormReset(int eventType,
        PdfFormField field, boolean exclude) throws PdfException
    {
        if (actionDict == null)
        {
            actionDict = new PdfDict(new HashMap());
        }

        HashMap hm = new HashMap();
        hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_ACTION));
        hm.put(new PdfName(PDF_S), new PdfName(PDF_RESET_FORM));
        if (field != null)
        {
            ArrayList fields = new ArrayList();
            fields.add(field);
            hm.put(new PdfName(PDF_FIELDS), fields);
            hm.put(new PdfName(PDF_FLAGS), new PdfInteger(exclude ? 1
                : 0));
        }
        switch (eventType)
        {
            case PdfAction.PdfEvent.ON_MOUSE_ENTER:
                if (mouseEntryActionList == null)
                {
                    mouseEntryActionList = new ArrayList();
                }
                mouseEntryActionList.add(new PdfDict(hm));
                break;
                
            case PdfAction.PdfEvent.ON_MOUSE_EXIT:
                if (mouseExitActionList == null)
                {
                    mouseExitActionList = new ArrayList();
                }
                mouseExitActionList.add(new PdfDict(hm));
                break;
                
            case PdfAction.PdfEvent.ON_MOUSE_DOWN:
                if (mouseDownActionList == null)
                {
                    mouseDownActionList = new ArrayList();
                }
                mouseDownActionList.add(new PdfDict(hm));
                break;
                
            case PdfAction.PdfEvent.ON_MOUSE_UP:
                if (mouseUpActionList == null)
                {
                    mouseUpActionList = new ArrayList();
                }
                mouseUpActionList.add(new PdfDict(hm));
                break;
                
            case PdfAction.PdfEvent.ON_INPUT_FOCUS:
                if (inputFocusActionList == null)
                {
                    inputFocusActionList = new ArrayList();
                }
                inputFocusActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_INPUT_BLURRED:
                if (inputBlurActionList == null)
                {
                    inputBlurActionList = new ArrayList();
                }
                inputBlurActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_CLOSE:
                if (pageCloseActionList == null)
                {
                    pageCloseActionList = new ArrayList();
                }
                pageCloseActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_OPEN:
                if (pageOpenActionList == null)
                {
                    pageOpenActionList = new ArrayList();
                }
                pageOpenActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_VISIBLE:
                if (pageVisibleActionList == null)
                {
                    pageVisibleActionList = new ArrayList();
                }
                pageVisibleActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_INVISIBLE:
                if (pageInvisibleActionList == null)
                {
                    pageInvisibleActionList = new ArrayList();
                }
                pageInvisibleActionList.add(new PdfDict(hm));
                break;

            default:
                break;
        }
    }

    public synchronized void addActionFormReset(int eventType,
        ArrayList fields, boolean exclude) throws PdfException
    {
        if (actionDict == null)
        {
            actionDict = new PdfDict(new HashMap());
        }

        HashMap hm = new HashMap();
        hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_ACTION));
        hm.put(new PdfName(PDF_S), new PdfName(PDF_RESET_FORM));
        if (fields != null)
        {
            hm.put(new PdfName(PDF_FIELDS), fields);
            hm.put(new PdfName(PDF_FLAGS), new PdfInteger(exclude ? 1
                : 0));
        }
        switch (eventType)
        {
            case PdfAction.PdfEvent.ON_MOUSE_ENTER:
                if (mouseEntryActionList == null)
                {
                    mouseEntryActionList = new ArrayList();
                }
                mouseEntryActionList.add(new PdfDict(hm));
                break;
                
            case PdfAction.PdfEvent.ON_MOUSE_EXIT:
                if (mouseExitActionList == null)
                {
                    mouseExitActionList = new ArrayList();
                }
                mouseExitActionList.add(new PdfDict(hm));
                break;
                
            case PdfAction.PdfEvent.ON_MOUSE_DOWN:
                if (mouseDownActionList == null)
                {
                    mouseDownActionList = new ArrayList();
                }
                mouseDownActionList.add(new PdfDict(hm));
                break;
                
            case PdfAction.PdfEvent.ON_MOUSE_UP:
                if (mouseUpActionList == null)
                {
                    mouseUpActionList = new ArrayList();
                }
                mouseUpActionList.add(new PdfDict(hm));
                break;
                
            case PdfAction.PdfEvent.ON_INPUT_FOCUS:
                if (inputFocusActionList == null)
                {
                    inputFocusActionList = new ArrayList();
                }
                inputFocusActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_INPUT_BLURRED:
                if (inputBlurActionList == null)
                {
                    inputBlurActionList = new ArrayList();
                }
                inputBlurActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_CLOSE:
                if (pageCloseActionList == null)
                {
                    pageCloseActionList = new ArrayList();
                }
                pageCloseActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_OPEN:
                if (pageOpenActionList == null)
                {
                    pageOpenActionList = new ArrayList();
                }
                pageOpenActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_VISIBLE:
                if (pageVisibleActionList == null)
                {
                    pageVisibleActionList = new ArrayList();
                }
                pageVisibleActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_INVISIBLE:
                if (pageInvisibleActionList == null)
                {
                    pageInvisibleActionList = new ArrayList();
                }
                pageInvisibleActionList.add(new PdfDict(hm));
                break;

            default:
                break;
        }
    }

    public synchronized void addAction(int eventType, int namedAction)
        throws PdfException
    {
        if (actionDict == null)
        {
            actionDict = new PdfDict(new HashMap());
        }

        HashMap hm = new HashMap();
        hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_ACTION));
        hm.put(new PdfName(PDF_S), new PdfName(PDF_NAMED));
        
        if (namedAction == PdfAction.NAMED_FIRSTPAGE)
        {
            hm.put(new PdfName(PDF_N), new PdfName(PDF_FIRST_PAGE));
        }
        else if (namedAction == PdfAction.NAMED_LASTPAGE)
        {
            hm.put(new PdfName(PDF_N), new PdfName(PDF_LAST_PAGE));
        }
        else if (namedAction == PdfAction.NAMED_NEXTPAGE)
        {
            hm.put(new PdfName(PDF_N), new PdfName(PDF_NEXT_PAGE));
        }
        else if (namedAction == PdfAction.NAMED_PREVPAGE)
        {
            hm.put(new PdfName(PDF_N), new PdfName(PDF_PREV_PAGE));
        }
        else
        {
            throw new PdfException("Invalid Named Action.");
        }

        switch (eventType)
        {
            case PdfAction.PdfEvent.ON_MOUSE_ENTER:
                if (mouseEntryActionList == null)
                {
                    mouseEntryActionList = new ArrayList();
                }
                mouseEntryActionList.add(new PdfDict(hm));
                break;
                
            case PdfAction.PdfEvent.ON_MOUSE_EXIT:
                if (mouseExitActionList == null)
                {
                    mouseExitActionList = new ArrayList();
                }
                mouseExitActionList.add(new PdfDict(hm));
                break;
                
            case PdfAction.PdfEvent.ON_MOUSE_DOWN:
                if (mouseDownActionList == null)
                {
                    mouseDownActionList = new ArrayList();
                }
                mouseDownActionList.add(new PdfDict(hm));
                break;
                
            case PdfAction.PdfEvent.ON_MOUSE_UP:
                if (mouseUpActionList == null)
                {
                    mouseUpActionList = new ArrayList();
                }
                mouseUpActionList.add(new PdfDict(hm));
                break;
                
            case PdfAction.PdfEvent.ON_INPUT_FOCUS:
                if (inputFocusActionList == null)
                {
                    inputFocusActionList = new ArrayList();
                }
                inputFocusActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_INPUT_BLURRED:
                if (inputBlurActionList == null)
                {
                    inputBlurActionList = new ArrayList();
                }
                inputBlurActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_CLOSE:
                if (pageCloseActionList == null)
                {
                    pageCloseActionList = new ArrayList();
                }
                pageCloseActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_OPEN:
                if (pageOpenActionList == null)
                {
                    pageOpenActionList = new ArrayList();
                }
                pageOpenActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_VISIBLE:
                if (pageVisibleActionList == null)
                {
                    pageVisibleActionList = new ArrayList();
                }
                pageVisibleActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_INVISIBLE:
                if (pageInvisibleActionList == null)
                {
                    pageInvisibleActionList = new ArrayList();
                }
                pageInvisibleActionList.add(new PdfDict(hm));
                break;

            default:
                break;
        }
    }
    
    public synchronized void addAction(int actionType, int eventType,
        String javascriptOrUri) throws PdfException
    {
        if (actionDict == null)
        {
            actionDict = new PdfDict(new HashMap());
        }

        HashMap hm = new HashMap();
        hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_ACTION));

        if (actionType == PdfAction.URI)
        {
            hm.put(new PdfName(PDF_S), new PdfName(PDF_URI_ACTION));
            hm.put(new PdfName(PDF_URI_ACTION), new PdfString(
                javascriptOrUri, true));
        }
        else if (actionType == PdfAction.JAVASCRIPT)
        {
            hm.put(new PdfName(PDF_S), new PdfName(
                PDF_JAVASCRIPT_ACTION));
            hm.put(new PdfName(PDF_JS), new PdfString(
                javascriptOrUri, true));
        }
        else
        {
            throw new PdfException("Invalid Action Type.");
        }

        switch (eventType)
        {
            case PdfAction.PdfEvent.ON_MOUSE_ENTER:
                if (mouseEntryActionList == null)
                {
                    mouseEntryActionList = new ArrayList();
                }
                mouseEntryActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_MOUSE_EXIT:
                if (mouseExitActionList == null)
                {
                    mouseExitActionList = new ArrayList();
                }
                mouseExitActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_MOUSE_DOWN:
                if (mouseDownActionList == null)
                {
                    mouseDownActionList = new ArrayList();
                }
                mouseDownActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_MOUSE_UP:
                if (mouseUpActionList == null)
                {
                    mouseUpActionList = new ArrayList();
                }
                mouseUpActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_INPUT_FOCUS:
                if (inputFocusActionList == null)
                {
                    inputFocusActionList = new ArrayList();
                }
                inputFocusActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_INPUT_BLURRED:
                if (inputBlurActionList == null)
                {
                    inputBlurActionList = new ArrayList();
                }
                inputBlurActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_CLOSE:
                if (pageCloseActionList == null)
                {
                    pageCloseActionList = new ArrayList();
                }
                pageCloseActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_OPEN:
                if (pageOpenActionList == null)
                {
                    pageOpenActionList = new ArrayList();
                }
                pageOpenActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_VISIBLE:
                if (pageVisibleActionList == null)
                {
                    pageVisibleActionList = new ArrayList();
                }
                pageVisibleActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_INVISIBLE:
                if (pageInvisibleActionList == null)
                {
                    pageInvisibleActionList = new ArrayList();
                }
                pageInvisibleActionList.add(new PdfDict(hm));
                break;

            default:
                break;
        }
        
        if (actionType == PdfAction.JAVASCRIPT)
        {
            switch (eventType)
            {
                case PdfAction.PdfEvent.ON_FIELD_BEFORE_FORMAT:
                    if (beforeFormatJavaScriptList == null)
                    {
                        beforeFormatJavaScriptList = new ArrayList();
                    }
                    beforeFormatJavaScriptList.add(new PdfDict(hm));
                    break;

                case PdfAction.PdfEvent.ON_FIELD_KEY_STROKE:
                    if (keyStrokeJavaScriptList == null)
                    {
                        keyStrokeJavaScriptList = new ArrayList();
                    }
                    keyStrokeJavaScriptList.add(new PdfDict(hm));
                    break;

                case PdfAction.PdfEvent.ON_FIELD_VALUE_CHANGE:
                    if (valueChangeJavaScriptList == null)
                    {
                        valueChangeJavaScriptList = new ArrayList();
                    }
                    valueChangeJavaScriptList.add(new PdfDict(hm));
                    break;

                case PdfAction.PdfEvent.ON_FIELD_VALUE_RECALCULATE:
                    if (recalculateJavaScriptList == null)
                    {
                        recalculateJavaScriptList = new ArrayList();
                    }
                    recalculateJavaScriptList.add(new PdfDict(hm));
                    break;
                default:
                    break;
            }
        }
    }

    public synchronized void addAction(int actionType, int eventType,
        String applicationToLaunch, boolean isPrint)
        throws PdfException
    {
        if (actionDict == null)
        {
            actionDict = new PdfDict(new HashMap());
        }

        HashMap hm = new HashMap();

        if (actionType == PdfAction.LAUNCH)
        {
            hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_ACTION));
            hm.put(new PdfName(PDF_S), new PdfName(
                PDF_LAUNCH_ACTION));
            hm.put(new PdfName(PDF_F), new PdfString(
                applicationToLaunch, true));
            HashMap winHm = new HashMap();
            winHm.put(new PdfName(PDF_F), new PdfString(
                applicationToLaunch, true));
            if (isPrint)
            {
                winHm.put(new PdfName(PDF_O), new PdfString("print",
                    false));
            }
            hm.put(new PdfName("Win"), new PdfDict(winHm));
        }
        else
        {
            throw new PdfException("Invalid Action Type...");
        }

        switch (eventType)
        {
            case PdfAction.PdfEvent.ON_MOUSE_ENTER:
                if (mouseEntryActionList == null)
                {
                    mouseEntryActionList = new ArrayList();
                }
                mouseEntryActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_MOUSE_EXIT:
                if (mouseExitActionList == null)
                {
                    mouseExitActionList = new ArrayList();
                }
                mouseExitActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_MOUSE_DOWN:
                if (mouseDownActionList == null)
                {
                    mouseDownActionList = new ArrayList();
                }
                mouseDownActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_MOUSE_UP:
                if (mouseUpActionList == null)
                {
                    mouseUpActionList = new ArrayList();
                }
                mouseUpActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_INPUT_FOCUS:
                if (inputFocusActionList == null)
                {
                    inputFocusActionList = new ArrayList();
                }
                inputFocusActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_INPUT_BLURRED:
                if (inputBlurActionList == null)
                {
                    inputBlurActionList = new ArrayList();
                }
                inputBlurActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_CLOSE:
                if (pageCloseActionList == null)
                {
                    pageCloseActionList = new ArrayList();
                }
                pageCloseActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_OPEN:
                if (pageOpenActionList == null)
                {
                    pageOpenActionList = new ArrayList();
                }
                pageOpenActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_VISIBLE:
                if (pageVisibleActionList == null)
                {
                    pageVisibleActionList = new ArrayList();
                }
                pageVisibleActionList.add(new PdfDict(hm));
                break;

            case PdfAction.PdfEvent.ON_PAGE_INVISIBLE:
                if (pageInvisibleActionList == null)
                {
                    pageInvisibleActionList = new ArrayList();
                }
                pageInvisibleActionList.add(new PdfDict(hm));
                break;

            default:
                break;
        }
    }
}