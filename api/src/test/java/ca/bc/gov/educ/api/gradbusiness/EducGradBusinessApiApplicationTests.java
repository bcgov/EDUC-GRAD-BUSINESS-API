package ca.bc.gov.educ.api.gradbusiness;

import ca.bc.gov.educ.api.gradbusiness.service.GradBusinessService;
import ca.bc.gov.educ.api.gradbusiness.util.EducGradStudentApiConstants;
import ca.bc.gov.educ.api.gradbusiness.util.EducGraduationApiConstants;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class EducGradBusinessApiApplicationTests {

	@MockBean
	WebClient webClient;

	@Mock
	private WebClient.RequestHeadersSpec requestHeadersMock;

	@Mock
	private WebClient.RequestHeadersUriSpec requestHeadersUriMock;
	@Mock
	private WebClient.RequestBodySpec requestBodyMock;
	@Mock
	private WebClient.RequestBodyUriSpec requestBodyUriMock;
	@Mock
	private WebClient.ResponseSpec responseMock;

	@Autowired
	private EducGradStudentApiConstants educGradStudentApiConstants;

	@Autowired
	private EducGraduationApiConstants educGraduationApiConstants;

	@Autowired
	private GradBusinessService gradBusinessService;

	@BeforeEach
	public void setUp() {
		openMocks(this);
	}

	@AfterEach
	public void tearDown() {

	}

	@org.junit.jupiter.api.Test
	void testReportDataByPen() throws Exception {

		String studentGradData = readFile("json/gradstatus.json");
		assertNotNull(studentGradData);

		String pen = "128385861";

		String reportData = readFile("json/studentTranscriptReportData.json");
		assertNotNull(reportData);

		when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
		when(this.requestHeadersUriMock.uri(String.format(educGraduationApiConstants.getGraduateReportDataByPenUrl(),"128385861") + "?type=")).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(byte[].class)).thenReturn(Mono.just(reportData.getBytes()));

		ResponseEntity<byte[]> byteData = gradBusinessService.prepareReportDataByPen(pen, null, "accessToken");
		assertNotNull(byteData);
		assertTrue(byteData.getBody().length > 0);
		String json = new String(byteData.getBody());
		assertEquals(json,reportData);

		reportData = readFile("json/studentCertificateReportData.json");
		assertNotNull(reportData);

		when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
		when(this.requestHeadersUriMock.uri(String.format(educGraduationApiConstants.getGraduateReportDataByPenUrl(),"128385861") + "?type=CERT")).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(byte[].class)).thenReturn(Mono.just(reportData.getBytes()));

		byteData = gradBusinessService.prepareReportDataByPen(pen, "CERT", "accessToken");
		assertNotNull(byteData);
		assertTrue(byteData.getBody().length > 0);
		json = new String(byteData.getBody());
		assertEquals(json,reportData);


	}

	@org.junit.jupiter.api.Test
	void testReportDataByGraduationData() throws Exception {

		String studentGradData = readFile("json/gradstatus.json");
		assertNotNull(studentGradData);

		String reportData = readFile("json/studentTranscriptReportData.json");
		assertNotNull(reportData);

		when(this.webClient.post()).thenReturn(this.requestBodyUriMock);
		when(this.requestBodyUriMock.uri(educGraduationApiConstants.getGraduateReportDataByGraduation() + "?type=")).thenReturn(this.requestBodyUriMock);
		when(this.requestBodyUriMock.headers(any(Consumer.class))).thenReturn(this.requestBodyMock);
		when(this.requestBodyMock.contentType(any())).thenReturn(this.requestBodyMock);
		when(this.requestBodyMock.body(any(BodyInserter.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(byte[].class)).thenReturn(Mono.just(reportData.getBytes()));

		ResponseEntity<byte[]> byteData = gradBusinessService.prepareReportDataByGraduation(studentGradData, null, "accessToken");
		assertNotNull(byteData);
		assertTrue(byteData.getBody().length > 0);
		String json = new String(byteData.getBody());
		assertEquals(json,reportData);

		when(this.webClient.post()).thenReturn(this.requestBodyUriMock);
		when(this.requestBodyUriMock.uri(educGraduationApiConstants.getGraduateReportDataByGraduation() + "?type=XML")).thenReturn(this.requestBodyUriMock);
		when(this.requestBodyUriMock.headers(any(Consumer.class))).thenReturn(this.requestBodyMock);
		when(this.requestBodyMock.contentType(any())).thenReturn(this.requestBodyMock);
		when(this.requestBodyMock.body(any(BodyInserter.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(byte[].class)).thenReturn(Mono.just(reportData.getBytes()));

		byteData = gradBusinessService.prepareReportDataByGraduation(studentGradData, "XML", "accessToken");
		assertNotNull(byteData);
		assertTrue(byteData.getBody().length > 0);
		json = new String(byteData.getBody());
		assertEquals(json,reportData);

		reportData = readFile("json/studentCertificateReportData.json");
		assertNotNull(reportData);

		when(this.webClient.post()).thenReturn(this.requestBodyUriMock);
		when(this.requestBodyUriMock.uri(educGraduationApiConstants.getGraduateReportDataByGraduation() + "?type=CERT")).thenReturn(this.requestBodyUriMock);
		when(this.requestBodyUriMock.headers(any(Consumer.class))).thenReturn(this.requestBodyMock);
		when(this.requestBodyMock.contentType(any())).thenReturn(this.requestBodyMock);
		when(this.requestBodyMock.body(any(BodyInserter.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(byte[].class)).thenReturn(Mono.just(reportData.getBytes()));

		byteData = gradBusinessService.prepareReportDataByGraduation(studentGradData, "CERT", "accessToken");
		assertNotNull(byteData);
		assertTrue(byteData.getBody().length > 0);
		json = new String(byteData.getBody());
		assertEquals(json,reportData);

	}

	@org.junit.jupiter.api.Test
	void testXmlTranscriptReportData() throws Exception {

		String xmlReportRequest = readFile("json/xmlTranscriptReportRequest.json");
		assertNotNull(xmlReportRequest);

		String xmlTranscriptReportData = readFile("json/xml_report_sample.xml");
		assertNotNull(xmlTranscriptReportData);

		when(this.webClient.post()).thenReturn(this.requestBodyUriMock);
		when(this.requestBodyUriMock.uri(educGraduationApiConstants.getXmlTranscriptReportData())).thenReturn(this.requestBodyUriMock);
		when(this.requestBodyUriMock.headers(any(Consumer.class))).thenReturn(this.requestBodyMock);
		when(this.requestBodyMock.contentType(any())).thenReturn(this.requestBodyMock);
		when(this.requestBodyMock.body(any(BodyInserter.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(byte[].class)).thenReturn(Mono.just(xmlTranscriptReportData.getBytes()));

		ResponseEntity<byte[]> byteData = gradBusinessService.prepareXmlTranscriptReportDataByXmlRequest(xmlReportRequest, "accessToken");
		assertNotNull(byteData);
		assertTrue(byteData.getBody().length > 0);
		String json = new String(byteData.getBody());
		assertEquals(json,xmlTranscriptReportData);

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

	@Test
	void testSchoolReportPDFByMincode() throws Exception {

		String mincode = "128385861";
		String type = "NONGRADPRJ";
		InputStream is = getClass().getClassLoader().getResourceAsStream("json/xmlTranscriptReportRequest.json");
		InputStreamResource pdf = new InputStreamResource(is);

		when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
		when(this.requestHeadersUriMock.uri(String.format(educGraduationApiConstants.getSchoolReportByMincode(),mincode,type))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(InputStreamResource.class)).thenReturn(Mono.just(pdf));

		ResponseEntity<byte[]> byteData = gradBusinessService.getSchoolReportPDFByMincode(mincode, type, "accessToken");
		assertNotNull(byteData);
		assertTrue(byteData.getBody().length > 0);
	}
}
