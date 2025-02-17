package com.onkore_backend.onkore.Service.Data;

import com.onkore_backend.onkore.Model.Admin;
import com.onkore_backend.onkore.Model.Availability;
import com.onkore_backend.onkore.Model.Current_Course;
import com.onkore_backend.onkore.Model.Lesson_Dates;
import com.onkore_backend.onkore.Repository.AdminRepository;
import com.onkore_backend.onkore.Repository.AvailabilityRepository;
import com.onkore_backend.onkore.Repository.CurrentCourseRepository;
import com.onkore_backend.onkore.Repository.LessonDatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class PutServices {

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    AvailabilityRepository availabilityRepository;

    @Autowired
    CurrentCourseRepository currentCourseRepository;
    @Autowired
    private LessonDatesRepository lessonDatesRepository;

    public String putLessonStatus(String course_id, String lesson_id, String status) {
        if (!status.equals("accepted") && !status.equals("rejected") && !status.equals("canceled") && !status.equals("running now") && !status.equals("done") ) {
            throw new IllegalArgumentException("Invalid status name");
        }

        Current_Course currentCourse;
        Optional<Current_Course> courseOptional = currentCourseRepository.findById(course_id);
        if (!courseOptional.isPresent()) {
            throw new IllegalArgumentException("Course with ID " + course_id + " not found.");
        }

        Boolean lessonFound = false;
        currentCourse = courseOptional.get();
        for (Lesson_Dates lesson : currentCourse.getLessonDates()) {
            if (lesson.getId().equals(lesson_id)) {
                lesson.setStatus(status);
                lessonDatesRepository.save(lesson);

                lessonFound = true;
                break;
            }
        }

        if (!lessonFound) {
            throw new IllegalArgumentException("Lesson with ID " + lesson_id + " not found.");
        }

        return "Lesson's status changed to canceled";
    }

    // public void putMaterial(String course_id, String material) {}   // this one has to be improved, but I don't know how ot implement files transfer yet

    public void updateLessonLink(String courseId, String lessonId, String link) {
        Current_Course course = currentCourseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("No course found with given ID"));

        for (Lesson_Dates lesson : course.getLessonDates()) {
            if (lesson.getId().equals(lessonId)) {
                lesson.setLink(link);
                lesson.setStatus(link.isEmpty() ? "zaplanowane" : "w trakcie");
                lessonDatesRepository.save(lesson);
                return;
            }
        }
        throw new RuntimeException("No lesson found with given ID in this course");
    }

    public void updateLessonStatus(String courseId, String lessonId, String newStatus) {
        if (newStatus.equalsIgnoreCase("zaplanowane") && newStatus.equalsIgnoreCase("w trakcie") && newStatus.equalsIgnoreCase("zakończone")) {
            throw new RuntimeException("Invalid lesson status: " + newStatus);
        }

        Current_Course course = currentCourseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("No course found with given ID"));

        for (Lesson_Dates lesson : course.getLessonDates()) {
            if (lesson.getId().equals(lessonId)) {
                lesson.setStatus(newStatus);
                lessonDatesRepository.save(lesson);
                return;
            }
        }
        throw new RuntimeException("No lesson found with given ID in this course");
    }


    public void putAvailability(String admin_id, LocalTime startHour, LocalTime endHour, String weekday) {
        // Fetch the admin from the database
        System.out.println(admin_id);
        Optional<Admin> optionalAdmin = adminRepository.findById(admin_id);
        if (!optionalAdmin.isPresent()) {
            throw new RuntimeException("Admin not found");
        }
        System.out.println("Working1");
        Admin admin = optionalAdmin.get();

        // Create new availability entry
        System.out.println("Working2");
        Availability availability = new Availability();
        availability.setHourStart(startHour);
        availability.setHourEnd(endHour);
        availability.setWeekday(weekday);
        System.out.println("Working3");

        // Save availability first so it has an ID
        availabilityRepository.save(availability);
        System.out.println("Working4");

        // Ensure admin's availability list is initialized
        if (admin.getAvailability() == null) {
            admin.setAvailability(new ArrayList<>());
        }
        System.out.println("Working5");

        // Add availability to admin and save
        admin.getAvailability().add(availability);
        adminRepository.save(admin);
        System.out.println("Working6");

    }


    public void putTopic(String course_id, String topicName) {
        Current_Course currentCourse;
        Optional optionalCurrentCourse = currentCourseRepository.findById(course_id);
        if (optionalCurrentCourse.isPresent()) {
            currentCourse = (Current_Course) optionalCurrentCourse.get();
        } else {
            throw new RuntimeException("Course not found");
        }

        ArrayList<String> newTopicList = currentCourse.getTopics();
        newTopicList.add(topicName);

        currentCourse.setTopics(newTopicList);
        currentCourseRepository.save(currentCourse);
    }
}
