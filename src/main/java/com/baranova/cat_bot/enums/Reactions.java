package com.baranova.cat_bot.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public enum Reactions {
    LIKE("лайк", 0),
    DISLIKE("дизлайк", 1);

    private final String name;
    private final int id;
}
