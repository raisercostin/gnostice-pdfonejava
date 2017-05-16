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

abstract class PdfFormButtonField extends PdfFormField
{
    static final PdfName YES = new PdfName(PDF_YES);
    
    static final PdfName OFF = new PdfName(PDF_OFF);

    static final int FLAG_RADIOBUTTON = 1 << 15;

    static final int FLAG_CHECKBOX = 0;
    
    static final int FLAG_PUSHBUTTON = 1 << 16;

    /* These SCALE constants are only for Push Button fields */
    public static final int SCALE_ALWAYS = 0;

    public static final int SCALE_NEVER = 1;

    public static final int SCALE_WHEN_ICON_IS_BIGGER = 2;

    public static final int SCALE_WHEN_ICON_IS_SMALLER = 3;

    public static final int SCALE_TYPE_PROPORTIONAL = 0;

    public static final int SCALE_TYPE_ANAMORPHIC = 1;

    //Following members are only for push buttons
    String downCaption;

    int captionPosition;

    int scaleEvent;

    int scaleType;

    double iconLeftPadding = 0.5; // 0.0 to 1.0

    double iconBottomPadding = 0.5; // 0.0 to 1.0

    boolean iconFitToRect;

    //Following members are only for radio and check
    HashMap normalAppMap;
    
    HashMap rolloverAppMap;
    
    HashMap downAppMap;
    
    String normalCaption; 
    
    int appearanceState;
    
    PdfName onState;
    
    PdfFormButtonField(int type)
    {
        super();
        this.level = LEVEL_TERMINAL;
        this.type = type;
    }
    
    PdfFormButtonField(int type, int fieldFlag)
    {
        super();
        this.level = LEVEL_TERMINAL;
        this.type = type;
        this.fieldFlag = fieldFlag;
    }

    PdfFormButtonField(int type, String name, int fieldFlags)
    {
        super(type, name, fieldFlags);
        this.level = LEVEL_TERMINAL;
    }

    PdfFormButtonField(int type, String name, String alternateName,
        String mappingName, int fieldFlags)
    {
        super(type, name, alternateName, mappingName,
            fieldFlags);
        this.level = LEVEL_TERMINAL;
    }

    PdfFormButtonField(int type, PdfRect r, String name, int fieldFlags)
    {
        setRect(r);
        this.name = name;
        this.fieldFlag = fieldFlags;
        this.level = LEVEL_TERMINAL;
        this.type = type;
    }

    PdfFormButtonField(int type, PdfRect r, String name, String alternateName,
        String mappingName, int fieldFlags)
    {
        setRect(r);
        this.name = name;
        this.altName = alternateName;
        this.mappingName = mappingName;
        this.fieldFlag = fieldFlags;
        this.level = LEVEL_TERMINAL;
        this.type = type;
    }

    PdfFormButtonField(int type, String name, int fieldFlags,
        Color borderColor, Color backgroundColor)
    {
        super(type, name, fieldFlags, borderColor,
            backgroundColor);
        this.level = LEVEL_TERMINAL;
    }

    PdfFormButtonField(int type, String name, String alternateName,
        String mappingName, int fieldFlags, Color borderColor,
        Color backgroundColor)
    {
        super(type, name, alternateName, mappingName,
            fieldFlags, borderColor, backgroundColor);
        this.level = LEVEL_TERMINAL;
    }

    PdfFormButtonField(int type, PdfRect r, String name, int fieldFlags,
        Color borderColor, Color backgroundColor)
    {
        super(type, name, fieldFlags, borderColor,
            backgroundColor);
        setRect(r);
        this.level = LEVEL_TERMINAL;
    }

    PdfFormButtonField(int type, PdfRect r, String name, String alternateName,
        String mappingName, int fieldFlags, Color borderColor,
        Color backgroundColor)
    {
        super(type, name, alternateName, mappingName,
            fieldFlags, borderColor, backgroundColor);
        setRect(r);
        this.level = LEVEL_TERMINAL;
    }

    static PdfFormButtonField getInstance(int flag)
    {
        if ((flag & FLAG_PUSHBUTTON) != 0)
        {
            return new PdfFormPushButton();
        }
        else if ((flag & FLAG_RADIOBUTTON) != 0)
        {
            return new PdfFormRadioButton();
        }
        else
        {
            return new PdfFormCheckBox();
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
            else if (name.equals(PDF_AS))
            {
                String state = ((PdfName) value).getString();
                if (!state.equals("Off"))
                {
                    this.appearanceState = BUTTON_STATE_ON;
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
                    else if (mk_name.equals(PDF_CA))
                    {
                        this.setNormalCaption(((PdfString) mk_value)
                            .getString());
                    }
                    else if (mk_name.equals(PDF_AC))
                    {
                        this.downCaption = ((PdfString) mk_value)
                            .getString();
                    }
                    else if (mk_name.equals(PDF_TP))
                    {
                        this.captionPosition = ((PdfNumber) mk_value)
                            .getInt();
                    }
                    else if (mk_name.equals(PDF_IF))
                    {
                        HashMap ifMap = (HashMap) ((PdfDict) mk_value)
                            .getMap();
                        String if_name;
                        PdfObject if_key, if_value, if_currObj;
                        for (Iterator iter2 = ifMap.keySet()
                            .iterator(); iter2.hasNext();)
                        {
                            if_key = (PdfObject) iter2.next();
                            if_currObj = (PdfObject) ifMap
                                .get(if_key);
                            if_value = page.originDoc.reader
                                .getObject(if_currObj);
                            if (if_value instanceof PdfNull)
                            {
                                continue;
                            }
                            if_name = ((PdfName) if_key).getString();

                            if (if_name.equals("SW"))
                            {
                                if (((PdfName) if_value).getString() == "A")
                                {
                                    this.scaleEvent = SCALE_ALWAYS;
                                }
                                else if (((PdfName) if_value)
                                    .getString() == "B")
                                {
                                    this.scaleEvent = SCALE_WHEN_ICON_IS_BIGGER;
                                }
                                else if (((PdfName) if_value)
                                    .getString() == "S")
                                {
                                    this.scaleEvent = SCALE_WHEN_ICON_IS_SMALLER;
                                }
                                else if (((PdfName) if_value)
                                    .getString() == "N")
                                {
                                    this.scaleEvent = SCALE_NEVER;
                                }
                            }
                            if (if_name.equals(PDF_S))
                            {
                                if (((PdfName) if_value).getString() == "A")
                                {
                                    this.scaleType = SCALE_TYPE_ANAMORPHIC;
                                }
                                else if (((PdfName) if_value)
                                    .getString() == "P")
                                {
                                    this.scaleType = SCALE_TYPE_PROPORTIONAL;
                                }
                            }
                            if (if_name.equals(PDF_A))
                            {
                                List l = ((PdfArray) if_value)
                                    .getList();
                                this.iconLeftPadding = ((PdfNumber) l
                                        .get(0)).getFloat();
                                this.iconBottomPadding = ((PdfNumber) l
                                        .get(1)).getFloat();
                            }
                            if (if_name.equals("FB"))
                            {
                                this.iconFitToRect = ((PdfBoolean) if_value)
                                        .getBooleanValue();
                            }
                        }
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

    }

    protected HashMap prepareMKDict()
    {
        HashMap hm = super.prepareMKDict();
        if (this.normalCaption != null)
        {
            hm.put(new PdfName(PDF_CA), new PdfString(
                normalCaption));
        }
        return hm;
    }

    public synchronized String getNormalCaption()
    {
        return normalCaption;
    }

    public synchronized void setNormalCaption(String normalCaption)
    {
        this.normalCaption = normalCaption;
    }

    public synchronized int getHighlightMode()
    {
        return super.getHighlightMode();
    }

    public synchronized void setHighlightMode(int highlightMode)
    {
        super.setHighlightMode(highlightMode);
    }
    
    //Called only by check boxes and radio buttons
    void setExportValues()
    {
        exportValues = new ArrayList();
        exportValues.add(new PdfTextString(this.value == null ? ""
            : this.value, true));
    }

    boolean searchOnStateName(PdfStdPage page, PdfName n)
        throws IOException, PdfException
    {
        HashMap m;
        PdfDict appMap;
        boolean found = false;

        appMap = (PdfDict) page.originDoc.reader
            .getObject((PdfObject) apHm.get(n));
        if (appMap != null)
        {
            m = (HashMap) appMap.dictMap;
            Object[] objArr = m.keySet().toArray();
            for (int i = 0; i < objArr.length; ++i)
            {
                String key = ((PdfName) objArr[i]).getString();
                if ( !key.equals("Off"))
                {
                    onState = new PdfName(key);
                    found = true;
                }
            }
        }

        return found;
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

        HashMap annotMap = (HashMap) dict.getMap();
        annotMap.put(new PdfName(Usable.PDF_SUBTYPE), new PdfName(
            Usable.PDF_WIDGET));
        annotMap.put(new PdfName(PDF_MK),
            new PdfDict(prepareMKDict()));

        switch (this.type)
        {
            case TYPE_RADIOGROUP:
            case TYPE_CHECKGROUP:

                if (this.parent != null)
                {
                    if (this.fieldFlag != -1)
                    {
                        this.parent.dict.dictMap.put(new PdfName(PDF_FIELD_FLAG),
                            new PdfInteger(this.parent.fieldFlag != -1
                                ? this.fieldFlag ^ this.parent.fieldFlag
                                : this.fieldFlag));
                    }
                    
                    /* Name will be on state appearance name */
                    this.dict.dictMap.remove(new PdfName(PDF_T));
                }
                else 
                {
                    if (this.fieldFlag != -1)
                    {
                        annotMap.put(new PdfName(PDF_FIELD_FLAG),
                            new PdfInteger(this.fieldFlag));
                    }
                }
                break;
            default:
                if (this.fieldFlag != -1)
                {
                    annotMap.put(new PdfName(PDF_FIELD_FLAG),
                        new PdfInteger(this.fieldFlag));
                }
                break;
        }
        
        String mode;
        switch (this.highlightMode)
        {
            case HIGHLIGHT_MODE_NONE:
                mode = "N";
                break;

            case HIGHLIGHT_MODE_INVERT:
                mode = "I";
                break;

            case HIGHLIGHT_MODE_OUTLINE:
                mode = "O";
                break;

            case HIGHLIGHT_MODE_PUSH:
                mode = "P";
                break;

            default:
                mode = "N";
                break;
        }
        annotMap.put(new PdfName("H"), new PdfName(mode));
    }
    
    void writeRadioAndCheckBoxAppearance(PdfStdDocument d)
        throws IOException, PdfException
    {
        PdfAppearanceStream ap;
        if (normalAppMap != null)
        {
            ap = (PdfAppearanceStream) normalAppMap.get(onState);
            if (ap != null)
            {
                int index = ap.getObjectNumber();
                d.offset[index] = d.bytesWritten;
                d.bytesWritten += d.writer.writeIndirectObject(ap);

                normalAppMap.put(onState, new PdfIndirectReference(
                    ap.objNumber, 0));
            }
            ap = (PdfAppearanceStream) normalAppMap.get(OFF);
            if (ap != null)
            {
                int index = ap.getObjectNumber();
                d.offset[index] = d.bytesWritten;
                d.bytesWritten += d.writer.writeIndirectObject(ap);

                normalAppMap.put(OFF, new PdfIndirectReference(
                    ap.objNumber, 0));
            }
            apHm.put(new PdfName(PDF_N), new PdfDict(normalAppMap));
        }
        if (rolloverAppMap != null)
        {
            ap = (PdfAppearanceStream) rolloverAppMap.get(onState);
            if (ap != null)
            {
                int index = ap.getObjectNumber();
                d.offset[index] = d.bytesWritten;
                d.bytesWritten += d.writer.writeIndirectObject(ap);

                rolloverAppMap.put(onState, new PdfIndirectReference(
                    ap.objNumber, 0));
            }
            ap = (PdfAppearanceStream) rolloverAppMap.get(OFF);
            if (ap != null)
            {
                int index = ap.getObjectNumber();
                d.offset[index] = d.bytesWritten;
                d.bytesWritten += d.writer.writeIndirectObject(ap);

                rolloverAppMap.put(OFF, new PdfIndirectReference(
                    ap.objNumber, 0));
            }
            apHm.put(new PdfName(PDF_R), new PdfDict(rolloverAppMap));
        }
        if (downAppMap != null)
        {
            ap = (PdfAppearanceStream) downAppMap.get(onState);
            if (ap != null)
            {
                int index = ap.getObjectNumber();
                d.offset[index] = d.bytesWritten;
                d.bytesWritten += d.writer.writeIndirectObject(ap);

                downAppMap.put(onState, new PdfIndirectReference(
                    ap.objNumber, 0));
            }
            ap = (PdfAppearanceStream) downAppMap.get(OFF);
            if (ap != null)
            {
                int index = ap.getObjectNumber();
                d.offset[index] = d.bytesWritten;
                d.bytesWritten += d.writer.writeIndirectObject(ap);

                downAppMap.put(OFF, new PdfIndirectReference(
                    ap.objNumber, 0));
            }
            apHm.put(new PdfName(PDF_D), new PdfDict(downAppMap));
        }
        if ( !apHm.isEmpty())
        {
            this.dict.dictMap.put(new PdfName(Usable.PDF_AP),
                new PdfDict(apHm));
        }
    }

    void setRadioAndCheckBoxAppearances(PdfStdDocument d) throws IOException,
        PdfException
    {
        if (this.parent != null && this.parent.exportValues == null)
        {
            this.parent.setExportValues();
        }
        else if (this.parent == null)
        {
            setExportValues();
        }
        
        if (onState == null)
        {
            onState = new PdfName(this.getName());
        }

        this.dict.dictMap.put(new PdfName(PDF_AS),
            appearanceState == BUTTON_STATE_ON ? onState : OFF);

        this.dict.dictMap.put(new PdfName(PDF_V),
            appearanceState == BUTTON_STATE_ON ? onState : OFF);

        this.dict.dictMap.put(new PdfName(PDF_DV),
            appearanceState == BUTTON_STATE_ON ? onState : OFF);

        PdfAppearanceStream ap;
        if (normalAppMap != null)
        {
            ap = (PdfAppearanceStream) normalAppMap.get(YES);
            if (ap != null)
            {
                ap.set(d);
                normalAppMap.remove(YES);
                normalAppMap.put(onState, ap);
            }
            ap = (PdfAppearanceStream) normalAppMap.get(OFF);
            if (ap != null)
            {
                ap.set(d);
            }
        }
        if (rolloverAppMap != null)
        {
            ap = (PdfAppearanceStream) rolloverAppMap.get(YES);
            if (ap != null)
            {
                ap.set(d);
                rolloverAppMap.remove(YES);
                rolloverAppMap.put(onState, ap);
            }
            ap = (PdfAppearanceStream) rolloverAppMap.get(OFF);
            if (ap != null)
            {
                ap.set(d);
            }
        }
        if (downAppMap != null)
        {
            ap = (PdfAppearanceStream) downAppMap.get(YES);
            if (ap != null)
            {
                ap.set(d);
                downAppMap.remove(YES);
                downAppMap.put(onState, ap);
            }
            ap = (PdfAppearanceStream) downAppMap.get(OFF);
            if (ap != null)
            {
                ap.set(d);
            }
        }

        this.normalAppearance = null;
        this.rolloverAppearance = null;
        this.downAppearance = null;
    }
    
    void write(PdfStdDocument d) throws IOException, PdfException
    {
        writeAppearances(d);
        super.write(d);
    }

    /*
     * DOC-COMMENT: Following methods should NOT be used with
     * PushButton fields
     */

    /* state is on or off */
    public synchronized int getState()
    {
        return appearanceState;
    }

    public void addNormalAppearance(PdfAppearanceStream ap, int state)
    {
        if (ap == null)
        {
            return;
        }
        if (normalAppMap == null)
        {
            normalAppMap = new HashMap();
        }
        normalAppMap.put(state == BUTTON_STATE_ON ? YES : OFF, ap);
    }

    public void addRolloverAppearance(PdfAppearanceStream ap, int state)
    {
        if (ap == null)
        {
            return;
        }
        if (rolloverAppMap == null)
        {
            rolloverAppMap = new HashMap();
        }
        rolloverAppMap.put(state == BUTTON_STATE_ON ? YES : OFF, ap);
    }

    public void addDownAppearance(PdfAppearanceStream ap, int state)
    {
        if (ap == null)
        {
            return;
        }
        if (downAppMap == null)
        {
            downAppMap = new HashMap();
        }
        downAppMap.put(state == BUTTON_STATE_ON ? YES : OFF, ap);
    }
}
