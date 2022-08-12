package ca.bc.gov.educ.api.gradbusiness.util;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class EducGradBusinessUtil {

    private EducGradBusinessUtil() {}

    private static final String LOC = "/tmp/";

    private static Logger logger = LoggerFactory.getLogger(EducGradBusinessUtil.class);

    public static void mergeDocuments(List<InputStream> locations) {
        try {
            PDFMergerUtility objs = new PDFMergerUtility();
            StringBuilder pBuilder = new StringBuilder();
            pBuilder.append(LOC);
            Path path = Paths.get(pBuilder.toString());
            Files.createDirectories(path);
            pBuilder = new StringBuilder();
            pBuilder.append(LOC).append(".pdf");
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

    public static byte[] readFile(String fileName) {
        File file = new File(fileName.concat(".pdf"));
        File directory = new File(LOC);

        try {
            if(FileUtils.directoryContains(directory, file)) {
                Path path = Paths.get(LOC.concat(file.getName()));
                return Files.readAllBytes(path);
            }
        }
        catch (IOException e) {
            logger.debug("Error Message {}",e.getLocalizedMessage());
        }
        return new byte[0];
    }
}
