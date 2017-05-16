import java.io.IOException;

import com.gnostice.pdfone.PdfBookmark;
import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfPageMode;
import com.gnostice.pdfone.PdfReader;

public class UpdateBookMark
{
    /* Usage : java UpdateBookMark <input file path> <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        //////////////////////////////////////////////////
        // Open a document and modify its bookMark tree //
        //////////////////////////////////////////////////

        try{
        /* Create a PdfReader instance for the PDF file */
        PdfReader r = PdfReader.fileReader(args[0] , args[1]);

        /* Create a PdfDocument instance with the PdfReader */
        PdfDocument d = new PdfDocument(r);

        /* Set page mode for the document */
        d.setPageMode(PdfPageMode.USEOUTLINES);
        
        /* Retrieve the first bookmark in the document */
        PdfBookmark base = d.getFirstBookmark();

        /* 
         Create a new BookMark and insert it after the first bookmark
        */
        base.addNext("New BookMark", d.getPageCount());

        /*
         Create a new BookMark and add it as a child to the first
         bookmark
        */
        PdfBookmark child = d.addBookmark("New Child BookMark", base,
            d.getPageCount() - 1);

        /* Add a Javascript action to the child bookmark */
        child.addActionJavaScript("app.alert('Gnostice PDFOne..." +
                "(BookMark Action)')");

        /*
         Create a new bookmark and add it next to the child bookmark
        */
        child.addNext("Next Child BookMark",
            (int) d.getPageCount() / 2);

        /* 
         Obtain a reference to the bookmark next to the first 
         bookmark 
        */
        PdfBookmark next = base.getNext();

        /* Remove the bookmark next to the first bookmark */
        next.removeNext();

        d.setOpenAfterSave(true);
        d.write();
        r.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java UpdateBookMark" +
                    " <input file path> <output file path> ");
        }
    }
}
