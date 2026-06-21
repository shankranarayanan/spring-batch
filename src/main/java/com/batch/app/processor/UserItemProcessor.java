package com.batch.app.processor;

import com.batch.app.dto.UserDTO;
import com.batch.app.entity.User;
import com.batch.app.service.UserService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Spring Batch item processor that converts {@link UserDTO} records into persisted {@link User} entities.
 */
@Component
public class UserItemProcessor implements ItemProcessor<UserDTO, User> {

    private final UserService userService;

    /**
     * Creates the processor with the user persistence service.
     *
     * @param userService service responsible for creating and saving users
     */
    public UserItemProcessor(UserService userService) {
        this.userService = userService;
    }

    /**
     * Transforms a parsed DTO into a database-ready user entity.
     *
     * @param userDTO the input record read from a batch file
     * @return the persisted user entity
     * @throws Exception if user creation or persistence fails
     */
    @Override
    public User process(UserDTO userDTO) throws Exception {
        return userService.createUser(userDTO);
    }
}
