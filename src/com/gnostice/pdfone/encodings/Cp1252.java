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

package com.gnostice.pdfone.encodings;

class Cp1252 extends CodePage
{
    static {
    bytes = new byte[] { (byte) 0x00, (byte) 0x01, (byte) 0x02,
            (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06,
            (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0A,
            (byte) 0x0B, (byte) 0x0C, (byte) 0x0D, (byte) 0x0E,
            (byte) 0x0F, (byte) 0x10, (byte) 0x11, (byte) 0x12,
            (byte) 0x13, (byte) 0x14, (byte) 0x15, (byte) 0x16,
            (byte) 0x17, (byte) 0x18, (byte) 0x19, (byte) 0x1A,
            (byte) 0x1B, (byte) 0x1C, (byte) 0x1D, (byte) 0x1E,
            (byte) 0x1F, (byte) 0x20, (byte) 0x21, (byte) 0x22,
            (byte) 0x23, (byte) 0x24, (byte) 0x25, (byte) 0x26,
            (byte) 0x27, (byte) 0x28, (byte) 0x29, (byte) 0x2A,
            (byte) 0x2B, (byte) 0x2C, (byte) 0x2D, (byte) 0x2E,
            (byte) 0x2F, (byte) 0x30, (byte) 0x31, (byte) 0x32,
            (byte) 0x33, (byte) 0x34, (byte) 0x35, (byte) 0x36,
            (byte) 0x37, (byte) 0x38, (byte) 0x39, (byte) 0x3A,
            (byte) 0x3B, (byte) 0x3C, (byte) 0x3D, (byte) 0x3E,
            (byte) 0x3F, (byte) 0x40, (byte) 0x41, (byte) 0x42,
            (byte) 0x43, (byte) 0x44, (byte) 0x45, (byte) 0x46,
            (byte) 0x47, (byte) 0x48, (byte) 0x49, (byte) 0x4A,
            (byte) 0x4B, (byte) 0x4C, (byte) 0x4D, (byte) 0x4E,
            (byte) 0x4F, (byte) 0x50, (byte) 0x51, (byte) 0x52,
            (byte) 0x53, (byte) 0x54, (byte) 0x55, (byte) 0x56,
            (byte) 0x57, (byte) 0x58, (byte) 0x59, (byte) 0x5A,
            (byte) 0x5B, (byte) 0x5C, (byte) 0x5D, (byte) 0x5E,
            (byte) 0x5F, (byte) 0x60, (byte) 0x61, (byte) 0x62,
            (byte) 0x63, (byte) 0x64, (byte) 0x65, (byte) 0x66,
            (byte) 0x67, (byte) 0x68, (byte) 0x69, (byte) 0x6A,
            (byte) 0x6B, (byte) 0x6C, (byte) 0x6D, (byte) 0x6E,
            (byte) 0x6F, (byte) 0x70, (byte) 0x71, (byte) 0x72,
            (byte) 0x73, (byte) 0x74, (byte) 0x75, (byte) 0x76,
            (byte) 0x77, (byte) 0x78, (byte) 0x79, (byte) 0x7A,
            (byte) 0x7B, (byte) 0x7C, (byte) 0x7D, (byte) 0x7E,
            (byte) 0x7F, (byte) 0x80, (byte) 0x82, (byte) 0x83,
            (byte) 0x84, (byte) 0x85, (byte) 0x86, (byte) 0x87,
            (byte) 0x88, (byte) 0x89, (byte) 0x8A, (byte) 0x8B,
            (byte) 0x8C, (byte) 0x8E, (byte) 0x91, (byte) 0x92,
            (byte) 0x93, (byte) 0x94, (byte) 0x95, (byte) 0x96,
            (byte) 0x97, (byte) 0x98, (byte) 0x99, (byte) 0x9A,
            (byte) 0x9B, (byte) 0x9C, (byte) 0x9E, (byte) 0x9F,
            (byte) 0xA0, (byte) 0xA1, (byte) 0xA2, (byte) 0xA3,
            (byte) 0xA4, (byte) 0xA5, (byte) 0xA6, (byte) 0xA7,
            (byte) 0xA8, (byte) 0xA9, (byte) 0xAA, (byte) 0xAB,
            (byte) 0xAC, (byte) 0xAD, (byte) 0xAE, (byte) 0xAF,
            (byte) 0xB0, (byte) 0xB1, (byte) 0xB2, (byte) 0xB3,
            (byte) 0xB4, (byte) 0xB5, (byte) 0xB6, (byte) 0xB7,
            (byte) 0xB8, (byte) 0xB9, (byte) 0xBA, (byte) 0xBB,
            (byte) 0xBC, (byte) 0xBD, (byte) 0xBE, (byte) 0xBF,
            (byte) 0xC0, (byte) 0xC1, (byte) 0xC2, (byte) 0xC3,
            (byte) 0xC4, (byte) 0xC5, (byte) 0xC6, (byte) 0xC7,
            (byte) 0xC8, (byte) 0xC9, (byte) 0xCA, (byte) 0xCB,
            (byte) 0xCC, (byte) 0xCD, (byte) 0xCE, (byte) 0xCF,
            (byte) 0xD0, (byte) 0xD1, (byte) 0xD2, (byte) 0xD3,
            (byte) 0xD4, (byte) 0xD5, (byte) 0xD6, (byte) 0xD7,
            (byte) 0xD8, (byte) 0xD9, (byte) 0xDA, (byte) 0xDB,
            (byte) 0xDC, (byte) 0xDD, (byte) 0xDE, (byte) 0xDF,
            (byte) 0xE0, (byte) 0xE1, (byte) 0xE2, (byte) 0xE3,
            (byte) 0xE4, (byte) 0xE5, (byte) 0xE6, (byte) 0xE7,
            (byte) 0xE8, (byte) 0xE9, (byte) 0xEA, (byte) 0xEB,
            (byte) 0xEC, (byte) 0xED, (byte) 0xEE, (byte) 0xEF,
            (byte) 0xF0, (byte) 0xF1, (byte) 0xF2, (byte) 0xF3,
            (byte) 0xF4, (byte) 0xF5, (byte) 0xF6, (byte) 0xF7,
            (byte) 0xF8, (byte) 0xF9, (byte) 0xFA, (byte) 0xFB,
            (byte) 0xFC, (byte) 0xFD, (byte) 0xFE, (byte) 0xFF };

    unicodeMap = new char[] { '\u0000', '\u0001',
        '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007',
        '\u0008', '\u0009', '\012', '\u000B', '\u000C', '\015',
        '\u000E', '\u000F', '\u0010', '\u0011', '\u0012', '\u0013',
        '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019',
        '\u001A', '\u001B', '\u001C', '\u001D', '\u001E', '\u001F',
        '\u0020', '\u0021', '\u0022', '\u0023', '\u0024', '\u0025',
        '\u0026', '\047', '\u0028', '\u0029', '\u002A', '\u002B',
        '\u002C', '\u002D', '\u002E', '\u002F', '\u0030', '\u0031',
        '\u0032', '\u0033', '\u0034', '\u0035', '\u0036', '\u0037',
        '\u0038', '\u0039', '\u003A', '\u003B', '\u003C', '\u003D',
        '\u003E', '\u003F', '\u0040', '\u0041', '\u0042', '\u0043',
        '\u0044', '\u0045', '\u0046', '\u0047', '\u0048', '\u0049',
        '\u004A', '\u004B', '\u004C', '\u004D', '\u004E', '\u004F',
        '\u0050', '\u0051', '\u0052', '\u0053', '\u0054', '\u0055',
        '\u0056', '\u0057', '\u0058', '\u0059', '\u005A', '\u005B',
        '\134', '\u005D', '\u005E', '\u005F', '\u0060', '\u0061',
        '\u0062', '\u0063', '\u0064', '\u0065', '\u0066', '\u0067',
        '\u0068', '\u0069', '\u006A', '\u006B', '\u006C', '\u006D',
        '\u006E', '\u006F', '\u0070', '\u0071', '\u0072', '\u0073',
        '\u0074', '\u0075', '\u0076', '\u0077', '\u0078', '\u0079',
        '\u007A', '\u007B', '\u007C', '\u007D', '\u007E', '\u007F',
        '\u20AC', '\u201A', '\u0192', '\u201E', '\u2026', '\u2020',
        '\u2021', '\u02C6', '\u2030', '\u0160', '\u2039', '\u0152',
        '\u017D', '\u2018', '\u2019', '\u201C', '\u201D', '\u2022',
        '\u2013', '\u2014', '\u02DC', '\u2122', '\u0161', '\u203A',
        '\u0153', '\u017E', '\u0178', '\u00A0', '\u00A1', '\u00A2',
        '\u00A3', '\u00A4', '\u00A5', '\u00A6', '\u00A7', '\u00A8',
        '\u00A9', '\u00AA', '\u00AB', '\u00AC', '\u00AD', '\u00AE',
        '\u00AF', '\u00B0', '\u00B1', '\u00B2', '\u00B3', '\u00B4',
        '\u00B5', '\u00B6', '\u00B7', '\u00B8', '\u00B9', '\u00BA',
        '\u00BB', '\u00BC', '\u00BD', '\u00BE', '\u00BF', '\u00C0',
        '\u00C1', '\u00C2', '\u00C3', '\u00C4', '\u00C5', '\u00C6',
        '\u00C7', '\u00C8', '\u00C9', '\u00CA', '\u00CB', '\u00CC',
        '\u00CD', '\u00CE', '\u00CF', '\u00D0', '\u00D1', '\u00D2',
        '\u00D3', '\u00D4', '\u00D5', '\u00D6', '\u00D7', '\u00D8',
        '\u00D9', '\u00DA', '\u00DB', '\u00DC', '\u00DD', '\u00DE',
        '\u00DF', '\u00E0', '\u00E1', '\u00E2', '\u00E3', '\u00E4',
        '\u00E5', '\u00E6', '\u00E7', '\u00E8', '\u00E9', '\u00EA',
        '\u00EB', '\u00EC', '\u00ED', '\u00EE', '\u00EF', '\u00F0',
        '\u00F1', '\u00F2', '\u00F3', '\u00F4', '\u00F5', '\u00F6',
        '\u00F7', '\u00F8', '\u00F9', '\u00FA', '\u00FB', '\u00FC',
        '\u00FD', '\u00FE', '\u00FF' };
    }
}
