package ca.bc.gov.educ.api.gradbusiness.service;

import ca.bc.gov.educ.api.gradbusiness.model.dto.Student;
import ca.bc.gov.educ.api.gradbusiness.util.EducGradBusinessUtil;
import ca.bc.gov.educ.api.gradbusiness.util.EducGradBusinessApiConstants;
import ca.bc.gov.educ.api.gradbusiness.util.EducGraduationApiConstants;
import io.github.resilience4j.retry.annotation.Retry;
import org.apache.commons.io.IOUtils;
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

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * The type Grad business service.
 */
@Service
public class GradBusinessService {

    private static Logger logger = LoggerFactory.getLogger(GradBusinessService.class);

    private static final String BEARER = "Bearer ";
    private static final String APPLICATION_JSON = "application/json";
    private static final String APPLICATION_PDF = "application/pdf";
    /**
     * The Web client.
     */
    final WebClient webClient;

    /**
     * The Educ grad student api constants.
     */
    @Autowired
    EducGradBusinessApiConstants educGradStudentApiConstants;

    /**
     * The Educ graduation api constants.
     */
    @Autowired
    EducGraduationApiConstants educGraduationApiConstants;

    /**
     * Instantiates a new Grad business service.
     *
     * @param webClient the web client
     */
    @Autowired
    public GradBusinessService(WebClient webClient) {
        this.webClient = webClient;
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
            assert result != null;
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
            assert result != null;
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
            assert result != null;
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
            assert result != null;
            return handleBinaryResponse(result, "student_demog_data.json", MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            return getInternalServerErrorResponse(e);
        }
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

    public ResponseEntity<byte[]> getSchoolReportPDFByMincode(String mincode, String type, String accessToken) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("PST"), Locale.CANADA);
        int year = cal.get(Calendar.YEAR);
        String month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList(BEARER + accessToken));
            headers.put(HttpHeaders.ACCEPT, Collections.singletonList(APPLICATION_PDF));
            headers.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(APPLICATION_PDF));
            InputStreamResource result = webClient.get().uri(String.format(educGraduationApiConstants.getSchoolReportByMincode(), mincode,type)).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(InputStreamResource.class).block();
            assert result != null;
            byte[] res = IOUtils.toByteArray(result.getInputStream());
            return handleBinaryResponse(res, EducGradBusinessUtil.getFileNameSchoolReports(mincode,year,month,type), MediaType.APPLICATION_PDF);
        } catch (Exception e) {
            return getInternalServerErrorResponse(e);
        }
    }

    public ResponseEntity<byte[]> getAmalgamatedSchoolReportPDFByMincode(String mincode, String type, String accessToken) {
        List<UUID> studentList = webClient.get().uri(String.format(educGradStudentApiConstants.getStudentsForAmalgamatedReport(), mincode, type)).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(new ParameterizedTypeReference<List<UUID>>() {
        }).block();
        logger.debug("******** Fetched Student List ******");
        List<InputStream> locations = new ArrayList<>();
        if (studentList != null && !studentList.isEmpty()) {
            for (UUID studentID : studentList) {
                InputStreamResource result = webClient.get().uri(String.format(educGraduationApiConstants.getStudentCredentialByType(), studentID, "ACHV")).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(InputStreamResource.class).block();
                if (result != null) {
                    try {
                        locations.add(result.getInputStream());
                    } catch (IOException e) {
                        logger.debug("Error {}",e.getLocalizedMessage());
                    }
                }
            }
            logger.debug("******** Fetched Achievement Reports ******");
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("PST"), Locale.CANADA);
            int year = cal.get(Calendar.YEAR);
            String month = "00";
            String fileName = EducGradBusinessUtil.getFileNameSchoolReports(mincode, year, month, type);
            try {
                logger.debug("******** Merged Documents ******");
                byte[] res = EducGradBusinessUtil.mergeDocuments(locations);
                HttpHeaders headers = new HttpHeaders();
                headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList(BEARER + accessToken));
                headers.put(HttpHeaders.ACCEPT, Collections.singletonList(APPLICATION_PDF));
                headers.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(APPLICATION_PDF));
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
            assert result != null;
            byte[] res = IOUtils.toByteArray(result.getInputStream());
            return handleBinaryResponse(res, EducGradBusinessUtil.getFileNameStudentCredentials(studObj.getMincode(),pen,type), MediaType.APPLICATION_PDF);
        } catch (Exception e) {
            return getInternalServerErrorResponse(e);
        }
    }





}
