package com.onkore_backend.onkore.Repository;

import com.onkore_backend.onkore.Model.Subject_Course;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubjectCourseRepository extends MongoRepository<Subject_Course, String> {}
