package com.onkore_backend.onkore.Service.Data;

import com.onkore_backend.onkore.Model.*;
import com.onkore_backend.onkore.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServices {

    @Autowired
    private SubjectCourseRepository subjectCourseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CurrentCourseRepository currentCourseRepository;

    @Autowired
    private LessonDatesRepository lessonDatesRepository;

    @Autowired
    private DiscountCodeRepository DiscountCodeRepository;

    @Autowired
    private NewCourseRepository newCourseRepository;

    public void postDiscountCode(String codeName, Date beginsAt, Date expiresAt, Integer discountPercentage, Integer discountAmount, List<String> subjects, List<String> emails, String givenCodePassword, String authCodePassword) {
        Discount_Code newDiscountCode = new Discount_Code();
        if (authCodePassword == null || givenCodePassword == null || codeName == null || beginsAt == null || expiresAt == null || ( discountPercentage == null || discountAmount == null) || subjects == null || emails == null) {
            if (!authCodePassword.equals(givenCodePassword)) {
                throw new RuntimeException("Invalid auth code");
            }

            throw new RuntimeException("Invalid procedure");
        }

        newDiscountCode.setCode(codeName);
        newDiscountCode.setBeginsAt(beginsAt);
        newDiscountCode.setExpiresAt(expiresAt);
        newDiscountCode.setDiscountPercentage(discountPercentage);
        newDiscountCode.setDiscountAmount(discountAmount);
        newDiscountCode.setSubjects(subjects);
        newDiscountCode.setEmails(emails);
        DiscountCodeRepository.save(newDiscountCode);
    }

    public void postCourse(String user_id, String subjectCourse_id, List<Date> givenLessonDates) {
        User user;
        Optional optionalUser = userRepository.findById(user_id);
        if (optionalUser.isPresent()) {
            user = (User) optionalUser.get();
        } else {
            throw new RuntimeException("No user with given id found in database");
        }

        Subject_Course subjectCourse;
        Optional optionalSubjectCourse = subjectCourseRepository.findById(subjectCourse_id);
        if (optionalSubjectCourse.isPresent()) {
            subjectCourse = (Subject_Course) optionalSubjectCourse.get();
        } else {
            throw new RuntimeException("No course with given id found in database");
        }

        New_Course newCourse = new New_Course();
        newCourse.setUsername(user.getUsername());
        newCourse.setDescription(subjectCourse.getDescription());
        newCourse.setSubject(subjectCourse.getSubject());
        newCourse.setLessonDates(new ArrayList<>());

        List<Admin> availableAdmins = new ArrayList<>();

        for (Admin admin : adminRepository.findAll()) {
            boolean allDatesMatch = true;

            for (Date lessonDate : givenLessonDates) {
                boolean dateMatches = false;

                for (Availability availability : admin.getAvailability()) {
                    LocalDate localDate = lessonDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalTime lessonTime = lessonDate.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                    String weekday = localDate.getDayOfWeek().toString();

                    if (availability.getWeekday().equalsIgnoreCase(weekday)
                            && !lessonTime.isBefore(availability.getHourStart())
                            && !lessonTime.isAfter(availability.getHourEnd())) {
                        dateMatches = true;
                        break;
                    }
                }

                if (!dateMatches) {
                    allDatesMatch = false;
                    break;
                }
            }

            if (allDatesMatch) {
                availableAdmins.add(admin);
            }
        }

        if (availableAdmins.isEmpty()) {
            throw new RuntimeException("No available admins for the given lesson dates and times");
        }

        int minCourseCount = availableAdmins.stream()
                .mapToInt(admin -> admin.getNewCourses() == null ? 0 : admin.getNewCourses().size())
                .min()
                .orElse(0);

        List<Admin> leastBusyAdmins = availableAdmins.stream()
                .filter(admin -> {
                    if (admin.getNewCourses() == null) {
                        admin.setNewCourses(new ArrayList<>());
                    }
                    return admin.getNewCourses().size() == minCourseCount;
                })
                .collect(Collectors.toList());

        if (!leastBusyAdmins.isEmpty()) {
            newCourse.setAdmins(leastBusyAdmins);

            List<Lesson_Dates> newLessonDates = new ArrayList<Lesson_Dates>();
            for (Date givenLessonDate : givenLessonDates) {
                Lesson_Dates lessonDate = new Lesson_Dates();
                lessonDate.setLessonDate(givenLessonDate);
                lessonDate.setStatus("");
                lessonDatesRepository.save(lessonDate);

                newLessonDates.add(lessonDate);
            }

            newCourse.setLessonDates(newLessonDates);
            newCourseRepository.save(newCourse);

            for (Admin selectedAdmin : leastBusyAdmins) {
                if (selectedAdmin.getNewCourses() == null) {
                    selectedAdmin.setNewCourses(new ArrayList<>());
                }
                selectedAdmin.getNewCourses().add(newCourse);
                adminRepository.save(selectedAdmin);
            }

            Current_Course currentCourse = new Current_Course();
            currentCourse.setSubject(subjectCourse.getSubject());
            currentCourse.setDescription(subjectCourse.getDescription());
            currentCourse.setLevel(subjectCourse.getLevel());
            currentCourse.setPrice(subjectCourse.getPrice());
            currentCourse.setTopics(new ArrayList<>());
            currentCourse.setIconIndex(subjectCourse.getIconIndex());
            currentCourse.setUsername(user.getUsername());
            currentCourse.setLessonDates(newLessonDates);

            currentCourseRepository.save(currentCourse);

            if (user.getCurrentCourses() == null) {
                user.setCurrentCourses(new ArrayList<>());
            }
            user.getCurrentCourses().add(currentCourse);
            userRepository.save(user);
        }
    }

    // Remove course from new courses and add it to the new courses OR just remove it and check if there is available place in other teachers
}
