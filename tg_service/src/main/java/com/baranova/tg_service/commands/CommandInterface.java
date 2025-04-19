package com.baranova.tg_service.commands;

import com.baranova.shared.entity.Sendable;

public interface CommandInterface {
    public Sendable execute();
}
