import java.awt.Color;
import java.io.IOException;

import com.gnostice.pdfone.*;
import com.gnostice.pdfone.encodings.PdfEncodings;
import com.gnostice.pdfone.fonts.PdfFont;

public class Annotations
{
    /* Usage : java Annotation <LinkAnnotLaunchFile> 
     * <RemoteGoToFilePath> <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        ////////////////////////////////////////
        // Create a document with annotations //
        ////////////////////////////////////////

        try{
        
        PdfWriter w = PdfWriter.fileWriter(args[2]);

        /* Create a PdfDocument instance with the PdfWriter */
        PdfDocument d = new PdfDocument(w);

        /* Create some font objects */
        PdfFont fontHelvetica = PdfFont.create("Helvetica", 15,
            PdfEncodings.WINANSI);
        fontHelvetica.setColor(Color.BLUE);
        PdfFont fontCourier = PdfFont.create("COURIER", 15,
            PdfEncodings.WINANSI);
        fontCourier.setColor(Color.GREEN);
        PdfFont fontHelveticaSmall = PdfFont.create("Helvetica", 10,
            PdfEncodings.WINANSI);
        fontHelveticaSmall.setColor(Color.CYAN);

        PdfPage pageOne = new PdfPage(620, 850, PdfMeasurement.MU_POINTS);

        pageOne.writeText("Text Annotation", fontHelvetica, 220, 200);
        /* Create a text annotation and set its properties */
        PdfTextAnnot textAnnot1 = new PdfTextAnnot(180, 250,
            PdfTextAnnot.ICON_HELP, false);
        textAnnot1.setColor(Color.BLUE);
        textAnnot1
            .setContents("This Demo shows how to create a Text Annotation");
        textAnnot1.setTitle("Annotation Title");
        textAnnot1.setSubject("Annotation Subject");

        PdfTextAnnot textAnnot2 = new PdfTextAnnot(260, 250,
            PdfTextAnnot.ICON_KEY, false);
        textAnnot2.setColor(Color.RED);
        textAnnot2.setContents("This Demo shows how to create a Text Annotation");
        textAnnot2.setTitle("Annotation Title");
        textAnnot2.setSubject("Annotation Subject");
        textAnnot2.setOpen(false);
        
        PdfTextAnnot textAnnot3 = new PdfTextAnnot(330, 250,
            PdfTextAnnot.ICON_NOTE, true);
        textAnnot3.setColor(Color.GREEN);
        textAnnot3
            .setContents("This Demo shows how to create a Text Annotation");
        textAnnot3.setTitle("Annotation Title");
        textAnnot3.setSubject("Annotation Subject");
        textAnnot3.setOpen(true);

        /* Add the text annotations to page 2 */
        pageOne.addAnnotation(textAnnot1);
        pageOne.addAnnotation(textAnnot2);
        pageOne.addAnnotation(textAnnot3);

        pageOne.writeText("Link Annotation", fontHelvetica, 220, 400);

        pageOne.writeText("Click here", fontCourier, 100, 440);
        pageOne.writeText(
            "GoTo Action: Navigates with in the current document",
            fontHelveticaSmall, 210, 445);
        /* Create a link annotation and set its properties */
        PdfLinkAnnot linkAnnotGoto = new PdfLinkAnnot(new PdfRect(
            100, 445, 90, 12), Color.RED);
        linkAnnotGoto
            .setHighlightMode(PdfLinkAnnot.HIGHLIGHT_MODE_INVERT);
        linkAnnotGoto.addActionGoTo(1);

        pageOne.writeText("Click here", fontCourier, 100, 465);
        pageOne.writeText(
                "Named Action: Navigates to Next, Previous, Last or First page of the document",
                fontHelveticaSmall, 210, 470);
        PdfLinkAnnot linkAnnotNamed = new PdfLinkAnnot(new PdfRect(
            100, 470, 90, 12), Color.RED);
        linkAnnotNamed
            .setHighlightMode(PdfLinkAnnot.HIGHLIGHT_MODE_INVERT);
        linkAnnotNamed.addActionNamed(PdfAction.NAMED_FIRSTPAGE);

        pageOne.writeText("Click here", fontCourier, 100, 490);
        pageOne.writeText(
            "RemoteGoTo Action: Opens another PDF document", fontHelveticaSmall, 210,
            495);
        PdfLinkAnnot linkAnnotGotoR = new PdfLinkAnnot(new PdfRect(
            100, 495, 90, 12), Color.RED);
        linkAnnotGotoR
            .setHighlightMode(PdfLinkAnnot.HIGHLIGHT_MODE_INVERT);
        linkAnnotGotoR.addActionRemoteGoTo(args[1], PdfLinkAnnot
                .getRemoteGoToInstance(1), true);

        pageOne.writeText("Click here", fontCourier, 100, 515);
        pageOne.writeText("JavaScript Action: Executes Java Scripts",
            fontHelveticaSmall, 210, 520);
        PdfLinkAnnot linkAnnotJavaScript = new PdfLinkAnnot(
            new PdfRect(100, 520, 90, 12), Color.RED);
        linkAnnotJavaScript
            .setHighlightMode(PdfLinkAnnot.HIGHLIGHT_MODE_INVERT);
        linkAnnotJavaScript
            .addActionJavaScript("app.alert('Gnostice Information Technologies')");

        pageOne.writeText("Click here", fontCourier, 100, 540);
        pageOne.writeText("URI Action: Opens specified URI", fontHelveticaSmall, 210,
            545);
        PdfLinkAnnot linkAnnotURI = new PdfLinkAnnot(new PdfRect(100,
            545, 90, 12), Color.RED);
        linkAnnotURI
            .setHighlightMode(PdfLinkAnnot.HIGHLIGHT_MODE_INVERT);
        linkAnnotURI.addActionURI("www.gnostice.com");

        pageOne.writeText("Click here", fontCourier, 100, 565);
        pageOne.writeText(
                "Launch Action: Launches any file in there respective applications",
                fontHelveticaSmall, 210, 570);
        PdfLinkAnnot linkAnnotLaunch = new PdfLinkAnnot(new PdfRect(
            100, 570, 90, 12), Color.RED);
        linkAnnotLaunch
            .setHighlightMode(PdfLinkAnnot.HIGHLIGHT_MODE_INVERT);
        linkAnnotLaunch.addActionLaunch(args[0], false);

        /* Add the link annotations to page 2 */
        pageOne.addAnnotation(linkAnnotGoto);
        pageOne.addAnnotation(linkAnnotNamed);
        pageOne.addAnnotation(linkAnnotGotoR);
        pageOne.addAnnotation(linkAnnotJavaScript);
        pageOne.addAnnotation(linkAnnotURI);
        pageOne.addAnnotation(linkAnnotLaunch);
         
        /* Add the pages to the document */
        d.add(pageOne);

        d.setOpenAfterSave(true);
        d.write();
        w.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java Annotation " +
                    "<LinkAnnotLaunchFile> <RemoteGoToFilePath> " +
                    "<output file path>");
        }
    }
}
