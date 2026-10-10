package x10.zenfit.security.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.util.AntPathMatcher;
import x10.zenfit.common.dto.response.ErrorResponse;
import x10.zenfit.common.exceptions.CommonError;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityProperties securityProperties;

    @Autowired(required = false)
    private ObjectMapper objectMapper;

    /**
     * Danh sách đường dẫn public cho tài liệu Swagger / OpenAPI
     */
    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    // 1. Cho phép truy cập Swagger UI & OpenAPI docs không cần đăng nhập
                    auth.requestMatchers(SWAGGER_WHITELIST).permitAll();

                    // 2. Cấu hình bảo vệ linh hoạt theo danh sách protectedUrls trong application.yml
                    List<SecurityProperties.ProtectedUrlRule> rules = securityProperties.getProtectedUrls();
                    if (rules != null && !rules.isEmpty()) {
                        for (SecurityProperties.ProtectedUrlRule rule : rules) {
                            if (rule.getUrlPattern() != null && !rule.getUrlPattern().isBlank()) {
                                List<String> roles = rule.getRoles();
                                if (roles != null && !roles.isEmpty()) {
                                    List<String> authorities = new ArrayList<>();
                                    for (String r : roles) {
                                        authorities.add(r);
                                        if (!r.startsWith("ROLE_")) {
                                            authorities.add("ROLE_" + r);
                                        }
                                    }
                                    auth.requestMatchers(rule.getUrlPattern())
                                            .hasAnyAuthority(authorities.toArray(new String[0]));
                                } else {
                                    auth.requestMatchers(rule.getUrlPattern()).authenticated();
                                }
                            }
                        }
                    }

                    // 3. Các API không nằm trong protectedUrls thì permitAll()
                    auth.anyRequest().permitAll();
                })
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                );

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        AntPathMatcher pathMatcher = new AntPathMatcher();

        return (request, response, authException) -> {
            log.warn("[SECURITY_401_UNAUTHORIZED] path={}, error={}", request.getRequestURI(), authException.getMessage());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");

            String requestPath = request.getRequestURI();
            List<SecurityProperties.ProtectedUrlRule> rules = securityProperties.getProtectedUrls();
            if (rules != null) {
                for (SecurityProperties.ProtectedUrlRule rule : rules) {
                    if (rule.getUrlPattern() != null && pathMatcher.match(rule.getUrlPattern(), requestPath)) {
                        List<String> headers = rule.getUnauthorizedWwwAuthenticateHeaders();
                        if (headers != null) {
                            for (String header : headers) {
                                if ("Basic".equalsIgnoreCase(header)) {
                                    response.addHeader("WWW-Authenticate", "Basic realm=\"Zenfit\"");
                                } else if ("Bearer".equalsIgnoreCase(header)) {
                                    response.addHeader("WWW-Authenticate", "Bearer");
                                } else {
                                    response.addHeader("WWW-Authenticate", header);
                                }
                            }
                        }
                        break;
                    }
                }
            }

            ErrorResponse body = ErrorResponse.of(
                    CommonError.UNAUTHORIZED.type(),
                    HttpServletResponse.SC_UNAUTHORIZED,
                    CommonError.UNAUTHORIZED.code(),
                    CommonError.UNAUTHORIZED.defaultMessage(),
                    null,
                    requestPath,
                    MDC.get("traceId")
            );

            getObjectMapper().writeValue(response.getOutputStream(), body);
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            log.warn("[SECURITY_403_FORBIDDEN] path={}, error={}", request.getRequestURI(), accessDeniedException.getMessage());

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");

            ErrorResponse body = ErrorResponse.of(
                    CommonError.FORBIDDEN.type(),
                    HttpServletResponse.SC_FORBIDDEN,
                    CommonError.FORBIDDEN.code(),
                    CommonError.FORBIDDEN.defaultMessage(),
                    null,
                    request.getRequestURI(),
                    MDC.get("traceId")
            );

            getObjectMapper().writeValue(response.getOutputStream(), body);
        };
    }

    private ObjectMapper getObjectMapper() {
        if (this.objectMapper == null) {
            this.objectMapper = new ObjectMapper().findAndRegisterModules();
        }
        return this.objectMapper;
    }
}
