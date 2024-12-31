package ca.bc.gov.educ.api.gradbusiness.service.institute;

import ca.bc.gov.educ.api.gradbusiness.service.GradBusinessService;
import ca.bc.gov.educ.api.gradbusiness.util.EducGradBusinessApiConstants;
import ca.bc.gov.educ.api.gradbusiness.util.EducGradBusinessUtil;
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
@Service("schoolService")
public class SchoolService {


	final EducGradBusinessApiConstants constants;
	final WebClient webClient;

	private static Logger logger = LoggerFactory.getLogger(GradBusinessService.class);

	private static final String APPLICATION_PDF = "application/pdf";

	public ResponseEntity<byte[]> getSchoolReportPDFByMincode(String mincode, String type) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("PST"), Locale.CANADA);
		int year = cal.get(Calendar.YEAR);
		String month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
		try {

			InputStreamResource result = webClient.get().uri(String.format(constants.getSchoolReportByMincode(), mincode, type)).retrieve().bodyToMono(InputStreamResource.class).block();
			byte[] res = new byte[0];
			if (result != null) {
				res = result.getInputStream().readAllBytes();
			}
			return handleBinaryResponse(res, EducGradBusinessUtil.getFileNameSchoolReports(mincode, year, month, type, MediaType.APPLICATION_PDF), MediaType.APPLICATION_PDF);
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
}

