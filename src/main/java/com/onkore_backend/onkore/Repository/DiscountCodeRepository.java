package com.onkore_backend.onkore.Repository;

import com.onkore_backend.onkore.Model.Discount_Code;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DiscountCodeRepository extends MongoRepository<Discount_Code, String> {}
