package ca.bc.gov.educ.api.gradbusiness.util;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class EducGradBusinessUtil {

    private EducGradBusinessUtil() {}

    private static final String LOC = "/tmp/";
    private static final String DEL = "/";
    private static final String AMAL = "amalgamated";

    private static Logger logger = LoggerFactory.getLogger(EducGradBusinessUtil.class);

    public static void mergeDocuments(String mincode, String fileName,List<InputStream> locations) {
        try {
            PDFMergerUtility objs = new PDFMergerUtility();
            StringBuilder pBuilder = new StringBuilder();
            pBuilder.append(LOC).append(AMAL).append(DEL).append(mincode).append(DEL);
            Path path = Paths.get(pBuilder.toString());
            Files.createDirectories(path);
            pBuilder = new StringBuilder();
            pBuilder.append(LOC).append(AMAL).append(DEL).append(mincode).append(DEL).append(fileName).append(".pdf");
            objs.setDestinationFileName(pBuilder.toString());
            objs.addSources(locations);
            objs.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        }catch (Exception e) {
            logger.debug("Error {}",e.getLocalizedMessage());
        }
    }

    public static String getFileNameSchoolReports(String mincode, int year, String month, String type) {
        return mincode + "_" + year + month + "_" + type;
    }

    public static String getFileNameStudentCredentials(String mincode, String pen, String type) {
        return mincode + "_" + pen +"_" + type;
    }

    public static String getFilePath(String mincode,String filename) {
        return LOC + DEL + AMAL + DEL + mincode + DEL + filename + ".pdf";
    }

    public static byte[] readFile(File file) {

        try (FileInputStream fl = new FileInputStream(file)){
            byte[] arr = new byte[(int)file.length()];
            int count = fl.read(arr);
            if (count > 0)
                return arr;
        } catch (IOException e) {
            logger.debug("Error {}",e.getLocalizedMessage());
        }
        return new byte[0];
    }
}
