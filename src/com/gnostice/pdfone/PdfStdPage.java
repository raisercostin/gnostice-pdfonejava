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
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.gnostice.pdfone.encodings.PdfEncodings;
import com.gnostice.pdfone.filters.PdfFilter;
import com.gnostice.pdfone.fonts.PdfFont;
import com.gnostice.pdfone.graphics.PdfBrush;
import com.gnostice.pdfone.graphics.PdfPen;

class PdfStdPage extends PdfNode implements Usable, Cloneable
{
    /**
     * Constant to specify horizontal alignment to the left (inside
     * header, footer and watermarks)
     */
    public static final int HP_LEFT = 1;
    
    /**
     * Constant to specify horizontal alignment to the middle (inside 
     * header, footer and watermarks)
     */
    public static final int HP_MIDDLE = 2;
    
    /**
     * Constant to specify horizontal alignment to the right (inside 
     * header, footer and watermarks)
     */
    public static final int HP_RIGHT = 4;
    
    /**
     * Constant to specify vertical alignment to the top (inside 
     * header, footer and watermarks)
     */
    public static final int VP_TOP = 16;
    
    /**
     * Constant to specify vertical alignment to the center (inside 
     * header, footer and watermarks)
     */
    public static final int VP_CENTRE = 32;
    
    /**
     * Constant to specify vertical alignment to the bottom (inside 
     * header, footer and watermarks)
     */
    public static final int VP_BOTTOM = 64;
    
    /**
     * Constant to specify z-order underlay 
     */
    public static final boolean UNDERLAY = true;
    
    /**
     * Constant to specify z-order overlay
     */
    public static final boolean OVERLAY = false;
    
    protected static final PdfName CONTENTS = new PdfName(
        PDF_CONTENTS);

    protected static final PdfName FONT = new PdfName(PDF_FONT);
    
    protected static final PdfName XOBJ = new PdfName(PDF_XOBJECT);

    protected static final PdfName PAGE = new PdfName(PDF_PAGE);

    protected static final PdfName PROCSET = new PdfName(PDF_PROCSET);

    protected static final PdfName ANNOTS = new PdfName(PDF_ANNOTS);
    
    protected static final PdfName MEDIA = new PdfName(PDF_MEDIABOX);

    protected static final PdfName CROP = new PdfName(PDF_CROPBOX);
    
    protected static final PdfName RESOURCES = new PdfName(
        PDF_RESOURCES);

    protected static int imageCount = 1;
    
    static HashMap knownAttributes;
    
    static ArrayList inheritableProperties;
    
    private boolean marginsActive = true;
    
    int mode;
    
    protected double pageHeight;

    protected double pageWidth;
    
    protected double pageCropLeft;

    protected double pageCropBottom;

    protected double pageCropRight;

    protected double pageCropTop;

    protected int underlayStreamCount;
    
    protected PdfByteOutputStream contentStream;
    
    protected PdfByteOutputStream pageRotationStartStream;
    
    protected PdfByteOutputStream pageRotationEndStream;
    
    protected ArrayList rContentList;
    
    protected ArrayList contentList;

    protected PdfDict fontDict;
    
    protected PdfDict fontIndRefDict;
    
    protected PdfDict xObjDict;
    
    protected PdfDict xObjIndRefDict;

    protected List openActionList;

    protected List closeActionList;

    protected PdfDict actionDict;

    protected int measurementUnit;
    
    protected PdfTextFormatter tf;

    protected PdfPen pen;

    protected PdfPen prevPen;

    protected PdfBrush brush;

    protected PdfBrush prevBrush;

    protected PdfFont prevFont;

    protected boolean isLastItemShape;
    
    protected ArrayList annotList;
    
    protected ArrayList rAnnotList;
    
    protected HashMap rResources;
    
    protected double currentX;

    protected double currentY;

    protected double cursorPosX;

    protected double cursorPosY;
    
    protected boolean moveCursor;
    
    protected int rotation;
    
    protected ArrayList patternList;
    
    protected double pageLeftMargin;
    
    protected double pageTopMargin;
    
    protected double pageRightMargin;
    
    protected double pageBottomMargin;
    
    protected double pageHeaderHeight;
    
    protected double pageFooterHeight; 

    protected double pagePrevLeftMargin;
    
    protected double pagePrevTopMargin;
    
    protected double pagePrevRightMargin;
    
    protected double pagePrevBottomMargin;
    
    protected double pagePrevHeaderHeight;
    
    protected double pagePrevFooterHeight; 

    protected ArrayList fieldsAnnot;
    
    protected ArrayList userVariableWriteText;
    
    HashMap unknownAttributes = new HashMap();
    
    private boolean isPlaceHolderProcessed = false;
    
    static
    {
        knownAttributes = new HashMap();
        knownAttributes.put(PDF_CONTENTS, PdfNull.DUMMY);
        knownAttributes.put(PDF_ANNOTS, PdfNull.DUMMY);
        knownAttributes.put(PDF_PARENT, PdfNull.DUMMY);
        knownAttributes.put(PDF_TYPE, PdfNull.DUMMY);
        
        inheritableProperties = new ArrayList();
        inheritableProperties.add(new PdfName(PDF_RESOURCES));
        inheritableProperties.add(new PdfName(PDF_MEDIABOX));
        inheritableProperties.add(new PdfName(PDF_CROPBOX));
        inheritableProperties.add(new PdfName(PDF_BLEEDBOX));
        inheritableProperties.add(new PdfName(PDF_TRIMBOX));
        inheritableProperties.add(new PdfName(PDF_ARTBOX));
        inheritableProperties.add(new PdfName(PDF_ROTATE));
    }
    
    public Object clone()
    {
        PdfStdPage clone = null;
        try
        {
            clone = (PdfStdPage) super.clone();
            clone.annotList = this.annotList == null ? null
                : (ArrayList) this.annotList.clone();
            clone.contentList = this.contentList == null ? null
                : (ArrayList) this.contentList.clone();
            clone.fieldsAnnot = this.fieldsAnnot == null ? null
                : (ArrayList) this.fieldsAnnot.clone();
            clone.patternList = this.patternList == null ? null
                : (ArrayList) this.patternList.clone();

            clone.actionDict = this.actionDict == null ? null
                : (PdfDict) this.actionDict.clone();

            HashMap hm = new HashMap();
            if (this.fontDict != null)
            {
                Map dictMap = this.fontDict.dictMap;
                for (Iterator iter = dictMap.keySet().iterator(); iter
                    .hasNext();)
                {
                    PdfObject key = (PdfObject) iter.next();
                    PdfFont val = (PdfFont) dictMap.get(key);
                    hm.put(key.clone(), val.clone());
                }
                clone.fontDict = new PdfDict(hm);
            }
            
            clone.unknownAttributes = (HashMap) this.unknownAttributes
                .clone();
            
            clone.fontIndRefDict = this.fontIndRefDict == null ? null
                : (PdfDict) this.fontIndRefDict.clone();
            clone.xObjDict = this.xObjDict == null ? null
                : (PdfDict) this.xObjDict.clone();
            clone.xObjIndRefDict = this.xObjIndRefDict == null ? null
                : (PdfDict) this.xObjIndRefDict.clone();
            clone.brush = this.brush == null ? null
                : (PdfBrush) this.brush.clone();
            clone.tf = this.tf == null ? null
                : (PdfTextFormatter) this.tf.clone();
            clone.pen = this.pen == null ? null : (PdfPen) this.pen
                .clone();

            clone.prevFont = this.prevFont == null ? null
                : (PdfFont) this.prevFont.clone();
            clone.prevBrush = this.prevBrush == null ? null
                : (PdfBrush) this.prevBrush.clone();
            clone.prevPen = this.prevPen == null ? null
                : (PdfPen) this.prevPen.clone();

            if (this.openActionList != null)
            {
                clone.openActionList = new ArrayList();
                for (Iterator i = this.openActionList.iterator(); i
                    .hasNext();)
                {
                    clone.openActionList.add(((PdfObject) i.next())
                        .clone());
                }
            }
            if (this.closeActionList != null)
            {
                clone.closeActionList = new ArrayList();
                for (Iterator i = this.closeActionList.iterator(); i
                    .hasNext();)
                {
                    clone.closeActionList.add(((PdfObject) i.next())
                        .clone());
                }
            }
            if (this.contentStream != null)
            {
                clone.contentStream = new PdfByteOutputStream();
                try
                {
                    this.contentStream.writeTo(clone.contentStream);
                }
                catch (IOException io)
                {
                    clone.contentStream = null;
                }
            }
            if (this.pageRotationEndStream != null)
            {
                clone.pageRotationEndStream = new PdfByteOutputStream();
                try
                {
                    this.pageRotationEndStream
                        .writeTo(clone.pageRotationEndStream);
                }
                catch (IOException io)
                {
                    clone.pageRotationEndStream = null;
                }
            }
            if (this.pageRotationStartStream != null)
            {
                clone.pageRotationStartStream = new PdfByteOutputStream();
                try
                {
                    this.pageRotationStartStream
                        .writeTo(clone.pageRotationStartStream);
                }
                catch (IOException io)
                {
                    clone.pageRotationStartStream = null;
                }
            }
            if (rAnnotList != null)
            {
                clone.rAnnotList = (ArrayList) this.rAnnotList.clone();
            }
            if (rContentList != null)
            {
                clone.rContentList = (ArrayList) this.rContentList
                    .clone();
            }
            if (rResources != null)
            {
                clone.rResources = (HashMap) this.rResources.clone();
            }

            clone.dict = (PdfDict) this.dict.clone();

        }
        catch (CloneNotSupportedException cnse)
        {
            
        }
        
        return clone;
    }

    protected void reset()
    {
        int limit = (annotList == null) ? 0 : annotList.size();
        for (int i = 0; i < limit; i++)
        {
            PdfAnnot annot = (PdfAnnot) annotList.get(i);
            if (annot.isWritten)
            {
                /*This is for reusability of annot objects across
                documents & pages i.e. if same annot is reused
                in multiple pages or docs*/
                annot.isWritten = false;
                annot.dict.objNumber = 0;
            }
        }
    }
    
    private PdfObject inheritFrom(PdfNode node, PdfName property)
    {
        PdfObject value = null;
        if (node != null)
        {
            value = node.dict.getValue(property);
            if (value == null)
            {
                value = inheritFrom(node.parent, property);
            }
        }
        
        return value;
    }
    
    private void inheritProperties()
    {
        Map dictMap = dict.getMap();
        PdfName property;

        for (int i = 0, limit = inheritableProperties.size(); i < limit; ++i)
        {
            property = (PdfName) inheritableProperties.get(i);
            if ( !dictMap.containsKey(property))
            {
                PdfObject value = inheritFrom(this.parent, property);
                if (value != null)
                {
                    dictMap.put(property, value);
                }
            }
        }
    }
    
    protected void read(PdfStdDocument d) throws IOException,
        PdfException
	{
		if (originDoc == null)
		{
		    originDoc = d;
            this.mode = PdfDocument.READING_MODE;
		}
        
        Map dictMap = dict.getMap();
        dictMap.remove(PARENT);
        Iterator iter = dictMap.keySet().iterator();
        String name;
        PdfObject key, value;
        double[] mr = null;
        double[] cr = null;
        
        inheritProperties();
        
        while (iter.hasNext())
        {
            key = (PdfObject) iter.next();
            value = originDoc.reader.getObject((PdfObject) dictMap
                .get(key));
            if ( !(value instanceof PdfNull))
            {
                name = ((PdfName) key).getString();
                if (name.equals(Usable.PDF_MEDIABOX))
                {
                    PdfArray mediaBox = (PdfArray) value;
                    mr = readRegionBox(mediaBox);
                    unknownAttributes.put(key, value);
                }
                else if (name.equals(Usable.PDF_CROPBOX))
                {
                    PdfArray cropBox = (PdfArray) value;
                    cr = readRegionBox(cropBox);
                    unknownAttributes.put(key, value);
                }
                else if (name.equals(Usable.PDF_RESOURCES))
                {
                    rResources = (HashMap) ((PdfDict) value).getMap();
                }
                else if (name.equals(Usable.PDF_ROTATE))
                {
                    this.rotation = (int) ((PdfNumber) value)
                        .getVal();
                }
                else if (name.equals(Usable.PDF_ANNOTS))
                {
                    rAnnotList = (ArrayList) ((PdfArray) value)
                        .getList();
                }
                else 
                {
                    if ( !knownAttributes.containsKey(name))
                    {
                        unknownAttributes.put(key, value);
                    }
                }
            }//of if !PdfNull
        } //of while

        if (mr != null && cr == null)
        {
            this.pageWidth = mr[2] - mr[0];
            this.pageHeight = mr[3] - mr[1];
        }
        else if (mr != null && cr != null)
        {
            this.pageCropLeft = cr[0];
            this.pageCropRight = mr[2] - cr[2];
            this.pageCropBottom = cr[1];
            this.pageCropTop = mr[3] - cr[3];
            this.pageWidth = mr[2] - mr[0];
            this.pageHeight = mr[3] - mr[1];
        }
        else if (cr != null && mr == null)
        {
            this.pageWidth = cr[2] - cr[0];
            this.pageHeight = cr[3] - cr[1];
        }
        else
        {
            this.pageWidth = 612;
            this.pageHeight = 792;
        }
        
		// Read contents
		rContentList = d.readPageStream(this);

        if (this.rotation != 0 && this.rotation % 180 != 0)
        {
            double temp = pageWidth;
            pageWidth = pageHeight;
            pageHeight = temp;
        }
        if (this.rotation == 180)
        {
            double temp = pageCropLeft;
            pageCropLeft = pageCropRight;
            pageCropRight = temp;
            temp = pageCropBottom;
            pageCropBottom = pageCropTop;
            pageCropTop = temp;
        }
        else if (this.rotation == 90)
        {
            double temp = pageCropTop;
            pageCropTop = pageCropLeft;
            pageCropLeft = pageCropBottom;
            pageCropBottom = pageCropRight;
            pageCropRight = temp;
        }
        else if (this.rotation == 270)
        {
            double temp = pageCropTop;
            pageCropTop = pageCropRight;
            pageCropRight = pageCropBottom;
            pageCropBottom = pageCropLeft;
            pageCropLeft = temp;
        }
	}

    private double[] readRegionBox(PdfArray regionBox)
    {
        double[] region = new double[4];
        List regionList = regionBox.getList();
        
        if (regionList.size() >= 4)
        {
            PdfObject regionEntry;
            for (int i = 0; i < 4; ++i)
            {
                regionEntry = (PdfObject) regionList.get(i);
                if (regionEntry instanceof PdfNumber)
                {
                    region[i] = ((PdfNumber) regionEntry)
                        .getVal();
                }
            }
        }
        
        return region;
    }

    protected PdfPoint updatePageSettings(PdfPoint point)
    {
        return updatePageSettings(point, measurementUnit);
    }
    
    protected PdfPoint updatePageSettings(PdfPoint point, int unit)
    {
        double pointX = PdfMeasurement.convertToPdfUnit(
            unit, point.x);
        double pointY = PdfMeasurement.convertToPdfUnit(
            unit, point.y);

        double xLimit = this.pageWidth - this.pageRightMargin
            - this.pageCropRight;
        double yLimit = this.pageHeight - this.pageCropBottom
            - this.pageBottomMargin - this.pageFooterHeight;

        pointX += this.pageLeftMargin + this.pageCropLeft;
        pointY += this.pageTopMargin + this.pageHeaderHeight
            + this.pageCropTop;

        if (pointX < pageLeftMargin + pageCropLeft)
        {
            pointX = pageLeftMargin + pageCropLeft;
        }
        if (pointX > xLimit)
        {
            pointX = xLimit;
        }

        if (pointY < pageTopMargin + pageCropTop + pageHeaderHeight)
        {
            pointY = pageTopMargin + pageCropTop + pageHeaderHeight;
        }
        if (pointY > yLimit)
        {
            pointY = yLimit;
        }

        return new PdfPoint(PdfMeasurement.convertToMeasurementUnit(
            unit, pointX), PdfMeasurement
            .convertToMeasurementUnit(unit, pointY));
    }

    protected PdfRect updatePageSettings(PdfRect rect)
    {
        return updatePageSettings(rect, measurementUnit);
    }

    protected PdfRect updatePageSettings(PdfRect rect, int unit)
    {
        double rectWidth = PdfMeasurement.convertToPdfUnit(unit,
            rect.width);
        double rectHeight = PdfMeasurement.convertToPdfUnit(unit,
            rect.height);
        double rectX = PdfMeasurement.convertToPdfUnit(unit, rect.x);
        double rectY = PdfMeasurement.convertToPdfUnit(unit, rect.y);

        if (rectX < 0)
        {
            rectX = 0;
        }
        if (rectY < 0)
        {
            rectY = 0;
        }
        
        rectY += this.pageTopMargin + this.pageHeaderHeight
            + this.pageCropTop;
        rectX += this.pageLeftMargin + this.pageCropLeft;
        
        double availWidth = this.pageWidth - this.pageRightMargin
            - rectX - pageCropRight;
        double availHeight = this.pageHeight - this.pageBottomMargin
            - pageFooterHeight - rectY - pageCropBottom;
        
        /*if (availWidth <= 0 || availHeight <= 0)
        {
            return null;
        }
        if (rectY > this.pageHeight - this.pageBottomMargin
            - this.pageCropBottom - this.pageFooterHeight)
        {
            return null;
        }
        if (rectX > this.pageWidth - this.pageRightMargin
            - this.pageCropRight)
        {
            return null;
        }*/

        if (rectWidth > availWidth)
        {
            rectWidth = availWidth;
        }
        if (rectHeight > availHeight)
        {
            rectHeight = availHeight;
        }
        rectX = PdfMeasurement.convertToMeasurementUnit(unit, rectX);
        rectY = PdfMeasurement.convertToMeasurementUnit(unit, rectY);
        rectWidth = PdfMeasurement.convertToMeasurementUnit(unit, rectWidth);
        rectHeight = PdfMeasurement.convertToMeasurementUnit(unit, rectHeight);

        return new PdfRect(rectX, rectY, rectWidth, rectHeight);
    }
    
    void setUnknownAttributes(PdfStdDocument d)
        throws IOException, PdfException
    {
        if ( !unknownAttributes.isEmpty())
        {
            PdfObject key;
            PdfObject value;
            for (Iterator iter = unknownAttributes.keySet()
                .iterator(); iter.hasNext();)
            {
                key = (PdfObject) iter.next();
                value = (PdfObject) dict.getValue(key);
                if (value != null)
                {
                    d.updateIndirectRefs(originDoc, value, true);
                }
            }
        }
    }
    
    protected void setXObjDict(PdfStdDocument d)
    {
        Map pXObjDict = xObjDict.getMap();
        Map pXObjIndRefDict = xObjIndRefDict.getMap();
        Map dXObjMap = d.xObjMap;
        PdfObject dXObj = null;
        int objNo;
        for (Iterator iter = pXObjDict.keySet().iterator(); iter
            .hasNext();)
        {
            PdfObject key = (PdfObject) iter.next();
            PdfImage value = (PdfImage) pXObjDict.get(key);
            if (dXObjMap != null)
            {
                dXObj = (PdfObject) dXObjMap.get(key);
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
                PdfObject obj = value.colorSpace;
                if (obj instanceof PdfArray)
                {
                    List l = ((PdfArray) obj).getList();
                    PdfStream s = (PdfStream) l.get(3);
                    s.setObjectNumber(d.objectRun++);
                }

                value.setObjectNumber(d.objectRun++);
                d.addImage(((PdfInteger) key), value);
                objNo = value.getObjectNumber();
            }
            String imageName = ((PdfName) xObjIndRefDict
                .getValue(key)).getString();
            pXObjIndRefDict.put(new PdfName(imageName),
                new PdfIndirectReference(objNo, 0));
        }
    }
    
    protected void setFontDict(PdfStdDocument d) throws IOException,
        PdfException
    {
        Map pFontMap = fontDict.getMap();
        Map pFontIndRefMap = fontIndRefDict.getMap();
        Map dFontMap = d.fontMap;
        PdfObject dFontDict = null;
        for (Iterator iter = pFontMap.keySet().iterator(); iter
            .hasNext();)
        {
            PdfObject key = (PdfObject) iter.next();
            PdfFont value = (PdfFont) pFontMap.get(key);
            if (dFontMap != null)
            {
                dFontDict = (PdfObject) dFontMap.get(key);
            }
            if (dFontDict != null)
            /* font dict present in document */
            {
                int objNo = ((PdfDict) dFontDict).getObjectNumber();
                pFontIndRefMap.put((PdfName) key,
                    new PdfIndirectReference(objNo, 0));
            }
            else
            /* font dict not present in document */
            {
                prepareFontAndAddToDoc(d, pFontIndRefMap, key, value);
            }
        }
    }

    static void prepareFontAndAddToDoc(PdfStdDocument d,
        Map fontIndRefMap, PdfObject fontNameAskey, PdfFont font)
        throws IOException, PdfException
    {
        HashMap hm = new HashMap();
        hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_FONT));
        font.getData(hm);

        switch (font.getType())
        {
            case PdfFont.TYPE_1:
                break;

            case PdfFont.TRUE_TYPE:
                PdfName n_key = new PdfName(PDF_FDESCRIPTOR);
                PdfDict fontDescriptor = null;
                if (hm.containsKey(n_key))
                {
                    fontDescriptor = (PdfDict) hm.get(n_key);
                    fontDescriptor.setObjectNumber(d.objectRun++);
                }
                n_key = new PdfName(RUBICON_EMBEDDED);
                if (hm.containsKey(n_key))
                {
                    PdfStream stm = (PdfStream) hm.get(n_key);
                    stm.setObjectNumber(d.objectRun++);
                    fontDescriptor.getMap().put(
                        new PdfName(PDF_FONTFILE_2),
                        new PdfIndirectReference(stm.objNumber, 0));
                }
                break;

            case PdfFont.CJK_TYPE:
                break;

            case PdfFont.CID_TYPE_2:
                n_key = new PdfName(PDF_DESCENDANT);
                PdfDict descendantFont = null;
                fontDescriptor = null;
                if (hm.containsKey(n_key))
                {
                    descendantFont = (PdfDict) hm.get(n_key);
                    descendantFont.setObjectNumber(d.objectRun++);
                    Map dfm = descendantFont.dictMap;
                    n_key = new PdfName(PDF_FDESCRIPTOR);
                    if (dfm.containsKey(n_key))
                    {
                        fontDescriptor = (PdfDict) dfm.get(n_key);
                        fontDescriptor.setObjectNumber(d.objectRun++);
                    }
                    n_key = new PdfName(RUBICON_EMBEDDED);
                    if (dfm.containsKey(n_key))
                    {
                        PdfStream stm = (PdfStream) dfm.get(n_key);
                        stm.setObjectNumber(d.objectRun++);
                        fontDescriptor.getMap()
                            .put(
                                new PdfName(PDF_FONTFILE_2),
                                new PdfIndirectReference(
                                    stm.objNumber, 0));
                    }
                }
                n_key = new PdfName(PDF_TOUNICODE);
                if (hm.containsKey(n_key))
                {
                    PdfStream toUniStm = (PdfStream) hm.get(n_key);
                    toUniStm.setObjectNumber(d.objectRun++);
                }
                break;
        }

        hm.put(new PdfName(PDF_NAME), new PdfName(((PdfName) fontNameAskey)
            .getString()));

        PdfDict fd = new PdfDict(hm);
        fd.setObjectNumber(d.objectRun++);
        d.addFont(((PdfName) fontNameAskey).getString(), fd);

        int objNo = fd.getObjectNumber();
        fontIndRefMap.put(fontNameAskey, new PdfIndirectReference(
            objNo, 0));
    }

    protected synchronized void set(PdfStdDocument d)
        throws IOException, PdfException
    {
		if (deleted)
        {
		    return;
        }
        if (originDoc == null)
		{
			originDoc = d;
		}
        
        if(userVariableWriteText != null)
        {
            isPlaceHolderProcessed = true;
            for(int i = 0; i< userVariableWriteText.size(); i++)
            {
                Object[] obj = (Object[]) userVariableWriteText.get(i);
                String str;
                str = (String)obj[0];
                PdfRect rect = (PdfRect)obj[1];
                PdfFont font = (PdfFont)obj[2];
                PdfTextFormatter tf = (PdfTextFormatter)obj[3];
                PdfInteger unit = (PdfInteger)obj[4];                            
                
                str = PdfTextParser.replaceUserDefVar(str, d);
                writeText(str, rect, font, tf, unit.getInt(), false);
            }
        }
        
        if (rAnnotList != null)
        {
            int limit = rAnnotList.size();
            for (int i = 0; i < limit; i++)
            {
                PdfObject annot = (PdfObject) rAnnotList.get(i);
                d.updateIndirectRefs(originDoc, annot, true);
            }
        }

        if (this.rotation != 0)
        {
            StringBuffer sb = new StringBuffer();
            pageRotationStartStream = new PdfByteOutputStream();
            pageRotationEndStream = new PdfByteOutputStream();
            int rotate = rotation % 360;
            AffineTransform m = new AffineTransform(1, 0, 0, 1, 0, 0);
            if (rotate % 270 == 0)
            {
                m.rotate(Math.toRadians(rotate), pageWidth / 2,
                    pageWidth / 2);
            }
            else if (rotate % 180 == 0)
            {
                m.rotate(Math.toRadians(rotate), pageWidth / 2,
                    pageHeight / 2);
            }
            else
            {
                m.rotate(Math.toRadians(rotate), pageHeight / 2,
                    pageHeight / 2);
            }

            sb.append(PDF_STORE_GS + PDF_SP);
            double[] flatMatrix = new double[6];
            m.getMatrix(flatMatrix);
            for (int i = 0; i < flatMatrix.length; ++i)
            {
                sb.append(flatMatrix[i] + " ");
            }
            sb.append(PDF_CM + PDF_SP);
            pageRotationStartStream.write(sb.toString().getBytes());
            pageRotationEndStream.write((" " + PDF_RESTORE_GS + " ")
                .getBytes());
        }

        if (contentStream != null)
        {
            if (contentList == null)
            {
                contentList = new ArrayList();
            }
            ByteBuffer bb = ((ByteBuffer) ByteBuffer.wrap(
                contentStream.getBuffer()).limit(
                contentStream.size())).slice();
            contentList.add(underlayStreamCount, new PdfStream(
                new PdfDict(new HashMap()), bb));
        }
        
        setOpenActions(d);
        setCloseActions(d);
        setAnnots(d);
        
        if (patternList != null)
        {
            for(int i = 0; i < patternList.size(); i++)
            {
	            PdfStream patternStream = (PdfStream) patternList
                    .get(i);
                patternStream.setObjectNumber(d.objectRun++);
            }
        }

        if (actionDict != null)
        {
            actionDict.setObjectNumber(d.objectRun++);
        }
        
        if (contentList != null)
        { 
            PdfStream strm;
            for (Iterator iter = contentList.iterator(); iter
                .hasNext();)
            {
                strm = (PdfStream) iter.next();
                strm.setObjectNumber(d.objectRun++);
            }
        }
 
        if (fontDict != null)
        {
            setFontDict(d);
        }
        if (xObjDict != null)
        {
            setXObjDict(d);
        }
        
        setUnknownAttributes(d);
    }

    void setOpenActions(PdfStdDocument d)
    {
        PdfDict actionDict = null;
        if (openActionList == null)
        {
            return;
        }
        /* remove existing actions */
        unknownAttributes.remove(new PdfName(PDF_AA));

        int size = openActionList.size();
        for (int i = size - 1; i >= 0; i--)
        {
            actionDict = (PdfDict) openActionList.get(i);
            if (i != size - 1)
            {
                actionDict.getMap().put(new PdfName(PDF_NEXT),
                    new PdfIndirectReference(d.objectRun - 1, 0));
            }
            actionDict.setObjectNumber(d.objectRun++);
        }

        this.actionDict.getMap().put(new PdfName(PDF_O),
            new PdfIndirectReference(d.objectRun - 1, 0));
    }

    void setCloseActions(PdfStdDocument d)
    {
        PdfDict actionDict = null;
        if (closeActionList == null)
        {
            return;
        }
        /* remove existing actions */
        unknownAttributes.remove(new PdfName(PDF_AA));

        int size = closeActionList.size();
        for (int i = size - 1; i >= 0; i--)
        {
            actionDict = (PdfDict) closeActionList.get(i);
            if (i != size - 1)
            {
                actionDict.getMap().put(new PdfName(PDF_NEXT),
                    new PdfIndirectReference(d.objectRun - 1, 0));
            }
            actionDict.setObjectNumber(d.objectRun++);
        }

        this.actionDict.getMap().put(new PdfName(PDF_C),
            new PdfIndirectReference(d.objectRun - 1, 0));
    }

    void setAnnots(PdfStdDocument d) throws IOException,
        PdfException
	{
        if (annotList == null)
        {
            return;
        }

        PdfAnnot annot;
        PdfDict annotDict;
    	int size = annotList.size();
    	for (int i = size - 1; i >= 0; i--)
    	{
			annot = (PdfAnnot) annotList.get(i);
			annotDict = annot.dict;
            if (annotDict.objNumber != 0)
            {
                continue;
                /*This is for the case where same annot object
                is added to different pages.*/
            }
			annotDict.setObjectNumber(d.objectRun++);
            annot.set(originDoc, d);
    	}
	}

    protected PdfStdPage()
    {
        this(612, 792, 0, 0, 0, 0, 0, 0, PdfMeasurement.MU_POINTS);
    }

    protected PdfStdPage(int pageSize, double pageHeaderHeight,
        double pageFooterHeight, double pageLeftMargin,
        double pageTopMargin, double pageRightMargin,
        double pageBottomMargin, int measurementUnit)
    {
        setWidthHeight(pageSize);
        contentStream = null;
        contentList = null;
        fontDict = null;
        fontIndRefDict = null;
        xObjDict = null;
        xObjIndRefDict = null;
        actionDict = null;
        openActionList = null;
        closeActionList = null;
        pen = null;
        brush = null;
        prevFont = null;
        isLastItemShape = false;
        moveCursor = false;
        dict.getMap().put(TYPE, PAGE);
        this.tf = null;
        this.measurementUnit = measurementUnit;
        
        this.pageTopMargin = PdfMeasurement.convertToPdfUnit(
            measurementUnit, pageTopMargin);
        this.pageLeftMargin = PdfMeasurement.convertToPdfUnit(
            measurementUnit, pageLeftMargin);
        this.pageRightMargin = PdfMeasurement.convertToPdfUnit(
            measurementUnit, pageRightMargin);
        this.pageBottomMargin = PdfMeasurement.convertToPdfUnit(
            measurementUnit, pageBottomMargin);
        this.pageHeaderHeight = PdfMeasurement.convertToPdfUnit(
            measurementUnit, pageHeaderHeight);
        this.pageFooterHeight = PdfMeasurement.convertToPdfUnit(
            measurementUnit, pageFooterHeight);
        
        List l = new ArrayList();
        l.add(new PdfInteger(0));
        l.add(new PdfInteger(0));
        l.add(new PdfFloat((float) this.pageWidth));
        l.add(new PdfFloat((float) this.pageHeight));
        dict.getMap().put(new PdfName(PDF_MEDIABOX), new PdfArray(l));
        
        this.mode = PdfDocument.WRITING_MODE;
    }

    protected PdfStdPage(double width, double height,
        double pageHeaderHeight, double pageFooterHeight,
        double pageLeftMargin, double pageTopMargin,
        double pageRightMargin, double pageBottomMargin,
        int measurementUnit)
    {
        contentStream = null;
        contentList = null;
        fontDict = null;
        fontIndRefDict = null;
        xObjDict = null;
        xObjIndRefDict = null;
        actionDict = null;
        openActionList = null;
        closeActionList = null;
        pen = null;
        brush = null;
        prevFont = null;
        isLastItemShape = false;
        moveCursor = false;
        dict.getMap().put(TYPE, PAGE);
        this.tf = null;
        this.measurementUnit = measurementUnit;
        this.pageHeight = PdfMeasurement.convertToPdfUnit(
            measurementUnit, height);
        this.pageWidth = PdfMeasurement.convertToPdfUnit(
            measurementUnit, width);

        this.pageTopMargin = PdfMeasurement.convertToPdfUnit(
            measurementUnit, pageTopMargin);
        this.pageLeftMargin = PdfMeasurement.convertToPdfUnit(
            measurementUnit, pageLeftMargin);
        this.pageRightMargin = PdfMeasurement.convertToPdfUnit(
            measurementUnit, pageRightMargin);
        this.pageBottomMargin = PdfMeasurement.convertToPdfUnit(
            measurementUnit, pageBottomMargin);
        this.pageHeaderHeight = PdfMeasurement.convertToPdfUnit(
            measurementUnit, pageHeaderHeight);
        this.pageFooterHeight = PdfMeasurement.convertToPdfUnit(
            measurementUnit, pageFooterHeight);
        
        List l = new ArrayList();
        l.add(new PdfInteger(0));
        l.add(new PdfInteger(0));
        l.add(new PdfFloat((float) this.pageWidth));
        l.add(new PdfFloat((float) this.pageHeight));
        dict.getMap().put(new PdfName(PDF_MEDIABOX), new PdfArray(l));
        
        this.mode = PdfDocument.WRITING_MODE;
    }

    private void setWidthHeight(int pageSize)
    {
        switch (pageSize)
        {
            case PdfPageSize.LETTER:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 8.5);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 11);
                break;
            case PdfPageSize.A3:
                this.pageWidth = 841.8888;
                this.pageHeight = 1190.5488;
                break;
            case PdfPageSize.A4:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 8.27);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 11.69);
                break;
            case PdfPageSize.A5:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 8.27);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 5.83);
                break;
            case PdfPageSize.ENVELOPE_10:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 9.5); 
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 4.12);
                break;
            case PdfPageSize.ENVELOPE_C5:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 9.02);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 6.38);
                break;
            case PdfPageSize.ENVELOPE_DL:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 8.66);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 4.33);
                break;
            case PdfPageSize.LEGAL:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 8.5);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 14);
                break;

            case PdfPageSize.A2:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 16.5354);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 23.3858);
                break;

            case PdfPageSize.A3EXTRA:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 12.6771);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 17.5196);
                break;

            case PdfPageSize.A3EXTRATRANSVERSE:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 322);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 445);
                break;

            case PdfPageSize.A3ROTATED:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 420);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 297);
                break;

            case PdfPageSize.A3TRANSVERSE:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 297);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 420);
                break;

            case PdfPageSize.A4EXTRA:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 236);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 322);
                break;

            case PdfPageSize.A4PLUS:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 210);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 330);
                break;

            case PdfPageSize.A4ROTATED:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 297);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 210);
                break;

            case PdfPageSize.A4SMALL:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 8.2677);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 11.6929);
                break;

            case PdfPageSize.A4TRANSVERSE:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 210);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 297);
                break;

            case PdfPageSize.A5EXTRA:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 174);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 235);
                break;

            case PdfPageSize.A5ROTATED:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 210);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 148);
                break;

            case PdfPageSize.A5TRANSVERSE:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 148);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 210);
                break;

            case PdfPageSize.A6:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 105);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 148);
                break;

            case PdfPageSize.A6ROTATED:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 148);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 105);
                break;

            case PdfPageSize.APLUS:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 227);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 356);
                break;

            case PdfPageSize.B4:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 9.8425);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 13.8976);
                break;

            case PdfPageSize.ENVELOPE_B4:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 9.8425);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 13.8976);
                break;

            case PdfPageSize.B4JisROTATED:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 364);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 257);
                break;

            case PdfPageSize.B5:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 6.9291);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 9.8425);
                break;

            case PdfPageSize.ENVELOPE_B5:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 6.9291);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 9.8425);
                break;

            case PdfPageSize.B5EXTRA:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 201);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 276);
                break;

            case PdfPageSize.B5JisROTATED:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 257);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 182);
                break;

            case PdfPageSize.B5TRANSVERSE:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 182);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 257);
                break;

            case PdfPageSize.ENVELOPE_B6:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 6.9291);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 4.9212);
                break;

            case PdfPageSize.B6Jis:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 128);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 182);
                break;

            case PdfPageSize.B6JisROTATED:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 182);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 128);
                break;

            case PdfPageSize.BPLUS:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 305);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 487);
                break;

            case PdfPageSize.ENVELOPE_C3:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 12.7559);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 18.0314);
                break;

            case PdfPageSize.ENVELOPE_C4:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 9.0157);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 12.7559);
                break;

            case PdfPageSize.ENVELOPE_C65:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 4.4881);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 9.0157);
                break;

            case PdfPageSize.ENVELOPE_C6:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 4.4881);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 6.3779);
                break;

            case PdfPageSize.SHEET_C:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 17);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 22);
                break;

            case PdfPageSize.SHEET_D:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 22);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 34);
                break;

            case PdfPageSize.SHEET_E:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 34);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 44);
                break;

            case PdfPageSize.EXECUTIVE:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 7.25);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 10.5);
                break;

            case PdfPageSize.FOLIO:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 8.5);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 13);
                break;

            case PdfPageSize.GERMAN_LEGAL_FANFOLD:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 8.5);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 13);
                break;

            case PdfPageSize.GERMAN_STANDARD_FANFOLD:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 8.5);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 12);
                break;

            case PdfPageSize.ENVELOPE_INVITE:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 220);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_MM, 220);
                break;

            case PdfPageSize.ISOB4:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 9.8425);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 13.8976);
                break;

            case PdfPageSize.ENVELOPE_ITALY:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 4.3307);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 9.0551);
                break;

            case PdfPageSize.LEDGER:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 17);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 11);
                break;

            case PdfPageSize.LEGALEXTRA:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 9.275);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 15);
                break;

            case PdfPageSize.LETTEREXTRA:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 9.275);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 12);
                break;

            case PdfPageSize.LETTEREXTRA_TRANSVERSE:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 9.275);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 12);
                break;

            case PdfPageSize.LETTERPLUS:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 8.5);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 12.69);
                break;

            case PdfPageSize.LETTERROTATED:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 11);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 8.5);
                break;

            case PdfPageSize.LETTERSMALL:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 8.5);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 11);
                break;

            case PdfPageSize.LETTERTRANSVERSE:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 8.275);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 11);
                break;

            case PdfPageSize.ENVELOPE_MONARCH:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 3.875);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 7.5);
                break;

            case PdfPageSize.NOTE:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 8.5);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 11);
                break;

            case PdfPageSize.ENVELOPE_NUMBER10:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 4.125);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 9.5);
                break;

            case PdfPageSize.ENVELOPE_NUMBER11:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 4.5);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 10.375);
                break;

            case PdfPageSize.ENVELOPE_NUMBER12:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 4.75);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 11);
                break;

            case PdfPageSize.ENVELOPE_NUMBER14:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 5);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 11.5);
                break;

            case PdfPageSize.ENVELOPE_NUMBER9:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 3.875);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 8.875);
                break;

            case PdfPageSize.ENVELOPE_PERSONAL:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 3.625);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 6.5);
                break;

            case PdfPageSize.QUARTO:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 8.4645);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 10.8267);
                break;

            case PdfPageSize.STATEMENT:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 5.5);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 8.5);
                break;

            case PdfPageSize.TABLOID:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 11);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 17);
                break;

            case PdfPageSize.TABLOIDEXTRA:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 11.69);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 18);
                break;

            case PdfPageSize.US_STANDARD_STANFOLD:
                this.pageWidth = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 14.875);
                this.pageHeight = PdfMeasurement.convertToPdfUnit(
                    PdfMeasurement.MU_INCHES, 11);
                break;

            default:
                break;
        }
    }

    /**
     * Specifies default measurement unit to be used for this
     * <code>PdfPage</code>.
     * 
     * @param mu
     *         constant specifying the new default measurement unit
     * @since 1.0
     * @see #getMeasurementUnit()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#setMeasurementUnit">example</a>.
     */
    public synchronized void setMeasurementUnit(int mu)
    {
        this.measurementUnit = mu;
    }

    /**
     * Returns default measurement unit currently used for this
     * <code>PdfPage</code>.
     * 
     * @return constant identifying the current default measurement
     *         unit
     * @since 1.0
     * @see #setMeasurementUnit(int)
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#getMeasurementUnit">example</a>.
     */
    public synchronized int getMeasurementUnit()
    {
        return this.measurementUnit;
    }

    void writeActions(PdfStdDocument d) throws IOException
    {
        if (actionDict == null)
        {
            return;
        }

        if (openActionList != null)
        {
            PdfDict openActionDict;
            for (int i = 0, limit = openActionList.size(); i < limit; i++)
            {
                openActionDict = (PdfDict) openActionList.get(i);
                int index = openActionDict.getObjectNumber();
                d.offset[index] = d.bytesWritten;
                d.bytesWritten += d.writer
                    .writeIndirectObject(openActionDict);
            }
        }

        if (closeActionList != null)
        {
            PdfDict closeActionDict;
            for (int i = 0, limit = closeActionList.size(); i < limit; i++)
            {
                closeActionDict = (PdfDict) closeActionList.get(i);
                int index = closeActionDict.getObjectNumber();
                d.offset[index] = d.bytesWritten;
                d.bytesWritten += d.writer
                    .writeIndirectObject(closeActionDict);
            }
        }
        int index = actionDict.getObjectNumber();
        d.offset[index] = d.bytesWritten;
        d.bytesWritten += d.writer.writeIndirectObject(actionDict);
    }

    void writePattern(PdfStdDocument d) throws IOException
    {
        for (int i = 0; i < patternList.size(); i++)
        {
            PdfStream patternStream = (PdfStream) patternList.get(i);
            int index = patternStream.getObjectNumber();
            d.offset[index] = d.bytesWritten;
            d.bytesWritten += d.writer
                .writeIndirectObject(patternStream);
        }
    }
    
    void writeAnnots(PdfStdDocument d) throws IOException,
    	PdfException
	{
    	int limit = (annotList == null) ? 0 : annotList.size();
    	PdfDict annotDict;
    	for (int i = 0; i < limit; i++)
    	{
    	    PdfAnnot annot = (PdfAnnot) annotList.get(i);
            if (annot.isWritten)
            {
                /*This is for reusability of annot objects
                across documents & pages i.e. if same
                annot is reused in multiple pages or docs*/ 
                continue;
            }
            annotDict = annot.dict;
    	    annot.write(d);
    		
            int index = annotDict.getObjectNumber();
    		d.offset[index] = d.bytesWritten;
    		d.bytesWritten += d.writer.writeIndirectObject(
                annotDict);
    	}
	}

    protected synchronized void write(PdfStdDocument d)
        throws IOException, PdfException
    {
        if (deleted)
        {
            return;
        }
        if (originDoc == null)
        {
            originDoc = d;
        }
        
        ArrayList clist = new ArrayList();
        if (rContentList != null)
		{
            PdfArray array = new PdfArray(rContentList);
            d.updateIndirectRefs(originDoc, array, true);
            clist.addAll(rContentList);
		}
        
        /*if (rContentList != null)
        {
            int limit = rContentList.size();
            for (int i = 0; i < limit; i++)
            {
                int objNumber = ((PdfIndirectReference) rContentList
                    .get(i)).objNumber;
                int objNo = d.getNewObjNo(originDoc, objNumber);
                d.offset[objNo] = d.bytesWritten;
                PdfObject obj = originDoc.reader.dereferObject(objNumber);
                ((PdfStream) obj).getDictionary().getMap().remove(
                    new PdfName(PDF_LENGTH));
                d.updateIndirectRefs(originDoc, obj);
                obj.objNumber = objNo;
                d.bytesWritten += d.writer.writeIndirectObject(obj);
                clist.add(new PdfIndirectReference(obj.objNumber, 0));
            }
        }*/

        HashMap hm = new HashMap();
        hm.put(PROCSET, new PdfIndirectReference(d.procSet
            .getObjectNumber(), 0));

        if (patternList != null)
        {
            HashMap pm = new HashMap();
            for (int i = 0, limit = patternList.size(); i < limit; i++)
            {
                PdfStream patternStream = (PdfStream) patternList
                    .get(i);
                if (patternStream != null)
                {
                    PdfInteger intObj = (PdfInteger) patternStream
                        .getDictionary().dictMap.get(new PdfName(
                        "RubPattern"));
                    if (intObj == null)
                        continue;
                    int type = intObj.getInt();
                    patternStream.getDictionary().dictMap
                        .remove(new PdfName("RubPattern"));
                    String str;
                    switch (type)
                    {
                        case PdfBrush.PATTERN_VERTICAL:
                            str = PDF_PV;
                            break;
                        case PdfBrush.PATTERN_HORIZONTAL:
                            str = PDF_PH;
                            break;
                        case PdfBrush.PATTERN_FORWARD_DIAGONAL:
                            str = PDF_PFD;
                            break;
                        case PdfBrush.PATTERN_BACKWARD_DIAGONAL:
                            str = PDF_PBD;
                            break;
                        case PdfBrush.PATTERN_CROSS:
                            str = PDF_PC;
                            break;
                        case PdfBrush.PATTERN_DIAGONAL_CROSS:
                            str = PDF_PDC;
                            break;
                        default:
                            str = "";
                            break;
                    }
                    pm.put(new PdfName(str),
                        new PdfIndirectReference(patternStream
                            .getObjectNumber(), 0));
                }
            }
            hm.put(new PdfName(PDF_PATTERN), new PdfDict(pm));
            pm = new HashMap();
            ArrayList l = new ArrayList();
            l.add(new PdfName(PDF_PATTERN));
            l.add(new PdfName(PDF_DEVICE_RGB));
            pm.put(new PdfName(PDF_CSP), new PdfArray(l));
            hm.put(new PdfName(PDF_COLORSPACE), new PdfDict(pm));
            writePattern(d);
        }

        prepareResources(d, hm);

        dict.getMap().put(RESOURCES, new PdfDict(hm));

        if (actionDict != null)
        {
            dict.getMap().put(
                new PdfName(PDF_AA),
                new PdfIndirectReference(
                    actionDict.getObjectNumber(), 0));
        }

        // Prepare annots
        ArrayList annList = prepareAnnots(d);
        if (annList != null && !annList.isEmpty())
        {       
            dict.getMap().put(ANNOTS, new PdfArray(annList));
        }
        else
        {
            dict.dictMap.remove(ANNOTS);
        }
        
        dict.getMap().put(
            PARENT,
            new PdfIndirectReference(parent.getDict()
                .getObjectNumber(), 0));
        
        if (contentList != null)
        {
            if (this.rotation != 0)
            {
                modifyContentStreams();
            }
            int index;
            Iterator iter;
            ArrayList list = new ArrayList();
            for (iter = contentList.iterator(); iter.hasNext();)
            {
                PdfStream stream = (PdfStream) iter.next();
                d.filters = PdfFilter.encode(stream, d.filters,
                    d.addDefaultFilter, d.compressionLevel);
                index = stream.getObjectNumber();
                d.offset[index] = d.bytesWritten;
                d.bytesWritten += d.writer.writeIndirectObject(stream);
                list.add(new PdfIndirectReference(index, 0));
            }
            if (rContentList != null)
            {
                int count = underlayStreamCount;
                for (iter = clist.iterator(); iter.hasNext(); count++)
                {
                    list.add(count, iter.next());
                }
            }
            dict.getMap().put(CONTENTS, new PdfArray(list));
        }
        else if (rContentList != null)
        {
            dict.getMap().put(CONTENTS, new PdfArray(clist));
        }

        int index = dict.getObjectNumber();
        d.offset[index] = d.bytesWritten;
        d.bytesWritten += d.writer.writeIndirectObject(dict);
        
        writeActions(d);
        writeAnnots(d);
        
        /* reset */
        this.contentList = null;
    }

    void prepareResources(PdfStdDocument d, HashMap hm)
        throws IOException, PdfException
    {
        Map newXObjMap = new HashMap();
        if (xObjIndRefDict != null)
        {
            for (Iterator iter = xObjDict.getMap().keySet()
                .iterator(); iter.hasNext();)
            {
                PdfObject key = (PdfObject) iter.next();
                PdfName imgName = (PdfName) xObjIndRefDict
                    .getValue(key);
                PdfIndirectReference imgRef = (PdfIndirectReference) xObjIndRefDict
                    .getValue(imgName);
                newXObjMap.put(imgName, imgRef);
            }
            hm.put(XOBJ, new PdfDict(newXObjMap));
        }
        if (fontIndRefDict != null)
        {
            hm.put(FONT, fontIndRefDict.clone());
        }
        if (rResources != null)
        {
            PdfName key;
            PdfObject value;
            for (Iterator iter = rResources.keySet().iterator(); iter
                .hasNext();)
            {
                key = (PdfName) iter.next();
                if (key.equals(PROCSET))
                {
                    continue;
                }
                value = (PdfObject) rResources.get(key);
                value = (PdfObject) originDoc.reader.getObject(value)
                    .clone(); // case of extracting page to diff
                                // docs.
                d.updateIndirectRefs(originDoc, value, true);
                if (key.equals(XOBJ))
                {
                    Map m = ((PdfDict) value).getMap();
                    Iterator iter1 = newXObjMap.keySet().iterator();
                    while (iter1.hasNext())
                    {
                        Object imgKey = iter1.next();
                        Object imgVal = newXObjMap.get(imgKey);
                        m.put(imgKey, imgVal);
                    }
                }
                else if (key.equals(FONT) && fontDict != null)
                {
                    Map m = ((PdfDict) value).getMap();
                    Iterator iter1 = fontIndRefDict.dictMap.keySet()
                        .iterator();
                    while (iter1.hasNext())
                    {
                        Object fontKey = iter1.next();
                        Object fontVal = fontIndRefDict.dictMap
                            .get(fontKey);
                        m.put(fontKey, fontVal);
                    }
                }
                /*
                 * else //code for additional handled resource items
                 * in future
                 */
                hm.put(key, value);
            }
        }
    }

    ArrayList prepareAnnots(PdfStdDocument d) throws PdfException,
        IOException
    {
        ArrayList annList = new ArrayList();

        if (annotList != null)
        {
            for (int i = 0, limit = annotList.size(); i < limit; i++)
            {
                annList.add(new PdfIndirectReference(
                    ((PdfAnnot) annotList.get(i)).dict.objNumber, 0));
            }
        }

        if (rAnnotList != null)
        {
            annList.addAll(rAnnotList);
        }

        return annList;
    }
    
    private void modifyContentStreams()
    {
        ByteBuffer startBuffer = ((ByteBuffer) ByteBuffer.wrap(
            pageRotationStartStream.getBuffer()).limit(
                pageRotationStartStream.size())).slice();
        ByteBuffer endBuffer = ((ByteBuffer) ByteBuffer.wrap(
            pageRotationEndStream.getBuffer()).limit(
                pageRotationEndStream.size())).slice();
        
        PdfStream s = (PdfStream) contentList.get(0);
        s.setBuffer(modifyBuffer(s.streamBuffer, startBuffer, false));
        
        if (underlayStreamCount != 0)
        {
            s = (PdfStream) contentList.get(underlayStreamCount - 1);
            s.setBuffer(modifyBuffer(s.streamBuffer, endBuffer,
                true));

            s = (PdfStream) contentList.get(underlayStreamCount);
            s.setBuffer(modifyBuffer(s.streamBuffer, startBuffer,
                false));
        }
        
        s = (PdfStream) contentList.get(contentList.size() - 1);
        s.setBuffer(modifyBuffer(s.streamBuffer, endBuffer, true));
    }
    
    private ByteBuffer modifyBuffer(ByteBuffer origBuf,
        ByteBuffer newBuf, boolean append)
    {
        ByteBuffer retBuf = ByteBuffer.allocate(newBuf
            .position(0).capacity()
            + origBuf.position(0).capacity());
        if (append)
        {
            retBuf.put(origBuf).put(newBuf);
        }
        else
        {
            retBuf.put(newBuf).put(origBuf);    
        }
        
        return retBuf;
    }
    
    private int countWords(String text)
    {
        int count = 0, index = 0, state = 0;
        int length = text.length();
        char c;
        for (; index <= length - 1; ++index)
        {
            c = text.charAt(index);
            if (c == PDF_SP || c == PDF_NEWLINE || c == PDF_TAB)
            {
                 state = 0;
            }
            else if (state == 0)
            {
                state = 1;
                ++count;
            }
        }

        return count;
    }
    
    protected StringBuffer drawInternalBezier(double ctrlX1,
        double ctrlY1, double ctrlX2, double ctrlY2, double endX,
        double endY)
    {
        PdfPoint end = updatePageSettings(new PdfPoint(endX, endY));
        PdfPoint ctrl_1 = updatePageSettings(new PdfPoint(ctrlX1,
            ctrlY1));
        PdfPoint ctrl_2 = updatePageSettings(new PdfPoint(ctrlX2,
            ctrlY2));
        
        endX = PdfMeasurement.convertToPdfUnit(measurementUnit,
            (float) end.x);
        endY = PdfMeasurement.convertToPdfUnit(measurementUnit,
            (float) end.y);
        ctrlX1 = PdfMeasurement.convertToPdfUnit(measurementUnit,
            (float) ctrl_1.x);
        ctrlY1 = PdfMeasurement.convertToPdfUnit(measurementUnit,
            (float) ctrl_1.y);
        ctrlX2 = PdfMeasurement.convertToPdfUnit(measurementUnit,
            (float) ctrl_2.x);
        ctrlY2 = PdfMeasurement.convertToPdfUnit(measurementUnit,
            (float) ctrl_2.y);
        
        endY = this.pageHeight - endY;
        ctrlY1 = this.pageHeight - ctrlY1;
        ctrlY2 = this.pageHeight - ctrlY2;

        StringBuffer sb = new StringBuffer();
        if (pen == null)
        {
            pen = new PdfPen();
        }
        if (brush == null)
        {
            brush = new PdfBrush();
        }
        if (contentStream == null)
        {
            contentStream = new PdfByteOutputStream();
        }
        else
        {
            sb.append(PDF_NEWLINE);
        }

        sb.append(PdfWriter.formatFloat(ctrlX1) + PDF_SP
            + PdfWriter.formatFloat(ctrlY1) + PDF_SP);
        sb.append(PdfWriter.formatFloat(ctrlX2) + PDF_SP
            + PdfWriter.formatFloat(ctrlY2) + PDF_SP);
        sb.append(PdfWriter.formatFloat(endX) + PDF_SP
            + PdfWriter.formatFloat(endY) + PDF_SP);
        sb.append(" c ");

        currentX = endX;
        currentY = endY;
        
        return sb;
    }

    protected synchronized StringBuffer setPenBrush(boolean isFill,
        boolean isStroke)
    {
        if (pen == null)
        {
            pen = new PdfPen();
        }
        
        if (brush == null)
        {
            brush = new PdfBrush();
        }
        
        StringBuffer sb = new StringBuffer();
        if ((pen.dashLength != 0) || (pen.dashGap != 0))
        {
        	sb.append(PDF_ARRAYSTART
                + PdfWriter.formatFloat(pen.dashLength) + PDF_SP
                + PdfWriter.formatFloat(pen.dashGap) + PDF_ARRAYEND + PDF_SP);
            sb.append(PdfWriter.formatFloat(pen.dashPhase) + " d ");
        }
        
        if ((prevPen != null && prevPen.width != pen.width)
            || pen.width != PdfPen.DEFAULT_WIDTH)
        {
            sb.append(PdfWriter.formatFloat(pen.width) + " w ");
        }

        if ((prevPen == null) || !isLastItemShape
            || ((prevPen.strokeColor != pen.strokeColor) && (isStroke)))
        {
            if (pen.strokeColor != null)
            {
            sb.append(PdfWriter.formatFloat((double)
                pen.strokeColor.getRed() / 255f) + PDF_SP);
            sb.append(PdfWriter.formatFloat((double)
                pen.strokeColor.getGreen() / 255f) + PDF_SP);
            sb.append(PdfWriter.formatFloat((double)
                pen.strokeColor.getBlue() / 255f) + " RG ");
        }
            else
            {
                sb.append("0 0 0 RG ");
            }
        }

        if ((prevBrush == null) || !isLastItemShape
            || ((prevBrush.fillColor != brush.fillColor) && (isFill)))
        {
            sb.append(PdfWriter.formatFloat((double)
                brush.fillColor.getRed() / 255f) + PDF_SP);
            sb.append(PdfWriter.formatFloat((double)
                brush.fillColor.getGreen() / 255f) + PDF_SP);
            sb.append(PdfWriter.formatFloat((double)
                brush.fillColor.getBlue() / 255f) + " rg ");
        }

        if ((prevPen != null && prevPen.capStyle != pen.capStyle)
            || pen.capStyle != PdfPen.CAPSTYLE_BUTT)
        {
            sb.append(Integer.toString(
                pen.capStyle) + " J ");
        }

        if ((prevPen != null && prevPen.joinStyle != pen.joinStyle)
            || pen.joinStyle != PdfPen.JOINSTYLE_MITER)
        {
            sb.append(Integer.toString(
                pen.joinStyle) + " j ");
        }

        if ((prevPen != null && prevPen.miterLimit != pen.miterLimit)
            || pen.miterLimit != PdfPen.DEFAULT_MITERLIMIT)
        {
            sb.append(PdfWriter.formatFloat(
                pen.miterLimit) + " M ");
        }

        if (isStroke)
        {
	        prevPen = (PdfPen) pen.clone();
        }
        if (isFill)
        {
	        prevBrush = (PdfBrush) brush.clone();
        }
        isLastItemShape = true;
        
        return sb;
    }

    /**
     * Specifies default width for this page's pen.
     * 
     * @param width default width for the page's pen
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#setPenWidth">example</a>.
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
     * Specifies default color for this page's pen. 
     * 
     * @param color default color for the page's pen
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#setPenColor">example</a>.
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
     * of this PdfDocument's pen. 
     * 
     * @param length length of dashes in the default dash pattern
     * @since 1.0
     * @see #setPenDashGap(double)
     * @see #setPenDashPhase(double)
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#setPenDashLength">example</a>.
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
     * of this page's pen. 
     * 
     * @param gap length of gaps in the default dash pattern
     * @since 1.0
     * @see #setPenDashLength(double)
     * @see #setPenDashPhase(double)
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#setPenDashGap">example</a>.
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
     * Specifies length of phase of default 
     * <a href="{@docRoot}/doc-files/glossary.htm#dash_pattern" target="_GnosticeGlossaryWindow"
     * >dash pattern</a> 
     * of this page's pen. 
     * 
     * @param phase length of phase of the default dash pattern
     * @since 1.0
     * @see #setPenDashGap(double)
     * @see #setPenDashLength(double)
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#setPenDashPhase">example</a>.
     */
    public synchronized void setPenDashPhase(double phase)
    {
        if (pen == null)
        {
            pen = new PdfPen();
        }
        //prevPen.dashPhase = pen.dashPhase;
        pen.dashPhase = phase;
    }

    /**
     * Specifies default shape of endpoints of paths in this page. 
     * 
     * @param capStyle constant specifying the default shape
     * @since 1.0
     * @see PdfPen
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#setPenCapStyle">example</a>.
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
     * <a href="{@docRoot}/doc-files/glossary.htm#phase" target="_GnosticeGlossaryWindow" 
     * >paths</a> 
     * that connect at an angle for this pages's pen. 
     * 
     * @param joinStyle constant specifying the default shape
     * @since 1.0
     * @see PdfPen
     * @see #setPenCapStyle(int)
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#setPenJoinStyle">example</a>.
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
     * Specifies default miter limit for this page's pen. 
     * 
     * @param limit default miter limit for the page's pen
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#setPenMiterLimit">example</a>.
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
     * Specifies default color for this page's brush.
     * 
     * @param c default color for the page's brush
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#setBrushColor">example</a>.
     */
    public synchronized void setBrushColor(Color c)
    {
        if (brush == null)
        {
            brush = new PdfBrush();
        }
        brush.fillColor = c;
    }

    /*public synchronized void setBrushPattern(int brushPattern)
    {
        if (brush == null)
        {
            brush = new PdfBrush();
        }
        brush.brushPattern = brushPattern;
    }*/
    
    /**
     * Adds action of resolving specified Uniform Resource Identifier
     * URI) or executing a JavaScript script for the specified event.
     * 
     * @param event
     *            constant specifying trigger event for executing the
     *            action
     * @param actionType
     *            constant specifying resolution of URI or execution
     *            of JavaScript script
     * @param javascriptOrURI
     *            specified URI or Javascript script
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfAction
     * @see PdfAction.PdfEvent
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#addAction_int_String">example</a>.
     */
    public synchronized void addAction(int event, int actionType,
        String javascriptOrURI) throws PdfException
    {
        if (actionDict == null)
        {
            actionDict = new PdfDict(new HashMap());
        }

        Map hm = new HashMap();

        if (actionType == PdfAction.URI)
        {
            hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_ACTION));
            hm.put(new PdfName(PDF_S), new PdfName(PDF_URI_ACTION));
            hm.put(new PdfName(PDF_URI_ACTION), new PdfString(javascriptOrURI,
                true));
        }
        else if (actionType == PdfAction.JAVASCRIPT)
        {
            hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_ACTION));
            hm.put(new PdfName(PDF_S), new PdfName(
                PDF_JAVASCRIPT_ACTION));
            hm.put(new PdfName(PDF_JS), new PdfString(javascriptOrURI, true));
        }

        else
        {
            throw new PdfException("Invalid Action Type...");
        }

        if (event == PdfAction.PdfEvent.ON_PAGE_OPEN)
        {
            if (openActionList == null)
            {
                openActionList = new ArrayList();
            }
            openActionList.add(new PdfDict(hm));
        }
        else if (event == PdfAction.PdfEvent.ON_PAGE_CLOSE)
        {
            if (closeActionList == null)
            {
                closeActionList = new ArrayList();
            }
            closeActionList.add(new PdfDict(hm));
        }
    }

    /**
     * Adds action of launching specified application, or displaying
     * or printing specified file for the specified event.
     * 
     * @param event
     *            constant specifying the trigger event for executing
     *            the named action
     * @param actionType
     *            constant specifying action of launching an
     *            application, or displaying or printing a file
     * @param applicationToLaunch
     *            pathname of the application that needs to be
     *            launched or the file that needs to be displayed or
     *            printed
     * @param isPrint
     *            whether to file needs to be printed instead of 
     *            being displayed
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfAction
     * @see PdfAction.PdfEvent
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#addAction_int_String_int">example</a>.
     */
    public synchronized void addAction(int event, int actionType,
        String applicationToLaunch, boolean isPrint)
        throws PdfException
    {
        if (actionDict == null)
        {
            actionDict = new PdfDict(new HashMap());
        }

        Map hm = new HashMap();

        if (actionType == PdfAction.LAUNCH)
        {
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
            hm.put(new PdfName("Win"), new PdfDict(winHm));
        }
        else
        {
            throw new PdfException("Invalid Action Type...");
        }

        if (event == PdfAction.PdfEvent.ON_PAGE_OPEN)
        {
            if (openActionList == null)
            {
                openActionList = new ArrayList();
            }
            openActionList.add(new PdfDict(hm));
        }
        else if (event == PdfAction.PdfEvent.ON_PAGE_CLOSE)
        {
            if (closeActionList == null)
            {
                closeActionList = new ArrayList();
            }
            closeActionList.add(new PdfDict(hm));
        }
    }

    /**
     * Adds specified 
     * <a href="{@docRoot}/doc-files/glossary.htm#named_action" target="_GnosticeGlossaryWindow" 
     * >named action</a> 
     * for specified trigger event.
     * 
     * @param event
     *            constant specifying the trigger event for executing
     *            the named action
     * @param namedAction
     *            constant specifying the named action that needs to 
     *            be executed when the trigger event occurs
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfAction
     * @see PdfAction.PdfEvent
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#addAction_int_int">example</a>.
     */
    public synchronized void addAction(int event, int namedAction)
        throws PdfException
    {
        if (actionDict == null)
        {
            actionDict = new PdfDict(new HashMap());
        }

        Map hm = new HashMap();
        hm.put(new PdfName(PDF_TYPE), new PdfName(PDF_ACTION));
        hm.put(new PdfName(PDF_S), new PdfName(PDF_NAMED));

        if (namedAction == PdfAction.NAMED_FIRSTPAGE)
        {
            hm.put(new PdfName(PDF_N), new PdfName(PDF_FIRST_PAGE));
        }
        else if (namedAction == PdfAction.NAMED_LASTPAGE)
        {
            hm.put(new PdfName(PDF_N), new PdfName(PDF_LAST_PAGE));
        }
        else if (namedAction == PdfAction.NAMED_NEXTPAGE)
        {
            hm.put(new PdfName(PDF_N), new PdfName(PDF_NEXT_PAGE));
        }
        else if (namedAction == PdfAction.NAMED_PREVPAGE)
        {
            hm.put(new PdfName(PDF_N), new PdfName(PDF_PREV_PAGE));
        }
        else
        {
            throw new PdfException("Invalid Named Action.");
        }

        if (event == PdfAction.PdfEvent.ON_PAGE_OPEN)
        {
            if (openActionList == null)
            {
                openActionList = new ArrayList();
            }
            openActionList.add(new PdfDict(hm));
        }
        else if (event == PdfAction.PdfEvent.ON_PAGE_CLOSE)
        {
            if (closeActionList == null)
            {
                closeActionList = new ArrayList();
            }
            closeActionList.add(new PdfDict(hm));
        }
    }

    /**
     * Adds specified annotation to this <code>PdfPage</code>.
     * 
     * @param annotation
     *            a link or text annotation
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawAnnotation">example</a>.
     */
    public synchronized void addAnnotation(PdfAnnot annotation)
        throws PdfException
    {
        if (annotList == null)
        {
            annotList = new ArrayList();
        }
        annotList.add(annotation.encode(this));
    }

    /**
     * Adds specified text as watermark with its exact position
     * determined by <code>position</code> and
     * <code>applyMargins</code>. 
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
     * @param applyMargins
     *            whether page margins need to be considered when
     *            positioning the text
     * @param angle
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @param underlay
     *            whether the text needs to be placed underneath other
     *            page contents
     * @throws IOException
     *             if an I/O error occurs.
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#addWatermarkText_String_PdfFont_int_boolean_double_boolean">example</a>.
     */
    public synchronized void addWatermarkText(String text,
        PdfFont font, int position, boolean applyMargins,
        double angle, boolean underlay) throws IOException,
        PdfException
    {
        if (applyMargins)
        {
        if (text == null || text.equals(""))
        {
            return;
        }
        if (font == null)
        {
            font = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        if (underlay)
        {
            ++underlayStreamCount;
        }
        double width = font.getWidth(text, PdfMeasurement.MU_POINTS);
        double height = font.getHeight();
        double x = 0, y = 0;
        int horizPos = position & 0x0f;
        int vertiPos = position & 0xf0;

        switch (horizPos)
        {
            case 1: /* HP_LEFT */
                x = 0 + pageCropLeft + pageLeftMargin;
                break;
            case 2: /* HP_MIDDLE */
                double avlWidth = pageWidth - pageCropLeft
                    - pageCropRight - pageLeftMargin
                    - pageRightMargin;
                x = (avlWidth - width) / 2 + pageCropLeft
                    + pageLeftMargin;
                break;
            case 4: /* HP_RIGHT */
                x = pageWidth - pageCropRight - pageRightMargin
                    - width;
                break;
            default:
                throw new PdfException(
                    "Invalid horizontal position for watermark text.");
        }
        switch (vertiPos)
        {
            /* Header & Footer heights not cosidered */
            case 16: /* VP_TOP */
                y = 0 + pageCropTop + pageTopMargin;
                break;
            case 32: /* VP_CENTRE */
                double avlHeight = pageHeight - pageCropTop
                    - pageCropBottom - pageTopMargin
                    - pageBottomMargin;
                y = (avlHeight - height) / 2;
                break;
            case 64: /* VP_BOTTOM */
                y = pageHeight - pageCropBottom - pageBottomMargin
                    - height;
                break;
            default:
                throw new PdfException(
                    "Invalid vertical position for watermark text.");
        }
        x = PdfMeasurement.convertToMeasurementUnit(measurementUnit,
            x);
        y = PdfMeasurement.convertToMeasurementUnit(measurementUnit,
            y);
        PdfByteOutputStream temp = contentStream;
        PdfFont tempFont = prevFont;
        contentStream = null;

        PdfRect rect = new PdfRect(x, y, PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, width),
            PdfMeasurement.convertToMeasurementUnit(measurementUnit,
                height));
        PdfTextFormatter tf = new PdfTextFormatter();
        tf.setRotation(angle);
        writeText(text, rect, font, tf, measurementUnit, true);

        if (contentList == null)
        {
            contentList = new ArrayList();
        }
        ByteBuffer bb = ((ByteBuffer) ByteBuffer.wrap(
            contentStream.getBuffer()).limit(contentStream.size()))
            .slice();
        contentList.add(underlay ? 0 : contentList.size(),
            new PdfStream(new PdfDict(new HashMap()), bb));

        contentStream = temp;
        prevFont = tempFont;
        }
        else
        {
            addWatermarkText(text, font, position, angle, underlay);
        }
    }

    /**
     * Adds specified text as watermark.
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
     * @param angle
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @param underlay
     *            whether the text needs to be placed underneath 
     *            other page contents
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#addWatermarkText_String_PdfFont_int_double_boolean">example</a>.
     */
    public synchronized void addWatermarkText(String text,
        PdfFont font, int position, double angle, boolean underlay)
        throws IOException, PdfException
    {
        if (text == null || text.equals(""))
        {
            return;
        }
        if (font == null)
        {
            font = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        if (underlay)
        {
            ++underlayStreamCount;
        }
        double width = font.getWidth(text, PdfMeasurement.MU_POINTS);
        double height = font.getHeight();
        double x = 0, y = 0;
        int horizPos = position & 0x0f;
        int vertiPos = position & 0xf0;

        switch (horizPos)
        {
            case 1: /* HP_LEFT */
                x = 0 + pageCropLeft;
                break;
            case 2: /* HP_MIDDLE */
                double avlWidth = pageWidth - pageCropLeft
                    - pageCropRight;
                x = (avlWidth - width) / 2 + pageCropLeft;
                break;
            case 4: /* HP_RIGHT */
                x = pageWidth - pageCropRight - width;
                break;
            default:
                throw new PdfException(
                    "Invalid horizontal position for watermark text.");
        }
        switch (vertiPos)
        {
            case 16: /* VP_TOP */
                y = 0 + pageCropTop;
                break;
            case 32: /* VP_CENTRE */
                double avlHeight = pageHeight - pageCropTop
                    - pageCropBottom;
                y = (avlHeight - height) / 2;
                break;
            case 64: /* VP_BOTTOM */
                y = pageHeight - pageCropBottom - height;
                break;
            default:
                throw new PdfException(
                    "Invalid vertical position for watermark text.");
        }
        x = PdfMeasurement.convertToMeasurementUnit(measurementUnit,
            x);
        y = PdfMeasurement.convertToMeasurementUnit(measurementUnit,
            y);
        PdfByteOutputStream temp = contentStream;
        PdfFont tempFont = prevFont;
        contentStream = null;

        PdfRect rect = new PdfRect(x, y, PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, width),
            PdfMeasurement.convertToMeasurementUnit(measurementUnit,
                height));
        PdfTextFormatter tf = new PdfTextFormatter();
        tf.setRotation(angle);
        writeText(text, rect, font, tf, measurementUnit, true);

        if (contentList == null)
        {
            contentList = new ArrayList();
        }
        ByteBuffer bb = ((ByteBuffer) ByteBuffer.wrap(
            contentStream.getBuffer()).limit(contentStream.size()))
            .slice();
        contentList.add(underlay ? 0 : contentList.size(),
            new PdfStream(new PdfDict(new HashMap()), bb));

        contentStream = temp;
        prevFont = tempFont;
    }
    
    /**
     * Adds <code>PdfImage</code> object as watermark image with its
     * exact position determined by <code>position</code> and
     * <code>applyMargins</code>. 
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
     * @param applyMargins
     *            whether page margins need to be considered when
     *            positioning the image
     * @param angle
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
     * @param underlay
     *            whether the image needs to be placed underneath
     *            other page contents
     * @throws IOException
     *             if an I/O error occurs.
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#addWatermarkImage_PdfImage_boolean_int_double_boolean">example</a>.
     */
    public synchronized void addWatermarkImage(PdfImage image,
        int position, boolean applyMargins, double angle,
        boolean underlay) throws IOException, PdfException
    {
        if (applyMargins)
        {
            if (image == null)
            {
                return;
            }
            if (underlay)
            {
                underlayStreamCount++;
            }
            double width = image.scaledWidth <= 0 ? image.width
                : PdfMeasurement.convertToPdfUnit(measurementUnit,
                    image.scaledWidth);
            double height = image.scaledHeight <= 0 ? image.height
                : PdfMeasurement.convertToPdfUnit(measurementUnit,
                    image.scaledHeight);
            double x = 0, y = 0;
            int horizPos = position & 0x0f;
            int vertiPos = position & 0xf0;

            switch (horizPos)
            {
                case 1: /* HP_LEFT */
                    x = 0 + pageCropLeft + pageLeftMargin;
                    break;
                case 2: /* HP_MIDDLE */
                    double avlWidth = pageWidth - pageCropLeft
                        - pageCropRight - pageLeftMargin
                        - pageRightMargin;
                    x = (avlWidth - width) / 2 + pageCropLeft
                        + pageLeftMargin;
                    break;
                case 4: /* HP_RIGHT */
                    x = pageWidth - pageCropRight - pageRightMargin
                        - width;
                    break;
                default:
                    throw new PdfException(
                        "Invalid horizontal position for watermark image.");
            }
            switch (vertiPos)
            {
                /* Header & Footer heights not cosidered */
                case 16: /* VP_TOP */
                    y = 0 + pageCropTop + pageTopMargin;
                    break;
                case 32: /* VP_CENTRE */
                    double avlHeight = pageHeight - pageCropTop
                        - pageCropBottom - pageTopMargin
                        - pageBottomMargin;
                    y = (avlHeight - height) / 2;
                    break;
                case 64: /* VP_BOTTOM */
                    y = pageHeight - pageCropBottom
                        - pageBottomMargin - height;
                    break;
                default:
                    throw new PdfException(
                        "Invalid vertical position for watermark image.");
            }
            x = PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, x);
            y = PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, y);
            PdfByteOutputStream temp = contentStream;
            contentStream = null;
            image.setRotation(angle);

            double rectW = image.scaledWidth <= 0 ? PdfMeasurement
                .convertToMeasurementUnit(measurementUnit,
                    image.width) : image.scaledWidth;
            double rectH = image.scaledHeight <= 0 ? PdfMeasurement
                .convertToMeasurementUnit(measurementUnit,
                    image.height) : image.scaledHeight;

            drawImage(image, new PdfRect(x, y, rectW, rectH), null,
                measurementUnit);

            if (contentList == null)
            {
                contentList = new ArrayList();
            }
            ByteBuffer bb = ((ByteBuffer) ByteBuffer.wrap(
                contentStream.getBuffer())
                .limit(contentStream.size())).slice();
            contentList.add(underlay ? 0 : contentList.size(),
                new PdfStream(new PdfDict(new HashMap()), bb));
            contentStream = temp;
        }
        else
        {
            addWatermarkImage(image, position, angle, underlay);
        }
    }

    /**
     * Adds <code>PdfImage</code> object as watermark image.
     * 
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
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#addWatermarkImage_PdfImage_int_double_boolean">example</a>.
     */
    public synchronized void addWatermarkImage(PdfImage image,
        int position, double angle, boolean underlay)
        throws IOException, PdfException
    {
        if (image == null)
        {
             return;
        }
        if (underlay)
        {
            underlayStreamCount++;
        }
		double width = image.scaledWidth <= 0 ? image.width
            : PdfMeasurement.convertToPdfUnit(measurementUnit,
                image.scaledWidth);
		double height = image.scaledHeight <= 0 ? image.height
            : PdfMeasurement.convertToPdfUnit(measurementUnit,
                image.scaledHeight);
        double x = 0, y = 0;
        int horizPos = position & 0x0f;
        int vertiPos = position & 0xf0;

        switch (horizPos)
        {
            case 1: /*HP_LEFT*/
                x = 0 + pageCropLeft;
                break;
            case 2: /*HP_MIDDLE*/
                double avlWidth = pageWidth - pageCropLeft - pageCropRight;
                x = (avlWidth - width) / 2 + pageCropLeft;
                break;
            case 4: /*HP_RIGHT*/
                x = pageWidth - pageCropRight - width;
                break;
            default:
                throw new PdfException(
                    "Invalid horizontal position for watermark image.");
        }
        switch (vertiPos)
        {
            case 16: /*VP_TOP*/
                y = 0 + pageCropTop;                
                break;
            case 32: /*VP_CENTRE*/
                double avlHeight = pageHeight - pageCropTop - pageCropBottom;
                y = (avlHeight - height) / 2;
                break;
            case 64: /*VP_BOTTOM*/
                y = pageHeight - pageCropBottom - height;
                break;
            default:
                throw new PdfException(
                    "Invalid vertical position for watermark image.");
        }
        x = PdfMeasurement.convertToMeasurementUnit(
            	measurementUnit, x);
        y = PdfMeasurement.convertToMeasurementUnit(
            	measurementUnit, y);
        PdfByteOutputStream temp = contentStream;
        contentStream = null;
        image.setRotation(angle);
        
        double rectW = image.scaledWidth <= 0 ? PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, image.width)
                : image.scaledWidth;
        double rectH = image.scaledHeight <= 0 ? PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, image.height)
                : image.scaledHeight;

        drawImage(image, new PdfRect(x, y, rectW, rectH), null,
            measurementUnit);

        if (contentList == null)
        {
            contentList = new ArrayList();
        }
        ByteBuffer bb = ((ByteBuffer) ByteBuffer.wrap(
            contentStream.getBuffer())
            .limit(contentStream.size())).slice();
        contentList.add(underlay ? 0 : contentList.size(),
            new PdfStream(new PdfDict(new HashMap()), bb));
        contentStream = temp;
    }

    /**
     * Adds image, specified by its pathname, as watermark image
     * with its exact position determined by <code>position</code>
     * and <code>applyMargins</code>.
     * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param path
     *            pathname of the watermark image
     * @param position
     *            constant specifying the combination of vertical and
     *            horizontal alignment of the image
     * @param applyMargins
     *            whether page margins need to be considered when
     *            positioning the image
     * @param angle
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
     * @param underlay
     *            whether the image needs to be placed underneath
     *            other page contents
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#addWatermarkImage_String_int_boolean_double_boolean">example</a>.
     */
    public void addWatermarkImage(String path, int position,
        boolean applyMargins, double angle, boolean underlay)
        throws IOException, PdfException
    {
        addWatermarkImage(PdfImage.create(path), position,
            applyMargins, angle, underlay);
    }

    /**
     * Adds image, specified by its pathname, as watermark image.
     * 
     * <p>
     * The image is rotated on center of its bounding box by 
     * <code>angle</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param path
     *            pathname of the watermark image
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
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#addWatermarkImage_String_int_double_boolean">example</a>.
     */
    public void addWatermarkImage(String path, int position,
        double angle, boolean underlay) throws IOException,
        PdfException
    {
        addWatermarkImage(PdfImage.create(path), position, angle,
            underlay);
    }
    
    /**
     * Adds specified text to header.
     * 
     * @param text
     *            text that needs to be added to the header
     * @param font
     *            font with which the text needs to be written
     * @param position
     *            constant specifying the combination of vertical and
     *            horizontal alignment of the text within the header
     * @param underlay
     *            whether the text needs to be placed underneath other
     *            content in the header
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#addHeaderText">example</a>.
     */
    public synchronized void addHeaderText(String text, PdfFont font,
        int position, boolean underlay/*, double angle*/)
        throws IOException, PdfException
    {
        if (text == null || text.equals(""))
        {
            return;
        }
        if (font == null)
        {
            font = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        if (underlay)
        {
            ++underlayStreamCount;
        }
		double width = font.getWidth(text, PdfMeasurement.MU_POINTS);
		double height = font.getHeight();
        double x = 0, y = 0;
        int horizPos = position & 0x0f;
        int vertiPos = position & 0xf0;

        switch (horizPos)
        {
            case 1: /*HP_LEFT*/
                x = pageLeftMargin + pageCropLeft;
                break;
            case 2: /*HP_MIDDLE*/
                double rectWidth = pageWidth - pageLeftMargin
                    - pageRightMargin - pageCropLeft - pageCropRight;
                x = (rectWidth - width) / 2 + pageCropLeft
                    + pageLeftMargin;
                break;
            case 4: /*HP_RIGHT*/
                x = pageWidth - pageRightMargin - pageCropRight - width;
                break;
            default:
                throw new PdfException(
                    "Invalid horizontal position for header text.");
        }
        switch (vertiPos)
        {
            case 16: /*VP_TOP*/
                y = pageTopMargin + pageCropTop;                
                break;
            case 32: /*VP_CENTRE*/
                y = pageTopMargin + pageCropTop
                    + ((pageHeaderHeight - height) / 2);
                break;
            case 64: /*VP_BOTTOM*/
                y = pageTopMargin + pageHeaderHeight + pageCropTop - height;
                break;
            default:
                throw new PdfException(
                    "Invalid vertical position for header text.");
        }
        x = PdfMeasurement.convertToMeasurementUnit(
            	measurementUnit, x);
        y = PdfMeasurement.convertToMeasurementUnit(
            	measurementUnit, y);
        PdfByteOutputStream temp = contentStream;
        PdfFont tempFont = prevFont;
        
        double rectWidth = PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth - pageLeftMargin
                - pageRightMargin - pageCropLeft - pageCropRight);
        double rectHeight = PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageHeaderHeight);
        PdfRect rect = new PdfRect(x, y, rectWidth, rectHeight);
        contentStream = null;

        writeText(text, rect, font, new PdfTextFormatter(),
            measurementUnit, true);
        
        if (contentList == null)
        {
            contentList = new ArrayList();
        }
        ByteBuffer bb = ((ByteBuffer) ByteBuffer.wrap(
            contentStream.getBuffer())
            .limit(contentStream.size())).slice();
        contentList.add(underlay ? 0 : contentList.size(),
            new PdfStream(new PdfDict(new HashMap()), bb));
        
        contentStream = temp;
        prevFont = tempFont;
    }
    
    /**
     * Adds <code>PdfImage</code> object to the header.
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
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#addHeaderImage_PdfImage_int_boolean">example</a>.
     */
    public synchronized void addHeaderImage(PdfImage img,
        int position, boolean underlay) throws IOException,
        PdfException
    {
        if (img == null)
        {
            return;
        }
        if (underlay)
        {
            underlayStreamCount++;
        }

        double rectWidth = pageWidth - pageLeftMargin
            - pageRightMargin - pageCropLeft - pageCropRight;
        double rectHeight = pageHeaderHeight;
		double imageWidth = img.scaledWidth <= 0 ? img.width
            : PdfMeasurement.convertToPdfUnit(measurementUnit,
                img.scaledWidth);
		double imageHeight = img.scaledHeight <= 0 ? img.height
            : PdfMeasurement.convertToPdfUnit(measurementUnit,
                img.scaledHeight);
		
        double tImgWidth = img.width;
        double tImgHeight = img.height;
        double tImgScaledWidth = img.scaledWidth;
        double tImgScaledHeight = img.scaledHeight;
        
        img.scaledWidth = (float) Math.min(imageWidth, rectWidth);
        imageWidth = img.getWidth();
        img.scaledHeight = (float) Math.min(imageHeight, rectHeight);
        imageHeight = img.getHeight();
		
        double x = 0, y = 0;
        int horizPos = position & 0x0f;
        int vertiPos = position & 0xf0;
        switch (horizPos)
        {
            case 1: /*HP_LEFT*/
                x = pageLeftMargin + pageCropLeft;
                break;
            case 2: /*HP_MIDDLE*/
                x = (rectWidth - imageWidth) / 2 + pageCropLeft + pageLeftMargin;
                break;
            case 4: /*HP_RIGHT*/
                x = pageWidth - pageRightMargin - pageCropRight - imageWidth;
                break;
            default:
                throw new PdfException(
                    "Invalid horizontal position for header image.");
        }
        switch (vertiPos)
        {
            case 16: /*VP_TOP*/
                y = pageTopMargin + pageCropTop;
                break;
            case 32: /*VP_CENTRE*/
                y = pageTopMargin + pageCropTop
                    + ((pageHeaderHeight - imageHeight) / 2);
                break;
            case 64: /*VP_BOTTOM*/
                y = pageTopMargin + pageHeaderHeight + pageCropTop - imageHeight;
                break;
            default:
                throw new PdfException(
                    "Invalid vertical position for header image.");
        }
        
        PdfByteOutputStream temp = contentStream;
        contentStream = null;

        drawImage(img, new PdfRect(x, y, img.scaledWidth,
            img.scaledHeight), null, PdfMeasurement.MU_POINTS);
        if (contentList == null)
        {
            contentList = new ArrayList();
        }
        ByteBuffer bb = ((ByteBuffer) ByteBuffer.wrap(
            contentStream.getBuffer())
            .limit(contentStream.size())).slice();
        contentList.add(underlay ? 0 : contentList.size(),
            new PdfStream(new PdfDict(new HashMap()), bb));

        contentStream = temp;
        img.width = (float) tImgWidth;
        img.height = (float) tImgHeight;
        img.scaledHeight = (float) tImgScaledHeight;
        img.scaledWidth = (float) tImgScaledWidth;
    }
    
    /**
     * Adds image, specified by its pathname, to header.
     * 
     * @param path
     *            pathname of the image, which needs to be added to
     *            the header
     * @param position
     *            constant specifying the combination of vertical and
     *            horizontal alignment of the image within the header
     * @param underlay
     *            whether the image needs to be placed underneath
     *            other content in the header
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#addHeaderImage_String_int_boolean">example</a>.
     */
    public void addHeaderImage(String path, int position,
        boolean underlay) throws IOException, PdfException
    {
        addHeaderImage(PdfImage.create(path), position, underlay);
    }

    /**
     * Adds specified text to footer.
     * 
     * @param text
     *            text that needs to be added to the footer
     * @param font
     *            font with which the text next needs to be written
     * @param position
     *            constant specifying the combination of vertical and
     *            horizontal alignment of the text within the footer
     * @param underlay
     *            whether the text needs to be placed underneath other
     *            content in the footer
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#addFooterText">example</a>.
     */
    public synchronized void addFooterText(String text, PdfFont font,
        int position, boolean underlay) throws IOException,
        PdfException
    {
        if (text == null || text.equals(""))
        {
            return;
        }
        if (font == null)
        {
            font = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        if (underlay)
        {
            ++underlayStreamCount;
        }
		double width = font.getWidth(text, PdfMeasurement.MU_POINTS);
		double height = font.getHeight();
        double x = 0, y = 0;
        int horizPos = position & 0x0f;
        int vertiPos = position & 0xf0;

        switch (horizPos)
        {
            case 1: /*HP_LEFT*/
                x = pageLeftMargin + pageCropLeft;
                break;
            case 2: /*HP_MIDDLE*/
                double rectWidth = pageWidth - pageLeftMargin
                    - pageRightMargin - pageCropLeft - pageCropRight;
                x = (rectWidth - width) / 2 + pageCropLeft
                    + pageLeftMargin;
                break;
            case 4: /*HP_RIGHT*/
                x = pageWidth - pageRightMargin - pageCropRight - width;
                break;
            default:
                throw new PdfException(
                    "Invalid horizontal position for footer text.");
        }
        switch (vertiPos)
        {
            case 16: /*VP_TOP*/
                y = pageHeight - pageBottomMargin - pageFooterHeight
                    - pageCropBottom;                
                break;
            case 32: /*VP_CENTRE*/
                y = pageHeight - pageBottomMargin - pageCropBottom
                    - ((pageFooterHeight + height) / 2);
                break;
            case 64: /*VP_BOTTOM*/
                y = pageHeight - pageBottomMargin - pageCropBottom
                    - height;
                break;
            default:
                throw new PdfException(
                    "Invalid vertical position for footer text.");
        }
        x = PdfMeasurement.convertToMeasurementUnit(
            	measurementUnit, x);
        y = PdfMeasurement.convertToMeasurementUnit(
            	measurementUnit, y);
        PdfByteOutputStream temp = contentStream;
        PdfFont tempFont = prevFont;
        
        double rectWidth = PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth - pageLeftMargin
                - pageRightMargin - pageCropLeft - pageCropRight);
        double rectHeight = PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageFooterHeight);
        PdfRect rect = new PdfRect(x, y, rectWidth, rectHeight);
        contentStream = null;

        writeText(text, rect, font, new PdfTextFormatter(),
            measurementUnit, true);

        if (contentList == null)
        {
            contentList = new ArrayList();
        }
        ByteBuffer bb = ((ByteBuffer) ByteBuffer.wrap(
            contentStream.getBuffer())
            .limit(contentStream.size())).slice();
        contentList.add(underlay ? 0 : contentList.size(),
            new PdfStream(new PdfDict(new HashMap()), bb));
        
        contentStream = temp;
        prevFont = tempFont;
    }
    
    /**
     * Adds <code>PdfImage</code> object to footer.
     * 
     * @param img
     *            <code>PdfImage</code> object that needs to be
     *            added to the footer
     * @param position
     *            constant specifying the combination of vertical and
     *            horizontal alignment of the image within the footer
     * @param underlay
     *            whether the image needs to be placed underneath
     *            other content in the footer
     * @throws IOException
     *             if an I/O error occurs.
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#addFooterImage_PdfImage_int_boolean">example</a>.
     */
    public synchronized void addFooterImage(PdfImage img,
        int position, boolean underlay) throws IOException,
        PdfException
    {
        if (img == null)
        {
            return;
        }
        if (underlay)
        {
            underlayStreamCount++;
        }
        double rectWidth = pageWidth - pageLeftMargin
            - pageRightMargin - pageCropLeft - pageCropRight;
        double rectHeight = pageFooterHeight;
		double imageWidth = img.scaledWidth <= 0 ? img.width
            : PdfMeasurement.convertToPdfUnit(measurementUnit,
                img.scaledWidth);
		double imageHeight = img.scaledHeight <= 0 ? img.height
            : PdfMeasurement.convertToPdfUnit(measurementUnit,
                img.scaledHeight);
		
        double tImgWidth = img.width;
        double tImgHeight = img.height;
        double tImgScaledWidth = img.scaledWidth;
        double tImgScaledHeight = img.scaledHeight;
        
        img.scaledWidth = (float) Math.min(imageWidth, rectWidth);
        imageWidth = img.getWidth();
        img.scaledHeight = (float) Math.min(imageHeight, rectHeight);
        imageHeight = img.getHeight();;
		
        double x = 0, y = 0;
        int horizPos = position & 0x0f;
        int vertiPos = position & 0xf0;
        switch (horizPos)
        {
            case 1: /*HP_LEFT*/
                x = pageLeftMargin + pageCropLeft;
                break;
            case 2: /*HP_MIDDLE*/
                x = (rectWidth - imageWidth) / 2 + pageCropLeft + pageLeftMargin;
                break;
            case 4: /*HP_RIGHT*/
                x = pageWidth - pageRightMargin - pageCropRight - imageWidth;
                break;
            default:
                throw new PdfException(
                    "Invalid horizontal position for footer image.");
        }
        switch (vertiPos)
        {
            case 16: /*VP_TOP*/
                y = pageHeight - pageCropBottom - pageBottomMargin
                    - pageFooterHeight;
                break;
            case 32: /*VP_CENTRE*/
                y = pageHeight - pageCropBottom - pageBottomMargin
                    - ((pageFooterHeight + imageHeight) / 2);
                break;
            case 64: /*VP_BOTTOM*/
                y = pageHeight - pageCropBottom - pageBottomMargin
                    - imageHeight;
                break;
            default:
                throw new PdfException(
                    "Invalid vertical position for footer image.");
        }
        
        PdfByteOutputStream temp = contentStream;
        contentStream = null;

        drawImage(img, new PdfRect(x, y, img.scaledWidth,
            img.scaledHeight), null, PdfMeasurement.MU_POINTS);

        if (contentList == null)
        {
            contentList = new ArrayList();
        }
        ByteBuffer bb = ((ByteBuffer) ByteBuffer.wrap(
            contentStream.getBuffer())
            .limit(contentStream.size())).slice();
        contentList.add(underlay ? 0 : contentList.size(),
            new PdfStream(new PdfDict(new HashMap()), bb));

        contentStream = temp;
        img.width = (float) tImgWidth;
        img.height = (float) tImgHeight;
        img.scaledHeight = (float) tImgScaledHeight;
        img.scaledWidth = (float) tImgScaledWidth;
    }
    
    /**
     * Adds image, specified by its pathname, to footer.
     * 
     * @param path
     *            pathname of the image, which needs to be added to
     *            the footer
     * @param position
     *            constant specifying the combination of vertical and
     *            horizontal alignment of the image within the footer
     * @param underlay
     *            whether the image needs to be placed underneath
     *            other content in the footer
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#addFooterImage_String_int_boolean">example</a>.
     */
    public void addFooterImage(String path, int position,
        boolean underlay) throws IOException, PdfException
    {
        addFooterImage(PdfImage.create(path), position, underlay);
    }
    
    /**
     * Writes specified text on this <code>PdfPage</code>.
     * 
     * @param str
     *            text that needs to be written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String">example</a>.
     */
    public void writeText(String str) throws IOException,
        PdfException
    {
        PdfRect r = new PdfRect(0, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, cursorPosY), PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, cursorPosY)));

        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }

        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        /*if (cursorPosX < this.pageLeftMargin + this.pageCropLeft)
        {
            cursorPosX = this.pageLeftMargin + this.pageCropLeft;
        }*/
        tf.setFirstLinePosition(PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, cursorPosX));
        PdfFont f = prevFont;
        if (f == null)
        {
            f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        moveCursor = true;
        writeText(str, r, f, tf, measurementUnit, true);
        moveCursor = false;
    }

    /**
     * Writes text <code>str</code> with specified alignment and
     * wrap setting.
     * 
     * @param str
     *            text that needs to be written
     * @param alignment
     *            constant specifying how the text needs to be aligned
     * @param wrap
     *            constant specifying how the text needs to be aligned
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_int_boolean">example</a>.
     */
    public void writeText(String str, int alignment, boolean wrap)
        throws IOException, PdfException
    {
        PdfRect r = new PdfRect(0, cursorPosY, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, cursorPosY)));

        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }

        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setWrap(wrap);
        tf.setAlignment(alignment);
        tf.setFirstLinePosition(PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, cursorPosX
            /*- pageLeftMargin*/));
        PdfFont f = prevFont;
        if (f == null)
        {
            f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        moveCursor = true;
        writeText(str, r, f, tf, measurementUnit, true);
        moveCursor = false;
    }

    /**
     * Writes text <code>str</code> with specified wrap setting on 
     * this <codd>PdfPage</code>.
     * 
     * @param str
     *            text that needs to be written
     * @param wrap
     *            constant specifying whether the text needs to be
     *            wrapped
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_boolean">example</a>.
     */
    public void writeText(String str, boolean wrap)
        throws IOException, PdfException
    {
        PdfRect r = new PdfRect(0, cursorPosY, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, cursorPosY)));

        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }

        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setWrap(wrap);
        tf.setFirstLinePosition(PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, cursorPosX
            /*- pageLeftMargin*/));
        PdfFont f = prevFont;
        if (f == null)
        {
            f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        moveCursor = true;
        writeText(str, r, f, tf, measurementUnit, true);
        moveCursor = false;
    }

    /**
     * Writes <code>str</code> with specified alignment.
     * 
     * @param str
     *            text that needs to be written
     * @param alignment
     *            constant specifying how the text needs to be 
     *            aligned
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_int">example</a>.
     */
    public void writeText(String str, int alignment)
        throws IOException, PdfException
    {
        PdfRect r = new PdfRect(0, cursorPosY, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, cursorPosY)));

        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }

        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setAlignment(alignment);
        tf.setFirstLinePosition(PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, cursorPosX
            /*- pageLeftMargin*/));
        PdfFont f = prevFont;
        if (f == null)
        {
            f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        moveCursor = true;
        writeText(str, r, f, tf, measurementUnit, true);
        moveCursor = false;
    }

    /**
     * Writes specified text with specified font.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfFont">example</a>.
     */
    public void writeText(String str, PdfFont f) throws IOException,
        PdfException
    {
        PdfRect r = new PdfRect(0, cursorPosY, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, cursorPosY)));
        
        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }
        
        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setFirstLinePosition(PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, cursorPosX
                /*- pageLeftMargin*/));
        
        moveCursor = true;
        writeText(str, r, f, tf, measurementUnit, true);
        moveCursor = false;
    }

    /**
     * Writes text <code>str</code> with specified font, alignment,
     * and wrap setting.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be aligned
     * @param wrap
     *            constant specifying whether the text needs to be
     *            wrapped
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfFont_int_boolean">example</a>.
     */
    public void writeText(String str, PdfFont f, int alignment,
        boolean wrap) throws IOException, PdfException
    {
        PdfRect r = new PdfRect(0, cursorPosY, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, cursorPosY)));
        
        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }
        
        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setWrap(wrap);
        tf.setAlignment(alignment);
        tf.setFirstLinePosition(PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, cursorPosX
            /*- pageLeftMargin*/));

        moveCursor = true;
        writeText(str, r, f, tf, measurementUnit, true);
        moveCursor = false;
    }

    /**
     * Writes text <code>str</code> with specified font and wrap
     * setting.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param wrap
     *            constant specifying whether the text needs to be
     *            wrapped
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfFont_boolean">example</a>.
     */
    public void writeText(String str, PdfFont f, boolean wrap)
        throws IOException, PdfException
    {
        PdfRect r = new PdfRect(0, cursorPosY, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, cursorPosY)));
        
        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }
        
        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setWrap(wrap);
        tf.setFirstLinePosition(PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, cursorPosX
            /*- pageLeftMargin*/));
        
        moveCursor = true;
        writeText(str, r, f, tf, measurementUnit, true);
        moveCursor = false;
    }

    /**
     * Writes text <code>str</code> with specified font and
     * alignment.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be aligned
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfFont_int">example</a>.
     */
    public void writeText(String str, PdfFont f, int alignment)
        throws IOException, PdfException
    {
        PdfRect r = new PdfRect(0, cursorPosY, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, cursorPosY)));
        
        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }
        
        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setAlignment(alignment);
        tf.setFirstLinePosition(PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, cursorPosX
            /*- pageLeftMargin*/));
        
        moveCursor = true;
        writeText(str, r, f, tf, measurementUnit, true);
        moveCursor = false;
    }

    /**
     * Writes text <code>str</code> at position (<code>x</code>,
     * <code>y</code>).
     * 
     * @param str
     *            text that needs to be written
     * @param x
     *            x-coordinate of the position where the text needs to
     *            be written
     * @param y
     *            y-coordinate of the position where the text needs to
     *            be written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_double_double">example</a>.
     */
    public void writeText(String str, double x, double y)
        throws IOException, PdfException
    {
        PdfRect r = new PdfRect(0, y, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, y)));

        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }
        
        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setFirstLinePosition(x);
        PdfFont f = prevFont;
        if (f == null)
        {
            f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        writeText(str, r, f, tf, measurementUnit, true);
    }
	
    /**
     * Writes specified text at specified point.
     * 
     * @param str
     *            text that needs to be written
     * @param p
     *            <code>PdfPoint</code> where the text needs to be
     *            written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfPoint">example</a>.
     */
    public void writeText(String str, PdfPoint p) throws IOException,
        PdfException
    {
        PdfRect r = new PdfRect(0, p.y, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, p.y)));

        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }

        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setFirstLinePosition(p.x);

        PdfFont f = prevFont;
        if (f == null)
        {
            f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        writeText(str, r, f, tf, measurementUnit, true);
    }
    
    /**
     * Writes text <code>str</code> at position (<code>x</code>,
     * <code>y</code>) with specified alignment.
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
     *            constant specifying how the text needs to be aligned
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_double_double_int">example</a>.
     */
    public void writeText(String str, double x, double y,
        int alignment) throws IOException, PdfException
    {
        PdfRect r = new PdfRect(0, y, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, y)));
        
        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }
        
        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setFirstLinePosition(x);
        tf.setAlignment(alignment);
        
        PdfFont f = prevFont;
        if (f == null)
        {
            f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        writeText(str, r, f, tf, measurementUnit, true);
    }

    /**
     * Writes text <code>str</code> with specified alignment at specified point.
     * 
     * @param str
     *            text that needs to be written
     * @param p
     *            <code>PdfPoint</code> where the text needs to be
     *            written
     * @param alignment
     *            constant specifying how the text needs to be aligned
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfPoint_int">example</a>.
     */
    public void writeText(String str, PdfPoint p, int alignment)
        throws IOException, PdfException
    {
        PdfRect r = new PdfRect(0, p.y, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, p.y)));

        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }

        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setFirstLinePosition(p.x);
        tf.setAlignment(alignment);
        
        PdfFont f = prevFont;
        if (f == null)
        {
            f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        writeText(str, r, f, tf, measurementUnit, true);
    }

    /**
     * Writes text <code>str</code> at position 
     * (<code>x</code>, <code>y</code>) with specified font.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param x
     *            x-coordinate of the position where the text needs to
     *            be written
     * @param y
     *            y-coordinate of the position where the text needs to
     *            be written
     * @throws IOException
     *            if an I/O error occurs. 
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfFont_double_double">example</a>.
     */
    public void writeText(String str, PdfFont f, double x, double y)
        throws IOException, PdfException
	{
        PdfRect r = new PdfRect(0, y, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, y)));

        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }

        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
		tf.setFirstLinePosition(x);
		writeText(str, r, f, tf, measurementUnit, true);
	}

    /**
     * Writes text <code>str</code> with specified font at specified
     * point.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param p
     *            <code>PdfPoint</code> where the text needs to be
     *            written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfFont_PdfPoint">example</a>.
     */
    public void writeText(String str, PdfFont f, PdfPoint p)
        throws IOException, PdfException
    {
        PdfRect r = new PdfRect(0, p.y, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, p.y)));
        
        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }
        
        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setFirstLinePosition(p.x);
        writeText(str, r, f, tf, measurementUnit, true);
    }

    /**
     * Writes text <code>str</code> at position (<code>x</code>,
     * <code>y</code>) with specified font and alignement
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be aligned
     * @param x
     *            x-coordinate of the position where the text needs to
     *            be written
     * @param y
     *            y-coordinate of the position where the text needs to
     *            be written
     * @throws IOException
     *             if an I/O error occurs.
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfFont_int_double_double">example</a>.
     */
    public void writeText(String str, PdfFont f, int alignment,
        double x, double y) throws IOException, PdfException
    {
        PdfRect r = new PdfRect(0, y, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, y)));
        
        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }
        
        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setFirstLinePosition(x);
        tf.setAlignment(alignment);
        writeText(str, r, f, tf, measurementUnit, true);
    }

    /**
     * Writes text <code>str</code> with specified font and
     * alignment at specified point.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be aligned
     * @param p
     *            <code>PdfPoint</code> where the text needs to be 
     *            written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfFont_int_PdfPoint">example</a>.
     */
    public void writeText(String str, PdfFont f, int alignment,
        PdfPoint p) throws IOException, PdfException
    {
        PdfRect r = new PdfRect(0, p.y, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, p.y)));

        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }

        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setFirstLinePosition(p.x);
        tf.setAlignment(alignment);
        writeText(str, r, f, tf, measurementUnit, true);
    }

    /**
     * Writes text <code>str</code> at position (<code>x</code>,
     * <code>y</code>) with specified font and wrap setting.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param x
     *            x-coordinate of the position where the text needs to
     *            be written
     * @param y
     *            y-coordinate of the position where the text needs to
     *            be written
     * @param wrap
     *            constant specifying whether the text needs to be
     *            wrapped
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfFont_double_double_boolean">example</a>.
     */
    public void writeText(String str, PdfFont f, double x, double y,
        boolean wrap) throws IOException, PdfException
    {
        PdfRect r = new PdfRect(0, y, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, y)));

        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }

        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setWrap(wrap);
        tf.setFirstLinePosition(x);
        writeText(str, r, f, tf, measurementUnit, true);
    }

    /**
     * Writes text <code>str</code> with specified font and wrap
     * setting at specified point.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param p
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
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfFont_PdfPoint_boolean">example</a>.
     */
    public void writeText(String str, PdfFont f, PdfPoint p,
        boolean wrap) throws IOException, PdfException
    {
        PdfRect r = new PdfRect(0, p.y, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, p.y)));

        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }

        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setWrap(wrap);
        tf.setFirstLinePosition(p.x);
        writeText(str, r, f, tf, measurementUnit, true);
    }

    /**
     * Writes text <code>str</code> at position (<code>x</code>,
     * <code>y</code>) with specified font, alignment, and wrap
     * setting.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be aligned
     * @param x
     *            x-coordinate of the position where the text needs to
     *            be written
     * @param y
     *            y-coordinate of the position where the text needs to
     *            be written
     * @param wrap
     *            constant specifying whether the text needs to be
     *            wrapped
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfFont_int_double_double_boolean">example</a>.
     */
    public void writeText(String str, PdfFont f, int alignment,
        double x, double y, boolean wrap) throws IOException,
        PdfException
    {
        PdfRect r = new PdfRect(0, y, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, y)));

        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }

        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setWrap(wrap);
        tf.setFirstLinePosition(x);
        tf.setAlignment(alignment);
        writeText(str, r, f, tf, measurementUnit, true);
    }

    /**
     * Writes text <code>str</code> with specified font, alignment,
     * and wrap setting at specified point.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be aligned
     * @param p
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
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfFont_int_PdfPoint_boolean">example</a>.
     */
    public void writeText(String str, PdfFont f, int alignment,
        PdfPoint p, boolean wrap) throws IOException, PdfException
    {
        PdfRect r = new PdfRect(0, p.y, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, p.y)));

        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }

        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setWrap(wrap);
        tf.setFirstLinePosition(p.x);
        tf.setAlignment(alignment);
        writeText(str, r, f, tf, measurementUnit, true);
    }

    /**
     * Writes text <code>str</code> at position 
     * (<code>x</code>, <code>y</code>) with specified wrap setting.
     * 
     * @param str
     *            text that needs to be written
     * @param x
     *            x-coordinate of the position where the text needs to
     *            be written
     * @param y
     *            y-coordinate of the position where the text needs to
     *            be written
     * @param wrap
     *            constant specifying how the text needs to be aligned
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_double_double_boolean">example</a>.
     */
    public void writeText(String str, double x, double y, boolean wrap)
        throws IOException, PdfException
    {
        PdfRect r = new PdfRect(0, y, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, y)));

        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }

        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setWrap(wrap);
        tf.setFirstLinePosition(x);
        PdfFont f = prevFont;
        if (f == null)
        {
            f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        writeText(str, r, f, tf, measurementUnit, true);
    }

    /**
     * Writes text <code>str</code> with specified wrap setting at
     * specified point.
     * 
     * @param str
     *            text that needs to be written
     * @param p
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
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfPoint_boolean">example</a>.
     */
    public void writeText(String str, PdfPoint p, boolean wrap)
        throws IOException, PdfException
    {
        PdfRect r = new PdfRect(0, p.y, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, p.y)));

        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }

        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setWrap(wrap);
        tf.setFirstLinePosition(p.x);
        PdfFont f = prevFont;
        if (f == null)
        {
            f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        writeText(str, r, f, tf, measurementUnit, true);
    }

    /**
     * Writes <code>str</code> at position (<code>x</code>,
     * <code>y</code>) with specified alignment and wrap setting.
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
     *            constant specifying how the text needs to be aligned
     * @param wrap
     *            constant specifying whether the text needs to be
     *            wrapped
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_double_double_int_boolean">example</a>.
     */
    public void writeText(String str, double x, double y,
        int alignment, boolean wrap) throws IOException, PdfException
    {
        PdfRect r = new PdfRect(0, y, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, y)));

        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }

        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setWrap(wrap);
        tf.setFirstLinePosition(x);
        tf.setAlignment(alignment);
        PdfFont f = prevFont;
        if (f == null)
        {
            f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        writeText(str, r, f, tf, measurementUnit, true);
    }

    /**
     * Writes text <code>str</code> with specified alignment and
     * wrap setting at specified point.
     * 
     * @param str
     *            text that needs to be written
     * @param p
     *            <code>PdfPoint</code> where the text needs to be
     *            written
     * @param alignment
     *            constant specifying how the text needs to be aligned
     * @param wrap
     *            constant specifying whether the text needs to be
     *            wrapped
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfPoint_int_boolean">example</a>.
     */
    public void writeText(String str, PdfPoint p, int alignment,
        boolean wrap) throws IOException, PdfException
    {
        PdfRect r = new PdfRect(0, p.y, PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, pageWidth), PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, pageHeight - PdfMeasurement.convertToPdfUnit(
                    measurementUnit, p.y)));

        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }

        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setWrap(wrap);
        tf.setFirstLinePosition(p.x);
        tf.setAlignment(alignment);
        PdfFont f = prevFont;
        if (f == null)
        {
            f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        writeText(str, r, f, tf, measurementUnit, true);
    }
    
    /**
     * Writes text <code>str</code>, rotated by
     * <code>rotation</code> degrees, at specified point.
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>rotation</code> degrees in anti-clockwise direction.
     * </p>
     * <p>
     * This method is recommended only for writing rotated
     * <i>single-line</i> text. For writing rotated <i>multi-line</i>
     * text, the {@link #writeText(String, PdfRect, double, double)}
     * method is preferred.
     * </p>
     * 
     * @param str
     *            text that needs to be written
     * @param p
     *            <code>PdfPoint</code> where the text needs to be
     *            written
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @throws IOException
     *             if an I/O error occurs.
     * @throws PdfException
     *             if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfPoint_double">example</a>.
     */
    public void writeText(String str, PdfPoint p, double rotation)
        throws IOException, PdfException
    {
        PdfFont f = prevFont;
        if (f == null)
        {
            f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        double width = f.getWidth(str, PdfMeasurement.MU_POINTS);
        double height = f.getHeight();

        PdfRect r = new PdfRect(p.x, p.y, PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, width),
            PdfMeasurement.convertToMeasurementUnit(measurementUnit,
                height));
        
        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }

        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setRotation(rotation);
        writeText(str, r, f, tf, measurementUnit, true);
    }

    /**
     * Writes text <code>str</code>, rotated by
     * <code>rotation</code> degrees, at position (<code>x</code>,
     * <code>y</code>).
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>rotation</code> degrees in anti-clockwise direction.
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
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_double_double_double">example</a>.
     */
    public void writeText(String str, double x, double y,
        double rotation) throws IOException, PdfException
    {
        PdfFont f = prevFont;
        if (f == null)
        {
            f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        double width = f.getWidth(str, PdfMeasurement.MU_POINTS);
        double height = f.getHeight();
        
        PdfRect r = new PdfRect(x, y, PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, width),
            PdfMeasurement.convertToMeasurementUnit(measurementUnit, 
                height));

        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }
        
        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setRotation(rotation);
        writeText(str, r, f, tf, measurementUnit, true);
    }

    /**
     * Writes text <code>str</code>, rotated by
     * <code>rotation</code> degrees, at specified point with
     * specified font. 
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>rotation</code> degrees in anti-clockwise direction.
     * </p> 
     * <p>
     * This method is recommended only for writing
     * rotated <i>single-line</i> text. For writing rotated
     * <i>multi-line</i> text, the
     * {@link #writeText(String, PdfFont, PdfRect, double, double)}
     * method is preferred over this.
     * </p>
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param p
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
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfFont_PdfPoint_double">example</a>.
     */
    public void writeText(String str, PdfFont f, PdfPoint p,
        double rotation) throws IOException, PdfException
    {
        if (f == null)
        {
            return;
        }
        double width = f.getWidth(str, PdfMeasurement.MU_POINTS);
        double height = f.getHeight();
        
        PdfRect r = new PdfRect(p.x, p.y, PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, width),
            PdfMeasurement.convertToMeasurementUnit(measurementUnit, 
                height));
        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }
        
        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setRotation(rotation);
        writeText(str, r, f, tf, measurementUnit, true);
    }

    /**
     * Writes text <code>str</code>, rotated by
     * <code>rotation</code> degrees, at specified position with 
     * specified font.
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>rotation</code> degrees in anti-clockwise direction.
     * </p> 
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
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
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfFont_double_double_double">example</a>.
     */
    public void writeText(String str, PdfFont f, double x, double y,
        double rotation) throws IOException, PdfException
    {
        if (f == null)
        {
            return;
        }
        double width = f.getWidth(str, PdfMeasurement.MU_POINTS);
        double height = f.getHeight();
        
        PdfRect r = new PdfRect(x, y, PdfMeasurement
            .convertToMeasurementUnit(measurementUnit, width),
            PdfMeasurement.convertToMeasurementUnit(measurementUnit, 
                height));
        r = updatePageSettings(r);
        if (r == null)
        {
            return;
        }
        
        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setRotation(rotation);
        writeText(str, r, f, tf, measurementUnit, true);
    }
    
	/**
     * Writes specified text inside specified rectanlge.
     * 
     * @param str
     *            text that needs to be written
     * @param rect
     *            rectangle inside which the text needs to be written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfRect">example</a>.
     */
	public void writeText(String str, PdfRect rect)
        throws IOException, PdfException
    {
        rect = updatePageSettings(rect);
        if (rect == null)
        {
            return;
        }
        PdfFont f = prevFont;
        if (f == null)
        {
            f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        writeText(str, rect, f, tf, measurementUnit, true);

    }

    /**
     * Writes text <code>str</code>, rotated by
     * <code>rotation</code> degrees, with specified first-line
     * position inside specified rectangle.
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>rotation</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param str
     *            text that needs to be written
     * @param rect
     *            rectangle inside which the text needs to be written
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @param firstLinePosition
     *            position inside the rectangle where the first line
     *            of text should begin
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfRect_double_double">example</a>.
     */
    public void writeText(String str, PdfRect rect, double rotation,
        double firstLinePosition) throws IOException, PdfException
    {
        rect = updatePageSettings(rect);
        if (rect == null)
        {
            return;
        }
        PdfFont f = prevFont;
        if (f == null)
        {
            f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setRotation(rotation);
        tf.setFirstLinePosition(firstLinePosition);
        writeText(str, rect, f, tf, measurementUnit, true);
    }

	/**
     * Writes text <code>str</code> with specified alignment inside
     * specified rectangle.
     * 
     * @param str
     *            text that needs to be written
     * @param rect
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
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfRect_int">example</a>.
     */
	public void writeText(String str, PdfRect rect, int alignment)
        throws IOException, PdfException
	{
        rect = updatePageSettings(rect);
        if (rect == null)
        {
            return;
        }
        
        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
		tf.setAlignment(alignment);

        PdfFont f = prevFont;
		if (f == null)
		{
		    f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
		}
		writeText(str, rect, f, tf, measurementUnit, true);
	}
	
    /**
     * Writes text <code>str</code>, rotated by
     * <code>rotation</code> degrees, with specified alignment and
     * first-line position inside specified rectangle.
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>rotation</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param str
     *            text that needs to be written
     * @param rect
     *            rectangle inside which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be aligned
     *            inside the rectangle
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @param firstLinePosition
     *            position inside the rectangle where the first line
     *            of text should begin
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfRect_int_double_double">example</a>.
     */
    public void writeText(String str, PdfRect rect, int alignment,
        double rotation, double firstLinePosition)
        throws IOException, PdfException
    {
        rect = updatePageSettings(rect);
        if (rect == null)
        {
            return;
        }
        PdfFont f = prevFont;
        if (f == null)
        {
            f = PdfFont.create("Arial", 10, PdfEncodings.CP1252);
        }
        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setRotation(rotation);
        tf.setFirstLinePosition(firstLinePosition);
        tf.setAlignment(alignment);
        writeText(str, rect, f, tf, measurementUnit, true);
    }

    /**
     * Writes text <code>str</code> with specified font inside
     * specified rectangle.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param rect
     *            rectangle inside which the text needs to be written
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfFont_PdfRect">example</a>.
     */
    public void writeText(String str, PdfFont f, PdfRect rect)
        throws IOException, PdfException
    {
        rect = updatePageSettings(rect);
        if (rect == null)
        {
            return;
        }
        writeText(str, rect, f, tf, measurementUnit, true);
    }

    /**
     * Writes text <code>str</code>, rotated by
     * <code>rotation</code> degrees, with specified font and
     * first-line position inside specified rectangle.
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>rotation</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param rect
     *            rectangle inside which the text needs to be written
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @param firstLinePosition
     *            position inside the rectangle where the first line
     *            of text should begin
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfFont_PdfRect_double_double">example</a>.
     */
    public void writeText(String str, PdfFont f, PdfRect rect,
        double rotation, double firstLinePosition)
        throws IOException, PdfException
    {
        rect = updatePageSettings(rect);
        if (rect == null)
        {
            return;
        }
        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setRotation(rotation);
        tf.setFirstLinePosition(firstLinePosition);
        writeText(str, rect, f, tf, measurementUnit, true);
    }

    /**
     * Writes text <code>str</code> with specified font and
     * alignment inside specified rectangle.
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param rect
     *            rectangle inside which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be aligned
     *            inside the rectangle
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfTextFormatter
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfFont_PdfRect_int">example</a>.
     */
    public void writeText(String str, PdfFont f, PdfRect rect,
        int alignment) throws IOException, PdfException
    {
        rect = updatePageSettings(rect);
        if (rect == null)
        {
            return;
        }
        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setAlignment(alignment);
        writeText(str, rect, f, tf, measurementUnit, true);
    }

    /**
     * Writes text <code>str</code>, rotated by
     * <code>rotation</code> degrees, with specified font,
     * alignment, and first-line position inside specified rectangle.
     * <p>
     * The text is rotated on center of its bounding box by 
     * <code>rotation</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param str
     *            text that needs to be written
     * @param f
     *            font with which the text needs to be written
     * @param rect
     *            rectangle inside which the text needs to be written
     * @param alignment
     *            constant specifying how the text needs to be aligned
     *            inside the rectangle
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the text with reference to  
     *            center of its bounding box
     * @param firstLinePosition
     *            position inside the rectangle where the first line
     *            of text should begin
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#writeText_String_PdfFont_PdfRect_int_double_double">example</a>.
     */
    public void writeText(String str, PdfFont f, PdfRect rect,
        int alignment, double rotation, double firstLinePosition)
        throws IOException, PdfException
    {
        rect = updatePageSettings(rect);
        if (rect == null)
        {
            return;
        }
        PdfTextFormatter tf = (PdfTextFormatter) this
            .getTextFormatter().clone();
        tf.setRotation(rotation);
        tf.setFirstLinePosition(firstLinePosition);
        tf.setAlignment(alignment);
        writeText(str, rect, f, tf, measurementUnit, true);
    }
    
    private boolean processWriteText(String str, PdfRect rect,
        PdfFont font, PdfTextFormatter tf, int unit)
        throws IOException, PdfException
	{
        if(PdfTextParser.containsHTMLtags(str))
        {
            String s;
            PdfFont f;
            double length,firstLinePos;
            int lines;
            PdfRect r = (PdfRect)rect.clone();
            PdfTextFormatter t = (PdfTextFormatter)tf.clone();
            ArrayList strList =  PdfTextParser.replaceHTMLvar(str);
            for(int i=0; i< strList.size();i++)
            {
                Object[] obj = (Object[]) strList.get(i);
                s = (String) obj[0];
                if(obj[1] == null)
                {
                    f = (PdfFont)font.clone();
                }
                else
                {
                    f = (PdfFont)obj[1];
                }
                
                if(( !isPlaceHolderProcessed)
                    && PdfTextParser.containsUserVariable(s))
                {
                    if (userVariableWriteText == null)
                    {
                        userVariableWriteText = new ArrayList();
                    }
                    userVariableWriteText.add(new Object[] { s, r.clone(),
                        f.clone(), t.clone(), new PdfInteger(unit)});
                }
                else
                {
                    writeText(s, r, f, t, unit, false);
                }
                length = f.getWidth(s, PdfMeasurement.MU_POINTS,
                    false) + t.getFirstLinePosition();
                lines = (int) (length / PdfMeasurement
                    .convertToPdfUnit(unit, r.width()));
                firstLinePos = length % PdfMeasurement
                        .convertToPdfUnit(unit, r.width());
                r.y += lines * f.getSize();
                r.height -= lines * f.getSize();
                t = (PdfTextFormatter) t.clone();
                t.setFirstLinePosition(firstLinePos);
            }
            return true;
        }
        else if (( !isPlaceHolderProcessed)
            && PdfTextParser.containsUserVariable(str))
        {
            if (userVariableWriteText == null)
            {
                userVariableWriteText = new ArrayList();
            }
            Object[] obj = { str, rect, font, tf,
                new PdfInteger(unit) };
            userVariableWriteText.add(obj);
            return true;
        }
        return false;
    }
    
	protected synchronized void writeText(String str, PdfRect rect,
        PdfFont font, PdfTextFormatter tf, int unit, boolean processText)
        throws IOException, PdfException
	{
        if (font == null || rect == null || str == null)
        {
            throw new PdfException(
                "Illegal argument(s) to method writeText.");
        }
        if (str == "")
        {
            return;
        }
        if (processText)
        {
            if(processWriteText(str, rect, font, tf, unit))
            {
                return;
            }
        }
        
	    double xTd = 0, yTd = 0;
        StringBuffer rotationMatrix = null;
        
	    if (fontDict == null)
        {
            fontDict = new PdfDict(new HashMap());
        }
        if (fontIndRefDict == null)
        {
            fontIndRefDict = new PdfDict(new HashMap());
        }
        if (contentStream == null)
        {
            contentStream = new PdfByteOutputStream();
        }
        if (tf == null)
        {
            tf = getTextFormatter();
        }

	    StringBuffer sb = new StringBuffer();
		StringBuffer underline = new StringBuffer();
		ArrayList textArr = new ArrayList();
		double diff = 0;
		boolean gsStored = false;
        int align = tf.getAlignment();
		boolean isUnderlined = (font.getStyle() & 
		    PdfFont.UNDERLINE) == PdfFont.UNDERLINE;
		double startLinePos = PdfMeasurement
            .convertToPdfUnit(unit, tf.getFirstLinePosition());

        currentX = 0;
        currentY = 0;
		/* this will populate the textArr List */
        wrapText(str, textArr, rect, startLinePos, font, unit);
        if (textArr.size() == 0)
        {
            return;
        }

		double rectX = PdfMeasurement.convertToPdfUnit(unit, rect.x); 
		double rectY = PdfMeasurement.convertToPdfUnit(unit, rect.y);
		double currentTextWidth = 0;
		String fontName = PDF_NAMESTART + PDF_FONTNAMEPREFIX
            + font.getName();

		if ((font.getStyle() & PdfFont.BOLD) == PdfFont.BOLD)
        {
            fontName += 'B';
        }
        if ((font.getStyle() & PdfFont.ITALIC) == PdfFont.ITALIC)
        {
            fontName += 'I';
        }
        fontName += PDF_SP;

		/* width of first line */
		currentTextWidth = font.getWidth((String) textArr.get(0),
            PdfMeasurement.MU_POINTS, true);
		if (align == PdfTextFormatter.RIGHT
            || align == PdfTextFormatter.CENTER
            || align == PdfTextFormatter.JUSTIFIED)
		{
			diff = (PdfMeasurement.convertToPdfUnit(
			    unit, rect.width) - currentTextWidth);
			if (align == PdfTextFormatter.CENTER)
			{
				diff /= 2;
			}
		}
        if ((font.getStyle() & PdfFont.STROKE_AND_FILL) == PdfFont.STROKE_AND_FILL
            ||(font.getStyle() & PdfFont.STROKE) == PdfFont.STROKE)
        {
            gsStored = true;
            sb.append(PDF_STORE_GS + PDF_SP);
        }
		sb.append(PDF_BEGINTEXT + PDF_SP);
		
		sb.append(fontName + Integer.toString(font.getSize())
		    + PDF_SP + PDF_TEXTFONT + PDF_SP);

        if ((font.getStyle() & PdfFont.STROKE_AND_FILL) == PdfFont.STROKE_AND_FILL)
        {
            sb.append(PDF_SP
                + PdfWriter.formatFloat(font.getStrokeWidth()) + " w ");
            sb.append(Integer.toString(2) + PDF_SP + PDF_TEXTRENDER
                + PDF_SP);
            sb.append(PdfWriter.formatFloat(font.getStrokeColor()
                .getRed() / 255f)
                + PDF_SP);
            sb.append(PdfWriter.formatFloat(font.getStrokeColor()
                .getGreen() / 255f)
                + PDF_SP);
            sb.append(PdfWriter.formatFloat(font.getStrokeColor()
                .getBlue() / 255f)
                + " RG ");
        }
        else if ((font.getStyle() & PdfFont.STROKE) == PdfFont.STROKE)
        {
            sb.append(PdfWriter.formatFloat(font.getStrokeWidth())
                + " w ");
            sb.append(Integer.toString(1) + PDF_SP + PDF_TEXTRENDER
                + PDF_SP);
            sb.append(PdfWriter.formatFloat(font.getStrokeColor()
                .getRed() / 255f)
                + PDF_SP);
            sb.append(PdfWriter.formatFloat(font.getStrokeColor()
                .getGreen() / 255f)
                + PDF_SP);
            sb.append(PdfWriter.formatFloat(font.getStrokeColor()
                .getBlue() / 255f)
                + " RG ");
        }
        else
        {
            sb.append(Integer.toString(0) + PDF_SP + PDF_TEXTRENDER
                + PDF_SP);
        }

        sb.append(PdfWriter.formatFloat(font.getColor().getRed() / 255f)
            + PDF_SP);
        sb.append(PdfWriter
            .formatFloat(font.getColor().getGreen() / 255f)
            + PDF_SP);
        sb.append(PdfWriter
            .formatFloat(font.getColor().getBlue() / 255f)
            + " rg ");

        // x y Td
		if (align == PdfTextFormatter.RIGHT)
		{
		    xTd = rectX - currentX + diff;
		    yTd = pageHeight - rectY - font.getSize();
			if (isUnderlined)
			{
		        double yOffset = font.getSize()
                    + (font.getUnderlinePosition() * 0.012); 
		        double startx = rectX - currentX + diff;
				double starty = this.pageHeight - rectY - yOffset;
				underline.append(PdfWriter.formatFloat(startx) +
					PDF_SP + PdfWriter.formatFloat(starty) + " m ");
				underline.append(PdfWriter.formatFloat(rectX
                    - currentX + currentTextWidth + diff)
                    + PDF_SP + PdfWriter.formatFloat(pageHeight - rectY
                        - yOffset) + " l S ");					
			}
		}
		else if (align == PdfTextFormatter.CENTER)
		{
		    xTd = rectX - currentX + diff + startLinePos / 2;
		    yTd = pageHeight - rectY - font.getSize();
			if (isUnderlined)
			{
		        double yOffset = font.getSize()
                    + (font.getUnderlinePosition() * 0.012); 
		        double startx = rectX - currentX + diff + startLinePos / 2;
				double starty = this.pageHeight - rectY - yOffset;
				underline.append(PdfWriter.formatFloat(startx) +
					PDF_SP + PdfWriter.formatFloat(starty) + " m ");
				underline.append(PdfWriter.formatFloat(rectX
                    - currentX + currentTextWidth + diff + startLinePos / 2)
                    + PDF_SP + PdfWriter.formatFloat(pageHeight - rectY
                        - yOffset) + " l S ");					
			}
		}
		else if (align == PdfTextFormatter.JUSTIFIED)
		{
		    if (countWords((String) textArr.get(0)) > 1)
		    {
				sb.append(PdfWriter.formatFloat((diff - startLinePos)
                    / (countWords((String) textArr.get(0)) - 1))
                    + PDF_SP + PDF_TEXTWIDTH + PDF_SP);
		    }
		    else
		    {
				sb.append(PdfWriter.formatFloat(diff - startLinePos)
                    + PDF_SP + PDF_TEXTWIDTH + PDF_SP);
		    }

		    xTd = rectX + startLinePos - currentX;
		    yTd = pageHeight - rectY - font.getSize();
		    if (isUnderlined)
			{
		        double yOffset = font.getSize()
                    + (font.getUnderlinePosition() * 0.012); 
		        double startx = rectX + startLinePos - (float)currentX;
				double starty = pageHeight - rectY - yOffset;
				underline.append(PdfWriter.formatFloat(startx)
                    + PDF_SP + PdfWriter.formatFloat(starty) + " m ");
				underline.append(PdfWriter.formatFloat(rectX
                    - currentX + currentTextWidth + diff)
                    + PDF_SP + PdfWriter.formatFloat(
                        pageHeight - rectY - yOffset) + " l S ");					
			}
		}
		else //if (align == LEFT)
		{
		    xTd = rectX + startLinePos - currentX;
		    yTd = pageHeight - rectY - font.getSize();

			if (isUnderlined)
			{
		        double yOffset = font.getSize()
                    + (font.getUnderlinePosition() * 0.012); 
			    double startx = rectX + startLinePos - currentX;
				double starty = pageHeight - rectY - yOffset;
				underline.append(PdfWriter.formatFloat(startx)
                    + PDF_SP + PdfWriter.formatFloat(starty) + " m ");
				underline.append(PdfWriter.formatFloat(rectX
                    + startLinePos - currentX + currentTextWidth)
                    + PDF_SP + PdfWriter.formatFloat(pageHeight - rectY
                        - yOffset) + " l S ");					
			}
		}

        if (tf.getRotation() != 0)
		{
            tf.setRotation(tf.getRotation() % 360);
		    double rot = tf.getRotation();
            if (rot < 0)
            //-ve input from user rotate clockwise with abs value
            {
                tf.setRotation(rot + 360);
            }
            if (rot != 0)
            {
                double yOffset = PdfMeasurement.convertToPdfUnit(
                    unit, rect.height);
                double xOffset = PdfMeasurement.convertToPdfUnit(
                    unit, rect.width);
                double radius = Math.sqrt((xOffset * xOffset)
                    + (yOffset * yOffset)) / 2;
                double theta = Math.asin(yOffset / (2 * radius));
                double phi = -rot / 180 * Math.PI;
                double xShift = (radius * Math.cos(theta))
                    - (radius * Math.cos(theta + phi));
                double yShift = (radius * Math.sin(theta + phi))
                    - (radius * Math.sin(theta));
                float cos = (float) Math.cos(phi);
                float sin = (float) Math.sin(phi);
                double Tx = xShift;
                double Ty = yShift;
                double Tx1 = rectX;
                double Ty1 = pageHeight - rectY;
                double xTranslate = Tx + Tx1
                    + (( -Tx1 * cos) + (Ty1 * -sin));
                double yTranslate = Ty + Ty1
                    + (( -Tx1 * -sin) - (Ty1 * cos));
                
                rotationMatrix = new StringBuffer();
                rotationMatrix.append(PdfWriter.formatFloat(cos) + PDF_SP
                    + PdfWriter.formatFloat(-sin) + PDF_SP
                    + PdfWriter.formatFloat(sin) + PDF_SP
                    + PdfWriter.formatFloat(cos) + PDF_SP
                    + PdfWriter.formatFloat(xTranslate) + PDF_SP
                    + PdfWriter.formatFloat(yTranslate) + PDF_SP);
                sb.append(rotationMatrix + PDF_TEXTMATRIX + PDF_SP);
                tf.setRotation(0);
            }
		}
        
        sb.append(PdfWriter.formatFloat(font.getHeight()) + PDF_SP
            + PDF_TEXTLEAD + PDF_SP);
        sb.append(PdfWriter.formatFloat(xTd) + PDF_SP
            + PdfWriter.formatFloat(yTd) + PDF_SP + PDF_TEXTDIMENSION
            + PDF_SP);
		
        sb.append(PdfString.toLiteral(PdfString.escape(new String(
            font.updateGlyphList((String) textArr.get(0)),
            "Cp1252")))
            + PDF_SP + PDF_SHOWTEXT + PDF_SP);
        
        /*sb.append(PdfString.toLiteral(PdfString
            .escape((String) textArr.get(0)))
            + PDF_SP + PDF_SHOWTEXT + PDF_SP);*/

        isLastItemShape = false;
		if (align == PdfTextFormatter.RIGHT
            || align == PdfTextFormatter.CENTER)
		{
			currentX = rectX + startLinePos + diff;
		}
		else
		{
			currentX = rectX + startLinePos;
		}
		currentY = rectY;

		if (tf.isWrap())
		{
			int limit = textArr.size();
		    for (int i = 1; i < limit; i++)
			{
				// width of current string
		        currentTextWidth = font.getWidth((String) textArr
                  .get(i), PdfMeasurement.MU_POINTS, true);

				if (align == PdfTextFormatter.RIGHT
                    || align == PdfTextFormatter.CENTER
                    || align == PdfTextFormatter.JUSTIFIED)
				{
					diff = (PdfMeasurement.convertToPdfUnit(unit,
                        rect.width) - currentTextWidth);
					if (align == PdfTextFormatter.CENTER)
					{
						diff /= 2;
					}
				}
			
				// x y Td
				if (align == PdfTextFormatter.RIGHT)
				{
					sb.append(PdfWriter.formatFloat(rectX - currentX
                        + diff + startLinePos) + PDF_SP
                        + PdfWriter.formatFloat(currentY - rectY)
                        + PDF_SP + PDF_TEXTDIMENSION + PDF_SP);

					// underline
					if (isUnderlined)
					{
					    double yOffset = font.getUnderlinePosition() * 0.012;
					    double startx = rectX + diff;
					    double starty = pageHeight - currentY
                            - font.getHeight() * (i + 1) - yOffset
                            + (font.getHeight() - font.getSize());
                        
					    underline.append(PdfWriter.formatFloat(startx)
					        + PDF_SP + PdfWriter.formatFloat(starty)
					        + " m ");
					    underline.append(PdfWriter.formatFloat(startx
					        + currentTextWidth) + PDF_SP
					        + PdfWriter.formatFloat(starty) + " l S ");
					}
					startLinePos = 0;
				}
				else if (align == PdfTextFormatter.CENTER)
				{
					sb.append(PdfWriter.formatFloat(rectX - currentX
                        + diff + startLinePos / 2) + PDF_SP
                        + PdfWriter.formatFloat(currentY - rectY)
                        + PDF_SP + PDF_TEXTDIMENSION + PDF_SP);

					// underline
					if (isUnderlined)
					{
					    double yOffset = font.getUnderlinePosition() * 0.012;
					    double startx = rectX + diff;
					    double starty = pageHeight - currentY
                            - font.getHeight() * (i + 1) - yOffset
                            + (font.getHeight() - font.getSize());
                        
					    underline.append(PdfWriter.formatFloat(startx)
					        + PDF_SP + PdfWriter.formatFloat(starty)
					        + " m ");
					    underline.append(PdfWriter.formatFloat(startx
					        + currentTextWidth) + PDF_SP
					        + PdfWriter.formatFloat(starty) + " l S ");
						startLinePos = 0;
					}
				}
				else if (align == PdfTextFormatter.JUSTIFIED)
				{
					if (i == limit - 1 && !tf.isJustifyLastLine())
					{
						sb.append("0 " + PDF_TEXTWIDTH + PDF_SP); 
					}
					else
					{
						if (countWords((String)textArr.get(i)) > 1)
						{
							sb.append(PdfWriter
                                .formatFloat(diff
                                    / (countWords((String) textArr
                                        .get(i)) - 1))
                                + PDF_SP + PDF_TEXTWIDTH + PDF_SP);
						}
						else
						{
							sb.append("0 " + PDF_TEXTWIDTH + PDF_SP);
						}
					}
					sb.append(PdfWriter.formatFloat(rectX - currentX)
					    + PDF_SP + PdfWriter.formatFloat(currentY - rectY)
					    + PDF_SP + PDF_TEXTDIMENSION + PDF_SP);

					// underline
					if (isUnderlined)
					{
					    double yOffset = font.getUnderlinePosition() * 0.012;
					    double startx = currentX - startLinePos;
					    double starty = pageHeight - currentY
                            - font.getHeight() * (i + 1) - yOffset
                            + (font.getHeight() - font.getSize());
						underline.append(PdfWriter.formatFloat(startx) 
						    + PDF_SP + PdfWriter.formatFloat(starty)
						    + " m ");
						if (i == limit - 1 && !tf.isJustifyLastLine())
						{
							underline.append(PdfWriter
                                .formatFloat(startx
                                    + currentTextWidth)
                                + PDF_SP + PdfWriter.formatFloat(starty)
                                + " l S ");			
						}
						else
						{
							underline.append(PdfWriter
                                .formatFloat(startx
                                    + PdfMeasurement
                                        .convertToPdfUnit(unit,
                                            rect.width))
                                + PDF_SP
                                + PdfWriter.formatFloat(starty)
                                + " l S ");			
						}
						startLinePos = 0;
					}
				}
				else //align == LEFT
				{
					sb.append(PdfWriter.formatFloat(rectX - currentX)
					    + PDF_SP + PdfWriter.formatFloat(currentY - rectY)
					    + PDF_SP + PDF_TEXTDIMENSION + PDF_SP);

					// underline
					if (isUnderlined)
					{
					    double yOffset = font.getUnderlinePosition() * 0.012;
					    double startx = currentX - startLinePos /*+ xShift*/;
					    double starty = pageHeight - currentY
                            - font.getHeight() * (i + 1) - yOffset
                            + (font.getHeight() - font.getSize());
					    underline.append(PdfWriter.formatFloat(startx) + PDF_SP
					        + PdfWriter.formatFloat(starty) + " m ");
					    underline.append(PdfWriter.formatFloat(startx
					        + currentTextWidth) + PDF_SP
					        + PdfWriter.formatFloat(starty) + " l S ");
					    startLinePos = 0;
					}
				}
		
				if (i < limit)
				{
					sb.append(PDF_TEXTNEWLINESTART + PDF_SP);
				}

                sb.append(PdfString.toLiteral(PdfString.escape(new String(
                    font.updateGlyphList((String) textArr.get(i)),
                    "Cp1252")))
                    + PDF_SP + PDF_SHOWTEXT + PDF_SP);

                /*sb.append(PDF_LITERALSTRINGSTART
				    + (String) textArr.get(i) + PDF_LITERALSTRINGEND
				    + PDF_SHOWTEXT + PDF_SP);*/
				if (align == PdfTextFormatter.RIGHT)
				{
					currentX = rectX + diff;
				}
				else if (align == PdfTextFormatter.CENTER)
				{
					currentX = rectX + diff + startLinePos / 2;
				}
				else
				{
					currentX = rectX;
				}
				currentY = rectY;
			}
		}

		/* override previous Tw */
		if (align == PdfTextFormatter.JUSTIFIED)
		{
			sb.append("0 " + PDF_TEXTWIDTH + PDF_SP);
		}

		/* update cursor positions */
		if (moveCursor)
		{
			int limit = textArr.size();
		    if (align == PdfTextFormatter.LEFT)
			{
				if (tf.isWrap())
				{
                    cursorPosY += (limit - 1) * font.getHeight();
					if (limit > 1)
					{
						cursorPosX = font.getWidth((String) textArr
			                  .get(limit - 1), PdfMeasurement.MU_POINTS, true); 
					}
					else
					{
						cursorPosX += font.getWidth((String)textArr
						    .get(limit - 1), PdfMeasurement.MU_POINTS, true);
					}
				}
				else
				{
					cursorPosX += font.getWidth((String) textArr
                        .get(0), PdfMeasurement.MU_POINTS, true);
					if (cursorPosX >= pageWidth)
					{
						cursorPosX = 0;
                        cursorPosY += font.getHeight();
					}
				}
			}
			else
			{
				if (tf.isWrap())
				{
                    cursorPosY += limit * font.getHeight();
				}
				else
				{
                    cursorPosY += font.getHeight();
				}
				cursorPosX = PdfMeasurement.convertToPdfUnit(unit,
                    rect.x);
			}
		}

		sb.append(PDF_ENDTEXT + PDF_SP);
		if (gsStored)
		{
		    sb.append(PDF_RESTORE_GS + PDF_SP);
		    gsStored = false;
		}
        
		if (rotationMatrix != null)
        {
		    sb.append(PDF_STORE_GS + PDF_SP + rotationMatrix + PDF_CM
                + PDF_SP + underline + PDF_RESTORE_GS + PDF_SP);
        }
        else
        {
            sb.append(underline);
        }
		contentStream.write(sb.toString().getBytes());

        fontName = fontName.substring(1, fontName.length() - 1);
        PdfFont obj = fontDict.getValueAsFont(new PdfName(fontName));
        if (obj == null)
        {
            PdfName fn = new PdfName(fontName);
            fontDict.getMap().put(fn, font);
            fontIndRefDict.getMap().put(fn, fn);
        }
        font.updateGlyphList(str);
        prevFont = (PdfFont) font.clone();
	}

	protected synchronized void wrapText(String str, ArrayList list,
        PdfRect rect, double startLinePos, PdfFont font, int unit)
        throws PdfException
	{
		String s = "";
		double width = startLinePos;
		double height = 0;
		int lastWhiteChar = -1;
		int limit = str.length();
        boolean added = false;
		for (int i = 0; i < limit; i++)
		{
            added = false;
			//check new width less than rect.widht
			double temp = font.getWidth(str.charAt(i),
                PdfMeasurement.MU_POINTS);
			if (temp > PdfMeasurement.convertToPdfUnit(unit, rect.width)) 
			{
				throw new PdfException(
                    "Text region too small to accomodate text");
			}
			if (str.charAt(i) == ' ' || str.charAt(i) == '\t')
			{
				lastWhiteChar = s.length();
			}
			if (width + temp <= PdfMeasurement.convertToPdfUnit(unit,
                rect.width))
			{
				s += str.charAt(i);
				width += temp;
			}
			else //next line
			{
				//check new height
                if (height + font.getHeight() <= PdfMeasurement
                   .convertToPdfUnit(unit, rect.height))
				{
                    height += font.getHeight();
					if (lastWhiteChar != -1)
					{
						String subStr = s.substring(0, lastWhiteChar);
						list.add(subStr);
						i = i - (s.length() - subStr.length());
					}
					else
					{
						list.add(s);
						i--;
					}
					width = 0;
					lastWhiteChar = -1;
					s = "";
                    added = true;
				}
				else //wrapping complete
				{
				    if (lastWhiteChar != -1)
				    {
//				        i -= s.length();
				        return;
				    }
				}
			}
		}
//		if (!(s.equals("")) && width < PdfMeasurement.convertToPdfUnit(unit,
//            rect.width) && height + font.getSize() <= PdfMeasurement
//            .convertToPdfUnit(unit, rect.height))
        if (!added && height + font.getHeight() <= PdfMeasurement
          .convertToPdfUnit(unit, rect.height))
		{
			list.add(s);
		}
		
	}
    
	protected synchronized String wrapText(String str, PdfRect rect,
        double startLinePos, PdfFont font, int unit)
        throws PdfException
	{
		String s = "";
		double width = PdfMeasurement.convertToPdfUnit(unit, startLinePos);
		double height = 0;
		int lastWhiteChar = -1;
		int limit = str.length();
        boolean added = false;
		for (int i = 0; i < limit; i++)
		{
            added = false;
            if (height + font.getHeight() > PdfMeasurement.convertToPdfUnit(unit,
                rect.height))
            {
                return str.substring(i);
            }
			//check new width less than rect.widht
			double temp = font.getWidth(str.charAt(i),
                PdfMeasurement.MU_POINTS);
			if (temp > PdfMeasurement.convertToPdfUnit(unit, rect.width)) 
			{
				throw new PdfException(
                    "Text region too small to accomodate text");
			}
			if (str.charAt(i) == ' ' || str.charAt(i) == '\t')
			{
				lastWhiteChar = s.length();
			}
			if (width + temp <= PdfMeasurement.convertToPdfUnit(unit,
                rect.width))
			{
				s += str.charAt(i);
				width += temp;
			}
			else //next line
			{
				//check new height
				if (height + font.getHeight() <= PdfMeasurement
                    .convertToPdfUnit(unit, rect.height))
				{
					height += font.getHeight();
					if (lastWhiteChar != -1)
					{
						String subStr = s.substring(0, lastWhiteChar);
						i = i - (s.length() - subStr.length());
					}
					else
					{
						i--;
					}
					width = 0;
					lastWhiteChar = -1;
					s = "";
                    added = false;
				}
				else //wrapping complete
				{
				    if (lastWhiteChar != -1)
				    {
				        i -= s.length();
				    }
					return str.substring(i);
				}
			}
		}
//		if (width < PdfMeasurement.convertToPdfUnit(unit,
//            rect.width) && height + font.getSize() <= PdfMeasurement
//            .convertToPdfUnit(unit, rect.height))
        if (!added && height + font.getHeight() <= PdfMeasurement
          .convertToPdfUnit(unit, rect.height))
		{
		    return "";
		}
		
		return s;
	}
	
    protected void setPattern() throws IOException
    {
        if ((prevBrush != null)
            && (brush.brushPattern == prevBrush.brushPattern))
        { 
            return;
        }
        HashMap pm = new HashMap();
        pm.put(new PdfName(PDF_TYPE), new PdfName(PDF_PATTERN));
        pm.put(new PdfName(PDF_PATTERN_TYPE), new PdfInteger(1));
        pm.put(new PdfName(PDF_PAINT_TYPE), new PdfInteger(2));
        pm.put(new PdfName(PDF_TILING_TYPE), new PdfInteger(1));
        pm.put(new PdfName(PDF_XSTEP), new PdfInteger(5));
        pm.put(new PdfName(PDF_YSTEP), new PdfInteger(5));
        ArrayList l= new ArrayList();
        l.add(new PdfInteger(0));
        l.add(new PdfInteger(0));
        l.add(new PdfInteger(5));
        l.add(new PdfInteger(5));
        pm.put(new PdfName(PDF_BBOX), new PdfArray(l));
        
        l = new ArrayList();
        l.add(new PdfName(PDF_PDF));
        HashMap resMap = new HashMap();
        resMap.put(new PdfName(PDF_PROCSET), new PdfArray(l));
        pm.put(new PdfName(PDF_RESOURCES), new PdfDict(resMap));
        
        StringBuffer psb = new StringBuffer();
        String str;
        switch (brush.brushPattern)	
        {
            case PdfBrush.PATTERN_FORWARD_DIAGONAL:
                str = "2 J 2 j 0.4 w 0 2.5 m 2.5 0 l 5 2.5 m 2.5 5 l S";
                break;
            case PdfBrush.PATTERN_BACKWARD_DIAGONAL:
                str = "2 J 2 j 0.4 w 0 2.5 m 2.5 5 l 2.5 0 m 5 2.5 l S";
                break;
            case PdfBrush.PATTERN_HORIZONTAL:
                str = "2 J 2 j 0.4 w 0 2.5 m 5 2.5 l S";
                break;
            case PdfBrush.PATTERN_VERTICAL:
                str = "2 J 2 j 0.4 w 2.5 0 m 2.5 5 l S";
                break;
            case PdfBrush.PATTERN_CROSS:
                str = "2 J 2 j 0.4 w 0 2.5 m 5 2.5 l 2.5 0 m 2.5 5 l S";
                break;
            case PdfBrush.PATTERN_DIAGONAL_CROSS:
                str = "2 J 2 j 0.3 w 2.5 0 m 5 2.5 l 2.5 5 l 0 2.5 l s";
                break;
            default: str = null;
                break;
        }
        psb.append(str);
        pm.put(new PdfName(PDF_LENGTH), new PdfInteger(psb.length()));
        pm.put(new PdfName("RubPattern"), new PdfInteger(brush.brushPattern));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write((psb.toString()).getBytes());
        PdfStream patternStream = new PdfStream(new PdfDict(pm), ByteBuffer.wrap(baos
            .toByteArray()));
        if (patternList == null)
        {
            patternList = new ArrayList();
        }
        patternList.add(patternStream);
        prevBrush = (PdfBrush) brush.clone();;
    }
    
    protected StringBuffer setFillPattern() throws IOException
    {
        if (brush.brushPattern == PdfBrush.PATTERN_SOLID)
        {
            return new StringBuffer("");
        }
        
        StringBuffer sb = new StringBuffer();
            sb.append(PDF_NAMESTART + PDF_CSP + PDF_SP + PDF_CS + PDF_SP);
        sb.append(PdfWriter
            .formatFloat(brush.fillColor.getRed() / 255f)
            + PDF_SP);
        sb.append(PdfWriter
            .formatFloat(brush.fillColor.getGreen() / 255f)
            + PDF_SP);
        sb.append(PdfWriter
            .formatFloat(brush.fillColor.getBlue() / 255f)
            + PDF_SP);
        if (brush.brushPattern == PdfBrush.PATTERN_FORWARD_DIAGONAL)
        {
            sb.append(PDF_NAMESTART + PDF_PFD + PDF_SP);
        }
        else if (brush.brushPattern == PdfBrush.PATTERN_BACKWARD_DIAGONAL)
        {
            sb.append(PDF_NAMESTART + PDF_PBD + PDF_SP);
        }
        else if (brush.brushPattern == PdfBrush.PATTERN_HORIZONTAL)
        {
            sb.append(PDF_NAMESTART + PDF_PH + PDF_SP);
        }
        else if (brush.brushPattern == PdfBrush.PATTERN_VERTICAL)
        {
            sb.append(PDF_NAMESTART + PDF_PV + PDF_SP);
        }
        else if (brush.brushPattern == PdfBrush.PATTERN_CROSS)
        {
            sb.append(PDF_NAMESTART + PDF_PC + PDF_SP);
        }
        else if (brush.brushPattern == PdfBrush.PATTERN_DIAGONAL_CROSS)
        {
            sb.append(PDF_NAMESTART + PDF_PDC + PDF_SP);
        }

        sb.append(PDF_SCN + PDF_SP + PDF_ENDPATH + PDF_SP);
        return sb;
    }

    protected StringBuffer drawInternalLine(double endx, double endy)
        throws IOException
    {
        PdfPoint end = updatePageSettings(new PdfPoint(endx, endy));
        endx = PdfMeasurement.convertToPdfUnit(measurementUnit, end.x);
        endy = PdfMeasurement.convertToPdfUnit(measurementUnit, end.y);
        endy = this.pageHeight - endy;
        
        StringBuffer sb = new StringBuffer();
        sb.append(PdfWriter.formatFloat( endx)
            + PDF_SP + PdfWriter.formatFloat( endy) + " l ");
        currentX = endx;
        currentY = endy;
        
        return sb;
    }

    /**
     * Draws a line between positions 
     * (<code>startx</code>,  <code>starty</code>) and 
     * (<code>endx</code>, <code>endy</code>).
     * 
     * @param startx
     *            x-coordinate of the starting position of the line
     * @param starty
     *            y-coordinate of the starting position of the line
     * @param endx
     *            x-coordinate of the ending position of the line
     * @param endy
     *            y-coordinate of the ending position of the line
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawLine_double_double_double_double">example</a>.
     */
    public synchronized void drawLine(double startx, double starty,
        double endx, double endy) throws IOException
    {
        PdfPoint start = updatePageSettings(new PdfPoint(startx,
            starty));

        double startX = PdfMeasurement.convertToPdfUnit(measurementUnit,
            start.x);
        double startY = PdfMeasurement.convertToPdfUnit(measurementUnit,
            start.y);
        startY = this.pageHeight - startY;
        StringBuffer sb = new StringBuffer();
        sb.append (setPenBrush(false, true));
        sb.append(PdfWriter.formatFloat(startX)
            + PDF_SP + PdfWriter.formatFloat(startY) + " m ");
        sb.append(drawInternalLine(endx, endy));
        sb.append(" S ");
        
        if (contentStream == null)
        {
            contentStream = new PdfByteOutputStream();
        }
        contentStream.write((sb.toString()).getBytes());
    }

    /**
     * Draws a line between points <code>start</code> and
     * <code>end</code>.
     * 
     * @param start
     *            starting point of the line
     * @param end
     *            ending point of the line
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawLine_PdfPoint_PdfPoint">example</a>.
     */
    public void drawLine(PdfPoint start, PdfPoint end)
        throws IOException
    {
        drawLine(start.getX(), start.getY(), end.getX(), end.getY());
    }

    /**
     * Draws a rectangle at specified point with specified width,
     * height, brush, and pen settings.
     * 
     * @param p
     *            position of the top-left corner of the rectangle
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
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawRect_PdfPoint_double_double_boolean_boolean">example</a>.
     */
    public void drawRect(PdfPoint p, double width, double height,
        boolean isFill, boolean isStroke) throws IOException
    {
        drawRect(p.x, p.y, width, height, isFill, isStroke);
    }

    /**
     * Draws a rectangle at position (<code>x</code>, <code>y</code>)
     * with specified width, height, brush, and pen settings.
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
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawRect_double_double_double_double_boolean_boolean">example</a>.
     */
    public synchronized void drawRect(double x, double y,
        double width, double height, boolean isFill, boolean isStroke)
        throws IOException
    {
        PdfRect r = updatePageSettings(new PdfRect(x, y, width,
            height));
        if (r == null)
        {
            return;
        }
        x = PdfMeasurement.convertToPdfUnit(measurementUnit, r.x);
        y = PdfMeasurement.convertToPdfUnit(measurementUnit, r.y);
        width = PdfMeasurement.convertToPdfUnit(measurementUnit,
            r.width);
        height = PdfMeasurement.convertToPdfUnit(measurementUnit,
            r.height);
        y = this.pageHeight - y - height;

        StringBuffer sb = new StringBuffer();
        sb.append(setPenBrush(isFill, isStroke));
        if ((prevBrush != brush) && (isFill == true))
        {
            setPattern();
        }
        if (isFill)
        {
            if (brush.brushPattern != PdfBrush.PATTERN_SOLID)
            {
                sb.append(setFillPattern());
                sb.append(PdfWriter.formatFloat(x) + PDF_SP
                    + PdfWriter.formatFloat(y) + PDF_SP
                    + PdfWriter.formatFloat(width) + PDF_SP
                    + PdfWriter.formatFloat(height) + " re f ");
                isFill = false;
            }
        }

        sb.append(PdfWriter.formatFloat(x) + PDF_SP
            + PdfWriter.formatFloat(y) + PDF_SP
            + PdfWriter.formatFloat(width) + PDF_SP
            + PdfWriter.formatFloat(height) + " re ");

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

        if (contentStream == null)
        {
            contentStream = new PdfByteOutputStream();
        }
        contentStream.write((sb.toString()).getBytes());
    }

    public void drawRect(Rectangle r) throws IOException
    {
        drawRect(r.getX(), r.getY(), r.getWidth(), r.getHeight(),
            false, true);
    }

    /**
     * Draws a rectangle.
     * 
     * @param r rectangle that needs to be drawn
     * @throws IOException if an I/O error occurs. 
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawRect_PdfRect">example</a>.
     */
    public void drawRect(PdfRect r) throws IOException
    {
        drawRect(r.getX(), r.getY(), r.width, r.height, false, true);
    }

    /**
     * Draws a rectangle at position (<code>x</code>, <code>y</code>)
     * with specified width and height.
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
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawRect_double_double_double_double">example</a>.
     */
    public void drawRect(double x, double y, double width,
        double height) throws IOException
    {
        drawRect(x, y, width, height, false, true);
    }

    /**
     * Draws a rectangle at specified point with specified width and
     * height.
     * 
     * @param p
     *            position of the top-left corner of the rectangle
     * @param width
     *            width of the rectangle
     * @param height
     *            height of the rectangle
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawRect_PdfPoint_double_double">example</a>.
     */
    public void drawRect(PdfPoint p, double width, double height)
        throws IOException
    {
        drawRect(p, width, height, false, true);
    }

    /**
     * Draws a square at position (<code>x</code>, <code>y</code>)
     * with specified fill and stroke settings.
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
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawSquare_double_double_double_boolean_boolean">example</a>.
     */
    public void drawSquare(double x, double y, double length,
        boolean isFill, boolean isStroke) throws IOException
    {
        drawRect(x, y, length, length, isFill, isStroke);
    }

    /**
     * Draws a square at position (<code>x</code>, <code>y</code>).
     * 
     * @param x
     *            x-coordinate of the top-left corner of the square
     * @param y
     *            y-coordinate of the top-left corner of the square
     * @param length
     *            length of a side of the square
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawSquare_double_double_double">example</a>.
     */
    public void drawSquare(double x, double y, double length)
        throws IOException
    {
        drawRect(x, y, length, length, false, true);
    }

    /**
     * Draws a square at point <code>p</code> with specified fill
     * and stroke settings.
     * 
     * @param p
     *            position of top-left corner of the rectangle
     * @param length
     *            length of a side of the square
     * @param isFill
     *            whether the square needs to be filled
     * @param isStroke
     *            whether the square needs to be stroked
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawSquare_PdfPoint_double_boolean_boolean">example</a>.
     */
    public void drawSquare(PdfPoint p, double length, boolean isFill,
        boolean isStroke) throws IOException
    {
        drawRect(p, length, length, isFill, isStroke);
    }

    /**
     * Draws a square at point <code>p</code>.
     * 
     * @param p
     *            position of top-left corner of the rectangle
     * @param length
     *            length of a side of the square
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawSquare_PdfPoint_double">example</a>.
     */
    public void drawSquare(PdfPoint p, double length)
        throws IOException
    {
        drawRect(p, length, length, false, true);
    }

    /**
     * Draws a Bézier curve with two control points.
     * 
     * @param start
     *            starting point of the curve
     * @param controlPoint1
     *            first control point of the curve
     * @param controlPoint2
     *            second control point of the curve
     * @param end
     *            end point of the curve
     * @param isFill
     *            whether the curve needs to be filled
     * @param isStroke
     *            whether the curve needs to be stroked
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawBezierCurve_PdfPoint_PdfPoint_PdfPoint_PdfPoint_boolean_boolean">example</a>.
     */
    public synchronized void drawBezierCurve(PdfPoint start,
        PdfPoint controlPoint1, PdfPoint controlPoint2, PdfPoint end,
        boolean isFill, boolean isStroke) throws IOException
    {
        drawBezierCurve(start.x, start.y, controlPoint1.x,
            controlPoint1.y, controlPoint2.x, controlPoint2.y, end.x,
            end.y, isFill, isStroke);
    }

    /**
     * Draws a Bézier curve with control points at 
     * (<code>ctrlX1</code>, <code>ctrlY1</code>) and 
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
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawBezierCurve_double_double_double_double_double_double_double_double_boolean_boolean">example</a>.
     */
    public synchronized void drawBezierCurve(double startX, double startY,
        double ctrlX1, double ctrlY1, double ctrlX2, double ctrlY2,
        double endX, double endY, boolean isFill, boolean isStroke)
        throws IOException
    {
        PdfPoint start = updatePageSettings(new PdfPoint(startX, startY));
        startX = PdfMeasurement.convertToPdfUnit(measurementUnit,
            (float) start.x);
        startY = PdfMeasurement.convertToPdfUnit(measurementUnit,
            (float) start.y);

        StringBuffer sb = new StringBuffer();
        sb.append(setPenBrush(isFill, isStroke));
        sb.append(PdfWriter.formatFloat(startX) + PDF_SP
            + PdfWriter.formatFloat((pageHeight - startY)));
        sb.append(" m ");
        sb.append(drawInternalBezier(ctrlX1, ctrlY1, ctrlX2, ctrlY2,
            endX, endY));

        if ((isFill == true) && (isStroke == false))
        {
            sb.append("f ");
        }
        else if ((isFill == false) && (isStroke == true))
        {
            sb.append(" S ");
        }
        else if ((isFill == true) && (isStroke == true))
        {
            sb.append("h B ");
        }
        
        if(contentStream == null)
        {
            contentStream = new PdfByteOutputStream();
        }
        contentStream.write((sb.toString()).getBytes());
        currentX = endX;
        currentY = endY;
    }

    /**
     * Draws a Bézier curve with a single control point.
     * 
     * @param start
     *            starting point of the curve
     * @param control
     *            control point of the curve
     * @param end
     *            end point of the curve
     * @param isFill
     *            whether the curve needs to be filled
     * @param isStroke
     *            whether the curve needs to be stroked
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawBezierCurve_PdfPoint_PdfPoint_PdfPoint_boolean_boolean">example</a>.
     */
    public synchronized void drawBezierCurve(PdfPoint start,
        PdfPoint control, PdfPoint end, boolean isFill,
        boolean isStroke) throws IOException
    {
        drawBezierCurve(start.x, start.y, control.x, control.y,
            end.x, end.y, isFill, isStroke);
    }
    
    /**
     * Draws a Bézier curve with a control point specified by 
     * (<code>ctrlX</code>, <code>ctrlY</code>).
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
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawBezierCurve_double_double_double_double_double_double_boolean_boolean">example</a>.
     */
    public synchronized void drawBezierCurve(double startX, double startY,
        double ctrlX, double ctrlY, double endX, double endY,
        boolean isFill, boolean isStroke) throws IOException
    {
        PdfPoint start = updatePageSettings(new PdfPoint(startX,
            startY));
        PdfPoint end = updatePageSettings(new PdfPoint(endX,
            endY));
        PdfPoint control = updatePageSettings(new PdfPoint(ctrlX,
            ctrlY));

        startX = PdfMeasurement.convertToPdfUnit(measurementUnit,
            (float) start.x);
        startY = PdfMeasurement.convertToPdfUnit(measurementUnit,
            (float) start.y);
        endX = PdfMeasurement.convertToPdfUnit(measurementUnit,
            (float) end.x);
        endY = PdfMeasurement.convertToPdfUnit(measurementUnit,
            (float) end.y);
        ctrlX = PdfMeasurement.convertToPdfUnit(measurementUnit,
            (float) control.x);
        ctrlY = PdfMeasurement.convertToPdfUnit(measurementUnit,
            (float) control.y);

        startY = this.pageHeight - startY;
        endY = this.pageHeight - endY;
        ctrlY = this.pageHeight - ctrlY;

        StringBuffer sb = new StringBuffer();
        sb.append(setPenBrush(isFill, isStroke));
        sb.append(PDF_SP + PdfWriter.formatFloat( startX)
            + PDF_SP + PdfWriter.formatFloat( startY));
        sb.append(PDF_SP + " m ");

        sb.append(PDF_SP + PdfWriter.formatFloat( ctrlX)
            + PDF_SP + PdfWriter.formatFloat( ctrlY));
        sb.append(PDF_SP + PdfWriter.formatFloat( endX)
            + PDF_SP + PdfWriter.formatFloat( endY));
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
        
        if(contentStream == null)
        {
            contentStream = new PdfByteOutputStream();
        }
        contentStream.write((sb.toString()).getBytes());
        currentX = endX;
        currentY = endY;
    }

    /**
     * Draws an ellipse whose bounding box has its top-left corner 
     * at (<code>x1</code>, <code>y1</code>) and its bottom-right 
     * corner at (<code>x2</code>, <code>y2</code>).
     * 
     * @param x1
     *            x-coordinate of the top-left corner of the 
     *            ellipse's bounding box
     * @param y1
     *            y-coordinate of the top-left corner of the 
     *            ellipse's bounding box
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
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawEllipse_double_double_double_double_boolean_boolean">example</a>.
     */
    public synchronized void drawEllipse(double x1, double y1, double x2,
        double y2, boolean isFill, boolean isStroke)
        throws IOException
    {
        double cpFactor = (4.0 / 3.0) * (Math.sqrt(2) - 1.0);

        double a1, b1, c1, d1, e1;
        double a2, b2, c2, d2, e2;

        if (x1 < x2)
        {
            a1 = x1;
        }
        else
        {
            a1 = x2;
        }

        if (x1 > x2)
        {
            e1 = x1;
        }
        else
        {
            e1 = x2;
        }

        c1 = (x1 + x2) / 2.0;
        b1 = c1 - cpFactor * (c1 - a1);
        d1 = c1 + cpFactor * (c1 - a1);

        if (y1 < y2)
        {
            e2 = y1;
        }
        else
        {
            e2 = y2;
        }

        if (y1 > y2)
        {
            a2 = y1;
        }
        else
        {
            a2 = y2;
        }

        c2 = (y1 + y2) / 2.0;
        b2 = c2 - cpFactor * (c2 - a2);
        d2 = c2 + cpFactor * (c2 - a2);

        currentX = c1;
        currentY = a2;
        
        StringBuffer sb = new StringBuffer();
        
        double cc1 = PdfMeasurement.convertToPdfUnit(measurementUnit,
            (float) c1);
        double aa2 = PdfMeasurement.convertToPdfUnit(measurementUnit,
            (float) a2);

        cc1 = cc1 + pageLeftMargin + pageCropLeft;
        aa2 = aa2 + pageTopMargin + pageHeaderHeight + pageCropTop;
        sb.append(setPenBrush(isFill, isStroke));
        if ((prevBrush != brush) && (isFill == true))
        {
            setPattern();
        }
        if (isFill)
        {
	        if (brush.brushPattern != PdfBrush.PATTERN_SOLID) 
	        {
	            sb.append(setFillPattern());
	            sb.append(PdfWriter.formatFloat( cc1)
	                + PDF_SP + PdfWriter.formatFloat( (pageHeight - aa2)) + " m ");
	            
	            sb.append(drawInternalBezier(d1, a2, e1, b2, e1, c2));
	            sb.append(drawInternalBezier(e1, d2, d1, e2, c1, e2));
	            sb.append(drawInternalBezier(b1, e2, a1, d2, a1, c2));
	            sb.append(drawInternalBezier(a1, b2, b1, a2, c1, a2));
	            sb.append("f ");
	            isFill = false;
	        }
        }
        
        sb.append(PdfWriter.formatFloat( cc1)
            + PDF_SP + PdfWriter.formatFloat( (pageHeight - aa2)) + " m ");
        
        sb.append(drawInternalBezier(d1, a2, e1, b2, e1, c2));
        sb.append(drawInternalBezier(e1, d2, d1, e2, c1, e2));
        sb.append(drawInternalBezier(b1, e2, a1, d2, a1, c2));
        sb.append(drawInternalBezier(a1, b2, b1, a2, c1, a2));

        if ((isFill == true) && (isStroke == false))
        {
            sb.append(PDF_SP + "f ");
        }
        else if ((isFill == false) && (isStroke == true))
        {
            sb.append("S ");
        }
        else if ((isFill == true) && (isStroke == true))
        {
            sb.append(PDF_SP + "B ");
        }
        else
        {
            sb.append(PDF_SP + "n ");
        }
        
        if(contentStream == null)
        {
            contentStream = new PdfByteOutputStream();
        }
        contentStream.write((sb.toString()).getBytes());
    }

    /**
     * Draws an ellipse whose bounding box has its top-left corner 
     * at <code>point1</code> and its bottom-right corner at
     * <code>point2</code>.
     * 
     * @param p1
     *            top-left corner of the ellipse's bounding box
     * @param p2
     *            bottom-right corner of the ellipse's bounding box
     * @param isFill
     *            whether the ellipse needs to be filled
     * @param isStroke
     *            whether the ellipse needs to be stroked
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawEllipse_PdfPoint_PdfPoint_boolean_boolean">example</a>.
     */
    public synchronized void drawEllipse(PdfPoint p1, PdfPoint p2,
        boolean isFill, boolean isStroke) throws IOException
    {
        drawEllipse(p1.x, p1.y, p2.x, p2.y, isFill, isStroke);
    }
        
    /**
     * Draws a circle with its center at position 
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
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawCircle_double_double_double_boolean_boolean">example</a>.
     */
    public void drawCircle(double x, double y, double radius,
        boolean isFill, boolean isStroke) throws IOException
    {
        drawEllipse(x - radius, y - radius, x + radius, y + radius,
            isFill, isStroke);
    }
    
    /**
     * Draws a circle with its center at the specified point.
     * 
     * @param center
     *            position of the center of the circle
     * @param radius
     *            radius of the circle
     * @param isFill
     *            whether the circle needs to be filled
     * @param isStroke
     *           whether the circle needs to be stroked
     * @throws IOException
     *           if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawCircle_PdfPoint_double_boolean_boolean">example</a>.
     */
    public void drawCircle(PdfPoint center, double radius,
        boolean isFill, boolean isStroke) throws IOException
    {
        drawEllipse(center.x - radius, center.y - radius, center.x
            + radius, center.y + radius, isFill, isStroke);
    }

    /**
     * Draws a polyline.
     * 
     * @param xPoints
     *            array containing x-coordinates of the kinks in the
     *            polyline
     * @param yPoints
     *            array containing y-coordinates of the kinks in the
     *            polyline
     * @param nPoints
     *            number of kinks in the polyline
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawPolyline">example</a>.
     */
    public synchronized void drawPolyline(double xPoints[],
        double yPoints[], int nPoints) throws IOException,
        PdfException
    {
        if ((nPoints > xPoints.length) || (nPoints > yPoints.length))
        {
            throw new PdfException("Invalid number of points");
        }
        PdfPoint p;
        for (int i = 0; i < nPoints; i++)
        {
            p = updatePageSettings(new PdfPoint(xPoints[i],
                yPoints[i]));
            xPoints[i] = PdfMeasurement.convertToPdfUnit(
                measurementUnit, p.x);
            yPoints[i] = PdfMeasurement.convertToPdfUnit(
                measurementUnit, p.y);
        }
        StringBuffer sb = new StringBuffer();

        sb.append(setPenBrush(false, true));
        sb.append(PdfWriter.formatFloat( xPoints[0]) + PDF_SP + PdfWriter
            .formatFloat((this.pageHeight - yPoints[0])));
        sb.append(" m ");
        for (int i = 1; i < nPoints; i++)
        {
            sb.append(PdfWriter.formatFloat(xPoints[i]) + PDF_SP
                    + PdfWriter.formatFloat(
                        (this.pageHeight - yPoints[i])) + " l ");
        }
        sb.append(" S ");
        currentX = xPoints[nPoints - 1];
        currentY = yPoints[nPoints - 1];

        if(contentStream == null)
        {
            contentStream = new PdfByteOutputStream();
        }
        contentStream.write((sb.toString()).getBytes());
    }

    /**
     * Draws a polygon.
     * 
     * @param xPoints
     *            array containing x-coordinates of the corners of 
     *            the polygon
     * @param yPoints
     *            array containing y-coordinates of the corners of 
     *            the polygon
     * @param nPoints
     *            number of sides of the polygon
     * @param isFill
     *            whether the polygon needs to be filled
     * @param isStroke
     *            whether the polygon needs to be stroked
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawPolygon">example</a>.
     */
    public synchronized void drawPolygon(double xPoints[], double yPoints[],
        int nPoints, boolean isFill, boolean isStroke)
        throws IOException, PdfException
    {
        if ((nPoints > xPoints.length) || (nPoints > yPoints.length))
        {
            throw new PdfException("Invalid no of points");
        }
        PdfPoint p;
        for (int i = 0; i < nPoints; i++)
        {
            p = updatePageSettings(new PdfPoint(xPoints[i],
                yPoints[i]));
            xPoints[i] = PdfMeasurement.convertToPdfUnit(
                measurementUnit, p.x);
            yPoints[i] = PdfMeasurement.convertToPdfUnit(
                measurementUnit, p.y);
        }
        StringBuffer sb = new StringBuffer();

        sb.append(setPenBrush(isFill, isStroke));
        if (brush == null)
        {
            brush = new PdfBrush();
        }
        if ((prevBrush != brush) && (isFill == true))
        {
            setPattern();
        }
        if ((brush.brushPattern != 0) && (isFill == true))
        {
            sb.append(setFillPattern());
            sb.append(PdfWriter.formatFloat( xPoints[0]) + PDF_SP + PdfWriter
                .formatFloat((this.pageHeight - yPoints[0])));
            sb.append(" m ");

            for (int i = 1; i < nPoints; i++)
            {
               sb.append(PDF_SP
                    + PdfWriter.formatFloat(xPoints[i])
                    + PDF_SP
                    + PdfWriter
                        .formatFloat((this.pageHeight - yPoints[i]))
                    + " l ");
            }
            sb.append("h f ");
            isFill = false;
        }

        sb.append(PdfWriter.formatFloat( xPoints[0]) + PDF_SP + PdfWriter
            .formatFloat((this.pageHeight - yPoints[0])));
        sb.append(" m ");

        for (int i = 1; i < nPoints; i++)
        {
           sb.append(PDF_SP
                + PdfWriter.formatFloat( xPoints[i])
                + PDF_SP
                + PdfWriter
                    .formatFloat((this.pageHeight - yPoints[i]))
                + " l ");
        }
        currentX = xPoints[nPoints - 1];
        currentY = yPoints[nPoints - 1];

        if ((isFill == true) && (isStroke == false))
        {
            sb.append("h f ");
        }
        else if ((isFill == false) && (isStroke == true))
        {
            sb.append("h S ");
        }
        else if ((isFill == true) && (isStroke == true))
        {
            sb.append("h B ");
        }

        if(contentStream == null)
        {
            contentStream = new PdfByteOutputStream();
        }
        contentStream.write((sb.toString()).getBytes());
    }

/*    public void drawChord(double x, double y, double radius,
        double x1, double y1, double x2, double y2)
        throws IOException
    {
        drawCircle(x, y, radius, false, true);

        double m = (y2 - y1) / (x2 - x1);

        double c = (x2 * y1 - y2 * x1) / (x2 - x1);

        double l = (m * y + x - m * c) / (m * m + 1);

        double a = (y - m * x - c) / Math.sqrt(1 + m * m);

        double p = Math.sqrt(radius * radius - (a * a))
            / Math.sqrt(1 + m * m);

        double lx1 = l + p;
        double lx2 = l - p;

        l = (m * m * y + m * x + c) / (m * m + 1);

        double ly1 = l + m * p;
        double ly2 = l - m * p;

        drawLine(lx1, ly1, lx2, ly2);
    }
*/
    
    private StringBuffer encodeArc(double xCenter, double yCenter,
        double radiusX, double radiusY, double startAngle,
        double endAngle)
    {
        double alphaRad = Math.toRadians(360 - startAngle);
        double betaRad = Math.toRadians(360 - endAngle);
        double cpFactor = (4.0 / 3 * (1 - Math
            .cos((betaRad - alphaRad) / 2)) / Math
            .sin((betaRad - alphaRad) / 2));

        double sinAlpha = Math.sin(alphaRad);
        double sinBeta = Math.sin(betaRad);
        double cosAlpha = Math.cos(alphaRad);
        double cosBeta = Math.cos(betaRad);

        return drawInternalBezier(xCenter - radiusX
            * (cosAlpha - cpFactor * sinAlpha), yCenter - radiusY
            * (sinAlpha + cpFactor * cosAlpha), xCenter - radiusX
            * (cosBeta + cpFactor * sinBeta), yCenter - radiusY
            * (sinBeta - cpFactor * cosBeta), xCenter - radiusX
            * cosBeta, yCenter - radiusY * sinBeta);

    }

    protected StringBuffer drawInternalArc(double x, double y,
        double width, double height, double startAngle,
        double arcAngle)
    {
        double endAngle = startAngle + arcAngle;
        double xCenter = x + width / 2.0;
        double yCenter = y + height / 2.0;
        double radiusX = width / 2.0;
        double radiusY = height / 2.0;
        StringBuffer sb = new StringBuffer();
        startAngle += 180;
        endAngle += 180;

        while (endAngle < startAngle)
        {
            endAngle += 360;
        }

        while (endAngle - startAngle > 90)
        {
            double tempAngle = startAngle + 90;
            sb.append(encodeArc(xCenter, yCenter, radiusX, radiusY,
                startAngle, tempAngle));

            startAngle = tempAngle;
        }
        if (startAngle != endAngle)
        {
            sb.append(encodeArc(xCenter, yCenter, radiusX, radiusY,
                startAngle, endAngle));
        }
        return sb;
    }

    /**
     * Draws an arc. Rectangle <code>rect</code> specifies bounding
     * box of an imaginary circle, which completes the arc. The arc 
     * begins at <code>startAngle</code> degrees and spans for
     * <code>sweepAngle</code> degrees. <code>startAngle</code> is
     * measured in anti-clockwise direction.
     * 
     * @param rect
     *            bounding box of the imaginary circle that completes
     *            the arc
     * @param startAngle
     *            (measured in anti-clockwise direction and expressed
     *            in degrees) angle from which the arc needs to begin
     * @param sweepAngle
     *            (expressed in degrees) angle for which the arc needs
     *            to span
     * @throws IOException
     *             if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawArc">example</a>.
     */
    public synchronized void drawArc(PdfRect rect,
        double startAngle, double sweepAngle) throws IOException
    {
        startAngle = -startAngle;
        double alpha = startAngle;
        double beta = sweepAngle;
        double AlphaRad, /*BetaRad,*/ cosAlpha, sinAlpha;

        /*if(trueapplyPageSettings)
        {*/
        rect = updatePageSettings(rect);
        if (rect == null)
        {
            return;
        }
        if ((PdfMeasurement.convertToPdfUnit(measurementUnit, rect.x)
            + pageLeftMargin > (pageWidth - pageRightMargin))
            || (PdfMeasurement.convertToPdfUnit(measurementUnit,
                rect.y)
                + pageTopMargin + pageHeaderHeight) > (pageHeight - pageBottomMargin))
        {
            return;
        }
        rect.x += PdfMeasurement.convertToPdfUnit(measurementUnit,
            pageLeftMargin);
        rect.y += PdfMeasurement.convertToPdfUnit(measurementUnit,
            pageTopMargin + pageHeaderHeight);
            
        //clipping for the margin
        //clipPageSettings();
        /*}*/

        rect.x = PdfMeasurement.convertToPdfUnit(measurementUnit,
            rect.x);
        rect.y = PdfMeasurement.convertToPdfUnit(measurementUnit,
            rect.y);
        rect.y = pageHeight  - rect.y; 
        rect.width = PdfMeasurement.convertToPdfUnit(measurementUnit,
            rect.width);
        rect.height = PdfMeasurement.convertToPdfUnit(
            measurementUnit, rect.height);

        double x = rect.x + (rect.width / 2);
        double y = rect.y - (rect.height / 2);
        double rX = rect.width / 2;
        double rY = rect.height / 2;

        alpha += 180;
        beta += 180;

        // Convert from degrees to radians.
        AlphaRad = Math.PI * (alpha) / 180.0f;
        /*BetaRad  = Math.PI * (beta) / 180.0f;*/

        sinAlpha = Math.sin(AlphaRad);
        cosAlpha = Math.cos(AlphaRad);

        //setPenBrush(false, true);

        StringBuffer sb = new StringBuffer();
        sb.append(setPenBrush(false, true));
        sb.append(" " + PdfWriter.formatFloat(x - (rX * cosAlpha))
            + " " + PdfWriter.formatFloat(y - (rY * sinAlpha))
            + " m ");
        double tempAngle = startAngle;
        while (sweepAngle > 90)
        {
            sb.append(drawShortArc(rect.x, rect.y, rect.width,
                rect.height, tempAngle, tempAngle - 90));
            sweepAngle -= 90;
            tempAngle = tempAngle - 90;
        }
        if (sweepAngle != 0)
        {
            sb.append(drawShortArc(rect.x, rect.y, rect.width,
                rect.height, tempAngle, tempAngle - sweepAngle));
        }
        sb.append(" S ");
        if (contentStream == null)
        {
            contentStream = new PdfByteOutputStream();
        }

        contentStream.write(sb.toString().getBytes());
    }
    
    protected String drawShortArc(double xPos, double yPos,
        double rectWidth, double rectHeight, double Alpha, double Beta)
    {
        double cpFactor, AlphaRad, BetaRad, cosAlpha, cosBeta, sinAlpha, sinBeta;
        double X = xPos + rectWidth / 2;
        double Y = yPos - rectHeight / 2;
        double RX = rectWidth / 2;
        double RY = rectHeight / 2;

        Alpha += 180;
        Beta += 180;

        // Convert from degrees to radians.
        AlphaRad = Math.PI * (Alpha) / 180.0f;
        BetaRad  = Math.PI * (Beta) / 180.0f;

        // This factor is used to calculate control points.
        cpFactor = (4.0/3 * (1 - Math.cos((BetaRad - AlphaRad)/2)) /
            Math.sin((BetaRad - AlphaRad) / 2));
        
        sinAlpha = Math.sin(AlphaRad);
        sinBeta = Math.sin(BetaRad);
        cosAlpha = Math.cos(AlphaRad);
        cosBeta = Math.cos(BetaRad);

        return PdfWriter.formatFloat(X - RX * (cosAlpha - cpFactor * sinAlpha)) + " " +
        PdfWriter.formatFloat(Y - RY * (sinAlpha + cpFactor * cosAlpha)) + " " +
        PdfWriter.formatFloat(X - RX * (cosBeta + cpFactor * sinBeta)) + " " +
        PdfWriter.formatFloat(Y - RY * (sinBeta - cpFactor * cosBeta)) + " " +
        PdfWriter.formatFloat(X - RX * cosBeta) + " " +
        PdfWriter.formatFloat(Y - RY * sinBeta) + " c ";
    }
    
    /*private void clipPageSettings() throws IOException
    {       
        if (true)
        {
            double tempX = pageLeftMargin;
            double tempY = pageHeight - pageTopMargin
                - pageHeaderHeight;
            double tempWidth = pageWidth - pageLeftMargin
                - pageRightMargin;
            double tempHeight = pageHeight - pageTopMargin
                - pageBottomMargin - pageFooterHeight
                - pageHeaderHeight;
            String sTemp = PDF_SP + PDF_STORE_GS + PDF_SP
                + PdfWriter.formatFloat(tempX) + PDF_SP
                + PdfWriter.formatFloat(tempY) + PDF_SP
                + PdfWriter.formatFloat(tempWidth) + PDF_SP
                + PdfWriter.formatFloat( -tempHeight) + " re"
                + " W* n ";
            if (contentStream == null)
            {
                contentStream = new PdfByteOutputStream();
            }
            contentStream.write(sTemp.getBytes());
        }
    }*/
    
    /**
     * Draws a pie segment. The position 
     * (<code>x</code>, <code>y</code>) represents the top-left
     * corner of the bounding box of an imaginary ellipse, which 
     * the pie segment can neatly fit into.
     * 
     * @param x
     *            x-coordinate of top-left corner of the bounding box
     *            of the imaginary ellipse that contains the pie 
     *            segment
     * @param y
     *            x-coordinate of top-left corner of the bounding box
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
     * @param sweepAngle
     *            (expressed in degrees) angle for which the pie
     *            segment needs to span
     * @param isFill
     *            whether the pie segment needs to be filled
     * @param isStroke
     *            whether the pie segment needs to be stroked
     * @throws IOException
     *            if an I/O error occurs.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawPie">example</a>.
     */
     
    public void drawPie(double x, double y, double width, double height, double startAngle, 
        double sweepAngle, boolean isFill, boolean isStroke) throws IOException
    {
        PdfRect rect = updatePageSettings(new PdfRect(x, y, width,
            height));
        if(rect == null)
        {
            return;
        }
        x = rect.x;
        y = rect.y;
        width = rect.width;
        height = rect.height;

        StringBuffer sb = new StringBuffer();

        /*double endAngle = startAngle + sweepAngle; */

        double xCenter = x + width / 2.0;
        double yCenter = y + height / 2.0;
        double radiusX = width / 2.0;
        double radiusY = height / 2.0;

        sb.append(setPenBrush(isFill, isStroke));

        double startAngle1 = startAngle + 180;
        /*double endAngle1 = endAngle + 180;*/ 

        startAngle1 = -startAngle1;
        startAngle = -startAngle;
        double startX = xCenter - radiusX
            * Math.cos((360 - startAngle1) * (Math.PI / 180));
        double startY = yCenter - radiusY
            * Math.sin((360 - startAngle1) * (Math.PI / 180));

        startX = PdfMeasurement.convertToPdfUnit(measurementUnit,
            startX);
        startY = PdfMeasurement.convertToPdfUnit(measurementUnit,
            PdfMeasurement.convertToMeasurementUnit(measurementUnit,
                this.pageHeight) - startY);

        sb.append(PDF_SP + PdfWriter.formatFloat(startX) + PDF_SP
            + PdfWriter.formatFloat(startY));
        sb.append(PDF_SP + "m" + PDF_SP);

        double tempAngle = startAngle;
        while (sweepAngle > 90)
        {
            sb.append(drawShortArc(PdfMeasurement.convertToPdfUnit(
                measurementUnit, x), PdfMeasurement.convertToPdfUnit(
                measurementUnit, (PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, this.pageHeight) - y)), PdfMeasurement
                .convertToPdfUnit(measurementUnit, width),
                PdfMeasurement.convertToPdfUnit(measurementUnit,
                    height), tempAngle, tempAngle - 90));
            sweepAngle -= 90;
            tempAngle = tempAngle - 90;
        }
        if (sweepAngle != 0)
        {
            sb.append(drawShortArc(PdfMeasurement.convertToPdfUnit(
                measurementUnit, x), PdfMeasurement.convertToPdfUnit(
                measurementUnit, (PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, this.pageHeight) - y)), PdfMeasurement
                .convertToPdfUnit(measurementUnit, width),
                PdfMeasurement.convertToPdfUnit(measurementUnit,
                    height), tempAngle, tempAngle - sweepAngle));
        }

        double w = PdfMeasurement.convertToPdfUnit(measurementUnit,
            (x + width / 2));
        double h = PdfMeasurement.convertToPdfUnit(measurementUnit,
            PdfMeasurement.convertToMeasurementUnit(measurementUnit,
                this.pageHeight) - ((height / 2) + y));
        sb.append(PDF_SP + PdfWriter.formatFloat(w));
        sb.append(PDF_SP + PdfWriter.formatFloat(h));
        sb.append(PDF_SP + "l");

        if ((isFill == true) && (isStroke == false))
        {
            sb.append(PDF_SP + "h f");
        }
        else if ((isFill == false) && (isStroke == true))
        {
            sb.append(PDF_SP + "h S");
        }
        else if ((isFill == true) && (isStroke == true))
        {
            sb.append(PDF_SP + "h B");
        }
        sb.append(PDF_SP);
        if (contentStream == null)
        {
            contentStream = new PdfByteOutputStream();
        }

        contentStream.write(sb.toString().getBytes());
    }
    
    /**
     * Draws rectangle <code>rect</code> with specified rounded
     * corners.
     * 
     * @param rect
     *            rectangle that needs to be drawn
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
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawRoundRect_PdfRect_double_double_boolean_boolean">example</a>.
     */
    public void drawRoundRect(PdfRect rect, double arcWidth,
        double arcHeight, boolean isFill, boolean isStroke)
        throws IOException
    {
        drawRoundRect(rect.x, rect.y, rect.width, rect.height,
            arcWidth, arcHeight, isFill, isStroke);
    }
    
    /**
     * Draws a rectangle with rounded corners. The corners of the
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
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawRoundRect_double_double_double_double_double_double_boolean_boolean">example</a>.
     */
    public synchronized void drawRoundRect(double x, double y, double width,
        double height, double arcWidth, double arcHeight,
        boolean isFill, boolean isStroke) throws IOException
    {
        PdfRect r = updatePageSettings(new PdfRect(x, y, width,
            height));
        if (r == null)
        {
            return;
        }
        double startX = PdfMeasurement.convertToPdfUnit(
            measurementUnit, r.x);
        double startY = PdfMeasurement.convertToPdfUnit(
            measurementUnit, r.y + arcHeight);
        
        StringBuffer sb = new StringBuffer();
        sb.append(setPenBrush(isFill, isStroke));

        if ((prevBrush != brush) && (isFill == true))
        {
            setPattern();
        }
        if (isFill)
        {
	        if (brush.brushPattern != PdfBrush.PATTERN_SOLID) 
	        {
	            sb.append(setFillPattern()); 
	        
	            sb.append(PdfWriter.formatFloat(startX)
	                + PDF_SP + PdfWriter.formatFloat(pageHeight - startY));
	            sb.append(" m ");
	
	            sb.append(drawInternalLine(x, y + height - arcHeight));
	            sb.append(drawInternalArc(x, y + height - arcHeight * 2, arcWidth * 2,
	                arcHeight * 2, 180, 90));
	            sb.append(drawInternalLine(x + width - arcWidth, y + height));
	            sb.append(drawInternalArc(x + width - arcWidth * 2, y + height
	                - arcHeight * 2, arcWidth * 2, arcHeight * 2, 270, 90));
	            sb.append(drawInternalLine(x + width, y + arcHeight));
	            sb.append(drawInternalArc(x + width - arcWidth * 2, y, arcWidth * 2,
	                arcHeight * 2, 0, 90));
	            sb.append(drawInternalLine(x + arcWidth, y));
	            sb.append(drawInternalArc(x, y, arcWidth * 2, arcHeight * 2, 90, 90));
	
	            sb.append(" f ");
	            isFill = false;
	        }
        }   
        
        sb.append(PdfWriter.formatFloat( startX)
            + PDF_SP + PdfWriter.formatFloat( (pageHeight - startY)));

        sb.append(" m ");

        sb.append(drawInternalLine(x, y + height - arcHeight));
        sb.append(drawInternalArc(x, y + height - arcHeight * 2, arcWidth * 2,
            arcHeight * 2, 180, 90));
        sb.append(drawInternalLine(x + width - arcWidth, y + height));
        sb.append(drawInternalArc(x + width - arcWidth * 2, y + height
            - arcHeight * 2, arcWidth * 2, arcHeight * 2, 270, 90));
        sb.append(drawInternalLine(x + width, y + arcHeight));
        sb.append(drawInternalArc(x + width - arcWidth * 2, y, arcWidth * 2,
            arcHeight * 2, 0, 90));
        sb.append(drawInternalLine(x + arcWidth, y));
        sb.append(drawInternalArc(x, y, arcWidth * 2, arcHeight * 2, 90, 90));

        if ((isFill == true) && (isStroke == false))
        {
            sb.append(" h f ");
        }
        else if ((isFill == false) && (isStroke == true))
        {
            sb.append(" h S ");
        }
        else if ((isFill == true) && (isStroke == true))
        {
            sb.append(" h B ");
        }
        else
        {
            sb.append("n ");
        }
       
        if(contentStream == null)
        {
            contentStream = new PdfByteOutputStream();
        }
        contentStream.write((sb.toString()).getBytes());
    }

    /**
     * Retrieves <code>PdfTextFormatter</code> object contained by
     * this <code>PdfPage</code>.
     * 
     * @return <code>PdfTextFormatter</code> object contained by the
     *         <code>PdfPage</code>
     * @since 1.0
     * @see #setTextFormatter(PdfTextFormatter)
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#getTextFormatter">example</a>.
     */
    public synchronized PdfTextFormatter getTextFormatter()
    {
        if (tf == null)
        {
            tf = new PdfTextFormatter();
        }
        return tf;
    }
    
    /**
     * Specifies new text formatting settings.
     * 
     * @param tf new text formatting settings
     * @since 1.0
     * @see #getTextFormatter()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#setTextFormatter">example</a>.
     */
    public synchronized void setTextFormatter(PdfTextFormatter tf)
    {
        this.tf = tf;
    }

    
    /**
     * Draws image, specified by its pathname, at position 
     * (<code>x</code>, <code>y</code>).
     * 
     * @param path
     *            pathname of the image file
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
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawImage_String_double_double">example</a>.
     */    
    public void drawImage(String path, double x, double y)
        throws IOException, PdfException
    {
        if (path == null || path.equals(""))
        {
            throw new PdfException(
                "Illegal argument to drawImage (path == null " +
                "|| path.equals(\"\")).");
        }
        PdfImage img = PdfImage.create(path);
        PdfRect r = updatePageSettings(new PdfRect(x, y,
            PdfMeasurement.convertToMeasurementUnit(measurementUnit,
                img.width()), PdfMeasurement
                .convertToMeasurementUnit(measurementUnit, img
                    .height())));
        if (r == null)
        {
            return;
        }
        drawImage(img, r, null, this.measurementUnit);
    }
	
    /**
     * Draws image, specified by its pathname, at position (<code>x</code>,
     * <code>y</code>) with specified width and height.
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
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawImage_String_double_double_double_double">example</a>.
     */
    public void drawImage(String path, double x, double y,
        double width, double height) throws IOException, PdfException
    {
        if (path == null || path.equals(""))
        {
            throw new PdfException(
                "Illegal argument to drawImage (path == null " +
                "|| path.equals(\"\")).");
        }
        PdfImage img = PdfImage.create(path); 
        PdfRect r = updatePageSettings(new PdfRect(x, y, width,
            height));
        if (r == null)
        {
            return;
        }
        drawImage(img, r, null, this.measurementUnit);
    }

    /**
     * Draws image, specified by its pathname, at point <code>p</code>.
     * 
     * @param path
     *            pathname of the image file
     * @param p
     *            point where the image needs to be drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawImage_String_PdfPoint">example</a>.
     */
    public void drawImage(String path, PdfPoint p)
        throws IOException, PdfException
    {
        if (path == null || path.equals(""))
        {
            throw new PdfException(
                "Illegal argument to drawImage (path == null "
                    + "|| path.equals(\"\")).");
        }
        PdfImage img = PdfImage.create(path);
        PdfRect r = updatePageSettings(new PdfRect(p.x, p.y,
            PdfMeasurement.convertToMeasurementUnit(measurementUnit,
                img.width()), PdfMeasurement
                .convertToMeasurementUnit(measurementUnit, img
                    .height())));
        if (r == null)
        {
            return;
        }
        drawImage(img, r, null, this.measurementUnit);
    }

    /**
     * Draws image, specified by its pathname, at point 
     * <code>p</code> with specified width and height.
     * 
     * @param path
     *            pathname of the image file
     * @param p
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
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawImage_String_PdfPoint_double_double">example</a>.
     */
    public void drawImage(String path, PdfPoint p, double width,
        double height) throws IOException, PdfException
    {
        if (path == null || path.equals(""))
        {
            throw new PdfException(
                "Illegal argument to drawImage (path == null "
                    + "|| path.equals(\"\")).");
        }
        PdfImage img = PdfImage.create(path);
        PdfRect r = updatePageSettings(new PdfRect(p.x, p.y, width,
            height));
        if (r == null)
        {
            return;
        }
        drawImage(img, r, null, this.measurementUnit);
    }
    
    /**
     * Draws image, specified by its pathname, on rectangle
     * <code>rect</code>.
     * 
     * @param path
     *            pathname of the image file
     * @param rect
     *            rectangle on which the image needs to be drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawImage_String_PdfRect">example</a>.
     */
    public void drawImage(String path, PdfRect rect)
        throws IOException, PdfException
    {
        if (path == null || path.equals(""))
        {
            throw new PdfException(
                "Illegal argument to drawImage (path == null " +
                "|| path.equals(\"\")).");
        }
        if (rect == null)
        {
            throw new PdfException(
                "Illegal argument to drawImage (PdfRect == null).");
        }

        PdfImage img = PdfImage.create(path);
        rect = updatePageSettings(rect);
        if (rect == null)
        {
            return;
        }
        drawImage(img, rect, null, this.measurementUnit);
    }
    
    /**
     * Draws specified image at position (<code>x</code>,
     * <code>y</code>).
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
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawImage_PdfImage_double_double">example</a>.
     */
    public void drawImage(PdfImage img, double x, double y)
        throws IOException, PdfException
    {
        if (img == null)
        {
            throw new PdfException(
            	"Illegal argument to drawImage (PdfImage == null).");
        }
        double width = img.scaledWidth >= 0 ? img.getWidth()
            : PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, img.width());

        double height = img.scaledHeight >= 0 ? img.getHeight()
            : PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, img.height());
        
        PdfRect r = updatePageSettings(new PdfRect(x, y, width,
            height));
        if (r == null)
        {
            return;
        }
        img.store();
        drawImage(img, r, null, this.measurementUnit);
        img.reStore();
    }
    
    /**
     * Draws specified image at position (<code>x</code>,
     * <code>y</code>) with specified width and height.
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
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawImage_PdfImage_double_double_double_double">example</a>.
     */
    public void drawImage(PdfImage img, double x, double y,
        double width, double height) throws IOException, PdfException
    {
        if (img == null)
        {
            throw new PdfException(
                "Illegal argument to drawImage (PdfImage == null).");
        }
        PdfRect r = updatePageSettings(new PdfRect(x, y, width,
            height));
        if (r == null)
        {
            return;
        }
        img.store();
        drawImage(img, r, null, this.measurementUnit);
        img.reStore();
    }
    
    /**
     * Draws specified image at specified point.
     * 
     * @param img
     *            image that needs to be drawn
     * @param p
     *            point where the image needs to be drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawImage_PdfImage_PdfPoint">example</a>.
     */
    public void drawImage(PdfImage img, PdfPoint p)
        throws IOException, PdfException
    {
        if (img == null)
        {
            throw new PdfException(
                "Illegal argument to drawImage (PdfImage == null).");
        }
        double width = img.scaledWidth >= 0 ? img.getWidth()
            : PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, img.width());

        double height = img.scaledHeight >= 0 ? img.getHeight()
            : PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, img.height());
        
        PdfRect r = updatePageSettings(new PdfRect(p.x, p.y, width,
            height));
        if (r == null)
        {
            return;
        }
        img.store();
        drawImage(img, r, null, this.measurementUnit);
        img.reStore();
    }

    /**
     * Draws specified image at point <code>p</code> with specified
     * width and height.
     * 
     * @param img
     *            image that needs to be drawn
     * @param p
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
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawImage_PdfImage_PdfPoint_double_double">example</a>.
     */
    public void drawImage(PdfImage img, PdfPoint p, double width,
        double height) throws IOException, PdfException
    {
        if (img == null)
        {
            throw new PdfException(
                "Illegal argument to drawImage (PdfImage == null).");
        }
        PdfRect r = updatePageSettings(new PdfRect(p.x, p.y, width,
            height));
        if (r == null)
        {
            return;
        }
        img.store();
        drawImage(img, r, null, this.measurementUnit);
        img.reStore();
    }

    /**
     * Draws specified image on specified rectangle.
     * 
     * @param img
     *            image that needs to be drawn
     * @param rect
     *            rectangle on which the image needs to be drawn
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawImage_PdfImage_PdfRect">example</a>.
     */
    public void drawImage(PdfImage img, PdfRect rect)
        throws IOException, PdfException
    {
        /* precaution: rect and img shld be created with same mu */
        if (img == null)
        {
            throw new PdfException(
                "Illegal argument to drawImage (PdfImage == null).");
        }
        if (rect == null)
        {
            throw new PdfException(
            	"Illegal argument to drawImage (PdfRect == null).");
        }
        
        rect = updatePageSettings(rect);
        if (rect == null)
        {
            return;
        }
        img.store();
        drawImage(img, rect, null, this.measurementUnit);
        img.reStore();
    }

    /**
     * Draw image, specified by its pathname and rotated by
     * <code>rotation</code> degrees, at position (<code>x</code>,
     * <code>y</code>).
     * <p>
     * The image is rotated on center of its bounding box by 
     * <code>rotation</code> degrees in anti-clockwise direction.
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
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawImage_String_double_double_double">example</a>.
     */    
    public void drawImage(String path, double x, double y,
        double rotation) throws IOException, PdfException
    {
        if (path == null || path.equals(""))
        {
            throw new PdfException(
                "Illegal argument to drawImage (path == null "
                    + "|| path.equals(\"\")).");
        }
        PdfImage img = PdfImage.create(path);
        img.setRotation(rotation);
        
        PdfRect r = updatePageSettings(new PdfRect(x, y,
            PdfMeasurement.convertToMeasurementUnit(measurementUnit,
                img.width()), PdfMeasurement
                .convertToMeasurementUnit(measurementUnit, img
                    .height())));
        if (r == null)
        {
            return;
        }
        drawImage(img, r, null, this.measurementUnit);
    }

    /**
     * Draws image, specified by its pathname and rotated by
     * <code>rotation</code> degrees, at position (<code>x</code>,
     * <code>y</code>) with specified width and height.
     * <p>
     * The image is rotated on center of its bounding box by 
     * <code>rotation</code> degrees in anti-clockwise direction.
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
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawImage_String_double_double_double_double_double">example</a>.
     */
    public void drawImage(String path, double x, double y,
        double width, double height, double rotation)
        throws IOException, PdfException
    {
        if (path == null || path.equals(""))
        {
            throw new PdfException(
                "Illegal argument to drawImage (path == null "
                    + "|| path.equals(\"\")).");
        }
        PdfImage img = PdfImage.create(path);
        img.setRotation(rotation);
        PdfRect r = updatePageSettings(new PdfRect(x, y, width,
            height));
        if (r == null)
        {
            return;
        }
        drawImage(img, r, null, this.measurementUnit);
    }

    /**
     * Draws image, specified by its pathname and rotated by
     * <code>rotation</code> degrees, at point <code>p</code>.
     * <p>
     * The image is rotated on center of its bounding box by 
     * <code>rotation</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param path
     *            pathname of the image file
     * @param p
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
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawImage_String_PdfPoint_double">example</a>.
     */
    public void drawImage(String path, PdfPoint p, double rotation)
        throws IOException, PdfException
    {
        if (path == null || path.equals(""))
        {
            throw new PdfException(
                "Illegal argument to drawImage (path == null "
                    + "|| path.equals(\"\")).");
        }
        PdfImage img = PdfImage.create(path);
        img.setRotation(rotation);
        PdfRect r = updatePageSettings(new PdfRect(p.x, p.y,
            PdfMeasurement.convertToMeasurementUnit(measurementUnit,
                img.width()), PdfMeasurement
                .convertToMeasurementUnit(measurementUnit, img
                    .height())));
        if (r == null)
        {
            return;
        }
        drawImage(img, r, null, this.measurementUnit);
    }

    /**
     * Draws image, specified by its pathname and rotated by
     * <code>rotation</code> degrees, at point <code>p</code> with
     * specified width and height.
     * <p>
     * The image is rotated on center of its bounding box by 
     * <code>rotation</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param path
     *            pathname of the image file
     * @param p
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
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawImage_String_PdfPoint_double_double_double">example</a>.
     */
    public void drawImage(String path, PdfPoint p, double width,
        double height, double rotation) throws IOException,
        PdfException
    {
        if (path == null || path.equals(""))
        {
            throw new PdfException(
                "Illegal argument to drawImage (path == null "
                    + "|| path.equals(\"\")).");
        }
        PdfImage img = PdfImage.create(path);
        img.setRotation(rotation);
        PdfRect r = updatePageSettings(new PdfRect(p.x, p.y, width,
            height));
        if (r == null)
        {
            return;
        }
        drawImage(img, r, null, this.measurementUnit);
    }

    /**
     * Draws image, specified by its pathname and rotated by
     * <code>rotation</code> degrees, on rectangle
     * <code>rect</code>.
     * <p>
     * The image is rotated on center of its bounding box by 
     * <code>rotation</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param path
     *            pathname of the image file
     * @param rect
     *            rectangle on which the image needs to be drawn
     * @param rotation
     *            (measured in anti-clockwise direction and expressed  
     *            in degrees) tilt of the image with reference to  
     *            center of its bounding box
     * @throws IOException
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawImage_String_PdfRect_double">example</a>.
     */
    public void drawImage(String path, PdfRect rect, double rotation)
        throws IOException, PdfException
    {
        if (path == null || path.equals(""))
        {
            throw new PdfException(
                "Illegal argument to drawImage (path == null "
                    + "|| path.equals(\"\")).");
        }
        if (rect == null)
        {
            throw new PdfException(
                "Illegal argument to drawImage (PdfRect == null).");
        }

        PdfImage img = PdfImage.create(path);
        img.setRotation(rotation);
        rect = updatePageSettings(rect);
        if (rect == null)
        {
            return;
        }
        drawImage(img, rect, null, this.measurementUnit);
    }

    /**
     * Draws specified image, rotated by <code>rotation</code>
     * degrees, at position (<code>x</code>, <code>y</code>).
     * <p>
     * The image is rotated on center of its bounding box by 
     * <code>rotation</code> degrees in anti-clockwise direction.
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
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawImage_PdfImage_double_double_double">example</a>.
     */
    public void drawImage(PdfImage img, double x, double y,
        double rotation) throws IOException, PdfException
    {
        if (img == null)
        {
            throw new PdfException(
                "Illegal argument to drawImage (PdfImage == null).");
        }
        img.store();
        img.setRotation(rotation);
        double width = img.scaledWidth >= 0 ? img.getWidth()
            : PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, img.width());

        double height = img.scaledHeight >= 0 ? img.getHeight()
            : PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, img.height());
        
        PdfRect r = updatePageSettings(new PdfRect(x, y, width,
            height));
        if (r == null)
        {
            return;
        }
        drawImage(img, r, null, this.measurementUnit);
        img.reStore();
    }

    /**
     * Draw specified image, rotated by <code>rotation</code>
     * degrees, at position (<code>x</code>, <code>y</code>)
     * with specified height and width.
     * <p>
     * The image is rotated on center of its bounding box by 
     * <code>rotation</code> degrees in anti-clockwise direction.
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
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawImage_PdfImage_double_double_double_double_double">example</a>.
     */
    public void drawImage(PdfImage img, double x, double y,
        double width, double height, double rotation)
        throws IOException, PdfException
    {
        if (img == null)
        {
            throw new PdfException(
                "Illegal argument to drawImage (PdfImage == null).");
        }
        img.store();
        img.setRotation(rotation);
        PdfRect r = updatePageSettings(new PdfRect(x, y, width,
            height));
        if (r == null)
        {
            return;
        }
        drawImage(img, r, null, this.measurementUnit);
        img.reStore();
    }

    /**
     * Draws specified image, rotated by <code>rotation</code>
     * degrees, at point <code>p</code>.
     * <p>
     * The image is rotated on center of its bounding box by 
     * <code>rotation</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param img
     *            image that needs to be drawn
     * @param p
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
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawImage_PdfImage_PdfPoint_double">example</a>.
     */
    public void drawImage(PdfImage img, PdfPoint p, double rotation)
        throws IOException, PdfException
    {
        if (img == null)
        {
            throw new PdfException(
                "Illegal argument to drawImage (PdfImage == null).");
        }
        img.store();
        img.setRotation(rotation);
        double width = img.scaledWidth >= 0 ? img.getWidth()
            : PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, img.width());

        double height = img.scaledHeight >= 0 ? img.getHeight()
            : PdfMeasurement.convertToMeasurementUnit(
                measurementUnit, img.height());
        
        PdfRect r = updatePageSettings(new PdfRect(p.x, p.y, width,
            height));
        if (r == null)
        {
            return;
        }
        drawImage(img, r, null, this.measurementUnit);
        img.reStore();
    }

    /**
     * Draws specified image, rotated by <code>rotation</code>
     * degrees, at point <code>p</code> with specified width and
     * height.
     * <p>
     * The image is rotated on center of its bounding box by 
     * <code>rotation</code> degrees in anti-clockwise direction.
     * </p>
     * 
     * @param img
     *            image that needs to be drawn
     * @param p
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
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawImage_PdfImage_PdfPoint_double_double_double">example</a>.
     */
    public void drawImage(PdfImage img, PdfPoint p, double width,
        double height, double rotation) throws IOException,
        PdfException
    {
        if (img == null)
        {
            throw new PdfException(
                "Illegal argument to drawImage (PdfImage == null).");
        }
        img.store();
        img.setRotation(rotation);
        PdfRect r = updatePageSettings(new PdfRect(p.x, p.y, width,
            height));
        if (r == null)
        {
            return;
        }
        drawImage(img, r, null, this.measurementUnit);
        img.reStore();
    }

    /**
     * Draws specified image, rotated by <code>rotation</code>
     * degrees, on specified rectangle.
     * <p>
     * The image is rotated on center of its bounding box by 
     * <code>rotation</code> degrees in anti-clockwise direction.
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
     *            if an I/O error occurs.
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#drawImage_PdfImage_PdfRect_double">example</a>.
     */
    public void drawImage(PdfImage img, PdfRect rect, double rotation)
        throws IOException, PdfException
    {
        /* precaution: rect and img shld be created with same mu */
        if (img == null)
        {
            throw new PdfException(
                "Illegal argument to drawImage (PdfImage == null).");
        }
        if (rect == null)
        {
            throw new PdfException(
                "Illegal argument to drawImage (PdfRect == null).");
        }
        img.store();
        img.setRotation(rotation);
        rect = updatePageSettings(rect);
        if (rect == null)
        {
            return;
        }
        drawImage(img, rect, null, this.measurementUnit);
        img.reStore();
    }
    
    private boolean isUniqueImageName(String imageName)
    {
        boolean isUnique = true;
        Object obj = rResources.get(XOBJ);
        if (obj != null)
        {
            Map rXObjMap = ((PdfDict) obj).getMap();
            if (rXObjMap.containsKey(new PdfName(imageName)))
            {
                return false;
            }
        }

        return isUnique;
    }

    protected synchronized void drawImage(PdfImage img, PdfRect rect,
        PdfRect clipRect, /* int compression, */int mu)
        throws IOException, PdfException
    {
        if (img == null)
        {
            throw new PdfException(
                "Illegal argument to drawImage (PdfImage == null).");
        }
        if (xObjDict == null)
        {
            xObjDict = new PdfDict(new HashMap());
        }
        if (xObjIndRefDict == null)
        {
            xObjIndRefDict = new PdfDict(new HashMap());
        }
        if (contentStream == null)
        {
            contentStream = new PdfByteOutputStream();
        }
        img.setWidth(rect.width);
        img.setHeight(rect.height);
        double x = PdfMeasurement.convertToPdfUnit(mu, rect.x);
        double y = PdfMeasurement.convertToPdfUnit(mu, rect.y);
        y = pageHeight - y;
        String imageName = null;
        
        img.streamDict.getMap().put(new PdfName(PDF_WIDTH),
            new PdfInteger((int) img.width));
        img.streamDict.getMap().put(new PdfName(PDF_HEIGHT),
            new PdfInteger((int) img.height));
        img.streamDict.getMap().put(new PdfName(PDF_BITS_PER_COMPONENT),
            new PdfInteger(img.bitsPerComp));
        img.streamDict.getMap().put(new PdfName(PDF_COLORSPACE),
            img.colorSpace);

        img.streamBuffer.position(0);
        PdfInteger hash = new PdfInteger(img.hashCode());
        PdfImage tImg = xObjDict.getValueAsImage(hash);
        if (tImg == null)
        {
            imageName = "RubiconImage" + Integer.toString(imageCount++);
            if (rResources != null)
            {
                int iteration = 0;
                while (!isUniqueImageName(imageName)) 
                {
                    imageName += '_' + iteration;
                    ++iteration;
                }
            }
            xObjDict.getMap().put(hash, img);
            xObjIndRefDict.getMap().put(hash,
                new PdfName(imageName));
        }
        else
        {
            imageName = ((PdfName) xObjIndRefDict.getValue(hash))
                .getString();
        }
        
        StringBuffer sb = new StringBuffer();
        sb.append(PDF_STORE_GS + PDF_SP);
        if (clipRect != null)
        {
            clipRect.x = PdfMeasurement.convertToPdfUnit(mu, clipRect.x);
            clipRect.y = PdfMeasurement.convertToPdfUnit(mu, clipRect.y);
            clipRect.y = pageHeight - clipRect.y;
            clipRect.height = PdfMeasurement.convertToPdfUnit(mu, clipRect.height);
            clipRect.width = PdfMeasurement.convertToPdfUnit(mu, clipRect.width);
            clipRect.y -= clipRect.height;
            
            sb.append(PdfWriter.formatFloat(clipRect.x) + PDF_SP
                + PdfWriter.formatFloat(clipRect.y) + PDF_SP
                + PdfWriter.formatFloat(clipRect.width) + PDF_SP
                + PdfWriter.formatFloat(clipRect.height) + PDF_SP
                + PDF_RE + PDF_SP + PDF_EOCLIP + PDF_SP
                + PDF_ENDPATH + PDF_SP);
        }
        
        double yOffset = img.scaledHeight <= 0 ? img.height
            : PdfMeasurement.convertToPdfUnit(mu, img.scaledHeight);
        
        //sb.append(" 0 600 100 100 re W* n ");

        //Calculate Translation
        AffineTransform m = new AffineTransform(1, 0, 0, 1, 0, 0);
        m.translate(x, y - yOffset);
        if (img.isRotated)
        {
            img.rotation %= 360;
            if (img.rotation < 0)
            //-ve input from user rotate clockwise with abs value
            {
                img.rotation += 360;
            }
            if (img.rotation != 0)
            {
                double xShift = PdfMeasurement.convertToPdfUnit(mu,
                    img.getWidth());
                double yShift = PdfMeasurement.convertToPdfUnit(mu,
                    img.getHeight());
                m.rotate(Math.toRadians(img.rotation), xShift / 2,
                    yShift / 2);
            }
        }
        //Calculate Scaling 
        float Sx = img.scaledWidth <= 0 ? img.width
            : (float) PdfMeasurement.convertToPdfUnit(mu,
                img.scaledWidth);
        float Sy = img.scaledHeight <= 0 ? img.height
            : (float) PdfMeasurement.convertToPdfUnit(mu,
                img.scaledHeight);
        m.scale(Sx, Sy);
        double[] flatMatrix = new double[6];
        m.getMatrix(flatMatrix);
        for (int i = 0; i < flatMatrix.length; ++i)
        {
            sb.append(flatMatrix[i] + " ");
        }
        sb.append(PDF_CM + PDF_SP);

        sb.append(PDF_NAMESTART + imageName + PDF_SP + PDF_SHOWIMG
            + PDF_SP);
        sb.append(PDF_RESTORE_GS + PDF_SP);
        contentStream.write(sb.toString().getBytes());
    }
    
    /**
     * Disables all margins on this <code>PdfPage</code>. 
     *  
     * @since 1.0
     * @see #enableAllMargins()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#disableAllMargins">example</a>.
     */
    public synchronized void disableAllMargins()
    {
        if (!marginsActive)
        {
            return;
        }
        
        pagePrevLeftMargin = pageLeftMargin;
        
        pagePrevTopMargin = pageTopMargin;
        
        pagePrevRightMargin = pageRightMargin;
        
        pagePrevBottomMargin = pageBottomMargin;
        
        pagePrevHeaderHeight = pageHeaderHeight;
        
        pagePrevFooterHeight = pageFooterHeight;  

        pageLeftMargin = 0;
        
        pageTopMargin = 0;
        
        pageRightMargin = 0;
        
        pageBottomMargin = 0;
        
        pageHeaderHeight = 0;
        
        pageFooterHeight = 0;
        
        marginsActive = false;
    }

    /**
     * Enables all margins on this <code>PdfPage</code>.
     * 
     * @since 1.0
     * @see #disableAllMargins()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#enableAllMargins">example</a>.
     */
    public synchronized void enableAllMargins()
    {
        if (marginsActive)
        {
            return;
        }
        
        pageLeftMargin = pagePrevLeftMargin;

        pageTopMargin = pagePrevTopMargin;

        pageRightMargin = pagePrevRightMargin;

        pageBottomMargin = pagePrevBottomMargin;

        pageHeaderHeight = pagePrevHeaderHeight;

        pageFooterHeight = pagePrevFooterHeight;
        
        marginsActive = true;
    }

    /**
     * Returns size of this page's bottom margin.
     *   
     * @return (expressed in points) size of the bottom margin
     * @since 1.0 
     */
    public double getBottomMargin()
    {
        return pageBottomMargin;
    }
    
    /**
     * Returns height of this page's footer.
     * 
     * @return (expressed in points) height of the footer
     * @since 1.0 
     */
    public double getFooterHeight()
    {
        return pageFooterHeight;
    }
    
    /**
     * Returns height of this page's footer. 
     * 
     * @return (expressed in points) height of the footer
     * @since 1.0
     */
    public double getHeaderHeight()
    {
        return pageHeaderHeight;
    }
    
    /**
     * Returns size of this page's left margin.
     * 
     * @return (expressed in points) size of the left margin
     * @since
     */
    public double getLeftMargin()
    {
        return pageLeftMargin;
    }
    
    /**
     * Returns size of this page's right margin.
     * 
     * @return (expressed in points) size of the right margin
     * @since 1.0 
     */
    public double getRightMargin()
    {
        return pageRightMargin;
    }
    
    /**
     * Returns the size of this page's top margin.
     *  
     * @return (expressed in points) size of the top margin
     * @since 1.0
     */
    public double getTopMargin()
    {
        return pageTopMargin;
    }

    /**
     * Returns default brush used on this page.
     * 
     * @return default brush of the page
     * @since 1.0
     */
    public PdfBrush getBrush()
    {
        return brush;
    }

    /**
     * Returns difference between bottom boundaries of this page's 
     * media box and crop box. 
     * 
     * @return (expressed in points) difference between bottom 
     *         boundaries of the page's media box and crop box
     * @since 1.0
     */
    public double getCropBottom()
    {
        return pageCropBottom;
    }

    /**
     * Returns difference between left boundaries of this page's 
     * media box and crop box.
     * 
     * @return (expressed in points) difference between left
     *         boundaries of this page's media box and crop box
     * @since 1.0
     */
    public double getCropLeft()
    {
        return pageCropLeft;
    }

    /**
     * Returns difference between right boundaries of this page's
     * media box and crop box.
     * 
     * @return (expressed in points) difference between right
     *         boundaries of this page's media box and crop box
     * @since 1.0
     */
    public double getCropRight()
    {
        return pageCropRight;
    }

    /**
     * Returns difference between top boundaries of this page's media
     * box and crop box.
     * 
     * @return (expressed in points) difference between top boundaries
     *         of this page's media box and crop box.
     * @since 1.0
     */
    public double getCropTop()
    {
        return pageCropTop;
    }

    /**
     * Returns height of this page.
     * 
     * @return (expressed in points) height of the page
     * @since 1.0
     */
    public double getHeight()
    {
        return pageHeight;
    }

    /**
     * Returns width of this page.
     * 
     * @return (expressed in points) width of the page
     * @since 1.0
     */
    public double getWidth()
    {
        return pageWidth;
    }

    /**
     * Returns a <code>PdfPen</code> object representing this page's
     * pen.
     * 
     * @return a <code>PdfPen</code> object representing the page's
     *         pen.
     * @since 1.0
     * @see #setPen(PdfPen)
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#getPen">example</a>.
     */
    public PdfPen getPen()
    {
        return pen;
    }
    
    /**
     * Sets specified <code>PdfPen</code> object as this page's pen.
     * 
     * @param pen
     *            <code>PdfPen</code> object to be set as this
     *            page's pen
     * @since 1.0
     * @see #getPen()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#setPen">example</a>.
     */
    public void setPen(PdfPen pen)
    {
        if(pen != null)
        {
            this.pen = pen;
        }
    }

    /**
     * Returns angle of rotation for contents of this page. The angle
     * of rotation will be expressed in integer multiples of 90 
     * degrees.
     * 
     * @return (expressed in integer multiples of 90 degrees and 
     *         measured in counterclockwise direction) angle of
     *         rotation for contents of the page
     * @since 1.0
     */
    public int getRotation()
    {
        return rotation;
    }
}