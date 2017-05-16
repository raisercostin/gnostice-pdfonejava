import java.io.IOException;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfImage;
import com.gnostice.pdfone.PdfMeasurement;
import com.gnostice.pdfone.PdfPage;
import com.gnostice.pdfone.PdfTable;
import com.gnostice.pdfone.PdfWriter;

public class DrawTable
{
    /* Usage : java DrawTable <image1> <image2> <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        ///////////////////////////////////
        // Drawing a table on a document //
        ///////////////////////////////////

        try{
        /* Create a PdfWriter instance for the PDF file */
        PdfWriter w = PdfWriter.fileWriter(args[2]);

        /* Create a PdfDocument instance with the PdfWriter */
        PdfDocument d = new PdfDocument(w);

        /* Create a page and add it to the document */
        PdfPage p = new PdfPage(800, 900, 0, 0, 50, 50, 50, 50,
            PdfMeasurement.MU_POINTS);
        d.add(p);

        /* Set measurement unit of the document as points */
        d.setMeasurementUnit(PdfMeasurement.MU_POINTS);

        /* Create a table and set the cell margins*/
        PdfTable t = new PdfTable(6);
        t.setCellLeftMargin(10);
        t.setCellTopMargin(10);
        t.setCellRightMargin(10);
        t.setCellBottomMargin(10);
        String text = "Welcome to Gnostice Information Technologies. We are a technology company "
            + "involved in the creation of high-quality, feature-rich software solutions for software "
            + "developers and business users. 3000+ happy customers, many in the Fortune 500 list, use our"
            + "products today.";

        /* Create images that could be added to cells in the table */
        PdfImage jpgImg = PdfImage.create(args[0]);
        PdfImage bmpImg = PdfImage.create(args[1]);

        /* Add cells to the table */
        for (int i = 0; i < 30; i++)
        {
            t.addCell(2, 2, jpgImg);
            t.addCell(1, 3, text);
            t.addCell(1, 2, bmpImg);
            t.addCell(1, 2, text);
            t.addCell(1, 1, text);
            t.addCell(1, 1, text);
        }
        /* Add table to the document */
        d.addTable(t, 80f, 100f, 1);

        d.setOpenAfterSave(true);
        d.write();
        w.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java DrawTable" +
                    " <image1> <image2> <output file path>");
        }
    }
}
