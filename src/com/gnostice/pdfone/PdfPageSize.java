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
 * This interface defines constants for various page sizes including
 * both ISO and North American paper sizes.
 * 
 * @since 1.0
 * @version 1.0
 * @gnostice.example See <a target="_GnosticeExampleWindow" href="{@docRoot}/doc-files/PdfOne.PdfPage.ExampleSyntax.htm#PdfPageSize">example</a>. 
 */
public interface PdfPageSize
{
    /**
     * Letter paper 
     */
    public static final int LETTER = 0;

    /**
     * A3 paper  
     */
    public static final int A3 = 1;

    /**
     * A4 paper
     */
    public static final int A4 = 2;

    /**
     * A5 paper
     */
    public static final int A5 = 3;

    /**
     * #10 envelope
     */
    public static final int ENVELOPE_10 = 4;

    /**
     * DL envelope (<i>Dimension Lengthwise envelope</i>, as per ISO
     * 269)
     */
    public static final int ENVELOPE_DL = 5;

    /**
     * C5 envelope
     */
    public static final int ENVELOPE_C5 = 6;

    /**
     * Legal paper
     */
    public static final int LEGAL = 7;

    /**
     * User-defined page size
     */
    public static final int CUSTOM = 8;

    /**
     * A2 paper
     */
    public static final int A2 = 9;

    /**
     * A4 extra paper
     */
    public static final int A3EXTRA = 10;

    /**
     * A4 extra transverse paper
     */
    public static final int A3EXTRATRANSVERSE = 11;

    /**
     * A3 rotated paper
     */
    public static final int A3ROTATED = 12;

    /**
     * A3 transverse paper
     */
    public static final int A3TRANSVERSE = 13;

    /**
     * A4 extra paper
     */
    public static final int A4EXTRA = 14;

    /**
     * A4 plus paper
     */
    public static final int A4PLUS = 15;

    /**
     * A4 rotated paper
     */
    public static final int A4ROTATED = 16;

    /**
     * A4 small paper
     */
    public static final int A4SMALL = 17;

    /**
     * A4 transverse paper
     */
    public static final int A4TRANSVERSE = 18;

    /**
     * A5 extra paper
     */
    public static final int A5EXTRA = 19;

    /**
     * A5 rotated paper
     */
    public static final int A5ROTATED = 20;

    /**
     * A5 transverse paper
     */
    public static final int A5TRANSVERSE = 21;

    /**
     * A6 paper 
     */
    public static final int A6 = 22;

    /**
     * A6 rotated paper
     */
    public static final int A6ROTATED = 23;

    /**
     * SuperA/SuperA/A4 paper
     */
    public static final int APLUS = 24;

    /**
     * B4 paper
     */
    public static final int B4 = 25;

    /**
     * B4 Envelope
     */
    public static final int ENVELOPE_B4 = 26;

    /**
     * Japanese Industrial Standard (JIS) B4 rotated paper
     */
    public static final int B4JisROTATED = 27;

    /**
     * B5 paper
     */
    public static final int B5 = 28;

    /**
     * B5 envelope
     */
    public static final int ENVELOPE_B5 = 29;

    /**
     * B5 extra paper
     */
    public static final int B5EXTRA = 30;

    /**
     * Japanese Industrial Standard (JIS) B5 rotated paper
     */
    public static final int B5JisROTATED = 31;

    /**
     * B5 transverse paper
     */
    public static final int B5TRANSVERSE = 32;

    /**
     * B6 envelope
     */
    public static final int ENVELOPE_B6 = 33;

    /**
     * Japanese Industrial Standard (JIS) B6 paper
     */
    public static final int B6Jis = 34;

    /**
     * Japanese Industrial Standard (JIS) B6 rotated paper
     */
    public static final int B6JisROTATED = 35;

    /**
     * SuperB/SuperB/A3 paper
     */
    public static final int BPLUS = 36;

    /**
     * C3 envelope (as per ISO 269)
     */
    public static final int ENVELOPE_C3 = 37;

    /**
     * C4 envelope (as per ISO 269)
     */
    public static final int ENVELOPE_C4 = 38;

    /**
     * C6/C5 envelope (as per ISO 269)
     */
    public static final int ENVELOPE_C65 = 40;

    /**
     * C6 envelope (as per ISO 269)
     */
    public static final int ENVELOPE_C6 = 41;

    /**
     * C paper
     */
    public static final int SHEET_C = 42;

    /**
     * D paper
     */
    public static final int SHEET_D = 43;

    /**
     * E paper
     */
    public static final int SHEET_E = 44;

    /**
     * Executive paper
     */
    public static final int EXECUTIVE = 45;

    /**
     * Folio paper
     */
    public static final int FOLIO = 46;

    /**
     * German legal fanfold 
     */
    public static final int GERMAN_LEGAL_FANFOLD = 47;

    /**
     * German standard fanfold 
     */
    public static final int GERMAN_STANDARD_FANFOLD = 48;

    /**
     * Invitation envelope
     */
    public static final int ENVELOPE_INVITE = 49;

    /**
     * ISO B4 paper
     */
    public static final int ISOB4 = 50;

    /**
     * Italy envelope
     */
    public static final int ENVELOPE_ITALY = 51;

    /**
     * Ledger paper
     */
    public static final int LEDGER = 52;

    /**
     * Legal extra paper
     */
    public static final int LEGALEXTRA = 53;

    /**
     * Letter 
     */
    public static final int LETTEREXTRA = 54;

    /**
     * Letter extra transverse paper
     */
    public static final int LETTEREXTRA_TRANSVERSE = 55;

    /**
     * Letter plus paper
     */
    public static final int LETTERPLUS = 56;

    /**
     * Letter rotated paper
     */
    public static final int LETTERROTATED = 57;

    /**
     * Letter small paper
     */
    public static final int LETTERSMALL = 58;

    /**
     * Letter transverse paper
     */
    public static final int LETTERTRANSVERSE = 59;

    /**
     * Monarch envelope
     */
    public static final int ENVELOPE_MONARCH = 60;

    /**
     * Note paper
     */
    public static final int NOTE = 61;

    /**
     * Size 10 envelope 
     */
    public static final int ENVELOPE_NUMBER10 = 62;

    /**
     * Size 11 envelope 
     */
    public static final int ENVELOPE_NUMBER11 = 63;

    /**
     * Size 12 envelope 
     */
    public static final int ENVELOPE_NUMBER12 = 64;

    /**
     * Size 14 envelope 
     */
    public static final int ENVELOPE_NUMBER14 = 65;

    /**
     * Size 9 envelope 
     */
    public static final int ENVELOPE_NUMBER9 = 66;

    /**
     * Size 6 3/4 envelope
     */
    public static final int ENVELOPE_PERSONAL = 67;

    /**
     * Quarto paper
     */
    public static final int QUARTO = 68;

    /**
     * Statement paper
     */
    public static final int STATEMENT = 69;

    /**
     * Tabloid paper
     */
    public static final int TABLOID = 70;

    /**
     * Tabloid extra paper
     */
    public static final int TABLOIDEXTRA = 71;

    /**
     * US standard fanfold
     */
    public static final int US_STANDARD_STANFOLD = 72;
}
