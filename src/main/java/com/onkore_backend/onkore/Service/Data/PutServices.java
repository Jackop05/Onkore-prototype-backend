package com.onkore_backend.onkore.Service.Data;

import com.onkore_backend.onkore.Model.Admin;
import com.onkore_backend.onkore.Model.Availability;
import com.onkore_backend.onkore.Repository.AdminRepository;
import com.onkore_backend.onkore.Repository.AvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class PutServices {

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    AvailabilityRepository availabilityRepository;

    public void putCanceledLesson(String lesson_id, String course_id) {}

    public void putAcceptedLesson(String course_id) {}

    public void putNotAcceptedLesson(String course_id) {}

    // public void putMaterial(String course_id, String material) {}   // this one has to be improved, but I don't know how ot implement files transfer yet

    public void putAvailability(String admin_id, LocalTime startHour, LocalTime endHour, String weekday) {
        Admin admin;
        Optional optionalAdmin = adminRepository.findById(admin_id);
        if (optionalAdmin.isPresent()) {
            admin = (Admin) optionalAdmin.get();
        } else {
            throw new RuntimeException("Admin not found");
        }

        Availability availability = new Availability();
        availability.setHourStart(startHour);
        availability.setHourEnd(endHour);
        availability.setWeekday(weekday);
        availabilityRepository.save(availability);

        admin.getAvailability().add(availability);
    }

    public void putTopic(String course_id, String topicName) {}
}
