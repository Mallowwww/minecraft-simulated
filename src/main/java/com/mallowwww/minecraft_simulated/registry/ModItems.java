package com.mallowwww.minecraft_simulated.registry;

import com.mallowwww.minecraft_simulated.MinecraftSimulated;
import com.mallowwww.minecraft_simulated.item.MalletItem;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;

public class ModItems
{
    public static void register(){} //the gay

    public static  final RegistryEntry<Item, MalletItem> WOODEN_MALLET = MinecraftSimulated.REGISTRATE.get().item("wooden_mallet",
                    p -> new MalletItem(Tiers.WOOD, p
                            .attributes(MalletItem.createAttributes(Tiers.WOOD, 1.0F, -2.8F))
                    ))
            .tab(CreativeModeTabs.TOOLS_AND_UTILITIES)
            .register();

    public static  final RegistryEntry<Item, MalletItem> STONE_MALLET = MinecraftSimulated.REGISTRATE.get().item("stone_mallet",
                    p -> new MalletItem(Tiers.STONE, p
                            .attributes(MalletItem.createAttributes(Tiers.STONE, 1.0F, -2.8F))
                    ))
            .tab(CreativeModeTabs.TOOLS_AND_UTILITIES)
            .register();

    public static  final RegistryEntry<Item, MalletItem> IRON_MALLET = MinecraftSimulated.REGISTRATE.get().item("iron_mallet",
                    p -> new MalletItem(Tiers.IRON, p
                            .attributes(MalletItem.createAttributes(Tiers.IRON, 1.0F, -2.8F))
                    ))
            .tab(CreativeModeTabs.TOOLS_AND_UTILITIES)
            .register();

    public static  final RegistryEntry<Item, MalletItem> GOLDEN_MALLET = MinecraftSimulated.REGISTRATE.get().item("golden_mallet",
                    p -> new MalletItem(Tiers.GOLD, p
                            .attributes(MalletItem.createAttributes(Tiers.GOLD, 1.0F, -2.8F))
                    ))
            .tab(CreativeModeTabs.TOOLS_AND_UTILITIES)
            .register();

    public static  final RegistryEntry<Item, MalletItem> DIAMOND_MALLET = MinecraftSimulated.REGISTRATE.get().item("diamond_mallet",
                    p -> new MalletItem(Tiers.DIAMOND, p
                            .attributes(MalletItem.createAttributes(Tiers.DIAMOND, 1.0F, -2.8F))
                    ))
            .tab(CreativeModeTabs.TOOLS_AND_UTILITIES)
            .register();

    public static  final RegistryEntry<Item, MalletItem> NETHERITE_MALLET = MinecraftSimulated.REGISTRATE.get().item("netherite_mallet",
                    p -> new MalletItem(Tiers.NETHERITE, p
                            .attributes(MalletItem.createAttributes(Tiers.NETHERITE, 1.0F, -2.8F))
                    ))
            .tab(CreativeModeTabs.TOOLS_AND_UTILITIES)
            .register();
}
