package com.onkore_backend.onkore.Service.Data;

import java.time.LocalDate;

public class PutServices {

    public void putCanceledLesson(String lesson_id, String course_id) {}

    public void putAcceptedLesson(String course_id) {}

    public void putNotAcceptedLesson(String course_id) {}

    public void putMaterial(String course_id, String material) {}   // this one has to be improved, but I don't know how ot implement files transfer yet

    public void putAvailability(String admin_id, LocalDate lessonDate) {}

    public void putTopic(String course_id, String topicName) {}
}
