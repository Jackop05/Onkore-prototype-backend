package com.onkore_backend.onkore.Repository;

import com.onkore_backend.onkore.Model.Availability;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AvailabilityRepository extends MongoRepository<Availability, String> {}
