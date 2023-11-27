package ca.bc.gov.educ.api.gradbusiness.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EducGradBusinessUtil {

    private static final Logger logger = LoggerFactory.getLogger(EducGradBusinessUtil.class);

    public static final String TMP_DIR = "/tmp";

    private EducGradBusinessUtil() {}


    private static final int BUFFER_SIZE = 250000;

    public static byte[] mergeDocumentsPDFs(List<InputStream> locations) throws IOException {
        File bufferDirectory = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayInputStream result;
        try {
            bufferDirectory = IOUtils.createTempDirectory(TMP_DIR, "buffer");
            PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
            pdfMergerUtility.setDestinationStream(outputStream);
            pdfMergerUtility.addSources(locations);
            MemoryUsageSetting memoryUsageSetting = MemoryUsageSetting.setupMixed(50000000)
                    .setTempDir(bufferDirectory);
            pdfMergerUtility.mergeDocuments(memoryUsageSetting);
            result = new ByteArrayInputStream(outputStream.toByteArray());
            return result.readAllBytes();
        } catch (Exception e) {
            logger.error("Error {}", e.getLocalizedMessage());
        } finally {
            if (bufferDirectory != null) {
                IOUtils.removeFileOrDirectory(bufferDirectory);
            }
            outputStream.close();
        }
        return new byte[0];
    }

    public static byte[] mergeDocuments(List<InputStream> locations) throws IOException {
        final byte[] result;

        List<byte[]> pdfs = new ArrayList<>();
        for(InputStream is: locations) {
            pdfs.add(is.readAllBytes());
        }

        // Create a place to write the new bundle.
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE)) {
            final Document document = new Document();
            final PdfCopy copy = new PdfSmartCopy(document, out);
            document.open();

            byte[] previousPdf = pdfs.get(0);
            PdfReader reader = new PdfReader(previousPdf);

            // Concatenate the PDF documents.
            for (final byte[] pdf : pdfs) {
                // Interpret a PDF only when it is different to the previous one
                if (previousPdf != pdf) {
                    previousPdf = pdf;
                    reader = new PdfReader(pdf);
                }

                final int pages = reader.getNumberOfPages();

                // Copy all the pages from the list into a new document.
                for (int i = 1; i <= pages; i++) {
                    copy.addPage(copy.getImportedPage(reader, i));
                }

                reader.close();
            }

            document.close();
            result = out.toByteArray();
        } catch (final DocumentException e) {
            throw new IOException(e);
        }
        return result;
    }

    public static String getTempDirPath() {
        return Optional.ofNullable(System.getProperty("java.io.tmpdir")).orElse("/tmp").concat(File.pathSeparator);
    }

    public static String getFileNameSchoolReports(String mincode, int year, String month, String type, MediaType mediaType) {
        return mincode + "_" + year + month + "_" + type + "." + mediaType.getSubtype();
    }

    public static String getFileNameStudentCredentials(String mincode, String pen, String type) {
        return mincode + "_" + pen +"_" + type + ".pdf";
    }

}
