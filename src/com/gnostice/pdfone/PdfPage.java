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

/**
 * This class represents a PDF page. <code>PdfPage</code> offers
 * numerous methods to work with elements such as text, shapes,
 * images, annotations, and bookmarks.
 * <p>
 * After content is written to a page, it is usually added to a
 * <code>PdfDocument</code> object using the
 * <code>PdfDocument.add(PdfPage)</code> method.A 
 * <code>PdfPage</code> object can be cloned using the
 * {@link #clone()} method and then added to a
 * <code>PdfDocument</code> one more time. However, the
 * same <code>PdfPage</code> object can be added to multiple
 * <code>PdfDocument</code> object without cloning.
 * </p>
 * <pre style="margin-left: 15%;>
 *  <span style="color: green;">  // Creates several PdfDocument objects</span>
 *  PdfDocument doc1 = ...
 *  PdfDocument doc2 = ...
 *  PdfDocument doc1 = ...
 *  ...
 *  PdfDocument docn = ...
 *  
 *  <span style="color: green;">// Creates a PdfPage object</span>
 *  PdfPage page = new PdfPage();
 *  
 *  <span style="color: green;">// Performs operations with the above PdfPage object</span>
 *  ...
 *  
 *  <span style="color: green;">// Adds the same PdfPage object to the PdfDocument objects 
 *  // created above</span>
 *  <span style="background-color: lavender;"> doc1.add(page); </span>
 *  <span style="background-color: lavender;"> doc2.add(page); </span>
 *  <span style="background-color: lavender;"> doc3.add(page); </span>
 *  <span style="background-color: lavender;"> ...  </span>
 *  <span style="background-color: lavender;"> docn.add(page); </span> 
 *  
 *  ...</pre>
 * <p>
 * While writing to a page, the position where the content should
 * appear is very important. The coordinates of the position is 
 * always made in reference to the top-left corner the page. Whenever
 * coordinates, position, or sizes are used, they are usually applied
 * in terms of the page's current measurement unit, which can be
 * centimeters, inches, pixels, points, and twips. However, in
 * situations where a measurement unit cannot be applied or
 * determined, the measurement unit will be by default points.
 * </p>
 * <p>
 * Every page has a default pen setting and a default brush setting.
 * The pen for example is used to stroke the borders when a rectangle 
 * is drawn. In the same example, the brush would be used when the 
 * area bounded by the rectangle is filled.
 * </p>
 * 
 * @since 1.0
 * @version 1.0
 */
public class PdfPage extends PdfProPage
{
    /**
     * Zero-argument default constructor.
     * 
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#PdfPage">example</a>.
     */
    public PdfPage()
    {
        super();
    }

    /**
     * Constructs a page with specified page size.
     * 
     * @param pageSize constant specifying the page size
     * @since 1.0
     * @see PdfPageSize
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#PdfPage_int">example</a>.
     */
    public PdfPage(int pageSize)
    {
        super(pageSize, 0, 0, 0, 0, 0, 0, PdfMeasurement.MU_POINTS);
    }

    /**
     * Constructs a page with its width and height. 
     * 
     * @param width width of the page
     * @param height height of page
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#PdfPage_double_double">example</a>. 
     */
    public PdfPage(double width, double height)
    {
        super(width, height, 0, 0, 0, 0, 0, 0,
            PdfMeasurement.MU_POINTS);
    }

    /**
     * Constructs a page with its width and height specified in
     * <code>measurementUnit</code>.
     * 
     * @param width
     *            width of the page
     * @param height
     *            height of the page
     * @param measurementUnit
     *            constant specifying the measurement unit
     * @since 1.0
     * @see PdfMeasurement
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#PdfPage_double_double_int">example</a>.
     */
    public PdfPage(double width, double height, int measurementUnit)
    {
        super(width, height, 0, 0, 0, 0, 0, 0, measurementUnit);
    }

    /*
     * Constructs page with specified page size and margins.
     * 
     * @param pageSize
     *            constant specifying the page size
     * @param pageLeftMargin
     *            size of the left margin
     * @param pageTopMargin
     *            size of the top margin
     * @param pageRightMargin
     *            size of the right margin
     * @param pageBottomMargin
     *            size of the bottom margin
     * @since 1.0
     * @see PdfPageSize
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#PdfPage_int_double_double_double_double">example</a>.
     */
    /*public PdfPage(int pageSize, double pageLeftMargin,
        double pageTopMargin, double pageRightMargin,
        double pageBottomMargin)
    {
        super(pageSize, 0, 0, pageLeftMargin, pageTopMargin,
            pageRightMargin, pageBottomMargin,
            PdfMeasurement.MU_POINTS);
    }*/
    
    /*
     * Constructs a page with specified width, height, left margin,
     * top margin, right margin, and left margin.
     * 
     * @param width
     *            width of the page
     * @param height
     *            height of the page
     * @param pageLeftMargin
     *            size of the left margin
     * @param pageTopMargin
     *            size of the top margin
     * @param pageRightMargin
     *            size of the right margin
     * @param pageBottomMargin
     *            size of the bottom margin
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#PdfPage_double_double_double_double_double_double">example</a>.
     */
    /*public PdfPage(double width, double height, 
        double pageLeftMargin, double pageTopMargin, 
        double pageRightMargin, double pageBottomMargin)
    {
        super(width, height, 0, 0, pageLeftMargin, pageTopMargin,
            pageRightMargin, pageBottomMargin,
            PdfMeasurement.MU_POINTS);
    }*/

    /**
     * Constructs a page with specified page size and with its margins
     * specified in <code>measurementUnit</code>.
     * 
     * @param pageSize
     *            constant specifying the page size
     * @param pageLeftMargin
     *            size of the left margin
     * @param pageTopMargin
     *            size of the top margin
     * @param pageRightMargin
     *            size of the right margin
     * @param pageBottomMargin
     *            size of the bottom margin
     * @param measurementUnit
     *            constant specifying the measurement unit
     * @since 1.0
     * @see PdfPageSize
     * @see PdfMeasurement
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#PdfPage_int_double_double_double_double_int">example</a>.
     */
    public PdfPage(int pageSize, double pageLeftMargin,
        double pageTopMargin, double pageRightMargin,
        double pageBottomMargin, int measurementUnit)
    {
        super(pageSize, 0, 0, pageLeftMargin, pageTopMargin,
            pageRightMargin, pageBottomMargin, measurementUnit);
    }

    /**
     * Constructs a page with its width, height, left margin, top
     * margin, right margin, and bottom margin specified in
     * <code>measurementUnit</code>.
     * 
     * @param width
     *            width of the page
     * @param height
     *            height of the page
     * @param pageLeftMargin
     *            size of the left margin
     * @param pageTopMargin
     *            size of the top margin
     * @param pageRightMargin
     *            size of the right margin
     * @param pageBottomMargin
     *            size of the bottom margin
     * @param measurementUnit
     *            constant specifying the measurement unit
     * @since 1.0
     * @see PdfMeasurement
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#PdfPage_double_double_double_double_double_double_int">example</a>.
     */
    public PdfPage(double width, double height,
        double pageLeftMargin, double pageTopMargin,
        double pageRightMargin, double pageBottomMargin,
        int measurementUnit)
    {
        super(width, height, 0, 0, pageLeftMargin, pageTopMargin,
            pageRightMargin, pageBottomMargin, measurementUnit);
    }
    
    /*
     * Constructs page with specified page size, header height, footer
     * height, left margin, top margin, right margin, and left margin.
     * 
     * @param pageSize
     *            constant specifying the page size
     * @param pageHeaderHeight
     *            height of the header
     * @param pageFooterHeight
     *            height of the footer
     * @param pageLeftMargin
     *            size of the left margin
     * @param pageTopMargin
     *            size of the top margin
     * @param pageRightMargin
     *            size of the right margin
     * @param pageBottomMargin
     *            size of the bottom margin
     * @since 1.0
     * @see PdfPageSize
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#PdfPage_int_double_double_double_double_double_double">example</a>.
     */
    /*public PdfPage(int pageSize, double pageHeaderHeight,
        double pageFooterHeight, double pageLeftMargin,
        double pageTopMargin, double pageRightMargin,
        double pageBottomMargin)
    {
        super(pageSize, pageHeaderHeight, pageFooterHeight,
            pageLeftMargin, pageTopMargin, pageRightMargin,
            pageBottomMargin, PdfMeasurement.MU_POINTS);
    }*/

    /*
     * Constructs a page with specified width, height, header height,
     * footer height, left margin, top margin, right margin, and
     * bottom margin.
     * 
     * @param width
     *            width of the page
     * @param height
     *            height of the page
     * @param pageHeaderHeight
     *            size of the header
     * @param pageFooterHeight
     *            size of the footer
     * @param pageLeftMargin
     *            size of the left margin
     * @param pageTopMargin
     *            size of the top margin
     * @param pageRightMargin
     *            size of the right margin
     * @param pageBottomMargin
     *            size of the bottom margin
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#PdfPage_double_double_double_double_double_double_double_double">example</a>.
     */
    /*public PdfPage(double width, double height,
       double pageHeaderHeight, double pageFooterHeight,
       double pageLeftMargin, double pageTopMargin,
       double pageRightMargin, double pageBottomMargin)
   {
        super(width, height, pageHeaderHeight, pageFooterHeight,
           pageLeftMargin, pageTopMargin, pageRightMargin,
           pageBottomMargin, PdfMeasurement.MU_POINTS);
   }*/

    /**
     * Constructs page with specified page size and with its header 
     * height, footer height, and page margins specified in 
     * <code>measurementUnit</code>.
     * 
     * @param pageSize
     *            constant specifying the page size
     * @param pageHeaderHeight
     *            height of the header
     * @param pageFooterHeight
     *            height of the footer
     * @param pageLeftMargin
     *            size of the left margin
     * @param pageTopMargin
     *            size of the top margin
     * @param pageRightMargin
     *            size of the right margin
     * @param pageBottomMargin
     *            size of the bottom margin
     * @param measurementUnit
     *            constant specifying the measurement unit
     * @since 1.0
     * @see PdfPageSize
     * @see PdfMeasurement
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#PdfPage_int_double_double_double_double_double_double_int">example</a>.
     */
    public PdfPage(int pageSize, double pageHeaderHeight,
        double pageFooterHeight, double pageLeftMargin,
        double pageTopMargin, double pageRightMargin,
        double pageBottomMargin, int measurementUnit)
    {
        super(pageSize, pageHeaderHeight, pageFooterHeight,
            pageLeftMargin, pageTopMargin, pageRightMargin,
            pageBottomMargin, measurementUnit);
    }

    /**
     * Constructs a page with its width, height, header height, footer
     * height, left margin, top margin, right margin, and bottom
     * margin specified in <code>measurementUnit</code>.
     * 
     * @param width
     *            width of the page
     * @param height
     *            height of the page
     * @param pageHeaderHeight
     *            height of the header
     * @param pageFooterHeight
     *            height of the footer
     * @param pageLeftMargin
     *            size of the left margin
     * @param pageTopMargin
     *            size of the top margin
     * @param pageRightMargin
     *            size of the right margin
     * @param pageBottomMargin
     *            size of the bottom margin
     * @param measurementUnit
     *            constant specifying the measurement unit
     * @since 1.0
     * @see PdfMeasurement
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#PdfPage_double_double_double_double_double_double__double_double_int">example</a>.
     */
    public PdfPage(double width, double height,
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
        return (PdfPage) super.clone();
    }
}