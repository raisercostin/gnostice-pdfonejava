import java.io.IOException;

import com.gnostice.pdfone.PdfAction;
import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfPage;
import com.gnostice.pdfone.PdfPageMode;
import com.gnostice.pdfone.PdfPageSize;
import com.gnostice.pdfone.PdfWriter;

public class DocActions
{
    /* Usage : java DocActions <FileToLaunch> <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        ////////////////////////////////////
        // Setting document-level actions //
        ////////////////////////////////////

        try{
        /* Create a PdfWriter instance for the PDF file */
        PdfWriter w = PdfWriter.fileWriter(args[1]);

        /* Create a PdfDocument instance with the PdfWriter */
        PdfDocument d = new PdfDocument(w);

        /*
         Sets page mode to hide Bookmarks, Thumbnails,
         Attachments and other tabs
        */
        d.setPageMode(PdfPageMode.USENONE);
        d.add(new PdfPage(PdfPageSize.A3));

        /* 
         Add action to document so that it launches the file
         specified by the command-line argument
        */
        d.addAction(PdfAction.LAUNCH, args[0], false, null);

        /* 
         Add action to document so that it opens the 
         Gnostice website URL
        */
        d.addAction(PdfAction.URI, "http://www.gnostice.com");

        /*
         Only JAVASCRIPT actions can be added in document
         event triggers. Examples of document event triggers are 
         DOCUMENT_CLOSE and DOCUMENT_BEFORE_PRINT.
        */
        d.addAction(PdfAction.PdfEvent.ON_DOCUMENT_CLOSE,
            PdfAction.JAVASCRIPT,
            "app.alert('Gnostice PDFOne... (Doc Close Action)')");

        d.addAction(PdfAction.PdfEvent.ON_BEFORE_DOCUMENT_PRINT,
            PdfAction.JAVASCRIPT,
            "app.alert('Gnostice PDFOne...(Before Print)')");

        d.setOpenAfterSave(true);
        d.write();
        w.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java DocActions " +
                    "<FileToLaunch> <output file path> ");
        }
    }
}
