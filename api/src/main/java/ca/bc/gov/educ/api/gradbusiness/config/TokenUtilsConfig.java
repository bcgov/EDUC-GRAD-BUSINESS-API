package ca.bc.gov.educ.api.gradbusiness.config;

import ca.bc.gov.educ.api.gradbusiness.model.dto.ResponseObjCache;
import ca.bc.gov.educ.api.gradbusiness.util.EducGraduationApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenUtilsConfig {

    EducGraduationApiConstants constants;

    @Autowired
    public TokenUtilsConfig(EducGraduationApiConstants constants) {
        this.constants = constants;
    }

    @Bean
    public ResponseObjCache createResponseObjCache() {
        return new ResponseObjCache(constants.getTokenExpiryOffset());
    }

}
