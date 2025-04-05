package com.baranova.tg_service.commands;

import com.baranova.tg_service.entity.Sendable;

public interface CommandInterface {
    public Sendable execute();
}
