package com.onkore_backend.onkore.Util;

import java.time.LocalTime;
import java.util.List;

public class Sorters {
    public List<List<LocalTime>> sortLocalTimeArray(List<List<LocalTime>> timeArray) {
        quickSortLocalTime(timeArray, 0, timeArray.toArray().length -1);

        return timeArray;
    }

    private static void quickSortLocalTime(List<List<LocalTime>> timeArrray, int left, int right) {
        if (left < right) {
            int pivotIndex = partitionLocalTime(timeArrray, left, right);

            quickSortLocalTime(timeArrray, left, pivotIndex - 1);
            quickSortLocalTime(timeArrray, pivotIndex + 1, right);
        }
    }

    private static int partitionLocalTime(List<List<LocalTime>> timeArray, int left, int right) {
        LocalTime pivot = timeArray.get(right).get(0);
        int i = left - 1;

        for (int j = left; j < right; j++) {
            if (!timeArray.get(j).get(0).isAfter(pivot)) {
                i++;
                swap(timeArray, i, j);
            }
        }

        swap(timeArray, i + 1, right);
        return i + 1;
    }

    private static void swap(List<List<LocalTime>> timeArray, int i, int j) {
        List<LocalTime> temp = timeArray.get(i);
        timeArray.set(i, timeArray.get(j));
        timeArray.set(j, temp);
    }
}
