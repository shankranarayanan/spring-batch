package com.batch.app.service;

import com.batch.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository for {@link User} entity persistence and lookup operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their business-level unique identifier.
     *
     * @param userId the unique user ID to search for
     * @return the matching user, or empty if not found
     */
    Optional<User> findByUserId(String userId);

    /**
     * Counts users whose IDs start with the given prefix.
     *
     * @param userIdPrefix prefix to match against user IDs
     * @return number of users with matching ID prefix
     */
    @Query(value = "SELECT COUNT(*) FROM users WHERE user_id LIKE CONCAT(?1, '%')", nativeQuery = true)
    int countByUserIdLike(String userIdPrefix);
}
