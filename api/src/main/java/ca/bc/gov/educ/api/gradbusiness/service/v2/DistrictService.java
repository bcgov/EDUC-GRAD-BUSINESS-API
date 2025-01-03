package ca.bc.gov.educ.api.gradbusiness.service.v2;

import ca.bc.gov.educ.api.gradbusiness.model.dto.v2.District;
import ca.bc.gov.educ.api.gradbusiness.service.GradBusinessService;
import ca.bc.gov.educ.api.gradbusiness.service.RESTService;
import ca.bc.gov.educ.api.gradbusiness.util.EducGradBusinessApiConstants;
import ca.bc.gov.educ.api.gradbusiness.util.EducGradBusinessUtil;
import ca.bc.gov.educ.api.gradbusiness.util.EducGraduationApiConstants;
import ca.bc.gov.educ.api.gradbusiness.util.JsonTransformer;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class DistrictService {

    EducGradBusinessApiConstants constants;
    EducGraduationApiConstants educGraduationApiConstants;
    final WebClient webClient;
    final RESTService restService;
    JsonTransformer jsonTransformer;

    private static Logger logger = LoggerFactory.getLogger(DistrictService.class);

    @Autowired
    public DistrictService(EducGradBusinessApiConstants constants, WebClient webClient, RESTService restService, JsonTransformer jsonTransformer, EducGraduationApiConstants educGraduationApiConstants) {
        this.constants = constants;
        this.webClient = webClient;
        this.restService = restService;
        this.jsonTransformer = jsonTransformer;
        this.educGraduationApiConstants = educGraduationApiConstants;
    }

    public ResponseEntity<byte[]> getSchoolReportPDFByDistcode(String distCode, String type) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("PST"), Locale.CANADA);
        int year = cal.get(Calendar.YEAR);
        String month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
        try {
            Optional<District> districtDetails = Optional.ofNullable(getDistrictDetails(distCode));
            if (districtDetails.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            String districtId = districtDetails.get().getDistrictId();
            var result = restService.get(String.format(educGraduationApiConstants.getSchoolReportByDistrictIdAndReportType(), districtId, type), InputStreamResource.class);
            byte[] response = new byte[0];
            if (result != null) {
                response = result.getInputStream().readAllBytes();
            }

            return handleBinaryResponse(response, EducGradBusinessUtil.getFileNameSchoolReportsForDistrict(distCode,year,month,type, MediaType.APPLICATION_PDF), MediaType.APPLICATION_PDF);
        } catch (Exception e) {
            logger.error("Error getting school report PDF by distCode: {}", e.getMessage());
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
        if (message == null) {
            message = tmp.getClass().getName();
        }

        result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message.getBytes());
        return result;
    }


    private ResponseEntity<byte[]> handleBinaryResponse(byte[] resultBinary, String reportFile, MediaType contentType) {
        ResponseEntity<byte[]> response;
        if (resultBinary.length > 0) {
            String fileType = contentType.getSubtype().toUpperCase();
            logger.debug("Sending {} response {} KB", fileType, resultBinary.length / (1024));
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

    public District getDistrictDetails(String distNo) {
        var response = this.restService.get(String.format(educGraduationApiConstants.getDistrictDetails(),distNo), District.class);
        return jsonTransformer.convertValue(response, new TypeReference<>() {});
    }
}
