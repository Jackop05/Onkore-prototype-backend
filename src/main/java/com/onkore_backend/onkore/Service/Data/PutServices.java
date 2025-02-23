package com.onkore_backend.onkore.Service.Data;

import com.onkore_backend.onkore.Model.*;
import com.onkore_backend.onkore.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PutServices {
    private static final String UPLOAD_DIR = "/tmp/uploads";

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    AvailabilityRepository availabilityRepository;

    @Autowired
    CurrentCourseRepository currentCourseRepository;

    @Autowired
    private LessonDatesRepository lessonDatesRepository;

    @Autowired
    private MaterialRepository materialRepository;

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





    public Material uploadPdfAndAddToCourse(String courseId, MultipartFile file) throws IOException {
        // 1) Ensure course exists
        Current_Course course = currentCourseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // 2) Store the file on disk
        if (!new File(UPLOAD_DIR).exists()) {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        }

        // For uniqueness, you can do something like a timestamp or a random ID
        String uniqueFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR, uniqueFileName);
        Files.write(filePath, file.getBytes());  // Save the PDF bytes

        // 3) Create Material doc in DB
        Material material = new Material();
        material.setFilePath(filePath.toString());
        material.setOriginalFilename(file.getOriginalFilename());
        material = materialRepository.save(material);

        // 4) Add to the course's list of materials
        if (course.getMaterials() == null) {
            course.setMaterials(new ArrayList<>());
        }
        course.getMaterials().add(material);

        // 5) Save the updated course
        currentCourseRepository.save(course);

        return material;
    }
}
