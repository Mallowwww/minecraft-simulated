package com.mallowwww.minecraft_simulated;

import com.mallowwww.minecraft_simulated.registry.ModCommands;
import com.mallowwww.minecraft_simulated.registry.ModComponents;
import com.mallowwww.minecraft_simulated.registry.ModItems;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MinecraftSimulated.MOD_ID)
public class MinecraftSimulated
{
    public static final String MOD_ID = "minecraft_simulated";
    public static final NonNullSupplier<Registrate> REGISTRATE = NonNullSupplier.lazy(() -> Registrate.create(MOD_ID));
    public static final Logger LOGGER = LogUtils.getLogger();
    public MinecraftSimulated(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
        REGISTRATE.get().defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

        ModItems.register();
        ModComponents.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static ResourceLocation loc(String s) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, s);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}
