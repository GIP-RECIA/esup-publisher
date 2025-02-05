package org.esupportail.publisher.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.esupportail.publisher.domain.AbstractItem;
import org.esupportail.publisher.domain.News;
import org.esupportail.publisher.domain.Publisher;
import org.esupportail.publisher.domain.PublisherStructureTree;
import org.esupportail.publisher.domain.ReadingIndincator;
import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.repository.ItemRepository;
import org.esupportail.publisher.repository.PublisherRepository;
import org.esupportail.publisher.repository.ReaderRepository;
import org.esupportail.publisher.repository.ReadingIndincatorRepository;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.UserDetailsService;
import org.esupportail.publisher.service.exceptions.ObjectNotFoundException;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.esupportail.publisher.web.rest.vo.Actualite;
import org.esupportail.publisher.web.rest.vo.ItemVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.data.domain.Example;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.Maps;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;


@ExtendWith(SpringExtension.class)
class NewsServiceTest {

    @Spy
    @InjectMocks
    private NewsService service;

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private TreeGenerationService treeGenerationService;

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private ReadingIndincatorRepository readingIndincatorRepository;

    @Mock
    private ItemRepository<AbstractItem> itemRepository;

    @Mock
    private HttpServletRequest request;

    @Test
    void shouldThrowException_whenUserNotFound() {

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        User user = new User("FR1", "alice");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(customUserDetails);
        // Given
        when(userDetailsService.loadUserByUsername(any())).thenReturn(null);

        // When & Then
        assertThrows(ObjectNotFoundException.class, () ->
            service.getNewsByUserOnContext(1L, null, request));
    }

    @Test
    void shouldReturnEmptyActualite_whenNoNewsFound() throws Exception {

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Given
        User user = new User("FR1", "alice");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());
        when(userDetailsService.loadUserByUsername(any())).thenReturn(customUserDetails);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(customUserDetails);

        doReturn(Collections.emptyList()).when(service).generateNewsTreeByReader(anyLong(), any());

        // When
        Actualite actualite = this.service.getNewsByUserOnContext(1L, null, request);

        // Then
        assertNotNull(actualite);
        assertTrue(actualite.getItems().isEmpty());
        assertTrue(actualite.getRubriques().isEmpty());
        assertTrue(actualite.getRubriques().isEmpty());
    }

    @Test
    void shouldGeneratePublisherStructureTreesForValidPublishers() throws Exception {
        // Arrange
        Long readerId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);

        Publisher publisher1 = new Publisher();
        Publisher publisher2 = new Publisher();
        Actualite actualite = new Actualite();

        when(publisherRepository.findAll(any(BooleanBuilder.class))).thenReturn(List.of(publisher1, publisher2));
        when(treeGenerationService.getActualiteByPublisher(any(Publisher.class), eq(request))).thenReturn(actualite);

        // Act
        List<PublisherStructureTree> result = service.generateNewsTreeByReader(readerId, request);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // Deux publishers = deux arbres
        verify(treeGenerationService, times(2)).getActualiteByPublisher(any(Publisher.class), eq(request));
        verify(publisherRepository, times(1)).findAll(any(BooleanBuilder.class));
    }

    @Test
    void shouldThrowNewsExceptionWhenNoPublishersFound() {
        // Arrange
        Long readerId = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(publisherRepository.findAll(any(BooleanBuilder.class))).thenReturn(List.of()); // Aucun publisher

        // Act & Assert
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            service.generateNewsTreeByReader(readerId, request);
        });

        assertNotNull(exception);
        verify(publisherRepository, times(1)).findAll(any(BooleanBuilder.class));
        verifyNoInteractions(treeGenerationService); // Aucun appel au service d'arbre
    }

    @Test
    void shouldReturnReadingInfos_whenUserAndIndicatorsExist() {

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


        User user = new User("FR1", "alice");
        AbstractItem item1 = new News();
        item1.setId(1L);
        item1.setTitle("Item 1");
        AbstractItem item2 = new News();
        item2.setId(2L);
        item2.setTitle("Item 2");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());

        ReadingIndincator readingIndincator1 = new ReadingIndincator();
        ReadingIndincator readingIndincator2 = new ReadingIndincator();
        ReadingIndincator readingIndincator3 = new ReadingIndincator();

        readingIndincator1.setUser(customUserDetails.getInternalUser());
        readingIndincator2.setUser(customUserDetails.getInternalUser());
        readingIndincator3.setUser(new User("FR2", "bob"));

        readingIndincator1.setItem(item1);
        readingIndincator2.setItem(item2);
        readingIndincator3.setItem(item1);

        readingIndincator1.setRead(true);
        readingIndincator2.setRead(false);
        readingIndincator3.setRead(false);

        Mockito.when(this.userDetailsService.loadUserByUsername(ArgumentMatchers.any())).thenReturn(customUserDetails);
        Mockito.when(this.readingIndincatorRepository.findAllByUserId(ArgumentMatchers.any())).thenReturn(Arrays.asList(readingIndincator1, readingIndincator2));
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);

        Map<String, Boolean> result = this.service.getAllReadindInfosForCurrentUser();

        System.out.println(result);

        assertEquals(result.size(), 2);
        assertTrue(result.containsKey("1"));
        assertTrue(result.containsKey("2"));
        assertEquals(result.get("1"), true);
        assertEquals(result.get("2"), false);

    }

    @Test
    void shouldReturnEmptyMap_whenNoIndicatorsExist() {

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);



        AbstractItem item1 = new News();
        item1.setId(1L);
        item1.setTitle("Item 1");
        AbstractItem item2 = new News();
        item2.setId(2L);
        item2.setTitle("Item 2");

        User user = new User("FR1", "alice");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());

        ReadingIndincator readingIndincator1 = new ReadingIndincator();
        ReadingIndincator readingIndincator2 = new ReadingIndincator();
        ReadingIndincator readingIndincator3 = new ReadingIndincator();

        readingIndincator1.setUser(customUserDetails.getInternalUser());
        readingIndincator2.setUser(customUserDetails.getInternalUser());
        readingIndincator3.setUser(new User("FR2", "bob"));

        readingIndincator1.setItem(item1);
        readingIndincator2.setItem(item2);
        readingIndincator3.setItem(item1);

        readingIndincator1.setRead(true);
        readingIndincator2.setRead(false);
        readingIndincator3.setRead(false);

        Mockito.when(this.userDetailsService.loadUserByUsername(ArgumentMatchers.any())).thenReturn(customUserDetails);
        Mockito.when(this.readingIndincatorRepository.findAllByUserId(ArgumentMatchers.any())).thenReturn(Arrays.asList());
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);

        Map<String, Boolean> result = this.service.getAllReadindInfosForCurrentUser();

        System.out.println(result);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    void shouldCreateReadingIndicator_whenItemIsReadForTheFirstTime() throws ObjectNotFoundException {

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User user = new User("FR1", "alice");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());

        AbstractItem item1 = new News();
        item1.setId(1L);
        item1.setTitle("Item 1");


        Mockito.when(this.userDetailsService.loadUserByUsername(ArgumentMatchers.any())).thenReturn(customUserDetails);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        Mockito.when(this.itemRepository.findOne((Predicate) any())).thenReturn(Optional.of(item1));
        Mockito.when(this.readingIndincatorRepository.existsByItemIdAndUserId(any(), any())).thenReturn(false);

        // Act
        this.service.readingManagement(item1.getId(), true);

        // Assert
        verify(readingIndincatorRepository, times(1)).save(any(ReadingIndincator.class));
    }

    @Test
    void shouldUpdateReadingIndicator_whenItemIsMarkedAsReadAgain() throws ObjectNotFoundException {

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Given
        Long itemId = 1L;
        boolean isRead = true;

        User user = new User("FR1", "alice");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());
        when(userDetailsService.loadUserByUsername(any())).thenReturn(customUserDetails);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(customUserDetails);

        AbstractItem item = mock(News.class); // Item factice
        when(itemRepository.findOne((Example<AbstractItem>) any())).thenReturn(Optional.of(item));
        when(readingIndincatorRepository.existsByItemIdAndUserId(itemId, customUserDetails.getUser().getLogin())).thenReturn(true);

        // Act
        service.readingManagement(itemId, isRead);

        // Assert
        verify(readingIndincatorRepository, times(1)).readingManagement(eq(itemId), eq(customUserDetails.getInternalUser().getLogin()), eq(true));
        verify(readingIndincatorRepository, times(1)).incrementReadingCounter(eq(itemId), eq(customUserDetails.getInternalUser().getLogin()));
    }

    @Test
    void shouldDeleteReadingIndicator_whenItemIsMarkedAsUnread() throws ObjectNotFoundException {

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Given
        Long itemId = 1L;
        boolean isRead = false;

        User user = new User("FR1", "alice");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());
        when(userDetailsService.loadUserByUsername(any())).thenReturn(customUserDetails);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(customUserDetails);

        when(readingIndincatorRepository.existsByItemIdAndUserId(itemId, customUserDetails.getUser().getLogin())).thenReturn(true);

        // Act
        service.readingManagement(itemId, isRead);

        // Assert
        verify(readingIndincatorRepository, times(1)).readingManagement(eq(itemId), eq(customUserDetails.getInternalUser().getLogin()), eq(false));
    }

    @Test
    void shouldThrowException_whenItemNotFoundForUnread() {

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Given
        Long itemId = 1L;
        boolean isRead = false;

        User user = new User("FR1", "alice");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());
        when(userDetailsService.loadUserByUsername(any())).thenReturn(customUserDetails);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(customUserDetails);

        when(readingIndincatorRepository.existsByItemIdAndUserId(itemId, customUserDetails.getUser().getLogin())).thenReturn(false);

        // Act & Assert
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> {
            service.readingManagement(itemId, isRead);
        });

        assertNotNull(exception);
    }

    @Test
    void shouldThrowException_whenItemNotFoundForRead() {

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Given
        Long itemId = 1L;
        boolean isRead = true;

        User user = new User("FR1", "alice");
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDTO("FR1", "user", true, false), user,
            Arrays.asList());
        when(userDetailsService.loadUserByUsername(any())).thenReturn(customUserDetails);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(customUserDetails);

        when(itemRepository.findOne((Example<AbstractItem>) any())).thenReturn(Optional.empty());

        // Act & Assert
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> {
            service.readingManagement(itemId, isRead);
        });

        assertNotNull(exception);
    }

}
