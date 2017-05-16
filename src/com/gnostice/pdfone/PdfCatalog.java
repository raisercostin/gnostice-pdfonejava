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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class PdfCatalog extends PdfObject
{
    static HashMap knownAttributes;
    
    static
    {
        knownAttributes = new HashMap();
        knownAttributes.put(Usable.PDF_PAGES, PdfNull.DUMMY);
        knownAttributes.put(Usable.PDF_OUTLINES, PdfNull.DUMMY);
        knownAttributes.put(Usable.PDF_TYPE, PdfNull.DUMMY);
        knownAttributes.put(Usable.PDF_PAGELAYOUT, PdfNull.DUMMY);
        knownAttributes.put(Usable.PDF_PAGEMODE, PdfNull.DUMMY);
        knownAttributes.put(Usable.PDF_ACROFORM, PdfNull.DUMMY);
    }

    PdfDict catalogDict;

    PdfDict acroDict;
    
    HashMap unknownAttributes;
    
    String formDA; //DA entry in AcroDict.
    
    List fields; //Fields entry in AcroDict.
    
	PdfCatalog(PdfDict dict)
	{
		this.catalogDict = dict;
		this.setObjectNumber(catalogDict.getObjectNumber());
        unknownAttributes = new HashMap();
	}

	synchronized PdfDict getDictionary()
	{
		return this.catalogDict;
	}

	synchronized void setDictionary(PdfDict dict)
	{
		this.catalogDict = dict;
	}

    /*
     * Invoke to remove field ind ref from /Fields list for existing
     * docs only
     */
    void removeFieldsEntry(int objNo, int genNo, PdfStdDocument d)
        throws IOException, PdfException
    {
        if (acroDict != null)
        {
            if (fields == null)
            {
                PdfName name_fields = new PdfName(Usable.PDF_FIELDS);
                PdfObject obj = d.reader.getObject(acroDict
                    .getValue(name_fields));
                fields = ((PdfArray) obj).getList();
            }
            fields.remove(new PdfIndirectReference(objNo, genNo));
        }
    }
    
    /* Invoke to read /DA entry for existing docs only */
    String getFormDA(PdfStdDocument d) throws IOException,
        PdfException
    {
        if (formDA != null)
        {
            return formDA;
        }
        if (this.acroDict != null)
        {
            PdfName name_da = new PdfName(Usable.PDF_DA);
            PdfObject obj = d.reader.getObject(acroDict
                .getValue(name_da));
            if (obj != null)
            {
                formDA = ((PdfString) obj).getString();
            }
        }
        else
        {
            formDA = "";
        }

        return formDA;
    }

    void setAcroDict(PdfStdDocument d) throws IOException,
        PdfException
    {
        HashMap acroHm = d.prepareAcroMap();
        if (acroHm == null) // fields not added to Doc
        {
            if (acroDict != null) // fields present initially
            {
                d.updateIndirectRefs(d, acroDict, true);
                catalogDict.dictMap.put(new PdfName(
                    Usable.PDF_ACROFORM), acroDict);
            }
        }
        else //fields added to Doc
        {
            if (acroDict == null) //fields not present initially
            {
                catalogDict.dictMap.put(new PdfName(
                    Usable.PDF_ACROFORM), new PdfDict(acroHm));
            }
            else // fields present initially
            {
                HashMap newAcroMap = new HashMap();
                
                PdfName name_fields = new PdfName(Usable.PDF_FIELDS);
                PdfArray rFields = (PdfArray) d.reader
                    .getObject(acroDict.getValue(name_fields));
                d.updateIndirectRefs(d, rFields, true);
                List rFieldsList = rFields == null ? new ArrayList()
                    : rFields.getList();
                List fieldsList = ((PdfArray) acroHm.get(name_fields))
                    .getList();
                rFieldsList.addAll(fieldsList);
                newAcroMap.put(name_fields, new PdfArray(rFieldsList));
                
                PdfName name_dr = new PdfName(Usable.PDF_DR);
                PdfName name_font = new PdfName(Usable.PDF_FONT);
                PdfDict rDR = (PdfDict) d.reader.getObject(acroDict
                    .getValue(name_dr));
                d.updateIndirectRefs(d, rDR, true);
                Map rDRMap = rDR == null ? new HashMap() : rDR.getMap();
                Map DRMap  = ((PdfDict) acroHm.get(name_dr))
                    .getMap();
                Map rDRFontMap = ((PdfDict) rDRMap.get(name_font)).dictMap;
                Map  DRFontMap = ((PdfDict)  DRMap.get(name_font)).dictMap;
                rDRFontMap.putAll(DRFontMap);
                newAcroMap.put(name_dr, new PdfDict(rDRMap));
                
                PdfName name_needApp = new PdfName(
                    Usable.PDF_NEEDAPPEARANCES);
                PdfObject obj = (PdfObject) acroHm.get(name_needApp);
                if (obj == null)
                {
                    obj = acroDict.getValue(name_needApp);
                }
                if (obj != null)
                {
                    newAcroMap.put(name_needApp, acroHm
                        .get(name_needApp));
                }

                PdfName name_q = new PdfName(Usable.PDF_Q);
                newAcroMap.put(name_q, acroHm.get(name_q));

                PdfName name_da = new PdfName(Usable.PDF_DA);
                obj = d.reader.getObject(acroDict
                    .getValue(name_da));
                d.updateIndirectRefs(d, obj, true);
                if (obj != null)
                {
                    newAcroMap.put(name_da, obj);
                }

                PdfName name_sigFlags = new PdfName("SigFlags");
                obj = d.reader.getObject(acroDict
                    .getValue(name_sigFlags));
                d.updateIndirectRefs(d, obj, true);
                if (obj != null)
                {
                    newAcroMap.put(name_sigFlags, obj);
                }
                
                PdfName name_co = new PdfName("CO");
                obj = d.reader.getObject(acroDict
                    .getValue(name_co));
                d.updateIndirectRefs(d, obj, true);
                if (obj != null)
                {
                    newAcroMap.put(name_co, obj);
                }
                
                PdfName name_xfa = new PdfName("XFA");
                obj = d.reader.getObject(acroDict
                    .getValue(name_xfa));
                d.updateIndirectRefs(d, obj, true);
                if (obj != null)
                {
                    newAcroMap.put(name_xfa, obj);
                }
                
                catalogDict.dictMap.put(new PdfName(
                    Usable.PDF_ACROFORM), new PdfDict(newAcroMap));
            }
        }
    }
    
    void set(PdfStdDocument d) throws IOException, PdfException
    {
        setAcroDict(d);
        
        if ( !unknownAttributes.isEmpty())
        {
            PdfObject key;
            PdfObject value;
            for (Iterator iter = unknownAttributes.keySet()
                .iterator(); iter.hasNext();)
            {
                key = (PdfObject) iter.next();
                value = (PdfObject) unknownAttributes.get(key);
                if (value != null)
                {
                    d.updateIndirectRefs(d, value, true);
                    /* DO WE NEED ORIGINDOC HERE IN CASE OF MERGING */ 
                }
            }
        }
    }
    
	protected int write(PdfWriter writer) throws IOException
	{
        return catalogDict.write(writer);
	}
}