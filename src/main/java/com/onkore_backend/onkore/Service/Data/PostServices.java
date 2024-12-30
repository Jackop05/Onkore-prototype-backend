package com.onkore_backend.onkore.Service.Data;

import com.onkore_backend.onkore.Model.*;
import com.onkore_backend.onkore.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.Subject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.DataTypeOperators.Type.typeOf;

@Service
public class PostServices {

    @Autowired
    private SubjectCourseRepository subjectCourseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository AdminRepository;

    @Autowired
    private CurrentCourseRepository currentCourseRepository;

    @Autowired
    private LessonDatesRepository lessonDatesRepository;

    @Autowired
    private DiscountCodeRepository DiscountCodeRepository;

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
        if (optionalUser.isPresent()) {
            subjectCourse = (Subject_Course) optionalSubjectCourse.get();
        } else {
            throw new RuntimeException("No course with given id found in database");
        }


        Current_Course currentCourse = new Current_Course();
        currentCourse.setSubject(subjectCourse.getSubject());
        currentCourse.setDescription(subjectCourse.getDescription());
        currentCourse.setLevel(subjectCourse.getLevel());
        currentCourse.setPrice(subjectCourse.getPrice());
        currentCourse.setTopics(new ArrayList<String>());
        currentCourse.setIconIndex(subjectCourse.getIconIndex());
        currentCourse.setUsername(user.getUsername());

        if (currentCourse.getLessonDates() == null) {
            currentCourse.setLessonDates(new ArrayList<>());
        }
        for (Date givenLessonDate : givenLessonDates) {
            Lesson_Dates lessonDate = new Lesson_Dates();
            lessonDate.setLessonDate(givenLessonDate);
            lessonDate.setStatus(null);
            lessonDatesRepository.save(lessonDate);

            currentCourse.getLessonDates().add(lessonDate);
        }

        currentCourseRepository.save(currentCourse);

        user.getCurrentCourses().add(currentCourse);
        userRepository.save(user);


        // !!! Teachers newCourse logic yet to implement here !!!
    }
}
