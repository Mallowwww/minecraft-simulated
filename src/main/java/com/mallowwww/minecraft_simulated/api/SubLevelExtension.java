package com.mallowwww.minecraft_simulated.api;

import com.mallowwww.minecraft_simulated.api.component.SubLevelComponent;
import dev.ryanhcode.sable.sublevel.SubLevel;
import org.spongepowered.asm.mixin.Unique;

import java.util.Arrays;

public interface SubLevelExtension {
    <T extends SubLevelComponent> boolean add(T component);
    <T extends SubLevelComponent> boolean remove(T component);
    int id();
    int[] componentTypes();
    default void removeComponents() {
        var componentTypes = componentTypes();
        for (int i = 0; i < 1024; i++) {
            if ((componentTypes[i / 32] & (1 << (i % 32))) > 0) {
                var type = SubLevelComponentRegistry.ID_TO_COMPONENT_TYPE.get(i);

                type.removeData((SubLevel) this);
            }
        }
        Arrays.fill(componentTypes, 0);

    }
}
