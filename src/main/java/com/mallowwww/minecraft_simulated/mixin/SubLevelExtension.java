package com.mallowwww.minecraft_simulated.mixin;

import com.mallowwww.minecraft_simulated.api.component.SubLevelComponent;

public interface SubLevelExtension {
    <T extends SubLevelComponent> boolean add(T component);
    <T extends SubLevelComponent> boolean remove(T component);
    int id();
}
