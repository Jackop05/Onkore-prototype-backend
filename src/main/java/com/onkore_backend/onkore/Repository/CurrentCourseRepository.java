package com.onkore_backend.onkore.Repository;

import com.onkore_backend.onkore.Model.Current_Course;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CurrentCourseRepository extends MongoRepository<Current_Course, String> {}
