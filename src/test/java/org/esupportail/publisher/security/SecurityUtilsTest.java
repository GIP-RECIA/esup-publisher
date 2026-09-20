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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Test class for the SecurityUtils utility class.
 *
 * @see SecurityUtils
 */
public class SecurityUtilsTest {

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testGetCurrentLogin() {
        setAuthentication(new UsernamePasswordAuthenticationToken("admin", "password"));

        assertThat(SecurityUtils.getCurrentLogin(), equalTo("admin"));
    }

    @Test
    public void testIsAuthenticated() {
        setAuthentication(new UsernamePasswordAuthenticationToken("admin", "password",
            AuthorityUtils.createAuthorityList(AuthoritiesConstants.USER)));

        assertThat(SecurityUtils.isAuthenticated(), equalTo(true));
    }

    @Test
    public void testAnonymousIsNotAuthenticated() {
        setAuthentication(new UsernamePasswordAuthenticationToken("anonymous", "password",
            AuthorityUtils.createAuthorityList(AuthoritiesConstants.ANONYMOUS)));

        assertThat(SecurityUtils.isAuthenticated(), equalTo(false));
    }

    private void setAuthentication(UsernamePasswordAuthenticationToken authentication) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
