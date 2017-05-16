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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.gnostice.pdfone.fonts.PdfFont;

class PdfTextParser
{
    private static final Pattern PAT_DATE = Pattern.compile(
        "([~]*)<%\\s*(date)\\s*((')([^']*)('))?\\s*%>", 
        Pattern.CASE_INSENSITIVE);

    private static final Pattern PAT_PAGENO = Pattern.compile(
        "([~]*)<%\\s*(pageNo)\\s*%>", Pattern.CASE_INSENSITIVE);

    private static final Pattern PAT_PAGECOUNT = Pattern.compile(
        "([~]*)<%\\s*(PageCount)\\s*%>", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern PAT_FONT = Pattern
        .compile("<%\\s*(font)\\s*(([a-zA-Z]*)\\s*=\\s*((')([^']*)" +
                "(')))?\\s*(([a-zA-Z]*)\\s*=\\s*((')([^']*)(')))?\\s*" +
                "(([a-zA-Z]*)\\s*=\\s*((')([^']*)(')))?\\s*(([a-zA-Z]*)" +
                "\\s*(=)\\s*(')([^']*)('))?\\s*%>([^<]*)(</font>)",
                Pattern.CASE_INSENSITIVE);
    
    private static String html_FontName = "Arial";
    
    private static String html_FontColor = "000000";
    
    private static String html_FontSize = "10";
    
    private static String html_FontEncoding = "";
    
    static boolean containsHTMLtags(String str)
    {
        Matcher matcher;

        matcher = PAT_FONT.matcher(str);
        if (matcher.find())
        {
            return true;
        }

        return false;
    }
    
    private static void setHtmlFontParameters(String par, String val)
    {
        if(par.equalsIgnoreCase("file"))
        {
            html_FontName = val;
        }
        else if(par.equalsIgnoreCase("color"))
        {
            html_FontColor = val;
        }
        else if(par.equalsIgnoreCase("size"))
        {
            html_FontSize = val;
        }
        else if(par.equalsIgnoreCase("encoding"))
        {
            html_FontEncoding = val;
        }
    }
    
    static ArrayList replaceHTMLvar(String str)
        throws IOException, PdfException
    {
        
        Matcher matcher;
        StringBuffer sb;
        
        matcher = PAT_FONT.matcher(str);
        
        ArrayList Strlist = new ArrayList();
        while (matcher.find())
        {
            if (matcher.group(3) != null && matcher.group(6) != null
                && !matcher.group(3).equals("")
                && !matcher.group(6).equals(""))
            {
                setHtmlFontParameters(matcher.group(3), matcher
                    .group(6));
            }
            if (matcher.group(9) != null && matcher.group(12) != null
                && !matcher.group(9).equals("")
                && !matcher.group(12).equals(""))
            {
                setHtmlFontParameters(matcher.group(9), matcher
                    .group(12));
            }
            if (matcher.group(15) != null
                && matcher.group(18) != null
                && !matcher.group(15).equals("")
                && !matcher.group(18).equals(""))
            {
                setHtmlFontParameters(matcher.group(15), matcher
                    .group(18));
            }
            if (matcher.group(21) != null
                && matcher.group(24) != null
                && !matcher.group(21).equals("")
                && !matcher.group(24).equals(""))
            {
                setHtmlFontParameters(matcher.group(15), matcher
                    .group(18));
            }
            
            sb = new StringBuffer();
            matcher.appendReplacement(sb,"");
            Strlist.add(new Object[]{sb.toString(), null});
            
            int size = 10;
            if(Integer.valueOf(html_FontSize) != null)
                size = Integer.valueOf(html_FontSize).intValue();

            int encoding;
            if(html_FontEncoding.equalsIgnoreCase("UTF_16BE"))
            {
                encoding = 1;
            }
            else
            {
                encoding = 0;
            }
//            PdfFont font = PdfFont.create("Arial", size,
//                encoding);
            PdfFont font = PdfFont.create(html_FontName, size,
                encoding);
            
            StringBuffer cb = new StringBuffer(html_FontColor);
            String st = (String)cb.subSequence(0,2);
            int r = Integer.valueOf(st, 16).intValue();
            st = (String)cb.subSequence(2,4);
            int g = Integer.valueOf(st, 16).intValue();
            st = (String)cb.subSequence(4,6);
            int b = Integer.valueOf(st, 16).intValue();
            font.setColor(new Color(r,g,b));
            
            Strlist.add(new Object[]{matcher.group(26), font});
        }
        sb = new StringBuffer();
        matcher.appendTail(sb);
        Strlist.add(new Object[]{sb.toString(), null});
        return Strlist;
    }
    
    static boolean containsUserVariable(String str)
    {
        Matcher matcher;

        matcher = PAT_DATE.matcher(str);
        if (matcher.find())
        {
            return true;
        }
        matcher = PAT_PAGENO.matcher(str);
        if (matcher.find())
        {
            return true;
        }
        matcher = PAT_PAGECOUNT.matcher(str);
        if (matcher.find())
        {
            return true;
        }

        return false;
    }

    static String replaceUserDefVar(String str,
        PdfStdDocument d) throws IOException, PdfException
    {
        Matcher matcher;
        StringBuffer sb = new StringBuffer();
        
        matcher = PAT_DATE.matcher(str);
        String format;
        Calendar c;
        SimpleDateFormat dateFormat;
        
        while (matcher.find())
        {
            format = matcher.group(5);
            c = Calendar.getInstance();
            try
            {
                if (format == null || format.equals(""))
                {
                    dateFormat = new SimpleDateFormat();
                }
                else
                {
                    dateFormat = new SimpleDateFormat(format);
                }
                if (matcher.group(1) != null && !matcher.group(1).equals(""))
                {
                    if (isNotEscapeSequence(matcher.group(1)))
                    {
                        matcher.appendReplacement(sb, escapedString(matcher.
                            group(1)) + dateFormat.format(c.getTime()));
                    }
                    else
                    {
                        matcher.appendReplacement(sb, escapedString(matcher.
                            group(1))+ removeEscapeString(matcher.group(0), 
                                matcher.group(1).length()));
                    }
                }
                else
                {
                    matcher.appendReplacement(sb, dateFormat.format(
                        c.getTime()));
                }
            }
            catch (IllegalArgumentException ilae)
            {
                throw new PdfException(ilae.getMessage());
            }
            catch (NullPointerException ne)
            {
                throw new PdfException(ne.getMessage());
            }
        }
        matcher.appendTail(sb);
        
        matcher = PAT_PAGENO.matcher(sb);
        sb = new StringBuffer();
        while (matcher.find())
        {
            if (matcher.group(1) != null && !matcher.group(1).
                equals(""))
            {
                if (isNotEscapeSequence(matcher.group(1)))
                {
                    matcher.appendReplacement(sb, escapedString(
                        matcher.group(1)) + new Integer(
                            d.pageTree.pageCnt).toString());
                }
                else
                {
                    matcher.appendReplacement(sb, escapedString(
                        matcher.group(1)) + removeEscapeString(matcher.
                            group(0), matcher.group(1).length()));
                }
            }
            else
            {
                matcher.appendReplacement(sb, new Integer(
                    d.pageTree.pageCnt).toString());
            }
        }
        matcher.appendTail(sb);
        matcher = PAT_PAGECOUNT.matcher(sb);
        sb = new StringBuffer();
        while (matcher.find())
        {
            if (matcher.group(1) != null
                && !matcher.group(1).equals(""))
            {
                if (isNotEscapeSequence(matcher.group(1)))
                {
                    matcher.appendReplacement(sb, escapedString(matcher.
                        group(1)) + (new Integer(d.getPageCount())).toString());
                }
                else
                {
                    matcher.appendReplacement(sb, escapedString(matcher.
                        group(1)) + removeEscapeString(matcher.group(0),
                            matcher.group(1).length()));
                }
            }
            else
            {
                matcher.appendReplacement(sb, (new Integer(d
                    .getPageCount())).toString());
            }
        }
        matcher.appendTail(sb);
        
        return sb.toString();
    }   
    
    private static boolean isNotEscapeSequence(String str)
    {
        int len = str.length();
        
//        if(len % 2 == 0)
//            return true;
//            
//        return false;
        if(len - 1 >= 0)
            return false;
            
        return true;
    }
    
    private static String escapedString(String str)
    {
        StringBuffer ret = new StringBuffer();
        //double len = str.length()/2;
        double len = str.length()-1;
        for(int i = 0; i< len; i++)
        {
            ret = ret.append('~');
        }
        return ret.toString();
    }
    
    private static String removeEscapeString(String str, int n)
    {
        StringBuffer ret = new StringBuffer();      
        for(int i = n; i< str.length(); i++)
        {
            ret = ret.append(str.charAt(i));
        }
        return ret.toString();
    }
    
}
