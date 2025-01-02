package com.onkore_backend.onkore.Repository;

import com.onkore_backend.onkore.Model.New_Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface NewCourseRepository extends MongoRepository<New_Course, String> {
    @Query(value = "{ '_id': ?0 }", delete = true)
    void deleteById(String course_id);
}
