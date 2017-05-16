import java.io.IOException;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfPage;
import com.gnostice.pdfone.PdfWriter;

public class ExtractPages
{
    /* Usage : java ExtractPages <ExtractToFilepath> <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        //////////////////////////////////////////////
        // Extract pages from current document and  //
        // append them to another document          //
        //////////////////////////////////////////////
        try{
        /* Create a PdfWriter instance for the PDF file */
        PdfWriter w = PdfWriter.fileWriter(args[1]);

        /* Create a PdfDocument instance with the PdfWriter */
        PdfDocument d = new PdfDocument(w);

        /* Add some pages to the document */
        PdfPage p;
        for (int i = 0; i < 5; i++)
        {
            p = new PdfPage();
            d.add(p);
            p.writeText("This is Page:" + (i+1));
        }

        /*
         Extract pages 2 to 4 from current document and 
         them to file specified in command-line argument
        */
        d.extractPagesTo(args[0], "2-4");

        d.setOpenAfterSave(true);
        d.write();
        w.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java ExtractPages " +
                    "<ExtractToFilepath> <output file path>");
        }
    }
}
