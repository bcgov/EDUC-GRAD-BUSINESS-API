package ca.bc.gov.educ.api.gradbusiness;

import ca.bc.gov.educ.api.gradbusiness.controller.GradBusinessController;
import ca.bc.gov.educ.api.gradbusiness.model.dto.Student;
import ca.bc.gov.educ.api.gradbusiness.service.GradBusinessService;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
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
import static org.mockito.MockitoAnnotations.openMocks;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class EducGradBusinessApiControllerTests {

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

		Mockito.when(gradBusinessService.prepareReportDataByPen(pen, null, "abc")).thenReturn(response);
		gradBusinessController.transcriptReportDataByPen(pen, null, "abc");
		Mockito.verify(gradBusinessService).prepareReportDataByPen(pen, null, "abc");

		reportData = readFile("json/studentCertificateReportData.json");
		assertNotNull(reportData);

		headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=StudentGraduationData.json");
		response = ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_JSON)
				.body(reportData.getBytes());

		Mockito.when(gradBusinessService.prepareReportDataByPen(pen, "CERT", "abc")).thenReturn(response);
		gradBusinessController.certificateReportDataByPen(pen, "abc");
		Mockito.verify(gradBusinessService).prepareReportDataByPen(pen, "CERT", "abc");

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


		Mockito.when(gradBusinessService.prepareReportDataByGraduation(studentGradData, null, "abc")).thenReturn(response);
		gradBusinessController.transcriptReportDataFromGraduation(studentGradData, null, "abc");
		Mockito.verify(gradBusinessService).prepareReportDataByGraduation(studentGradData, null, "abc");

		reportData = readFile("json/studentCertificateReportData.json");
		assertNotNull(reportData);

		 headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=StudentGraduationData.json");
		response = ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_JSON)
				.body(reportData.getBytes());

		Mockito.when(gradBusinessService.prepareReportDataByGraduation(studentGradData, "CERT", "abc")).thenReturn(response);
		gradBusinessController.certificateReportDataFromGraduation(studentGradData, "abc");
		Mockito.verify(gradBusinessService).prepareReportDataByGraduation(studentGradData, "CERT", "abc");
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

		Mockito.when(gradBusinessService.prepareXmlTranscriptReportDataByXmlRequest(xmlReportRequest, "abc")).thenReturn(response);
		gradBusinessController.transcriptXmlReportDataFromXmlRequest(xmlReportRequest, "abc");
		Mockito.verify(gradBusinessService).prepareXmlTranscriptReportDataByXmlRequest(xmlReportRequest, "abc");

	}

	@Test
	void testGetGradStudentByPenFromStudentAPI() throws Exception {

		Student obj = new Student();
		obj.setPen("12312321");
		obj.setStudentID(UUID.randomUUID().toString());

		Mockito.when(gradBusinessService.getStudentByPenFromStudentAPI("12312321", "abc")).thenReturn(List.of(obj));
		gradBusinessController.getGradStudentByPenFromStudentAPI("12312321", "abc");
		Mockito.verify(gradBusinessService).getStudentByPenFromStudentAPI("12312321", "abc");

	}

	@Test
	void testGetGradStudentDemographicsByPen() throws Exception {
		byte[] greBPack = "Any String you want".getBytes();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=xmlTranscriptData.json");
		ResponseEntity response = ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_XML)
				.body(greBPack);

		Mockito.when(gradBusinessService.getStudentDemographicsByPen("12312321", "abc")).thenReturn(response);
		gradBusinessController.getGradStudentDemographicsByPen("12312321", "abc");
		Mockito.verify(gradBusinessService).getStudentDemographicsByPen("12312321", "abc");

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

		Mockito.when(gradBusinessService.getSchoolReportPDFByMincode("12312321", "GRAD","abc")).thenReturn(response);
		gradBusinessController.schoolReportByMincode("12312321","GRAD", "abc");
		Mockito.verify(gradBusinessService).getSchoolReportPDFByMincode("12312321","GRAD", "abc");

	}

	@Test
	void testAmalgamatedSchoolReportByMincode() throws Exception {
		byte[] greBPack = "Any String you want".getBytes();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=xmlTranscriptData.json");
		ResponseEntity response = ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_XML)
				.body(greBPack);

		Mockito.when(gradBusinessService.getAmalgamatedSchoolReportPDFByMincode("12312321", "TVRNONGRAD","abc")).thenReturn(response);
		gradBusinessController.amalgamatedSchoolReportByMincode("12312321","TVRNONGRAD", "abc");
		Mockito.verify(gradBusinessService).getAmalgamatedSchoolReportPDFByMincode("12312321","TVRNONGRAD", "abc");

	}

	@Test
	void testStudentCredentialByType() throws Exception {
		byte[] greBPack = "Any String you want".getBytes();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=xmlTranscriptData.json");
		ResponseEntity response = ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_XML)
				.body(greBPack);

		Mockito.when(gradBusinessService.getStudentCredentialPDFByType("12312321","TRAN", "abc")).thenReturn(response);
		gradBusinessController.studentCredentialByType("12312321", "TRAN","abc");
		Mockito.verify(gradBusinessService).getStudentCredentialPDFByType("12312321","TRAN", "abc");

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
