package com.mallowwww.minecraft_simulated.api;

import com.mallowwww.minecraft_simulated.api.component.SubLevelComponent;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public abstract class ManagedSubLevel extends SubLevel {

    private int[] componentTypes = new int[32];
    public final int id;
    private static int nextId = 0;
    private static final int maxId = 2023;

    /**
     * Creates a new sub-level with the given parent level and pose.
     *
     * @param level the parent level
     * @param plotX the global plot x coordinate
     * @param plotY the global plot y coordinate
     * @param pose  the initialization pose of the sub-level
     */
    protected ManagedSubLevel(Level level, int plotX, int plotY, Pose3d pose) {
        super(level, plotX, plotY, pose);
        id = nextId++;
        if (id > maxId)
            throw new RuntimeException("Cannot have more than "+(maxId+1)+" managed sublevels!");
    }

    public final <T extends SubLevelComponent> boolean add(T component) {
        int type = component.type();

        if ((componentTypes[type / 32] & (1 << (type % 32))) > 0)
            return false;

        componentTypes[type / 32] |= 1 << (type % 32);
        var componentType = SubLevelComponentRegistry.ID_TO_COMPONENT_TYPE.get(type);
        componentType.addData(
                this, component
        );
        componentType.apply(this, component);



        return true;
    }
    public final boolean remove(SubLevelComponent component) {
        int type = component.type();

        if ((componentTypes[type / 32] & (1 << (type % 32))) == 0)
            return false;

        componentTypes[type / 32] ^= 1 << (type % 32);

        var componentType = SubLevelComponentRegistry.ID_TO_COMPONENT_TYPE.get(type);
        componentType.removeData(
                this, component
        );
        componentType.remove(this, component);

        return true;
    }


}
