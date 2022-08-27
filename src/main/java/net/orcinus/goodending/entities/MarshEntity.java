package net.orcinus.goodending.entities;

import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class MarshEntity extends PathAwareEntity {
    private static final TrackedData<Integer> BURPING_TICKS = DataTracker.registerData(MarshEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private Potion potion = Potions.EMPTY;
    public AnimationState idlingAnimationState = new AnimationState();
    public AnimationState walkingAnimationState = new AnimationState();

    public MarshEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createMarshAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1f);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BURPING_TICKS, 0);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("BurpingTicks", this.getBurpingTicks());
        nbt.putString("StoredPotion", Registry.POTION.getId(this.getStoredPotion()).toString());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setBurpingTicks(nbt.getInt("BurpingTicks"));
        this.setStoredPotion(PotionUtil.getPotion(nbt));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isClient()) {
            if (this.shouldWalk()) {
                this.idlingAnimationState.stop();
                this.walkingAnimationState.startIfNotRunning(this.age);
            }
            else {
                this.walkingAnimationState.stop();
                this.idlingAnimationState.startIfNotRunning(this.age);
            }
        }
        this.setBurpingTicks(this.getBurpingTicks() - 1);
    }

    private boolean shouldWalk() {
        return this.onGround && this.getVelocity().horizontalLengthSquared() > 1.0E-6 && !this.isInsideWaterOrBubbleColumn();
    }

    public int getBurpingTicks() {
        return this.dataTracker.get(BURPING_TICKS);
    }

    public void setBurpingTicks(int burpingTicks) {
        this.dataTracker.set(BURPING_TICKS, burpingTicks);
    }

    public Potion getStoredPotion() {
        return this.potion;
    }

    public void setStoredPotion(Potion potion) {
        this.potion = potion;
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() instanceof PotionItem) {
            if (!PotionUtil.getPotion(stack).hasInstantEffect()) {
                this.setStoredPotion(PotionUtil.getPotion(stack));
                this.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 1.0F, 1.0F);
                stack.decrement(1);
                if (!player.getAbilities().creativeMode) {
                    if (stack.isEmpty()) {
                        player.setStackInHand(hand, new ItemStack(Items.GLASS_BOTTLE));
                    } else if (!player.getInventory().insertStack(new ItemStack(Items.GLASS_BOTTLE))) {
                        player.dropItem(new ItemStack(Items.GLASS_BOTTLE), false);
                    }
                }
                return ActionResult.success(world.isClient());
            }
        }
        if (stack.getItem() instanceof SwordItem && this.getStoredPotion() != null) {
            ItemStack copy = stack.copy();
            PotionUtil.setPotion(copy, this.getStoredPotion());
            player.setStackInHand(hand, copy);
            this.setStoredPotion(null);
            this.playSound(SoundEvents.ENTITY_PLAYER_BURP, 1.0F, 0.3F);
            this.setBurpingTicks(20);
            return ActionResult.success(this.world.isClient());
        }
        return super.interactMob(player, hand);
    }

}