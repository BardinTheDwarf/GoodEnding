package net.orcinus.goodending.entities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.orcinus.goodending.entities.ai.FollowMobWithEffectGoal;
import net.orcinus.goodending.entities.ai.StrangerDangerGoal;
import net.orcinus.goodending.init.GoodEndingCriteriaTriggers;
import net.orcinus.goodending.init.GoodEndingSoundEvents;
import net.orcinus.goodending.init.GoodEndingTags;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class MarshEntity extends PathfinderMob {
    private static final EntityDataAccessor<Boolean> TRUSTED = SynchedEntityData.defineId(MarshEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> BREWING = SynchedEntityData.defineId(MarshEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BURPING = SynchedEntityData.defineId(MarshEntity.class, EntityDataSerializers.INT);
    private Potion potion = Potions.EMPTY;
    private boolean infinite;

    public MarshEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TRUSTED, false);
        this.entityData.define(BREWING, 0);
        this.entityData.define(BURPING, 0);
    }

    public static AttributeSupplier.Builder createMarshAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 16.0).add(Attributes.MOVEMENT_SPEED, 0.2);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new StrangerDangerGoal<>(this, Player.class));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25f));
        this.goalSelector.addGoal(2, new FollowMobWithEffectGoal(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0f));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    public boolean isTrusted() {
        return this.entityData.get(TRUSTED);
    }

    public void setTrusted(boolean trusted) {
        this.entityData.set(TRUSTED, trusted);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.getStoredPotion() != Potions.EMPTY && this.getBrewingTicks() > 0) {
            Collection<MobEffectInstance> collection = this.getStoredPotion().getEffects();
            int i = PotionUtils.getColor(collection);

            float f1 = this.getRandom().nextFloat();
            if (f1 > 0.35f) {
                return;
            }

            double d = (double)(i >> 16 & 0xFF) / 255.0;
            double e = (double)(i >> 8 & 0xFF) / 255.0;
            double f = (double)(i & 0xFF) / 255.0;
            this.level().addParticle(ParticleTypes.ENTITY_EFFECT, this.getX(), this.getY() + 1f, this.getZ(), d, e, f);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            if (this.getBrewingTicks() > 0) {
                if (this.getBrewingTicks() == 1) {
                    this.playSound(GoodEndingSoundEvents.ENTITY_MARSH_BURP, 1.0F, 1.0F);
                }
                this.setBrewingTicks(this.getBrewingTicks() - 1);
            }
            if (this.getBurpingTicks() > 0) {
                this.setBurping(this.getBurpingTicks() - 1);
            }
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        Item item = itemStack.getItem();
        if (itemStack.is(GoodEndingTags.MARSH_TRUSTED_ITEMS) && !this.isTrusted()) {
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            this.setTrusted(true);
            this.level().broadcastEntityEvent(this, (byte) 18);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        if (item instanceof PotionItem && this.getStoredPotion() == Potions.EMPTY && this.isTrusted()) {
            if (!PotionUtils.getPotion(itemStack).hasInstantEffects()) {
                this.setInfinite(item instanceof LingeringPotionItem);
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                    if (!(item instanceof ThrowablePotionItem)) {
                        this.spawnAtLocation(Items.GLASS_BOTTLE, 1);
                    }
                }
                if (player instanceof ServerPlayer serverPlayer) {
                    GoodEndingCriteriaTriggers.BREW_POTION.trigger(serverPlayer);
                }
                this.setStoredPotion(PotionUtils.getPotion(itemStack));
                this.setBrewingTicks(1800 + this.getRandom().nextInt(1200));

                return InteractionResult.SUCCESS;
            }
        }
        if (item instanceof MilkBucketItem && this.getStoredPotion() != Potions.EMPTY && this.isTrusted()) {
            if (!player.getAbilities().instabuild) {
                player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
            }
            this.setStoredPotion(Potions.EMPTY);
            this.playSound(SoundEvents.GENERIC_DRINK, 1, 1);
            this.setBrewingTicks(0);

            return InteractionResult.SUCCESS;
        }
        if (this.getStoredPotion() != Potions.EMPTY && itemStack.getTag() != null && !itemStack.getTag().contains("Potion") && this.getValidItems(item) && this.getBrewingTicks() == 0) {
            if (itemStack.getTag() != null) {
                if (this.isInfinite()) {
                    itemStack.getTag().putBoolean("Infinite", true);
                    this.setInfinite(false);
                } else {
                    itemStack.getTag().putInt("Amount", 20);
                }
                PotionUtils.setPotion(itemStack, this.getStoredPotion());
                this.setBurping(15);
                this.playSound(GoodEndingSoundEvents.ENTITY_MARSH_BURP, 1, 1);
                this.setStoredPotion(Potions.EMPTY);
                this.setBrewingTicks(0);

                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == 42) {
            this.spawnParticles(5, ParticleTypes.SPLASH, 1.0);
        } else if (status == 18) {
            this.spawnParticles(7, ParticleTypes.HEART, 0.5);
        } else {
            super.handleEntityEvent(status);
        }
    }

    private void spawnParticles(int x, SimpleParticleType splash, double x1) {
        for (int i = 0; i < x; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.level().addParticle(splash, this.getRandomX(1.0), this.getRandomY() + x1, this.getRandomZ(1.0), d, e, f);
        }
    }

    public boolean getValidItems(Item item) {
        for (MobEffectInstance effectInstance : this.getStoredPotion().getEffects()) {
            MobEffectCategory category = effectInstance.getEffect().getCategory();
            if (category == MobEffectCategory.HARMFUL && item instanceof SwordItem) {
                return true;
            } else if (category == MobEffectCategory.BENEFICIAL && item instanceof ShieldItem) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putString("Potion", BuiltInRegistries.POTION.getKey(this.getStoredPotion()).toString());
        nbt.putBoolean("Infinite", this.isInfinite());
        nbt.putBoolean("Trusted", this.isTrusted());
        nbt.putInt("BrewingTicks", this.getBrewingTicks());
        nbt.putInt("BurpingTicks", this.getBurpingTicks());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.setInfinite(nbt.getBoolean("Infinite"));
        this.setTrusted(nbt.getBoolean("Trusted"));
        this.setStoredPotion(PotionUtils.getPotion(nbt));
        this.setBrewingTicks(nbt.getInt("BrewingTicks"));
        this.setBurping(nbt.getInt("BurpingTicks"));
    }

    public int getBurpingTicks() {
        return this.entityData.get(BURPING);
    }

    public void setBurping(int burpingTicks) {
        this.entityData.set(BURPING, burpingTicks);
    }

    public int getBrewingTicks() {
        return this.entityData.get(BREWING);
    }

    public void setBrewingTicks(int brewingTicks) {
        this.entityData.set(BREWING, brewingTicks);
    }

    public boolean isInfinite() {
        return this.infinite;
    }

    public void setInfinite(boolean infinite) {
        this.infinite = infinite;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    public Potion getStoredPotion() {
        return this.potion;
    }

    public void setStoredPotion(Potion potion) {
        this.potion = potion;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.getStoredPotion() != Potions.EMPTY) return GoodEndingSoundEvents.ENTITY_MARSH_BREWING_IDLE;
        else return GoodEndingSoundEvents.ENTITY_MARSH_IDLE;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return GoodEndingSoundEvents.ENTITY_MARSH_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return GoodEndingSoundEvents.ENTITY_MARSH_DEATH;
    }
}