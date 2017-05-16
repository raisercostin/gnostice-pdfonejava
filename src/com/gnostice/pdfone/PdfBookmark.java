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
import java.util.List;
import java.util.Map;

/**
 * This class represents a bookmark in a document. It is used to 
 * create, delete, and modify bookmarks. An object of this class is 
 * returned when <code>addBookmark</code> method of
 * {@link PdfDocument} class is employed.
 * <code>getFirstBookmark</code> method of {@link PdfDocument} class
 * is used to retrieve the first bookmark of an existing document.
 * <p>
 * An object of this class has methods to:
 * </p>
 * <ul>
 *  <li>add and remove actions to the bookmark,</li>
 *  <li>add and remove a new bookmark immediately ahead or following
 * the bookmark, and</li>
 *  <li>retrieve and specify the bookmark's text, color, and style.</li>
 * </ul>
 * 
 * @since 1.0
 * @version 1.0
 */
public final class PdfBookmark extends PdfNode
{
	public static class RemoteGoTo
    {
        int pageNo = 1;

        int fit = -1;
        
        double left;

        double top;

        double right;

        double bottom;
        
        double pos;
        
        double zoom;
    }
    
    static final PdfName TITLE = new PdfName(Usable.PDF_TITLE);

    static final PdfName PARENT = new PdfName(Usable.PDF_PARENT);

    static final PdfName NEXT = new PdfName(Usable.PDF_NEXT);

    static final PdfName ACTION = new PdfName(Usable.PDF_A);
    
    static final PdfName DEST = new PdfName(Usable.PDF_DESTINATION);

    protected int pageNo;

    protected String title;
    
    protected PdfString rTitle;

    protected ArrayList actionList;
    
    protected Color color;
    
    protected int style;
    
    /**
     * Constant for adjusting magnification (zoom) factor of a page 
     * to tightly fit its entire width inside a window.
     */
    public static final int FITH = 0;

    /**
     * Constant for adjusting magnification (zoom) factor of a page  
     * to tightly fit entire width of its  
     * <a href="{@docRoot}/doc-files/glossary.htm#bounding_box" target="_GnosticeGlossaryWindow"
     * >bounding box</a> 
     * inside a window.
     */
    public static final int FITBH = 1;

    /**
     * Constant for adjusting magnification (zoom) factor of a page
     * to tightly fit entire height of its 
     * <a href="{@docRoot}/doc-files/glossary.htm#bounding_box" target="_GnosticeGlossaryWindow"
     * >bounding box</a> 
     * inside a window.
     */
    public static final int FITBV = 2;
    
    /**
     * Constant for adjusting magnification (zoom) factor of a page
     * to tightly fit its entire height inside a window.
     */
    public static final int FITV = 3;

    
    /**
     * Constant for adjusting magnification (zoom) factor of a page  
     * to tightly fit entire height and width of its 
     * <a href="{@docRoot}/doc-files/glossary.htm#bounding_box" target="_GnosticeGlossaryWindow"
     * >bounding box</a> 
     * inside a window.
     */
    public static final int FITB = 4;
    
    /**
     * Constant for displaying titles of bookmarks in bold type.
     */
    public static final int BOLD = 2;
    
    /**
     * Constant for displaying title of bookmarks in italic style.
     */
    public static final int ITALIC = 1;

    public static RemoteGoTo getRemoteGoToInstance(int pageNo)
    {
        RemoteGoTo obj = new RemoteGoTo();
        obj.pageNo = pageNo;

        return obj;
    }

    public static RemoteGoTo getRemoteGoToInstance(int pageNo,
        double pos, int fit)
    {
        RemoteGoTo obj = new RemoteGoTo();
        obj.pageNo = pageNo;
        obj.fit = fit;
        obj.pos = pos;

        return obj;
    }

    public static RemoteGoTo getRemoteGoToInstance(int pageNo, int fit)
    {
        RemoteGoTo obj = new RemoteGoTo();
        obj.pageNo = pageNo;
        obj.fit = fit;

        return obj;
    }

    public static RemoteGoTo getRemoteGoToInstance(int pageNo,
        PdfRect rect)
    {
        RemoteGoTo obj = new RemoteGoTo();
        obj.pageNo = pageNo;

        obj.left = rect.x;
        obj.top = rect.height;
        obj.right = rect.width;
        obj.bottom = rect.y;

        return obj;
    }

    public static RemoteGoTo getRemoteGoToInstance(int pageNo,
        double left, double bottom, double right, double top)
    {
        RemoteGoTo obj = new RemoteGoTo();
        obj.pageNo = pageNo;

        obj.left = left;
        obj.top = top;
        obj.right = right;
        obj.bottom = bottom;

        return obj;
    }
    
    protected PdfBookmark()
    {
        super();
        title = null;
        pageNo = 0;
        actionList = new ArrayList();
    }

    protected PdfBookmark(String title)
    {
        super();
        this.title = title;
        this.rTitle = null;
        this.pageNo = 0;
        actionList = new ArrayList();
    }
    
    protected PdfBookmark(PdfString title)
    {
        super();
        this.rTitle = title;
        this.title = null;
        this.pageNo = 0;
        actionList = new ArrayList();
    }

    protected PdfBookmark(String title, int pageNo)
    {
        super();
        this.title = title;
        this.rTitle = null;
        this.pageNo = pageNo;
        actionList = new ArrayList();
    }

    protected void set(PdfStdDocument d) throws IOException,
        PdfException
    {
        if (color != null)
        {
            ArrayList c = new ArrayList();
            c.add(new PdfFloat(color.getRed() / 255));
            c.add(new PdfFloat(color.getGreen() / 255));
            c.add(new PdfFloat(color.getBlue() / 255));
            
            this.dict.getMap().put(new PdfName(Usable.PDF_C),
                new PdfArray(c));
        }
        if (this.style != 0)
        {
            this.dict.getMap().put(new PdfName(Usable.PDF_F),
                new PdfInteger(this.style));
        }
        setActions(d);
        dict.setObjectNumber(d.objectRun++);
    }

	void setActions(PdfStdDocument d) throws PdfException, IOException {
		if (actionList != null)
        {
            PdfDict actionDict;
            int size = actionList.size();
            for (int i = 0; i < size; i++)
            {
                actionDict = (PdfDict) actionList.get(i);
                PdfArray arr = (PdfArray) actionDict
                    .getValue(new PdfName(Usable.PDF_D));
                PdfName actionType = (PdfName) actionDict
                    .getValue(new PdfName(Usable.PDF_S));
                if (arr != null && 
                    !(actionType != null && 
                        actionType.getString().equals("GoToR")))
                {
                    List l = arr.getList();
                    if (this.pageNo > 0)
                    {
                        PdfNode page = d.pageTree.getPage(pageNo);
                        int ref = page.getDict().getObjectNumber();
                        l.add(0, new PdfIndirectReference(ref, 0));
                        l.add(page);
                    }
                    else //reading mode
                    {
                        /*int limit = l.size();
                        for (int j = 0; j < limit; j++)
                        {
                            PdfObject obj = (PdfObject) l.get(j);
                            if (obj instanceof PdfIndirectReference)
                            {
                                obj.objNumber = d.getNewObjNo(
                                    originDoc, obj.objNumber);
                            }
                        }*/
                        d.updateIndirectRefs(originDoc, arr, true);
                    }
                }
                actionDict.setObjectNumber(d.objectRun++);
                if (i != 0)
                {
                    ((PdfDict) actionList.get(i - 1)).getMap().put(
                        new PdfName(Usable.PDF_NEXT),
                        new PdfIndirectReference(d.objectRun - 1, 0));
                }
                else
                {
                    dict.getMap().put(new PdfName(Usable.PDF_A),
                        new PdfIndirectReference(actionDict
                            .getObjectNumber(), 0));
                }
            }
        }
	}

    List processDestList(List l)
    {
        PdfStdPage page = (PdfStdPage) l.get(l.size() - 1);
        l.remove(l.size() - 1);
        ArrayList newList = new ArrayList(l);
        PdfObject obj = (PdfObject) l.get(1);
        if (obj instanceof PdfName)
        {
            String name = ((PdfName) obj).getString();
            if (name.equals(Usable.PDF_XYZ))
            {
                double left = ((PdfNumber) l.get(2)).getVal();
                double top = ((PdfNumber) l.get(3)).getVal();
                left += page.pageCropLeft;
                top = page.pageHeight - top - page.pageCropTop;
                newList.set(2, new PdfFloat(left));
                newList.set(3, new PdfFloat(top));
            }
            else if (name.equals(Usable.PDF_FITH))
            {
                double top = ((PdfNumber) l.get(2)).getVal();
                top = page.pageHeight - top - page.pageCropTop;
                newList.set(2, new PdfFloat(top));
            }
            else if (name.equals(Usable.PDF_FITV))
            {
                double left = ((PdfNumber) l.get(2)).getVal();
                left += page.pageCropLeft;
                newList.set(2, new PdfFloat(left));
            }
            else if (name.equals(Usable.PDF_FITR))
            {
                double left = ((PdfNumber) l.get(2)).getVal();
                double bottom = ((PdfNumber) l.get(3)).getVal();
                double right = ((PdfNumber) l.get(4)).getVal();
                double top = ((PdfNumber) l.get(5)).getVal();
                left += page.pageCropLeft;
                bottom = page.pageHeight - bottom - page.pageCropTop;
                right += page.pageCropLeft;
                top = page.pageHeight - top - page.pageCropTop;
                newList.set(2, new PdfFloat(left));
                newList.set(3, new PdfFloat(bottom));
                newList.set(4, new PdfFloat(right));
                newList.set(5, new PdfFloat(top));
            }
            else if (name.equals(Usable.PDF_FITBH))
            {
                double top = ((PdfNumber) l.get(2)).getVal();
                top = page.pageHeight - top - page.pageCropTop;
                newList.set(2, new PdfFloat(top));
            }
            else if (name.equals(Usable.PDF_FITBV))
            {
                double left = ((PdfNumber) l.get(2)).getVal();
                left += page.pageCropLeft;
                newList.set(2, new PdfFloat(left));
            }
        }
        
        return newList;
    }

    protected int write(PdfStdDocument d, PdfWriter w)
        throws IOException, PdfException
    {
        Map m = getDict().getMap();
        int ref;
        
        ref = getParent().getDict().getObjectNumber();
        if (ref > 0)
            m.put(PARENT, new PdfIndirectReference(ref, 0));
        
        ref = PdfBookmarkTree.next(this);
        if (ref > 0)
            m.put(NEXT, new PdfIndirectReference(ref, 0));
        
        ref = PdfBookmarkTree.first(this);
        if (ref > 0)
            m.put(PdfBookmarkTree.FIRST, new PdfIndirectReference(
                ref, 0));
        
        ref = PdfBookmarkTree.last(this);
        if (ref > 0)
            m.put(PdfBookmarkTree.LAST, new PdfIndirectReference(ref,
                0));
        
        ref = PdfBookmarkTree.count(this);
        if (ref > 0)
            m.put(PdfBookmarkTree.COUNT, new PdfInteger(-ref));
        
        if (title != null)
        {
            m.put(TITLE, new PdfTextString(title, true));
        }
        else if (rTitle != null)
        {
            m.put(TITLE, rTitle);
        }
        
        /*int retVal = writeActions(d, w);
        retVal += w.writePDFObject(dict);*/
        writeActions(d);
        int retVal = w.writePDFObject(dict);
        resetPageRefs();
        
        return retVal; 
    }

    protected void write(PdfStdDocument d) throws IOException,
        PdfException
    {
        Map m = getDict().getMap();
        int ref;

        ref = getParent().getDict().getObjectNumber();
        if (ref > 0)
            m.put(PARENT, new PdfIndirectReference(ref, 0));

        ref = PdfBookmarkTree.next(this);
        if (ref > 0)
            m.put(NEXT, new PdfIndirectReference(ref, 0));

        ref = PdfBookmarkTree.first(this);
        if (ref > 0)
            m.put(PdfBookmarkTree.FIRST, new PdfIndirectReference(
                ref, 0));

        ref = PdfBookmarkTree.last(this);
        if (ref > 0)
            m.put(PdfBookmarkTree.LAST, new PdfIndirectReference(ref,
                0));

        ref = PdfBookmarkTree.count(this);
        if (ref > 0)
            m.put(PdfBookmarkTree.COUNT, new PdfInteger(-ref));

        if (title != null)
        {
            m.put(TITLE, new PdfTextString(title, true));
        }
        else if (rTitle != null)
        {
            m.put(TITLE, rTitle);
        }
        
        int index = getDict().getObjectNumber();
        d.offset[index] = d.bytesWritten;
        d.bytesWritten += d.writer.writeIndirectObject(getDict());

        writeActions(d);
        resetPageRefs();
    }

    protected void writeActions(PdfStdDocument d) throws IOException
    {
        PdfDict actionDict;
        int index;
        final PdfName D = new PdfName(Usable.PDF_D);
        for (int i = 0, limit = actionList.size(); i < limit; i++)
        {
            actionDict = (PdfDict) actionList.get(i);
            PdfArray arr = (PdfArray) actionDict.getValue(D);
            PdfName actionType = (PdfName) actionDict
                .getValue(new PdfName(Usable.PDF_S));

            if (arr != null && 
                !(actionType != null && 
                    actionType.getString().equals("GoToR")))
            {
                List l = arr.getList();
                if (this.pageNo > 0)
                {
                    List l2 = processDestList(l);
                    actionDict.dictMap.put(D, new PdfArray(l2));
                }
            }
            index = actionDict.getObjectNumber();
            d.offset[index] = d.bytesWritten;
            d.bytesWritten += d.writer
                .writeIndirectObject(actionDict);
            actionDict.dictMap.put(D, arr);
        }
    }

    /*protected int writeActions(PdfStdDocument d, PdfWriter w)
        throws IOException
    {
        PdfDict actionDict;
        int retVal = 0;
        final PdfName D = new PdfName(Usable.PDF_D);
        for (int i = 0, limit = actionList.size(); i < limit; i++)
        {
            actionDict = (PdfDict) actionList.get(i);
            PdfArray arr = (PdfArray) actionDict.getValue(D);
            if (arr != null)
            {
                List l = arr.getList();
                if (this.pageNo > 0)
                {
                    List l2 = processDestList(l);
                    actionDict.dictMap.put(D, new PdfArray(l2));
                }
            }
            retVal += w.writePDFObject(actionDict);
            actionDict.dictMap.put(D, arr);
        }

        return retVal;
    }*/

    void resetPageRefs()
    {
        if (actionList != null)
        {
            PdfDict actionDict;
            for (int i = 0, size = actionList.size(); i < size; ++i)
            {
                actionDict = (PdfDict) actionList.get(i);
                PdfArray arr = (PdfArray) actionDict
                    .getValue(new PdfName(Usable.PDF_D));
                if (arr != null && this.pageNo != 0)
                {
                    arr.getList().remove(0);
                }
            }
        }
    }

    private void addPrevious(PdfNode n) throws PdfException
    {
        int index = this.parent.childList.indexOf(this);
        this.parent.addChild(index, n);
        n.parent = this.parent;
    }

    private void addNext(PdfNode n) throws PdfException
    {
        int index = this.parent.childList.indexOf(this) + 1;
        this.parent.addChild(index, n);
        n.parent = this.parent;
    }
    
    /*
     * Returns a newly created bookmark, inserted immediately after
     * this bookmark, that that links to a particular position on page
     * specified by <code>pageNo</code>. The linked position on the
     * page is specified by <code>left</code> and <code>top</code>.
     * The bookmark is set to display the page with a zoom value
     * specified by <code>zoom</code>.
     * 
     * @param title
     *            text to be used to display the bookmark
     * @param pageNo
     *            number of page set to be displayed when bookmark is
     *            selected
     * @param left
     *            x-coordinate of the linked position
     * @param top
     *            y-coordinate of the linked position
     * @param zoom
     *            percentage of displayed size of page with reference
     *            to original
     * @return a new bookmark placed immediately ahead of this
     *         bookmark
     * @since 1.0
     * @exception PdfException
     *                if an illegal argument is supplied.
     */
    public synchronized PdfBookmark addPrevious(String title, int pageNo,
        double left, double top, double zoom) throws PdfException
    {
        PdfBookmark b = new PdfBookmark(title, pageNo);
        ArrayList list = new ArrayList();
        list.add(new PdfName(Usable.PDF_XYZ));
        list.add(new PdfFloat((float) left));
        list.add(new PdfFloat((float) top));
        if (zoom <= 0)
        {
            list.add(new PdfNull());
        }
        else
        {
            list.add(new PdfFloat((float) (zoom / 100)));
        }

        HashMap hm = new HashMap();
        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_GOTO_ACTION));
        hm.put(new PdfName(Usable.PDF_D), new PdfArray(list));

        b.addToActionList(new PdfDict(hm));
        addPrevious(b);

        return b;
    }

    /*
     * Inserts ahead of this bookmark a new bookmark that links to
     * page specified by <code>pageNo</code>.
     * 
     * @param title
     *            text to be used to display bookmark
     * @param pageNo
     *            number of page set to be displayed when bookmark is
     *            selected
     * @return a new bookmark placed immediately after this bookmark
     * @since 1.0
     * @exception PdfException
     *                if an illegal argument is supplied.
     */
    public synchronized PdfBookmark addPrevious(String title, int pageNo)
        throws PdfException
    {
        PdfBookmark b = new PdfBookmark(title, pageNo);
        ArrayList list = new ArrayList();
        list.add(new PdfName(Usable.PDF_FIT));
        HashMap hm = new HashMap();
        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_GOTO_ACTION));
        hm.put(new PdfName(Usable.PDF_D), new PdfArray(list));

        b.addToActionList(new PdfDict(hm));
        addPrevious(b);

        return b;
    }

    public synchronized PdfBookmark addPrevious(String title, int pageNo,
        double pos, int fit) throws PdfException
    {
        PdfBookmark b = new PdfBookmark(title, pageNo);
        ArrayList list = new ArrayList();
        switch (fit)
        {
            case PdfBookmark.FITH:
                list.add(new PdfName(Usable.PDF_FITH));
                break;

            case PdfBookmark.FITBH:
                list.add(new PdfName(Usable.PDF_FITBH));
                break;

            case PdfBookmark.FITBV:
                list.add(new PdfName(Usable.PDF_FITBV));
                break;

            case PdfBookmark.FITV:
                list.add(new PdfName(Usable.PDF_FITV));
                break;

            default:
                throw new PdfException("invalid destination type");
        }
        list.add(new PdfFloat((float) pos));

        HashMap hm = new HashMap();
        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_GOTO_ACTION));
        hm.put(new PdfName(Usable.PDF_D), new PdfArray(list));

        b.addToActionList(new PdfDict(hm));
        addPrevious(b);

        return b;
    }

    public synchronized PdfBookmark addPrevious(String title, int pageNo,
        int fit) throws PdfException
    {
        PdfBookmark b = new PdfBookmark(title, pageNo);
        ArrayList list = new ArrayList();
        switch (fit)
        {
            case PdfBookmark.FITB:
                list.add(new PdfName(Usable.PDF_FITB));
                break;

            default:
                throw new PdfException("invalid destination type");
        }

        HashMap hm = new HashMap();
        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_GOTO_ACTION));
        hm.put(new PdfName(Usable.PDF_D), new PdfArray(list));

        b.addToActionList(new PdfDict(hm));
        addPrevious(b);

        return b;
    }

    /*
	 * Inserts ahead of this bookmark a new bookmark that links to a PDF
	 * rectangle specified by <code>rect</code> and located on page specified
	 * by <code>pageNo</code>. a
	 * 
	 * @param title
	 *            text to be used to display bookmark
	 * @param pageNo
	 *            number of page set to be displayed when bookmark is selected
	 * @param rect
	 *            PDF Rectangle to which the bookmark is to be linked.
	 * @return a new bookmark placed immediately ahead of this bookmark
	 * @since 1.0
	 * @exception PdfException
	 *                if an illegal argument is supplied. 
	 */
    public synchronized PdfBookmark addPrevious(String title, int pageNo,
        PdfRect rect) throws PdfException
    {
        return addPrevious(title, pageNo, rect.x, rect.height,
            rect.width, rect.y);
    }

    /*
	 * Inserts ahead of this bookmark a new bookmark that links to a
	 * rectanglular area inside page specified by <code>pageNo</code>. The
	 * rectangular area is specified in terms of its offsets from margins, as
	 * specified by <code>left</code>, <code>bottom</code>,
	 * <code>right</code>, and <code>top</code>. Page zoom value is set
	 * automatically.
	 * 
	 * @param title
	 *            text to be used to display the bookmark
	 * @param pageNo
	 *            number of page set to be displayed immediately after
	 *            navigating into the file
	 * @param left
	 *            number of pixels separating the rectangle from the left
	 *            margin
	 * @param bottom
	 *            number of pixels separating the rectangle from the bottom
	 *            margin
	 * @param right
	 *            number of pixels separating the rectangle from the right
	 *            margin
	 * @param top
	 *            number of pixels separating the rectangle from the top margin
	 * @return a new bookmark placed immediately ahead of this bookmark
	 * @since 1.0
	 * @exception PdfException
	 *                if an illegal argument is supplied. 
	 */
    public synchronized PdfBookmark addPrevious(String title, int pageNo,
        double x, double y, double width, double height)
        throws PdfException
    {
        PdfBookmark b = new PdfBookmark(title, pageNo);
        ArrayList list = new ArrayList();
        list.add(new PdfName(Usable.PDF_FITR));
        list.add(new PdfFloat((float) x));
        list.add(new PdfFloat((float) height));
        list.add(new PdfFloat((float) width));
        list.add(new PdfFloat((float) y));

        HashMap hm = new HashMap();
        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_GOTO_ACTION));
        hm.put(new PdfName(Usable.PDF_D), new PdfArray(list));

        b.addToActionList(new PdfDict(hm));
        addPrevious(b);

        return b;
    }
    
    /*
	 * Inserts after this bookmark a new bookmark that navigates to a page
	 * specified by <code>namedAction</code>.
	 * 
	 * @param namedAction
	 *			  action to be performed when the bookmark is selected            
	 * @param title
	 *            text to be used to display bookmark
	 * @return a new bookmark placed immediately ahead of this bookmark
	 * @since 1.0
	 * @exception PdfException
	 *                if an illegal argument is supplied. 
	 */
    public synchronized PdfBookmark addPrevious(int namedAction,
        String title) throws PdfException
    {
        PdfBookmark b = new PdfBookmark(title);
        HashMap hm = new HashMap();
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_NAMED));
        switch (namedAction)
        {
            case PdfAction.NAMED_FIRSTPAGE:
                hm.put(new PdfName(Usable.PDF_N), new PdfName(
                    Usable.PDF_FIRST_PAGE));
                break;
            case PdfAction.NAMED_LASTPAGE:
                hm.put(new PdfName(Usable.PDF_N), new PdfName(
                    Usable.PDF_LAST_PAGE));
                break;
            case PdfAction.NAMED_NEXTPAGE:
                hm.put(new PdfName(Usable.PDF_N), new PdfName(
                    Usable.PDF_NEXT_PAGE));
                break;
            case PdfAction.NAMED_PREVPAGE:
                hm.put(new PdfName(Usable.PDF_N), new PdfName(
                    Usable.PDF_PREV_PAGE));
                break;
            default:
                break;
        }
        b.addToActionList(new PdfDict(hm));
        addPrevious(b);
        
        return b;
    }
    
    /*
	 * Inserts ahead of this bookmark a new bookmark that links to a Uniform
	 * Resource Locator (URL) or is set to perform a Javascript action as
	 * specified by <code>uri</code>.
	 * 
	 * @param title
	 *            text to be used to display the bookmark
	 * @param javascriptOrURI
	 *            text containing the URL or the Javascript action
	 * @param actionType
	 *            action to be performed when the bookmark is selected
	 * @return a new bookmark placed immediately ahead of this bookmark
	 * @since 1.0
	 * @exception PdfException
	 *             if an illegal argument is supplied. 
	 */
    public synchronized PdfBookmark addPrevious(String title,
        String javascriptOrURI, int actionType) throws PdfException
    {
        PdfBookmark b = new PdfBookmark(title);
        HashMap hm = new HashMap();

        switch (actionType)
        {
            case PdfAction.URI:
                hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
                    Usable.PDF_ACTION));
                hm.put(new PdfName(Usable.PDF_S), new PdfName(
                    Usable.PDF_URI_ACTION));
                hm.put(new PdfName(Usable.PDF_URI_ACTION),
                    new PdfString(javascriptOrURI, true));
                break;

            case PdfAction.JAVASCRIPT:
                hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
                    Usable.PDF_ACTION));
                hm.put(new PdfName(Usable.PDF_S), new PdfName(
                    Usable.PDF_JAVASCRIPT_ACTION));
                hm.put(new PdfName(Usable.PDF_JAVASCRIPT_ACTION),
                    new PdfString(javascriptOrURI, true));
                break;

            default:
                throw new PdfException("Invalid action type");
        }

        b.addToActionList(new PdfDict(hm));
        addPrevious(b);

        return b;
    }

    /*
	 * Inserts ahead of this bookmark a new bookmark that is set to launch an
	 * application or file specified by <code>applicationToLaunch</code>. If
	 * <code>print</code> is set to <code>true</code> and the
	 * <code>applicationToLaunch</code> is set to a non-executable file, then
	 * a command will be given to the file's default application to print the
	 * file. For executable files, <code>print</code> is ignored.
	 * 
	 * @param title
	 *            text to be used to display bookmark
	 * @param applicationToLaunch
	 *            fully qualified path to the application or the file
	 * @param print
	 *            <code>true</code> sets the bookmark to give a command to the
	 *            file's default application to print the file;
	 *            <code>false</code> sets the bookmark to execute the file
	 *            with its default application but without any specific print
	 *            command
	 * @return a new bookmark placed immediately ahead of this bookmark
	 * @since 1.0
	 * @exception PdfException
	 *                if an illegal argument is supplied. 
	 */
    public synchronized PdfBookmark addPrevious(String title,
        String applicationOrFileToLaunch, boolean print)
        throws PdfException
    {
        PdfBookmark b = new PdfBookmark(title);
        HashMap hm = new HashMap();

        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_LAUNCH_ACTION));
        hm.put(new PdfName(Usable.PDF_F), new PdfString(
            applicationOrFileToLaunch, true));
        hm.put(new PdfName(Usable.PDF_O), print ? new PdfString(
            "print", false) : new PdfString("open", false));

        b.addToActionList(new PdfDict(hm));
        addPrevious(b);

        return b;
    }

    /*
	 * Inserts ahead of this bookmark a new bookmark that links to an external
	 * PDF file specified by <code>fileName</code>. The bookmark is set to
	 * display a page specified by <code>pageNo</code> immediately after
	 * navigating into the file. Parameter <code>newWindow</code> specifies
	 * whether a new window will be displayed when the bookmark is selected.
	 * 
	 * @param title
	 *            text to be used to display the bookmark
	 * @param fileName
	 *            file to be linked by the bookmark
	 * @param pageNo
	 *            number of page set to be displayed immediately after
	 *            navigating into the file
	 * @param newWindow
	 *            <code>true</code> sets the bookmark to display the file in a
	 *            new window; <code>false</code> sets the bookmark to dislay
	 *            the file in same window
	 * @return a new bookmark placed immediately ahead of this bookmark
	 * @since 1.0
	 * @exception PdfException
	 *                if an illegal argument is supplied. 
	 */
    public synchronized PdfBookmark addPrevious(String title,
        String pdfFilePath, int pageNo, boolean newWindow)
        throws PdfException
    {
        PdfBookmark b = new PdfBookmark(title);
        HashMap hm = new HashMap();

        ArrayList list = new ArrayList();
        list.add(new PdfInteger(pageNo));
        // Has to pass the Explicit destination properties

        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_REMOTEGOTO_ACTION));
        hm.put(new PdfName(Usable.PDF_F), new PdfString(pdfFilePath,
            true));
        hm.put(new PdfName(Usable.PDF_D), new PdfArray(list));
        hm.put(new PdfName(Usable.PDF_NEWWINDOW), new PdfBoolean(
            newWindow));

        b.addToActionList(new PdfDict(hm));
        addPrevious(b);

        return b;
    }
    
    /**
	 * Returns a newly created bookmark, inserted immediately after this
	 * bookmark, that leads to position specified by (<code>left</code>,
	 * <code>top</code>) on page specified by <code>pageNo</code> and also
	 * displays the page with a zoom factor specified by <code>zoom</code>.
	 * 
	 * @param title
	 *            text to be used to display bookmark
	 * @param pageNo
	 *            destination page number
	 * @param left
	 *            x-coordinate of the linked position
	 * @param top
	 *            y-coordinate of the linked position
	 * @param zoom
	 *            zoom factor to be applied when displaying the page
	 * @return a new bookmark placed immediately after this bookmark
	 * @throws PdfException
	 *             if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfBookmark.ExampleSyntax.htm#addNext_String_int_double_double_double">example</a>.
	 */
    public synchronized PdfBookmark addNext(String title, int pageNo,
        double left, double top, double zoom) throws PdfException
    {
        PdfBookmark b = new PdfBookmark(title, pageNo);
        ArrayList list = new ArrayList();
        list.add(new PdfName(Usable.PDF_XYZ));
        list.add(new PdfFloat((float) left));
        list.add(new PdfFloat((float) top));
        if (zoom <= 0)
        {
            list.add(new PdfNull());
        }
        else
        {
            list.add(new PdfFloat((float) (zoom / 100)));
        }

        HashMap hm = new HashMap();
        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_GOTO_ACTION));
        hm.put(new PdfName(Usable.PDF_D), new PdfArray(list));

        b.addToActionList(new PdfDict(hm));
        addNext(b);

        return b;
    }

    /**
	 * Returns a newly created bookmark, inserted immediately after this
	 * bookmark, that leads to page specified by <code>page</code> and has
	 * title specified by <code>title</code>.
	 * 
	 * @param title
	 *            text to be used to display bookmark
	 * @param pageNo
	 *            destination page number
	 * @return a new bookmark placed immediately after this bookmark
	 * @throws PdfException
	 *             if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfBookmark.ExampleSyntax.htm#addNext_String_int">example</a>.
	 */
    public synchronized PdfBookmark addNext(String title, int pageNo)
        throws PdfException
    {
        PdfBookmark b = new PdfBookmark(title, pageNo);
        ArrayList list = new ArrayList();
        list.add(new PdfName(Usable.PDF_FIT));
        HashMap hm = new HashMap();
        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_GOTO_ACTION));
        hm.put(new PdfName(Usable.PDF_D), new PdfArray(list));

        b.addToActionList(new PdfDict(hm));
        addNext(b);

        return b;
    }

    /**
     * Returns a newly created bookmark, inserted immediately after
     * this bookmark, that leads to destination specified by
     * <code>pos</code> and <code>fit</code> on page specified by
     * <code>pageNo</code>. 
     * 
     * <table border="1" cellpadding="5" 
     *      summary="fit constant, pos, how page is displayed">
     *  <tr>
     *   <th align="center" width="15%"><code>fit</code></th>
     *   <th align="center" width="15%"><code>pos</code></th>
     *   <th align="center" width="70%">Page Display</th>
     *  </tr>
     *  <tr>
     *   <td>{@link #FITH}</td>
     *   <td>vertical coordinate of destination</td>
     *   <td>
     *    <ul>
     *     <li>
     *      <code>pos</code> is positioned on the top edge of the 
     *      window</li>
     *     <li>Page is zoomed to tightly fit the entire width of 
     *       the page inside the window</li>
     *    </ul>
     *   </td>
     *  </tr>
     *  <tr>
     *   <td>{@link #FITBH}</td>
     *   <td>vertical coordinate of destination</td>
     *   <td>
     *    <ul>
     *     <li><code>pos</code> is positioned on top edge of the window</li>
     *     <li>Page is zoomed to tightly fit the entire width of its <a href="{@docRoot}/doc-files/glossary.htm#bounding_box"
     * target="_GnosticeGlossaryWindow">bounding box</a> inside the window</li>
     *    </ul>
     *   </td>
     *  </tr>
     *  <tr>
     *   <td>{@link #FITBV}</td>
     *   <td>horizontal coordinate of destination</td>
     *   <td>
     *    <ul>
     *     <li><code>pos</code> is positioned on the left edge of the window.</li>
     *     <li>Page is zoomed to tightly fit the entire height of its <a href="{@docRoot}/doc-files/glossary.htm#bounding_box"
     * target="_GnosticeGlossaryWindow">bounding box</a> inside the window</li>
     *    </ul>
     *   </td>
     *  </tr>
     *  <tr>
     *   <td>{@link #FITV}</td>
     *   <td>horizontal coordinate of destination</td>
     *   <td>
     *    <ul>
     *     <li><code>pos</code> is positioned on the left edge of the window.</li>
     *     <li>Page is zoomed to tightly fit the entire height of the page inside
     * the window.</li>
     *    </ul>
     *   </td>
     *  </tr>
     * </table>
     * 
     * @param title
     *            text to be used to display the bookmark
     * @param pageNo
     *            destination page number
     * @param pos
     *            horizontal or vertical coordinate of the bookmark's
     *            destination
     * @param fit
     *            constant determining how page needs to be displayed
     *            inside window
     * @return a new bookmark placed immediately after this bookmark
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfBookmark.ExampleSyntax.htm#addNext_String_int_double_int">example</a>.
     */
    public synchronized PdfBookmark addNext(String title, int pageNo,
        double pos, int fit) throws PdfException
    {
        PdfBookmark b = new PdfBookmark(title, pageNo);
        ArrayList list = new ArrayList();
        switch (fit)
        {
            case PdfBookmark.FITH:
                list.add(new PdfName(Usable.PDF_FITH));
                break;

            case PdfBookmark.FITBH:
                list.add(new PdfName(Usable.PDF_FITBH));
                break;

            case PdfBookmark.FITBV:
                list.add(new PdfName(Usable.PDF_FITBV));
                break;

            case PdfBookmark.FITV:
                list.add(new PdfName(Usable.PDF_FITV));
                break;

            default:
                throw new PdfException("invalid destination type");
        }
        list.add(new PdfFloat((float) pos));

        HashMap hm = new HashMap();
        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_GOTO_ACTION));
        hm.put(new PdfName(Usable.PDF_D), new PdfArray(list));

        b.addToActionList(new PdfDict(hm));
        addNext(b);

        return b;
    }

    /*
     * Returns a newly created bookmark, inserted immediately after
     * this bookmark, that leads to page specified by
     * <code>pageNo</code> and that zooms the page to tightly fit
     * entire height and width of its bounding box inside a window.
     * 
     * @param title
     *            text to be used to display the bookmark
     * @param pageNo
     *            destination page number
     * @param fit
     *            constant determining how page needs to be displayed
     *            inside window (Always is {@link #FITB})
     * @return a new bookmark placed immediately after this bookmark
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     */
    public synchronized PdfBookmark addNext(String title, int pageNo,
        int fit) throws PdfException
    {
        PdfBookmark b = new PdfBookmark(title, pageNo);
        ArrayList list = new ArrayList();
        switch (fit)
        {
            case PdfBookmark.FITB:
                list.add(new PdfName(Usable.PDF_FITB));
                break;

            default:
                throw new PdfException("invalid destination type");
        }

        HashMap hm = new HashMap();
        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_GOTO_ACTION));
        hm.put(new PdfName(Usable.PDF_D), new PdfArray(list));

        b.addToActionList(new PdfDict(hm));
        addNext(b);

        return b;
    }

    /*
	 * Inserts after this bookmark a new bookmark that links to a PDF rectangle
	 * specified by <code>rect</code> and located on page specified by
	 * <code>pageNo</code>.
	 * 
	 * @since 1.0
	 * @param title
	 *            text to be used to display the bookmark
	 * @param pageNo
	 *            number of page set to be displayed when bookmark is selected
	 * @param rect
	 *            PDF Rectangle to which the bookmark is to be linked
	 * @exception PdfException
	 *                if an illegal argument is supplied.
	 * @return a new bookmark placed immediately after this bookmark
	 */
    public synchronized PdfBookmark addNext(String title, int pageNo,
        PdfRect rect) throws PdfException
    {
        return addNext(title, pageNo, rect.x, rect.height,
            rect.width, rect.y);
    }

    /*
	 * Returns a newly created bookmark, inserted immediately after this
	 * bookmark, that leads to a rectanglular area inside page specified by
	 * <code>pageNo</code>. The rectangular area is defined in terms of its
	 * offsets from margins, as specified by <code>left</code>,
	 * <code>bottom</code>, <code>right</code>, and <code>top</code>.
	 * The zoom factor of the page is decided automatically.
	 * 
	 * @param title
	 *            text to be used to display the bookmark
	 * @param pageNo
	 *            destination page number
	 * @param left
	 *            number of points separating the rectangular area from left
	 *            margin
	 * @param bottom
	 *            number of points separating the rectangular area from bottom
	 *            margin
	 * @param right
	 *            number of points separating the rectangular area from right
	 *            margin
	 * @param top
	 *            number of points separating the rectangular area from top
	 *            margin
	 * @return a new bookmark placed immediately after this bookmark
	 * @throws PdfException
	 *             if an illegal argument is supplied.
	 * @since 1.0
	 *  
	 */
    public synchronized PdfBookmark addNext(String title, int pageNo,
        double x, double y, double width, double height)
        throws PdfException
    {
        PdfBookmark b = new PdfBookmark(title, pageNo);
        ArrayList list = new ArrayList();
        list.add(new PdfName(Usable.PDF_FITR));
        list.add(new PdfFloat((float) x));
        list.add(new PdfFloat((float) height));
        list.add(new PdfFloat((float) width));
        list.add(new PdfFloat((float) y));

        HashMap hm = new HashMap();
        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_GOTO_ACTION));
        hm.put(new PdfName(Usable.PDF_D), new PdfArray(list));

        b.addToActionList(new PdfDict(hm));
        addNext(b);

        return b;

    }
    
    /**
     * Returns a newly created bookmark, inserted immediately after
     * this bookmark, that leads to 
     * <a href="{@docRoot}/doc-files/glossary.htm#destination"
     * target="_GnosticeGlossaryWindow">destination</a> specified by
     * <code>namedAction</code> and also has title specified by
     * <code>title</code>.
     * 
     * @param namedAction
     *            action to be performed when bookmark is selected
     *            (Either {@link PdfAction#NAMED_FIRSTPAGE},
     *            {@link PdfAction#NAMED_LASTPAGE},
     *            {@link PdfAction#NAMED_PREVPAGE}, or
     *            {@link PdfAction#NAMED_NEXTPAGE}.)
     * @param title
     *            text to be used to display the bookmark
     * @return a new bookmark placed immediately after this bookmark
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfBookmark.ExampleSyntax.htm#addNext_int_String">example</a>.
     */
    public synchronized PdfBookmark addNext(int namedAction,
        String title) throws PdfException
    {
        PdfBookmark b = new PdfBookmark(title);
        HashMap hm = new HashMap();
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_NAMED));
        switch (namedAction)
        {
            case PdfAction.NAMED_FIRSTPAGE:
                hm.put(new PdfName(Usable.PDF_N), new PdfName(
                    Usable.PDF_FIRST_PAGE));
                break;
            case PdfAction.NAMED_LASTPAGE:
                hm.put(new PdfName(Usable.PDF_N), new PdfName(
                    Usable.PDF_LAST_PAGE));
                break;
            case PdfAction.NAMED_NEXTPAGE:
                hm.put(new PdfName(Usable.PDF_N), new PdfName(
                    Usable.PDF_NEXT_PAGE));
                break;
            case PdfAction.NAMED_PREVPAGE:
                hm.put(new PdfName(Usable.PDF_N), new PdfName(
                    Usable.PDF_PREV_PAGE));
                break;
            default:
                break;
        }
        b.addToActionList(new PdfDict(hm));
        addNext(b);
        
        return b;
    }

    /*
	 * Inserts ahead of this bookmark a new bookmark that links to a Uniform
	 * Resource Locator (URL) or is set to perform a Javascript action specified
	 * by <code>uri</code>.
	 * 
	 * @param title
	 *            text to be used to display the bookmark
	 * @param javascriptOrURI
	 *            text containing the URL or the Javascript action
	 * @param actionType
	 *            action to be performed when bookmark is selected
	 *            (Either {@link PdfAction#URI} or {@link        
	 *            PdfAction#JAVASCRIPT}.)
	 * @return a new bookmark placed immediately after this bookmark
	 * @since 1.0
	 * @exception PdfException
	 *                if an illegal argument is supplied. 
	 */
    public synchronized PdfBookmark addNext(String title,
        String javascriptOrURI, int actionType) throws PdfException
    {
        PdfBookmark b = new PdfBookmark(title);
        HashMap hm = new HashMap();

        switch (actionType)
        {
            case PdfAction.URI:
                hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
                    Usable.PDF_ACTION));
                hm.put(new PdfName(Usable.PDF_S), new PdfName(
                    Usable.PDF_URI_ACTION));
                hm.put(new PdfName(Usable.PDF_URI_ACTION),
                    new PdfString(javascriptOrURI, true));
                break;

            case PdfAction.JAVASCRIPT:
                hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
                    Usable.PDF_ACTION));
                hm.put(new PdfName(Usable.PDF_S), new PdfName(
                    Usable.PDF_JAVASCRIPT_ACTION));
                hm.put(new PdfName(Usable.PDF_JAVASCRIPT_ACTION),
                    new PdfString(javascriptOrURI, true));
                break;

            default:
                throw new PdfException("Invalid action type");
        }

        b.addToActionList(new PdfDict(hm));
        addNext(b);

        return b;
    }

    /*
	 * Inserts after this bookmark a new bookmark that is set to launch an
	 * application or file specified by <code>applicationToLaunch</code>. If
	 * <code>print</code> is set to <code>true</code> and the
	 * <code>applicationToLaunch</code> is set to a non-executable file, then
	 * a command will be given to the file's default application to print the
	 * file. For executable files, <code>print</code> is ignored.
	 * 
	 * @since 1.0
	 * @param title
	 *            text to be used to display bookmark
	 * @param applicationToLaunch
	 *            fully qualified path to the application or the file
	 * @param print
	 *            <code>true</code> sets the bookmark to give a command to the
	 *            file's default application to print the fle;
	 *            <code>false</code> sets the bookmark to execute the file
	 *            with its default application but without any specific print
	 *            command
	 * @exception PdfException
	 *                if an illegal argument is supplied.
	 * @return a new bookmark placed immediately after this bookmark
	 */
    public synchronized PdfBookmark addNext(String title,
        String applicationOrFileToLaunch, boolean print)
        throws PdfException
    {
        PdfBookmark b = new PdfBookmark(title);
        HashMap hm = new HashMap();

        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_LAUNCH_ACTION));
        hm.put(new PdfName(Usable.PDF_F), new PdfString(
            applicationOrFileToLaunch, true));
        hm.put(new PdfName(Usable.PDF_O), print ? new PdfString(
            "print", false) : new PdfString("open", false));

        b.addToActionList(new PdfDict(hm));
        addNext(b);

        return b;
    }

    /*
	 * Inserts after this bookmark a new bookmark that links to an external PDF
	 * file specified by <code>fileName</code>. The bookmark is set to
	 * display page specified by <code>pageNo</code> immediately after
	 * navigating into the file. Parameter <code>newWindow</code> specifies
	 * whether a new window will be created when the bookmark is selected.
	 * 
	 * @since 1.0
	 * @param title
	 *            text to be used to display the bookmark
	 * @param fileName
	 *            file to be linked by the bookmark
	 * @param pageNo
	 *            number of page set to be displayed immediately after
	 *            navigating into the file
	 * @param newWindow
	 *            <code>true</code> sets the bookmark to display the file in a
	 *            new window; false sets the bookmark to dislay the file in same
	 *            window
	 * @exception PdfException
	 *                if an illegal argument is supplied.
	 * @return a new bookmark placed immediately after this bookmark
	 */
    public synchronized PdfBookmark addNext(String title,
        String pdfFilePath, int pageNo, boolean newWindow)
        throws PdfException
    {
        PdfBookmark b = new PdfBookmark(title);
        HashMap hm = new HashMap();

        ArrayList list = new ArrayList();
        list.add(new PdfInteger(pageNo));
        // Has to pass the Explicit destination properties

        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_REMOTEGOTO_ACTION));
        hm.put(new PdfName(Usable.PDF_F), new PdfString(pdfFilePath,
            true));
        hm.put(new PdfName(Usable.PDF_D), new PdfArray(list));
        hm.put(new PdfName(Usable.PDF_NEWWINDOW), new PdfBoolean(
            newWindow));

        b.addToActionList(new PdfDict(hm));
        addNext(b);

        return b;
    }

    public synchronized PdfBookmark getParentNode()
    {
        return (PdfBookmark) getParent();
    }
    
    /**
	 * Returns bookmark that is immediately ahead of current bookmark.
	 * 
	 * @since 1.0
	 * @exception PdfException
	 *                if an illegal argument is supplied.
	 * @return bookmark placed immediately ahead of current bookmark.
	 */
    public synchronized PdfBookmark getPrevious()
    {
        int index = this.parent.childList.indexOf(this) - 1;
        if (index < 0)
        {
            return this;
        }
        try
        {
            return (PdfBookmark) this.parent.getChild(index);
        }
        catch (PdfException pdfe)
        {
            return null;
        } 
    }

    /**
	 * Returns bookmark that is immediately after current bookmark.
	 * 
	 * @since 1.0
	 * @exception PdfException
	 *                if an illegal argument is supplied.
	 * @return bookmark placed immediately after current bookmark
	 */
    public synchronized PdfBookmark getNext()
    {
        int index = this.parent.childList.indexOf(this) + 1;
        if (index == this.parent.childList.size())
        {
            return this;
        }
        try
        {
            return (PdfBookmark) this.parent.getChild(index);
        }
        catch (PdfException pdfe)
        {
            return null;
        } 
    }

    public synchronized void removeFirstchild() throws PdfException
    {
        this.removeChild(0);
    }
    
    public synchronized void removePrevious() throws PdfException
    {
        int index = this.parent.childList.indexOf(this) - 1;
        if (index < 0)
        {
            return;
        }
        this.parent.removeChild(index);
    }

    public synchronized void removeNext() throws PdfException
    {
        int index = this.parent.childList.indexOf(this) + 1;

        if (index == this.parent.childList.size())
        {
            return;
        }
        this.parent.removeChild(index);
    }

    /**
	 * Retrieves text currently used to display this bookmark.
	 * 
	 * @since 1.0
	 * @return text currently used to display the bookmark
	 * @see #setTitle
	 */
    public synchronized String getTitle()
    {
        return title == null ? rTitle.getString() : title;
    }

    /**
	 * Specifies text to be used to display this bookmark.
	 * 
	 * @since 1.0
	 * @param title
	 *            text to be used to display the bookmark
	 * @see #getTitle
	 */
    public synchronized void setTitle(String title)
    {
        this.title = title;
    }
    
    public synchronized PdfBookmark getFirstChild() throws PdfException
    {
        return (PdfBookmark) ((PdfNode) this).getChild(0);
    }

    protected synchronized void addToActionList(PdfDict actionDict)
    {
        if (actionDict == null)
        {
            return;
        }
        if (actionList == null)
        {
            actionList = new ArrayList();
        }
        actionList.add(actionDict);
    }
    
    /**
     * Adds a 
     * <a href="{@docRoot}/doc-files/glossary.htm#go_to_action" target="_GnosticeGlossaryWindow"
     * >go-to action</a> 
     * to this bookmark linking it to 
     * <a href="{@docRoot}/doc-files/glossary.htm#destination" target="_GnosticeGlossaryWindow"
     * >destination</a>
     * specified by position (<code>left</code>, <code>top</code>),
     * page <code>pageNo</code>, and magnification factor 
     * <code>zoom</code>.
     * 
     * @param pageNo
     *            destination page number
     * @param left
     *            x-coordinate of the top-left corner of the 
     *            window on the page
     * @param top
     *            y-coordinate of the top-left corner of the 
     *            window on the page
     * @param zoom
     *            magnification (zoom) factor to be applied when 
     *            displaying the page
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfBookmark.ExampleSyntax.htm#addActionGoTo_int_double_double_double">example</a>.
     */
    public synchronized void addActionGoto(int pageNo, double left,
        double top, double zoom) throws PdfException
    {
        removeAllActions(PdfAction.GOTO);
        this.pageNo = pageNo;
        ArrayList list = new ArrayList();
        list.add(new PdfName(Usable.PDF_XYZ));
        list.add(new PdfFloat((float) left));
        list.add(new PdfFloat((float) top));
        if (zoom <= 0)
        {
            list.add(new PdfNull());
        }
        else
        {
            list.add(new PdfFloat((float) (zoom / 100)));
        }

        HashMap hm = new HashMap();
        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_GOTO_ACTION));
        hm.put(new PdfName(Usable.PDF_D), new PdfArray(list));

        addToActionList(new PdfDict(hm));
    }


    /**
     * Adds a
     * <a href="{@docRoot}/doc-files/glossary.htm#go_to_action" target="_GnosticeGlossaryWindow"
     * >go-to action</a> 
     * to this bookmark linking it to page specified by 
     * <code>pageNo</code>.
     * 
     * @param pageNo
     *            number of the page
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfBookmark.ExampleSyntax.htm#addActionGoTo_int">example</a>.
     */
    public synchronized void addActionGoto(int pageNo)
        throws PdfException
    {
        this.pageNo = pageNo;
        removeAllActions(PdfAction.GOTO);
        ArrayList list = new ArrayList();
        list.add(new PdfName(Usable.PDF_FITH));
        list.add(new PdfInteger(0));
        
        HashMap hm = new HashMap();
        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_GOTO_ACTION));
        hm.put(new PdfName(Usable.PDF_D), new PdfArray(list));

        addToActionList(new PdfDict(hm));
    }
    
    /**
     * Adds a
     * <a href="{@docRoot}/doc-files/glossary.htm#go_to_action" target="_GnosticeGlossaryWindow"
     * >go-to action</a> to this bookmark linking it to  
     * <a href="{@docRoot}/doc-files/glossary.htm#destination" target="_GnosticeGlossaryWindow"
     * >destination</a> specified by <code>pos</code> 
     * and <code>fit</code> on page specified by <code>pageNo</code>.
     * 
     * <br>&nbsp;
     * 
     * <table border="1" cellpadding="5" summary="fit, pos, How
     * page is displayed">
     *  <tr>
     *   <th align="center" width="15%"><code>fit</code> </th>
     *   <th align="center" width="15%"><code>pos</code> </th>
     *   <th align="center" width="70%">How page is displayed </th>
     *  </tr>
     *  <tr>
     *   <td>{@link #FITH}</td>
     *   <td>
     *    vertical coordinate of top-left corner of window on the
     *    page
     *   </td>
     *   <td>
     *    <ul>
     *     <li>
     *      <code>pos</code> is positioned on the top edge of the
     *      window 
     *     </li>
     *     <li>
     *      Page magnification (zoom) factor is adjusted to tightly
     *      fit the entire width of the page inside the window 
     *     </li>
     *    </ul>
     *   </td>
     *  </tr>
     *  <tr>
     *   <td>{@link #FITBH}</td>
     *   <td>
     *    vertical coordinate of top-left corner of window on the
     *    page
     *   </td>
     *   <td>
     *    <ul>
     *     <li>
     *      <code>pos</code> is positioned on top edge of the window
     *     </li>
     *     <li>
     *      Page magnification (zoom) factor is adjusted to tightly
     *      fit the entire width of its 
     *      <a href="{@docRoot}/doc-files/glossary.htm#bounding_box" target="_GnosticeGlossaryWindow"
     *      >bounding box</a> 
     *      inside the window 
     *     </li>
     *    </ul>
     *   </td>
     *  </tr>
     *  <tr>
     *   <td>{@link #FITBV}</td>
     *   <td>
     *    horizontal coordinate of top-left corner of window on
     *    the page
     *   </td>
     *   <td>
     *    <ul>
     *     <li>
     *     <code>pos</code> is positioned on the left edge of the
     *     window 
     *     </li>
     *     <li>
     *      Page magnification (zoom) factor is adjusted to tightly
     *      fit the entire height of its 
     *      <a href="{@docRoot}/doc-files/glossary.htm#bounding_box" target="_GnosticeGlossaryWindow"
     *      >bounding box</a> 
     *      inside the window
     *     </li>
     *    </ul>
     *   </td>
     *  </tr>
     *  <tr>
     *   <td>{@link #FITV}</td>
     *   <td>
     *    horizontal coordinate of top-left corner of window on
     *    the page
     *   </td>
     *   <td>
     *    <ul>
     *     <li>
     *      <code>pos</code> is positioned on the left edge of the
     *      window
     *     </li>
     *     <li>
     *      Page magnification (zoom) factor is adjusted to tightly
     *      fit the entire height of the page inside the window 
     *     </li>
     *    </ul>
     *   </td>
     *  </tr>
     * </table>
     * 
     * @param pageNo
     *            destination page number
     * @param pos
     *            horizontal or vertical coordinate of top-left 
     *            corner of the window on the page
     * @param fit
     *            constant for specifying magnification (zoom) factor 
     *            of the page
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfBookmark.ExampleSyntax.htm#addActionGoTo_int_double_int">example</a>.
     */
    public synchronized void addActionGoto(int pageNo, double pos,
        int fit) throws PdfException
    {
        this.pageNo = pageNo;
        removeAllActions(PdfAction.GOTO);
        ArrayList list = new ArrayList();
        switch (fit)
        {
            case PdfBookmark.FITH:
                list.add(new PdfName(Usable.PDF_FITH));
                break;

            case PdfBookmark.FITBH:
                list.add(new PdfName(Usable.PDF_FITBH));
                break;

            case PdfBookmark.FITBV:
                list.add(new PdfName(Usable.PDF_FITBV));
                break;

            case PdfBookmark.FITV:
                list.add(new PdfName(Usable.PDF_FITV));
                break;

            default:
                throw new PdfException("invalid destination type");
        }
        list.add(new PdfFloat((float) pos));

        HashMap hm = new HashMap();
        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_GOTO_ACTION));
        hm.put(new PdfName(Usable.PDF_D), new PdfArray(list));

        addToActionList(new PdfDict(hm));
    }
    
    /**
     * Sets this bookmark to lead to page specified by
     * <code>pageNo</code> and to zoom the page to tightly fit
     * entire height and width of its bounding box inside a window.
     * 
     * @param pageNo
     *            destination page number
     * @param fit
     *            constant determining how page needs to be displayed
     *            inside window (Always is {@link #FITB})
     * @throws PdfException
     *            if an illegal argument (constant other than
     *            {@link #FITB}) is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfBookmark.ExampleSyntax.htm#addActionGoTo_int_int">example</a>.
     */
    public synchronized void addActionGoto(int pageNo, int fit)
        throws PdfException
    {
        this.pageNo = pageNo;
        removeAllActions(PdfAction.GOTO);
        ArrayList list = new ArrayList();
        switch (fit)
        {
            case PdfBookmark.FITB:
                list.add(new PdfName(Usable.PDF_FITB));
                break;

            default:
                throw new PdfException("invalid destination type");
        }

        HashMap hm = new HashMap();
        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_GOTO_ACTION));
        hm.put(new PdfName(Usable.PDF_D), new PdfArray(list));

        addToActionList(new PdfDict(hm));
    }
    
    /**
     * Sets this bookmark to lead to a rectangle specified by
     * <code>rect</code> on page specified by <code>pageNo</code>.
     * 
     * @param pageNo
     *            destination page number
     * @param rect
     *            rectangle on destination page
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfBookmark.ExampleSyntax.htm#addActionGoTo_int_PdfRect">example</a>.
     */
    public synchronized void addActionGoto(int pageNo, PdfRect rect)
        throws PdfException
    {
        addActionGoto(pageNo, rect.x, rect.height, rect.width, rect.y);
    }

    public synchronized void addActionGoto(int pageNo, double x,
        double y, double width, double height) throws PdfException
    {
        this.pageNo = pageNo;
        removeAllActions(PdfAction.GOTO);
        ArrayList list = new ArrayList();
        list.add(new PdfName(Usable.PDF_FITR));
        list.add(new PdfFloat((float) x));
        list.add(new PdfFloat((float) height));
        list.add(new PdfFloat((float) width));
        list.add(new PdfFloat((float) y));

        HashMap hm = new HashMap();
        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_GOTO_ACTION));
        hm.put(new PdfName(Usable.PDF_D), new PdfArray(list));

        addToActionList(new PdfDict(hm));
    }
    
    public synchronized void addActionLaunch(
        String applicationOrFileToLaunch, boolean print)
    {
        HashMap hm = new HashMap();

        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_LAUNCH_ACTION));
        hm.put(new PdfName(Usable.PDF_F), new PdfString(
            applicationOrFileToLaunch, true));
        hm.put(new PdfName(Usable.PDF_O), print ? new PdfString(
            "print", false) : new PdfString("open", false));
        hm.put(new PdfName(Usable.PDF_NEWWINDOW), PdfBoolean.TRUE);

        addToActionList(new PdfDict(hm));
    }

    /**
     * Sets this bookmark to launch a uniform resource indicator
     * specified by <code>uri</code>. If the bookmark is linked to
     * a page destination, then that page is also displayed.
     * 
     * @param uri
     *            uniform resource indicator set to be launched when
     *            bookmark is selected
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfBookmark.ExampleSyntax.htm#addActionURI">example</a>.
     */
    public synchronized void addActionURI(String uri)
    {
        HashMap hm = new HashMap();
        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_URI_ACTION));
        hm.put(new PdfName(Usable.PDF_URI_ACTION), new PdfString(uri,
            true));
        
        addToActionList(new PdfDict(hm));
    }
    
    /**
     * Sets this bookmark to lead to a 
     * <a href="{@docRoot}/doc-files/glossary.htm#destination"
     * target="_GnosticeGlossaryWindow">destination</a> specified by
     * <code>namedAction</code>.
     * 
     * @param namedAction
     *            action to be performed when bookmark is selected
     *            (Either {@link PdfAction#NAMED_FIRSTPAGE},
     *            {@link PdfAction#NAMED_LASTPAGE},
     *            {@link PdfAction#NAMED_NEXTPAGE} or
     *            {@link PdfAction#NAMED_PREVPAGE}.)
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfBookmark.ExampleSyntax.htm#addActionNamed">example</a>.
     */
    public synchronized void addActionNamed(int namedAction)
    {
        HashMap hm = new HashMap();
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_NAMED));
        switch (namedAction)
        {
            case PdfAction.NAMED_FIRSTPAGE:
                hm.put(new PdfName(Usable.PDF_N), new PdfName(
                    Usable.PDF_FIRST_PAGE));
                break;
            case PdfAction.NAMED_LASTPAGE:
                hm.put(new PdfName(Usable.PDF_N), new PdfName(
                    Usable.PDF_LAST_PAGE));
                break;
            case PdfAction.NAMED_NEXTPAGE:
                hm.put(new PdfName(Usable.PDF_N), new PdfName(
                    Usable.PDF_NEXT_PAGE));
                break;
            case PdfAction.NAMED_PREVPAGE:
                hm.put(new PdfName(Usable.PDF_N), new PdfName(
                    Usable.PDF_PREV_PAGE));
                break;
            default:
                break;
        }
        addToActionList(new PdfDict(hm));
    }

    public synchronized void addActionJavaScript(String javascript)
    {
        HashMap hm = new HashMap();
        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_JAVASCRIPT_ACTION));
        hm.put(new PdfName(Usable.PDF_JS),
            new PdfString(javascript, false));
        
        addToActionList(new PdfDict(hm));
    }

    public synchronized void addActionRemoteGoTo(String pdfFilePath,
        RemoteGoTo rGoTo, boolean newWindow) throws PdfException
    {
        HashMap hm = new HashMap();

        ArrayList list = new ArrayList();
        list.add(new PdfInteger(rGoTo.pageNo - 1));

        if(rGoTo.fit >= 0)
        {
            switch (rGoTo.fit)
            {
                case PdfBookmark.FITH:
                    list.add(new PdfName(Usable.PDF_FITH));
                    list.add(new PdfFloat((float) rGoTo.pos));
                    break;

                case PdfBookmark.FITBH:
                    list.add(new PdfName(Usable.PDF_FITBH));
                    list.add(new PdfFloat((float) rGoTo.pos));
                    break;

                case PdfBookmark.FITBV:
                    list.add(new PdfName(Usable.PDF_FITBV));
                    list.add(new PdfFloat((float) rGoTo.pos));
                    break;

                case PdfBookmark.FITV:
                    list.add(new PdfName(Usable.PDF_FITV));
                    list.add(new PdfFloat((float) rGoTo.pos));
                    break;
                    
                case PdfBookmark.FITB:
                    list.add(new PdfName(Usable.PDF_FITB));
                    break;  
                    
                default:
                    throw new PdfException("invalid destination type");    
            }
        }
        else if (rGoTo.fit >= -1)
        {
            if (rGoTo.left == 0 && rGoTo.top == 0 && rGoTo.right == 0
                && rGoTo.bottom == 0)
            {
                list.add(new PdfName(Usable.PDF_FITH));
            }
            else
            {
                list.add(new PdfName(Usable.PDF_FITR));
                list.add(new PdfFloat((float) rGoTo.left));
                list.add(new PdfFloat((float) rGoTo.top));
                list.add(new PdfFloat((float) rGoTo.right));
                list.add(new PdfFloat((float) rGoTo.bottom));
            }
        }
        
        hm.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ACTION));
        hm.put(new PdfName(Usable.PDF_S), new PdfName(
            Usable.PDF_REMOTEGOTO_ACTION));
        hm.put(new PdfName(Usable.PDF_F), new PdfString(pdfFilePath,
            true));
        hm.put(new PdfName(Usable.PDF_D), new PdfArray(list));
        hm.put(new PdfName(Usable.PDF_NEWWINDOW), new PdfBoolean(
            newWindow));

        addToActionList(new PdfDict(hm));
    }

    private boolean removeActionGoto()
    {
        int count = -1;
        boolean found = false;
        Iterator iter = actionList.iterator();
        PdfName actionType = new PdfName(Usable.PDF_S);
        PdfName actionName;

        while (iter.hasNext())
        {
            ++count;
            actionName = (PdfName) ((PdfDict) iter.next()).getMap()
                .get(actionType); 
            if (actionName.getString().equalsIgnoreCase(
                Usable.PDF_GOTO_ACTION))
            {
                found = true;
                break;
            }
        }
        if (found)
        {
            actionList.remove(count);
        }
        
        return found;
    }
    
    private boolean removeActionRemoteGoTo()
    {
        int count = -1;
        boolean found = false;
        Iterator iter = actionList.iterator();
        PdfName actionType = new PdfName(Usable.PDF_S);
        PdfName actionName;

        while (iter.hasNext())
        {
            ++count;
            actionName = (PdfName) ((PdfDict) iter.next()).getMap()
                .get(actionType);
            if (actionName.getString().equalsIgnoreCase(
                Usable.PDF_REMOTEGOTO_ACTION))
            {
                found = true;
                break;
            }
        }
        if (found)
        {
            actionList.remove(count);
        }
        
        return found;
    }
    
    private boolean removeActionFirstPage()
    {
        int count = -1;
        boolean found = false;
        Iterator iter = actionList.iterator();
        PdfName actionType = new PdfName(Usable.PDF_S);
        PdfName actionName;

        while (iter.hasNext())
        {
            ++count;
            actionName = (PdfName) ((PdfDict) iter.next()).getMap()
                .get(actionType);
            if (actionName.getString().equalsIgnoreCase(
                Usable.PDF_FIRST_PAGE))
            {
                found = true;
                break;
            }
        }
        if (found)
        {
            actionList.remove(count);
        }
        
        return found;
    }

    private boolean removeActionLastPage()
    {
        int count = -1;
        boolean found = false;
        Iterator iter = actionList.iterator();
        PdfName actionType = new PdfName(Usable.PDF_S);
        PdfName actionName;

        while (iter.hasNext())
        {
            ++count;
            actionName = (PdfName) ((PdfDict) iter.next()).getMap()
                .get(actionType);
            if (actionName.getString().equalsIgnoreCase(
                Usable.PDF_LAST_PAGE))
            {
                found = true;
                break;
            }
        }
        if (found)
        {
            actionList.remove(count);
        }
        
        return found;
    }

    private boolean removeActionNextPage()
    {
        int count = -1;
        boolean found = false;
        Iterator iter = actionList.iterator();
        PdfName actionType = new PdfName(Usable.PDF_S);
        PdfName actionName;

        while (iter.hasNext())
        {
            ++count;
            actionName = (PdfName) ((PdfDict) iter.next()).getMap()
                .get(actionType);
            if (actionName.getString().equalsIgnoreCase(
                Usable.PDF_NEXT_PAGE))
            {
                found = true;
                break;
            }
        }
        if (found)
        {
            actionList.remove(count);
        }
        
        return found;
    }

    private boolean removeActionPrevPage()
    {
        int count = -1;
        boolean found = false;
        Iterator iter = actionList.iterator();
        PdfName actionType = new PdfName(Usable.PDF_S);
        PdfName actionName;

        while (iter.hasNext())
        {
            ++count;
            actionName = (PdfName) ((PdfDict) iter.next()).getMap()
                .get(actionType);
            if (actionName.getString().equalsIgnoreCase(
                Usable.PDF_PREV_PAGE))
            {
                found = true;
                break;
            }
        }
        if (found)
        {
            actionList.remove(count);
        }
        
        return found;
    }

    private boolean removeActionLaunch()
    {
        int count = -1;
        boolean found = false;
        Iterator iter = actionList.iterator();
        PdfName actionType = new PdfName(Usable.PDF_S);
        PdfName actionName;

        while (iter.hasNext())
        {
            ++count;
            actionName = (PdfName) ((PdfDict) iter.next()).getMap()
                .get(actionType);
            if (actionName.getString().equalsIgnoreCase(
                Usable.PDF_LAUNCH_ACTION))
            {
                found = true;
                break;
            }
        }
        if (found)
        {
            actionList.remove(count);
        }
        
        return found;
    }

    private boolean removeActionURI()
    {
        int count = -1;
        boolean found = false;
        Iterator iter = actionList.iterator();
        PdfName actionType = new PdfName(Usable.PDF_S);
        PdfName actionName;

        while (iter.hasNext())
        {
            ++count;
            actionName = (PdfName) ((PdfDict) iter.next()).getMap()
                .get(actionType);
            if (actionName.getString().equalsIgnoreCase(
                Usable.PDF_URI_ACTION))
            {
                found = true;
                break;
            }
        }
        if (found)
        {
            actionList.remove(count);
        }
        
        return found;
    }

    private boolean removeActionJavaScript()
    {
        int count = -1;
        boolean found = false;
        Iterator iter = actionList.iterator();
        PdfName actionType = new PdfName(Usable.PDF_S);
        PdfName actionName;

        while (iter.hasNext())
        {
            ++count;
            actionName = (PdfName) ((PdfDict) iter.next()).getMap()
                .get(actionType);
            if (actionName.getString().equalsIgnoreCase(
                Usable.PDF_JAVASCRIPT_ACTION))
            {
                found = true;
                break;
            }
        }
        if (found)
        {
            actionList.remove(count);
        }
        
        return found;
    }
    
    public synchronized void removeAllActions()
    {
        this.actionList =  new ArrayList();
    }
    
    public synchronized void removeAction(int type) throws PdfException
    {
        switch(type)
        {
            case PdfAction.GOTO:
                removeActionGoto();
                break;
            case PdfAction.JAVASCRIPT:
                removeActionJavaScript();
                break;
            case PdfAction.LAUNCH:
                removeActionLaunch();
                break;
            case PdfAction.URI:
                removeActionURI();
                break;
            case PdfAction.REMOTE_GOTO:
                removeActionRemoteGoTo();
                break;
            case PdfAction.NAMED_FIRSTPAGE:
                removeActionFirstPage();
                break;
            case PdfAction.NAMED_LASTPAGE:
                removeActionLastPage();
                break;
            case PdfAction.NAMED_NEXTPAGE:
                removeActionNextPage();
                break;
            case PdfAction.NAMED_PREVPAGE:
                removeActionPrevPage();
                break;
            default:
                throw new PdfException("Invalid action type.");
        }
    }
    
    public synchronized void removeAllActions(int type)
        throws PdfException 
    {
        boolean removed = false;
        switch(type)
        {
            case PdfAction.GOTO:
                do
                {
                    removed = removeActionGoto();
                } while (removed);
                break;
            case PdfAction.JAVASCRIPT:
                do
                {
                    removed = removeActionJavaScript();
                } while (removed);
                break;
            case PdfAction.LAUNCH:
                do
                {
                    removed = removeActionLaunch();
                } while (removed);
                break;
            case PdfAction.URI:
                do
                {
                    removed = removeActionURI();
                } while (removed);
                break;
            case PdfAction.REMOTE_GOTO:
                do
                {
                    removed = removeActionRemoteGoTo();
                } while (removed);
                break;
            default:
                throw new PdfException("Invalid action type.");
        }
    }
    
    protected Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    /**
     * Retrieves color currently used to display this bookmark.
     * 
     * @since 1.0
     * @return color currently used to display the bookmark
     * @see #setColor
     */
    public synchronized Color getColor()
    {
        return color;
    }

    /**
     * Specifies color to be used to display this bookmark.
     * 
     * @since 1.0
     * @param color
     *            color to be used to display the bookmark
     * @see #getColor
     * @see #setColorAndStyle
     */
    public synchronized void setColor(Color color)
    {
        this.color = color;
    }

    /**
     * Retrieves font style currently used to display this bookmark.
     * 
     * @since 1.0
     * @return style currently used to display the bookmark 
     * @see #setStyle
     */
    public synchronized int getStyle()
    {
        return style;
    }

    /**
     * Specifies font style to be used to display the bookmark.
     * 
     * @since 1.0
     * @param style
     *            style to be used to display the bookmark
     * @see #getStyle
     * @see #setColorAndStyle
     */
    public synchronized void setStyle(int style)
    {
        this.style = style;
    }
    
    /**
     * Specifies font color and style to be used to display this
     * bookmark. Combines the functions of the methods
     * <code>setColor</code> and <code>setStyle</code>.
     * 
     * @since 1.0
     * @param c
     *            color to be used to display the bookmark
     * @param s
     *            style to be used to display the bookmark
     * @see #setColor
     * @see #setStyle
     */
    public synchronized void setColorAndStyle(Color c, int s)
    {
        this.color = c;
        this.style = s;
    }
}
