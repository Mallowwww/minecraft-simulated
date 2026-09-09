package com.mallowwww.minecraftsimulated.mixin;

import com.mallowwww.minecraftsimulated.ModEntities;
import com.mallowwww.minecraftsimulated.entity.FallingBlockPhysicsEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.world.level.block.FallingBlock.isFree;

@Mixin(FallingBlock.class)
public class FallingBlockMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private static void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        ci.cancel();
        if (isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight()) {
            var fallingBlockPhysicsEntity = new FallingBlockPhysicsEntity(
                    ModEntities.FALLING_BLOCK_PHYSICS_ENTITY.get(),
                    level,
                    state,
                    pos
            );
            level.addFreshEntity(fallingBlockPhysicsEntity);
        }
    }
}
