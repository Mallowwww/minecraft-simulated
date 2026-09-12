package com.mallowwww.minecraft_simulated.mixin;

import com.mallowwww.minecraft_simulated.api.SubLevelComponentRegistry;
import com.mallowwww.minecraft_simulated.api.SubLevelExtension;
import com.mallowwww.minecraft_simulated.api.component.SubLevelComponent;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.server.level.ServerLevel;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.logging.Level;
@Mixin(ServerSubLevel.class)
public abstract class ServerSubLevelMixin extends SubLevel implements SubLevelExtension {
    private int[] componentTypes = new int[32];
    public int id;
    private static int nextId = 0;
    private static final int maxId = 1023;

    private ServerSubLevelMixin() {
        super(null, 0, 0, null);
        id = 0;
    }

    @Inject(method = "<init>", at=@At("TAIL"))
    public void init(ServerLevel level, int plotX, int plotY, Pose3d pose, CallbackInfo ci) {
        id = nextId++;
        if (id > maxId)
            throw new RuntimeException("Cannot have more than "+(maxId+1)+" managed sublevels!");
    }


    @Inject(method = "tick", at=@At("TAIL"))
    public void tick(CallbackInfo ci) {
        if (this.isRemoved()) ((SubLevelExtension) this).removeComponents();
    }
    @Unique
    public int[] componentTypes() {
        return componentTypes;
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
                this
        );
        componentType.remove(this, component);

        return true;
    }
    public int id() {
        return id;
    }
}
