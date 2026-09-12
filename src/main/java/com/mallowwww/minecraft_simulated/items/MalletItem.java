package com.mallowwww.minecraft_simulated.items;

import com.mallowwww.minecraft_simulated.utils.SublevelUtils;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;

@EventBusSubscriber
public class MalletItem extends TieredItem
{
    public MalletItem(Tier tier, Properties properties)
    {
        super(tier, properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        SublevelUtils.assembleSingleBlock(context.getLevel(), context.getClickedPos());
        return InteractionResult.SUCCESS;
    }

    @SubscribeEvent
    public static void onAttackBlock(PlayerInteractEvent.LeftClickBlock event)
    {
        if(event.getLevel().isClientSide()) return;

        var player = event.getEntity();
        var sl = SableCompanion.INSTANCE.getContaining(event.getLevel(), event.getPos());


        var result = event.getLevel().clip(new ClipContext(player.getEyePosition(), player.getEyePosition().add(player.getLookAngle()), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, (Entity) null));

        if(sl != null)
        {
//            final Vec3 localPosition = result.getLocation();
//            final Vec3 localHeading = player.getLookAngle().normalize().scale(0.1);

            RigidBodyHandle.of((ServerSubLevel) sl).applyLinearImpulse(new Vector3d(0,10,0));
        }
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    public static ItemAttributeModifiers createAttributes(Tier tier)
    {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 1 + tier.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.8, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }
}
