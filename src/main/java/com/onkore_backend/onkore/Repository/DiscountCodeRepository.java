package com.onkore_backend.onkore.Repository;

import com.onkore_backend.onkore.Model.Discount_Code;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DiscountCodeRepository extends MongoRepository<Discount_Code, String> {
    Optional<Discount_Code> findByCode(String code);
}
