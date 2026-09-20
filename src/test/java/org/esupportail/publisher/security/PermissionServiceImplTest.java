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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.service.ContextService;
import org.esupportail.publisher.service.bean.UserContextTree;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.querydsl.core.types.Predicate;
import com.mysema.commons.lang.Pair;

@ExtendWith(MockitoExtension.class)
class PermissionServiceImplTest {

    @Mock
    private UserContextTree userSessionTree;

    @Mock
    private UserContextLoaderService userSessionTreeLoader;

    @Mock
    private ContextService contextService;

    private PermissionServiceImpl permissionService;
    private UserDTO user;

    @BeforeEach
    void setUp() {
        permissionService = new PermissionServiceImpl();
        permissionService.userSessionTree = userSessionTree;
        permissionService.userSessionTreeLoader = userSessionTreeLoader;
        permissionService.contextService = contextService;
        user = new UserDTO("alice", "Alice", true, false);
    }

    @Test
    void administratorHasEveryContextPermission() {
        Authentication authentication = authenticationWith(AuthoritiesConstants.ADMIN);

        assertEquals(PermissionType.ADMIN, permissionService.getRoleOfUserInContext(authentication,
            new ContextKey(1L, ContextType.ITEM)));
        assertTrue(permissionService.canCreateInCtx(authentication, new ContextKey(1L, ContextType.ORGANIZATION)));
        assertTrue(permissionService.canEditCtx(authentication, new ContextKey(1L, ContextType.ITEM)));
        assertTrue(permissionService.canDeleteCtx(authentication, new ContextKey(1L, ContextType.PUBLISHER)));
        verifyNoInteractions(userSessionTree, userSessionTreeLoader);
    }

    @Test
    void userWithoutUserRoleHasNoPermission() {
        Authentication authentication = authenticationWith();
        ContextKey contextKey = new ContextKey(1L, ContextType.ITEM);

        assertFalse(permissionService.canCreateInCtx(authentication, contextKey));
        assertFalse(permissionService.canEditCtx(authentication, contextKey));
        assertFalse(permissionService.canDeleteCtx(authentication, contextKey));
        verifyNoInteractions(userSessionTree, userSessionTreeLoader);
    }

    @Test
    void userTreeIsLoadedBeforeResolvingRole() {
        Authentication authentication = authenticationWith(AuthoritiesConstants.USER);
        ContextKey contextKey = new ContextKey(1L, ContextType.CATEGORY);
        when(userSessionTree.isTreeLoaded()).thenReturn(false);
        when(userSessionTree.getRoleFromContextTree(contextKey)).thenReturn(PermissionType.MANAGER);

        assertEquals(PermissionType.MANAGER, permissionService.getRoleOfUserInContext(authentication, contextKey));

        verify(userSessionTreeLoader).loadUserTree(user, ((CustomUserDetails) authentication.getPrincipal()).getAuthorities());
        verify(userSessionTree).getRoleFromContextTree(contextKey);
    }

    @Test
    void creationUsesContributorThresholdForFeed() {
        Authentication authentication = authenticationWith(AuthoritiesConstants.USER);
        ContextKey contextKey = new ContextKey(1L, ContextType.FEED);
        when(userSessionTree.isTreeLoaded()).thenReturn(true);
        when(userSessionTree.getRoleFromContextTree(contextKey)).thenReturn(PermissionType.CONTRIBUTOR);

        assertTrue(permissionService.canCreateInCtx(authentication, contextKey));
    }

    @Test
    void categoryWithoutItemsRequiresManagerToCreate() {
        Authentication authentication = authenticationWith(AuthoritiesConstants.USER);
        ContextKey contextKey = new ContextKey(1L, ContextType.CATEGORY);
        when(userSessionTree.isTreeLoaded()).thenReturn(true);
        when(userSessionTree.getRoleFromContextTree(contextKey)).thenReturn(PermissionType.CONTRIBUTOR);
        when(userSessionTree.contextContainsItems(contextKey)).thenReturn(false);

        assertFalse(permissionService.canCreateInCtx(authentication, contextKey));
    }

    @Test
    void itemOwnerCanEditAndDeleteWithContributorPermission() {
        Authentication authentication = authenticationWith(AuthoritiesConstants.USER);
        ContextKey contextKey = new ContextKey(1L, ContextType.ITEM);
        when(userSessionTree.isTreeLoaded()).thenReturn(true);
        when(userSessionTree.getRoleFromContextTree(contextKey)).thenReturn(PermissionType.CONTRIBUTOR);
        when(userSessionTree.isItemOwner(1L, user.getModelId())).thenReturn(true);

        assertTrue(permissionService.canEditCtx(authentication, contextKey));
        assertTrue(permissionService.canDeleteCtx(authentication, contextKey));
    }

    @Test
    void organizationAndPublisherCannotBeDeletedByRegularUser() {
        Authentication authentication = authenticationWith(AuthoritiesConstants.USER);
        when(userSessionTree.isTreeLoaded()).thenReturn(true);

        assertFalse(permissionService.canDeleteCtx(authentication, new ContextKey(1L, ContextType.ORGANIZATION)));
        assertFalse(permissionService.canDeleteCtx(authentication, new ContextKey(1L, ContextType.PUBLISHER)));
    }

    @Test
    void userWithoutRoleCannotFilterContextType() {
        Authentication authentication = authenticationWith();
        Predicate predicate = mock(Predicate.class);

        assertThrows(AccessDeniedException.class, () -> permissionService.filterAuthorizedAllOfContextType(
            authentication, ContextType.ORGANIZATION, PermissionType.EDITOR, predicate));
    }

    @Test
    void administratorKeepsOriginalFilterPredicate() {
        Authentication authentication = authenticationWith(AuthoritiesConstants.ADMIN);
        Predicate predicate = mock(Predicate.class);

        assertSame(predicate, permissionService.filterAuthorizedAllOfContextType(
            authentication, ContextType.ITEM, PermissionType.EDITOR, predicate));
    }

    @Test
    void publisherPermissionManagementRequiresLinkedPublisherAuthorization() {
        Authentication authentication = authenticationWith(AuthoritiesConstants.USER);
        ContextKey contextKey = new ContextKey(1L, ContextType.CATEGORY);
        when(contextService.isLinkedPublisherHasSubPermManagement(contextKey)).thenReturn(false);

        assertFalse(permissionService.canEditCtxPerms(authentication, contextKey));
    }

    @Test
    void regularUserCannotManageOrganizationPermissions() {
        Authentication authentication = authenticationWith(AuthoritiesConstants.USER);
        ContextKey contextKey = new ContextKey(1L, ContextType.ORGANIZATION);

        assertFalse(permissionService.canEditCtxPerms(authentication, contextKey));
    }

    @Test
    void managerCanManagePublisherPermissionsWhenLinkedPublisherAllowsIt() {
        Authentication authentication = authenticationWith(AuthoritiesConstants.USER);
        ContextKey contextKey = new ContextKey(1L, ContextType.CATEGORY);
        when(contextService.isLinkedPublisherHasSubPermManagement(contextKey)).thenReturn(true);
        when(userSessionTree.isTreeLoaded()).thenReturn(true);
        when(userSessionTree.getRoleFromContextTree(contextKey)).thenReturn(PermissionType.MANAGER);

        assertTrue(permissionService.canEditCtxPerms(authentication, contextKey));
    }

    @Test
    void itemHasNoAuthorizedChildren() {
        Authentication authentication = authenticationWith(AuthoritiesConstants.ADMIN);

        assertFalse(permissionService.hasAuthorizedChilds(authentication, new ContextKey(1L, ContextType.ITEM)));
    }

    @Test
    void userHasAuthorizedChildrenWhenTreeReportsLookoverAccess() {
        Authentication authentication = authenticationWith(AuthoritiesConstants.USER);
        ContextKey contextKey = new ContextKey(1L, ContextType.CATEGORY);
        when(userSessionTree.isTreeLoaded()).thenReturn(true);
        when(userSessionTree.hasChildsOnContext(contextKey, PermissionType.LOOKOVER)).thenReturn(true);

        assertTrue(permissionService.hasAuthorizedChilds(authentication, contextKey));
    }

    @Test
    void userCanModerateWhenUpperPermissionExceedsEditor() {
        Authentication authentication = authenticationWith(AuthoritiesConstants.USER);
        when(userSessionTree.isTreeLoaded()).thenReturn(true);
        when(userSessionTree.getUpperPerm()).thenReturn(PermissionType.MANAGER);

        assertTrue(permissionService.canModerateSomething(authentication));
    }

    @Test
    void userCanHighlightOnlyAboveContributor() {
        Authentication authentication = authenticationWith(AuthoritiesConstants.USER);
        ContextKey contextKey = new ContextKey(1L, ContextType.CATEGORY);
        when(userSessionTree.isTreeLoaded()).thenReturn(true);
        when(userSessionTree.getRoleFromContextTree(contextKey)).thenReturn(PermissionType.EDITOR);

        assertTrue(permissionService.canHighlightInCtx(authentication, contextKey));
    }

    @Test
    void userFilterChildContextsByAuthorizedType() {
        Authentication authentication = authenticationWith(AuthoritiesConstants.USER);
        ContextKey contextKey = new ContextKey(1L, ContextType.PUBLISHER);
        Predicate predicate = mock(Predicate.class);
        when(userSessionTree.isTreeLoaded()).thenReturn(true);
        when(userSessionTree.getChildsOfContext(contextKey, PermissionType.EDITOR))
            .thenReturn(new Pair<>(ContextType.CATEGORY, Collections.singleton(2L)));

        Predicate filtered = permissionService.filterAuthorizedChildsOfContext(
            authentication, contextKey, PermissionType.EDITOR, predicate);

        assertNotNull(filtered);
    }

    private Authentication authenticationWith(String... authorities) {
        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = new CustomUserDetails(user, new org.esupportail.publisher.domain.User("alice", "Alice"),
            Arrays.stream(authorities).map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        when(authentication.getPrincipal()).thenReturn(userDetails);
        return authentication;
    }
}
