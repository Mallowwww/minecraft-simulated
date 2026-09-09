package com.mallowwww.minecraftsimulated.entity;

import com.mallowwww.minecraftsimulated.ModEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.physics.object.box.BoxHandle;
import dev.ryanhcode.sable.api.physics.object.box.BoxPhysicsObject;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.mixinterface.block_properties.BlockStateExtension;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyTypes;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.CrashReportCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.Set;

public class FallingBlockPhysicsEntity extends Entity
{
    private static final Logger LOGGER = LogUtils.getLogger();

    private RigidBodyHandle bodyHandle;
    private BoxHandle boxHandle;

    private BlockState blockState = Blocks.SAND.defaultBlockState();
    public int idleTime;
    @Nullable
    public CompoundTag blockData;
    public boolean forceTickAfterTeleportToDuplicate;

    public FallingBlockPhysicsEntity(EntityType<? extends FallingBlockPhysicsEntity> entityType, Level level, BlockState state, BlockPos pos)
    {
        super(entityType, level);
        if (!(level instanceof ServerLevel sLevel)) return;

        var pose = new Pose3d(new Vector3d(pos.getX(), pos.getY(), pos.getZ()), new Quaterniond(), new Vector3d(), new Vector3d(1));
        var ext = new Vector3d(0.5);

        blockState =
                state.hasProperty(BlockStateProperties.WATERLOGGED) ?
                        state.setValue(BlockStateProperties.WATERLOGGED, Boolean.FALSE) : state;

        var box = new BoxPhysicsObject(pose, ext, ((BlockStateExtension) blockState).sable$getProperty(PhysicsBlockPropertyTypes.MASS.get()));

        boxHandle = SubLevelPhysicsSystem.require(sLevel).getPipeline().addBox(box);
        bodyHandle = RigidBodyHandle.of(sLevel, box);

        level.setBlock(pos, blockState.getFluidState().createLegacyBlock(), 3);
        //level.addFreshEntity(this);

    }
    public FallingBlockPhysicsEntity(EntityType<? extends FallingBlockPhysicsEntity> entityType, Level level) {
        super(entityType, level);
        // This constructor is for reconstruction from a packet, so the relevant physics setup will happen in recreateFromPacket()
    }

    @Override
    protected Entity.@NotNull MovementEmission getMovementEmission()
    {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public boolean isPickable()
    {
        return !this.isRemoved();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
    }

    @Override
    public boolean teleportTo(ServerLevel level, double x, double y, double z, Set<RelativeMovement> relativeMovements, float yRot, float xRot) {
        //Fix teleports not bringing the physics object with the entity, since the entity follows the physics object
        //Needed for /summon to work
        bodyHandle.teleport(new Vector3d(x, y, z), new Quaterniond().rotateY(yRot).rotateX(xRot));

        return super.teleportTo(level, x, y, z, relativeMovements, yRot, xRot);

    }

    @Override
    public void tick()
    {
        if (this.blockState.isAir())
        {
            this.discard();
            return;
        }

        this.handlePortal();
        if (this.level().isClientSide || (!this.isAlive() && !this.forceTickAfterTeleportToDuplicate)) return;

        var readPose = new Pose3d();
        boxHandle.readPose(readPose);

        this.setPos(readPose.position().x, readPose.position().y, readPose.position().z);
        if (!bodyHandle.isValid()) return;
        if (bodyHandle.getLinearVelocity().length() < 0.1)
        {
            idleTime++;

            BlockPos pos = this.blockPosition();

            //concrete can harden on the surface
            boolean canHarden =
                    this.blockState.getBlock() instanceof ConcretePowderBlock &&
                            this.blockState.canBeHydrated(this.level(), pos, this.level().getFluidState(pos), pos);

            //if we are slow or in water as concrete
            if (idleTime >= 20 || canHarden)
            {
                var block = this.blockState.getBlock();

                var stateToPlace = this.blockState;
                // Unless we do access configuration BS, we should just let it handle how it wants to place
                if(canHarden)
                    stateToPlace = ((ConcretePowderBlock)block).getStateForPlacement(new BlockPlaceContext(
                            this.level(), null, InteractionHand.MAIN_HAND, ItemStack.EMPTY, new BlockHitResult(pos.getCenter(), Direction.DOWN, pos, false)
                    ));

                if(stateToPlace.hasProperty(BlockStateProperties.WATERLOGGED)
                        && this.level().getFluidState(pos).getType() == Fluids.WATER)
                            stateToPlace = stateToPlace.setValue(BlockStateProperties.WATERLOGGED, Boolean.TRUE);

                //try place in world, otherwise wait and let the time tick up
                if (this.level().setBlock(pos, stateToPlace, 3))
                {
                    ((ServerLevel) this.level())
                            .getChunkSource()
                            .chunkMap
                            .broadcast(this, new ClientboundBlockUpdatePacket(pos, this.level().getBlockState(pos)));

                    this.discard();//kill ourselves

                    //handle block entity data
                    if (this.blockData != null && this.blockState.hasBlockEntity())
                    {
                        BlockEntity newEntity = this.level().getBlockEntity(pos);

                        //is there a better way to do this shit? this is vanilla
                        if (newEntity != null)
                        {
                            CompoundTag newData = newEntity.saveWithoutMetadata(this.level().registryAccess());

                            //copy data
                            for (String s : this.blockData.getAllKeys())
                                newData.put(s, this.blockData.get(s).copy());

                            try
                            {
                                newEntity.loadWithComponents(newData, this.level().registryAccess());
                            }
                            catch (Exception exception)
                            {
                                LOGGER.error("Failed to load block entity from falling block", exception);
                            }

                            newEntity.setChanged();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound)
    {
        compound.put("BlockState", NbtUtils.writeBlockState(this.blockState));
        compound.putInt("Time", this.idleTime);
        if (this.blockData != null)
            compound.put("TileEntityData", this.blockData);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    protected void readAdditionalSaveData(CompoundTag compound)
    {
        this.blockState = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), compound.getCompound("BlockState"));
        this.idleTime = compound.getInt("Time");

        if (compound.contains("TileEntityData", 10))
            this.blockData = compound.getCompound("TileEntityData").copy();

        if (this.blockState.isAir())
            this.blockState = Blocks.SAND.defaultBlockState();
    }

    @Override
    public boolean displayFireAnimation()
    {
        return false;
    }

    @Override
    public void fillCrashReportCategory(CrashReportCategory category)
    {
        super.fillCrashReportCategory(category);
        category.setDetail("Physics Block Imitating BlockState", this.blockState.toString());
    }

    public BlockState getBlockState()
    {
        return this.blockState;
    }

    @Override
    protected Component getTypeName()
    {
        return Component.translatable("entity.minecraftsimulated.falling_block_type", this.blockState.getBlock().getName());
    }

    @Override
    public boolean onlyOpCanSetNbt()
    {
        return true;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity)
    {
        return new ClientboundAddEntityPacket(this, entity, Block.getId(this.getBlockState()));
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet)
    {
        super.recreateFromPacket(packet);
        this.blockState = Block.stateById(packet.getData());
        this.blocksBuilding = true;
        this.setPos(packet.getX(), packet.getY(), packet.getZ());
    }

    @Nullable
    @Override
    public Entity changeDimension(DimensionTransition transition)
    {
        ResourceKey<Level> toDim = transition.newLevel().dimension();
        ResourceKey<Level> fromDim = this.level().dimension();

        boolean endInvolved = (fromDim == Level.END || toDim == Level.END) && fromDim != toDim;
        Entity entity = super.changeDimension(transition);
        this.forceTickAfterTeleportToDuplicate = entity != null && endInvolved;
        return entity;
    }
    @EventBusSubscriber
    public static class Renderer extends EntityRenderer<FallingBlockPhysicsEntity> {
        private final BlockRenderDispatcher dispatcher;
        protected Renderer(EntityRendererProvider.Context context) {
            super(context);
            dispatcher = context.getBlockRenderDispatcher();
        }

        @Override
        public ResourceLocation getTextureLocation(FallingBlockPhysicsEntity entity) {
            return ResourceLocation.parse("minecraft:block/dirt.png");
        }

        @Override
        public void render(FallingBlockPhysicsEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            var model = this.dispatcher.getBlockModel(entity.blockState);
            var modelData = model.getModelData(
                    entity.level(),
                    BlockPos.containing(entity.position()),
                    entity.blockState,
                    ModelData.EMPTY
            );
            var renderType = RenderTypeHelper.getMovingBlockRenderType(model.getRenderTypes(
                    entity.blockState, entity.random, modelData
            ).asList().getFirst());
            this.dispatcher.getModelRenderer().tesselateBlock(
                    entity.level(),
                    model,
                    entity.blockState,
                    BlockPos.containing(entity.position()),
                    poseStack,
                    bufferSource.getBuffer(renderType),
                    false,
                    entity.random,
                    0L,
                    0xFFFFFFFF,
                    modelData,
                    renderType
            );
            Minecraft.getInstance().debugRenderer.collisionBoxRenderer.render(
                    poseStack, bufferSource, 0, 0, 0
            );
        }

        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(
                    ModEntities.FALLING_BLOCK_PHYSICS_ENTITY.get(),
                    Renderer::new
            );
        }
    }
}
