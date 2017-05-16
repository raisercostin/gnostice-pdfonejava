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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author arjun
 * 
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
class PdfBookmarkTree extends PdfTree
{
    static final PdfName COUNT = new PdfName(Usable.PDF_COUNT);

    static final PdfName FIRST = new PdfName(Usable.PDF_FIRST);

    static final PdfName LAST = new PdfName(Usable.PDF_LAST);
    
    static final PdfName STYLE = new PdfName(Usable.PDF_F);
    
    static final PdfName COLOR = new PdfName(Usable.PDF_C);
    
    int bytesWritten; //for object stream
    
    int position; //position of a node in objStream;

    static int next(PdfNode n)
    {
        List l = n.getParent().getChildList();
        int index = l.indexOf(n);

        if (index == l.size() - 1)
        {
            return -1;
        }
        return ((PdfNode) l.get(index + 1)).getDict()
            .getObjectNumber();
    }

    static int first(PdfNode n)
    {
        List l = n.getChildList();

        if (l != null)
        {
            PdfNode nn = (PdfNode) l.get(0);
            if (nn != null)
            {
                return nn.getDict().getObjectNumber();
            }
        }
        return -1;
    }

    static int last(PdfNode n)
    {
        List l = n.getChildList();

        if (l != null)
        {
            PdfNode nn = (PdfNode) l.get(l.size() - 1);
            if (nn != null)
            {
                return nn.getDict().getObjectNumber();
            }
        }
        return -1;
    }

    PdfBookmarkTree(PdfStdDocument d)
    {
        if (root == null)
        {
            root = new PdfBookmark();
            root.childList = new ArrayList();
        }
        parentDoc = d;
        bytesWritten = 0;
    }

    void insert(PdfNode n) throws PdfException
    {

    }

    void insert(int index, PdfNode n) throws PdfException
    {

    }

    void delete(int index) throws PdfException
    {

    }

    void merge(PdfTree t)
    {
        if (!(t instanceof PdfBookmarkTree))
        {
            return;
        }
        List list = t.getRoot().getChildList();
        if (list != null)
        {
            Iterator iter = list.iterator();
            while (iter.hasNext())
            {
                this.root.childList.add((PdfNode) iter.next());        
            }
        }
    }

    static int count(PdfNode n) throws PdfException
    {
        if (n == null)
        {
            return 0;
        }
        int sum = 0, limit = 0;
        if (n.childList != null)
        {
            limit = n.getChildList().size();
            sum += limit;
        }
        for (int i = 0; i < limit; i++)
        {
            sum += count(n.getChild(i));
        }

        return sum;
    }

    void set(PdfNode n) throws IOException, PdfException
    {
        if (n == null)
        {
            return;
        }
        if (n == root)
        {
            n.dict.setObjectNumber(parentDoc.objectRun++);
        }
        int limit = 0;
        if (n.childList != null)
        {
            limit = n.getChildList().size();
        }

        PdfNode nn;
        for (int i = 0; i < limit; i++)
        {
            nn = n.getChild(i);
            if (nn != null)
            {
                ((PdfBookmark) nn).set(parentDoc);
                set(nn); //recurse
            }
        }
    }

    void writeNodes(PdfNode n) throws IOException, PdfException
    {
        if (n == null)
        {
            return;
        }
        int limit = 0;
        if (n.childList != null)
        {
            limit = n.getChildList().size();
        }
        PdfNode nn;
        for (int i = 0; i < limit; i++)
        {
            nn = n.getChild(i);
            if (nn != null)
            {
                writeNodes(nn);
                nn.write(parentDoc);
            }
        }
    }

    void write() throws IOException, PdfException
    {
        if (root == null)
        {
            return;
        }
        Map m = root.getDict().getMap();
        m.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_OUTLINES));

        int first = root.getChild(0).dict.getObjectNumber();
        m.put(FIRST, new PdfIndirectReference(first, 0));
        int last = root.getChild(root.getChildList().size() - 1).dict
            .getObjectNumber();
        m.put(LAST, new PdfIndirectReference(last, 0));
        m.put(COUNT, new PdfInteger(count(root)));

        int index = root.getDict().getObjectNumber();
        parentDoc.offset[index] = parentDoc.bytesWritten;
        parentDoc.bytesWritten += parentDoc.writer
            .writeIndirectObject(root.dict);
        
        writeNodes(root);
    }
    
    void writeNodes(PdfNode n, PdfObjectStream objStm, PdfWriter w)
        throws IOException, PdfException
    {
        if (n == null)
        {
            return;
        }
        int limit = 0;
        if (n.childList != null)
        {
            limit = n.getChildList().size();
        }
        PdfNode nn;
        for (int i = 0; i < limit; i++)
        {
            nn = n.getChild(i);
            if (nn != null)
            {
                int index = nn.dict.getObjectNumber();
                parentDoc.objStreamId.put(new PdfInteger(index),
                    new PdfInteger(objStm.stream.getObjectNumber()));
                parentDoc.objStreamPos.put(new PdfInteger(index),
                    new PdfInteger(position++));
                objStm.offsets.write((Integer.toString(index)
                    + Usable.PDF_SP).getBytes());
                objStm.offsets.write((Integer.toString(bytesWritten)
                    + Usable.PDF_SP).getBytes());

                bytesWritten += ((PdfBookmark) nn)
                    .write(parentDoc, w);
                
                writeNodes(nn, objStm, w);
            }
        }
    }

    void write(PdfObjectStream objStm) throws IOException,
        PdfException
    {
        if (root == null)
        {
            return;
        }
        Map m = root.getDict().getMap();
        m.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_OUTLINES));

        int first = root.getChild(0).dict.getObjectNumber();
        m.put(FIRST, new PdfIndirectReference(first, 0));
        int last = root.getChild(root.getChildList().size() - 1).dict
            .getObjectNumber();
        m.put(LAST, new PdfIndirectReference(last, 0));
        m.put(COUNT, new PdfInteger(count(root)));

        int index = root.getDict().getObjectNumber();
        parentDoc.objStreamId.put(new PdfInteger(index),
            new PdfInteger(objStm.stream.getObjectNumber()));
        parentDoc.objStreamPos.put(new PdfInteger(index),
            new PdfInteger(position++));
        objStm.offsets
            .write((Integer.toString(index) + Usable.PDF_SP)
                .getBytes());
        objStm.offsets
            .write((Integer.toString(bytesWritten) + Usable.PDF_SP)
                .getBytes());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter w = PdfWriter.memoryWriter(baos);
        bytesWritten += w.writePDFObject(root.dict);

        writeNodes(root, objStm, w);

        objStm.objectCount = count(root) + 1;
        objStm.initialize(baos);
    }
}