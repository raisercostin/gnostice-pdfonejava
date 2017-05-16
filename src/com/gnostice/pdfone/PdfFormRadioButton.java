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

import com.gnostice.pdfone.encodings.PdfEncodings;
import com.gnostice.pdfone.fonts.PdfFont;
import com.gnostice.pdfone.graphics.PdfBrush;
import com.gnostice.pdfone.graphics.PdfPen;

/*
 * DOC COMMENT: Do not set normal rollover and down apps directly. Use
 * addAppearance instead
 */

/*
 * DOC COMMENT: getValue and setValue are export values [Opt entry]
 */

/*
 * DOC COMMENT: Name can never be overridden by a radio widget henceif name is set for radio button it becomes the on
 * state name and not the /T entry.
 */

public class PdfFormRadioButton extends PdfFormButtonField
{
    static final PdfName FLAG = new PdfName(PDF_FIELD_FLAG);

    private boolean nameChangeNeeded;
    
    PdfFormRadioButton()
    {
        super(TYPE_RADIOGROUP);
    }

    public PdfFormRadioButton(String name, int fieldFlags)
    {
        super(TYPE_RADIOGROUP, name, fieldFlags
            | PdfFormButtonField.FLAG_RADIOBUTTON);
    }

    public PdfFormRadioButton(String name, String alternateName,
        String mappingName, int fieldFlags)
    {
        super(TYPE_RADIOGROUP, name, alternateName, mappingName,
            fieldFlags | PdfFormButtonField.FLAG_RADIOBUTTON);
    }

    public PdfFormRadioButton(String name, String alternateName,
        String mappingName)
    {
        super(TYPE_RADIOGROUP, name, alternateName, mappingName,
            PdfFormButtonField.FLAG_RADIOBUTTON);
    }

    public PdfFormRadioButton(String name)
    {
        super(TYPE_RADIOGROUP, name,
            PdfFormButtonField.FLAG_RADIOBUTTON);
    }

    /*
     * DOC COMMENT: This C'tor should not be used for widgets without
     * parent as in PDF, name is mandatory for all widgets. Name
     * should be present in some dict in the hierarchy.
     */
    public PdfFormRadioButton(PdfRect rect)
    {
        super(TYPE_RADIOGROUP, PdfFormButtonField.FLAG_RADIOBUTTON);
        setRect(rect);
    }

    public PdfFormRadioButton(PdfRect r, String name, int fieldFlags)
    {
        super(TYPE_RADIOGROUP, r, name, fieldFlags
            | PdfFormButtonField.FLAG_RADIOBUTTON);
    }

    public PdfFormRadioButton(PdfRect r, String name)
    {
        super(TYPE_RADIOGROUP, r, name,
            PdfFormButtonField.FLAG_RADIOBUTTON);
    }

    public PdfFormRadioButton(PdfRect r, String name,
        String alternateName, String mappingName, int fieldFlags)
    {
        super(TYPE_RADIOGROUP, r, name, alternateName, mappingName,
            fieldFlags | PdfFormButtonField.FLAG_RADIOBUTTON);
    }

    public PdfFormRadioButton(PdfRect r, String name,
        String alternateName, String mappingName)
    {
        super(TYPE_RADIOGROUP, r, name, alternateName, mappingName,
            PdfFormButtonField.FLAG_RADIOBUTTON);
    }

    public PdfFormRadioButton(String name, int fieldFlags,
        Color borderColor, Color backgroundColor)
    {
        super(TYPE_RADIOGROUP, name, fieldFlags
            | PdfFormButtonField.FLAG_RADIOBUTTON, borderColor,
            backgroundColor);
    }

    public PdfFormRadioButton(String name, String alternateName,
        String mappingName, int fieldFlags, Color borderColor,
        Color backgroundColor)
    {
        super(TYPE_RADIOGROUP, name, alternateName, mappingName,
            fieldFlags | PdfFormButtonField.FLAG_RADIOBUTTON,
            borderColor, backgroundColor);
    }

    public PdfFormRadioButton(String name, String alternateName,
        String mappingName, Color borderColor, Color backgroundColor)
    {
        super(TYPE_RADIOGROUP, name, alternateName, mappingName,
            PdfFormButtonField.FLAG_RADIOBUTTON, borderColor,
            backgroundColor);
    }

    public PdfFormRadioButton(String name, Color borderColor,
        Color backgroundColor)
    {
        super(TYPE_RADIOGROUP, name,
            PdfFormButtonField.FLAG_RADIOBUTTON, borderColor,
            backgroundColor);
    }

    public PdfFormRadioButton(PdfRect r, String name, int fieldFlags,
        Color borderColor, Color backgroundColor)
    {
        super(TYPE_RADIOGROUP, r, name, fieldFlags
            | PdfFormButtonField.FLAG_RADIOBUTTON, borderColor,
            backgroundColor);
    }

    public PdfFormRadioButton(PdfRect r, String name,
        Color borderColor, Color backgroundColor)
    {
        super(TYPE_RADIOGROUP, r, name,
            PdfFormButtonField.FLAG_RADIOBUTTON, borderColor,
            backgroundColor);
    }

    public PdfFormRadioButton(PdfRect r, String name,
        String alternateName, String mappingName, int fieldFlags,
        Color borderColor, Color backgroundColor)
    {
        super(TYPE_RADIOGROUP, r, name, alternateName, mappingName,
            fieldFlags | PdfFormButtonField.FLAG_RADIOBUTTON,
            borderColor, backgroundColor);
    }

    public PdfFormRadioButton(PdfRect r, String name,
        String alternateName, String mappingName, Color borderColor,
        Color backgroundColor)
    {
        super(TYPE_RADIOGROUP, r, name, alternateName, mappingName,
            PdfFormButtonField.FLAG_RADIOBUTTON, borderColor,
            backgroundColor);
    }

    public synchronized void setName(String name)
    {
        super.setName(name);
        nameChangeNeeded = true;
    }

    public synchronized void setState(int appearanceState)
    {
        if (this.parent == null
            || !(this instanceof PdfFormRadioButton))
        {
            this.appearanceState = appearanceState;
        }
        else
        {
            PdfFormField sibling;
            PdfFormRadioButton srb;
            for (int i = 0, limit = parent.kids.size(); i < limit; ++i)
            {
                sibling = (PdfFormField) parent.kids.get(i);
                if ( !(sibling instanceof PdfFormRadioButton))
                {
                    continue;
                }
                srb = (PdfFormRadioButton) sibling;
                if (srb.getOnStateName().equals(
                    ((PdfFormRadioButton) this).getOnStateName()))
                {
                    srb.appearanceState = appearanceState;
                }
                else
                {
                    srb.appearanceState = appearanceState ^ 1;
                }
            }
        }
    }

    public String getOnStateName()
    {
        return onState == null ? "" : onState.getString();
    }
    
    public synchronized List getChildList()
    {
        ArrayList retList = new ArrayList();
        retList.add(this);
        
        return retList;
    }

    void applyPropertiesFrom(PdfDict annotDict, PdfStdPage page)
        throws IOException, PdfException
    {
        super.applyPropertiesFrom(annotDict, page);

        if (!apHm.isEmpty())
        {
            PdfName[] names = new PdfName[] { new PdfName(PDF_N),
                new PdfName(PDF_R), new PdfName(PDF_D) };
            int i = 0;
            boolean found = false;

            do
            {
                found = searchOnStateName(page, names[i]);
                ++i;
            } while (!found && i <= 2);
        }
    }

    protected void set(PdfStdPage page, PdfStdDocument d)
        throws IOException, PdfException
    { 
        if ( !(this.mode == PdfDocument.READING_MODE))
        {
            double w1 = PdfMeasurement.convertToPdfUnit(
                page.measurementUnit, this.rect.width);
            double h1 = PdfMeasurement.convertToPdfUnit(
                page.measurementUnit, this.rect.height);

            createAppearance(page, new PdfRect(0, 0, w1, h1));
        }
        else if (!apHm.isEmpty() && nameChangeNeeded
            && page.mode == PdfDocument.READING_MODE)
        {
            changeOnStateName(page);
        }

        setRadioAndCheckBoxAppearances(d);

        super.set(page, d);
    }

    void writeAppearances(PdfStdDocument d) throws IOException,
        PdfException
    {
        writeRadioAndCheckBoxAppearance(d);
    }
    
    private void createAppearance(PdfStdPage page, PdfRect r)
        throws IOException, PdfException
    {
        if (normalAppMap == null)
        {
            PdfPen pen = new PdfPen();
            PdfBrush brush = new PdfBrush();
            pen.strokeColor = this.borderColor;
            pen.width = this.borderWidth;
            brush.fillColor = this.backgroundColor;
            
            PdfPoint centre = new PdfPoint(r.width / 2, r.height / 2);
            double radius = Math.min(r.width, r.height) / 2 - pen.width;
            PdfFont font = PdfFont.create("ZapfDingbats", (int) radius,
                PdfEncodings.CP1252);
            
            PdfAppearanceStream normal_on = new PdfAppearanceStream(r);
            if (this.borderColor != null
                || this.backgroundColor != null)
            {
                normal_on.drawCircle(centre, radius,
                    this.borderColor == null ? null : pen,
                    this.backgroundColor == null ? null : brush);
            }
            double textWidth = font.getWidth('l', PdfMeasurement.MU_POINTS);
            double textHeight = font.getHeight();
            normal_on.writeText("l", font, new PdfRect(
                centre.x - textWidth / 2, centre.y - textHeight / 1.6,
                textWidth, textHeight), new PdfTextFormatter());

            addNormalAppearance(normal_on, BUTTON_STATE_ON);
            
            PdfAppearanceStream normal_off = new PdfAppearanceStream(r);
            if (this.borderColor != null
                || this.backgroundColor != null)
            {
                normal_off.drawCircle(centre, radius,
                    this.borderColor == null ? null : pen,
                    this.backgroundColor == null ? null : brush);
            }
            
            addNormalAppearance(normal_off, BUTTON_STATE_OFF);
        }
    }
    
    private void changeOnStateName(PdfStdPage page)
        throws IOException, PdfException
    {
        PdfDict appMap;
        HashMap m;

        onState = new PdfName(this.getName());

        appMap = (PdfDict) page.originDoc.reader
            .getObject((PdfObject) apHm.get(new PdfName(PDF_N)));
        if (appMap != null)
        {
            m = (HashMap) appMap.dictMap;
            modifyAppMap(m);
        }

        appMap = (PdfDict) page.originDoc.reader
            .getObject((PdfObject) apHm.get(new PdfName(PDF_R)));
        if (appMap != null)
        {
            m = (HashMap) appMap.dictMap;
            modifyAppMap(m);
        }

        appMap = (PdfDict) page.originDoc.reader
            .getObject((PdfObject) apHm.get(new PdfName(PDF_D)));
        if (appMap != null)
        {
            m = (HashMap) appMap.dictMap;
            modifyAppMap(m);
        }
    }
    
    private void modifyAppMap(HashMap m)
    {
        HashMap clone = (HashMap) m.clone();
        
        for (Iterator iter = clone.keySet().iterator(); iter
            .hasNext();)
        {
            PdfName key = (PdfName) iter.next();
            if (key.getString().equals("Off"))
            {
                continue;
            }
            else
            {
                PdfObject obj = (PdfObject) m.get(key);
                m.remove(key);
                m.put(onState, obj);
            }
        }
    }
}
