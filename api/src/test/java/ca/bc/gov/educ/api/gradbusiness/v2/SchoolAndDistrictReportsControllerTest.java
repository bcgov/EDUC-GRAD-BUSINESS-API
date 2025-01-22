package ca.bc.gov.educ.api.gradbusiness.v2;


import ca.bc.gov.educ.api.gradbusiness.controller.v2.SchoolAndDistrictReportsController;
import ca.bc.gov.educ.api.gradbusiness.service.GradBusinessService;
import ca.bc.gov.educ.api.gradbusiness.service.RESTService;
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


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class SchoolAndDistrictReportsControllerTest {

    @MockBean
    private TokenUtils tokenUtils;

    @Mock
    private GradBusinessService gradBusinessService;

    @InjectMocks
    private SchoolAndDistrictReportsController schoolAndDistrictReportsController;

    @MockBean
    private RESTService restService;

    @Test
    void testSchoolReportByMincode()  {
        byte[] greBPack = "Any String you want".getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=xmlTranscriptData.json");
        ResponseEntity response = ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_XML)
                .body(greBPack);

        Mockito.when(gradBusinessService.getSchoolReportPDFByMincode("12312321", "GRAD")).thenReturn(response);
        schoolAndDistrictReportsController.schoolReportByMincode("12312321","GRAD");
        Mockito.verify(gradBusinessService).getSchoolReportPDFByMincode("12312321","GRAD");

    }

    @Test
    void testDistrictReportBydistCode()  {
        byte[] greBPack = "Any String you want".getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=xmlTranscriptData.json");
        ResponseEntity response = ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_XML)
                .body(greBPack);

        Mockito.when(gradBusinessService.getDistrictReportPDFByDistcode("12312321", "GRAD")).thenReturn(response);
        schoolAndDistrictReportsController.districtReportByDistrictCode("12312321","GRAD");
        Mockito.verify(gradBusinessService).getDistrictReportPDFByDistcode("12312321","GRAD");

    }
}










