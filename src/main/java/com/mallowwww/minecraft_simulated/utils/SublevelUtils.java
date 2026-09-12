package com.mallowwww.minecraft_simulated.utils;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Collections;

public class SublevelUtils
{
    public static ServerSubLevel assembleSingleBlock(Level level, BlockPos pos)
    {
        if(level.isClientSide()) return null;
        return SubLevelAssemblyHelper.assembleBlocks(
                (ServerLevel) level,
                pos,
                Collections.singleton(pos),
                new BoundingBox3i(0,0,0,1,1,1)
        );
    }
}
