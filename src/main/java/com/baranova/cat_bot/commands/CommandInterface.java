package com.baranova.cat_bot.commands;

import com.baranova.cat_bot.entity.Sendable;

public interface CommandInterface {
    public Sendable execute();
}
