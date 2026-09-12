package com.mallowwww.minecraft_simulated.items;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class MalletItem extends TieredItem
{
    public MalletItem(Tier tier, Properties properties)
    {
        super(tier, properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context)
    {
        if(context.getLevel().isClientSide()) return super.useOn(context);

        if(!SableCompanion.INSTANCE.isInPlotGrid(context.getLevel(), context.getClickedPos()))
        {
            SubLevelAssemblyHelper.assembleBlocks((ServerLevel) context.getLevel(), context.getClickedPos(), Collections.singleton(context.getClickedPos()), new BoundingBox3i(0,0,0,1,1,1));
            return InteractionResult.SUCCESS;
        }

        return super.useOn(context);
    }

    public static ItemAttributeModifiers createAttributes(Tier tier, float attackDamage, float attackSpeed)
    {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage + tier.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }
}
