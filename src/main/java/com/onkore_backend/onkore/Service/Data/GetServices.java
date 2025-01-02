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
}
