package gg.moonflower.animationoverhaul.animations;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.animationoverhaul.AnimationOverhaulMain;
import gg.moonflower.animationoverhaul.animations.entity.LivingEntityPartAnimator;
import gg.moonflower.animationoverhaul.util.animation.BakedPose;
import gg.moonflower.animationoverhaul.util.animation.LocatorRig;
import gg.moonflower.animationoverhaul.util.data.EntityAnimationData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.UUID;

public class AnimatorDispatcher {
    public static final AnimatorDispatcher INSTANCE = new AnimatorDispatcher();

    private final HashMap<UUID, EntityAnimationData> entityAnimationDataMap = Maps.newHashMap();
    private final HashMap<UUID, BakedPose> bakedPoseMap = Maps.newHashMap();

    public AnimatorDispatcher(){
    }

    public void tickEntity(LivingEntity livingEntity, LivingEntityPartAnimator<?, ?> livingEntityPartAnimator){
        if(!entityAnimationDataMap.containsKey(livingEntity.getUUID())){
            entityAnimationDataMap.put(livingEntity.getUUID(), new EntityAnimationData());
        }
        livingEntityPartAnimator.tickMethods(livingEntity);
    }

    public <T extends LivingEntity, M extends EntityModel<T>> boolean animateEntity(T livingEntity, M entityModel, PoseStack poseStack, float partialTicks){
        if(entityAnimationDataMap.containsKey(livingEntity.getUUID())){
            if(AnimationOverhaulMain.ENTITY_ANIMATORS.contains(livingEntity.getType())){
                LivingEntityPartAnimator<T, M> livingEntityPartAnimator = (LivingEntityPartAnimator<T, M>) AnimationOverhaulMain.ENTITY_ANIMATORS.get(livingEntity.getType());
                livingEntityPartAnimator.animate(livingEntity, entityModel, poseStack, entityAnimationDataMap.get(livingEntity.getUUID()), partialTicks);
                return true;
            }
        }
        return false;
    }

    public void saveBakedPose(UUID uuid, BakedPose bakedPose){
        this.bakedPoseMap.put(uuid, bakedPose);
    }

    public BakedPose getBakedPose(UUID uuid){
        if(this.bakedPoseMap.containsKey(uuid)){
            return this.bakedPoseMap.get(uuid);
        }
        return new BakedPose();
    }

    public boolean hasAnimationData(UUID uuid){
        return this.entityAnimationDataMap.containsKey(uuid);
    }

    public EntityAnimationData getEntityAnimationData(UUID uuid){
        if(entityAnimationDataMap.containsKey(uuid)){
            return entityAnimationDataMap.get(uuid);
        }
        return new EntityAnimationData();
    }

    public <T extends Entity> EntityAnimationData getEntityAnimationData(T entity){
        return getEntityAnimationData(entity.getUUID());
    }
}
