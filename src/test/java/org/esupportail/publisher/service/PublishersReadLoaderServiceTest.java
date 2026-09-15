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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.esupportail.publisher.domain.Reader;
import org.esupportail.publisher.repository.ReaderRepository;
import org.esupportail.publisher.web.rest.dto.PublisherDTO;
import org.esupportail.publisher.web.rest.dto.SubscriberDTO;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.esupportail.publisher.web.rest.vo.PublisherForRead;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublishersReadLoaderServiceTest {

    private static final long READER_ID = 42L;

    @Spy
    @InjectMocks
    private PublishersReadLoaderService publishersReadLoaderService;

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private ViewService viewService;

    @Test
    void loadCachesPublishersForReadersWithAnId() {
        Reader persistedReader = new Reader();
        persistedReader.setId(READER_ID);
        Reader transientReader = new Reader();
        List<PublisherForRead> cachedPublishers = Collections.singletonList(publisherForRead());
        when(readerRepository.findAll()).thenReturn(Arrays.asList(persistedReader, transientReader));
        doReturn(cachedPublishers).when(publishersReadLoaderService).getPublisherStructureTreeOfReader(READER_ID);

        publishersReadLoaderService.load();

        UserDTO user = new UserDTO("user", "User", true, false);
        when(viewService.evalSubscribing(eq(user), anyList())).thenReturn(true);
        List<PublisherDTO> result = publishersReadLoaderService.getUserPublishersToReadOfReader(user, READER_ID);

        verify(publishersReadLoaderService).getPublisherStructureTreeOfReader(READER_ID);
        assertEquals(1, result.size());
        assertSame(cachedPublishers.get(0).getPublisherDTO(), result.get(0));
    }

    @Test
    void reloadReplacesTheCachedPublishersForReader() {
        PublisherForRead initialPublisher = publisherForRead();
        PublisherForRead reloadedPublisher = publisherForRead();
        doReturn(Collections.singletonList(initialPublisher), Collections.singletonList(reloadedPublisher))
            .when(publishersReadLoaderService).getPublisherStructureTreeOfReader(READER_ID);

        publishersReadLoaderService.reloadPublishersOfReader(READER_ID);
        publishersReadLoaderService.reloadPublishersOfReader(READER_ID);

        UserDTO user = new UserDTO("user", "User", true, false);
        when(viewService.evalSubscribing(eq(user), anyList())).thenReturn(true);
        List<PublisherDTO> result = publishersReadLoaderService.getUserPublishersToReadOfReader(user, READER_ID);

        verify(publishersReadLoaderService, times(2)).getPublisherStructureTreeOfReader(READER_ID);
        assertEquals(1, result.size());
        assertSame(reloadedPublisher.getPublisherDTO(), result.get(0));
    }

    @Test
    void getUserPublishersToReadOfReaderKeepsOnlyPublishersAllowedByViewService() {
        PublisherForRead allowedPublisher = publisherForRead();
        PublisherForRead deniedPublisher = publisherForRead();
        List<SubscriberDTO> allowedSubscribers = allowedPublisher.getSubscribersDTO();
        List<SubscriberDTO> deniedSubscribers = deniedPublisher.getSubscribersDTO();
        doReturn(Arrays.asList(allowedPublisher, deniedPublisher))
            .when(publishersReadLoaderService).getPublisherStructureTreeOfReader(READER_ID);
        publishersReadLoaderService.reloadPublishersOfReader(READER_ID);
        UserDTO user = new UserDTO("user", "User", true, false);
        when(viewService.evalSubscribing(user, allowedSubscribers)).thenReturn(true);
        when(viewService.evalSubscribing(user, deniedSubscribers)).thenReturn(false);

        List<PublisherDTO> result = publishersReadLoaderService.getUserPublishersToReadOfReader(user, READER_ID);

        assertEquals(1, result.size());
        assertSame(allowedPublisher.getPublisherDTO(), result.get(0));
        verify(viewService).evalSubscribing(user, allowedSubscribers);
        verify(viewService).evalSubscribing(user, deniedSubscribers);
    }

    private PublisherForRead publisherForRead() {
        return new PublisherForRead(new PublisherDTO(), Collections.singletonList(mock(SubscriberDTO.class)));
    }
}
