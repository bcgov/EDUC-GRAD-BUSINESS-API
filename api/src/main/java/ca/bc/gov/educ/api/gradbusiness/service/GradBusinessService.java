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

@Service
public class GradBusinessService {

    private static final Logger logger = LoggerFactory.getLogger(GradBusinessService.class);

    final WebClient webClient;

    @Autowired
    EducGradStudentApiConstants educGradStudentApiConstants;

    @Autowired
    EducGraduationApiConstants educGraduationApiConstants;

    @Autowired
    public GradBusinessService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * @param pen
     * @param accessToken
     * @return
     */
    @Transactional
    @Retry(name = "searchbypen")
    public List<Student> getStudentByPenFromStudentAPI(String pen, String accessToken) {
        List<Student> stuDataList = webClient.get().uri(String.format(educGradStudentApiConstants.getPenStudentApiByPenUrl(), pen)).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(new ParameterizedTypeReference<List<Student>>() {}).block();
        return stuDataList;
    }

    public ResponseEntity<byte[]> prepareReportDataByPen(String pen, String accessToken) {
        try {
            byte[] result = webClient.get().uri(String.format(educGraduationApiConstants.getGraduateReportDataByPenUrl(), pen)).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(byte[].class).block();
            return handleBinaryResponse(result, "graduation_report_data.json", MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            return getInternalServerErrorResponse(e);
        }
    }

    public ResponseEntity<byte[]> prepareReportDataByGraduation(String graduationData, String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList("Bearer " + accessToken));
            headers.put(HttpHeaders.ACCEPT, Collections.singletonList("application/json"));
            headers.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList("application/json"));
            byte[] result = webClient.post().uri(educGraduationApiConstants.getGaduateReportDataByGraduation()).headers(h -> h.addAll(headers)).body(BodyInserters.fromValue(graduationData)).retrieve().bodyToMono(byte[].class).block();
            return handleBinaryResponse(result, "graduation_report_data.json", MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            return getInternalServerErrorResponse(e);
        }
    }

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
