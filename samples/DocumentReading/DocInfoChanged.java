import java.io.IOException;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfReader;

public class DocInfoChanged
{

    /* Usage : java DocInfoChanged <input file path> <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        // //////////////////////////////////////////////
        // Change a document's information Properties  //
        // //////////////////////////////////////////////

        try
        {
            /* Create a PdfReader instance for the PDF file */
            PdfReader r = PdfReader.fileReader(args[0], args[1]);

            /* Create a PdfDocument instance with the PdfReader */
            PdfDocument d = new PdfDocument(r);

            /*
             * Set various entries in the document information
             * dictionary of the document
             */
            d.setTitle("PDFOne Java document properties Changed");
            d.setAuthor("Danny Developer Changed");
            d.setSubject("PDFOne Java document properties" +
                    " setting demo Changed");
            d.setKeywords("These doc info properties can be in "
                    + "unicode charsets supported by Adobe for" +
                            " this window  Changed.");

            d.setOpenAfterSave(true);
            d.write();
            r.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java DocInfoChanged" +
                    " <input file path> <output file path>");
        }
    }
}
