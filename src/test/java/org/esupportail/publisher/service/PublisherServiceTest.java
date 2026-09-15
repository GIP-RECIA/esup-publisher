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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.OrganizationReaderRedactorKey;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.repository.PublisherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PublisherServiceTest {

    private static final long ORGANIZATION_ID = 1L;
    private static final long PUBLISHER_ID = 2L;

    @InjectMocks
    private PublisherService publisherService;

    @Mock
    private PublisherRepository publisherRepository;

    @Test
    public void doMoveToLowerPositionUpdatesRangeAndSavesPublisher() {
        Publisher publisher = publisherInOrganization();
        Publisher storedPublisher = new Publisher();
        when(publisherRepository.getDisplayOrderOf(PUBLISHER_ID)).thenReturn(2);
        when(publisherRepository.findById(PUBLISHER_ID)).thenReturn(Optional.of(storedPublisher));

        publisherService.doMove(publisher, 5);

        verify(publisherRepository).setLowerDisplayOrderOfRange(ORGANIZATION_ID, 2, 5);
        verify(publisherRepository).findById(PUBLISHER_ID);
        verify(publisherRepository).save(storedPublisher);
        assertThat(storedPublisher.getDisplayOrder(), equalTo(5));
    }

    @Test
    public void doMoveToUpperPositionUpdatesRangeAndSavesPublisher() {
        Publisher publisher = publisherInOrganization();
        Publisher storedPublisher = new Publisher();
        when(publisherRepository.getDisplayOrderOf(PUBLISHER_ID)).thenReturn(5);
        when(publisherRepository.findById(PUBLISHER_ID)).thenReturn(Optional.of(storedPublisher));

        publisherService.doMove(publisher, 2);

        verify(publisherRepository).setUpperDisplayOrderOfRange(ORGANIZATION_ID, 2, 5);
        verify(publisherRepository).findById(PUBLISHER_ID);
        verify(publisherRepository).save(storedPublisher);
        assertThat(storedPublisher.getDisplayOrder(), equalTo(2));
    }

    @Test
    public void doMoveToCurrentPositionDoesNotSavePublisher() {
        Publisher publisher = publisherInOrganization();
        when(publisherRepository.getDisplayOrderOf(PUBLISHER_ID)).thenReturn(2);

        publisherService.doMove(publisher, 2);

        verify(publisherRepository, never()).setLowerDisplayOrderOfRange(ORGANIZATION_ID, 2, 2);
        verify(publisherRepository, never()).setUpperDisplayOrderOfRange(ORGANIZATION_ID, 2, 2);
        verify(publisherRepository, never()).findById(PUBLISHER_ID);
        verify(publisherRepository, never()).save(any(Publisher.class));
    }

    private Publisher publisherInOrganization() {
        Organization organization = new Organization();
        organization.setId(ORGANIZATION_ID);
        Publisher publisher = new Publisher();
        publisher.setId(PUBLISHER_ID);
        publisher.setContext(new OrganizationReaderRedactorKey(organization, null, null));
        return publisher;
    }
}
