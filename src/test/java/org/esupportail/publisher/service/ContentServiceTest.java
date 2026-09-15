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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.LinkedFileItem;
import org.esupportail.publisher.domain.News;
import org.esupportail.publisher.repository.ItemClassificationOrderRepository;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.LinkedFileItemRepository;
import org.esupportail.publisher.repository.ReadingIndicatorRepository;
import org.esupportail.publisher.repository.SubscriberRepository;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class ContentServiceTest {

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
}
