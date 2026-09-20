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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import com.querydsl.core.types.Predicate;
import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.Subscriber;
import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.SubjectType;
import org.esupportail.publisher.domain.enums.SubscribeType;
import org.esupportail.publisher.domain.externals.ExternalUserHelper;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.SubscriberRepository;
import org.esupportail.publisher.security.AuthoritiesConstants;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.SecurityUtils;
import org.esupportail.publisher.service.exceptions.CustomAccessDeniedException;
import org.esupportail.publisher.service.factories.SubscriberDTOFactory;
import org.esupportail.publisher.web.rest.dto.ContextKeyDTO;
import org.esupportail.publisher.web.rest.dto.SubjectKeyExtendedDTO;
import org.esupportail.publisher.web.rest.dto.SubscriberDTO;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
class ViewServiceTest {

    @InjectMocks
    private ViewService viewService;

    @Mock
    private ItemRepository<AbstractItem> itemRepository;

    @Mock
    private SubscriberRepository subscriberRepository;

    @Mock
    private ExternalUserHelper externalUserHelper;

    @Mock
    private SubscriberDTOFactory subscriberDTOFactory;

    @Test
    void itemViewRejectsMissingOrNonPublishedItems() {
        when(itemRepository.findOne(any(Predicate.class))).thenReturn(Optional.empty());

        assertThrows(CustomAccessDeniedException.class, () -> viewService.itemView(1L, mock(HttpServletRequest.class)));
    }

    @Test
    void isSubscriberAllowsPublicRssAndContextsWithoutSubscribers() {
        AbstractItem publicItem = mock(AbstractItem.class);
        when(publicItem.isRssAllowed()).thenReturn(true);

        assertTrue(viewService.isSubscriber(publicItem));
        verifyNoInteractions(subscriberRepository);

        AbstractItem unrestrictedItem = mock(AbstractItem.class);
        when(unrestrictedItem.isRssAllowed()).thenReturn(false);
        when(unrestrictedItem.getContextKey()).thenReturn(new ContextKey(1L, ContextType.PUBLISHER));
        when(subscriberRepository.findAll(any(Predicate.class))).thenReturn(List.of());

        assertTrue(viewService.isSubscriber(unrestrictedItem));
    }

    @Test
    void isSubscriberAcceptsAuthenticatedGroupSubscriber() {
        AbstractItem item = mock(AbstractItem.class);
        Subscriber subscriber = mock(Subscriber.class);
        UserDTO user = new UserDTO("alice", "Alice", "alice@example.test",
            Map.of("memberOf", List.of("staff-members")));
        CustomUserDetails userDetails = new CustomUserDetails(user, new User("alice", "Alice"), List.of());
        SubscriberDTO groupSubscriber = subscriber(SubjectType.GROUP, "staff");

        when(item.isRssAllowed()).thenReturn(false);
        when(item.getContextKey()).thenReturn(new ContextKey(1L, ContextType.PUBLISHER));
        when(item.getCreatedBy()).thenReturn(new User("creator", "Creator"));
        when(item.getLastModifiedBy()).thenReturn(new User("modifier", "Modifier"));
        when(subscriberRepository.findAll(any(Predicate.class))).thenReturn(List.of(subscriber));
        when(subscriberDTOFactory.asDTOList(List.of(subscriber))).thenReturn(List.of(groupSubscriber));
        when(externalUserHelper.getUserGroupAttribute()).thenReturn("memberOf");

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserDetails).thenReturn(userDetails);

            assertTrue(viewService.isSubscriber(item));
        }
    }

    @Test
    void isSubscriberRejectsAnonymousUserWhenSubscribersExist() {
        AbstractItem item = restrictedItem();
        when(subscriberRepository.findAll(any(Predicate.class))).thenReturn(List.of(mock(Subscriber.class)));

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserDetails).thenReturn(null);

            assertThrows(AccessDeniedException.class, () -> viewService.isSubscriber(item));
        }
    }

    @Test
    void isSubscriberAllowsAdminWhenSubscribersExist() {
        AbstractItem item = restrictedItem();
        CustomUserDetails admin = new CustomUserDetails(new UserDTO("admin", "Admin", true, false),
            new User("admin", "Admin"), List.of(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN)));
        when(subscriberRepository.findAll(any(Predicate.class))).thenReturn(List.of(mock(Subscriber.class)));

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserDetails).thenReturn(admin);

            assertTrue(viewService.isSubscriber(item));
        }
    }

    @Test
    void isSubscriberAllowsCreatorWhenSubscribersExist() {
        AbstractItem item = restrictedItem();
        CustomUserDetails creator = new CustomUserDetails(new UserDTO("creator", "Creator", true, false),
            new User("creator", "Creator"), List.of());
        when(item.getCreatedBy()).thenReturn(new User("creator", "Creator"));
        when(subscriberRepository.findAll(any(Predicate.class))).thenReturn(List.of(mock(Subscriber.class)));

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserDetails).thenReturn(creator);

            assertTrue(viewService.isSubscriber(item));
        }
    }

    @Test
    void evalSubscribingMatchesPersonSubscriberCaseInsensitively() {
        UserDTO user = new UserDTO("alice", "Alice", true, false);

        assertTrue(viewService.evalSubscribing(user, List.of(subscriber(SubjectType.PERSON, "ALICE"))));
        assertFalse(viewService.evalSubscribing(user, List.of(subscriber(SubjectType.PERSON, "bob"))));
    }

    @Test
    void evalSubscribingMatchesPersonAttributeExactly() {
        UserDTO user = new UserDTO("alice", "Alice", "alice@example.test", Map.of("department", List.of("IT")));
        when(externalUserHelper.getUserGroupAttribute()).thenReturn("memberOf");

        assertTrue(viewService.evalSubscribing(user, List.of(subscriber(SubjectType.PERSON_ATTR, "IT", "department"))));
    }

    @Test
    void evalSubscribingMatchesPersonAttributeRegex() {
        UserDTO user = new UserDTO("alice", "Alice", "alice@example.test", Map.of("department", List.of("IT-42")));
        when(externalUserHelper.getUserGroupAttribute()).thenReturn("memberOf");

        assertTrue(viewService.evalSubscribing(user,
            List.of(subscriber(SubjectType.PERSON_ATTR_REGEX, "IT-[0-9]+", "department"))));
    }

    @Test
    void evalSubscribingRejectsUnsupportedSubjectTypeWhenPresent() {
        SubjectType unsupported = Arrays.stream(SubjectType.values())
            .filter(type -> type != SubjectType.GROUP && type != SubjectType.PERSON && type != SubjectType.PERSON_ATTR
                && type != SubjectType.PERSON_ATTR_REGEX)
            .findFirst()
            .orElse(null);
        assumeTrue(unsupported != null, "All SubjectType values are managed");

        assertThrows(IllegalStateException.class,
            () -> viewService.evalSubscribing(new UserDTO("alice", "Alice", true, false), List.of(subscriber(unsupported, "value"))));
    }

    private SubscriberDTO subscriber(SubjectType type, String value) {
        return new SubscriberDTO(new SubjectKeyExtendedDTO(value, type),
            new ContextKeyDTO(1L, ContextType.PUBLISHER), SubscribeType.FREE);
    }

    private SubscriberDTO subscriber(SubjectType type, String value, String attribute) {
        SubscriberDTO subscriber = subscriber(type, value);
        subscriber.getModelId().getSubjectKey().setKeyAttribute(attribute);
        return subscriber;
    }

    private AbstractItem restrictedItem() {
        AbstractItem item = mock(AbstractItem.class);
        when(item.isRssAllowed()).thenReturn(false);
        when(item.getContextKey()).thenReturn(new ContextKey(1L, ContextType.PUBLISHER));
        return item;
    }
}
