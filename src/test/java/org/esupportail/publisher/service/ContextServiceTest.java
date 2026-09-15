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
package org.esupportail.publisher.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.esupportail.publisher.domain.Category;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.OrganizationReaderRedactorKey;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.Reader;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.DisplayOrderType;
import org.esupportail.publisher.domain.enums.ItemType;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.repository.CategoryRepository;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.service.factories.TreeJSDTOFactory;
import org.esupportail.publisher.web.rest.dto.TreeJS;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class ContextServiceTest {

    @InjectMocks
    private ContextService contextService;
    @Mock
    private IPermissionService permissionService;
    @Mock
    private TreeJSDTOFactory treeJSDTOFactory;
    @Mock
    private PublisherRepository publisherRepository;
    @Mock
    private CategoryRepository categoryRepository;

    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void getTreeChildsReturnsAuthorizedPublishers() {
        ContextKey contextKey = new ContextKey(1L, ContextType.ORGANIZATION);
        Predicate authorized = mock(Predicate.class);
        Publisher publisher = mock(Publisher.class);
        List<TreeJS> expected = List.of();
        when(permissionService.filterAuthorizedChildsOfContext(eq(authentication), eq(contextKey), eq(PermissionType.LOOKOVER), any(Predicate.class))).thenReturn(authorized);
        when(publisherRepository.findAll(any(Predicate.class), any(OrderSpecifier[].class))).thenReturn(List.of(publisher));
        when(treeJSDTOFactory.asDTOList(anyList(), eq(PermissionType.LOOKOVER))).thenReturn(expected);

        assertThat(contextService.getTreeChilds(contextKey, PermissionType.LOOKOVER, true), equalTo(expected));
        verify(treeJSDTOFactory).asDTOList(argThat(children -> children.equals(List.of(publisher))), eq(PermissionType.LOOKOVER));
    }

    @Test
    public void getTreeChildsRemovesFlashCategoriesForNonAdmin() {
        ContextKey contextKey = new ContextKey(2L, ContextType.PUBLISHER);
        Publisher publisher = mock(Publisher.class);
        Category category = mock(Category.class);
        Reader reader = mock(Reader.class);
        when(publisherRepository.findById(2L)).thenReturn(Optional.of(publisher));
        when(publisher.getDefaultDisplayOrder()).thenReturn(DisplayOrderType.NAME);
        when(permissionService.filterAuthorizedChildsOfContext(eq(authentication), eq(contextKey), eq(PermissionType.LOOKOVER), any(Predicate.class))).thenReturn(mock(Predicate.class));
        when(permissionService.getRoleOfUserInContext(authentication, contextKey)).thenReturn(PermissionType.EDITOR);
        when(categoryRepository.findAll(any(Predicate.class), any(OrderSpecifier[].class))).thenReturn(List.of(category));
        when(category.getPublisher()).thenReturn(publisher);
        when(publisher.getContext()).thenReturn(new OrganizationReaderRedactorKey(null, reader, null));
        when(reader.getAuthorizedTypes()).thenReturn(Set.of(ItemType.FLASH));
        when(treeJSDTOFactory.asDTOList(anyList(), eq(PermissionType.LOOKOVER))).thenReturn(List.of());

        contextService.getTreeChilds(contextKey, PermissionType.LOOKOVER, true);

        verify(treeJSDTOFactory).asDTOList(argThat(List::isEmpty), eq(PermissionType.LOOKOVER));
    }

    @Test
    public void getTreeChildsKeepsFlashCategoriesForAdmin() {
        ContextKey contextKey = new ContextKey(2L, ContextType.PUBLISHER);
        Publisher publisher = mock(Publisher.class);
        Category category = mock(Category.class);
        when(publisherRepository.findById(2L)).thenReturn(Optional.of(publisher));
        when(publisher.getDefaultDisplayOrder()).thenReturn(DisplayOrderType.NAME);
        when(permissionService.filterAuthorizedChildsOfContext(eq(authentication), eq(contextKey), eq(PermissionType.LOOKOVER), any(Predicate.class))).thenReturn(mock(Predicate.class));
        when(permissionService.getRoleOfUserInContext(authentication, contextKey)).thenReturn(PermissionType.ADMIN);
        when(categoryRepository.findAll(any(Predicate.class), any(OrderSpecifier[].class))).thenReturn(List.of(category));
        when(treeJSDTOFactory.asDTOList(anyList(), eq(PermissionType.LOOKOVER))).thenReturn(List.of());

        contextService.getTreeChilds(contextKey, PermissionType.LOOKOVER, true);

        verify(treeJSDTOFactory).asDTOList(argThat(children -> children.equals(List.of(category))), eq(PermissionType.LOOKOVER));
    }
}
