import java.io.IOException;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfPreferences;
import com.gnostice.pdfone.PdfWriter;

public class ViewerPreference
{
    /* Usage : java ViewerPreference <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        /////////////////////////////////////////
        // Setting document viewer preferences //
        /////////////////////////////////////////

        try{
        /* Create a PdfWriter instance for the PDF file */
        PdfWriter w = PdfWriter.fileWriter(args[0]);

        /* Create a PdfDocument instance with the PdfWriter */
        PdfDocument d = new PdfDocument(w);

        /* Set various viewer preferences for the document */
        int prefences = PdfPreferences.HIDE_TOOLBAR
            | PdfPreferences.HIDE_MENUBAR
            | PdfPreferences.HIDE_WINDOWUI
            | PdfPreferences.CENTER_WINDOW
            | PdfPreferences.DISPLAY_DOC_TITLE
            | PdfPreferences.NonFullScreenPageMode.OUTLINES;
        d.setViewerPreferences(prefences);

        d.setOpenAfterSave(true);
        d.write();
        w.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java ViewerPreference" +
                    " <output file path>");
        }
    }
}
