package ca.bc.gov.educ.api.gradbusiness.model.dto;

import lombok.*;
import org.springframework.stereotype.Component;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Component
@NoArgsConstructor
@AllArgsConstructor
public class District extends BaseModel {

    private String districtId;
    private String districtNumber;
    private String faxNumber;
    private String phoneNumber;
    private String email;
    private String website;
    private String displayName;
    private String districtRegionCode;
    private String districtStatusCode;


}
