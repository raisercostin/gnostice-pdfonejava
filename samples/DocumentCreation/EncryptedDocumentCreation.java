import java.io.IOException;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfEncryption;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfWriter;

public final class EncryptedDocumentCreation
{
    /* Usage : java EncryptedDocumentCreation <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        ////////////////////////////////////
        // Creating an encrypted document //
        ////////////////////////////////////

        try{
        /* Create a PdfWriter instance for the PDF file */
        PdfWriter w = PdfWriter.fileWriter(args[0]);

        /* Create a PdfDocument instance with the PdfWriter */
        PdfDocument d = new PdfDocument(w);

        /* Obtain PdfEncryption object of the PdfDocument */
        PdfEncryption e = d.getEncryptor();
        e.setOwnerPwd("owner");
        e.setUserPwd("user");
        e.setLevel(PdfEncryption.LEVEL_128_BIT);
        e.setPermissions(PdfEncryption.AllowHighResPrint
            | PdfEncryption.AllowAccessibility);

        /* Change the PdfEncryption object of the PdfDocument */
        d.setEncryptor(e);

        d.setOpenAfterSave(true);
        d.write();
        w.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java EncryptedDocumentCreation " +
                    "<output file path> ");
        }
    }
}
