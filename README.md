# gnostice-pdfonejava
Gnostice PDFOne Java GPL is a rich set of APIs to create, manipulate and organize PDF documents, process PDF forms and perform other PDF document related tasks from within your Java applications. 

## Usage 
[A sample usage](https://www.gnostice.com/nl_article.asp?id=135&t=Convert_PDF_To_High_Resolution_Images_Using_Java)

	import java.io.IOException;

	import com.gnostice.pdfone.PDFOne;
	import com.gnostice.pdfone.PdfDocument;
	import com.gnostice.pdfone.PdfException;


	public class PdfSaveAsImage_Example {
	  static {
		PDFOne.activate("your-activation-key",
						"your-product-key");
	  }

	  public static void main(String[] args)
		throws IOException, PdfException {

		// Open a PDF document
		PdfDocument doc1 = new PdfDocument();
		doc1.load("Input_Docs\\sample_doc.pdf");

		// Save page 10 as a 96-dpi JPEG image
		doc1.saveAsImage("jpg",               // format
						 "10",                // page number
						 "image96_of_page#",  // image prefix
						 ".\\Output_Docs",    // output directory
						 96);                 // DPI

		// Save page 10 as a 204-dpi JPEG image
		doc1.saveAsImage("jpg", "10", "image204_of_page#", ".\\Output_Docs", 204);

		// Close the PDF document
		doc1.close();
	  }
	}

## Versions
Version 1.04 exported at
- https://code.google.com/archive/p/pdfonejava/
- https://sourceforge.net/projects/pdfonejava/files/pdfonejava_gpl/1.04/


The src directory contains the source code for PDFOne Java Library.

The samples directory contains sample programs that demonstrate how to create and manipulate PDF documents using PDFOne Java Library.

The build.xml file is included to create jar file for PDFOne Java using Ant.

If you wish to use PDFOne Java or any part of it in a GPL product, you merely need to ensure you
fully comply with the GPL license. Otherwise you must obtain a commercial license to use it.

Gnostice Information Technologies
http://www.gnostice.com


# Description from code.google.com

Powerful All in one PDF library for Java Application Development

Gnostice PDFOne Java is a Java library for developers to implement PDF based software solutions. PDFOne Java provides a rich set of APIs to create, manipulate and organize PDF documents, process PDF forms and perform other PDF document related tasks from within your Java applications. No external PDF software such as Adobe® PDF library, Adobe Acrobat® Professional or Ghostscript is required!

Commercial newer version with full support

Project Information

License: GNU GPL v3
4 stars
svn-based source control
Labels: 
pdf java api ghostscript adobe pdflibrary acroforms formfields annotations mergesplit create_pdf edit_pdf