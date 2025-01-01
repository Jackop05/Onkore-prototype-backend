package com.onkore_backend.onkore.Service.Data;

import com.onkore_backend.onkore.Model.Availability;
import com.onkore_backend.onkore.Repository.AvailabilityRepository;
import com.onkore_backend.onkore.Repository.SubjectCourseRepository;
import com.onkore_backend.onkore.Model.Subject_Course;
import com.onkore_backend.onkore.Util.Sorters;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;

import static com.onkore_backend.onkore.Util.JsonWebToken.getTokenDataFromCookie;

@Service
public class GetServices {

    @Autowired
    private SubjectCourseRepository subjectCourseRepository;
    @Autowired
    private AvailabilityRepository availabilityRepository;

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
        Map<String, List> availableDates = new HashMap<>();
        availableDates.put("Monday", new ArrayList<>());
        availableDates.put("Tuesday", new ArrayList<>());
        availableDates.put("Wednesday", new ArrayList<>());
        availableDates.put("Thursday", new ArrayList<>());
        availableDates.put("Friday", new ArrayList<>());
        availableDates.put("Saturday", new ArrayList<>());
        availableDates.put("Sunday", new ArrayList<>());

        List<Availability> availabilities = availabilityRepository.findAll();

        Map<String, List<List<LocalTime>>> availabilityMap = new HashMap<>();

        for (Availability availability : availabilities) {
            String weekday = availability.getWeekday();
            LocalTime start = availability.getHourStart();
            LocalTime end = availability.getHourEnd();

            List<LocalTime> availabilityBlock = new ArrayList<LocalTime>();
            availabilityBlock.add(start);
            availabilityBlock.add(end);
            availabilityMap.get(weekday).add(availabilityBlock);
        }

        return availabilityMap;
    }

    public Map<String, List<List<LocalTime>>> getRedusedAvailableDates() {
        Map<String, List<List<LocalTime>>> allAvailableDates = getAllAvailableDates();

        Sorters sorter = new Sorters();
        for (String weekday : allAvailableDates.keySet()) {
            sorter.sortLocalTimeArray(allAvailableDates.get(weekday));
        }

        for (String weekday : allAvailableDates.keySet()) {
            // Yet to finish here!!!
        }

        // And yet to finish here!!!
    }
}
