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

public class PdfFreeTextAnnot extends PdfProAnnot
{
    public static final int ALIGNMENT_LEFT = 0;
    
    public static final int ALIGNMENT_CENTER = 1;
    
    public static final int ALIGNMENT_RIGHT = 2;

    public static final int INTENT_FREETEXT_CALLOUT = 0;
        
    public static final int INTENT_FREETEXT_TYPEWRITER = 1;
    
    private int intent;
    
    private double calloutLine[];

    /*private PdfFont font;

    private Color backgroundColor;*/

    private int alignment;
    
    public Object clone()
    {
        PdfFreeTextAnnot clone = (PdfFreeTextAnnot) super.clone();
        if (this.calloutLine != null)
        {
            clone.calloutLine = new double[calloutLine.length];
            for (int i = 0; i < calloutLine.length; ++i)
            {
                clone.calloutLine[i] = calloutLine[i];
            }
        }
        
        return clone;
    }

    public PdfFreeTextAnnot() throws IOException, PdfException
    {
        /*backgroundColor = Color.WHITE;
        font = PdfFont.create("Arial", PdfFont.PLAIN, 12,
            PdfEncodings.CP1252);*/
        alignment = ALIGNMENT_LEFT;
        this.annotType = ANNOT_TYPE_FREE_TEXT;
    }

    public PdfFreeTextAnnot(PdfRect r, String subject,
        String contents, String title, int flags, Color c,
        int alignment/*, PdfFont f, Color background*/)
        throws IOException, PdfException
    {
        super(r, subject, contents, title, flags, c);
        /*backgroundColor = Color.WHITE;
        font = (PdfFont) f.clone();*/
        alignment = ALIGNMENT_LEFT;
        this.annotType = ANNOT_TYPE_FREE_TEXT;
    }

    public PdfFreeTextAnnot(PdfRect r, String subject, String contents,
        String title) throws IOException, PdfException
    {
        super(r, subject, contents, title);
        /*backgroundColor = Color.WHITE;
        font = PdfFont.create("Arial", PdfFont.PLAIN, 12,
            PdfEncodings.CP1252);*/
        alignment = ALIGNMENT_LEFT;
        this.annotType = ANNOT_TYPE_FREE_TEXT;
    }

    public PdfFreeTextAnnot(PdfRect r, String subject, String contents,
        String title, Color c) throws IOException, PdfException
    {
        super(r, subject, contents, title, c);
        /*backgroundColor = Color.WHITE;
        font = PdfFont.create("Arial", PdfFont.PLAIN, 12,
            PdfEncodings.CP1252);*/
        alignment = ALIGNMENT_LEFT;
        this.annotType = ANNOT_TYPE_FREE_TEXT;
   }
    
    public PdfFreeTextAnnot(PdfRect r, String subject, String contents,
        String title, int flags) throws IOException, PdfException
    {
        super(r, subject, contents, title, flags);
        /*backgroundColor = Color.WHITE;
        font = PdfFont.create("Arial", PdfFont.PLAIN, 12,
            PdfEncodings.CP1252);*/
        alignment = ALIGNMENT_LEFT;
        this.annotType = ANNOT_TYPE_FREE_TEXT;
    }
    
    public PdfFreeTextAnnot(PdfRect r, String subject, String contents,
        String title, int flags, Color c) throws IOException, PdfException
    {
        super(r, subject, contents, title, flags, c);
        /*backgroundColor = Color.WHITE;
        font = PdfFont.create("Arial", PdfFont.PLAIN, 12,
            PdfEncodings.CP1252);*/
        alignment = ALIGNMENT_LEFT;
        this.annotType = ANNOT_TYPE_FREE_TEXT;
    }
    
    public PdfFreeTextAnnot(PdfRect r, int flags) throws IOException,
        PdfException
    {
        super(r, flags);
        /*backgroundColor = Color.WHITE;
        font = PdfFont.create("Arial", PdfFont.PLAIN, 12,
            PdfEncodings.CP1252);*/
        alignment = ALIGNMENT_LEFT;
        this.annotType = ANNOT_TYPE_FREE_TEXT;
    }

    public PdfFreeTextAnnot(PdfRect r, int flags, Color c)
        throws IOException, PdfException
    {
        super(r, flags, c);
        /*backgroundColor = Color.WHITE;
        font = PdfFont.create("Arial", PdfFont.PLAIN, 12,
            PdfEncodings.CP1252);*/
        alignment = ALIGNMENT_LEFT;
        this.annotType = ANNOT_TYPE_FREE_TEXT;
    }

    public PdfFreeTextAnnot(PdfRect r, Color c) throws IOException,
        PdfException
    {
        super(r, c);
        /*backgroundColor = Color.WHITE;
        font = PdfFont.create("Arial", PdfFont.PLAIN, 12,
            PdfEncodings.CP1252);*/
        alignment = ALIGNMENT_LEFT;
        this.annotType = ANNOT_TYPE_FREE_TEXT;
    }
    
    /*private PdfString getDA()
    {
        drawRect of backGround Color
        Other than Tj write all entries from writeText
        StringBuffer sb = new StringBuffer();
        
        sb.append(PdfWriter.formatFloat(backgroundColor
            .getRed() / 255f)
            + Usable.PDF_SP);
        sb.append(PdfWriter.formatFloat(backgroundColor
            .getGreen() / 255f)
            + Usable.PDF_SP);
        sb.append(PdfWriter.formatFloat(backgroundColor
            .getBlue() / 255f)
            + " RG ");

        sb.append(PdfWriter.formatFloat(rect.x) + Usable.PDF_SP
            + PdfWriter.formatFloat(rect.y) + Usable.PDF_SP
            + PdfWriter.formatFloat(rect.width) + Usable.PDF_SP
            + PdfWriter.formatFloat(rect.height) + " re f ");

        if ((font.getStyle() & PdfFont.STROKE_AND_FILL) == PdfFont.STROKE_AND_FILL
            || (font.getStyle() & PdfFont.STROKE) == PdfFont.STROKE)
        {
            sb.append(Usable.PDF_STORE_GS + Usable.PDF_SP);
        }
        String fontName = Usable.PDF_NAMESTART
            + Usable.PDF_FONTNAMEPREFIX + font.getName();

        if ((font.getStyle() & PdfFont.BOLD) == PdfFont.BOLD)
        {
            fontName += 'B';
        }
        if ((font.getStyle() & PdfFont.ITALIC) == PdfFont.ITALIC)
        {
            fontName += 'I';
        }
        fontName += Usable.PDF_SP;

        sb.append(fontName + Integer.toString(font.getSize())
            + Usable.PDF_SP + Usable.PDF_TEXTFONT + Usable.PDF_SP);
        if ((font.getStyle() & PdfFont.STROKE_AND_FILL) == PdfFont.STROKE_AND_FILL)
        {
            sb.append(Usable.PDF_SP
                + PdfWriter.formatFloat(font.getStrokeWidth()) + " w ");
            sb.append(Integer.toString(2) + Usable.PDF_SP + Usable.PDF_TEXTRENDER
                + Usable.PDF_SP);
            sb.append(PdfWriter.formatFloat(font.getStrokeColor()
                .getRed() / 255f)
                + Usable.PDF_SP);
            sb.append(PdfWriter.formatFloat(font.getStrokeColor()
                .getGreen() / 255f)
                + Usable.PDF_SP);
            sb.append(PdfWriter.formatFloat(font.getStrokeColor()
                .getBlue() / 255f)
                + " RG ");
        }
        else if ((font.getStyle() & PdfFont.STROKE) == PdfFont.STROKE)
        {
            sb.append(PdfWriter.formatFloat(font.getStrokeWidth())
                + " w ");
            sb.append(Integer.toString(1) + Usable.PDF_SP + Usable.PDF_TEXTRENDER
                + Usable.PDF_SP);
            sb.append(PdfWriter.formatFloat(font.getStrokeColor()
                .getRed() / 255f)
                + Usable.PDF_SP);
            sb.append(PdfWriter.formatFloat(font.getStrokeColor()
                .getGreen() / 255f)
                + Usable.PDF_SP);
            sb.append(PdfWriter.formatFloat(font.getStrokeColor()
                .getBlue() / 255f)
                + " RG ");
        }
        else
        {
            sb.append(Integer.toString(0) + Usable.PDF_SP + Usable.PDF_TEXTRENDER
                + Usable.PDF_SP);
        }

        sb.append(PdfWriter.formatFloat(font.getColor().getRed() / 255f)
            + Usable.PDF_SP);
        sb.append(PdfWriter
            .formatFloat(font.getColor().getGreen() / 255f)
            + Usable.PDF_SP);
        sb.append(PdfWriter
            .formatFloat(font.getColor().getBlue() / 255f)
            + " rg ");
        
        return new PdfString(sb + "");
    }*/
    
    public synchronized int getAlignment()
    {
        return alignment;
    }

    public synchronized void setAlignment(int alignment)
    {
        if (alignment >= 0 && alignment <= 2)
        {
            this.alignment = alignment;
        }
    }

    public synchronized double[] getCalloutLine()
    {
        return calloutLine;
    }
    
    public synchronized void setCalloutLine(double[] calloutLine)
    {
        this.calloutLine = calloutLine;
    }
    
    public synchronized int getIntent()
    {
        return intent;
    }
    
    public synchronized void setIntent(int intent)
    {
        this.intent = intent;
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
            if (name.equals(PDF_Q))
            {
                unknownAttributes.remove(key);
                setAlignment(((PdfInteger) value).getInt());
            }
            else if (name.equals(PDF_IT))
            {
                unknownAttributes.remove(key);
                String intent = ((PdfName) value).getString();
                if (intent.equals(PDF_FREE_TEXT_TYPEWRITER))
                {
                    setIntent(1);
                }
                else
                {
                    setIntent(0);
                }
            }
            else if (name.equals(PDF_CL))
            {
                if (value instanceof PdfArray)
                {
                    unknownAttributes.remove(key);
                    ArrayList arrPoints = (ArrayList) ((PdfArray) value)
                        .getList();
                    double[] ver = new double[Math.min(6, arrPoints
                        .size())];
                    PdfObject obj = null;
                    double val = 0;

                    for (int j = 0; j < ver.length; j++)
                    {
                        obj = (PdfObject) arrPoints.get(j);
                        if (obj instanceof PdfNumber)
                        {
                            val = ((PdfNumber) obj).getVal();
                            ver[j] = j % 2 == 0 ? val
                                : page.pageHeight - val;
                        }
                    }
                    setCalloutLine(ver);
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
            Usable.PDF_FREETEXTANNOT));
        annotMap.put(new PdfName(Usable.PDF_Q), new PdfInteger(
            getAlignment()));
        
        double pageHeight = p.pageHeight;
        double callout[] = this.getCalloutLine();
        if (callout != null)
        {
            ArrayList arrCallout = new ArrayList();
            if (callout.length == 4 || callout.length == 6)
            {
                double[] newCallout = new double[callout.length];
                for (int i = 0; i < callout.length; i += 2)
                {
                    PdfPoint point = p.updatePageSettings(new PdfPoint(
                        callout[i], callout[i + 1]));
                    newCallout[i] = point.x;
                    newCallout[i + 1] = point.y;
                }
                for (int i = 0; i < newCallout.length; i++)
                {
                    if ((i % 2) > 0)
                    {
                        arrCallout.add(new PdfFloat((float) pageHeight
                            - newCallout[i]));
                    }
                    else
                    {
                        arrCallout.add(new PdfFloat(newCallout[i]));
                    }
                }
                annotMap.put(new PdfName(Usable.PDF_CL), new PdfArray(
                    arrCallout));
            }
            else
            {
                throw new PdfException(
                "Invalid callout points count.");
            }
        }
        
        return this;
    }
}