package ca.bc.gov.educ.api.gradbusiness.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class EducGraduationApiConstants {
    /**
     * The constant GRADUATION_API.
     */
    public static final String GRADUATION_API = "GRAD-GRADUATION-API";

    //API end-point Mapping constants
    public static final String API_ROOT_MAPPING = "";
    public static final String API_VERSION = "v1";
    public static final String GRADUATE_TRANSCRIPT_REPORT_DATA_BY_PEN = "/transcript/report/data/{pen}";
    public static final String GRADUATE_TRANSCRIPT_REPORT_DATA = "/transcript/report/data";
    public static final String GRADUATE_CERTIFICATE_REPORT_DATA_BY_PEN = "/certificate/report/data/{pen}";
    public static final String GRADUATE_CERTIFICATE_REPORT_DATA = "/certificate/report/data";

    //Default Date format constants
    public static final String DEFAULT_CREATED_BY = "API_GRADUATION";
    public static final String DEFAULT_UPDATED_BY = "API_GRADUATION";

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String TRAX_DATE_FORMAT = "yyyyMM";

    //Endpoints
    @Value("${endpoint.grad-graduation-api.report-data-by-pen.url}")
    private String graduateReportDataByPenUrl;

    @Value("${endpoint.grad-graduation-api.report-data-by-graduation.url}")
    private String gaduateReportDataByGraduation;
    
}
