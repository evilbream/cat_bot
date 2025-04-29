package com.baranova.shared.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Commands {
    START("comamndStart"),
    ADD_CAT_NAME("commandAddCatName"),
    ADD_CAT_PHOTO("commandAddCatPhoto"),
    MY_CATS("commandMyCats"),
    VIEW_CATS("commandViewCats");

    private final String commandName;
}
