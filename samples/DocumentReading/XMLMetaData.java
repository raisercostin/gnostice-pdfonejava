import java.io.IOException;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfReader;

public class XMLMetaData
{
   
    /* Usage : java XMLMetaData <input file path> <output file path> */
    /* Supply PDF file with XML metadata as "input file path"*/
    public static void main(String[] args) throws IOException,
        PdfException
    {
        ////////////////////////////////////////////////
        // Open a doc and print XML MetaData  from it //
        ////////////////////////////////////////////////
        
        try{
        /* Create a PdfReader instance for the PDF file */
        PdfReader r = PdfReader.fileReader(args[0] , args[0]);

        /* Create a PdfDocument instance with the PdfReader */
        PdfDocument d = new PdfDocument(r);

        /* Retrieve XML Metadata of the document as a string */
        String s = d.getXMLMetadata();
        
        System.out.println(s);
        d.writeText(s.trim());
        
        d.setOpenAfterSave(true);
        d.write();
        r.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java XMLMetaData" +
                    " <input file path> <output file path>");
        }
    }
}
