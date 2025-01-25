package com.onkore_backend.onkore.Util;

import com.onkore_backend.onkore.Model.Availability;

import java.time.LocalTime;

public class TimeOperator {
    public boolean isTimeInRange(Availability availability, LocalTime hourStart, LocalTime hourEnd) {
        return !availability.getHourStart().isAfter(hourStart) && !availability.getHourEnd().isBefore(hourEnd);
    }
}
