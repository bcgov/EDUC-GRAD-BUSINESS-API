package ca.bc.gov.educ.api.gradbusiness.service;

import ca.bc.gov.educ.api.gradbusiness.model.dto.District;
import ca.bc.gov.educ.api.gradbusiness.util.EducGraduationApiConstants;
import ca.bc.gov.educ.api.gradbusiness.util.JsonTransformer;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DistrictService {

    EducGraduationApiConstants educGraduationApiConstants;
    final RESTService restService;
    JsonTransformer jsonTransformer;

    private static Logger logger = LoggerFactory.getLogger(DistrictService.class);

    @Autowired
    public DistrictService(RESTService restService, JsonTransformer jsonTransformer, EducGraduationApiConstants educGraduationApiConstants) {
        this.restService = restService;
        this.jsonTransformer = jsonTransformer;
        this.educGraduationApiConstants = educGraduationApiConstants;
    }

    public District getDistrictDetails(String distNo) {
        var response = this.restService.get(String.format(educGraduationApiConstants.getDistrictDetails(),distNo), District.class);
        return jsonTransformer.convertValue(response, new TypeReference<>() {});
    }
}
