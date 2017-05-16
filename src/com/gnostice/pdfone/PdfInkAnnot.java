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
import java.util.Map;

public class PdfInkAnnot extends PdfProAnnot
{
    private double[][] vertices;

    public PdfInkAnnot()
    {
        this.annotType = ANNOT_TYPE_INK;
    }

    public PdfInkAnnot(PdfRect r, String subject, String contents,
        String title, int flags, Color c, double[][] vertices)
    {
        super(r, subject, contents, title, flags, c);
        this.vertices = vertices;
        this.annotType = ANNOT_TYPE_INK;
    }

    public PdfInkAnnot(PdfRect r, String subject, String contents,
        String title)
    {
        super(r, subject, contents, title);
        this.annotType = ANNOT_TYPE_INK;
    }

    public PdfInkAnnot(PdfRect r, String subject, String contents,
        String title, Color c)
    {
        super(r, subject, contents, title, c);
        this.annotType = ANNOT_TYPE_INK;
    }
    
    public PdfInkAnnot(PdfRect r, String subject, String contents,
        String title, int flags)
    {
        super(r, subject, contents, title, flags);
        this.annotType = ANNOT_TYPE_INK;
    }
    
    public PdfInkAnnot(PdfRect r, String subject, String contents,
        String title, int flags, Color c)
    {
        super(r, subject, contents, title, flags, c);
        this.annotType = ANNOT_TYPE_INK;
    }
    
    public PdfInkAnnot(PdfRect r, int flags)
    {
        super(r, flags);
        this.annotType = ANNOT_TYPE_INK;
    }
    
    public PdfInkAnnot(PdfRect r, int flags, Color c)
    {
        super(r, flags, c);
        this.annotType = ANNOT_TYPE_INK;
    }
    
    public PdfInkAnnot(PdfRect r, Color c)
    {
        super(r, c);
        this.annotType = ANNOT_TYPE_INK;
    }
    
    void applyPropertiesFrom(PdfDict annotDict, PdfStdPage page)
        throws IOException, PdfException
    {
        super.applyPropertiesFrom(annotDict, page);
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
            if (name.equals(PDF_INKLIST))
            {
                if (value instanceof PdfArray)
                {
                    unknownAttributes.remove(key);
                    ArrayList outer = (ArrayList) ((PdfArray) value)
                        .getList();
                    PdfObject obj = (PdfObject) outer.get(0);
                    int limit = outer.size();
                    double val = 0;
                    if (obj instanceof PdfNumber)
                    {
                        double[] vertices = new double[limit];
                        for (int i = 0; i < limit; ++i)
                        {
                            val = ((PdfNumber) outer.get(i)).getVal();
                            vertices[i] = i % 2 == 0 ? val
                                : page.pageHeight - val;
                        }
                        setVertices(vertices);
                    }
                    else if (obj instanceof PdfArray)
                    {
                        double[][] vertices = new double[limit][];
                        for (int i = 0; i < limit; ++i)
                        {
                            ArrayList inner = (ArrayList) ((PdfArray) outer
                                .get(i)).getList();
                            int in_limit = inner.size();
                            double[] in_arr = new double[in_limit];
                            for (int j = 0; j < in_limit; ++j)
                            {
                                val = ((PdfNumber) inner.get(j))
                                    .getVal();
                                in_arr[j] = j % 2 == 0 ? val
                                    : page.pageHeight - val;
                            }
                            vertices[i] = in_arr;
                        }
                        setVertices(vertices);
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
    }

    public synchronized double[][] getVertices()
    {
        return vertices;
    }

    public synchronized void setVertices(double[][] vertices)
    {
        this.vertices = vertices;
    }

    public synchronized void setVertices(double[] vertices)
    {
        this.vertices = new double[1][];
        this.vertices[0] = vertices;
    }

    public synchronized void setVertices(double[][] vertices,
        int measurementUnit) throws PdfException
    {
        this.vertices = new double[vertices.length][];
        for (int i = 0; i < vertices.length; ++i)
        {
            this.vertices[i] = new double[vertices[i].length];
            for (int j = 0; j < vertices[i].length; ++j)
            {
                this.vertices[i][j] = PdfMeasurement
                    .convertToPdfUnit(measurementUnit, vertices[i][j]);
            }
        }
    }

    public synchronized void setVertices(double[] vertices,
        int measurementUnit)
    {
        this.vertices = new double[1][vertices.length];
        for (int j = 0; j < vertices.length; ++j)
        {
            this.vertices[0][j] = PdfMeasurement.convertToPdfUnit(
                measurementUnit, vertices[j]);
        }
    }
    
    protected PdfAnnot encode(PdfStdPage p) throws PdfException
    {
        super.encode(p);
        
        double[][] prevVertices = this.vertices;
        HashMap annotMap = (HashMap)dict.getMap();
        annotMap.put(new PdfName(Usable.PDF_SUBTYPE), new PdfName(
            Usable.PDF_INK));
        
        double pageHeight = p.pageHeight;
        if (this.vertices != null)
        {
            updateVertices(p);

            ArrayList outer = new ArrayList();
            ArrayList inner = null;
            for (int i = 0; i < this.vertices.length; ++i)
            {
                inner = new ArrayList();
                for (int j = 0; j < this.vertices[i].length; ++j)
                {
                    inner.add(new PdfFloat(
                        j % 2 == 0 ? this.vertices[i][j] : pageHeight
                            - this.vertices[i][j]));
                }
                outer.add(new PdfArray(inner));
            }
            annotMap.put(new PdfName(Usable.PDF_INKLIST),
                new PdfArray(outer));
        }

        this.vertices = prevVertices;
        
        return this;
    }

    private void updateVertices(PdfStdPage p) throws PdfException
    {
        double[][] newVertices = new double[vertices.length][];
        PdfPoint point = null;
        
        for (int i = 0; i < vertices.length; ++i)
        {
            if (vertices[i].length % 2 != 0)
            {
                throw new PdfException(
                    "Invalid points count for vertices.");
            }
            newVertices[i] = new double[vertices[i].length];
            for (int j = 0; j < vertices[i].length; j += 2)
            {
                point = p.updatePageSettings(new PdfPoint(
                    vertices[i][j], vertices[i][j + 1]));
                newVertices[i][j] = point.x;
                newVertices[i][j + 1] = point.y;
            }
        }
        
        this.vertices = newVertices;
    }
    
    public Object clone()
    {
        PdfInkAnnot clone = (PdfInkAnnot) super.clone();
        if (this.vertices != null)
        {
            clone.vertices = new double[vertices.length][];
            for (int i = 0; i < vertices.length; ++i)
            {
                clone.vertices[i] = new double[vertices[i].length];
                for (int j = 0; j < vertices[i].length; ++j)
                {
                    clone.vertices[i][j] = vertices[i][j];
                }
            }
        }

        return clone;
    }
}