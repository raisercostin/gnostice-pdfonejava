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
 * This class represents an action for the viewer application to
 * perform when the user interacts with an annotation or a bookmark.
 * Actions can also be triggered by events specified in the 
 * {@link PdfEvent PdfEvent} inner class. An action can be as simple 
 * as navigating to a particular page in the document to launching
 * a file with its print command.
 * 
 * @since 1.0
 * @version 1.0
 */
public final class PdfAction
{
    /**
     * Action for launching an application, or a displaying or
     * printing a file.
     */
    public static final int LAUNCH = 0;

    /**
     * Action for navigating to a  
     * <a href="{@docRoot}/doc-files/glossary.htm#goto" target="_GnosticeGlossaryWindow"
     * >destination</a>
     * in current document.
     */
    public static final int GOTO = 1;

    /**
     * Action for navigating to a  
     * <a href="{@docRoot}/doc-files/glossary.htm#remote_goto" target="_GnosticeGlossaryWindow"
     * >destination</a>
     * in another document.
     */
    public static final int REMOTE_GOTO = 2;

    /**
     * Action for resolving a Uniform Resource Identifier (URI). 
     */
    public static final int URI = 3;

    /**
     * Action for executing a JavaScript script.
     */
    public static final int JAVASCRIPT = 4;
    
    /**
     * <a href="{@docRoot}/doc-files/glossary.htm#named_action" target="_GnosticeGlossaryWindow"
     * >Named action</a>
     * for navigating to next page in current document.  
     */
    public static final int NAMED_NEXTPAGE = 5;

    /**
     * <a href="{@docRoot}/doc-files/glossary.htm#named_action" target="_GnosticeGlossaryWindow"
     * >Named action</a>
     * for navigating to previous page in current document.  
     */
    public static final int NAMED_PREVPAGE = 6;

    /**
     * <a href="{@docRoot}/doc-files/glossary.htm#named_action" target="_GnosticeGlossaryWindow"
     * >Named action</a>
     * for navigating to first page in current document.  
     */
    public static final int NAMED_FIRSTPAGE = 7;

    /**
     * <a href="{@docRoot}/doc-files/glossary.htm#named_action" target="_GnosticeGlossaryWindow"
     * >Named action</a>
     * for navigating to last page in current document.  
     */
    public static final int NAMED_LASTPAGE = 8;

    /**
     * This inner class represents an event that can act as a
     * trigger for an action or a set of actions. A set of standard
     * events are predefined here.
     * 
     * @since 1.0
     * @version 1.0
     */
    public final static class PdfEvent
    {
        /**
         * Event to trigger action(s) before document is opened in the
         * viewer.
         */
        public static final int ON_DOCUMENT_OPEN = 0;

        /**
         * Event to trigger action(s) before document is closed in the
         * viewer.
         */
        public static final int ON_DOCUMENT_CLOSE = 1;

        /**
         * Event to trigger action(s) before document is saved in the
         * viewer.
         */
        public static final int ON_BEFORE_DOCUMENT_SAVE = 2;

        /**
         * Event to trigger action(s) after document is saved in the
         * viewer.
         */
        public static final int ON_AFTER_DOCUMENT_SAVE = 3;

        /**
         * Event to trigger action(s) before document is printed in
         * the viewer.
         */
        public static final int ON_BEFORE_DOCUMENT_PRINT = 4;

        /**
         * Event to trigger action(s) after document is printed in the
         * viewer.
         */
        public static final int ON_AFTER_DOCUMENT_PRINT = 5;
        
        /**
         * Event to trigger action(s) when a given page is 
         * <i>opened</i> 
         * in the viewer. In some layouts, multiple pages may be 
         * visible. However, only one of them is considered open.
         */
        public static final int ON_PAGE_OPEN = 6;
        
        /**
         * Event to trigger action(s) when a given page is 
         * <i>closed</i>
         * in the viewer. In some layouts, multiple pages may be 
         * visible. However, only one of them is considered open. A 
         * page that is visible can be considered as closed if another 
         * page is opened.
         */
        public static final int ON_PAGE_CLOSE = 7;

        /**
         * Event to trigger action(s) before a given page becomes 
         * <i>visible</i>
         * in the viewer. In some layouts, multiple pages may be 
         * visible. However, only one of them is considered open.
         */
        public static final int ON_PAGE_VISIBLE = 19;
        
        /**
         * Event to trigger action(s) before a given page becomes 
         * <i>invisible</i>
         * in the viewer. In some layouts, multiple pages may be 
         * visible. Pages that are not currently visible are 
         * considered invisible.
         */
        public static final int ON_PAGE_INVISIBLE = 20;

        /**
         * Event to trigger action(s) before mouse pointer is brought
         * over the viewer.
         */
        public static final int ON_MOUSE_ENTER = 9;
        
        /**
         * Event to trigger action(s) before mouse pointer is taken
         * off the viewer.
         */
        public static final int ON_MOUSE_EXIT = 10;
        
        /**
         * Event to trigger action(s) before selection button of mouse 
         * is pressed.
         */
        public static final int ON_MOUSE_DOWN = 11;
        
        /**
         * Event to trigger action(s) when selection button of mouse 
         * is released.
         */         
        public static final int ON_MOUSE_UP = 12;
        
        /**
         * Event to trigger action(s) when a form field receives input
         * focus.
         */
        public static final int ON_INPUT_FOCUS = 13;
        
        /**
         * Event to trigger action(s) when a form field loses input
         * focus.
         */
        public static final int ON_INPUT_BLURRED = 14;
        
        /**
         * Event to trigger {@link PdfAction#JAVASCRIPT JAVASCRIPT}
         * action(s) when keystrokes are input in a form field or when
         * a selection is made or changed in scrollable list box. This
         * event can be used to check for validity of the input, or to
         * change it or reject it.
         */
        public static final int ON_FIELD_KEY_STROKE = 15;
        
        /**
         * Event to trigger {@link PdfAction#JAVASCRIPT JAVASCRIPT}
         * action(s) before value of a form field value is formatted.
         * This event can be used to modify the value of the form
         * field before it is formatted.
         */
        public static final int ON_FIELD_BEFORE_FORMAT = 16;
        
        /**
         * Event to trigger {@link PdfAction#JAVASCRIPT JAVASCRIPT}
         * action(s) when value of a form field is changed. This 
         * event can be used to check the validity of the new value.
         */
        public static final int ON_FIELD_VALUE_CHANGE = 17;
        
        /**
         * Event to trigger {@link PdfAction#JAVASCRIPT JAVASCRIPT}
         * action(s) when value a form field is recalculated after
         * value of another field is changed.
         */
        public static final int ON_FIELD_VALUE_RECALCULATE = 18;
    }
}