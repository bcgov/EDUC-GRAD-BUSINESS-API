package ca.bc.gov.educ.api.gradbusiness;

import ca.bc.gov.educ.api.gradbusiness.controller.GradBusinessController;
import ca.bc.gov.educ.api.gradbusiness.model.dto.Student;
import ca.bc.gov.educ.api.gradbusiness.service.GradBusinessService;
import ca.bc.gov.educ.api.gradbusiness.util.TokenUtils;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class EducGradBusinessApiControllerTests {

	@MockBean
	private TokenUtils tokenUtils;

	@Mock
	private GradBusinessService gradBusinessService;

	@InjectMocks
	private GradBusinessController gradBusinessController;

	@Test
	void testReportDataByPen() throws Exception {

		String pen = "128385861";

		String reportData = readFile("json/studentTranscriptReportData.json");
		assertNotNull(reportData);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=StudentGraduationData.json");
		ResponseEntity response = ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_JSON)
				.body(reportData.getBytes());

		Mockito.when(gradBusinessService.prepareReportDataByPen(pen, null, "accessToken")).thenReturn(response);
		gradBusinessController.transcriptReportDataByPen(pen, null, "accessToken");
		Mockito.verify(gradBusinessService).prepareReportDataByPen(pen, null, "accessToken");

		reportData = readFile("json/studentCertificateReportData.json");
		assertNotNull(reportData);

		headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=StudentGraduationData.json");
		response = ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_JSON)
				.body(reportData.getBytes());

		Mockito.when(gradBusinessService.prepareReportDataByPen(pen, "CERT", "accessToken")).thenReturn(response);
		gradBusinessController.certificateReportDataByPen(pen, "accessToken");
		Mockito.verify(gradBusinessService).prepareReportDataByPen(pen, "CERT", "accessToken");

	}

	@Test
	void testReportDataByGraduationData() throws Exception {

		String studentGradData = readFile("json/gradstatus.json");
		assertNotNull(studentGradData);

		String reportData = readFile("json/studentTranscriptReportData.json");
		assertNotNull(reportData);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=StudentGraduationData.json");
		ResponseEntity response = ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_JSON)
				.body(reportData.getBytes());


		Mockito.when(gradBusinessService.prepareReportDataByGraduation(studentGradData, null, "accessToken")).thenReturn(response);
		gradBusinessController.transcriptReportDataFromGraduation(studentGradData, null, "accessToken");
		Mockito.verify(gradBusinessService).prepareReportDataByGraduation(studentGradData, null, "accessToken");

		reportData = readFile("json/studentCertificateReportData.json");
		assertNotNull(reportData);

		 headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=StudentGraduationData.json");
		response = ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_JSON)
				.body(reportData.getBytes());

		Mockito.when(gradBusinessService.prepareReportDataByGraduation(studentGradData, "CERT", "accessToken")).thenReturn(response);
		gradBusinessController.certificateReportDataFromGraduation(studentGradData, "accessToken");
		Mockito.verify(gradBusinessService).prepareReportDataByGraduation(studentGradData, "CERT", "accessToken");
	}

	@Test
	void testXmlTranscriptData() throws Exception {

		String xmlReportRequest = readFile("json/xmlTranscriptReportRequest.json");
		assertNotNull(xmlReportRequest);

		String xmlTranscriptReportData = readFile("json/xml_report_sample.xml");
		assertNotNull(xmlTranscriptReportData);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=xmlTranscriptData.json");
		ResponseEntity response = ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_XML)
				.body(xmlTranscriptReportData.getBytes());

		Mockito.when(gradBusinessService.prepareXmlTranscriptReportDataByXmlRequest(xmlReportRequest, "accessToken")).thenReturn(response);
		gradBusinessController.transcriptXmlReportDataFromXmlRequest(xmlReportRequest, "accessToken");
		Mockito.verify(gradBusinessService).prepareXmlTranscriptReportDataByXmlRequest(xmlReportRequest, "accessToken");

	}

	@Test
	void testGetGradStudentByPenFromStudentAPI() {

		Student obj = new Student();
		obj.setPen("12312321");
		obj.setStudentID(UUID.randomUUID().toString());

		Mockito.when(gradBusinessService.getStudentByPenFromStudentAPI("12312321", "accessToken")).thenReturn(List.of(obj));
		gradBusinessController.getGradStudentByPenFromStudentAPI("12312321", "accessToken");
		Mockito.verify(gradBusinessService).getStudentByPenFromStudentAPI("12312321", "accessToken");

	}

	@Test
	void testGetGradStudentDemographicsByPen() {
		byte[] greBPack = "Any String you want".getBytes();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=xmlTranscriptData.json");
		ResponseEntity response = ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_XML)
				.body(greBPack);

		Mockito.when(gradBusinessService.getStudentDemographicsByPen("12312321", "accessToken")).thenReturn(response);
		gradBusinessController.getGradStudentDemographicsByPen("12312321", "accessToken");
		Mockito.verify(gradBusinessService).getStudentDemographicsByPen("12312321", "accessToken");

	}

	@Test
	void testSchoolReportByMincode() throws Exception {
		byte[] greBPack = "Any String you want".getBytes();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=xmlTranscriptData.json");
		ResponseEntity response = ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_XML)
				.body(greBPack);

		Mockito.when(gradBusinessService.getSchoolReportPDFByMincode("12312321", "GRAD")).thenReturn(response);
		gradBusinessController.schoolReportByMincode("12312321","GRAD");
		Mockito.verify(gradBusinessService).getSchoolReportPDFByMincode("12312321","GRAD");

	}

	@Test
	void testAmalgamatedSchoolReportByMincode() {
		byte[] greBPack = "Any String you want".getBytes();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=xmlTranscriptData.json");
		ResponseEntity response = ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_XML)
				.body(greBPack);

		Mockito.when(gradBusinessService.getAmalgamatedSchoolReportPDFByMincode("12312321", "TVRNONGRAD")).thenReturn(response);
		gradBusinessController.amalgamatedSchoolReportByMincode("12312321","TVRNONGRAD");
		Mockito.verify(gradBusinessService).getAmalgamatedSchoolReportPDFByMincode("12312321","TVRNONGRAD");

	}

	@Test
	void testStudentCredentialByType() {
		byte[] greBPack = "Any String you want".getBytes();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=xmlTranscriptData.json");
		ResponseEntity response = ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_XML)
				.body(greBPack);

		Mockito.when(gradBusinessService.getStudentCredentialPDFByType("12312321","TRAN", "accessToken")).thenReturn(response);
		gradBusinessController.studentCredentialByType("12312321", "TRAN","accessToken");
		Mockito.verify(gradBusinessService).getStudentCredentialPDFByType("12312321","TRAN", "accessToken");

	}

	@Test
	void testStudentTranscriptPDFByType() {
		byte[] greBPack = "Any String you want".getBytes();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=StudentTranscript.pdf");
		ResponseEntity response = ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_XML)
				.body(greBPack);

		when(this.tokenUtils.getAccessToken()).thenReturn("accessToken");

		Mockito.when(gradBusinessService.getStudentTranscriptPDFByType("12312321","xml", null,"accessToken")).thenReturn(response);
		gradBusinessController.studentTranscriptByType("12312321", "xml", null, "accessToken");
		Mockito.verify(gradBusinessService).getStudentTranscriptPDFByType("12312321","xml",  null,"accessToken");

	}


	@Test
	void testGetDocumentByPEN() {
		String res = gradBusinessController.getDocumentByPEN("12312311","XML","xml");
		assertNotNull(res);
	}

	private String readFile(String jsonPath) throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(jsonPath);
		StringBuffer sb = new StringBuffer();
		InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		BufferedReader reader = new BufferedReader(streamReader);
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}
}
