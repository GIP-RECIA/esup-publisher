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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.AbstractClassification;
import org.esupportail.publisher.domain.LinkedFileItem;
import org.esupportail.publisher.domain.News;
import org.esupportail.publisher.domain.Redactor;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.ItemStatus;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.domain.enums.WritingFormat;
import org.esupportail.publisher.domain.enums.WritingMode;
import org.esupportail.publisher.repository.ClassificationRepository;
import org.esupportail.publisher.repository.ItemClassificationOrderRepository;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.LinkedFileItemRepository;
import org.esupportail.publisher.repository.RedactorRepository;
import org.esupportail.publisher.repository.ReadingIndicatorRepository;
import org.esupportail.publisher.repository.SubscriberRepository;
import org.esupportail.publisher.security.IPermissionService;
import org.esupportail.publisher.security.UserContextLoaderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.esupportail.publisher.web.rest.dto.ContentDTO;
import org.esupportail.publisher.web.rest.dto.LinkedFileItemDTO;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class ContentServiceTest {

    @Test
    public void archivePublishedContents_ArchivesExpiredPublishedItems() {
        final ContentService contentService = new ContentService();
        final ItemRepository<AbstractItem> itemRepository = mock(ItemRepository.class);
        ReflectionTestUtils.setField(contentService, "itemRepository", itemRepository);

        contentService.archivePublishedContents();

        verify(itemRepository).archiveExpiredPublished();
    }

    @Test
    public void publishScheduledContents_PublishesScheduledItems() {
        final ContentService contentService = new ContentService();
        final ItemRepository<AbstractItem> itemRepository = mock(ItemRepository.class);
        ReflectionTestUtils.setField(contentService, "itemRepository", itemRepository);

        contentService.publishScheduledContents();

        verify(itemRepository).publishScheduled();
    }

    @Test
    public void removeOldContents_DeletesEveryItemSelectedForRemoval() {
        final AbstractItem firstItem = new News();
        firstItem.setId(42L);
        final AbstractItem secondItem = new News();
        secondItem.setId(84L);
        final ContentService contentService = spy(new ContentService());
        final ItemRepository<AbstractItem> itemRepository = mock(ItemRepository.class);
        ReflectionTestUtils.setField(contentService, "itemRepository", itemRepository);
        when(itemRepository.findAll(any(Predicate.class))).thenReturn(Arrays.asList(firstItem, secondItem));
        doNothing().when(contentService).deleteContent(any(Long.class));

        contentService.removeOldContents();

        verify(itemRepository).findAll(any(Predicate.class));
        verify(contentService).deleteContent(firstItem.getId());
        verify(contentService).deleteContent(secondItem.getId());
    }

    @Test
    public void deleteContent_DeletesItemAndItsFilesAndAssociations() {
        final Long itemId = 42L;
        final String enclosure = "/files/item-enclosure.pdf";
        final String linkedFileUri = "42/linked-file.pdf";
        final AbstractItem item = new News();
        item.setId(itemId);
        item.setEnclosure(enclosure);
        final LinkedFileItem linkedFile = new LinkedFileItem(linkedFileUri, item);

        final ContentService contentService = new ContentService();
        final ItemRepository<AbstractItem> itemRepository = mock(ItemRepository.class);
        final LinkedFileItemRepository linkedFileItemRepository = mock(LinkedFileItemRepository.class);
        final SubscriberRepository subscriberRepository = mock(SubscriberRepository.class);
        final ItemClassificationOrderRepository itemClassificationOrderRepository = mock(ItemClassificationOrderRepository.class);
        final ReadingIndicatorRepository readingIndicatorRepository = mock(ReadingIndicatorRepository.class);
        final FileService fileService = mock(FileService.class);
        ReflectionTestUtils.setField(contentService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(contentService, "linkedFileItemRepository", linkedFileItemRepository);
        ReflectionTestUtils.setField(contentService, "subscriberRepository", subscriberRepository);
        ReflectionTestUtils.setField(contentService, "itemClassificationOrderRepository", itemClassificationOrderRepository);
        ReflectionTestUtils.setField(contentService, "readingIndincatorRepository", readingIndicatorRepository);
        ReflectionTestUtils.setField(contentService, "fileService", fileService);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(linkedFileItemRepository.findByAbstractItemId(itemId)).thenReturn(Collections.singletonList(linkedFile));
        when(subscriberRepository.findAll(any(Predicate.class))).thenReturn(Collections.emptyList());
        when(itemClassificationOrderRepository.findAll(any(Predicate.class))).thenReturn(Collections.emptyList());
        when(readingIndicatorRepository.findAll(any(Predicate.class))).thenReturn(Collections.emptyList());

        contentService.deleteContent(itemId);

        verify(fileService).deleteInternalResource(enclosure);
        verify(linkedFileItemRepository).deleteAll(Collections.singletonList(linkedFile));
        verify(fileService).deletePrivateResource(linkedFileUri);
        verify(itemRepository).deleteById(itemId);
    }

    @Test
    public void updateLinkedFilesToItem_TreatsNullAsEmptyCollection() {
        final Long itemId = 42L;
        final AbstractItem item = new News();
        item.setId(itemId);
        item.setStatus(ItemStatus.DRAFT);
        final LinkedFileItem linkedFile = new LinkedFileItem("42/linked-file.pdf", item);

        final ContentService contentService = new ContentService();
        final LinkedFileItemRepository linkedFileItemRepository = mock(LinkedFileItemRepository.class);
        ReflectionTestUtils.setField(contentService, "linkedFileItemRepository", linkedFileItemRepository);
        when(linkedFileItemRepository.findByAbstractItemId(itemId)).thenReturn(Collections.singletonList(linkedFile));

        final Set<LinkedFileItemDTO> linkedFiles = null;
        ReflectionTestUtils.invokeMethod(contentService, "updateLinkedFilesToItem", item, linkedFiles);

        verify(linkedFileItemRepository).deleteAll(Collections.singleton(linkedFile));
    }

    @Test
    public void saveContent_ReturnsBadRequestWhenRedactorDoesNotExist() throws Exception {
        final ContentService contentService = new ContentService();
        final RedactorRepository redactorRepository = mock(RedactorRepository.class);
        final Redactor redactor = new Redactor("redactor", "Redactor", "Description", WritingFormat.HTML,
            WritingMode.STATIC, 1, false, 90);
        final News item = new News();
        item.setRedactor(redactor);
        final ContentDTO content = new ContentDTO();
        content.setItem(item);
        ReflectionTestUtils.setField(contentService, "redactorRepository", redactorRepository);
        when(redactorRepository.findById(null)).thenReturn(Optional.empty());

        assertEquals(HttpStatus.BAD_REQUEST, contentService.saveContent(content).getStatusCode());
    }

    @Test
    public void saveContent_SavesIncompleteContentAsDraftWithoutClassifications() throws Exception {
        final ContentService contentService = new ContentService();
        final RedactorRepository redactorRepository = mock(RedactorRepository.class);
        final ItemRepository<AbstractItem> itemRepository = mock(ItemRepository.class);
        final ItemClassificationOrderRepository itemClassificationOrderRepository = mock(ItemClassificationOrderRepository.class);
        final LinkedFileItemRepository linkedFileItemRepository = mock(LinkedFileItemRepository.class);
        final UserContextLoaderService userSessionTreeLoader = mock(UserContextLoaderService.class);
        final Redactor redactor = new Redactor("redactor", "Redactor", "Description", WritingFormat.HTML,
            WritingMode.STATIC, 1, false, 90);
        final News item = new News();
        item.setRedactor(redactor);
        item.setStatus(ItemStatus.PUBLISHED);
        final ContentDTO content = new ContentDTO();
        content.setItem(item);
        ReflectionTestUtils.setField(contentService, "redactorRepository", redactorRepository);
        ReflectionTestUtils.setField(contentService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(contentService, "itemClassificationOrderRepository", itemClassificationOrderRepository);
        ReflectionTestUtils.setField(contentService, "linkedFileItemRepository", linkedFileItemRepository);
        ReflectionTestUtils.setField(contentService, "userSessionTreeLoader", userSessionTreeLoader);
        when(redactorRepository.findById(null)).thenReturn(Optional.of(redactor));
        when(itemRepository.save(item)).thenAnswer(invocation -> {
            item.setId(42L);
            return item;
        });
        when(itemClassificationOrderRepository.findAll(any(Predicate.class))).thenReturn(Collections.emptyList());
        when(linkedFileItemRepository.findByAbstractItemId(42L)).thenReturn(Collections.emptyList());

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        try {
            assertEquals(HttpStatus.CREATED, contentService.saveContent(content).getStatusCode());
            assertEquals(ItemStatus.DRAFT, item.getStatus());
            verify(itemRepository).save(item);
            verify(userSessionTreeLoader).loadUserTree(authentication);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    public void saveContent_ReturnsForbiddenWhenClassificationCannotBeCreated() throws Exception {
        final ContentService contentService = new ContentService();
        final RedactorRepository redactorRepository = mock(RedactorRepository.class);
        final ClassificationRepository<AbstractClassification> classificationRepository = mock(ClassificationRepository.class);
        final Redactor redactor = new Redactor("redactor", "Redactor", "Description", WritingFormat.HTML,
            WritingMode.STATIC, 1, false, 90);
        final News item = new News();
        item.setRedactor(redactor);
        item.setStatus(ItemStatus.DRAFT);
        final ContextKey classificationKey = new ContextKey(1L, ContextType.CATEGORY);
        final ContentDTO content = new ContentDTO();
        content.setItem(item);
        content.setClassifications(Collections.singleton(classificationKey));
        ReflectionTestUtils.setField(contentService, "redactorRepository", redactorRepository);
        ReflectionTestUtils.setField(contentService, "classificationRepository", classificationRepository);
        when(redactorRepository.findById(null)).thenReturn(Optional.of(redactor));
        when(classificationRepository.findById(1L)).thenReturn(Optional.empty());

        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("alice");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        try {
            assertEquals(HttpStatus.FORBIDDEN, contentService.saveContent(content).getStatusCode());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    public void setValidationItem_ReturnsForbiddenWhenUserHasNoPermission() {
        final ContentService contentService = new ContentService();
        final IPermissionService permissionService = mock(IPermissionService.class);
        final News item = new News();
        item.setId(42L);
        ReflectionTestUtils.setField(contentService, "permissionService", permissionService);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(permissionService.getRoleOfUserInContext(authentication, item.getContextKey())).thenReturn(null);

        try {
            assertEquals(HttpStatus.FORBIDDEN, contentService.setValidationItem(true, item).getStatusCode());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    public void setValidationItem_UnvalidatesItemForAuthorizedUser() {
        final ContentService contentService = new ContentService();
        final IPermissionService permissionService = mock(IPermissionService.class);
        final ItemRepository<AbstractItem> itemRepository = mock(ItemRepository.class);
        final News item = new News();
        item.setId(42L);
        item.setStatus(ItemStatus.PUBLISHED);
        ReflectionTestUtils.setField(contentService, "permissionService", permissionService);
        ReflectionTestUtils.setField(contentService, "itemRepository", itemRepository);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(permissionService.getRoleOfUserInContext(authentication, item.getContextKey())).thenReturn(PermissionType.MANAGER);

        try {
            assertEquals(HttpStatus.OK, contentService.setValidationItem(false, item).getStatusCode());
            assertEquals(ItemStatus.PENDING, item.getStatus());
            verify(itemRepository).save(item);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    public void setEnclosureItem_UpdatesEnclosureForAuthorizedUser() {
        final ContentService contentService = new ContentService();
        final IPermissionService permissionService = mock(IPermissionService.class);
        final ItemRepository<AbstractItem> itemRepository = mock(ItemRepository.class);
        final News item = new News();
        item.setId(42L);
        item.setStatus(ItemStatus.DRAFT);
        ReflectionTestUtils.setField(contentService, "permissionService", permissionService);
        ReflectionTestUtils.setField(contentService, "itemRepository", itemRepository);
        Authentication authentication = mock(Authentication.class);
        when(permissionService.canEditCtx(authentication, item.getContextKey())).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            assertEquals(HttpStatus.OK, contentService.setEnclosureItem("/files/news.pdf", item).getStatusCode());
            assertEquals("/files/news.pdf", item.getEnclosure());
            verify(itemRepository).save(item);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    public void removeLinkedFileToItem_DeletesExistingLink() {
        final ContentService contentService = new ContentService();
        final LinkedFileItemRepository linkedFileItemRepository = mock(LinkedFileItemRepository.class);
        final News item = new News();
        item.setId(42L);
        item.setStatus(ItemStatus.DRAFT);
        final LinkedFileItem linkedFile = new LinkedFileItem("42/news.pdf", item);
        ReflectionTestUtils.setField(contentService, "linkedFileItemRepository", linkedFileItemRepository);
        when(linkedFileItemRepository.findByAbstractItemId(42L)).thenReturn(Collections.singletonList(linkedFile));

        assertEquals(HttpStatus.OK, contentService.removeLinkedFileToItem(item, "42/news.pdf").getStatusCode());
        verify(linkedFileItemRepository).delete(linkedFile);
    }
}
