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
        if (authCodePassword == null || givenCodePassword == null || codeName == null || beginsAt == null || expiresAt == null || (discountPercentage == null || discountAmount == null) || subjects == null || emails == null) {
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


    public void postCourse(String username, String subjectCourse_id, List<Date> givenLessonDates, String bonusInfo) {
        User user = (User) userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("No user with given username in database"));
        Subject_Course subjectCourse = subjectCourseRepository.findById(subjectCourse_id)
                .orElseThrow(() -> new RuntimeException("No course with given id found in database"));

        New_Course newCourse = new New_Course();
        newCourse.setUsername(user.getUsername());
        newCourse.setDescription(subjectCourse.getDescription());
        newCourse.setSubject(subjectCourse.getSubject());
        newCourse.setLessonDates(new ArrayList<>());
        newCourse.setSubjectCourse(subjectCourse);
        newCourse.setAdditionalInfo(bonusInfo);

        List<Admin> availableAdmins = getAvailableAdmins(givenLessonDates, subjectCourse.getSubject());
        newCourse.setAdmins(availableAdmins);

        List<Lesson_Dates> newLessonDates = givenLessonDates.stream().map(date -> {
            Lesson_Dates lessonDate = new Lesson_Dates();
            lessonDate.setLessonDate(date);
            lessonDate.setStatus("");
            lessonDate.setLink("");
            lessonDatesRepository.save(lessonDate);
            return lessonDate;
        }).collect(Collectors.toList());

        newCourse.setLessonDates(newLessonDates);
        newCourseRepository.save(newCourse);

        // Create Current_Course
        Current_Course currentCourse = new Current_Course();
        currentCourse.setId(newCourse.getId()); // Keep the same ID
        currentCourse.setSubject(newCourse.getSubject());
        currentCourse.setDescription(newCourse.getDescription());
        currentCourse.setLessonDates(newLessonDates);
        currentCourse.setSubjectCourse(newCourse.getSubjectCourse());
        currentCourse.setUsername(newCourse.getUsername());
        currentCourse.setAdditionalInfo(newCourse.getAdditionalInfo());
        currentCourseRepository.save(currentCourse);

        // Add Current_Course to User
        if (user.getCurrentCourses() == null) {
            user.setCurrentCourses(new ArrayList<>());
        }
        user.getCurrentCourses().add(currentCourse);
        userRepository.save(user);
    }


    public void handleNewCourse(String courseId, String adminId) {
        Current_Course currentCourse = currentCourseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("No current_course with given id found in database"));

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("No admin found with the given id"));

        User user = (User) userRepository.findByUsername(currentCourse.getUsername())
                .orElseThrow(() -> new RuntimeException("No user found with the given username"));

        if (admin.getCurrentCourses() == null) {
            admin.setCurrentCourses(new ArrayList<>());
        }

        Current_Course courseToRemove = null;

        // Check if the admin already has a course with the same subject and username
        for (Current_Course existingCourse : admin.getCurrentCourses()) {
            if (existingCourse.getSubjectCourse().getId().equals(currentCourse.getSubjectCourse().getId())
                    && existingCourse.getUsername().equals(currentCourse.getUsername())) {

                // Ensure lesson lists are initialized
                if (existingCourse.getLessonDates() == null) {
                    existingCourse.setLessonDates(new ArrayList<>());
                }
                if (currentCourse.getLessonDates() != null) {
                    existingCourse.getLessonDates().addAll(currentCourse.getLessonDates());
                }

                courseToRemove = currentCourse;  // Mark for removal
                break;
            }
        }

        if (courseToRemove != null) {
            admin.getCurrentCourses().remove(courseToRemove);
            user.getCurrentCourses().remove(courseToRemove);
            adminRepository.save(admin);
            userRepository.save(user);
            currentCourseRepository.deleteById(courseId);
            return;
        }

        // If no existing course was found, add as a new course
        admin.getCurrentCourses().add(currentCourse);
        adminRepository.save(admin);

        // Remove from new_courses collection
        newCourseRepository.deleteById(courseId);
    }




    public void changeCourse(String course_id, String admin1Username, String admin1Email, String admin2Username, String admin2Email) {
        Admin admin1 = adminRepository.findByUsername(admin1Username)
                .orElseThrow(() -> new RuntimeException("Admin1 not found"));
        if (!admin1.getEmail().equals(admin1Email)) {
            throw new RuntimeException("Admin1 email does not match");
        }

        Admin admin2 = adminRepository.findByUsername(admin2Username)
                .orElseThrow(() -> new RuntimeException("Admin2 not found"));
        if (!admin2.getEmail().equals(admin2Email)) {
            throw new RuntimeException("Admin2 email does not match");
        }

        Current_Course course = currentCourseRepository.findById(course_id)
                .orElseThrow(() -> new RuntimeException("No course with given id found"));

        if (admin1.getCurrentCourses() != null) {
            admin1.getCurrentCourses().remove(course);
            adminRepository.save(admin1);
        }

        if (admin2.getCurrentCourses() == null) {
            admin2.setCurrentCourses(new ArrayList<>());
        }
        admin2.getCurrentCourses().add(course);
        adminRepository.save(admin2);
    }

    private List<Admin> getAvailableAdmins(List<Date> lessonDates, String subject) {
        return adminRepository.findAll().stream()
                .filter(admin -> admin.getSubjectTeachingList().contains(subject))
                .filter(admin -> isAvailableForLessonDates(admin, lessonDates))
                .collect(Collectors.toList());
    }

    private boolean isAvailableForLessonDates(Admin admin, List<Date> lessonDates) {
        for (Date lessonDate : lessonDates) {
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
                return false;
            }
        }
        return true;
    }



}









/*
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
        newCourse.setSubjectCourse(subjectCourse);

        List<Admin> leastBusyAdmins = getLeastBusyAdmins(givenLessonDates, 0);
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

            Current_Course currentCourse = getCurrentCourseFromNewCourse(subjectCourse, newCourse, user.getUsername());
            currentCourseRepository.save(currentCourse);

            newCourse.setCurrentCourse(currentCourse);
            newCourseRepository.save(newCourse);

            for (Admin selectedAdmin : leastBusyAdmins) {
                if (selectedAdmin.getNewCourses() == null) {
                    selectedAdmin.setNewCourses(new ArrayList<>());
                }
                selectedAdmin.getNewCourses().add(newCourse);
                adminRepository.save(selectedAdmin);
            }

            if (user.getCurrentCourses() == null) {
                user.setCurrentCourses(new ArrayList<>());
            }
            user.getCurrentCourses().add(currentCourse);
            userRepository.save(user);
        }
    }

    public String handleNewCourse(String course_id, String admin_id, String action) {
        if (!action.equals("rejected") && !action.equals("accepted") ) {
            throw new IllegalArgumentException("Invalid action name");
        }

        Optional optionalNewCourse = newCourseRepository.findById(course_id);
        if (!optionalNewCourse.isPresent()) {
            throw new RuntimeException("No new_course with given id found in database");
        }
        New_Course newCourse = (New_Course) optionalNewCourse.get();

        Optional optionalAdmin = adminRepository.findById(admin_id);
        if (!optionalAdmin.isPresent()) {
            throw new RuntimeException("No admin with given id found in database");
        }
        Admin admin = (Admin) optionalAdmin.get();

        Optional optionalCurrentCourse = currentCourseRepository.findById(newCourse.getCurrentCourse().getId());
        if (!optionalCurrentCourse.isPresent()) {
            throw new RuntimeException("No admin with given id found in database");
        }
        Current_Course currentCourse = (Current_Course) optionalCurrentCourse.get();

        // to implement here : check if new_course is in admins new_courses

        int numberOfAdmins = newCourse.getAdmins().size();

        if (action.equalsIgnoreCase("rejected")) {
            newCourse.getAdmins().remove(admin);
            newCourseRepository.save(newCourse);

            if (numberOfAdmins == 1) {
                List<Date> lessonDates = new ArrayList<>();
                for (Lesson_Dates lesson : newCourse.getLessonDates()) {
                    lessonDates.add(lesson.getLessonDate());
                }
                List<Admin> leastBusyAdmins = getLeastBusyAdmins(lessonDates, admin.getCurrentCourses().size());

                newCourse.setAdmins(leastBusyAdmins);
                newCourseRepository.save(newCourse);

                if (!leastBusyAdmins.isEmpty()) {
                    for (Admin selectedAdmin : leastBusyAdmins) {
                        if (selectedAdmin.getNewCourses() == null) {
                            selectedAdmin.setNewCourses(new ArrayList<>());
                        }

                        if (selectedAdmin.getId() != admin.getId()) {
                            selectedAdmin.getNewCourses().add(newCourse);
                        } else {
                            admin.getNewCourses().remove(newCourse);
                        }
                    }
                    adminRepository.saveAll(leastBusyAdmins);

                } else {

                    return "No other admin with available dates for this course found in database";
                }
            }

            return "Course rejected successfully";

        } else if (action.equalsIgnoreCase("accepted")) {

                // Remove the new course from all associated admins
                for (Admin selectedAdmin : newCourse.getAdmins()) {
                    selectedAdmin.getNewCourses().remove(newCourse);
                    adminRepository.save(selectedAdmin); // Save each admin explicitly
                }

                // Ensure the accepting admin's currentCourses list is initialized
                if (admin.getCurrentCourses() == null) {
                    admin.setCurrentCourses(new ArrayList<>());
                }

                // Add the current course to the accepting admin's currentCourses
                admin.getCurrentCourses().add(currentCourse);

                // Save the accepting admin
                adminRepository.save(admin);

                // Delete the new course
                newCourseRepository.deleteById(course_id);

                return "Course accepted and registered successfully";
            }

            return null;
    }

    private static Current_Course getCurrentCourseFromNewCourse(Subject_Course subjectCourse, New_Course newCourse, String username) {
        Current_Course currentCourse = new Current_Course();
        currentCourse.setSubject(subjectCourse.getSubject());
        currentCourse.setDescription(subjectCourse.getDescription());
        currentCourse.setLevel(subjectCourse.getLevel());
        currentCourse.setPrice(subjectCourse.getPrice());
        currentCourse.setTopics(new ArrayList<>());
        currentCourse.setIconIndex(subjectCourse.getIconIndex());
        currentCourse.setUsername(username);
        currentCourse.setLessonDates(newCourse.getLessonDates());
        currentCourse.setSubjectCourse(newCourse.getSubjectCourse());
        return currentCourse;
    }

    private List<Admin> getLeastBusyAdmins(List<Date> lessonDates, int minCourseCountAsumption) {
        List<Admin> availableAdmins = new ArrayList<>();

        for (Admin admin : adminRepository.findAll()) {

            boolean allDatesMatch = true;
            for (Date lessonDate : lessonDates) {

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

        int minCourseCount;
        minCourseCount = availableAdmins.stream()
                .mapToInt(admin -> ((admin.getNewCourses()) == null || admin.getNewCourses().size() != minCourseCountAsumption) ? 0 : admin.getNewCourses().size())
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

        return leastBusyAdmins;
    }

*/
