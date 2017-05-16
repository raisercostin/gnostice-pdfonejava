import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfWriter;

public class WriteToMemory
{
    /* Usage : java WriteToMemory <output file path> */
    public static void main(String[] args) throws IOException,
    PdfException
    {
        //////////////////////////////////////////
        // Write a document's content to memory //
        //////////////////////////////////////////
        try{
        /* Create a ByteArrayOutputStream object */
        ByteArrayOutputStream boas = new ByteArrayOutputStream();

        /* Create a new PdfWriter object with the
         ByteArrayOutputStream object */
        PdfWriter writer = PdfWriter.memoryWriter(boas);

        /* Create a PdfDocument object with the PdfWriter object */
        PdfDocument document = new PdfDocument(writer);

        /* Write a line of text to the document */
        document.writeText("A new file was created using PdfWriter "
                           + "with ByteArrayOutputStream.");

        document.setOpenAfterSave(true);

        /* 
         Writes the PdfDocument object to the ByteArrayOutputStream
         object 
        */
        document.write();

        /*
         Sets the output file to be used by the ByteArrayOutputStream 
         object 
        */
        OutputStream os = new FileOutputStream(args[0]);

        /* Writes to the output file */
        boas.writeTo(os);

        /* Frees the PdfWriter object */
        writer.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java WriteToMemory" +
                    " <output file path>");
        }
    }

}
