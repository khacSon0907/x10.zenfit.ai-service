package x10.zenfit.security.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    /**
     * Danh sách các URL pattern cần bảo vệ.
     * Request không hợp lệ vào các URL này sẽ nhận 401 Unauthorized và header WWW-Authenticate tương ứng.
     */
    private List<ProtectedUrlRule> protectedUrls = new ArrayList<>();

    @Data
    public static class ProtectedUrlRule {
        private String urlPattern;
        private List<String> roles = new ArrayList<>();
        private List<String> unauthorizedWwwAuthenticateHeaders = new ArrayList<>();
    }
}
