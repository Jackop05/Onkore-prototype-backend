package com.onkore_backend.onkore.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class JsonFormatter {
    public static <T> List<T> convertStringToList(String input, Function<String, T> elementConverter) {
        List<T> resultList = new ArrayList<>();
        if (input == null || input.isEmpty()) {
            return resultList;
        }

        // Split the input string by commas and convert each element
        String[] elements = input.split(",");
        for (String element : elements) {
            resultList.add(elementConverter.apply(element.trim()));
        }

        return resultList;
    }

    public static <T> Map<T, T> convertStringToMap(String input, Function<String, T> elementConverter) {
        Map<T, T> resultList = new HashMap<>();
        if (input == null || input.isEmpty()) {
            return resultList;
        }


        String[] elements = input.split(",");
        for (String element : elements) {
            String[] keyValue = element.split(":");
            resultList.put(elementConverter.apply(keyValue[0].trim()), elementConverter.apply(keyValue[1].trim()));
        }

        return resultList;
    }
}
