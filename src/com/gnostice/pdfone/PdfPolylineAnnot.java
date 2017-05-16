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

public class PdfPolylineAnnot extends PdfProAnnot
{
    public static final int LINEENDSTYLE_SQUARE = 0;
    
    public static final int LINEENDSTYLE_CIRCLE = 1;
    
    public static final int LINEENDSTYLE_DIAMOND = 2;
    
    public static final int LINEENDSTYLE_OPEN_ARROW = 3;
    
    public static final int LINEENDSTYLE_CLOSED_ARROW = 4;
    
    public static final int LINEENDSTYLE_NONE = 5;
    
    public static final int LINEENDSTYLE_BUTT = 6;
    
    public static final int LINEENDSTYLE_ROPEN_ARROW = 7;
    
    public static final int LINEENDSTYLE_RCLOSED_ARROW = 8;
    
    public static final int LINEENDSTYLE_SLASH = 9;

    private int lineStartStyle;
    
    private int lineEndStyle;

    Color interiorColor;

    double vertices[];

    public Object clone()
    {
        PdfPolylineAnnot clone = (PdfPolylineAnnot) super.clone();
        if (this.interiorColor != null)
        {
            clone.interiorColor = new Color(interiorColor.getRed(),
                interiorColor.getGreen(), interiorColor.getBlue());
        }
        if (this.vertices != null)
        {
            clone.vertices = new double[vertices.length];
            System.arraycopy(vertices, 0, clone.vertices, 0,
                vertices.length);
        }
        
        return clone;
    }

    public PdfPolylineAnnot()
    {
        interiorColor = Color.WHITE;
        this.annotType = ANNOT_TYPE_POLYLINE;
    }

    public PdfPolylineAnnot(PdfRect r, String subject,
        String contents, String title, int flags, Color c,
        int lineStartStyle, int lineEndStyle, Color interior,
        double[] vertices)
    {
        super(r, subject, contents, title, flags, c);
        this.lineStartStyle = lineStartStyle;
        this.lineEndStyle = lineEndStyle;
        this.interiorColor = interior;
        this.vertices = vertices; 
        this.annotType = ANNOT_TYPE_POLYLINE;
    }

    public PdfPolylineAnnot(PdfRect r, String subject,
        String contents, String title)
    {
        super(r, subject, contents, title);
        interiorColor = Color.WHITE;
        this.annotType = ANNOT_TYPE_POLYLINE;
    }

    public PdfPolylineAnnot(PdfRect r, String subject, String contents,
        String title, Color c)
    {
        super(r, subject, contents, title, c);
        interiorColor = Color.WHITE;
        this.annotType = ANNOT_TYPE_POLYLINE;
    }
    
    public PdfPolylineAnnot(PdfRect r, String subject, String contents,
        String title, int flags)
    {
        super(r, subject, contents, title, flags);
        interiorColor = Color.WHITE;
        this.annotType = ANNOT_TYPE_POLYLINE;
    }
    
    public PdfPolylineAnnot(PdfRect r, String subject, String contents,
        String title, int flags, Color c)
    {
        super(r, subject, contents, title, flags, c);
        interiorColor = Color.WHITE;
        this.annotType = ANNOT_TYPE_POLYLINE;
    }
    
    public PdfPolylineAnnot(PdfRect r, int flags)
    {
        super(r, flags);
        interiorColor = Color.WHITE;
        this.annotType = ANNOT_TYPE_POLYLINE;
    }
    
    public PdfPolylineAnnot(PdfRect r, int flags, Color c)
    {
        super(r, flags, c);
        interiorColor = Color.WHITE;
        this.annotType = ANNOT_TYPE_POLYLINE;
    }
    
    public PdfPolylineAnnot(PdfRect r, Color c)
    {
        super(r, c);
        interiorColor = Color.WHITE;
        this.annotType = ANNOT_TYPE_POLYLINE;
    }
    
    public synchronized Color getInteriorColor()
    {
        return interiorColor;
    }

    public synchronized void setInteriorColor(Color interiorColor)
    {
        this.interiorColor = interiorColor;
    }

    public synchronized double[] getVertices()
    {
        return vertices;
    }

    public synchronized void setVertices(double[] vertices)
    {
        this.vertices = vertices;
    }

    public synchronized void setVertices(double[] vertices,
        int measurementUnit)
    {
        this.vertices = new double[vertices.length];
        for (int i = 0; i < vertices.length; ++i)
        {
            this.vertices[i] = PdfMeasurement.convertToPdfUnit(
                measurementUnit, vertices[i]);
        }
    }

    public final synchronized int getLineStartStyle()
    {
        return lineStartStyle;
    }

    public final synchronized void setLineStartStyle(int lineStartStyle)
    {
        this.lineStartStyle = lineStartStyle;
    }

    public final synchronized int getLineEndStyle()
    {
        return lineEndStyle;
    }

    public final synchronized void setLineEndStyle(int lineEndStyle)
    {
        this.lineEndStyle = lineEndStyle;
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
            if (name.equals(PDF_VERTICES))
            {
                if (value instanceof PdfArray)
                {
                    unknownAttributes.remove(key);
                    ArrayList arrPoints = (ArrayList) ((PdfArray) value)
                        .getList();
                    int size = arrPoints.size();
                    PdfObject obj = null;
                    double val = 0;
                    double ver[] = new double[size];

                    for (int j = 0; j < size; ++j)
                    {
                        obj = (PdfObject) arrPoints.get(j);
                        if (obj instanceof PdfNumber)
                        {
                            val = ((PdfNumber) obj).getVal();
                            ver[j] = j % 2 == 0 ? val
                                : page.pageHeight - val;
                        }
                    }
                    setVertices(ver);
                }
            }
            else if (name.equals(PDF_IC))
            {
                unknownAttributes.remove(key);
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
                setInteriorColor(col);
            }
            else if (name.equals(PDF_LE))
            {
                unknownAttributes.remove(key);
                ArrayList arrEndStyle = (ArrayList) ((PdfArray) value)
                    .getList();
                String startS = ((PdfName) arrEndStyle.get(0))
                    .getString();
                String endS = ((PdfName) arrEndStyle.get(1))
                    .getString();
                if (startS.equals("Square"))
                {
                    setLineStartStyle(0);
                }
                else if (startS.equals("Circle"))
                {
                    setLineStartStyle(1);
                }
                else if (startS.equals("Diamond"))
                {
                    setLineStartStyle(2);
                }
                else if (startS.equals("OpenArrow"))
                {
                    setLineStartStyle(3);
                }
                else if (startS.equals("CloseArrow"))
                {
                    setLineStartStyle(4);
                }
                else if (startS.equals("None"))
                {
                    setLineStartStyle(5);
                }
                else if (startS.equals("Butt"))
                {
                    setLineStartStyle(6);
                }
                else if (startS.equals("ROpenArrow"))
                {
                    setLineStartStyle(7);
                }
                else if (startS.equals("RClosedArrow"))
                {
                    setLineStartStyle(8);
                }
                else if (startS.equals("Slash"))
                {
                    setLineStartStyle(9);
                }
                else
                {
                    setLineStartStyle(5);
                }

                if (endS.equals("Square"))
                {
                    setLineEndStyle(0);
                }
                else if (endS.equals("Circle"))
                {
                    setLineEndStyle(1);
                }
                else if (endS.equals("Diamond"))
                {
                    setLineEndStyle(2);
                }
                else if (endS.equals("OpenArrow"))
                {
                    setLineEndStyle(3);
                }
                else if (endS.equals("CloseArrow"))
                {
                    setLineEndStyle(4);
                }
                else if (endS.equals("None"))
                {
                    setLineEndStyle(5);
                }
                else if (endS.equals("Butt"))
                {
                    setLineEndStyle(6);
                }
                else if (endS.equals("ROpenArrow"))
                {
                    setLineEndStyle(7);
                }
                else if (endS.equals("RClosedArrow"))
                {
                    setLineEndStyle(8);
                }
                else if (endS.equals("Slash"))
                {
                    setLineEndStyle(9);
                }
                else
                {
                    setLineEndStyle(5);
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
        double[] prevVertices = this.vertices;
        super.encode(p);
        
        HashMap annotMap = (HashMap)dict.getMap();
        annotMap.put(new PdfName(Usable.PDF_SUBTYPE), new PdfName(
            Usable.PDF_POLYLINEANNOT));

        ArrayList arrIntColor = new ArrayList(4);
        arrIntColor.add(new PdfFloat(interiorColor.getRed() / 255f));
        arrIntColor.add(new PdfFloat(interiorColor.getGreen() / 255f));
        arrIntColor.add(new PdfFloat(interiorColor.getBlue() / 255f));
        annotMap.put(new PdfName(Usable.PDF_IC), new PdfArray(
            arrIntColor));

        ArrayList arrLineEndStyle = new ArrayList();
        String lineStartStyle = null;
        switch (this.getLineStartStyle())
        {
            case PdfPolylineAnnot.LINEENDSTYLE_SQUARE:
                lineStartStyle = "Square";
                break;

            case PdfPolylineAnnot.LINEENDSTYLE_CIRCLE:
                lineStartStyle = "Circle";
                break;

            case PdfPolylineAnnot.LINEENDSTYLE_DIAMOND:
                lineStartStyle = "Diamond";
                break;

            case PdfPolylineAnnot.LINEENDSTYLE_OPEN_ARROW:
                lineStartStyle = "OpenArrow";
                break;

            case PdfPolylineAnnot.LINEENDSTYLE_CLOSED_ARROW:
                lineStartStyle = "ClosedArrow";
                break;

            case PdfPolylineAnnot.LINEENDSTYLE_NONE:
                lineStartStyle = "None";
                break;

            case PdfPolylineAnnot.LINEENDSTYLE_BUTT:
                lineStartStyle = "Butt";
                break;

            case PdfPolylineAnnot.LINEENDSTYLE_ROPEN_ARROW:
                lineStartStyle = "ROpenArrow";
                break;

            case PdfPolylineAnnot.LINEENDSTYLE_RCLOSED_ARROW:
                lineStartStyle = "RClosedArrow";
                break;

            default:
                lineStartStyle = "None";
                break;
        }
        arrLineEndStyle.add(new PdfName(lineStartStyle));

        String lineEndStyle;
        switch (this.getLineEndStyle())
        {
            case PdfPolylineAnnot.LINEENDSTYLE_SQUARE:
                lineEndStyle = "Square";
                break;

            case PdfPolylineAnnot.LINEENDSTYLE_CIRCLE:
                lineEndStyle = "Circle";
                break;

            case PdfPolylineAnnot.LINEENDSTYLE_DIAMOND:
                lineEndStyle = "Diamond";
                break;

            case PdfPolylineAnnot.LINEENDSTYLE_OPEN_ARROW:
                lineEndStyle = "OpenArrow";
                break;

            case PdfPolylineAnnot.LINEENDSTYLE_CLOSED_ARROW:
                lineEndStyle = "ClosedArrow";
                break;

            case PdfPolylineAnnot.LINEENDSTYLE_NONE:
                lineEndStyle = "None";
                break;

            case PdfPolylineAnnot.LINEENDSTYLE_BUTT:
                lineEndStyle = "Butt";
                break;

            case PdfPolylineAnnot.LINEENDSTYLE_ROPEN_ARROW:
                lineEndStyle = "ROpenArrow";
                break;

            case PdfPolylineAnnot.LINEENDSTYLE_RCLOSED_ARROW:
                lineEndStyle = "RClosedArrow";
                break;

            default:
                lineEndStyle = "None";
        }
        arrLineEndStyle.add(new PdfName(lineEndStyle));
        annotMap.put(new PdfName(Usable.PDF_LE), new PdfArray(
            arrLineEndStyle));
        
        double pageHeight = p.pageHeight;
        if (this.vertices != null)
        {
            updateVertices(p);
            ArrayList arrVertices = new ArrayList();
            double ver[] = this.getVertices();
            for (int i = 0; i < ver.length; i++)
            {
                ver[i] = (float) PdfMeasurement.convertToPdfUnit(
                    p.measurementUnit, ver[i]);
            }

            for (int i = 0; i < ver.length; i++)
            {
                if ((i % 2) > 0)
                {
                    arrVertices.add(new PdfFloat((float) pageHeight
                        - ver[i]));
                }
                else
                {
                    arrVertices.add(new PdfFloat(ver[i]));
                }
            }
            annotMap.put(new PdfName(Usable.PDF_VERTICES),
                new PdfArray(arrVertices));
        }

        this.vertices = prevVertices;
        
        return this;
    }

    protected void set(PdfStdDocument originDoc, PdfStdDocument d)
        throws IOException, PdfException
    {
        super.set(originDoc, d);
        PdfObject name = new PdfName(Usable.PDF_LE);
        HashMap annotMap = (HashMap)dict.getMap();
        String version = d.getVersion();
        if (version.equals(PdfDocument.VERSION_1_5)
            || version.equals(PdfDocument.VERSION_1_6))
        {
            if (annotMap.containsKey(name))
            {
                ArrayList l = (ArrayList) ((PdfArray) annotMap
                    .get(name)).getList();
                if (this.getLineStartStyle() == PdfLineAnnot.LINEENDSTYLE_BUTT)
                {
                    l.set(0, new PdfName("Butt"));
                }
                else if (this.getLineStartStyle() == PdfLineAnnot.LINEENDSTYLE_ROPEN_ARROW)
                {
                    l.set(0, new PdfName("ROpenArrow"));
                }
                else if (this.getLineStartStyle() == PdfLineAnnot.LINEENDSTYLE_RCLOSED_ARROW)
                {
                    l.set(0, new PdfName("RClosedArrow"));
                }
                if (this.getLineEndStyle() == PdfLineAnnot.LINEENDSTYLE_BUTT)
                {
                    l.set(0, new PdfName("Butt"));
                }
                else if (this.getLineEndStyle() == PdfLineAnnot.LINEENDSTYLE_ROPEN_ARROW)
                {
                    l.set(0, new PdfName("ROpenArrow"));
                }
                else if (this.getLineEndStyle() == PdfLineAnnot.LINEENDSTYLE_RCLOSED_ARROW)
                {
                    l.set(0, new PdfName("RClosedArrow"));
                }
            }
        }
        if (version.equals(PdfDocument.VERSION_1_6))
        {
            if (annotMap.containsKey(name))
            {
                ArrayList l = (ArrayList) ((PdfArray) annotMap
                    .get(name)).getList();
                if (this.getLineStartStyle() == PdfLineAnnot.LINEENDSTYLE_SLASH)
                {
                    l.set(0, new PdfName("Slash"));
                }
                if (this.getLineEndStyle() == PdfLineAnnot.LINEENDSTYLE_SLASH)
                {
                    l.set(1, new PdfName("Slash"));
                }
            }
        }
    }
    
    void updateVertices(PdfStdPage p) throws PdfException
    {
        if (vertices.length % 2 != 0)
        {
            throw new PdfException(
                "Invalid points count for vertices.");
        }
        double[] newVertices = new double[vertices.length];
        PdfPoint point = null;
        for (int i = 0; i < vertices.length; i += 2)
        {
            point = p.updatePageSettings(new PdfPoint(vertices[i],
                vertices[i + 1]));
            newVertices[i] = point.x;
            newVertices[i + 1] = point.y;
        }
        
        this.vertices = newVertices;
    }
}