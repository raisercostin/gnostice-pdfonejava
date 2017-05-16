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

public class PdfLineAnnot extends PdfProAnnot
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
    
    private double[] leaderLineArray;

    private double x1;
    
    private double x2;
    
    private double y1;
    
    private double y2;

    private Color interiorColor;

    public Object clone()
    {
        PdfLineAnnot clone = (PdfLineAnnot) super.clone();
        if (this.interiorColor != null)
        {
            clone.interiorColor = new Color(this.interiorColor
                .getRed(), this.interiorColor.getGreen(),
                this.interiorColor.getBlue());
        }
        
        if (this.leaderLineArray != null)
        {
            clone.leaderLineArray = new double[this.leaderLineArray.length];
            for (int i = 0; i < this.leaderLineArray.length; ++i)
            {
                clone.leaderLineArray[i] = this.leaderLineArray[i];
            }
        }
        
        return super.clone();
    }

    public PdfLineAnnot()
    {
        interiorColor = Color.black;
        lineStartStyle = LINEENDSTYLE_NONE;
        lineEndStyle = LINEENDSTYLE_NONE;
        this.borderWidth = 1;
        this.annotType = ANNOT_TYPE_LINE;
    }
    
    public PdfLineAnnot(PdfRect r, String subject, String contents,
        String title, int flags, Color c, int lineStartStyle,
        int lineEndStyle, Color interior)
    {
        super(r, subject, contents, title, flags, c);
        this.interiorColor = interior;
        this.lineStartStyle = lineStartStyle;
        this.lineEndStyle = lineEndStyle;
        this.borderWidth = 1;
        this.annotType = ANNOT_TYPE_LINE;
    }

    public PdfLineAnnot(PdfRect r, String subject, String contents,
        String title)
    {
        super(r, subject, contents, title);
        interiorColor = Color.black;
        lineStartStyle = LINEENDSTYLE_NONE;
        lineEndStyle = LINEENDSTYLE_NONE;
        this.borderWidth = 1;
        this.annotType = ANNOT_TYPE_LINE;
    }

    public PdfLineAnnot(PdfRect r, String subject, String contents,
        String title, Color c)
    {
        super(r, subject, contents, title, c);
        interiorColor = Color.black;
        lineStartStyle = LINEENDSTYLE_NONE;
        lineEndStyle = LINEENDSTYLE_NONE;
        this.borderWidth = 1;
        this.annotType = ANNOT_TYPE_LINE;
    }
    
    public PdfLineAnnot(PdfRect r, String subject, String contents,
        String title, int flags)
    {
        super(r, subject, contents, title, flags);
        interiorColor = Color.black;
        lineStartStyle = LINEENDSTYLE_NONE;
        lineEndStyle = LINEENDSTYLE_NONE;
        this.borderWidth = 1;
        this.annotType = ANNOT_TYPE_LINE;
    }
    
    public PdfLineAnnot(PdfRect r, String subject, String contents,
        String title, int flags, Color c)
    {
        super(r, subject, contents, title, flags, c);
        interiorColor = Color.black;
        lineStartStyle = LINEENDSTYLE_NONE;
        lineEndStyle = LINEENDSTYLE_NONE;
        this.borderWidth = 1;
        this.annotType = ANNOT_TYPE_LINE;
    }
    
    public PdfLineAnnot(PdfRect r, int flags)
    {
        super(r, flags);
        interiorColor = Color.black;
        lineStartStyle = LINEENDSTYLE_NONE;
        lineEndStyle = LINEENDSTYLE_NONE;
        this.borderWidth = 1;
        this.annotType = ANNOT_TYPE_LINE;
    }
    
    public PdfLineAnnot(PdfRect r, int flags, Color c)
    {
        super(r, flags, c);
        interiorColor = Color.black;
        lineStartStyle = LINEENDSTYLE_NONE;
        lineEndStyle = LINEENDSTYLE_NONE;
        this.borderWidth = 1;
        this.annotType = ANNOT_TYPE_LINE;
    }
    
    public PdfLineAnnot(PdfRect r, Color c)
    {
        super(r, c);
        interiorColor = Color.black;
        lineStartStyle = LINEENDSTYLE_NONE;
        lineEndStyle = LINEENDSTYLE_NONE;
        this.borderWidth = 1;
        this.annotType = ANNOT_TYPE_LINE;
    }
    
    public synchronized void setPoints(double[] points)
        throws PdfException
    {
        if (points.length < 4)
        {
            throw new PdfException("Illegal Argument to setEndPoints"
                + " [array.length < 4].");
        }
        this.x1 = points[0];
        this.y1 = points[1];
        this.x2 = points[2];
        this.y2 = points[3];
    }
    
    public synchronized void setPoints(double[] points,
        int measurementUnit) throws PdfException
    {
        if (points.length < 4)
        {
            throw new PdfException("Illegal Argument to setEndPoints"
                + " [array.length < 4].");
        }
        this.x1 = PdfMeasurement.convertToPdfUnit(measurementUnit,
            points[0]);
        this.y1 = PdfMeasurement.convertToPdfUnit(measurementUnit,
            points[1]);
        this.x2 = PdfMeasurement.convertToPdfUnit(measurementUnit,
            points[2]);
        this.y2 = PdfMeasurement.convertToPdfUnit(measurementUnit,
            points[3]);
    }

    public synchronized int getLineStartStyle()
    {
        return lineStartStyle;
    }

    public synchronized void setLineStartStyle(int lineStartStyle)
    {
        this.lineStartStyle = lineStartStyle;
    }

    public synchronized int getLineEndStyle()
    {
        return lineEndStyle;
    }

    public synchronized void setLineEndStyle(int lineEndStyle)
    {
        this.lineEndStyle = lineEndStyle;
    }

    public synchronized double getX1()
    {
        return x1;
    }

    public synchronized void setX1(double x1)
    {
        this.x1 = x1;
    }

    public synchronized double getX2()
    {
        return x2;
    }

    public synchronized void setX2(double x2)
    {
        this.x2 = x2;
    }

    public synchronized double getY1()
    {
        return y1;
    }

    public synchronized void setY1(double y1)
    {
        this.y1 = y1;
    }

    public synchronized double getY2()
    {
        return y2;
    }

    public synchronized void setY2(double y2)
    {
        this.y2 = y2;
    }

    public synchronized double[] getLeaderLineArray()
    {
        return leaderLineArray;
    }

    public synchronized void setLeaderLineArray(
        double[] leaderLineArray) throws PdfException
    {
        if (leaderLineArray.length < 2)
        {
            throw new PdfException(
                "Illegal Argument to setLeaderLineArray" +
                " [array.length < 2].");
        }
        this.leaderLineArray = leaderLineArray;
    }

    public synchronized String getCaption()
    {
        return this.contents;
    }
    
    public synchronized void setCaption(String caption)
    {
        this.contents = caption;
    }
    
    public synchronized Color getInteriorColor()
    {
        return interiorColor;
    }

    public synchronized void setInteriorColor(Color interiorColor)
    {
        this.interiorColor = interiorColor;
    }

    void applyPropertiesFrom(PdfDict annotDict, PdfStdPage page)
        throws IOException, PdfException
    {
        super.applyPropertiesFrom(annotDict, page);
        HashMap annotMap = (HashMap) annotDict.getMap();
        Iterator iter = annotMap.keySet().iterator();
        String name;
        double[] lineExtn = null;
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
            if (name.equals(PDF_L))
            {
                ArrayList arrPoints = (ArrayList) ((PdfArray) value)
                    .getList();
                PdfObject obj = (PdfObject) arrPoints.get(0);
                if (obj instanceof PdfNumber)
                {
                    unknownAttributes.remove(key);
                    setX1(((PdfNumber) obj).getVal());
                }

                obj = (PdfObject) arrPoints.get(1);
                if (obj instanceof PdfNumber)
                {
                    unknownAttributes.remove(key);
                    setY1(page.pageHeight
                        - ((PdfNumber) obj).getVal());
                }

                obj = (PdfObject) arrPoints.get(2);
                if (obj instanceof PdfNumber)
                {
                    unknownAttributes.remove(key);
                    setX2(((PdfNumber) obj).getVal());
                }

                obj = (PdfObject) arrPoints.get(3);
                if (obj instanceof PdfNumber)
                {
                    unknownAttributes.remove(key);
                    setY2(page.pageHeight
                        - ((PdfNumber) obj).getVal());
                }
            }
            else if (name.equals(PDF_IC))
            {
                ArrayList arrColor = (ArrayList) ((PdfArray) value)
                    .getList();
                double r = 0, g = 0, b = 0;
                unknownAttributes.remove(key);
                
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
                ArrayList arrEndStyle = (ArrayList) ((PdfArray) value)
                    .getList();
                String startS = ((PdfName) arrEndStyle.get(0))
                    .getString();
                String endS = ((PdfName) arrEndStyle.get(1))
                    .getString();
                unknownAttributes.remove(key);
                
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
            else if (name.equals("LLE"))
            {
                if (value instanceof PdfNumber)
                {
                    unknownAttributes.remove(key);
                    if (lineExtn == null)
                        lineExtn = new double[2];
                    lineExtn[1] = ((PdfNumber) value).getVal();
                }
            }
            else if (name.equals("LL"))
            {
                if (value instanceof PdfNumber)
                {
                    unknownAttributes.remove(key);
                    if (lineExtn == null)
                        lineExtn = new double[2];
                    lineExtn[0] = ((PdfNumber) value).getVal();
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
        if (lineExtn != null)
        {
            setLeaderLineArray(lineExtn);
        }
    }

    protected void set(PdfStdDocument originDoc, PdfStdDocument d)
        throws IOException, PdfException
    {
        super.set(originDoc, d);
        HashMap annotMap = (HashMap)dict.getMap();
        PdfObject name = new PdfName(Usable.PDF_LE);
        String version = d.getVersion();
        if ((version.equals(PdfDocument.VERSION_1_5)
            || version.equals(PdfDocument.VERSION_1_6))
            && annotMap.containsKey(name))
        {
            ArrayList l = (ArrayList) ((PdfArray) annotMap
                .get(name)).getList();
            if (this.getLineStartStyle() 
                == PdfLineAnnot.LINEENDSTYLE_BUTT)
            {
                l.set(0, new PdfName("Butt"));
            }
            else if (this.getLineStartStyle() 
                == PdfLineAnnot.LINEENDSTYLE_ROPEN_ARROW)
            {
                l.set(0, new PdfName("ROpenArrow"));
            }
            else if (this.getLineStartStyle() 
                == PdfLineAnnot.LINEENDSTYLE_RCLOSED_ARROW)
            {
                l.set(0, new PdfName("RClosedArrow"));
            }
            if (this.getLineEndStyle() 
                == PdfLineAnnot.LINEENDSTYLE_BUTT)
            {
                l.set(1, new PdfName("Butt"));
            }
            else if (this.getLineEndStyle() 
                == PdfLineAnnot.LINEENDSTYLE_ROPEN_ARROW)
            {
                l.set(1, new PdfName("ROpenArrow"));
            }
            else if (this.getLineEndStyle() 
                == PdfLineAnnot.LINEENDSTYLE_RCLOSED_ARROW)
            {
                l.set(1, new PdfName("RClosedArrow"));
            }
        }
        if (version.equals(PdfDocument.VERSION_1_6)
            && annotMap.containsKey(name))
        {
            if (this.contents != null)
            {
                annotMap.put(new PdfName("Cap"), PdfBoolean.TRUE);
            }
            if (this.leaderLineArray != null)
            {
                annotMap.put(new PdfName("LL"), new PdfFloat((float)
                    leaderLineArray[0]));
                annotMap.put(new PdfName("LLE"), new PdfFloat((float)
                    leaderLineArray[1]));
            }
            ArrayList l = (ArrayList) ((PdfArray) annotMap
                .get(new PdfName(Usable.PDF_LE))).getList();
            if (this.getLineStartStyle() 
                == PdfLineAnnot.LINEENDSTYLE_SLASH)
            {
                l.set(0, new PdfName("Slash"));
            }
            if (this.getLineEndStyle() 
                == PdfLineAnnot.LINEENDSTYLE_SLASH)
            {
                l.set(1, new PdfName("Slash"));
            }
        }
    }
    
    protected PdfAnnot encode(PdfStdPage p) throws PdfException
    {
        super.encode(p);
        
        PdfPoint prevStart = new PdfPoint(x1, y1);
        PdfPoint prevEnd = new PdfPoint(x2, y2);

        HashMap annotMap = (HashMap)dict.getMap();
        annotMap.put(new PdfName(Usable.PDF_SUBTYPE), new PdfName(
            Usable.PDF_LINEANNOT));
        
        Color intCol = this.getInteriorColor();
        ArrayList arrIntColor = new ArrayList(4);
        arrIntColor.add(new PdfFloat((float) intCol.getRed() / 255));
        arrIntColor.add(new PdfFloat((float) intCol.getGreen() / 255));
        arrIntColor.add(new PdfFloat((float) intCol.getBlue() / 255));
        annotMap.put(new PdfName(Usable.PDF_IC), new PdfArray(arrIntColor));

        double pageHeight = p.pageHeight;        
        PdfPoint start = p.updatePageSettings(new PdfPoint(x1, y1));
        PdfPoint end = p.updatePageSettings(new PdfPoint(x2, y2));
        double x1 = PdfMeasurement.convertToPdfUnit(
            p.measurementUnit, start.x);
        double y1 = pageHeight - PdfMeasurement.convertToPdfUnit(
            p.measurementUnit, start.y);
        double x2 = PdfMeasurement.convertToPdfUnit(
            p.measurementUnit, end.x);
        double y2 = pageHeight - PdfMeasurement.convertToPdfUnit(
            p.measurementUnit, end.y);

        ArrayList arrLine = new ArrayList();
        arrLine.add(new PdfFloat((float) x1));
        arrLine.add(new PdfFloat((float) y1));
        arrLine.add(new PdfFloat((float) x2));
        arrLine.add(new PdfFloat((float) y2));
        annotMap.put(new PdfName(Usable.PDF_L), new PdfArray(
            arrLine));

        ArrayList arrLineEndStyle = new ArrayList();
        String lineStartStyle = null;
        switch (this.getLineStartStyle())
        {
            case PdfLineAnnot.LINEENDSTYLE_SQUARE:
                lineStartStyle = "Square";
                break;

            case PdfLineAnnot.LINEENDSTYLE_CIRCLE:
                lineStartStyle = "Circle";
                break;

            case PdfLineAnnot.LINEENDSTYLE_DIAMOND:
                lineStartStyle = "Diamond";
                break;

            case PdfLineAnnot.LINEENDSTYLE_OPEN_ARROW:
                lineStartStyle = "OpenArrow";
                break;

            case PdfLineAnnot.LINEENDSTYLE_CLOSED_ARROW:
                lineStartStyle = "ClosedArrow";
                break;

            case PdfLineAnnot.LINEENDSTYLE_NONE:
                lineStartStyle = "None";
                break;

            case PdfLineAnnot.LINEENDSTYLE_BUTT:
                lineStartStyle = "Butt";
                break;

            case PdfLineAnnot.LINEENDSTYLE_ROPEN_ARROW:
                lineStartStyle = "ROpenArrow";
                break;

            case PdfLineAnnot.LINEENDSTYLE_RCLOSED_ARROW:
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
            case PdfLineAnnot.LINEENDSTYLE_SQUARE:
                lineEndStyle = "Square";
                break;

            case PdfLineAnnot.LINEENDSTYLE_CIRCLE:
                lineEndStyle = "Circle";
                break;

            case PdfLineAnnot.LINEENDSTYLE_DIAMOND:
                lineEndStyle = "Diamond";
                break;

            case PdfLineAnnot.LINEENDSTYLE_OPEN_ARROW:
                lineEndStyle = "OpenArrow";
                break;

            case PdfLineAnnot.LINEENDSTYLE_CLOSED_ARROW:
                lineEndStyle = "ClosedArrow";
                break;

            case PdfLineAnnot.LINEENDSTYLE_NONE:
                lineEndStyle = "None";
                break;

            case PdfLineAnnot.LINEENDSTYLE_BUTT:
                lineEndStyle = "Butt";
                break;

            case PdfLineAnnot.LINEENDSTYLE_ROPEN_ARROW:
                lineEndStyle = "ROpenArrow";
                break;

            case PdfLineAnnot.LINEENDSTYLE_RCLOSED_ARROW:
                lineEndStyle = "RClosedArrow";
                break;

            default:
                lineEndStyle = "None";
        }
        arrLineEndStyle.add(new PdfName(lineEndStyle));
        annotMap.put(new PdfName(Usable.PDF_LE), new PdfArray(
            arrLineEndStyle));

        this.x1 = prevStart.x;
        this.y1 = prevStart.y;
        this.x2 = prevEnd.x;
        this.y2 = prevEnd.y;
        
        return this;
    }
}