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

import java.util.HashMap;

public class PdfPagePresentation implements Cloneable
{
    public static final int SPLIT = 0;
    
    public static final int BLINDS = 1;
    
    public static final int BOX = 2;
    
    public static final int WIPE = 3;
    
    public static final int DISSOLVE = 4;

    public static final int GLITTER = 5;
    
    public static final int REPLACE = 6;
    
    public static final int FLY = 7;
    
    public static final int PUSH = 8;
    
    public static final int COVER = 9;

    public static final int UNCOVER = 10;
    
    public static final int FADE = 11;

    public static final int HORIZONTAL = 1;
    
    public static final int VERTICAL = 2;

    public static final int INWARD = 1;
    
    public static final int OUTWARD = 2;

    public static final int LEFT_TO_RIGHT = 0;
    
    public static final int BOTTOM_TO_TOP = 90;

    public static final int RIGHT_TO_LEFT = 180;
    
    public static final int TOP_TO_BOTTOM = 270;

    public static final int TOP_LEFT__TO__BOTTOM_RIGHT = 315;
    
    private int transitionStyle;
    
    private double transitionDuration;

    private int transitionDimension;
    
    private int transitionDirection;
    
    private int transitionMotion;
    
    private double flyTransitionEndScale;
    
    private boolean flyTransitionAreaOpaque;
    
    public synchronized int getTransitionStyle()
    {
        return transitionStyle;
    }

    public synchronized void setTransitionStyle(int transitionStyle)
    {
        this.transitionStyle = transitionStyle;
    }

    public synchronized double getTransitionDuration()
    {
        return transitionDuration;
    }

    public synchronized void setTransitionDuration(
        double transitionDuration)
    {
        this.transitionDuration = transitionDuration;
    }

    public synchronized int getTransitionDimension()
    {
        return transitionDimension;
    }

    public synchronized void setTransitionDimension(
        int transitionDimension)
    {
        this.transitionDimension = transitionDimension;
    }

    public synchronized int getTransitionMotion()
    {
        return transitionMotion;
    }

    public synchronized void setTransitionMotion(int transitionMotion)
    {
        this.transitionMotion = transitionMotion;
    }

    public synchronized int getTransitionDirection()
    {
        return transitionDirection;
    }

    public synchronized void setTransitionDirection(
        int transitionDirection)
    {
        this.transitionDirection = transitionDirection;
    }

    public synchronized boolean isFlyTransitionAreaOpaque()
    {
        return flyTransitionAreaOpaque;
    }

    public synchronized void setFlyTransitionAreaOpaque(
        boolean flyTransitionAreaOpaque)
    {
        this.flyTransitionAreaOpaque = flyTransitionAreaOpaque;
    }

    public synchronized double getFlyTransitionEndScale()
    {
        return flyTransitionEndScale;
    }

    public synchronized void setFlyTransitionEndScale(
        double flyTransitionEndScale)
    {
        this.flyTransitionEndScale = flyTransitionEndScale;
    }
    
    public PdfDict prepareDict()
    {
        HashMap hm = new HashMap();
        switch(transitionStyle)
        {
            case SPLIT:
                hm.put(new PdfName(Usable.PDF_S), new PdfName(
                    Usable.PDF_SPLIT));
                break;
            case BLINDS:
                hm.put(new PdfName(Usable.PDF_S), new PdfName(
                    Usable.PDF_BLINDS));
                break;
            case BOX:
                hm.put(new PdfName(Usable.PDF_S), new PdfName(
                    Usable.PDF_BOX));
                break;
            case WIPE:
                hm.put(new PdfName(Usable.PDF_S), new PdfName(
                    Usable.PDF_WIPE));
                break;
            case DISSOLVE:
                hm.put(new PdfName(Usable.PDF_S), new PdfName(
                    Usable.PDF_DISSOLVE));
                break;
            case GLITTER:
                hm.put(new PdfName(Usable.PDF_S), new PdfName(
                    Usable.PDF_GLITTER));
                break;
            case REPLACE:
                hm.put(new PdfName(Usable.PDF_S), new PdfName(
                    Usable.PDF_REPLACE));
                break;
            case FLY:
                hm.put(new PdfName(Usable.PDF_S), new PdfName(
                    Usable.PDF_FLY));
                break;
            case PUSH:
                hm.put(new PdfName(Usable.PDF_S), new PdfName(
                    Usable.PDF_PUSH));
                break;
            case COVER:
                hm.put(new PdfName(Usable.PDF_S), new PdfName(
                    Usable.PDF_COVER));
                break;
            case UNCOVER:
                hm.put(new PdfName(Usable.PDF_S), new PdfName(
                    Usable.PDF_UNCOVER));
                break;
            case FADE:
                hm.put(new PdfName(Usable.PDF_S), new PdfName(
                    Usable.PDF_FADE));
                break;
            default:
                break;
        }
        switch (transitionDimension)
        {
            case HORIZONTAL:
                hm.put(new PdfName(Usable.PDF_DM), new PdfName(
                    Usable.PDF_HORIZONTAL));
                break;
            case VERTICAL:
                hm.put(new PdfName(Usable.PDF_DM), new PdfName(
                    Usable.PDF_VERTICAL));
                break;
            default:
                break;
        }
        switch (transitionMotion)
        {
            case INWARD:
                hm.put(new PdfName(Usable.PDF_M), new PdfName(
                    Usable.PDF_INWARD));
                break;
            case OUTWARD:
                hm.put(new PdfName(Usable.PDF_M), new PdfName(
                    Usable.PDF_OUTWARD));
                break;
            default:
                break;
        }
        if (transitionDirection != 0)
        {
            hm.put(new PdfName(Usable.PDF_DI), new PdfInteger(
                transitionDirection));
        }
        if (transitionDuration != 0)
        {
            hm.put(new PdfName(Usable.PDF_D), new PdfFloat(
                transitionDuration));
        }
        if (flyTransitionEndScale != 0)
        {
            hm.put(new PdfName(Usable.PDF_SS), new PdfFloat(
                flyTransitionEndScale));
        }
        hm.put(new PdfName(Usable.PDF_B), new PdfBoolean(
            flyTransitionAreaOpaque));
        
        return new PdfDict(hm);
    }
    
    public Object clone()
    {
        PdfPagePresentation clone = null;
        try
        {
            clone = (PdfPagePresentation) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
        }
        
        return clone;
    }
}
