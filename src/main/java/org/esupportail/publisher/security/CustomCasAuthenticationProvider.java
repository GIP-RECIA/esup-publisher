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

import jakarta.servlet.http.HttpServletRequest;

import org.apereo.cas.client.validation.Assertion;
import org.apereo.cas.client.validation.TicketValidationException;
import org.apereo.cas.client.validation.TicketValidator;
import org.esupportail.publisher.service.bean.ServiceUrlHelper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.cas.authentication.CasServiceTicketAuthenticationToken;
import org.springframework.security.cas.authentication.NullStatelessTicketCache;
import org.springframework.security.cas.authentication.StatelessTicketCache;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * CAS authentication provider, kept custom only because the CAS service URL must be
 * validated against app.security.authorized-domain-names (see SecurityUtils.makeDynamicCASServiceUrl).
 * No longer carries any "remember the original target url" logic - that is handled by the
 * standard SavedRequestAwareAuthenticationSuccessHandler / HttpSessionRequestCache.
 */
public class CustomCasAuthenticationProvider implements AuthenticationProvider, InitializingBean, MessageSourceAware {

    private AuthenticationUserDetailsService<CasAssertionAuthenticationToken> authenticationUserDetailsService;
    private final UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();
    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private StatelessTicketCache statelessTicketCache = new NullStatelessTicketCache();
    private String key;
    private TicketValidator ticketValidator;
    private ServiceProperties serviceProperties;
    private ServiceUrlHelper urlHelper;
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.authenticationUserDetailsService, "An authenticationUserDetailsService must be set");
        Assert.notNull(this.ticketValidator, "A ticketValidator must be set");
        Assert.notNull(this.statelessTicketCache, "A statelessTicketCache must be set");
        Assert.notNull(this.serviceProperties, "serviceProperties must be set");
        Assert.notNull(this.urlHelper, "urlHelper must be set");
        Assert.hasText(this.key,
            "A Key is required so CasAuthenticationProvider can identify tokens it previously authenticated");
        Assert.notNull(this.messages, "A message source must be set");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }

        if (authentication instanceof CasAuthenticationToken) {
            if (this.key.hashCode() == ((CasAuthenticationToken) authentication).getKeyHash()) {
                return authentication;
            }
            throw new BadCredentialsException(messages.getMessage("CasAuthenticationProvider.incorrectKey",
                "The presented CasAuthenticationToken does not contain the expected key"));
        }

        if (authentication.getCredentials() == null || "".equals(authentication.getCredentials())) {
            throw new BadCredentialsException(messages.getMessage("CasAuthenticationProvider.noServiceTicket",
                "Failed to provide a CAS service ticket to validate"));
        }

        final String ticket = authentication.getCredentials().toString();
        CasAuthenticationToken result = this.statelessTicketCache.getByTicketId(ticket);
        if (result == null) {
            result = authenticateNow(authentication);
            result.setDetails(authentication.getDetails());
            this.statelessTicketCache.putTicketInCache(result);
        }
        return result;
    }

    private CasAuthenticationToken authenticateNow(final Authentication authentication) throws AuthenticationException {
        try {
            final Assertion assertion = this.ticketValidator.validate(authentication.getCredentials().toString(), getServiceUrl());
            final UserDetails userDetails = loadUserByAssertion(assertion);
            userDetailsChecker.check(userDetails);
            return new CasAuthenticationToken(this.key, userDetails, authentication.getCredentials(),
                authoritiesMapper.mapAuthorities(userDetails.getAuthorities()), userDetails, assertion);
        } catch (final TicketValidationException e) {
            throw new BadCredentialsException(e.getMessage(), e);
        }
    }

    /**
     * The service URL used to validate the ticket must match exactly the one used to issue it
     * (built by CustomCasAuthenticationEntryPoint.createServiceUrl() against the same incoming request).
     */
    private String getServiceUrl() {
        final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("No request context available to rebuild the CAS service URL");
        }
        final HttpServletRequest request = attributes.getRequest();
        return SecurityUtils.makeDynamicCASServiceUrl(urlHelper, request) + this.serviceProperties.getService();
    }

    protected UserDetails loadUserByAssertion(final Assertion assertion) {
        final CasAssertionAuthenticationToken token = new CasAssertionAuthenticationToken(assertion, "");
        return this.authenticationUserDetailsService.loadUserDetails(token);
    }

    public boolean supports(final Class<?> authentication) {
        return CasServiceTicketAuthenticationToken.class.isAssignableFrom(authentication)
            || CasAuthenticationToken.class.isAssignableFrom(authentication)
            || CasAssertionAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void setAuthenticationUserDetailsService(
        final AuthenticationUserDetailsService<CasAssertionAuthenticationToken> authenticationUserDetailsService) {
        this.authenticationUserDetailsService = authenticationUserDetailsService;
    }

    public void setServiceProperties(final ServiceProperties serviceProperties) {
        this.serviceProperties = serviceProperties;
    }

    public void setUrlHelper(final ServiceUrlHelper urlHelper) {
        this.urlHelper = urlHelper;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setStatelessTicketCache(final StatelessTicketCache statelessTicketCache) {
        this.statelessTicketCache = statelessTicketCache;
    }

    public void setTicketValidator(final TicketValidator ticketValidator) {
        this.ticketValidator = ticketValidator;
    }

    public void setAuthoritiesMapper(GrantedAuthoritiesMapper authoritiesMapper) {
        this.authoritiesMapper = authoritiesMapper;
    }

    public void setMessageSource(final MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }
}
