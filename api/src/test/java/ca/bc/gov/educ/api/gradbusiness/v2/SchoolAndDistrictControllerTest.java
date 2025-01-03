package ca.bc.gov.educ.api.gradbusiness.v2;


import ca.bc.gov.educ.api.gradbusiness.controller.v2.SchoolController;
import ca.bc.gov.educ.api.gradbusiness.controller.v2.DistrictController;
import ca.bc.gov.educ.api.gradbusiness.service.RESTService;
import ca.bc.gov.educ.api.gradbusiness.service.v2.SchoolDetailsService;
import ca.bc.gov.educ.api.gradbusiness.service.v2.DistrictService;
import ca.bc.gov.educ.api.gradbusiness.util.EducGraduationApiConstants;
import ca.bc.gov.educ.api.gradbusiness.util.TokenUtils;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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
class SchoolAndDistrictControllerTest {

    @MockBean
    private TokenUtils tokenUtils;

    @Mock
    private SchoolDetailsService schoolDetailsService;

    @Mock
    private DistrictService districtService;

    @InjectMocks
    private SchoolController schoolController;

    @InjectMocks
    private DistrictController districtController;

    @Autowired
    private EducGraduationApiConstants educGraduationApiConstants;

    @MockBean
    private RESTService restService;

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

        Mockito.when(schoolDetailsService.getSchoolReportPDFByMincode("12312321", "GRAD")).thenReturn(response);
        schoolController.schoolReportByMincode("12312321","GRAD");
        Mockito.verify(schoolDetailsService).getSchoolReportPDFByMincode("12312321","GRAD");

    }

    @Test
    void testSchoolReportBydistCode() throws Exception {
        byte[] greBPack = "Any String you want".getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=xmlTranscriptData.json");
        ResponseEntity response = ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_XML)
                .body(greBPack);

        Mockito.when(districtService.getSchoolReportPDFByDistcode("12312321", "GRAD")).thenReturn(response);
        districtController.schoolReportByDistrictCode("12312321","GRAD");
        Mockito.verify(districtService).getSchoolReportPDFByDistcode("12312321","GRAD");

    }
}










