package ca.bc.gov.educ.api.gradbusiness.controller.v2;

import ca.bc.gov.educ.api.gradbusiness.service.v2.DistrictService;
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
@RestController("districtController")
@RequestMapping("/api/v2/district")
@Slf4j
@OpenAPIDefinition(info = @Info(title = "API for District Data.", description = "This Read API is for Reading District data.", version = "2"),
        security = {@SecurityRequirement(name = "OAUTH2", scopes = {"SCOPE_READ_GRAD_SCHOOL_REPORT"})})
public class DistrictController {

    EducGradBusinessApiConstants educGradBusinessApiConstants;
    DistrictService districtService;

    @Autowired
    public DistrictController(DistrictService districtService) {
        this.districtService = districtService;

    }

    @GetMapping(EducGradBusinessApiConstants.SCHOOL_REPORT_PDF_DISTCODE_V2)
    @PreAuthorize("hasAuthority('SCOPE_READ_GRAD_SCHOOL_REPORT')")
    @Operation(summary = "Get School Report pdf from graduation by distcode and report type", description = "Get School Report pdf from graduation by distcode and report type", tags = { "Graduation Data" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<byte[]> schoolReportByDistrictCode(@PathVariable String distcode, @RequestParam(name = "type") String type) {
        return districtService.getSchoolReportPDFByDistcode(distcode, type);
    }
}
