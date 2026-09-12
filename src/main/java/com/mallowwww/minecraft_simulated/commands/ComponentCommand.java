package com.mallowwww.minecraft_simulated.commands;

import com.mallowwww.minecraft_simulated.api.SubLevelComponentRegistry;
import com.mallowwww.minecraft_simulated.mixin.SubLevelExtension;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.ryanhcode.sable.companion.SableCompanion;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.network.chat.Component;

public class ComponentCommand
{
    private static final SimpleCommandExceptionType ERROR_NOT_FOUND = new SimpleCommandExceptionType(Component.literal("Could not find specified component type in registry. Check spelling."));
    private static final SimpleCommandExceptionType ERROR_NO_SUBLEVEL = new SimpleCommandExceptionType(Component.literal("Target is not a sublevel."));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(
            Commands.literal("component")
                .requires(src -> src.hasPermission(2))
                .then(
                    Commands.argument("pos", BlockPosArgument.blockPos())
                        .then(
                            Commands.argument("cmp", ResourceLocationArgument.id())
                                .executes(
                                    ctx -> {
                                        var pos = BlockPosArgument.getBlockPos(ctx, "pos");
                                        var cTypeLoc = ResourceLocationArgument.getId(ctx, "cmp");
                                        var cType = SubLevelComponentRegistry.COMPONENT_TYPES.get(cTypeLoc);

                                        if(cType == null) throw ERROR_NOT_FOUND.create();
                                        if(!SableCompanion.INSTANCE.isInPlotGrid(ctx.getSource().getLevel(), pos)) throw ERROR_NO_SUBLEVEL.create();

                                        var component = cType.create();

                                        var sublevel = (SubLevelExtension) SableCompanion.INSTANCE.getContaining(ctx.getSource().getLevel(), pos);
                                        assert sublevel != null;

                                        if(!sublevel.add(component))
                                        {
                                            sublevel.remove(component);
                                        }

                                        return 1;
                                    }
                                )
                        )
                )
        );
    }
}
