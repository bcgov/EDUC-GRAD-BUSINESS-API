package ca.bc.gov.educ.api.gradbusiness.support;

import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * The type Mock configuration.
 */
@Profile("test")
@Configuration
@ContextConfiguration(classes = {MockConfiguration.class})
public class MockConfiguration {

  @Bean
  @Primary
  public RestTemplate restTemplate() {
    return Mockito.mock(RestTemplate.class);
  }

  @Bean
  @Primary
  public WebClient webClient() {
    return Mockito.mock(WebClient.class);
  }

  @Bean
  @Qualifier("gradBusinessClient")
  public WebClient getGraduationClientWebClient() {
    return Mockito.mock(WebClient.class);
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Bean
  public ClientRegistrationRepository clientRegistrationRepository() {
    return Mockito.mock(ClientRegistrationRepository.class);
  }

  @Bean
  public OAuth2AuthorizedClientService authorizedClientService() {
    return Mockito.mock(OAuth2AuthorizedClientService.class);
  }

  @Bean
  public OAuth2AuthorizedClientRepository authorizedClientRepository() {
    return Mockito.mock(OAuth2AuthorizedClientRepository.class);
  }
}
