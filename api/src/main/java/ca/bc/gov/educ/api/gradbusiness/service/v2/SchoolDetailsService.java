package ca.bc.gov.educ.api.gradbusiness.service.v2;

import ca.bc.gov.educ.api.gradbusiness.model.dto.School;
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
@Service("schoolDetailsService")
public class SchoolDetailsService {


	final EducGradBusinessApiConstants constants;
	final WebClient webClient;
	final RESTService restService;
	JsonTransformer jsonTransformer;
	EducGraduationApiConstants educGraduationApiConstants;


	private static Logger logger = LoggerFactory.getLogger(SchoolDetailsService.class);


	public ResponseEntity<byte[]> getSchoolReportPDFByMincode(String mincode, String type) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("PST"), Locale.CANADA);
		int year = cal.get(Calendar.YEAR);
		String month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
		try {
			List<School> schoolDetails = getSchoolDetails(mincode);
			if (schoolDetails.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			String schoolOfRecordId = schoolDetails.get(0).getSchoolId();

			var result = restService.get(String.format(educGraduationApiConstants.getSchoolReportBySchoolIdAndReportType(), schoolOfRecordId,type), InputStreamResource.class);
			byte[] response = new byte[0];
			if (result != null) {
				response = result.getInputStream().readAllBytes();
			}

			return handleBinaryResponse(response, EducGradBusinessUtil.getFileNameSchoolReports(mincode,year,month,type,MediaType.APPLICATION_PDF), MediaType.APPLICATION_PDF);
		} catch (Exception e) {
			logger.error("Error getting school report PDF by mincode: {}", e.getMessage());
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

	public List<School> getSchoolDetails(String mincode) {
		var response = this.restService.get(String.format(constants.getSchoolDetails(),mincode), List.class);
		return jsonTransformer.convertValue(response, new TypeReference<>() {});
	}
}

