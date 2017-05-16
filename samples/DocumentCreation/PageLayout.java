import java.io.IOException;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfPage;
import com.gnostice.pdfone.PdfPageLayout;
import com.gnostice.pdfone.PdfPageSize;
import com.gnostice.pdfone.PdfWriter;

public class PageLayout
{
    /* Usage : java PageLayout <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        // ////////////////////////////////
        // Setting document page layout //
        // ////////////////////////////////
        try{
        /* Create a PdfWriter instance for the PDF file */
        PdfWriter w = PdfWriter.fileWriter(args[0]);

        /* Create a PdfDocument instance with the PdfWriter */
        PdfDocument d = new PdfDocument(w);

        /* Create and add some pages to the document */
        PdfPage p = new PdfPage(PdfPageSize.A3);
        d.add(p);
        p = new PdfPage(PdfPageSize.A3);
        d.add(p);
        p = new PdfPage(PdfPageSize.A3);
        d.add(p);

        /* Set page layout for the document */
        d.setPageLayout(PdfPageLayout.TWO_COLUMN_LEFT);

        d.setOpenAfterSave(true);
        d.write();
        w.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java PageLayout" +
                    " <output file path>");
        }
    }
}
