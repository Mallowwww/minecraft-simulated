package com.mallowwww.minecraft_simulated.components;

import com.mallowwww.minecraft_simulated.api.component.SubLevelComponent;
import com.mallowwww.minecraft_simulated.registry.ModComponents;

public class GoUpComponent implements SubLevelComponent {
    @Override
    public int type() {
        return ModComponents.GO_UP_COMPONENT.get().id();
    }

    @Override
    public SubLevelComponent clone() {
        return new GoUpComponent();
    }
}
