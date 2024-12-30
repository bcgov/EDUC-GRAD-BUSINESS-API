package ca.bc.gov.educ.api.gradbusiness.service;

import ca.bc.gov.educ.api.gradbusiness.model.dto.School;
import ca.bc.gov.educ.api.gradbusiness.util.EducGradBusinessApiConstants;
import ca.bc.gov.educ.api.gradbusiness.util.JsonTransformer;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchoolService {
	EducGradBusinessApiConstants educGraduationApiConstants;
	RESTService restService;

	JsonTransformer jsonTransformer;
	@Autowired
	public SchoolService(EducGradBusinessApiConstants educGraduationApiConstants, RESTService restService, JsonTransformer jsonTransformer) {
		this.educGraduationApiConstants = educGraduationApiConstants;
		this.restService = restService;
		this.jsonTransformer = jsonTransformer;
	}

	public List<School> getSchoolDetails(String mincode) {
		var response = this.restService.get(String.format(educGraduationApiConstants.getSchoolDetails(),mincode), List.class);
		return jsonTransformer.convertValue(response, new TypeReference<>() {});
	}
}

