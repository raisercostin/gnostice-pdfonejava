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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class represents a link annotation. It is used to link to a 
 * <a href="{@docRoot}/doc-files/glossary.htm#destination" target="_GnosticeGlossaryWindow" 
 * >destination</a> 
 * in the same document or in another document. It can also be used 
 * to specify actions for the viewer application to perform.
 * <p>
 * The location of a link annotation is specified using a
 * <code>PdfRect</code> object. However, the position and size of a 
 * <code>PdfRect</code> object is applied in a literal sense. As a 
 * result, <b>the actual position of the link annotation on a page 
 * is wholly dependent on whatever measurement unit that is currently 
 * applicable</b>. As this can cause serious shifts in position of  
 * the link annotation, care has to taken when reusing a link 
 * annotation in different places or in different situations.
 * </p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class PdfLinkAnnot extends PdfAnnot
{
    /**
     * Returns a 
     * <a href="{@docRoot}/doc-files/glossary.htm#remote_go_to_action" target="_GnosticeGlossaryWindow" 
     * >remote go-to action</a> that leads to page specified by
     * <code>pageNo</code>.
     * 
     * @param pageNo
     *            number of the page
     * @return a remote go-to action
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#getRemoteGoToInstance_int">example</a>.
     */
    public static PdfBookmark.RemoteGoTo getRemoteGoToInstance(
        int pageNo)
    {
        PdfBookmark.RemoteGoTo obj = new PdfBookmark.RemoteGoTo();
        obj.pageNo = pageNo;

        return obj;
    }

    /**
     * Returns a 
     * <a href="{@docRoot}/doc-files/glossary.htm#remote_go_to_action" target="_GnosticeGlossaryWindow" 
     * >remote go-to action</a> 
     * that leads to 
     * <a href="{@docRoot}/doc-files/glossary.htm#destination" target="_GnosticeGlossaryWindow" 
     * >destination</a> 
     * specified by <code>pos</code> and <code>fit</code> on page 
     * specified by <code>pageNo</code>. As this is a remote 
     * destination, <u>the coordinates of <code>pos</code> are 
     * specified assuming that position (0, 0) represents the 
     * bottom-left corner of the page</u>.
     *  
     * <br>&nbsp; 
     * 
     * <table border="1" cellpadding="5" summary="fit, pos, How
     * page is displayed">
     *  <tr>
     *   <th align="center" width="15%"><code>fit</code> </th>
     *   <th align="center" width="15%"><code>pos</code> </th>
     *   <th align="center" width="70%">How page is displayed </th>
     *  </tr>
     *  <tr>
     *   <td>{@link #FITH}</td>
     *   <td>
     *    vertical coordinate of top-left corner of window on the
     *    page
     *   </td>
     *   <td>
     *    <ul>
     *     <li>
     *      <code>pos</code> is positioned on the top edge of the
     *      window 
     *     </li>
     *     <li>
     *      Page magnification (zoom) factor is adjusted to tightly
     *      fit the entire width of the page inside the window 
     *     </li>
     *    </ul>
     *   </td>
     *  </tr>
     *  <tr>
     *   <td>{@link #FITBH}</td>
     *   <td>
     *    vertical coordinate of top-left corner of window on the
     *    page
     *   </td>
     *   <td>
     *    <ul>
     *     <li>
     *      <code>pos</code> is positioned on top edge of the window
     *     </li>
     *     <li>
     *      Page magnification (zoom) factor is adjusted to tightly
     *      fit the entire width of its 
     *      <a href="{@docRoot}/doc-files/glossary.htm#bounding_box" target="_GnosticeGlossaryWindow"
     *      >bounding box</a> 
     *      inside the window 
     *     </li>
     *    </ul>
     *   </td>
     *  </tr>
     *  <tr>
     *   <td>{@link #FITBV}</td>
     *   <td>
     *    horizontal coordinate of top-left corner of window on
     *    the page
     *   </td>
     *   <td>
     *    <ul>
     *     <li>
     *     <code>pos</code> is positioned on the left edge of the
     *     window 
     *     </li>
     *     <li>
     *      Page magnification (zoom) factor is adjusted to tightly
     *      fit the entire height of its 
     *      <a href="{@docRoot}/doc-files/glossary.htm#bounding_box" target="_GnosticeGlossaryWindow"
     *      >bounding box</a> 
     *      inside the window
     *     </li>
     *    </ul>
     *   </td>
     *  </tr>
     *  <tr>
     *   <td>{@link #FITV}</td>
     *   <td>
     *    horizontal coordinate of top-left corner of window on
     *    the page
     *   </td>
     *   <td>
     *    <ul>
     *     <li>
     *      <code>pos</code> is positioned on the left edge of the
     *      window
     *     </li>
     *     <li>
     *      Page magnification (zoom) factor is adjusted to tightly
     *      fit the entire height of the page inside the window 
     *     </li>
     *    </ul>
     *   </td>
     *  </tr>
     * </table>
     * 
     * @param pageNo
     *            number of the page
     * @param pos
     *            horizontal or vertical coordinate of top-left
     *            corner of window on the page
     * @param fit
     *            constant for specifying magnification (zoom) factor
     *            of the page
     * @return remote go-to action
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#getRemoteGoToInstance_int_double_int">example</a>.
     */
    public static PdfBookmark.RemoteGoTo getRemoteGoToInstance(
        int pageNo, double pos, int fit)
    {
        PdfBookmark.RemoteGoTo obj = new PdfBookmark.RemoteGoTo();
        obj.pageNo = pageNo;
        obj.fit = fit;
        obj.pos = pos;

        return obj;
    }

    /**
     * Returns a 
     * <a href="{@docRoot}/doc-files/glossary.htm#remote_go_to_action" target="_GnosticeGlossaryWindow"
     * >remote go-to action</a> that leads to page specified by 
     * <code>pageNo</code> and displays the page with its entire 
     * height and width tightly fit inside the window.
     * 
     * @param pageNo
     *            number of the page
     * @param fit
     *            constant for specifying magnification (zoom) factor 
     *            of the page (Always is {@link #FITB})
     * @return a remote go-to action
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#getRemoteGoToInstance_int_int">example</a>.
     */
    public static PdfBookmark.RemoteGoTo getRemoteGoToInstance(
        int pageNo, int fit)
    {
        PdfBookmark.RemoteGoTo obj = new PdfBookmark.RemoteGoTo();
        obj.pageNo = pageNo;
        obj.fit = fit;

        return obj;
    }

    /**
     * Returns a 
     * <a href="{@docRoot}/doc-files/glossary.htm#remote_go_to_action" target="_GnosticeGlossaryWindow" 
     * >remote go-to action</a> that leads to specified rectangle on 
     * page specified by <code>pageNo</code>.
     * 
     * @param pageNo
     *            number of the page
     * @param rect
     *            rectangle on the page
     * @return a remote go-to action
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#getRemoteGoToInstance_int_PdfRect">example</a>.
     */
    public static PdfBookmark.RemoteGoTo getRemoteGoToInstance(
        int pageNo, PdfRect rect)
    {
        PdfBookmark.RemoteGoTo obj = new PdfBookmark.RemoteGoTo();
        obj.pageNo = pageNo;

        obj.left = rect.x;
        obj.top = rect.height;
        obj.right = rect.width;
        obj.bottom = rect.y;

        return obj;
    }

    /**
     * Returns a 
     * <a href="{@docRoot}/doc-files/glossary.htm#remote_go_to_action" target="_GnosticeGlossaryWindow" 
     * >remote go-to action</a> that leads to specified rectangular 
     * area on specified page. 
     * 
     * @param pageNo
     *            number of the page
     * @param left
     *            distance from left edge of the page to the left
     *            edge of the rectangular area
     * @param bottom
     *            distance from bottom edge of the page to the
     *            bottom edge of the rectangular area
     * @param right
     *            distance from right edge of the page to the left
     *            edge of the rectangular area
     * @param top
     *            distance from top edge of the page to the bottom
     *            edge of the rectangular area
     * @return remote go-to action
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#getRemoteGoToInstance_int_double_double_double_double">example</a>.
     */
    public static PdfBookmark.RemoteGoTo getRemoteGoToInstance(
        int pageNo, double left, double bottom, double right,
        double top)
    {
        PdfBookmark.RemoteGoTo obj = new PdfBookmark.RemoteGoTo();
        obj.pageNo = pageNo;

        obj.left = left;
        obj.top = top;
        obj.right = right;
        obj.bottom = bottom;

        return obj;
    }

    /**
     * Highlight mode for displaying contents of annotation rectangle
     * without any change from their normal appearance.
     * 
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#HIGHLIGHT_MODE_NONE">example</a>.
     */
    public static final int HIGHLIGHT_MODE_NONE = 0;
    
    /**
     * Highlight mode for displaying contents of annotation rectangle
     * inverted.
     * 
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#HIGHLIGHT_MODE_INVERT">example</a>.
     */
    public static final int HIGHLIGHT_MODE_INVERT = 1;
    
    /**
     * Highlight mode for displaying border of annotation rectangle
     * inverted.
     * 
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#HIGHLIGHT_MODE_OUTLINE">example</a>.
     */
    public static final int HIGHLIGHT_MODE_OUTLINE = 2;

    /**
     * Highlight mode for displaying contents of annotation rectangle
     * as if they were pushed from below the surface of the page.
     * 
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#HIGHLIGHT_MODE_PUSH">example</a>.
     */
    public static final int HIGHLIGHT_MODE_PUSH = 3;
    
    /**
     * Constant for adjusting magnification (zoom) factor of a page 
     * to tightly fit its entire width inside a window.
     */
    public static final int FITH = 0;

    /**
     * Constant for adjusting magnification (zoom) factor of a page  
     * to tightly fit entire width of its  
     * <a href="{@docRoot}/doc-files/glossary.htm#bounding_box" target="_GnosticeGlossaryWindow"
     * >bounding box</a> 
     * inside a window.
     */
    public static final int FITBH = 1;

    /**
     * Constant for adjusting magnification (zoom) factor of a page
     * to tightly fit entire height of its 
     * <a href="{@docRoot}/doc-files/glossary.htm#bounding_box" target="_GnosticeGlossaryWindow"
     * >bounding box</a> 
     * inside a window.
     */
    public static final int FITBV = 2;
    
    /**
     * Constant for adjusting magnification (zoom) factor of a page
     * to tightly fit its entire height inside a window.
     */
    public static final int FITV =3;

    /**
     * Constant for adjusting magnification (zoom) factor of a page  
     * to tightly fit entire height and width of its 
     * <a href="{@docRoot}/doc-files/glossary.htm#bounding_box" target="_GnosticeGlossaryWindow"
     * >bounding box</a> 
     * inside a window.
     */
    public static final int FITB =4;
    
    private int highlightMode;

    private PdfBookmark bm;
    
    /**
     * Zero-argument default constructor.
     * 
     * @version 1.0
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#PdfLinkAnnot">example</a>.
     */
    public PdfLinkAnnot()
    {
        bm = new PdfBookmark();
        this.annotType = ANNOT_TYPE_LINK;
    }
    
    /**
     * Constructs a link annotation with specified annotation
     * rectangle, subject, contents, title, flags, annotation
     * rectangle color, and highlight mode.
     * 
     * @param r
     *            annotation rectangle
     * @param subject
     *            text to be used as the annotation's subject
     * @param contents
     *            text to be used as the annotation's contents
     * @param title
     *            text to be displayed in the annotation's title bar
     * @param flags
     *            flag or combined value of flags representing
     *            characteristics of the annotation
     * @param c
     *            color of the annotation rectangle
     * @param highlightMode
     *            highlight mode
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#PdfLinkAnnotation_PdfRect_String_String_String_int_Color_int">example</a>.
     */
    public PdfLinkAnnot(PdfRect r, String subject, String contents,
        String title, int flags, Color c, int highlightMode)
    {
        super(r, subject, contents, title, flags, c);
        this.highlightMode = highlightMode;
        bm = new PdfBookmark();
        this.annotType = ANNOT_TYPE_LINK;
    }

    /**
     * Constructs a link annotation with specified annotation
     * rectangle, subject, contents, and title.
     * 
     * @param r
     *            annotation rectangle
     * @param subject
     *            text to be used as the annotation's subject
     * @param contents
     *            text to be used as the annotation's contents
     * @param title
     *            text to be displayed in the annotation's title bar
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#PdfLinkAnnot_PdfRect_String_String_String">example</a>.
     */
    public PdfLinkAnnot(PdfRect r, String subject, String contents,
        String title)
    {
        super(r, subject, contents, title);
        bm = new PdfBookmark();
        this.annotType = ANNOT_TYPE_LINK;
    }

    /**
     * Constructs a link annotation with specified annotation
     * rectangle, subject, contents, title, and annotation rectangle
     * color.
     * 
     * @param r
     *            annotation rectangle
     * @param subject
     *            text to be used as the annotation's subject
     * @param contents
     *            text to be used as the annotation's contents
     * @param title
     *            text to be displayed in the annotation's title bar
     * @param c
     *            color of the annotation rectangle
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#PdfLinkAnnot_PdfRect_String_String_String_Color">example</a>.
     */
    public PdfLinkAnnot(PdfRect r, String subject, String contents,
        String title, Color c)
    {
        super(r, subject, contents, title, c);
        bm = new PdfBookmark();
        this.annotType = ANNOT_TYPE_LINK;
    }
    
    /**
     * Constructs a link annotation with specified annotation
     * rectangle, subject, contents, title, and flags.
     * 
     * @param r
     *            annotation rectangle
     * @param subject
     *            text to be used as the annotation's subject
     * @param contents
     *            text to be used as the annotation's contents
     * @param title
     *            text to be displayed in the annotation's title bar
     * @param flags
     *            flag or combined value of flags representing
     *            characteristics of the annotation
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#PdfLinkAnnot_PdfRect_String_String_String_int">example</a>.
     */
    public PdfLinkAnnot(PdfRect r, String subject, String contents,
        String title, int flags)
    {
        super(r, subject, contents, title, flags);
        bm = new PdfBookmark();
        this.annotType = ANNOT_TYPE_LINK;
    }
    
    /**
     * Constructs a link annotation with specified annotation
     * rectangle, subject, contents, title, flags, and annotation
     * rectangle.
     * 
     * @param r
     *            annotation rectangle
     * @param subject
     *            text to be used as the annotation's subject
     * @param contents
     *            text to be used as the annotation's contents
     * @param title
     *            text to be displayed in the annotation's title bar
     * @param flags
     *            flag or combined value of flags representing
     *            characteristics of the annotation
     * @param c
     *            color of the annotation rectangle
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#PdfLinkAnnot_PdfRect_String_String_String_int_Color">example</a>.
     */
    public PdfLinkAnnot(PdfRect r, String subject, String contents,
        String title, int flags, Color c)
    {
        super(r, subject, contents, title, flags, c);
        bm = new PdfBookmark();
        this.annotType = ANNOT_TYPE_LINK;
    }
    
    /**
     * Constructs a link annotation with specified annotation
     * rectangle and flags.
     * 
     * @param r
     *            annotation rectangle
     * @param flags
     *            flag or combined value of flags representing
     *            characteristics of the annotation
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#PdfLinkAnnot_PdfRect_int">example</a>.
     */
    public PdfLinkAnnot(PdfRect r, int flags)
    {
        super(r, flags);
        bm = new PdfBookmark();
        this.annotType = ANNOT_TYPE_LINK;
    }
    
    /**
     * Constructs a link annotation with specified annotation
     * rectangle, flags, and annotation rectangle color.
     * 
     * @param r
     *            annotation rectangle
     * @param flags
     *            flag or combined value of flags representing
     *            characteristics of the annotation
     * @param c
     *            color of the annotation rectangle
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#PdfLinkAnnot_PdfRect_int_Color">example</a>.
     */
    public PdfLinkAnnot(PdfRect r, int flags, Color c)
    {
        super(r, flags, c);
        bm = new PdfBookmark();
        this.annotType = ANNOT_TYPE_LINK;
    }
    
    /**
     * Constructs a link annotation with specified annotation
     * rectangle and annotation rectangle color.
     * 
     * @param r
     *            annotation rectangle
     * @param c
     *            color of the annotation rectangle
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#PdfLinkAnnot_PdfRect_Color">example</a>.
     */
    public PdfLinkAnnot(PdfRect r, Color c)
    {
        super(r, c);
        bm = new PdfBookmark();
        this.annotType = ANNOT_TYPE_LINK;
    }
    
    /**
     * Returns constant identifying this link annotation's highlight
     * mode.
     * 
     * @return constant identifying the highlight mode
     * @since 1.0
     * @see #setHighlightMode(int)
     */
    public synchronized int getHighlightMode()
    {
        return highlightMode;
    }

    /**
     * Specifies this link annotation's highlight mode.
     * 
     * @param highlightMode
     *            constant specifying the highlight mode
     * @since 1.0
     */
    public synchronized void setHighlightMode(int highlightMode)
    {
        this.highlightMode = highlightMode;
    }

    /**
     * Adds a  
     * <a href="{@docRoot}/doc-files/glossary.htm#go_to_action" target="_GnosticeGlossaryWindow"
     * >go-to action</a>
     * to this link annotation linking it to  
     * <a href="{@docRoot}/doc-files/glossary.htm#destination" target="_GnosticeGlossaryWindow"
     * >destination</a> 
     * specified by position (<code>left</code>, <code>top</code>), 
     * page <code>pageNo</code>, and magnification factor 
     * <code>zoom</code>.
     * 
     * @param pageNo
     *            destination page number
     * @param left
     *            x-coordinate of the top-left corner of the 
     *            window on the page
     * @param top
     *            y-coordinate of the top-left corner of the 
     *            window on the page
     * @param zoom
     *            magnification (zoom) factor to be applied when 
     *            displaying the page
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#addActionGoTo_int_double_double_double">example</a>.
     */
    public synchronized void addActionGoTo(int pageNo, double left,
        double top, double zoom) throws PdfException
    {
        bm.addActionGoto(pageNo, left, top, zoom);
    }

    /**
     * Adds a 
     * <a href="{@docRoot}/doc-files/glossary.htm#go_to_action" target="_GnosticeGlossaryWindow"
     * >go-to action</a> to this link annotation linking it to page 
     * specified by <code>pageNo</code>.
     * 
     * @param pageNo
     *            number of the page
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#addActionGoTo_int">example</a>.
     */
    public synchronized void addActionGoTo(int pageNo)
        throws PdfException
    {
        bm.addActionGoto(pageNo);
    }

    /**
     * Adds a
     * <a href="{@docRoot}/doc-files/glossary.htm#go_to_action" target="_GnosticeGlossaryWindow"
     * >go-to action</a> to this link annotation linking it to  
     * <a href="{@docRoot}/doc-files/glossary.htm#destination" target="_GnosticeGlossaryWindow"
     * >destination</a> specified by <code>pos</code> 
     * and <code>fit</code> on page specified by <code>pageNo</code>.
     * 
     * <br>&nbsp;
     * 
     * <table border="1" cellpadding="5" summary="fit, pos, How
     * page is displayed">
     *  <tr>
     *   <th align="center" width="15%"><code>fit</code> </th>
     *   <th align="center" width="15%"><code>pos</code> </th>
     *   <th align="center" width="70%">How page is displayed </th>
     *  </tr>
     *  <tr>
     *   <td>{@link #FITH}</td>
     *   <td>
     *    vertical coordinate of top-left corner of window on the
     *    page
     *   </td>
     *   <td>
     *    <ul>
     *     <li>
     *      <code>pos</code> is positioned on the top edge of the
     *      window 
     *     </li>
     *     <li>
     *      Page magnification (zoom) factor is adjusted to tightly
     *      fit the entire width of the page inside the window 
     *     </li>
     *    </ul>
     *   </td>
     *  </tr>
     *  <tr>
     *   <td>{@link #FITBH}</td>
     *   <td>
     *    vertical coordinate of top-left corner of window on the
     *    page
     *   </td>
     *   <td>
     *    <ul>
     *     <li>
     *      <code>pos</code> is positioned on top edge of the window
     *     </li>
     *     <li>
     *      Page magnification (zoom) factor is adjusted to tightly
     *      fit the entire width of its 
     *      <a href="{@docRoot}/doc-files/glossary.htm#bounding_box" target="_GnosticeGlossaryWindow"
     *      >bounding box</a> 
     *      inside the window 
     *     </li>
     *    </ul>
     *   </td>
     *  </tr>
     *  <tr>
     *   <td>{@link #FITBV}</td>
     *   <td>
     *    horizontal coordinate of top-left corner of window on
     *    the page
     *   </td>
     *   <td>
     *    <ul>
     *     <li>
     *     <code>pos</code> is positioned on the left edge of the
     *     window 
     *     </li>
     *     <li>
     *      Page magnification (zoom) factor is adjusted to tightly
     *      fit the entire height of its 
     *      <a href="{@docRoot}/doc-files/glossary.htm#bounding_box" target="_GnosticeGlossaryWindow"
     *      >bounding box</a> 
     *      inside the window
     *     </li>
     *    </ul>
     *   </td>
     *  </tr>
     *  <tr>
     *   <td>{@link #FITV}</td>
     *   <td>
     *    horizontal coordinate of top-left corner of window on
     *    the page
     *   </td>
     *   <td>
     *    <ul>
     *     <li>
     *      <code>pos</code> is positioned on the left edge of the
     *      window
     *     </li>
     *     <li>
     *      Page magnification (zoom) factor is adjusted to tightly
     *      fit the entire height of the page inside the window 
     *     </li>
     *    </ul>
     *   </td>
     *  </tr>
     * </table> 
     * 
     * @param pageNo
     *            number of the page
     * @param pos
     *            horizontal or vertical coordinate of top-left 
     *            corner of the window on the page 
     * @param fit
     *            constant for specifying magnification (zoom) factor 
     *            of the page
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#addActionGoTo_int_double_int">example</a>.
     */
    public synchronized void addActionGoTo(int pageNo, double pos,
        int fit)  throws PdfException
    {
    	bm.addActionGoto(pageNo, pos, fit); 
    }
    
    /**
     * Adds a 
     * <a href="{@docRoot}/doc-files/glossary.htm#go_to_action" target="_GnosticeGlossaryWindow"
     * >go-to action</a> to this link annotation linking it to specified 
     * rectangular area on specified page.
     * 
     * @param pageNo
     *            number of the page
     * @param left
     *            distance from the left edge of the page to the left
     *            edge of the annotation rectangle
     * @param bottom
     *            distance from the top edge of the page to the bottom
     *            edge of the annotation rectangle
     * @param right
     *            distance from the left edge of the page to the right
     *            edge of the annotation rectangle
     * @param top
     *            distance from the top edge of the page to the top
     *            edge of the annotation rectangle
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#addActionGoTo_int_double_double_double_double">example</a>.
     */
    public synchronized void addActionGoTo(int pageNo, double left,
        double bottom, double right, double top) throws PdfException
    {
    	bm.addActionGoto(pageNo, left, bottom, right, top); 
    }    

    /**
     * Adds a 
     * <a href="{@docRoot}/doc-files/glossary.htm#remote_go_to_action" target="_GnosticeGlossaryWindow" 
     * >remote go-to action</a> to this link annotation.
     * 
     * @param pdfFilePath
     *            pathname of the PDF file containing the action's
     *            destination
     * @param rGoTo
     *            remote go-to action
     * @param newWindow
     *            whether a new window is to be opened to perform the
     *            remote go-to action
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#addActionRemoteGoTo">example</a>.
     */
    public synchronized void addActionRemoteGoTo(String pdfFilePath,
        PdfBookmark.RemoteGoTo rGoTo, boolean newWindow)
        throws PdfException
    {
        bm.addActionRemoteGoTo(pdfFilePath, rGoTo, newWindow);
    }    
    
    /**
     * Adds action to this link annotation making it perform 
     * specified named action.
     * 
     * @param actionType
     *            named action
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#addActionNamed">example</a>.
     */
    public synchronized void addActionNamed(int actionType) 
    {
    	bm.addActionNamed(actionType); 
    }    

    /**
     * Adds an action to this link annotation making it launch
     * specified application, or open or print specified document.
     * 
     * @param applicationToLaunch
     *            pathname of the application or document
     * @param print
     *            whether the document is to be printed 
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#addActionLaunch">example</a>.
     */
    public synchronized void addActionLaunch(
        String applicationToLaunch, boolean print) 
    {
    	bm.addActionLaunch(applicationToLaunch, print);
    } 
    
    /**
     * Adds an action to this link annotation making it launch
     * specified Uniform Resource Identifier (URI).
     * 
     * @param uri
     *            Uniform Resource Identifier (URI) that needs to be
     *            launched
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#addActionURI">example</a>.
     */
    public synchronized void addActionURI(String uri) 
    {
    	bm.addActionURI(uri); 
    }    
    
    /**
     * Adds an action to this annotation making it execute specified
     * Javascript script.
     * 
     * @param script
     *            Javascript script that needs to be executed
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#addActionJavaScript">example</a>.
     */
    public synchronized void addActionJavaScript(String script) 
    {
    	bm.addActionJavaScript(script); 
    }    
    
    /**
     * Removes action of specified type that was first added to this 
     * link annotation.
     * 
     * @param type
     *            constant specifying the action type
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfAction
     */
    public synchronized void removeAction(int type)
        throws PdfException
    {
        bm.removeAction(type);
    }

    /**
     * Removes all actions of specified type that were added to this 
     * link annotation.
     * 
     * @param type
     *            constant specifying the action type
     * @throws PdfException
     *            if an illegal argument is supplied.
     * @since 1.0
     * @see PdfAction
     */
    public synchronized void removeAllActions(int type)
        throws PdfException
    {
    	bm.removeAllActions(type);
    }

    /**
     * Removes all actions that were added to this link annotation.
     * 
     * @since 1.0
     */
    public synchronized void removeAllActions()
    {
    	bm.removeAllActions();
    }
    
    void applyPropertiesFrom(PdfDict annotDict, PdfStdPage page)
        throws IOException, PdfException
    {
        annotDict.dictMap.remove(new PdfName(PDF_P));
        unknownAttributes = new HashMap();
        Map annotMap = annotDict.getMap();
        Iterator iter = annotMap.keySet().iterator();
        String name;
        PdfObject key, value, currObj;

        while (iter.hasNext())
        {
            key = (PdfObject) iter.next();
            currObj = (PdfObject) annotMap.get(key);
            value = page.originDoc.reader.getObject(currObj);
            if (value instanceof PdfNull)
            {
                continue;
            }
            name = ((PdfName) key).getString();

            if (name.equals(Usable.PDF_CONTENTS))
            {
                setContents(((PdfString) value).getString());
            }
            else if (name.equals(Usable.PDF_ANNOT_SUBJECT))
            {
                setSubject(((PdfString) value).getString());
            }
            else if (name.equals(Usable.PDF_ANNOT_NAME))
            {
                setAnnotName(((PdfString) value).getString());
            }
            else if (name.equals(Usable.PDF_F))
            {
                setFlags(((PdfInteger) value).getInt());
            }
            else if (name.equals(Usable.PDF_T))
            {
                setTitle(((PdfString) value).getString());
            }
            else if (name.equals(Usable.PDF_RECT))
            {
                if (value instanceof PdfArray)
                {
                    setRect(new PdfRect((PdfArray) value,
                        page.pageHeight));
                }
            }
            else if (name.equals(Usable.PDF_COLOR))
            {
                ArrayList arrColor = (ArrayList) ((PdfArray) value)
                    .getList();
                double r = 0, g = 0, b = 0;

                if (arrColor.get(0) instanceof PdfNumber)
                {
                    r = ((PdfNumber) arrColor.get(0)).getVal();
                }
                if (arrColor.get(1) instanceof PdfNumber)
                {
                    g = ((PdfNumber) arrColor.get(1)).getVal();
                }
                if (arrColor.get(2) instanceof PdfNumber)
                {
                    b = ((PdfNumber) arrColor.get(2)).getVal();
                }
                Color col = new Color((float) r, (float) g, (float) b);
                setColor(col);
            }
            else if (name.equals(Usable.PDF_BS))
            {
                showRect = true;
                if (value instanceof PdfDict)
                {
                    Iterator bs = ((PdfDict) value).getMap().keySet()
                        .iterator();
                    PdfObject bsKey, bsValue;
                    String bsName;

                    while (bs.hasNext())
                    {
                        bsKey = (PdfObject) bs.next();
                        bsValue = (PdfObject) ((PdfDict) value)
                            .getMap().get(bsKey);
                        bsValue = page.originDoc.reader
                            .getObject(bsValue);

                        bsName = ((PdfName) bsKey).getString();
                        if (bsName.equals(Usable.PDF_W))
                        {
                            if (bsValue instanceof PdfNumber)
                            {
                                setBorderWidth(((PdfNumber) bsValue)
                                    .getVal());
                            }
                        }
                        else if (bsName.equals(Usable.PDF_S))
                        {
                            String style = ((PdfName) bsValue)
                                .getString();
                            int sty = 0;
                            if (style.equals("D"))
                            {
                                sty = 1;
                            }
                            else if (style.equals("B"))
                            {
                                sty = 2;
                            }
                            else if (style.equals("I"))
                            {
                                sty = 3;
                            }
                            else if (style.equals("U"))
                            {
                                sty = 4;
                            }
                            setBorderStyle(sty);
                        }
                        else if (bsName.equals(Usable.PDF_D))
                        {
                            if (bsValue instanceof PdfArray)
                            {
                                List l = ((PdfArray) bsValue)
                                    .getList();
                                int limit = l.size();
                                int[] d = new int[limit];
                                for (int i = 0; i < limit; ++i)
                                {
                                    d[i] = ((PdfInteger) l.get(i))
                                        .getInt();
                                }
                                setDashPattern(d);
                            }
                        }
                    }
                }
            }
            else
            {
                if ( !knownAttributes.containsKey(name))
                {
                    unknownAttributes.put(key,
                        value.objNumber == 0 ? value : currObj);
                }
            }
        }
        annotDict.setObjectNumber(0);
        this.dict = annotDict;
        readActions(page.originDoc);
    }

    protected PdfAnnot encode(PdfStdPage p) throws PdfException
    {
        super.encode(p);

        HashMap annotMap = (HashMap)dict.getMap();
        annotMap.put(new PdfName(Usable.PDF_SUBTYPE), new PdfName(
            Usable.PDF_LINKANNOT));

        if (showRect)
        {
            Map m = new HashMap();
            m.put(new PdfName(Usable.PDF_TYPE), new PdfName(
                Usable.PDF_BORDER));
            m.put(new PdfName(Usable.PDF_W), new PdfFloat(
                this.borderWidth));
            String style = "S";
            switch (getBorderStyle())
            {
                case 1:
                    style = "D";
                    break;
                case 2:
                    style = "B";
                    break;
                case 3:
                    style = "I";
                    break;
                case 4:
                    style = "U";
                    break;
            }
            m.put(new PdfName(Usable.PDF_S), new PdfName(style));
            if (dashPattern != null)
            {
                m.put(new PdfName(Usable.PDF_D), new PdfArray(
                    this.dashPattern));
            }
            annotMap.put(new PdfName(Usable.PDF_BS), new PdfDict(m));
        }
        
        String mode;
        switch (this.getHighlightMode())
        {
            case HIGHLIGHT_MODE_NONE:
                mode = "N";
                break;

            case HIGHLIGHT_MODE_INVERT:
                mode = "I";
                break;

            case HIGHLIGHT_MODE_OUTLINE:
                mode = "O";
                break;

            case HIGHLIGHT_MODE_PUSH:
                mode = "P";
                break;

            default:
                mode = "N";
                break;
        }
        annotMap.put(new PdfName("H"), new PdfName(mode)); 

        //Actions added in set()
        return this;
    }
    
    protected void set(PdfStdDocument originDoc, PdfStdDocument d)
        throws IOException, PdfException
    {
        if (unknownAttributes != null)
        {
            for (Iterator iter = unknownAttributes.keySet()
                .iterator(); iter.hasNext();)
            {
                PdfObject key = (PdfObject) iter.next();
                PdfObject value = (PdfObject) unknownAttributes
                    .get(key);
                if (value != null)
                {
                    d.updateIndirectRefs(originDoc, value, true);
                }
            }
        }

        bm.setActions(d);
        
        final PdfName A = new PdfName(Usable.PDF_A);
        PdfObject obj = bm.dict.getValue(A);
        if (obj != null)
        {
            this.dict.getMap().put(A, obj);
        }
    }

    void readActions(PdfStdDocument originDoc)
        throws IOException, PdfException
    {
        PdfObject action = dict.getValue(PdfBookmark.ACTION);
        PdfObject dest = dict.getValue(PdfBookmark.DEST);
        if (action == null)
        {
            dest = originDoc.getDestArray(originDoc.reader
                .getObject(dest));
            if (dest != null)
            {
                bm.addToActionList(originDoc
                    .processDestination((PdfArray) dest));
            }
        }
        else
        {
            do
            {
                action = originDoc.reader.getObject(action);
                dest = ((PdfDict) action)
                    .getValue(new PdfName(PDF_D));
                dest = originDoc.getDestArray(originDoc.reader
                    .getObject(dest));
                if (dest != null)
                {
                    ((PdfDict) action).setValue(new PdfName(PDF_D),
                        dest);
                }
                bm.addToActionList((PdfDict) action);
                action = ((PdfDict) action)
                    .getValue(PdfBookmark.NEXT);
            } while (action != null);
        }
    }
    
    void updatePageRefs(PdfStdDocument d) throws PdfException
    {
        final PdfName D = new PdfName(Usable.PDF_D);
        final PdfName S = new PdfName(Usable.PDF_S);
        PdfDict actionDict;
        PdfArray arr;
        PdfName actionType;
        if (bm.actionList != null)
        {
            int size =  bm.actionList.size();
            for (int i = 0; i < size; i++)    
            {
                actionDict = (PdfDict) bm.actionList.get(i);
                arr = (PdfArray) actionDict.getValue(D);
                actionType = (PdfName) actionDict.getValue(S);
                if (arr != null
                    && (!actionType.getString().equals(
                        Usable.PDF_REMOTEGOTO_ACTION)))
                {
                    List l = arr.getList();
                    PdfNode page = d.pageTree.getPage(bm.pageNo);
                    int ref = page.getDict().getObjectNumber();
                    l.set(0, new PdfIndirectReference(ref, 0));
                }
            }
        }
    }

	void write(PdfStdDocument d) throws IOException, PdfException
	{
        super.write(d);
        updatePageRefs(d);
		bm.writeActions(d);
		bm.resetPageRefs();
	}
}