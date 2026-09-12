package com.mallowwww.minecraft_simulated.registry;

import com.mallowwww.minecraft_simulated.api.SubLevelComponentRegistry;
import com.mallowwww.minecraft_simulated.api.component.SubLevelComponent;
import com.mallowwww.minecraft_simulated.api.component.SubLevelComponentType;
import com.mallowwww.minecraft_simulated.components.GoUpComponent;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.core.RegistryAccess;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.joml.Vector3d;

public class ModComponents {
    public static final DeferredRegister<SubLevelComponentType<?>> REGISTER = DeferredRegister.create(SubLevelComponentRegistry.COMPONENT_TYPES, "components");
    public static final DeferredHolder<SubLevelComponentType<?>, SubLevelComponentType<GoUpComponent>> GO_UP_COMPONENT = REGISTER.register("go_up",
            (resourceLocation) -> new SubLevelComponentType.Builder<GoUpComponent>()
                    .tick(((subLevel, goUpComponent) -> {
                        // TODO makes it go up ig
                        if (subLevel instanceof ServerSubLevel serverSubLevel) {
                            var handle = RigidBodyHandle.of(serverSubLevel);
                            handle.applyLinearImpulse(new Vector3d(0f, 3f, 0f));
                        }
                    }))
                    .create(GoUpComponent::new)
                    .build(resourceLocation)
    );
}
