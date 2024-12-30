package com.onkore_backend.onkore.Repository;

import com.onkore_backend.onkore.Model.Lesson_Dates;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LessonDatesRepository extends MongoRepository<Lesson_Dates, String> {
}