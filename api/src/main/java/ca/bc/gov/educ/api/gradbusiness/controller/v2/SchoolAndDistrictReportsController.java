package ca.bc.gov.educ.api.gradbusiness.controller.v2;

import ca.bc.gov.educ.api.gradbusiness.service.GradBusinessService;
import ca.bc.gov.educ.api.gradbusiness.util.EducGradBusinessApiConstants;
import ca.bc.gov.educ.api.gradbusiness.util.EducGraduationApiConstants;
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
@RestController
@RequestMapping(EducGraduationApiConstants.GRAD_BUSINESS_API_ROOT_MAPPING)
@Slf4j
@OpenAPIDefinition(info = @Info(title = "API for School and District reports.", description = "This Read API is for Reading school and district data from TRAX.", version = "2"),
		security = {@SecurityRequirement(name = "OAUTH2", scopes = {"GET_GRADUATION_DATA"})})
public class SchoolAndDistrictReportsController {

    private final GradBusinessService gardBusinessService;

    @Autowired
    public SchoolAndDistrictReportsController(GradBusinessService gardBusinessService) {
        this.gardBusinessService = gardBusinessService;
    }

    @GetMapping(EducGradBusinessApiConstants.SCHOOL_REPORT_PDF_MINCODE_V2)
    @PreAuthorize("hasAuthority('SCOPE_GET_GRADUATION_DATA')")
    @Operation(summary = "Get School Report pdf from graduation by mincode and report type", description = "Get School Report pdf from graduation by mincode and report type", tags = { "Graduation Data" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
    public ResponseEntity<byte[]> schoolReportByMincode(@PathVariable String mincode,@RequestParam(name = "type") String type) {
        return gardBusinessService.getSchoolReportPDFByMincode(mincode, type);
    }

    @GetMapping(EducGradBusinessApiConstants.DISTRICT_REPORT_PDF_DISTCODE_V2)
    @PreAuthorize("hasAuthority('SCOPE_GET_GRADUATION_DATA')")
    @Operation(summary = "Get District Report pdf from graduation by distcode and report type", description = "Get District Report pdf from graduation by distcode and report type", tags = { "Graduation Data" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
    public ResponseEntity<byte[]> districtReportByDistrictCode(@PathVariable String distcode, @RequestParam(name = "type") String type) {
        return gardBusinessService.getDistrictReportPDFByDistcode(distcode, type);
    }

}
