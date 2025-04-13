package com.baranova.cat_service.commands;

import com.baranova.cat_service.entity.Sendable;

import lombok.AllArgsConstructor;

@AllArgsConstructor
abstract class AbsCommand implements CommandInterface {
    protected Sendable sendable;

}
