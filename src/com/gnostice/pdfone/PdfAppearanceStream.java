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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import com.gnostice.pdfone.fonts.PdfFont;
import com.gnostice.pdfone.graphics.PdfBrush;
import com.gnostice.pdfone.graphics.PdfPen;

public class PdfAppearanceStream extends PdfStream implements Usable
{
    PdfStdPage page;
    
    PdfRect rect;
    
    public Object clone()
    {
        PdfAppearanceStream clone = (PdfAppearanceStream) super
            .clone();
        clone.page = (PdfStdPage) this.page.clone();
        clone.rect = (PdfRect) this.rect.clone();
        
        return clone;
    }

    PdfAppearanceStream(PdfDict d, ByteBuffer bb)
    {
        super(d, bb);
        page = new PdfPage();
    }

    public PdfAppearanceStream(PdfRect rect, int measurementUnit)
    {
        super();
        page = new PdfPage(rect.width, rect.height, measurementUnit);
        this.rect = (PdfRect) rect.clone();
    }

    public PdfAppearanceStream(PdfRect rect)
    {
        this(rect, PdfMeasurement.MU_POINTS);
    }

    void setDict(PdfDict dict)
    {
        this.streamDict = dict;
    }
    
    protected void set(PdfStdDocument d) throws IOException,
        PdfException
    {
        if (page.fontDict != null)
        {
            page.setFontDict(d);
        }
        if (page.xObjDict != null)
        {
            page.setXObjDict(d);
        }
        
        this.objNumber = d.objectRun++;
        
        double width = 0, height = 0;
        if (this.rect != null)
        {
            width = PdfMeasurement.convertToPdfUnit(
                page.measurementUnit, this.rect.width);
            height = PdfMeasurement.convertToPdfUnit(
                page.measurementUnit, this.rect.height);
        }
        
        ByteBuffer bb = page.contentStream != null ? ((ByteBuffer) ByteBuffer
            .wrap(page.contentStream.getBuffer()).limit(
                page.contentStream.size())).slice()
            : ByteBuffer.allocate(0);

        setBuffer(bb);

        HashMap hm = new HashMap();
        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_XOBJECT));
        hm.put(new PdfName(Usable.PDF_SUBTYPE), new PdfName(
            Usable.PDF_FORM));
        hm.put(new PdfName(Usable.PDF_BBOX), new PdfArray(
            new double[] { 0, 0, width, height }));

        HashMap resources = new HashMap();
        page.prepareResources(d, resources);
        hm.put(new PdfName(PDF_RESOURCES), new PdfDict(resources));
        
        setDict(new PdfDict(hm));
    }
    
    public synchronized void drawLine(PdfPoint start, PdfPoint end,
        PdfPen pen) throws IOException
    {
        page.setPen(pen);
        page.drawLine(start, end);
    }

    public synchronized void drawEllipse(PdfPoint p1, PdfPoint p2,
        PdfPen pen, PdfBrush brush) throws IOException
    {
        page.setPen(pen);
        page.brush = brush;
        page.drawEllipse(p1, p2, brush != null, pen != null);
    }
        
    public synchronized void drawCircle(PdfPoint center, double radius,
        PdfPen pen, PdfBrush brush) throws IOException
    {
        page.pen = pen;
        page.brush = brush;
        page.drawCircle(center, radius, brush != null, pen != null);
    }

    public synchronized void drawPolyline(double xPoints[],
        double yPoints[], int nPoints, PdfPen pen)
        throws IOException, PdfException
    {
        page.pen = pen;
        page.drawPolyline(xPoints, yPoints, nPoints);
    }

    public synchronized void drawPolygon(double xPoints[],
        double yPoints[], int nPoints, PdfPen pen, PdfBrush brush)
        throws IOException, PdfException
    {
        page.pen = pen;
        page.brush = brush;
        page.drawPolygon(xPoints, yPoints, nPoints, brush != null,
            pen != null);
    }

    public synchronized void drawRect(PdfRect rect, PdfPen pen,
        PdfBrush brush) throws IOException
    {
        page.pen = pen;
        page.brush = brush;
        page.drawRect(rect.x, rect.y, rect.width, rect.height,
            brush != null, pen != null);
    }

    public synchronized void drawImage(PdfImage img, PdfRect rect)
        throws IOException, PdfException
    {
        page.drawImage(img, rect);
    }

    public synchronized void drawImage(PdfImage img)
        throws IOException, PdfException
    {
        page.drawImage(img, new PdfRect(0, 0, PdfMeasurement
            .convertToMeasurementUnit(page.measurementUnit,
                page.pageWidth), PdfMeasurement
            .convertToMeasurementUnit(page.measurementUnit,
                page.pageHeight)));
    }

    public synchronized void writeText(String text, PdfFont font,
        PdfRect rect, PdfTextFormatter tf) throws IOException,
        PdfException
    {
        PdfTextFormatter prevTF = page.getTextFormatter();
        page.setTextFormatter(tf);
        page.writeText(text, font, rect);
        page.setTextFormatter(prevTF);
    }

    public synchronized void writeText(String text, PdfFont font,
        PdfRect rect, PdfTextFormatter tf, int measurementUnit)
        throws IOException, PdfException
    {
        page.writeText(text, rect, font, tf, measurementUnit, true);
    }
}
