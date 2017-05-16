import java.awt.Color;
import java.io.File;
import java.io.IOException;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfMeasurement;
import com.gnostice.pdfone.PdfPage;
import com.gnostice.pdfone.PdfWriter;
import com.gnostice.pdfone.encodings.PdfEncodings;
import com.gnostice.pdfone.fonts.PdfFont;

public class FontSupport
{
    /*
     To execute from command line, supply pathnames of
     of three font files
    */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        //////////////////////////////////
        // Various font types supported //
        //////////////////////////////////

        /*
         * PDFOne Java classifies Fonts into four categories
         * 1. Built-in Type1 fonts. (The 14 Acrobat built-in fonts)
         * 2. True type fonts. (With TTF and OTF file extentions)
         * 3. True Type Collections. (With TTC file extention)
         * 4. Unicode fonts (OTF, TTF and TTC for unicode text)
         *
         * All, but first, of these fonts types can be embedded into PDF.
         *
         * This code snippet demonstrated how to create PDFFont object
         * for each category.
         *
         */

        char pathSeparator = File.separatorChar;
        /* Create a PdfWriter instance for the PDF file */
        PdfWriter w = PdfWriter.fileWriter("." + pathSeparator
            + "PDFs" + pathSeparator + "FontSupport.pdf");

        /* Create a PdfDocument instance with the PdfWriter */
        PdfDocument d = new PdfDocument(w);

        /* Create a new page */
        PdfPage p = new PdfPage(800, 900, 0, 0, 100, 50, 50, 50,
            PdfMeasurement.MU_POINTS);
        p.setMeasurementUnit(PdfMeasurement.MU_INCHES);

        /* 1. Built-in (type-1) fonts - the 14 Acrobat built-in fonts
         Here we demonstrate only Arial (Helvetica) font. Please refer
         documentation for other supported fonts.
        */
        PdfFont builtinArialFont = PdfFont.create("Arial", PdfFont.BOLD
            | PdfFont.ITALIC, 20, PdfEncodings.CP1252);

        builtinArialFont.setColor(Color.BLUE);

        p.writeText("This is Built-in Type1 font [Arial].",
            builtinArialFont, 1, 1);

        /* 2. True type font - with TTF and OTF file extentions */
        String ttfFontPath = args[0];
        if(ttfFontPath != null)
        {
            PdfFont ttfFont = PdfFont.create(ttfFontPath,
                PdfFont.UNDERLINE, 20, PdfEncodings.CP1252);

            ttfFont.setColor(Color.GREEN);

            p.writeText(
                "This is a TTF font, with full embedding.",
                ttfFont, 1, 2);
        }



        /* 3. True Type Collections - With TTC file extention.
         A true type collection is a file with multiple TTF fonts
         in a single file with .ttc extension. To specify particular
         TTF font within the TTC file, suffix the TTF index in the 
         command-line argument.

         Example: Assuming there is a TTC file XYZ.ttc with 3 TTF fonts.
         To select 2nd TTF font within the TTC file, provide file path
         as XYZ.ttc[2] as argument to main().
        */
        String ttcFontPath = args[1];
        if(ttcFontPath != null)
        {
            PdfFont ttcFont = PdfFont.create(ttcFontPath, PdfFont.STROKE,
                20, PdfEncodings.UTF_16BE);

            ttcFont.setStrokeColor(Color.RED);
            ttcFont.setStrokeWidth(1);

            p.writeText("This is a TTC font, with subset embedding.",
                ttcFont, 1, 3);
        }



        /* 4. Unicode fonts - with OTF, TTF and TTC for Unicode text */
        String unicodeFontPath = args[2];
        if(unicodeFontPath != null)
        {
            builtinArialFont.setStyle(PdfFont.PLAIN);
            builtinArialFont.setColor(Color.BLACK);

            /* Write some descriptive text */
            p.writeText("Unicode : ", builtinArialFont, 1, 4);
            p.writeText("    Thai : ", builtinArialFont, 1.9, 4.4);
            p.writeText("Japanese : ", builtinArialFont, 1.5, 4.8);
            p.writeText("   Korean : ", builtinArialFont, 1.5, 5.2);

            /* Create Arial Unicode MS font */
            PdfFont unicodeFont = PdfFont.create(unicodeFontPath, 20,
                PdfEncodings.UTF_16BE, PdfFont.EMBED_SUBSET);

            unicodeFont.setStrokeColor(Color.RED);
            unicodeFont.setColor(Color.BLUE);

            p.writeText("\u0e05 \u0e2c ", unicodeFont, 3.2, 4.4);
            p.writeText("\u3041 \u304e ", unicodeFont, 3.2, 4.8);
            p.writeText("\uac00 \uac01 ", unicodeFont, 3.2, 5.2);
        }

        d.add(p);
        d.setOpenAfterSave(true);
        d.write();
        w.dispose();
    }
}
