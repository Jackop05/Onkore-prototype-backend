package com.onkore_backend.onkore.Repository;

import com.onkore_backend.onkore.Model.Material;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends MongoRepository<Material, String> {
}