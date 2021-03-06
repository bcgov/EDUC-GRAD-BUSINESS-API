package ca.bc.gov.educ.api.gradbusiness;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableCaching
public class EducGradBusinessApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EducGradBusinessApiApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {

		ModelMapper modelMapper = new ModelMapper();
		return modelMapper;
	}

	@Bean
	public WebClient webClient() {
		HttpClient client = HttpClient.create();
		client.warmup().block();
		return WebClient.builder().exchangeStrategies(ExchangeStrategies.builder()
				.codecs(configurer -> configurer
						.defaultCodecs()
						.maxInMemorySize(20 * 1024 * 1024))
				.build()).build();
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Configuration
	static
	class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
		/**
		 * Instantiates a new Web security configuration.
		 * This makes sure that security context is propagated to async threads as well.
		 */
		public WebSecurityConfiguration() {
			super();
			SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
		}

		@Override
		public void configure(WebSecurity web) {
			web.ignoring().antMatchers("/api/v1/api-docs-ui.html",
					"/api/v1/swagger-ui/**", "/api/v1/api-docs/**",
					"/actuator/health", "/actuator/prometheus", "/health");
		}
		@Override
		protected void configure(final HttpSecurity http) throws Exception {
			http.authorizeRequests()
					.anyRequest()
					.authenticated()
					.and()
					.oauth2ResourceServer()
					.jwt();
		}
	}
}
