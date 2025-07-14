package shop.wannab.book_service.global.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter @Setter
@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticsearchConfig {

    private String host;
    private int port;
    private String username;
    private String password;

    @Bean
    public ElasticsearchClient elasticsearchClient(
            @Qualifier("elasticsearchObjectMapper") ObjectMapper elasticsearchObjectMapper
    ) {
        RestClient restClient = RestClient
                .builder(new HttpHost(host, port, "http"))
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("Authorization", basicAuthHeader(username, password))
                })
                .build();

        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper(elasticsearchObjectMapper)
        );

        return new ElasticsearchClient(transport);
    }

    private String basicAuthHeader(String username, String password) {
        String auth = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
    }
}
