import java.awt.Color;
import java.io.IOException;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfMeasurement;
import com.gnostice.pdfone.PdfPage;
import com.gnostice.pdfone.PdfPageSize;
import com.gnostice.pdfone.PdfRect;
import com.gnostice.pdfone.PdfWriter;

public class DrawShapes
{
    /* Usage : java DrawShapes <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        ////////////////////////////////
        // Draw shapes on a document  //
        ////////////////////////////////
        try{
        /* Create a PdfWriter instance for the PDF file */
        PdfWriter w = PdfWriter.fileWriter(args[0]);

        /* Create a PdfDocument instance with the PdfWriter */
        PdfDocument d = new PdfDocument(w);

        /* Create a new page */
        PdfPage p = new PdfPage(PdfPageSize.A3);

        p.setMeasurementUnit(PdfMeasurement.MU_INCHES);

        /* Set pen width for the page */
        p.setPenWidth(4);

        /* Draw a line on the page */
        p.drawLine(1, 1, 6, 1);

        /* Set a different pen setting */
        p.setPenDashPhase(8.8);
        p.setPenDashLength(4.4);
        p.setPenDashGap(4);
        p.setPenColor(Color.RED);
        p.setPenWidth(5);
        
        /* Draw a line on page with the new pen setting */
        p.drawLine(1, 2, 6, 2);

        /* Set brush color for the page */
        p.setBrushColor(Color.DARK_GRAY);

        /* Draws a rectangle on the page */
        p.drawRect(1, 3, 4, 2, true, true);

        /* Set a different brush Color to the page */
        p.setBrushColor(Color.YELLOW);

        /* Draws a rectangle with rounded corners on the page */
        p.drawRoundRect(6, 3, 4, 2, 0.4, 0.4, true, true);

        /* Change the brush color for the page */
        p.setBrushColor(Color.lightGray);

        /* Draw a square on the page */
        p.drawSquare(1, 6, 2, true, true);

        /* Draw an ellipse on the page */
        p.drawEllipse(4, 6, 7, 8, false, true);

        /* Draw a circle on the page */
        p.drawCircle(9, 7, 1, true, false);

        /* Change the pen settings */
        p.setPenDashPhase(3);
        p.setPenDashLength(6.4);
        p.setPenDashGap(4);
        p.setPenColor(Color.BLUE);
        p.setPenWidth(8);

        /* Draw an arc on the page */
        p.drawArc(new PdfRect(2, 8, 4, 4), 90, 90);
        
        /* Draw a pie on the page */
        p.drawPie(5, 9, 2, 2, 90, 39, false, true);

        /* Add the page to the document */
        d.add(p);

        d.setOpenAfterSave(true);
        d.write();
        w.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java DrawShapes" +
                    " <output file path>");
        }
    }
}
