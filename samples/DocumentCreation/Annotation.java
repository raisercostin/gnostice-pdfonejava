import java.awt.Color;
import java.io.IOException;

import com.gnostice.pdfone.*;
import com.gnostice.pdfone.encodings.PdfEncodings;
import com.gnostice.pdfone.fonts.PdfFont;

public class Annotation
{
    /* Usage : java Annotation <FileToAttach> <LinkAnnotLaunchFile> 
     * <RemoteGoToFilePath> <output file path> */
    public static void main(String[] args) throws IOException,
        PdfException
    {
        ////////////////////////////////////////
        // Create a document with annotations //
        ////////////////////////////////////////

        try{
        
        PdfWriter w = PdfWriter.fileWriter(args[3]);

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

        /* Create a line annotation and set its properties */
        double endPts[] = { 80, 200, 250, 200 };
        pageOne.writeText("Line Annotations", fontHelvetica, 220, 140);
        PdfLineAnnot lineAnnot1 = new PdfLineAnnot(new PdfRect(80,
            200, 260, 200), Color.GREEN);
        lineAnnot1.setTitle("Title");
        lineAnnot1.setSubject("Line Annotation");
        lineAnnot1
            .setContents("This Demo shows how to create a Line Annotation");
        lineAnnot1.setInteriorColor(Color.cyan);
        lineAnnot1
            .setLineStartStyle(PdfLineAnnot.LINEENDSTYLE_DIAMOND);
        lineAnnot1
            .setLineEndStyle(PdfLineAnnot.LINEENDSTYLE_CLOSED_ARROW);
        lineAnnot1.setBorderStyle(PdfLineAnnot.BORDERSTYLE_DASHED);
        lineAnnot1.setBorderWidth(3);
        lineAnnot1.setPoints(endPts);

        double endPts1[] = { 350, 200, 500, 200 };
        PdfLineAnnot lineAnnot2 = new PdfLineAnnot(new PdfRect(350,
            200, 250, 200), Color.BLUE);
        lineAnnot2.setSubject("Line Annotation");
        lineAnnot2.setTitle("Title");
        lineAnnot2
            .setContents("This Demo shows how to create a Line Annotation");
        lineAnnot2.setInteriorColor(Color.darkGray);
        lineAnnot2
            .setLineStartStyle(PdfLineAnnot.LINEENDSTYLE_OPEN_ARROW);
        lineAnnot2.setLineEndStyle(PdfLineAnnot.LINEENDSTYLE_CIRCLE);
        lineAnnot2.setBorderStyle(PdfLineAnnot.BORDERSTYLE_SOLID);
        lineAnnot2.setBorderWidth(3);
        lineAnnot2.setPoints(endPts1);

        /* Add the line annotation to page 1 */
        pageOne.addAnnotation(lineAnnot1);
        pageOne.addAnnotation(lineAnnot2);

        pageOne.writeText("Circle Annotations", fontHelvetica, 220, 260);

        /* Create a circle annotation and set its  properties */
        PdfCircleAnnot circleAnnot1 = new PdfCircleAnnot(new PdfRect(
            100, 310, 120, 80), Color.RED);
        circleAnnot1.setSubject("Annotation Subject");
        circleAnnot1.setTitle("Title");
        circleAnnot1
            .setContents("This Demo shows how to create a Circle Annotation");
        circleAnnot1.setBorderStyle(PdfCircleAnnot.BORDERSTYLE_DASHED);
        circleAnnot1.setBorderWidth(3);
        circleAnnot1.setColor(Color.RED);

        PdfCircleAnnot circleAnnot2 = new PdfCircleAnnot(new PdfRect(
            350, 310, 120, 60), Color.RED);
        circleAnnot2.setSubject("Annotation Subject");
        circleAnnot2
            .setContents("This Demo shows how to create a Circle Annotation");
        circleAnnot2.setBorderStyle(PdfCircleAnnot.BORDERSTYLE_SOLID);
        circleAnnot2.setBorderWidth(3);
        circleAnnot2.setTitle("Title");
        circleAnnot2.setCloudy(true);
        circleAnnot2.setCloudIntensity(2);
        
        /* Add the circle annotations to page 1 */
        pageOne.addAnnotation(circleAnnot1);
        pageOne.addAnnotation(circleAnnot2);

        pageOne.writeText("Square Annotations", fontHelvetica, 220, 420);
        /* Create a square annotation and set its properties */
        PdfSquareAnnot squareAnnot1 = new PdfSquareAnnot(new PdfRect(
            100, 470, 100, 60), Color.BLUE);
        squareAnnot1.setSubject("Annotation Subject");
        squareAnnot1.setContents("This Demo shows how to create "
                + "a Square Annotation. Interior color is not set. Cloud Intensity is nil.");
        squareAnnot1.setBorderStyle(PdfSquareAnnot.BORDERSTYLE_SOLID);
        squareAnnot1.setBorderWidth(3);
        squareAnnot1.setTitle("Title");
        squareAnnot1.setColor(Color.BLUE);

        PdfSquareAnnot squareAnnot2 = new PdfSquareAnnot(new PdfRect(
            350, 470, 100, 60), Color.BLUE);
        squareAnnot2.setSubject("Annotation Subject");
        squareAnnot2.setContents("This Demo shows how to create "
            + "Square Annotation. Cloud Intensity is more");
        squareAnnot2
            .setBorderStyle(PdfSquareAnnot.BORDERSTYLE_DASHED);
        squareAnnot2.setBorderWidth(3);
        squareAnnot2.setTitle("Title");
        squareAnnot2.setColor(Color.BLUE);
        squareAnnot2.setCloudy(true);
        squareAnnot2.setCloudIntensity(2);

        /* Add the square annotation to page 1 */
        pageOne.addAnnotation(squareAnnot1);
        pageOne.addAnnotation(squareAnnot2);

        pageOne.writeText("Polygon Annotation", fontHelvetica, 220, 580);
        /* Create a polygon annotation  and set its properties */
        double verPolyg[] = { 100, 620, 140, 680, 100, 740, 200, 680 };
        PdfPolygonAnnot polyAnnot1 = new PdfPolygonAnnot(new PdfRect(
            220, 500, 120, 180), Color.LIGHT_GRAY);
        polyAnnot1.setVertices(verPolyg);
        polyAnnot1.setCloudIntensity(1);
        polyAnnot1.setBorderWidth(3);
        polyAnnot1.setBorderStyle(PdfPolygonAnnot.BORDERSTYLE_DASHED);
        polyAnnot1.setSubject("Annotation Subject");
        polyAnnot1.setTitle("Annotation Title");
        polyAnnot1.setInteriorColor(Color.cyan);
        polyAnnot1
            .setContents("This Demo shows how to create a Polygon Annotation");

        double verPolg1[] = { 340, 630, 380, 690, 340, 750, 440, 690 };
        PdfPolygonAnnot polyAnnot2 = new PdfPolygonAnnot(new PdfRect(
            420, 500, 120, 180), Color.CYAN);
        polyAnnot2.setVertices(verPolg1);
        polyAnnot2.setCloudy(true);
        polyAnnot2.setColor(Color.cyan);
        polyAnnot2.setCloudIntensity(1);
        polyAnnot2.setBorderWidth(2);
        polyAnnot2
            .setBorderStyle(PdfPolygonAnnot.BORDERSTYLE_BEVELED);
        polyAnnot2.setSubject("Annotation Subject");
        polyAnnot2.setTitle("Annotation Title");
        polyAnnot2
            .setContents("This Demo shows how to create a Polygon Annotation");

        /* Add the polygon annotations to page 1 */
        pageOne.addAnnotation(polyAnnot1);
        pageOne.addAnnotation(polyAnnot2);

        pageOne.writeText("Caret Annotation", fontHelvetica, 220, 760);
        pageOne.writeText("Caret annotation displays the"
            + " missing information in the popup window.", fontHelvetica, 120,
            790);
        /* Create a caret annotation and set its properties */
        PdfCaretAnnot caretAnnot = new PdfCaretAnnot(new PdfRect(360,
            800, 8, 10), Color.ORANGE);
        caretAnnot.setTitle("Title");
        caretAnnot.setContents("like this text here");
        caretAnnot.setSymbol(PdfCaretAnnot.SYMBOL_PARAGRAPH);
        caretAnnot.setSubject("Annotation Subject");

        /* Add the caret annotation to page 1 */
        pageOne.addAnnotation(caretAnnot);

        /* Creates a new page */
        PdfPage pageTwo = new PdfPage(620, 850,
            PdfMeasurement.MU_POINTS);

        pageTwo.writeText("PolyLine Annotation", fontHelvetica, 220, 60);
        /* Create a polyline annotation and set its properties */
        double ver1[] = { 160, 160, 260, 80, 260, 160, 200, 100, 300,
            100, 220, 150 };

        PdfPolylineAnnot PolylineAnnot = new PdfPolylineAnnot();
        PolylineAnnot.setRect(160,80,200,200);
        PolylineAnnot.setVertices(ver1);
        PolylineAnnot.setColor(Color.MAGENTA);
        PolylineAnnot
            .setBorderStyle(PdfPolylineAnnot.BORDERSTYLE_DASHED);
        PolylineAnnot
            .setLineStartStyle(PdfPolylineAnnot.LINEENDSTYLE_OPEN_ARROW);
        PolylineAnnot
            .setLineEndStyle(PdfPolylineAnnot.LINEENDSTYLE_CIRCLE);
        PolylineAnnot
            .setContents("This Demo shows how to create a Polyline Annotation");
        PolylineAnnot.setInteriorColor(Color.green);
        PolylineAnnot.setBorderWidth(3);
        PolylineAnnot.setSubject("Annotation Subject");
        PolylineAnnot.setTitle("Annotation Title");

        /* Add the polyline annotation to page 2 */
        pageTwo.addAnnotation(PolylineAnnot);

        pageTwo.writeText("Text Annotation", fontHelvetica, 220, 200);
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
        pageTwo.addAnnotation(textAnnot1);
        pageTwo.addAnnotation(textAnnot2);
        pageTwo.addAnnotation(textAnnot3);

        pageTwo.writeText("Free Text Annotation", fontHelvetica, 220, 300);
        /* Create a free text annotation and set its properties */
        PdfFreeTextAnnot freetextAnnot = new PdfFreeTextAnnot(
            new PdfRect(180, 350, 200, 40), Color.YELLOW);
        freetextAnnot.setAlignment(PdfFreeTextAnnot.ALIGNMENT_CENTER);
        double dou[] = { 280, 360, 380, 390, };
        freetextAnnot.setCalloutLine(dou);
        freetextAnnot.setContents("Gnostice PDFOne");

        /* Add FreeText Annotation to page pageTwo */
        pageTwo.addAnnotation(freetextAnnot);

        pageTwo.writeText("File Attachment Annotation", fontHelvetica, 220, 420);
        /* Create a file attachment annotation and set its properties */
        PdfFileAttachmentAnnot fileAttachmentAnnot = new PdfFileAttachmentAnnot(
            260, 460, Color.RED);
        // Uses the first command-line argument
        fileAttachmentAnnot.setFilePath(args[0]);
        fileAttachmentAnnot.setContents("Gnostice PDFOne");
        fileAttachmentAnnot.setIcon(PdfFileAttachmentAnnot.ICON_GRAPH);

        /* Add the file attachment annotation to page 2 */
        pageTwo.addAnnotation(fileAttachmentAnnot);

        pageTwo.writeText("Ink Annotation", fontHelvetica, 220, 510);
        /* Create an ink annotation and set its properties */
        double ver[] = { 100, 550, 150, 600, 200, 550, 250, 600, 300,
            550, 350, 600, 400, 550, 450, 600 };
        PdfInkAnnot inkAnnot = new PdfInkAnnot();
        inkAnnot.setRect(100,550,50,50);
        inkAnnot.setColor(Color.BLUE);
        inkAnnot.setVertices(ver);

        /* Add the ink annotation to page 2 */
        pageTwo.addAnnotation(inkAnnot);

        pageTwo.writeText("Link Annotation", fontHelvetica, 220, 610);

        pageTwo.writeText("Click here", fontCourier, 100, 640);
        pageTwo.writeText(
            "GoTo Action: Navigates with in the current document",
            fontHelveticaSmall, 210, 645);
        /* Create a link annotation and set its properties */
        PdfLinkAnnot linkAnnotGoto = new PdfLinkAnnot(new PdfRect(
            100, 645, 90, 12), Color.RED);
        linkAnnotGoto
            .setHighlightMode(PdfLinkAnnot.HIGHLIGHT_MODE_INVERT);
        linkAnnotGoto.addActionGoTo(1);

        pageTwo.writeText("Click here", fontCourier, 100, 665);
        pageTwo.writeText(
                "Named Action: Navigates to Next, Previous, Last or First page of the document",
                fontHelveticaSmall, 210, 670);
        PdfLinkAnnot linkAnnotNamed = new PdfLinkAnnot(new PdfRect(
            100, 670, 90, 12), Color.RED);
        linkAnnotNamed
            .setHighlightMode(PdfLinkAnnot.HIGHLIGHT_MODE_INVERT);
        linkAnnotNamed.addActionNamed(PdfAction.NAMED_FIRSTPAGE);

        pageTwo.writeText("Click here", fontCourier, 100, 690);
        pageTwo.writeText(
            "RemoteGoTo Action: Opens another PDF document", fontHelveticaSmall, 210,
            695);
        PdfLinkAnnot linkAnnotGotoR = new PdfLinkAnnot(new PdfRect(
            100, 695, 90, 12), Color.RED);
        linkAnnotGotoR
            .setHighlightMode(PdfLinkAnnot.HIGHLIGHT_MODE_INVERT);
        linkAnnotGotoR.addActionRemoteGoTo(args[2], PdfLinkAnnot
                .getRemoteGoToInstance(1), true);

        pageTwo.writeText("Click here", fontCourier, 100, 715);
        pageTwo.writeText("JavaScript Action: Executes Java Scripts",
            fontHelveticaSmall, 210, 720);
        PdfLinkAnnot linkAnnotJavaScript = new PdfLinkAnnot(
            new PdfRect(100, 720, 90, 12), Color.RED);
        linkAnnotJavaScript
            .setHighlightMode(PdfLinkAnnot.HIGHLIGHT_MODE_INVERT);
        linkAnnotJavaScript
            .addActionJavaScript("app.alert('Gnostice Information Technologies')");

        pageTwo.writeText("Click here", fontCourier, 100, 740);
        pageTwo.writeText("URI Action: Opens specified URI", fontHelveticaSmall, 210,
            745);
        PdfLinkAnnot linkAnnotURI = new PdfLinkAnnot(new PdfRect(100,
            745, 90, 12), Color.RED);
        linkAnnotURI
            .setHighlightMode(PdfLinkAnnot.HIGHLIGHT_MODE_INVERT);
        linkAnnotURI.addActionURI("www.gnostice.com");

        pageTwo.writeText("Click here", fontCourier, 100, 765);
        pageTwo.writeText(
                "Launch Action: Launches any file in there respective applications",
                fontHelveticaSmall, 210, 770);
        PdfLinkAnnot linkAnnotLaunch = new PdfLinkAnnot(new PdfRect(
            100, 770, 90, 12), Color.RED);
        linkAnnotLaunch
            .setHighlightMode(PdfLinkAnnot.HIGHLIGHT_MODE_INVERT);
        linkAnnotLaunch.addActionLaunch(args[1], false);

        /* Add the link annotations to page 2 */
        pageTwo.addAnnotation(linkAnnotGoto);
        pageTwo.addAnnotation(linkAnnotNamed);
        pageTwo.addAnnotation(linkAnnotGotoR);
        pageTwo.addAnnotation(linkAnnotJavaScript);
        pageTwo.addAnnotation(linkAnnotURI);
        pageTwo.addAnnotation(linkAnnotLaunch);

        /* Adds a new page */
        PdfPage pageThree = new PdfPage(620, 850,
            PdfMeasurement.MU_POINTS);

        pageThree.writeText("Text Markup Annotation", fontHelvetica, 220, 60);
        pageThree.writeText("This is Squiggly Text Markup Annotation",
            fontHelveticaSmall, 200, 105);

        /* Create a markup annotation and set its properties */
        double verMarkupAnot[] = { 200, 100, 400, 100, 200, 120, 400,
            120 };
        PdfMarkupAnnot markAnnot1 = new PdfMarkupAnnot(
            "Annotation content", PdfMarkupAnnot.STYLE_SQUIGGLY,
            verMarkupAnot, Color.BLUE);
        markAnnot1.setRect(200, 100, 200, 20);
        markAnnot1.setSubject("Annotation Subject");
        markAnnot1.setTitle("Annotation Title");
        
        pageThree.writeText("This is Underline Text Markup Annotation",
            fontHelveticaSmall, 200, 145);
        double verMarkupAnot1[] = { 200, 140, 400, 140, 200, 160,
            400, 160 };
        PdfMarkupAnnot markAnnot2 = new PdfMarkupAnnot(
            "Annotation content", PdfMarkupAnnot.STYLE_UNDERLINE,
            verMarkupAnot1, Color.BLUE);
        markAnnot2.setRect(200, 140, 200, 20);
        markAnnot2.setSubject("Annotation Subject");
        markAnnot2.setTitle("Annotation Title");

        pageThree.writeText("This is Highlight Text Markup Annotation",
            fontHelveticaSmall, 200, 185);
        double verMarkupAnot2[] = { 200, 180, 400, 180, 200, 200,
            400, 200 };
        PdfMarkupAnnot markAnnot3 = new PdfMarkupAnnot(
            "Annotation content", PdfMarkupAnnot.STYLE_HIGHLIGHT,
            verMarkupAnot2, Color.yellow);
        markAnnot3.setRect(200, 180, 200, 20);
        markAnnot3.setSubject("Annotation Subject");
        markAnnot3.setTitle("Annotation Title");

        pageThree.writeText("This is Strikeout Text Markup Annotation",
            fontHelveticaSmall, 200, 225);
        double verMarkupAnot3[] = { 200, 220, 400, 220, 200, 240,
            400, 240 };
        PdfMarkupAnnot markAnnot4 = new PdfMarkupAnnot(
            "Annotation content", PdfMarkupAnnot.STYLE_STRIKEOUT,
            verMarkupAnot3, Color.BLUE);
        markAnnot4.setRect(200, 220, 200, 20);
        markAnnot4.setSubject("Annotation Subject");
        markAnnot4.setTitle("Annotation Title");

        /* Add the markup annotations to page 3 */
        pageThree.addAnnotation(markAnnot1);
        pageThree.addAnnotation(markAnnot2);
        pageThree.addAnnotation(markAnnot3);       
        pageThree.addAnnotation(markAnnot4);
        
        /* Add the pages to the document */
        d.add(pageOne);
        d.add(pageTwo);
        d.add(pageThree);

        d.setOpenAfterSave(true);
        d.write();
        w.dispose();
        }
        catch (ArrayIndexOutOfBoundsException n)
        {
            System.out.println("Usage : java Annotation <FileToAttach> " +
                    "<LinkAnnotLaunchFile> <RemoteGoToFilePath> " +
                    "<output file path>");
        }
    }
}
