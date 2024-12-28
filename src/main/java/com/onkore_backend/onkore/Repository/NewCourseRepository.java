package com.onkore_backend.onkore.Repository;

import com.onkore_backend.onkore.Model.New_Course;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NewCourseRepository extends MongoRepository<New_Course, String> {}
