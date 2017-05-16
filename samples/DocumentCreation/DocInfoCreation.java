import java.io.IOException;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfWriter;

public class DocInfoCreation
{
    /* Usage : java DocInfoCreation <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        ////////////////////////////////////////////////////////
        // Setting entries in document information dictionary //
        ////////////////////////////////////////////////////////

        try{
        /* Create a PdfWriter instance for the PDF file */
        PdfWriter w = PdfWriter.fileWriter(args[0]);

        /* Create a PdfDocument instance with the PdfWriter */
        PdfDocument d = new PdfDocument(w);

        /* Set various document information properties */
        d.setTitle("PDFOne Java document properties");
        d.setAuthor("Danny Developer");
        d.setSubject("PDFOne Java document properties setting demo");
        d.setKeywords("These doc info properties can be in " +
                "unicode charsets supported by Adobe for this window.");

        d.setOpenAfterSave(true);
        d.write();
        w.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java DocInfoCreation " +
                    "<output file path> ");
        }
    }
}
