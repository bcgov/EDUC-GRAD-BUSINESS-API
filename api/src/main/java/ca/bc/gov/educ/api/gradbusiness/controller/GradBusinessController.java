package ca.bc.gov.educ.api.gradbusiness.controller;

import ca.bc.gov.educ.api.gradbusiness.model.dto.GradSearchStudent;
import ca.bc.gov.educ.api.gradbusiness.service.GradBusinessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1")
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
    @PreAuthorize("#oauth2.hasScope('GRAD_BUSINESS_R','READ_GRAD_STUDENT_DATA')")
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
    @PreAuthorize("#oauth2.hasScope('GRAD_BUSINESS_R','READ_GRAD_STUDENT_DATA')")
    @Operation(summary = "Search For Students by PEN", description = "Search for Student Demographics by PEN", tags = { "Student Demographics" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public List<GradSearchStudent> getGradStudentByPenFromStudentAPI(@PathVariable String pen) {
        OAuth2AuthenticationDetails auth = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String accessToken = auth.getTokenValue();
        return gradBusinessService.getStudentByPenFromStudentAPI(pen,accessToken);
    }
}
