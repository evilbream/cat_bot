package com.baranova.cat_service.enums;

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

    public static Commands fromCommandName(String commandName) {
        for (Commands command : values()) {
            if (command.getCommandName().equals(commandName)) {
                return command;
            }
        }
        return null;
    }
}
