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
import java.awt.color.ColorSpace;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PdfArray extends PdfObject
{
    private ArrayList arrayList;

    static Color getColor(PdfArray colArray)
    {
        List l = colArray.getList();
        float[] fa = new float[l.size()];
        ColorSpace csp;
        Color col = null;

        for (int i = 0; i < l.size(); i++)
        {
            fa[i] = ((PdfNumber) l.get(i)).getFloat();
        }

        switch (fa.length)
        {
            case 1:
                csp = ColorSpace.getInstance(ColorSpace.CS_GRAY);
                col = new Color(csp, fa, 0);
                break;

            case 3:
                csp = ColorSpace.getInstance(ColorSpace.CS_sRGB);
                col = new Color(csp, fa, 0);
                break;

            case 4:
                float colors = 1 - fa[3];
                float rgb[] = { colors * (1 - fa[0]),
                    colors * (1 - fa[1]), colors * (1 - fa[1]) };
                csp = ColorSpace.getInstance(ColorSpace.CS_sRGB);
                col = new Color(csp, rgb, 0);

            default:
                return null;
        }

        return col;
    }
    
    public PdfArray(List arrayList)
    {
        if (arrayList instanceof ArrayList)
        {
            this.arrayList = (ArrayList) arrayList;
        }
        else
        {
            this.arrayList = new ArrayList(arrayList);
        }
    }
    
    public PdfArray(int[] ia)
    {
        if (ia == null)
        {
            return;
        }
        
        this.arrayList = new ArrayList();
        for (int i = 0; i < ia.length; ++i)
        {
            arrayList.add(new PdfInteger(ia[i]));
        }
    }

    public PdfArray(double[] da)
    {
        if (da == null)
        {
            return;
        }
        
        this.arrayList = new ArrayList();
        for (int i = 0; i < da.length; ++i)
        {
            arrayList.add(new PdfFloat((float) da[i]));
        }
    }

    public PdfArray(float[] fa)
    {
        if (fa == null)
        {
            return;
        }
        
        this.arrayList = new ArrayList();
        for (int i = 0; i < fa.length; ++i)
        {
            arrayList.add(new PdfFloat(fa[i]));
        }
    }

    public Object clone()
    {
        PdfArray clone = (PdfArray) super.clone();
        ArrayList nal = new ArrayList();
        for (Iterator iter = arrayList.iterator(); iter.hasNext();)
        {
            PdfObject obj = (PdfObject) iter.next();
            nal.add(obj.clone());
        }
        clone.arrayList = nal;
        
        return clone;
    }

    public boolean equals(Object obj)
    {
        if ((obj == null) || ( !(obj instanceof PdfArray)))
        {
            return false;
        }
        return arrayList.equals(((PdfArray) obj).arrayList);
    }

    public List getList()
    {
        return arrayList;
    }

    protected int write(PdfWriter writer) throws IOException
    {
        DataOutputStream dataOpStream = writer.getDataOutputStream();
        int byteCount = 2; // for [ and ]

        dataOpStream.write(Usable.PDF_ARRAYSTART);
        for (Iterator iter = arrayList.iterator(); iter.hasNext();)
        {
            PdfObject obj = (PdfObject) iter.next();
                byteCount += obj.write(writer);
//            if (iter.hasNext())
//            {
//                dataOpStream.writeByte(PDF_SP);
//                byteCount++;
//            }
        }
        dataOpStream.write(Usable.PDF_ARRAYEND);

        return byteCount;
    }
}