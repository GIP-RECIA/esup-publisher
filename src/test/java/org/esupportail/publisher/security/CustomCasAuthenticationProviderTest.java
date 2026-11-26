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
package org.esupportail.publisher.security;

import java.util.List;

import org.apereo.cas.client.authentication.AttributePrincipal;
import org.apereo.cas.client.validation.Assertion;
import org.apereo.cas.client.validation.TicketValidationException;
import org.apereo.cas.client.validation.TicketValidator;
import org.esupportail.publisher.service.bean.ServiceUrlHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.cas.authentication.CasServiceTicketAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Regression test for the Spring Security 7 migration: CasAuthenticationFilter now wraps the
 * ticket in a CasServiceTicketAuthenticationToken, which supports() must accept.
 *
 * @see CustomCasAuthenticationProvider
 */
public class CustomCasAuthenticationProviderTest {

    private static final String TICKET = "ST-1-fake-ticket";
    private static final String KEY = "test-key";

    private TicketValidator ticketValidator;
    private AuthenticationUserDetailsService<CasAssertionAuthenticationToken> authenticationUserDetailsService;
    private CustomCasAuthenticationProvider provider;

    @BeforeEach
    public void setUp() {
        ticketValidator = mock(TicketValidator.class);
        //noinspection unchecked
        authenticationUserDetailsService = mock(AuthenticationUserDetailsService.class);

        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService("j_spring_cas_security_check");

        ServiceUrlHelper urlHelper = new ServiceUrlHelper(
            "/publisher", List.of("http://localhost"), "http://", "/view/item/", "/view/mynews/");

        provider = new CustomCasAuthenticationProvider();
        provider.setTicketValidator(ticketValidator);
        provider.setAuthenticationUserDetailsService(authenticationUserDetailsService);
        provider.setServiceProperties(serviceProperties);
        provider.setUrlHelper(urlHelper);
        provider.setKey(KEY);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/publisher/j_spring_cas_security_check");
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(80);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    public void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void supportsCasServiceTicketAuthenticationToken() {
        assertThat(provider.supports(CasServiceTicketAuthenticationToken.class), equalTo(true));
    }

    @Test
    public void authenticatesAValidServiceTicket() throws Exception {
        Assertion assertion = mock(Assertion.class);
        AttributePrincipal principal = mock(AttributePrincipal.class);
        when(principal.getName()).thenReturn("jdoe");
        when(assertion.getPrincipal()).thenReturn(principal);
        when(ticketValidator.validate(eq(TICKET), anyString())).thenReturn(assertion);

        UserDetails userDetails = new User("jdoe", "", AuthorityUtils.createAuthorityList("ROLE_USER"));
        when(authenticationUserDetailsService.loadUserDetails(any(CasAssertionAuthenticationToken.class)))
            .thenReturn(userDetails);

        CasServiceTicketAuthenticationToken token = CasServiceTicketAuthenticationToken.stateful(TICKET);

        CasAuthenticationToken result = (CasAuthenticationToken) provider.authenticate(token);

        assertThat(result.getName(), equalTo("jdoe"));
        assertThat(result.getAuthorities(), contains(AuthorityUtils.createAuthorityList("ROLE_USER").get(0)));
    }

    @Test
    public void rejectsAnInvalidServiceTicket() throws Exception {
        when(ticketValidator.validate(eq(TICKET), anyString()))
            .thenThrow(new TicketValidationException("ticket already consumed"));

        CasServiceTicketAuthenticationToken token = CasServiceTicketAuthenticationToken.stateful(TICKET);

        assertThrows(BadCredentialsException.class, () -> provider.authenticate(token));
    }

    @Test
    public void rejectsAMissingTicket() {
        CasServiceTicketAuthenticationToken token = CasServiceTicketAuthenticationToken.stateful("");

        assertThrows(BadCredentialsException.class, () -> provider.authenticate(token));
    }
}
