package ca.bc.gov.educ.api.gradbusiness;

import ca.bc.gov.educ.api.gradbusiness.controller.GradBusinessController;
import ca.bc.gov.educ.api.gradbusiness.service.GradBusinessService;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

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

	@Before
	public void setUp() {
		openMocks(this);
	}

	@After
	public void tearDown() {

	}

	@org.junit.jupiter.api.Test
	public void testReportDataByPen() throws Exception {

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

		Authentication authentication = Mockito.mock(Authentication.class);
		OAuth2AuthenticationDetails details = Mockito.mock(OAuth2AuthenticationDetails.class);
		// Mockito.whens() for your authorization object
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		Mockito.when(authentication.getDetails()).thenReturn(details);
		SecurityContextHolder.setContext(securityContext);

		Mockito.when(gradBusinessService.prepareReportDataByPen(pen, null, details.getTokenValue())).thenReturn(response);
		gradBusinessController.transcriptReportDataByPen(pen);
		Mockito.verify(gradBusinessService).prepareReportDataByPen(pen, null, details.getTokenValue());

		reportData = readFile("json/studentCertificateReportData.json");
		assertNotNull(reportData);

		headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=StudentGraduationData.json");
		response = ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_JSON)
				.body(reportData.getBytes());

		Mockito.when(gradBusinessService.prepareReportDataByPen(pen, "CERT", details.getTokenValue())).thenReturn(response);
		gradBusinessController.certificateReportDataByPen(pen);
		Mockito.verify(gradBusinessService).prepareReportDataByPen(pen, "CERT", details.getTokenValue());

	}

	@org.junit.jupiter.api.Test
	public void testReportDataByGraduationData() throws Exception {

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

		Authentication authentication = Mockito.mock(Authentication.class);
		OAuth2AuthenticationDetails details = Mockito.mock(OAuth2AuthenticationDetails.class);
		// Mockito.whens() for your authorization object
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		Mockito.when(authentication.getDetails()).thenReturn(details);
		SecurityContextHolder.setContext(securityContext);

		Mockito.when(gradBusinessService.prepareReportDataByGraduation(studentGradData, null, details.getTokenValue())).thenReturn(response);
		gradBusinessController.transcriptReportDataFromGraduation(studentGradData);
		Mockito.verify(gradBusinessService).prepareReportDataByGraduation(studentGradData, null, details.getTokenValue());

		reportData = readFile("json/studentCertificateReportData.json");
		assertNotNull(reportData);

		 headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=StudentGraduationData.json");
		response = ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_JSON)
				.body(reportData.getBytes());

		Mockito.when(gradBusinessService.prepareReportDataByGraduation(studentGradData, "CERT", details.getTokenValue())).thenReturn(response);
		gradBusinessController.certificateReportDataFromGraduation(studentGradData);
		Mockito.verify(gradBusinessService).prepareReportDataByGraduation(studentGradData, "CERT", details.getTokenValue());
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
