package com.batch.app.mapper;

import org.springframework.stereotype.Component;

/**
 * Generates deterministic user IDs from name components and an optional numeric suffix.
 * <p>
 * ID format: first two characters of last name + first four characters of first name + optional counter.
 */
@Component
public class UserIdGenerator {

    /**
     * Builds a user ID from the given name parts and optional counter suffix.
     *
     * @param firstName user's first name
     * @param lastName  user's last name
     * @param counter   optional numeric suffix appended when resolving ID collisions; may be {@code null}
     * @return the generated user ID string
     * @throws IllegalArgumentException if either name is {@code null}
     */
    public String generateUserId(String firstName, String lastName, Integer counter) {
        if (firstName == null || lastName == null) {
            throw new IllegalArgumentException("First name and last name cannot be null");
        }

        String lastNamePrefix = lastName.substring(0, Math.min(2, lastName.length()));
        String firstNamePrefix = firstName.substring(0, Math.min(4, firstName.length()));

        String baseUserId = lastNamePrefix + firstNamePrefix;

        if (counter != null && counter > 0) {
            return baseUserId + counter;
        }

        return baseUserId;
    }
}
