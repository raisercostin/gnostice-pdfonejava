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
 * This class is used to create, read from, and write to PDF 
 * documents. To create a new PDF file and then write to it, a 
 * <code>PdfDocument</code> object should be created with a
 * <code>PdfWriter</code> object. To read from a PDF file, and
 * possibly make changes and save it to a new file, a
 * <code>PdfDocument</code> object should be created with a
 * <code>PdfReader</code> object.
 * <p>
 * The <code>PdfDocument</code> class offers numerous methods to
 * work with elements such as text, images, annotations, bookmarks,
 * and pages in PDF documents.
 * </p>
 * <p>
 * When content is written to a new <code>PdfDocument</code> object,
 * a default <code>PdfPage</code> object is automatically created
 * and added to the <code>PdfDocument</code>. This
 * <code>PdfPage</code> also becomes <code>PdfDocument</code>'s
 * "current page." Whenever data is written to a document without
 * explicitly specifying a page range, the data is automatically
 * written to the <code>PdfDocument</code>'s current page. This
 * does not change even when new <code>PdfPage</code> objects have
 * been added to the <code>PdfDocument</code> object. To make a page
 * that is to be added set as the current page, the overloaded
 * {@link #add(PdfPage p, boolean setAsCurrentPage)} method should be
 * used. To write content to a specific page that is not necessarily
 * the current page, methods that have a page range argument should be
 * used.
 * </p>
 * <p>
 * While writing to a <code>PdfDocument</code> object, the position
 * where the content should appear is very important. The coordinates
 * of the position is always made in reference to the top-left corner
 * the <code>PdfDocument</code>'s current page. Whenever
 * coordinates, position, or sizes are used, they are usually applied
 * in terms of the document's current measurement unit, which can be
 * pixels, twips, points, inches, or centimeters. However, in
 * situations where a measurement unit cannot be applied or
 * determined, the measurement unit will be by default points.
 * </p>
 * <p>
 * Every document has a default pen setting and a default brush 
 * setting. The pen for example is used to stroke the borders when a 
 * rectangle is drawn. In the same example, the brush would be used 
 * when the area bounded by the rectangle is filled.
 * </p>
 * 
 * @since 1.0
 * @see PdfWriter
 * @see PdfReader
 * @see PdfPage
 * @version 1.0
 */
public class PdfDocument extends PdfProDocument
{
    /**
	 * 
	 * Constructs a new <code>PdfDocument</code> object with a
	 * <code>PdfWriter</code> object. The new <code>PdfDocument</code>
	 * object is used to create a new PDF file.
	 * 
	 * @param writer
	 *            <code>PdfWriter</code> object with which a
	 *            <code>PdfDocument</code> object is to be created
	 * @throws PdfException
	 *             if an illegal argument is supplied.
	 * @since 1.0
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#PdfDocument_PdfWriter">example</a>.
	 */	
    public PdfDocument(PdfWriter writer) throws PdfException
    {
        super(writer);
    }
    
    /**
	 * Constructs a new <code>PdfDocument</code> with a 
	 * <code>PdfReader</code> object. The new <code>PdfDocument</code>
	 * object is used to read from a file.
	 * 
	 * @param reader
	 *            <code>PdfReader</code> object with which a
	 *            <code>PdfDocument</code> object is to be created
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @throws PdfException
	 *             if an illegal argument is supplied.
	 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfDocument.ExampleSyntax.htm#PdfDocument_PdfReader">example</a>.
	 */	
    public PdfDocument(PdfReader reader) throws IOException,
        PdfException
    {
        super(reader);
    }
}