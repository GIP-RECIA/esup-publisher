/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.publisher.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import jakarta.inject.Inject;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.security.AjaxAuthenticationFailureHandler;
import org.esupportail.publisher.security.AjaxLogoutSuccessHandler;
import org.esupportail.publisher.security.AuthoritiesConstants;
import org.esupportail.publisher.security.CustomCasAuthenticationEntryPoint;
import org.esupportail.publisher.security.CustomCasAuthenticationProvider;
import org.esupportail.publisher.security.CustomSingleSignOutFilter;
import org.esupportail.publisher.security.HasIpRangeExpressionCreator;
import org.esupportail.publisher.security.UserDetailsServiceFromCAS;
import org.esupportail.publisher.service.bean.ServiceUrlHelper;
import org.esupportail.publisher.web.FeedController;
import org.esupportail.publisher.web.filter.CsrfCookieGeneratorFilter;
import org.apereo.cas.client.validation.Cas20ServiceTicketValidator;
import fr.recia.notifications.soffit_java_client.SoffitJwtAuthenticationFilter;
import fr.recia.notifications.soffit_java_client.SoffitJwtValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@Slf4j
@Profile("!mock-no-cas")
public class SecurityConfiguration {

    private static final String APP_CONTEXT_PATH = "server.servlet.context-path";

    // preflight cache duration in the browser
    private static final Long maxAge = 600L; // 600 seconds = 10 minutes
    private static final String[] allowedMethods = {"GET","POST","PUT","DELETE","OPTIONS"};
    private static final String[] allowedHeaders = {"X-CSRF-TOKEN","Content-Type","Accept","Origin"};

    private final ESUPPublisherProperties esupPublisherProperties;

    private Environment env;

    @Inject
    private AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;

    @Inject
    private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;


    public SecurityConfiguration(Environment environment, ESUPPublisherProperties esupPublisherProperties) {
        this.env = environment;
        this.esupPublisherProperties = esupPublisherProperties;
    }

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties sp = new ServiceProperties();
        Assert.hasText(esupPublisherProperties.getSecurity().getAuthUriFilterPath(), "The CAS URI service should be set to be able to apply CAS Auth Filter, see property 'app.security.auth-uri-filter-path'");
        sp.setService(esupPublisherProperties.getSecurity().getAuthUriFilterPath());
        sp.setSendRenew(false);
        sp.setAuthenticateAllArtifacts(true);
        return sp;
    }


    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler savedRequestAwareAuthenticationSuccessHandler() {
        return new SavedRequestAwareAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> userDetailsServiceCAS() {
        return new UserDetailsServiceFromCAS();
    }

    @Bean
    public CustomCasAuthenticationProvider casAuthenticationProvider(ServiceUrlHelper serviceUrlHelper) {
        CustomCasAuthenticationProvider casAuthenticationProvider = new CustomCasAuthenticationProvider();
        casAuthenticationProvider.setAuthenticationUserDetailsService(userDetailsServiceCAS());
        casAuthenticationProvider.setServiceProperties(serviceProperties());
        casAuthenticationProvider.setUrlHelper(serviceUrlHelper);
        casAuthenticationProvider.setTicketValidator(cas20ServiceTicketValidator());
        Assert.hasText(esupPublisherProperties.getSecurity().getIdKeyProvider(), "The CAS security Key should be set, see property 'app.security.idKeyProvider'");
        casAuthenticationProvider.setKey(esupPublisherProperties.getSecurity().getIdKeyProvider());
        return casAuthenticationProvider;
    }

    @Bean
    public Cas20ServiceTicketValidator cas20ServiceTicketValidator() {
        return new Cas20ServiceTicketValidator(esupPublisherProperties.getCas().getUrlPrefix());
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter(ServiceUrlHelper serviceUrlHelper) throws Exception {
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        Assert.hasText(esupPublisherProperties.getSecurity().getAuthUriFilterPath(), "The CAS URI service should be set to be able to apply CAS Auth Filter, see property 'app.security.auth-uri-filter-path'");
        casAuthenticationFilter.setFilterProcessesUrl("/" + esupPublisherProperties.getSecurity().getAuthUriFilterPath());
        casAuthenticationFilter.setAuthenticationManager(authenticationManager(serviceUrlHelper));
        casAuthenticationFilter.setAuthenticationFailureHandler(ajaxAuthenticationFailureHandler);
        casAuthenticationFilter.setAuthenticationSuccessHandler(savedRequestAwareAuthenticationSuccessHandler());
        return casAuthenticationFilter;
    }


    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(ServiceUrlHelper serviceUrlHelper) {
        CustomCasAuthenticationEntryPoint casAuthenticationEntryPoint = new CustomCasAuthenticationEntryPoint(serviceUrlHelper);
        casAuthenticationEntryPoint.setLoginUrl(esupPublisherProperties.getCas().getUrlLogin());
        casAuthenticationEntryPoint.setServiceProperties(serviceProperties());

        LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>();
        entryPoints.put(PathPatternRequestMatcher.pathPattern("/app/**"), casAuthenticationEntryPoint);
        entryPoints.put(PathPatternRequestMatcher.pathPattern(PROTECTED_PATH + "**"), casAuthenticationEntryPoint);

        DelegatingAuthenticationEntryPoint delegatingEntryPoint = new DelegatingAuthenticationEntryPoint(entryPoints);
        delegatingEntryPoint.setDefaultEntryPoint((request, response, authException) ->
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Access Denied"));
        return delegatingEntryPoint;
    }

    @Bean
    public CustomSingleSignOutFilter singleSignOutFilter() {
        CustomSingleSignOutFilter singleSignOutFilter = new CustomSingleSignOutFilter();
        singleSignOutFilter.setCasServerUrlPrefix(esupPublisherProperties.getCas().getUrlPrefix());
        return singleSignOutFilter;
    }

    @Bean
    protected AuthenticationManager authenticationManager(ServiceUrlHelper serviceUrlHelper) throws Exception {
        return new ProviderManager(Collections.singletonList(casAuthenticationProvider(serviceUrlHelper)));
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = esupPublisherProperties.getCors();
        if (config.getAllowedMethods() == null || config.getAllowedMethods().isEmpty())
            config.setAllowedMethods(Arrays.asList(allowedMethods));
        if (config.getAllowedHeaders() == null || config.getAllowedHeaders().isEmpty())
            config.setAllowedHeaders(Arrays.asList(allowedHeaders));
        if (config.getMaxAge() == null)
            config.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        source.registerCorsConfiguration("/management/**", config);
        source.registerCorsConfiguration("/v3/api-docs", config);
        source.registerCorsConfiguration("/swagger-ui/**", config);
        return source;
    }

    @Bean
    public HasIpRangeExpressionCreator servicesPublishedIpAdressFilter(ESUPPublisherProperties esupPublisherProperties) {
        return new HasIpRangeExpressionCreator(esupPublisherProperties.getAuthorizedServices().getIpRanges());
    }

    @Bean
    public HasIpRangeExpressionCreator prometeusIpAdressFilter(ESUPPublisherProperties esupPublisherProperties) {
        return new HasIpRangeExpressionCreator(esupPublisherProperties.getMetrics().getPrometeusAuthorizedAcess().getIpRanges());
    }

    @Bean
    @Order(1) // Priorité élevée pour les URLs liées à JWT
    public SecurityFilterChain configureSoffit(HttpSecurity http) throws Exception {

        final SoffitJwtAuthenticationFilter soffitFilter =
            new SoffitJwtAuthenticationFilter(new SoffitJwtValidator(esupPublisherProperties.getSecurity().getSoffitJwtSigningKey()));

        http.securityMatcher("/news/**")
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(soffitFilter, BasicAuthenticationFilter.class)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/news/home").permitAll() // URLs publiques
            .requestMatchers("/news/**").authenticated()); // URLs nécessitant une authentification JWT

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain configure(HttpSecurity http, ServiceUrlHelper serviceUrlHelper) throws Exception {
        http.cors(Customizer.withDefaults())
            .addFilterAfter(new CsrfCookieGeneratorFilter(), CsrfFilter.class)
            .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(authenticationEntryPoint(serviceUrlHelper)))
            .addFilterBefore(casAuthenticationFilter(serviceUrlHelper), BasicAuthenticationFilter.class)
            .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class);



        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(RegexRequestMatcher.regexMatcher("^/app/.+\\.(?:js|html)$")).permitAll()
                .requestMatchers("/i18n/**").permitAll()
                .requestMatchers("/content/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/test/**").permitAll()
                .requestMatchers("/app/**").authenticated()
                .requestMatchers("/api/register").denyAll()
                .requestMatchers("/api/activate").denyAll()
                .requestMatchers("/api/authenticate").denyAll()
                .requestMatchers("/api/logs/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .requestMatchers("/api/enums/**").permitAll()
                .requestMatchers("/api/conf/**").permitAll()
                .requestMatchers("/api/admin/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .requestMatchers("/api/**").hasAuthority(AuthoritiesConstants.USER)
                .requestMatchers("/management/health").access(new WebExpressionAuthorizationManager("hasRole('" + AuthoritiesConstants.ADMIN + "') or (" + prometeusIpAdressFilter(esupPublisherProperties).getExpression()
                            + " or hasIpAddress('127.0.0.1/32') or hasIpAddress('::1'))"))
                .requestMatchers("/management/health/**").access(new WebExpressionAuthorizationManager("hasRole('" + AuthoritiesConstants.ADMIN + "') or (" + prometeusIpAdressFilter(esupPublisherProperties).getExpression()
                            + " or hasIpAddress('127.0.0.1/32') or hasIpAddress('::1'))"))
                .requestMatchers("/management/info").access(new WebExpressionAuthorizationManager("hasRole('" + AuthoritiesConstants.ADMIN + "') or (" + prometeusIpAdressFilter(esupPublisherProperties).getExpression()
                            + " or hasIpAddress('127.0.0.1/32') or hasIpAddress('::1'))"))
                .requestMatchers("/management/prometheus").access(new WebExpressionAuthorizationManager("hasRole('" + AuthoritiesConstants.ADMIN + "') or (" + prometeusIpAdressFilter(esupPublisherProperties).getExpression()
                            + " or hasIpAddress('127.0.0.1/32') or hasIpAddress('::1'))"))
                .requestMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .requestMatchers("/api-docs/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .requestMatchers("/published/**").access(new WebExpressionAuthorizationManager("hasRole('" + AuthoritiesConstants.ANONYMOUS + "') and (" + servicesPublishedIpAdressFilter(esupPublisherProperties).getExpression()
                            + " or hasIpAddress('127.0.0.1/32') or hasIpAddress('::1'))"))
                .requestMatchers(FeedController.PRIVATE_RSS_FEED_URL_PATH + "**").access(new WebExpressionAuthorizationManager("hasRole('" + AuthoritiesConstants.ANONYMOUS + "') and (" + servicesPublishedIpAdressFilter(esupPublisherProperties).getExpression()
                            + " or hasIpAddress('127.0.0.1/32') or hasIpAddress('::1'))"))
                .requestMatchers(FeedController.FEED_CONTROLLER_PATH + "/**").permitAll()
                .requestMatchers(PROTECTED_PATH + "**").authenticated()
                .requestMatchers("/view/**").permitAll()
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/images/**").permitAll()
                .requestMatchers("/files/**").permitAll()
                .requestMatchers("/fonts/**").permitAll()
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/static/**").permitAll()
                .requestMatchers("/ui/**").permitAll()
                .anyRequest().denyAll())
            .authenticationManager(authenticationManager(serviceUrlHelper))
            .logout(logout -> logout
                .logoutUrl("/api/logout")
                .logoutSuccessHandler(ajaxLogoutSuccessHandler)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll());

        return http.build();
    }

    // Specific path where pages are served (outter of REST API with ANGULAR)
    public static final String PROTECTED_PATH = "/protected/";

}
