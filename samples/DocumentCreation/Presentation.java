import java.io.File;
import java.io.IOException;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfPage;
import com.gnostice.pdfone.PdfPageMode;
import com.gnostice.pdfone.PdfPagePresentation;
import com.gnostice.pdfone.PdfPageSize;
import com.gnostice.pdfone.PdfWriter;

public class Presentation
{
    public static void main(String[] args) throws IOException,
        PdfException
    {
        ////////////////////////////////
        // Setting page presentations //
        ////////////////////////////////

        char pathSeparator = File.separatorChar;
        /* Create a PdfWriter instance for the PDF file */
        PdfWriter w = PdfWriter.fileWriter("." + pathSeparator
            + "PDFs" + pathSeparator + "Presentation.pdf");

        /* Create a PdfDocument instance with the PdfWriter */
        PdfDocument d = new PdfDocument(w);

        /* Set page mode for the document */
        d.setPageMode(PdfPageMode.USEOUTLINES);

        PdfPage p = new PdfPage(PdfPageSize.A3);

        /* Create PagePresentation instance and set its properties */
        PdfPagePresentation pp = new PdfPagePresentation();

        /* Set duration of transition effect, in seconds */
        pp.setTransitionDuration(10);

        /*
         Set starting or ending scale with which the changes are
         drawn.
        */
        pp.setFlyTransitionEndScale(1);

        /*
         Set direction in which the specified transition
         effect moves
        */
        pp.setTransitionDirection(90);

        /* Set opacity state of the fly transition area  */
        pp.setFlyTransitionAreaOpaque(true);

        /*
         Set dimension with which the specified transition
         effect occurs
        */
        pp.setTransitionDimension(PdfPagePresentation.VERTICAL);

        /* 
         Set type of motion for the specified transition 
         effect 
        */
        pp.setTransitionMotion(PdfPagePresentation.OUTWARD);

        /* Set transition style to use when moving to this page */
        pp.setTransitionStyle(PdfPagePresentation.GLITTER);

        /* Add page presentation to the page */
        p.setPresentation(pp);

        d.add(p);
        d.setOpenAfterSave(true);
        d.write();
        w.dispose();
    }
}
