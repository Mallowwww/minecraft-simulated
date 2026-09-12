package com.mallowwww.minecraft_simulated.registry;

import com.mallowwww.minecraft_simulated.commands.ComponentCommand;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber
public class ModCommands
{
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        ComponentCommand.register(event.getDispatcher());
    }
}
