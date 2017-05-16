import java.io.IOException;
import java.lang.reflect.Method;

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfMeasurement;
import com.gnostice.pdfone.PdfReader;

public class OnPageRead
{
    /* This is the OnPageRead event handler */
    public static void myFunctionOnPageRead(PdfDocument d, double[] margin, int[] mu, int[] pageno, double[] widthHeight)
    {
        System.out.println("Pageno : " + pageno[0]);
        
        /* Set page header height */
        margin[0] = 50;
        /* Set page footer height */
        margin[1] = 50;
        /* Set page left margin */
        margin[2] = 100;
        /* Set page top margin */
        margin[3] = 100;
        /* Set page right margin */
        margin[4] = 100;
        /* Set page bottom margin */
        margin[5] = 100;
    }

    /* Usage : java OnPageRead <input file path> <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        ///////////////////////////////////////////////////
        // Open a document and set page margins through  //
        // OnPageRead event                              //
        ///////////////////////////////////////////////////
        try{
        /* Create a PdfReader instance for the PDF file */
        PdfReader r = PdfReader.fileReader(args[0], args[1]);

        /* Set the OnPageRead event handler for the PdfReader instance */
        try
        {
            Method m = OnPageRead.class.getDeclaredMethod(
                "myFunctionOnPageRead", new Class[] {
                    PdfDocument.class, double[].class, int[].class,
                    int[].class, double[].class });

            r.setOnPageRead(m);
        }
        catch (NoSuchMethodException nsme)
        {
            System.out.println("Should not occur");
            /* should not occur */
        }

        /* Create a PdfDocument instance with the PdfReader */
        PdfDocument d = new PdfDocument(r);
        d.setMeasurementUnit(PdfMeasurement.MU_POINTS);

        /* Write text to the document */
        d.writeText("Text written at 0, 0", 0, 0, "1");

        d.setOpenAfterSave(true);
        d.write();
        r.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java OnPageRead " +
                    "<input file path> <output file path>");
        }
    }

}
