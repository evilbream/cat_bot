package com.baranova.cat_service.commands;

import com.baranova.cat_service.entity.Sendable;

public interface CommandInterface {
    public Sendable execute();
}
