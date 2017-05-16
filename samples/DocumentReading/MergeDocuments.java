import java.io.IOException;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfReader;

public class MergeDocuments
{
    /* Usage : java MergeDocuments <inputfile1> <inputfile2> .. <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        //////////////////////////////
        // Merging of PDF Documents //
        //////////////////////////////

        try{
        /* Create a PdfReader instance for the PDF file */
        PdfReader r = PdfReader.fileReader(args[0] , args[args.length - 1]);

        /* Create a PdfDocument instance with the PdfReader */
        PdfDocument d = new PdfDocument(r);

        java.util.List lis = new java.util.ArrayList();
        for (int i = 1; i < args.length - 1 ; i++)
        {
          lis.add(args[i]);
        }
        /* Merge documents in the list */
        d.merge(lis);

        d.setOpenAfterSave(true);
        d.write();
        r.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java MergeDocuments " +
                    "<inputfile1> <inputfile2> .. <output file path>");
        }
    }
}
