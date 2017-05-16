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

abstract class PdfProAnnot extends PdfAnnot
{
    PdfAppearanceStream normalAppearance;
    
    PdfAppearanceStream rolloverAppearance;
    
    PdfAppearanceStream downAppearance;    

    PdfPopUpAnnot popup;
    
    HashMap apHm;
    
    PdfDict actionDict;
    
    ArrayList mouseEntryActionList;
    
    ArrayList mouseExitActionList;
    
    ArrayList mouseUpActionList;
    
    ArrayList mouseDownActionList;
    
    ArrayList pageOpenActionList;
    
    ArrayList pageCloseActionList;
    
    ArrayList pageVisibleActionList;
    
    ArrayList pageInvisibleActionList;
    
    static
    {
        knownAttributes.put(PDF_POPUP, PdfNull.DUMMY);
        knownAttributes.put(PDF_AP, PdfNull.DUMMY);
    }

    protected Object clone()
    {
        PdfProAnnot clone = (PdfProAnnot) super.clone();
        if (this.popup != null)
        {
            clone.popup = (PdfPopUpAnnot) this.popup.clone();
            if (this.popup.parent != null)
            {
                clone.popup.parent = clone;
            }
        }
        if (normalAppearance != null)
        {
            clone.normalAppearance = (PdfAppearanceStream) normalAppearance
                .clone();
        }
        if (rolloverAppearance != null)
        {
            clone.rolloverAppearance = (PdfAppearanceStream) rolloverAppearance
                .clone();
        }
        if (downAppearance != null)
        {
            clone.downAppearance = (PdfAppearanceStream) downAppearance
                .clone();
        }
        if (this.unknownAttributes != null)
        {
            clone.unknownAttributes = (HashMap) this.unknownAttributes
                .clone();
        }
        if (this.apHm != null)
        {
            clone.apHm = (HashMap) this.apHm.clone();
        }
        
        return clone;
    }

    PdfProAnnot()
    {
        super();
        apHm = new HashMap();
    }

    PdfProAnnot(PdfRect r, Color c)
    {
        super(r, c);
        apHm = new HashMap();
    }

    PdfProAnnot(PdfRect r, int flags, Color c)
    {
        super(r, flags, c);
        apHm = new HashMap();
    }

    PdfProAnnot(PdfRect r, int flags)
    {
        super(r, flags);
        apHm = new HashMap();
    }

    PdfProAnnot(PdfRect r, String subject, String contents,
        String title, Color c)
    {
        super(r, subject, contents, title, c);
        apHm = new HashMap();
    }

    PdfProAnnot(PdfRect r, String subject, String contents,
        String title, int flags, Color c)
    {
        super(r, subject, contents, title, flags, c);
        apHm = new HashMap();
    }

    PdfProAnnot(PdfRect r, String subject, String contents,
        String title, int flags)
    {
        super(r, subject, contents, title, flags);
        apHm = new HashMap();
    }

    PdfProAnnot(PdfRect r, String subject, String contents,
        String title)
    {
        super(r, subject, contents, title);
        apHm = new HashMap();
    }

    void applyPropertiesFrom(PdfDict annotDict, PdfStdPage page)
        throws IOException, PdfException
    {
        annotDict.dictMap.remove(new PdfName(PDF_P));
        unknownAttributes = new HashMap();
        Map annotMap = annotDict.getMap();
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
            
            if (name.equals(Usable.PDF_CONTENTS))
            {
                setContents(((PdfString) value).getString());
            }
            else if (name.equals(Usable.PDF_ANNOT_SUBJECT))
            {
                setSubject(((PdfString) value).getString());
            }
            else if (name.equals(Usable.PDF_ANNOT_NAME))
            {
                setAnnotName(((PdfString) value).getString());
            }
            else if (name.equals(Usable.PDF_F))
            {
                setFlags(((PdfInteger) value).getInt());
            }
            else if (name.equals(Usable.PDF_T))
            {
                setTitle(((PdfString) value).getString());
            }
            else if (name.equals(Usable.PDF_RECT))
            {
                if (value instanceof PdfArray)
                {
                    setRect(new PdfRect((PdfArray) value,
                        page.pageHeight));
                }
            }
            else if (name.equals(Usable.PDF_COLOR))
            {
                ArrayList arrColor = (ArrayList) ((PdfArray) value)
                    .getList();
                double r = 0, g = 0, b = 0;

                if (arrColor.get(0) instanceof PdfNumber)
                {
                    r = ((PdfNumber) arrColor.get(0)).getVal();
                }
                if (arrColor.get(1) instanceof PdfNumber)
                {
                    g = ((PdfNumber) arrColor.get(1)).getVal();
                }
                if (arrColor.get(2) instanceof PdfNumber)
                {
                    b = ((PdfNumber) arrColor.get(2)).getVal();
                }
                Color col = new Color((float) r, (float) g, (float) b);
                setColor(col);
            }
            else if (name.equals(Usable.PDF_POPUP))
            {
                if (value instanceof PdfDict)
                {
                    PdfDict popupDict = (PdfDict) value;
                    PdfPopUpAnnot popup = new PdfPopUpAnnot();
                    popup.applyPropertiesFrom(popupDict, page);
                    boolean override = popupDict.dictMap
                        .containsKey(new PdfName(Usable.PDF_PARENT));
                    setPopup(popup, override);
                }
            }
            else if (name.equals(PDF_AP))
            {
                if (value instanceof PdfDict)
                {
                    apHm = (HashMap) ((PdfDict) value).dictMap;
                }
            }
            else if (name.equals(Usable.PDF_BS))
            {
                if (value instanceof PdfDict)
                {
                    Iterator bs = ((PdfDict) value).getMap().keySet()
                        .iterator();
                    PdfObject bsKey, bsValue;
                    String bsName;
                    
                    while (bs.hasNext())
                    {
                        bsKey = (PdfObject) bs.next();
                        bsValue = (PdfObject) ((PdfDict) value)
                            .getMap().get(bsKey);
                        bsValue = page.originDoc.reader
                            .getObject(bsValue);

                        bsName = ((PdfName) bsKey).getString();
                        if (bsName.equals(Usable.PDF_W))
                        {
                            if (bsValue instanceof PdfNumber)
                            {
                                setBorderWidth(((PdfNumber) bsValue)
                                    .getVal());
                            }
                        }
                        else if (bsName.equals(Usable.PDF_S))
                        {
                            String style = ((PdfName) bsValue)
                                .getString();
                            int sty = 0;
                            if (style.equals("D"))
                            {
                                sty = 1;
                            }
                            else if (style.equals("B"))
                            {
                                sty = 2;
                            }
                            else if (style.equals("I"))
                            {
                                sty = 3;
                            }
                            else if (style.equals("U"))
                            {
                                sty = 4;
                            }
                            setBorderStyle(sty);
                        }
                        else if (bsName.equals(Usable.PDF_D))
                        {
                            if (bsValue instanceof PdfArray)
                            {
                                List l = ((PdfArray) bsValue)
                                    .getList();
                                int limit = l.size();
                                int[] d = new int[limit];
                                for (int i = 0; i < limit; ++i)
                                {
                                    d[i] = ((PdfInteger) l.get(i))
                                        .getInt();
                                }
                                setDashPattern(d);
                            }
                        }
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
        annotDict.setObjectNumber(0);
        this.dict = annotDict;
    }

    public synchronized PdfPopUpAnnot getPopup()
    {
        return popup;
    }

    public synchronized void setPopup(PdfPopUpAnnot popup,
        boolean overridePopUpProperties)
    {
        this.popup = popup;
        if (popup != null && overridePopUpProperties)
        {
            popup.parent = this;
        }
    }

    public synchronized PdfAppearanceStream getDownAppearance()
    {
        return downAppearance;
    }

    public synchronized void setDownAppearance(
        PdfAppearanceStream downAppearance)
    {
        this.downAppearance = downAppearance;
    }

    public synchronized PdfAppearanceStream getNormalAppearance()
    {
        return normalAppearance;
    }

    public synchronized void setNormalAppearance(
        PdfAppearanceStream normalAppearance)
    {
        this.normalAppearance = normalAppearance;
    }

    public synchronized PdfAppearanceStream getRolloverAppearance()
    {
        return rolloverAppearance;
    }

    public synchronized void setRolloverAppearance(
        PdfAppearanceStream rolloverAppearance)
    {
        this.rolloverAppearance = rolloverAppearance;
    }

    PdfAnnot encode(PdfStdPage p) throws PdfException
    {
        super.encode(p);
        if (this.popup != null)
        {
            p.annotList.add(this.popup.encode(p));
        }
        
        /*double width = 0, height = 0;
        if (this.rect != null)
        {
            width = PdfMeasurement.convertToPdfUnit(
                p.measurementUnit, this.rect.width);
            height = PdfMeasurement.convertToPdfUnit(
                p.measurementUnit, this.rect.height);
        }
        if (normalAppearance != null)
        {
            ByteBuffer bb = ((ByteBuffer) ByteBuffer.wrap(
                normalAppearance.page.contentStream.getBuffer()).limit(
                normalAppearance.page.contentStream.size())).slice();
            
            HashMap hm = new HashMap();
            hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
                Usable.PDF_XOBJECT));
            hm.put(new PdfName(Usable.PDF_SUBTYPE), new PdfName(
                Usable.PDF_FORM));
            hm.put(new PdfName(Usable.PDF_BBOX), new PdfArray(
                new double[] { 0, 0, width, height }));
            
            normalAppearance.setDict(new PdfDict (hm));
            normalAppearance.setBuffer(bb);
        }
        
        if (rolloverAppearance != null)
        {
            ByteBuffer bb = ((ByteBuffer) ByteBuffer.wrap(
                rolloverAppearance.page.contentStream.getBuffer()).limit(
                rolloverAppearance.page.contentStream.size())).slice();

            HashMap hm = new HashMap();
            hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
                Usable.PDF_XOBJECT));
            hm.put(new PdfName(Usable.PDF_SUBTYPE), new PdfName(
                Usable.PDF_FORM));
            hm.put(new PdfName(Usable.PDF_BBOX), new PdfArray(
                new double[] { 0, 0, width, height }));
            
            rolloverAppearance.setDict(new PdfDict (hm));
            rolloverAppearance.setBuffer(bb);
        }

        if (downAppearance != null)
        {
            ByteBuffer bb = ((ByteBuffer) ByteBuffer.wrap(
                downAppearance.page.contentStream.getBuffer()).limit(
                downAppearance.page.contentStream.size())).slice();

            HashMap hm = new HashMap();
            hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
                Usable.PDF_XOBJECT));
            hm.put(new PdfName(Usable.PDF_SUBTYPE), new PdfName(
                Usable.PDF_FORM));
            hm.put(new PdfName(Usable.PDF_BBOX), new PdfArray(
                new double[] { 0, 0, width, height }));
            
            downAppearance.setDict(new PdfDict (hm));
            downAppearance.setBuffer(bb);
        }*/

        return this;
    }

    void write(PdfStdDocument d) throws IOException, PdfException
    {
        isWritten = true;
        
        if (normalAppearance != null)
        {
            int index = normalAppearance.getObjectNumber();
            d.offset[index] = d.bytesWritten;
            d.bytesWritten += d.writer
                .writeIndirectObject(normalAppearance);
        }
        if (downAppearance != null)
        {
            int index = downAppearance.getObjectNumber();
            d.offset[index] = d.bytesWritten;
            d.bytesWritten += d.writer
                .writeIndirectObject(downAppearance);
        }
        if (rolloverAppearance != null)
        {
            int index = rolloverAppearance.getObjectNumber();
            d.offset[index] = d.bytesWritten;
            d.bytesWritten += d.writer
                .writeIndirectObject(rolloverAppearance);
        }
    }

    protected void set(PdfStdDocument originDoc, PdfStdDocument d)
        throws IOException, PdfException
    {
        if (this.popup != null)
        {
            this.popup.set(originDoc, d);
            this.dict.getMap().put(new PdfName(Usable.PDF_POPUP),
                new PdfIndirectReference(
                    this.popup.dict.objNumber, 0));

        }
        if (unknownAttributes != null)
        {
            for (Iterator iter = unknownAttributes.keySet()
                .iterator(); iter.hasNext();)
            {
                PdfObject key = (PdfObject) iter.next();
                PdfObject value = (PdfObject) unknownAttributes
                    .get(key);
                if (value != null)
                {
                    d.updateIndirectRefs(originDoc, value, true);
                }
            }
        }
        
        PdfName normal = new PdfName(PDF_N);
        PdfName rollover = new PdfName(PDF_R);
        PdfName down = new PdfName(PDF_D);
        PdfObject value = null;
        if (normalAppearance != null)
        {
            /*normalAppearance.setObjectNumber(d.objectRun++);*/
            normalAppearance.set(d);
            
            apHm.put(normal, new PdfIndirectReference(
                normalAppearance.getObjectNumber(), 0));
        }
        else if (apHm.containsKey(normal))
        {
            value = (PdfObject) apHm.get(normal);
            d.updateIndirectRefs(originDoc, value, true);
        }
        
        if (rolloverAppearance != null)
        {
            /*rolloverAppearance.setObjectNumber(d.objectRun++);*/
            rolloverAppearance.set(d);           
            
            apHm.put(rollover, new PdfIndirectReference(
                rolloverAppearance.getObjectNumber(), 0));
        }
        else if (apHm.containsKey(rollover))
        {
            value = (PdfObject) apHm.get(rollover);
            d.updateIndirectRefs(originDoc, value, true);
        }

        if (downAppearance != null)
        {
            /*downAppearance.setObjectNumber(d.objectRun++);*/
            downAppearance.set(d);            
            
            apHm.put(down, new PdfIndirectReference(downAppearance
                .getObjectNumber(), 0));
        }
        else if (apHm.containsKey(down))
        {
            value = (PdfObject) apHm.get(down);
            d.updateIndirectRefs(originDoc, value, true);
        }
        
        if ( !apHm.isEmpty())
        {
            this.dict.dictMap.put(new PdfName(Usable.PDF_AP),
                new PdfDict(apHm));
        }
    }
}
