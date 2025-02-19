package gg.moonflower.animationoverhaul.animations.entity;

import gg.moonflower.animationoverhaul.AnimationOverhaulMain;
import gg.moonflower.animationoverhaul.util.animation.Locator;
import gg.moonflower.animationoverhaul.util.animation.LocatorRig;
import gg.moonflower.animationoverhaul.util.data.EntityAnimationData;
import gg.moonflower.animationoverhaul.util.data.TimelineGroupData;
import gg.moonflower.animationoverhaul.util.time.TimerProcessor;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;

import java.util.List;

public class CreeperPartAnimator extends NPCPartAnimator<Creeper, CreeperModel<Creeper>> {

    private Locator locatorMaster;
    private Locator locatorHead;
    private Locator locatorBody;
    private Locator locatorLeftFrontLeg;
    private Locator locatorRightFrontLeg;
    private Locator locatorLeftBackLeg;
    private Locator locatorRightBackLeg;

    private List<Locator> locatorListAll;

    public CreeperPartAnimator() {
        super();
    }

    @Override
    protected void buildRig(LocatorRig locatorRig) {
        this.locatorMaster = new Locator("root");
        this.locatorHead = new Locator("head");
        this.locatorBody = new Locator("body");
        this.locatorLeftFrontLeg = new Locator("leftFrontLeg");
        this.locatorRightFrontLeg = new Locator("rightFrontLeg");
        this.locatorLeftBackLeg = new Locator("leftBackLeg");
        this.locatorRightBackLeg = new Locator("rightBackLeg");

        this.locatorListAll = List.of(locatorLeftFrontLeg, locatorRightFrontLeg, locatorLeftBackLeg, locatorRightBackLeg, locatorBody, locatorHead);

        locatorRig.addLocator(locatorMaster);
        locatorRig.addLocatorModelPart(locatorHead, "head", PartPose.offset(0.0f, 6.0f, 0.0f));
        locatorRig.addLocatorModelPart(locatorBody, "body", PartPose.offset(0.0f, 6.0f, 0.0f));
        locatorRig.addLocatorModelPart(locatorLeftFrontLeg, locatorRightFrontLeg, "left_front_leg", PartPose.offset(2.0f, 18.0f, -4.0f));
        locatorRig.addLocatorModelPart(locatorRightFrontLeg, locatorLeftFrontLeg, "right_front_leg", PartPose.offset(-2.0f, 18.0f, -4.0f));
        locatorRig.addLocatorModelPart(locatorLeftBackLeg, locatorRightBackLeg, "left_hind_leg", PartPose.offset(2.0f, 18.0f, 4.0f));
        locatorRig.addLocatorModelPart(locatorRightBackLeg, locatorLeftBackLeg, "right_hind_leg", PartPose.offset(-2.0f, 18.0f, 4.0f));
    }

    @Override
    protected ModelPart getRoot(CreeperModel<Creeper> creeperModel) {
        return creeperModel.root();
    }

    @Override
    public void tick(LivingEntity livingEntity, EntityAnimationData entityAnimationData) {
        tickBodyRotationTimersNormal(livingEntity, entityAnimationData);
        tickGeneralMovementTimers(livingEntity, entityAnimationData);
        tickHeadTimers(livingEntity, entityAnimationData);
        tickLeanTimers(entityAnimationData);
        tickWalkToStopTimer(entityAnimationData, 0.3F, 8);
        tickAggroTimers(entityAnimationData, livingEntity, TimelineGroupData.INSTANCE.get(AnimationOverhaulMain.MOD_ID, EntityType.CREEPER, "aggro_start").getFrameLength(), 10);
    }

    @Override
    protected void poseLocatorRig() {
        poseHeadRotation(this.locatorHead);
        poseLookLean(locatorListAll, getTimelineGroup(AnimationOverhaulMain.MOD_ID, "look_vertical"), getTimelineGroup(AnimationOverhaulMain.MOD_ID, "look_horizontal"));

        poseWalkCycle();
        poseWalkToStop(locatorListAll, getTimelineGroup(AnimationOverhaulMain.MOD_ID, "walk_to_stop"));

        poseAggro(locatorListAll, getTimelineGroup(AnimationOverhaulMain.MOD_ID, "aggro_loop"), getTimelineGroup(AnimationOverhaulMain.MOD_ID, "aggro_start"));
    }

    private void poseWalkCycle() {
        TimelineGroupData.TimelineGroup walkNormalTimelineGroup = getTimelineGroup(AnimationOverhaulMain.MOD_ID, "walk_normal");

        float walkNormalTimer = new TimerProcessor(getDataValue(ANIMATION_POSITION))
                .speedUp(3.5F)
                .repeat(walkNormalTimelineGroup)
                .getValue();

        float walkNormalWeight = Math.min(getDataValue(ANIMATION_SPEED) / 0.5F, 1)
                * (1 - getDataValue(ANIMATION_SPEED_Y));

        this.locatorRig.animateMultipleLocatorsAdditive(locatorListAll, walkNormalTimelineGroup, walkNormalTimer, walkNormalWeight, false);
    }
}

