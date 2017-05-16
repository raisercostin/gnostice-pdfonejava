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

public class PdfFormCheckBox extends PdfFormButtonField
{
    PdfFormCheckBox()
    {
        super(TYPE_CHECKGROUP);
    }

    public PdfFormCheckBox(String name, int fieldFlags)
    {
        super(TYPE_CHECKGROUP, name, fieldFlags
            | PdfFormButtonField.FLAG_CHECKBOX);
    }

    public PdfFormCheckBox(String name, String alternateName,
        String mappingName, int fieldFlags)
    {
        super(TYPE_CHECKGROUP, name, alternateName, mappingName,
            fieldFlags | PdfFormButtonField.FLAG_CHECKBOX);
    }

    public PdfFormCheckBox(String name, String alternateName,
        String mappingName)
    {
        super(TYPE_CHECKGROUP, name, alternateName, mappingName,
            PdfFormButtonField.FLAG_CHECKBOX);
    }

    public PdfFormCheckBox(String name)
    {
        super(TYPE_CHECKGROUP, name, PdfFormButtonField.FLAG_CHECKBOX);
    }

    public PdfFormCheckBox(PdfRect rect)
    {
        super(TYPE_CHECKGROUP, PdfFormButtonField.FLAG_CHECKBOX);
        setRect(rect);
    }

    public PdfFormCheckBox(PdfRect r, String name, int fieldFlags)
    {
        super(TYPE_CHECKGROUP, r, name, fieldFlags
            | PdfFormButtonField.FLAG_CHECKBOX);
    }

    public PdfFormCheckBox(PdfRect r, String name)
    {
        super(TYPE_CHECKGROUP, r, name,
            PdfFormButtonField.FLAG_CHECKBOX);
    }

    public PdfFormCheckBox(PdfRect r, String name,
        String alternateName, String mappingName, int fieldFlags)
    {
        super(TYPE_CHECKGROUP, r, name, alternateName, mappingName,
            fieldFlags | PdfFormButtonField.FLAG_CHECKBOX);
    }

    public PdfFormCheckBox(PdfRect r, String name,
        String alternateName, String mappingName)
    {
        super(TYPE_CHECKGROUP, r, name, alternateName, mappingName,
            PdfFormButtonField.FLAG_CHECKBOX);
    }

    public PdfFormCheckBox(String name, int fieldFlags,
        Color borderColor, Color backgroundColor)
    {
        super(TYPE_CHECKGROUP, name, fieldFlags
            | PdfFormButtonField.FLAG_CHECKBOX, borderColor,
            backgroundColor);
    }

    public PdfFormCheckBox(String name, String alternateName,
        String mappingName, int fieldFlags, Color borderColor,
        Color backgroundColor)
    {
        super(TYPE_CHECKGROUP, name, alternateName, mappingName,
            fieldFlags | PdfFormButtonField.FLAG_CHECKBOX,
            borderColor, backgroundColor);
    }

    public PdfFormCheckBox(String name, String alternateName,
        String mappingName, Color borderColor, Color backgroundColor)
    {
        super(TYPE_CHECKGROUP, name, alternateName, mappingName,
            PdfFormButtonField.FLAG_CHECKBOX, borderColor,
            backgroundColor);
    }

    public PdfFormCheckBox(String name, Color borderColor,
        Color backgroundColor)
    {
        super(TYPE_CHECKGROUP, name,
            PdfFormButtonField.FLAG_CHECKBOX, borderColor,
            backgroundColor);
    }

    public PdfFormCheckBox(PdfRect r, String name, int fieldFlags,
        Color borderColor, Color backgroundColor)
    {
        super(TYPE_CHECKGROUP, r, name, fieldFlags
            | PdfFormButtonField.FLAG_CHECKBOX, borderColor,
            backgroundColor);
    }

    public PdfFormCheckBox(PdfRect r, String name, Color borderColor,
        Color backgroundColor)
    {
        super(TYPE_CHECKGROUP, r, name,
            PdfFormButtonField.FLAG_CHECKBOX, borderColor,
            backgroundColor);
    }

    public PdfFormCheckBox(PdfRect r, String name,
        String alternateName, String mappingName, int fieldFlags,
        Color borderColor, Color backgroundColor)
    {
        super(TYPE_CHECKGROUP, r, name, alternateName, mappingName,
            fieldFlags | PdfFormButtonField.FLAG_CHECKBOX,
            borderColor, backgroundColor);
    }

    public PdfFormCheckBox(PdfRect r, String name,
        String alternateName, String mappingName, Color borderColor,
        Color backgroundColor)
    {
        super(TYPE_CHECKGROUP, r, name, alternateName, mappingName,
            PdfFormButtonField.FLAG_CHECKBOX, borderColor,
            backgroundColor);
    }

    public synchronized List getChildList()
    {
        ArrayList retList = new ArrayList();
        retList.add(this);
        
        return retList;
    }

    public synchronized void setState(int appearanceState)
    {
        if (this.parent == null
            || !(this instanceof PdfFormCheckBox))
        {
            this.appearanceState = appearanceState;
        }
        else
        {
            PdfFormField sibling;
            PdfFormCheckBox scb;
            for (int i = 0, limit = parent.kids.size(); i < limit; ++i)
            {
                sibling = (PdfFormField) parent.kids.get(i);
                if ( !(sibling instanceof PdfFormCheckBox))
                {
                    continue;
                }
                scb = (PdfFormCheckBox) sibling;
                if (scb.getValue().equals(
                    ((PdfFormCheckBox) this).getValue()))
                {
                    scb.appearanceState = appearanceState;
                }
                else
                {
                    scb.appearanceState = appearanceState & 0;
                }
            }
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
            double radius = Math.min(r.width, r.height) / 2
                - pen.width;
            PdfFont font = PdfFont.create("ZapfDingbats",
                (int) radius, PdfEncodings.CP1252);

            PdfAppearanceStream normal_on = new PdfAppearanceStream(r);
            if (this.borderColor != null
                || this.backgroundColor != null)
            {
                normal_on.drawRect(r,
                    this.borderColor == null ? null : pen,
                    this.backgroundColor == null ? null : brush);
            }
            
            double textWidth = font.getWidth('4',
                PdfMeasurement.MU_POINTS);
            double textHeight = font.getHeight();
            normal_on.writeText("4", font, new PdfRect(centre.x
                - textWidth / 2, centre.y - textHeight / 1.6,
                textWidth, textHeight), new PdfTextFormatter());

            addNormalAppearance(normal_on, BUTTON_STATE_ON);

            PdfAppearanceStream normal_off = new PdfAppearanceStream(r);
            if (this.borderColor != null
                || this.backgroundColor != null)
            {
                normal_off.drawRect(r,
                    this.borderColor == null ? null : pen,
                    this.backgroundColor == null ? null : brush);
            }

            addNormalAppearance(normal_off, BUTTON_STATE_OFF);
        }
    }
}
