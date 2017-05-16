import java.io.IOException;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfReader;

public final class EncryptedDocumentReading 
{
    /* This is the password event handler */
    public static void onPassword(PdfDocument sender,
        StringBuffer password, boolean[] flags)
    {
        password.append("owner");
    }

    /* Usage : java EncryptedDocumentReading <input file path> <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        /////////////////////////////////
        //Reading an encrypted document//
        /////////////////////////////////
        
        try{
        /* Create a PdfReader instance for the PDF file */
        PdfReader r = PdfReader.fileReader(args[0] , args[1]);

        /* Set password event handler for PdfReader instance */
        try
        {
            r.setOnPassword(EncryptedDocumentReading.class
                .getDeclaredMethod("onPassword", new Class[] {
                    PdfDocument.class, StringBuffer.class,
                    boolean[].class }));
        }
        catch (NoSuchMethodException nsme)
        {
            /* should not occur */
        }

        /* Create a PdfDocument instance with the PdfReader */
        PdfDocument d = new PdfDocument(r);

        d.setOpenAfterSave(true);
        d.write();
        r.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java EncryptedDocumentReading " +
                    "<input file path> <output file path>");
        }
    }
}
