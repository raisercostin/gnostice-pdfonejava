import java.awt.Color;
import java.io.File;
import java.io.IOException;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfPage;
import com.gnostice.pdfone.PdfPageSize;
import com.gnostice.pdfone.PdfWriter;
import com.gnostice.pdfone.encodings.PdfEncodings;
import com.gnostice.pdfone.fonts.PdfFont;

public final class SinglePageMultiDoc
{
    /* Usage : java SinglePageMultiDoc <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        //////////////////////////////////////////
        // Adding a single PdfPage instance to  //
        // multiple PdfDocument instances       //
        //////////////////////////////////////////

        try{
        /* Create a font and set its color */
        PdfFont arialFont = PdfFont.create("Arial",
            PdfFont.BOLD, 25, PdfEncodings.CP1252);
        arialFont.setColor(Color.GREEN);

        char pathSeparator = File.separatorChar;
        /* Create a PdfWriter instance for the first PDF file */
        PdfWriter firstWriter = PdfWriter.fileWriter(args[0]);

        /* Create a PdfDocument instance with the first PdfWriter */
        PdfDocument firstDoc = new PdfDocument(firstWriter);

        /* Create a PdfWriter instance for the second PDF file */
        PdfWriter secondWriter = PdfWriter.fileWriter("."
            + pathSeparator + "PDFs" + pathSeparator
            + "SinglePageMultiDoc_2.pdf");

        /* Create a second PdfDocument instance with the second PdfWriter */
        PdfDocument secondDoc = new PdfDocument(secondWriter);

        /* 
         Create a PdfPage instance that is to be added to 
         both PdfDocument instances 
        */
        PdfPage masterPage = new PdfPage(PdfPageSize.A4);

        /* 
         Create coordinates
        */
        double x = 72 * 1.5; // equals 1.5 inches, as 1 inch = 72 points
        double y = 36;       // 0.5 inches

        /* Write text on the page at above coordinates */
        masterPage.writeText("This is first line of text.",
            arialFont, x, y);
        /* Add page to the first PdfDocument instance */
        firstDoc.add(masterPage);

        /* Write firstDoc and dispose firstWriter */
        firstDoc.setOpenAfterSave(true);
        firstDoc.write();
        firstWriter.dispose();

        arialFont.setStyle(PdfFont.STROKE);
        arialFont.setStrokeColor(Color.ORANGE);
        arialFont.setStrokeWidth(1);
        /* Write more text to the page and add it to secondDoc */
        masterPage.writeText("This is another line of text.",
            arialFont, x, y * 2);
        secondDoc.add(masterPage);

        /* Write secondDoc and dispose secondWriter */
        secondDoc.setOpenAfterSave(true);
        secondDoc.write();
        secondWriter.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java SinglePageMultiDoc" +
                    " <output file path>");
        }
    }
}
