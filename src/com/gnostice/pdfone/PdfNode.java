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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

abstract class PdfNode
{
    protected static final PdfName TYPE = new PdfName(Usable.PDF_TYPE);

    protected static final PdfName PARENT = new PdfName(
        Usable.PDF_PARENT);

    protected static final PdfName MEDIABOX = new PdfName(
        Usable.PDF_MEDIABOX);
    
    protected PdfDict dict;

    protected List childList;

    protected PdfNode parent;
    
    protected PdfStdDocument originDoc;

    protected boolean deleted;

    protected PdfNode()
    {
        dict = new PdfDict(new HashMap());
        childList = null;
        parent = null;
    }

    protected PdfNode(PdfDict d)
    {
        dict = d;
        childList = null;
        parent = null;
    }

    protected synchronized PdfDict getDict()
    {
        return this.dict;
    }

    protected synchronized void setDict(PdfDict d)
    {
        this.dict = d;
    }

    protected synchronized PdfNode getParent()
    {
        return this.parent;
    }

    protected synchronized void setParent(PdfNode n)
    {
        this.parent = n;
    }

    protected synchronized List getChildList()
    {
        return this.childList;
    }

    protected synchronized PdfNode getChild(int index)
        throws PdfException
    {
        if (childList == null)
        {
            return null;
        }
        try
        {
            return (PdfNode) childList.get(index);
        }
        catch (IndexOutOfBoundsException iob)
        {
            throw new PdfException(
                "Cannot get child node, index out of bounds.");
        }
        catch (ClassCastException cce)
        {
            throw new PdfException(
                "Cannot get child node, type not PdfNode.");
        }
    }

    protected synchronized void addChild(PdfNode n)
    {
        if (childList == null)
        {
            childList = new ArrayList();
        }
        childList.add(n);
        n.parent = this;
    }

    protected synchronized void addChild(int index, PdfNode n)
        throws PdfException
    {
        if (childList == null)
        {
            throw new PdfException("Cannot add node at index "
                + index + ", list is empty.");
        }
        try
        {
            childList.add(index, n);
            n.parent = this;
        }
        catch (IndexOutOfBoundsException iob)
        {
            throw new PdfException("Cannot add node at index "
                + index + ", index out of bounds.");
        }
    }

    protected synchronized void removeChild(int index)
        throws PdfException
    {
        if (childList != null)
        {
            try
            {
                childList.remove(index);
            }
            catch (IndexOutOfBoundsException iob)
            {
                throw new PdfException(
                    "Cannot remove child node, index out of bounds.");
            }
        }
    }

    protected synchronized void setChild(int index, PdfNode node)
        throws PdfException
    {
        if (childList == null)
        {
            throw new PdfException(
                "Cannot set node as child, list is empty.");
        }
        try
        {
            childList.set(index, node);
            node.parent = this;
        }
        catch (NullPointerException npe)
        {
            throw new PdfException(
                "Cannot set node as child, no child present at index.");
        }
        catch (IndexOutOfBoundsException iob)
        {
            throw new PdfException(
                "Cannot set node as child, index out of bounds.");
        }
    }

    abstract protected void write(PdfStdDocument d)
        throws IOException, PdfException;
}