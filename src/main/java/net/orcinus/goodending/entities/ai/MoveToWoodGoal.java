package net.orcinus.goodending.entities.ai;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import net.orcinus.goodending.entities.WoodpeckerEntity;
import net.orcinus.goodending.init.GoodEndingSoundEvents;

import java.util.List;

public class MoveToWoodGoal extends Goal {
    private final WoodpeckerEntity woodpecker;
    private boolean canStartPecking = false;
    private boolean hasReached = false;
    private boolean cancel = false;
    private int peckingTicks = 100;

    public MoveToWoodGoal(WoodpeckerEntity woodpecker) {
        this.woodpecker = woodpecker;
    }

    @Override
    public boolean canStart() {
        BlockPos woodPos = this.woodpecker.getWoodPos();
        if (woodPos != null) {
            List<WoodpeckerEntity> woodpeckerEntities = this.woodpecker.world.getNonSpectatingEntities(WoodpeckerEntity.class, new Box(woodPos.offset(this.woodpecker.getAttachedFace())));
            if (woodpeckerEntities.size() > 1) {
                return false;
            }
        }
        if (this.woodpecker.getAttacker() != null) {
            return false;
        }
        return this.woodpecker.getWoodPos() != null && this.woodpecker.world.getBlockState(this.woodpecker.getWoodPos()).isIn(BlockTags.LOGS) && this.woodpecker.getPeckingWoodCooldown() == 0;
    }

    //TODO: MAKE IT DISCONTINUE
    @Override
    public boolean shouldContinue() {
        BlockPos woodPos = this.woodpecker.getWoodPos();
        if (woodPos != null) {
            List<WoodpeckerEntity> woodpeckerEntities = this.woodpecker.world.getNonSpectatingEntities(WoodpeckerEntity.class, new Box(woodPos.offset(this.woodpecker.getAttachedFace())));
            if (woodpeckerEntities.size() > 1 && this.peckingTicks == 100) {
                this.cancel = true;
                this.woodpecker.setPeckingWoodCooldown(200);
                return false;
            }
        }
        if (this.woodpecker.getAttacker() != null) {
            return false;
        }
        if (this.cancel) {
            return false;
        }
        if (this.peckingTicks == 0) {
            return false;
        }
        return this.woodpecker.getWoodPos() != null && this.woodpecker.world.getBlockState(this.woodpecker.getWoodPos()).isIn(BlockTags.LOGS);
    }

    @Override
    public void stop() {
        super.stop();
        this.woodpecker.setPeckingWoodCooldown(this.cancel ? 200 : 400);
        this.woodpecker.setPose(EntityPose.FALL_FLYING);
        this.peckingTicks = 100;
    }

    @Override
    public void tick() {
        BlockPos woodPos = this.woodpecker.getWoodPos();
        if (woodPos != null) {
            Direction attachedFace = this.woodpecker.getAttachedFace();
            boolean flag = attachedFace == Direction.UP || attachedFace == Direction.DOWN;
            if (!flag) {
                Vec3d center = Vec3d.ofBottomCenter(woodPos.offset(attachedFace));
                this.woodpecker.getLookControl().lookAt(Vec3d.ofCenter(woodPos));
                if (this.woodpecker.getBlockPos().getSquaredDistance(center) <= 3D) {
                    center = Vec3d.ofBottomCenter(woodPos);
                }
                this.woodpecker.getNavigation().startMovingTo(center.getX(), center.getY(), center.getZ(), 1.2F);
                double squaredDistance = this.woodpecker.getBlockPos().getSquaredDistance(Vec3d.ofBottomCenter(woodPos));
                if (squaredDistance <= 3D) {
                    BlockHitResult blockHitResult = this.woodpecker.world.raycast(new BlockStateRaycastContext(this.woodpecker.getPos(), Vec3d.ofCenter(woodPos), state -> state.isIn(BlockTags.LOGS)));
                    this.woodpecker.setVelocity(Vec3d.ZERO);
                    this.woodpecker.getLookControl().lookAt(Vec3d.ofCenter(woodPos));
                    if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                        Direction direction = blockHitResult.getSide();
                        List<WoodpeckerEntity> woodpeckerEntities = this.woodpecker.world.getNonSpectatingEntities(WoodpeckerEntity.class, new Box(woodPos.offset(this.woodpecker.getAttachedFace())));
                        if (woodpeckerEntities.size() > 1 && this.peckingTicks > 90) {
                            this.cancel = true;
                        }
                        BlockPos pos = new BlockPos(Vec3d.ofBottomCenter(woodPos));
                        this.woodpecker.getLookControl().lookAt(Vec3d.ofCenter(pos));
                        double xPosition = pos.getX() + (attachedFace.getAxis() == Direction.Axis.Z ? 0.5D : (attachedFace == Direction.WEST ? -0.2D : 1.2D));
                        double yPosition = pos.getY() + 0.25D;
                        double zPosition = pos.getZ() + (attachedFace.getAxis() == Direction.Axis.X ? 0.5D : (attachedFace == Direction.NORTH ? -0.2D : 1.2D));
//                            if (this.woodpecker.world.isSpaceEmpty(this.woodpecker, this.woodpecker.getBoundingBox().offset(new BlockPos(xPosition, yPosition, zPosition)))) {
//                            }
                        this.woodpecker.refreshPositionAndAngles(xPosition, yPosition, zPosition, this.woodpecker.getYaw(), this.woodpecker.getPitch());
                        this.woodpecker.getNavigation().stop();
                        if (this.peckingTicks > 0) {
                            this.peckingTicks--;
                        }
                        if (this.peckingTicks % 5 == 0) {
                            this.woodpecker.setPose(EntityPose.DIGGING);
                            BlockState blockState = this.woodpecker.world.getBlockState(woodPos);
                            for (int i = 0; i < 30; ++i) {
                                double d = woodPos.getX() + 0.5D;
                                double e = woodPos.getY() + 0.5D;
                                double f = woodPos.getZ() + 0.5D;
                                ((ServerWorld)this.woodpecker.world).spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), d, e, f, 1, 0.5, 0.25, 0.5, 0.0);
                            }
                        }
                    }
                }
            }
        }
    }

}
