import java.io.IOException;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfMeasurement;
import com.gnostice.pdfone.PdfPage;
import com.gnostice.pdfone.PdfWriter;
import com.gnostice.pdfone.encodings.PdfEncodings;
import com.gnostice.pdfone.fonts.PdfFont;

public class HeaderFooter
{
    /* Usage : java HeaderFooter <image1> <image2> <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        //////////////////////////////////////////////
        // Create document with a header and footer //
        //////////////////////////////////////////////

        try{
        /* Create a PdfWriter instance for the PDF file */
        PdfWriter w = PdfWriter.fileWriter(args[2]);

        /* Create a PdfDocument instance with the PdfWriter */
        PdfDocument d = new PdfDocument(w);

        PdfPage p ;

        for (int i = 0; i < 5; i++)
        {
            /*
             Create a page specifying page width, page height, page header,
             footer height, left margin, top margin, right margin, bottom
             margin expressed in a particular measurement unit.
             */
            p = new PdfPage(800, 900, 100, 100, 50, 50, 50, 50,
                PdfMeasurement.MU_POINTS);

            /* Add images to header of the page */
            p.addHeaderImage(args[0], PdfPage.HP_LEFT
                | PdfPage.VP_TOP, true);
            p.addHeaderImage(args[0], PdfPage.HP_RIGHT
                | PdfPage.VP_BOTTOM, true);
            p.addHeaderImage(args[1], PdfPage.HP_MIDDLE
                | PdfPage.VP_CENTRE, true);

            /* Add images to footer of the page */
            p.addFooterImage(args[0], PdfPage.HP_RIGHT
                | PdfPage.VP_TOP, true);
            p.addFooterImage(args[0], PdfPage.HP_LEFT
                | PdfPage.VP_BOTTOM, true);
            p.addFooterImage(args[1], PdfPage.HP_MIDDLE
                | PdfPage.VP_CENTRE, true);

            /* Add text to header of the page */
            p.addHeaderText("Header Center Center", PdfFont.create(
                "Arial", 15, PdfEncodings.CP1252), PdfPage.HP_MIDDLE
                | PdfPage.VP_CENTRE, false);
            p.addHeaderText("Header Center Top", PdfFont.create(
                "Arial", 15, PdfEncodings.CP1252), PdfPage.HP_MIDDLE
                | PdfPage.VP_TOP, false);
            p.addHeaderText("Header Center Bottom", PdfFont.create(
                "Arial", 15, PdfEncodings.CP1252), PdfPage.HP_MIDDLE
                | PdfPage.VP_BOTTOM, false);
            p.addHeaderText("Header Left Center", PdfFont.create(
                "Arial", 15, PdfEncodings.CP1252), PdfPage.HP_LEFT
                | PdfPage.VP_CENTRE, false);
            p.addHeaderText("Header Left Top", PdfFont.create(
                "Arial", 15, PdfEncodings.CP1252), PdfPage.HP_LEFT
                | PdfPage.VP_TOP, false);
            p.addHeaderText("Header Left Bottom", PdfFont.create(
                "Arial", 15, PdfEncodings.CP1252), PdfPage.HP_LEFT
                | PdfPage.VP_BOTTOM, false);
            p.addHeaderText("Header Right Top", PdfFont.create(
                "Arial", 15, PdfEncodings.CP1252), PdfPage.HP_RIGHT
                | PdfPage.VP_CENTRE, false);
            p.addHeaderText("Header Left Top", PdfFont.create(
                "Arial", 15, PdfEncodings.CP1252), PdfPage.HP_RIGHT
                | PdfPage.VP_TOP, false);
            p.addHeaderText("Header Right Bottom", PdfFont.create(
                "Arial", 15, PdfEncodings.CP1252), PdfPage.HP_RIGHT
                | PdfPage.VP_BOTTOM, false);

          /* Add text to footer of the page */
            p.addFooterText("Footer Center Center", PdfFont.create(
                "Arial", 15, PdfEncodings.CP1252), PdfPage.HP_MIDDLE
                | PdfPage.VP_CENTRE, false);
            p.addFooterText("Footer Center Top", PdfFont.create(
                "Arial", 15, PdfEncodings.CP1252), PdfPage.HP_MIDDLE
                | PdfPage.VP_TOP, false);
            p.addFooterText("Footer Center Bottom", PdfFont.create(
                "Arial", 15, PdfEncodings.CP1252), PdfPage.HP_MIDDLE
                | PdfPage.VP_BOTTOM, false);
            p.addFooterText("Footer Left Center", PdfFont.create(
                "Arial", 15, PdfEncodings.CP1252), PdfPage.HP_LEFT
                | PdfPage.VP_CENTRE, false);
            p.addFooterText("Footer Left Top", PdfFont.create(
                "Arial", 15, PdfEncodings.CP1252), PdfPage.HP_LEFT
                | PdfPage.VP_TOP, false);
            p.addFooterText("Footer Left Bottom", PdfFont.create(
                "Arial", 15, PdfEncodings.CP1252), PdfPage.HP_LEFT
                | PdfPage.VP_BOTTOM, false);
            p.addFooterText("Footer Right Top", PdfFont.create(
                "Arial", 15, PdfEncodings.CP1252), PdfPage.HP_RIGHT
                | PdfPage.VP_CENTRE, false);
            p.addFooterText("Footer Left Top", PdfFont.create(
                "Arial", 15, PdfEncodings.CP1252), PdfPage.HP_RIGHT
                | PdfPage.VP_TOP, false);
            p.addFooterText("Footer Right Bottom", PdfFont.create(
                "Arial", 15, PdfEncodings.CP1252), PdfPage.HP_RIGHT
                | PdfPage.VP_BOTTOM, false);

          /* Add the page to document */
          d.add(p);
        }
        
        d.setOpenAfterSave(true);
        d.write();
        w.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java HeaderFooter " +
                    "<image1> <image2> <output file path>");
        }
    }
}
