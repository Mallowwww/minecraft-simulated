package com.mallowwww.minecraftsimulated;

import com.google.common.collect.ImmutableSet;
import com.mallowwww.minecraftsimulated.entity.FallingBlockPhysicsEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MinecraftSimulated.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<FallingBlockPhysicsEntity>> FALLING_BLOCK_PHYSICS_ENTITY = ENTITIES.register("falling_block_physics_entity",
            () -> new EntityType<FallingBlockPhysicsEntity>(
                    FallingBlockPhysicsEntity::new,
                    MobCategory.MISC,
                    true,
                    true,
                    true,
                    true,
                    ImmutableSet.of(),
                    EntityDimensions.fixed(1, 1),
                    1,
                    50,
                    1,
                    FeatureFlagSet.of()
            ));

    public static void init(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
