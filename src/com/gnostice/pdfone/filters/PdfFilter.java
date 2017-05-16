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

package com.gnostice.pdfone.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.gnostice.pdfone.PdfArray;
import com.gnostice.pdfone.PdfBadFileException;
import com.gnostice.pdfone.PdfDict;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfInteger;
import com.gnostice.pdfone.PdfName;
import com.gnostice.pdfone.PdfObject;
import com.gnostice.pdfone.PdfStream;
import com.gnostice.pdfone.Usable;

public class PdfFilter
{
    public static final int FLATE = 0;
    
    public static final int RUNLENGTH = 1;
    
    public static final int ASCIIHEX = 2;
    
    public static final int ASCII85 = 3;
    
    public static void decompress(PdfStream s) throws IOException,
        PdfException
    {
        PdfObject filters = s.getFilters();
        if (filters == null)
        {
            return;
        }
        if (filters instanceof PdfArray)
        {
            try
            {
                ArrayList l = (ArrayList) ((PdfArray) filters)
                    .getList();
                int i = 0;
                for (Iterator iter = l.iterator(); iter.hasNext(); ++i)
                {
                    PdfName filter = (PdfName) iter.next();
                    String filterName = filter.getString();
                    decompressByName(s, filterName, i);
                }
            }
            catch (ClassCastException cce)
            {
                throw new PdfBadFileException(
                    "Improper entry in filter array.");
            }
        }
        else if (filters instanceof PdfName)
        {
            String filterName = ((PdfName) filters).getString();
            decompressByName(s, filterName, -1);
        }
        else
        {
            throw new PdfBadFileException(
                "Improper '/Filters' entry in stream dictinary.");
        }
    }

    public static boolean allFiltersSupported(PdfStream s)
    {
        boolean isOK = true;
        PdfObject filters = s.getFilters();
        if (filters == null)
        {
            return isOK;
        }
        if (filters instanceof PdfArray)
        {
            try
            {
                ArrayList l = (ArrayList) ((PdfArray) filters)
                    .getList();
                int i = 0;
                for (Iterator iter = l.iterator(); iter.hasNext(); ++i)
                {
                    PdfName filter = (PdfName) iter.next();
                    String filterName = filter.getString();
                    isOK = checkFilter(filterName);
                }
            }
            catch (ClassCastException cce)
            {
                isOK = false;
            }
        }
        else if (s.getFilters() instanceof PdfName)
        {
            String filterName = ((PdfName) filters).getString();
            isOK = checkFilter(filterName);
        }
        else
        {
            isOK = false;
        }

        return isOK;
    }

    public static ArrayList encode(PdfStream stream, ArrayList filters,
        boolean addDefaultFilter, int compressionLevel)
        throws PdfException
    {
        if (filters == null && addDefaultFilter)
        {
            filters = new ArrayList();
            filters.add(new PdfInteger(PdfFilter.FLATE));
        }
        int limit = filters == null ? 0 : filters.size();
        for (int i = 0, id; i < limit; ++i)
        {
            id = ((PdfInteger) filters.get(i)).getInt();
            switch (id)
            {
                case PdfFilter.FLATE:
                    PdfFlateFilter.encode(stream, compressionLevel);
                    break;
                case PdfFilter.RUNLENGTH:
                    PdfRunLengthFilter.encode(stream);
                    break;
                case PdfFilter.ASCII85:
                    PdfAscii85Filter.encode(stream);
                    break;
                case PdfFilter.ASCIIHEX:
                    PdfAsciiHexFilter.encode(stream);
                    break;
                default:
                    break;
            }
        }
        
        return filters;
    }

    private static boolean checkFilter(String filterName)
    {
        return (Usable.PDF_FLATE.equals(filterName) || Usable.PDF_FLATE_NEW
            .equals(filterName))
            || (Usable.PDF_ASCIIHEX.equals(filterName) || Usable.PDF_ASCIIHEX_NEW
                .equals(filterName))
            || (Usable.PDF_ASCII85.equals(filterName) || Usable.PDF_ASCII85_NEW
                .equals(filterName))
            || (Usable.PDF_RUNLENGTH.equals(filterName) || Usable.PDF_RUNLENGTH_NEW
                .equals(filterName));
    }

    private static void decompressByName(PdfStream s,
        String filterName, int i) throws PdfException
    {
        PdfObject decodeParams = s.getDecodeParms();

        if (Usable.PDF_FLATE.equals(filterName)
            || Usable.PDF_FLATE_NEW.equals(filterName))
        {
            s.setBuffer(PdfFlateFilter.decode(s.getBuffer()));
            if (decodeParams instanceof PdfArray)
            {
                PdfObject dp = (PdfObject) ((ArrayList) ((PdfArray) decodeParams)
                    .getList()).get(i);
                if (dp instanceof PdfDict)
                {
                    s.setBuffer(PdfFlateFilter.dePredict(s
                        .getBuffer(), (PdfDict) dp));
                }
            }
            else if (decodeParams instanceof PdfDict)
            {
                s.setBuffer(PdfFlateFilter.dePredict(s.getBuffer(),
                    (PdfDict) decodeParams));
            }
        }
        else if (Usable.PDF_ASCIIHEX.equals(filterName)
            || Usable.PDF_ASCIIHEX_NEW.equals(filterName))
        {
            s.setBuffer(PdfAsciiHexFilter.decode(s.getBuffer()));
        }
        else if (Usable.PDF_ASCII85.equals(filterName)
            || Usable.PDF_ASCII85_NEW.equals(filterName))
        {
            s.setBuffer(PdfAscii85Filter.decode(s.getBuffer()));
        }
        else if (Usable.PDF_RUNLENGTH.equals(filterName)
            || Usable.PDF_RUNLENGTH_NEW.equals(filterName))
        {
            s.setBuffer(PdfRunLengthFilter.decode(s.getBuffer()));
        }
        // else if (Usable.PDF_DCTDECODE.equals(filterName)
        // || Usable.PDF_DCTDECODE_NEW.equals(filterName))
        //    {
        //        this.streamBuffer = PdfFlateFilter
        //            .decode(this.streamBuffer);
        //    }
        else
        {
            throw new PdfException("Filter " + filterName
                + " not implemented.");
        }
    }
}
