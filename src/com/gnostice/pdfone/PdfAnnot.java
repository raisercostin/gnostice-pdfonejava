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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This is an abstract class representing annotations.
 * <p>
 * An "annotation rectangle" defines the position and size of an
 * annotation on a page. This rectangle may be specified by
 * coordinates of its top-left corner, width, and height. An 
 * annotation rectangle has to be compulsorily specified for an
 * annotation. Even when a default constructor is used to create an
 * annotation, an annotation rectangle should be specified
 * subsequently. These specifications are applied in a literal sense. 
 * As a result, <b>the actual position and size of an annotation on a 
 * page is wholly dependent on whatever measurement unit that is 
 * currently applicable</b>.
 * </p>
 * <p>
 * Adding the same annotation to pages with different default
 * measurement units, for example, will cause a shift in 
 * their relative positions.
 * </p>
 * <pre>
        PdfWriter writer = PdfWriter.fileWriter(
            "PdfAnnot_INTRO_Example.pdf");
        PdfDocument document = new PdfDocument(writer);
        
        PdfPage page_point = new PdfPage();
        PdfPage page_pixel = new PdfPage();
        
       <span style="background-color: lavender;"> page_pixel.setMeasurementUnit(PdfMeasurement.MU_PIXELS); </span>
       <span style="background-color: lavender;"> page_point.setMeasurementUnit(PdfMeasurement.MU_POINTS); </span>
        
       <span style="color: green;"> // Creates a text annotation object </span>
        PdfTextAnnot tAnnot = new PdfTextAnnot();

       <span style="color: green;"> // Specifies properties of the annotation </span>
        tAnnot.setColor(Color.PINK);
        tAnnot.setIconName(PdfTextAnnot.ICON_KEY);        
        tAnnot.setTitle("Attn: All");
        tAnnot.setSubject("EOD Deliverables");
        tAnnot.setContents(
            "Please check this document. It has some errors.");
        tAnnot.setFlags(PdfAnnot.FLAG_PRINT);        
        tAnnot.setOpen(true);
        
       <span style="color: green;"> // Specifies the annotation rectangle of the annotation </span>  
       <span style="background-color: lavender;"> tAnnot.setRect(300, 200, 100, 100); </span>
        
       <span style="color: green;"> // Adds clones of the same annotation to pages with 
        // different measurement units 
        // Makes them appear in different positions, relative to each other</span>
       <span style="background-color: lavender;"> page_pixel.addAnnotation(tAnnot); </span>
       <span style="background-color: lavender;"> page_point.addAnnotation((PdfAnnot) tAnnot.clone()); </span>
        
        document.add(page_pixel);
        document.add(page_point);

        document.setOpenAfterSave(true);
        document.write();
        writer.dispose();
 * </pre>
 * 
 * @since 1.0
 * @version 1.0
 */
public abstract class PdfAnnot implements Cloneable, Usable
{
    /**
     * Flag to indicate whether an annotation is displayed using its 
     * appearance stream if it does not belong to one of the standard 
     * annotation types and if no 
     * <a href="{@docRoot}/doc-files/glossary.htm#annotation_handler" target="_GnosticeGlossaryWindow"
     * >annotation handler</a> is specified.
     */
    public static final int FLAG_INVISIBLE = 1;
    
    /**
     * Flag to indicate whether an annotation is displayed, printed, 
     * or allowed to interact with the user, irrespective of the 
     * annotation's type or the existence of an 
     * <a href="{@docRoot}/doc-files/glossary.htm#annotation_handler" target="_GnosticeGlossaryWindow" 
     * >annotation handler</a>. Used where space is limited and 
     * annotations are alternatively hidden and displayed in combination with
     * <a href="{@docRoot}/doc-files/glossary.htm#appearance_stream" target="_GnosticeGlossaryWindow"
     * >appearance streams</a>.
     */
    public static final int FLAG_HIDDEN = 2;
    
    /**
     * Flag to indicate whether an annotation is printed when the 
     * page is printed.
     */
    public static final int FLAG_PRINT = 4;
    
    /**
     * Flag to indicate whether an annotation is magnified in sync  
     * with zoom level of its page.
     */
    public static final int FLAG_NO_ZOOM = 8;
    
    /**
     * Flag to indicate whether an annotation is rotated in sync 
     * with rotation of its page.   
     */
    public static final int FLAG_NO_ROTATE = 16;
    
    /**
     * Flag to indicate whether an annotation is displayed or allowed
     * to interact with the user. Has no effect on whether the annotation 
     * is printed, which is solely determined by the 
     * {@link #FLAG_PRINT} flag.
     * 
     * @see #FLAG_TOGGLE_NO_VIEW
     */
    public static final int FLAG_NO_VIEW = 32;
    
    /**
     * Flag to indicate whether an annotation is allowed to interact 
     * with the user. 
     */
    public static final int FLAG_READONLY = 64;
    
    /**
     * Flag to indicate whether an annotation is not allowed to be  
     * deleted or have its properties modified by the user.
     */
    public static final int FLAG_LOCKED = 128;
    
    /**
     * Flag to indicate whether the implementation of the 
     * {@link #FLAG_NO_VIEW} of an annotation is to be inverted.
     */
    public static final int FLAG_TOGGLE_NO_VIEW = 256;

    /**
     * Annotation border style with which the border is drawn as 
     * a solid rectangle.
     */
    public static final int BORDERSTYLE_SOLID = 0;
    
    /**
     * Annotation border style with which the border is drawn as 
     * a dashed rectangle. 
     */
    public static final int BORDERSTYLE_DASHED = 1;
    
    /**
     * Annotation border style with which the border is drawn as   
     * an embossed rectangle that appears to have risen above the 
     * surface of the page. 
     */
    public static final int BORDERSTYLE_BEVELED = 2;
    
    /**
     * Annotation border style with which the border is drawn as 
     * an engraved rectangle that appears to have sunken below the 
     * surface of the page.
     */
    public static final int BORDERSTYLE_INSET = 3;
    
    /**
     * Annotation border style with which the annotation rectangle's
     * bottom line is drawn as the border.
     */
    public static final int BORDERSTYLE_UNDERLINE = 4;
    
    static final int ANNOT_TYPE_ALL = -1;
    
    static final int ANNOT_TYPE_WIDGET = 16384;
    
    public static final int ANNOT_TYPE_FILE_ATTACHMENT = 1;
    
    public static final int ANNOT_TYPE_FREE_TEXT = 2;
    
    public static final int ANNOT_TYPE_INK = 4;
    
    public static final int ANNOT_TYPE_LINE = 8;
    
    public static final int ANNOT_TYPE_MARKUP = 16;
    
    public static final int ANNOT_TYPE_POLYLINE = 32;
    
    public static final int ANNOT_TYPE_POLYGON = 64;
    
    public static final int ANNOT_TYPE_POPUP = 128;
    
    public static final int ANNOT_TYPE_STAMP = 256;
    
    public static final int ANNOT_TYPE_TEXT = 512;
    
    public static final int ANNOT_TYPE_LINK = 1024;
    
    public static final int ANNOT_TYPE_CARET = 2048;

    public static final int ANNOT_TYPE_SQUARE = 4096;
    
    public static final int ANNOT_TYPE_CIRCLE = 8192;
    
    static HashMap knownAttributes;

    int annotType;
    
    boolean isWritten;
    
    PdfRect rect;
    
    int borderStyle;
    
    double borderWidth;
    
    int[] dashPattern;
    
    boolean showRect;

    String subject;

    String contents;
    
    String annotName;
    
    String title;

    int flags;
    
    Color color;
    
    double left;
    
    double top;
    
    double right;
    
    double bottom;
    
    boolean considerPageMargins;
    
    PdfDict dict;
    
    int mode; //reading or writing 
    
    /* This is present here due to Link Annot */
    HashMap unknownAttributes;

    static
    {
        knownAttributes = new HashMap();
        knownAttributes.put(PDF_TYPE, PdfNull.DUMMY);
        knownAttributes.put(PDF_SUBTYPE, PdfNull.DUMMY);
        knownAttributes.put(PDF_CONTENTS, PdfNull.DUMMY);
        knownAttributes.put(PDF_RECT, PdfNull.DUMMY);
        
        knownAttributes.put(PDF_ANNOT_NAME, PdfNull.DUMMY);
        knownAttributes.put(PDF_M, PdfNull.DUMMY);
        knownAttributes.put(PDF_F, PdfNull.DUMMY);
        knownAttributes.put(PDF_BS, PdfNull.DUMMY);
        knownAttributes.put(PDF_C, PdfNull.DUMMY);
        knownAttributes.put(PDF_T, PdfNull.DUMMY);
        knownAttributes.put(PDF_ANNOT_SUBJECT, PdfNull.DUMMY);
        
        knownAttributes.put(PDF_PARENT, PdfNull.DUMMY);
    }

    static PdfArray normalizeColor(Color c)
    {
        if (c == null)
        {
            return new PdfArray(new int[] {});
        }

        float[] components = c.getComponents(null);
        float[] colorCompts = new float[components.length-1];
        for (int i = 0; i < components.length-1; ++i)
        {
            components[i] = Math.min(1,  components[i]);
            components[i] = Math.max(0,  components[i]);
            
            colorCompts[i] = components[i];
        }
        /*new float[] {c.getRed(), c.getGreen(), c.getBlue()};*/
        
        return new PdfArray(colorCompts);
    }
    
    protected Object clone()
    {
        try
        {
            Object clone = super.clone();
            PdfAnnot annot = (PdfAnnot) clone;
            annot.rect = (PdfRect) this.rect.clone();
            annot.color = new Color(this.color.getRed(), this.color
                .getGreen(), this.color.getBlue());
            annot.dict = (PdfDict) this.dict.clone();
            
            return clone;
        }
        catch (CloneNotSupportedException e)
        {
            return null; //never, we are clonable
        }
    }

    public int hashCode()
    {
        return this.dict.hashCode();
    }

    PdfAnnot()
    {
        borderStyle = BORDERSTYLE_SOLID;
        color = Color.BLACK;
        dict = new PdfDict(new HashMap());
        considerPageMargins = true;
    }
    
    PdfAnnot(PdfRect r, String subject, String contents,
        String title)
    {
        setRect(r);
        this.subject = subject;
        this.contents = contents;
        this.title = title;
        borderStyle = BORDERSTYLE_SOLID;
        color = Color.BLACK;
        dict = new PdfDict(new HashMap());
        considerPageMargins = true;
    }

    PdfAnnot(PdfRect r, String subject, String contents,
        String title, Color c)
    {
        setRect(r);
        this.subject = subject;
        this.contents = contents;
        this.title = title;
        borderStyle = BORDERSTYLE_SOLID;
        color = c;
        dict = new PdfDict(new HashMap());
        considerPageMargins = true;
    }
    
    PdfAnnot(PdfRect r, String subject, String contents,
        String title, int flags)
    {
        setRect(r);
        this.subject = subject;
        this.contents = contents;
        this.title = title;
        this.flags = flags;
        borderStyle = BORDERSTYLE_SOLID;
        color = Color.BLACK;
        dict = new PdfDict(new HashMap());
        considerPageMargins = true;
    }
    
    PdfAnnot(PdfRect r, String subject, String contents,
        String title, int flags, Color c)
    {
        setRect(r);
        this.subject = subject;
        this.contents = contents;
        this.title = title;
        this.flags = flags;
        borderStyle = BORDERSTYLE_SOLID;
        color = c;
        dict = new PdfDict(new HashMap());
        considerPageMargins = true;
    }
    
    PdfAnnot(PdfRect r, int flags)
    {
        setRect(r);
        this.flags = flags;
        borderStyle = BORDERSTYLE_SOLID;
        color = Color.BLACK;
        dict = new PdfDict(new HashMap());
        considerPageMargins = true;
    }
    
    PdfAnnot(PdfRect r, int flags, Color c)
    {
        setRect(r);
        this.flags = flags;
        borderStyle = BORDERSTYLE_SOLID;
        color = c;
        dict = new PdfDict(new HashMap());
        considerPageMargins = true;
    }
    
    PdfAnnot(PdfRect r, Color c)
    {
        setRect(r);
        borderStyle = BORDERSTYLE_SOLID;
        color = c;
        dict = new PdfDict(new HashMap());
        considerPageMargins = true;
    }
    
    synchronized void applyRotation(PdfStdPage p)
    {
        this.setFlags(this.getFlags() | FLAG_NO_ROTATE);
        
        double pH = PdfMeasurement.convertToMeasurementUnit(
            p.measurementUnit, p.pageHeight);
        double pW = PdfMeasurement.convertToMeasurementUnit(
            p.measurementUnit, p.pageWidth);
        switch ((int) p.rotation)
        {
            case 90:
                this.setLeft(rect.x);
                this.setRight(rect.y + rect.height);
                this.setTop(pH - rect.x - rect.width);
                this.setBottom(rect.y);
                break;
            case 180:
                this.setLeft(pW - rect.x - rect.width);
                this.setRight(pW - rect.x);
                this.setTop(pH - rect.y - rect.height);
                this.setBottom(pH - rect.y);
                break;
            case 270:
                this.setLeft(pW - rect.y - rect.width);
                this.setRight(pW - rect.y);
                this.setTop(rect.x);
                this.setBottom(rect.x + rect.width);
                break;
        }
    }
    
    public synchronized int getType()
    {
        return annotType;
    }
    
    /**
     * Returns text used as subject of this annotation's contents.
     * 
     * @return text used as subject of the annotation's contents
     * @since 1.0
     */
    public synchronized String getSubject()
    {
        return subject;
    }

    /**
     * Specifies text to be used as subject of this annotation.
     * 
     * @param subject
     *            text to be used as subject of the annotation
     * @since 1.0
     * @see #getSubject()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfTextAnnot.ExampleSyntax.htm#setSubject">example</a>.
     */
    public synchronized void setSubject(String subject)
    {
        this.subject = subject;
    }

    /**
     * Returns text used in this annotation's title bar. 
     * 
     * @return text used in the annotation's title bar
     * @since 1.0
     * @see #setTitle(String)
     */
    public synchronized String getTitle()
    {
        return title;
    }

    /**
     * Specifies text to be displayed in title bar of this
     * annotation's popup window.
     * 
     * @param title
     *            text to be displayed in title bar of the
     *            annotation's popup window
     * @since 1.0
     * @see #getTitle()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfTextAnnot.ExampleSyntax.htm#setTitle">example</a>.
     */
    public synchronized void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns color of this annotation's popup.
     * 
     * @return color of the annotation popup
     * @since 1.0
     * @see #setColor(Color)
     */
    public synchronized Color getColor()
    {
        return color;
    }

    /**
     * Specifies color of this annotation's popup.
     * 
     * @param color color of the annotion popup
     * @since 1.0
     * @see #getColor()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfTextAnnot.ExampleSyntax.htm#setColor">example</a>.
     */
    public synchronized void setColor(Color color)
    {
        this.color = color;
    }

    /**
     * Returns text displayed by this annotation. If annotation does 
     * not display text, returns alternate description (typically used 
     * to ensure accessibility for people with disabilities).
     * 
     * @return text displayed by this annotation
     * @since 1.0
     * @see #setContents(String)
     */
    public synchronized String getContents()
    {
        return contents;
    }

    /**
     * Specifies contents for this annotation. If the annotation does 
     * not display text, specifies 
     * <a href="{@docRoot}/doc-files/glossary.htm#alternate_description" target="_GnosticeGlossaryWindow"
     * >alternate description</a>
     * for it.
     * 
     * @param contents contents of the annotation
     * @since 1.0
     * @see #getContents()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfTextAnnot.ExampleSyntax.htm#setContents">example</a>.
     */
    public synchronized void setContents(String contents)
    {
        this.contents = contents;
    }

    
    /**
     * Returns combined value of flags representing various 
     * characteristics of this annotation.
     * 
     * @return combined value of flags representing various 
     *         characteristics of the annotation
     * @since 1.0
     * @see #setFlags(int)
     */
    public final synchronized int getFlags()
    {
        return flags;
    }

    /**
     * Specifies flags representing various characteristics of this 
     * annotation.
     * 
     * @param flags
     *            combined value of flags representing various
     *            characteristics of this annotation
     * @since 1.0
     * @see #getFlags()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfTextAnnot.ExampleSyntax.htm#setFlags">example</a>. 
     */
    public final synchronized void setFlags(int flags)
    {
        this.flags ^= flags;
    }

    /** 
     * Returns annotation name.
     * 
     * @return name of the annotation
     * @since 1.0
     * @see #getAnnotName()
     */
    public synchronized String getAnnotName()
    {
        return annotName;
    }

    /**
     * Specifies unique name for this annotation in its document.
     * 
     * @param name
     *            unique name for the annotation
     * @since 1.0
     * @see #getAnnotName()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfTextAnnot.ExampleSyntax.htm#setAnnotName">example</a>.
     */
    public synchronized void setAnnotName(String name)
    {
        this.annotName = name;
    }

    /*These getters and setter for left, right, top and
    bottom are redundant and should NOT be exposed to
    user. These are implemented due to incorrect
    implementation of annots reading in Pro.*/
    synchronized void setBottom(double bottom)
    {
        this.bottom = bottom;
    }

    synchronized void setLeft(double left)
    {
        this.left = left;
    }

    synchronized void setRight(double right)
    {
        this.right = right;
    }

    synchronized void setTop(double top)
    {
        this.top = top;
    }

    /**
     * Returns distance from top edge of page to bottom edge of this
     * annotation's annotation rectangle.
     * 
     * @return distance from top of page to bottom of the annotation
     *         rectangle
     * @since 1.0 
     */
    public synchronized double getBottom()
    {
        return bottom;
    }

    /**
     * Returns distance from left edge of page to left edge of this
     * annotation's annotation rectangle.
     * 
     * @return distance from left edge of page to left edge of the
     *         annotation rectangle
     * @since 1.0
     */
    public synchronized double getLeft()
    {
        return left;
    }

    /**
     * Returns distance from left edge of page and right edge of this
     * annotation's annotation rectangle.
     * 
     * @return distance from left edge of page and right edge of the
     *         annotation rectangle.
     * @since 1.0
     */
    public synchronized double getRight()
    {
        return right;
    }

    /**
     * Returns distance from top edge of page to top edege of this
     * annotation's rectangle.
     * 
     * @return distance from top edge of page to top edege of the
     *         annotation rectangle
     * @since 1.0
     */
    public synchronized double getTop()
    {
        return top;
    }

    /**
     * Returns constant identifying annotation's border style. Works 
     * with circle, ink, link, and square annotation types if border
     * has been set to be displayed.
     * 
     * @return constant identifying annotation's border style
     * @since 1.0
     * @see #setBorderStyle(int)
     */
    public synchronized int getBorderStyle()
    {
        return borderStyle;
    }

    /**
     * Specifies this annotation's border style. Applicable to 
     * circle, ink, link, and square annotation types. Works if 
     * border has been set to be displayed.
     * 
     * @param borderStyle
     *            constant specifying annotation's border style
     * @since 1.0
     * @see #getBorderStyle()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#setBorderStyle">example</a>.
     */
    public synchronized void setBorderStyle(int borderStyle)
    {
        if (borderWidth <= 0)
        {
            borderWidth = 1;
        }
        
        this.borderStyle = borderStyle;
    }

    /**
     * Returns an array containing lengths of dashes and gaps in this
     * annotation's 
     * <a href="{@docRoot}/doc-files/glossary.htm#dash_pattern" target="_GnosticeGlossaryWindow" 
     * >dash pattern</a>.
     * 
     * @return an array containing lengths of dashes and gaps in the
     *         annotation's dash pattern
     * @since 1.0
     * @see #setDashPattern(int[])
     */
    public synchronized int[] getDashPattern()
    {
        return dashPattern;
    }

    /**
     * Specifies lengths of dashes and gaps for this annotation's 
     * <a href="{@docRoot}/doc-files/glossary.htm#dash_pattern" target="_GnosticeGlossaryWindow" 
     * >dash pattern</a>. For this method to be effective, the border 
     * style needs to be set to {@link #BORDERSTYLE_DASHED} using the 
     * {@link #setBorderStyle(int)} method and the border must be set 
     * to be displayed using the {@link #setShowRect(boolean)} 
     * method.  
     * 
     * @param dashPattern
     *            array containing lengths of dashes and gaps for 
     *            this annotation's dash pattern
     * @since 1.0
     * @see #getDashPattern()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#setDashPattern">example</a>.
     */
    public synchronized void setDashPattern(int[] dashPattern)
    {
        this.dashPattern = dashPattern;
    }

    /**
     * Returns width of this annotation's border.
     * 
     * @return width of the annotation's border
     * @since 1.0
     * @see #setBorderWidth(double)
     */
    public synchronized double getBorderWidth()
    {
        return borderWidth;
    }

    /**
     * Specifies width of this annotation's border.
     * 
     * @param borderWidth width of the annotation's border
     * @since 1.0
     * @see #getBorderWidth()
     */
    public synchronized void setBorderWidth(double borderWidth)
    {
        this.borderWidth = borderWidth;
    }

    /**
     * Returns whether this annotation's border is displayed.
     * 
     * @return whether the annotation's border is displayed
     * @since 1.0
     * @see #setShowRect(boolean)
     */
    public synchronized boolean isShowRect()
    {
        return showRect;
    }

    /**
     * Specifies whether this annotation's border needs to be
     * displayed.
     * 
     * @param showRect
     *            whether the annotation's border needs to be
     *            displayed
     * @since 1.0
     * @see #isShowRect()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfLinkAnnot.ExampleSyntax.htm#setShowRect">example</a>.
     */
    public synchronized void setShowRect(boolean showRect)
    {
        this.showRect = showRect;
    }

    /**
     * Returns annotation rectangle of this annotation.
     * 
     * @return annotation rectangle of the annotation
     * @since 1.0
     * @see #setRect(double, double, double, double)
     * @see #setRect(double, double, double, double, int)
     * @see #setRect(PdfRect)
     * @see #setRect(PdfRect, int)
     * @see #setRect(Rectangle)
     */
    public synchronized PdfRect getRect()
    {
        return rect;
    }

    /**
     * Specifies <code>PdfRect</code> object as 
     * <a href="{@docRoot}/doc-files/glossary.htm#annotation_rectangle" target="_GnosticeGlossaryWindow"
     * >annotation rectangle</a> 
     * for this annotation. 
     * <p>
     * The position and size of the rectangle are applied in the 
     * default measurement unit of the page to which the annotation
     * is added.
     * </p>   
     * 
     * @param rect annotation rectangle for this annotation
     * @since 1.0
     * @see #getRect()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfTextAnnot.ExampleSyntax.htm#setRect_PdfRect">example</a>.
     */
    public synchronized void setRect(PdfRect rect)
    {
        this.rect = rect;
        this.left = rect.left();
        this.right = rect.right();
        this.top = rect.top();
        this.bottom = rect.bottom();
    }

    /**
     * Specifies <code>PdfRect</code> object as 
     * <a href="{@docRoot}/doc-files/glossary.htm#annotation_rectangle"
     * target="_GnosticeGlossaryWindow" 
     * >annotation rectangle</a> for this annotation in specified
     * measurement unit.
     * 
     * @param rect
     *            annotation rectangle for this annotation
     * @param measurementUnit
     *            measurement unit with which the annotation rectangle
     *            is specified
     * @since 1.0
     * @see #getRect()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfTextAnnot.ExampleSyntax.htm#setRect_PdfRect_int">example</a>.
     */
    public synchronized void setRect(PdfRect rect, int measurementUnit)
    {
        double x = PdfMeasurement.convertToPdfUnit(measurementUnit,
            rect.x);
        double y = PdfMeasurement.convertToPdfUnit(measurementUnit,
            rect.y);
        double w = PdfMeasurement.convertToPdfUnit(measurementUnit,
            rect.width);
        double h = PdfMeasurement.convertToPdfUnit(measurementUnit,
            rect.height);
        this.rect = new PdfRect(x, y, w, h);
        this.left = this.rect.left();
        this.right = this.rect.right();
        this.top = this.rect.top();
        this.bottom = this.rect.bottom();
    }

    /**
     * Specifies 
     * <a href="{@docRoot}/doc-files/glossary.htm#annotation_rectangle" target="_GnosticeGlossaryWindow"
     * >annotation rectangle</a> 
     * for this annotation at (<code>x</code>, <code>y</code>) 
     * with specified width and height.
     * <p>
     * The position (<code>x</code>, <code>y</code>), and dimensions 
     * <code>width</code> and <code>height</code> are applied in the
     * default measurement unit of the page to which the annotation 
     * is added.
     * </p>
     * 
     * @param x
     *            x-coordinate of the annotation rectangle
     * @param y
     *            y-coordinate of the annotation rectangle
     * @param width
     *            width of the annotation rectangle
     * @param height
     *            height of the annotation rectangle
     * @since 1.0
     * @see #getRect()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfTextAnnot.ExampleSyntax.htm#setRect_double_double_double_double">example</a>.
     */
    public synchronized void setRect(double x, double y,
        double width, double height)
    {
        PdfRect r = new PdfRect(x, y, width, height);
        this.rect = r;
        this.left = r.left();
        this.right = r.right();
        this.top = r.top();
        this.bottom = r.bottom();
    }
    
    /**
     * Specifies 
     * <a href="{@docRoot}/doc-files/glossary.htm#annotation_rectangle" target="_GnosticeGlossaryWindow"
     * >annotation rectangle</a> for this annotation in specified 
     * measurement unit.
     * 
     * @param x
     *            x-coordinate of the annotation rectangle
     * @param y
     *            y-coordinate of the annotation rectangle
     * @param width
     *            width of the annotation rectangle
     * @param height
     *            height of the annotation rectangle
     * @param measurementUnit
     *            measurement unit with which the annotation rectangle
     *            is specified
     * @since 1.0
     * @see #getRect()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfTextAnnot.ExampleSyntax.htm#setRect_double_double_double_double_int">example</a>.
     */
    public synchronized void setRect(double x, double y,
        double width, double height, int measurementUnit)
    {
        double x1 = PdfMeasurement.convertToPdfUnit(measurementUnit,
            x);
        double y1 = PdfMeasurement.convertToPdfUnit(measurementUnit,
            y);
        double w1 = PdfMeasurement.convertToPdfUnit(measurementUnit,
            width);
        double h1 = PdfMeasurement.convertToPdfUnit(measurementUnit,
            height);

        PdfRect r = new PdfRect(x1, y1, w1, h1);
        this.rect = r;
        this.left = r.left();
        this.right = r.right();
        this.top = r.top();
        this.bottom = r.bottom();
    }
    
    /**
     * Specifies <code>Rectangle</code> object as 
     * <a href="{@docRoot}/doc-files/glossary.htm#annotation_rectangle" target="_GnosticeGlossaryWindow"
     * >annotation rectangle</a> for this annotation. 
     * 
     * @param rect annotation rectangle for this annotation
     * @since 1.0
     * @see #getRect()
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfTextAnnot.ExampleSyntax.htm#setRect_Rectangle">example</a>.
     */
    public synchronized void setRect(Rectangle rect)
    {
        PdfRect r = new PdfRect(rect);
        this.rect = r;
        this.left = r.left();
        this.right = r.right();
        this.top = r.top();
        this.bottom = r.bottom();
    }

    synchronized PdfDict getDict()
    {
        return dict;
    }
    
    synchronized void setDict(PdfDict dict)
    {
        this.dict = dict;
    }
    
    PdfAnnot encode(PdfStdPage p) throws PdfException
    {
        PdfRect prevRect = this.rect;
        PdfRect r = p.updatePageSettings(this.rect);
        if (r == null)
        {
            throw new PdfException("Invalid Annot Rectangle");
        }
        setRect(r);
        
        if (p.rotation % 360 != 0)
        {
            applyRotation(p);
        }

        HashMap annotMap = (HashMap) dict.getMap();
        annotMap.put(new PdfName(Usable.PDF_TYPE), new PdfName(
            Usable.PDF_ANNOT));

        double pageHeight = p.pageHeight;
        double left = PdfMeasurement.convertToPdfUnit(
            p.measurementUnit, this.getLeft());
        double top = pageHeight
            - PdfMeasurement.convertToPdfUnit(p.measurementUnit, this
                .getTop());
        double right = PdfMeasurement.convertToPdfUnit(
            p.measurementUnit, this.getRight());
        double bottom = pageHeight
            - PdfMeasurement.convertToPdfUnit(p.measurementUnit, this
                .getBottom());
        ArrayList arrRect = new ArrayList();
        arrRect.add(new PdfFloat((float) left));
        arrRect.add(new PdfFloat((float) bottom));
        arrRect.add(new PdfFloat((float) right));
        arrRect.add(new PdfFloat((float) top));
        annotMap.put(new PdfName(Usable.PDF_RECT), new PdfArray(
            arrRect));

        if (color != null)
        {
            ArrayList arrColor = new ArrayList();
            arrColor.add(new PdfFloat((float) color.getRed() / 255));
            arrColor
                .add(new PdfFloat((float) color.getGreen() / 255));
            arrColor.add(new PdfFloat((float) color.getBlue() / 255));
            annotMap.put(new PdfName(Usable.PDF_COLOR), new PdfArray(
                arrColor));
        }

        annotMap.put(new PdfName(Usable.PDF_M), new PdfString(PdfDate
            .CurrentDate(), true));
        annotMap.put(new PdfName(Usable.PDF_F), new PdfInteger(this
            .getFlags()));

        if (this.contents != null)
        {
            annotMap.put(new PdfName(Usable.PDF_CONTENTS),
                new PdfTextString(this.getContents()));
        }
        if (this.annotName != null)
        {
            annotMap.put(new PdfName(Usable.PDF_ANNOT_NAME),
                new PdfTextString(this.getAnnotName()));
        }
        if (this.title != null)
        {
            annotMap.put(new PdfName(Usable.PDF_T),
                new PdfTextString(this.getTitle()));
        }
        if (this.subject != null)
        {
            annotMap.put(new PdfName(Usable.PDF_ANNOT_SUBJECT),
                new PdfTextString(this.getSubject()));
        }
        
        Map m = null;
        switch (borderStyle)
        {
            case BORDERSTYLE_BEVELED:
                m = new HashMap();
                m.put(new PdfName(Usable.PDF_TYPE), new PdfName(
                    Usable.PDF_BORDER));
                m.put(new PdfName(Usable.PDF_W), new PdfFloat((float)
                    borderWidth));
                m.put(new PdfName(Usable.PDF_S), new PdfName(
                    "B"));
                annotMap.put(new PdfName(Usable.PDF_BS),
                    new PdfDict(m));
                break;
            case BORDERSTYLE_DASHED:
                m = new HashMap();
                m.put(new PdfName(Usable.PDF_TYPE), new PdfName(
                    Usable.PDF_BORDER));
                m.put(new PdfName(Usable.PDF_W), new PdfFloat((float)
                    borderWidth));
                m.put(new PdfName(Usable.PDF_S), new PdfName(
                    "D"));
                if (dashPattern != null)
                {
                    m.put(new PdfName(Usable.PDF_D),
                        new PdfArray(dashPattern));
                }
                annotMap.put(new PdfName(Usable.PDF_BS),
                    new PdfDict(m));
                break;
            case BORDERSTYLE_INSET:
                m = new HashMap();
                m.put(new PdfName(Usable.PDF_TYPE), new PdfName(
                    Usable.PDF_BORDER));
                m.put(new PdfName(Usable.PDF_W), new PdfFloat((float)
                    borderWidth));
                m.put(new PdfName(Usable.PDF_S), new PdfName(
                   "I"));
                annotMap.put(new PdfName(Usable.PDF_BS),
                    new PdfDict(m));
                break;
            case BORDERSTYLE_SOLID:
                m = new HashMap();
                m.put(new PdfName(Usable.PDF_TYPE), new PdfName(
                    Usable.PDF_BORDER));
                m.put(new PdfName(Usable.PDF_W), new PdfFloat((float)
                    (borderWidth <= 0 ? 1 : borderWidth)));
                m.put(new PdfName(Usable.PDF_S), new PdfName(
                    "S"));
                annotMap.put(new PdfName(Usable.PDF_BS),
                    new PdfDict(m));
                break;
            case BORDERSTYLE_UNDERLINE:
                m = new HashMap();
                m.put(new PdfName(Usable.PDF_TYPE), new PdfName(
                    Usable.PDF_BORDER));
                m.put(new PdfName(Usable.PDF_W), new PdfFloat((float)
                    borderWidth));
                m.put(new PdfName(Usable.PDF_S), new PdfName(
                    "U"));
                annotMap.put(new PdfName(Usable.PDF_BS),
                    new PdfDict(m));
                break;
            default:
                m = new HashMap();
                m.put(new PdfName(Usable.PDF_TYPE), new PdfName(
                    Usable.PDF_BORDER));
                m.put(new PdfName(Usable.PDF_W), new PdfInteger(0));
                m.put(new PdfName(Usable.PDF_S), new PdfName("S"));
                annotMap.put(new PdfName(Usable.PDF_BS), new PdfDict(
                    m));
                break;
        }

        this.rect = prevRect;

        return this;
    }
    
    abstract void applyPropertiesFrom(PdfDict annotDict,
        PdfStdPage page) throws IOException, PdfException;
    
    abstract void set(PdfStdDocument originDoc, PdfStdDocument d)
        throws IOException, PdfException;

    void write(PdfStdDocument d) throws IOException, PdfException
    {
        isWritten = true;
    }
}