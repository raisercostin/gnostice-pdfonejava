import java.io.IOException;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfPage;
import com.gnostice.pdfone.PdfWriter;

public class AppendPages
{
    /* Usage : java AppendPages <FilepathToExtract> <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        ///////////////////////////////////////////////////////////////////
        // Create a document and append pages extracted from another PDF //
        ///////////////////////////////////////////////////////////////////

        try{
        /* Create a PdfWriter instance for the PDF file */
        PdfWriter w = PdfWriter.fileWriter(args[1]);

        /* Create a PdfDocument instance with the PdfWriter */
        PdfDocument d = new PdfDocument(w);

        /* Add new pages to the document */
        PdfPage p;
        for (int i = 0; i < 3; i++)
        {
            p = new PdfPage();
            d.add(p);
        }

        /*
          Extract pages 1 to 3 from the file specified in the
          command-line argument and append them to the document
        */
        d.appendPagesFrom(args[0], "1-3");
        
        d.setOpenAfterSave(true);
        d.write();
        w.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java AppendPages " +
                    "<FilepathToExtract> <output file path> ");
        }
    }
}
