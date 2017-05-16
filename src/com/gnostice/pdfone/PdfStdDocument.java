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
import java.awt.Point;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gnostice.pdfone.encodings.PdfEncodings;
import com.gnostice.pdfone.filters.PdfFilter;
import com.gnostice.pdfone.filters.PdfFlateFilter;
import com.gnostice.pdfone.fonts.PdfFont;
import com.gnostice.pdfone.graphics.PdfBrush;
import com.gnostice.pdfone.graphics.PdfPen;

class PdfStdDocument implements Usable
{
    protected static final int WRITING_MODE = 1;
    
    protected static final int READING_MODE = 2;
    
    /**
     * PDF version 1.4
     */
    public static final String VERSION_1_4 = "1.4";

    /**
     * PDF version 1.5
     */    
    public static final String VERSION_1_5 = "1.5";
    
    /**
     * PDF version 1.6
     */
    public static final String VERSION_1_6 = "1.6";
    
    private StringBuffer password;
    
    private boolean[] flags;
    
    private int pageRead;
    
    private Random random;
    
    protected Method onRenameField;
    
    ArrayList filters;
    
    boolean addDefaultFilter = true;
    
    protected PdfDict infoDict;

    protected PdfDict addnDict;

    protected List actionList;

    protected List additionalList;

    protected int pageModeValue;
    
    protected int pageLayoutValue;

    protected PdfWriter writer = null;
    
    protected PdfReader reader = null;

    protected PdfCatalog catalog;

    protected PdfPageTree pageTree;

    protected PdfBookmarkTree bookmarkTree;

    protected PdfCrossRefTable crossRefTable;
    
    protected PdfCrossReferenceStream crossRefStream;

    protected PdfArray procSet;

    protected HashMap fontMap;
    
    protected HashMap xObjMap; //for image

    protected int objectRun;

    protected long bytesWritten;

    protected long[] offset;

    protected boolean isWritten;
    
    protected Hashtable objStreamId;
    
    protected Hashtable objStreamPos;

    protected int measurementUnit;
    
    protected int compressionLevel;
    
    protected int mode = 0; //reading or writing
    
    protected String version;
    
    protected int versionId;
    
    protected PdfDict encryptDict;
    
    protected boolean encryptDocument; 
    
    private boolean openAfterSave;
    
    /*private boolean emailAfterSave;*/
    
    protected PdfArray fileID;
    
    protected PdfEncryption encrypto;
    
    protected Hashtable namedDestinations;
    
    protected PdfObjectStream outlineStream;
    
    protected PdfObjectStream fontStream;

    protected Hashtable unknownObjMaps;
    
    /**
     * Document property determining whether a new page will be
     * automatically appended when content written to current page
     * starts overflowing the page's confines.
     */
    public boolean autoPaginate;
    
    protected int currentPage;

    protected Hashtable objMaps;
    //objMaps of all docs
    
    protected PdfPen pen;

    protected PdfBrush brush;
    
    protected boolean isOwner;
    
    protected boolean writingUnknownObjs;
    
    protected Hashtable writtenObjs;

    protected ArrayList unknownObjOffset;
    
    /*protected Vector eMails;*/
    
	public PdfStdDocument(PdfWriter writer) throws PdfException
    {
        if (writer == null)
        {
            throw new PdfException(
                "Illegal argument to Document constructor" +
                " [writer == null]");
        }
        if (writer.inUse)
        {
            throw new PdfException(
                "PdfWriter already in use by other Document.");
        }
        if (mode == READING_MODE)
        {
            throw new PdfException(
                "Document already opened in reading mode.");
        }
        this.writer = writer;
        writer.inUse = true;
        this.mode = WRITING_MODE;
        objMaps = new Hashtable();
        bytesWritten = 0;
        objectRun = 1;
        catalog = null;
        crossRefTable = null;
        procSet = null;
        fontMap = null;
        xObjMap = null;
        pageTree = null;
        bookmarkTree = null;
        actionList = null;
        additionalList = null;
        namedDestinations = null;
        outlineStream = null;
        fontStream = null;
        pageModeValue = PdfPageMode.USENONE;
        pageLayoutValue = PdfPageLayout.ONE_COLUMN;
        random = new Random();
        openAfterSave = false;
        encryptDocument  = false;
        measurementUnit = PdfMeasurement.MU_POINTS;
        compressionLevel = PdfFlateFilter.BEST_COMPRESSION;
        version = VERSION_1_5;
        infoDict = new PdfDict(new HashMap());
        autoPaginate = true;
        currentPage = 1;
        isOwner = true;
    }

    public PdfStdDocument(PdfReader reader) throws IOException,
        PdfException
    {
        if (reader == null)
        {
            throw new PdfException(
                "Illegal argument to Document constructor" +
                " [reader == null]");
        }
        if (mode == WRITING_MODE)
        {
            throw new PdfException(
                "Document already opened in writing mode");
        }
        this.reader = reader;
        this.mode = READING_MODE;
        objMaps = new Hashtable();
        bytesWritten = 0;
        objectRun = 1;
        catalog = null;
        crossRefTable = null;
        procSet = null;
        fontMap = null;
        xObjMap = null;
        pageTree = null;
        bookmarkTree = null;
        actionList = null;
        additionalList = null;
        namedDestinations = null;
        outlineStream = null;
        fontStream = null;
        pageModeValue = PdfPageMode.USENONE;
        pageLayoutValue = PdfPageLayout.ONE_COLUMN;
        random = new Random();
        openAfterSave = false;
        encryptDocument  = false;
        measurementUnit = PdfMeasurement.MU_POINTS;
        compressionLevel = PdfFlateFilter.BEST_COMPRESSION;
        version = VERSION_1_5;
        autoPaginate = true;
        currentPage = 1;
        isOwner = true;
        read();
    }
    
    protected void read() throws IOException, PdfException
    {
        if (mode != READING_MODE)
        {
            return;
        }
        reader.readCrossRefInfo(reader.readStartXref());
        reader.initialize();
        readEncryptDict();
        if(reader.onPassword != null && reader.encryptDict != null)
        {
            try
            {
                password = new StringBuffer();
                flags = new boolean[1];
                reader.onPassword.invoke(null, new Object[] { this,
                    password, flags });
                verifyPassword(password.toString());
                encryptDocument = true;
            }
            catch(IllegalAccessException iae)
            {
                throw new PdfException("method not accessible");
            }
            catch(IllegalArgumentException iare)
            {
                throw new PdfException("invalid method signature");
            }
            catch(NullPointerException npe)
            {
                throw new PdfException("method not static");
            }
            catch(InvocationTargetException ite)
            {
                throw new PdfException(ite.getMessage());
            }
        }
        else if(reader.encryptDict != null)
        {
            verifyPassword("");
            encryptDocument = true;
        }
        readCatalog();
        readInfoDict(); //can be optional
        readPageDicts();
        if ((reader.properties & PdfReader.READ_OUTLINES) ==
            PdfReader.READ_OUTLINES)
        {
            readBookmarks();
        }
        parseCatalog(); //can be optional
        
        if (infoDict == null)
        {
            infoDict = new PdfDict(new HashMap());
        }
    }
    
    protected void readPageDicts() throws IOException, PdfException
    {
        if (mode != READING_MODE)
        {
            return;
        }
        PdfObject pages = catalog.getDictionary().getValue(
            new PdfName(PDF_PAGES));
        if (pages == null)
        {
            return;
        }
        pages = reader.getObject(pages);
        if (! (pages instanceof PdfDict))
        {
            throw new PdfBadFileException(
                "PageTree root is not a dictionary object.");
        }
        synchronized(this)
        {
            if (pageTree == null)
            {
                pageTree = new PdfPageTree(this);
                pageTree.root = new PdfIntermediatePageNode();
                pageTree.root.dict = (PdfDict)pages;
            }
        }
        buildPageTree(pageTree.root);
        pageTree.initialize();
    }
    
    protected void buildPageTree(PdfNode parent) throws IOException,
        PdfException
    {
        PdfNode child = null;
        PdfObject childDict = null;
        PdfDict parentDict = parent.dict;
        PdfObject kids = parentDict.getValue(new PdfName(PDF_KIDS));
        try {
        if (kids != null)
        {
            kids = reader.getObject(kids);
            ArrayList list = (ArrayList) ((PdfArray) kids)
                .getList();
            for (Iterator iter = list.iterator(); iter.hasNext();)
            {
                childDict = (PdfObject) iter.next();
                childDict = reader.getObject(childDict);
                PdfObject type = ((PdfDict) childDict)
                    .getValue(PdfBookmark.TYPE);
                type = reader.getObject(type);
                if (((PdfName) type).getString()
                    .equalsIgnoreCase(PDF_PAGES))
                {
                    child = new PdfIntermediatePageNode();
                }
                else if (((PdfName) type).getString()
                    .equalsIgnoreCase(PDF_PAGE))
                {
                    child = new PdfPage();
                }
                child.dict = (PdfDict) childDict;
				parent.addChild(child);

                if (child instanceof PdfStdPage)
                {
                    ((PdfStdPage) child).read(this);
                    if (this.reader.onPageRead != null)
                    {
                        invokePageCreateEvent((PdfStdPage) child);
                    }
                }

                buildPageTree(child);
            }
        }
        }//of try
        catch (ClassCastException cce)
        {
            throw new PdfBadFileException(
                "Improper object found in Page tree.");
        }
    }
    
    private void invokePageCreateEvent(PdfStdPage page)
        throws PdfException
    {
        try
        {
            double[] margins = new double[6];
            int[] measurementUnit = new int[] { PdfMeasurement.MU_POINTS };

            reader.onPageRead.invoke(null, new Object[] { this,
                margins, measurementUnit,
                new int[] { ++this.pageRead },
                new double[] { page.pageWidth, page.pageHeight } });
            
            page.pageHeaderHeight = PdfMeasurement.convertToPdfUnit(
                measurementUnit[0], margins[0]);
            page.pageFooterHeight = PdfMeasurement.convertToPdfUnit(
                measurementUnit[0], margins[1]);
            page.pageLeftMargin = PdfMeasurement.convertToPdfUnit(
                measurementUnit[0], margins[2]);
            page.pageTopMargin = PdfMeasurement.convertToPdfUnit(
                measurementUnit[0], margins[3]);
            page.pageRightMargin = PdfMeasurement.convertToPdfUnit(
                measurementUnit[0], margins[4]);
            page.pageBottomMargin = PdfMeasurement.convertToPdfUnit(
                measurementUnit[0], margins[5]);
        }
        catch (IllegalAccessException iae)
        {
            throw new PdfException("method not accessible");
        }
        catch (IllegalArgumentException iare)
        {
            throw new PdfException("invalid method signature");
        }
        catch (NullPointerException npe)
        {
            throw new PdfException("method not static");
        }
        catch (InvocationTargetException ite)
        {
            throw new PdfException(ite.getMessage());
        }
    }
    
    protected void readNames() throws IOException, PdfException
    {
        if (mode != READING_MODE)
        {
            return;
        }
        PdfObject namesRoot = catalog.getDictionary().getValue(
            new PdfName(PDF_NAMES));
        if (namesRoot == null)
        {
            return;
        }
        namesRoot = reader.getObject(namesRoot);
        if (namesRoot instanceof PdfDict)
        {
            PdfObject dests = ((PdfDict) namesRoot)
                .getValue(new PdfName(PDF_DESTS));
            if (dests != null)
            {
                readNamedDestinations(dests);
            }
            /*PdfObject javascript = ((PdfDict) namesRoot)
                .getValue(new PdfName(PDF_JAVASCRIPT_ACTION));
            if (javascript != null)
            {
                //readNamedJavaScript(javascript);
            }*/
        }
        else
        {
            throw new PdfBadFileException(
                "Names tree root is not a dictionary.");
        }
    }
    
    protected void readNamedDestinations(PdfObject dests)
        throws IOException, PdfException
    {
        if (dests == null)
        {
            return;
        }
        dests = reader.getObject(dests);
        if (dests instanceof PdfDict)
        {
            if (namedDestinations == null)
            {
                namedDestinations = new Hashtable();
            }
            PdfObject kids = ((PdfDict) dests).getValue(new PdfName(
                PDF_KIDS));
            if (kids != null) /* root node */
            {
                kids = reader.getObject(kids);
                processNamesTreeNode(kids, true);
            }
            PdfObject names = ((PdfDict) dests).getValue(new PdfName(
                PDF_NAMES));
            if (names != null) /* the only node in name tree */
            {
                names = reader.getObject(names);
                processNamesTreeNode(names, false);
            }
        }
        else
        {
            throw new PdfBadFileException(
                "Named destinations tree root is not a dictionary.");
        }
    }
    
    protected void processNamesTreeNode(PdfObject kidsOrNames,
        boolean isKids) throws IOException, PdfException
    {
        if (!(kidsOrNames instanceof PdfArray))
        {
            throw new PdfBadFileException(
                "/Names or /Kids entry is not an array.");
        }
        ArrayList list = (ArrayList) ((PdfArray) kidsOrNames)
            .getList();
        try {
            if (isKids)
            {
                for (Iterator iter = list.iterator(); iter.hasNext();)
                {
                    PdfObject obj = (PdfObject) iter.next();
                    obj = reader.getObject(obj);
                    /* should be a dict */
                    
                    PdfObject kids = ((PdfDict) obj)
                        .getValue(new PdfName(PDF_KIDS));
                    if (kids != null)
                    {
                        kids = reader.getObject(kids);
                        processNamesTreeNode(kids, true);
                    }
                    PdfObject names = ((PdfDict) obj)
                        .getValue(new PdfName(PDF_NAMES));
                    if (names != null)
                    {
                        names = reader.getObject(names);
                        processNamesTreeNode(names, false);
                    }
                }
            }
            else /* this is names */
            {
                for (Iterator iter = list.iterator(); iter.hasNext();)
                {
                    PdfObject key = (PdfObject) iter.next();
                    PdfObject val = (PdfObject) iter.next();
                    
                    key = reader.getObject(key);
                    /* should be PdfString */
                    val = reader.getObject(val);
                    if (val instanceof PdfDict)
                    {
                        PdfObject k1 = ((PdfDict) val)
                            .getValue(new PdfName(PDF_D));
                        PdfObject k2 = ((PdfDict) val)
                            .getValue(new PdfName(PDF_DESTINATION));
                        val = (k1 == null) ? reader.getObject(k2)
                            : reader.getObject(k1);
                    }
                    /* should be PdfArray */
                    namedDestinations.put(key, val);
                }
            }
        }
        catch (ClassCastException cce)
        {
            throw new PdfBadFileException(
                "Improper object found in dest NamesTree.");
        }
    }
    
    protected PdfDict processDestination(PdfArray dest)
    {
        HashMap hm = new HashMap();
        hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_ACTION));
        hm.put(new PdfName(PDF_D), dest);
        
        ArrayList list = (ArrayList) dest.getList();
        Iterator iter = list.iterator();
        PdfObject page = (PdfObject) iter.next();
        if (page instanceof PdfInteger) /* GoToR */
        {
            hm.put(new PdfName(PDF_S), new PdfName(
                PDF_REMOTEGOTO_ACTION));
        }
        else if (page instanceof PdfIndirectReference)
        {
            hm.put(new PdfName(PDF_S), new PdfName(PDF_GOTO_ACTION));
        }
        
        return new PdfDict(hm);
    }
    
    protected void readBookmarks() throws IOException, PdfException
    {
        if (mode != READING_MODE)
        {
            return;
        }
        readNames();
        PdfName outline = new PdfName(PDF_OUTLINES); 
        PdfObject outlineRoot = catalog.getDictionary().getValue(
            outline);
        if (outlineRoot == null)
        {
            return;
        }
        try {
            outlineRoot = reader.getObject(outlineRoot);
            if (outlineRoot instanceof PdfNull)
            {
                catalog.getDictionary().dictMap.put(outline,
                    outlineRoot);
                return;
            }
            PdfObject first = ((PdfDict) outlineRoot)
                .getValue(new PdfName(PDF_FIRST));
            if (first == null)
            {
                return;
            }
            first = reader.getObject(first);
            buildBookmarkTree((PdfDict) first, null);
        }
        catch (ClassCastException cce)
        {
            return;
        }
    }
    
    protected void buildBookmarkTree(PdfDict outlineNode,
        PdfBookmark parent) throws IOException, PdfException
    {
        if (mode != READING_MODE)
        {
            return;
        }
        try
        {
            PdfString title = (PdfString) reader.getObject(outlineNode
                .getValue(PdfBookmark.TITLE));
            PdfObject action = outlineNode.getValue(PdfBookmark.ACTION);
            PdfObject dest = outlineNode.getValue(PdfBookmark.DEST);
            PdfObject next = outlineNode.getValue(PdfBookmark.NEXT);
            PdfObject first = outlineNode.getValue(PdfBookmarkTree.FIRST);
            PdfBookmark b = new PdfBookmark(title);
            b.originDoc = this;
            if (action == null)
            {
                dest = getDestArray(reader.getObject(dest));
                if (dest != null)
                {
                    b.addToActionList(processDestination(
                        (PdfArray) dest));
                }
            }
            else
            {
                do
                {
                    action = reader.getObject(action);
                    dest = ((PdfDict) action).getValue(new PdfName(
                        PDF_D));
                    dest = getDestArray(reader.getObject(dest));
                    if (dest != null)
                    {
                        ((PdfDict) action).setValue(
                            new PdfName(PDF_D), dest);
                    }
                    b.addToActionList((PdfDict) action);
                    action = ((PdfDict) action)
                        .getValue(PdfBookmark.NEXT);
                } while (action != null);
            }
            addBookmark(b, parent == null ? getBookmarkRoot()
                : parent);
            
            if (next != null)
            {
                next = reader.getObject(next);
                buildBookmarkTree((PdfDict) next, parent);
            }
            if (first != null)
            {
                first = reader.getObject(first);
                buildBookmarkTree((PdfDict) first, b);
            }
            
            PdfObject style = outlineNode
                .getValue(PdfBookmarkTree.STYLE);
            if(style != null)
            {
                style = reader.getObject(style);
                b.setStyle(((PdfInteger) style).getInt());
            }
            
            PdfObject color = outlineNode
                .getValue(PdfBookmarkTree.COLOR);
            if(color != null)
            {
                color = reader.getObject(color);
                PdfArray colArr = (PdfArray) color;
                List l = colArr.getList();
                double red = 0, green = 0, blue = 0;
                PdfObject colCom = (PdfObject) l.get(0);
                if(colCom instanceof PdfNumber)
                {
                    red = ((PdfNumber)colCom).getVal();
                }
                
                colCom = (PdfObject) l.get(1);
                if(colCom instanceof PdfNumber)
                {
                    green = ((PdfNumber)colCom).getVal();
                }

                colCom = (PdfObject) l.get(2);
                if(colCom instanceof PdfNumber)
                {
                    blue = ((PdfNumber)colCom).getVal();
                }
                
                b.setColor(new Color((float) red, (float) green,
                    (float) blue));
            }
        }
        catch (ClassCastException cce)
        {
            throw new PdfBadFileException(
                "Improper object type present in" +
                	" outline dictionary.");
        }
    }
    
    protected PdfArray getDestArray(PdfObject dest)
        throws PdfException
    {
        PdfArray arr = null;
        if (dest instanceof PdfArray)
        {
            arr = (PdfArray) dest;
        }
        else if (dest instanceof PdfString)
        {
            if (namedDestinations == null)
            {
                throw new PdfBadFileException(
                    "Names tree not present in the document.");
            }
            if (!namedDestinations.containsKey(dest))
            {
                throw new PdfBadFileException(
                    "Destination name not present in Names Tree.");
            }
            PdfObject obj = (PdfObject) namedDestinations.get(dest);
            if (! (obj instanceof PdfNull))
            {
                if (obj instanceof PdfArray)
                {
                    arr = (PdfArray) obj;
                }
                else
                {
                    throw new PdfBadFileException(
                        "Improper destination object.");
                }
           }
        }
        
        return arr;
    }
    
    protected void readInfoDict() throws IOException, PdfException
    {
        if (mode != READING_MODE)
        {
            return;
        }
        try {
            infoDict = (PdfDict) reader.getObject(reader.infoDict);
        }
        catch (ClassCastException cce)
        {
            //this.infoDict = null;
        }
    }
    
    protected void readCatalog() throws IOException, PdfException
    {
        if (mode != READING_MODE)
        {
            return;
        }
        if (reader.catalogDict == null)
        {
            throw new PdfBadFileException(
                "Catalog not present in document.");
        }
        if (reader.catalogDict instanceof PdfIndirectReference)
        {
            int objNo = ((PdfIndirectReference) reader
                .catalogDict).objNumber;
            reader.catalogDict = reader.dereferObject(objNo);
        }
        if (reader.catalogDict instanceof PdfDict)
        {
            this.catalog = new PdfCatalog(
                (PdfDict) reader.catalogDict);
        }
        else
        {
            throw new PdfBadFileException(
                "Catalog is not a dictionary object.");
        }
    }
	
    protected void parseCatalog() throws IOException, PdfException
    {
        if (mode != READING_MODE || this.catalog == null)
        {
            return;
        }
        Map m = catalog.getDictionary().getMap();
        String name;
        PdfObject key, value, currObj;

        for (Iterator iter = m.keySet().iterator(); iter.hasNext();)
        {
            key = (PdfObject) iter.next();
            currObj = (PdfObject) m.get(key);
            value = reader.getObject(currObj);
            if ( !(value instanceof PdfNull))
            {
                name = ((PdfName) key).getString();
                if (name.equals(Usable.PDF_VERSION))
                {
                    value = reader.getObject(value);
                    String version = ((PdfName) value).getString();

                    this.version = version.equals("1.1")
                        || version.equals("1.2")
                        || version.equals("1.3")
                        || version.equals("1.4") ? VERSION_1_4
                        : version.equals("1.5") ? VERSION_1_5
                            : version.equals("1.6") ? VERSION_1_6
                                : VERSION_1_5;
                }
                else if (name.equals(Usable.PDF_PAGEMODE))
                {
                    value = reader.getObject(value);
                    String pageMode = ((PdfName) value).getString();

                    this.pageModeValue = pageMode.equals(PDF_USEOUTLINES)
                        ? PdfPageMode.USEOUTLINES : pageMode.equals(PDF_USETHUMBS)
                        ? PdfPageMode.USETHUMBS : pageMode.equals(PDF_FULLSCREEN)
                        ? PdfPageMode.FULLSCREEN : pageMode.equals(PDF_USEOC)
                        ? PdfPageMode.USEOC : pageMode.equals(PDF_USEATTACHMENTS)
                        ? PdfPageMode.USEATTACHMENTS : PdfPageMode.USENONE;
                }
                else if (name.equals(Usable.PDF_PAGELAYOUT))
                {
                    value = reader.getObject(value);
                    String pageLayout = ((PdfName) value).getString();

                    this.pageLayoutValue = pageLayout.equals(PDF_SINGLEPAGE)
                        ? PdfPageLayout.SINGLE_PAGE : pageLayout.equals(PDF_TWOPAGE_RIGHT)
                        ? PdfPageLayout.TWO_PAGE_RIGHT : pageLayout.equals(PDF_TWOCOLUMN_LEFT)
                        ? PdfPageLayout.TWO_COLUMN_LEFT : pageLayout.equals(PDF_TWOCOLUMN_RIGHT)
                        ? PdfPageLayout.TWO_COLUMN_RIGHT : pageLayout.equals(PDF_TWOPAGE_LEFT)
                        ? PdfPageLayout.TWO_PAGE_LEFT : PdfPageLayout.ONE_COLUMN;
                }
                else if (name.equals(PDF_ACROFORM))
                {
                    catalog.acroDict = (PdfDict) value;
                }
                else if ( !PdfCatalog.knownAttributes
                    .containsKey(name))
                {
                    catalog.unknownAttributes.put(key,
                        value.objNumber == 0 ? value : currObj);
                }
            }
        }
    }
	
    protected void readEncryptDict() throws IOException, PdfException
    {
        if (mode != READING_MODE)
        {
            return;
        }
        if (reader.encryptDict == null)
        {
            return;
        }
        this.encryptDict = (PdfDict) reader.getObject(
            reader.encryptDict);
        if (!(this.encryptDict instanceof PdfDict))
        {
            throw new PdfBadFileException(
                "encryptDict is not a dictionary object.");
        }
        
        reader.encryptDict = this.encryptDict;
    }
    
    /* This method will be overridden in PRO */ 
    HashMap prepareAcroMap() throws PdfException, IOException
    {
       return null;
    }
    
    protected void setObjMaps()
	{
		if (objMaps.isEmpty())
		{
			return;
		}
		
		Hashtable t;
		for (Iterator iter = objMaps.keySet().iterator(); iter
            .hasNext();)
		{
			t = (Hashtable) objMaps.get(iter.next());
			set(t);
		}
	}

	protected void set(Hashtable pool)
	{
		if (pool == null)
		{
			return;
		}

		PdfInteger key, value;
        ArrayList list = new ArrayList();
		Iterator iter = pool.keySet().iterator();
		while(iter.hasNext())
		{
			key = (PdfInteger) iter.next();
			value = (PdfInteger) pool.get(key);
		    if (value.getInt() == 0)
			{
				list.add(key);
			}
		}
		int limit = list.size();
		for (int i = 0; i < limit; i++, objectRun++)
		{
		    pool.put((PdfInteger) list.get(i), new PdfInteger(
                objectRun));
		}
	}

    protected void writeUnknownObjs() throws IOException, PdfException
    {
        if (unknownObjMaps == null)
        {
            return;
        }
        
        Hashtable t;
        Object key;
        for (Iterator iter = unknownObjMaps.keySet().iterator(); iter
            .hasNext();)
        {
            writingUnknownObjs = true;
            key = iter.next();
            t = (Hashtable) unknownObjMaps.get(key);
            write((PdfStdDocument) key, t);
            writingUnknownObjs = false;
        }
   
    }
    
    protected void write(PdfStdDocument originDoc, Hashtable pool)
        throws IOException, PdfException
    {
        if (pool == null || originDoc.mode != READING_MODE)
        {
            return;
        }
        Iterator iter = pool.keySet().iterator();
        while (iter.hasNext())
        {
            PdfInteger intObj = ((PdfInteger) iter.next());
            if (writtenObjs.get(new PdfInteger(getNewObjNo(originDoc,
                intObj.getInt()))) != null)
            {
                continue;
            }
            PdfObject obj = originDoc.reader.dereferObject(intObj
                .getInt());
            /*
             * boolean skip = checkForPage(obj); //check for
             * /type/page here if (skip) continue;
             */
            updateIndirectRefs(originDoc, obj);
            addToObjMaps(originDoc, obj.objNumber, getNewObjNo(
                originDoc, obj.objNumber));
            obj.objNumber = getNewObjNo(originDoc, obj.objNumber);
            if (obj.objNumber > 0)
            {
                invokeRenameField(obj);
                updateXref(obj.objNumber);
                bytesWritten += writer.writeIndirectObject(obj);
            }
        }
    }
    
    void updateXref(int objNumber) throws IOException
    {
        if (objNumber < offset.length)
        {
            offset[objNumber] = bytesWritten;
        }
        else
        {
            if (unknownObjOffset == null)
            {
                unknownObjOffset = new ArrayList();
            }
            int count = objNumber - offset.length + 1; 
            while (unknownObjOffset.size() < count)
            {
                unknownObjOffset.add(new PdfLong(0));
            }
            unknownObjOffset.set(objNumber - offset.length,
                    new PdfLong(bytesWritten));
        }
    }
	
	protected ArrayList readPageStream(PdfStdPage page)
        throws IOException, PdfException
	{
		if (pageTree == null || mode != READING_MODE) 
		{
			return null;
		}
		ArrayList streams = null;
		PdfDict pageDict = page.dict;
		PdfObject contents = pageDict.getValue(
			new PdfName(PDF_CONTENTS));
		contents = reader.getObject(contents);
		try 
		{
			if (contents != null)
			{
				streams = new ArrayList();
				if (contents instanceof PdfArray)
				{
					ArrayList list = (ArrayList) ((PdfArray) contents)
						.getList();
					Iterator iter = list.iterator();
					while (iter.hasNext())
					{
						PdfObject strm = (PdfObject) iter.next();
						strm = reader.getObject(strm);
						streams.add(new PdfIndirectReference(
                            strm.objNumber, 0));
					}
				}
				else if (contents instanceof PdfStream)
				{
					streams.add(new PdfIndirectReference(
                        contents.objNumber, 0));
				}
				else
				{
					throw new PdfException(
						"Improper '/Contents' entry in page.");
				}
			}
		}
		catch (ClassCastException ice)
		{
			throw new PdfException("Improper object found in file.");
		}
    
		return streams;
	}
 
    protected void updateIndirectRefs(PdfStdDocument originDoc,
        PdfObject obj) throws PdfException, IOException
    {
        updateIndirectRefs(originDoc, obj, false);
    }

    protected void updateIndirectRefs(PdfStdDocument originDoc,
        PdfObject obj, boolean addToUnknownPool) throws PdfException,
        IOException
    {
        if (obj instanceof PdfIndirectReference)
        {
            if (addToUnknownPool)
            {
                addToUnknownObjPool(originDoc, obj.objNumber);
            }
            obj.objNumber = getNewObjNo(originDoc, obj.objNumber);
        }
        else if (obj instanceof PdfStream)
        {
            PdfDict strmDict = ((PdfStream) obj).streamDict;
            strmDict.dictMap.remove(new PdfName(PDF_LENGTH));
            updateIndirectRefs(originDoc,
                strmDict, addToUnknownPool);
        }
        else if (obj instanceof PdfDict)
        {
            Map m = ((PdfDict) obj).getMap();
            Iterator iter = m.keySet().iterator();
            while (iter.hasNext())
            {
                PdfObject temp = (PdfObject) m.get(iter.next());
                if (temp instanceof PdfArray
                    || temp instanceof PdfDict
                    || temp instanceof PdfStream
                    || temp instanceof PdfIndirectReference)
                {
                    updateIndirectRefs(originDoc, temp,
                        addToUnknownPool);
                }
            }
        }
        else if (obj instanceof PdfArray)
        {
            List l = ((PdfArray) obj).getList();
            int limit = l.size();
            for (int i = 0; i < limit; ++i)
            {
                PdfObject temp = (PdfObject) l.get(i);
                if (temp instanceof PdfArray
                    || temp instanceof PdfDict
                    || temp instanceof PdfStream
                    || temp instanceof PdfIndirectReference)
                {
                    updateIndirectRefs(originDoc, (PdfObject) l
                        .get(i), addToUnknownPool);
                }
            }
        }
        /*if (obj.objNumber != 0)
        {
            obj.objNumber = getNewObjNo(originDoc, obj.objNumber);
        }*/
    }	
    
	protected void addToUnknownObjPool(PdfStdDocument d, int oldObjNo)
	{
		if (unknownObjMaps == null)
		{
			unknownObjMaps = new Hashtable();
		}
		if (d == null)
		{
			d = this;
		}
		if (unknownObjMaps.get(d) == null)
		{
			unknownObjMaps.put(d, new Hashtable());
		}
		Hashtable t = (Hashtable) unknownObjMaps.get(d);
		t.put(new PdfInteger(oldObjNo), PdfInteger.DUMMY);
	}

    protected void addToObjMaps(PdfStdDocument doc, int oldObjNo,
        int newObjNo)
    {
        if (oldObjNo == 0)
        {
            return;
        }
        if (doc == null)
        {
            doc = this;
        }
        if (objMaps.get(doc) == null)
        {
            objMaps.put(doc, new Hashtable());
        }
        Hashtable m = (Hashtable) objMaps.get(doc);
        m.put(new PdfInteger(oldObjNo), new PdfInteger(newObjNo));
    }
	
    protected synchronized int getNewObjNo(PdfStdDocument doc,
        int oldObjNo) throws PdfException, IOException
    {
        if (doc == null)
        {
            doc = this;
            if (objMaps != null && objMaps.get(doc) == null)
            {
                objMaps.put(doc, new Hashtable());
            }
        }

        if (objMaps.get(doc) == null)
        {
            throw new PdfException("Object not present");
        }

        int objNo = 0;
        Hashtable m = (Hashtable) objMaps.get(doc);
        PdfInteger intObj = new PdfInteger(oldObjNo);
        if (m.get(intObj) != null)
        {
            objNo = ((PdfInteger) m.get(intObj)).getInt();
        }
        if (objNo == 0)
        {
            objNo = objectRun++;
            addToObjMaps(doc, oldObjNo, objNo);
            // updateXref(objNo);
            if (writingUnknownObjs
                && !writtenObjs.containsKey(new PdfInteger(
                    getNewObjNo(doc, oldObjNo))))
            {
                PdfObject obj = doc.reader.dereferObject(oldObjNo);
                updateIndirectRefs(doc, obj, false);
                obj.objNumber = getNewObjNo(doc, oldObjNo);
                updateXref(obj.objNumber);
                bytesWritten += writer.writeIndirectObject(obj);
            }
        }

        return objNo;
    }
	
    protected synchronized ArrayList readPageStream(int pageNo)
        throws IOException, PdfException
    {
        if (pageTree == null || mode != READING_MODE) 
        {
            return null;
        }

        return readPageStream(getPage(pageNo));
    }
    
    protected Vector getPageObjects(String pageRange)
        throws PdfException
    {
        Vector pageIndices = getPages(pageRange);
        Vector pageObjects = new Vector();
        PdfStdPage p;
        for (Iterator iter = pageIndices.iterator(); iter.hasNext();)
        {
            p = getPage(((Integer) iter.next()).intValue());
            pageObjects.add(p);
        }

        return pageObjects.size() != 0 ? pageObjects : null;
    }
    
    protected Vector getPages(String pageRange) throws PdfException
    {
        if (pageRange == null || pageRange == "")
        {
            return null;
        }
        pageRange = pageRange.trim();
        Pattern pat = Pattern.compile("(-\\s*(\\d++))|" +
    		"((\\d++)\\s*-\\s*(\\d++))|((\\d++)\\s*-)|" +
    		"(\\d++)|(-[^\\d]*)");
        Matcher mat = pat.matcher(pageRange.subSequence(
            0, pageRange.length()));
        Integer start = null;
        Integer end = null;
        Vector v = new Vector();
        int pageCount = pageTree != null ? pageTree.getCount() : 0;
        if (pageRange.toUpperCase().equals("ODD"))
        {
            for (int i = 1; i <= pageCount; i += 2)
            {
                v.add(new Integer(i));
            }
        }
        else if (pageRange.toUpperCase().equals("EVEN"))
        {
            for (int i = 2; i <= pageCount; i += 2)
            {
                v.add(new Integer(i));
            }
        }
        else if (pageRange.toUpperCase().equals("ALL"))
        {
            for (int i = 1; i <= pageCount; ++i)
            {
                v.add(new Integer(i));
            }
        }
        else while (mat.find())
        {
            start = mat.group(1) != null ? 
            		new Integer(1) :
            	mat.group(3) != null ? 
            	    new Integer(Integer.parseInt(mat.group(4))) : 
        	    mat.group(6) != null ? 
        	        new Integer(Integer.parseInt(mat.group(7))) :
                mat.group(8) != null ? 
                    new Integer(Integer.parseInt(mat.group(8))) : 
        	    mat.group(9) != null ? 
        	        new Integer(1) : null;
            
            end = mat.group(1) != null ? 
            	  new Integer(Integer.parseInt(mat.group(2))) :
              mat.group(3) != null ? 
                  new Integer(Integer.parseInt(mat.group(5))) : 
              mat.group(6) != null ? 
                  new Integer(pageCount) :
              mat.group(9) != null ? new Integer(pageCount) : null;
            
            int i = start != null ? Math.abs(start.intValue()) : 0;
            int j = end != null ? Math.abs(end.intValue()) : 0;
            if (j < i && j != 0) //swap i and j
            {
                i -= j;
                j += i;
                i = (j - i);
            }
            do if (i != 0)
            {
                start = new Integer(i);
                if (!(v.contains(start)))
                {
                    v.add(start);
                }
            } while (++i <= j);
        }
        
        return v;
    }

    protected synchronized void addFont(String name, PdfDict d)
    {
        if (fontMap == null)
        {
            fontMap = new HashMap();
        }
        fontMap.put(new PdfName(name), d);
    }

    protected synchronized void addImage(PdfInteger hash, PdfImage m)
    {
        if (xObjMap == null)
        {
            xObjMap = new HashMap();
        }
        xObjMap.put(hash, m);
    }

    protected void setBookmarks() throws IOException, PdfException
    {
        bookmarkTree.set(bookmarkTree.root);
        
        if (versionId > 4)
        {
            outlineStream = new PdfObjectStream();
            outlineStream.stream.setObjectNumber(objectRun++);
        }
    }

    protected void setProcSet()
    {
        if (procSet == null)
        {
            ArrayList arr = new ArrayList();
            arr.add(new PdfName(PDF_PDF));
            arr.add(new PdfName(PDF_TEXT));
            arr.add(new PdfName(PDF_IMAGEC));
            arr.add(new PdfName(PDF_IMAGEI));
            arr.add(new PdfName(PDF_IMAGEB));
            procSet = new PdfArray(arr);
        }
        procSet.setObjectNumber(objectRun++);
    }

    protected void setCatalog() throws IOException, PdfException
    {
		if (catalog == null)
		{
			catalog = new PdfCatalog(new PdfDict(new HashMap()));
		}
		
		Map m = catalog.catalogDict.getMap();
		m.put(new PdfName(PDF_TYPE), new PdfName(PDF_CATALOG));
        if (pageTree != null)
        {
            m.put(new PdfName(PDF_PAGES), new PdfIndirectReference(
                pageTree.getRoot().getDict().getObjectNumber(), 0));
        }
        if (actionList != null)
        {
            PdfDict actionDict;
            int size = actionList.size();
            for (int i = size - 1; i >= 0; i--)
            {
                actionDict = (PdfDict) actionList.get(i);
                if (i != size - 1)
                {
                    actionDict.getMap().put(new PdfName(PDF_NEXT),
                        new PdfIndirectReference(objectRun - 1, 0));
                }
                actionDict.setObjectNumber(objectRun++);
            }
            PdfName key = new PdfName(PDF_OPEN_ACTION);
            m.put(key, new PdfIndirectReference(objectRun - 1, 0));
            catalog.unknownAttributes.remove(key);
        }
        if (addnDict != null)
        {
            PdfName key = new PdfName(PDF_AA);
            m.put(key, new PdfIndirectReference(addnDict
                .getObjectNumber(), 0));
            catalog.unknownAttributes.remove(key);
        }
        if (bookmarkTree != null)
        {
            m.put(new PdfName(PDF_OUTLINES),
                new PdfIndirectReference(bookmarkTree.getRoot()
                    .getDict().getObjectNumber(), 0));
        }

        catalog.set(this);
        
        PdfDict catDict = new PdfDict(m);
        catDict.setObjectNumber(objectRun++);
        catalog = new PdfCatalog(catDict);
    }

    protected void setEncryptDict()
    {
        encryptDict.setObjectNumber(objectRun++);
    }
    
    protected void setDocInfo()
    {
        if (mode == WRITING_MODE)
        {
            Map hm = infoDict.getMap();
            hm.put(new PdfName(PDF_CREATOR), new PdfString(
                "Gnostice PDFOne Java"));
            hm.put(new PdfName(PDF_CREATIONDATE), new PdfTextString(
                PdfDate.CurrentDate()));
            hm.put(new PdfName(PDF_MODDATE), new PdfTextString(
                PdfDate.CurrentDate()));
			hm.put(new PdfName(PDF_PRODUCER), new PdfString(
                "Gnostice PDFOne Java"));
        }
        else if (mode == READING_MODE)
        {
            Map hm = infoDict.getMap();
            hm.put(new PdfName(PDF_CREATOR), new PdfString(
                "Gnostice PDFOne Java"));
            hm.put(new PdfName(PDF_MODDATE), new PdfTextString(
                PdfDate.CurrentDate()));
			hm.put(new PdfName(PDF_PRODUCER), new PdfString(
                "Gnostice PDFOne Java"));
        }
        
        infoDict.setObjectNumber(objectRun++);
    }

    protected void setAddnAction()
    {
        addnDict.setObjectNumber(objectRun++);
    }
    
    protected void setPageTree() throws PdfException, IOException
    {
        pageTree.set();
    }

    protected void setTrailerDict(int root)
    {
        int encrypt = -1;
        if (encryptDict != null)
        {
            encrypt = encryptDict.getObjectNumber();
        }
        ArrayList arr = new ArrayList();
        if (mode == READING_MODE)
        {
            if (this.encrypto != null)
            {
                fileID = new PdfArray(encrypto.getFileID());
            }
            else
            {
                String id = PdfString.fromBytes(PdfEncryption
                    .createDocumentId());
                arr.add(reader.fileID != null ? new PdfString(
                    reader.fileID, true) : new PdfString(id, true));
                arr.add(new PdfString(id, true));
                fileID = new PdfArray(arr);
            }
        }
        else if (mode == WRITING_MODE)
        {
            if (!encryptDocument)
            {
                String id = PdfString.fromBytes(PdfEncryption
                    .createDocumentId());
                arr.add(new PdfString(id, true));
                arr.add(new PdfString(id, true));
                fileID = new PdfArray(arr);
            }
            else
            {
                fileID = new PdfArray(encrypto.getFileID());
            }
        }
        if (root != -1)
        {
            crossRefTable.set(objectRun, catalog.getObjectNumber(),
                infoDict.getObjectNumber(), encrypt, fileID);
        }
        else
        {
            crossRefTable.set(objectRun, root, infoDict
                .getObjectNumber(), encrypt, fileID);
        }
    }

    /**
     * Retrieves current encryption settings of this
     * <code>PdfDocument</code>.
     * 
     * @return a <code>PdfEncryption</code> object identifying 
     *         the current encryption settings of the document
     * @since 1.0
     * @see #setEncryptor(PdfEncryption)
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#getEncryptor">example</a>.
     */
    public synchronized PdfEncryption getEncryptor()
    {
        if (mode == READING_MODE && reader.decryptor != null)
        {
            return reader.decryptor;
        }
        if(encrypto == null)
        {
            encrypto = new PdfEncryption();
        }
        return encrypto;
    }
    
    /**
     * Specify encryption settings for this 
     * <code>PdfDocument</code>.
     * 
     * @param encrypto
     *            <code>PdfEncryption</code> specifying the
     *            encryption settings for the document
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see #getEncryptor()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#setEncryptor">example</a>.
     */
    public synchronized void setEncryptor(PdfEncryption encrypto)
        throws PdfException
    {
        if (encrypto == null)
        {
            throw new PdfException(
                "Illegal argument to setEncryptor() " +
                "[PdfEncryption == null]");
        }
        this.encrypto = encrypto;
        setEncrypt(this.encrypto);
    }
    
    protected void setEncrypt(PdfEncryption encryption)
        throws PdfException
    {
        String userPassword = encryption.getUserPwd();
        String ownerPassword = encryption.getOwnerPwd();
        int encrytionLevel = encryption.getLevel();
        int permissions = encryption.getPermissions();

        encryptDocument = true;
        Hashtable  t = new Hashtable();
        PdfName filter = new PdfName("Filter");
        PdfName standard = new PdfName("Standard");
        PdfName algoVersion = new PdfName(PDF_V);
        PdfName revision = new PdfName(PDF_R);
        PdfName length = new PdfName(PDF_LENGTH);
//        PdfName permission = new PdfName(PDF_P);
        PdfName owner = new PdfName(PDF_O);
        PdfName user = new PdfName(PDF_U);
        
        t.put(filter, standard);
        if (encrytionLevel == PdfEncryption.LEVEL_128_BIT)
        {
            t.put(algoVersion, new PdfInteger(2));
            t.put(revision, new PdfInteger(3));
            t.put(length, new PdfInteger(128));
        }
        else
        {
            t.put(algoVersion, new PdfInteger(1));
            t.put(revision, new PdfInteger(2));
            t.put(length, new PdfInteger(40));
        }

//        if (  ((permissions & 256/*PdfEncryption.AllowFormFill*/) == /*8 + */256)
//            | ((permissions & 512/*PdfEncryption.AllowAccessibility*/) == /*16 + */512)
//            | ((permissions & 1024/*PdfEncryption.AllowAssembly*/) == /*8 + */1024)
//            | ((permissions & 2048/*PdfEncryption.AllowHighResPrint*/) == /*4 + */2048))
//        {
//            t.put(revision, new PdfInteger(3));
//        }
        
        String userpwd = userPassword;
        String ownerpwd = ownerPassword;
        byte userPad[];
        byte ownerPad[];
        userPad = PdfString.toBytes(userpwd);
        ownerPad = PdfString.toBytes(ownerpwd);
        encryption.setupAllKeys(userPad, ownerPad, permissions,
            encrytionLevel == PdfEncryption.LEVEL_128_BIT);

        ownerpwd = PdfString.fromBytes(encryption.ownerKey);
        userpwd = PdfString.fromBytes(encryption.userKey);
        t.put(owner, new PdfString(ownerpwd, true));
        t.put(user, new PdfString(userpwd, true));
        t.put(new PdfName(PDF_P), new PdfInteger(permissions));
        encryptDict = new PdfDict(t);
        if (mode == WRITING_MODE)
        {
            writer.encryptor = encryption;
            writer.encryptDocument = true;
        }
        else
        {
            if(!isOwner)
            {
                throw new PdfException(
                    "Operation aborted, Owner password needed.");
            }
            this.encrypto = encryption;
        }
    }
    
    protected void writeHeader() throws IOException
    {
        bytesWritten += writer.writePDFHeader(version);
    }

    protected void writeCatalog() throws IOException
    {
        int index = catalog.getObjectNumber();
        offset[index] = bytesWritten;
        bytesWritten += writer.writeIndirectObject(catalog);
    }

    protected void writeDocInfo() throws IOException
    {
        int index = infoDict.getObjectNumber();
        offset[index] = bytesWritten;
        bytesWritten += writer.writeIndirectObject(infoDict);
    }

    protected void writeFonts() throws IOException, PdfException
    {
        PdfName descendant = new PdfName(PDF_DESCENDANT);
        PdfObject key;
        PdfDict value;
        
        for (Iterator i = fontMap.keySet().iterator(); i.hasNext();)
        {
            key = (PdfObject) i.next();
            value = (PdfDict) fontMap.get(key);
            PdfDict descendantFont = (PdfDict) value
                .getValue(descendant);
            
            if (descendantFont != null) /* Type0 Font */
            {
                ArrayList list = new ArrayList();
                list.add(new PdfIndirectReference(
                    descendantFont.objNumber, 0));
                value.getMap().put(descendant, new PdfArray(list));
                
                PdfName emb = new PdfName(RUBICON_EMBEDDED);
                PdfObject stm = descendantFont.getValue(emb);
                if (stm != null)
                {
                    filters = PdfFilter.encode((PdfStream) stm,
                        filters, addDefaultFilter, compressionLevel);

                    int index = stm.objNumber;
                    offset[index] = bytesWritten;
                    bytesWritten += writer.writeIndirectObject(stm);

                    descendantFont.dictMap.remove(emb);
                }

                PdfObject fontDescriptor = descendantFont
                    .getValue(PdfFont.FONT_DESCRIPTOR);
                if (fontDescriptor != null)
                {
                    descendantFont.getMap().put(PdfFont.FONT_DESCRIPTOR,
                        new PdfIndirectReference(fontDescriptor.objNumber,
                            fontDescriptor.genNumber));
                    
                    int index = fontDescriptor.objNumber;
                    offset[index] = bytesWritten;
                    bytesWritten += writer
                        .writeIndirectObject(fontDescriptor);
                }

                int index = descendantFont.objNumber;
                offset[index] = bytesWritten;
                bytesWritten += writer
                    .writeIndirectObject(descendantFont);
                
                emb = new PdfName(PDF_TOUNICODE);
                stm = value.getValue(emb);
                if (stm != null)
                {
                    filters = PdfFilter.encode((PdfStream) stm,
                        filters, addDefaultFilter, compressionLevel);

                    index = stm.objNumber;
                    offset[index] = bytesWritten;
                    bytesWritten += writer.writeIndirectObject(stm);

                    value.dictMap.put(emb, new PdfIndirectReference(
                        stm.objNumber, 0));
                }
            }
            else /* Type1 & TrueType Fonts */
            {
                PdfObject fontDescriptor = value
                    .getValue(PdfFont.FONT_DESCRIPTOR);
                if (fontDescriptor != null)
                {
                    value.getMap().put(
                        PdfFont.FONT_DESCRIPTOR,
                        new PdfIndirectReference(
                            fontDescriptor.objNumber,
                            fontDescriptor.genNumber));
                    
                    int index = fontDescriptor.objNumber;
                    offset[index] = bytesWritten;
                    bytesWritten += writer
                        .writeIndirectObject(fontDescriptor);
                }

                PdfName emb = new PdfName(RUBICON_EMBEDDED);
                PdfObject stm = value.getValue(emb);
                if (stm != null)
                {
                    filters = PdfFilter.encode((PdfStream) stm,
                        filters, addDefaultFilter, compressionLevel);

                    int index = stm.objNumber;
                    offset[index] = bytesWritten;
                    bytesWritten += writer.writeIndirectObject(stm);

                    value.dictMap.remove(emb);
                }
            }
            
            int index = ((PdfDict) value).getObjectNumber();
            offset[index] = bytesWritten;
            bytesWritten += writer.writeIndirectObject(value);
        }
    }
    
    protected void writeImages() throws IOException, PdfException
    {
        for (Iterator i = xObjMap.keySet().iterator(); i.hasNext();)
        {
            PdfObject key = (PdfObject) i.next();
            PdfObject value = (PdfObject) xObjMap.get(key);
            if (value instanceof PdfImage)
            {
                PdfImage img = (PdfImage) value;
                //added for Indexed Images
                PdfObject obj = img.colorSpace;
                if (obj instanceof PdfArray)
                {
                    List l = ((PdfArray) obj).getList();
                    PdfStream s = (PdfStream) l.get(3);
                    int objNo = s.getObjectNumber();
                    PdfIndirectReference r = new PdfIndirectReference(
                        objNo, 0);
                    l.set(3, r);
                    
                    int index = objNo;
                    offset[index] = bytesWritten;
                    filters = PdfFilter.encode(s, filters,
                        addDefaultFilter, compressionLevel);
                    bytesWritten += writer.writeIndirectObject(s);
                }

                int index = img.getObjectNumber();
                offset[index] = bytesWritten;
                if (!(value instanceof PdfImageJpeg))
                {
                    filters = PdfFilter.encode((PdfImage) value,
                        filters, addDefaultFilter, compressionLevel);
                }
                bytesWritten += writer.writeIndirectObject(value);
            }
        }
    }

    protected void writeBookmarks() throws IOException, PdfException
    {
        if (versionId > 4)
        {
            bookmarkTree.write(outlineStream);
        }
        else 
        {
            bookmarkTree.write();
        }
    }

    protected void writePages() throws IOException, PdfException
    {
        pageTree.write(pageTree.root);
    }

    protected void writeProcSet() throws IOException
    {
        int index = procSet.getObjectNumber();
        offset[index] = bytesWritten;
        bytesWritten += writer.writeIndirectObject(procSet);
    }

    protected void writeAction() throws IOException
    {
        PdfDict actionDict;
        int index;
        if (actionList != null)
        {
            for (int i = 0, limit = actionList.size(); i < limit; i++)
            {
                actionDict = (PdfDict) actionList.get(i);
                index = actionDict.getObjectNumber();
                offset[index] = bytesWritten;
                bytesWritten += writer
                    .writeIndirectObject(actionDict);
            }
        }
        if (addnDict != null)
        {
            index = addnDict.getObjectNumber();
            offset[index] = bytesWritten;
            bytesWritten += writer.writeIndirectObject(addnDict);
        }
    }

    protected void writeEncryt() throws IOException
    {
        writer.encryptDocument = false;
        PdfName key = new PdfName(PDF_P);
        PdfObject p = encryptDict.getValue(key);
        if (p instanceof PdfLong)
        {
            PdfInteger np = new PdfInteger(((PdfLong) p).getInt());
            encryptDict.setValue(key, np);
        }
        int index = encryptDict.getObjectNumber();
        offset[index] = bytesWritten;
        bytesWritten += writer.writeIndirectObject(encryptDict);
        writer.encryptDocument = true;
    }
    
    protected void writeObjectStreams() throws IOException,
        PdfException
    {
        if (outlineStream != null)
        {
            filters = PdfFilter.encode(outlineStream.stream, filters,
                addDefaultFilter, compressionLevel);

            int index = outlineStream.stream.getObjectNumber();
            offset[index] = bytesWritten;
            bytesWritten += writer.writeIndirectObject(
                outlineStream.stream);
        }
        if (fontStream != null)
        {
            filters = PdfFilter.encode(fontStream.stream, filters,
                addDefaultFilter, compressionLevel);

            int index = fontStream.stream.getObjectNumber();
            offset[index] = bytesWritten;
            bytesWritten += writer.writeIndirectObject(
                fontStream.stream);
        }
    }
    
    protected void setLayout()
    {
        switch (pageLayoutValue)
        {
            case PdfPageLayout.SINGLE_PAGE:
                catalog.getDictionary().getMap().put(
                    new PdfName(PDF_PAGELAYOUT),
                    new PdfName(PDF_SINGLEPAGE));
                break;

            case PdfPageLayout.ONE_COLUMN:
                catalog.getDictionary().getMap().put(
                    new PdfName(PDF_PAGELAYOUT),
                    new PdfName(PDF_ONECOLUMN));
                break;

            case PdfPageLayout.TWO_COLUMN_LEFT:
                catalog.getDictionary().getMap().put(
                    new PdfName(PDF_PAGELAYOUT),
                    new PdfName(PDF_TWOCOLUMN_LEFT));
                break;

            case PdfPageLayout.TWO_COLUMN_RIGHT:
                catalog.getDictionary().getMap().put(
                    new PdfName(PDF_PAGELAYOUT),
                    new PdfName(PDF_TWOCOLUMN_RIGHT));
                break;

            case PdfPageLayout.TWO_PAGE_LEFT:
                catalog.getDictionary().getMap()
                    .put(new PdfName(PDF_PAGELAYOUT),
                        new PdfName(PDF_TWOPAGE_LEFT));
                break;

            case PdfPageLayout.TWO_PAGE_RIGHT:
                catalog.getDictionary().getMap().put(
                    new PdfName(PDF_PAGELAYOUT),
                    new PdfName(PDF_TWOPAGE_RIGHT));
                break;

            default:
                catalog.getDictionary().getMap().put(
                    new PdfName(PDF_PAGELAYOUT),
                    new PdfName(PDF_ONECOLUMN));
                break;
        }
    }

    protected void setMode()
    {
        switch (pageModeValue)
        {
            case PdfPageMode.USENONE:
                catalog.getDictionary().getMap().put(
                    new PdfName(PDF_PAGEMODE),
                    new PdfName(PDF_USENONE));
                break;

            case PdfPageMode.USEOUTLINES:
                catalog.getDictionary().getMap().put(
                    new PdfName(PDF_PAGEMODE),
                    new PdfName(PDF_USEOUTLINES));
                break;

            case PdfPageMode.USETHUMBS:
                catalog.getDictionary().getMap().put(
                    new PdfName(PDF_PAGEMODE),
                    new PdfName(PDF_USETHUMBS));
                break;

            case PdfPageMode.FULLSCREEN:
                catalog.getDictionary().getMap().put(
                    new PdfName(PDF_PAGEMODE),
                    new PdfName(PDF_FULLSCREEN));
                break;

            case PdfPageMode.USEOC:
                catalog.getDictionary().getMap()
                    .put(new PdfName(PDF_PAGEMODE),
                        new PdfName(PDF_USEOC));
                break;

            case PdfPageMode.USEATTACHMENTS:
                catalog.getDictionary().getMap().put(
                    new PdfName(PDF_PAGEMODE),
                    new PdfName(PDF_USEATTACHMENTS));
                break;

            default:
                catalog.getDictionary().getMap().put(
                    new PdfName(PDF_PAGEMODE),
                    new PdfName(PDF_USENONE));
                break;
        }
    }

    protected void writeCrossRefTable(int root) throws IOException
    {
        if (unknownObjOffset != null)
        {
            long[] offsetNew = new long[objectRun];
            System.arraycopy(offset, 0, offsetNew, 0, offset.length);
            for (int i = 0, index = offset.length, limit = unknownObjOffset
                    .size(); i < limit; ++i, ++index)
            {
                long longVal = ((PdfLong) unknownObjOffset.get(i)).getLong();
                offsetNew[index] = longVal;
            }
            offset = offsetNew;
        }

        crossRefTable = new PdfCrossRefTable(offset);
        setTrailerDict(root);
        bytesWritten += writer.writeXrefTable(crossRefTable,
            bytesWritten, mode == READING_MODE);
    }
    
    protected void writeCrossReferenceStream(int root)
        throws IOException, PdfException
    {
        crossRefTable = (mode == READING_MODE) ? reader.xrt
            : new PdfCrossRefTable(offset);
        crossRefStream.setObjectNumber(objectRun);
        if (unknownObjOffset != null)
        {
            long[] offsetNew = new long[objectRun];
            System.arraycopy(offset, 0, offsetNew, 0, offset.length);
            for (int i = 0, index = offset.length, limit = unknownObjOffset
                    .size(); i < limit; ++i, ++index)
            {
                long longVal = ((PdfLong) unknownObjOffset.get(i)).getLong();
                offsetNew[index] = longVal;
            }
            offset = offsetNew;
        }
        
        long[] /*offsetSorted = crossRefTable.offsetArraySort;
        if (mode == WRITING_MODE)
        {*/
            offsetSorted = new long[objectRun];
            System.arraycopy(offset, 0, offsetSorted, 0, objectRun - 1);
            Arrays.sort(offsetSorted);
        /*}*/
        long maxVal = offsetSorted[offsetSorted.length - 1];

        final int BYTE = 1;
        final int SHORT = 2;
        final int INT = 4;
        final int LONG = 8;
        final int MID_BYTE_SIZE = maxVal < Math.pow(2, 8) ? BYTE
            : maxVal < Math.pow(2, 16) ? SHORT : maxVal < Math.pow(2,
                32) ? INT : LONG;
        crossRefStream.widths = new int[] { 1, MID_BYTE_SIZE, 2 };
        ByteArrayOutputStream baos = crossRefStream.baos;
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeByte(0);
        switch (MID_BYTE_SIZE)
        {
            case BYTE:
                dos.writeByte(0);
                break;
            case SHORT:
                dos.writeShort(0);
                break;
            case INT:
                dos.writeInt(0);
                break;
            case LONG:
                dos.writeLong(0);
                break;
        }
        dos.writeShort(-1);        
        
        PdfInteger objNo;
        switch (MID_BYTE_SIZE)
        {
            case BYTE:
                for (int i = 1; i < objectRun; ++i)
                {
                    objNo = new PdfInteger(i);
                    int type = (objStreamId.containsKey(objNo)) ? 2
                        : 1;
                    dos.writeByte(type);
                    if (type == 2)
                    {
                        dos.writeByte(((PdfInteger) objStreamId
                            .get(objNo)).getInt());
                        dos.writeShort(((PdfInteger) objStreamPos
                            .get(objNo)).getInt());
                    }
                    else
                    {
                        dos.writeByte((int) offset[i]);
                        dos.writeShort(0);
                    }
                }
                break;
            case SHORT:
                for (int i = 1; i < objectRun; ++i)
                {
                    objNo = new PdfInteger(i);
                    int type = (objStreamId.containsKey(objNo)) ? 2
                        : 1;
                    dos.writeByte(type);
                    if (type == 2)
                    {
                        dos.writeShort(((PdfInteger) objStreamId
                            .get(objNo)).getInt());
                        dos.writeShort(((PdfInteger) objStreamPos
                            .get(objNo)).getInt());
                    }
                    else
                    {
                        dos.writeShort((int) offset[i]);
                        dos.writeShort(0);
                    }
                }
                break;
            case INT:
                for (int i = 1; i < objectRun; ++i)
                {
                    objNo = new PdfInteger(i);
                    int type = (objStreamId.containsKey(objNo)) ? 2
                        : 1;
                    dos.writeByte(type);
                    if (type == 2)
                    {
                        dos.writeInt(((PdfInteger) objStreamId
                            .get(objNo)).getInt());
                        dos.writeShort(((PdfInteger) objStreamPos
                            .get(objNo)).getInt());
                    }
                    else
                    {
                        dos.writeInt((int) offset[i]);
                        dos.writeShort(0);
                    }
                }
                break;
            case LONG:
                for (int i = 1; i < objectRun; ++i)
                {
                    objNo = new PdfInteger(i);
                    int type = (objStreamId.containsKey(objNo)) ? 2
                        : 1;
                    dos.writeByte(type);
                    if (type == 2)
                    {
                        dos.writeLong(((PdfInteger) objStreamId
                            .get(objNo)).getInt());
                        dos.writeShort(((PdfInteger) objStreamPos
                            .get(objNo)).getInt());
                    }
                    else
                    {
                        dos.writeLong((int) offset[i]);
                        dos.writeShort(0);
                    }
                }
                break;
        }
        
        setTrailerDict(root);
        crossRefStream.streamDict = crossRefTable.trailerDict;
        crossRefStream.initialize();
        bytesWritten += writer.writeCrossRefStream(crossRefStream,
            bytesWritten);
        dos.flush();
        dos.close();
    }

    protected void invokeRenameField(PdfObject obj) throws PdfException
    {
        try
        {
            if (obj instanceof PdfDict)
            {
                PdfDict acroDict = (PdfDict) obj;
                if (acroDict.dictMap.containsKey(new PdfName(PDF_FT)))
                {
                    PdfString fieldName = (PdfString) acroDict
                        .getValue(new PdfName(PDF_T));
                    if (fieldName != null && onRenameField != null)
                    {
                        String oldName = fieldName.getString();
                        boolean[] rename = new boolean[] { false };
                        StringBuffer newName = new StringBuffer();
                        onRenameField.invoke(null, new Object[] {
                            this, oldName, newName, rename });
                        if ( !rename[0])
                        {
                             newName.append(oldName
                                + random.nextInt(10000));
                        }
                        acroDict.dictMap.put(new PdfName(PDF_T),
//                            new PdfTextString(newName + "", true));
                        new PdfString(newName + ""));
                    }
                }
            }
        }
        catch (IllegalAccessException iae)
        {
            throw new PdfException("method not accessible");
        }
        catch (IllegalArgumentException iare)
        {
            throw new PdfException("invalid method signature");
        }
        catch (NullPointerException npe)
        {
            throw new PdfException("method not static");
        }
        catch (InvocationTargetException ite)
        {
            throw new PdfException(ite.getMessage());
        }
    }

    protected void mergeResources(PdfDict sourceDict,
        PdfDict destDict, PdfStdDocument d) throws IOException,
        PdfException
    {
        Map source = sourceDict.getMap();
        Map dest = destDict.getMap();
        for (Iterator iter = source.keySet().iterator(); iter.hasNext();)
        {
            PdfName key = (PdfName) iter.next();
            if (key.getString().equals(PDF_PROCSET))
            {
                continue;
            }
            PdfDict val = (PdfDict) d.reader
                .getObject((PdfObject) source.get(key));
            if (dest.containsKey(key))
            {
                PdfDict destVal = (PdfDict) this.reader
                    .getObject((PdfObject) dest.get(key));
                for (Iterator innerIter = val.getMap().keySet()
                    .iterator(); innerIter.hasNext();)
                {
                    PdfName key1 = (PdfName) innerIter.next();
                    if (!destVal.getMap().containsKey(key1))
                    {
                        destVal.getMap().put(key1,
                            val.getMap().get(key1));
                    }
                }
            }
            else
            {
                dest.put(key, val);
            }
        }
    }
    
    /*protected void mergeFields() throws IOException, PdfException
    {
        if (objMaps != null)
        {
            try
            {
                PdfObject this_dr = null;
                PdfObject inner_dr = null;
                PdfName DR = new PdfName(PDF_DR);
                for (Iterator iter = objMaps.keySet().iterator(); iter
                    .hasNext();)
                {
                    PdfStdDocument d = (PdfStdDocument) iter.next();
                    if (d == this)
                    {
                        continue;
                    }
                    PdfObject innerAcroDict = d.catalog
                        .getDictionary()
                        .getValue(PdfCatalog.ACROFORM);
                    innerAcroDict = ((PdfDict) d.reader.catalogDict)
                        .getValue(PdfCatalog.ACROFORM);

                    if (innerAcroDict != null)
                    {
                        innerAcroDict = (PdfDict) d.reader
                            .getObject(innerAcroDict);
                        ((PdfDict) innerAcroDict).getMap().remove(
                            new PdfName(PDF_FIELDS));
                        if (this.acroDict == null)
                        {
                            updateIndirectRefs(d, innerAcroDict,
                                false);
                            this.acroDict = (PdfDict) innerAcroDict;
                        }
                        else
                        {
                            this_dr = this.acroDict.getValue(DR);
                            this_dr = (PdfDict) reader
                                .getObject(this_dr);
                            inner_dr = ((PdfDict) innerAcroDict)
                                .getValue(DR);
                            inner_dr = ((PdfDict) d.reader
                                .getObject(inner_dr));
                            mergeResources((PdfDict) inner_dr,
                                (PdfDict) this_dr, d);
                        }
                    }
                }
            }
            catch (ClassCastException cce)
            {
                throw new PdfBadFileException(
                    "Invalid object in Catalog.");
            }
        }
    }*/
    
    protected synchronized void setObjects() throws IOException,
        PdfException
    {
        versionId = Integer.parseInt(version.substring(version
            .indexOf(".") + 1, version.length()));

        if (pageTree != null)
        {
            setPageTree();
            setProcSet();
        }

        setObjMaps();
        
        if (getBookmarkRoot() != null
            && getBookmarkRoot().getChildList().size() == 0)
        {
            bookmarkTree = null;
        }
        
        if (bookmarkTree != null)
        {
            setBookmarks();
        }

        if (addnDict != null)
        {
            setAddnAction();
        }

        setCatalog();

        setMode();

        setLayout();
        
        setDocInfo();
        
        if (encryptDict != null)
        {
            setEncryptDict();
        }
        
        if (versionId > 4)
        {
            crossRefStream = new PdfCrossReferenceStream();
        }
    }

    protected synchronized void writeObjects() throws IOException,
        PdfException
    {
        /* each of these methods updates bytesWritten */
        writeHeader();
        
        writeCatalog();
        
        writeDocInfo();
        
        writeAction();
        
        if (pageTree != null)
        {
            writePages();
            
            writeProcSet();
            
            if (fontMap != null)
            {
                writeFonts();    
            }
            if (xObjMap != null)
            {
                writeImages();
            }
        }
        if (bookmarkTree != null)
        {
            writeBookmarks();
        }
        if (versionId > 4)
        {
            writeObjectStreams();
        }
        
        writeUnknownObjs();
        
        if (encryptDict != null)
        {
            writeEncryt();
        }
    }

    /**
     * Returns constant identifying this <code>PdfDocument</code>'s
     * default page mode. This page mode is applied by default when
     * the document is opened in a viewer application.
     * 
     * @return constant identifying page mode
     * @since 1.0
     * @see #setPageMode(int)
     * @see PdfPageMode
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfBookmark.ExampleSyntax.htm#PdfDocument_getPageMode">example</a>.
     */
    public synchronized int getPageMode()
    {
        return this.pageModeValue;
    }

    /**
     * Specifies default page mode with which this
     * <code>PdfDocument</code> needs to be opened.
     * 
     * @param value
     *            constant specifying page mode 
     * @since 1.0
     * @see #getPageMode()
     * @see PdfPageMode 
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfBookmark.ExampleSyntax.htm#PdfDocument_setPageMode">example</a>.
     */
    public synchronized void setPageMode(int value)
    {
        this.pageModeValue = value;
    }

    /**
     * Returns constant identifying page layout used as default when
     * opening this document.
     * 
     * @return constant identifying the page layout
     * @since 1.0
     * @see #setPageLayout(int)
     * @see PdfPageLayout
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#getPageLayout">example</a>.
     */
    public synchronized int getPageLayout()
    {
        return pageLayoutValue;
    }

    /**
     * Specifies default page layout to be used when opening this
     * document.
     * 
     * @param value
     *            constant specifying the page layout 
     * @since 1.0
     * @see #getPageLayout()
     * @see PdfPageLayout 
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#setPageLayout">example</a>.
     */
    public synchronized void setPageLayout(int value)
    {
        this.pageLayoutValue = value;
    }

    /**
     * Returns a <code>PdfBookmark</code> object that points to the
     * root of the bookmark tree of this <code>PdfDocument</code>.
     * <u>Should be used only if the <code>PdfDocument</code> was 
     * created with a <code>PdfWriter</code> object</u>.
     * <p> 
     * The root is at the top of the hierarchy that contains  
     * bookmarks displayed in the document outline. The 
     * {@link PdfBookmark#getFirstChild()} method can be used on 
     * the <code>PdfBookmark</code> returned by  
     * <code>getBookmarkRoot()</code> to navigate to the first 
     * bookmark in the document outline.
     * </p>  
     * 
     * @return 
     *           <code>PdfBookmark</code> object that points to the
     *           root of the bookmark tree
     * @throws PdfException
     *           An illegal argument was supplied
     * @since 1.0
     * @see #getFirstBookmark()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfBookmark.ExampleSyntax.htm#PdfDocument_getBookmarkRoot">example</a>.
     */
    public synchronized PdfBookmark getBookmarkRoot()
        throws PdfException
    {
        if (bookmarkTree == null)
        {
            return null;
        }
        return (PdfBookmark) bookmarkTree.getRoot();
    }

    /**
     * Returns first bookmark in this <code>PdfDocument</code>'s
     * document outline. <u>Should be used only if the 
     * <code>PdfDocument</code> was created with a 
     * <code>PdfReader</code> object</u>. 
     * 
     * @return a <code>PdfBookmark</code> object of the first
     *         bookmark in the document outline
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @see #getBookmarkRoot()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfBookmark.ExampleSyntax.htm#PdfDocument_getFirstBookmark">example</a>.
     */
    public synchronized PdfBookmark getFirstBookmark()
        throws PdfException
    {
        if (bookmarkTree == null)
        {
            return null;
        }
        return (PdfBookmark) bookmarkTree.getRoot().getChild(0);
    }

    /*public*/ synchronized PdfBookmark addBookmark(PdfBookmark b,
        PdfBookmark parent) throws PdfException
    {
//        if (pageTree == null)
//        {
//            return null;
//        }
        if (bookmarkTree == null)
        {
            bookmarkTree = new PdfBookmarkTree(this);
        }
        if (parent == null)
        {
            parent = (PdfBookmark) bookmarkTree.root;
        }
        parent.addChild(b);

        return b;
    }
    
    /**
     * Returns a new bookmark that leads to position 
     * (<code>left</code>, <code>top</code>) on specified page, 
     * displays the page with specified zoom, and is added as a child 
     * bookmark under <code>parent</code> with specified title.
     * 
     * @param title
     *            text to be used to display the new bookmark
     * @param parent
     *            bookmark under which the new bookmark is to be
     *            created
     * @param pageNo
     *            number of the page that is to be displayed when
     *            bookmark is selected
     * @param left
     *            offset of the position from (0, top) (expressed in
     *            points)
     * @param top
     *            offset of the position from (left, 0) (expressed in
     *            points)
     * @param zoom
     *            zoom factor to be applied when displaying the page
     * @return a new bookmark created under parent
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#addBookmark_String_PdfBookmark_int_double_double_double">example</a>.
     */
    public synchronized PdfBookmark addBookmark(String title,
        PdfBookmark parent, int pageNo, double left, double top,
        double zoom) throws PdfException
    {
//        if (pageTree == null
//            || pageNo > PdfPageTree.count(pageTree.getRoot()))
//        {
//            return null;
//        }
        if (pageTree == null || pageNo > pageTree.getCount()
            || pageNo <= 0)
        {
            return null;
        }
        if (bookmarkTree == null)
        {
            bookmarkTree = new PdfBookmarkTree(this);
        }
        if (parent == null)
        {
            parent = (PdfBookmark) bookmarkTree.root;
        }

        PdfBookmark b = new PdfBookmark(title, pageNo);
        ArrayList list = new ArrayList();
        list.add(new PdfName(PDF_XYZ));
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
        hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_ACTION));
        hm.put(new PdfName(PDF_S), new PdfName(PDF_GOTO_ACTION));
        hm.put(new PdfName(PDF_D), new PdfArray(list));

        b.addToActionList(new PdfDict(hm));
        parent.addChild(b);

        return b;
    }

    /**
	 * Returns a new bookmark that leads to specified page and is 
	 * added as a child bookmark under <code>parent</code> 
	 * with specified title.
	 * 
	 * @param title
	 *            text to be used to display bookmark
	 * @param parent
	 *            bookmark under which the new bookmark is to be 
	 *            created
	 * @param pageNo
	 *            number of the page that is to be displayed when 
	 *            bookmark is selected
	 * @return a new bookmark created under <code>parent</code>
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#addBookmark_String_PdfBookmark_int">example</a>.
	 */
    public synchronized PdfBookmark addBookmark(String title,
        PdfBookmark parent, int pageNo) throws PdfException
    {
//        if (pageTree == null
//            || pageNo > PdfPageTree.count(pageTree.getRoot())
//            || pageNo <= 0)
//        {
//            return null;
//        }
        if (pageTree == null || pageNo > pageTree.getCount()
            || pageNo <= 0)
        {
            return null;
        }
        if (bookmarkTree == null)
        {
            bookmarkTree = new PdfBookmarkTree(this);
        }
        if (parent == null)
        {
            parent = (PdfBookmark) bookmarkTree.root;
        }

        PdfBookmark b = new PdfBookmark(title, pageNo);
        ArrayList list = new ArrayList();
        list.add(new PdfName(PDF_FIT));
        HashMap hm = new HashMap();
        hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_ACTION));
        hm.put(new PdfName(PDF_S), new PdfName(PDF_GOTO_ACTION));
        hm.put(new PdfName(PDF_D), new PdfArray(list));

        b.addToActionList(new PdfDict(hm));
        parent.addChild(b);

        return b;
    }

    /**
	 * Returns a new bookmark that leads to a destination specified 
	 * by <code>pageNo</code>, <code>pos</code> and <code>fit</code>, 
	 * and is added as a child bookmark under <code>parent</code> 
	 * with specified title.
	 * <p>
	 * <table border="1" cellpadding="5" 
	 *  summary="fit constant, pos, how page is displayed">
	 *  <tr>
	 *   <th align="center" width="15%"> 
	 *    <code>fit</code> 
	 *   </th>
	 *   <th align="center" width="15%">
	 *    <code>pos</code>
	 *   </th>
	 *   <th align="center" width="70%"> 
	 *    Page Display 
	 *   </th>
	 *  </tr>
	 *  <tr>
	 *   <td>{@link PdfBookmark#FITH}</td>
	 *   <td>vertical coordinate of destination</td>
	 *   <td>
	 *    <ul>
	 *     <li><code>pos</code> is positioned on the top edge of the 
	 *      window.</li>
  	 *     <li>Page is zoomed to tightly fit the entire width of the
  	 *      page inside the window. </li>
	 *   </ul>
	 *  </td>
	 * </tr>
	 * <tr>
	 *  <td>{@link PdfBookmark#FITBH}</td>
	 *  <td>vertical coordinate of destination</td>
	 *  <td>
	 *   <ul>
	 *    <li><code>pos</code> is positioned on top edge of the 
	 *    window.</li>
	 *    <li>Page is zoomed to tightly fit the entire width of its
	 *     <a href="{@docRoot}/doc-files/glossary.htm#bounding_box" 
	 *     target="_GnosticeGlossaryWindow">bounding box</a> inside the window.
	 *    </li>
	 *  </ul>
	 *  </td>
	 *  </tr>
	 *  <tr>
	 *   <td>{@link PdfBookmark#FITBV}</td>
	 *   <td>horizontal coordinate of destination</td>
	 *    <td>
	 *     <ul>
	 *      <li><code>pos</code> is positioned on the left edge of the window.</li>
	 *      <li>Page is zoomed to tightly fit the entire height of its <a href="{@docRoot}/doc-files/glossary.htm#bounding_box"
	 *       target="_GnosticeGlossaryWindow">bounding box</a> inside the window.</ul>
	 *     </td>
	 *   </tr>
	 *  <tr>
	 *   <td>{@link PdfBookmark#FITV}</td>
	 *   <td>horizontal coordinate of destination</td>
	 *   <td>
	 *   <ul>
	 *    <li><code>pos</code> is positioned on the left edge of the
	 *     window.</li>
	 *    <li>Page is zoomed to tightly fit the entire height of the
	 *     page insidethe window.</li>
	 *   </ul>
	 *  </td>
	 *  </tr>
	 * </table>
	 * 
	 * @param title
	 *            text to be used to display bookmark
	 * @param parent
	 *            bookmark under which the new bookmark is to be 
	 *            created
	 * @param pageNo
	 *            number of the page that is to be displayed when
	 *            bookmark is selected
	 * @param pos
	 *            horizontal or vertical coordinate of the bookmark's
	 *            destination
	 * @param fit
	 *            constant determining how page is displayed inside 
	 *            window
	 * @return a new bookmark created under parent
	 * @throws PdfException
	 *             if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#addBookmark_String_PdfBookmark_int_double_int">example</a>.
	 */
    public synchronized PdfBookmark addBookmark(String title,
        PdfBookmark parent, int pageNo, double pos, int fit)
        throws PdfException
    {
//        if (pageTree == null
//            || pageNo > PdfPageTree.count(pageTree.getRoot()))
//        {
//            return null;
//        }
        if (pageTree == null || pageNo > pageTree.getCount()
            || pageNo <= 0)
        {
            return null;
        }
        if (bookmarkTree == null)
        {
            bookmarkTree = new PdfBookmarkTree(this);
        }
        if (parent == null)
        {
            parent = (PdfBookmark) bookmarkTree.root;
        }

        PdfBookmark b = new PdfBookmark(title, pageNo);
        ArrayList list = new ArrayList();
        switch (fit)
        {
            case PdfBookmark.FITH:
                list.add(new PdfName(PDF_FITH));
                break;

            case PdfBookmark.FITBH:
                list.add(new PdfName(PDF_FITBH));
                break;

            case PdfBookmark.FITBV:
                list.add(new PdfName(PDF_FITBV));
                break;

            case PdfBookmark.FITV:
                list.add(new PdfName(PDF_FITV));
                break;

            default:
                throw new PdfException("invalid destination type");
        }
        list.add(new PdfFloat((float) pos));

        HashMap hm = new HashMap();
        hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_ACTION));
        hm.put(new PdfName(PDF_S), new PdfName(PDF_GOTO_ACTION));
        hm.put(new PdfName(PDF_D), new PdfArray(list));
        b.addToActionList(new PdfDict(hm));
        parent.addChild(b);

        return b;
    }

    /**
	 * Returns a new bookmark that leads to specified page, displays 
	 * the page so as to fit entire height and weight of its bounding 
	 * box inside window, and is added to as a child bookmark under 
	 * <code>parent</code> with specified title.
	 * 
	 * @param title
	 *            text to be used to display the new bookmark
	 * @param parent
	 *            bookmark under which the new bookmark is to be 
	 *            created
	 * @param pageNo
	 *            number of the page that is to be displayed when 
	 *            bookmark is selected
	 * @param fit
	 *            constant determining how page is displayed inside 
	 *            window (Always is {@link PdfBookmark#FITB})
	 * @return a new bookmark created under parent
	 * @throws PdfException
	 *             if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#addBookmark_String_PdfBookmark_int_int">example</a>.
	 */
    public synchronized PdfBookmark addBookmark(String title,
        PdfBookmark parent, int pageNo, int fit) throws PdfException
    {
//        if (pageTree == null
//            || pageNo > PdfPageTree.count(pageTree.getRoot()))
//        {
//            return null;
//        }
        if (pageTree == null || pageNo > pageTree.getCount()
            || pageNo <= 0)
        {
            return null;
        }
        if (bookmarkTree == null)
        {
            bookmarkTree = new PdfBookmarkTree(this);
        }
        if (parent == null)
        {
            parent = (PdfBookmark) bookmarkTree.root;
        }

        PdfBookmark b = new PdfBookmark(title, pageNo);
        ArrayList list = new ArrayList();
        switch (fit)
        {
            case PdfBookmark.FITB:
                list.add(new PdfName(PDF_FITB));
                break;

            default:
                throw new PdfException("invalid destination type");
        }

        HashMap hm = new HashMap();
        hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_ACTION));
        hm.put(new PdfName(PDF_S), new PdfName(PDF_GOTO_ACTION));
        hm.put(new PdfName(PDF_D), new PdfArray(list));
        b.addToActionList(new PdfDict(hm));
        parent.addChild(b);

        return b;
    }

    /**
	 * Returns a new bookmark that leads to specified rectangular 
	 * area on specified page and is added as a child bookmark under 
	 * <code>parent</code> with specified title.
	 * 
	 * @param title
	 *            text to be used to display bookmark
	 * @param parent
	 *            bookmark under which the new bookmark is to be 
	 *            created
	 * @param pageNo
	 *            number of the page that is to be displayed when 
	 *            bookmark is selected
	 * @param rect
	 *            rectangular area on the specified page
	 * @return a new bookmark created under parent
	 * @throws PdfException
	 *             if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#addBookmark_String_PdfBookmark_int_Rectangle">example</a>.
	 */
    public synchronized PdfBookmark addBookmark(String title,
        PdfBookmark parent, int pageNo, Rectangle rect)
        throws PdfException
    {
        return (addBookmark(title, parent, pageNo, rect.x,
            rect.height, rect.width, rect.y));
    }

    /**
	 * Returns a new bookmark that leads to specified rectangle on 
	 * specified page and is added as a child bookmark under 
	 * <code>parent</code> with specified title.
	 * 
	 * @param title
	 *            text to be used to display bookmark
	 * @param parent
	 *            bookmark under which the new bookmark is to be 
	 *            created
	 * @param pageNo
	 *            number of the page that is to be displayed when 
	 *            bookmark is selected
	 * @param rect
	 *            rectangle on the specified page
	 * @return a new bookmark created under parent
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#addBookmark_String_PdfBookmark_int_PdfRectangle">example</a>.
	 */
    public synchronized PdfBookmark addBookmark(String title,
        PdfBookmark parent, int pageNo, PdfRect rect)
        throws PdfException
    {
        return (addBookmark(title, parent, pageNo, rect.x,
            rect.height, rect.width, rect.y));
    }

    public synchronized PdfBookmark addBookmark(String title,
        PdfBookmark parent, int pageNo, double x, double y,
        double width, double height) throws PdfException
    {
//        if (pageTree == null
//            || pageNo > PdfPageTree.count(pageTree.getRoot()))
//        {
//            return null;
//        }
        if (pageTree == null || pageNo > pageTree.getCount()
            || pageNo <= 0)
        {
            return null;
        }
        if (bookmarkTree == null)
        {
            bookmarkTree = new PdfBookmarkTree(this);
        }
        if (parent == null)
        {
            parent = (PdfBookmark) bookmarkTree.root;
        }

        PdfBookmark b = new PdfBookmark(title, pageNo);
        ArrayList list = new ArrayList();
        list.add(new PdfName(PDF_FITR));
        list.add(new PdfFloat((float) x));
        list.add(new PdfFloat((float) height));
        list.add(new PdfFloat((float) width));
        list.add(new PdfFloat((float) y));
        
        HashMap hm = new HashMap();
        hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_ACTION));
        hm.put(new PdfName(PDF_S), new PdfName(PDF_GOTO_ACTION));
        hm.put(new PdfName(PDF_D), new PdfArray(list));
        b.addToActionList(new PdfDict(hm));
        parent.addChild(b);

        return b;
    }

    public synchronized PdfBookmark addBookmark(String title,
        PdfBookmark parent, String javascriptOrURI, int actionType)
        throws PdfException
    {
        if (bookmarkTree == null)
        {
            bookmarkTree = new PdfBookmarkTree(this);
        }
        if (parent == null)
        {
            parent = (PdfBookmark) bookmarkTree.root;
        }
        PdfBookmark b = new PdfBookmark(title);
        HashMap hm = new HashMap();

        switch (actionType)
        {
            case PdfAction.URI:
                hm.put(new PdfName(PDF_TYPE), new PdfName(
                        PDF_ACTION));
                hm.put(new PdfName(PDF_S),
                    new PdfName(PDF_URI_ACTION));
                hm.put(new PdfName(PDF_URI_ACTION), new PdfString(
                    javascriptOrURI, true));
                break;

            case PdfAction.JAVASCRIPT:
                hm.put(new PdfName(PDF_TYPE), new PdfName(
                        PDF_ACTION));
                hm.put(new PdfName(PDF_S), new PdfName(
                    PDF_JAVASCRIPT_ACTION));
                hm.put(new PdfName(PDF_JS),
                    new PdfString(javascriptOrURI, true));
                break;

            default:
                throw new PdfException("Invalid action type");
        }

        b.addToActionList(new PdfDict(hm));
        parent.addChild(b);

        return b;
    }

    public synchronized PdfBookmark addBookmark(String title,
        PdfBookmark parent, String applicationToLaunch, boolean print)
        throws PdfException
    {
        if (bookmarkTree == null)
        {
            bookmarkTree = new PdfBookmarkTree(this);
        }
        if (parent == null)
        {
            parent = (PdfBookmark) bookmarkTree.root;
        }
        PdfBookmark b = new PdfBookmark(title);
        HashMap hm = new HashMap();

        hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_ACTION));
        hm.put(new PdfName(PDF_S), new PdfName(PDF_LAUNCH_ACTION));
        hm.put(new PdfName(PDF_F), new PdfString(applicationToLaunch,
            true));
        
        Map winHm = new HashMap();
        winHm.put(new PdfName(PDF_F), new PdfString(
            applicationToLaunch, true));
        winHm.put(new PdfName(PDF_O), print ? new PdfString("print",
          false) : new PdfString("open", false));
        hm.put(new PdfName("Win"), new PdfDict(winHm));
        
        b.addToActionList(new PdfDict(hm));
        parent.addChild(b);

        return b;
    }

    public synchronized PdfBookmark addBookmark(String title,
        PdfBookmark parent, String pdfFileName, int pageNo,
        boolean newWindow) throws PdfException
    {
        if (bookmarkTree == null)
        {
            bookmarkTree = new PdfBookmarkTree(this);
        }
        if (parent == null)
        {
            parent = (PdfBookmark) bookmarkTree.root;
        }
        PdfBookmark b = new PdfBookmark(title);
        HashMap hm = new HashMap();

        ArrayList list = new ArrayList();
        list.add(new PdfInteger(pageNo - 1));
        list.add(new PdfName(Usable.PDF_FITH));
        //Has to pass the Explicit destination properties

        hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_ACTION));
        hm.put(new PdfName(PDF_S), new PdfName(
                PDF_REMOTEGOTO_ACTION));
        hm.put(new PdfName(PDF_F), new PdfString(pdfFileName, true));
        hm.put(new PdfName(PDF_D), new PdfArray(list));
        hm.put(new PdfName(PDF_NEWWINDOW), new PdfBoolean(newWindow));

        b.addToActionList(new PdfDict(hm));
        parent.addChild(b);

        return b;
    }

    /**
	 * Returns a new bookmark that performs specified action and is
	 * added as a child bookmark under <code>parent</code> with 
	 * specified title.
	 * 
	 * @param namedAction
	 *            action to be be performed when bookmark is selected 
	 * @param title
	 *            text to be used to display the new bookmark
	 * @param parent
	 *            bookmark under which the new bookmark is to be 
	 *            created
	 * @return new bookmark created under <code>parent</code>
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#addBookmark_int_String_PdfBookmark">example</a>.
	 */
    public synchronized PdfBookmark addBookmark(int namedAction,
        String title, PdfBookmark parent)
    {
        if (bookmarkTree == null)
        {
            bookmarkTree = new PdfBookmarkTree(this);
        }
        if (parent == null)
        {
            parent = (PdfBookmark) bookmarkTree.root;
        }
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
        parent.addChild(b);

        return b;
    }
    
    public synchronized void addAction(int actionType,
        String applicationToLaunch, boolean isPrint,
        String parameterToApplication) throws IOException,
        PdfException
    {
        if (actionType == PdfAction.LAUNCH)
        {
            Map hm = new HashMap();
            hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_ACTION));
            hm.put(new PdfName(PDF_S), new PdfName(
                    PDF_LAUNCH_ACTION));
            hm.put(new PdfName(PDF_F), new PdfString(
                applicationToLaunch, true));
            
            Map winHm = new HashMap();
            winHm.put(new PdfName(PDF_F), new PdfString(
                applicationToLaunch, true));
            if (isPrint)
            {
                winHm.put(new PdfName(PDF_O), new PdfString("print",
                    false));
            }
            
            if (parameterToApplication != null)
            {
                winHm.put(new PdfName("P"), new PdfString(
                    parameterToApplication, false));
            }
            hm.put(new PdfName("Win"), new PdfDict(winHm));

            if (actionList == null)
            {
                actionList = new ArrayList();
            }
            actionList.add(new PdfDict(hm));
        }
        else
        {
            throw new PdfException("Invalid Action Type...");
        }
    }

    /**
     * Sets this document to execute action specified by
     * <code>javascriptOrURI</code> when the document is displayed.
     * 
     * @param actionType
     *            constant specifying the type of action that needs
     *            to be executed
     * @param javascriptOrURI
     *            Javascript statement or Uniform Resource Indicator 
     *            (URI) that needs to be executed
     * @throws IOException
     *            if an illegal argument is supplied.
     * @throws PdfException
     *            if an I/O error occurs.
     * @since 1.0
     * @see PdfAction#JAVASCRIPT
     * @see PdfAction#URI
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#addAction_int_String">example</a>.
     */
    public synchronized void addAction(int actionType,
        String javascriptOrURI) throws IOException, PdfException
    {
        if (actionType == PdfAction.URI)
        {
            Map hm = new HashMap();

            hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_ACTION));
            hm.put(new PdfName(PDF_S), new PdfName(PDF_URI_ACTION));
            hm.put(new PdfName(PDF_URI_ACTION), new PdfString(javascriptOrURI,
                true));

            if (actionList == null)
            {
                actionList = new ArrayList();
            }
            actionList.add(new PdfDict(hm));
            
            return;
        }
        if (actionType == PdfAction.JAVASCRIPT)
        {
            Map hm = new HashMap();

            hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_ACTION));
            hm.put(new PdfName(PDF_S), new PdfName(
                PDF_JAVASCRIPT_ACTION));
            hm.put(new PdfName(PDF_JS), new PdfString(javascriptOrURI, true));

            if (actionList == null)
            {
                actionList = new ArrayList();
            }
            actionList.add(new PdfDict(hm));
            
            return;
        }
        
        throw new PdfException("Invalid Action Type...");
    }

    public synchronized void addAction(int event,
        int actionType, String javascript) throws IOException,
        PdfException
    {
        Map hm = new HashMap();

        if (actionType == PdfAction.JAVASCRIPT)
        {
            hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_ACTION));
            hm.put(new PdfName(PDF_S), new PdfName(
                PDF_JAVASCRIPT_ACTION));
            hm.put(new PdfName(PDF_JS), new PdfString(javascript,
                true));

            if (additionalList == null)
            {
                additionalList = new ArrayList();
            }
            additionalList.add(new PdfDict(hm));

            if (addnDict == null)
            {
                addnDict = new PdfDict(new HashMap());
            }
            switch (event)
            {
                case PdfAction.PdfEvent.ON_DOCUMENT_CLOSE:
                    addnDict.getMap().put(new PdfName("WC"),
                        new PdfDict(hm));
                    break;
                case PdfAction.PdfEvent.ON_BEFORE_DOCUMENT_PRINT:
                    addnDict.getMap().put(new PdfName("WP"),
                        new PdfDict(hm));
                    break;
                case PdfAction.PdfEvent.ON_AFTER_DOCUMENT_PRINT:
                    addnDict.getMap().put(new PdfName("DP"),
                        new PdfDict(hm));
                    break;
                case PdfAction.PdfEvent.ON_BEFORE_DOCUMENT_SAVE:
                    addnDict.getMap().put(new PdfName("WS"),
                        new PdfDict(hm));
                    break;
                case PdfAction.PdfEvent.ON_AFTER_DOCUMENT_SAVE:
                    addnDict.getMap().put(new PdfName("DS"),
                        new PdfDict(hm));
                    break;
                default:
                    throw new PdfException("Invalid Event Type...");
            }
        }
        else
        {
            throw new PdfException("Invalid Action Type...");
        }
    }

    /**
	 * Sets this document to execute action specified by 
     * <code>namedAction</code> when the document is displayed.
     * <p></p> 
	 * <table border="1" cellpadding="5" summary="PdfAction, effect">
	 * <tr>
	 * <th><code>namedAction</code></th>
	 * <th>Effect</th>
	 * </tr>
	 * <tr>
	 * <td>{@link PdfAction#NAMED_FIRSTPAGE}</td>
	 * <td>Navigation to the first page</td>
	 * </tr>
	 * <tr>
	 * <td>{@link PdfAction#NAMED_LASTPAGE}</td>
	 * <td>Navigation to the last page</td>
	 * </tr>
	 * <tr>
	 * <td>{@link PdfAction#NAMED_NEXTPAGE}</td>
	 * <td>Navigation to the next page</td>
	 * </tr>
	 * <tr>
	 * <td>{@link PdfAction#NAMED_PREVPAGE}</td>
	 * <td>Navigation to the previous page</td>
	 * </tr>
	 * </table>
	 * 
	 * @param namedAction
	 *            constant specifying the action to be executed 
     *            when the document is displayed
	 * @since 1.0
     * @see PdfAction
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#addAction_int">example</a>.
	 */
    public synchronized void addAction(int namedAction)
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
                return;
        }
        if (actionList == null)
        {
            actionList = new ArrayList();
        }
        actionList.add(new PdfDict(hm));
    }
    
    /**
     * Adds specified <code>PdfPage</code> to this
     * <code>PdfDocument</code>.
     * <p>
     * It is not recommended that a <code>PdfPage</code> object is 
     * added to the same document or to multiple documents more than 
     * once. If at all necessary, it is better to clone the 
     * <code>PdfPage</code> object as many times.
     * </p>
     * 
     * @param p
     *            <code>PdfPage</code> to be added
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#add">example</a>.
     */
    public synchronized void add(PdfPage p) throws PdfException
    {
        if (p == null)
        {
            return;
        }
        if (pageTree == null)
        {
            pageTree = new PdfPageTree(this);
        }
        if (mode == WRITING_MODE)
        {
            pageTree.insert(p);
        }
        else if (mode == READING_MODE)
        {
            pageTree.insert(pageTree.getCount() + 1, p);
        }
    }

    /**
     * Adds specified <code>PdfPage</code> to this
     * <code>PdfDocument</code> and, if
     * <code>setAsCurrentPage</code> is true, sets the
     * <code>PdfPage</code> as the <code>PdfDocument</code>'s
     * current page. 
     * <p>
     * By default, the first page that is added to a
     * <code>PdfDocument</code> is the default current page. If some
     * content has been written directly to a 
     * <code>PdfDocument</code> that is without first adding a page, 
     * then a default page is automatically added to it and becomes 
     * its current page.
     * </p>
     * 
     * @param p
     *            <code>PdfPage</code> to be added
     * @param setAsCurrentPage
     *            whether the <code>PdfPage</code> should be set as 
     *            the current page
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#add_boolean">example</a>.
     */
    public synchronized void add(PdfPage p, boolean setAsCurrentPage)
        throws PdfException
    {
        if (p != null)
        {
            add(p);
            if(setAsCurrentPage)
            {
                currentPage = pageTree.getCount();
            }
        }
     }
    
    /**
     * Specifies title entry to be used in this
     * <code>PdfDocument</code>'s document information dictionary.
     * 
     * @param s
     *            title entry to be used in the document information
     *            dictionary
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#setTitle">example</a>.
     */
    public void setTitle(String s)
    {
        infoDict.getMap().put(new PdfName(PDF_TITLE),
            new PdfTextString(s, true));
    }

    /**
     * Specifies author entry to be used in this
     * <code>PdfDocument</code>'s document information dictionary.
     * 
     * @param s
     *            author entry to be used in the document information 
     *            dictionary
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#setAuthor">example</a>.
     */
    public void setAuthor(String s)
    {
        infoDict.getMap().put(new PdfName(PDF_AUTHOR),
            new PdfTextString(s, true));
    }

    /**
     * Specifies subject entry to be used in this
     * <code>PdfDocument</code>'s document information dictionary.
     * 
     * @param s
     *            author entry to be used in the document information
     *            dictionary
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#setSubject">example</a>.
     */
    public void setSubject(String s)
    {
        infoDict.getMap().put(new PdfName(PDF_DOC_SUBJECT),
            new PdfTextString(s, true));
    }

    /**
     * Specifies keywords entry to be used in this
     * <code>PdfDocument</code>'s document information dictionary.
     * 
     * @param s
     *            keywords entry to be used in the document 
     *            information dictionary
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#setKeywords">example</a>.
     */
    public void setKeywords(String s)
    {
        infoDict.getMap().put(new PdfName(PDF_KEYWORDS),
            new PdfTextString(s, true));
    }

    /**
     * Specifies producer entry to be used in this
     * <code>PdfDocument</code>'s document information dictionary.
     * 
     * @param s
     *            producer entry to be used in the document
     *            information dictionary
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#setProducer">example</a>.
     */
    public void setProducer(String s)
    {
        infoDict.getMap().put(new PdfName(PDF_PRODUCER),
            new PdfTextString(s, true));
    }

	/*
	 * READ CAREFULLY
	 * --------------
	 * 
	 * These methods mark(PdfStdPage) and mark() are used to put trial mark on
	 * documents generated with unregistered JARs of all editions of Gnostice
	 * PDFOne Java.
	 * 
	 * Since Java doesn't support conditional compilation, we use a customised
	 * Ant Build script (XML) to "remove" these two methods [mark(PdfStdPage)
	 * and mark()] from source code [this file] to eliminate occurrence of any
	 * trial mark on documents generated with registered JAR of any edition of
	 * Gnostice PDFOne Java.
	 * 
	 * Hence all Gnostice PDFOne Java developers must take into notice the
	 * following very strictly :-
	 * 
	 * 1. DO NOT MODIFY THE SEQUENCE '// FOLLOWED BY [TAG]' AND '// FOLLOWED BY
	 * [ENDTAG]' APPEARING ANYWHERE IN THE SOURCE IN ANY .JAVA FILE IN THIS
	 * PROJECT AS THESE ARE USED BY THE ANT BUILD PROGRAM TO ELIMINATE mark()
	 * METHOD CALL AND IMPLEMENTATION FROM REGISTERED JAR. EVEN A SINGLE SPACE
	 * BETWEEN THE ABOVE MENTIONED SQUENCES WILL MAKE ANT BUILD FAIL FOR
	 * REGISTERED JARS.
	 * 
	 * 2. DO NOT PUT MULTILINE JAVA COMMENT IN BETWEEN THE SEQUENCE '// FOLLOWED
	 * BY [TAG]' AND '// FOLLOWED BY [ENDTAG]'. THIS MEANS THERE SHOULD NOT BE
	 * "/*" AND " * /" PRESENT IN THE SOURCE STARTING FROM "[TAG]" TILL
	 * "[ENDTAG]".
	 */    
    
    /*
    private void mark(PdfStdPage p) throws IOException,
        PdfException
    {
        PdfFont t = PdfFont.create("Arial", 2, 35, 0);
        t.setColor(Color.RED);
        PdfFont s = PdfFont.create("Arial", 2, 22, 0);
        s.setColor(Color.BLUE);
        
        int tempMu = p.measurementUnit;
        p.setMeasurementUnit(this.measurementUnit);
        PdfFont tempFont = p.prevFont;
        
        double avlWidth = p.pageWidth - p.pageCropLeft
            - p.pageCropRight;
        double avlHeight = p.pageHeight - p.pageCropTop
            - p.pageCropBottom;
        double tWidth = t.getWidth(Usable.TEXT, 1);
        while (tWidth > avlHeight)
        {
            t.setSize(t.getSize() - 2);
            tWidth = t.getWidth(Usable.TEXT, 1);
        }
        double tHeight = t.getHeight();
        s.setSize(Math.max(t.getSize() - 5, 0));
        double sWidth = s.getWidth(Usable.SITE, 1);
        double sHeight = s.getHeight();
        PdfTextFormatter tf = new PdfTextFormatter();

        double x = PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, (-tWidth / 2) + (avlWidth / 8)
            + p.pageCropLeft);
        double y = PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, (avlHeight - tHeight)/ 2
            + p.pageCropTop);
        PdfRect r = new PdfRect(x, y, PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, tWidth),
            PdfMeasurement.convertToMeasurementUnit(measurementUnit, 
                tHeight));
        tf.setRotation(90);
        p.writeText(Usable.TEXT, r, t, tf, measurementUnit, true);

        x = PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, (-sWidth / 2) + (avlWidth / 8) + tHeight
            + p.pageCropLeft);
        r = new PdfRect(x, y, PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, sWidth),
            PdfMeasurement.convertToMeasurementUnit(measurementUnit, 
                sHeight));
        tf.setRotation(90);
        p.writeText(Usable.SITE, r, s, tf, measurementUnit, true);

        x = PdfMeasurement.convertToMeasurementUnit(measurementUnit,
                p.pageWidth - p.pageCropRight - tWidth / 2 
                    - avlWidth / 8);
        r = new PdfRect(x, y, PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, tWidth),
            PdfMeasurement.convertToMeasurementUnit(measurementUnit, 
                tHeight));
        tf.setRotation(-90);
        p.writeText(Usable.TEXT, r, t, tf, measurementUnit, true);
        
        x = PdfMeasurement.convertToMeasurementUnit(measurementUnit,
                p.pageWidth - p.pageCropRight - sWidth / 2 
                    - avlWidth / 8  - tHeight);
        r = new PdfRect(x, y, PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, sWidth),
            PdfMeasurement.convertToMeasurementUnit(measurementUnit, 
                sHeight));
        tf.setRotation(-90);
        p.writeText(Usable.SITE, r, s, tf, measurementUnit, true);
        
        
//        double r1X = PdfMeasurement.convertToMeasurementUnit(
//            measurementUnit, (p.pageWidth / 10) + tHeight + sHeight / 2);
//        double r1Y = PdfMeasurement.convertToMeasurementUnit(
//            measurementUnit, (p.pageHeight - sWidth + sHeight) / 2); 
//        double r1W = PdfMeasurement.convertToMeasurementUnit(
//            measurementUnit, sHeight);
//        double r1H = PdfMeasurement.convertToMeasurementUnit(
//            measurementUnit, sWidth);
//        double r2X = PdfMeasurement.convertToMeasurementUnit(
//            measurementUnit, p.pageWidth - ((p.pageWidth / 10)
//                + tHeight + sHeight * 2));

//        if (p.contentList == null)
//        {
//            p.contentList = new ArrayList();
//        }
//        ByteBuffer bb = ((ByteBuffer) ByteBuffer.wrap(
//            p.contentStream.getBuffer()).limit(
//            p.contentStream.size())).slice();
//        p.contentList.add(p.contentList.size(), new PdfStream(
//            new PdfDict(new HashMap()), bb));
//
//        p.contentStream = temp;
        p.prevFont = tempFont;
        
//        PdfRect r1 = new PdfRect(r1X, r1Y, r1W, r1H);
//        PdfRect r2 = new PdfRect(r2X, r1Y, r1W, r1H);
//        PdfLinkAnnot la1 = new PdfLinkAnnot(r1, "Trial Link",
//            "Company URL", "Trial Annot", 0, Color.BLACK,
//            PdfLinkAnnot.HIGHLIGHT_MODE_INVERT);
//        PdfLinkAnnot la2 = new PdfLinkAnnot(r2, "Trial Link",
//            "Company URL", "Trial Annot", 0, Color.BLACK,
//            PdfLinkAnnot.HIGHLIGHT_MODE_INVERT); 
//        la1.addActionURI(Usable.SITE);
//        la2.addActionURI(Usable.SITE);
//        p.addAnnot(la1);
//        p.addAnnot(la2);
        p.measurementUnit = tempMu;
    }
    
    private void mark() throws IOException, PdfException
    {
        Vector v = getPages("-");
        if (v == null)
        {
            throw new PdfException("Invalid pageRange.");
        }
        for (Iterator iter = v.iterator(); iter.hasNext();)
        {
            PdfStdPage p = getPage(((Integer) iter.next())
                .intValue());
            mark(p);
        }
    }
    */
    
    /**
     * Saves contents in this <code>PdfDocument</code> to 
     * the <code>PdfDocument</code>'s output stream or file and 
     * returns number of bytes that was saved.
     * 
     * @return number of bytes that was saved to the output stream or
     *         file
     * @throws IOException
     *         if an I/O error occurs.
     * @throws PdfException
     *         if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#write">example</a>.
     */
    public synchronized long write() throws IOException,
        PdfException
    {
        if (isWritten)
        {
            return 0;
        }
        if (mode == READING_MODE)
        {
            writer = (reader.getOutFilePath() != null) ? PdfWriter
                .fileWriter(new File(reader.getOutFilePath())) : PdfWriter
                .streamWriter(reader.getOutputStream());
            
            writer.decompressStreams = !this.addDefaultFilter;
            
            if(reader.encryptDict != null)
            {
                writer.encryptDocument = true;
                writer.encryptor = this.encrypto == null ? reader.decryptor
                    : this.encrypto;
            }
            else if(this.encrypto != null)
            {
                writer.encryptor = this.encrypto;
                writer.encryptDocument = true;
            }
        }
        writtenObjs = new Hashtable();
        writer.writtenObjs = writtenObjs;

        if (pageTree == null || pageTree.getCount() == 0)
        {
            PdfPage p = new PdfPage();
            add(p);
        }
        
        /*
        mark();
        */
        
        setObjects();

        offset = new long[objectRun];
        offset[0] = (long) 0;

        if (versionId > 4)
        {
            objStreamId = new Hashtable();
            objStreamPos = new Hashtable();
        }
        
        writeObjects();
        
        if (versionId > 4)
        {
            writer.encryptDocument = false;
            writer.decompressStreams = false;
            writeCrossReferenceStream(catalog.getObjectNumber());
        }
        else
        {
            writeCrossRefTable(catalog.getObjectNumber());
        }

        if (openAfterSave)
        {
            File f = writer.getFile();
            if (f != null)
            {
                String osName = System.getProperty("os.name");

                if (osName.equals("Windows NT")
                    || osName.equals("Windows 2000")
                    || osName.equals("Windows 2003")
                    || osName.equals("Windows XP"))
                {
                    Runtime r = Runtime.getRuntime();
                    r.exec(new String[] { "cmd.exe", "/c",
                        "start " + f.getAbsolutePath() });
                }
                else if (osName.equals("Windows 95")
                    || osName.equals("Windows 98"))
                {
                    Runtime r = Runtime.getRuntime();
                    r.exec(new String[] { "command.com", "/c",
                        "start " + f.getAbsolutePath() });
                }
            }
        }
        /*if (emailAfterSave && eMails != null && writer.file != null)
        {
            String filePath = writer.file.getAbsolutePath();
            for (int i = 0, limit = eMails.size(); i < limit; ++i)
            {
                PdfEMail eMail = (PdfEMail) eMails.get(i);
                eMail.addAttachment(filePath);
                eMail.send();
            }
        }*/
        
        long retVal = this.bytesWritten;
        reset();
        
        return retVal;
    }

    private void reset() throws PdfException
    {
        this.objectRun = 1;
        this.bytesWritten = 0;
        this.isWritten = true;
        this.fontMap = null;
        this.xObjMap = null;
        this.objMaps = new Hashtable();
        this.unknownObjOffset = null;
        this.unknownObjMaps = null;
        this.writtenObjs = null;
        this.addDefaultFilter = true;
        this.pageTree.reset(this.pageTree.root);
        if (this.writer != null)
        {
            this.writer.writtenObjs = null;
        }
    }

    /**
     * Returns default measurement unit currently in use for this
     * <code>PdfDocument</code>.
     * 
     * @return constant identifying the current default measurement
     *         unit
     * @since 1.0
     * @see #setMeasurementUnit(int)
     * @see PdfMeasurement
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#getMeasurementUnit">example</a>.
     */
    public synchronized int getMeasurementUnit()
    {
        return measurementUnit;
    }

    /**
     * Specifies default measurement unit to be used for this 
     * <code>PdfDocument</code>.
     * 
     * @param measurementUnit
     *            constant specifying the new default measurement unit 
     * @since 1.0
     * @see #getMeasurementUnit()
     * @see PdfMeasurement
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#setMeasurementUnit">example</a>.
     */
    public synchronized void setMeasurementUnit(int measurementUnit)
    {
        this.measurementUnit = measurementUnit;
    }
    
    /**
     * Returns constant identifying compression level of this
     * <code>Pdfdocument</code>.
     * 
     * @return constant identifying the compression level 
     * @since 1.0
     * @see #setCompressionLevel(int)
     * @see PdfFlateFilter 
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#getCompressionLevel">example</a>.
     */
    public synchronized int getCompressionLevel()
    {
        return compressionLevel;
    }
    
    /**
     * Specifies compression level for this <code>PdfDocument</code>.
     * 
     * @param compressionLevel
     *            constant specifying the compression level 
     * @since 1.0
     * @see #getCompressionLevel()
     * @see PdfFlateFilter 
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#setCompressionLevel">example</a>.
     */
    public synchronized void setCompressionLevel(int compressionLevel)
    {
        this.compressionLevel = compressionLevel;
    }
    
    /**
     * Returns constant identifying this <code>PdfDocument</code>'s
     * PDF version.
     * 
     * @return constant identifying PDF version
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#getVersion">example</a>.
     */
    public synchronized String getVersion()
    {
        return version;
    }
    
    /**
     * Specifies PDF version of this <code>PdfDocument</code>.
     * 
     * @param version
     *            constant specifying PDF version
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#setVersion">example</a>.
     */
    public synchronized void setVersion(String version)
    {
        this.version = version;
    }
    
    /**
     * Specifies <code>PdfWriter</code> object to be used with this
     * <code>PdfDocument</code>.
     * 
     * @param w
     *            <code>PdfWriter</code> object to be used with this
     *            <code>PdfDocument</code>
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see #getWriter()
     * @see #setReader(PdfReader)
     * @see #getReader()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#setWriter">example</a>.
     */
    public synchronized void setWriter(PdfWriter w)
        throws PdfException
    {
        if (w == null)
        {
            throw new PdfException(
                "Illegal argument to setWriter()" +
                " [PdfWriter == null]");
        }
        if (w.inUse)
        {
            throw new PdfException(
                "PdfWriter already in use by other Document.");
        }
        if (mode == READING_MODE)
        {
            throw new PdfException(
                "Document already opened in reading mode.");
        }

        this.objMaps = new Hashtable();
        this.writer = w;
        w.inUse  = true;
        this.mode = WRITING_MODE;
        this.isWritten = false;
    }
    
    /**
     * Specifies <code>PdfReader</code> object to be used with this
     * <code>PdfDocument</code>.
     * 
     * @param r
     *            <code>PdfReader</code> object to be used with this
     *            <code>PdfDocument</code>
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see #getReader()
     * @see #getWriter()
     * @see #setWriter(PdfWriter)
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#setReader">example</a>.
     */
    public synchronized void setReader(PdfReader r)
        throws IOException, PdfException
    {
        if (r == null)
        {
            throw new PdfException(
                "Illegal argument to setReader()" +
                " [PdfReader == null]");
        }
        if (mode == WRITING_MODE)
        {
            throw new PdfException(
                "Document already opened in writing mode");
        }
        
        this.reader = r;
        this.mode = READING_MODE;
        this.isWritten = false;

        bytesWritten = 0;
        objectRun = 1;
        catalog = null;
        crossRefTable = null;
        procSet = null;
        fontMap = null;
        xObjMap = null;
        pageTree = null;
        bookmarkTree = null;
        actionList = null;
        additionalList = null;
        namedDestinations = null;
        outlineStream = null;
        fontStream = null;
        pageModeValue = PdfPageMode.USENONE;
        pageLayoutValue = PdfPageLayout.ONE_COLUMN;
        random = new Random();
        openAfterSave = false;
        encryptDocument  = false;
        measurementUnit = PdfMeasurement.MU_POINTS;
        compressionLevel = PdfFlateFilter.BEST_COMPRESSION;
        autoPaginate = true;
        currentPage = 1;
        this.unknownObjOffset = null;
        this.unknownObjMaps = null;
        this.writtenObjs = null;
        this.objMaps = new Hashtable();
        if (this.writer != null)
        {
            this.writer.writtenObjs = null;
        }

        read();
    }
    
    /**
     * Returns <code>PdfReader</code> object currently used with
     * this <code>PdfDocument</code>.
     *  
     * @return <code>PdfReader</code> object currently used with the
     *         <code>PdfDocument</code>; <code>null</code> if the
     *         <code>PdfDocument</code> is used with a 
     *         <code>PdfWriter</code> object 
     * @since 1.0
     * @see #setReader(PdfReader)
     * @see #getReader()
     * @see #setReader(PdfReader)
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#getReader">example</a>.
     */
    public synchronized PdfReader getReader()
    {
        return reader;
    }
    
    /**
     * Returns <code>PdfWriter</code> object currently used with
     * this <code>PdfDocument</code>.
     * 
     * @return <code>PdfWriter</code> object currently used with the
     *         <code>PdfDocument</code>; <code>null</code> if the
     *         <code>PdfDocument</code> is used with a 
     *         <code>PdfReader</code> object  
     * @since 1.0
     * @see #setWriter(PdfWriter)
     * @see #getReader()
     * @see #setReader(PdfReader)
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#getWriter">example</a>.
     */
    public synchronized PdfWriter getWriter()
    {
        return writer;
    }
    
    public synchronized void appendPagesFrom(PdfDocument d,
        String pageRange) throws IOException, PdfException
    {
        if (pageTree == null)
        {
            pageTree = new PdfPageTree(this);
        }
        insertPagesFrom(d, pageRange, pageTree.getCount());
    }
    
    /*
     * Extracts pages in specified page range of source document and
     * then appends them to this <code>PdfDocument</code>.
     * 
     * @param path
     *            pathname of the source document from which pages are
     *            to be extracted
     * @param pageRange
     *            page range in the source document whose pages are to
     *            be extracted
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#appendPagesFrom_String_String">example</a>.
     */
    public synchronized void appendPagesFrom(String path,
        String pageRange) throws IOException, PdfException
    {
        if (mode != READING_MODE)
        {
            return;
        }
        if(pageTree == null)
        {
            pageTree = new PdfPageTree(this);
        }
        PdfReader r = PdfReader.fileReader(path, 0); 
        insertPagesFrom(new PdfStdDocument(r), pageRange, pageTree
            .getCount());
        //r.dispose();
    }

	/**
     * Writes specified text with specified font at position 
     * (<code>x</code>, <code>y</code>) on pages in 
     * specified page range.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param x
     *            x-coordinate of the position where the text needs 
     *            to be written
     * @param y
     *            y-coordinate of the position where the text needs 
     *            to be written
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfFont_double_double_String">example</a>.
     */
	public void writeText(String str, PdfFont f, double x, double y,
        String pageRange) throws PdfException, IOException
	{
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }
	    if (f == null)
		{
            throw new PdfException("Illegal argument, font is null");
		}
	    PdfPage p;
	    	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for(Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        if (pageTree == null)
            {
                throw new PdfException("page number out of range.");
            }
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        double tempY = y;
	        
            if (tempY + PdfMeasurement.convertToMeasurementUnit(
               measurementUnit, f.getHeight()) >= PdfMeasurement
               .convertToMeasurementUnit(measurementUnit, p.pageHeight
               - (p.pageTopMargin + p.pageCropTop + p.pageHeaderHeight 
               + p.pageBottomMargin  + p.pageFooterHeight + p.pageCropBottom)))
            {
                for (int m = ii; m < siz; m++)
                {
                    pageNos[m] = pageNos[m] + 1;
                }
                p = new PdfPage(p.pageWidth, p.pageHeight,
                    p.pageHeaderHeight, p.pageFooterHeight,
                    p.pageLeftMargin, p.pageTopMargin,
                    p.pageRightMargin, p.pageBottomMargin,
                    PdfMeasurement.MU_POINTS);
                tempY = 0;
                this.pageTree.insert(pageNo + 1, p);
                currentPage = pageNo + 1;
                pageNo++;
            }

	        PdfRect r = new PdfRect(x, tempY, p.pageWidth, p.pageHeight);
	        r = p.updatePageSettings(r, measurementUnit);
	        
	        tf = p.getTextFormatter();
	        
	        if (autoPaginate == true)
            {
                String tempStr = str;
                int n = 1;
                boolean isFirstTime = true;
                while (tempStr != "")
                {
                    p.writeText(tempStr, r, f, tf, measurementUnit, true);
                    tempStr = p.wrapText(tempStr, r, 0, f, measurementUnit);
                    if (isFirstTime)
                    {
                        r = new PdfRect(x, 0, p.pageWidth, p.pageHeight);
                        r = p.updatePageSettings(r, measurementUnit);
                    }
                    isFirstTime = false;
                    if (tempStr != "")
                    {
                        for(int m = ii; m < siz; m++)
                        {
                            pageNos[m] = pageNos[m] + 1;
                        }
                        n++;
                        p = new PdfPage(p.pageWidth, p.pageHeight,
                            p.pageHeaderHeight, p.pageFooterHeight,
                            p.pageLeftMargin, p.pageTopMargin,
                            p.pageRightMargin, p.pageBottomMargin,
                            PdfMeasurement.MU_POINTS);

                        this.pageTree.insert(pageNo + 1, p);
                        currentPage = pageNo + 1;
                        pageNo++;
                    }
                }
	        }
	        else
	        {
	            p.writeText(str, r, f, tf, measurementUnit, true);
	        }
	    }
	}

	/**
     * Writes specified text with specified font at position 
     * (<code>x</code>, <code>y</code>) on this 
     * <code>PdfDocument</code>'s current page.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param x
     *            x-coordinate of the position where the text needs
     *            to be written
     * @param y
     *            y-coordinate of the position where the text needs 
     *            to be written
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @throws IOException
     *             if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfFont_double_double">example</a>.
     */
	public void writeText(String str, PdfFont f, double x, double y)
        throws PdfException, IOException
	{
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, f, x, y, Integer.toString(currentPage));
	}
	
	/**
     * Writes specified text with specified font at specified point on
     * pages in specified page range.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param pt
     *            <code>PdfPoint</code> where the text needs to be
     *            written
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @throws IOException
     *             if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfFont_PdfPoint_String">example</a>.
     */
	public void writeText(String str, PdfFont f, PdfPoint pt,
        String pageRange) throws PdfException, IOException
	{
	    writeText(str, f, pt.x, pt.y, pageRange);
	}
	
	/**
     * Writes specified text with specified font at specified point on
     * this <code>PdfDocument</code>'s current page.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param pt
     *            <code>PdfPoint</code> where the text needs to be
     *            written
     * @throws PdfException
     *            if an I/O error occurs.
     * @throws IOException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfFont_PdfPoint">example</a>.
     */
	public void writeText(String str, PdfFont f, PdfPoint pt)
        throws PdfException, IOException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, f, pt, Integer.toString(currentPage));
    }
	
    /**
     * Writes specified text with specified font and alignment at
     * position (<code>x</code>, <code>y</code>) on pages in
     * specified page range.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned
     * @param x
     *            x-coordinate of the position where the text needs 
     *            to be written
     * @param y
     *            y-coordinate of the position where the text needs 
     *            to be written
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfFont_int_double_double_String">example</a>.
     */
    public void writeText(String str, PdfFont f, int alignment,
        double x, double y, String pageRange) throws IOException,
        PdfException
    {
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }
	    if (f == null)
		{
            throw new PdfException("Illegal argument, font is null");
		}
	    PdfPage p;
	    	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for(Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        if (pageTree == null)
            {
                throw new PdfException("page number out of range.");
            }
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        double tempY = y;
	        
            if (tempY + PdfMeasurement.convertToMeasurementUnit(
               measurementUnit, f.getHeight()) >= PdfMeasurement
               .convertToMeasurementUnit(measurementUnit, p.pageHeight
               - (p.pageTopMargin + p.pageCropTop + p.pageHeaderHeight 
               + p.pageBottomMargin  + p.pageFooterHeight + p.pageCropBottom)))
            {
                for (int m = ii; m < siz; m++)
                {
                    pageNos[m] = pageNos[m] + 1;
                }
                p = new PdfPage(p.pageWidth, p.pageHeight,
                    p.pageHeaderHeight, p.pageFooterHeight,
                    p.pageLeftMargin, p.pageTopMargin,
                    p.pageRightMargin, p.pageBottomMargin,
                    PdfMeasurement.MU_POINTS);
                tempY = 0;
                this.pageTree.insert(pageNo + 1, p);
                currentPage = pageNo + 1;
                pageNo++;
            }

	        PdfRect r = new PdfRect(0, tempY, p.pageWidth, p.pageHeight);
	        r = p.updatePageSettings(r, measurementUnit);
	        
	        tf = (PdfTextFormatter)p.getTextFormatter().clone();
	        tf.setAlignment(alignment);
            tf.setFirstLinePosition(x);
	        
	        if (autoPaginate == true)
            {
                String tempStr = str;
                int n = 1;
                boolean isFirstTime = true;
                while (tempStr != "")
                {
                    p.writeText(tempStr, r, f, tf, measurementUnit, true);
                    tempStr = p.wrapText(tempStr, r, 0, f, measurementUnit);
                    if (isFirstTime)
                    {
                        r = new PdfRect(x, 0, p.pageWidth, p.pageHeight);
                        r = p.updatePageSettings(r, measurementUnit);
                    }
                    isFirstTime = false;
                    if (tempStr != "")
                    {
                        for(int m = ii; m < siz; m++)
                        {
                            pageNos[m] = pageNos[m] + 1;
                        }
                        n++;
                        p = new PdfPage(p.pageWidth, p.pageHeight,
                            p.pageHeaderHeight, p.pageFooterHeight,
                            p.pageLeftMargin, p.pageTopMargin,
                            p.pageRightMargin, p.pageBottomMargin,
                            PdfMeasurement.MU_POINTS);

                        this.pageTree.insert(pageNo + 1, p);
                        currentPage = pageNo + 1;
                        pageNo++;
                    }
                }
	        }
	        else
	        {
	            p.writeText(str, r, f, tf, measurementUnit, true);
	        }
	    }
    }

    /**
     * Writes specified text with specified font and alignment at
     * position (<code>x</code>, <code>y</code>) on this
     * <code>PdfDocument</code>'s current page.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned
     * @param x
     *            x-coordinate of the position where the text needs 
     *            to be written
     * @param y
     *            y-coordinate of the position where the text needs 
     *            to be written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter 
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfFont_int_double_double">example</a>.
     */
    public void writeText(String str, PdfFont f, int alignment,
        double x, double y) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, f, alignment, x, y, Integer.toString(currentPage));
    }
    
    /**
     * Writes specified text with specified font and alignment at
     * specified point on pages in specified page range.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned
     * @param pt
     *            <code>PdfPoint</code> where the text needs to be
     *            written
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfFont_int_PdfPoint_String">example</a>.
     */
    public void writeText(String str, PdfFont f, int alignment,
        PdfPoint pt, String pageRange) throws IOException, PdfException
    {
        writeText(str, f, alignment, pt.x, pt.y, pageRange);
    }
    
    /**
     * Writes specified text with specified font and alignment at
     * specified point on this <code>PdfDocument</code>'s current
     * page.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned
     * @param pt
     *            <code>PdfPoint</code> where the text needs to be
     *            written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfFont_int_PdfPoint">example</a>.
     */
    public void writeText(String str, PdfFont f, int alignment,
        PdfPoint pt) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, f, alignment, pt.x, pt.y, Integer.toString(currentPage));
    }
	
    /**
     * Writes specified text with specified font and wrap setting at
     * position (<code>x</code>, <code>y</code>) on pages in
     * specified page range.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param x
     *            x-coordinate of the position where the text needs 
     *            to be written
     * @param y
     *            y-coordinate of the position where the text needs 
     *            to be written
     * @param wrap
     *            constant specifying whether the text needs to be 
     *            wrapped
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter 
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfFont_double_double_boolean_String">example</a>.
     */
    public void writeText(String str, PdfFont f, double x, double y,
        boolean wrap, String pageRange) throws IOException, PdfException
    {
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }
	    if (f == null)
		{
            throw new PdfException("Illegal argument, font is null");
		}
	    PdfPage p;
	    	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for(Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        if (pageTree == null)
            {
                throw new PdfException("page number out of range.");
            }
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        double tempY = y;
	        
            if (tempY + PdfMeasurement.convertToMeasurementUnit(
               measurementUnit, f.getHeight()) >= PdfMeasurement
               .convertToMeasurementUnit(measurementUnit, p.pageHeight
               - (p.pageTopMargin + p.pageCropTop + p.pageHeaderHeight 
               + p.pageBottomMargin  + p.pageFooterHeight + p.pageCropBottom)))
            {
                for (int m = ii; m < siz; m++)
                {
                    pageNos[m] = pageNos[m] + 1;
                }
                p = new PdfPage(p.pageWidth, p.pageHeight,
                    p.pageHeaderHeight, p.pageFooterHeight,
                    p.pageLeftMargin, p.pageTopMargin,
                    p.pageRightMargin, p.pageBottomMargin,
                    PdfMeasurement.MU_POINTS);
                tempY = 0;
                this.pageTree.insert(pageNo + 1, p);
                currentPage = pageNo + 1;
                pageNo++;
            }

	        PdfRect r = new PdfRect(x, tempY, p.pageWidth, p.pageHeight);
	        r = p.updatePageSettings(r, measurementUnit);
	        
	        tf = (PdfTextFormatter)p.getTextFormatter().clone();
	        tf.setWrap(wrap);
	        
	        if (autoPaginate == true)
            {
                String tempStr = str;
                int n = 1;
                boolean isFirstTime = true;
                while (tempStr != "")
                {
                    p.writeText(tempStr, r, f, tf, measurementUnit, true);
                    tempStr = p.wrapText(tempStr, r, 0, f, measurementUnit);
                    if (isFirstTime)
                    {
                        r = new PdfRect(x, 0, p.pageWidth, p.pageHeight);
                        r = p.updatePageSettings(r, measurementUnit);
                    }
                    isFirstTime = false;
                    if (tempStr != "")
                    {
                        for(int m = ii; m < siz; m++)
                        {
                            pageNos[m] = pageNos[m] + 1;
                        }
                        n++;
                        p = new PdfPage(p.pageWidth, p.pageHeight,
                            p.pageHeaderHeight, p.pageFooterHeight,
                            p.pageLeftMargin, p.pageTopMargin,
                            p.pageRightMargin, p.pageBottomMargin,
                            PdfMeasurement.MU_POINTS);

                        this.pageTree.insert(pageNo + 1, p);
                        currentPage = pageNo + 1;
                        pageNo++;
                    }
                }
	        }
	        else
	        {
	            p.writeText(str, r, f, tf, measurementUnit, true);
	        }
	    }
    }
    
    /**
     * Writes specified text with specified font and wrap setting at
     * position (<code>x</code>, <code>y</code>) on this
     * <code>PdfDocument</code>'s current page.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param x
     *            x-coordinate of the position where the text needs 
     *            to be written
     * @param y
     *            y-coordinate of the position where the text needs 
     *            to be written
     * @param wrap
     *            constant specifying whether the text needs to be 
     *            wrapped 
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfFont_double_double_boolean">example</a>.
     */
    public void writeText(String str, PdfFont f, double x, double y,
        boolean wrap) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, f, x, y, wrap, Integer.toString(currentPage));
    }
    
    /**
     * Writes text <code>str</code> with specified font and wrap
     * setting at specified point on pages in specified page range.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param pt
     *            <code>PdfPoint</code> where the text needs to be
     *            written
     * @param wrap
     *            constant specifying whether the text needs to be
     *            wrapped
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfFont_PdfPoint_boolean_String">example</a>.
     */
    public void writeText(String str, PdfFont f, PdfPoint pt,
        boolean wrap, String pageRange) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, f, pt.x, pt.y, wrap, pageRange);
    }

    /**
     * Writes text <code>str</code> with specified font and wrap
     * setting at specified point on this <code>PdfDocument</code>'s
     * current page.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param pt
     *            <code>PdfPoint</code> where the text needs to be
     *            written
     * @param wrap
     *            constant specifying whether the text needs to be
     *            wrapped
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfFont_PdfPoint_boolean">example</a>.
     */
    public void writeText(String str, PdfFont f, PdfPoint pt,
        boolean wrap) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, f, pt.x, pt.y, wrap, Integer.toString(currentPage));
    }

    /**
     * Writes specified text with specified font, alignment, and
     * wrapping at position (<code>x</code>, <code>y</code>) on
     * pages in specified page range.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned
     * @param x
     *            x-coordinate of the position where the text needs 
     *            to be written
     * @param y
     *            y-coordinate of the position where the text needs 
     *            to be written
     * @param wrap
     *            constant specifying whether the text needs to be 
     *            wrapped
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfFont_int_double_double_boolean_String">example</a>.
     */
    public void writeText(String str, PdfFont f, int alignment,
        double x, double y, boolean wrap, String pageRange) throws IOException,
        PdfException
    {
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }
	    if (f == null)
		{
            throw new PdfException("Illegal argument, font is null");
		}
	    PdfPage p;
	    	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for(Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        if (pageTree == null)
            {
                throw new PdfException("page number out of range.");
            }
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        double tempY = y;
	        
            if (tempY + PdfMeasurement.convertToMeasurementUnit(
               measurementUnit, f.getHeight()) >= PdfMeasurement
               .convertToMeasurementUnit(measurementUnit, p.pageHeight
               - (p.pageTopMargin + p.pageCropTop + p.pageHeaderHeight 
               + p.pageBottomMargin  + p.pageFooterHeight + p.pageCropBottom)))
            {
                for (int m = ii; m < siz; m++)
                {
                    pageNos[m] = pageNos[m] + 1;
                }
                p = new PdfPage(p.pageWidth, p.pageHeight,
                    p.pageHeaderHeight, p.pageFooterHeight,
                    p.pageLeftMargin, p.pageTopMargin,
                    p.pageRightMargin, p.pageBottomMargin,
                    PdfMeasurement.MU_POINTS);
                tempY = 0;
                this.pageTree.insert(pageNo + 1, p);
                currentPage = pageNo + 1;
                pageNo++;
            }

	        PdfRect r = new PdfRect(x, tempY, p.pageWidth, p.pageHeight);
	        r = p.updatePageSettings(r, measurementUnit);
	        
	        tf = (PdfTextFormatter)p.getTextFormatter().clone();
	        tf.setWrap(wrap);
	        tf.setAlignment(alignment);
	        
	        if (autoPaginate == true)
            {
                String tempStr = str;
                int n = 1;
                boolean isFirstTime = true;
                while (tempStr != "")
                {
                    p.writeText(tempStr, r, f, tf, measurementUnit, true);
                    tempStr = p.wrapText(tempStr, r, 0, f, measurementUnit);
                    if (isFirstTime)
                    {
                        r = new PdfRect(x, 0, p.pageWidth, p.pageHeight);
                        r = p.updatePageSettings(r, measurementUnit);
                    }
                    isFirstTime = false;
                    if (tempStr != "")
                    {
                        for(int m = ii; m < siz; m++)
                        {
                            pageNos[m] = pageNos[m] + 1;
                        }
                        n++;
                        p = new PdfPage(p.pageWidth, p.pageHeight,
                            p.pageHeaderHeight, p.pageFooterHeight,
                            p.pageLeftMargin, p.pageTopMargin,
                            p.pageRightMargin, p.pageBottomMargin,
                            PdfMeasurement.MU_POINTS);

                        this.pageTree.insert(pageNo + 1, p);
                        currentPage = pageNo + 1;
                        pageNo++;
                    }
                }
	        }
	        else
	        {
	            p.writeText(str, r, f, tf, measurementUnit, true);
	        }
	    }       
    }
    
    /**
     * Writes specified text with specified font, alignment, and
     * wrapping at position (<code>x</code>, <code>y</code>) on
     * this <code>PdfDocument</code>'s current page.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned
     * @param x
     *            x-coordinate of the position where the text needs 
     *            to be written
     * @param y
     *            y-coordinate of the position where the text needs
     *            to be written
     * @param wrap
     *            constant specifying whether the text needs to be 
     *            wrapped
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfFont_int_double_double_boolean">example</a>.
     */
    public void writeText(String str, PdfFont f, int alignment,
        double x, double y, boolean wrap) throws IOException,
        PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, f, alignment, x, y, wrap, Integer.toString(currentPage));
    }

    /**
     * Writes specified text with specified font, alignment, and wrap
     * setting at specified point on pages in specified page range.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned
     * @param pt
     *            <code>PdfPoint</code> where the text needs to be
     *            written
     * @param wrap
     *            constant specifying whether the text needs to be 
     *            wrapped
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfFont_int_PdfPoint_boolean_String">example</a>.
     */
    public void writeText(String str, PdfFont f, int alignment,
        PdfPoint pt, boolean wrap, String pageRange) throws IOException,
        PdfException
    {
        writeText(str, f, alignment, pt.x, pt.y, wrap, pageRange);
    }
    
    /**
     * Writes specified text with specified font, alignment, and wrap
     * setting at specified point on this <code>PdfDocument</code>'s
     * current page.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned
     * @param pt
     *            <code>PdfPoint</code> where the text needs to be
     *            written
     * @param wrap
     *            constant specifying whether the text needs to be 
     *            wrapped
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an I/O error occurs.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfFont_int_PdfPoint_boolean">example</a>.
     */
    public void writeText(String str, PdfFont f, int alignment,
        PdfPoint pt, boolean wrap) throws IOException,
        PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, f, alignment, pt.x, pt.y, wrap, Integer.toString(currentPage));
    }

    /**
     * Writes specified text rotated by <code>rotation</code>
     * degrees with specified font at position (<code>x</code>,
     * <code>y</code>) on pages in specified page range.
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param x
     *            x-coordinate of the position where the text needs 
     *            to be written
     * @param y
     *            y-coordinate of the position where the text needs
     *            to be written
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfFont_double_double_double_String">example</a>.
     */
    public void writeText(String str, PdfFont f, double x, double y,
        double rotation, String pageRange) throws IOException,
        PdfException
    {
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }
	    if (f == null)
		{
            throw new PdfException("Illegal argument, font is null");
		}
	    PdfPage p;
	    	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for(Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        if (pageTree == null)
            {
                throw new PdfException("page number out of range.");
            }
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        double tempY = y;
	        
            if (tempY + PdfMeasurement.convertToMeasurementUnit(
               measurementUnit, f.getHeight()) >= PdfMeasurement
               .convertToMeasurementUnit(measurementUnit, p.pageHeight
               - (p.pageTopMargin + p.pageCropTop + p.pageHeaderHeight 
               + p.pageBottomMargin  + p.pageFooterHeight + p.pageCropBottom)))
            {
                for (int m = ii; m < siz; m++)
                {
                    pageNos[m] = pageNos[m] + 1;
                }
                p = new PdfPage(p.pageWidth, p.pageHeight,
                    p.pageHeaderHeight, p.pageFooterHeight,
                    p.pageLeftMargin, p.pageTopMargin,
                    p.pageRightMargin, p.pageBottomMargin,
                    PdfMeasurement.MU_POINTS);
                tempY = 0;
                this.pageTree.insert(pageNo + 1, p);
                currentPage = pageNo + 1;
                pageNo++;
            }

            double width = f.getWidth(str, PdfMeasurement.MU_POINTS);
            double height = f.getHeight();
            PdfRect r = new PdfRect(x, tempY, width, height);

	        r = p.updatePageSettings(r, measurementUnit);
	        
	        tf = (PdfTextFormatter)p.getTextFormatter().clone();
	        tf.setRotation(rotation);
	        
	        p.writeText(str, r, f, tf, measurementUnit, true);
	    }       
    }

    /**
     * Writes specified text rotated by <code>rotation</code>
     * degrees with specified font at position (<code>x</code>,
     * <code>y</code>) on this <code>PdfDocument</code>'s
     * current page.
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param x
     *            x-coordinate of the position where the text needs 
     *            to be written
     * @param y
     *            y-coordinate of the position where the text needs 
     *            to be written
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfFont_double_double_double">example</a>.
     */
    public void writeText(String str, PdfFont f, double x, double y,
        double rotation) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, f, x, y, rotation, Integer.toString(currentPage));
    }
    
    /**
     * Writes text <code>str</code> rotated by <code>rotation</code>
     * degrees with specified font at specified point on pages in
     * specified page range.
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            text that needs to be written
     * @param pt
     *            <code>PdfPoint</code> where the text needs to be
     *            written
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfFont_PdfPoint_double_String">example</a>.
     */
    public void writeText(String str, PdfFont f, PdfPoint pt,
        double rotation, String pageRange) throws IOException, PdfException
    {
        writeText(str, f, pt.x, pt.y, rotation, pageRange);
    }

    /**
     * Writes text <code>str</code> rotated by <code>rotation</code>
     * degrees with specified font at specified point on this
     * <code>PdfDocument</code>'s current page.
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param pt
     *            <code>PdfPoint</code> where the text needs to be 
     *            written
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfFont_PdfPoint_double">example</a>.
     */
    public void writeText(String str, PdfFont f, PdfPoint pt,
        double rotation) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, f, pt.x, pt.y, rotation, Integer.toString(currentPage));
    }

	/**
     * Writes specified text at position 
     * (<code>x</code>, <code>y</code>) 
     * on pages in specified page range.
     * 
     * @param str
     *            text that needs to be written
     * @param x
     *            x-coordinate of the position where the text needs to
     *            be written
     * @param y
     *            y-coordinate of the position where the text needs to
     *            be written
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws PdfException
     *            if an illegal argument is supplied. 
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_double_double_String">example</a>.
     */
	public void writeText(String str, double x, double y,
        String pageRange) throws PdfException, IOException
	{
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }

	    PdfPage p;
	    PdfFont f;
	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for(Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        if (pageTree == null)
            {
                throw new PdfException("page number out of range.");
            }
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        f = p.prevFont;
	        if (f == null)
			{
			    f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
			}
	        double tempY = y;
	        
            if (tempY + PdfMeasurement.convertToMeasurementUnit(
               measurementUnit, f.getHeight()) >= PdfMeasurement
               .convertToMeasurementUnit(measurementUnit, p.pageHeight
               - (p.pageTopMargin + p.pageCropTop + p.pageHeaderHeight 
                   + p.pageBottomMargin  + p.pageFooterHeight + p.pageCropBottom)))
            {
                for (int m = ii; m < siz; m++)
                {
                    pageNos[m] = pageNos[m] + 1;
                }
                p = new PdfPage(p.pageWidth, p.pageHeight,
                    p.pageHeaderHeight, p.pageFooterHeight,
                    p.pageLeftMargin, p.pageTopMargin,
                    p.pageRightMargin, p.pageBottomMargin,
                    PdfMeasurement.MU_POINTS);
                tempY = 0;
                this.pageTree.insert(pageNo + 1, p);
                currentPage = pageNo + 1;
                pageNo++;
            }

	        PdfRect r = new PdfRect(x, tempY, p.pageWidth, p.pageHeight);
	        r = p.updatePageSettings(r, measurementUnit);
	        
	        tf = p.getTextFormatter();
	        //tf.setFirstLinePosition(x);
	        
	        if (autoPaginate == true)
            {
                String tempStr = str;
                int n = 1;
                boolean isFirstTime = true;
                while (tempStr != "")
                {
                    p.writeText(tempStr, r, f, tf, measurementUnit, true);
                    tempStr = p.wrapText(tempStr, r, 0, f, measurementUnit);
                    if (isFirstTime)
                    {
                        r = new PdfRect(x, 0, p.pageWidth, p.pageHeight);
                        r = p.updatePageSettings(r, measurementUnit);
                    }
                    isFirstTime = false;
                    if (tempStr != "")
                    {
                        for(int m = ii; m < siz; m++)
                        {
                            pageNos[m] = pageNos[m] + 1;
                        }
                        n++;
                        p = new PdfPage(p.pageWidth, p.pageHeight,
                            p.pageHeaderHeight, p.pageFooterHeight,
                            p.pageLeftMargin, p.pageTopMargin,
                            p.pageRightMargin, p.pageBottomMargin,
                            PdfMeasurement.MU_POINTS);

                        this.pageTree.insert(pageNo + 1, p);
                        currentPage = pageNo + 1;
                        pageNo++;
                    }
                }
	        }
	        else
	        {
	            p.writeText(str, r, f, tf, measurementUnit, true);
	        }
	    }
	}
	
	/**
     * Writes specified text at position (<code>x</code>,
     * <code>y</code>) on this <code>PdfDocument</code>'s
     * current page.
     * 
     * @param str
     *            text that needs to be written
     * @param x
     *            x-coordinate of the position where the text needs to
     *            be written
     * @param y
     *            y-coordinate of the position where the text needs to
     *            be written
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_double_double">example</a>.
     */
	public void writeText(String str, double x, double y)
		throws PdfException, IOException
	{
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, x, y,Integer.toString(currentPage));
	}
	
	/**
     * Writes specified text at specified point on pages in specified
     * page range.
     * 
     * @param str
     *            text that needs to be written
     * @param pt
     *            <code>PdfPoint</code> where the text needs to be
     *            written
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfPoint_String">example</a>.
     */
	public void writeText(String str, PdfPoint pt, String pageRange)
        throws PdfException, IOException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, pt.x, pt.y, pageRange);
    }

    /**
     * Writes specified text at specified point on this
     * <code>PdfDocument</code>'s current page.
     * 
     * @param str
     *            text that needs to be written
     * @param pt
     *            <code>PdfPoint</code> where the text needs to be
     *            written
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfPoint">example</a>.
     */
    public void writeText(String str, PdfPoint pt)
        throws PdfException, IOException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, pt.x, pt.y, Integer.toString(currentPage));
    }
	
	/**
     * Writes specified text with specified alignment at 
     * position (<code>x</code>, <code>y</code>) on pages in 
     * specified range.
     * 
     * @param str
     *            text that needs to be written
     * @param x
     *            x-coordinate of the position where the text needs to
     *            be written
     * @param y
     *            y-coordinate of the position where the text needs to
     *            be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_double_double_int_String">example</a>.
     */
	public void writeText(String str, double x, double y, int alignment,
        String pageRange) throws PdfException, IOException
	{
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }

	    PdfPage p;
	    PdfFont f;
	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for(Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        if (pageTree == null)
            {
                throw new PdfException("page number out of range.");
            }
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        f = p.prevFont;
	        if (f == null)
			{
			    f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
			}
	        double tempY = y;
	        
            if (tempY + PdfMeasurement.convertToMeasurementUnit(
               measurementUnit, f.getHeight()) >= PdfMeasurement
               .convertToMeasurementUnit(measurementUnit, p.pageHeight
               - (p.pageTopMargin + p.pageCropTop + p.pageHeaderHeight 
                   + p.pageBottomMargin  + p.pageFooterHeight + p.pageCropBottom)))
            {
                for (int m = ii; m < siz; m++)
                {
                    pageNos[m] = pageNos[m] + 1;
                }
                p = new PdfPage(p.pageWidth, p.pageHeight,
                    p.pageHeaderHeight, p.pageFooterHeight,
                    p.pageLeftMargin, p.pageTopMargin,
                    p.pageRightMargin, p.pageBottomMargin,
                    PdfMeasurement.MU_POINTS);
                tempY = 0;
                this.pageTree.insert(pageNo + 1, p);
                currentPage = pageNo + 1;
                pageNo++;
            }

	        PdfRect r = new PdfRect(x, tempY, p.pageWidth, p.pageHeight);
	        r = p.updatePageSettings(r, measurementUnit);
	        
	        tf = (PdfTextFormatter) p.getTextFormatter().clone();
	        //tf.setFirstLinePosition(0);
	        tf.setAlignment(alignment);
	        
	        if (autoPaginate == true)
            {
                String tempStr = str;
                int n = 1;
                boolean isFirstTime = true;
                while (tempStr != "")
                {
                    p.writeText(tempStr, r, f, tf, measurementUnit, true);
                    tempStr = p.wrapText(tempStr, r, 0, f, measurementUnit);
                    if (isFirstTime)
                    {
                        r = new PdfRect(x, 0, p.pageWidth, p.pageHeight);
                        r = p.updatePageSettings(r, measurementUnit);
                    }
                    isFirstTime = false;
                    if (tempStr != "")
                    {
                        for(int m = ii; m < siz; m++)
                        {
                            pageNos[m] = pageNos[m] + 1;
                        }
                        n++;
                        p = new PdfPage(p.pageWidth, p.pageHeight,
                            p.pageHeaderHeight, p.pageFooterHeight,
                            p.pageLeftMargin, p.pageTopMargin,
                            p.pageRightMargin, p.pageBottomMargin,
                            PdfMeasurement.MU_POINTS);

                        this.pageTree.insert(pageNo + 1, p);
                        currentPage = pageNo + 1;
                        pageNo++;
                    }
                }
	        }
	        else
	        {
	            p.writeText(str, r, f, tf, measurementUnit, true);
	        }
	    }
	}

	/**
     * Writes specified text with specified alignment at
     * position (<code>x</code>, <code>y</code>) on this
     * <code>PdfDocument</code>'s current page.
     * 
     * @param str
     *            text that needs to be written
     * @param x
     *            x-coordinate of the position where the text needs to
     *            be written
     * @param y
     *            y-coordinate of the position where the text needs to
     *            be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_double_double_int">example</a>.
     */
	public void writeText(String str, double x, double y,
        int alignment) throws PdfException, IOException
	{
	    if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, x, y, alignment, Integer.toString(currentPage));
	}
	
	public void writeText(String str, Point pt, int alignment,
        String pageRange) throws PdfException, IOException
	{
	    writeText(str, pt.x, pt.y, alignment, pageRange);
	}
	
	public void writeText(String str, Point pt, int alignment)
        throws PdfException, IOException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, pt.x, pt.y, alignment, Integer.toString(currentPage));
    }
	
	
	/**
     * Writes specified text with specified wrap setting at
     * position (<code>x</code>, <code>y</code>) on pages in
     * specified page range.
     * 
     * @param str
     *            text that needs to be written
     * @param x
     *            x-coordinate of the position where the text needs  
     *            to be written
     * @param y
     *            y-coordinate of the position where the text needs 
     *            to be written
     * @param wrap
     *            constant specifying whether the text needs to be 
     *            wrapped            
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_double_double_boolean_String">example</a>.
     */
	public void writeText(String str, double x, double y, boolean wrap,
        String pageRange) throws PdfException, IOException
	{
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }

	    PdfPage p;
	    PdfFont f;
	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for(Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        if (pageTree == null)
            {
                throw new PdfException("page number out of range.");
            }
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        f = p.prevFont;
	        if (f == null)
			{
			    f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
			}
	        double tempY = y;
	        
            if (tempY + PdfMeasurement.convertToMeasurementUnit(
               measurementUnit, f.getHeight()) >= PdfMeasurement
               .convertToMeasurementUnit(measurementUnit, p.pageHeight
               - (p.pageTopMargin + p.pageCropTop + p.pageHeaderHeight 
                   + p.pageBottomMargin  + p.pageFooterHeight + p.pageCropBottom)))
            {
                for (int m = ii; m < siz; m++)
                {
                    pageNos[m] = pageNos[m] + 1;
                }
                p = new PdfPage(p.pageWidth, p.pageHeight,
                    p.pageHeaderHeight, p.pageFooterHeight,
                    p.pageLeftMargin, p.pageTopMargin,
                    p.pageRightMargin, p.pageBottomMargin,
                    PdfMeasurement.MU_POINTS);
                tempY = 0;
                this.pageTree.insert(pageNo + 1, p);
                currentPage = pageNo + 1;
                pageNo++;
            }

	        PdfRect r = new PdfRect(x, tempY, p.pageWidth, p.pageHeight);
	        r = p.updatePageSettings(r, measurementUnit);
	        
	        tf = (PdfTextFormatter) p.getTextFormatter().clone();
	        //tf.setFirstLinePosition(0);
	        tf.setWrap(wrap);
	        
	        if (autoPaginate == true)
            {
                String tempStr = str;
                int n = 1;
                boolean isFirstTime = true;
                while (tempStr != "")
                {
                    p.writeText(tempStr, r, f, tf, measurementUnit, true);
                    tempStr = p.wrapText(tempStr, r, 0, f, measurementUnit);
                    if (isFirstTime)
                    {
                        r = new PdfRect(x, 0, p.pageWidth, p.pageHeight);
                        r = p.updatePageSettings(r, measurementUnit);
                    }
                    isFirstTime = false;
                    if (tempStr != "")
                    {
                        for(int m = ii; m < siz; m++)
                        {
                            pageNos[m] = pageNos[m] + 1;
                        }
                        n++;
                        p = new PdfPage(p.pageWidth, p.pageHeight,
                            p.pageHeaderHeight, p.pageFooterHeight,
                            p.pageLeftMargin, p.pageTopMargin,
                            p.pageRightMargin, p.pageBottomMargin,
                            PdfMeasurement.MU_POINTS);

                        this.pageTree.insert(pageNo + 1, p);
                        currentPage = pageNo + 1;
                        pageNo++;
                    }
                }
	        }
	        else
	        {
	            p.writeText(str, r, f, tf, measurementUnit, true);
	        }
	    }
	}

	/**
     * Writes specified text with specified wrap setting at position 
     * (<code>x</code>, <code>y</code>) on this 
     * <code>PdfDocument</code>'s current page.
     * 
     * @param str
     *            text that needs to be written
     * @param x
     *            x-coordinate of the position where the text needs
     * @param y
     *            y-coordinate of the position where the text needs to
     *            be written
     * @param wrap
     *            constant specifying whether the text needs to be
     *            wrapped
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_double_double_boolean">example</a>.
     */
	public void writeText(String str, double x, double y, boolean wrap)
        throws PdfException, IOException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, x, y, wrap, Integer.toString(currentPage));
    }
	
	/**
     * Writes text <code>str</code> with specified wrap setting at
     * point <code>pt</code> on pages in specified page range.
     * 
     * @param str
     *            text that needs to be written
     * @param pt
     *            <code>PdfPoint</code> where the text needs to be
     *            written
     * @param wrap
     *            constant specifying whether the text needs to be
     *            wrapped
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfPoint_boolean_String">example</a>.
     */
	public void writeText(String str, PdfPoint pt, boolean wrap,
        String pageRange) throws PdfException, IOException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, pt.x, pt.y, wrap, pageRange);
    }

    /**
     * Writes text <code>str</code> with specified wrap setting at 
     * point <code>pt</code> on this <code>PdfDocument</code>'s 
     * current page.
     * 
     * @param str
     *            text that needs to be written
     * @param pt
     *            <code>PdfPoint</code> where the text needs to be
     *            written
     * @param wrap
     *            constant specifying whether the text needs to be
     *            wrapped
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfPoint_boolean">example</a>.
     */
    public void writeText(String str, PdfPoint pt, boolean wrap)
        throws PdfException, IOException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, pt.x, pt.y, wrap, Integer.toString(currentPage));
    }

    /**
     * Writes specified text with specified alignment and 
     * wrap setting at position (<code>x</code>, <code>y</code>)
     * on pages in specified page range.
     * 
     * @param str
     *            text that needs to be written
     * @param x
     *            x-coordinate of the position where the text needs 
     *            to be written
     * @param y
     *            y-coordinate of the position where the text needs
     *            to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned
     * @param wrap
     *            constant specifying whether the text needs to be 
     *            wrapped
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_double_double_int_boolean_String">example</a>.
     */
    public void writeText(String str, double x, double y,
        int alignment, boolean wrap, String pageRange)
        throws IOException, PdfException
    {
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }

	    PdfPage p;
	    PdfFont f;
	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for(Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        if (pageTree == null)
            {
                throw new PdfException("page number out of range.");
            }
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        f = p.prevFont;
	        if (f == null)
			{
			    f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
			}
	        double tempY = y;
	        
            if (tempY + PdfMeasurement.convertToMeasurementUnit(
               measurementUnit, f.getHeight()) >= PdfMeasurement
               .convertToMeasurementUnit(measurementUnit, p.pageHeight
               - (p.pageTopMargin + p.pageCropTop + p.pageHeaderHeight 
                   + p.pageBottomMargin  + p.pageFooterHeight + p.pageCropBottom)))
            {
                for (int m = ii; m < siz; m++)
                {
                    pageNos[m] = pageNos[m] + 1;
                }
                p = new PdfPage(p.pageWidth, p.pageHeight,
                    p.pageHeaderHeight, p.pageFooterHeight,
                    p.pageLeftMargin, p.pageTopMargin,
                    p.pageRightMargin, p.pageBottomMargin,
                    PdfMeasurement.MU_POINTS);
                tempY = 0;
                this.pageTree.insert(pageNo + 1, p);
                currentPage = pageNo + 1;
                pageNo++;
            }

	        PdfRect r = new PdfRect(x, tempY, p.pageWidth, p.pageHeight);
	        r = p.updatePageSettings(r, measurementUnit);
	        
	        tf = (PdfTextFormatter) p.getTextFormatter().clone();
	        //tf.setFirstLinePosition(0);
	        tf.setWrap(wrap);
	        tf.setAlignment(alignment);
	        
	        if (autoPaginate == true)
            {
                String tempStr = str;
                int n = 1;
                boolean isFirstTime = true;
                while (tempStr != "")
                {
                    p.writeText(tempStr, r, f, tf, measurementUnit, true);
                    tempStr = p.wrapText(tempStr, r, 0, f, measurementUnit);
                    if (isFirstTime)
                    {
                        r = new PdfRect(x, 0, p.pageWidth, p.pageHeight);
                        r = p.updatePageSettings(r, measurementUnit);
                    }
                    isFirstTime = false;
                    if (tempStr != "")
                    {
                        for(int m = ii; m < siz; m++)
                        {
                            pageNos[m] = pageNos[m] + 1;
                        }
                        n++;
                        p = new PdfPage(p.pageWidth, p.pageHeight,
                            p.pageHeaderHeight, p.pageFooterHeight,
                            p.pageLeftMargin, p.pageTopMargin,
                            p.pageRightMargin, p.pageBottomMargin,
                            PdfMeasurement.MU_POINTS);

                        this.pageTree.insert(pageNo + 1, p);
                        currentPage = pageNo + 1;
                        pageNo++;
                    }
                }
	        }
	        else
	        {
	            p.writeText(str, r, f, tf, measurementUnit, true);
	        }
	    }
    }    
	
    /**
     * Writes specified text with specified alignment and 
     * wrap setting at position (<code>x</code>, <code>y</code>)
     * on this <code>PdfDocument</code>'s current page.
     * 
     * @param str
     *            text that needs to be written
     * @param x
     *            x-coordinate of the position where the text needs 
     *            to be written
     * @param y
     *            y-coordinate of the position where the text needs 
     *            to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned
     * @param wrap
     *            constant specifying whether the text needs to be 
     *            wrapped
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_double_double_int_boolean">example</a>.
     */
    public void writeText(String str, double x, double y,
        int alignment, boolean wrap) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, x, y, alignment, wrap, Integer
            .toString(currentPage));
    }    
	
    /**
     * Writes text <code>str</code> with specified alignment and
     * wrap setting at point <code>pt</code> on pages in specified
     * page range.
     * 
     * @param str
     *            text that needs to be written
     * @param pt
     *            <code>PdfPoint</code> where the text needs to be
     *            written
     * @param alignment
     *            constant specifying how the text needs to be aligned
     * @param wrap
     *            constant specifying whether the text needs to be
     *            wrapped
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfPoint_int_boolean_String">example</a>.
     */
    public void writeText(String str, PdfPoint pt,
        int alignment, boolean wrap, String pageRange)
        throws IOException, PdfException
    {
        writeText(str, pt.x, pt.y, alignment, wrap, pageRange);
    }    
	
    /**
     * Writes text <code>str</code> with specified alignment and wrap
     * setting at point <code>pt</code> on this 
     * <code>PdfDocument</code>'s current page.
     * 
     * @param str
     *            text that needs to be written
     * @param pt
     *            PdfPoint where the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned
     * @param wrap
     *            constant specifying whether the text needs to be
     *            wrapped
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfPoint_int_boolean">example</a>.
     */
    public void writeText(String str, PdfPoint pt,
        int alignment, boolean wrap) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, pt.x, pt.y, alignment, wrap, Integer
            .toString(currentPage));
    }    
	
    /**
     * Writes specified text rotated by <code>rotation</code>
     * degrees at position (<code>x</code>, <code>y</code>) on
     * pages in specified page range.
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param str
     *            text that needs to be written
     * @param x
     *            x-coordinate of the position where the text needs to
     *            be written
     * @param y
     *            y-coordinate of the position where the text needs to
     *            be written
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws IOException if an I/O error occurs.  
     * @throws PdfException if an illegal argument is supplied. 
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_double_double_double_String">example</a>.
     */
    public void writeText(String str, double x, double y,
        double rotation, String pageRange) throws IOException, PdfException
    {
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }

	    PdfPage p;
	    PdfFont f;
	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for(Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        if (pageTree == null)
            {
                throw new PdfException("page number out of range.");
            }
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        f = p.prevFont;
	        if (f == null)
			{
			    f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
			}
	        double tempY = y;
	        
            if (tempY + PdfMeasurement.convertToMeasurementUnit(
               measurementUnit, f.getHeight()) >= PdfMeasurement
               .convertToMeasurementUnit(measurementUnit, p.pageHeight
               - (p.pageTopMargin + p.pageCropTop + p.pageHeaderHeight 
                   + p.pageBottomMargin  + p.pageFooterHeight + p.pageCropBottom)))
            {
                for (int m = ii; m < siz; m++)
                {
                    pageNos[m] = pageNos[m] + 1;
                }
                p = new PdfPage(p.pageWidth, p.pageHeight,
                    p.pageHeaderHeight, p.pageFooterHeight,
                    p.pageLeftMargin, p.pageTopMargin,
                    p.pageRightMargin, p.pageBottomMargin,
                    PdfMeasurement.MU_POINTS);
                tempY = 0;
                this.pageTree.insert(pageNo + 1, p);
                currentPage = pageNo + 1;
                pageNo++;
            }

            double width = f.getWidth(str, PdfMeasurement.MU_POINTS);
            double height = f.getHeight();
            PdfRect r = new PdfRect(x, tempY, width, height);
            
	        r = p.updatePageSettings(r, measurementUnit);
	        
	        tf = (PdfTextFormatter) p.getTextFormatter().clone();
	        //tf.setFirstLinePosition(0);
	        tf.setRotation(rotation);
	        
	        p.writeText(str, r, f, tf, measurementUnit, true);
	    }
    }    
 
    /**
     * Writes specified text rotated by <code>rotation</code> degrees
     * at position (<code>x</code>, <code>y</code>) on this 
     * <code>PdfDocument</code>'s current page.
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * @param str
     *            text that needs to be written
     * @param x
     *            x-coordinate of the position where the text needs to
     *            be written
     * @param y
     *            y-coordinate of the position where the text needs to
     *            be written
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_double_double_double">example</a>.
     */
    public void writeText(String str, double x, double y,
        double rotation) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, x, y, rotation, Integer
            .toString(currentPage));
    }    

    /**
     * Writes text <code>str</code>, rotated <code>rotation</code>
     * degrees, at point <code>pt</code> on pages in specified page
     * range.
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param str
     *            text that needs to be written
     * @param pt
     *            PdfPoint where the text needs to be written
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfPoint_double_String">example</a>.
     */
    public void writeText(String str, PdfPoint pt,
        double rotation, String pageRange) throws IOException, PdfException
    {
        writeText(str, pt.x, pt.y, rotation, pageRange);
    }    
 
    /**
     * Writes text <code>str</code>, rotated by <code>rotation</code>
     * degrees, at point <code>pt</code> on this  
     * <code>PdfDocument</code>'s current page.
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param str
     *            text that needs to be written
     * @param pt
     *            <code>PdfPoint</code> where the text needs to be
     *            written
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfPoint_double">example</a>.
     */
    public void writeText(String str, PdfPoint pt,
        double rotation) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, pt.x, pt.y, rotation, Integer
            .toString(currentPage));
    }  
    
	/**
     * Writes specified text with specified font inside specified
     * rectangle on pages in specified page range.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param rectangle
     *            rectangle inside which the text needs to be written
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfFont_PdfRect_String">example</a>.
     */
	public void writeText(String str, PdfFont f, PdfRect rectangle, 
        String pageRange) throws PdfException, IOException
	{
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }

	    PdfPage p;
	    if (f == null)
        {
	        throw new PdfException("Illegal argument, font is null");
        }
	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for (Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    double rectY = rectangle.y; 
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        rectangle.y = rectY;
            if(rectangle.y + PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, f.getHeight()) >= PdfMeasurement
                .convertToMeasurementUnit(measurementUnit, p.pageHeight
                - (p.pageTopMargin + p.pageCropTop + p.pageHeaderHeight 
                    + p.pageBottomMargin  + p.pageFooterHeight + p.pageCropBottom)))
            {
                 for (int m = ii; m < siz; m++)
                 {
                     pageNos[m] = pageNos[m] + 1;
                 }
                 p = new PdfPage(p.pageWidth, p.pageHeight,
                     p.pageHeaderHeight, p.pageFooterHeight,
                     p.pageLeftMargin, p.pageTopMargin,
                     p.pageRightMargin, p.pageBottomMargin,
                     PdfMeasurement.MU_POINTS);
                 rectangle.y = 0;
                 this.pageTree.insert(pageNo + 1, p);
                 currentPage = pageNo + 1;
                 pageNo++;
            }
	        
            PdfRect r = new PdfRect(rectangle.x, rectangle.y,
                rectangle.width, rectangle.height);
	        r = p.updatePageSettings(r, measurementUnit);
	        
	        tf = (PdfTextFormatter) p.getTextFormatter().clone();
	        //tf.setFirstLinePosition(1);
	        
	        if (autoPaginate == true)
            {
                String tempStr = str;
                int n = 1;
                while (tempStr != "")
                {
                    boolean isFirstTime = true;
                    p.writeText(tempStr, r, f, tf, measurementUnit, true);
                    tempStr = p.wrapText(tempStr, r, tf.getFirstLinePosition(), f,
                        measurementUnit);
                    if (isFirstTime)
                    {
                        tf.setFirstLinePosition(0);
                    }
                    isFirstTime = false;
                    
                    if (tempStr != "")
                    {
                        for(int m = ii; m < siz; m++)
                        {
                            pageNos[m] = pageNos[m] + 1;
                        }
                        n++;
                        p = new PdfPage(p.pageWidth, p.pageHeight,
                            p.pageHeaderHeight, p.pageFooterHeight,
                            p.pageLeftMargin, p.pageTopMargin,
                            p.pageRightMargin, p.pageBottomMargin,
                            PdfMeasurement.MU_POINTS);

                        this.pageTree.insert(pageNo + 1, p);
                        currentPage = pageNo + 1;
                        pageNo++;
                    }
                }
	        }
	        else
	        {
	            p.writeText(str, r, f, tf, measurementUnit, true);
	        }
	    }
	}

	/**
     * Writes specified text with specified font inside specified
     * rectangle on this <code>PdfDocument</code>'s current page.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param rectangle
     *            rectangle inside which the text needs to be written
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfFont_PdfRect">example</a>.
     */
	public void writeText(String str, PdfFont f, PdfRect rectangle)
        throws PdfException, IOException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, f,rectangle, Integer.toString(currentPage));
    }
	
	/**
     * Writes specified text inside specified rectangle on pages in
     * specified page range.
     * 
     * @param str
     *            text that needs to be written
     * @param rectangle
     *            rectangle inside which the text needs to be written
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfRect_String">example</a>.
     */
	public void writeText(String str, PdfRect rectangle, 
        String pageRange) throws PdfException, IOException
	{
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }
	    PdfFont f;
	    PdfPage p;
	    	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for (Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    double rectY = rectangle.y; 
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        f = p.prevFont;
	        if (f == null)
			{
			    f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
			}
	        
	        rectangle.y = rectY;
            if(rectangle.y + PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, f.getHeight()) >= PdfMeasurement
                .convertToMeasurementUnit(measurementUnit, p.pageHeight
                - (p.pageTopMargin + p.pageCropTop + p.pageHeaderHeight 
                    + p.pageBottomMargin  + p.pageFooterHeight + p.pageCropBottom)))
            {
                 for (int m = ii/* + n */; m < siz; m++)
                 {
                     pageNos[m] = pageNos[m] + 1;
                 }
                 p = new PdfPage(p.pageWidth, p.pageHeight,
                     p.pageHeaderHeight, p.pageFooterHeight,
                     p.pageLeftMargin, p.pageTopMargin,
                     p.pageRightMargin, p.pageBottomMargin,
                     PdfMeasurement.MU_POINTS);
                 rectangle.y = 0;
                 this.pageTree.insert(pageNo + 1, p);
                 currentPage = pageNo + 1;
                 pageNo++;
            }
	        
            PdfRect r = new PdfRect(rectangle.x, rectangle.y,
                rectangle.width, rectangle.height);
	        r = p.updatePageSettings(r, measurementUnit);
	        
	        tf = (PdfTextFormatter) p.getTextFormatter().clone();
	        //tf.setFirstLinePosition(0);
	        
	        if (autoPaginate == true)
            {
                String tempStr = str;
                int n = 1;
                while (tempStr != "")
                {
                    boolean isFirstTime = true;
                    p.writeText(tempStr, r, f, tf, measurementUnit, true);
                    tempStr = p.wrapText(tempStr, r, tf.getFirstLinePosition(), f,
                        measurementUnit);
                    if (isFirstTime)
                    {
                        tf.setFirstLinePosition(0);
                    }
                    isFirstTime = false;
                                        
                    if (tempStr != "")
                    {
                        for(int m = ii/*+ n*/; m < siz; m++)
                        {
                            pageNos[m] = pageNos[m] + 1;
                        }
                        n++;
                        p = new PdfPage(p.pageWidth, p.pageHeight,
                            p.pageHeaderHeight, p.pageFooterHeight,
                            p.pageLeftMargin, p.pageTopMargin,
                            p.pageRightMargin, p.pageBottomMargin,
                            PdfMeasurement.MU_POINTS);

                        this.pageTree.insert(pageNo + 1, p);
                        currentPage = pageNo + 1;
                        pageNo++;
                    }
                }
	        }
	        else
	        {
	            p.writeText(str, r, f, tf, measurementUnit, true);
	        }
	    }
	}
	
	/**
     * Writes specified text inside specified rectangle on this
     * <code>PdfDocument</code>'s current page.
     * 
     * @param str
     *            text that needs to be written
     * @param rectangle
     *            rectangle inside which the text needs to be written
     * @throws PdfException
     *            if an illegal argument is supplied. 
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfRect">example</a>.
     */
	public void writeText(String str, PdfRect rectangle)
        throws PdfException, IOException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, rectangle, Integer.toString(currentPage));
    }
	
	/**
     * Writes specified text with specified alignment inside specified
     * rectangle on pages in specified page range.
     * 
     * @param str
     *            text that needs to be written
     * @param rectangle
     *            rectangle inside which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned inside the rectangle 
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @see PdfTextFormatter 
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfRect_int_String">example</a>.
     */
	public void writeText(String str, PdfRect rectangle,
        int alignment, String pageRange) throws PdfException,
        IOException
	{
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }
	    PdfFont f;
	    PdfPage p;
	    	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for (Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    double rectY = rectangle.y; 
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        f = p.prevFont;
	        if (f == null)
			{
			    f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
			}
	        
	        rectangle.y = rectY;
            if(rectangle.y + PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, f.getHeight()) >= PdfMeasurement
                .convertToMeasurementUnit(measurementUnit, p.pageHeight
                - (p.pageTopMargin + p.pageCropTop + p.pageHeaderHeight 
                    + p.pageBottomMargin  + p.pageFooterHeight + p.pageCropBottom)))
            {
                 for (int m = ii/* + n */; m < siz; m++)
                 {
                     pageNos[m] = pageNos[m] + 1;
                 }
                 p = new PdfPage(p.pageWidth, p.pageHeight,
                     p.pageHeaderHeight, p.pageFooterHeight,
                     p.pageLeftMargin, p.pageTopMargin,
                     p.pageRightMargin, p.pageBottomMargin,
                     PdfMeasurement.MU_POINTS);
                 rectangle.y = 0;
                 this.pageTree.insert(pageNo + 1, p);
                 currentPage = pageNo + 1;
                 pageNo++;
            }
	        
            PdfRect r = new PdfRect(rectangle.x, rectangle.y,
                rectangle.width, rectangle.height);
	        r = p.updatePageSettings(r, measurementUnit);
	        
	        tf = (PdfTextFormatter) p.getTextFormatter().clone();
	        //tf.setFirstLinePosition(0);
	        tf.setAlignment(alignment);
	        
	        if (autoPaginate == true)
            {
                String tempStr = str;
                int n = 1;
                while (tempStr != "")
                {
                    boolean isFirstTime = true;
                    p.writeText(tempStr, r, f, tf, measurementUnit, true);
                    tempStr = p.wrapText(tempStr, r, tf.getFirstLinePosition(), f,
                        measurementUnit);
                    if (isFirstTime)
                    {
                        tf.setFirstLinePosition(0);
                    }
                    isFirstTime = false;
                                        
                    if (tempStr != "")
                    {
                        for(int m = ii/*+ n*/; m < siz; m++)
                        {
                            pageNos[m] = pageNos[m] + 1;
                        }
                        n++;
                        p = new PdfPage(p.pageWidth, p.pageHeight,
                            p.pageHeaderHeight, p.pageFooterHeight,
                            p.pageLeftMargin, p.pageTopMargin,
                            p.pageRightMargin, p.pageBottomMargin,
                            PdfMeasurement.MU_POINTS);

                        this.pageTree.insert(pageNo + 1, p);
                        currentPage = pageNo + 1;
                        pageNo++;
                    }
                }
	        }
	        else
	        {
	            p.writeText(str, r, f, tf, measurementUnit, true);
	        }
	    }
	}

	/**
     * Writes specified text with specfied text alignment on this
     * <code>PdfDocument</code>'s current page.
     * 
     * @param str
     *            text that needs to be written
     * @param rectangle
     *            rectangle inside which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned inside the rectangle
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfRect_int">example</a>.
     */
	public void writeText(String str, PdfRect rectangle, int alignment)
        throws PdfException, IOException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, rectangle, alignment, Integer
            .toString(currentPage));
    }
	
    /**
     * Writes specified text rotated by <code>rotation</code>
     * degrees with specified first-line position inside specified
     * rectangle on pages in specified page range.
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param str
     *            text that needs to be written
     * @param rectangle
     *            rectangle inside which the text needs to be written
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @param firstLinePosition
     *            position inside the rectangle where the first line
     *            of text should begin (Applied in current measurement
     *            unit)
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfRect_double_double_String">example</a>.
     */
    public void writeText(String str, PdfRect rectangle, double rotation,
        double firstLinePosition, String pageRange)
        throws IOException, PdfException
    {
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }
	    PdfFont f;
	    PdfPage p;
	    	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for (Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    double rectY = rectangle.y; 
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        f = p.prevFont;
	        if (f == null)
			{
			    f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
			}
	        
	        rectangle.y = rectY;
            if(rectangle.y + PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, f.getHeight()) >= PdfMeasurement
                .convertToMeasurementUnit(measurementUnit, p.pageHeight
                - (p.pageTopMargin + p.pageCropTop + p.pageHeaderHeight 
                    + p.pageBottomMargin  + p.pageFooterHeight + p.pageCropBottom)))
            {
                 for (int m = ii/* + n */; m < siz; m++)
                 {
                     pageNos[m] = pageNos[m] + 1;
                 }
                 p = new PdfPage(p.pageWidth, p.pageHeight,
                     p.pageHeaderHeight, p.pageFooterHeight,
                     p.pageLeftMargin, p.pageTopMargin,
                     p.pageRightMargin, p.pageBottomMargin,
                     PdfMeasurement.MU_POINTS);
                 rectangle.y = 0;
                 this.pageTree.insert(pageNo + 1, p);
                 currentPage = pageNo + 1;
                 pageNo++;
            }
	        
            PdfRect r = new PdfRect(rectangle.x, rectangle.y,
                rectangle.width, rectangle.height);
	        r = p.updatePageSettings(r, measurementUnit);
	        
	        tf = (PdfTextFormatter) p.getTextFormatter().clone();
	        tf.setRotation(rotation);
	        tf.setFirstLinePosition(firstLinePosition);
	        
	        p.writeText(str, r, f, tf, measurementUnit, true);
	    }
    } 
    
    /**
     * Writes specified text rotated by <code>rotation</code>
     * degrees with specified first-line position inside specified
     * rectangle on this <code>PdfDocument</code>'s current page.
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param str
     *            text that needs to be written
     * @param rectangle
     *            rectangle inside which the text needs to be written
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @param firstLinePosition
     *            position inside the rectangle where the first line
     *            of text should begin (Applied in current measurement
     *            unit)
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfRect_double_double">example</a>.
     */
    public void writeText(String str, PdfRect rectangle, double rotation,
        double firstLinePosition) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, rectangle, rotation, firstLinePosition, Integer
            .toString(currentPage));
    }    

    /**
     * Writes specified text rotated by <code>rotation</code>
     * degrees with specified alignment and first-line position inside
     * specified rectangle on pages in specified page range.
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param str
     *            text that needs to be written
     * @param rectangle
     *            rectangle inside which the text needs to be written
     * @param alignment
     *            how the text needs to be aligned inside the
     *            rectangle
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @param firstLinePosition
     *            position inside the rectangle where the first line
     *            of text should begin (Applied in current measurement
     *            unit)
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfRect_int_double_double_String">example</a>.
     */
    public void writeText(String str, PdfRect rectangle, int alignment,
        double rotation, double firstLinePosition, String pageRange)
        throws IOException, PdfException
    {
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }
	    PdfFont f;
	    PdfPage p;
	    	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for (Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    double rectY = rectangle.y; 
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        f = p.prevFont;
	        if (f == null)
			{
			    f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
			}
	        
	        rectangle.y = rectY;
            if(rectangle.y + PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, f.getHeight()) >= PdfMeasurement
                .convertToMeasurementUnit(measurementUnit, p.pageHeight
                - (p.pageTopMargin + p.pageCropTop + p.pageHeaderHeight 
                    + p.pageBottomMargin  + p.pageFooterHeight + p.pageCropBottom)))
            {
                for (int m = ii/* + n */; m < siz; m++)
                 {
                     pageNos[m] = pageNos[m] + 1;
                 }
                 p = new PdfPage(p.pageWidth, p.pageHeight,
                     p.pageHeaderHeight, p.pageFooterHeight,
                     p.pageLeftMargin, p.pageTopMargin,
                     p.pageRightMargin, p.pageBottomMargin,
                     PdfMeasurement.MU_POINTS);
                 rectangle.y = 0;
                 this.pageTree.insert(pageNo + 1, p);
                 currentPage = pageNo + 1;
                 pageNo++;
            }
	        
            PdfRect r = new PdfRect(rectangle.x, rectangle.y,
                rectangle.width, rectangle.height);
	        r = p.updatePageSettings(r, measurementUnit);
	        
	        tf = (PdfTextFormatter) p.getTextFormatter().clone();
	        tf.setRotation(rotation);
	        tf.setFirstLinePosition(firstLinePosition);
	        tf.setAlignment(alignment);
	        
	        p.writeText(str, r, f, tf, measurementUnit, true);
	    }
    }

    /**
     * Writes specified text rotated by <code>rotation</code>
     * degrees with specified first-line position inside specified
     * rectangle on this <code>PdfDocument</code>'s current page.
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param str
     *            text that needs to be written
     * @param rectangle
     *            rectangle inside which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned inside the rectangle
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @param firstLinePosition
     *            position inside the rectangle where the first line
     *            of text should begin (Applied in current measurement
     *            unit)
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfRect_int_double_double">example</a>.
     */
    public void writeText(String str, PdfRect rectangle, int alignment,
        double rotation, double firstLinePosition)
        throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, rectangle, alignment, rotation, firstLinePosition, Integer
            .toString(currentPage));
    }

    /**
     * Writes specified text rotated by <code>rotation</code>
     * degrees with specified font and first-line position inside
     * specified rectangle on pages in specified page range.
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            text that needs to be written
     * @param rectangle
     *            rectangle inside which the text needs to be written
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @param firstLinePosition
     *            position inside the rectangle where the first line
     *            of text should begin (Applied in current 
     *            measurement unit)
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfFont_PdfRect_double_double_String">example</a>.
     */
    public void writeText(String str, PdfFont f, PdfRect rectangle,
        double rotation, double firstLinePosition, String pageRange)
        throws IOException, PdfException
    {
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }

	    PdfPage p;
	    if (f == null)
        {
	        throw new PdfException("Illegal argument, font is null");
        }
	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for (Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    double rectY = rectangle.y; 
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        rectangle.y = rectY;
            if(rectangle.y + PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, f.getHeight()) >= PdfMeasurement
                .convertToMeasurementUnit(measurementUnit, p.pageHeight
                - (p.pageTopMargin + p.pageCropTop + p.pageHeaderHeight 
                    + p.pageBottomMargin  + p.pageFooterHeight + p.pageCropBottom)))
            {
                 for (int m = ii; m < siz; m++)
                 {
                     pageNos[m] = pageNos[m] + 1;
                 }
                 p = new PdfPage(p.pageWidth, p.pageHeight,
                     p.pageHeaderHeight, p.pageFooterHeight,
                     p.pageLeftMargin, p.pageTopMargin,
                     p.pageRightMargin, p.pageBottomMargin,
                     PdfMeasurement.MU_POINTS);
                 rectangle.y = 0;
                 this.pageTree.insert(pageNo + 1, p);
                 currentPage = pageNo + 1;
                 pageNo++;
            }
	        
            PdfRect r = new PdfRect(rectangle.x, rectangle.y,
                rectangle.width, rectangle.height);
	        r = p.updatePageSettings(r, measurementUnit);
	        
	        tf = (PdfTextFormatter) p.getTextFormatter().clone();
	        tf.setFirstLinePosition(firstLinePosition);
	        tf.setRotation(rotation);
	        
	        p.writeText(str, r, f, tf, measurementUnit, true);
	    }
    }
    
    /**
     * Writes specified text rotated by <code>rotation</code>
     * degrees with specified font and first-line position inside
     * specified rectangle on this <code>PdfDocument</code>'s
     * current page.
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param rectangle
     *            rectangle inside which the text needs to be written
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @param firstLinePosition
     *            position inside the rectangle where the first line
     *            of text should begin (Applied in current measurement
     *            unit)
     * @throws IOException
     *             if an I/O error occurs.
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfFont_PdfRect_double_double">example</a>.
     */
    public void writeText(String str, PdfFont f, PdfRect rectangle,
        double rotation, double firstLinePosition)
        throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, f, rectangle, rotation, firstLinePosition,
            Integer.toString(currentPage));
    }  
    
    /**
     * Writes specified text with specified font and alignment inside
     * specified rectangle on pages in specified page range.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param rectangle
     *            rectangle inside which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned inside the rectangle 
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfFont_PdfRect_int_String">example</a>.
     */
    public void writeText(String str, PdfFont f, PdfRect rectangle,
        int alignment, String pageRange) throws IOException,
        PdfException
    {
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }

	    PdfPage p;
	    if (f == null)
        {
	        throw new PdfException("Illegal argument, font is null");
        }
	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for (Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    double rectY = rectangle.y; 
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        rectangle.y = rectY;
            if(rectangle.y + PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, f.getHeight()) >= PdfMeasurement
                .convertToMeasurementUnit(measurementUnit, p.pageHeight
                - (p.pageTopMargin + p.pageCropTop + p.pageHeaderHeight 
                    + p.pageBottomMargin  + p.pageFooterHeight + p.pageCropBottom)))
            {
                 for (int m = ii; m < siz; m++)
                 {
                     pageNos[m] = pageNos[m] + 1;
                 }
                 p = new PdfPage(p.pageWidth, p.pageHeight,
                     p.pageHeaderHeight, p.pageFooterHeight,
                     p.pageLeftMargin, p.pageTopMargin,
                     p.pageRightMargin, p.pageBottomMargin,
                     PdfMeasurement.MU_POINTS);
                 rectangle.y = 0;
                 this.pageTree.insert(pageNo + 1, p);
                 currentPage = pageNo + 1;
                 pageNo++;
            }
	        
            PdfRect r = new PdfRect(rectangle.x, rectangle.y,
                rectangle.width, rectangle.height);
	        r = p.updatePageSettings(r, measurementUnit);
	        
	        tf = (PdfTextFormatter) p.getTextFormatter().clone();
	        tf.setAlignment(alignment);
	        
	        if (autoPaginate == true)
            {
                String tempStr = str;
                int n = 1;
                while (tempStr != "")
                {
                    boolean isFirstTime = true;
                    p.writeText(tempStr, r, f, tf, measurementUnit, true);
                    tempStr = p.wrapText(tempStr, r, tf.getFirstLinePosition(), f,
                        measurementUnit);
                    if (isFirstTime)
                    {
                        tf.setFirstLinePosition(0);
                    }
                    isFirstTime = false;
                    
                    if (tempStr != "")
                    {
                        for(int m = ii; m < siz; m++)
                        {
                            pageNos[m] = pageNos[m] + 1;
                        }
                        n++;
                        p = new PdfPage(p.pageWidth, p.pageHeight,
                            p.pageHeaderHeight, p.pageFooterHeight,
                            p.pageLeftMargin, p.pageTopMargin,
                            p.pageRightMargin, p.pageBottomMargin,
                            PdfMeasurement.MU_POINTS);

                        this.pageTree.insert(pageNo + 1, p);
                        currentPage = pageNo + 1;
                        pageNo++;
                    }
                }
	        }
	        else
	        {
	            p.writeText(str, r, f, tf, measurementUnit, true);
	        }
	    }
    }
    
    /**
     * Writes specified text with specified alignment and font inside
     * specified rectangle on this <code>PdfDocument</code>'s
     * current page.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param rectangle
     *            rectangle inside which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned inside the rectangle
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfFont_PdfRect_int">example</a>.
     */
    public void writeText(String str, PdfFont f, PdfRect rectangle,
        int alignment) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, f, rectangle, alignment, Integer
            .toString(currentPage));
    }
    
    /**
     * Writes specified text rotated by <code>rotation</code>
     * degrees with specified alignment, first-line position, and 
     * font inside specified rectangle on pages in specified page 
     * range.
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            text that needs to be written
     * @param rectangle
     *            rectangle inside which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned inside the rectangle
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @param firstLinePosition
     *            position inside the rectangle where the first line
     *            of text should begin (Applied in current measurement
     *            unit)
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfFont_PdfRect_int_double_double_String">example</a>.
     */
    public void writeText(String str, PdfFont f, PdfRect rectangle,
        int alignment, double rotation, double firstLinePosition,
        String pageRange) throws IOException, PdfException
    {
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }

	    PdfPage p;
	    if (f == null)
        {
	        throw new PdfException("Illegal argument, font is null");
        }
	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for (Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    double rectY = rectangle.y; 
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        rectangle.y = rectY;
            if(rectangle.y + PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, f.getHeight()) >= PdfMeasurement
                .convertToMeasurementUnit(measurementUnit, p.pageHeight
                - (p.pageTopMargin + p.pageCropTop + p.pageHeaderHeight 
                    + p.pageBottomMargin  + p.pageFooterHeight + p.pageCropBottom)))
            {
                 for (int m = ii; m < siz; m++)
                 {
                     pageNos[m] = pageNos[m] + 1;
                 }
                 p = new PdfPage(p.pageWidth, p.pageHeight,
                     p.pageHeaderHeight, p.pageFooterHeight,
                     p.pageLeftMargin, p.pageTopMargin,
                     p.pageRightMargin, p.pageBottomMargin,
                     PdfMeasurement.MU_POINTS);
                 rectangle.y = 0;
                 this.pageTree.insert(pageNo + 1, p);
                 currentPage = pageNo + 1;
                 pageNo++;
            }
	        
            PdfRect r = new PdfRect(rectangle.x, rectangle.y,
                rectangle.width, rectangle.height);
	        r = p.updatePageSettings(r, measurementUnit);
	        
	        tf = (PdfTextFormatter) p.getTextFormatter().clone();
	        tf.setFirstLinePosition(firstLinePosition);
	        tf.setRotation(rotation);
	        tf.setAlignment(alignment);
	        
	        p.writeText(str, r, f, tf, measurementUnit, true);
	    }
    }
    
    /**
     * Writes specified text rotated by <code>rotation</code>
     * degrees with specified alignment, first-line position, and 
     * font inside specified rectangle on this 
     * <code>PdfDocument</code>'s current page.
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param rectangle
     *            rectangle inside which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned inside the rectangle
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @param firstLinePosition
     *            position inside the rectangle where the first line
     *            of text should begin (Applied in current measurement
     *            unit)
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_PdfFont_PdfRect_int_double_double">example</a>.
     */
    public void writeText(String str, PdfFont f, PdfRect rectangle,
        int alignment, double rotation, double firstLinePosition)
        throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, f, rectangle, alignment, rotation,
            firstLinePosition, Integer.toString(currentPage));
    }
    
	/**
     * Writes specified text at current position on pages in specified
     * page range.
     * 
     * @param str
     *            text that needs to be written
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_String">example</a>.
     */
	public void writeText(String str, String pageRange)
    	throws PdfException, IOException
    {
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    double CurX=0/*, CurY =0*/;
	    double tempY;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }
        PdfFont f;

	    PdfPage p;
	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for(Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        f = p.prevFont;
	        if (f == null)
			{
			    f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
			}
	        
	        tempY = PdfMeasurement.convertToMeasurementUnit(
  	             measurementUnit, p.cursorPosY);
	        CurX = PdfMeasurement.convertToMeasurementUnit(
  	             measurementUnit, p.cursorPosX);
	        tf = (PdfTextFormatter) p.getTextFormatter().clone();
	        
            while (tempY + PdfMeasurement.convertToMeasurementUnit(
                    measurementUnit, f.getHeight()) >= PdfMeasurement
                .convertToMeasurementUnit(measurementUnit, p.pageHeight
                - (p.pageBottomMargin + p.pageFooterHeight + p.pageCropBottom)))
            {
                for (int m = ii; m < siz; m++)
                {
                    pageNos[m] = pageNos[m] + 1;
                }
                p = new PdfPage(p.pageWidth, p.pageHeight,
                    p.pageHeaderHeight, p.pageFooterHeight,
                    p.pageLeftMargin, p.pageTopMargin,
                    p.pageRightMargin, p.pageBottomMargin,
                    PdfMeasurement.MU_POINTS);
                tempY = PdfMeasurement.convertToMeasurementUnit(
   	             measurementUnit, p.cursorPosY);
                CurX = PdfMeasurement.convertToMeasurementUnit(
   	             measurementUnit, p.cursorPosX);
                
                this.pageTree.insert(pageNo + 1, p);
                currentPage = pageNo + 1;
                pageNo++;
                tf.setFirstLinePosition(CurX);
            }

            PdfRect r = new PdfRect(0, tempY, p.pageWidth, p.pageHeight);
            r = p.updatePageSettings(r, measurementUnit);
	        
	        tf.setFirstLinePosition(CurX);
	        
	        if (autoPaginate == true)
            {
                String tempStr = str;
                int n = 1;
                boolean isFirstTime = true;
                while (tempStr != "")
                {
                    p.moveCursor = true;
                    p.writeText(tempStr, r, f, tf, measurementUnit, true);
                    p.moveCursor = false;
                    double tmpCurX = PdfMeasurement.convertToPdfUnit(
                        measurementUnit, CurX);
                    tempStr = p.wrapText(tempStr, r, tmpCurX, f, measurementUnit);  
                    if (isFirstTime)
                    {
                        r = new PdfRect(0, 0, p.pageWidth, p.pageHeight);
                        r = p.updatePageSettings(r, measurementUnit);
                    }
                    isFirstTime = false;
                    if (tempStr != "")
                    {
                        for(int m = ii; m < siz; m++)
                        {
                            pageNos[m] = pageNos[m] + 1;
                        }
                        n++;
                        p = new PdfPage(p.pageWidth, p.pageHeight,
                            p.pageHeaderHeight, p.pageFooterHeight,
                            p.pageLeftMargin, p.pageTopMargin,
                            p.pageRightMargin, p.pageBottomMargin,
                            PdfMeasurement.MU_POINTS);

                        this.pageTree.insert(pageNo + 1, p);
                        currentPage = pageNo + 1;
                        pageNo++;
                        CurX = PdfMeasurement.convertToMeasurementUnit(
                            measurementUnit, p.cursorPosX);
                        tf.setFirstLinePosition(0);
                    }
                }
	        }
	        else
	        {
	            p.moveCursor = true;
	            p.writeText(str, r, f, tf, measurementUnit, true);
	            p.moveCursor = false;
	        }
	    }
    }
	
	/**
     * Writes specified text at current position on this
     * <code>PdfDocument</code>'s current page.
     * 
     * @param str
     *            text that needs to be written
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String">example</a>.
     */
	public void writeText(String str) throws PdfException,
        IOException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, Integer.toString(currentPage));
    }
	
	/**
     * Writes specified text with specified font on pages in specified
     * page range.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @throws IOException
     *             if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfFont_String">example</a>.
     */
	public void writeText(String str, PdfFont f, String pageRange)
    throws PdfException, IOException
    {
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    double CurX=0/*, CurY =0*/;
	    double tempY;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }
        if (f == null)
		{
            throw new PdfException("Illegal argument, font is null");
		}

	    PdfPage p;
	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for(Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        tempY = PdfMeasurement.convertToMeasurementUnit(
  	             measurementUnit, p.cursorPosY);
	        CurX = PdfMeasurement.convertToMeasurementUnit(
  	             measurementUnit, p.cursorPosX);
	        tf = (PdfTextFormatter) p.getTextFormatter().clone();
	        
            while (tempY + PdfMeasurement.convertToMeasurementUnit(
                    measurementUnit, f.getHeight()) >= PdfMeasurement
                .convertToMeasurementUnit(measurementUnit, p.pageHeight
                - (p.pageBottomMargin + p.pageFooterHeight + p.pageCropBottom)))
            {
                for (int m = ii; m < siz; m++)
                {
                    pageNos[m] = pageNos[m] + 1;
                }
                p = new PdfPage(p.pageWidth, p.pageHeight,
                    p.pageHeaderHeight, p.pageFooterHeight,
                    p.pageLeftMargin, p.pageTopMargin,
                    p.pageRightMargin, p.pageBottomMargin,
                    PdfMeasurement.MU_POINTS);
                tempY = PdfMeasurement.convertToMeasurementUnit(
   	             measurementUnit, p.cursorPosY);
                CurX = PdfMeasurement.convertToMeasurementUnit(
   	             measurementUnit, p.cursorPosX);
                
                this.pageTree.insert(pageNo + 1, p);
                currentPage = pageNo + 1;
                pageNo++;
                tf.setFirstLinePosition(CurX);
            }

            PdfRect r = new PdfRect(0, tempY, p.pageWidth, p.pageHeight);
            r = p.updatePageSettings(r, measurementUnit);
	        
	        tf.setFirstLinePosition(CurX);
	        
	        if (autoPaginate == true)
            {
                String tempStr = str;
                int n = 1;
                boolean isFirstTime = true;
                while (tempStr != "")
                {
                    p.moveCursor = true;
                    p.writeText(tempStr, r, f, tf, measurementUnit, true);
                    p.moveCursor = false;
                    double tmpCurX = PdfMeasurement.convertToPdfUnit(measurementUnit, CurX);
                    tempStr = p.wrapText(tempStr, r, tmpCurX, f, measurementUnit);  
                    if (isFirstTime)
                    {
                        r = new PdfRect(0, 0, p.pageWidth, p.pageHeight);
                        r = p.updatePageSettings(r, measurementUnit);
                    }
                    isFirstTime = false;
                    if (tempStr != "")
                    {
                        for(int m = ii; m < siz; m++)
                        {
                            pageNos[m] = pageNos[m] + 1;
                        }
                        n++;
                        p = new PdfPage(p.pageWidth, p.pageHeight,
                            p.pageHeaderHeight, p.pageFooterHeight,
                            p.pageLeftMargin, p.pageTopMargin,
                            p.pageRightMargin, p.pageBottomMargin,
                            PdfMeasurement.MU_POINTS);

                        this.pageTree.insert(pageNo + 1, p);
                        currentPage = pageNo + 1;
                        pageNo++;
                        CurX = PdfMeasurement.convertToMeasurementUnit(
                            measurementUnit, p.cursorPosX);
                        tf.setFirstLinePosition(0);
                    }
                }
	        }
	        else
	        {
	            p.moveCursor = true;
	            p.writeText(str, r, f, tf, measurementUnit, true);
	            p.moveCursor = false;
	        }
	    }    
	}

	/**
     * Writes specified text with specified font on this
     * <code>PdfDocument</code>'s current page.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfFont">example</a>.
     */
	public void writeText(String str, PdfFont f) throws PdfException,
        IOException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, f, Integer.toString(currentPage));
    }
	
	/**
     * Writes specified text with specified alignment and
     * wrap setting at current position on pages in
     * specified page range.
     * 
     * @param str
     *            text that needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned 
     * @param wrap
     *            constant specifying whether the text needs to be 
     *            wrapped 
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_int_boolean_String">example</a>.
     */
	public void writeText(String str, int alignment, boolean wrap,
        String pageRange) throws PdfException, IOException
    {
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    double CurX=0/*, CurY =0*/;
	    double tempY;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }
        PdfFont f;

	    PdfPage p;
	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for(Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        f = p.prevFont;
	        if (f == null)
			{
			    f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
			}
	        
	        tempY = PdfMeasurement.convertToMeasurementUnit(
  	             measurementUnit, p.cursorPosY);
	        CurX = PdfMeasurement.convertToMeasurementUnit(
  	             measurementUnit, p.cursorPosX);
	        tf = (PdfTextFormatter) p.getTextFormatter().clone();
	        
            while (tempY + PdfMeasurement.convertToMeasurementUnit(
                    measurementUnit, f.getHeight()) >= PdfMeasurement
                .convertToMeasurementUnit(measurementUnit, p.pageHeight
                - (p.pageBottomMargin + p.pageFooterHeight + p.pageCropBottom)))
            {
                for (int m = ii; m < siz; m++)
                {
                    pageNos[m] = pageNos[m] + 1;
                }
                p = new PdfPage(p.pageWidth, p.pageHeight,
                    p.pageHeaderHeight, p.pageFooterHeight,
                    p.pageLeftMargin, p.pageTopMargin,
                    p.pageRightMargin, p.pageBottomMargin,
                    PdfMeasurement.MU_POINTS);
                tempY = PdfMeasurement.convertToMeasurementUnit(
   	             measurementUnit, p.cursorPosY);
                CurX = PdfMeasurement.convertToMeasurementUnit(
   	             measurementUnit, p.cursorPosX);
                
                this.pageTree.insert(pageNo + 1, p);
                currentPage = pageNo + 1;
                pageNo++;
                tf.setFirstLinePosition(CurX);
            }

            PdfRect r = new PdfRect(0, tempY, p.pageWidth, p.pageHeight);
            r = p.updatePageSettings(r, measurementUnit);
	        
	        tf.setFirstLinePosition(CurX);
	        tf.setWrap(wrap);
	        tf.setAlignment(alignment);
	        
	        if (autoPaginate == true)
            {
                String tempStr = str;
                int n = 1;
                boolean isFirstTime = true;
                while (tempStr != "")
                {
                    p.moveCursor = true;
                    p.writeText(tempStr, r, f, tf, measurementUnit, true);
                    p.moveCursor = false;
                    double tmpCurX = PdfMeasurement.convertToPdfUnit(
                        measurementUnit, CurX);
                    tempStr = p.wrapText(tempStr, r, tmpCurX, f, measurementUnit);  
                    if (isFirstTime)
                    {
                        r = new PdfRect(0, 0, p.pageWidth, p.pageHeight);
                        r = p.updatePageSettings(r, measurementUnit);
                    }
                    isFirstTime = false;
                    if (tempStr != "")
                    {
                        for(int m = ii; m < siz; m++)
                        {
                            pageNos[m] = pageNos[m] + 1;
                        }
                        n++;
                        p = new PdfPage(p.pageWidth, p.pageHeight,
                            p.pageHeaderHeight, p.pageFooterHeight,
                            p.pageLeftMargin, p.pageTopMargin,
                            p.pageRightMargin, p.pageBottomMargin,
                            PdfMeasurement.MU_POINTS);

                        this.pageTree.insert(pageNo + 1, p);
                        currentPage = pageNo + 1;
                        pageNo++;
                        CurX = PdfMeasurement.convertToMeasurementUnit(
                            measurementUnit, p.cursorPosX);
                        tf.setFirstLinePosition(0);
                    }
                }
	        }
	        else
	        {
	            p.moveCursor = true;
	            p.writeText(str, r, f, tf, measurementUnit, true);
	            p.moveCursor = false;
	        }
	    }
    }

	/**
     * Writes specified text with specified alignment and
     * specified wrap setting at current position on this
     * <code>PdfDocument</code>'s current page.
     * 
     * @param str
     *            text that needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned
     * @param wrap
     *            constant specifying whether the text needs to 
     *            be wrapped
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_int_boolean">example</a>.
     */
	public void writeText(String str, int alignment, boolean wrap)
        throws PdfException, IOException
    {
	    if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, alignment, wrap, Integer.toString(currentPage));
    }    
	
	/**
     * Writes specified text with specified alignment at current
     * position on pages in specified page range.
     * 
     * @param str
     *            text that needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_int_String">example</a>.
     */
	public void writeText(String str, int alignment, String pageRange)
        throws PdfException, IOException
    {
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    double CurX=0/*, CurY =0*/;
	    double tempY;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }
        PdfFont f;

	    PdfPage p;
	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for(Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        f = p.prevFont;
	        if (f == null)
			{
			    f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
			}
	        
	        tempY = PdfMeasurement.convertToMeasurementUnit(
  	             measurementUnit, p.cursorPosY);
	        CurX = PdfMeasurement.convertToMeasurementUnit(
  	             measurementUnit, p.cursorPosX);
	        tf = (PdfTextFormatter) p.getTextFormatter().clone();
	        
            while (tempY + PdfMeasurement.convertToMeasurementUnit(
                    measurementUnit, f.getHeight()) >= PdfMeasurement
                .convertToMeasurementUnit(measurementUnit, p.pageHeight
                - (p.pageBottomMargin + p.pageFooterHeight + p.pageCropBottom)))
            {
                for (int m = ii; m < siz; m++)
                {
                    pageNos[m] = pageNos[m] + 1;
                }
                p = new PdfPage(p.pageWidth, p.pageHeight,
                    p.pageHeaderHeight, p.pageFooterHeight,
                    p.pageLeftMargin, p.pageTopMargin,
                    p.pageRightMargin, p.pageBottomMargin,
                    PdfMeasurement.MU_POINTS);
                tempY = PdfMeasurement.convertToMeasurementUnit(
   	             measurementUnit, p.cursorPosY);
                CurX = PdfMeasurement.convertToMeasurementUnit(
   	             measurementUnit, p.cursorPosX);
                
                this.pageTree.insert(pageNo + 1, p);
                currentPage = pageNo + 1;
                pageNo++;
                tf.setFirstLinePosition(CurX);
            }

            PdfRect r = new PdfRect(0, tempY, p.pageWidth, p.pageHeight);
            r = p.updatePageSettings(r, measurementUnit);
	        
	        tf.setFirstLinePosition(CurX);
	        tf.setAlignment(alignment);
	        
	        if (autoPaginate == true)
            {
                String tempStr = str;
                int n = 1;
                boolean isFirstTime = true;
                while (tempStr != "")
                {
                    p.moveCursor = true;
                    p.writeText(tempStr, r, f, tf, measurementUnit, true);
                    p.moveCursor = false;
                    double tmpCurX = PdfMeasurement.convertToPdfUnit(
                        measurementUnit, CurX);
                    tempStr = p.wrapText(tempStr, r, tmpCurX, f, measurementUnit);  
                    if (isFirstTime)
                    {
                        r = new PdfRect(0, 0, p.pageWidth, p.pageHeight);
                        r = p.updatePageSettings(r, measurementUnit);
                    }
                    isFirstTime = false;
                    if (tempStr != "")
                    {
                        for(int m = ii; m < siz; m++)
                        {
                            pageNos[m] = pageNos[m] + 1;
                        }
                        n++;
                        p = new PdfPage(p.pageWidth, p.pageHeight,
                            p.pageHeaderHeight, p.pageFooterHeight,
                            p.pageLeftMargin, p.pageTopMargin,
                            p.pageRightMargin, p.pageBottomMargin,
                            PdfMeasurement.MU_POINTS);

                        this.pageTree.insert(pageNo + 1, p);
                        currentPage = pageNo + 1;
                        pageNo++;
                        CurX = PdfMeasurement.convertToMeasurementUnit(
                            measurementUnit, p.cursorPosX);
                        tf.setFirstLinePosition(0);
                    }
                }
	        }
	        else
	        {
	            p.moveCursor = true;
	            p.writeText(str, r, f, tf, measurementUnit, true);
	            p.moveCursor = false;
	        }
	    }
    }

	/**
     * Writes specified text with specified alignment at 
     * current position on this <code>PdfDocument</code>'s current
     * page.
     * 
     * @param str
     *            text that needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_int">example</a>.
     */
	public void writeText(String str, int alignment)
        throws PdfException, IOException
    {
	    if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, alignment, Integer.toString(currentPage));
    }
	
	/**
     * Writes specified text with specified wrap setting at
     * current position on pages in specified page range.
     * 
     * @param str
     *            text that needs to be written
     * @param wrap
     *            constant specifying whether the text needs to be 
     *            wrapped at the margins            
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_boolean_String">example</a>.
     */
	public void writeText(String str, boolean wrap, String pageRange)
        throws PdfException, IOException
    {
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    double CurX=0/*, CurY =0*/;
	    double tempY;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }
        PdfFont f;

	    PdfPage p;
	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for(Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        p = (PdfPage) pageTree.getPage(pageNo);
	        
	        f = p.prevFont;
	        if (f == null)
			{
			    f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
			}
	        
	        tempY = PdfMeasurement.convertToMeasurementUnit(
  	             measurementUnit, p.cursorPosY);
	        CurX = PdfMeasurement.convertToMeasurementUnit(
  	             measurementUnit, p.cursorPosX);
	        tf = (PdfTextFormatter) p.getTextFormatter().clone();
	        
            while (tempY + PdfMeasurement.convertToMeasurementUnit(
                    measurementUnit, f.getHeight()) >= PdfMeasurement
                .convertToMeasurementUnit(measurementUnit, p.pageHeight
                - (p.pageBottomMargin + p.pageFooterHeight + p.pageCropBottom)))
            {
                for (int m = ii; m < siz; m++)
                {
                    pageNos[m] = pageNos[m] + 1;
                }
                p = new PdfPage(p.pageWidth, p.pageHeight,
                    p.pageHeaderHeight, p.pageFooterHeight,
                    p.pageLeftMargin, p.pageTopMargin,
                    p.pageRightMargin, p.pageBottomMargin,
                    PdfMeasurement.MU_POINTS);
                tempY = PdfMeasurement.convertToMeasurementUnit(
   	             measurementUnit, p.cursorPosY);
                CurX = PdfMeasurement.convertToMeasurementUnit(
   	             measurementUnit, p.cursorPosX);
                
                this.pageTree.insert(pageNo + 1, p);
                currentPage = pageNo + 1;
                pageNo++;
                tf.setFirstLinePosition(CurX);
            }

            PdfRect r = new PdfRect(0, tempY, p.pageWidth, p.pageHeight);
            r = p.updatePageSettings(r, measurementUnit);
	        
	        tf.setFirstLinePosition(CurX);
	        tf.setWrap(wrap);
	        
	        if (autoPaginate == true)
            {
                String tempStr = str;
                int n = 1;
                boolean isFirstTime = true;
                while (tempStr != "")
                {
                    p.moveCursor = true;
                    p.writeText(tempStr, r, f, tf, measurementUnit, true);
                    p.moveCursor = false;
                    double tmpCurX = PdfMeasurement.convertToPdfUnit(
                        measurementUnit, CurX);
                    tempStr = p.wrapText(tempStr, r, tmpCurX, f, measurementUnit);  
                    if (isFirstTime)
                    {
                        r = new PdfRect(0, 0, p.pageWidth, p.pageHeight);
                        r = p.updatePageSettings(r, measurementUnit);
                    }
                    isFirstTime = false;
                    if (tempStr != "")
                    {
                        for(int m = ii; m < siz; m++)
                        {
                            pageNos[m] = pageNos[m] + 1;
                        }
                        n++;
                        p = new PdfPage(p.pageWidth, p.pageHeight,
                            p.pageHeaderHeight, p.pageFooterHeight,
                            p.pageLeftMargin, p.pageTopMargin,
                            p.pageRightMargin, p.pageBottomMargin,
                            PdfMeasurement.MU_POINTS);

                        this.pageTree.insert(pageNo + 1, p);
                        currentPage = pageNo + 1;
                        pageNo++;
                        CurX = PdfMeasurement.convertToMeasurementUnit(
                            measurementUnit, p.cursorPosX);
                        tf.setFirstLinePosition(0);
                    }
                }
	        }
	        else
	        {
	            p.moveCursor = true;
	            p.writeText(str, r, f, tf, measurementUnit, true);
	            p.moveCursor = false;
	        }
	    }
    }

	/**
     * Writes specified text with specified wrap setting at
     * current position on this <code>PdfDocument</code>'s current
     * page.
     * 
     * @param str
     *            text that needs to be written
     * @param wrap
     *            constant specifying whether the text needs to be 
     *            wrapped 
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_boolean">example</a>.
     */
	public void writeText(String str, boolean wrap)
    throws PdfException, IOException
    {
	    if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, wrap, Integer.toString(currentPage));
    }
	
	/**
     * Writes specified text with specified font, alignment and wrap
     * setting on pages in specified page range.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned
     * @param wrap
     *            constant specifying whether the text needs to be 
     *            wrapped
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfFont_int_boolean_String">example</a>.
     */
	public void writeText(String str, PdfFont f, int alignment,
        boolean wrap, String pageRange) throws PdfException,
        IOException
    {
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    double CurX=0/*, CurY =0*/;
	    double tempY;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }
	    if (f == null)
		{
            throw new PdfException("Illegal argument, font is null");
		}

	    PdfPage p;
	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for(Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        p = (PdfPage) pageTree.getPage(pageNo);
	                
	        tempY = PdfMeasurement.convertToMeasurementUnit(
  	             measurementUnit, p.cursorPosY);
	        CurX = PdfMeasurement.convertToMeasurementUnit(
  	             measurementUnit, p.cursorPosX);
	        tf = (PdfTextFormatter) p.getTextFormatter().clone();
	        
            while (tempY + PdfMeasurement.convertToMeasurementUnit(
                    measurementUnit, f.getHeight()) >= PdfMeasurement
                .convertToMeasurementUnit(measurementUnit, p.pageHeight
                - (p.pageBottomMargin + p.pageFooterHeight + p.pageCropBottom)))
            {
                for (int m = ii; m < siz; m++)
                {
                    pageNos[m] = pageNos[m] + 1;
                }
                p = new PdfPage(p.pageWidth, p.pageHeight,
                    p.pageHeaderHeight, p.pageFooterHeight,
                    p.pageLeftMargin, p.pageTopMargin,
                    p.pageRightMargin, p.pageBottomMargin,
                    PdfMeasurement.MU_POINTS);
                tempY = PdfMeasurement.convertToMeasurementUnit(
   	             measurementUnit, p.cursorPosY);
                CurX = PdfMeasurement.convertToMeasurementUnit(
   	             measurementUnit, p.cursorPosX);
                
                this.pageTree.insert(pageNo + 1, p);
                currentPage = pageNo + 1;
                pageNo++;
                tf.setFirstLinePosition(CurX);
            }

            PdfRect r = new PdfRect(0, tempY, p.pageWidth, p.pageHeight);
            r = p.updatePageSettings(r, measurementUnit);
	        
	        tf.setFirstLinePosition(CurX);
	        tf.setWrap(wrap);
	        tf.setAlignment(alignment);
	        
	        if (autoPaginate == true)
            {
                String tempStr = str;
                int n = 1;
                boolean isFirstTime = true;
                while (tempStr != "")
                {
                    p.moveCursor = true;
                    p.writeText(tempStr, r, f, tf, measurementUnit, true);
                    p.moveCursor = false;
                    double tmpCurX = PdfMeasurement.convertToPdfUnit(
                        measurementUnit, CurX);
                    tempStr = p.wrapText(tempStr, r, tmpCurX, f, measurementUnit);  
                    if (isFirstTime)
                    {
                        r = new PdfRect(0, 0, p.pageWidth, p.pageHeight);
                        r = p.updatePageSettings(r, measurementUnit);
                    }
                    isFirstTime = false;
                    if (tempStr != "")
                    {
                        for(int m = ii; m < siz; m++)
                        {
                            pageNos[m] = pageNos[m] + 1;
                        }
                        n++;
                        p = new PdfPage(p.pageWidth, p.pageHeight,
                            p.pageHeaderHeight, p.pageFooterHeight,
                            p.pageLeftMargin, p.pageTopMargin,
                            p.pageRightMargin, p.pageBottomMargin,
                            PdfMeasurement.MU_POINTS);

                        this.pageTree.insert(pageNo + 1, p);
                        currentPage = pageNo + 1;
                        pageNo++;
                        CurX = PdfMeasurement.convertToMeasurementUnit(
                            measurementUnit, p.cursorPosX);
                        tf.setFirstLinePosition(0);
                    }
                }
	        }
	        else
	        {
	            p.moveCursor = true;
	            p.writeText(str, r, f, tf, measurementUnit, true);
	            p.moveCursor = false;
	        }
	    }
    }
	
	/**
     * Writes specified text with specified font, alignment, and wrap
     * setting on this <code>PdfDocument</code>'s current page.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned
     * @param wrap
     *            constant specifying whether the text needs to be 
     *            wrapped
     * @throws PdfException
     *            if an I/O error occurs.
     * @throws IOException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfFont_int_boolean">example</a>.
     */
	public void writeText(String str, PdfFont f, int alignment,
        boolean wrap) throws PdfException, IOException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, f, alignment, wrap, Integer
            .toString(currentPage));
    }
	
	/**
     * Writes specified text with specified font and wrap setting on
     * pages in specified page range.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param wrap
     *            constant specifying whether the text needs to be 
     *            wrapped
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfFont_boolean_String">example</a>.
     */
	public void writeText(String str, PdfFont f, boolean wrap,
        String pageRange) throws PdfException, IOException
    {
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    double CurX=0/*, CurY =0*/;
	    double tempY;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }
	    if (f == null)
		{
            throw new PdfException("Illegal argument, font is null");
		}

	    PdfPage p;
	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for(Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        p = (PdfPage) pageTree.getPage(pageNo);
	                
	        tempY = PdfMeasurement.convertToMeasurementUnit(
  	             measurementUnit, p.cursorPosY);
	        CurX = PdfMeasurement.convertToMeasurementUnit(
  	             measurementUnit, p.cursorPosX);
	        tf = (PdfTextFormatter) p.getTextFormatter().clone();
	        
            while (tempY + PdfMeasurement.convertToMeasurementUnit(
                    measurementUnit, f.getHeight()) >= PdfMeasurement
                .convertToMeasurementUnit(measurementUnit, p.pageHeight
                - (p.pageBottomMargin + p.pageFooterHeight + p.pageCropBottom)))
            {
                for (int m = ii; m < siz; m++)
                {
                    pageNos[m] = pageNos[m] + 1;
                }
                p = new PdfPage(p.pageWidth, p.pageHeight,
                    p.pageHeaderHeight, p.pageFooterHeight,
                    p.pageLeftMargin, p.pageTopMargin,
                    p.pageRightMargin, p.pageBottomMargin,
                    PdfMeasurement.MU_POINTS);
                tempY = PdfMeasurement.convertToMeasurementUnit(
   	             measurementUnit, p.cursorPosY);
                CurX = PdfMeasurement.convertToMeasurementUnit(
   	             measurementUnit, p.cursorPosX);
                
                this.pageTree.insert(pageNo + 1, p);
                currentPage = pageNo + 1;
                pageNo++;
                tf.setFirstLinePosition(CurX);
            }

            PdfRect r = new PdfRect(0, tempY, p.pageWidth, p.pageHeight);
            r = p.updatePageSettings(r, measurementUnit);
	        
	        tf.setFirstLinePosition(CurX);
	        tf.setWrap(wrap);
	        
	        if (autoPaginate == true)
            {
                String tempStr = str;
                int n = 1;
                boolean isFirstTime = true;
                while (tempStr != "")
                {
                    p.moveCursor = true;
                    p.writeText(tempStr, r, f, tf, measurementUnit, true);
                    p.moveCursor = false;
                    double tmpCurX = PdfMeasurement.convertToPdfUnit(
                        measurementUnit, CurX);
                    tempStr = p.wrapText(tempStr, r, tmpCurX, f, measurementUnit);  
                    if (isFirstTime)
                    {
                        r = new PdfRect(0, 0, p.pageWidth, p.pageHeight);
                        r = p.updatePageSettings(r, measurementUnit);
                    }
                    isFirstTime = false;
                    if (tempStr != "")
                    {
                        for(int m = ii; m < siz; m++)
                        {
                            pageNos[m] = pageNos[m] + 1;
                        }
                        n++;
                        p = new PdfPage(p.pageWidth, p.pageHeight,
                            p.pageHeaderHeight, p.pageFooterHeight,
                            p.pageLeftMargin, p.pageTopMargin,
                            p.pageRightMargin, p.pageBottomMargin,
                            PdfMeasurement.MU_POINTS);

                        this.pageTree.insert(pageNo + 1, p);
                        currentPage = pageNo + 1;
                        pageNo++;
                        CurX = PdfMeasurement.convertToMeasurementUnit(
                            measurementUnit, p.cursorPosX);
                        tf.setFirstLinePosition(0);
                    }
                }
	        }
	        else
	        {
	            p.moveCursor = true;
	            p.writeText(str, r, f, tf, measurementUnit, true);
	            p.moveCursor = false;
	        }
	    }
    }

	/**
     * Writes specified text with specified wrap setting and font on
     * this <code>PdfDocument</code>'s current page.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param wrap
     *            constant specifying whether the text needs to be 
     *            wrapped
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfFont_boolean">example</a>.
     */
	public void writeText(String str, PdfFont f, boolean wrap)
        throws PdfException, IOException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, f, wrap, Integer
            .toString(currentPage));
    }
	
	/**
     * Writes specified text with specified font and alignment on
     * pages in specified page range.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned
     * @param pageRange
     *            page range on whose pages the text needs to be
     *            written
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfFont_int_String">example</a>.
     */
	public void writeText(String str, PdfFont f, int alignment,
        String pageRange) throws PdfException, IOException
    {
	    int  pageNo = 1;
	    PdfTextFormatter tf;
	    double CurX=0/*, CurY =0*/;
	    double tempY;
	    Vector v = getPages(pageRange);
	    if ( v == null)
	    {
	       throw new PdfException("Invalid PageRange");
	    }
	    if (f == null)
		{
            throw new PdfException("Illegal argument, font is null");
		}

	    PdfPage p;
	    
	    int siz = v.size();
	    int pageNos[] = new int[siz];
	    int k = 0;
	    for(Iterator j = v.iterator(); j.hasNext();)
	    {
	        pageNos[k] = ((Integer)j.next()).intValue();
	        k++;
	    }
	    
	    for(int ii = 0; ii < siz; ii++)
	    {
	        pageNo = pageNos[ii];
	        p = (PdfPage) pageTree.getPage(pageNo);
	                
	        tempY = PdfMeasurement.convertToMeasurementUnit(
  	             measurementUnit, p.cursorPosY);
	        CurX = PdfMeasurement.convertToMeasurementUnit(
  	             measurementUnit, p.cursorPosX);
	        tf = (PdfTextFormatter) p.getTextFormatter().clone();
	        
            while (tempY + PdfMeasurement.convertToMeasurementUnit(
                    measurementUnit, f.getHeight()) >= PdfMeasurement
                .convertToMeasurementUnit(measurementUnit, p.pageHeight
                - (p.pageBottomMargin + p.pageFooterHeight + p.pageCropBottom)))
            {
                for (int m = ii; m < siz; m++)
                {
                    pageNos[m] = pageNos[m] + 1;
                }
                p = new PdfPage(p.pageWidth, p.pageHeight,
                    p.pageHeaderHeight, p.pageFooterHeight,
                    p.pageLeftMargin, p.pageTopMargin,
                    p.pageRightMargin, p.pageBottomMargin,
                    PdfMeasurement.MU_POINTS);
                tempY = PdfMeasurement.convertToMeasurementUnit(
   	             measurementUnit, p.cursorPosY);
                CurX = PdfMeasurement.convertToMeasurementUnit(
   	             measurementUnit, p.cursorPosX);
                
                this.pageTree.insert(pageNo + 1, p);
                currentPage = pageNo + 1;
                pageNo++;
                tf.setFirstLinePosition(CurX);
            }

            PdfRect r = new PdfRect(0, tempY, p.pageWidth, p.pageHeight);
            r = p.updatePageSettings(r, measurementUnit);
	        
	        tf.setFirstLinePosition(CurX);
	        tf.setAlignment(alignment);
	        
	        if (autoPaginate == true)
            {
                String tempStr = str;
                int n = 1;
                boolean isFirstTime = true;
                while (tempStr != "")
                {
                    p.moveCursor = true;
                    p.writeText(tempStr, r, f, tf, measurementUnit, true);
                    p.moveCursor = false;
                    double tmpCurX = PdfMeasurement.convertToPdfUnit(
                        measurementUnit, CurX);
                    tempStr = p.wrapText(tempStr, r, tmpCurX, f, measurementUnit);  
                    if (isFirstTime)
                    {
                        r = new PdfRect(0, 0, p.pageWidth, p.pageHeight);
                        r = p.updatePageSettings(r, measurementUnit);
                    }
                    isFirstTime = false;
                    if (tempStr != "")
                    {
                        for(int m = ii; m < siz; m++)
                        {
                            pageNos[m] = pageNos[m] + 1;
                        }
                        n++;
                        p = new PdfPage(p.pageWidth, p.pageHeight,
                            p.pageHeaderHeight, p.pageFooterHeight,
                            p.pageLeftMargin, p.pageTopMargin,
                            p.pageRightMargin, p.pageBottomMargin,
                            PdfMeasurement.MU_POINTS);

                        this.pageTree.insert(pageNo + 1, p);
                        currentPage = pageNo + 1;
                        pageNo++;
                        CurX = PdfMeasurement.convertToMeasurementUnit(
                            measurementUnit, p.cursorPosX);
                        tf.setFirstLinePosition(0);
                    }
                }
	        }
	        else
	        {
	            p.moveCursor = true;
	            p.writeText(str, r, f, tf, measurementUnit, true);
	            p.moveCursor = false;
	        }
	    }
    }

	/**
     * Writes specified text with specified font and alignment on 
     * this <code>PdfDocument</code>'s current page.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#writeText_String_PdfFont_int">example</a>.
     */
	public void writeText(String str, PdfFont f, int alignment)
        throws PdfException, IOException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        writeText(str, f, alignment, Integer
            .toString(currentPage));
    }
	
    /**
     * Draws a line between position (<code>startx</code>,
     * <code>starty</code>) and (<code>endx</code>,
     * <code>endy</code>) on pages in specified page range.
     * 
     * @param startx
     *            x-coordinate of the position from which the line 
     *            needs to be drawn
     * @param starty
     *            y-coordinate of the position from which the line 
     *            needs to be drawn
     * @param endx
     *            x-coordinate of the position to which the line 
     *            needs to be drawn
     * @param endy
     *            y-coordinate of the position to which the line 
     *            needs to be drawn
     * @param pageRange
     *            page range on whose pages the line needs to be 
     *            drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawLine_double_double_double_double_String">example</a>.
     */
    public synchronized void drawLine(double startx, double starty,
        double endx, double endy, String pageRange)
        throws IOException, PdfException
    {
        int pageNo = 1;
        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid PageRange");
        }
        Iterator i = v.iterator();
        while (i.hasNext())
        {
            pageNo = ((Integer) i.next()).intValue();

            PdfPage p = (PdfPage) pageTree.getPage(pageNo);
            int tempMu = p.measurementUnit;
            p.measurementUnit = measurementUnit;
            
            PdfPen tempPen  = null;
            PdfBrush tempBrush = null;
            tempPen = p.pen;
            p.pen = this.pen != null ?(PdfPen)this.pen.clone()
                : null;
            
            tempBrush = p.brush;
            p.brush = this.brush != null ?(PdfBrush)this.brush.clone()
                : null;
            
            PdfPoint start = p.updatePageSettings(new PdfPoint(startx,
                starty));

            double startX = PdfMeasurement.convertToPdfUnit(measurementUnit,
                start.x);
            double startY = PdfMeasurement.convertToPdfUnit(measurementUnit,
                start.y);
            startY = p.pageHeight - startY;
            
            StringBuffer sb = new StringBuffer();
            sb.append(p.setPenBrush(false, true));

            sb.append(PdfWriter.formatFloat(startX) + PDF_SP
                + PdfWriter.formatFloat(startY) + " m ");            
            sb.append(p.drawInternalLine(endx, endy));
            sb.append(" S ");
            if (p.contentStream == null)
            {
                p.contentStream = new PdfByteOutputStream();
            }

            p.contentStream.write((sb.toString()).getBytes());
            p.measurementUnit = tempMu;

            p.brush = tempBrush;
            p.pen = tempPen;
        }
    }

    /**
     * Draws a line between position 
     * (<code>startx</code>, <code>starty</code>) and 
     * (<code>endx</code>, <code>endy</code>) on this 
     * <code>PdfDocument</code>'s current page.
     * 
     * @param startx
     *            x-coordinate of the position from which the line 
     *            needs to be drawn
     * @param starty
     *            y-coordinate of the position from which the line 
     *            needs to be drawn
     * @param endx
     *            x-coordinate of the position to which the line  
     *            needs to be drawn
     * @param endy
     *            y-coordinate of the position to which the line 
     *            needs to be drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawLine_double_double_double_double">example</a>.
     */
    public synchronized void drawLine(double startx, double starty,
        double endx, double endy) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawLine(startx, starty, endx, endy, Integer
            .toString(currentPage)); 
    }
    
    /**
     * Draws a line from <code>start</code> to <code>end</code> on
     * pages in specified page range.
     * 
     * @param start
     *            starting point of the line 
     * @param end
     *            end point of the line 
     * @param pageRange
     *            page range on whose pages the line needs to be 
     *            drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawLine_PdfPoint_PdfPoint_String">example</a>.
     */
    public void drawLine(PdfPoint start, PdfPoint end, String pageRange) 
    throws IOException,PdfException 
    {
        drawLine(start.getX(), start.getY(), end.getX(), end.getY(),
            pageRange);
    }
	
    /**
     * Draws a line from <code>start</code> to <code>end</code>
     * on this <code>PdfDocument</code>'s current page.
     * 
     * @param start
     *            starting point of the line
     * @param end
     *            end point of the line
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawLine_PdfPoint_PdfPoint">example</a>.
     */
    public void drawLine(PdfPoint start, PdfPoint end) throws IOException,
        PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawLine(start, end, Integer.toString(currentPage));
    }
    
    /**
     * Draws a rectangle on pages in specified page range page at
     * position (<code>x</code>, <code>y</code>) with specified
     * <code>width</code>, <code>height</code>, brush, and pen
     * settings.
     * 
     * @param x
     *            x-coordinate of top-left corner of the rectangle
     * @param y
     *            y-coordinate of top-left corner of the rectangle
     * @param width
     *            width of the rectangle
     * @param height
     *            height of the rectangle
     * @param isFill
     *            whether the rectangle needs to be filled
     * @param isStroke
     *            whether the rectangle needs to be stroked
     * @param pageRange
     *            page range on whose pages the rectangle needs to be
     *            drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawRect_double_double_double_double_boolean_boolean_String">example</a>.
     */
    public synchronized void drawRect(double x, double y,
        double width, double height, boolean isFill,
        boolean isStroke, String pageRange) throws IOException,
        PdfException
    {
        int pageNo;
        PdfPage  p;
        int tempMu;
        PdfPen tempPen  = null;
        PdfBrush tempBrush = null;

        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid PageRange");
        }
        Iterator i = v.iterator();
        while (i.hasNext())
        {
            pageNo = ((Integer) i.next()).intValue();

            p = pageTree.getPage(pageNo);
            tempMu = p.measurementUnit;
            p.measurementUnit = measurementUnit;
            
            tempPen = p.pen;
            p.pen = this.pen != null ? (PdfPen) this.pen.clone()
                : new PdfPen();
            
            tempBrush = p.brush;
            p.brush = this.brush != null ? (PdfBrush) this.brush.clone()
                : new PdfBrush();
            
            double tempX = x;
            double tempY = y;
            double tempWidth = width;
            double tempHeight = height;
            
            PdfRect r = p.updatePageSettings(new PdfRect(tempX, tempY, tempWidth,
                tempHeight));
            tempX = PdfMeasurement.convertToPdfUnit(measurementUnit, r.x);
            tempY = PdfMeasurement.convertToPdfUnit(measurementUnit, r.y);
            tempWidth = PdfMeasurement.convertToPdfUnit(measurementUnit,
                r.width);
            tempHeight = PdfMeasurement.convertToPdfUnit(measurementUnit,
                r.height);
            tempY = p.pageHeight - tempY - tempHeight;            
            
            StringBuffer sb = new StringBuffer();

            sb.append(p.setPenBrush(isFill, isStroke));
            if ((p.prevBrush != p.brush) && (isFill == true))
            {
                p.setPattern();
            }
            if (isFill)
            {
                if (p.brush.brushPattern != PdfBrush.PATTERN_SOLID)
                {
                    sb.append(p.setFillPattern());
                    sb.append(PdfWriter.formatFloat(tempX) + PDF_SP
                        + PdfWriter.formatFloat(tempY) + PDF_SP
                        + PdfWriter.formatFloat(tempWidth) + PDF_SP
                        + PdfWriter.formatFloat(tempHeight) + " re f ");
                    isFill = false;
                }
            }

            sb.append(PdfWriter.formatFloat(tempX) + PDF_SP
                + PdfWriter.formatFloat(tempY) + PDF_SP
                + PdfWriter.formatFloat(tempWidth) + PDF_SP
                + PdfWriter.formatFloat(tempHeight) + " re ");

            if ((isFill == true) && (isStroke == false))
            {
                sb.append("f ");
            }
            else if ((isFill == false) && (isStroke == true))
            {
                sb.append("S ");
            }
            else if ((isFill == true) && (isStroke == true))
            {
                sb.append("B ");
            }
            else
            {
                sb.append("n ");
            }

            if (p.contentStream == null)
            {
                p.contentStream = new PdfByteOutputStream();
            }
            p.contentStream.write((sb.toString()).getBytes());

            p.brush = tempBrush;
            p.pen = tempPen;
            p.measurementUnit = tempMu;
        }
    }

    /**
     * Draws a rectangle on this <code>PdfDocument</code>'s current
     * page at position (<code>x</code>, <code>y</code>) with
     * specified <code>width</code>, <code>height</code>, pen,
     * and brush settings.
     * 
     * @param x
     *            x-coordinate of top-left corner of the rectangle
     * @param y
     *            y-coordinate of top-left corner of the rectangle
     * @param width
     *            width of the rectangle
     * @param height
     *            height of the rectangle
     * @param isFill
     *            whether the rectangle needs to be filled
     * @param isStroke
     *            whether the rectangle needs to be stroked
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawRect_double_double_double_double_boolean_boolean">example</a>.
     */
    public synchronized void drawRect(double x, double y,
        double width, double height, boolean isFill, boolean isStroke)
        throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawRect(x, y, width, height, isFill, isStroke, Integer
            .toString(currentPage));
    }
    
    /**
     * Draws specified {@link java.awt.Rectangle} object on pages in
     * specified page range.
     * 
     * @param r
     *            {@link java.awt.Rectangle} object, which needs to be
     *            drawn
     * @param pageRange
     *            page range on whose pages the rectangle needs to be
     *            drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawRect_Rectangle_String">example</a>.
     */
    public void drawRect(Rectangle r, String pageRange)
        throws IOException, PdfException
    {
        drawRect(r.getX(), r.getY(), r.getWidth(), r.getHeight(),
            false, true, pageRange);
    }

    /**
     * Draws specified {@link java.awt.Rectangle} object on this
     * <code>PdfDocument</code>'s current page.
     * 
     * @param r
     *            {@link java.awt.Rectangle} object, which needs to 
     *            be drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawRect_Rectangle">example</a>.
     */
    public void drawRect(Rectangle r) throws IOException,
        PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawRect(r, Integer.toString(currentPage));
    }
    
    /**
     * Draws specified <code>PdfRect</code> object on pages in
     * specified page range.
     * 
     * @param r
     *            <code>PdfRect</code> object which needs to be
     *            drawn
     * @param pageRange
     *            page range on whose pages the rectangle needs to be
     *            drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawRect_PdfRect_String">example</a>.
     */
    public void drawRect(PdfRect r, String pageRange)
        throws IOException, PdfException
    {
        drawRect(r.getX(), r.getY(), r.width, r.height, false, true,
            pageRange);
    }

    /**
     * Draws rectangle <code>r</code> on this
     * <code>PdfDocument</code>'s current page.
     * 
     * @param r
     *             rectangle that needs to be drawn
     * @throws IOException
     *             if an I/O error occurs.
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawRect_PdfRect">example</a>.
     */
    public void drawRect(PdfRect r) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawRect(r, Integer.toString(currentPage));
    }
    
    /**
     * Draws a rectangle at position (<code>x</code>,
     * <code>y</code>) with specified <code>width</code> and
     * <code>height</code> on pages in specified page range.
     * 
     * @param x
     *            x-coordinate of top-left corner of the rectangle
     * @param y
     *            y-coordinate of top-left corner of the rectangle
     * @param width
     *            width of the rectangle
     * @param height
     *            height of the rectangle
     * @param pageRange
     *            page range on whose pages the rectangle needs to be
     *            drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawRect_double_double_double_double_String">example</a>.
     */
    public void drawRect(double x, double y, double width,
        double height, String pageRange) throws IOException,
        PdfException
    {
        drawRect(x, y, width, height, false, true, pageRange);
    }

    /**
     * Draws a rectangle at position (<code>x</code>,
     * <code>y</code>) with specified <code>width</code> and
     * <code>height</code> on this <code>PdfDocument</code>'s
     * current page.
     * 
     * @param x
     *            x-coordinate of top-left corner of the rectangle
     * @param y
     *            y-coordinate of top-left corner of the rectangle
     * @param width
     *            width of the rectangle
     * @param height
     *            height of the rectangle
     * @throws IOException
     *             if an I/O error occurs.
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawRect_double_double_double_double">example</a>.
     */
    public void drawRect(double x, double y, double width,
        double height) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawRect(x, y, width, height, Integer.toString(currentPage));
    }
    
    /**
     * Draws a square with specified pen and brush settings on 
     * pages in specified page range.
     * 
     * @param x
     *            x-coordinate of the top-left corner of the square
     * @param y
     *            y-coordinate of the top-left corner of the square
     * @param length
     *            length of a side of the square
     * @param isFill
     *            whether the square needs to be filled
     * @param isStroke
     *            whether the square needs to be stroked
     * @param pageRange
     *            page range on whose pages the square needs to be
     *            drawn
     * @throws IOException
     *             if an I/O error occurs.
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawSquare_double_double_double_boolean_boolean_String">example</a>.
     */
    public void drawSquare(double x, double y, double length,
        boolean isFill, boolean isStroke, String pageRange)
        throws IOException, PdfException
    {
        drawRect(x, y, length, length, isFill, isStroke, pageRange);
    }

    /**
     * Draws a square with specified brush and pen settings on this
     * <code>PdfDocument</code>'s current page.
     * 
     * @param x
     *            x-coordinate of the top-left corner of the square
     * @param y
     *            y-coordinate of the top-left corner of the square
     * @param length
     *            length of a side of the square
     * @param isFill
     *            whether the square needs to be filled
     * @param isStroke
     *            whether the square needs to be stroked
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawSquare_double_double_double_boolean_boolean">example</a>.
     */
    public void drawSquare(double x, double y, double length,
        boolean isFill, boolean isStroke) throws IOException,
        PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawSquare(x, y, length, isFill, isStroke, Integer
            .toString(currentPage));
    }
    
    /**
     * Draws a square on pages in specified page range.
     * 
     * @param x
     *            x-coordinate of the top-left corner of the square
     * @param y
     *            y-coordinate of the top-left corner of the square
     * @param length
     *            length of a side of the square
     * @param pageRange
     *            page range on whose pages the square needs to be
     *            drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawSquare_double_double_double_String">example</a>.
     */
    public void drawSquare(double x, double y, double length,
        String pageRange) throws IOException, PdfException
    {
        drawRect(x, y, length, length, false, true, pageRange);
    }

    /**
     * Draws a square on this <code>PdfDocument</code>'s current
     * page.
     * 
     * @param x
     *            x-coordinate of the top-left corner of the square
     * @param y
     *            y-coordinate of the top-left corner of the square
     * @param length
     *            length of a side of the square
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawSquare_double_double_double">example</a>.
     */
    public void drawSquare(double x, double y, double length)
        throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawSquare(x, y, length, Integer.toString(currentPage));
    }
    
    /**
     * Returns a <code>PdfPage</code> object specified by page number 
     * in this document.
     * 
     * @param pageNo
     *            page number in the document
     * @return 
     *            a <code>PdfPage</code> object
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#getPage">example</a>.
     */
    public synchronized PdfPage getPage(int pageNo)
        throws PdfException
    {
        return pageTree.getPage(pageNo);
    }
    
    /**
     * Returns number of pages in this <code>PdfDocument</code>.
     * 
     * @return number of pages
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#getPageCount">example</a>.
     */
    public synchronized int getPageCount()
    {
        return pageTree.getCount();
    }
    
    /**
     * Draws a Bézier curve with two control points on pages in
     * specified page range on this <code>PdfDocument</code>. The
     * curve starts at (<code>startX</code>, <code>startY</code>)
     * and ends at (<code>endX</code>, <code>endY</code>. Its
     * first control point is at (<code>ctrlX1</code>,
     * <code>ctrlY1</code>). Its second control point is at 
     * (<code>ctrlX2</code>, <code>ctrlY2</code>). 
     * 
     * @param startX
     *            x-coordinate of starting point of the curve
     * @param startY
     *            y-coordinate of starting point of the curve
     * @param ctrlX1
     *            x-coordinate of first control point of the curve
     * @param ctrlY1
     *            y-coordinate of first control point of the curve
     * @param ctrlX2
     *            x-coordinate of second control point of the curve
     * @param ctrlY2
     *            y-coordinate of second control point of the curve
     * @param endX
     *            x-coordinate of end point of the curve
     * @param endY
     *            y-coordinate of end point of the curve
     * @param isFill
     *            whether the curve needs to be filled
     * @param isStroke
     *            whether the curve needs to be stroked
     * @param pageRange
     *            page range on whose pages the curve needs to be 
     *            drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawBezierCurve_double_double_double_double_double_double_double_doubleboolean_boolean_String">example</a>.
     */
    public synchronized void drawBezierCurve(double startX, double startY,
        double ctrlX1, double ctrlY1, double ctrlX2, double ctrlY2,
        double endX, double endY, boolean isFill, boolean isStroke, 
        String pageRange) throws IOException, PdfException
    {
        int pageNo;
        PdfPage  p;
        int tempMu;
        PdfPen tempPen  = null;
        PdfBrush tempBrush = null;
        
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid PageRange");
        }
        Iterator i = v.iterator();
        while (i.hasNext())
        {
            pageNo = ((Integer) i.next()).intValue();
            p = pageTree.getPage(pageNo);
            tempMu = p.measurementUnit;
            p.measurementUnit = measurementUnit;
            
            tempPen = p.pen;
            p.pen = this.pen != null ? (PdfPen) this.pen.clone()
                : new PdfPen();
            
            tempBrush = p.brush;
            p.brush = this.brush != null ? (PdfBrush) this.brush.clone()
                : new PdfBrush();
            PdfPoint start = p.updatePageSettings(new PdfPoint(startX, startY));
            double tempStartX = PdfMeasurement.convertToPdfUnit(measurementUnit,
                (float) start.x);
            double tempStartY = PdfMeasurement.convertToPdfUnit(measurementUnit,
                (float) start.y);
            
            StringBuffer sb = new StringBuffer();
            
            sb.append(p.setPenBrush(isFill, isStroke));
            
            sb.append(PdfWriter.formatFloat( tempStartX)
                + PDF_SP + PdfWriter.formatFloat( (p.pageHeight - tempStartY)));
            sb.append(" m ");
            
            sb.append(p.drawInternalBezier(ctrlX1, ctrlY1, ctrlX2, ctrlY2,
                endX, endY));

            if ((isFill == true) && (isStroke == false))
            {
                sb.append("f ");
            }
            else if ((isFill == false) && (isStroke == true))
            {
                sb.append("h S ");
            }
            else if ((isFill == true) && (isStroke == true))
            {
                sb.append("h B ");
            }
            
            if(p.contentStream == null)
            {
                p.contentStream = new PdfByteOutputStream();
            }
            p.contentStream.write((sb.toString()).getBytes());
            p.currentX = endX;
            p.currentY = endY;
            p.measurementUnit = tempMu;
            p.brush = tempBrush;
            p.pen = tempPen;
        }
    }
    
    /**
     * Draws a Bézier curve with two control points on current page of
     * this <code>PdfDocument</code>. The curve starts at 
     * (<code>startX</code>, <code>startY</code>) and ends at 
     * (<code>endX</code>, <code>endY</code>. Its first control point 
     * is at (<code>ctrlX1</code>, <code>ctrlY1</code>). Its second 
     * control point is at (<code>ctrlX2</code>, <code>ctrlY2</code>).
     * 
     * @param startX
     *            x-coordinate of starting point of the curve
     * @param startY
     *            y-coordinate of starting point of the curve
     * @param ctrlX1
     *            x-coordinate of first control point of the curve
     * @param ctrlY1
     *            y-coordinate of first control point of the curve
     * @param ctrlX2
     *            x-coordinate of second control point of the curve
     * @param ctrlY2
     *            y-coordinate of second control point of the curve
     * @param endX
     *            x-coordinate of end point of the curve
     * @param endY
     *            y-coordinate of end point of the curve
     * @param isFill
     *            whether the curve needs to be filled
     * @param isStroke
     *            whether the curve needs to be stroked
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawBezierCurve_double_double_double_double_double_double_double_double_boolean_boolean">example</a>.
     */
    public synchronized void drawBezierCurve(double startX,
        double startY, double ctrlX1, double ctrlY1, double ctrlX2,
        double ctrlY2, double endX, double endY, boolean isFill,
        boolean isStroke) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawBezierCurve(startX, startY, ctrlX1, ctrlY1, ctrlX2,
            ctrlY2, endX, endY, isFill, isStroke, Integer
                .toString(currentPage));
    }
    
    /**
     * Draws a Bézier curve with a single control point on pages in
     * specified page range in this <code>PdfDocument</code>. The
     * curves starts at (<code>startX</code>, <code>startY</code>)
     * and ends at (<code>endX</code>, <code>endY</code> with
     * its control point being at (<code>ctrlX</code>,
     * <code>ctrlY</code>).
     * 
     * @param startX
     *            x-coordinate of starting point of the curve
     * @param startY
     *            y-coordinate of starting point of the curve
     * @param ctrlX
     *            x-coordinate of control point of the curve
     * @param ctrlY
     *            y-coordinate of control point of the curve
     * @param endX
     *            x-coordinate of end point of the curve
     * @param endY
     *            y-coordinate of end point of the Bézier curve
     * @param isFill
     *            whether the curve needs to be filled
     * @param isStroke
     *            whether the curve needs to be stroked
     * @param pageRange
     *            page range on whose pages the curve needs to be 
     *            drawn
     * @throws IOException
     *             if an I/O error occurs.
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawBezierCurve_double_double_double_double_double_double_boolean_boolean_String">example</a>.
     */
    public synchronized void drawBezierCurve(double startX, double startY,
        double ctrlX, double ctrlY, double endX, double endY,
        boolean isFill, boolean isStroke, String pageRange) 
    	throws IOException, PdfException
    {
        int pageNo;
        PdfPen tempPen  = null;
        PdfBrush tempBrush = null;
        int tempMu;
        
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid PageRange");
        }
        Iterator i = v.iterator();
        while (i.hasNext())
        {
            pageNo = ((Integer) i.next()).intValue();

            PdfPage p = pageTree.getPage(pageNo);
            tempMu = p.measurementUnit;
            p.measurementUnit = measurementUnit;
            tempPen = p.pen;
            p.pen = this.pen != null ? (PdfPen) this.pen.clone()
                : new PdfPen();
            
            tempBrush = p.brush;
            p.brush = this.brush != null ? (PdfBrush) this.brush.clone()
                : new PdfBrush();
            
            PdfPoint start = p.updatePageSettings(new PdfPoint(startX,
                startY));
            PdfPoint end = p.updatePageSettings(new PdfPoint(endX,
                endY));
            PdfPoint control = p.updatePageSettings(new PdfPoint(ctrlX,
                ctrlY));
            
            double tempStartX = PdfMeasurement.convertToPdfUnit(measurementUnit,
                (float) start.x);
            double tempStartY = PdfMeasurement.convertToPdfUnit(measurementUnit,
                (float) start.y);
            double tempEndX = PdfMeasurement.convertToPdfUnit(measurementUnit,
                (float) end.x);
            double tempEndY = PdfMeasurement.convertToPdfUnit(measurementUnit,
                (float) end.y);
            double tempCtrlX = PdfMeasurement.convertToPdfUnit(measurementUnit,
                (float) control.x);
            double tempCtrlY = PdfMeasurement.convertToPdfUnit(measurementUnit,
                (float) control.y);
            
            tempStartY = p.pageHeight - tempStartY;
            tempEndY = p.pageHeight - tempEndY;
            tempCtrlY = p.pageHeight - tempCtrlY;
            
            StringBuffer sb = new StringBuffer();
            sb.append(p.setPenBrush(isFill, isStroke));
            sb.append(PDF_SP + PdfWriter.formatFloat( tempStartX)
                + PDF_SP + PdfWriter.formatFloat( tempStartY));
            sb.append(PDF_SP + " m ");

            sb.append(PDF_SP + PdfWriter.formatFloat( tempCtrlX)
                + PDF_SP + PdfWriter.formatFloat( tempCtrlY));
            sb.append(PDF_SP + PdfWriter.formatFloat( tempEndX)
                + PDF_SP + PdfWriter.formatFloat( tempEndY));
            sb.append(" v ");

            if ((isFill == true) && (isStroke == false))
            {
                sb.append("f ");
            }
            else if ((isFill == false) && (isStroke == true))
            {
                sb.append("S ");
            }
            else if ((isFill == true) && (isStroke == true))
            {
                sb.append(PDF_SP + "h B ");
            }
            
            if(p.contentStream == null)
            {
                p.contentStream = new PdfByteOutputStream();
            }
            p.contentStream.write((sb.toString()).getBytes());
            p.currentX = tempEndX;
            p.currentY = tempEndY;
            p.measurementUnit = tempMu;
            p.brush = tempBrush;
            p.pen = tempPen;
        }
    }

    /**
     * Draws a Bézier curve with a single control point on current
     * page of this <code>PdfDocument</code>. The curves starts at 
     * (<code>startX</code>, <code>startY</code>) and ends at 
     * (<code>endX</code>, <code>endY</code> with its control point 
     * being at (<code>ctrlX</code>, <code>ctrlY</code>). 
     * 
     * @param startX
     *            x-coordinate of starting point of the curve
     * @param startY
     *            y-coordinate of starting point of the curve
     * @param ctrlX
     *            x-coordinate of control point of the curve
     * @param ctrlY
     *            y-coordinate of control point of the curve
     * @param endX
     *            x-coordinate of end point of the curve
     * @param endY
     *            y-coordinate of end point of the curve
     * @param isFill
     *            whether the curve needs to be filled
     * @param isStroke
     *            whether the curve needs to be stroked
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawBezierCurve_double_double_double_double_double_double_boolean_boolean">example</a>.
     */
    public synchronized void drawBezierCurve(double startX,
        double startY, double ctrlX, double ctrlY, double endX,
        double endY, boolean isFill, boolean isStroke)
        throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawBezierCurve(startX, startY, ctrlX, ctrlY, endX, endY,
            isFill, isStroke, Integer.toString(currentPage));
    }
    
    /**
     * Draws an ellipse on pages in specified page range on this
     * <code>PdfDocument</code>'s current page. Top-left corner of
     * ellipse's bounding box is specified by (<code>x1</code>,
     * <code>y1</code>). Bottom-right corner of ellipse's bounding
     * box is specified by (<code>x2</code>, <code>y2</code>).
     * 
     * @param x1
     *            x-coordinate of the top-left corner of the ellipse's
     *            bounding box
     * @param y1
     *            y-coordinate of the top-left corner of the ellipse's
     *            bounding box
     * @param x2
     *            x-coordinate of the bottom-right corner of the
     *            ellipse's bounding box
     * @param y2
     *            y-coordinate of the bottom-right corner of the
     *            ellipse's bounding box
     * @param isFill
     *            whether the ellipse needs to be filled
     * @param isStroke
     *            whether the ellipse needs to be stroked
     * @param pageRange
     *            page range on whose pages the ellipse needs to be
     *            drawn
     * @throws IOException
     *             if an I/O error occurs.
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawEllipse_double_double_double_double_boolean_boolean_String">example</a>.
     */
    public synchronized void drawEllipse(double x1, double y1, double x2,
        double y2, boolean isFill, boolean isStroke, String pageRange) 
	throws IOException, PdfException
    {
        int pageNo;
        PdfPage  p;
        int tempMu;
        PdfPen tempPen  = null;
        PdfBrush tempBrush = null;
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        
        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid PageRange");
        }
        Iterator i = v.iterator();
        while (i.hasNext())
        {
            pageNo = ((Integer) i.next()).intValue();
            p = pageTree.getPage(pageNo);
            tempMu = p.measurementUnit;
            p.measurementUnit = measurementUnit;
            
            tempPen = p.pen;
            p.pen = this.pen != null ? (PdfPen) this.pen.clone()
                : new PdfPen();
            
            tempBrush = p.brush;
            p.brush = this.brush != null ? (PdfBrush) this.brush.clone()
                : new PdfBrush();
            
            p.drawEllipse(x1, y1, x2, y2, isFill, isStroke); 
            p.measurementUnit = tempMu;
            p.brush = tempBrush;
            p.pen = tempPen;
        }
    }

    /**
     * Draws an ellipse on this <code>PdfDocument</code>'s current
     * page. Top-left corner of ellipse's bounding box is specified 
     * by (<code>x1</code>, <code>y1</code>). Bottom-right corner of 
     * ellipse's bounding box is specified by 
     * (<code>x2</code>, <code>y2</code>). 
     * 
     * @param x1
     *            x-coordinate of the top-left corner of the ellipse's
     *            bounding box
     * @param y1
     *            y-coordinate of the top-left corner of the ellipse's
     *            bounding box
     * @param x2
     *            x-coordinate of the bottom-right corner of the
     *            ellipse's bounding box
     * @param y2
     *            y-coordinate of the bottom-right corner of the
     *            ellipse's bounding box
     * @param isFill
     *            whether the ellipse needs to be filled
     * @param isStroke
     *            whether the ellipse needs to be stroked
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawEllipse_double_double_double_double_boolean_boolean">example</a>.
     */
    public synchronized void drawEllipse(double x1, double y1,
        double x2, double y2, boolean isFill, boolean isStroke)
        throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawEllipse(x1, y1, x2, y2, isFill, isStroke, Integer
            .toString(currentPage));
    }
    
    /**
	 * Draws a circle with specified radius on pages in specified 
	 * page range on this <code>PdfDocument</code>. 
	 * The circle's center is positioned at 
	 * (<code>x</code>, <code>y</code>). 
	 * 
	 * @param x
	 *            x-coordinate of the center of the circle
	 * @param y
	 *            y-coordinate of the center of the circle
	 * @param radius
	 *            radius of the circle
	 * @param isFill
	 *            whether the circle needs to be filled
	 * @param isStroke
	 *            whether the circle needs to be stroked
	 * @param pageRange
	 *            page range on whose pages the circle will be drawn
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawCircle_double_double_double_boolean_boolean_String">example</a>.
	 */
    public void drawCircle(double x, double y, double radius,
        boolean isFill, boolean isStroke, String pageRange) 
    	throws IOException, PdfException
    {
        drawEllipse(x - radius, y - radius, x + radius, y + radius,
            isFill, isStroke, pageRange);
    }
    
    /**
     * Draws a circle with specified radius on this
     * <code>PdfDocument</code>'s current page. The circle's center
     * is positioned at (<code>x</code>, <code>y</code>).
     * 
     * @param x
     *            x-coordinate of the center of the circle
     * @param y
     *            y-coordinate of the center of the circle
     * @param radius
     *            radius of the circle
     * @param isFill
     *            whether the circle needs to be filled
     * @param isStroke
     *            whether the circle needs to be stroked
     * @throws IOException
     *             if an I/O error occurs.
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawCircle_double_double_double_boolean_boolean">example</a>.
     */
    public void drawCircle(double x, double y, double radius,
        boolean isFill, boolean isStroke) throws IOException,
        PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawCircle(x, y, radius, isFill, isStroke, Integer
            .toString(currentPage));
    }
    
    /**
     * Draws a polyline on pages in specified page range. Arrays
     * <code>xPoints</code> and <code>yPoints</code> contain x-
     * and y-coordinates of certain specific points on the page.
     * <code>nPoints</code> represents the number of these points,
     * starting with the first, that need to be connected to draw 
     * the polyline.
     * 
     * @param xPoints
     *            array containing x-coordinates of certain specific
     *            points on pages in specified page rnage
     * @param yPoints
     *            array containing y-coordinates of certain specific
     *            points on pages in specified page rnage
     * @param nPoints
     *            number of points that need to be connected together,
     *            starting with the first of those points represented
     *            by <code>xPoints</code> and <code>yPoints</code>,
     *            to draw the polyline
     * @param pageRange
     *            page range on whose pages the polyline needs to be
     *            drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawPolygon_double[]_double[]_int_String">example</a>.
     */
    public synchronized void drawPolyline(double xPoints[],
        double yPoints[], int nPoints, String pageRange) 
    	throws IOException, PdfException
    {
        int pageNo;
        PdfPage  p;
        int tempMu;
        PdfPen tempPen  = null;
        PdfBrush tempBrush = null;
        
        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid PageRange");
        }
        Iterator i = v.iterator();
        while (i.hasNext())
        {
            pageNo = ((Integer) i.next()).intValue();

            double tempXPoints[] = new double[xPoints.length];
            double tempYPoints[] = new double[yPoints.length];
            for (int j =0; j < xPoints.length; j++)
            {
                tempXPoints[j] = xPoints[j];
            }
            for (int j =0; j < yPoints.length; j++)
            {
                tempYPoints[j] = yPoints[j];
            }
            
            p = pageTree.getPage(pageNo);
            tempMu = p.measurementUnit;
            p.measurementUnit = measurementUnit;
            
            tempPen = p.pen;
            p.pen = this.pen != null ? (PdfPen) this.pen.clone()
                : new PdfPen();
            
            tempBrush = p.brush;
            p.brush = this.brush != null ? (PdfBrush) this.brush.clone()
                : new PdfBrush();
            
            p.drawPolyline(tempXPoints, tempYPoints, nPoints);
            p.measurementUnit = tempMu;
            p.brush = tempBrush;
            p.pen = tempPen;
        }
    }
    
    /**
     * Draws a polyline on this <code>PdfDocument</code>'s current
     * page. Arrays <code>xPoints</code> and <code>yPoints</code>
     * contain x- and y-coordinates of certain specific points on the
     * page. <code>nPoints</code> represents the number of these
     * points, starting with the first, that need to be connected to
     * draw the polyline.
     * 
     * @param xPoints
     *            array containing x-coordinates of certain specific
     *            points on the page
     * @param yPoints
     *            array containing y-coordinates of certain specific
     *            points on the page
     * @param nPoints
     *            number of points that need to be connected together,
     *            starting with the first of those points represented 
     *            by <code>xPoints</code> and <code>yPoints</code>,
     *            to draw the polyline
     * @throws IOException
     *             if an I/O error occurs.
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawPolygon_double[]_double[]_int">example</a>.
     */
    public synchronized void drawPolyline(double xPoints[],
        double yPoints[], int nPoints) throws IOException,
        PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawPolyline(xPoints, yPoints, nPoints, Integer
            .toString(currentPage));
    }
    
    /**
     * Draws a polygon on pages in specified page range. Arrays 
     * <code>xPoints</code> and <code>yPoints</code> contain 
     * x- and y-coordinates of certain specific points on the 
     * page. <code>nPoints</code> represents the number of these 
     * points, starting with the first, that need to be connected 
     * to draw the polygon.
     * 
     * @param xPoints
     *            array containing x-coordinates of certain specific 
     *            points on pages in specified page range 
     * @param yPoints
     *            array containing y-coordinates of certain specific 
     *            points on pages in specified page range
     * @param nPoints
     *            number of points that need to be connected together,
     *            starting with the first of those points represented 
     *            by <code>xPoints</code> and <code>yPoints</code>,
     *            to draw the polygon
     * @param isFill
     *            whether the polygon nees to be filled
     * @param isStroke
     *            whether the polygon needs to be stroked
     * @param pageRange
     *            page range on whose pages the polygon needs to be
     *            drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawPolygon_double[]_double[]_int_boolean_boolean_String">example</a>.
     */
    public synchronized void drawPolygon(double xPoints[], double yPoints[],
        int nPoints, boolean isFill, boolean isStroke, String pageRange)
        throws IOException, PdfException
    {
        int pageNo;
        PdfPage  p;
        int tempMu;
        PdfPen tempPen  = null;
        PdfBrush tempBrush = null;
        
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid PageRange");
        }
        Iterator i = v.iterator();
        while (i.hasNext())
        {
            pageNo = ((Integer) i.next()).intValue();

            double tempXPoints[] = new double[xPoints.length];
            double tempYPoints[] = new double[yPoints.length];
            for (int j =0; j < xPoints.length; j++)
            {
                tempXPoints[j] = xPoints[j];
            }
            for (int j =0; j < yPoints.length; j++)
            {
                tempYPoints[j] = yPoints[j];
            }
            
            p = pageTree.getPage(pageNo);
            tempMu = p.measurementUnit;
            p.measurementUnit = measurementUnit;
            
            tempPen = p.pen;
            p.pen = this.pen != null ? (PdfPen) this.pen.clone()
                : new PdfPen();
            
            tempBrush = p.brush;
            p.brush = this.brush != null ? (PdfBrush) this.brush.clone()
                : new PdfBrush();
            
            p.drawPolygon(tempXPoints, tempYPoints, nPoints, isFill,
                isStroke);
            p.measurementUnit = tempMu;
            p.brush = tempBrush;
            p.pen = tempPen;
        }
    }
    
    /**
     * Draws a polygon on this <code>PdfDocument</code>'s current
     * page. Arrays <code>xPoints</code> and
     * <code>yPoints</code> contain x- and y-coordinates of 
     * certain specific points on the page. <code>nPoints</code> 
     * represents the number of these points, starting with the  
     * first, that need to be connected to draw the polygon.    
     * 
     * @param xPoints
     *            array containing x-coordinates of certain specific 
     *            points on the page
     * @param yPoints
     *            array containing y-coordinates of certain specific   
     *            points on the page
     * @param nPoints
     *            number of points that need to be connected together,
     *            starting with the first of those points represented 
     *            by <code>xPoints</code> and <code>yPoints</code>,
     *            to draw the polygon
     * @param isFill
     *            whether the polygon needs to be filled
     * @param isStroke
     *            whether the polygon needs to be stroked
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawPolygon_double[]_double[]_int_boolean_boolean">example</a>.
     */
    public synchronized void drawPolygon(double xPoints[],
        double yPoints[], int nPoints, boolean isFill,
        boolean isStroke) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawPolygon(xPoints, yPoints, nPoints, isFill, isStroke,
            Integer.toString(currentPage));
    }
    
    /**
     * Draws an arc on pages in specified page range. Rectangle
     * <code>rect</code> represents bounding box of an imaginary
     * circle that completes the arc. The arc begins at
     * <code>startAngle</code> degrees and spans for
     * <code>arcAngle</code> degrees. <code>startAngle</code> is
     * measured in anti-clockwise direction.
     * 
     * @param rect
     *            bounding box of the imaginary circle that completes
     *            the arc
     * @param startAngle
     *            (measured in anti-clockwise direction and expressed
     *            in degrees) angle from which the arc needs to begin
     * @param arcAngle
     *            (expressed in degrees) angle for which the arc needs
     *            to span
     * @param pageRange
     *            page page range on whose pages the arc needs to be
     *            drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawArc">example</a>.
     */
    public synchronized void drawArc(PdfRect rect, double startAngle,
        double arcAngle, String pageRange) throws IOException,
        PdfException
    {
        int pageNo;
        PdfPage  p;
        int tempMu;
        PdfPen tempPen  = null;
        PdfBrush tempBrush = null;
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        
        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid PageRange");
        }
        Iterator i = v.iterator();
        while (i.hasNext())
        {
            pageNo = ((Integer) i.next()).intValue();

            p = pageTree.getPage(pageNo);
            tempMu = p.measurementUnit;
            p.measurementUnit = measurementUnit;
            
            tempPen = p.pen;
            p.pen = this.pen != null ? (PdfPen) this.pen.clone()
                : new PdfPen();
            
            tempBrush = p.brush;
            p.brush = this.brush != null ? (PdfBrush) this.brush.clone()
                : new PdfBrush();
            
            p.drawArc(new PdfRect(rect.x, rect.y, rect.width,
                rect.height), startAngle, arcAngle);
            p.measurementUnit = tempMu;
            p.brush = tempBrush;
            p.pen = tempPen;
        }
    }
    
    
    /**
     * Draws an arc on the current page of this 
     * <code>PdfDocument</code>. Rectangle <code>rect</code> 
     * represents bounding box of an imaginary circle that completes 
     * the arc. The arc begins at <code>startAngle</code> degrees and 
     * spans for <code>arcAngle</code> degrees. 
     * <code>startAngle</code> is measured in anti-clockwise 
     * direction.
     * 
     * @param rect
     *            bounding box of the imaginary circle that completes
     *            the arc
     * @param startAngle
     *            (measured in anti-clockwise direction and expressed
     *            in degrees) angle from which the arc needs to begin
     * @param arcAngle
     *            (expressed in degrees) angle for which the arc needs
     *            to span
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawArc">example</a>.
     */
    public synchronized void drawArc(PdfRect rect, double startAngle,
        double arcAngle) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawArc(rect, startAngle,
            arcAngle, Integer.toString(currentPage));
    }
    
    /**
     * Draws a pie segment on pages in specified page range. The
     * position (<code>x</code>, <code>y</code>) represents the
     * top-left corner of the bounding box of an imaginary ellipse,
     * which the pie segment can neatly fit into.
     * 
     * @param x
     *            x-coordinate of top-left corner of the bounding box
     *            of the imaginary ellipse that contains the pie
     *            segment
     * @param y
     *            y-coordinate of top-left corner of the bounding box
     *            of the imaginary ellipse that contains the pie
     *            segment
     * @param width
     *            width of the bounding box of the imaginary ellipse
     *            that contains the pie segment
     * @param height
     *            height of the bounding box of the imaginary pie of
     *            which the pie segment can be an integral part
     * @param startAngle
     *            (measured in anti-clockwise direction and expressed
     *            in degrees) angle from which the pie segment needs
     *            to start
     * @param arcAngle
     *            (expressed in degrees) angle for which the pie
     *            segment needs to span
     * @param isFill
     *            whether the pie segment needs to be filled
     * @param isStroke
     *            whether the pie segment needs to be stroked
     * @param pageRange
     *            page page range on whose pages the pie segment needs
     *            to be drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawPie_double_double_double_double_double_double_boolean_boolean_String">example</a>.
     */
    public synchronized void drawPie(double x, double y,
        double width, double height, double startAngle,
        double arcAngle, boolean isFill, boolean isStroke,
        String pageRange) throws IOException, PdfException
    {
        int pageNo;
        PdfPage  p;
        int tempMu;
        PdfPen tempPen  = null;
        PdfBrush tempBrush = null;

        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid PageRange");
        }
        Iterator i = v.iterator();
        while (i.hasNext())
        {
            pageNo = ((Integer) i.next()).intValue();

            p = pageTree.getPage(pageNo);
            tempMu = p.measurementUnit;
            p.measurementUnit = measurementUnit;
            
            tempPen = p.pen;
            p.pen = this.pen != null ? (PdfPen) this.pen.clone()
                : new PdfPen();
            
            tempBrush = p.brush;
            p.brush = this.brush != null ? (PdfBrush) this.brush.clone()
                : new PdfBrush();
            
            p.drawPie(x, y, width, height, startAngle, arcAngle,
                isFill, isStroke);
            p.measurementUnit = tempMu;
            p.brush = tempBrush;
            p.pen = tempPen;
        }
    }
    
    /**
     * Draws a pie segment on this <code>PdfDocument</code>'s
     * current page. The position (<code>x</code>, <code>y</code>)
     * represents the top-left corner of the bounding box of an
     * imaginary ellipse, which the pie segment can neatly fit into.
     * 
     * @param x
     *            x-coordinate of top-left corner of the bounding box
     *            of the imaginary ellipse that contains the pie
     *            segment
     * @param y
     *            y-coordinate of top-left corner of the bounding box
     *            of the imaginary ellipse that contains the pie
     *            segment
     * @param width
     *            width of the bounding box of the imaginary ellipse
     *            that contains the pie segment
     * @param height
     *            height of the bounding box of the imaginary ellipse
     *            that contains the pie segment
     * @param startAngle
     *            (measured in anti-clockwise direction and expressed
     *            in degrees) angle from which the pie segment needs
     *            to start
     * @param arcAngle
     *            (expressed in degrees) angle for which the pie
     *            segment needs to span
     * @param isFill
     *            whether the pie segment needs to be filled
     * @param isStroke
     *            whether the pie segment needs to be stroked
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawPie_double_double_double_double_double_double_boolean_boolean">example</a>.
     */
    public synchronized void drawPie(int x, int y, int width,
        int height, double startAngle, double arcAngle,
        boolean isFill, boolean isStroke) throws IOException,
        PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawPie(x, y, width, height, startAngle, arcAngle, isFill,
            isStroke, Integer.toString(currentPage));
    }
    
    /**
     * Draws a rectangle with rounded corners on pages in specified
     * page range. The corners of the rectangle are actually arcs
     * whose dimensions are specified by <code>arcWidth</code> and
     * <code>arcHeight</code>. The dimensions of the whole
     * rectangle are specified by <code>width</code> and
     * <code>height</code>.
     * 
     * @param x
     *            x-coordinate of top-left corner of the rectangle
     * @param y
     *            y-coordinate of top-left corner of the rectangle
     * @param width
     *            width of the rectangle
     * @param height
     *            height of the rectangle
     * @param arcWidth
     *            width of the rounded corners
     * @param arcHeight
     *            height of the rounded corners
     * @param isFill
     *            whether the rectangle needs to be filled
     * @param isStroke
     *            whether the rectangle needs to be stroked
     * @param pageRange
     *            page range on whose pages the rectangle needs to be
     *            drawn
     * @throws IOException
     *             if an I/O error occurs.
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawRoundRect_double_double_double_double_double_double_boolean_boolean_String">example</a>.
     */
    public synchronized void drawRoundRect(double x, double y, 
        double width, double height, double arcWidth, double 
        arcHeight, boolean isFill, boolean isStroke, 
        String pageRange) throws IOException, PdfException
    {
        int pageNo;
        PdfPage  p;
        int tempMu;
        PdfPen tempPen  = null;
        PdfBrush tempBrush = null;

        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid PageRange");
        }
        Iterator i = v.iterator();
        while (i.hasNext())
        {
            pageNo = ((Integer) i.next()).intValue();
            p = pageTree.getPage(pageNo);
            tempMu = p.measurementUnit;
            p.measurementUnit = measurementUnit;
            
            tempPen = p.pen;
            p.pen = this.pen != null ? (PdfPen) this.pen.clone()
                : new PdfPen();
            
            tempBrush = p.brush;
            p.brush = this.brush != null ? (PdfBrush) this.brush.clone()
                : new PdfBrush();
            
            p.drawRoundRect( x, y, width, height, arcWidth, arcHeight,
                isFill, isStroke);
            p.measurementUnit = tempMu;
            p.brush = tempBrush;
            p.pen = tempPen;
        }
    }
   
    /**
     * Draws a rectangle with rounded corners on this
     * <code>PdfDocument</code>'s current page. The corners of the
     * rectangle are actually arcs whose dimensions are specified by
     * <code>arcWidth</code> and <code>arcHeight</code>. The
     * dimensions of the whole rectangle are specified by
     * <code>width</code> and <code>height</code>.
     * 
     * @param x
     *            x-coordinate of top-left corner of the rectangle
     * @param y
     *            y-coordinate of top-left corner of the rectangle
     * @param width
     *            width of the rectangle
     * @param height
     *            height of the rectangle
     * @param arcWidth
     *            width of the rounded corners
     * @param arcHeight
     *            height of the rounded corners
     * @param isFill
     *            whether the rectangle needs to be filled
     * @param isStroke
     *            whether the rectangle needs to be stroked
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawRoundRect_double_double_double_double_double_double_boolean_boolean">example</a>.
     */
    public synchronized void drawRoundRect(double x, double y,
        double width, double height, double arcWidth,
        double arcHeight, boolean isFill, boolean isStroke)
        throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawRoundRect(x, y, width, height, arcWidth, arcHeight,
            isFill, isStroke, Integer.toString(currentPage));
    }
    
    /**
	 * Draws image specified by its pathname at position 
	 * (<code>x</code>, <code>y</code>) on pages in specified page 
	 * range.
	 * 
	 * @param path
	 *            pathname of the image file
	 * @param x
	 *            x-coordinate of the position where the image needs 
     *            to be drawn
	 * @param y
	 *            y-coordinate of the position where the image needs 
     *            to be drawn
	 * @param pageRange
	 *            page range on whose pages the image needs to be 
     *            drawn 
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_String_double_double_String">example</a>.
	 */
    public void drawImage(String path, double x, double y,
        String pageRange)throws IOException, PdfException
    {
        if (path == null || path.equals(""))
        {
            throw new PdfException(
                "Illegal argument to drawImage (path == null " +
                "|| path.equals(\"\")).");
        }
        
        PdfImage img = PdfImage.create(path);
        double width = PdfMeasurement.
			convertToMeasurementUnit(measurementUnit, img.width);
        double height = PdfMeasurement.
			convertToMeasurementUnit(measurementUnit, img.height);
        drawImage(img, x, y, width, height, pageRange, img.rotation);
    }
    
    /**
	 * Draws image specified by its pathname at position 
	 * (<code>x</code>, <code>y</code>) on this 
	 * <code>PdfDocument</code>'s current page.
	 * 
	 * @param path
	 *            pathname of the image file
	 * @param x
	 *            x-coordinate of the position where the image 
	 *            needs to be drawn
	 * @param y
	 *            y-coordinate of the position where the image 
	 *            needs to be drawn
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @throws PdfException
	 *             if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_String_double_double">example</a>.
	 */
    public void drawImage(String path, double x, double y)
        throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawImage(path, x, y, Integer.toString(currentPage));
    }
    
    /**
	 * Draws image specified by its pathname at position 
	 * (<code>x</code>, <code>y</code>)with specified width and 
	 * height on pages in specified page range.
	 * 
	 * @param path
	 *            pathname of the image file
	 * @param x
	 *            x-coordinate of the position where the image needs 
	 *            to be drawn
	 * @param y
	 *            y-coordinate of the position where the image needs 
	 *            to be drawn
	 * @param width
	 *            width of the image
	 * @param height
	 *            height of the image
	 * @param pageRange
     *            page range on whose pages the image needs to be 
     *            drawn
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_String_double_double_double_double_String">example</a>.
	 */
    public void drawImage(String path, double x, double y,
        double width, double height, String pageRange)
    	throws IOException, PdfException
    {
        if (path == null || path.equals(""))
        {
            throw new PdfException(
                "Illegal argument to drawImage (path == null " +
                "|| path.equals(\"\")).");
        }
        
        PdfImage img = PdfImage.create(path);
        drawImage(img, x, y, width, height, pageRange, img.rotation);
    }
    
    /**
	 * Draws image specified by its pathname at position (x, y) 
	 * with specified width and height on this 
	 * <code>PdfDocument</code>'s current page.
	 * 
	 * @param path
	 *            pathname of the image file
	 * @param x
	 *            x-coordinate of the position where the image needs  
	 *            to be drawn
	 * @param y
	 *            y-coordinate of the position where the image needs 
	 *            to be drawn
	 * @param width
	 *            width of the image
	 * @param height
	 *            height of the image
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_String_double_double_double_double">example</a>.
	 */
    public void drawImage(String path, double x, double y,
        double width, double height) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawImage(path, x, y, width, height, Integer
            .toString(currentPage));
    }
  
    /**
     * Draws image specified by its pathname at specified point on
     * pages in specified page range.
     * 
     * @param path
     *            pathname of the image file
     * @param pt
     *           point where the image needs to be drawn 
     * @param pageRange
     *            page range on whose pages the image needs to be 
     *            drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_String_PdfPoint_String">example</a>.
     */
    public void drawImage(String path, PdfPoint pt, String pageRange)
        throws IOException, PdfException
    {
        drawImage(path, pt.x, pt.y, pageRange);
    }
    
    /**
     * Draws image specified by its pathname at specified point on
     * this <code>PdfDocument</code>'s current page.
     * 
     * @param path
     *            pathname of the image file
     * @param pt
     *            point where the image needs to be drawn 
     * @throws IOException
     *             if an I/O error occurs.
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_String_PdfPoint">example</a>.
     */
    public void drawImage(String path, PdfPoint pt)
        throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawImage(path, pt.x, pt.y, Integer.toString(currentPage));
    }
    
    /**
     * Draws image specified by its pathname at specified position
     * with specified width and height on pages in specified range.
     * 
     * @param path
     *            pathname of the image file
     * @param pt
     *            point where the image needs to be drawn 
     * @param width
     *            width of the image
     * @param height
     *            height of the image
     * @param pageRange
     *            page range on whose pages the image needs to be 
     *            drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_String_PdfPoint_double_double_String">example</a>.
     */
    public void drawImage(String path, PdfPoint pt, double width,
        double height, String pageRange) throws IOException,
        PdfException
    {
        drawImage(path, pt.x, pt.y, width, height, pageRange);
    }   
    
    /**
     * Draws image specified by its pathname at specified point with
     * specified width and height on this <code>PdfDocument</code>'s
     * current page.
     * 
     * @param path
     *            pathname of the image file
     * @param pt
     *            point where the image needs to be drawn 
     * @param width
     *            width of the image
     * @param height
     *            height of the image
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_String_PdfPoint_double_double">example</a>.
     */
    public void drawImage(String path, PdfPoint pt, double width,
        double height) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawImage(path, pt.x, pt.y, width, height, Integer.toString(currentPage));
    } 
    
    /**
	 * Draws image specified by its pathname inside specified 
	 * rectangle on pages  in specified page range.
	 * 
	 * @param path
	 *            pathname of the image file
	 * @param rect
	 *            rectangle inside which the image needs to be drawn 
	 * @param pageRange
     *            page range on whose pages the image needs to be 
     *            drawn
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_String_PdfRect_String">example</a>.
	 */
    public void drawImage(String path, PdfRect rect, String pageRange)
		throws IOException, PdfException
	{
        if (path == null || path.equals(""))
        {
            throw new PdfException(
                "Illegal argument to drawImage (path == null " +
                "|| path.equals(\"\")).");
        }
        
        PdfImage img = PdfImage.create(path);
        drawImage(img, rect.x, rect.y, rect.width, rect.height,
            pageRange, img.rotation);
	}
    
    /**
	 * Draws image specified by its pathname inside specified 
	 * rectangle on this <code>PdfDocument</code>'s current page.
	 * 
	 * @param path
	 *            pathname of the image file
	 * @param rect
	 *            rectangle inside which the image needs to be drawn             
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_String_PdfRect">example</a>.
	 */
    public void drawImage(String path, PdfRect rect)
        throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawImage(path, rect, Integer.toString(currentPage));
    }
    
    /**
	 * Draws specified image at position 
	 * (<code>x</code>, <code>y</code>) on pages in specified page 
	 * range.
	 * 
	 * @param img
	 *            image that needs to be drawn
	 * @param x
	 *            x-coordinate of the position where the image needs 
	 *            to be drawn
	 * @param y
	 *            y-coordinate of the position where the image needs 
	 *            to be drawn
	 * @param pageRange
     *            page range on whose pages the image needs to be 
     *            drawn
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_PdfImage_double_double_String">example</a>.
	 */
    public void drawImage(PdfImage img, double x, double y,
        String pageRange) throws IOException, PdfException
    {
        if (img == null )
        {
            throw new PdfException(
                "Illegal argument to drawImage, img == null ");
        }
        double width = img.scaledWidth <= 0 ? PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, img.width)
            : img.scaledWidth;
        double height = img.scaledHeight <= 0 ? PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, img.height)
            : img.scaledHeight;
        
        drawImage(img, x, y, width, height, pageRange, img.rotation);
    }
   
    /**
	 * Draws specified image at position 
	 * (<code>x</code>, <code>y</code>)
	 * on this <code>PdfDocument</code>'s current page.
	 * 
	 * @param img
	 *            image that needs to be drawn
	 * @param x
	 *            x-coordinate of the position where the image needs 
	 *            to be drawn
	 * @param y
	 *            y-coordinate of the position where the image needs 
	 *            to be drawn
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_PdfImage_double_double">example</a>.
	 */
    public void drawImage(PdfImage img, double x, double y)
        throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawImage(img, x, y, Integer.toString(currentPage));
    }
    
    /**
	 * Draws specified image inside specified rectangle on pages in 
	 * specified page range.
	 * 
	 * @param img
	 *            image that needs to be drawn
	 * @param rect
	 *            rectangle on which the image needs to be drawn
	 * @param pageRange
     *            page range on whose pages the image needs to be 
     *            drawn
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_PdfImage_PdfRect_String">example</a>.
	 */
    public void drawImage(PdfImage img, PdfRect rect, String pageRange)
        throws IOException, PdfException
    {
        drawImage(img, rect.x, rect.y, rect.width, rect.height,
            pageRange, img.rotation);
    }
   
    /**
	 * Draws specified image inside specified rectangle on this
	 * <code>PdfDocument</code>'s current page.
	 * 
	 * @param img
	 *            image that needs to be drawn
	 * @param rect
	 *            <code>PdfRectangle</code> object inside which the 
	 *            image needs to be drawn
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @throws PdfException
	 *             if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_PdfImage_PdfRect">example</a>.
	 */
    public void drawImage(PdfImage img, PdfRect rect)
        throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawImage(img, rect, Integer.toString(currentPage));
    }
 
    /**
	 * Draws specified image at position 
	 * (<code>x</code>, <code>y</code>) with specified height and 
	 * width on pages in specified page range.
	 * 
	 * @param img
	 *            image that needs to be drawn
	 * @param x
	 *            x-coordinate of the position where the image needs 
	 *            to be drawn
	 * @param y
	 *            y-coordinate of the position where the image needs 
	 *            to be drawn
	 * @param width
	 *            width of the image
	 * @param height
	 *            height of the image
	 * @param pageRange
     *            page range on whose pages the image needs to be 
     *            drawn
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_PdfImage_double_double_double_double_String">example</a>.
	 */
    public void drawImage(PdfImage img, double x,
        double y, double width, double height, String pageRange)
        throws IOException, PdfException
    {
        drawImage(img, x, y, width, height, pageRange, img.rotation);
    }

    protected synchronized void drawImage(PdfImage img, double x,
        double y, double width, double height, String pageRange,
        double rotation) throws IOException, PdfException
    {
        img.store();
        img.setRotation(rotation);
        int pageNo = 1;
        Vector v = getPages(pageRange);
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        if (v == null || v.size() == 0)
        {
            throw new PdfException("Invalid PageRange");
        }

        int siz = v.size();
        int pageNos[] = new int[siz];
        int k = 0;
        for(Iterator j = v.iterator(); j.hasNext();)
        {
            pageNos[k] = ((Integer)j.next()).intValue();
            k++;
        }
        
        if (img == null )
        {
            throw new PdfException(
                "Illegal argument to drawImage, img == null ");
        }
        img.scaledWidth = (float) width;
        img.scaledHeight = (float) height;
        
        boolean tempPaginate = autoPaginate;
        if(img.rotation != 0)
        {
            autoPaginate = false;
        }
        for(int ii = 0; ii < siz; ii++)
        {
            pageNo = pageNos[ii];
            PdfPage p = (PdfPage) pageTree.getPage(pageNo);
            
            int tempMu = p.measurementUnit;
            p.measurementUnit = measurementUnit;

            double tempX = x + PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, p.pageLeftMargin + p.pageCropLeft);
            double tempY = y + PdfMeasurement.convertToMeasurementUnit(
                    measurementUnit, p.pageTopMargin + p.pageCropTop
                        + p.pageHeaderHeight);

            while (tempY > PdfMeasurement.convertToMeasurementUnit(
              measurementUnit, p.pageHeight - (p.pageTopMargin + 
              p.pageCropTop  + p.pageHeaderHeight + p.pageBottomMargin
              + p.pageCropBottom + p.pageFooterHeight)))
            {
                p = new PdfPage(p.pageWidth, p.pageHeight,
                    p.pageHeaderHeight, p.pageFooterHeight,
                    p.pageLeftMargin, p.pageTopMargin,
                    p.pageRightMargin, p.pageBottomMargin,
                    PdfMeasurement.MU_POINTS);
                tempY -= p.pageHeight - (p.pageTopMargin + p.pageCropTop
                        + p.pageHeaderHeight + p.pageBottomMargin
                        + p.pageCropBottom + p.pageFooterHeight);
                //currentPage++;
            }
            
            PdfRect clipRect = new PdfRect(tempX, tempY, img.scaledWidth,
                img.scaledHeight);
            double imgWidth = 0;
            double imgHeight = 0;
            if(img.scaledWidth  > PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, p.pageWidth - (p.pageLeftMargin 
                + p.pageCropLeft + p.pageRightMargin + p.pageCropRight)) - x)
            {
                imgWidth = PdfMeasurement.convertToMeasurementUnit(
                    measurementUnit, p.pageWidth - (p.pageLeftMargin +
                    p.pageCropLeft + p.pageRightMargin + p.pageCropRight)) - x;
                clipRect.width = imgWidth;
            }
            else
            {
                imgWidth = img.scaledWidth; 
                clipRect.width = imgWidth;
            }
            
            imgHeight = PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, p.pageHeight - (p.pageTopMargin 
                + p.pageCropTop + p.pageHeaderHeight + p.pageBottomMargin
                + p.pageCropBottom + p.pageFooterHeight)) - y;
            clipRect.height = imgHeight;
            
            double heightImg = img.scaledHeight;
            if(autoPaginate)
            {
                p.drawImage(img, new PdfRect(tempX, tempY,
                    img.scaledWidth, img.scaledHeight), new PdfRect(
                    clipRect.x, clipRect.y, clipRect.width,
                    clipRect.height), this.measurementUnit);
                
                heightImg -=  clipRect.height;
                clipRect.y = PdfMeasurement.convertToMeasurementUnit(
                    measurementUnit, p.pageTopMargin + p.pageCropTop
                        + p.pageHeaderHeight);
                tempY = clipRect.y - clipRect.height;
                
                while(heightImg > 0)
                {
                    p.measurementUnit = tempMu;
                    
                    if(pageNo < pageTree.getCount())
                    {
                        p = (PdfPage) pageTree.getPage(pageNo +1);
                    }
                    else
                    {    
                        for(int m = ii; m < siz; m++)
                        {
                            pageNos[m] = pageNos[m] + 1;
                        }
                        p = new PdfPage(p.pageWidth, p.pageHeight,
                            p.pageHeaderHeight, p.pageFooterHeight,
                            p.pageLeftMargin, p.pageTopMargin,
                            p.pageRightMargin, p.pageBottomMargin,
                            PdfMeasurement.MU_POINTS);
                    
                        this.pageTree.insert(pageNo + 1, p);
                        //currentPage = pageNo + 1;
                        pageNo++;
                    }
                    tempMu = p.measurementUnit;
                    p.measurementUnit = measurementUnit;
                    
                    clipRect.height = heightImg;
                    
                    if(clipRect.height > PdfMeasurement.
                        convertToMeasurementUnit(measurementUnit, p.pageHeight
                        - (p.pageTopMargin + p.pageCropTop  + p.pageHeaderHeight + 
                        p.pageBottomMargin + p.pageCropBottom + p.pageFooterHeight)))
                    {
                        clipRect.height = PdfMeasurement.convertToMeasurementUnit(
                                measurementUnit, p.pageHeight - (p.pageTopMargin
                                + p.pageCropTop + p.pageHeaderHeight 
                                + p.pageBottomMargin + p.pageCropBottom 
                                + p.pageFooterHeight));
                    }
                    p.drawImage(img, new PdfRect(tempX, tempY,
                        img.scaledWidth, img.scaledHeight),
                        new PdfRect(clipRect.x, clipRect.y,
                            clipRect.width, clipRect.height),
                        this.measurementUnit);
                    
                    heightImg -= clipRect.height;
                    tempY = tempY - clipRect.height;
                }
            }
            else
            {
                if (img.rotation != 0)
                {
                    p.drawImage(img, new PdfRect(tempX, tempY,
                        img.scaledWidth, img.scaledHeight),
                        null, measurementUnit);
                }
                else
                {
                    p.drawImage(img, new PdfRect(tempX, tempY,
                        img.scaledWidth, img.scaledHeight), clipRect,
                        measurementUnit);
                }
            }
            p.measurementUnit = tempMu;
        }
        currentPage = pageNos[pageNos.length -1];
        autoPaginate = tempPaginate;
        img.reStore();
    }

    /**
	 * Draws specified image at position 
	 * (<code>x</code>, <code>y</code>) with specified height and 
	 * width on this <code>PdfDocument</code>'s current page.
	 * 
	 * @param img
	 *            image that needs to be drawn
	 * @param x
	 *            x-coordinate of the position where the image needs 
	 *            to be drawn
	 * @param y
	 *            y-coordinate of the position where the image needs 
	 *            to be drawn
	 * @param width
	 *            width of the image
	 * @param height
	 *            height of the image
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_PdfImage_double_double_double_double">example</a>.
	 */
    public void drawImage(PdfImage img, double x, double y,
        double width, double height) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawImage(img, x, y, width, height, Integer
            .toString(currentPage), img.rotation);
    }
    
    /**
	 * Draws specified image at specified point on pages in specified 
	 * page range.
	 * 
	 * @param img
	 *            image that needs to be drawn
	 * @param pt
	 *            point where the image needs to be drawn 
	 * @param pageRange
     *            page range on whose pages the image needs to be 
     *            drawn
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_PdfImage_PdfPoint_String">example</a>.
	 */
    public void drawImage(PdfImage img, PdfPoint pt, String pageRange)
        throws IOException, PdfException
    {
        drawImage(img, pt.x, pt.y, pageRange);
    }
    
    /**
	 * Draws specified image at specified point on this 
	 * <code>PdfDocument</code>'s current page.
	 * 
	 * @param img
	 *            image that needs to be drawn
	 * @param pt
	 *            point where the image needs to be drawn 
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_PdfImage_PdfPoint">example</a>.
	 */
    public void drawImage(PdfImage img, PdfPoint pt)
        throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawImage(img, pt.x, pt.y, Integer.toString(currentPage));
    }
    
    /**
	 * Draws specified image at specified point with specified width 
	 * and height on pages in the specified page range.
	 * 
	 * @param img
	 *            image that needs to be drawn
	 * @param pt
	 *            point where the image needs to be drawn  
	 * @param width
	 *            width of the image
	 * @param height
	 *            width of the image
	 * @param pageRange
     *            page range on whose pages the image needs to be 
     *            drawn
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_PdfImage_PdfPoint_double_double_String">example</a>.
	 */
    public void drawImage(PdfImage img, PdfPoint pt, double width,
        double height, String pageRange) throws IOException,
        PdfException
    {
        drawImage(img, pt.x, pt.y, width, height, pageRange, img.rotation);
    }

    /**
	 * Draws specified image at specified point with specified width 
	 * and height on this <code>PdfDocument</code>'s current page.
	 * 
	 * @param img
	 *            image that needs to be drawn
	 * @param pt
	 *            point where the image needs to be drawn 
	 * @param width
	 *            width of the image
	 * @param height
	 *            height of the image
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_PdfImage_PdfPoint_double_double">example</a>.
	 */
    public void drawImage(PdfImage img, PdfPoint pt, double width,
        double height) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawImage(img, pt.x, pt.y, width, height, Integer
            .toString(currentPage), img.rotation);
    }
     
    /**
     * Draws image specified by its pathname rotated by
     * <code>rotation</code> degrees at position (<code>x</code>,
     * <code>y</code>)on pages in specified range.
     * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param path
     *            pathname of the image file
     * @param x
     *            x-coordinate of the position where the image needs
     *            to be drawn
     * @param y
     *            y-coordinate of the position where the image needs
     *            to be drawn
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box 
     * @param pageRange
     *            page range on whose pages the image needs to be
     *            drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_String_double_double_double_String">example</a>.
     */
    public void drawImage(String path, double x, double y,
        double rotation, String pageRange) throws IOException,
        PdfException
    {
        if (path == null || path.equals(""))
        {
            throw new PdfException(
                "Illegal argument to drawImage (path == null " +
                "|| path.equals(\"\")).");
        }
        
        PdfImage img = PdfImage.create(path);
        double width = PdfMeasurement.
            convertToMeasurementUnit(measurementUnit, img.width);
        double height = PdfMeasurement.
            convertToMeasurementUnit(measurementUnit, img.height);
        drawImage(img, x, y, width, height, pageRange, rotation);
    }
    
    /**
	 * Draws image specified by its pathname rotated at 
	 * <code>rotation</code> degrees at position 
	 * (<code>x</code>, <code>y</code>) on this 
	 * <code>PdfDocument</code>'s current page.
	 * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
	 * @param path
	 *            pathname of the image file
	 * @param x
	 *            x-coordinate of the position where the image needs
     *            to be drawn
	 * @param y
	 *            y-coordinate of the position where the image needs 
     *            to be drawn
	 * @param rotation
	 *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box 
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_String_double_double_double">example</a>.
	 */
    public void drawImage(String path, double x, double y,
        double rotation) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawImage(path, x, y, rotation, Integer.toString(currentPage));
    }

    /**
     * Draws image specified by its pathname rotated by
     * <code>rotation</code> degrees at specified point on pages in
     * specified range.
     * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param path
     *            pathname of the image file
     * @param pt
     *            point where the image needs to be drawn 
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
     * @param pageRange
     *            page range on whose pages the image needs to be 
     *            drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_String_PdfPoint_double_String">example</a>.
     */
    public void drawImage(String path, PdfPoint pt, double rotation,
        String pageRange) throws IOException, PdfException
    {
        drawImage(path, pt.x, pt.y, rotation, pageRange);
    }

    /**
     * Draws image specified by its pathname rotated by
     * <code>rotation</code> degrees at specified point on this
     * <code>PdfDocument</code>'s current page.
     * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param path
     *            pathname of the image file
     * @param pt
     *            point where the image needs to be drawn 
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_String_PdfPoint_double">example</a>.
     */
    public void drawImage(String path, PdfPoint pt, double rotation)
        throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawImage(path, pt.x, pt.y, rotation, Integer.toString(currentPage));
    }

    /**
	 * Draws image specified by its pathname rotated by 
	 * <code>rotation</code> degrees at position 
	 * (<code>x</code>, <code>y</code>) with specified width and 
	 * height on pages in specified page range.
	 * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
	 * @param path
	 *            pathname of the image file
	 * @param x
	 *            x-coordinate of the position where the image needs
     *            to be drawn
	 * @param y
	 *            y-coordinate of the position where the image needs 
	 *            to be drawn
	 * @param width
	 *            width of the image
	 * @param height
	 *            height of the image
	 * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
	 * @param pageRange
     *            page range on whose pages the image needs to be 
     *            drawn
	 * @throws IOException if an I/O error occurs. 
	 * @throws PdfException if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_String_double_double_double_double_double_String">example</a>.
	 */
    public void drawImage(String path, double x, double y,
        double width, double height, double rotation, String pageRange)
        throws IOException, PdfException
    {
        if (path == null || path.equals(""))
        {
            throw new PdfException(
                "Illegal argument to drawImage (path == null " +
                "|| path.equals(\"\")).");
        }
        
        PdfImage img = PdfImage.create(path);
        drawImage(img, x, y, width, height, pageRange, rotation);
    }
    
    /**
	 * Draws image specified by its pathname rotated by 
	 * <code>rotation</code> degrees at position 
	 * (<code>x</code>, <code>y</code>) with specified width and 
	 * height on this <code>PdfDocument</code>'s current page.
	 * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
	 * @param path
	 *            pathname of the image file
	 * @param x
	 *            x-coordinate of the position where the image needs
     *            to be drawn
	 * @param y
	 *            y-coordinate of the position where the image needs
     *            to be drawn
	 * @param width
	 *            width of the image
	 * @param height
	 *            height of the image
	 * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @throws PdfException
	 *             if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_String_double_double_double_double_double">example</a>.
	 */
    public void drawImage(String path, double x, double y,
        double width, double height, double rotation)
        throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawImage(path, x, y, width, height, rotation, Integer
            .toString(currentPage));
    }

    /**
     * Draws image specified by its pathname rotated by
     * <code>rotation</code> degrees at specified position with
     * specified width and height on pages in specified page range.
     * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param path
     *            pathname of the image file
     * @param pt
     *            point where the image needs to be drawn 
     * @param width
     *            width of the image
     * @param height
     *            height of the image
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
     * @param pageRange
     *            page range on whose pages the image needs to be 
     *            drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_String_PdfPoint_double_double_double_String">example</a>.
     */
    public void drawImage(String path, PdfPoint pt, double width,
        double height, double rotation, String pageRange)
        throws IOException, PdfException
    {
        drawImage(path, pt.x, pt.y, width, height, rotation, pageRange);
    }   
    
    /**
     * Draws image specified by its pathname rotated by
     * <code>rotation</code> degrees at specified point with
     * specified width and height on this <code>PdfDocument</code>'s
     * current page.
     * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param path
     *            pathname of the image file
     * @param pt
     *            point where the image needs to be drawn 
     * @param width
     *            width of the image
     * @param height
     *            height of the image
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
     * @throws IOException
     *             if an I/O error occurs.
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_String_PdfPoint_double_double_double">example</a>.
     */
    public void drawImage(String path, PdfPoint pt, double width,
        double height, double rotation) throws IOException,
        PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawImage(path, pt.x, pt.y, width, height, rotation, Integer
            .toString(currentPage));
    } 

    /**
	 * Draws image specified by its pathname rotated by 
	 * <code>rotation</code> degrees inside specified rectangle on 
	 * pages in specified page range.
	 * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
	 * @param path
	 *            pathname of the image file
	 * @param rect
	 *            rectangle inside which the image needs to be drawn 
	 * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
	 * @param pageRange
     *            page range on whose pages the image needs to be 
     *            drawn
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_String_PdfRect_double_String">example</a>.
	 */
    public void drawImage(String path, PdfRect rect, double rotation,
        String pageRange) throws IOException, PdfException
    {
        if (path == null || path.equals(""))
        {
            throw new PdfException(
                "Illegal argument to drawImage (path == null "
                    + "|| path.equals(\"\")).");
        }

        PdfImage img = PdfImage.create(path);
        drawImage(img, rect.x, rect.y, rect.width, rect.height,
            pageRange, rotation);
    }

    /**
	 * Draws image specified by its pathname rotated by 
	 * <code>rotation</code> degrees inside specified rectangle on 
	 * this <code>PdfDocument</code>'s current page.
	 * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
	 * @param path
	 *            pathname of the image file
	 * @param rect
	 *            rectangle inside which the image needs to be drawn 
	 * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_String_PdfRect_double">example</a>.
	 */
    public void drawImage(String path, PdfRect rect, double rotation)
        throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawImage(path, rect, rotation, Integer.toString(currentPage));
    }

    /**
	 * Draws specified image rotated by <code>rotation</code> degrees 
	 * at position (<code>x</code>, <code>y</code>) on pages in 
	 * specified page range.
	 * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
	 * @param img
	 *            image that needs to be drawn
	 * @param x
	 *            x-coordinate of the position where the image needs 
	 *            to be drawn
	 * @param y
	 *            y-coordinate of the position where the image needs 
	 *            to be drawn
	 * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
	 * @param pageRange
     *            page range on whose pages the image needs to be 
     *            drawn
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_PdfImage_double_double_double_String">example</a>.
	 */
    public void drawImage(PdfImage img, double x, double y,
        double rotation, String pageRange) throws IOException,
        PdfException
    {
        if (img == null)
        {
            throw new PdfException(
                "Illegal argument to drawImage, img == null ");
        }
        double width = img.scaledWidth <= 0 ? PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, img.width)
            : img.scaledWidth;
        double height = img.scaledHeight <= 0 ? PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, img.height)
            : img.scaledHeight;
        drawImage(img, x, y, width, height, pageRange, rotation);
    }

    /**
	 * Draws specified image rotated by <code>rotation</code> 
	 * degrees at position (<code>x</code>, <code>y</code>) on this
	 * <code>PdfDocument</code>'s current page.
	 * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
	 * @param img
	 *            image that needs to be drawn
	 * @param x
	 *            x-coordinate of the position where the image needs 
	 *            to be drawn
	 * @param y
	 *            y-coordinate of the position where the image needs 
	 *            to be drawn
	 * @param rotation
	 *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_PdfImage_double_double_double">example</a>.
	 */
    public void drawImage(PdfImage img, double x, double y,
        double rotation) throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        double width = img.scaledWidth <= 0 ? PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, img.width)
            : img.scaledWidth;
        double height = img.scaledHeight <= 0 ? PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, img.height)
            : img.scaledHeight;
        
        drawImage(img, x, y, width, height, Integer
            .toString(currentPage), rotation);
    }
    
    /**
	 * Draws specified image rotated by <code>rotation</code> degrees
	 * inside specified rectangle on pages in specified range.
	 * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
	 * @param img
	 *            image that needs to be drawn
	 * @param rect
	 *            <code>PdfRect</code> object of the rectangle inside 
	 *            which the image is to be drawn
	 * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
	 * @param pageRange
	 *            page range on whose pages the image is to be 
	 *            drawn
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_PdfImage_PdfRect_double_String">example</a>.
	 */
    public void drawImage(PdfImage img, PdfRect rect,
        double rotation, String pageRange) throws IOException,
        PdfException
    {
        drawImage(img, rect.x, rect.y, rect.width, rect.height,
            pageRange, rotation);
    }

    /**
	 * Draws specified image rotated by <code>rotation</code> degrees
	 * inside specified rectangle on this <code>PdfDocument</code>'s
	 * current page.
	 * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
	 * @param img
	 *            image that needs to be drawn 
	 * @param rect
	 *            rectangle on which the image needs to be drawn
	 * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @throws PdfException
	 *             if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_PdfImage_PdfRect_double">example</a>.
	 */
    public void drawImage(PdfImage img, PdfRect rect, double rotation)
        throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawImage(img, rect.x, rect.y, rect.width, rect.height,
            Integer.toString(currentPage), rotation);
    }

    /**
	 * Draws specified image rotated by <code>rotation</code> degrees
	 * at position (<code>x</code>, <code>y</code>) with specified 
	 * height and width on pages in specified page range.
	 * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
	 * @param img
	 *            image that needs to be drawn
	 * @param x
	 *            x-coordinate of the position where the image needs 
	 *            to be drawn
	 * @param y
	 *            y-coordinate of the position where the image needs 
	 *            to be drawn
	 * @param width
	 *            width of the image
	 * @param height
	 *            height of the image
	 * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
	 * @param pageRange
     *            page range on whose pages the image needs to be 
     *            drawn
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_PdfImage_double_double_double_double_double_string">example</a>.
	 */
    public void drawImage(PdfImage img, double x, double y,
        double width, double height, double rotation, String pageRange)
        throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawImage(img, x, y, width, height, pageRange, rotation);
    }

    /**
	 * Draws specified image rotated by <code>rotation</code> degrees
	 * at position (<code>x</code>, <code>y</code>) with specified 
	 * height and width on this <code>PdfDocument</code>'s current 
	 * page.
	 * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
	 * @param img
	 *            image that needs to be drawn
	 * @param x
	 *            x-coordinate of the position where the image needs 
	 *            to be drawn
	 * @param y
	 *            y-coordinate of the position where the image needs 
	 *            to be drawn
	 * @param width
	 *            width of the image
	 * @param height
	 *            height of the image
	 * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_PdfImage_double_double_double_double_double">example</a>.
	 */
    public void drawImage(PdfImage img, double x, double y,
        double width, double height, double rotation)
        throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawImage(img, x, y, width, height, Integer
            .toString(currentPage), rotation);
    }
    
    /**
	 * Draws specified image rotated by <code>rotation</code> degrees 
	 * at specified point on pages in the specified page range.
	 * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
	 * @param img
	 *            image that needs to be drawn
	 * @param pt
	 *            point where the image needs to be drawn 
	 * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
	 * @param pageRange
     *            page range on whose pages the image needs to be 
     *            drawn
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_PdfImage_PdfPoint_double_String">example</a>.
	 */
    public void drawImage(PdfImage img, PdfPoint pt, double rotation,
        String pageRange) throws IOException, PdfException
    {
        double width = img.scaledWidth <= 0 ? PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, img.width)
            : img.scaledWidth;
        double height = img.scaledHeight <= 0 ? PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, img.height)
            : img.scaledHeight;
        
        drawImage(img, pt.x, pt.y, width, height, pageRange, rotation);
    }

    /**
	 * Draws specified image rotated by <code>rotation</code> degrees
	 * at specified point on this <code>PdfDocument</code>'s current 
	 * page.
	 * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
	 * @param img
	 *            image that needs to be drawn
	 * @param pt
	 *            point where the image needs to be drawn 
	 * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_PdfImage_PdfPoint_double">example</a>.
	 */
    public void drawImage(PdfImage img, PdfPoint pt, double rotation)
        throws IOException, PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        double width = img.scaledWidth <= 0 ? PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, img.width)
            : img.scaledWidth;
        double height = img.scaledHeight <= 0 ? PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, img.height)
            : img.scaledHeight;
        
        drawImage(img, pt.x, pt.y, width, height, Integer
            .toString(currentPage), rotation);
    }

    /**
	 * Draws specified image rotated by <code>rotation</code> degrees 
	 * at specified point with specified width and height on pages in 
	 * the specified page range.
	 * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
	 * @param img
	 *            image that needs to be drawn
	 * @param pt
	 *            point where the image needs to be drawn 
	 * @param width
	 *            width of the image
	 * @param height
	 *            height of the image
	 * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
	 * @param pageRange
     *            page range on whose pages the image needs to be 
     *            drawn
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_PdfImage_PdfPoint_double_double_double_String">example</a>.
	 */
    public void drawImage(PdfImage img, PdfPoint pt, double width,
        double height, double rotation, String pageRange)
        throws IOException, PdfException
    {
        drawImage(img, pt.x, pt.y, width, height, pageRange, rotation);
    }

    /**
     * Draws specified image rotated by <code>rotation</code>
     * degrees at specified point on this <code>PdfDocument</code>'s
     * current page.
     * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param img
     *            image that needs to be drawn
     * @param pt
     *            point where the image needs to be drawn 
     * @param width
     *            width of the image
     * @param height
     *            height of the image
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
     * @throws IOException
     *             if an I/O error occurs.
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#drawImage_PdfImage_PdfPoint_double_double_double">example</a>.
     */
    public void drawImage(PdfImage img, PdfPoint pt, double width,
        double height, double rotation) throws IOException,
        PdfException
    {
        if (this.pageTree == null)
        {
            add(new PdfPage());
        }
        drawImage(img, pt.x, pt.y, width, height, Integer
            .toString(currentPage), rotation);
    }
    
    /**
     * Specifies default width for this <code>PdfDocument</code>'s
     * pen.
     * 
     * @param width
     *            default width for the <code>PdfDocument</code>'s
     *            pen
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#setPenWidth">example</a>.
     */
    public synchronized void setPenWidth(double width)
    {
        if (pen == null)
        {
            pen = new PdfPen();
        }
        pen.width = width;
    }
    
    /**
     * Specifies default color for this <code>PdfDocument</code>'s
     * pen.
     * 
     * @param color
     *            default color for the <code>PdfDocument</code>'s
     *            pen
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#setPenColor">example</a>.
     */
    public synchronized void setPenColor(Color color)
    {
        if (pen == null)
        {
            pen = new PdfPen();
        }
        pen.strokeColor = color;
    }

    /**
     * Specifies length of dashes in default 
     * <a href="{@docRoot}/doc-files/glossary.htm#dash_pattern" target="_GnosticeGlossaryWindow" 
     * >dash pattern</a> 
     * of this <code>PdfDocument</code>'s pen.
     * 
     * @param length
     *            length of dashes in the default dash pattern            
     * @since 1.0
     * @see #setPenDashGap(double)
     * @see #setPenDashPhase(double)
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#setPenDashLength">example</a>.
     */
    public synchronized void setPenDashLength(double length)
    {
        if (pen == null)
        {
            pen = new PdfPen();
        }
        pen.dashLength = length;
    }

    /**
     * Specifies length of gaps in default
     * <a href="{@docRoot}/doc-files/glossary.htm#dash_pattern" target="_GnosticeGlossaryWindow"  
     * >dash pattern</a> 
     * of this <code>PdfDocument</code>'s pen.
     * 
     * @param gap
     *            length of gaps in the default dash pattern
     * @since 1.0
     * @see #setPenDashLength(double)
     * @see #setPenDashPhase(double)
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#setPenDashGap">example</a>.
     */
    public synchronized void setPenDashGap(double gap)
    {
        if (pen == null)
        {
            pen = new PdfPen();
        }
        pen.dashGap = gap;
    }

    /**
     * Specifies length of 
     * <a href="{@docRoot}/doc-files/glossary.htm#phase" target="_GnosticeGlossaryWindow" 
     * >phase</a> 
     * of default 
     * <a href="{@docRoot}/doc-files/glossary.htm#dash_pattern" target="_GnosticeGlossaryWindow" 
     * >dash pattern</a> 
     * of this <code>PdfDocument</code>'s pen.
     * 
     * @param phase
     *            length of phase of the default dash pattern
     * @since 1.0
     * @see #setPenDashGap(double)
     * @see #setPenDashLength(double)
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#setPenDashPhase">example</a>.
     */
    public synchronized void setPenDashPhase(double phase)
    {
        if (pen == null)
        {
            pen = new PdfPen();
        }
        pen.dashPhase = phase;
    }

    /**
     * Specifies default shape of endpoints of paths in this
     * <code>PdfDocument</code>.
     * 
     * @param capStyle
     *            constant specifying the default shape
     * @since 1.0
     * @see PdfPen
     * @see #setPenJoinStyle(int)
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#setPenCapStyle">example</a>.
     */
    public synchronized void setPenCapStyle(int capStyle)
    {
        if (pen == null)
        {
            pen = new PdfPen();
        }
        pen.capStyle = capStyle;
    }

    /**
     * Specifies default shape of joints of 
     * <a href="{@docRoot}/doc-files/glossary.htm#path" target="_GnosticeGlossaryWindow" 
     * >paths</a> 
     * that connect at an angle for this <code>PdfDocument</code>'s 
     * pen.
     * 
     * @param joinStyle
     *            constant specifying the default shape
     * @since 1.0
     * @see PdfPen
     * @see #setPenCapStyle(int)
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#setPenJoinStyle">example</a>.
     */
    public synchronized void setPenJoinStyle(int joinStyle)
    {
        if (pen == null)
        {
            pen = new PdfPen();
        }
        pen.joinStyle = joinStyle;
    }

    /**
     * Specifies default miter limit for this 
     * <code>PdfDocument</code>'s pen.
     * 
     * @param limit
     *            default miter limit for the 
     *            <code>PdfDocument</code>'s pen
     * @since 1.0
     * @see PdfPen
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#setPenMiterLimit">example</a>.
     */
    public synchronized void setPenMiterLimit(int limit)
    {
        if (pen == null)
        {
            pen = new PdfPen();
        }
        pen.miterLimit = limit;
    }

    /**
     * Specifies default color for this <code>PdfDocument</code>'s
     * brush.
     * 
     * @param c
     *            default color for the <code>PdfDocument</code>'s
     *            brush
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#setBrushColor">example</a>.
     */
    public synchronized void setBrushColor(Color c)
    {
        if (brush == null)
        {
            brush = new PdfBrush();
        }
        brush.fillColor = c;
    }

/*    public synchronized void setBrushPattern(int brushPattern)
    {
        if (brush == null)
        {
            brush = new PdfBrush();
        }
        brush.brushPattern = brushPattern;
    }
*/    
    public synchronized void insertPagesFrom(PdfStdDocument d,
        String pageRange, int insertAfterPage) throws PdfException
    {
        Vector pages = d.getPages(pageRange);
        int count = pageTree.getCount();
        if (insertAfterPage > count || insertAfterPage < 0)
        {
            throw new PdfException(
                "Illegal argument (insertAfterPage not valid).");
        }
        if (pages == null)
        {
            throw new PdfException("Invalid PageRange.");
        }
        for (Iterator iter = pages.iterator(); iter.hasNext();) 
        {
            PdfNode n = d.getPage(((Integer) iter.next()).intValue());
            if (n.originDoc == null)
            {
                n.originDoc = d;
            }
            if ( !objMaps.containsKey(d))
            {
                objMaps.put(d, new Hashtable());
            }
            this.pageTree.insert(++insertAfterPage, n);
        }
    }
    
    public synchronized void insertPagesFrom(String path,
        String pageRange, int insertAfterPage) throws IOException,
        PdfException
    {
        PdfReader r = PdfReader.fileReader(path, 0); 
        insertPagesFrom(new PdfStdDocument(r), pageRange,
            insertAfterPage);
    }
    
    /*
     * Rotates pages specified by pageRange parameter by the angle
     * <code>angle</code> in clockwise direction. The angle specified
     * must be an integer multiple of 90 degree.
     * 
     * @param pageRange Page numbers to be rotated
     * @param angle Angle of rotation in degrees (integer multiple
     *  of 90 degree)
     * @throws PdfException
     * @since 1.0 
     */
    /*
    public synchronized void rotatePages(String pageRange,
        int angle) throws PdfException
    {
        if (angle % 90 != 0)
        {
            throw new PdfException(
                "Illegal argument (rotation not valid).");
        }
        angle %= 360;
        if (angle < 0)
        {
            angle += 360;
        }
        if (angle != 0)
        {
            PdfName rotation = new PdfName(PDF_ROTATE);
            Vector pages = this.getPages(pageRange);
            if (pages == null)
            {
                throw new PdfException("Invalid PageRange.");
            }
            for (Iterator iter = pages.iterator(); iter.hasNext();) 
            {
                PdfStdPage p = this.getPage(((Integer) iter.next())
                    .intValue());
                p.getDict().getMap().put(rotation,
                    new PdfInteger(angle));
            }
        }
    }

    public synchronized void rotatePages(String pageRange,
        int angle, boolean applyToAddedContents) throws PdfException
    {
        if (angle % 90 != 0)
        {
            throw new PdfException(
                "Illegal argument (rotation not valid).");
        }
        angle %= 360;
        if (angle < 0)
        {
            angle += 360;
        }
        if (angle != 0)
        {
            PdfName rotation = new PdfName(PDF_ROTATE);
            Vector pages = this.getPages(pageRange);
            if (pages == null)
            {
                throw new PdfException("Invalid PageRange.");
            }
            for (Iterator iter = pages.iterator(); iter.hasNext();) 
            {
                PdfStdPage p = this.getPage(((Integer) iter.next())
                    .intValue());
                p.getDict().getMap().put(rotation,
                    new PdfInteger(angle));
                if (applyToAddedContents)
                {
                    p.rotation = angle;
                }
            }
        }
    }
*/
    protected void verifyPassword(String password)
        throws IOException, PdfException
    {
        if(password == null)
        {
            return;
        }
        if (reader.fileID == null)
        {
            throw new PdfBadFileException(
                "File ID not present in the Document.");
        }
        try {
            PdfString o = (PdfString) reader.getObject(encryptDict
                .getValue(new PdfName("O")));
            byte ownerPassword[] = PdfString.toBytes(o.getString());
            PdfString u = (PdfString) reader.getObject(encryptDict
                .getValue(new PdfName("U")));
            byte userPassword[] = PdfString.toBytes(u.getString());
            
            PdfObject p = reader.getObject(encryptDict
                .getValue(new PdfName(PDF_P)));
            
            PdfInteger r = (PdfInteger) reader.getObject(encryptDict
                .getValue(new PdfName(PDF_R)));
            PdfObject length = reader.getObject(encryptDict
                .getValue(new PdfName(PDF_LENGTH)));
            if(length == null)
            {
                length = new PdfInteger(40);
            }
            
            byte fileid[] = PdfString.toBytes(reader.fileID);
            byte pasword[] = PdfString.toBytes(password);
            
            PdfEncryption decrypt = new PdfEncryption();
            if (p instanceof PdfInteger)
            {
                PdfInteger pc = (PdfInteger) p;
                decrypt.setupByUserPassword(fileid, pasword, ownerPassword, 
                    pc.getInt(), r.getInt() == 3);
            }
            else if (p instanceof PdfLong)
            {
                PdfLong pc = (PdfLong) p;
                decrypt.setupByUserPassword(fileid, pasword, ownerPassword, 
                    pc.getInt(), r.getInt() == 3);
            }

            if ( !equalsArray(userPassword, decrypt.userKey, r
                .getInt() == 3 ? 16 : 32))
            {
                if (p instanceof PdfInteger)
                {
                    PdfInteger pc = (PdfInteger) p;
                    decrypt.setupByOwnerPassword(fileid, pasword,
                        userPassword, ownerPassword, pc.getInt(), r
                            .getInt() == 3);
                }
                else if (p instanceof PdfLong)
                {
                    PdfLong pc = (PdfLong) p;
                    decrypt.setupByOwnerPassword(fileid, pasword,
                        userPassword, ownerPassword, pc.getInt(), r
                            .getInt() == 3);
                }
                if(!Arrays.equals(userPassword, decrypt.userKey))
                {
                    throw new PdfException("Password is incorrect");
                }
            }
            else
            {
                isOwner = false;
            }
            
            ownerPassword = PdfString.toBytes(u.getString());
            userPassword = PdfString.toBytes(o.getString());
            int enLevel = ((PdfInteger) length).getInt();
            decrypt.setLevel(enLevel > 40 ? 
                PdfEncryption.LEVEL_128_BIT : PdfEncryption.LEVEL_40_BIT);
            reader.decryptor = decrypt;
            
        }
        catch (ClassCastException cce)
        {
            throw new PdfBadFileException(
                "Invalid object present in the file.");
        }
        
    }
    
    private static boolean equalsArray(byte ar1[], byte ar2[], int size)
    {
        for (int k = 0; k < size; ++k)
        {
            if (ar1[k] != ar2[k])
                return false;
        }
        return true;
    }
    
    /**
	 * Deletes pages in specified page range from this 
	 * <code>PdfDocument</code>.
	 * 
	 * @param pageRange
	 *            page range from which pages need to be deleted
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#deletePages">example</a>.
	 */
    public void deletePages(String pageRange) throws PdfException
    {
        Vector pages = getPageObjects(pageRange);
        for (int i = 0, limit = pages.size(); i < limit; ++i)
        {
            PdfPage p = (PdfPage) pages.get(i);
            p.deleted = true;
            pageTree.decrementCount();
            addToObjMaps(this, p.dict.objNumber, -1);
        }
    }

    public synchronized void merge(PdfDocument d)
        throws PdfException
    {
        if (d == null)
        {
            return;
        }
        insertPagesFrom(d, "-", getPageCount());
        if (this.bookmarkTree == null)
        {
            this.bookmarkTree = d.bookmarkTree;
            if (this.bookmarkTree != null)
            {
                this.bookmarkTree.parentDoc = this;
            }
        }
        else if (d.bookmarkTree != null)
        {
            this.bookmarkTree.merge(d.bookmarkTree);
        }
    }
    
    /*
     * Merges file specified by its pathname with this
     * <code>PdfDocument</code>.
     * 
     * @param path
     *            pathname of file to be merged
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#merge_String">example</a>.
     */
    public void merge(String path) throws IOException, PdfException
    {
        if (path == null || path.equals(""))
        {
            return;
        }
        merge(new PdfDocument(PdfReader.fileReader(path,
            PdfReader.READ_OUTLINES)));
    }
    
    /**
     * Extracts pages in specified page range and places them in
     * a file specified by its pathname.
     * 
     * @param path
     *            pathname of the file to which the pages are to be
     *            added
     * @param pageRange
     *            page range whose pages are to be extracted
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#extractPagesTo_String_String">example</a>.
     */
    public synchronized void extractPagesTo(String path,
        String pageRange) throws IOException, PdfException
    {
        if (path == null || path.equals(""))
        {
            return;
        }
        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid pageRange.");
        }
        PdfWriter w = PdfWriter.fileWriter(new File(path));
        PdfStdDocument d = new PdfStdDocument(w);
        for (Iterator iter = v.iterator(); iter.hasNext();)
        {
            PdfPage page = getPage(((Integer) iter.next())
                .intValue());
            d.add((PdfPage) page.clone());
        }
        d.write();
        w.dispose();
    }

    /**
     * Extracts pages in specified page range in specified PDF version
     * and places them in a new file specified by its pathname.
     * 
     * @param path
     *            pathname of the file to which the pages are to be
     *            added
     * @param pageRange
     *            page range whose pages are to be extracted
     * @param extractAsVersion
     *            PDF version in which pages are extracted and saved
     *            to the new file
     * @param openAfterExtraction
     *            whether the output file is to be opened after it is
     *            written to
     * @throws IOException
     *             if an I/O error occurs.
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#extractPagesTo_String_String_String_boolean">example</a>.
     */
    public synchronized void extractPagesTo(String path,
        String pageRange, String extractAsVersion,
        boolean openAfterExtraction) throws IOException, PdfException
    {
        if (path == null || path.equals(""))
        {
            return;
        }
        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid pageRange.");
        }
        PdfWriter w = PdfWriter.fileWriter(new File(path));
        PdfStdDocument d = new PdfStdDocument(w);
        for (Iterator iter = v.iterator(); iter.hasNext();)
        {
            PdfPage page = getPage(((Integer) iter.next()).intValue());
            d.add((PdfPage) page.clone());
        }
        d.setVersion(version);
        d.setOpenAfterSave(openAfterExtraction);
        d.write();
        w.dispose();
    }

    /*
     * Merges documents in <code>docList</code> with this
     * <code>PdfDocument</code>.
     * 
     * @param docList
     *            list containing <code>PdfDocument</code> object(s)
     *            and/or pathname(s) of files to be merged
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#merge_List">example</a>.
     */
    public synchronized void merge(List docList) throws IOException,
        PdfException
    {
        if (docList == null)
        {
            return;
        }
        PdfDocument d;
        Object obj;
        try {
            for (Iterator iter = docList.iterator(); iter.hasNext();)
            {
                obj = iter.next();
                d = (obj instanceof String) ? new PdfDocument(
                    PdfReader.fileReader((String) obj,
                        PdfReader.READ_OUTLINES)) : (PdfDocument) obj;  
                insertPagesFrom(d, "-", getPageCount());
                if (this.bookmarkTree == null)
                {
                    this.bookmarkTree = d.bookmarkTree;
                    if (this.bookmarkTree != null)
                    {
                        this.bookmarkTree.parentDoc = this;
                    }
                }
                else if (d.bookmarkTree != null)
                {
                    this.bookmarkTree.merge(d.bookmarkTree);
                }
            }
        }
        catch (ClassCastException cce)
        {
            throw new PdfException(
                "Invalid object encountered in List.");
        }
    }

    /**
	 * Adds specified text as watermark with its exact position 
     * determined by <code>position</code> and 
	 * <code>applyPageMargins</code> on pages in specified page 
     * range. 
     * <p>
     * Constants defined in {@link PdfPage} can be used to
     * align the text inside the watermark.
     * </p>
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
	 * 
	 * @param text
	 *            text that needs to be added as the watermark
	 * @param font
	 *            font with which the watermark needs to be written
	 * @param position
     *            constant specifying the combination of vertical and
     *            horizontal alignment of the text
	 * @param applyPageMargins
     *            whether page margins need to be considered when
     *            positioning the text
	 * @param angle
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
	 * @param underlay 
     *            whether the text needs to be placed underneath 
     *            other page contents
	 * @param pageRange
	 * 			  page range on whose pages the text needs to be 
     *            applied as the watermark
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#addWatermarkText">example</a>.
	 */
    public synchronized void addWatermarkText(String text,
        PdfFont font, int position, boolean applyPageMargins,
        double angle, boolean underlay, String pageRange)
        throws IOException, PdfException
    {
        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid pageRange.");
        }
        for (Iterator iter = v.iterator(); iter.hasNext();)
        {
            PdfStdPage page = getPage(((Integer) iter.next())
                .intValue());
            int mu = page.measurementUnit;
            page.measurementUnit = this.measurementUnit;
            page.addWatermarkText(text, font, position,
                applyPageMargins, angle, underlay);
            page.measurementUnit = mu;
        }
    }

    /**
	 * Adds specified text as watermark on pages in specified page 
     * range.
     * <p>
     * Constants defined in {@link PdfPage} can be used to align the 
     * text inside the watermark.
     * </p>
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
	 * 
	 * @param text
	 *            text to be displayed as watermark
	 * @param font
	 *            font with which the watermark needs to be written
	 * @param position
     *            constant specifying the combination of vertical and
     *            horizontal alignment of the text
	 * @param angle
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
	 * @param underlay
     *            whether the text needs to be placed underneath 
     *            other page contents
	 * @param pageRange
     *            page range on whose pages the text needs to be 
     *            applied as the watermark
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#addWaterMarkText_String_PdfFont_int_double_boolean_String">example</a>.
	 */
    public synchronized void addWatermarkText(String text,
        PdfFont font, int position, double angle, boolean underlay,
        String pageRange) throws IOException, PdfException
    {
        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid pageRange.");
        }
        for (Iterator iter = v.iterator(); iter.hasNext();)
        {
            PdfStdPage page = getPage(((Integer) iter.next())
                .intValue());
            int mu = page.measurementUnit;
            page.measurementUnit = this.measurementUnit;
            page.addWatermarkText(text, font, position, angle,
                underlay);
            page.measurementUnit = mu;
        }
    }

    /**
     * Adds <code>PdfImage</code> object as watermark with its exact
     * position determined by <code>position</code> and
     * <code>applyPageMargins</code>.
     * <p>
     * Constants defined in {@link PdfPage} can be used to align the 
     * image inside the watermark.
     * </p>
     * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param image
     *            <code>PdfImage</code> object that needs to be used
     *            as the watermark image
     * @param position
     *            constant specifying the combination of vertical and
     *            horizontal alignment of the image
     * @param applyPageMargins
     *            whether page margins need to be considered when
     *            positioning the image
     * @param angle
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
     * @param underlay
     *            whether the image needs to be placed underneath
     *            other page contents
     * @param pageRange
     *            page range on whose pages the image needs to be
     *            applied as the watermark
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#addWatermarkImage_PdfImage_int_boolean_double_boolean_String">example</a>.
     */
    public synchronized void addWatermarkImage(PdfImage image,
        int position, boolean applyPageMargins, double angle,
        boolean underlay, String pageRange) throws IOException,
        PdfException
    {
        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid pageRange.");
        }
        for (Iterator iter = v.iterator(); iter.hasNext();)
        {
            PdfStdPage page = getPage(((Integer) iter.next())
                .intValue());
            int mu = page.measurementUnit;
            page.measurementUnit = this.measurementUnit;
            page.addWatermarkImage(image, position, applyPageMargins,
                angle, underlay);
            page.measurementUnit = mu;
        }
    }

    /**
     * Adds <code>PdfImage</code> object as watermark on pages in
     * specified page range.
     * <p>
     * Constants defined in {@link PdfPage} can be used to align 
     * the image inside the watermark.
     * </p>
     * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param image
     *            <code>PdfImage</code> object that needs to be used
     *            as the watermark image
     * @param position
     *            constant specifying the combination of vertical and
     *            horizontal alignment of the image
     * @param angle
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
     * @param underlay
     *            whether the image needs to be placed underneath
     *            other page contents
     * @param pageRange
     *            page range on whose pages the image needs to be
     *            applied as the watermark
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#addWatermarkImage_PdfImage_int_double_boolean_String">example</a>.
     */
    public synchronized void addWatermarkImage(PdfImage image,
        int position, double angle, boolean underlay, String pageRange)
        throws IOException, PdfException
    {
        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid pageRange.");
        }
        for (Iterator iter = v.iterator(); iter.hasNext();)
        {
            PdfStdPage page = getPage(((Integer) iter.next())
                .intValue());
            int mu = page.measurementUnit;
            page.measurementUnit = this.measurementUnit;
            page.addWatermarkImage(image, position, angle, underlay);
            page.measurementUnit = mu;
        }
    }
    
    /**
	 * Adds image, specified by its pathname, as watermark with its 
     * exact position determined by <code>position</code> and 
	 * <code>applyPageMargins</code> on pages in specified page 
     * range. 
     * <p>
     * Constants defined in {@link PdfPage} can be used to
     * align the image inside the watermark.
     * </p>
	 * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
	 * @param path 
	 * 			pathname of the watermark image
	 * @param position 
     *          constant specifying the combination of vertical and
     *          horizontal alignment of the image
	 * @param applyPageMargins 
     *          whether page margins need to be considered when
     *          positioning the image 
	 * @param angle 
     *          (measured in anti-clockwise direction and expressed  
     *          in degrees) tilt of the image with reference to  
     *          center of its bounding box
	 * @param underlay 
     *          whether the image needs to be placed underneath
     *          other page contents
	 * @param pageRange 
     *          page range on whose pages the image needs to be
     *          applied as the watermark 
	 * @throws IOException 
	 * 			if an I/O error occurs.
	 * @throws PdfException 
	 * 			if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#addWatermarkImage_String_int_boolean_double_boolean_String">example</a>.
	 */
    public void addWatermarkImage(String path, int position,
        boolean applyPageMargins, double angle, boolean underlay,
        String pageRange) throws IOException, PdfException
    {
        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid pageRange.");
        }
        for (Iterator iter = v.iterator(); iter.hasNext();)
        {
            PdfStdPage page = getPage(((Integer) iter.next())
                .intValue());
            int mu = page.measurementUnit;
            page.measurementUnit = this.measurementUnit;
            page.addWatermarkImage(PdfImage.create(path), position,
                applyPageMargins, angle, underlay);
            page.measurementUnit = mu;
        }
    }
    
    /**
	 * Adds image, specified by its pathname, as watermark on pages
     * in specified page range.
     * <p>
     * Constants defined in {@link PdfPage} can be used to align the 
     * image inside the watermark.
     * </p>
     * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
	 * 
	 * @param path
	 *          pathname of the watermark image
	 * @param position
     *          constant specifying the combination of vertical and
     *          horizontal alignment of the image
	 * @param angle
     *          (measured in anti-clockwise direction and expressed  
     *          in degrees) tilt of the image with reference to  
     *          center of its bounding box
	 * @param underlay
     *          whether the image needs to be placed underneath
     *          other page contents
	 * @param pageRange
     *          page range on whose pages the image needs to be
     *          applied as the watermark
	 * @throws IOException
	 *          if an I/O error occurs.
	 * @throws PdfException
	 *          if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#addWatermarkImage_String_int_double_boolean_String">example</a>.
	 */
    public void addWatermarkImage(String path, int position,
        double angle, boolean underlay, String pageRange)
        throws IOException, PdfException
    {
        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid pageRange.");
        }
        for (Iterator iter = v.iterator(); iter.hasNext();)
        {
            PdfStdPage page = getPage(((Integer) iter.next())
                .intValue());
            int mu = page.measurementUnit;
            page.measurementUnit = this.measurementUnit;
            page.addWatermarkImage(PdfImage.create(path), position,
                angle, underlay);
            page.measurementUnit = mu;
        }
    }
    
    /**
	 * Adds specified text to header of pages in specified page 
     * range. 
     * <p>
     * Constants defined in {@link PdfPage} can be used to
     * align the text inside the watermark.
     * </p>
	 * 
	 * @param text
	 *            text to be added to header
	 * @param font
	 *            font with which the text next needs to be written
     * @param position
	 *            combination of vertical and horizontal alignment
	 * @param underlay
     *            whether the text needs to be placed underneath 
     *            other content in the header
	 * @param pageRange
	 *            page range on whose pages the text is to be added
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#addHeaderText_String_PdfFont_int_boolean_String">example</a>.
	 */
    public synchronized void addHeaderText(String text, PdfFont font,
        int position, boolean underlay, String pageRange)
        throws IOException, PdfException
    {
        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid pageRange.");
        }
        for (Iterator iter = v.iterator(); iter.hasNext();)
        {
            PdfStdPage page = getPage(((Integer) iter.next())
                .intValue());
            int mu = page.measurementUnit;
            page.measurementUnit = this.measurementUnit;
            page.addHeaderText(text, font, position, underlay);
            page.measurementUnit = mu;
        }
    }	
    
    /**
	 * Adds a <code>PdfImage</code> object to header of pages in 
	 * specified page range. 
     * <p>
     * Constants defined in {@link PdfPage}
     * can be used to align the image inside the header.
	 * </p>
     * 
	 * @param img
     *            <code>PdfImage</code> object that needs to be
     *            added to the header
	 * @param position
     *            constant specifying the combination of vertical and
     *            horizontal alignment of the image within the header
	 * @param underlay
     *            whether the image needs to be placed underneath 
     *            other content in the header
	 * @param pageRange
     *            page range on whose pages the image is to be added
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#addHeaderImage_PdfImage_int_boolean_String">example</a>.
	 */
    public synchronized void addHeaderImage(PdfImage img,
        int position, boolean underlay, String pageRange)
        throws IOException, PdfException
    {
        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid pageRange.");
        }
        for (Iterator iter = v.iterator(); iter.hasNext();)
        {
            PdfStdPage page = getPage(((Integer) iter.next())
                .intValue());
            int mu = page.measurementUnit;
            page.measurementUnit = this.measurementUnit;
            page.addHeaderImage(img, position, underlay);
            page.measurementUnit = mu;
        }
    }
    
    /**
	 * Adds image, specified by its pathname, to footer of pages in 
	 * specified page range. 
     * <p>
     * Constants defined in {@link PdfPage} can be used to align the 
     * image inside the header.
     * </p>
	 * 
	 * @param path
	 *            pathname of the image
	 * @param position
     *            constant specifying combination of vertical and
     *            horizontal alignment of the image within the header
	 * @param underlay
     *            whether the image needs to be placed underneath 
     *            other content in the header
	 * @param pageRange
	 *            page range on whose pages the image is to be added
	 * @throws IOException
	 *            if an I/O error occurs.
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#addHeaderImage_String_int_boolean_String">example</a>.
	 */
    public void addHeaderImage(String path, int position,
        boolean underlay, String pageRange) throws IOException,
        PdfException
    {
        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid pageRange.");
        }
        for (Iterator iter = v.iterator(); iter.hasNext();)
        {
            PdfStdPage page = getPage(((Integer) iter.next())
                .intValue());
            int mu = page.measurementUnit;
            page.measurementUnit = this.measurementUnit;
            page.addHeaderImage(PdfImage.create(path), position,
                underlay);
            page.measurementUnit = mu;
        }
    }

    /**
     * Adds specified text to footer of pages in specified page range.
     * <p>
     * Constants defined in {@link PdfPage} can be used to align the 
     * text inside the footer.
     * </p>
     * 
     * @param text
     *            text to be added to the footer
     * @param font
     *            font with which the text next needs to be written
     * @param position
     *            constant specifying the combination of vertical and
     *            horizontal alignment of the text within the footer
     * @param underlay
     *            whether the text needs to be placed underneath other
     *            content in the footer
     * @param pageRange
     *            page range on whose pages the text is to be added
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#addFooterText_String_PdfFont_int_boolean_String">example</a>.
     */
    public synchronized void addFooterText(String text, PdfFont font,
        int position, boolean underlay, String pageRange)
        throws IOException, PdfException
    {
        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid pageRange.");
        }
        for (Iterator iter = v.iterator(); iter.hasNext();)
        {
            PdfStdPage page = getPage(((Integer) iter.next())
                .intValue());
            int mu = page.measurementUnit;
            page.measurementUnit = this.measurementUnit;
            page.addFooterText(text, font, position, underlay);
            page.measurementUnit = mu;
        }
    }
    
    /**
	 * Adds <code>PdfImage</code> object to footer of pages in 
	 * specified page range. 
     * <p>
     * Constants defined in {@link PdfPage} can be used to align 
     * the image inside the footer.
     * </p> 
	 * 
	 * @param img
	 *            <code>PdfImage</code> object to be used in the 
     *            footer
	 * @param position
     *            constant specifying combination of vertical and
     *            horizontal alignment of the image within the footer
	 * @param underlay
     *            whether the image needs to be placed underneath 
     *            other page elements
	 * @param pageRange
	 *            page range on whose pages the image is to be added
	 * @throws IOException if an I/O error occurs.
	 * @throws PdfException if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#addFooterImage_PdfImage_int_boolean_String">example</a>.
	 */
    public synchronized void addFooterImage(PdfImage img,
        int position, boolean underlay, String pageRange)
        throws IOException, PdfException
    {
        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid pageRange.");
        }
        for (Iterator iter = v.iterator(); iter.hasNext();)
        {
            PdfStdPage page = getPage(((Integer) iter.next())
                .intValue());
            int mu = page.measurementUnit;
            page.measurementUnit = this.measurementUnit;
            page.addFooterImage(img, position, underlay);
            page.measurementUnit = mu;
        }
    }
    
    
    /**
     * Adds image, specified by its pathname, to footer of pages in
     * specified page range. 
     * <p>
     * Constants defined in {@link PdfPage} can be used to align the 
     * image inside the footer.
     * </p>
     * 
     * @param path
     *            pathname of the image
     * @param position
     *            constant specifying combination of vertical and
     *            horizontal alignment of the image within the footer
     * @param underlay
     *            whether the image needs to be placed underneath
     *            other content in the footer
     * @param pageRange
     *            page range on whose pages the image is to be added
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#addFooterImage_String_int_boolean_String">example</a>.
     */
    public void addFooterImage(String path, int position,
        boolean underlay, String pageRange) throws IOException,
        PdfException
    {
        Vector v = getPages(pageRange);
        if (v == null)
        {
            throw new PdfException("Invalid pageRange.");
        }
        for (Iterator iter = v.iterator(); iter.hasNext();)
        {
            PdfStdPage page = getPage(((Integer) iter.next())
                .intValue());
            int mu = page.measurementUnit;
            page.measurementUnit = this.measurementUnit;
            page.addFooterImage(PdfImage.create(path), position, underlay);
            page.measurementUnit = mu;
        }
    }
    
    /**
     * Returns XML metadata of this <code>PdfDocument</code>.
     * 
     * @return XML metadata of the document
     * @throws IOException
     *             if an I/O error occurs.
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#getXMLMetadata">example</a>.
     */
    public String getXMLMetadata() throws IOException, PdfException
    {
        if (this.catalog == null)
        {
            return "";
        }
        PdfObject metadata = (PdfObject) catalog.catalogDict
            .getValue(new PdfName(PDF_METADATA));
        metadata = reader.getObject(metadata);
        try {
            ByteBuffer bb = ((PdfStream) metadata).streamBuffer;
            return Charset.forName("ISO-8859-1").decode(bb)
                .toString();
        }
        catch (ClassCastException cce)
        {
            throw new PdfBadFileException("Invalid /Metadata entry.");
        }
    }
    
    /**
	 * Disables all margins on pages in specified page range.
	 * 
	 * @param pageRange
	 *            page range on whose pages margins need to disabled
	 * @throws PdfException
	 *            if an illegal argument is supplied.
	 * @since 1.0
     * @see #enableAllMargins(String)
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#disableAllMargins">example</a>.
	 */
    public synchronized void disableAllMargins(String pageRange)
        throws PdfException
    {
        Vector v = getPageObjects(pageRange);
        for (int i = 0, limit = v.size(); i < limit; ++i)
        {
            ((PdfStdPage) v.get(i)).disableAllMargins();
        }
    }

    /**
     * Enables all margins on pages in specified page range.
     * 
     * @param pageRange
     *            page range on whose pages margins need to enabled
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see #enableAllMargins(String)
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#enableAllMargins">example</a>.
     */
    public synchronized void enableAllMargins(String pageRange)
        throws PdfException
    {
        Vector v = getPageObjects(pageRange);
        for (int i = 0, limit = v.size(); i < limit; ++i)
        {
            ((PdfStdPage) v.get(i)).enableAllMargins();
        }
    }

    /*public void addEMail(PdfEMail eMail)
    {
        if (eMails == null)
        {
            eMails = new Vector();
        }
        eMails.add(eMail);
    }
    
    public synchronized boolean isEmailAfterSave()
    {
        return emailAfterSave;
    }

    public synchronized void setEmailAfterSave(boolean emailAfterSave)
    {
        this.emailAfterSave = emailAfterSave;
    }*/

    public void setOnRenameField(Method onRenameField)
    {
        this.onRenameField = onRenameField;
    }

    /**
     * Returns whether document is set to be executed after data is
     * saved to file.
     * 
     * @return whether document is set to be executed after data is
     *         saved to file
     * @since 1.0
     * @see #setOpenAfterSave(boolean)
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#isOpenAfterSave">example</a>.
     */
    public synchronized boolean isOpenAfterSave()
    {
        return openAfterSave;
    }

    /**
     * Specifies whether document needs to be executed after data is
     * saved to file.
     * 
     * @param openAfterSave
     *            whether document needs to be executed after data is
     *            saved to file
     * @since 1.0
     * @see #isOpenAfterSave()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#SetOpenAfterSave">example</a>.
     */
    public synchronized void setOpenAfterSave(boolean openAfterSave)
    {
        this.openAfterSave = openAfterSave;
    }
    
    /**
	 * Adds a filter to the list of filters used to encode stream 
	 * objects in this document. 
     * <p>
     * Multiple filters can be added to the list of filters for a 
     * single document. Filters will be applied in the order they  
     * were added.
     * </p>
	 * 
	 * @param filter
	 *            filter to be added 
	 * @since 1.0
     * @see PdfFilter
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#addToFiltersList">example</a>.
	 */
    public synchronized void addToFiltersList(int filter)
    {
        if (filters == null)
        {
            filters = new ArrayList();
        }
        filters.add(new PdfInteger(filter));
    }
    
    protected synchronized void decompress()
    {
        filters = null;
        addDefaultFilter = false;
        if (mode == WRITING_MODE)
        {
            this.writer.decompressStreams = true;
        }
    }
}