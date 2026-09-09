package com.mallowwww.minecraftsimulated;

import com.mallowwww.minecraftsimulated.MinecraftSimulated;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeConfig;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = MinecraftSimulated.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = MinecraftSimulated.MODID, value = Dist.CLIENT)
public class MinecraftSimulatedClient {
    public MinecraftSimulatedClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {

    }
    @SubscribeEvent
    static void onRenderScreen(ScreenEvent.Render.Post event) {
        render(event.getGuiGraphics());
    }
    @SubscribeEvent
    static void onRenderGUI(RenderGuiEvent.Post event) {
        render(event.getGuiGraphics());
    }
    static void render(GuiGraphics gg) {
        if (FMLEnvironment.production) return;
        var s = "[Minecraft: Simulated "+versionString()+"]";
        gg.drawString(
                Minecraft.getInstance().font,
                Component.literal(s).withStyle(ChatFormatting.AQUA),
                5,
                5,
                0xFFFFFFFF
        );
    }
    public static String versionString() {
        return ModList.get().getModContainerById(MinecraftSimulated.MODID)
                .map(c -> c.getModInfo().getVersion().toString())
                .orElse("UNKNOWN");
    }
}