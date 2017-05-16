import java.io.IOException;

import com.gnostice.pdfone.PdfAction;
import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfMeasurement;
import com.gnostice.pdfone.PdfPage;
import com.gnostice.pdfone.PdfPageMode;
import com.gnostice.pdfone.PdfPageSize;
import com.gnostice.pdfone.PdfWriter;

public class PageAction
{
    /* Usage : java PageAction <ApplicationToLaunch> <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        /////////////////////////////////////////
        // Creating document with page Actions //
        /////////////////////////////////////////
        
        try{
        /* Create a PdfWriter instance for the PDF file */
        PdfWriter w = PdfWriter.fileWriter(args[1]);

        /* Create a PdfDocument instance with the PdfWriter */
        PdfDocument d = new PdfDocument(w);

        /* Set page mode for the document */
        d.setPageMode(PdfPageMode.USEOUTLINES);

        for (int i = 1; i < 6; i++)
        {
            /* Create and add a page to the document */
            PdfPage p;
            p = new PdfPage(PdfPageSize.A3);
            p.setMeasurementUnit(PdfMeasurement.MU_INCHES);
            if (i == 1)
            {
                /* Adds an URI action to the page */
                p.addAction(PdfAction.PdfEvent.ON_PAGE_CLOSE,
                    PdfAction.URI, "http://www.gnostice.com");
            }
            if (i == 2)
            {
                /* Adds a launch action to the page */
                p.addAction(PdfAction.PdfEvent.ON_PAGE_CLOSE,
                        PdfAction.LAUNCH, args[0],
                        false);
            }
            if (i == 4)
            {
                /* Adds a named action to the page */
                p.addAction(PdfAction.PdfEvent.ON_PAGE_OPEN,
                    PdfAction.NAMED_LASTPAGE);
            }
            /* Adds page to the document */
            d.add(p);
        }

        d.setOpenAfterSave(true);
        d.write();
        w.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java PageAction " +
                    "<ApplicationToLaunch> <output file path>");
        }
    }
}
