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

/**
 * @author amol
 * 
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
class PdfPageTree extends PdfTree
{
    private PdfNode currentNode;

    private PdfPage currentPage;

    private int currentPageNo;
    
    private int count; //count of nodes

    private byte degree = 10;/* DO NOT MAKE THIS STATIC */

    protected int pageCnt;
    
    private static boolean pageFound;

    private int depth(PdfNode n) throws PdfException
    {
        int d = 0;
        PdfNode t = n;

        while (t != null)
        {
            if (t.childList == null || t.childList.size() == 0)
            {
                break;
            }
            t = t.getChild(0);
            ++d;
        }

        return d;
    }

    private synchronized void updateCurrentNode()
    {
        if (currentNode == null || currentNode == root
            || currentNode.childList.size() < degree)
        {
            return;
        }

        currentNode = currentNode.parent;
        updateCurrentNode();
    }

    private synchronized void updateCurrentPage(PdfNode n)
        throws PdfException
    {
        if (n == null)
        {
            return;
        }

        int limit = 0;
        if (n instanceof PdfPage && !n.deleted)
        {
            currentPageNo--;
            if (0 == currentPageNo)
            {
                currentPage = (PdfPage) n;
            }
        }
        if (n.childList != null)
        {
            limit = n.getChildList().size();
        }
        for (int i = 0; i < limit; i++)
        {
            updateCurrentPage(n.getChild(i));
        }
    }
    
    synchronized int getCount()
    {
        return count;
    }
    
    synchronized void initialize() throws PdfException
    {
        count = count(root);
    }

    protected static int count(PdfNode n) throws PdfException
    {
        if (n == null)
        {
            return 0;
        }
        int sum = 0;
        int limit = n.childList != null ? 
            n.getChildList().size() : 0;
        for (int i = 0; i < limit; i++)
        {
            sum += count(n.getChild(i));
        }
        
        return (n instanceof PdfPage && !n.deleted) ? 1 : sum;
    }
    
    protected static void hasDescendentPage(PdfNode n)
        throws PdfException
    {
        if (n == null)
        {
            return;
        }
        pageFound = (n instanceof PdfPage && !n.deleted);

        int limit = n.childList != null ? n.childList.size() : 0;
        for (int i = 0; i < limit; i++)
        {
            if ( !pageFound)
            {
                hasDescendentPage(n.getChild(i));
            }
            else
            {
                break;
            }
        }
    }

    protected void setForWriter(PdfNode n) throws IOException,
        PdfException
    {
        if (n == null || n.deleted)
        {
            return;
        }
        if (pageCnt == 2 &&
            !(n instanceof PdfIntermediatePageNode))
        {
            n.getDict().setObjectNumber(1);
        }
        else
        {
            n.getDict().setObjectNumber(parentDoc.objectRun++);
        }
        if (n instanceof PdfPage && !n.deleted)
        {
            ((PdfPage) n).set(parentDoc);
            pageCnt++;
        }

        int limit = n.childList != null ? n.getChildList().size() : 0;
        for (int i = 0; i < limit; i++)
        {
            setForWriter(n.getChild(i));
        }
    }
    
    protected void setForReader(PdfNode n) throws IOException,
        PdfException
    {
        if (n == null || n.deleted)
        {
            return;
        }

        if (pageCnt == 2
            && !(n instanceof PdfIntermediatePageNode))
        {
            int old_no = n.getDict().objNumber;
            parentDoc.addToObjMaps(n.originDoc, old_no, 1);
            n.getDict().setObjectNumber(1);
        }
        else
        {
            int old_no = n.getDict().objNumber;
            int new_no = parentDoc.objectRun;
            parentDoc.addToObjMaps(n.originDoc, old_no, new_no);
            n.getDict().setObjectNumber(new_no);
            parentDoc.objectRun++;
        }
        
        if (n instanceof PdfPage && !n.deleted)
        {
            ((PdfPage)n).set(parentDoc);
            pageCnt++;
        }

        int limit = n.childList != null ? n.getChildList().size() : 0;
        for (int i = 0; i < limit; i++)
        {
            setForReader(n.getChild(i));
        }
    }
    
    private void removeEmptyIntermediateNodes(PdfNode n)
        throws PdfException
    {
        if (n == null || n instanceof PdfPage)
        {
            return;
        }
        PdfNode nn = null;

        for (int i = 0; i < n.childList.size(); i++)
        {
            nn = n.getChild(i);
            if (nn instanceof PdfPage)
            {
                continue;
            }
            hasDescendentPage(nn);
            if ( !pageFound)
            {
                n.removeChild(i);
                i--;
            }
            pageFound = false;
        }
        for (int i = 0; i < n.childList.size(); i++)
        {
            removeEmptyIntermediateNodes(n.getChild(i));
        }
    }
    
    protected void set() throws PdfException,IOException
    {
        if (root == null)
        {
            return;
        }
        
        removeEmptyIntermediateNodes(root);
        
        if (count > 1)
        {
            parentDoc.objectRun++;
        }
        pageCnt = 1;
        if (parentDoc.mode == PdfDocument.WRITING_MODE)
        {
            setForWriter(root);
        }
        else
        {
            setForReader(root);
        }
    }
    
    protected void reset(PdfNode n) throws PdfException
    {
        if (n == null)
        {
            return;
        }
        if (n instanceof PdfPage && !n.deleted)
        {
            ((PdfPage) n).reset();
        }

        int limit = n.childList != null ? n.getChildList().size() : 0;
        for (int i = 0; i < limit; i++)
        {
            reset(n.getChild(i));
        }
    }

    protected PdfPageTree(PdfStdDocument d)
    {
        super(d);
        currentNode = null;
        currentPage = null;
        currentPageNo = 0;
        count = 0;
    }

    protected PdfPageTree(PdfNode n, PdfStdDocument d)
    {
        super(d);
        root = n;
        currentNode = null;
        currentPage = null;
        currentPageNo = 0;
        count = 0;
    }

    protected synchronized void insert(PdfNode n) throws PdfException
    {
        if (root == null)
        {
            root = new PdfIntermediatePageNode();
            currentNode = root;
        }

        int d = depth(currentNode);
        if (currentNode == root)
        {
            if (currentNode.childList.size() < degree)
            /* can accomodate */
            {
                for (int i = 1; i < d; i++)
                /* top-down...skip 0 & 1 depth */
                {
                    PdfNode t = new PdfIntermediatePageNode();
                    currentNode.addChild(t);
                    currentNode = t;
                }
            }
            else
            /* cannot accomodate...bottom-up */
            {
                PdfNode newRoot = new PdfIntermediatePageNode();
                newRoot.addChild(root);
                PdfNode t = newRoot;
                for (int i = 0; i < d; i++)
                {
                    PdfNode p = new PdfIntermediatePageNode();
                    t.addChild(p);
                    t = p;
                }
                currentNode = t;
                root = newRoot;
            }
        }
        else
        /* currentNode != root...top-down */
        {
            for (int i = 1; i < d; i++)
            /* top-down...skip 0 & 1 depth */
            {
                PdfNode t = new PdfIntermediatePageNode();
                currentNode.addChild(t);
                currentNode = t;
            }
        }
        currentNode.addChild(n);
        updateCurrentNode();
        count++;
    }

    protected synchronized void insert(int index, PdfNode n)
        throws PdfException
    {
        //int count = count(root);
        if (index == 0 || index > count + 1)
        {
            throw new PdfException(
                "Cannot insert page at index "
                    + index
                    + ", invalid index(can't be 0 or more than total pages).");

        }
        currentPageNo = index == count + 1 ? count : index;
        updateCurrentPage(root);
        PdfNode t = currentPage.getParent();
        int limit = t.childList.size();
        int i = 0;

        for (i = 0; i < limit; ++i)
        {
            if (t.getChild(i) == currentPage)
            {
                break;
            }
        }
        
        if (index == count + 1)
        {
            t.addChild(n);
        }
        else
        {
            t.addChild(i, n);
        }
        count++;
    }

    synchronized void decrementCount()
    {
        --count;
    }
    
    protected synchronized void delete(int index) throws PdfException
    {
        //if (Writer != null) return;
        count--;
    }

    protected void write(PdfNode n) throws IOException, PdfException
    {
        if (n.deleted)
        {
            return;
        }
        n.write(parentDoc);
        int limit = n.childList != null ? n.getChildList().size() : 0;
        for (int i = 0; i < limit; i++)
        {
            write(n.getChild(i));
        }
    }

    protected synchronized PdfPage getPage(int pageNo)
        throws PdfException
    {
        if (pageNo <= 0)
        {
            pageNo = 1;
        }
        if (pageNo > count)
        {
            throw new PdfException("Cannot get page " + pageNo
                + ", page number out of range.");
        }
        currentPageNo = pageNo;
        updateCurrentPage(root);
        return currentPage;
    }
}