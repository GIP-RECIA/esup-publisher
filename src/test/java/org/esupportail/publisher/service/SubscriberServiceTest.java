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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.Category;
import org.esupportail.publisher.domain.Organization;
import org.esupportail.publisher.domain.OrganizationReaderRedactorKey;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.repository.CategoryRepository;
import org.esupportail.publisher.repository.SubscriberRepository;

import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SubscriberServiceTest {

    @InjectMocks
    private SubscriberService subscriberService;
    @Mock
    private SubscriberRepository subscriberRepository;
    @Mock
    private PublisherRepository publisherRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @Test
    public void getDefinedSubscribersReturnsSubscribersOfContext() {
        Subscriber subscriber = mock(Subscriber.class);
        when(subscriberRepository.findAll(any(Predicate.class))).thenReturn(List.of(subscriber));

        assertThat(subscriberService.getDefinedSubscribersOfContext(new ContextKey(1L, ContextType.ORGANIZATION)), equalTo(List.of(subscriber)));
    }

    @Test
    public void getDefinedSubscribersReturnsSubscribersOfCategory() {
        Subscriber subscriber = mock(Subscriber.class);
        when(subscriberRepository.findAll(any(Predicate.class))).thenReturn(List.of(subscriber));

        assertThat(subscriberService.getDefinedSubscribersOfContext(new ContextKey(3L, ContextType.CATEGORY)), equalTo(List.of(subscriber)));
        verify(subscriberRepository).findAll(any(Predicate.class));
    }

    @Test
    public void getDefinedSubscribersReturnsSubscribersOfItem() {
        Subscriber subscriber = mock(Subscriber.class);
        when(subscriberRepository.findAll(any(Predicate.class))).thenReturn(List.of(subscriber));

        assertThat(subscriberService.getDefinedSubscribersOfContext(new ContextKey(4L, ContextType.ITEM)), equalTo(List.of(subscriber)));
    }

    @Test
    public void getDefaultSubscribersUsesPublisherSubscribersWhenDefined() {
        Subscriber subscriber = mock(Subscriber.class);
        when(subscriberRepository.findAll(any(Predicate.class))).thenReturn(List.of(subscriber));

        assertThat(subscriberService.getDefaultsSubscribersOfContext(new ContextKey(2L, ContextType.PUBLISHER)), equalTo(List.of(subscriber)));
        verifyNoInteractions(publisherRepository);
    }

    @Test
    public void getDefaultSubscribersFallsBackToOrganization() {
        Publisher publisher = mock(Publisher.class);
        Organization organization = mock(Organization.class);
        Subscriber subscriber = mock(Subscriber.class);
        when(subscriberRepository.findAll(any(Predicate.class))).thenReturn(List.of(), List.of(subscriber));
        when(publisherRepository.findById(2L)).thenReturn(Optional.of(publisher));
        when(publisher.getContext()).thenReturn(new OrganizationReaderRedactorKey(organization, null, null));
        when(organization.getContextKey()).thenReturn(new ContextKey(1L, ContextType.ORGANIZATION));

        assertThat(subscriberService.getDefaultsSubscribersOfContext(new ContextKey(2L, ContextType.PUBLISHER)), equalTo(List.of(subscriber)));
    }

    @Test
    public void getDefaultSubscribersForCategoryUsesPublisherSubscribers() {
        Category category = mock(Category.class);
        Publisher publisher = mock(Publisher.class);
        Subscriber subscriber = mock(Subscriber.class);
        when(categoryRepository.findById(3L)).thenReturn(Optional.of(category));
        when(category.getPublisher()).thenReturn(publisher);
        when(publisher.getContextKey()).thenReturn(new ContextKey(2L, ContextType.PUBLISHER));
        when(subscriberRepository.findAll(any(Predicate.class))).thenReturn(List.of(subscriber));

        assertThat(subscriberService.getDefaultsSubscribersOfContext(new ContextKey(3L, ContextType.CATEGORY)), equalTo(List.of(subscriber)));
    }

    @Test
    public void getDefaultSubscribersForItemIsEmpty() {
        assertThat(subscriberService.getDefaultsSubscribersOfContext(new ContextKey(4L, ContextType.ITEM)), equalTo(List.of()));
        verifyNoInteractions(subscriberRepository, publisherRepository, categoryRepository);
    }
}
