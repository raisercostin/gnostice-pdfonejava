import java.awt.Color;
import java.io.IOException;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfMeasurement;
import com.gnostice.pdfone.PdfPage;
import com.gnostice.pdfone.PdfTextFormatter;
import com.gnostice.pdfone.PdfWriter;
import com.gnostice.pdfone.encodings.PdfEncodings;
import com.gnostice.pdfone.fonts.PdfFont;

public class TextDemoTwo
{
    /* Usage : java TextDemoTwo <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        /////////////////////////////////
        // Writng text on the document //
        /////////////////////////////////
        try{
        
        /* Create a PdfWriter instance for the PDF file */
        PdfWriter w = PdfWriter.fileWriter(args[0]);

        /* Create a PdfDocument instance with the PdfWriter */
        PdfDocument d = new PdfDocument(w);

        /* Create a font */
        PdfFont arialFont = PdfFont.create("Arial", PdfFont.BOLD
            | PdfFont.UNDERLINE | PdfFont.ITALIC
            | PdfFont.STROKE, 20, PdfEncodings.CP1252);

        PdfFont timesFont = PdfFont.create("Times New Roman", 12,
            PdfEncodings.CP1252);

        arialFont.setStrokeColor(Color.BLUE);

        PdfPage p;
        for (int i = 1; i <= 3; i++)
        {
            /* Create a new page */
            p = new PdfPage();
            p.setMeasurementUnit(PdfMeasurement.MU_INCHES);

            /* 
             Obtain PdfTextFormatter object of the page and set 
             text alignment and wrap setting
            */
            p.getTextFormatter().setWrap(PdfTextFormatter.WRAP);
            p.getTextFormatter().setAlignment(PdfTextFormatter.LEFT);

            /* Write text on the page */
            p.writeText("Betty made some butter but the butter was bitter" +
                " so Bet", timesFont);

            p.writeText("This is an underlined, Left aligned Text."
                + " The call to write this was made in the middle "
                + "of writing the above text.", arialFont, 0, 1);

            p.writeText("ty made some more butter to make the bitter bu"
                , timesFont);

            p.writeText("This is an underlined, Left aligned Text"
                + " written again. Once again the call to write"
                + " this was made in the middle.", arialFont, 0, 2);

            p.writeText("tter better.", timesFont);

            /* Change the font property */
            timesFont.setStyle(timesFont.getStyle() | PdfFont.UNDERLINE);

            p.writeText(" This part of text is written to illlustrate that"
                + " the font style can be changed on the fly. Watch this"
                + " underline.", timesFont);

            /* Add page to the document */
            d.add(p);
        }

        d.setOpenAfterSave(true);
        d.write();
        w.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java TextDemoTwo" +
                    " <output file path>");
        }
    }
}
