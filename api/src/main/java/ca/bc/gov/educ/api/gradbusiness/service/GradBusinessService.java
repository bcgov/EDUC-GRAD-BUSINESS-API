package ca.bc.gov.educ.api.gradbusiness.service;

import ca.bc.gov.educ.api.gradbusiness.exception.ServiceException;
import ca.bc.gov.educ.api.gradbusiness.model.dto.School;
import ca.bc.gov.educ.api.gradbusiness.model.dto.Student;
import ca.bc.gov.educ.api.gradbusiness.model.dto.District;
import ca.bc.gov.educ.api.gradbusiness.util.*;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * The type Grad business service.
 */
@Service
public class GradBusinessService {

    private static Logger logger = LoggerFactory.getLogger(GradBusinessService.class);

    private static final String BEARER = "Bearer ";
    private static final String APPLICATION_JSON = "application/json";
    private static final String APPLICATION_PDF = "application/pdf";
    private static final String ACCEPT = "*/*";
    private static final String TMP = File.separator + "tmp";
    /**
     * The Web client.
     */
    final WebClient webClient;

    /**
     * Utility service to obtain access token
     * */
    final TokenUtils tokenUtils;

    /**
     * The Educ grad student api constants.
     */
    final EducGradBusinessApiConstants educGradStudentApiConstants;

    /**
     * The Educ graduation api constants.
     */
    final EducGraduationApiConstants educGraduationApiConstants;

    final SchoolService schoolService;
    final DistrictService districtService;
    final RESTService restService;
    final JsonTransformer jsonTransformer;

    /**
     * Instantiates a new Grad business service.
     *
     * @param webClient the web client
     */
    @Autowired
    public GradBusinessService(WebClient webClient, TokenUtils tokenUtils, EducGradBusinessApiConstants educGradStudentApiConstants, EducGraduationApiConstants educGraduationApiConstants, SchoolService schoolService, DistrictService districtService, RESTService restService, JsonTransformer jsonTransformer) {
        this.webClient = webClient;
        this.tokenUtils = tokenUtils;
        this.educGradStudentApiConstants = educGradStudentApiConstants;
        this.educGraduationApiConstants = educGraduationApiConstants;
        this.schoolService = schoolService;
        this.districtService = districtService;
        this.restService = restService;
        this.jsonTransformer = jsonTransformer;
    }

    /**
     * Gets student by pen from student api.
     *
     * @param pen         the pen
     * @param accessToken the access token
     * @return student by pen from student api
     */
    @Transactional
    @Retry(name = "searchbypen")
    public List<Student> getStudentByPenFromStudentAPI(String pen, String accessToken) {
        return webClient.get().uri(String.format(educGradStudentApiConstants.getPenStudentApiByPenUrl(), pen)).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(new ParameterizedTypeReference<List<Student>>() {}).block();
    }

    /**
     * Prepare report data by pen response entity.
     *
     * @param pen         the pen
     * @param accessToken the access token
     * @return the response entity
     */
    public ResponseEntity<byte[]> prepareReportDataByPen(String pen, String type, String accessToken) {
        type = Optional.ofNullable(type).orElse("");
        try {
            byte[] result = webClient.get().uri(String.format(educGraduationApiConstants.getGraduateReportDataByPenUrl(), pen) + "?type=" + type).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(byte[].class).block();
            if(result == null) {
                result = new byte[0];
            }
            return handleBinaryResponse(result, "graduation_report_data.json", MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            return getInternalServerErrorResponse(e);
        }
    }

    /**
     * Prepare report data by graduation response entity.
     *
     * @param graduationData the graduation data
     * @param accessToken    the access token
     * @return the response entity
     */
    public ResponseEntity<byte[]> prepareReportDataByGraduation(String graduationData, String type, String accessToken) {
        type = Optional.ofNullable(type).orElse("");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList(BEARER + accessToken));
            headers.put(HttpHeaders.ACCEPT, Collections.singletonList(APPLICATION_JSON));
            headers.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(APPLICATION_JSON));
            byte[] result = webClient.post().uri(educGraduationApiConstants.getGraduateReportDataByGraduation() + "?type=" + type).headers(h -> h.addAll(headers)).body(BodyInserters.fromValue(graduationData)).retrieve().bodyToMono(byte[].class).block();
            if(result == null) {
                result = new byte[0];
            }
            return handleBinaryResponse(result, "graduation_report_data.json", MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            return getInternalServerErrorResponse(e);
        }
    }

    /**
     * Prepare report data by graduation response entity.
     *
     * @param xmlRequest the graduation data
     * @param accessToken    the access token
     * @return the response entity
     */
    public ResponseEntity<byte[]> prepareXmlTranscriptReportDataByXmlRequest(String xmlRequest, String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList(BEARER + accessToken));
            headers.put(HttpHeaders.ACCEPT, Collections.singletonList(APPLICATION_JSON));
            headers.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(APPLICATION_JSON));
            byte[] result = webClient.post().uri(educGraduationApiConstants.getXmlTranscriptReportData()).headers(h -> h.addAll(headers)).body(BodyInserters.fromValue(xmlRequest)).retrieve().bodyToMono(byte[].class).block();
            if(result == null) {
                result = new byte[0];
            }
            return handleBinaryResponse(result, "xml_transcript_report_data.json", MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            return getInternalServerErrorResponse(e);
        }
    }

    /**
     * Get student demographic data
     *
     * @param pen the student pen
     * @param accessToken    the access token
     * @return the response entity
     */
    public ResponseEntity<byte[]> getStudentDemographicsByPen(String pen, String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList(BEARER + accessToken));
            headers.put(HttpHeaders.ACCEPT, Collections.singletonList(APPLICATION_JSON));
            headers.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(APPLICATION_JSON));
            byte[] result = webClient.get().uri(String.format(educGradStudentApiConstants.getPenDemographicStudentApiUrl(), pen)).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(byte[].class).block();
            if(result == null) {
                result = new byte[0];
            }
            return handleBinaryResponse(result, "student_demog_data.json", MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            return getInternalServerErrorResponse(e);
        }
    }

    public ResponseEntity<byte[]> getSchoolReportPDFByMincode(String mincode, String type) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("PST"), Locale.CANADA);
        int year = cal.get(Calendar.YEAR);
        String month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
        try {
            List<School> schoolDetails = schoolService.getSchoolDetails(mincode);
            if (schoolDetails.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            String schoolOfRecordId = schoolDetails.get(0).getSchoolId();

            var result = restService.get(String.format(educGraduationApiConstants.getSchoolReportBySchoolIdAndReportType(), schoolOfRecordId,type), InputStreamResource.class);
            byte[] response = new byte[0];
            if (result != null) {
                response = result.getInputStream().readAllBytes();
            }

            return handleBinaryResponse(response, EducGradBusinessUtil.getReportsFileNameForSchoolAndDistrict(mincode,year,month,type,MediaType.APPLICATION_PDF), MediaType.APPLICATION_PDF);
        } catch (Exception e) {
            logger.error("Error getting school report PDF by mincode: {}", e.getMessage());
            return getInternalServerErrorResponse(e);
        }
    }

    public ResponseEntity<byte[]> getDistrictReportPDFByDistcode(String distCode, String type) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("PST"), Locale.CANADA);
        int year = cal.get(Calendar.YEAR);
        String month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
        try {
            Optional<District> districtDetails = Optional.ofNullable(districtService.getDistrictDetails(distCode));
            if (districtDetails.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            String districtId = districtDetails.get().getDistrictId();
            var result = restService.get(String.format(educGraduationApiConstants.getDistrictReportByDistrictIdAndReportType(), districtId, type), InputStreamResource.class);
            byte[] response = new byte[0];
            if (result != null) {
                response = result.getInputStream().readAllBytes();
            }

            return handleBinaryResponse(response, EducGradBusinessUtil.getReportsFileNameForSchoolAndDistrict(distCode,year,month,type, MediaType.APPLICATION_PDF), MediaType.APPLICATION_PDF);
        } catch (Exception e) {
            logger.error("Error getting district report PDF by distCode: {}", e.getMessage());
            return getInternalServerErrorResponse(e);
        }
    }

    public ResponseEntity<byte[]> getAmalgamatedSchoolReportPDFByMincode(String mincode, String type, String accessToken) {
        logger.debug("******** Retrieve List of Students for Amalgamated School Report ******");
        List<UUID> studentList = webClient.get().uri(String.format(educGradStudentApiConstants.getStudentsForAmalgamatedReport(), mincode, type)).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(new ParameterizedTypeReference<List<UUID>>() {
        }).block();
        List<InputStream> locations = new ArrayList<>();
        if (studentList != null && !studentList.isEmpty()) {
            logger.debug("******** Fetched {} students ******", studentList.size());
            List<List<UUID>> partitions = ListUtils.partition(studentList, 200);
            getStudentAchievementReports(partitions, locations);
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("PST"), Locale.CANADA);
            int year = cal.get(Calendar.YEAR);
            String month = "00";
            String fileName = EducGradBusinessUtil.getReportsFileNameForSchoolAndDistrict(mincode, year, month, type, MediaType.APPLICATION_PDF);
            try {
                logger.debug("******** Merging Documents Started ******");
                byte[] res = EducGradBusinessUtil.mergeDocumentsPDFs(locations);
                logger.debug("******** Merged {} Documents ******", locations.size());
                HttpHeaders headers = new HttpHeaders();
                headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList(BEARER + accessToken));
                headers.put(HttpHeaders.ACCEPT, Collections.singletonList(APPLICATION_PDF));
                headers.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(APPLICATION_PDF));
                saveBinaryResponseToFile(res, fileName);
                return handleBinaryResponse(res, fileName, MediaType.APPLICATION_PDF);
            } catch (Exception e) {
                return getInternalServerErrorResponse(e);
            }
        }
        return null;
    }

    @Transactional
    public ResponseEntity<byte[]> getStudentCredentialPDFByType(String pen, String type, String accessToken) {
        List<Student> stud = getStudentByPenFromStudentAPI(pen,accessToken);
        Student studObj = stud.get(0);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList(BEARER + accessToken));
            headers.put(HttpHeaders.ACCEPT, Collections.singletonList(APPLICATION_PDF));
            headers.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(APPLICATION_PDF));
            InputStreamResource result = webClient.get().uri(String.format(educGraduationApiConstants.getStudentCredentialByType(), studObj.getStudentID(),type)).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(InputStreamResource.class).block();
            byte[] res = new byte[0];
            if(result != null) {
                res = result.getInputStream().readAllBytes();
            }
            return handleBinaryResponse(res, EducGradBusinessUtil.getFileNameStudentCredentials(studObj.getMincode(),pen,type), MediaType.APPLICATION_PDF);
        } catch (Exception e) {
            return getInternalServerErrorResponse(e);
        }
    }

    @Transactional
    public ResponseEntity<byte[]> getStudentTranscriptPDFByType(String pen, String type, String interim, String accessToken) {
        try {
            byte[] reportData = prepareReportDataByPen(pen, type, accessToken).getBody();
            boolean isPreview = (StringUtils.isNotBlank(type) && StringUtils.equalsAnyIgnoreCase(type, "xml", "interim") ||
                    StringUtils.isNotBlank(interim) && StringUtils.equalsAnyIgnoreCase(interim, "xml", "interim"));
            StringBuilder reportRequest = new StringBuilder();
            String reportOptions = "\"options\": {\n" +
                    "        \"cacheReport\": false,\n" +
                    "        \"convertTo\": \"pdf\",\n" +
                    "        \"preview\": \""+ (isPreview ? "true" : "false") +"\",\n" +
                    "        \"overwrite\": false,\n" +
                    "        \"reportName\": \"transcript\",\n" +
                    "        \"reportFile\": \""+pen+" Transcript Report.pdf\"\n" +
                    "    },\n";
            reportRequest.append("{\n");
            reportRequest.append(reportOptions);
            reportRequest.append("\"data\":\n");
            reportRequest.append(new String(reportData)).append("\n");
            reportRequest.append("}\n");
            HttpHeaders headers = new HttpHeaders();
            headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList(BEARER + accessToken));
            headers.put(HttpHeaders.ACCEPT, Collections.singletonList(ACCEPT));
            headers.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(APPLICATION_JSON));
            byte[] result = webClient.post().uri(educGraduationApiConstants.getStudentTranscriptReportByRequest())
                    .headers(h -> h.addAll(headers)).body(BodyInserters.fromValue(reportRequest.toString())).retrieve()
                    .onStatus(
                            HttpStatus.NO_CONTENT::equals,
                            response -> response.bodyToMono(String.class).thenReturn(new ServiceException("NO_CONTENT", response.statusCode().value()))
                    )
                    .bodyToMono(byte[].class).block();
            assert result != null;
            return handleBinaryResponse(result, pen + " Transcript Report.pdf", MediaType.APPLICATION_PDF);
        } catch (ServiceException e) {
            return handleBinaryResponse(new byte[0], pen + " Transcript Report.pdf", MediaType.APPLICATION_PDF);
        } catch (Exception e) {
            return getInternalServerErrorResponse(e);
        }
    }

    private void getStudentAchievementReports(List<List<UUID>> partitions, List<InputStream> locations) {
        logger.debug("******** Getting Student Achievement Reports ******");
        for(List<UUID> studentList: partitions) {
            String accessToken = tokenUtils.getAccessToken();
            logger.debug("******** Run partition with {} students ******", studentList.size());
            List<CompletableFuture<InputStream>> futures = studentList.stream()
                    .map(studentGuid -> CompletableFuture.supplyAsync(() -> getStudentAchievementReport(studentGuid, accessToken)))
                    .toList();
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
            CompletableFuture<List<InputStream>> result = allFutures.thenApply(v -> futures.stream()
                    .map(CompletableFuture::join)
                    .toList());
            locations.addAll(result.join());
        }
        logger.debug("******** Fetched All {} Student Achievement Reports ******", locations.size());
    }

    private InputStream getStudentAchievementReport(UUID studentGuid, String accessToken) {
        try {
            String finalAccessToken = tokenUtils.isTokenExpired() ? tokenUtils.getAccessToken() : accessToken;
            InputStreamResource result = webClient.get().uri(String.format(educGraduationApiConstants.getStudentCredentialByType(), studentGuid, "ACHV")).headers(h -> h.setBearerAuth(finalAccessToken)).retrieve().bodyToMono(InputStreamResource.class).block();
            if (result != null) {
                logger.debug("******** Fetched Achievement Report for {} ******", studentGuid);
                return result.getInputStream();
            }
        } catch (Exception e) {
            logger.debug("Error extracting report binary from stream: {}", e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * Gets internal server error response.
     *
     * @param t the t
     * @return the internal server error response
     */
    protected ResponseEntity<byte[]> getInternalServerErrorResponse(Throwable t) {
        ResponseEntity<byte[]> result;

        Throwable tmp = t;
        String message;
        if (tmp.getCause() != null) {
            tmp = tmp.getCause();
            message = tmp.getMessage();
        } else {
            message = tmp.getMessage();
        }
        if(message == null) {
            message = tmp.getClass().getName();
        }

        result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message.getBytes());
        return result;
    }

    private ResponseEntity<byte[]> handleBinaryResponse(byte[] resultBinary, String reportFile, MediaType contentType) {
        ResponseEntity<byte[]> response;
        if(resultBinary.length > 0) {
            String fileType = contentType.getSubtype().toUpperCase();
            logger.debug("Sending {} response {} KB", fileType, resultBinary.length/(1024));
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=" + reportFile);
            response = ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(contentType)
                    .body(resultBinary);
        } else {
            response = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return response;
    }

    private void saveBinaryResponseToFile(byte[] resultBinary, String reportFile) throws IOException {
        if(resultBinary.length > 0) {
            String pathToFile = TMP + File.separator + reportFile;
            logger.debug("Save generated PDF {} on the file system", reportFile);
            File fileToSave = new File(pathToFile);
            if(Files.deleteIfExists(fileToSave.toPath())) {
                logger.debug("Delete existing PDF {}", reportFile);
            }
            Files.write(fileToSave.toPath(), resultBinary);
            logger.debug("PDF {} saved successfully", pathToFile);
        }
    }
}
