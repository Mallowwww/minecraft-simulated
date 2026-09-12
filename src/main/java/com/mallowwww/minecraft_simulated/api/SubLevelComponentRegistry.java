package com.mallowwww.minecraft_simulated.api;

import com.mallowwww.minecraft_simulated.MinecraftSimulated;
import com.mallowwww.minecraft_simulated.api.component.SubLevelComponent;
import com.mallowwww.minecraft_simulated.api.component.SubLevelComponentType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.ArrayList;

@EventBusSubscriber
public class SubLevelComponentRegistry {
    public static final ArrayList<SubLevelComponentType<? extends SubLevelComponent>> ID_TO_COMPONENT_TYPE = new ArrayList<>();
    public static final Registry<SubLevelComponentType<? extends SubLevelComponent>> COMPONENT_TYPES = new RegistryBuilder<SubLevelComponentType<? extends SubLevelComponent>>(
            ResourceKey.createRegistryKey(MinecraftSimulated.loc("component_types"))
    ).onAdd((registry, id, key, value) -> {
        ID_TO_COMPONENT_TYPE.addLast(value);
    }).create();

    @SubscribeEvent
    public static void preTick(ServerTickEvent.Pre event) {
        COMPONENT_TYPES.forEach(SubLevelComponentType::tickAll);
    }
    @SubscribeEvent
    public static void register(NewRegistryEvent event) {
        event.register(COMPONENT_TYPES);
    }
}
