package ca.bc.gov.educ.api.gradbusiness.controller;

import ca.bc.gov.educ.api.gradbusiness.model.dto.Student;
import ca.bc.gov.educ.api.gradbusiness.service.GradBusinessService;
import ca.bc.gov.educ.api.gradbusiness.util.EducGraduationApiConstants;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@EnableResourceServer
@RequestMapping("/api/v1")
@OpenAPIDefinition(info = @Info(title = "API for GRAD external clients.",
        description = "This API is for STS/ISD calling GRAD APIs.", version = "1"),
        security = {@SecurityRequirement(name = "OAUTH2", scopes = {"GRAD_BUSINESS_R"})})
public class GradBusinessController {

    private static Logger logger = LoggerFactory.getLogger(GradBusinessController.class);

    private final GradBusinessService gradBusinessService;

    public GradBusinessController(GradBusinessService gradBusinessService) {
        this.gradBusinessService = gradBusinessService;
    }

    /**
     *
     * @param pen
     * PEN
     * Doc Format - PDF/XML
     * Document TYPE
     * @return
     */
    @GetMapping("/document/{pen}")
    @PreAuthorize("#oauth2.hasScope('GRAD_BUSINESS_R') and #oauth2.hasScope('READ_GRAD_STUDENT_DATA')")
    @Operation(summary = "Get student document by PEN", description = "Get a specific document for a student by PEN", tags = { "Student Demographics" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public String getDocumentByPEN(@PathVariable String pen,
                                   @RequestParam(required = false, defaultValue = "PDF") String docType,
                                   @RequestParam(required = false, defaultValue = "PDF") String docFormat) {
        return "200 OK "
                .concat("PEN: ").concat(pen)
                .concat(", Document Type: ").concat(docType)
                .concat(", Document Format: ").concat(docFormat);
    }

    /**
     *
     * @param pen
     * @return
     */
    @GetMapping("/pen/{pen}")
    @PreAuthorize("#oauth2.hasAnyScope('GRAD_BUSINESS_R','READ_GRAD_STUDENT_DATA')")
    @Operation(summary = "Search For Students by PEN", description = "Search for Student Demographics by PEN", tags = { "Student Demographics" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public List<Student> getGradStudentByPenFromStudentAPI(@PathVariable String pen) {
        OAuth2AuthenticationDetails auth = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String accessToken = auth.getTokenValue();
        return gradBusinessService.getStudentByPenFromStudentAPI(pen,accessToken);
    }

    @GetMapping(EducGraduationApiConstants.GRADUATE_REPORT_DATA_BY_PEN)
    @PreAuthorize("#oauth2.hasAnyScope('GET_GRADUATION_DATA')")
    @Operation(summary = "Get Report data from graduation by student pen", description = "Get Report data from graduation by student pen", tags = { "Graduation Data" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<byte[]> reportDataByPen(@PathVariable String pen) {
        logger.debug("Report Data By Student Pen: " + pen);
        OAuth2AuthenticationDetails auth = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String accessToken = auth.getTokenValue();
        return gradBusinessService.prepareReportDataByPen(pen, accessToken);
    }

    @PostMapping(EducGraduationApiConstants.GRADUATE_REPORT_DATA)
    @PreAuthorize("#oauth2.hasAnyScope('GET_GRADUATION_DATA')")
    @Operation(summary = "Adapt graduation data for reporting", description = "Adapt graduation data for reporting", tags = { "Graduation Data" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<byte[]> reportDataFromGraduation(@RequestBody String graduationData) {
        logger.debug("Report Data from graduation by graduation:\n" + graduationData);
        OAuth2AuthenticationDetails auth = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String accessToken = auth.getTokenValue();
        return gradBusinessService.prepareReportDataByGraduation(graduationData, accessToken);
    }
}
