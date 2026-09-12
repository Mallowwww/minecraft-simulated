package com.mallowwww.minecraft_simulated.mixin;

import com.mallowwww.minecraft_simulated.api.SubLevelComponentRegistry;
import com.mallowwww.minecraft_simulated.api.component.SubLevelComponent;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.SubLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.logging.Level;

@Mixin(SubLevel.class)
public abstract class SubLevelMixin implements SubLevelAccess, SubLevelExtension {
    private int[] componentTypes = new int[32];
    public int id;
    private static int nextId = 0;
    private static final int maxId = 1023;

    private SubLevelMixin() {
        id = 0;
    }

    @Inject(method = "<init>", at=@At("TAIL"))
    public void init(Level level, int plotX, int plotY, Pose3d pose, CallbackInfo ci) {
        id = nextId++;
        if (id > maxId)
            throw new RuntimeException("Cannot have more than "+(maxId+1)+" managed sublevels!");
    }
    @Unique
    public final <T extends SubLevelComponent> boolean add(T component) {
        int type = component.type();

        if ((componentTypes[type / 32] & (1 << (type % 32))) > 0)
            return false;

        componentTypes[type / 32] |= 1 << (type % 32);
        var componentType = SubLevelComponentRegistry.ID_TO_COMPONENT_TYPE.get(type);
        componentType.addData(
                (SubLevel) (Object) this, component
        );
        componentType.apply((SubLevel) (Object) this, component);



        return true;
    }
    @Unique
    public final boolean remove(SubLevelComponent component) {
        int type = component.type();

        if ((componentTypes[type / 32] & (1 << (type % 32))) == 0)
            return false;

        componentTypes[type / 32] ^= 1 << (type % 32);

        var componentType = SubLevelComponentRegistry.ID_TO_COMPONENT_TYPE.get(type);
        componentType.removeData(
                (SubLevel) (Object) this, component
        );
        componentType.remove( (SubLevel) (Object) this, component);

        return true;
    }
    public int id() {
        return id;
    }

}
