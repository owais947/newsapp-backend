package com.navi.assignment2.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.navi.assignment2.Exception.DatabaseErrorException;
import com.navi.assignment2.Exception.EmailAlreadyExistsException;
import com.navi.assignment2.Exception.InvalidEmailErrorException;
import com.navi.assignment2.Exception.UserNotFoundException;
import com.navi.assignment2.contract.request.UserRequest;
import com.navi.assignment2.contract.response.ValidSourceResponse;
import com.navi.assignment2.entity.User;
import com.navi.assignment2.repository.EndpointRepository;
import com.navi.assignment2.repository.UserRepository;
import com.navi.assignment2.service.SourceService;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {UserServiceImpl.class})
@ExtendWith(SpringExtension.class)
class UserServiceImplTest {
    @MockBean
    private EndpointRepository endpointRepository;

    @MockBean
    private SourceService sourceService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Test
    void shouldBeAbleToSaveUser() {
        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setEndpoints(new ArrayList<>());
        user.setId(1L);
        user.setPreferredSources("Preferred Sources");
        user.setSelectedCategory("Selected Category");
        user.setSelectedCountry("GB");
        user.setSubscribed(true);

        User user2 = new User();
        user2.setEmail("jane.doe@example.org");
        user2.setEndpoints(new ArrayList<>());
        user2.setId(1L);
        user2.setPreferredSources("Preferred Sources");
        user2.setSelectedCategory("Selected Category");
        user2.setSelectedCountry("GB");
        user2.setSubscribed(true);
        Optional<User> ofResult = Optional.of(user2);
        when(userRepository.save(Mockito.<User>any())).thenReturn(user);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        UserRequest userRequest = new UserRequest();
        userRequest.setPreferredSources(new ArrayList<>());
        assertSame(user2, userServiceImpl.save(userRequest));
        verify(userRepository).save(Mockito.<User>any());
        verify(userRepository).findById(Mockito.<Long>any());
    }

    @Test
    void shouldThrowDataBaseErrorForSaveUser() {
        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setEndpoints(new ArrayList<>());
        user.setId(1L);
        user.setPreferredSources("Preferred Sources");
        user.setSelectedCategory("Selected Category");
        user.setSelectedCountry("GB");
        user.setSubscribed(true);
        when(userRepository.save(Mockito.<User>any())).thenReturn(user);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(Optional.empty());

        UserRequest userRequest = new UserRequest();
        userRequest.setPreferredSources(new ArrayList<>());
        assertThrows(DatabaseErrorException.class, () -> userServiceImpl.save(userRequest));
        verify(userRepository).save(Mockito.<User>any());
        verify(userRepository).findById(Mockito.<Long>any());
    }


    @Test
    void shouldThrowInvalidEmailErrorForSaveUser() {
        when(userRepository.save(Mockito.<User>any())).thenThrow(new InvalidEmailErrorException("An error occurred"));

        UserRequest userRequest = new UserRequest();
        userRequest.setPreferredSources(new ArrayList<>());
        assertThrows(InvalidEmailErrorException.class, () -> userServiceImpl.save(userRequest));
        verify(userRepository).save(Mockito.<User>any());
    }

    @Test
    void shouldBeAbleToFetchUser() {
        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setEndpoints(new ArrayList<>());
        user.setId(1L);
        user.setPreferredSources("Preferred Sources");
        user.setSelectedCategory("Selected Category");
        user.setSelectedCountry("GB");
        user.setSubscribed(true);
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        assertSame(user, userServiceImpl.fetchUser(1L));
        verify(userRepository).findById(Mockito.<Long>any());
    }

    @Test
    void shouldThrowUserNotFoundErrorForFetchUser() {
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userServiceImpl.fetchUser(1L));
        verify(userRepository).findById(Mockito.<Long>any());
    }

    @Test
    void ShouldThrowDatabaseErrorForFetchUser() {
        when(userRepository.findById(Mockito.<Long>any())).thenThrow(new DatabaseErrorException("An error occurred"));
        assertThrows(DatabaseErrorException.class, () -> userServiceImpl.fetchUser(1L));
        verify(userRepository).findById(Mockito.<Long>any());
    }

    @Test
    void shouldThrowDatabaseErrorForValidateSources() throws Exception {
        ValidSourceResponse validSourceResponse = mock(ValidSourceResponse.class);
        when(validSourceResponse.getSources()).thenThrow(new DatabaseErrorException("An error occurred"));
        Optional<ValidSourceResponse> ofResult = Optional.of(validSourceResponse);
        when(sourceService.getValidSources(Mockito.<String>any(), Mockito.<String>any())).thenReturn(ofResult);
        assertThrows(DatabaseErrorException.class, () -> userServiceImpl.validateSources(new UserRequest()));
        verify(sourceService).getValidSources(Mockito.<String>any(), Mockito.<String>any());
        verify(validSourceResponse).getSources();
    }


    @Test
    void shouldThrowIllegalArgumentErrorForValidateUser() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> userServiceImpl.validateUser(new UserRequest()));
        assertThrows(IllegalArgumentException.class,
                () -> userServiceImpl.validateUser(new UserRequest("jane.doe@example.org", null, "GB", new ArrayList<>())));
        assertThrows(IllegalArgumentException.class, () -> userServiceImpl
                .validateUser(new UserRequest("jane.doe@example.org", "Invalid arguments", null, new ArrayList<>())));
    }

    @Test
    void shouldThrowEmailAlreadyExistsErrorForValidateUser() throws Exception {
        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setEndpoints(new ArrayList<>());
        user.setId(1L);
        user.setPreferredSources("Preferred Sources");
        user.setSelectedCategory("Selected Category");
        user.setSelectedCountry("GB");
        user.setSubscribed(true);
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findByEmail(Mockito.<String>any())).thenReturn(ofResult);
        assertThrows(EmailAlreadyExistsException.class, () -> userServiceImpl
                .validateUser(new UserRequest("jane.doe@example.org", "Invalid arguments", "GB", new ArrayList<>())));
        verify(userRepository).findByEmail(Mockito.<String>any());
    }


    @Test
    void shouldBeAbleToValidateUser() throws Exception {
        when(userRepository.findByEmail(Mockito.<String>any())).thenReturn(Optional.empty());
        UserRequest userRequest = new UserRequest("jane.doe@example.org", "cat", "GB", new ArrayList<>());

        userServiceImpl.validateUser(userRequest);
        verify(userRepository).findByEmail(Mockito.<String>any());
        assertEquals("jane.doe@example.org", userRequest.getEmail());
        assertEquals("GB", userRequest.getSelectedCountry());
        assertEquals("cat", userRequest.getSelectedCategory());
        assertTrue(userRequest.getPreferredSources().isEmpty());
    }

    @Test
    void shouldThrowDatabaseErrorForValidateUser() throws Exception {
        when(userRepository.findByEmail(Mockito.<String>any()))
                .thenThrow(new DatabaseErrorException("An error occurred"));
        assertThrows(DatabaseErrorException.class, () -> userServiceImpl
                .validateUser(new UserRequest("jane.doe@example.org", "Invalid arguments", "GB", new ArrayList<>())));
        verify(userRepository).findByEmail(Mockito.<String>any());
    }
}

