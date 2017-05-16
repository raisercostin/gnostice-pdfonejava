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
 * This class defines measurement units and provides static methods
 * for converting values from one unit to another.
 * 
 * @version 1.0
 * @since 1.0
 */
public final class PdfMeasurement implements Usable
{
    protected static byte measurementUnit;

    /**
     * Constant specifying measurement unit in pixels.
     */
    public static final int MU_PIXELS = 0;

    /**
     * Constant specifying measurement unit in points.
     */
    public static final int MU_POINTS = 1;

    /**
     * Constant specifying measurement unit in twips.
     */
    public static final int MU_TWIPS = 2;

    /**
     * Constant specifying measurement unit in inches.
     */
    public static final int MU_INCHES = 3;

    /**
     * Constant specifying measurement unit in millimeters.
     */
    public static final int MU_MM = 4;

    /**
     * Converts <code>value</code> from specified measurement unit to 
     * points.
     * 
     * @param measurementUnit
     *            constant specifying the measurement unit from 
     *            which <code>value</code> needs to be converted
     * @param value
     *            value that needs to be converted
     * @return <code>value</code>, converted to points
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfMeasurement.ExampleSyntax.htm#convertToPdfUnit">example</a>.
     */
    public static double convertToPdfUnit(int measurementUnit,
        double value)
    {
        switch (measurementUnit)
        {
            case (MU_PIXELS):
                return (value / PIXEL_PER_INCH * INCHES_TO_POINTS);

            case (MU_INCHES):
                return (value * INCHES_TO_POINTS);

            case (MU_MM):
                return (value * MM_TO_POINTS);

            case (MU_TWIPS):
                return (value * TWIPS_TO_POINTS);
        }
        return value;
    }

    /**
     * Converts <code>value</code> from points to specified
     * measurement unit.
     * 
     * @param measurementUnit
     *            constant specifying measurement unit to which
     *            <code>value</code> needs to be converted
     * @param value
     *            value that needs to be converted
     * @return <code>value</code>, in specified measurement unit
     * @since 1.0
     * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfMeasurement.ExampleSyntax.htm#convertToMeasurementUnit">example</a>.
     */
    public static double convertToMeasurementUnit(
        int measurementUnit, double value)
    {
        switch (measurementUnit)
        {
            case (MU_PIXELS):
                return (value / INCHES_TO_POINTS * PIXEL_PER_INCH);
            
            case (MU_INCHES):
                return (value / INCHES_TO_POINTS);
            
            case (MU_MM):
                return (value / MM_TO_POINTS);
            
            case (MU_TWIPS):
                return (value / TWIPS_TO_POINTS);
        }
        return value;
    }
}