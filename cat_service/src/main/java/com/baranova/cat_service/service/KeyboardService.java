package com.baranova.cat_service.service;

import java.util.LinkedHashMap;

import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class KeyboardService {
    public Map<String, String> makeKeyBoardFromList(List<String> keyValueList) {
        if (keyValueList.size() % 2 != 0) {
            throw new IllegalArgumentException("The list must contain an even number of elements (key-value pairs).");
        }
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        for (int i = 0; i < keyValueList.size(); i += 2) {
            buttons.put(keyValueList.get(i), keyValueList.get(i + 1));
        }
        return buttons;
    }

}
