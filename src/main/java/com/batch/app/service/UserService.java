package com.batch.app.service;

import com.batch.app.dto.UserDTO;
import com.batch.app.entity.User;
import com.batch.app.mapper.UserIdGenerator;
import org.springframework.stereotype.Service;

/**
 * Service layer for user creation and unique identifier assignment during batch ingestion.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserIdGenerator userIdGenerator;

    /**
     * Creates the user service with repository and ID generator dependencies.
     *
     * @param userRepository   JPA repository for user persistence
     * @param userIdGenerator  generator for constructing unique user IDs
     */
    public UserService(UserRepository userRepository, UserIdGenerator userIdGenerator) {
        this.userRepository = userRepository;
        this.userIdGenerator = userIdGenerator;
    }

    /**
     * Maps a DTO to a {@link User} entity, assigns a unique user ID, and persists the record.
     *
     * @param userDTO parsed user data from a batch file
     * @return the saved user entity
     */
    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setDateOfBirth(userDTO.getDateOfBirth());
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        user.setAddress(userDTO.getAddress());
        user.setCountry(userDTO.getCountry());
        user.setCreatedAt(java.time.LocalDate.now());

        String userId = generateUniqueUserId(userDTO.getFirstName(), userDTO.getLastName());
        user.setUserId(userId);

        return userRepository.save(user);
    }

    /**
     * Generates a unique user ID, appending a numeric suffix when collisions occur.
     *
     * @param firstName user's first name used in ID construction
     * @param lastName  user's last name used in ID construction
     * @return a unique user ID not already present in the database
     */
    private String generateUniqueUserId(String firstName, String lastName) {
        String baseUserId = userIdGenerator.generateUserId(firstName, lastName, null);

        if (userRepository.findByUserId(baseUserId).isEmpty()) {
            return baseUserId;
        }

        int counter = 1;
        while (userRepository.findByUserId(baseUserId + counter).isPresent()) {
            counter++;
        }

        return baseUserId + counter;
    }
}
