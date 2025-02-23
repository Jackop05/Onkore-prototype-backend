package com.onkore_backend.onkore.Service.Data;

import com.onkore_backend.onkore.Model.*;
import com.onkore_backend.onkore.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DeleteServices {

    @Autowired
    private DiscountCodeRepository discountCodeRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private CurrentCourseRepository currentCourseRepository;

    @Autowired
    private LessonDatesRepository lessonDatesRepository;

    @Autowired
    private MaterialRepository materialRepository;

    public String deleteDiscountCode(String codeName, String givenCodePassword, String authCodePassword) {
        if (authCodePassword == null ) {
            if (!authCodePassword.equals(givenCodePassword)) {
                throw new RuntimeException("Invalid auth code");
            }

            throw new RuntimeException("Invalid procedure");
        }

        Optional<Discount_Code> discountCode = discountCodeRepository.findAll().stream()
                .filter(dc -> dc.getCode().equals(codeName))
                .findFirst();
        discountCode.ifPresent(discountCodeRepository::delete);

        return "Discount code deleted successfully";
    }

    public String deleteAdminAvailability(String admin_id, String availability_id) {
        Optional<Admin> admin = adminRepository.findById(admin_id);
        if (admin.isPresent()) {
            Admin foundAdmin = admin.get();
            foundAdmin.getAvailability().removeIf(avail -> avail.getId().equals(availability_id));
            adminRepository.save(foundAdmin);
            availabilityRepository.deleteById(availability_id);

            return "Availability deleted successfully";
        }

        return "Admin or availability id not found";
    }

    public String deleteTopic(String course_id, String topicName) {
        Optional<Current_Course> course = currentCourseRepository.findById(course_id);
        if (course.isPresent()) {
            Current_Course foundCourse = course.get();
            foundCourse.getTopics().removeIf(topic -> topic.equals(topicName));
            currentCourseRepository.save(foundCourse);
            
            return "Topic deleted successfully";
        }

        return "Course not found";
    }

    public void cancelLesson(String courseId, String lessonId) {
        Current_Course course = currentCourseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("No course found with given ID"));

        for (Lesson_Dates lesson : course.getLessonDates()) {
            if (lesson.getId().equals(lessonId)) {
                lesson.setLink(null);
                lesson.setStatus("odwołane");
                lessonDatesRepository.save(lesson);
                return;
            }
        }
        throw new RuntimeException("No lesson found with given ID in this course");
    }







    public void deleteMaterialFromCourse(String courseId, String materialId, boolean deleteFileFromDisk) {
        Current_Course course = currentCourseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // 1) Filter out the material from the course
        List<Material> updatedList = new ArrayList<>();
        Material foundMaterial = null;

        if (course.getMaterials() != null) {
            for (Material m : course.getMaterials()) {
                if (!m.getId().equals(materialId)) {
                    updatedList.add(m);
                } else {
                    foundMaterial = m;
                }
            }
        }
        course.setMaterials(updatedList);
        currentCourseRepository.save(course);

        // 2) Optionally delete the Material record + file from disk
        if (foundMaterial != null) {
            if (deleteFileFromDisk) {
                File file = new File(foundMaterial.getFilePath());
                if (file.exists()) {
                    file.delete();
                }
            }
            materialRepository.deleteById(materialId);
        }
    }
}
