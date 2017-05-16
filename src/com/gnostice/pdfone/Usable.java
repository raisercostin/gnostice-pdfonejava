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
 * @author amol
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public interface Usable
{
	//All members are public static final by default
	int PIXEL_PER_INCH = 96;
	int INCHES_TO_POINTS = 72;
	float MM_TO_POINTS  = 2.83464F;
	float MM_TO_INCHES = 0.03937F;
	float TWIPS_TO_POINTS = 1 / 20;
	
	String PDF_FONTNAMEPREFIX = "Rub";
	String PDF_LF = "\r\n";
	String PDF_HEADER = "%PDF-"; 
	String PDF_BINARYDATA = PDF_LF + "%\342\343\317\323" + PDF_LF;
	String PDF_OBJ = "obj";
	String PDF_ENDOBJ = "endobj";
	String PDF_DICTSTART = "<<";
	String PDF_DICTEND = ">>";
	String PDF_TRUE = "true";
	String PDF_FALSE = "false";
	String PDF_NULL = "null";
	String PDF_XREF = "xref";
	String PDF_TRAILER = "trailer";
	String PDF_STARTXREF = "startxref";
	String PDF_SIZE = "Size";
	String PDF_ROOT = "Root";
	String PDF_KIDS = "Kids";
	String PDF_COUNT = "Count";
	String PDF_MEDIABOX = "MediaBox";
	String PDF_CROPBOX = "CropBox";
	String PDF_BLEEDBOX = "BleedBox";
	String PDF_TRIMBOX = "TrimBox";
	String PDF_ARTBOX = "ArtBox";
	String PDF_ROTATE = "Rotate";
	String PDF_PARENT = "Parent";
	String PDF_RESOURCES = "Resources";
	String PDF_CONTENTS = "Contents";
	String PDF_PROCSET = "ProcSet";
	String PDF_FONT = "Font";
	String PDF_PDF = "PDF";
	String PDF_TEXT = "Text";
	String PDF_IMAGEC = "ImageC";
	String PDF_IMAGEI = "ImageI";
	String PDF_IMAGEB = "ImageB";
	String PDF_EOF = "%%EOF";
	String PDF_PREV = "Prev";
	String PDF_ENCRYPT = "Encrypt";
	String PDF_ID = "ID";
	String PDF_INFO = "Info";
    
    /*
    String TEXT = "This document was created using Gnostice PDFOne Java Trial";
    String SITE = "www.gnostice.com";
    */
	
	char PDF_NAMESTART = '/';
	char PDF_ARRAYSTART = '[';
	char PDF_ARRAYEND = ']';
	char PDF_NEWLINE = '\n';
	char PDF_CARRIAGE = '\r';
	char PDF_TAB = '\t';
	char PDF_FORMFEED = '\f';
	char PDF_SP = ' ';
	char PDF_LITERALSTRINGSTART = '(';
	char PDF_LITERALSTRINGEND = ')';
	char PDF_HEXSTRINGSTART = '<';
	char PDF_HEXSTRINGEND = '>'; 
	char PDF_SINGLE_QUOTES = '\'';
	
	String PDF_TYPE = "Type";
	String PDF_WIDTHS = "Widths";
	String PDF_FDESCRIPTOR = "FontDescriptor";
	String PDF_FONTNAME = "FontName";
	String PDF_FONTBBOX = "FontBBox";
	String PDF_ASCENT = "Ascent";
	String PDF_DESCENT = "Descent";
	String PDF_CAPHEIGHT = "CapHeight";
	String PDF_AVGWIDTH = "AvgWidth";
	String PDF_MAXWIDTH = "MaxWidth";
	String PDF_MISSINGWIDTH = "MissingWidth";
	String PDF_HORIZ_STEM = "StemH";
	String PDF_VERT_STEM = "StemV";
	String PDF_ITALANGLE = "ItalicAngle";
	String PDF_FIRSTCHAR = "FirstChar";
	String PDF_LASTCHAR = "LastChar";
	String PDF_CATALOG = "Catalog";
	String PDF_OUTLINES = "Outlines";
	String PDF_METADATA = "Metadata";
	String PDF_NAMES = "Names"; 
	String PDF_DESTS = "Dests";
	String PDF_PAGES = "Pages";
	String PDF_PAGE = "Page";
	String PDF_LENGTH = "Length";
	String PDF_STREAM = "stream";
	String PDF_ENDSTREAM = "endstream";
	String PDF_SUBTYPE = "Subtype";
	String PDF_IMAGE = "Image";
	String PDF_NAME = "Name";
	String PDF_BEGINTEXT = "BT";
	String PDF_ENDTEXT = "ET";
	String PDF_TEXTFONT = "Tf";
	String PDF_TEXTDIMENSION = "Td";
	String PDF_SHOWTEXT = "Tj";
	String PDF_TEXTRENDER = "Tr";
	String PDF_TEXTLEAD = "TL";
	String PDF_TEXTWIDTH = "Tw";
	String PDF_TEXTNEWLINESTART = "T*";
	String PDF_TEXTMATRIX = "Tm";
	String PDF_COLOR = "C";
	String PDF_DATE_FORMAT = "yyyyMMddHHmmssZ";
	String PDF_DATE = "D:";
	
    //For Font Dicts
    String PDF_BASEFONT = "BaseFont";
    String PDF_ENCODING = "Encoding";
    String PDF_TYPE1 = "Type1";
    String PDF_TRUETYPE = "TrueType";
    String PDF_TYPE0 = "Type0";
    String PDF_WINANSIENCODING = "WinAnsiEncoding";
    String PDF_DESCENDANT_FONTS = "DescendantFonts";
    String PDF_TOUNICODE = "ToUnicode";
    String PDF_CIDFONT_TYPE1 = "CIDFontType1";
    String PDF_CIDFONT_TYPE2 = "CIDFontType2";
    String PDF_CIDSYSTEM_INFO = "CIDSystemInfo";
    String PDF_CID_TO_GID_MAP = "CIDToGIDMap";
    String PDF_DW = "DW";
    
    //For Fonts with embedding
    String PDF_FONTFILE = "FontFile";
    String PDF_FONTFILE_2 = "FontFile2";
    String PDF_FontFile_3 = "FontFile3";
    String RUBICON_EMBEDDED = "Rubicon_Embedded";
    String PDF_DESCENDANT = "DescendantFonts";
    String PDF_LENGTH_1 = "Length1";
    String PDF_LENGTH_2 = "Length2";
    String PDF_LENGTH_3 = "Length3";
    
	//For Catalog
	String PDF_VERSION = "Version";
	String PDF_PAGELABELS = "PageLabels";
	String PDF_PAGELAYOUT = "PageLayout";
	String PDF_THREADS = "Threads";
	String PDF_STRUCT_TREE = "StructTreeRoot";
	String PDF_MARKINFO = "MarkInfo";
	String PDF_LANG = "Lang";
	String PDF_SPIDERINFO = "SpiderInfo";
	String PDF_OUTPUTINTENTS = "OutputIntents";
	String PDF_PIECEINFO = "PieceInfo";
	String PDF_OCPROPERTIES = "OCProperties";
	String PDF_PERMS = "Perms";
	String PDF_LEGAL = "Legal";
	
	//For Page Resources
	String PDF_EXTGSTATE = "ExtGState";
	String PDF_SHADING = "Shading";
	String PDF_PROPERTIES = "Properties";
    String PDF_GROUP = "Group";
	
	//For Names Trees
	String PDF_JAVASCRIPT = "JavaScript";
	String PDF_TEMPLATES = "Templates";
	String PDF_IDS = "IDS";
	String PDF_URLS = "URLS";
	String PDF_EMBEDDEDFILES = "EmbeddedFiles";
	String PDF_ALTERNATEPRESENTATIONS = "AlternatePresentations";
	String PDF_RENDITIONS = "Renditions";
	
	//For Annotations
	String PDF_ANNOTS = "Annots";
	String PDF_ANNOT = "Annot";
	String PDF_TEXTANNOT = "Text";
	String PDF_FREETEXTANNOT = "FreeText";
	String PDF_LINEANNOT = "Line";
	String PDF_CARETANNOT = "Caret";
    String PDF_STAMPANNOT = "Stamp";
	String PDF_OPEN = "Open";
	String PDF_RECT = "Rect";
	String PDF_ANNOT_SUBJECT = "Subj";
    String PDF_ANNOT_NAME = "NM";
	String PDF_T = "T";
	String PDF_ANNOT_DEFAULT_TITLE = "Annotation - Gnostice Rubicon-Java";
	String PDF_BORDER = "Border";
	String PDF_S = "S";
	String PDF_D = "D";
	String PDF_W = "W";
	String PDF_BS = "BS";
	String PDF_DS = "DS";
	String PDF_CA = "CA";
	String PDF_Q = "Q";
	String PDF_M = "M";
	String PDF_L = "L";
	String PDF_LE = "LE";
	String PDF_IC = "IC";
	String PDF_SQUAREANNOT = "Square";
	String PDF_CIRCLEANNOT = "Circle";
	String PDF_POLYGONANNOT = "Polygon";
	String PDF_POLYLINEANNOT = "PolyLine";
	String PDF_LINKANNOT = "Link";
	String PDF_FILEATTACHMENTANNOT = "FileAttachment";
	String PDF_POPUP = "Popup";
    String PDF_INK = "Ink";
	String PDF_BE = "BE";
	String PDF_I = "I";
    String PDF_RI = "RI";
    String PDF_IX = "IX";
	String PDF_VERTICES = "Vertices";
	String PDF_INKLIST = "InkList";
	String PDF_RD = "RD";
	String PDF_DA = "DA";
	String PDF_CL = "CL";
	String PDF_IT = "IT";
	String PDF_QUADPOINTS = "QuadPoints";
	String PDF_FS = "FS";
	String PDF_EF = "EF";
    String PDF_DOS = "DOS";
    String PDF_UNIX = "Unix";
    String PDF_MAC = "Mac";
    String PDF_EMBEDDEDFILE = "EmbeddedFile";
    String PDF_FREE_TEXT_TYPEWRITER = "FreeTextTypeWriter";
    
	//For Document Information
	String PDF_TITLE = "Title";
	String PDF_AUTHOR = "Author";
	String PDF_DOC_SUBJECT = "Subject";
	String PDF_KEYWORDS = "Keywords";
	String PDF_CREATOR = "Creator";
	String PDF_PRODUCER = "Producer";
	String PDF_CREATIONDATE = "CreationDate";
	String PDF_MODDATE = "ModDate";
	
	//Viewer Preference
	String PDF_VIEWER_PREFERENCES = "ViewerPreferences";
	String PDF_HIDE_TOOLBAR = "HideToolbar";
	String PDF_HIDE_MENUBAR = "HideMenubar";
	String PDF_HIDE_WINDOWUI = "HideWindowUI";
	String PDF_FIT_WINDOW = "FitWindow";
	String PDF_CENTER_WINDOW = "CenterWindow";
	String PDF_DISPLAY_DOCTITLE = "DisplayDocTitle";
    String PDF_DIRECTION = "Direction";
    String PDF_L2R = "L2R";
    String PDF_R2L = "R2L";
    String PDF_NONFULLSCREEN_PAGEMODE = "NonFullScreenPageMode";
    
	//For Outlines
	String PDF_FIRST = "First";
	String PDF_DESTINATION = "Dest";
	String PDF_F = "F";
	String PDF_LAST = "Last";
	String PDF_NEXT = "Next";
	String PDF_A = "A";
	
	//For Page Mode
	String PDF_PAGEMODE = "PageMode";
	String PDF_USENONE = "UseNone";
	String PDF_USEOUTLINES = "UseOutlines";
	String PDF_USETHUMBS = "UseThumbs";
	String PDF_FULLSCREEN = "FullScreen";
	String PDF_USEOC = "UseOC";
	String PDF_USEATTACHMENTS = "UseAttachments";

    //For Page Layout
    String PDF_SINGLEPAGE = "SinglePage";
    String PDF_ONECOLUMN = "OneColumn";
    String PDF_TWOCOLUMN_LEFT = "TwoColumnLeft";
    String PDF_TWOCOLUMN_RIGHT = "TwoColumnRight";
    String PDF_TWOPAGE_LEFT = "TwoPageLeft";
    String PDF_TWOPAGE_RIGHT = "TwoPageRight";

	//For Action
	String PDF_ACTION = "Action";
	String PDF_OPEN_ACTION = "OpenAction";
	String PDF_LAUNCH_ACTION = "Launch";
	String PDF_GOTO_ACTION = "GoTo";
	String PDF_URI_ACTION = "URI";
	String PDF_JAVASCRIPT_ACTION = "JavaScript";
	String PDF_JS = "JS";
	String PDF_O = "O";
	String PDF_C = "C";
	String PDF_AA = "AA";
	String PDF_REMOTEGOTO_ACTION = "GoToR";
	String PDF_NEWWINDOW = "NewWindow";
	String PDF_NAMED = "Named";
	String PDF_N = "N";
	String PDF_NEXT_PAGE = "NextPage";
	String PDF_PREV_PAGE = "PrevPage";
	String PDF_FIRST_PAGE = "FirstPage";
	String PDF_LAST_PAGE = "LastPage";
		
	//For Destination
	String PDF_XYZ = "XYZ";
	String PDF_FIT = "Fit";
	String PDF_FITH = "FitH";
	String PDF_FITV = "FitV";
	String PDF_FITR = "FitR";
	String PDF_FITB = "FitB";
	String PDF_FITBH = "FitBH";
	String PDF_FITBV = "FitBV";
	
	//For Form Fields
	String PDF_V = "V";
	String PDF_YES = "Yes";
	String PDF_BBOX = "BBox";
	String PDF_XOBJECT = "XObject";
	String PDF_FORM = "Form";
	String PDF_H = "H";
	String PDF_P = "P";
	String PDF_BC = "BC";
	String PDF_BG = "BG";
	String PDF_MK = "MK";
	String PDF_B = "B";
	String PDF_AS = "AS";
	String PDF_OFF = "Off";
	String PDF_AP = "AP";
	String PDF_FT = "FT";
	String PDF_BTN = "Btn";
	String PDF_FIELDS = "Fields";
	String PDF_ACROFORM = "AcroForm";
	String PDF_FIELD_FLAG = "Ff";
	String PDF_RESET_FORM = "ResetForm";
	String PDF_SUBMIT_FORM = "SubmitForm";
	String PDF_FLAGS = "Flags";
	String PDF_URL = "URL";
	String PDF_AC = "AC";
	String PDF_RC = "RC";
    String PDF_TP = "TP";
    String PDF_IF = "IF";
	String PDF_R = "R";
	String PDF_TX = "Tx";
	String PDF_DV = "DV";
	String PDF_MAXLEN = "MaxLen";
	String PDF_WIDGET = "Widget";
	String PDF_CH = "Ch";
	String PDF_OPT = "Opt";
	String PDF_BMC = "BMC";
	String PDF_EMC = "EMC";
	String PDF_RE = "re";
	String PDF_TU = "TU";
	String PDF_FILESPEC = "Filespec";
	String PDF_IMPORTDATA = "ImportData";
	String PDF_DR = "DR";
	
    String PDF_E = "E";
	String PDF_X = "X";
	String PDF_U = "U";
	String PDF_FO = "Fo";
	String PDF_BL = "Bl";
    String PDF_PAGEOPEN = "PO";
    String PDF_PAGECLOSE = "PC";
    String PDF_PAGEVISIBLE = "PV";
    String PDF_PAGEINVISIBLE = "PI";
    String PDF_KEYSTROKE = "K";
    String PDF_BEFOREFORMAT = "F";
    String PDF_VALUECHANGE = "V";
    String PDF_RECALCULATE = "R";
    
	String PDF_TM = "TM";
    String PDF_NEEDAPPEARANCES = "NeedAppearances";
    
	//For Pattern
	String PDF_CSP = "CSP";
	String PDF_CS = "cs";
	String PDF_PFD = "PFD";
	String PDF_PBD = "PBD";
	String PDF_PV = "PV";
	String PDF_PH = "PH";
	String PDF_PC = "PC";
	String PDF_PDC = "PDC";
	String PDF_SCN = "scn";
	String PDF_ENDPATH = "n";
	String PDF_PATTERN = "Pattern";
	String PDF_PATTERN_TYPE = "PatternType";
	String PDF_PAINT_TYPE = "PaintType";
	String PDF_TILING_TYPE = "TilingType";
	String PDF_XSTEP = "XStep";
	String PDF_YSTEP = "YStep";
	
	//For Images
	String PDF_WIDTH = "Width";
	String PDF_HEIGHT = "Height";
	String PDF_BITS_PER_COMPONENT = "BitsPerComponent";
	String PDF_COLORSPACE = "ColorSpace";
	String PDF_DEVICE_RGB = "DeviceRGB";
	String PDF_DEVICE_GRAY = "DeviceGray";
	String PDF_INDEXED = "Indexed";
	String PDF_STORE_GS = "q";
	String PDF_RESTORE_GS = "Q";
	String PDF_SHOWIMG = "Do";
	String PDF_CM = "cm";
	String PDF_THUMB = "Thumb";
	String PDF_EOCLIP = "W*";
	
	//For Filters
	String PDF_FILTER = "Filter";
	String PDF_FLATE = "FlateDecode";
	String PDF_ASCIIHEX = "ASCIIHexDecode";
	String PDF_ASCII85 = "ASCII85Decode";
	String PDF_RUNLENGTH = "RunLengthDecode";
	String PDF_DCTDECODE = "DCTDecode";
	
	String PDF_FLATE_NEW = "Fl";
	String PDF_ASCIIHEX_NEW = "AHx";
	String PDF_ASCII85_NEW = "A85";
	String PDF_RUNLENGTH_NEW = "RL";
	String PDF_DCTDECODE_NEW = "DCT";
	
	String PDF_DECODEPARMS = "DecodeParms";
	String PDF_DP = "DP";
	String PDF_PREDICTOR = "Predictor";
	String PDF_COLOMNS = "Columns";
	
	//For Object Stream & CrossRef Stream
	String PDF_OBJSTREAM = "ObjStm";
	String PDF_NO_COMP_OBJ = "N";
	String PDF_XREFSTREAM = "XRef";
	String PDF_XREFSTMOFFSET = "XRefStm";
	String PDF_INDEX = "Index";
    
    //For Page Presentation
    String PDF_DISPLAY_DURATION = "Dur";
    String PDF_TRANSITION = "Trans";
    String PDF_SPLIT = "Split";
    String PDF_BLINDS = "Blinds";
    String PDF_BOX = "Box";
    String PDF_WIPE = "Wipe";
    String PDF_DISSOLVE = "Dissolve";
    String PDF_GLITTER = "Glitter";
    String PDF_REPLACE = "R";
    String PDF_FLY = "Fly";
    String PDF_PUSH = "Push";
    String PDF_COVER = "Cover";
    String PDF_UNCOVER = "Uncover";
    String PDF_FADE = "Fade";
    String PDF_DM = "Dm";
    String PDF_DI = "Di";
    String PDF_HORIZONTAL = "H";
    String PDF_VERTICAL = "V";
    String PDF_INWARD = "I";
    String PDF_OUTWARD = "O";
    String PDF_SS = "SS";
}