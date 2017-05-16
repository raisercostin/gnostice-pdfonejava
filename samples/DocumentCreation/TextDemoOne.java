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

public class TextDemoOne
{
    /* Usage : java TextDemoOne <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        ////////////////////////////////
        // Writng text on a document //
        ////////////////////////////////
        
        try{
        /* Create a PdfWriter instance for the PDF file */
        PdfWriter w = PdfWriter.fileWriter(args[0]);

        /* Create a PdfDocument instance with the PdfWriter */
        PdfDocument d = new PdfDocument(w);

        PdfPage p;

        /* Create a font */
        PdfFont font = PdfFont.create("Arial", PdfFont.BOLD
            | PdfFont.UNDERLINE | PdfFont.ITALIC
            | PdfFont.STROKE_AND_FILL, 30, PdfEncodings.CP1252);

        for (int i = 1; i <= 3; i++)
        {
            /* Create a new page */
            p = new PdfPage();
            p.setMeasurementUnit(PdfMeasurement.MU_INCHES);

            /*
             Retrieve the PdfTextFormatter object for the page
             and set the wrap setting to true
            */
            p.getTextFormatter().setWrap(PdfTextFormatter.WRAP);
            /* Set color of the font */
            font.setStrokeColor(Color.RED);
            font.setColor(Color.BLACK);

            /* Set text alignment on the page */
            p.getTextFormatter().setAlignment(PdfTextFormatter.LEFT);
            p.writeText("This is an underlined, Left aligned Text",
                font, 0, 1);

            font.setStrokeColor(Color.BLUE);
            font.setColor(Color.RED);
            p.getTextFormatter().setAlignment(PdfTextFormatter.RIGHT);
            p.writeText("This is an underlined, Right aligned Text",
                font, 0, 3);

            font.setStrokeColor(Color.YELLOW);
            font.setColor(Color.BLACK);

            /* Retrieve and set font size */
            int prevFontSize = font.getSize();
            font.setSize(20);

            /* Set text alignment for the page */
            p.getTextFormatter()
                .setAlignment(PdfTextFormatter.CENTER);
            p.writeText("This is an underlined, Center aligned Text",
                font, 0, 5);

            font.setStrokeColor(Color.GREEN);
            font.setColor(Color.RED);
            font.setSize(prevFontSize);

            p.getTextFormatter().setAlignment(
                PdfTextFormatter.JUSTIFIED);
            p.writeText("This is an underlined, Justified Text."
                        + " This is an additional line intoduced to illustrate it",
                    font, 0.1, 7);

            /* Add page to the document */
            d.add(p);
        }

        d.setOpenAfterSave(true);
        d.write();
        w.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java TextDemoOne" +
                    " <output file path>");
        }
        
    }
}
