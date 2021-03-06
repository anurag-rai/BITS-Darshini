package in.ac.bits.protocolanalyzer.persistence.repository;

import javax.annotation.Resource;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "in.ac.bits.protocolanalyzer.persistence.repository")
@PropertySource(value = "classpath:META-INF/elasticsearch.properties")
public class ElasticsearchConfig {

    @Resource
    private Environment environment;

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        String clusterName = environment
                .getProperty("elasticsearch.cluster.name");
        String nodeName = environment.getProperty("elasticsearch.node.name");
        String corsEnabled = environment
                .getProperty("elasticsearch.http.cors.enabled");
        String allowOrigin = environment
                .getProperty("elasticsearch.http.cors.allow-origin");
        String allowMethods = environment
                .getProperty("elasticsearch.http.cors.allow-methods");
        String allowHeaders = environment
                .getProperty("elasticsearch.http.cors.allow-headers");
	String dataPath = environment
		.getProperty("elasticsearch.path.data");
	String logPath = environment
		.getProperty("elasticsearch.path.logs");
        ImmutableSettings.Builder settingsBuilder = ImmutableSettings
                .settingsBuilder().put("cluster.name", clusterName)
                .put("node.name", nodeName).put("node.data", true)
		.put("path.data", dataPath).put("path.logs", logPath)
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 0)
                .put("http.cors.enabled",
                        Boolean.valueOf(corsEnabled).booleanValue())
                .put("http.cors.allow-origin", allowOrigin)
                .put("http.cors.allow-methods", allowMethods)
                .put("http.cors.allow-headers", allowHeaders);
        NodeBuilder builder = NodeBuilder.nodeBuilder().local(true)
                .settings(settingsBuilder.build());
        return new ElasticsearchTemplate(builder.node().client());
    }

}
