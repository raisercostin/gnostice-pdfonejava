import java.io.IOException;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfReader;

public class DeletePages
{
    /* Usage : java DocInfoChanged <input file path> <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        /////////////////////////////////////////////////
        // Open a document and delete some pages in it //
        /////////////////////////////////////////////////

        try{
        /* Create a PdfReader instance for the PDF file */
        PdfReader r = PdfReader.fileReader(args[0] , args[1]);

        /* Create a PdfDocument instance with the PdfReader */
        PdfDocument d = new PdfDocument(r);

        /* Delete pages 1 to 3 from the document */
        d.deletePages("1-3");
        
        d.setOpenAfterSave(true);
        d.write();
        r.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java DocInfoChanged " +
                    "<input file path> <output file path>");
        }
    }
}
