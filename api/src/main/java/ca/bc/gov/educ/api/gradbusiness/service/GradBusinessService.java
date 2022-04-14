package ca.bc.gov.educ.api.gradbusiness.service;

import ca.bc.gov.educ.api.gradbusiness.model.dto.Student;
import ca.bc.gov.educ.api.gradbusiness.util.EducGradStudentApiConstants;
import ca.bc.gov.educ.api.gradbusiness.util.EducGraduationApiConstants;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The type Grad business service.
 */
@Service
public class GradBusinessService {

    private static final Logger logger = LoggerFactory.getLogger(GradBusinessService.class);

    /**
     * The Web client.
     */
    final WebClient webClient;

    /**
     * The Educ grad student api constants.
     */
    @Autowired
    EducGradStudentApiConstants educGradStudentApiConstants;

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
        List<Student> stuDataList = webClient.get().uri(String.format(educGradStudentApiConstants.getPenStudentApiByPenUrl(), pen)).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(new ParameterizedTypeReference<List<Student>>() {}).block();
        return stuDataList;
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
            headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList("Bearer " + accessToken));
            headers.put(HttpHeaders.ACCEPT, Collections.singletonList("application/json"));
            headers.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList("application/json"));
            byte[] result = webClient.post().uri(educGraduationApiConstants.getGaduateReportDataByGraduation() + "?type=" + type).headers(h -> h.addAll(headers)).body(BodyInserters.fromValue(graduationData)).retrieve().bodyToMono(byte[].class).block();
            return handleBinaryResponse(result, "graduation_report_data.json", MediaType.APPLICATION_JSON);
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
        ResponseEntity<byte[]> result = null;

        Throwable tmp = t;
        String message = null;
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
        ResponseEntity<byte[]> response = null;

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
}
