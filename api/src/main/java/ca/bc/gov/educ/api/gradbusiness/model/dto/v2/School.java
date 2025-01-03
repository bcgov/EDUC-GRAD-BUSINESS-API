package ca.bc.gov.educ.api.gradbusiness.model.dto.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.stereotype.Component;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Component
@JsonIgnoreProperties(ignoreUnknown = true)
public class School extends BaseModel {

    private String schoolId;
    private String districtId;
    private String mincode;
    private String independentAuthorityId;
    private String schoolNumber;
    private String faxNumber;
    private String phoneNumber;
    private String email;
    private String website;
    private String displayName;
    private String displayNameNoSpecialChars;
    private String schoolReportingRequirementCode;
    private String schoolOrganizationCode;
    private String schoolCategoryCode;
    private String facilityTypeCode;
    private String openedDate;
    private String closedDate;
    private boolean canIssueTranscripts;
    private boolean canIssueCertificates;

}
