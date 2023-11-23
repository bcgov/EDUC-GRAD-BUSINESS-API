package ca.bc.gov.educ.api.gradbusiness.util;

import ca.bc.gov.educ.api.gradbusiness.model.dto.ResponseObj;
import ca.bc.gov.educ.api.gradbusiness.model.dto.ResponseObjCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class TokenUtils {
    private static Logger logger = LoggerFactory.getLogger(TokenUtils.class);

    private final EducGraduationApiConstants constants;
    private final WebClient webClient;
    private final ResponseObjCache responseObjCache;

    @Autowired
    public TokenUtils(EducGraduationApiConstants constants, WebClient webClient, ResponseObjCache responseObjCache) {
        this.constants = constants;
        this.webClient = webClient;
        this.responseObjCache = responseObjCache;
    }

    private String fetchAccessToken() {
        return this.getTokenResponseObject().getAccess_token();
    }

    public String getAccessToken() {
        return this.fetchAccessToken();
    }

    private ResponseObj getTokenResponseObject() {
        if(responseObjCache.isExpired()){
            responseObjCache.setResponseObj(getResponseObj());
        }
        return responseObjCache.getResponseObj();
    }
    
    private ResponseObj getResponseObj() {
        HttpHeaders httpHeaders = getHeaders(
                constants.getUserName(), constants.getPassword());
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        return this.webClient.post().uri(constants.getTokenUrl())
                .headers(h -> h.addAll(httpHeaders))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(map))
                .retrieve()
                .bodyToMono(ResponseObj.class).block();
    }

    private HttpHeaders getHeaders (String username,String password) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.setBasicAuth(username, password);
        return httpHeaders;
    }
}
