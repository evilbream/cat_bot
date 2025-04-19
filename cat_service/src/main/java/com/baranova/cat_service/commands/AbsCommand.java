package com.baranova.cat_service.commands;

import com.baranova.shared.entity.Sendable;

import lombok.AllArgsConstructor;

@AllArgsConstructor
abstract class AbsCommand implements CommandInterface {
    protected Sendable sendable;

}
