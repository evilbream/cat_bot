package com.baranova.shared.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CatActions {
    SAVE("save"),
    REMOVE("remove"),
    DELETE("delete"),
    GET_MY_CATS("getMyCats"),
    VIEW_MY_CATS("viewMyCats"),
    VIEW_CATS("viewCats"),
    LIKE("like"),
    DISLIKE("dislike");

    private final String actionName;
}
