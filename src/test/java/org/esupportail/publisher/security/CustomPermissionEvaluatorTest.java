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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.IContext;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class CustomPermissionEvaluatorTest {

    @Mock
    private IPermissionService permissionService;

    private CustomPermissionEvaluator permissionEvaluator;

    @BeforeEach
    public void setUp() {
        permissionEvaluator = new CustomPermissionEvaluator();
        permissionEvaluator.permissionService = permissionService;
    }

    @Test
    public void hasPermissionGrantsLowerRoleForContext() {
        Authentication authentication = authenticated();
        ContextKey contextKey = new ContextKey(1L, ContextType.ITEM);
        IContext context = mock(IContext.class);
        when(context.getContextKey()).thenReturn(contextKey);
        when(permissionService.getRoleOfUserInContext(authentication, contextKey)).thenReturn(PermissionType.EDITOR);

        assertThat(permissionEvaluator.hasPermission(authentication, context, PermissionType.CONTRIBUTOR.name()), equalTo(true));
        verify(permissionService).getRoleOfUserInContext(authentication, contextKey);
    }

    @Test
    public void hasPermissionRejectsHigherRoleForContext() {
        Authentication authentication = authenticated();
        ContextKey contextKey = new ContextKey(1L, ContextType.ITEM);
        IContext context = mock(IContext.class);
        when(context.getContextKey()).thenReturn(contextKey);
        when(permissionService.getRoleOfUserInContext(authentication, contextKey)).thenReturn(PermissionType.CONTRIBUTOR);

        assertThat(permissionEvaluator.hasPermission(authentication, context, PermissionType.EDITOR.name()), equalTo(false));
    }

    @Test
    public void hasPermissionResolvesContextFromIdAndType() {
        Authentication authentication = authenticated();
        ContextKey contextKey = new ContextKey(2L, ContextType.CATEGORY);
        when(permissionService.getRoleOfUserInContext(authentication, contextKey)).thenReturn(PermissionType.MANAGER);

        assertThat(permissionEvaluator.hasPermission(authentication, 2L, ContextType.CATEGORY.name(), PermissionType.EDITOR.name()), equalTo(true));
        verify(permissionService).getRoleOfUserInContext(authentication, contextKey);
    }

    @Test
    public void hasPermissionRejectsUnauthenticatedPrincipal() {
        Authentication unauthenticated = mock(Authentication.class);

        assertThat(permissionEvaluator.hasPermission(unauthenticated, 1L, ContextType.ITEM.name(), PermissionType.CONTRIBUTOR.name()), equalTo(false));
        verifyNoInteractions(permissionService);
    }

    @Test
    public void hasPermissionRejectsObjectThatIsNotAContext() {
        Authentication authentication = authenticated();
        assertThrows(IllegalArgumentException.class,
            () -> permissionEvaluator.hasPermission(authentication, new Object(), PermissionType.CONTRIBUTOR.name()));
    }

    private Authentication authenticated() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        return authentication;
    }
}
