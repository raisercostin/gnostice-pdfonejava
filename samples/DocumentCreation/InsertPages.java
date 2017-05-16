import java.io.IOException;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfPage;
import com.gnostice.pdfone.PdfWriter;

public class InsertPages
{
    /* Usage : java InsertPages <FilepathToExtract> <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        ////////////////////////////////////////////////////
        // Create a doc and insert pages from another PDF //
        ////////////////////////////////////////////////////

        try{
        /* Create a PdfWriter instance for the PDF file */
        PdfWriter w = PdfWriter.fileWriter(args[1]);

        /* Create a PdfDocument instance with the PdfWriter */
        PdfDocument d = new PdfDocument(w);

        /* Add some new pages to the document */
        PdfPage p;
        for (int i = 0; i < 5; i++)
        {
            p = new PdfPage();
            d.add(p);
        }

        /*
         Extract pages 1 to 3 from file specified by command-line
         argument and insert them to this document after page 2
        */
        d.insertPagesFrom(args[0], "1-3", 2);

        d.setOpenAfterSave(true);
        d.write();
        w.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java InsertPages " +
                    "<FilepathToExtract> <output file path>");
        }
    }
}
