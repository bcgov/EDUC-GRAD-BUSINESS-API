package ca.bc.gov.educ.api.gradbusiness;

import ca.bc.gov.educ.api.gradbusiness.exception.ServiceException;
import ca.bc.gov.educ.api.gradbusiness.model.dto.School;
import ca.bc.gov.educ.api.gradbusiness.model.dto.Student;
import ca.bc.gov.educ.api.gradbusiness.service.GradBusinessService;
import ca.bc.gov.educ.api.gradbusiness.service.RESTService;
import ca.bc.gov.educ.api.gradbusiness.service.v2.SchoolDetailsService;
import ca.bc.gov.educ.api.gradbusiness.util.EducGradBusinessApiConstants;
import ca.bc.gov.educ.api.gradbusiness.util.EducGraduationApiConstants;
import ca.bc.gov.educ.api.gradbusiness.util.TokenUtils;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest(classes = {EducGradBusinessApiApplication.class})
@ActiveProfiles("test")
class EducGradBusinessApiApplicationTests {

	@MockBean
	WebClient webClient;

	@MockBean
	private TokenUtils tokenUtils;
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
	private EducGradBusinessApiConstants educGradStudentApiConstants;

	@Autowired
	private EducGraduationApiConstants educGraduationApiConstants;

	@Autowired
	private GradBusinessService gradBusinessService;
	@MockBean
	private SchoolDetailsService schoolService;
	@MockBean
	private RESTService restService;

	@BeforeEach
	public void setUp() {
		openMocks(this);
	}

	@Test
	void testReportDataByPen() throws Exception {

		String studentGradData = readFile("json/gradstatus.json");
		assertNotNull(studentGradData);

		String pen = "128385861";

		String reportData = readFile("json/studentTranscriptReportData.json");
		assertNotNull(reportData);

		when(this.tokenUtils.getAccessToken()).thenReturn("accessToken");

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

		when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
		when(this.requestHeadersUriMock.uri(String.format(educGraduationApiConstants.getGraduateReportDataByPenUrl(),"128385861") + "?type=CERT")).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(byte[].class)).thenReturn(Mono.just(new byte[0]));

		byteData = gradBusinessService.prepareReportDataByPen(pen, "CERT", "accessToken");
		assertNotNull(byteData);
		assertNull(byteData.getBody());

		when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
		when(this.requestHeadersUriMock.uri(String.format(educGraduationApiConstants.getGraduateReportDataByPenUrl(),"128385861") + "?type=CERT")).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(byte[].class)).thenReturn(null);

		byteData = gradBusinessService.prepareReportDataByPen(pen, "CERT", "accessToken");
		assertNotNull(byteData);
		assertTrue(byteData.getStatusCode().is5xxServerError());

	}

	@org.junit.jupiter.api.Test
	void testReportDataByGraduationData() throws Exception {

		String studentGradData = readFile("json/gradstatus.json");
		assertNotNull(studentGradData);

		String reportData = readFile("json/studentTranscriptReportData.json");
		assertNotNull(reportData);

		when(this.tokenUtils.getAccessToken()).thenReturn("accessToken");

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

		when(this.webClient.post()).thenReturn(this.requestBodyUriMock);
		when(this.requestBodyUriMock.uri(educGraduationApiConstants.getGraduateReportDataByGraduation() + "?type=CERT")).thenReturn(this.requestBodyUriMock);
		when(this.requestBodyUriMock.headers(any(Consumer.class))).thenReturn(this.requestBodyMock);
		when(this.requestBodyMock.contentType(any())).thenReturn(this.requestBodyMock);
		when(this.requestBodyMock.body(any(BodyInserter.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(byte[].class)).thenReturn(Mono.just(new byte[0]));

		byteData = gradBusinessService.prepareReportDataByGraduation(studentGradData, "CERT", "accessToken");
		assertNotNull(byteData);
		assertNull(byteData.getBody());

		when(this.webClient.post()).thenReturn(this.requestBodyUriMock);
		when(this.requestBodyUriMock.uri(educGraduationApiConstants.getGraduateReportDataByGraduation() + "?type=CERT")).thenReturn(this.requestBodyUriMock);
		when(this.requestBodyUriMock.headers(any(Consumer.class))).thenReturn(this.requestBodyMock);
		when(this.requestBodyMock.contentType(any())).thenReturn(this.requestBodyMock);
		when(this.requestBodyMock.body(any(BodyInserter.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(byte[].class)).thenReturn(null);

		byteData = gradBusinessService.prepareReportDataByGraduation(studentGradData, "CERT", "accessToken");
		assertNotNull(byteData);
		assertTrue(byteData.getStatusCode().is5xxServerError());

	}

	@org.junit.jupiter.api.Test
	void testXmlTranscriptReportData() throws Exception {

		String xmlReportRequest = readFile("json/xmlTranscriptReportRequest.json");
		assertNotNull(xmlReportRequest);

		String xmlTranscriptReportData = readFile("json/xml_report_sample.xml");
		assertNotNull(xmlTranscriptReportData);

		when(this.tokenUtils.getAccessToken()).thenReturn("accessToken");

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

		byte[] samplePdf = readBinaryFile("data/sample.pdf");
		InputStreamResource pdf = new InputStreamResource(new ByteArrayInputStream(samplePdf));

		var schoolList = List.of(School.builder().mincode(mincode).schoolId(String.valueOf(UUID.randomUUID())).build());
		when(schoolService.getSchoolDetails(anyString())).thenReturn(schoolList);
		when(this.restService.get(any(String.class), any())).thenReturn(pdf);

		ResponseEntity<byte[]> byteData = gradBusinessService.getSchoolReportPDFByMincode(mincode, type);
		assertNotNull(byteData);
		assertEquals(HttpStatus.OK, byteData.getStatusCode());
		assertNotNull(byteData.getBody());
	}

	@Test
	void testgetAmalgamatedSchoolReportPDFByMincode() throws Exception {

		String mincode = "128385861";
		String type = "TVRNONGRAD";

		byte[] samplePdf = readBinaryFile("data/sample.pdf");
		InputStreamResource pdf = new InputStreamResource(new ByteArrayInputStream(samplePdf));

		when(this.tokenUtils.getAccessToken()).thenReturn("accessToken");

		UUID studentID = UUID.randomUUID();
		when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
		when(this.requestHeadersUriMock.uri(String.format(educGradStudentApiConstants.getStudentsForAmalgamatedReport(),mincode,type))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(new ParameterizedTypeReference<List<UUID>>() {})).thenReturn(Mono.just(List.of(studentID)));

		when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
		when(this.requestHeadersUriMock.uri(String.format(educGraduationApiConstants.getStudentCredentialByType(),studentID,"ACHV"))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(InputStreamResource.class)).thenReturn(Mono.just(pdf));

		ResponseEntity<byte[]> byteData = gradBusinessService.getAmalgamatedSchoolReportPDFByMincode(mincode, type, "accessToken");
		assertNotNull(byteData);
		assertNotNull(byteData.getBody());

		pdf = new InputStreamResource(new ByteArrayInputStream(new byte[0]));

		when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
		when(this.requestHeadersUriMock.uri(String.format(educGraduationApiConstants.getStudentCredentialByType(),studentID,"ACHV"))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(InputStreamResource.class)).thenReturn(Mono.just(pdf));

		byteData = gradBusinessService.getAmalgamatedSchoolReportPDFByMincode(mincode, type, "accessToken");
		assertNotNull(byteData);
		assertNull(byteData.getBody());

	}

	@Test
	void testSchoolReportPDFByMincode_NotFound() {

		String mincode = "128385861";
		String type = "NONGRADPRJ";

		byte[] samplePdf = new byte[0];
		InputStreamResource pdf = new InputStreamResource(new ByteArrayInputStream(samplePdf));

		when(schoolService.getSchoolDetails(anyString())).thenReturn(Collections.emptyList());
		when(this.restService.get(any(String.class), any())).thenReturn(pdf);

		ResponseEntity<byte[]> byteData = gradBusinessService.getSchoolReportPDFByMincode(mincode, type);
		assertNotNull(byteData);
		assertEquals(HttpStatus.NOT_FOUND, byteData.getStatusCode());
		assertNull(byteData.getBody());
	}

	@Test
	void testSchoolReportPDFByMincode_Error500() {

		String mincode = "128385861";
		String type = "NONGRADPRJ";

		when(schoolService.getSchoolDetails(anyString())).thenThrow(new RuntimeException());

		ResponseEntity<byte[]> byteData = gradBusinessService.getSchoolReportPDFByMincode(mincode, type);
		assertNotNull(byteData);
		assertTrue(byteData.getStatusCode().is5xxServerError());
	}

	@Test
	void testGetStudentDemographicsByPen() throws Exception {

		String pen = "128385861";
		InputStream is = getClass().getClassLoader().getResourceAsStream("json/xmlTranscriptReportRequest.json");
		byte[] barr = is.readAllBytes();

		when(this.tokenUtils.getAccessToken()).thenReturn("accessToken");

		when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
		when(this.requestHeadersUriMock.uri(String.format(educGradStudentApiConstants.getPenDemographicStudentApiUrl(),pen))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(byte[].class)).thenReturn(Mono.just(barr));

		ResponseEntity<byte[]> byteData = gradBusinessService.getStudentDemographicsByPen(pen,"accessToken");
		assertNotNull(byteData);
		assertTrue(byteData.getBody().length > 0);

		when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
		when(this.requestHeadersUriMock.uri(String.format(educGradStudentApiConstants.getPenDemographicStudentApiUrl(),pen))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(byte[].class)).thenReturn(Mono.just(new byte[0]));

		byteData = gradBusinessService.getStudentDemographicsByPen(pen,"accessToken");
		assertNotNull(byteData);
		assertNull(byteData.getBody());

		when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
		when(this.requestHeadersUriMock.uri(String.format(educGradStudentApiConstants.getPenDemographicStudentApiUrl(),pen))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(byte[].class)).thenThrow();

		byteData = gradBusinessService.getStudentDemographicsByPen(pen,"accessToken");
		assertNotNull(byteData);
		assertTrue(byteData.getBody().length > 0);
		assertTrue(byteData.getStatusCode().is5xxServerError());
	}

	@Test
	void testStudentCredentialPDFByType() throws Exception {

		String pen = "128385861";
		String type = "GRADREG";

		byte[] samplePdf = readBinaryFile("data/sample.pdf");
		InputStreamResource pdf = new InputStreamResource(new ByteArrayInputStream(samplePdf));

		Student sObj = new Student();
		sObj.setStudentID(UUID.randomUUID().toString());
		sObj.setPen(pen);
		sObj.setMincode("123123112");

		when(this.tokenUtils.getAccessToken()).thenReturn("accessToken");

		when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
		when(this.requestHeadersUriMock.uri(String.format(educGraduationApiConstants.getStudentCredentialByType(),sObj.getStudentID(),type))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(InputStreamResource.class)).thenReturn(Mono.just(pdf));

		final ParameterizedTypeReference<List<Student>> responseType = new ParameterizedTypeReference<>() {
		};

		when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
		when(this.requestHeadersUriMock.uri(String.format(educGradStudentApiConstants.getPenStudentApiByPenUrl(),pen))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(responseType)).thenReturn(Mono.just(List.of(sObj)));

		ResponseEntity<byte[]> byteData = gradBusinessService.getStudentCredentialPDFByType(pen, type, "accessToken");
		assertNotNull(byteData);
		assertTrue(byteData.getBody().length > 0);
	}

	@org.junit.jupiter.api.Test
	void testStudentTranscriptPDFByTypeByPen() throws Exception {

		String pen = "128385861";

		String reportData = readFile("json/studentTranscriptReportData.json");
		assertNotNull(reportData);

		when(this.tokenUtils.getAccessToken()).thenReturn("accessToken");

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

		byte[] transcriptPdfSample = readBinaryFile("data/sample.pdf");

		when(this.webClient.post()).thenReturn(this.requestBodyUriMock);
		when(this.requestBodyUriMock.uri(educGraduationApiConstants.getStudentTranscriptReportByRequest())).thenReturn(this.requestBodyUriMock);
		when(this.requestBodyUriMock.headers(any(Consumer.class))).thenReturn(this.requestBodyMock);
		when(this.requestBodyMock.contentType(any())).thenReturn(this.requestBodyMock);
		when(this.requestBodyMock.body(any(BodyInserter.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.onStatus(any(), any())).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(byte[].class)).thenReturn(Mono.just(transcriptPdfSample));

		when(this.tokenUtils.getAccessToken()).thenReturn("accessToken");

		ResponseEntity<byte[]> transcriptPdf = gradBusinessService.getStudentTranscriptPDFByType(pen, "xml", null,"accessToken");
		assertNotNull(transcriptPdf.getBody());
		assertEquals(transcriptPdfSample,transcriptPdf.getBody());

		when(this.webClient.post()).thenReturn(this.requestBodyUriMock);
		when(this.requestBodyUriMock.uri(educGraduationApiConstants.getStudentTranscriptReportByRequest())).thenReturn(this.requestBodyUriMock);
		when(this.requestBodyUriMock.headers(any(Consumer.class))).thenReturn(this.requestBodyMock);
		when(this.requestBodyMock.contentType(any())).thenReturn(this.requestBodyMock);
		when(this.requestBodyMock.body(any(BodyInserter.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.onStatus(any(), any())).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(byte[].class)).thenThrow(new ServiceException("NO_CONTENT", 204));

		when(this.tokenUtils.getAccessToken()).thenReturn("accessToken");

		transcriptPdf = gradBusinessService.getStudentTranscriptPDFByType(pen, "xml", null,"accessToken");
		assertNotNull(transcriptPdf);
		assertNull(transcriptPdf.getBody());
	}

	@Test
	void testStudentCredentialPDFByType_NotFound() {

		String pen = "128385861";
		String type = "NONGRADREG";

		byte[] samplePdf = new byte[0];
		InputStreamResource pdf = new InputStreamResource(new ByteArrayInputStream(samplePdf));

		Student sObj = new Student();
		sObj.setStudentID(UUID.randomUUID().toString());
		sObj.setPen(pen);
		sObj.setMincode("123123112");

		when(this.tokenUtils.getAccessToken()).thenReturn("accessToken");

		when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
		when(this.requestHeadersUriMock.uri(String.format(educGraduationApiConstants.getStudentCredentialByType(),sObj.getStudentID(),type))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(InputStreamResource.class)).thenReturn(Mono.just(pdf));

		final ParameterizedTypeReference<List<Student>> responseType = new ParameterizedTypeReference<>() {
		};

		when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
		when(this.requestHeadersUriMock.uri(String.format(educGradStudentApiConstants.getPenStudentApiByPenUrl(),pen))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(responseType)).thenReturn(Mono.just(List.of(sObj)));

		ResponseEntity<byte[]> byteData = gradBusinessService.getStudentCredentialPDFByType(pen, type, "accessToken");
		assertNotNull(byteData);
		assertNull(byteData.getBody());

	}

	@Test
	void testStudentCredentialPDFByType_Error500() {

		String pen = "128385861";
		String type = "TRAN";
		String studentID = UUID.randomUUID().toString();

		Student sObj = new Student();
		sObj.setStudentID(studentID);
		sObj.setPen(pen);
		sObj.setMincode("123123112");

		final ParameterizedTypeReference<List<Student>> responseType = new ParameterizedTypeReference<>() {
		};

		when(this.tokenUtils.getAccessToken()).thenReturn("accessToken");

		when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
		when(this.requestHeadersUriMock.uri(String.format(educGradStudentApiConstants.getPenStudentApiByPenUrl(),pen))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
		when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
		when(this.responseMock.bodyToMono(responseType)).thenReturn(Mono.just(List.of(sObj)));

		ResponseEntity<byte[]> byteData = gradBusinessService.getStudentCredentialPDFByType(pen, type, "accessToken");
		assertNotNull(byteData);
		assertTrue(byteData.getBody().length > 0);
		assertTrue(byteData.getStatusCode().is5xxServerError());
	}

	private byte[] readBinaryFile(String path) throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(path);
		return inputStream.readAllBytes();
	}

}
