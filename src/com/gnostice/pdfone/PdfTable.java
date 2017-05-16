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

import com.gnostice.pdfone.fonts.PdfFont;
import com.gnostice.pdfone.graphics.PdfPen;

public final class PdfTable 
{
    protected double currentX;
    
    protected double currentY;
    
    private ArrayList cellList;

    protected int activeCol;

    protected double[] colPos;

    protected int totalColumns;

	private double defaultColWidth = 72;

	protected double[] colWidthArray;
	
	protected double[] colHeightArray;

	protected double rowHeight;
    
	protected PdfPen  pen;
	
	protected double cellLeftMargin;
	
	protected double cellTopMargin;
	
	protected double cellRightMargin;
	
	protected double cellBottomMargin;

	protected boolean autoAdjRowHeight;
    
    private int measurementUnit;
    
    protected PdfFont font;
    
    protected Color  backgroundColor;
    
    protected int alignment;
    
    public PdfTable(int columns)
    {
        if (columns < 1)
        {
            columns = 1;
        }
        this.totalColumns = columns;
        colHeightArray = new double[columns];
        colWidthArray = new double[columns];
		for(int i = 0; i < columns; i++)
		{
			colWidthArray[i] = 72;
		}
		this.rowHeight = 72;
		
		this.autoAdjRowHeight = false;
		
		
		this.cellLeftMargin = 0;
		this.cellTopMargin = 0;
		this.cellRightMargin = 0;
		this.cellBottomMargin = 0;
        this.measurementUnit = PdfMeasurement.MU_POINTS;
        this.alignment = PdfTextFormatter.LEFT;
        this.font = null;
        this.pen = null;//new PdfPen();
        this.backgroundColor = null;
    }

    public PdfTable(int columns, double[] colwidth,
        int measurementUnit) throws PdfException
    {
        colHeightArray = new double[columns];
        this.autoAdjRowHeight = false;

        if (columns < 1)
        {
            columns = 1;
        }

        this.rowHeight = 72;

        if (colwidth == null)
        {
            throw new PdfException("The colwidth array "
                + "can not be null");
        }
        this.totalColumns = columns;

        colWidthArray = new double[columns];

        if (colwidth.length <= columns)
        {
            for (int i = 0; i < colwidth.length; i++)
            {
                colWidthArray[i] = PdfMeasurement.convertToPdfUnit(
                    measurementUnit, colwidth[i]);
            }
            if (columns > colwidth.length)
            {
                for (int i = colwidth.length; i < columns; i++)
                {
                    colWidthArray[i] = defaultColWidth;
                }
            }
        }
        else
        {
            for (int i = 0; i < columns; i++)
            {
                colWidthArray[i] = PdfMeasurement.convertToPdfUnit(
                    measurementUnit, colwidth[i]);
            }
        }
        this.cellLeftMargin = 0;
        this.cellTopMargin = 0;
        this.cellRightMargin = 0;
        this.cellBottomMargin = 0;
        this.measurementUnit = measurementUnit;
        this.alignment = PdfTextFormatter.LEFT;
        this.font = null;
        this.pen = null;//new PdfPen();
        this.backgroundColor = null;
    }

    public PdfTable(int columns, double[] colwidth,
        double cellHeight, int measurementUnit) throws PdfException
    {
        colHeightArray = new double[columns];
        this.autoAdjRowHeight = false;

        if (columns < 1)
        {
            columns = 1;
        }

        this.rowHeight = PdfMeasurement.convertToPdfUnit(
            measurementUnit, cellHeight);

        if (colwidth == null)
        {
            throw new PdfException("No of column width array "
                + "can not be null");
        }
        this.totalColumns = columns;

        colWidthArray = new double[columns];

        if (colwidth.length <= columns)
        {
            for (int i = 0; i < colwidth.length; i++)
            {
                colWidthArray[i] = PdfMeasurement.convertToPdfUnit(
                    measurementUnit, colwidth[i]);
            }
            if (columns > colwidth.length)
            {
                for (int i = colwidth.length; i < columns; i++)
                {
                    colWidthArray[i] = defaultColWidth;
                }
            }
        }
        else
        {
            for (int i = 0; i < columns; i++)
            {
                colWidthArray[i] = PdfMeasurement.convertToPdfUnit(
                    measurementUnit, colwidth[i]);
            }
        }
        this.cellLeftMargin = 0;
        this.cellTopMargin = 0;
        this.cellRightMargin = 0;
        this.cellBottomMargin = 0;
        this.measurementUnit = measurementUnit;
        this.alignment = PdfTextFormatter.LEFT;
        this.font = null;
        this.pen = null;//new PdfPen();
        this.backgroundColor = null;
    }

    protected ArrayList getCellList()
    {
        return this.cellList;
    }
   
    protected void setCellList(ArrayList cellList)
    {
        this.cellList = cellList;
    }
    
    private PdfCell setDefaultCellValues(PdfCell c)
    {
        c.cellLeftMargin = cellLeftMargin;
        c.cellTopMargin = cellTopMargin;
        c.cellRightMargin = cellRightMargin;
        c.cellBottomMargin = cellBottomMargin;
        
        if(this.font != null)
        {
            c.font = this.font;
        }
        if(this.pen != null)
        {
            c.pen = this.pen;
        }
        if(this.backgroundColor != null)
        {
            c.backgroundColor = this.backgroundColor;
        }
        c.alignment = this.alignment;
        return c;
    }
    
	public void addCell(int rowSpan, int colSpan)
	{
		if (cellList == null)
		{
		    cellList = new ArrayList();
		}
		PdfCell c = new PdfCell(rowSpan, colSpan);
        c = setDefaultCellValues(c);
		cellList.add(c);
	}

	public void addCell(int rowSpan, int colSpan, String text)
    {
        if (cellList == null)
        {
            cellList = new ArrayList();
        }
        PdfCell c = new PdfCell(rowSpan, colSpan);
        c.text = text;
        c = setDefaultCellValues(c);        
        cellList.add(c);
    }
	
    public void addCell(int rowSpan, int colSpan, String text,
        Color backgroundColor, PdfFont font, int aligntment)
    {
        if (cellList == null)
        {
            cellList = new ArrayList();
        }
        PdfCell c = new PdfCell(rowSpan, colSpan);
        c.text = text;
        c = setDefaultCellValues(c);      
        c.backgroundColor = backgroundColor;
        c.font = font;
        c.alignment = aligntment;
        cellList.add(c);
    }

    public void addCell(int rowSpan, int colSpan, String text,
        int aligntment)
    {
        if (cellList == null)
        {
            cellList = new ArrayList();
        }
        PdfCell c = new PdfCell(rowSpan, colSpan);
        c.text = text;
        c = setDefaultCellValues(c);      
        c.alignment = aligntment;
        cellList.add(c);
    }

    public void addCell(int rowSpan, int colSpan, String text,
        Color backgroundColor)
    {
        if (cellList == null)
        {
            cellList = new ArrayList();
        }
        PdfCell c = new PdfCell(rowSpan, colSpan);
        c.text = text;
        c = setDefaultCellValues(c);      
        c.backgroundColor = backgroundColor;
        cellList.add(c);
    }

	public void addCell(int rowSpan, int colSpan, PdfImage image)
    {
        if (cellList == null)
        {
            cellList = new ArrayList();
        }
        PdfCell c = new PdfCell(rowSpan, colSpan);
        c.image = image;
        c = setDefaultCellValues(c);
        
        cellList.add(c);
    }

	public void addCell(int rowSpan, int colSpan,
        Color backgroundColor)
    {
        if (cellList == null)
        {
            cellList = new ArrayList();
        }
        PdfCell c = new PdfCell(rowSpan, colSpan);
        c = setDefaultCellValues(c);
        c.backgroundColor = backgroundColor;
        cellList.add(c);
    }

	public void addCell(int rowSpan, int colSpan, String text,
        String imagePath) throws IOException, PdfException
    {
        if (cellList == null)
        {
            cellList = new ArrayList();
        }
        PdfCell c = new PdfCell(rowSpan, colSpan);
        c.text = text;
        PdfImage img = PdfImage.create(imagePath);
        c.image = img;
        c = setDefaultCellValues(c);
        cellList.add(c);
    }

	public void addCell(int rowSpan, int colSpan, String text,
	    PdfImage image, Color backgroundColor, int alignment)
		throws IOException, PdfException
	{
	    if (cellList == null)
		{
		    cellList = new ArrayList();
		}
		PdfCell c = new PdfCell(rowSpan, colSpan);
		c.text = text; 
		c.image = image;
        c = setDefaultCellValues(c);
		c.alignment = alignment;
        c.backgroundColor = backgroundColor;
		cellList.add(c);
	}
	    
    public void addCell(int rowSpan, int colSpan,
        PdfFormField formfield)
    {
        if (cellList == null)
        {
            cellList = new ArrayList();
        }
        PdfCell c = new PdfCell(rowSpan, colSpan);
        c = setDefaultCellValues(c);
        c.formfield = formfield;
        cellList.add(c);
    }

	public PdfPen getPen()
    {
        return this.pen;
    }
   
    public void setPen(PdfPen pen)
    {
        this.pen = pen;
    }
	
    public boolean getAutoAdjRowHeight()
    {
        return this.autoAdjRowHeight;
    }
   
    public void setAutoAdjRowHeight(boolean autoAdjColumnHeight)
    {
        this.autoAdjRowHeight = autoAdjColumnHeight;
    }

    public double getCellLeftMargin()
    {
        return PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, cellLeftMargin);
    }
   
    public void setCellLeftMargin(double cellLeftMargin)
    {
        this.cellLeftMargin = PdfMeasurement.convertToPdfUnit(
            measurementUnit, cellLeftMargin);
    }

    public double getCellTopMargin()
    {
        return PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, cellTopMargin);
    }
   
    public void setCellTopMargin(double cellTopMargin)
    {
        this.cellTopMargin = PdfMeasurement.convertToPdfUnit(
            measurementUnit, cellTopMargin);
    }
    
    public double getCellRightMargin()
    {
        return PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, cellRightMargin);
    }
   
    public void setCellRightMargin(double cellRightMargin)
    {
        this.cellRightMargin = PdfMeasurement.convertToPdfUnit(
            measurementUnit, cellRightMargin);
    }

    public double getCellBottomMargin()
    {
        return PdfMeasurement.convertToMeasurementUnit(
            measurementUnit, cellBottomMargin);
    }
   
    public void setCellBottomMargin(double cellBottomMargin)
    {
        this.cellBottomMargin = PdfMeasurement.convertToPdfUnit(
            measurementUnit, cellBottomMargin);
    }

    public double getRowHeight()
    {
        return PdfMeasurement.convertToMeasurementUnit(
          measurementUnit, this.rowHeight);
    }
   
    public void setRowHeight(double rowHeight)
    {
        this.rowHeight = PdfMeasurement.convertToPdfUnit(
            measurementUnit, rowHeight);
    }
    
    /* Copy will be returned */
    public double[] getColumnWidths()
    {
        double[] tempColWidth = new double[this.colWidthArray.length];
        for (int i = 0; i < this.colWidthArray.length; i++)
        {
            tempColWidth[i] = PdfMeasurement
                .convertToMeasurementUnit(measurementUnit,
                    this.colWidthArray[i]);
        }
        return tempColWidth;
    }

    public void setColumnWidth(double colwidth[])
    {
        for (int i = 0; i < colWidthArray.length
            && i < colwidth.length; i++)
        {
            colWidthArray[i] = PdfMeasurement.convertToPdfUnit(
                measurementUnit, colwidth[i]);
        }
    }

    public Color getBackgroundColor()
    {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    public PdfFont getFont()
    {
        return font;
    }

    public void setFont(PdfFont font)
    {
        this.font = font;
    }

    public int getAlignment()
    {
        return alignment;
    }

    public void setAlignment(int alignment)
    {
        this.alignment = alignment;
    }
    
}
