package rs.oris.back.config.log;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    private long timeInit = 0;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        timeInit = System.currentTimeMillis();
        logRequest(request);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        logResponse(request, response, timeInit, ex);
    }

    private void logRequest(HttpServletRequest request) {
        if (isSwaggerUrl(request.getRequestURL().toString())) return;

        StringBuilder sb = new StringBuilder();
        sb.append("\n================ REQUEST =================");
        sb.append("\nTime: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        sb.append("\nMethod: ").append(request.getMethod());
        sb.append("\nURL: ").append(request.getRequestURL());
        if (StringUtils.isNotBlank(request.getQueryString())) {
            sb.append("?").append(request.getQueryString());
        }
        sb.append("\nClient IP: ").append(request.getRemoteAddr());

        HttpSession session = request.getSession(false);
        if (session != null) sb.append("\nSession ID: ").append(session.getId());

        if (StringUtils.isNotBlank(request.getRemoteUser())) sb.append("\nUser: ").append(request.getRemoteUser());

        sb.append("\nHeaders: ").append(Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(
                        h -> h,
                        request::getHeader,
                        (h1, h2) -> h1
                )));

        log.info(sb.toString());
    }

    private void logResponse(HttpServletRequest request, HttpServletResponse response, long startTime, Exception ex) {
        if (isSwaggerUrl(request.getRequestURL().toString())) return;

        long duration = System.currentTimeMillis() - startTime;

        StringBuilder sb = new StringBuilder();
        sb.append("\n================ RESPONSE =================");
        sb.append("\nTime: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        sb.append("\nURL: ").append(request.getRequestURL());
        if (StringUtils.isNotBlank(request.getQueryString())) {
            sb.append("?").append(request.getQueryString());
        }
        sb.append("\nStatus: ").append(response.getStatus());
        sb.append("\nDuration: ").append(duration).append(" ms");
        sb.append("\nContent-Type: ").append(response.getContentType());

        sb.append("\nHeaders: ").append(response.getHeaderNames().stream()
                .collect(Collectors.toMap(
                        h -> h,
                        response::getHeader,
                        (h1, h2) -> h1
                )));

        if (ex != null) {
            sb.append("\nException: ").append(ex.getMessage());
        }

        log.info(sb.toString());
    }

    private boolean isSwaggerUrl(String url) {
        return url.toLowerCase().contains("swagger");
    }
}
