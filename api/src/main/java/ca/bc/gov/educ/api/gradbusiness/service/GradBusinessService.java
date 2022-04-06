package ca.bc.gov.educ.api.gradbusiness.service;

import ca.bc.gov.educ.api.gradbusiness.model.dto.GradSearchStudent;
import ca.bc.gov.educ.api.gradbusiness.model.dto.Student;
import ca.bc.gov.educ.api.gradbusiness.util.EducGradStudentApiConstants;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class GradBusinessService {

    private static final Logger logger = LoggerFactory.getLogger(GradBusinessService.class);

    final WebClient webClient;
    final EducGradStudentApiConstants constants;

    @Autowired
    public GradBusinessService(WebClient webClient, EducGradStudentApiConstants constants) {
        this.webClient = webClient;
        this.constants = constants;
    }

    @Transactional
    @Retry(name = "searchbypen")
    public List<GradSearchStudent> getStudentByPenFromStudentAPI(String pen, String accessToken) {
        List<GradSearchStudent> gradStudentList = new ArrayList<>();
        List<Student> stuDataList = webClient.get().uri(String.format(constants.getPenStudentApiByPenUrl(), pen)).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(new ParameterizedTypeReference<List<Student>>() {}).block();
        return gradStudentList;
    }
}
