package ca.bc.gov.educ.api.gradbusiness.config;

import ca.bc.gov.educ.api.gradbusiness.util.EducGradBusinessApiConstants;
import ca.bc.gov.educ.api.gradbusiness.util.JwtUtil;
import ca.bc.gov.educ.api.gradbusiness.util.LogHelper;
import ca.bc.gov.educ.api.gradbusiness.util.ThreadLocalStateUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.UUID;

@Component
@Slf4j
public class RequestResponseInterceptor implements AsyncHandlerInterceptor {

    EducGradBusinessApiConstants constants;

    @Autowired
    public RequestResponseInterceptor(EducGradBusinessApiConstants constants) {
        this.constants = constants;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // for async this is called twice so need a check to avoid setting twice.
        if (request.getAttribute("startTime") == null) {
            final long startTime = Instant.now().toEpochMilli();
            request.setAttribute("startTime", startTime);
        }
        // correlationID
        val correlationID = request.getHeader(EducGradBusinessApiConstants.CORRELATION_ID);
        ThreadLocalStateUtil.setCorrelationID(correlationID != null ? correlationID : UUID.randomUUID().toString());

        //Request Source
        val requestSource = request.getHeader(EducGradBusinessApiConstants.REQUEST_SOURCE);
        if(requestSource != null) {
            ThreadLocalStateUtil.setRequestSource(requestSource);
        }

        // Header userName
        val userName = request.getHeader(EducGradBusinessApiConstants.USER_NAME);
        if (userName != null) {
            ThreadLocalStateUtil.setCurrentUser(userName);
        }
        else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth instanceof JwtAuthenticationToken) {
                JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) auth;
                Jwt jwt = (Jwt) authenticationToken.getCredentials();
                String username = JwtUtil.getName(jwt);
                if (username != null) {
                    ThreadLocalStateUtil.setCurrentUser(username);
                }
            }
        }
        return true;
    }

    /**
     * After completion.
     *
     * @param request  the request
     * @param response the response
     * @param handler  the handler
     * @param ex       the ex
     */
    @Override
    public void afterCompletion(@NonNull final HttpServletRequest request, final HttpServletResponse response, @NonNull final Object handler, final Exception ex) {
        LogHelper.logServerHttpReqResponseDetails(request, response, constants.isSplunkLogHelperEnabled());
        ThreadLocalStateUtil.clear();
    }

}