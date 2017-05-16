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

/**
 * @author amol
 * 
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
class PdfIntermediatePageNode extends PdfNode
{
    private static final PdfName COUNT = new PdfName(Usable.PDF_COUNT);

    private static final PdfName KIDS = new PdfName(Usable.PDF_KIDS);

    private static final PdfName PAGES = new PdfName(Usable.PDF_PAGES);

    PdfIntermediatePageNode()
    {
        super();
        childList = new ArrayList();
        dict.getMap().put(TYPE, PAGES);
    }

    PdfIntermediatePageNode(PdfDict d)
    {
        super(d);
        childList = new ArrayList();
        dict.getMap().put(TYPE, PAGES);
    }

    protected void write(PdfStdDocument d)
        throws IOException, PdfException
    {
        PdfName property;
        for (int i = 0, limit = PdfStdPage.inheritableProperties
            .size(); i < limit; ++i)
        {
            property = (PdfName) PdfStdPage.inheritableProperties
                .get(i);
            this.dict.dictMap.remove(property);
        }

		ArrayList arr = new ArrayList();
        PdfNode n;
        for (int i = 0; i < childList.size(); i++)
        {
            n = (PdfNode)childList.get(i);
            if (n.deleted)
            {
                continue;
            }
            arr.add(new PdfIndirectReference(n.getDict()
                .getObjectNumber(), 0));
        }
        dict.getMap().put(KIDS, new PdfArray(arr));
        dict.getMap().put(COUNT,
            new PdfInteger(PdfPageTree.count(this)));

        if (parent != null) /*this is not a root*/
        {
            PdfDict dd = parent.getDict();
            int objNo = dd.getObjectNumber();
            dict.getMap().put(PARENT,
                new PdfIndirectReference(objNo, 0));
        }

        int index = dict.getObjectNumber();
        d.offset[index] = d.bytesWritten;
        d.bytesWritten += d.writer.writeIndirectObject(dict);
    }
}