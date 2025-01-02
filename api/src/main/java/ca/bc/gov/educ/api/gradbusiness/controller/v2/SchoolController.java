package ca.bc.gov.educ.api.gradbusiness.controller.v2;

import ca.bc.gov.educ.api.gradbusiness.service.v2.SchoolDetailsService;
import ca.bc.gov.educ.api.gradbusiness.util.EducGradBusinessApiConstants;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController("schoolController")
@RequestMapping("/api/v2/school")
@Slf4j
@OpenAPIDefinition(info = @Info(title = "API for School Data.", description = "This Read API is for Reading school data from TRAX.", version = "2"),
		security = {@SecurityRequirement(name = "OAUTH2", scopes = {"READ_GRAD_SCHOOL_REPORT"})})
public class SchoolController {

    EducGradBusinessApiConstants educGradBusinessApiConstants;
    private final SchoolDetailsService schoolDetailsService;

    @Autowired
    public SchoolController(SchoolDetailsService schoolDetailsService) {
        this.schoolDetailsService = schoolDetailsService;
    }

    @GetMapping(EducGradBusinessApiConstants.SCHOOL_REPORT_PDF_MINCODE_V2)
    @PreAuthorize("hasAuthority('SCOPE_READ_GRAD_SCHOOL_REPORT')")
    @Operation(summary = "Get School Report pdf from graduation by mincode and report type", description = "Get School Report pdf from graduation by mincode and report type", tags = { "Graduation Data" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<byte[]> schoolReportByMincode(@PathVariable String mincode,@RequestParam(name = "type") String type) {
        return schoolDetailsService.getSchoolReportPDFByMincode(mincode, type);
    }

}
