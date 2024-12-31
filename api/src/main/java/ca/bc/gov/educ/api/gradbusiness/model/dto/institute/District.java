package ca.bc.gov.educ.api.gradbusiness.model.dto.institute;

import ca.bc.gov.educ.api.gradbusiness.model.dto.institute.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Component("instituteDistrict")
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
