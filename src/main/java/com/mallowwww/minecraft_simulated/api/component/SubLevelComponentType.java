package com.mallowwww.minecraft_simulated.api.component;

import com.mallowwww.minecraft_simulated.mixin.SubLevelMixin;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import oshi.util.tuples.Pair;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class SubLevelComponentType<T extends SubLevelComponent> {
    private static int nextId = 0;
    private static final int maxId = 1023; // Needs to be pre-arranged since this is the size of the sparse array
    private final ResourceLocation location;
    private final int id;

    private final ArrayList<Pair<SubLevel, T>> DENSE = new ArrayList<>();
    private final int[] SPARSE = new int[1024];

    private SubLevelComponentType(ResourceLocation location) {
        this.location = location;
        id = nextId++;
        if (id > maxId)
            throw new RuntimeException("Max component ID reached!");
        Arrays.fill(SPARSE, -1);
    }

    public abstract void tick(SubLevel subLevel, SubLevelComponent component);
    public abstract void apply(SubLevel subLevel, SubLevelComponent component);
    public abstract void remove(SubLevel subLevel, SubLevelComponent component);

    public abstract Optional<CompoundTag> save(T component);
    public abstract Optional<T> load(CompoundTag tag);

    public abstract T create();

    public final void addData(SubLevel subLevel, SubLevelComponent component) {
        var managedSubLevel = (SubLevelMixin) (Object) subLevel;
        if (SPARSE[managedSubLevel.id] != -1)
            throw new RuntimeException("Sublevel already has this component!");
        SPARSE[managedSubLevel.id] = DENSE.size();
        DENSE.addLast(new Pair<SubLevel, T>(subLevel, (T) component));
    }
    public final void modifyData(SubLevel subLevel, SubLevelComponent component) {
        var managedSubLevel = (SubLevelMixin) (Object) subLevel;
        if (SPARSE[managedSubLevel.id] == -1)
            throw new RuntimeException("Sublevel does not have this component!");
        DENSE.set(SPARSE[managedSubLevel.id], null);
        SPARSE[managedSubLevel.id] = DENSE.size();
        DENSE.addLast(new Pair<>(subLevel, (T) component));
    }
    public final void removeData(SubLevel subLevel, SubLevelComponent component) {
        var managedSubLevel = (SubLevelMixin) (Object) subLevel;
        if (SPARSE[managedSubLevel.id] == -1)
            throw new RuntimeException("Sublevel does not have this component!");
        DENSE.set(SPARSE[managedSubLevel.id], null);
        SPARSE[managedSubLevel.id] = -1;
    }
    public final void forEach(BiConsumer<SubLevel, T> consumer) {
        DENSE.forEach(pair -> consumer.accept(pair.getA(), pair.getB()));
    }
    public final void tickAll() {
        forEach(this::tick);
    }

    public final ResourceLocation location() {
        return location;
    }
    public final int id() {
        return id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public static class Builder<T extends SubLevelComponent> {
        private BiConsumer<SubLevel, T> tick;
        private BiConsumer<SubLevel, T> apply;
        private BiConsumer<SubLevel, T> remove;

        private Function<T, CompoundTag> save;
        private Function<CompoundTag, T> load;
        private Supplier<T> create;

        public Builder() {
            tick = (a, b) -> {};
            apply = (a, b) -> {};
            remove = (a, b) -> {};

            save = null;
            load = null;
            create = null;
        }

        public Builder<T> tick(BiConsumer<SubLevel, T> consumer) {
            tick = consumer;
            return this;
        }
        public Builder<T> apply(BiConsumer<SubLevel, T> consumer) {
            apply = consumer;
            return this;
        }
        public Builder<T> remove(BiConsumer<SubLevel, T> consumer) {
            remove = consumer;
            return this;
        }

        public Builder<T> save(Function<T, CompoundTag> function) {
            save = function;
            return this;
        }
        public Builder<T> load(Function<CompoundTag, T> function) {
            load = function;
            return this;
        }
        public Builder<T> create(Supplier<T> supplier) {
            create = supplier;
            return this;
        }

        public SubLevelComponentType<T> build(ResourceLocation location) {
            if (save == null ^ load == null)
                if (save == null)
                    throw new RuntimeException("Failed to build SubLevelComponentType: Must implement save() to be serializable!");
                else
                    throw new RuntimeException("Failed to build SubLevelComponentType: Must implement load() to be serializable!");
            if (create == null)
                throw new RuntimeException("Failed to build SubLevelComponentType: Must be able to make the default component!");
            return new SubLevelComponentType<T>(location) {
                @Override
                public void tick(SubLevel subLevel, SubLevelComponent component) {
                    tick.accept(subLevel, (T) component);
                }

                @Override
                public void apply(SubLevel subLevel, SubLevelComponent component) {
                    apply.accept(subLevel, (T) component);
                }

                @Override
                public void remove(SubLevel subLevel, SubLevelComponent component) {
                    remove.accept(subLevel, (T) component);
                }

                @Override
                public Optional<CompoundTag> save(T component) {
                    if (save == null) return Optional.empty();
                    return Optional.of(save.apply(component));
                }

                @Override
                public Optional<T> load(CompoundTag tag) {
                    if (load == null) return Optional.empty();
                    return Optional.of(load.apply(tag));
                }

                @Override
                public T create() {
                    return create.get();
                }
            };
        }

    }
}
