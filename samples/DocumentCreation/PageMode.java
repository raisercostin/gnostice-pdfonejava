import java.io.IOException;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfPageMode;
import com.gnostice.pdfone.PdfWriter;

public class PageMode
{
    /* Usage : java PageMode <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        ////////////////////////////////
        // Setting document page mode //
        ////////////////////////////////

        try{
        /* Create a PdfWriter instance for the PDF file */
        PdfWriter w = PdfWriter.fileWriter(args[0]);

        /* Create a PdfDocument instance with the PdfWriter */
        PdfDocument d = new PdfDocument(w);

        /* Set page mode for the document */
        d.setPageMode(PdfPageMode.USEOUTLINES);

        d.setOpenAfterSave(true);
        d.write();
        w.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java PageMode" +
                    " <output file path>");
        }
    }
}
