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

public class PdfFormPushButton extends PdfFormButtonField
{
    public static final int CAPTION_ON = 0;

    public static final int CAPTION_OFF = 1;

    public static final int CAPTION_BELOW = 2;

    public static final int CAPTION_ABOVE = 3;

    public static final int CAPTION_RIGHTSIDE = 4;

    public static final int CAPTION_LEFTSIDE = 5;

    public static final int CAPTION_OVERLAID = 6;

    String rolloverCaption;

    PdfFormPushButton()
    {
        super(TYPE_PUSHBUTTON);
    }

    public PdfFormPushButton(String name, int fieldFlags)
    {
        super(TYPE_PUSHBUTTON, name, fieldFlags
            | PdfFormButtonField.FLAG_PUSHBUTTON);
    }

    public PdfFormPushButton(String name, String alternateName,
        String mappingName, int fieldFlags)
    {
        super(TYPE_PUSHBUTTON, name, alternateName, mappingName,
            fieldFlags | PdfFormButtonField.FLAG_PUSHBUTTON);
    }

    public PdfFormPushButton(String name, String alternateName,
        String mappingName)
    {
        super(TYPE_PUSHBUTTON, name, alternateName, mappingName,
            PdfFormButtonField.FLAG_PUSHBUTTON);
    }

    public PdfFormPushButton(String name)
    {
        super(TYPE_PUSHBUTTON, name,
            PdfFormButtonField.FLAG_PUSHBUTTON);
    }

    public PdfFormPushButton(PdfRect rect)
    {
        super(TYPE_PUSHBUTTON, PdfFormButtonField.FLAG_PUSHBUTTON);
        setRect(rect);
    }

    public PdfFormPushButton(PdfRect r, String name, int fieldFlags)
    {
        super(TYPE_PUSHBUTTON, r, name, fieldFlags
            | PdfFormButtonField.FLAG_PUSHBUTTON);
    }

    public PdfFormPushButton(PdfRect r, String name)
    {
        super(TYPE_PUSHBUTTON, r, name,
            PdfFormButtonField.FLAG_PUSHBUTTON);
    }

    public PdfFormPushButton(PdfRect r, String name,
        String alternateName, String mappingName, int fieldFlags)
    {
        super(TYPE_PUSHBUTTON, r, name, alternateName, mappingName,
            fieldFlags | PdfFormButtonField.FLAG_PUSHBUTTON);
    }

    public PdfFormPushButton(PdfRect r, String name,
        String alternateName, String mappingName)
    {
        super(TYPE_PUSHBUTTON, r, name, alternateName, mappingName,
            PdfFormButtonField.FLAG_PUSHBUTTON);
    }

    public PdfFormPushButton(String name, int fieldFlags,
        Color borderColor, Color backgroundColor)
    {
        super(TYPE_PUSHBUTTON, name, fieldFlags
            | PdfFormButtonField.FLAG_PUSHBUTTON, borderColor,
            backgroundColor);
    }

    public PdfFormPushButton(String name, String alternateName,
        String mappingName, int fieldFlags, Color borderColor,
        Color backgroundColor)
    {
        super(TYPE_PUSHBUTTON, name, alternateName, mappingName,
            fieldFlags | PdfFormButtonField.FLAG_PUSHBUTTON,
            borderColor, backgroundColor);
    }

    public PdfFormPushButton(String name, String alternateName,
        String mappingName, Color borderColor, Color backgroundColor)
    {
        super(TYPE_PUSHBUTTON, name, alternateName, mappingName,
            PdfFormButtonField.FLAG_PUSHBUTTON, borderColor,
            backgroundColor);
    }

    public PdfFormPushButton(String name, Color borderColor,
        Color backgroundColor)
    {
        super(TYPE_PUSHBUTTON, name,
            PdfFormButtonField.FLAG_PUSHBUTTON, borderColor,
            backgroundColor);
    }

    public PdfFormPushButton(PdfRect r, String name, int fieldFlags,
        Color borderColor, Color backgroundColor)
    {
        super(TYPE_PUSHBUTTON, r, name, fieldFlags
            | PdfFormButtonField.FLAG_PUSHBUTTON, borderColor,
            backgroundColor);
    }

    public PdfFormPushButton(PdfRect r, String name,
        Color borderColor, Color backgroundColor)
    {
        super(TYPE_PUSHBUTTON, r, name,
            PdfFormButtonField.FLAG_PUSHBUTTON, borderColor,
            backgroundColor);
    }

    public PdfFormPushButton(PdfRect r, String name,
        String alternateName, String mappingName, int fieldFlags,
        Color borderColor, Color backgroundColor)
    {
        super(TYPE_PUSHBUTTON, r, name, alternateName, mappingName,
            fieldFlags | PdfFormButtonField.FLAG_PUSHBUTTON,
            borderColor, backgroundColor);
    }

    public PdfFormPushButton(PdfRect r, String name,
        String alternateName, String mappingName, Color borderColor,
        Color backgroundColor)
    {
        super(TYPE_PUSHBUTTON, r, name, alternateName, mappingName,
            PdfFormButtonField.FLAG_PUSHBUTTON, borderColor,
            backgroundColor);
    }

    private HashMap prepareIconFitDict()
    {
        HashMap hm = new HashMap();
        PdfName sw;
        switch (this.scaleEvent)
        {
            case SCALE_NEVER:
                sw = new PdfName(PDF_N);
                break;
            case SCALE_WHEN_ICON_IS_BIGGER:
                sw = new PdfName(PDF_B);
                break;
            case SCALE_WHEN_ICON_IS_SMALLER:
                sw = new PdfName(PDF_S);
                break;
            default:
                sw = new PdfName(PDF_A);
                break;
        }
        hm.put(new PdfName("SW"), sw);
        switch (this.scaleType)
        {
            case SCALE_TYPE_ANAMORPHIC:
                sw = new PdfName(PDF_A);
                break;
            default:
                sw = new PdfName(PDF_P);
                break;
        }
        hm.put(new PdfName(PDF_S), sw);

        ArrayList padding = new ArrayList();
        padding.add(new PdfFloat(iconLeftPadding));
        padding.add(new PdfFloat(iconBottomPadding));
        hm.put(new PdfName(PDF_A), new PdfArray(padding));
        hm.put(new PdfName("FB"), new PdfBoolean(iconFitToRect));

        return hm;
    }

    protected HashMap prepareMKDict()
    {
        HashMap hm = super.prepareMKDict();
        if (this.downCaption != null)
        {
            hm.put(new PdfName(PDF_AC), new PdfTextString(
                downCaption, true));
        }
        if (this.rolloverCaption != null)
        {
            hm.put(new PdfName(PDF_RC), new PdfTextString(
                rolloverCaption, true));
        }
        hm.put(new PdfName(PDF_IF), new PdfDict(
                prepareIconFitDict()));
        hm.put(new PdfName(PDF_TP), new PdfInteger(
            this.captionPosition));

        return hm;
    }

    protected void set(PdfStdPage page, PdfStdDocument d)
        throws IOException, PdfException
    {
        super.set(page, d);
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
                + font.getSize() + " " + PDF_TEXTFONT;

            String da = color + fontName;

            this.dict.dictMap.put(new PdfName(PDF_DA), new PdfString(
                da));
        }

        PdfName ap = new PdfName(PDF_AP);
        if (this.dict.dictMap.containsKey(ap))
        {
            PdfName i = new PdfName(PDF_I);
            PdfName ri = new PdfName(PDF_RI);
            PdfName ix = new PdfName(PDF_IX);
            PdfName normal = new PdfName(PDF_N);
            PdfName rollover = new PdfName(PDF_R);
            PdfName down = new PdfName(PDF_D);
            
            HashMap aphm = (HashMap) ((PdfDict) this.dict.dictMap
                .get(ap)).dictMap;
            HashMap mkhm = (HashMap) ((PdfDict) this.dict.dictMap
                .get(new PdfName(PDF_MK))).dictMap;
            if (aphm.containsKey(normal))
            {
                mkhm.put(i, aphm.get(normal));
            }
            if (aphm.containsKey(rollover))
            {
                mkhm.put(ri, aphm.get(rollover));
            }
            if (aphm.containsKey(down))
            {
                mkhm.put(ix, aphm.get(down));
            }
            
            this.dict.dictMap.remove(ap);
        }
    }

    public synchronized double getIconBottomPadding()
    {
        return iconBottomPadding;
    }

    public synchronized void setIconBottomPadding(
        double iconBottomPadding)
    {
        this.iconBottomPadding = iconBottomPadding;
    }

    public synchronized boolean isIconFitToRect()
    {
        return iconFitToRect;
    }

    public synchronized void setIconFitToRect(boolean iconFitToRect)
    {
        this.iconFitToRect = iconFitToRect;
    }

    public synchronized double getIconLeftPadding()
    {
        return iconLeftPadding;
    }

    public synchronized void setIconLeftPadding(double iconLeftPadding)
    {
        this.iconLeftPadding = iconLeftPadding;
    }

    public synchronized int getScaleEvent()
    {
        return scaleEvent;
    }

    public synchronized void setScaleEvent(int scaleEvent)
    {
        this.scaleEvent = scaleEvent;
    }

    public synchronized int getScaleType()
    {
        return scaleType;
    }

    public synchronized void setScaleType(int scaleType)
    {
        this.scaleType = scaleType;
    }

    public synchronized int getCaptionPosition()
    {
        return captionPosition;
    }

    public synchronized void setCaptionPosition(int captionPosition)
    {
        this.captionPosition = captionPosition;
    }

    public synchronized String getDownCaption()
    {
        return downCaption;
    }

    public synchronized void setDownCaption(String downCaption)
    {
        this.downCaption = downCaption;
    }

    public synchronized String getRolloverCaption()
    {
        return rolloverCaption;
    }

    public synchronized void setRolloverCaption(String rolloverCaption)
    {
        this.rolloverCaption = rolloverCaption;
    }
}
