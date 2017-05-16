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
import java.util.Vector;

import com.gnostice.pdfone.encodings.PdfEncodings;
import com.gnostice.pdfone.fonts.PdfFont;
import com.gnostice.pdfone.graphics.PdfPen;

class PdfProDocument extends PdfStdDocument
{
    public static final int ALIGNMENT_LEFT = 0;
    
    public static final int ALIGNMENT_CENTER = 1;

    public static final int ALIGNMENT_RIGHT = 2;

    private int viewerPrefVal;
    
    HashMap fieldPageRanges;
    
    boolean overrideFieldAppearanceStreams = true;
    
    boolean autoAdjustFieldTextHeight;
    
    boolean needAcroDict;
    
    int defaultFieldAlignment;
    
    int defaultFormFontIndex;
    
    List formDefaultFonts;
    
    Color defaultFormFontColor = Color.BLACK;
    
    PdfProDocument(PdfWriter w) throws PdfException
    {
        super(w);
        defaultFieldAlignment = ALIGNMENT_LEFT;
    }

    PdfProDocument(PdfReader r) throws IOException, PdfException
    {
        super(r);
        defaultFieldAlignment = ALIGNMENT_LEFT;
    }
    
    /* Invoke to read /DA entry for existing docs only */
    String getFormDA() throws IOException, PdfException
    {
        return this.catalog != null ? this.catalog.getFormDA(this)
            : "";
    }
    
    protected void parseCatalog() throws IOException, PdfException
    {
        super.parseCatalog();
        Map m = catalog.getDictionary().getMap();
        String name;
        PdfObject key, value, currObj;

        for (Iterator iter = m.keySet().iterator(); iter.hasNext();)
        {
            key = (PdfObject) iter.next();
            currObj = (PdfObject) m.get(key);
            value = reader.getObject(currObj);
            if (value instanceof PdfNull)
            {
                continue;
            }

            name = ((PdfName) key).getString();
            if (name.equals(PDF_VIEWER_PREFERENCES))
            {
                catalog.unknownAttributes.remove(key);
                PdfDict viewPrefDict = (PdfDict) value;
                Map m1 = viewPrefDict.dictMap;
                for (Iterator iter1 = m1.keySet().iterator(); iter1
                    .hasNext();)
                {
                    PdfName key1 = (PdfName) iter1.next();
                    PdfObject val = (PdfObject) m1.get(key1);
                    if (val instanceof PdfBoolean
                        && val.equals(PdfBoolean.TRUE))
                    {
                        String s = key1.getString();
                        if (s.equalsIgnoreCase(PDF_HIDE_TOOLBAR))
                            this.viewerPrefVal |= PdfPreferences.HIDE_TOOLBAR;
                        if (s.equalsIgnoreCase(PDF_HIDE_MENUBAR))
                            this.viewerPrefVal |= PdfPreferences.HIDE_MENUBAR;
                        if (s.equalsIgnoreCase(PDF_HIDE_WINDOWUI))
                            this.viewerPrefVal |= PdfPreferences.HIDE_WINDOWUI;
                        if (s.equalsIgnoreCase(PDF_FIT_WINDOW))
                            this.viewerPrefVal |= PdfPreferences.FIT_WINDOW;
                        if (s.equalsIgnoreCase(PDF_CENTER_WINDOW))
                            this.viewerPrefVal |= PdfPreferences.CENTER_WINDOW;
                        if (s.equalsIgnoreCase(PDF_DISPLAY_DOCTITLE))
                            this.viewerPrefVal |= PdfPreferences.DISPLAY_DOC_TITLE;
                    }
                    else if (val instanceof PdfName)
                    {
                        String valString = ((PdfName) val)
                            .getString();
                        if (valString.equalsIgnoreCase(PDF_USENONE))
                            this.viewerPrefVal |= PdfPreferences.NonFullScreenPageMode.NONE;
                        if (valString
                            .equalsIgnoreCase(PDF_USEOUTLINES))
                            this.viewerPrefVal |= PdfPreferences.NonFullScreenPageMode.OUTLINES;
                        if (valString.equalsIgnoreCase(PDF_USEOC))
                            this.viewerPrefVal |= PdfPreferences.NonFullScreenPageMode.OC;
                        if (valString.equalsIgnoreCase(PDF_USETHUMBS))
                            this.viewerPrefVal |= PdfPreferences.NonFullScreenPageMode.THUMBS;
                        if (valString.equalsIgnoreCase(PDF_L2R))
                            this.viewerPrefVal |= PdfPreferences.Direction.LEFT_TO_RIGHT;
                        if (valString.equalsIgnoreCase(PDF_R2L))
                            this.viewerPrefVal |= PdfPreferences.Direction.RIGHT_TO_LEFT;
                    }
                }
            }
        }
    }

    protected void setViewPref() throws IOException, PdfException
    {
        if (viewerPrefVal != 0)
        {
            Map hm = new HashMap();
            if ((viewerPrefVal & PdfPreferences.HIDE_TOOLBAR) != 0)
            {
                hm.put(new PdfName(PDF_HIDE_TOOLBAR),
                        PdfBoolean.TRUE);
            }
            if ((viewerPrefVal & PdfPreferences.HIDE_MENUBAR) != 0)
            {
                hm.put(new PdfName(PDF_HIDE_MENUBAR),
                        PdfBoolean.TRUE);
            }
            if ((viewerPrefVal & PdfPreferences.HIDE_WINDOWUI) != 0)
            {
                hm.put(new PdfName(PDF_HIDE_WINDOWUI),
                    PdfBoolean.TRUE);
            }
            if ((viewerPrefVal & PdfPreferences.FIT_WINDOW) != 0)
            {
                hm.put(new PdfName(PDF_FIT_WINDOW), PdfBoolean.TRUE);
            }
            if ((viewerPrefVal & PdfPreferences.CENTER_WINDOW) != 0)
            {
                hm.put(new PdfName(PDF_CENTER_WINDOW),
                    PdfBoolean.TRUE);
            }
            if ((viewerPrefVal & PdfPreferences.DISPLAY_DOC_TITLE) != 0)
            {
                hm.put(new PdfName(PDF_DISPLAY_DOCTITLE),
                    PdfBoolean.TRUE);
            }

            if ((viewerPrefVal & PdfPreferences
                .Direction.LEFT_TO_RIGHT) != 0)
            {
                hm.put(new PdfName(PDF_DIRECTION), new PdfName(
                    PDF_L2R));
            }
            else if ((viewerPrefVal & PdfPreferences
                .Direction.RIGHT_TO_LEFT) != 0)
            {
                hm.put(new PdfName(PDF_DIRECTION), new PdfName(
                    PDF_R2L));
            }

            final PdfName NON_FULLSCREEN = new PdfName(
                PDF_NONFULLSCREEN_PAGEMODE);

            if ((viewerPrefVal & PdfPreferences
                .NonFullScreenPageMode.NONE) != 0)
            {
                hm.put(NON_FULLSCREEN, new PdfName(PDF_USENONE));
            }
            else if ((viewerPrefVal & PdfPreferences
                .NonFullScreenPageMode.OC) != 0)
            {
                hm.put(NON_FULLSCREEN, new PdfName(PDF_USEOC));
            }
            else if ((viewerPrefVal & PdfPreferences
                .NonFullScreenPageMode.OUTLINES) != 0)
            {
                hm.put(NON_FULLSCREEN, new PdfName(PDF_USEOUTLINES));
            }
            else if ((viewerPrefVal & PdfPreferences
                .NonFullScreenPageMode.THUMBS) != 0)
            {
                hm.put(NON_FULLSCREEN, new PdfName(PDF_USETHUMBS));
            }

            PdfName key = new PdfName(PDF_VIEWER_PREFERENCES);
            catalog.getDictionary().getMap()
                .put(key, new PdfDict(hm));
            catalog.unknownAttributes.remove(key);
        }
    }

    HashMap prepareAcroMap() throws PdfException, IOException
    {
        if ( !needAcroDict)
        {
            return null;
        }
        if (formDefaultFonts == null)
        {
            formDefaultFonts = new ArrayList();
            PdfFont helvetica = PdfFont.create("Arial", 12,
                PdfEncodings.CP1252);
            PdfFont symbol = PdfFont.create("ZapfDingbats", 12,
                PdfEncodings.CP1252);
            formDefaultFonts.add(helvetica);
            formDefaultFonts.add(symbol);
        }
        
        //Prepare DR
        HashMap dr = new HashMap();
        
        HashMap dr_fontMap = new HashMap();
        PdfFont font;
        String fontName;
        PdfName key;
        for (int i = 0, limit = formDefaultFonts.size(); i < limit; ++i)
        {
            font = (PdfFont) formDefaultFonts.get(i);
            if (font.getType() != PdfFont.TYPE_1)
            {
                //all widths should be present
                char[] ca = new char[256 - 32];
                for (int j = 32, k = 0; j < 256; ++j, ++k)
                {
                    ca[k] = (char) j;
                }
                font.updateGlyphList(new String(ca));
            }

            fontName = PDF_FONTNAMEPREFIX + font.getName();
            key = new PdfName(fontName);
            if (fontMap != null && fontMap.containsKey(key))
            {
                dr_fontMap.put(key, new PdfIndirectReference(
                    ((PdfObject) fontMap.get(key)).objNumber, 0));
            }
            else
            {
                PdfStdPage.prepareFontAndAddToDoc(this, dr_fontMap, key, font);
            }
        }
        dr.put(new PdfName(PDF_FONT), new PdfDict(dr_fontMap));

        //Prepare DA
        String defaultFontName = PDF_NAMESTART + PDF_FONTNAMEPREFIX
            + ((PdfFont) formDefaultFonts.get(defaultFormFontIndex))
                .getName();
        String color = PdfWriter.formatFloat(defaultFormFontColor
            .getRed() / 255f)
            + PDF_SP;
        color += PdfWriter.formatFloat(defaultFormFontColor
            .getGreen() / 255f)
            + PDF_SP;
        color += PdfWriter
            .formatFloat(defaultFormFontColor.getBlue() / 255f)
            + " rg ";
        fontName = defaultFontName + PDF_SP
            + (autoAdjustFieldTextHeight ? "0 " : "12 ")
            + PDF_TEXTFONT;

        String da = color + fontName;

        //Prepare Fields
        HashMap fieldsMap = new HashMap(); //to kep unique ind refs in /Fields entry
        int pageCount = getPageCount();
        PdfPage p;
        PdfFormField f;
        for (int i = 1; i <= pageCount; ++i)
        {
            p = getPage(i);
            if ( !p.deleted && p.fieldList != null)
            {
                for (int j = 0, limit = p.fieldList.size(); j < limit; ++j)
                {
                    f = (PdfFormField) p.fieldList.get(j);
                    fieldsMap.put(new PdfIndirectReference(
                        f.dict.objNumber, 0), PdfNull.DUMMY);
                }
            }
        }
        if (fieldPageRanges != null)
        {
            for (Iterator iter = fieldPageRanges.keySet().iterator(); iter
                .hasNext();)
            {
                f = (PdfFormField) iter.next();
                fieldsMap.put(new PdfIndirectReference(
                    f.dict.objNumber, 0), PdfNull.DUMMY);
            }
        }
        List fields = new ArrayList(fieldsMap.keySet());
        
        //Prepate XFA and CO
        /* To be implemented */
        
        //Prepare final map 
        HashMap acroHm = new HashMap();
        acroHm.put(new PdfName(PDF_Q), new PdfInteger(
            defaultFieldAlignment));
        acroHm.put(new PdfName(PDF_DA), new PdfString(da));
        acroHm.put(new PdfName(PDF_DR), new PdfDict(dr));
        acroHm.put(new PdfName(PDF_FIELDS), new PdfArray(fields));
        if (overrideFieldAppearanceStreams)
        {
            acroHm.put(new PdfName(PDF_NEEDAPPEARANCES),
                PdfBoolean.TRUE);
        }
        
        return acroHm;
    }

    protected void setCatalog() throws IOException, PdfException
    {
        this.setViewPref();
        super.setCatalog();
    }

    protected void setFields() throws IOException, PdfException
    {
        PdfFormField f, child;
        String[] ranges;
        Vector pages;
        ArrayList descendents;
        PdfPage page;
        boolean fieldEncoded;
        
        for (Iterator iter = fieldPageRanges.keySet().iterator(); iter
            .hasNext();)
        {
            f = (PdfFormField) iter.next();
            ranges = (String[]) fieldPageRanges.get(f);
            descendents = f.getAllDescendantWidgets();
            if (descendents == null)
            {
                continue;
            }
            
            fieldEncoded = false;
            for (int i = 0, limit = descendents.size(); i < limit; ++i)
            {
                int index = Math.min(i, ranges.length - 1);
                pages = getPageObjects(ranges[index]);
                child = (PdfFormField) descendents
                    .get(i);
                if (pages == null)
                {
                    continue;
                }
                for (int j = 0, in_limit = pages.size(); j < in_limit; ++j)
                {
                    page = (PdfPage) pages.get(j);
                    if ( !fieldEncoded)
                    {
                        f.encode(page);
                        f.set(page.originDoc, this);
                        fieldEncoded = true;
                    }
                    if (page.fieldIndRefList == null)
                    {
                        page.fieldIndRefList = new ArrayList();
                    }
                    page.fieldIndRefList
                        .add(new PdfIndirectReference(
                            child.dict.objNumber, 0));
                }
            }
        }
    }
    
    protected void setObjects() throws IOException, PdfException
    {
        super.setObjects();
        if (this.fieldPageRanges != null)
        {
            setFields();
        }
    }
    
    protected void writeFields() throws IOException, PdfException
    {
        for (Iterator iter = fieldPageRanges.keySet().iterator(); iter
            .hasNext();)
        {
            ((PdfFormField) iter.next()).write(this);
        }
    }
    
    protected void writeObjects() throws IOException, PdfException
    {
        super.writeObjects();
        if (fieldPageRanges != null)
        {
            writeFields();
        }
    }

    public synchronized void addDefaultFormFont(PdfFont font)
    {
        if (font == null)
        {
            return;
        }
        if (formDefaultFonts == null)
        {
            formDefaultFonts = new ArrayList();
        }

        formDefaultFonts.add(font);
    }
    
    public synchronized void addDefaultFormFontList(List fontList)
    {
        if (fontList == null)
        {
            return;
        }
        if (formDefaultFonts == null)
        {
            formDefaultFonts = new ArrayList();
        }
        
        formDefaultFonts.addAll(fontList);
    }
    
    public synchronized List getDefaultFormFontList()
    {
        return formDefaultFonts;
    }
    
    public synchronized void setDefaultFormFontList(List fontList)
    {
        this.formDefaultFonts = fontList;
    }
    
    public synchronized int getViewerPreferences()
    {
        return this.viewerPrefVal;
    }
    
    public synchronized void setViewerPreferences(int value)
    {
        viewerPrefVal |= value;
    }
    
    public synchronized boolean isOverrideFieldAppearanceStreams()
    {
        return overrideFieldAppearanceStreams;
    }

    public synchronized void setOverrideFieldAppearanceStreams(
        boolean overrideAppearanceStreams)
    {
        this.overrideFieldAppearanceStreams = overrideAppearanceStreams;
    }

    public synchronized int getDefaultFieldAlignment()
    {
        return defaultFieldAlignment;
    }

    public synchronized void setDefaultFieldAlignment(
        int defualtFieldAlignment)
    {
        this.defaultFieldAlignment = defualtFieldAlignment;
    }

    public synchronized Color getDefaultFormFontColor()
    {
        return defaultFormFontColor;
    }

    public synchronized void setDefaultFormFontColor(
        Color defaultFormFontColor)
    {
        this.defaultFormFontColor = defaultFormFontColor;
    }

    public synchronized boolean isAutoAdjustFieldTextHeight()
    {
        return autoAdjustFieldTextHeight;
    }

    public synchronized void setAutoAdjustFieldTextHeight(
        boolean autoAdjustFieldTextHeight)
    {
        this.autoAdjustFieldTextHeight = autoAdjustFieldTextHeight;
    }

    public synchronized void setDefaultFormFontIndex(
        int defaultFormFontIndex) /* starts with 0 */
    {
        this.defaultFormFontIndex = defaultFormFontIndex;
    }

    public void addTable(PdfTable table, double x, double y,
        int pageNo) throws PdfException, IOException
    {
        if (table == null)
        {
             return;
        }
        int pageIndex = pageNo;
        // Check if page count is 0 and add blank page if necessary
        if (table.getCellList() == null
            || PdfPageTree.count(pageTree.root) < pageNo)
        {
            return;
        }

        PdfPage p = (PdfPage) pageTree.getPage(pageNo);
        if (p.prevFont == null)
        {
            p.prevFont = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        if ((PdfMeasurement.convertToPdfUnit(measurementUnit, y) > p.pageHeight)
            || (PdfMeasurement.convertToPdfUnit(p.measurementUnit, y) < 0))
        {
            return;
        }

        //declaration & intialisation
        double pageHeightAvailable;
        boolean cellNotDrawn;
        int colNo = 0;
        int extraPageCount = 0;
        double pageHeightLeft;
        double rectX, rectY;
        double tempRowHeight;
        double cutHeight = 0;
        double tempCutHeight;
        double temppageHeightLeft;

        x = PdfMeasurement.convertToPdfUnit(p.measurementUnit, x);
        x += p.pageLeftMargin;
        y = PdfMeasurement.convertToPdfUnit(p.measurementUnit, y);

        ArrayList cellArray = table.getCellList();
        table.currentX = x;
        table.currentY = y + p.pageTopMargin + p.pageHeaderHeight;
        rectX = x;
        rectY = y + p.pageTopMargin + p.pageHeaderHeight;
        pageHeightAvailable = p.pageHeight
            - (p.pageTopMargin + p.pageHeaderHeight
                + p.pageBottomMargin + p.pageFooterHeight);
        pageHeightLeft = pageHeightAvailable - y;
        for (int i = 0; i < table.totalColumns; i++)
        {
            table.colHeightArray[i] = table.currentY;
        }

        if (table.autoAdjRowHeight == true)
        {
            String remStr;
            ArrayList arrRect = new ArrayList();
            double colheight;
            int tempPagecount = 0;
            int totalCells = 0;

            for (int i = 0; i < cellArray.size(); i++)
            {
                PdfCell c = (PdfCell) cellArray.get(i);
                totalCells += c.colSpan;
            }

            for (int i = 0; i < cellArray.size(); i++)
            {
                PdfCell c = (PdfCell) cellArray.get(i);
                if (pageHeightLeft - table.rowHeight < 0)
                {
                    PdfFont font = p.prevFont;
                    p = new PdfPage(p.pageWidth, p.pageHeight,
                        p.pageHeaderHeight, p.pageFooterHeight,
                        p.pageLeftMargin, p.pageTopMargin,
                        p.pageRightMargin, p.pageBottomMargin,
                        PdfMeasurement.MU_POINTS);
                    p.prevFont = font;
                    pageIndex++;
                    this.pageTree.insert(pageIndex, p);
                    
                    
                    table.currentY = p.pageTopMargin + p.pageHeaderHeight;
                    pageHeightLeft = pageHeightAvailable;
                    table.currentX = x;
                }

                c.width = 0;
                for (int j = colNo; (j < c.colSpan + colNo)
                    && (j < table.totalColumns); j++)
                {
                    c.width += (float) table.colWidthArray[j];
                }
                
                if(c.text == null)
                {
                    c.text = "";
                }

                remStr = c.text;
                int noLines = 0;

                PdfRect rem = new PdfRect(c.x, c.y, c.width,
                    p.prevFont.getHeight());
                rem.x += PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_POINTS, c.cellLeftMargin);
                rem.y += PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_POINTS, c.cellTopMargin);
                rem.width = rem.width
                    - PdfMeasurement.convertToPdfUnit(
                        PdfMeasurement.MU_POINTS,
                        (c.cellLeftMargin + c.cellRightMargin));
                rem.height = p.prevFont.getHeight();

                while (remStr != "")
                {
                    remStr = p.wrapText(remStr, rem, 0, p.prevFont,
                        PdfMeasurement.MU_POINTS);
                    rem.y += p.prevFont.getHeight(); 
                    c.y += p.prevFont.getHeight(); 
                    noLines++;
                }
                c.height = noLines * (p.prevFont.getHeight())
                    + (p.prevFont.getHeight() / 2);
                c.height += PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_POINTS,
                    (c.cellTopMargin + c.cellBottomMargin));

                arrRect.add(c);
                colNo += c.colSpan;

                if ((colNo >= table.totalColumns)
                    || (colNo >= totalCells))
                {
                    colheight = 0;
                    for (int k = 0; k < arrRect.size(); k++)
                    {
                        PdfCell pCell = (PdfCell) arrRect.get(k);
                        if (pCell.height > colheight)
                        {
                            colheight = pCell.height;
                        }
                    }

                    if (colheight < table.rowHeight)
                    {
                        colheight = table.rowHeight;
                    }

                    if (pageHeightLeft - colheight < 0)
                    {
                        for (int k = 0; k < arrRect.size(); k++)
                        {
                            PdfCell pCell = (PdfCell) arrRect.get(k);
                            pCell.x = table.currentX;
                            pCell.y = table.currentY;
                            pCell.height = colheight;
                            tempPagecount = 0;
                            cutHeight = 0;
                            temppageHeightLeft = pageHeightLeft;
                            while (temppageHeightLeft > 0)
                            {
                                temppageHeightLeft -= table.rowHeight;
                                if (temppageHeightLeft > 0)
                                {
                                    cutHeight += table.rowHeight;
                                }
                            }

                            PdfRect orginalRect = new PdfRect(
                                table.currentX, table.currentY,
                                pCell.width, colheight);
                            tempCutHeight = cutHeight;

                            drawCell(table.currentX, table.currentY,
                                pCell, cutHeight, p);
                            drawCellContent(table.currentX,
                                table.currentY, cutHeight, pCell, p,
                                orginalRect);
                            tempRowHeight = colheight - cutHeight
                                + PdfMeasurement.convertToPdfUnit(
                                        PdfMeasurement.MU_POINTS,
                                        (c.cellTopMargin + c.cellBottomMargin));
                            tempPagecount = 0;

                            while (tempRowHeight > 0) 
                            {
                                pageIndex++;
                                if (extraPageCount > 0)
                                {
                                    p = (PdfPage) pageTree
                                        .getPage(pageIndex);
                                    tempPagecount++;
                                    extraPageCount--;
                                }
                                else
                                {
                                    PdfFont font = p.prevFont;
                                    p = new PdfPage(p.pageWidth,
                                        p.pageHeight,
                                        p.pageHeaderHeight,
                                        p.pageFooterHeight,
                                        p.pageLeftMargin,
                                        p.pageTopMargin,
                                        p.pageRightMargin,
                                        p.pageBottomMargin,
                                        PdfMeasurement.MU_POINTS);

                                    p.prevFont = font;
                                    this.pageTree.insert(pageIndex, p);
                                    tempPagecount++;
                                }
                                if (tempRowHeight <= pageHeightAvailable)
                                {
                                    cutHeight = tempRowHeight;
                                    tempRowHeight = tempRowHeight
                                        - pageHeightAvailable;
                                }
                                else
                                {
                                    cutHeight = pageHeightAvailable;
                                    tempRowHeight = tempRowHeight - cutHeight
                                    + PdfMeasurement.convertToPdfUnit(
                                        PdfMeasurement.MU_POINTS,
                                        (c.cellTopMargin + c.cellBottomMargin));
                                }
                                drawCell(table.currentX,
                                    p.pageTopMargin
                                        + p.pageHeaderHeight,
                                    pCell, cutHeight, p);

                                orginalRect.y = p.pageTopMargin
                                    + p.pageHeaderHeight - tempCutHeight;
                                tempCutHeight += cutHeight;
                                drawCellContent(table.currentX,
                                    p.pageTopMargin + p.pageHeaderHeight,
                                    cutHeight, pCell, p, orginalRect);

                            }

                            if (k != arrRect.size() - 1)
                            {
                                pageIndex -= tempPagecount;
                                extraPageCount += tempPagecount;
                                p = (PdfPage) pageTree
                                    .getPage(pageIndex);
                                colNo += c.colSpan;
                                table.currentX += pCell.width;
                            }
                        }

                        table.currentY = cutHeight + p.pageTopMargin
                            + p.pageHeaderHeight;
                        pageHeightLeft = pageHeightAvailable
                            - cutHeight;
                    }
                    else
                    {
                        for (int k = 0; k < arrRect.size(); k++)
                        {
                            PdfCell pCell = (PdfCell) arrRect.get(k);
                            pCell.x = table.currentX;
                            pCell.y = table.currentY;
                            pCell.height = colheight;
                            tempPagecount = 0;
//                            PdfPen tPen = (PdfPen) p.prevPen;
                            drawCell(table.currentX, table.currentY,
                                pCell, colheight, p);

                            drawCellContent(table.currentX,
                                    table.currentY, colheight, pCell,
                                    p);
                            table.currentX += pCell.width;
                        }
                        table.currentY += colheight;
                        pageHeightLeft -= colheight;
                    }
                    arrRect = new ArrayList();
                    table.currentX = x;
                    colNo = 0;
                    totalCells -= table.totalColumns;
                }
            }

        }
        else
        {
            for (int i = 0; i < cellArray.size(); i++)
            {
                PdfCell c = (PdfCell) cellArray.get(i);

                if (colNo >= table.totalColumns)
                {
                    table.currentX = x;
                    colNo = 0;
                    table.currentY += table.rowHeight;
                    rectX = x;
                    rectY += table.rowHeight;
                    pageHeightLeft = pageHeightLeft
                        - table.rowHeight;
                }

                if ((pageHeightLeft - table.rowHeight) < 0)
                {
                    tempRowHeight = table.colHeightArray[colNo]
                        - pageHeightLeft;

                    if (extraPageCount > 0)
                    {
                        extraPageCount--;
                        pageIndex++;
                        p = (PdfPage) pageTree.getPage(pageIndex);
                    }
                    else
                    {
                        p = new PdfPage(p.pageWidth, p.pageHeight,
                            p.pageHeaderHeight, p.pageFooterHeight,
                            p.pageLeftMargin, p.pageTopMargin,
                            p.pageRightMargin, p.pageBottomMargin,
                            PdfMeasurement.MU_POINTS);

                        pageIndex++;
                        this.pageTree.insert(pageIndex, p);
                    }
                    rectY = p.pageTopMargin + p.pageHeaderHeight;
                    pageHeightLeft = pageHeightAvailable;
                }

                cellNotDrawn = true;
                while (cellNotDrawn)
                {
                    if (table.currentY >= table.colHeightArray[colNo])
                    {
                        c.width = 0;
                        int colDrawn = 0;
                        for (int j = colNo; (j < c.colSpan + colNo)
                            && (j < table.totalColumns)
                            && (table.currentY >= table.colHeightArray[j]); j++)
                        {
                            c.width += (float) table.colWidthArray[j];
                            colDrawn++;
                        }

                        for (int j = colNo; (j < c.colSpan + colNo)
                            && (j < table.totalColumns)
                            && (table.currentY >= table.colHeightArray[j]); j++)
                        {
                            table.colHeightArray[j] = table.colHeightArray[j]
                                + (table.rowHeight * c.rowSpan);
                        }

                        //if(colDrawn < c.colSpan)
                        c.colSpan = colDrawn;
                        if ((pageHeightLeft - (table.rowHeight * c.rowSpan)) >= 0)
                        {
                            drawCell(rectX, rectY, c,
                                table.rowHeight * c.rowSpan, p);
                            drawCellContent(rectX, rectY,
                                table.rowHeight * c.rowSpan, c, p);
                            colNo += c.colSpan;
                            cellNotDrawn = false;
                        }
                        else
                        {
                            cutHeight = 0;
                            temppageHeightLeft = pageHeightLeft;
                            PdfRect orginalRect = new PdfRect(rectX,
                                rectY, c.width, table.rowHeight
                                    * c.rowSpan);

                            while (temppageHeightLeft > 0)
                            {
                                temppageHeightLeft -= table.rowHeight;
                                if (temppageHeightLeft >= 0)
                                {
                                    cutHeight += table.rowHeight;
                                }
                            }
                            drawCell(rectX, rectY, c,
                                cutHeight, p);
                            drawCellContent(rectX, rectY, cutHeight,
                                c, p, orginalRect); 
                            tempCutHeight = cutHeight;

                            tempRowHeight = (table.rowHeight * c.rowSpan)
                                - cutHeight;
                            int tempPagecount = 0;
                            while (tempRowHeight > 0)
                            {
                                pageIndex++;
                                if (extraPageCount > 0)
                                {
                                    p = (PdfPage) pageTree
                                        .getPage(pageIndex);
                                    tempPagecount++;
                                    extraPageCount--;
                                }
                                else
                                {
                                    p = new PdfPage(p.pageWidth,
                                        p.pageHeight,
                                        p.pageHeaderHeight,
                                        p.pageFooterHeight,
                                        p.pageLeftMargin,
                                        p.pageTopMargin,
                                        p.pageRightMargin,
                                        p.pageBottomMargin,
                                        PdfMeasurement.MU_POINTS);

                                    this.pageTree.insert(pageIndex, p);
                                    tempPagecount++;
                                }
                                orginalRect.y = p.pageTopMargin + p.pageHeaderHeight
                                    - tempCutHeight;
                                tempCutHeight += cutHeight;

                                if (tempRowHeight <= pageHeightAvailable)
                                {
                                    drawCell(rectX, p.pageTopMargin + p.pageHeaderHeight,
                                        c, tempRowHeight, p);
                                    drawCellContent(rectX,
                                        p.pageTopMargin + p.pageHeaderHeight,
                                        tempRowHeight, c, p,
                                        orginalRect); //give cliprect
                                    tempRowHeight = tempRowHeight
                                        - pageHeightAvailable;
                                }
                                else
                                {
                                    cutHeight = 0;
                                    temppageHeightLeft = pageHeightAvailable;
                                    while (temppageHeightLeft > 0)
                                    {
                                        temppageHeightLeft -= table.rowHeight;
                                        if (temppageHeightLeft >= 0)
                                        {
                                            cutHeight += table.rowHeight;
                                        }
                                    }
                                    drawCell(rectX, p.pageTopMargin + p.pageHeaderHeight,
                                        c, cutHeight, p);
                                    drawCellContent(rectX,
                                        p.pageTopMargin + p.pageHeaderHeight, cutHeight,
                                        c, p, orginalRect);

                                    tempRowHeight = tempRowHeight
                                        - cutHeight;
                                }
                            }

                            pageIndex -= tempPagecount;
                            extraPageCount += tempPagecount;
                            p = (PdfPage) pageTree.getPage(pageIndex);
                            cellNotDrawn = false;
                            colNo += c.colSpan;
                        }
                    }
                    else
                    {
                        if (pageHeightLeft - table.rowHeight < 0)
                        {
                            pageIndex++;
                            if (extraPageCount > 0)
                            {
                                extraPageCount--;
                                p = (PdfPage) pageTree.getPage(pageIndex);
                            }
                            else
                            {
                                p = new PdfPage(p.pageWidth,
                                    p.pageHeight,
                                    p.pageHeaderHeight,
                                    p.pageFooterHeight,
                                    p.pageLeftMargin,
                                    p.pageTopMargin,
                                    p.pageRightMargin,
                                    p.pageBottomMargin,
                                    PdfMeasurement.MU_POINTS);

                                this.pageTree.insert(pageIndex, p);
                            }
                            rectY = p.pageTopMargin + p.pageHeaderHeight;
                            pageHeightLeft = pageHeightAvailable;
                            
                        }
                        colNo++;
                        if (colNo >= table.totalColumns)
                        {
                            pageHeightLeft = pageHeightLeft
                                - table.rowHeight;
                            rectY += table.rowHeight;
                            table.currentY += table.rowHeight;
                            table.currentX = x;
                            rectX = x;
                            colNo = 0;
                        }
                        else
                        {
                            table.currentX += table.colWidthArray[colNo - 1];
                            rectX = rectX
                                + table.colWidthArray[colNo - 1];
                        }
                    }
                }
                table.currentX += c.width;
                rectX += c.width;
            }
        }
    }

    protected void drawCell(double x, double y, PdfCell cell,
        double height, PdfPage p) throws IOException, PdfException
    {
        if(height == 0)
            return;
        x = PdfMeasurement.convertToMeasurementUnit(
            p.measurementUnit, x);
        y = PdfMeasurement.convertToMeasurementUnit(
            p.measurementUnit, y);
        double width = PdfMeasurement.convertToMeasurementUnit(
            p.measurementUnit, cell.width);
        height = PdfMeasurement.convertToMeasurementUnit(
            p.measurementUnit, height);
        double pageLeftMargin = p.pageLeftMargin;
        double pageTopMargin = p.pageTopMargin;
        double pageRightMargin = p.pageRightMargin;
        double pageBottomMargin = p.pageBottomMargin;
        double pageheaderHeight = p.pageHeaderHeight;
        double pageFooterHeight = p.pageFooterHeight;
        p.pageLeftMargin = 0; 
        p.pageTopMargin = 0;
        p.pageRightMargin = 0;
        p.pageBottomMargin = 0;
        p.pageHeaderHeight = 0;
        p.pageFooterHeight = 0;
        
        PdfPen tmPn = p.getPen();
        boolean fill = false;
        Color tmpBcColor = null;
        if(cell.pen != null)
        {
            p.setPen(cell.pen);
        }
        if(cell.backgroundColor != null)
        {
            if(p.getBrush() != null && p.getBrush().fillColor != null)
                tmpBcColor = p.getBrush().fillColor;
            fill = true;
            p.setBrushColor(cell.backgroundColor);
        }
        
        p.drawRect(x, y, width, height, fill, true);
        p.pageLeftMargin = pageLeftMargin; 
        p.pageTopMargin = pageTopMargin;
        p.pageRightMargin = pageRightMargin;
        p.pageBottomMargin = pageBottomMargin;
        p.pageHeaderHeight = pageheaderHeight;
        p.pageFooterHeight = pageFooterHeight;
        if(tmPn != null)
        {
            p.setPen(tmPn);
        }
        if(tmpBcColor != null)
        {
            p.setBrushColor(tmpBcColor);
        }
    }

    protected void drawCellContent(double x, double y,
        double colHeight, PdfCell c, PdfPage p) throws IOException,
        PdfException
    {
        if(colHeight == 0)
            return;
        x = PdfMeasurement.convertToMeasurementUnit(
            p.measurementUnit, x);
        y = PdfMeasurement.convertToMeasurementUnit(
            p.measurementUnit, y);
        double width = PdfMeasurement.convertToMeasurementUnit(
            p.measurementUnit, c.width);
        colHeight = PdfMeasurement.convertToMeasurementUnit(
            p.measurementUnit, colHeight);

        x += c.cellLeftMargin;
        y += c.cellTopMargin;
        width = width - (c.cellLeftMargin + c.cellRightMargin);
        colHeight = colHeight
            - (c.cellTopMargin + c.cellBottomMargin);

        if (c.image != null)
        {
            p.drawImage(c.image, new PdfRect(x, y, width,
                colHeight), null, p.measurementUnit);
        }
        if (c.text != null && c.text != "")
        {
            PdfFont f = p.prevFont;
            if(f == null)
            {
                f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
            }
            PdfTextFormatter tf = new PdfTextFormatter();
            tf.setAlignment(c.alignment);
            p.writeText(c.text, new PdfRect(x, y, width, colHeight),
                f, tf, p.measurementUnit, true);
        }
        if(c.formfield != null)
        {
            //PdfRect rect= new PdfRect(x, y, width, colHeight);
//            if(c.formfield instanceof PdfFormRadioButton )
//            {
//                PdfFormRadioButton tempRadBtn = (PdfFormRadioButton)c.formfield;
//                for( int i = 0; i < tempRadBtn.getItems().size(); i++)
//                {
//                    PdfFormRadioItem tempRadItm = (PdfFormRadioItem)tempRadBtn.getItems();
//                    if( tempRadItm.getRectangle().width > width)
//                    {
//                        tempRadItm.getRectangle().width = width - c.formfield.rect.x;
//                    }
//                    if( tempRadItm.getRectangle().height > colHeight)
//                    {
//                        tempRadItm.getRectangle().height = colHeight - c.formfield.rect.y;
//                    }
//                    tempRadItm.getRectangle().x += x;
//                    tempRadItm.getRectangle().y += y;
//                }
//            }
//            else
//            {
//                if( c.formfield.rect.width > width)
//                {
//                    c.formfield.rect.width = (width - c.formfield.rect.x);
//                }
//                if( c.formfield.rect.height > colHeight)
//                {
//                    c.formfield.rect.height = (colHeight - c.formfield.rect.y);
//                }
//                c.formfield.rect.x += x;
//                c.formfield.rect.y += y;
//            }
//            p.addFormField(c.formfield);
        }
    }

    protected void drawCellContent(double x, double y,
        double colHeight, PdfCell c, PdfPage p, PdfRect orginalRect)
        throws IOException, PdfException
    {
        if(colHeight == 0)
            return;
        x = PdfMeasurement.convertToMeasurementUnit(
            p.measurementUnit, x);
        y = PdfMeasurement.convertToMeasurementUnit(
            p.measurementUnit, y);
        double width = PdfMeasurement.convertToMeasurementUnit(
            p.measurementUnit, c.width);
        colHeight = PdfMeasurement.convertToMeasurementUnit(
            p.measurementUnit, colHeight);

        x += c.cellLeftMargin;
        y += c.cellTopMargin;
        width = width - (c.cellLeftMargin + c.cellRightMargin);
        colHeight = colHeight
            - (c.cellTopMargin + c.cellBottomMargin);

        PdfRect orgRect = new PdfRect(orginalRect.x, orginalRect.y,
            orginalRect.width, orginalRect.height);
        orgRect.x = PdfMeasurement.convertToMeasurementUnit(
            p.measurementUnit, orgRect.x);
        orgRect.y = PdfMeasurement.convertToMeasurementUnit(
            p.measurementUnit, orgRect.y);
        orgRect.width = PdfMeasurement.convertToMeasurementUnit(
            p.measurementUnit, orgRect.width);
        orgRect.height = PdfMeasurement.convertToMeasurementUnit(
            p.measurementUnit, orgRect.height);

        if (c.image != null)
        {
            p.drawImage(c.image, orgRect, new PdfRect(x, y, width,
                colHeight), p.measurementUnit);
        }
        if (c.text != null && c.text != "")
        {
            PdfFont f = p.prevFont;
            if(f == null)
            {
                f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
            }
            
            PdfTextFormatter tf = new PdfTextFormatter();
            tf.setAlignment(c.alignment);
            p.writeText(c.text, new PdfRect(x, y, width, colHeight),
                f, tf, p.measurementUnit, true);
            c.text = p.wrapText(c.text, new PdfRect(x, y, width,
                colHeight), 0, p.prevFont, p.measurementUnit);
        }
        if(c.formfield != null)
        {
            //PdfRect rect= new PdfRect(x, y, width, colHeight);
//            if(c.formfield instanceof PdfFormRadioButton )
//            {
//                PdfFormRadioButton tempRadBtn = (PdfFormRadioButton)c.formfield;
//                for( int i = 0; i < tempRadBtn.getItems().size(); i++)
//                {
//                    PdfFormRadioItem tempRadItm = (PdfFormRadioItem)tempRadBtn.getItems();
//                    if( tempRadItm.getRectangle().width > width)
//                    {
//                        tempRadItm.getRectangle().width = width - c.formfield.rect.x;
//                    }
//                    if( tempRadItm.getRectangle().height > colHeight)
//                    {
//                        tempRadItm.getRectangle().height = colHeight - c.formfield.rect.y;
//                    }
//                    tempRadItm.getRectangle().x += x;
//                    tempRadItm.getRectangle().y += y;
//                }
//            }
//            else
//            {
//                if( c.formfield.rect.width > width)
//                {
//                    c.formfield.rect.width = (width - c.formfield.rect.x);
//                }
//                if( c.formfield.rect.height > colHeight)
//                {
//                    c.formfield.rect.height = (colHeight - c.formfield.rect.y);
//                }
//                c.formfield.rect.x += x;
//                c.formfield.rect.y += y;
//            }
//            p.addFormField(c.formfield);
        }
    }

    /* Gets the list of annots of specified type */
    public synchronized void getAllAnnotations(int type,
        List listToPopulate) throws IOException, PdfException
    {
        PdfProPage p = null;

        for (int i = 1, limit = getPageCount(); i <= limit; ++i)
        {
            p = this.getPage(i);
            p.getAllAnnotations(type, listToPopulate);
        }
    }

    /* Gets the list of annots of all supported type */
    public synchronized void getAllAnnotations(List listToPopulate)
        throws IOException, PdfException
    {
        PdfProPage p = null;

        for (int i = 1, limit = getPageCount(); i <= limit; ++i)
        {
            p = this.getPage(i);
            p.getAllAnnotations(listToPopulate);
        }
    }

    /* Gets the list of annots of all supported type */
    public synchronized void getAllAnnotationsOnPage(int pageNo,
        List listToPopulate) throws IOException, PdfException
    {
        PdfPage p = this.getPage(pageNo);
        p.getAllAnnotations(listToPopulate);
    }

    /* Gets the list of annots of specified type */
    public synchronized void getAllAnnotationsOnPage(int pageNo,
        int type, List listToPopulate) throws PdfException,
        IOException
    {
        PdfPage p = this.getPage(pageNo);
        p.getAllAnnotations(type, listToPopulate);
    }

    /* Gets the list of annots of specified type to be edited */
    public synchronized List getAllAnnotations(int type)
        throws IOException, PdfException
    {
        HashMap hm = new HashMap();
        List pageAnnotList = null;
        PdfProPage p = null;

        for (int i = 1, limit = getPageCount(); i <= limit; ++i)
        {
            p = this.getPage(i);
            pageAnnotList = p.getAllAnnotations(type);
            if (pageAnnotList != null)
            {
                for (int j = 0, in_limit = pageAnnotList.size(); j < in_limit; ++j)
                {
                    hm.put(pageAnnotList.get(j), PdfNull.DUMMY);
                }
            }
        }
        ArrayList retVal = new ArrayList(hm.keySet());
        
        return retVal.size() > 0 ? retVal : null;
    }

    /* Gets the list of annots to be edited */
    public synchronized List getAllAnnotations() throws IOException,
        PdfException
    {
        HashMap hm = new HashMap();
        List pageAnnotList = null;
        PdfProPage p = null;

        for (int i = 1, limit = getPageCount(); i <= limit; ++i)
        {
            p = this.getPage(i);
            pageAnnotList = p.getAllAnnotations();
            if (pageAnnotList != null)
            {
                for (int j = 0, in_limit = pageAnnotList.size(); j < in_limit; ++j)
                {
                    hm.put(pageAnnotList.get(j), PdfNull.DUMMY);
                }
            }
        }
        ArrayList retVal = new ArrayList(hm.keySet());
        
        return retVal.size() > 0 ? retVal : null;
    }

    /* Gets the list of annots to be edited */
    public synchronized List getAllAnnotationsOnPage(int pageNo)
        throws PdfException, IOException
    {
        PdfPage p = this.getPage(pageNo);
        return p.getAllAnnotations();
    }

    /* Gets the list of annots of specified type to be edited */
    public synchronized List getAllAnnotationsOnPage(int pageNo, int type)
        throws PdfException, IOException
    {
        PdfPage p = this.getPage(pageNo);
        return p.getAllAnnotations(type);
    }

	/* Adds a list of newly created annots to page */
    /* Everything will be measured in page mu */
    public void addAnnotationList(List annotList, int pageNo,
        boolean removeExistingAnnots) throws PdfException
    {
        if (annotList != null)
        {
            PdfPage page = getPage(pageNo);
            for (int i = 0, limit = annotList.size(); i < limit; i++)
            {
                page.addAnnotation((PdfAnnot) annotList.get(i));
            }
            if (removeExistingAnnots)
            {
                /*
                 * Here all form fields are also removed. Devise a
                 * solution for this.
                 */
                page.rAnnotList = null;
            }
        }
    }

    /* Adds a list of newly created annots to page */
    /* Everything will be measured in page mu */
    public void addAnnotationList(List annotList, int pageNo)
        throws PdfException
    {
        addAnnotationList(annotList, pageNo, false);
    }

    /* Adds a list of newly created annots to pages */
    public void addAnnotationList(List annotList,
        String[] pageRanges, boolean removeExistingAnnots,
        int measurementUnit) throws PdfException
    {
        if (annotList != null && pageRanges != null)
        {
            Vector pages;
            PdfPage page;
            for (int i = 0; i < pageRanges.length; i++)
            {
                pages = getPageObjects(pageRanges[i]);
                if (pages != null)
                {
                    for (int j = 0, in_limit = pages.size(); j < in_limit; ++j)
                    {
                        page = (PdfPage) pages.get(j);
                        int mu = page.measurementUnit;
                        page.measurementUnit = measurementUnit;
                        page.addAnnotationList(annotList,
                            removeExistingAnnots);
                        page.measurementUnit = mu;
                    }
                }
            }
        }
    }

    /* Adds a list of newly created annots to pages */
    public void addAnnotationList(List annotList,
        String[] pageRanges, int measurementUnit) throws PdfException
    {
        addAnnotationList(annotList, pageRanges, false,
            measurementUnit);
    }

    /* This method should be used only when form field kids are to be
     * added to different pages. All involved pages mu should be same
     */ 
    public void addFormField(PdfFormField f, String[] pageRanges)
        throws PdfException
    {
        if (f == null  || f.parent != null) //only root level allowed
        {
            return;
        }
        if (fieldPageRanges == null)
        {
            fieldPageRanges = new HashMap();
        }
        fieldPageRanges.put(f, pageRanges);
    }

	/* Adds a list of formfields to a page */
    public void addFormFieldList(List formFieldList, int pageNo)
        throws PdfException
    {
        PdfPage page = getPage(pageNo);
        for (int i = 0, limit = formFieldList.size(); i < limit; ++i)
        {
            page.addFormField((PdfFormField) formFieldList.get(i));
        }
    }
	
    public synchronized void addThumbnailImage(String path, int pageNo)
        throws IOException, PdfException
    {
        getPage(pageNo).addThumbnailImage(path);
    }

    public synchronized void removeThumbnailImage(String pageRange)
        throws PdfException
    {
        Vector pages = getPageObjects(pageRange);
        for (Iterator iter = pages.iterator(); iter.hasNext();)
        {
            ((PdfPage) iter.next()).removeThumbnailImage();
        }
    }
}