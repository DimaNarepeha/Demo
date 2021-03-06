package com.softserve.demo.service.impl;

import com.softserve.demo.dto.CustomerDTO;
import com.softserve.demo.dto.ProviderDTO;
import com.softserve.demo.exceptions.AlreadyExistException;
import com.softserve.demo.exceptions.VerificationFailedException;
import com.softserve.demo.model.*;
import com.softserve.demo.repository.CustomerRepository;
import com.softserve.demo.repository.LocationRepository;
import com.softserve.demo.repository.ProviderRepository;
import com.softserve.demo.repository.UserRepository;
import com.softserve.demo.service.EmailService;
import com.softserve.demo.service.NotificationService;
import com.softserve.demo.service.PortfolioService;
import com.softserve.demo.service.RegisterService;
import com.softserve.demo.util.Constant;
import com.softserve.demo.util.SignatureGenerator;
import com.softserve.demo.util.mappers.CustomerMapper;
import com.softserve.demo.util.mappers.ProviderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class RegisterServiceImpl implements RegisterService {

    private static final String WELCOME = "Welcome!";
    private static final String SIGNING_UP = "Thank you for signing up!";
    private static final String HAVE_VERIFIED_YOUR_EMAIL = "You have verified your email!!";
    private static final String VERIFYING_YOUR_EMAIL = "Thank you for verifying your email!";
    private static final String FAILED_TO_VERIFY_MESSAGE = "Failed to verify! Your activation code is already used!";
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final ProviderRepository providerRepository;
    private final CustomerMapper customerMapper;
    private final ProviderMapper providerMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PortfolioService portfolioService;
    private final LocationRepository locationRepository;
    private final NotificationService notificationService;

    private static final String USERNAME_EXISTS = "This username already exist";
    private static final String EMAIL_EXISTS = "This email already used";

    public RegisterServiceImpl(
            final PasswordEncoder passwordEncoder,
            final CustomerMapper customerMapper,
            final ProviderMapper providerMapper,
            final CustomerRepository customerRepository,
            final UserRepository userRepository,
            final ProviderRepository providerRepository,
            final EmailService emailService,
            final PortfolioService portfolioService,
            final LocationRepository locationRepository,
            final NotificationService notificationService) {
        this.passwordEncoder = passwordEncoder;
        this.providerMapper = providerMapper;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.providerRepository = providerRepository;
        this.customerMapper = customerMapper;
        this.emailService = emailService;
        this.portfolioService = portfolioService;
        this.locationRepository = locationRepository;
        this.notificationService = notificationService;
    }

    /**
     * Sends default welcome notification for user
     * that have signed up.
     *
     * @param userId Id of user to be notified
     */
    private void sendWelcomeUserNotification(final Integer userId) {
        notifyUser(WELCOME, SIGNING_UP, userId);
    }

    private void notifyUser(String header, String message, Integer userId) {
        Notification newNotification = new Notification();
        newNotification.setHeader(header);
        newNotification.setMessage(message);
        notificationService.notifyByUserId(userId, newNotification);
    }

    @Override
    @Transactional
    public CustomerDTO createCustomer(final CustomerDTO customerDTO) {
        User user = userRepository.save(createUser(customerMapper.customerDTOToUser(customerDTO), Role.CUSTOMER));
        log.info(user.getPassword());
        sendVerificationCode(user);

        Customer customer = customerMapper.customerDTOToCustomer(customerDTO);
        customer.setUser(user);
        customer.setRating(Constant.DEFAULT_RATING);
        customer.setStatus(CustomerStatus.ACTIVE);
        sendWelcomeUserNotification(user.getId());
        return customerMapper.customerToCustomerDTO(customerRepository.save(customer));
    }

    @Override
    @Transactional
    public ProviderDTO createProvider(final ProviderDTO providerDTO) {
        User user = userRepository.save(createUser(providerMapper.providerDTOToUser(providerDTO), Role.PROVIDER));
        sendVerificationCode(user);
        Provider provider = providerMapper.providerDTOToProvider(providerDTO);
        provider.setLocation(locationRepository.findById(Constant.DEFAULT_LOCATION).get());
        provider.setUser(user);
        provider.setRaiting(Constant.DEFAULT_RATING);
        provider.setStatus(ProviderStatus.NOTAPPROVED);
        provider = providerRepository.save(provider);
        portfolioService.createEmptyPortfolio(provider);
        sendWelcomeUserNotification(user.getId());
        return providerMapper.providerToProviderDTO(provider);
    }

    private User createUser(User user, Role role) {
        validateForEmailAndUsername(user.getEmail(), user.getUsername());
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        user.setImage(Constant.DEFAULT_PHOTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setSignature(SignatureGenerator.getSignature(user));
        return user;
    }

    private void validateForEmailAndUsername(String email, String username) {
        if (userRepository.existsByUsername(username)) {
            throw new AlreadyExistException(USERNAME_EXISTS);
        }
        if (userRepository.existsByEmail(email)) {
            throw new AlreadyExistException(EMAIL_EXISTS);
        }
    }

    /**
     * This method verify user by verification code
     * if user with this code is present in database.
     *
     * @param activationCode code by which user will be searched
     * @return true if user is present in database
     */
    @Override
    @Transactional
    public boolean verifyUser(final String activationCode) {
        User user = userRepository.findByActivationCode(activationCode)
                .orElseThrow(() -> new VerificationFailedException(FAILED_TO_VERIFY_MESSAGE));
        user.setActivated(true);
        user.setActivationCode(null);
        notifyUser(HAVE_VERIFIED_YOUR_EMAIL, VERIFYING_YOUR_EMAIL, user.getId());
        return true;
    }


    /**
     * Sends verification link to the provided user.
     *
     * @param user to whom notification must be sent
     * @return user with set activation code
     */
    @Override
    @Transactional
    public User sendVerificationCode(final User user) {
        user.setActivationCode(UUID.randomUUID().toString());
        emailService.sendVerificationEmailTo(user);
        return user;
    }
}
