package cn.codesensi.leaf.rbac.framework.config;

import cn.codesensi.leaf.rbac.common.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;

/**
 * RestClient 配置类
 * <p>
 * 配置项目中使用的所有 RestClient 实例，包括超时、SSL 等设置。
 *
 * @author codesensi
 * @since 2026-07-10
 */
@RequiredArgsConstructor
@Configuration
public class RestClientConfig {

    private final AppProperties appProperties;

    /**
     * 民政部地名服务客户端
     */
    @Bean
    @Qualifier("mcaDmfwClient")
    public RestClient mcaDmfwClient() throws NoSuchAlgorithmException, KeyManagementException {

        // 1. 创建信任所有证书的 TrustManager
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        // 2. 初始化 SSLContext 并设置 TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        // 3. 构建 Java HttpClient，禁用主机名验证
        HttpClient httpClient = HttpClient.newBuilder()
                .sslContext(sslContext)
                .sslParameters(new SSLParameters() {{
                    setEndpointIdentificationAlgorithm(null); // 禁用主机名验证
                }})
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        // 4. 创建 JdkClientHttpRequestFactory 并设置超时
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(60));

        // 5. 构建 RestClient
        return RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(appProperties.getMcaDmfwApi())
                .build();
    }
}
