package ca.bc.gov.educ.api.gradbusiness.v2;

import ca.bc.gov.educ.api.gradbusiness.EducGradBusinessApiApplication;
import ca.bc.gov.educ.api.gradbusiness.model.dto.District;
import ca.bc.gov.educ.api.gradbusiness.service.GradBusinessService;
import ca.bc.gov.educ.api.gradbusiness.service.RESTService;
import ca.bc.gov.educ.api.gradbusiness.service.DistrictService;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    @SpringBootTest(classes = {EducGradBusinessApiApplication.class})
    @ActiveProfiles("test")
    class DistrictReportServiceTest {

        @MockBean
        WebClient webClient;

        @MockBean
        private GradBusinessService gradBusinessService;

        @MockBean
        private DistrictService districtService;

        @MockBean
        private RESTService restService;

        @BeforeEach
        public void setUp() {
            openMocks(this);
        }

        @Test
        void testDistrictReportByDistrictCode() throws Exception {

            String distCode = "128385861";
            String type = "NONGRADPRJ";

            byte[] samplePdf = readBinaryFile("data/sample.pdf");
            InputStreamResource pdf = new InputStreamResource(new ByteArrayInputStream(samplePdf));

            var district = District.builder().districtNumber(distCode).districtId(String.valueOf(UUID.randomUUID())).build();
            when(districtService.getDistrictDetails(anyString())).thenReturn(district);
            when(this.restService.get(any(String.class), any())).thenReturn(pdf);

            ResponseEntity<byte[]> byteData = gradBusinessService.getDistrictReportPDFByDistcode(distCode, type);
            assertNotNull(byteData);
            assertEquals(HttpStatus.OK, byteData.getStatusCode());
            assertNotNull(byteData.getBody());
        }

        @Test
        void testDistrictReportByDistrictCode_NotFound() {

            String distCode = "128385861";
            String type = "NONGRADPRJ";

            byte[] samplePdf = new byte[0];
            InputStreamResource pdf = new InputStreamResource(new ByteArrayInputStream(samplePdf));

            var district = District.builder().districtNumber(distCode).districtId(String.valueOf(UUID.randomUUID())).build();
            when(districtService.getDistrictDetails(anyString())).thenReturn(district);
            when(this.restService.get(any(String.class), any())).thenReturn(pdf);

            ResponseEntity<byte[]> byteData = gradBusinessService.getDistrictReportPDFByDistcode(distCode, type);
            assertNotNull(byteData);
            assertEquals(HttpStatus.NOT_FOUND, byteData.getStatusCode());
            assertNull(byteData.getBody());
        }

        @Test
        void testDistrictReportByDistrictCode_Error500() {

            String distCode = "128385861";
            String type = "NONGRADPRJ";

            when(districtService.getDistrictDetails(anyString())).thenThrow(new RuntimeException());

            ResponseEntity<byte[]> byteData = gradBusinessService.getDistrictReportPDFByDistcode(distCode, type);
            assertNotNull(byteData);
            assertTrue(byteData.getStatusCode().is5xxServerError());
        }

        private byte[] readBinaryFile(String path) throws Exception {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(path);
            return inputStream.readAllBytes();
        }
    }