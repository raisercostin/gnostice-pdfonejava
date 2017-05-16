import java.io.IOException;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfPageMode;
import com.gnostice.pdfone.PdfReader;

public class ManageThumbNails
{
    /* Usage : java ManageThumbNails <input file path> <output file path> */
    /* Supply PDF file with ThumbNails as "input file path" */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        ///////////////////////////////////////////
        // Open a documnet and delete thumbnails //
        // from its document outline             //
        ///////////////////////////////////////////

        try{
        /* Create a PdfReader instance for the PDF file */
        PdfReader r = PdfReader.fileReader(args[0] , args[1]);

        /* Create a PdfDocument instance with the PdfReader */
        PdfDocument d = new PdfDocument(r);

        /* Set the page mode for the document */
        d.setPageMode(PdfPageMode.USETHUMBS);

        /* Remove all thumbnail images from the document outline */
        d.removeThumbnailImage("1-"
            + Integer.toString(d.getPageCount()));

        d.setOpenAfterSave(true);
        d.write();
        r.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java ManageThumbNails" +
                    " <input file path> <output file path>");
        }
    }
}
