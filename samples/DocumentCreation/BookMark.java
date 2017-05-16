import java.awt.Color;
import java.io.IOException;

import com.gnostice.pdfone.PdfAction;
import com.gnostice.pdfone.PdfBookmark;
import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfMeasurement;
import com.gnostice.pdfone.PdfPage;
import com.gnostice.pdfone.PdfPageMode;
import com.gnostice.pdfone.PdfPageSize;
import com.gnostice.pdfone.PdfWriter;
import com.gnostice.pdfone.encodings.PdfEncodings;
import com.gnostice.pdfone.fonts.PdfFont;

public class BookMark
{
    /* Usage : java BookMark <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        /////////////////////////////////////
        // Creating bookmark in a document //
        /////////////////////////////////////

        try{
        /* Create a PdfWriter instance for the PDF file */
        PdfWriter w = PdfWriter.fileWriter(args[0]);

        /* Create a PdfDocument instance with the PdfWriter */
        PdfDocument d = new PdfDocument(w);

        /* Specify page mode for the document */
        d.setPageMode(PdfPageMode.USEOUTLINES);

        /* Create a font and set its color */
        PdfFont arialFont = PdfFont.create("Arial",
            PdfFont.BOLD, 25, PdfEncodings.CP1252);
        arialFont.setColor(Color.GREEN);

        PdfPage p;

        /* Add 20 new pages to the document */
        for (int i = 0; i < 20; i++)
        {
            p = new PdfPage(PdfPageSize.A3);
            p.setMeasurementUnit(PdfMeasurement.MU_INCHES);
            d.add(p);
        }
        
        /* Create a bookmark that leads to page 8 */
        d.addBookmark("Page8", d.getBookmarkRoot(), 8);
        PdfBookmark b = d.addBookmark("Chapter Two",
            d.getBookmarkRoot(), 10);
        b = d.addBookmark("Page12", d.getBookmarkRoot(), 12);

        /*
         Adds a bookmark that leads to page 14 as a child of
         bookmark b. Sets b to the new bookmark.
        */
        b = d.addBookmark("Page14", b, 14);

        /*
         Adds a new bookmark that leads to page 16 next to
         bookmark b.
        */
        b.addNext("Page16", 16);

        d.addBookmark("Page19", d.getBookmarkRoot(), 19);

        /* 
         Adds a bookmark with an action to open the
         Gnostice website's URL inside a browser window
       */
        d.addBookmark("Home Page URL", d.getBookmarkRoot(),
            "http://www.gnostice.com", PdfAction.URI);

        d.setOpenAfterSave(true);
        d.write();
        w.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java DocInfoChanged " +
                    "<output file path> ");
        }
    }
}
