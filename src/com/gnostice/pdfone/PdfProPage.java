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
import java.util.List;
import java.util.Map;

import com.gnostice.pdfone.encodings.PdfEncodings;
import com.gnostice.pdfone.fonts.PdfFont;

class PdfProPage extends PdfStdPage
{
    private static String fieldType;
    /* This is used in reading form hierarchy */

    private static int fieldFlags;
    /* This is used in reading form hierarchy */

    private static final PdfName KIDS = new PdfName(PDF_KIDS);

    private static final PdfName FIELD_TYPE = new PdfName(PDF_FT);

    private static final int ANNOT_SUPPORTED_CODE = 0;
    
    private static final int ANNOT_UNSUPPORTED_CODE = 1;
    
    private static final int ANNOT_WIDGET_CODE = 2;
    
    /* private static final int ANNOT_POPUP_CODE = 3; */
    
    List widgetList;
    
    /*
     * Above list is used only in reading mode. It contains
     * ind refs of all widgets on this page.
     */

    List annotsReturnList;
    
    /*
     * Above list is used only in reading mode. It contains
     * all read annot objs to be returned to the user for editing
     * on this page.
     */

    protected PdfImage thumbImage;
    
    protected ArrayList fieldList;

    /* no cloning and set to null after writing */
    protected ArrayList fieldIndRefList;
    
    protected double displayDuration;

    protected PdfPagePresentation presentation;

    ArrayList prepareAnnots(PdfStdDocument d) throws PdfException,
        IOException
    {
        ArrayList annList = super.prepareAnnots(d);

        if (fieldIndRefList != null)
        {
            annList.addAll(fieldIndRefList);
        }

        return annList;
    }

    void setUnknownAttributes(PdfStdDocument d) throws IOException,
        PdfException
    {
        setPresentation();
        super.setUnknownAttributes(d);
    }
    
    void setPresentation()
    {
        PdfName key;
        if (displayDuration != 0)
        {
            key = new PdfName(PDF_DISPLAY_DURATION);
            unknownAttributes.remove(key);
            this.dict.dictMap.put(key, new PdfFloat(displayDuration));
        }
        if (presentation != null)
        {
            key = new PdfName(PDF_TRANSITION);
            unknownAttributes.remove(key);
            this.dict.dictMap.put(key, presentation.prepareDict());
        }
    }

    void setThumbImage(PdfStdDocument d)
    {
        int objNo;
        Map dXObjMap = d.xObjMap;
        PdfObject dXObj = null;
        PdfInteger hash = new PdfInteger(thumbImage.hashCode());
        if (dXObjMap != null)
        {
            dXObj = (PdfObject) dXObjMap.get(hash);
        }
        if (dXObj != null)
        /* Image present in document */
        {
            objNo = ((PdfImage) dXObj).getObjectNumber();
        }
        else
        /* Image not present in the document */
        {
            //added for indexed Image
            PdfObject obj = thumbImage.colorSpace;
            if (obj instanceof PdfArray)
            {
                List l = ((PdfArray) obj).getList();
                PdfStream s = (PdfStream) l.get(3);
                s.setObjectNumber(d.objectRun++);
            }
            thumbImage.setObjectNumber(d.objectRun++);
            objNo = thumbImage.getObjectNumber();
            d.addImage(hash, thumbImage);
            objNo = thumbImage.getObjectNumber();
        }
        
        dict.dictMap.put(new PdfName(PDF_THUMB),
            new PdfIndirectReference(objNo, 0));
    }

    void setFields(PdfStdDocument d) throws IOException, PdfException
    {
        PdfFormField field, child;
        fieldIndRefList = new ArrayList();
        int size = fieldList.size();
        for (int i = size - 1; i >= 0; i--)
        {
            field = (PdfFormField) fieldList.get(i);
            if (field.dict.objNumber != 0)
            {
                continue;
                /* This is for the case where same FormField
                 * is added to different pages. */
            }
            
            /*field.encode(this); //with all descendents
            field.set(originDoc, d); //with all descendents*/
            
            field.set(this, d); //with all descendents

            ArrayList descendants = field.getAllDescendantWidgets();
            if (descendants != null)
            {
                for (int j = 0, in_limit = descendants.size(); j < in_limit; ++j)
                {
                    child = (PdfFormField) descendants.get(j);
                    fieldIndRefList.add(new PdfIndirectReference(
                        child.dict.objNumber, 0));
                }
            }
        }
    }

    /* Everything will be measured in points */
    void setEditedAnnotations() throws PdfException, IOException
    {
        int prevMU = this.measurementUnit;
        this.measurementUnit = PdfMeasurement.MU_POINTS;

        for (int i = 0, limit = annotsReturnList.size(); i < limit; i++)
        {
            addAnnotation((PdfAnnot) annotsReturnList.get(i));
        }

        this.measurementUnit = prevMU;
    }

    void setAnnots(PdfStdDocument d) throws IOException, PdfException
    {
        if (annotsReturnList != null)
        {
            setEditedAnnotations();
        }
        super.setAnnots(d);
    }

    protected void set(PdfStdDocument d) throws IOException,
        PdfException
    {
        if (!deleted)
        {
            super.set(d);
            if (fieldList != null)
            {
                ((PdfProDocument) d).needAcroDict = true;
                setFields(d);
            }
            if (thumbImage != null)
            {
                setThumbImage(d);
            }
        }
    }

    protected void write(PdfStdDocument d) throws IOException,
        PdfException
    {
        super.write(d);
        if (fieldList != null)
        {
            writeFields(d);
        }
    }

    void writeFields(PdfStdDocument d) throws IOException,
        PdfException
    {
        for (int i = 0; i < fieldList.size(); i++)
        {
            PdfFormField f = (PdfFormField) fieldList.get(i);
            f.write(d); //with all descendents
        }
    }

    protected PdfProPage()
    {
        super();
    }

    protected PdfProPage(int pageSize, double pageHeaderHeight,
        double pageFooterHeight, double pageLeftMargin,
        double pageTopMargin, double pageRightMargin,
        double pageBottomMargin, int measurementUnit)
    {
        super(pageSize, pageHeaderHeight, pageFooterHeight,
            pageLeftMargin, pageTopMargin, pageRightMargin,
            pageBottomMargin, measurementUnit);
    }

    protected PdfProPage(double width, double height,
        double pageHeaderHeight, double pageFooterHeight,
        double pageLeftMargin, double pageTopMargin,
        double pageRightMargin, double pageBottomMargin,
        int measurementUnit)
    {
        super(width, height, pageHeaderHeight, pageFooterHeight,
            pageLeftMargin, pageTopMargin, pageRightMargin,
            pageBottomMargin, measurementUnit);
    }
    
    public Object clone()
    {
        PdfProPage clone = (PdfProPage) super.clone();
        clone.fieldList = this.fieldList == null ? null
            : (ArrayList) this.fieldList.clone();
        if (this.presentation != null)
        {
            clone.presentation = (PdfPagePresentation) this.presentation
                .clone();
        }

        return super.clone();
    }

    public synchronized void addAnnotation(PdfAnnot annotation)
        throws PdfException
    {
        if (!(annotation instanceof PdfPopUpAnnot))
        {
            super.addAnnotation(annotation);
        }
    }
    
    /* Adds a list of newly created annots */
    public synchronized void addAnnotationList(List annotList)
        throws PdfException
    {
        if (annotList != null)
        {
            PdfAnnot annotation;

            for (int i = 0, limit = annotList.size(); i < limit; ++i)
            {
                annotation = (PdfAnnot) annotList.get(i);
                if ( !(annotation instanceof PdfPopUpAnnot))
                {
                    super.addAnnotation(annotation);
                }
            }
        }
    }

    /* Adds a list of newly created annots */
    public synchronized void addAnnotationList(List annotList,
        boolean removeExistingAnnots) throws PdfException
    {
        if (annotList != null)
        {
            PdfAnnot annotation;

            for (int i = 0, limit = annotList.size(); i < limit; ++i)
            {
                annotation = (PdfAnnot) annotList.get(i);
                if ( !(annotation instanceof PdfPopUpAnnot))
                {
                    super.addAnnotation(annotation);
                }
            }
            if (removeExistingAnnots)
            {
                rAnnotList = null;
            }
        }
    }

    public void addThumbnailImage(String path) throws IOException,
        PdfException
    {
        thumbImage = PdfImage.create(path);

        thumbImage.streamDict.getMap().put(new PdfName(PDF_WIDTH),
            new PdfInteger((int) thumbImage.width));
        thumbImage.streamDict.getMap().put(new PdfName(PDF_HEIGHT),
            new PdfInteger((int) thumbImage.height));
        thumbImage.streamDict.getMap().put(
            new PdfName(PDF_BITS_PER_COMPONENT),
            new PdfInteger(thumbImage.bitsPerComp));
        thumbImage.streamDict.getMap().put(
            new PdfName(PDF_COLORSPACE), thumbImage.colorSpace);
    }

    public void removeThumbnailImage()
    {
        this.dict.dictMap.remove(new PdfName(PDF_THUMB));
        this.thumbImage = null;
    }

    public void addFormField(PdfFormField f)
    {
        if (f == null || f.parent != null) // only root level allowed
        {
            return;
        }
        if (fieldList == null)
        {
            fieldList = new ArrayList();
        }
        fieldList.add(f);
    }

    public void addFormFieldList(List formFieldList)
    {
        if (formFieldList != null)
        {
            for (int i = 0, limit = formFieldList.size(); i < limit; ++i)
            {
                addFormField((PdfFormField) formFieldList.get(i));
            }
        }
    }

    public void addTable(PdfTable table, double x, double y)
        throws PdfException, IOException
    {
        if (table == null)
        {
            return;
        }

        PdfProPage p = this;
        if (p.prevFont == null)
        {
            p.prevFont = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        if ((PdfMeasurement.convertToPdfUnit(measurementUnit, y) > p.pageHeight)
            || (PdfMeasurement.convertToPdfUnit(p.measurementUnit, y) <= 0))
        {
            return;
        }

        // declaration & intialisation
        double pageHeightAvailable;
        boolean cellNotDrawn;
        int colNo = 0;
        double pageHeightLeft;
        double rectX, rectY;
        double cutHeight = 0;
        double temppageHeightLeft;

        x = PdfMeasurement.convertToPdfUnit(p.measurementUnit, x);
        x += PdfMeasurement.convertToPdfUnit(p.measurementUnit,
            p.pageLeftMargin);
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
                    break;
                }

                c.width = 0;
                for (int j = colNo; (j < c.colSpan + colNo)
                    && (j < table.totalColumns); j++)
                {
                    c.width += (float) table.colWidthArray[j];
                }

                if (c.text == null)
                {
                    c.text = "";
                }

                remStr = c.text;
                int noLines = 0;

                PdfRect rem = new PdfRect(c.x, c.y, c.width,
                    p.prevFont.getHeight());
                rem.width = rem.width
                    - PdfMeasurement.convertToPdfUnit(
                        PdfMeasurement.MU_POINTS,
                        (c.cellLeftMargin + c.cellRightMargin));
                rem.height = p.prevFont.getHeight();

                while (remStr != "")
                {

                    remStr = p.wrapText(remStr, rem, 0, p.prevFont,
                        PdfMeasurement.MU_POINTS);
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

                            drawCell(table.currentX, table.currentY,
                                pCell.width, cutHeight, p);
                            drawCellContent(table.currentX,
                                table.currentY, cutHeight, pCell, p,
                                orginalRect);

                            table.currentX += pCell.width;
                        }
                        break;
                    }
                    else
                    {
                        for (int k = 0; k < arrRect.size(); k++)
                        {
                            PdfCell pCell = (PdfCell) arrRect.get(k);
                            pCell.x = table.currentX;
                            pCell.y = table.currentY;
                            pCell.height = colheight;
                            drawCell(table.currentX, table.currentY,
                                pCell.width, colheight, p);

                            drawCellContent(table.currentX,
                                table.currentY, colheight, pCell, p);
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
                    break;
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

                        c.colSpan = colDrawn;
                        if ((pageHeightLeft - (table.rowHeight * c.rowSpan)) >= 0)
                        {
                            drawCell(rectX, rectY, c.width,
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
                            drawCell(rectX, rectY, c.width,
                                cutHeight, p);
                            drawCellContent(rectX, rectY, cutHeight,
                                c, p, orginalRect);
                            cellNotDrawn = false;

                            colNo += c.colSpan;
                        }

                    }
                    else
                    {
                        colNo++;
                        if (colNo >= table.totalColumns)
                        {
                            pageHeightLeft = pageHeightLeft
                                - table.rowHeight;
                            table.currentX = x;
                            rectX = x;
                            colNo = 0;
                            table.currentY += table.rowHeight;
                            rectY += table.rowHeight;
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

    protected void drawCell(double x, double y, double width,
        double height, PdfProPage p) throws IOException, PdfException
    {
        if (height == 0)
            return;
        x = PdfMeasurement.convertToMeasurementUnit(
            p.measurementUnit, x);
        y = PdfMeasurement.convertToMeasurementUnit(
            p.measurementUnit, y);
        width = PdfMeasurement.convertToMeasurementUnit(
            p.measurementUnit, width);
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
        p.drawRect(x, y, width, height);
        p.pageLeftMargin = pageLeftMargin;
        p.pageTopMargin = pageTopMargin;
        p.pageRightMargin = pageRightMargin;
        p.pageBottomMargin = pageBottomMargin;
        p.pageHeaderHeight = pageheaderHeight;
        p.pageFooterHeight = pageFooterHeight;
    }

    protected void drawCellContent(double x, double y,
        double colHeight, PdfCell c, PdfProPage p)
        throws IOException, PdfException
    {
        if (colHeight == 0)
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
            p.drawImage(c.image, new PdfRect(x, y, width, colHeight),
                null, p.measurementUnit);
        }
        if (c.text != null && c.text != "")
        {
            PdfFont f = p.prevFont;
            if (f == null)
            {
                f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
            }
            PdfTextFormatter tf = new PdfTextFormatter();
            tf.setAlignment(c.alignment);
            p.writeText(c.text, new PdfRect(x, y, width, colHeight),
                f, tf, p.measurementUnit, true);
        }
    }

    protected void drawCellContent(double x, double y,
        double colHeight, PdfCell c, PdfProPage p, PdfRect orginalRect)
        throws IOException, PdfException
    {
        if (colHeight == 0)
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
            if (f == null)
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
    }

    public synchronized double getDisplayDuration()
    {
        return displayDuration;
    }

    public synchronized void setDisplayDuration(double displayDuration)
    {
        this.displayDuration = displayDuration;
    }

    public synchronized PdfPagePresentation getPresentation()
    {
        return presentation;
    }

    public synchronized void setPresentation(
        PdfPagePresentation presentation)
    {
        this.presentation = (PdfPagePresentation) presentation
            .clone();
    }

    /*
     * DOC COMMENT: Only in reading mode. Returns if
     * invoked in creation mode
     */
    /* All measurements populated are in points */
    public void getAllAnnotations(List listToPopulate)
        throws IOException, PdfException
    {
        getAllAnnotations(PdfAnnot.ANNOT_TYPE_ALL, listToPopulate);
    }

    /*
     * DOC COMMENT: Only in reading mode. Returns if
     * invoked in creation mode
     */
    /* All measurements populated are in points */
    public void getAllAnnotations(int type, List listToPopulate)
        throws PdfException, IOException
    {
        if (originDoc == null || originDoc.reader == null)
        {
            return;
        }
        if (rAnnotList == null || listToPopulate == null)
        {
            return;
        }

        ArrayList annotList = readAnnotations(type, false);
        if ((annotList == null || annotList.isEmpty())
            && annotsReturnList != null)
        {
            PdfAnnot annotation;
            for (int i = 0, limit = annotsReturnList.size(); i < limit; ++i)
            {
                annotation = (PdfAnnot) annotsReturnList.get(i);
                if ((type & annotation.annotType) == annotation.annotType)
                {
                    listToPopulate.add(annotation.clone());
                }
            }

            return;
        }

        prepareAnnotObjects(annotList, listToPopulate, this);
    }

    /*
     * DOC COMMENT: Only in reading mode. Returns an empty List if
     * invoked in creation mode
     */
    /* All measurements should be changed in points */
    public synchronized List getAllAnnotations(int type) throws PdfException,
        IOException
    {
        if (originDoc == null || originDoc.reader == null)
        {
            return new ArrayList();
        }
        if (rAnnotList == null)
        {
            return new ArrayList();
        }
        if (annotsReturnList == null)
        {
            annotsReturnList = new ArrayList();
        }
        
        ArrayList annotList = readAnnotations(type, true);
        if (annotList == null || annotList.isEmpty())
        {
            ArrayList retList = new ArrayList();

            PdfAnnot annotation;
            for (int i = 0, limit = annotsReturnList.size(); i < limit; ++i)
            {
                annotation = (PdfAnnot) annotsReturnList.get(i);
                if ((type & annotation.annotType) == annotation.annotType)
                {
                    retList.add(annotation);
                }
            }

            return retList;
        }

        prepareAnnotObjects(annotList, annotsReturnList, this);
        
        return annotsReturnList;
    }

    /*
     * DOC COMMENT: Only in reading mode. Returns an empty List if
     * invoked in creation mode
     */
    /* All measurements should be changed in points */
    public synchronized List getAllAnnotations() throws PdfException,
        IOException
    {
        return getAllAnnotations(PdfAnnot.ANNOT_TYPE_ALL);
    }

    /* Only in reading mode */
    public synchronized List getAllFormFields() throws PdfException,
        IOException
    {
        if (originDoc == null || originDoc.reader == null)
        {
            return null;
        }
        
        readWidgets();
        
        if (rAnnotList != null)
        {
            rAnnotList.removeAll(widgetList);
        }
        
        return widgetList.isEmpty() ? new ArrayList()
            : createFieldHierarchy();
    }

    /*
     * Iterates through list of annot dicts and constructs PdfAnnot
     * objects out of it and adds it to annotsReturnList
     */
    private static void prepareAnnotObjects(List annotDictList,
        List annotsReturnList, PdfStdPage page) throws IOException,
        PdfException
    {
        PdfAnnot annotation = null;
        for (int i = 0, limit = annotDictList.size(); i < limit; i++)
        {
            PdfObject obj = (PdfObject) annotDictList.get(i);
            if ( !(obj instanceof PdfDict))
            {
                continue;
            }
            PdfDict rAnnotDict = (PdfDict) obj;
            PdfName nameSubType = new PdfName(PDF_SUBTYPE);
            if (rAnnotDict.dictMap.containsKey(nameSubType))
            {
                PdfObject objVal = (PdfObject) rAnnotDict.dictMap
                    .get(nameSubType);
                if (objVal instanceof PdfName)
                {
                    if (((PdfName) objVal).getString().equals(
                        PDF_TEXTANNOT))
                    {
                        annotation = new PdfTextAnnot();
                    }
                    else if (((PdfName) objVal).getString().equals(
                        PDF_LINEANNOT))
                    {
                        annotation = new PdfLineAnnot();
                    }
                    else if (((PdfName) objVal).getString().equals(
                        PDF_SQUAREANNOT))
                    {
                        annotation = new PdfSquareAnnot();
                    }
                    else if (((PdfName) objVal).getString().equals(
                        PDF_CIRCLEANNOT))
                    {
                        annotation = new PdfCircleAnnot();
                    }
                    else if (((PdfName) objVal).getString().equals(
                        PDF_POLYGONANNOT))
                    {
                        annotation = new PdfPolygonAnnot();
                    }
                    else if (((PdfName) objVal).getString().equals(
                        PDF_POLYLINEANNOT))
                    {
                        annotation = new PdfPolylineAnnot();
                    }
                    else if (((PdfName) objVal).getString().equals(
                        "Highlight")
                        || ((PdfName) objVal).getString().equals(
                            "Squiggly")
                        || ((PdfName) objVal).getString().equals(
                            "StrikeOut")
                        || ((PdfName) objVal).getString().equals(
                            "Underline"))
                    {
                        annotation = new PdfMarkupAnnot();
                    }
                    else if (((PdfName) objVal).getString().equals(
                        PDF_INK))
                    {
                        annotation = new PdfInkAnnot();
                    }
                    else if (((PdfName) objVal).getString().equals(
                        PDF_FREETEXTANNOT))
                    {
                        annotation = new PdfFreeTextAnnot();
                    }
                    else if (((PdfName) objVal).getString().equals(
                        PDF_LINKANNOT))
                    {
                        annotation = new PdfLinkAnnot();
                    }
                    else if (((PdfName) objVal).getString().equals(
                        PDF_CARETANNOT))
                    {
                        annotation = new PdfCaretAnnot();
                    }
                    else if (((PdfName) objVal).getString().equals(
                        PDF_STAMPANNOT))
                    {
                        annotation = new PdfStampAnnot();
                    }
                    else if (((PdfName) objVal).getString().equals(
                        PDF_FILEATTACHMENTANNOT))
                    {
                        annotation = new PdfFileAttachmentAnnot();
                    }
                    else
                    {
                        continue;
                    }
                }
            }
            annotation.applyPropertiesFrom(rAnnotDict, page);
            annotsReturnList.add(annotation);
        }
    }

    /*
     * Returns a list dictionaries of all supported annotations in the
     * page, keeping all formfields untouched.
     */
    private ArrayList readAnnotations(int requestedType,
        boolean remove) throws IOException, PdfException
    {
        ArrayList retList = new ArrayList();
        PdfObject annotObj;
        PdfDict annotDict;
        PdfName name_subtype = new PdfName(PDF_SUBTYPE);
        PdfName name_popup = new PdfName(PDF_POPUP);

        for (int i = 0; i < rAnnotList.size(); i++)
        /*
         * Here elements are removed from rAnnotList, hence compute
         * rAnnotList.size() always
         */
        {
            annotObj = originDoc.reader
                .getObject((PdfObject) rAnnotList.get(i));
            if (annotObj instanceof PdfDict)
            {
                annotDict = (PdfDict) annotObj;
                int annotCode = checkAnnotCode(annotDict
                    .getValue(name_subtype), requestedType);
                if (annotCode == ANNOT_SUPPORTED_CODE)
                {
                    retList.add(annotObj);
                    if (remove)
                    {
                        rAnnotList.remove(i--);
                        PdfObject popupVal = annotDict
                            .getValue(name_popup);
                        int popupIndex = rAnnotList.indexOf(popupVal);
                        if (popupIndex != -1)
                        {
                            rAnnotList.remove(popupVal);
                            if (popupIndex < i)
                            {
                                --i;
                            }
                        }
                    }
                }
            }
        }

        return retList.isEmpty() ? null : retList;
    }

    /*
     * Returns a list of ind refs of all supported widgets on the
     * page. This method is called internally when user wants to
     * edit or delete form fields.
     */
    private void readWidgets() throws IOException, PdfException
    {
        if (widgetList != null || originDoc == null
            || originDoc.reader == null)
        {
            return;
        }
        
        /*ArrayList widgets = new ArrayList();*/
        widgetList = new ArrayList();
        PdfObject annotObj, subtype;
        PdfName subtypeName = new PdfName(PDF_SUBTYPE);
        if (rAnnotList != null)
        {
            for (int i = 0, limit = rAnnotList.size(); i < limit; i++)
            {
                annotObj = originDoc.reader
                    .getObject((PdfObject) rAnnotList.get(i));
                if (annotObj instanceof PdfDict)
                {
                    subtype = ((PdfDict) annotObj)
                        .getValue(subtypeName);
                    if (checkAnnotCode(subtype,
                        PdfAnnot.ANNOT_TYPE_WIDGET) == ANNOT_WIDGET_CODE)
                    {
                        widgetList.add(rAnnotList.get(i));
                    }
                }
            }
        }
    }

    private int checkAnnotCode(PdfObject subtype, int requestedType)
    {
        int result = ANNOT_UNSUPPORTED_CODE;
        if (subtype instanceof PdfName)
        {
            String type = ((PdfName) subtype).getString();
            if (requestedType == PdfAnnot.ANNOT_TYPE_ALL)
            {
                if (type.equals(PDF_TEXT)
                    || type.equals(PDF_LINEANNOT)
                    || type.equals(PDF_CIRCLEANNOT)
                    || type.equals(PDF_SQUAREANNOT)
                    || type.equals(PDF_POLYGONANNOT)
                    || type.equals(PDF_POLYLINEANNOT)
                    || type.equals("Highlight")
                    || type.equals("Squiggly")
                    || type.equals("StrikeOut")
                    || type.equals("Underline")
                    || type.equals(PDF_INK)
                    || type.equals(PDF_LINKANNOT)
                    || type.equals(PDF_FREETEXTANNOT)
                    || type.equals(PDF_CARETANNOT)
                    || type.equals(PDF_STAMPANNOT)
                    || type.equals(PDF_FILEATTACHMENTANNOT))
                {
                    result = ANNOT_SUPPORTED_CODE;
                }
            }
            else
            {
                if (type.equals(PDF_WIDGET)
                    && (requestedType & PdfAnnot.ANNOT_TYPE_WIDGET)
                        == PdfAnnot.ANNOT_TYPE_WIDGET)
                {
                    result = ANNOT_WIDGET_CODE;
                }
                if (type.equals(PDF_TEXT)
                    && (requestedType & PdfAnnot.ANNOT_TYPE_TEXT)
                        == PdfAnnot.ANNOT_TYPE_TEXT)
                {
                    result = ANNOT_SUPPORTED_CODE;
                }
                if (type.equals(PDF_LINEANNOT)
                    && (requestedType & PdfAnnot.ANNOT_TYPE_LINE)
                        == PdfAnnot.ANNOT_TYPE_LINE)
                {
                    result = ANNOT_SUPPORTED_CODE;
                }
                if (type.equals(PDF_CIRCLEANNOT)
                    && (requestedType & PdfAnnot.ANNOT_TYPE_CIRCLE)
                        == PdfAnnot.ANNOT_TYPE_CIRCLE)
                {
                    result = ANNOT_SUPPORTED_CODE;
                }
                if (type.equals(PDF_SQUAREANNOT)
                    && (requestedType & PdfAnnot.ANNOT_TYPE_SQUARE)
                        == PdfAnnot.ANNOT_TYPE_SQUARE)
                {
                    result = ANNOT_SUPPORTED_CODE;
                }
                if (type.equals(PDF_POLYGONANNOT)
                    && (requestedType & PdfAnnot.ANNOT_TYPE_POLYGON)
                        == PdfAnnot.ANNOT_TYPE_POLYGON)
                {
                    result = ANNOT_SUPPORTED_CODE;
                }
                if (type.equals(PDF_POLYLINEANNOT)
                    && (requestedType & PdfAnnot.ANNOT_TYPE_POLYLINE)
                        == PdfAnnot.ANNOT_TYPE_POLYLINE)
                {
                    result = ANNOT_SUPPORTED_CODE;
                }
                if ((type.equals("Highlight")
                    || type.equals("Squiggly")
                    || type.equals("StrikeOut")
                    || type.equals("Underline"))
                    && (requestedType & PdfAnnot.ANNOT_TYPE_MARKUP)
                    == PdfAnnot.ANNOT_TYPE_MARKUP)
                {
                    result = ANNOT_SUPPORTED_CODE;
                }
                if (type.equals(PDF_INK)
                    && (requestedType & PdfAnnot.ANNOT_TYPE_INK)
                        == PdfAnnot.ANNOT_TYPE_INK)
                {
                    result = ANNOT_SUPPORTED_CODE;
                }
                if (type.equals(PDF_LINKANNOT)
                    && (requestedType & PdfAnnot.ANNOT_TYPE_LINK)
                        == PdfAnnot.ANNOT_TYPE_LINK)
                {
                    result = ANNOT_SUPPORTED_CODE;
                }
                if (type.equals(PDF_FREETEXTANNOT)
                    && (requestedType & PdfAnnot.ANNOT_TYPE_FREE_TEXT)
                        == PdfAnnot.ANNOT_TYPE_FREE_TEXT)
                {
                    result = ANNOT_SUPPORTED_CODE;
                }
                if (type.equals(PDF_CARETANNOT)
                    && (requestedType & PdfAnnot.ANNOT_TYPE_CARET)
                        == PdfAnnot.ANNOT_TYPE_CARET)
                {
                    result = ANNOT_SUPPORTED_CODE;
                }
                if (type.equals(PDF_STAMPANNOT)
                    && (requestedType & PdfAnnot.ANNOT_TYPE_STAMP)
                        == PdfAnnot.ANNOT_TYPE_STAMP)
                {
                    result = ANNOT_SUPPORTED_CODE;
                }
                if (type.equals(PDF_FILEATTACHMENTANNOT)
                    && (requestedType & PdfAnnot.ANNOT_TYPE_FILE_ATTACHMENT)
                        == PdfAnnot.ANNOT_TYPE_FILE_ATTACHMENT)
                {
                    result = ANNOT_SUPPORTED_CODE;
                }
            }
        }
        
        return result;
    }

    private void initializeFieldHierarchy(PdfFormField root)
        throws IOException, PdfException
    {
        root.exportValues = null;
        root.mode = PdfDocument.READING_MODE;
        root.applyPropertiesFrom(root.dict, this);
        if (root.kids != null)
        {
            PdfFormField kid;
            for (int i = 0, limit = root.kids.size(); i < limit; ++i)
            {
                kid = (PdfFormField) root.kids.get(i);
                initializeFieldHierarchy(kid);
            }
        }
    }
    
    private List createFieldHierarchy() throws IOException,
        PdfException
    {
        ArrayList retList = new ArrayList();
        PdfFormField root;
        PdfDict widget;
        
        while ( !widgetList.isEmpty())
        {
            /*
             * Here before buiding the hierarchy. Store /Sig fields
             * somewhere
             */
            fieldType = null;
            fieldFlags = 0;
            widget = (PdfDict) originDoc.reader
                .getObject((PdfObject) widgetList.get(0));

            root = widget.dictMap.containsKey(PARENT)
                ? buildFieldHierarchy(getRootFieldDict(widget), retList)
                : buildFormWidgetNode(widget, retList, false);
            
            initializeFieldHierarchy(root);
            addFormField(root);
        }

        return retList;
    }
    
    private PdfFormField buildFieldHierarchy(PdfDict fieldDict,
        List retList) throws IOException, PdfException
    {
        PdfFormField retVal = null;
        
//        PdfNumber flag = (PdfNumber) fieldDict.getValue(new PdfName(
//            PDF_FIELD_FLAG));
//        if (flag != null)
//        {
//            fieldFlags = flag.getInt();
//        }

        if (fieldDict.dictMap.containsKey(KIDS)) //intermediate
        {
            retVal = buildFormIntermediateNode(fieldDict);
            
            /*
             * Here all kids are being read. Instead, we have to read
             * only those which are present on this page. i.e. only in
             * the widgetList
             */
            PdfObject kids = originDoc.reader.getObject(fieldDict
                .getValue(KIDS));
            List kidsList = ((PdfArray) kids).getList();
            for (int i = 0, limit = kidsList.size(); i < limit; ++i)
            {
                PdfObject obj = (PdfObject) kidsList.get(i);
                widgetList.remove(obj);
                PdfDict widget = (PdfDict) originDoc.reader
                    .getObject(obj);
                PdfFormField child = buildFieldHierarchy(widget,
                    retList);
                retVal.addChildField(child);

                if (child.level == PdfFormField.LEVEL_TERMINAL && 
                    (child.type == PdfFormField.TYPE_RADIOGROUP
                    || child.type == PdfFormField.TYPE_CHECKGROUP))
                {
                    retVal.applyExportValueTo(child, this);
                    if (!retList.contains(retVal))
                    {
                        retVal.type = child.type;
                        retList.add(retVal);
                    }
                }
            }
        }
        else //widget
        {
            retVal = buildFormWidgetNode(fieldDict, retList, true);
        }
        
        return retVal;
    }

    private PdfFormField buildFormIntermediateNode(PdfDict fieldDict)
        throws IOException, PdfException
    {
        PdfFormField retVal = null;

        PdfName ft = (PdfName) fieldDict.getValue(FIELD_TYPE);
        if (ft != null)
        {
            fieldType = ft.getString();
        }

        PdfNumber flag = (PdfNumber) fieldDict.getValue(new PdfName(
            PDF_FIELD_FLAG));
        if (flag != null)
        {
            fieldFlags = flag.getInt();
        }

        if (fieldType != null)
        {
            if (fieldType.equalsIgnoreCase(PDF_TX))
            {
                retVal = new PdfFormField(PdfFormField.TYPE_TEXTFIELD);
            }
            else if (fieldType.equalsIgnoreCase(PDF_BTN))
            {
                retVal = new PdfFormField(PdfFormField.TYPE_PUSHBUTTON);
            }
            else if (fieldType.equalsIgnoreCase(PDF_CH))
            {
                retVal = new PdfFormField(PdfFormField.TYPE_LISTBOX);
            }
        }
        else
        {
            retVal = new PdfFormField();
        }

        retVal.dict = fieldDict;
        
        return retVal;
    }

    private PdfFormField buildFormWidgetNode(PdfDict fieldDict,
        List retList, boolean isHierarchy) throws IOException,
        PdfException
    {
        PdfFormField retVal = null;

        PdfName ft = (PdfName) fieldDict
            .getValue(new PdfName(PDF_FT));
        if (ft == null && fieldType == null)
        {
            throw new PdfBadFileException("Field type not specified");
        }
        else if (ft != null)
        {
            fieldType = ft.getString();
        }
        
        PdfNumber flag = (PdfNumber) fieldDict.getValue(new PdfName(
            PDF_FIELD_FLAG));
        if (flag != null)
        {
            fieldFlags = flag.getInt();
        }

        if (fieldType.equalsIgnoreCase(PDF_TX))
        {
            retVal = new PdfFormTextField();
            retList.add(retVal);
        }
        else if (fieldType.equalsIgnoreCase(PDF_BTN))
        {
            retVal = PdfFormButtonField.getInstance(fieldFlags);
            if (!isHierarchy)
            {
                retList.add(retVal);
            }
            else if (retVal.type == PdfFormField.TYPE_PUSHBUTTON)
            {
                retList.add(retVal);
            }
        }
        else if (fieldType.equalsIgnoreCase(PDF_CH))
        {
            retVal = PdfFormChoiceField.getInstance(fieldFlags);
            retList.add(retVal);
        }

        widgetList.remove(new PdfIndirectReference(
            fieldDict.objNumber, fieldDict.genNumber));

        retVal.dict = fieldDict;
        
        return retVal;
    }
    
    private PdfDict getRootFieldDict(PdfDict childDict)
        throws IOException, PdfException
    {
        if ( !childDict.dictMap.containsKey(PARENT))
        {
            return childDict;
        }

        return getRootFieldDict((PdfDict) originDoc.reader
            .getObject(childDict.getValue(PARENT)));

    }
}