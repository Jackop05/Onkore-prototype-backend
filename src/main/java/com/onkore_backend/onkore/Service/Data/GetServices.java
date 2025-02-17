package com.onkore_backend.onkore.Service.Data;

import com.onkore_backend.onkore.Model.*;
import com.onkore_backend.onkore.Repository.*;
import com.onkore_backend.onkore.Util.Sorters;
import com.onkore_backend.onkore.Util.TimeOperator;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.onkore_backend.onkore.Util.JsonWebToken.getTokenDataFromCookie;

@Service
public class GetServices {

    @Autowired
    private SubjectCourseRepository subjectCourseRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DiscountCodeRepository discountCodeRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CurrentCourseRepository currentCourseRepository;

    public List<Map<String, Object>> getAllAdminData() {
        List<Admin> admins = adminRepository.findAll();

        return admins.stream().map(admin -> {
            Map<String, Object> teacherData = new HashMap<>();
            teacherData.put("id", admin.getId());
            teacherData.put("name", admin.getUsername());
            teacherData.put("email", admin.getEmail());
            // teacherData.put("profileImage", admin.getProfileImage()); // Assuming a profile image field exists
            teacherData.put("subjects", admin.getSubjectTeachingList()); // List of subjects
            // teacherData.put("lessonsCount", admin.getLessonsCount()); // Assuming there’s a field for lesson count
            teacherData.put("about", admin.getDescription()); // Short bio
            return teacherData;
        }).collect(Collectors.toList());
    }


    public static Map<String, Object> getUserData(HttpServletRequest request) {
        Claims claims = getTokenDataFromCookie(request);

        if (claims != null) {
            Map<String, Object> studentData = new HashMap<>();
            studentData.put("id", claims.get("id"));
            studentData.put("username", claims.get("username"));
            studentData.put("email", claims.get("email"));
            studentData.put("currentCourses", claims.get("currentCourses"));
            System.out.println(studentData.get("currentCourses"));
            studentData.put("role", claims.get("role"));
            return studentData;
        } else {
            return null;
        }
    }

    public List<Map<String, Object>> getUserCurrentCourses(HttpServletRequest request) {
        Claims claims = getTokenDataFromCookie(request);
        if (claims == null) {
            return null;
        }

        String userId = (String) claims.get("id");
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        List<Current_Course> currentCourses = user.getCurrentCourses();

        return currentCourses.stream().map(course -> Map.of(
                "id", course.getId(),
                "subject", course.getSubject(),
                "description", course.getDescription(),
                "lessonDates", course.getLessonDates(),
                "level", course.getSubjectCourse().getLevel(),
                "iconIndex", course.getSubjectCourse().getIconIndex()
        )).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getAdminCurrentCourses(HttpServletRequest request) {
        Claims claims = getTokenDataFromCookie(request);
        if (claims == null) {
            return null;
        }
        System.out.println(claims);

        String adminId = (String) claims.get("id");
        Optional<Admin> adminOptional = adminRepository.findById(adminId);
        if (adminOptional.isEmpty()) {
            return null;
        }

        Admin admin = adminOptional.get();
        List<Current_Course> currentCourses = admin.getCurrentCourses();

        return currentCourses.stream().map(course -> Map.of(
                "id", course.getId(),
                "subject", course.getSubject(),
                "description", course.getDescription(),
                "lessonDates", course.getLessonDates().stream().map(lesson -> Map.of(
                        "id", lesson.getId(),
                        "lessonDate", lesson.getLessonDate(),
                        "status", lesson.getStatus(),
                        "link", lesson.getLink()
                )).collect(Collectors.toList()),
                "level", course.getSubjectCourse().getLevel(),
                "username", course.getUsername()
        )).collect(Collectors.toList());
    }


    public Map<String, Object> getSingleUserCurrentCourse(HttpServletRequest request, String courseId) {
        Claims claims = getTokenDataFromCookie(request);
        if (claims == null) {
            return null;
        }

        Optional<Current_Course> courseOptional = currentCourseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            return null;
        }

        Current_Course course = courseOptional.get();
        Map courseInfo = Map.of(
                "id", course.getId(),
                "subject", course.getSubject(),
                "description", course.getDescription(),
                "lessonDates", course.getLessonDates().stream().map(lesson -> Map.of(
                        "id", lesson.getId(),
                        "lessonDate", lesson.getLessonDate(),
                        "status", lesson.getStatus()
                )).collect(Collectors.toList()), // Convert lessonDates to detailed objects
                "level", course.getSubjectCourse().getLevel()
        );

        return courseInfo;
    }

    public static Map<String, Object> getAdminData(HttpServletRequest request) {
        Claims claims = getTokenDataFromCookie(request);

        if (claims != null) {
            Map<String, Object> studentData = new HashMap<>();
            studentData.put("id", claims.get("id"));
            studentData.put("username", claims.get("username"));
            studentData.put("email", claims.get("email"));
            studentData.put("description", claims.get("description"));
            studentData.put("contact", claims.get("contact"));
            studentData.put("availability", claims.get("availability"));
            studentData.put("currentCourses", claims.get("currentCourses"));
            studentData.put("newCourses", claims.get("newCourses"));
            studentData.put("role", claims.get("role"));
            return studentData;
        } else {
            return null;
        }
    }

    public List<Subject_Course> getSubjectCoursesData() {
        return subjectCourseRepository.findAll();
    }

    public Map<String, List<List<LocalTime>>> getAllAvailableDates() {
        Map<String, List<List<LocalTime>>> availableDates = new HashMap<>();
        availableDates.put("Monday", new ArrayList<List<LocalTime>>());
        availableDates.put("Tuesday", new ArrayList<List<LocalTime>>());
        availableDates.put("Wednesday", new ArrayList<List<LocalTime>>());
        availableDates.put("Thursday", new ArrayList<List<LocalTime>>());
        availableDates.put("Friday", new ArrayList<List<LocalTime>>());
        availableDates.put("Saturday", new ArrayList<List<LocalTime>>());
        availableDates.put("Sunday", new ArrayList<List<LocalTime>>());

        List<Availability> availabilities = availabilityRepository.findAll();

        for (Availability availability : availabilities) {
            String weekday = availability.getWeekday();
            LocalTime start = availability.getHourStart();
            LocalTime end = availability.getHourEnd();

            List<LocalTime> availabilityBlock = new ArrayList<>();
            availabilityBlock.add(start);
            availabilityBlock.add(end);
            availableDates.get(weekday).add(availabilityBlock);
        }

        Sorters sorter = new Sorters();
        for (String weekday : availableDates.keySet()) {
            sorter.sortLocalTimeArray(availableDates.get(weekday));
        }

        return availableDates;
    }

    public Map<String, List<List<LocalTime>>> getReducedAvailableDates() {
        Map<String, List<List<LocalTime>>> allAvailableDates = getAllAvailableDates();

        for (String weekday : allAvailableDates.keySet()) {
            List<List<LocalTime>> dates = allAvailableDates.get(weekday);
            for (int i = 0; i < dates.size() - 1; i++) {
                List<LocalTime> currentDate = dates.get(i);
                List<LocalTime> nextDate = dates.get(i + 1);

                LocalTime startOfCurrentDate = currentDate.get(0);
                LocalTime endOfCurrentDate = currentDate.get(1);
                LocalTime startOfNextDate = nextDate.get(0);
                LocalTime endOfNextDate = nextDate.get(1);

                if (!endOfCurrentDate.isBefore(startOfNextDate)) {
                    dates.remove(i + 1);

                    if (!endOfCurrentDate.isBefore(endOfNextDate)) {
                        i -= 2;
                    } else {
                        List<LocalTime> tempDates = Arrays.asList(startOfCurrentDate, endOfNextDate);
                        dates.set(i,  tempDates);
                        i -= 1;
                    }
                }
            }

            allAvailableDates.put(weekday, dates);
        }

        return allAvailableDates;
    }

    public Map<String, Object> getAvailability(String adminId) {
        Map<String, Object> response = new HashMap<>();

        // Check if admin exists
        Optional<Admin> optionalAdmin = adminRepository.findById(adminId);
        if (optionalAdmin.isEmpty()) {
            response.put("error", "Admin not found");
            return response;
        }

        Admin admin = optionalAdmin.get();
        List<Availability> availabilities = admin.getAvailability(); // Get availability list

        // Convert availability objects to a structured response
        List<Map<String, String>> availabilityList = availabilities.stream().map(availability -> Map.of(
                "id", availability.getId(),
                "weekday", availability.getWeekday(),
                "hourStart", availability.getHourStart().toString(),
                "hourEnd", availability.getHourEnd().toString()
        )).collect(Collectors.toList());

        response.put("admin_id", admin.getId());
        response.put("availability", availabilityList);
        return response;
    }
    public List<String> getAvailableDays(String courseId, String hour) {
        String[] hours = hour.split("-");

        if (hours.length != 2) {
            throw new IllegalArgumentException("Invalid hour format. Expected 'HH:mm-HH:mm'");
        }

        // Normalize hours to ensure leading zeros
        String formattedStart = String.format("%02d:%s", Integer.parseInt(hours[0].trim().split(":")[0]), hours[0].trim().split(":")[1]);
        String formattedEnd = String.format("%02d:%s", Integer.parseInt(hours[1].trim().split(":")[0]), hours[1].trim().split(":")[1]);

        LocalTime hourStart = LocalTime.parse(formattedStart);
        LocalTime hourEnd = LocalTime.parse(formattedEnd);

        // Retrieve the course subject
        Optional<Subject_Course> courseOptional = subjectCourseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            throw new IllegalArgumentException("Course not found for the given courseId");
        }
        String courseSubject = courseOptional.get().getSubject();

        // Retrieve all admins
        List<Admin> admins = adminRepository.findAll();

        // Filter admins based on availability and subject teaching
        TimeOperator timeOperator = new TimeOperator();
        List<Admin> availableAdmins = admins.stream()
                .filter(admin -> admin.getSubjectTeachingList() != null
                        && admin.getSubjectTeachingList().contains(courseSubject)) // Check subject
                .filter(admin -> admin.getAvailability() != null
                        && admin.getAvailability().stream().anyMatch(availability ->
                        timeOperator.isTimeInRange(availability, hourStart, hourEnd))) // Check time range
                .collect(Collectors.toList());



        // Get the list of available weekdays from all matching admins
        List<String> availableDays = availableAdmins.stream()
                .flatMap(admin -> admin.getAvailability().stream()
                        .filter(availability -> timeOperator.isTimeInRange(availability, hourStart, hourEnd))
                        .map(Availability::getWeekday))
                .distinct()
                .collect(Collectors.toList());

        return availableDays;
    }

    public boolean checkPromoCode(String promoCode, String email, String subjectId) {
        Optional<Discount_Code> discountCodeOptional = discountCodeRepository.findByCode(promoCode);

        if (discountCodeOptional.isEmpty()) {
            // Promo code not found
            return false;
        }

        Discount_Code discountCode = discountCodeOptional.get();

        // Validate if the promo code is active
        Date now = new Date();
        if (discountCode.getBeginsAt().after(now) || discountCode.getExpiresAt().before(now)) {
            return false;
        }

        // Validate if the promo code is applicable to the user's email or subjects
        boolean isValidForUser = false;

        // Check if the user's email is eligible
        if (discountCode.getEmails() != null && discountCode.getEmails().contains(email)) {
            isValidForUser = true;
        }

        Optional<Subject_Course> subjectCourseOptional = subjectCourseRepository.findById(subjectId);
        if (subjectCourseOptional.isEmpty()) {
            return false;
        }
        Subject_Course subjectCourse = subjectCourseOptional.get();

        // Check if the promo code applies to specific subjects
        if (!isValidForUser && discountCode.getSubjects() != null && !discountCode.getSubjects().isEmpty()) {
            // Assume the subject is passed in the username for now
            isValidForUser = discountCode.getSubjects().contains(subjectCourse.getSubject());
        }

        return isValidForUser;
    }
}
