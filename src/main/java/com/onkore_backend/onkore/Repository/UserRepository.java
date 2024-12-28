package com.onkore_backend.onkore.Repository;

import com.onkore_backend.onkore.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<Object> findByEmail(String email);
}
