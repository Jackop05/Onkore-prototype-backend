package com.onkore_backend.onkore.Service.Data;

import com.onkore_backend.onkore.Model.Admin;
import com.onkore_backend.onkore.Model.Availability;
import com.onkore_backend.onkore.Model.Discount_Code;
import com.onkore_backend.onkore.Repository.AdminRepository;
import com.onkore_backend.onkore.Repository.AvailabilityRepository;
import com.onkore_backend.onkore.Repository.DiscountCodeRepository;
import com.onkore_backend.onkore.Repository.SubjectCourseRepository;
import com.onkore_backend.onkore.Model.Subject_Course;
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
            studentData.put("role", claims.get("role"));
            return studentData;
        } else {
            return null;
        }
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





    public List<String> getAvailableDays(String courseId, String hour) {

        String[] hours = hour.split("-");
        if (hours.length != 2) {
            throw new IllegalArgumentException("Invalid hour format. Expected 'HH:mm-HH:mm'");
        }

        LocalTime hourStart = LocalTime.parse(hours[0].trim());
        LocalTime hourEnd = LocalTime.parse(hours[1].trim());

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
