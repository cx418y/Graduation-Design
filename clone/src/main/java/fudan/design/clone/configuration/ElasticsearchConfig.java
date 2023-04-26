package fudan.design.clone.configuration;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {
	@Value("${spring.elasticsearch.uris}")
	private String url;

	@Override
	public @NotNull ClientConfiguration clientConfiguration() {
		return ClientConfiguration.builder()
				.connectedTo(url)
				.build();
	}
}

