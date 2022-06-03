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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1")
@OpenAPIDefinition(info = @Info(title = "API for GRAD external clients.",
        description = "This API is for STS/ISD calling GRAD APIs.", version = "1"),
        security = {@SecurityRequirement(name = "OAUTH2", scopes = {"GRAD_BUSINESS_R"})})
public class GradBusinessController {

    private static Logger logger = LoggerFactory.getLogger(GradBusinessController.class);

    private final GradBusinessService gradBusinessService;
    private HttpServletRequest httpServletRequest;

    @Autowired
    public GradBusinessController(GradBusinessService gradBusinessService, HttpServletRequest httpServletRequest) {
        this.gradBusinessService = gradBusinessService;
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * For testing proxy connection (unsecured)
     * TODO: Remove
     * @return
     */
    @GetMapping("/logRequest")
    public ResponseEntity<?> logRequest() {
        // TODO: remove after testing
        logger.info(formatRequestDetails(httpServletRequest));
        return ResponseEntity.ok(formatRequestDetails(httpServletRequest));
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
    @PreAuthorize("hasAuthority('SCOPE_GRAD_BUSINESS_R') and hasAuthority('SCOPE_READ_GRAD_STUDENT_DATA')")
    @Operation(summary = "Get student document by PEN", description = "Get a specific document for a student by PEN", tags = { "Student Demographics" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public String getDocumentByPEN(@PathVariable String pen,
                                   @RequestParam(required = false, defaultValue = "PDF") String docType,
                                   @RequestParam(required = false, defaultValue = "PDF") String docFormat) {
        logger.info(formatRequestDetails(httpServletRequest));
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
    @PreAuthorize("hasAuthority('SCOPE_GRAD_BUSINESS_R') and hasAuthority('SCOPE_READ_GRAD_STUDENT_DATA')")
    @Operation(summary = "Search For Students by PEN", description = "Search for Student Demographics by PEN", tags = { "Student Demographics" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public List<Student> getGradStudentByPenFromStudentAPI(@PathVariable String pen, @RequestHeader(name="Authorization") String accessToken) {
        return gradBusinessService.getStudentByPenFromStudentAPI(pen,accessToken.replaceAll("Bearer ", ""));
    }

    @GetMapping(EducGraduationApiConstants.GRADUATE_TRANSCRIPT_REPORT_DATA_BY_PEN)
    @PreAuthorize("hasAuthority('SCOPE_GET_GRADUATION_DATA')")
    @Operation(summary = "Get Transcript Report data from graduation by student pen", description = "Get Transcript Report data from graduation by student pen", tags = { "Graduation Data" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<byte[]> transcriptReportDataByPen(@PathVariable String pen, @RequestParam(required = false) String type,
                                                            @RequestHeader(name="Authorization") String accessToken) {
        return gradBusinessService.prepareReportDataByPen(pen, type, accessToken.replaceAll("Bearer ", ""));
    }

    @GetMapping(EducGraduationApiConstants.GRADUATE_CERTIFICATE_REPORT_DATA_BY_PEN)
    @PreAuthorize("hasAuthority('SCOPE_GET_GRADUATION_DATA')")
    @Operation(summary = "Get Certificate Report data from graduation by student pen", description = "Get Certificate Report data from graduation by student pen", tags = { "Graduation Data" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<byte[]> certificateReportDataByPen(@PathVariable String pen, @RequestHeader(name="Authorization") String accessToken) {
        return gradBusinessService.prepareReportDataByPen(pen, "CERT", accessToken.replaceAll("Bearer ", ""));
    }

    @PostMapping(EducGraduationApiConstants.GRADUATE_TRANSCRIPT_REPORT_DATA)
    @PreAuthorize("hasAuthority('SCOPE_GET_GRADUATION_DATA')")
    @Operation(summary = "Adapt graduation data for transcript reporting", description = "Adapt graduation data for transcript reporting", tags = { "Graduation Data" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<byte[]> transcriptReportDataFromGraduation(@RequestBody String graduationData,
                                                                     @RequestParam(required = false) String type,
                                                                     @RequestHeader(name="Authorization") String accessToken) {
        return gradBusinessService.prepareReportDataByGraduation(graduationData, type, accessToken.replaceAll("Bearer ", ""));
    }

    @PostMapping(EducGraduationApiConstants.GRADUATE_TRANSCRIPT_XML_REPORT_DATA)
    @PreAuthorize("hasAuthority('SCOPE_GET_GRADUATION_DATA')")
    @Operation(summary = "Get Xml Data for transcript reporting", description = "Get Xml Data for transcript reporting", tags = { "Graduation Data" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<byte[]> transcriptXmlReportDataFromXmlRequest(@RequestBody String xmlRequest,
                                                                        @RequestHeader(name="Authorization") String accessToken) {
        return gradBusinessService.prepareXmlTranscriptReportDataByXmlRequest(xmlRequest, accessToken.replaceAll("Bearer ", ""));
    }

    @PostMapping(EducGraduationApiConstants.GRADUATE_CERTIFICATE_REPORT_DATA)
    @PreAuthorize("hasAuthority('SCOPE_GET_GRADUATION_DATA')")
    @Operation(summary = "Adapt graduation data for certificate reporting", description = "Adapt graduation data for certificate reporting", tags = { "Graduation Data" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<byte[]> certificateReportDataFromGraduation(@RequestBody String graduationData,
                                                                      @RequestHeader(name="Authorization") String accessToken) {
        return gradBusinessService.prepareReportDataByGraduation(graduationData, "CERT", accessToken.replaceAll("Bearer ", ""));
    }

    /**
     * Used for testing proxy
     * @param httpServletRequest
     * @return
     */
    private static String formatRequestDetails(HttpServletRequest httpServletRequest){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Request: \n");
        stringBuilder.append("\tHeaders: \n");
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        if(headerNames != null){
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                stringBuilder.append("\t\t" + headerName + ": " + httpServletRequest.getHeader(headerName) + "\n");
            }
        }
        return stringBuilder.toString();
    }
}
