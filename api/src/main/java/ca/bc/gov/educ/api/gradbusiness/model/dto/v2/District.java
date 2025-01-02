package ca.bc.gov.educ.api.gradbusiness.model.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@EqualsAndHashCode(callSuper = true)
@Component("District")
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
