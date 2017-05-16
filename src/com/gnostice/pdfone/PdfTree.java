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
 * @author amol
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

abstract class PdfTree
{
	PdfNode root;
	
	PdfStdDocument parentDoc;
	
	PdfTree()
	{
	    root = null;
	}
	
	PdfTree(PdfStdDocument d)
	{
	    root = null;
	    parentDoc = d;
	}
	
	abstract void insert(PdfNode n) throws PdfException;
	
	abstract void insert(int index, PdfNode n) throws PdfException;
	
	abstract void delete(int index) throws PdfException;
	
	void merge(PdfTree t)
	{
		/*
         * To be overridden by PdfNamesTree class and PdfBookmarkTree
         * class. If this method is kept abstract then empty
         * implementation will have to be placed in PdfPageTree class.
         */
	}
	
	synchronized PdfNode getRoot()
	{
		return root;
	}
	
	synchronized PdfStdDocument getParentDoc()
	{
	    return parentDoc;
	}
}
