import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfReader;

public class ReadFromStream
{
    /* Usage : java ReadFromStream <input file path> <output file path> */
    public static void main(String[] args) throws IOException,
    PdfException 
    {
        //////////////////////////////////////////////
        // Create a document from a FileInputStream //
        //////////////////////////////////////////////

        try{
        // Creates a FileInputStream object for the specified 
        // in the command-line argument
        FileInputStream fis = new FileInputStream(args[0]);

        // Creates an OutputStream object for the output file
        OutputStream os = new FileOutputStream(args[1]);

        /*
         * Creates a new PdfReader object with the FileInputStream
         * object and sets its output to the file represented by the
         * OutputStream object
         */
        PdfReader reader = PdfReader.fileStreamReader(fis, os);

        /* Creates a PdfDocument object with the PdfReader object */
        PdfDocument d = new PdfDocument(reader);

        /* Writes a line of text for the output file */
        d.writeText("This text goes in to the Output "
                           + " ReadStream.pdf");

        /* Writes the PdfDocument object to the output file */
        d.write();

        d.setOpenAfterSave(true);

        /* Frees the PdfReader object */
        reader.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java ReadFromStream " +
                    "<input file path> <output file path>");
        }
    }

}
