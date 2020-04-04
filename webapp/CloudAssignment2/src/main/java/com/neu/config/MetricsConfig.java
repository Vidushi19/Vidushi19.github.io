package com.neu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.timgroup.statsd.NoOpStatsDClient;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

@Configuration
public class MetricsConfig {
	@Value("${publish.metrics}")
	private boolean publishMetrics;

	@Value("${metrics.server.hostname}")
	private String metricsServerHost;

	@Value("${metrics.server.port}")
	private int metricsServerPort;

	@Bean
	public StatsDClient getStatsDClient() {

		if (publishMetrics) {
			System.out.println("inside metrics "+ metricsServerHost);
			
			return new NonBlockingStatsDClient("csye6225", metricsServerHost, metricsServerPort);
		}

		return new NoOpStatsDClient();
	}

}
