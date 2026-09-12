package com.mallowwww.minecraft_simulated.registry;

import com.mallowwww.minecraft_simulated.MinecraftSimulated;
import com.mallowwww.minecraft_simulated.api.SubLevelComponentRegistry;
import com.mallowwww.minecraft_simulated.api.component.SubLevelComponent;
import com.mallowwww.minecraft_simulated.api.component.SubLevelComponentType;
import com.mallowwww.minecraft_simulated.components.GoUpComponent;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.core.RegistryAccess;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.joml.Vector3d;

public class ModComponents {
    public static final DeferredRegister<SubLevelComponentType<?>> REGISTER = DeferredRegister.create(SubLevelComponentRegistry.COMPONENT_TYPES, MinecraftSimulated.MOD_ID);
    public static final DeferredHolder<SubLevelComponentType<?>, SubLevelComponentType<GoUpComponent>> GO_UP_COMPONENT = REGISTER.register("go_up",
            (resourceLocation) -> new SubLevelComponentType.Builder<GoUpComponent>()
                    .tick(((subLevel, goUpComponent) -> {
                        // TODO makes it go up ig
                        if (subLevel instanceof ServerSubLevel serverSubLevel) {
                            var handle = RigidBodyHandle.of(serverSubLevel);
                            if (!handle.isValid()) return;
                            handle.applyLinearImpulse(new Vector3d(0f, 0.5f, 0f));
                        }
                    }))
                    .create(GoUpComponent::new)
                    .build(resourceLocation)
    );
    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
