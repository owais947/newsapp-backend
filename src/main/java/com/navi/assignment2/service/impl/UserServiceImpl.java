package com.navi.assignment2.service.impl;

import com.navi.assignment2.Exception.*;
import com.navi.assignment2.contract.request.UserRequest;
import com.navi.assignment2.entity.Endpoint;
import com.navi.assignment2.entity.Source;
import com.navi.assignment2.contract.response.ValidSourceResponse;
import com.navi.assignment2.entity.User;
import com.navi.assignment2.repository.EndpointRepository;
import com.navi.assignment2.repository.UserRepository;
import com.navi.assignment2.service.EmailService;
import com.navi.assignment2.service.SourceService;
import com.navi.assignment2.service.UserService;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Metrics;
import io.prometheus.client.Counter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EndpointRepository endpointRepository;

    @Autowired
    private SourceService sourceService;
//    @Value("${disable-user-api}")
//    private boolean disableUserApi;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(UserRequest userRequest) {
        String preferredSources = "";
        for(String source : userRequest.getPreferredSources()) {
            preferredSources += source + ",";
        }
        User user = new User(userRequest.getEmail(), userRequest.getSelectedCategory(), userRequest.getSelectedCountry(), preferredSources);
        userRepository.save(user);

        Optional<User> savedUser = userRepository.findById(user.getId());

        if(savedUser.isPresent()) {
            log.info("User saved successfully: {}", savedUser.get().toString());
            return savedUser.get();
        }
        else {
            log.error("Unable to save user for user input: {}", userRequest.toString());
            throw new DatabaseErrorException("Unable to save user");
        }
    }

    @Override
    public User fetchUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()) {
            log.info("User fetched successfully");
            return user.get();
        }
        else {
            log.warn("User not found for id: {}", id);
            throw new UserNotFoundException("User not found");
        }
    }

    @Override
    public void validateSources(UserRequest userRequest) throws Exception {
        List<String> preferredSources = userRequest.getPreferredSources();

        Optional<ValidSourceResponse> totalSources = sourceService.getValidSources(userRequest.getSelectedCountry(), userRequest.getSelectedCategory());
        log.info("Valid sources fetched: {}", totalSources.get().getSources().toString());

        List<Source> sources = totalSources.get().getSources();
        for(String source: preferredSources) {
            boolean found = false;
            for(Source s: sources) {
                if(s.getName().equals(source)) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                log.warn("Invalid sources provided: {}", preferredSources.toString());
                throw new InvalidSourcesProvidedException("Invalid sources provided");
            }
        }
    }

    @Override
    public void validateUser(UserRequest userRequest) throws Exception {
//        if(disableUserApi) {
//            log.error("User api is disabled");
//            throw new UserApiDisabledException("User api is disabled");
//        }

        if(userRequest.getEmail() == null || userRequest.getSelectedCategory() == null || userRequest.getSelectedCountry() == null) {
            throw new IllegalArgumentException("Invalid arguments");
        }

        String regexPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Optional<User> user = userRepository.findByEmail(userRequest.getEmail());
        if(user.isPresent()) {
            log.warn("Email already exists: {}", userRequest.getEmail());
            throw new EmailAlreadyExistsException("Email already exists");
        }
        if(!userRequest.getEmail().matches(regexPattern)) {
            log.warn("Invalid email provided: {}", userRequest.getEmail());
            throw new InvalidEmailErrorException("Invalid email provided");
        }
    }
}
