package com.roaringcatgames.libgdxjam.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.roaringcatgames.kitten2d.ashley.K2ComponentMappers;
import com.roaringcatgames.kitten2d.ashley.VectorUtils;
import com.roaringcatgames.kitten2d.ashley.components.*;
import com.roaringcatgames.libgdxjam.Animations;
import com.roaringcatgames.libgdxjam.Assets;
import com.roaringcatgames.libgdxjam.components.*;
import com.roaringcatgames.libgdxjam.values.Z;

import java.util.Random;

/**
 * System to apply enemy damage of bullets
 */
public class EnemyDamageSystem extends IteratingSystem {

    private Array<Entity> bullets = new Array<>();
    private Array<Entity> enemies = new Array<>();

    private ScoreComponent scoreCard;

    Random r = new Random();

    public EnemyDamageSystem(){
        super(Family.one(BulletComponent.class, EnemyComponent.class).get());
    }


    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        ImmutableArray<Entity> scores = getEngine().getEntitiesFor(Family.all(ScoreComponent.class).get());
        if(scores != null && scores.size() > 0){
            scoreCard = scores.first().getComponent(ScoreComponent.class);
        }

        for(Entity bullet:bullets){
            CircleBoundsComponent bb = K2ComponentMappers.circleBounds.get(bullet);
            for(Entity enemy:enemies){
                if(enemy.isScheduledForRemoval()){
                    continue;
                }
                if(K2ComponentMappers.bounds.has(enemy)) {
                    BoundsComponent eb = K2ComponentMappers.bounds.get(enemy);
                    if (Intersector.overlaps(bb.circle, eb.bounds)) {
                        getEngine().removeEntity(bullet);
                        getEngine().removeEntity(enemy);
                    }
                }else if(K2ComponentMappers.circleBounds.has(enemy)){
                    CircleBoundsComponent cb = K2ComponentMappers.circleBounds.get(enemy);
                    if (cb.circle.overlaps(bb.circle)) {
                        processCollision(bullet, enemy);
                    }
                }
            }
        }

        enemies.clear();
        bullets.clear();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        if(!entity.isScheduledForRemoval()) {
            if (Mappers.bullet.has(entity)) {
                bullets.add(entity);
            } else {
               enemies.add(entity);
            }
        }
    }


    private Vector2 bulletPos = new Vector2();
    private Vector2 enemyPos = new Vector2();
    private void processCollision(Entity bullet, Entity enemy){

        EnemyComponent ec = Mappers.enemy.get(enemy);
        HealthComponent hc;
        //int scoredPoints = 0;

        hc = K2ComponentMappers.health.get(enemy);
        if(hc.health > 0f) {
            EnemyDamageUtil.attachPlant((PooledEngine)getEngine(), bullet, enemy, ec.enemyType == EnemyType.COMET);
            if (scoreCard != null) {
                scoreCard.setScore(scoreCard.score + 1);
            }

            float startHealth = hc.health;
            applyHealthChange(bullet, hc);


            if (startHealth > 0f && hc.health <= 0f) {
                EnemyDamageUtil.processEnemyDefeated(enemy, (PooledEngine)getEngine());
//                TransformComponent enemyTfm = tm.get(enemy);
//                switch (ec.enemyType) {
//                    case ASTEROID_FRAG:
//                    case COMET:
//                        if(stm.has(enemy)){
//                            StateComponent sc = stm.get(enemy);
//                            sc.setLooping(false);
//                            sc.set("FULL");
//                        }
//                        if(pfm.has(enemy)){
//                            PathFollowComponent pfc = pfm.get(enemy);
//                            pfc.setSpeed(pfc.speed/2f);
//                            pfc.setFacingPath(false);
//                        }else if (vm.has(enemy)) {
//                            VelocityComponent vc = vm.get(enemy);
//                            vc.speed.scl(0.5f);
//                        }
//
//                        float rotR = r.nextFloat();
//                        float rotSpeed = (20f*rotR) + 20f;
//                        if(rotR >0.5f){
//                            rotSpeed *= -1f;
//                        }
//                        enemy.add(RotationComponent.create(getEngine())
//                            .setRotationSpeed(rotSpeed));
//                        break;
//                    case ASTEROID_A:
//                        attachTreeCover(enemy, Animations.getAsteroidA());
//                        generateUpgradePowerUp(enemyTfm.position.x, enemyTfm.position.y);
//                        if(r.nextFloat() < 0.2f) {
//                            generateHealthPack(enemyTfm.position.x, enemyTfm.position.y, HealthPackType.WATER_CAN);
//                        }
//                        break;
//                    case ASTEROID_B:
//                        attachTreeCover(enemy, Animations.getAsteroidB());
//                        if(r.nextFloat() < 0.33f){
//                            generateHealthPack(enemyTfm.position.x, enemyTfm.position.y, HealthPackType.WATER_CAN);
//                        }
//                        break;
//                    case ASTEROID_C:
//                        attachTreeCover(enemy, Animations.getAsteroidC());
//                        generateHealthPack(enemyTfm.position.x, enemyTfm.position.y, HealthPackType.FERTILIZER);
//                        break;
//                    default:
//                        Gdx.app.log("EnemyType", "EnemyType:" + ec.enemyType);
//                        break;
//                }
//
//                //ec.setDamaging(false);
//                if (sm.has(enemy)) {
//                    sm.get(enemy).setPaused(true);
//                    if (vm.has(enemy)) {
//                        VelocityComponent vc = vm.get(enemy);
//                        vc.speed.scl(1.6f);
//                    }else{
//                        Gdx.app.log("EnemyDamageSystem", "Enemy doesn't have Velocity!");
//                    }
//                    popSfx.play(Volume.POP_SFX);
//                }
//
//                enemy.add(TweenComponent.create(getEngine())
//                        .addTween(Tween.to(enemy, K2EntityTweenAccessor.COLOR, 2f)
//                                .target(Colors.PLANTED_GREEN.r, Colors.PLANTED_GREEN.g, Colors.PLANTED_GREEN.b)
//                                .ease(TweenEquations.easeInOutSine)));

            }

            getEngine().removeEntity(bullet);
        }
    }

//    private void generateHealthPack(float x, float y, HealthPackType hType) {
//        PooledEngine engine = (PooledEngine) getEngine();
//        Entity healthPack = engine.createEntity();
//        healthPack.add(TransformComponent.create(engine)
//            .setPosition(x, y, Z.healthPack)
//            .setScale(1f, 1f));
//        healthPack.add(RotationComponent.create(engine)
//            .setRotationSpeed(-60f));
//        healthPack.add(TweenComponent.create(engine)
//            .addTween(Tween.to(healthPack, K2EntityTweenAccessor.SCALE, 0.5f)
//                    .target(1.25f, 1.25f)
//                    .ease(TweenEquations.easeInOutBounce)
//                    .repeatYoyo(Tween.INFINITY, 0)));
//        healthPack.add(VelocityComponent.create(engine)
//            .setSpeed(0f, healthPackSpeed));
//        healthPack.add(TextureComponent.create(engine));
//        Animation ani;
//        TextureRegion glowRegion;
//        float health;
//        if(hType == HealthPackType.FERTILIZER){
//            ani = Animations.getHealthFertilizer();
//            glowRegion = Assets.getFertilizerGlow();
//            health = Health.HealthPackFertilizer;
//        }else{
//            ani = Animations.getHealthWaterCan();
//            glowRegion = Assets.getWaterCanGlow();
//            health = Health.HealthPackWaterCan;
//        }
//        healthPack.add(AnimationComponent.create(engine)
//            .addAnimation("DEFAULT", ani));
//        healthPack.add(StateComponent.create(engine)
//            .set("DEFAULT")
//            .setLooping(true));
//        healthPack.add(HealthPackComponent.create(engine)
//            .setHealth(health)
//            .setInstant(true));
//        healthPack.add(BoundsComponent.create(engine)
//                .setBounds(0f, 0f, 1f, 1f));
//        engine.addEntity(healthPack);
//
//        Entity glow = engine.createEntity();
//        glow.add(TransformComponent.create(engine)
//            .setPosition(x, y, Z.healthGlow));
//        glow.add(TextureComponent.create(engine)
//            .setRegion(glowRegion));
//        glow.add(FollowerComponent.create(engine)
//            .setTarget(healthPack));
//        glow.add(TweenComponent.create(engine)
//                .addTween(Tween.to(glow, K2EntityTweenAccessor.COLOR, 0.5f)
//                        .target(Colors.GLOW_YELLOW.r, Colors.GLOW_YELLOW.g, Colors.GLOW_YELLOW.b)
//                        .ease(TweenEquations.easeInOutBounce)
//                        .repeatYoyo(Tween.INFINITY, 0))
//                .addTween(Tween.to(glow, K2EntityTweenAccessor.SCALE, 0.5f)
//                        .target(1.25f, 1.25f)
//                        .ease(TweenEquations.easeInOutBounce)
//                        .repeatYoyo(Tween.INFINITY, 0)));
//        engine.addEntity(glow);
//    }
//
//    private void generateUpgradePowerUp(float x, float y){
//
//        PooledEngine engine = (PooledEngine) getEngine();
//        Entity powerUp = engine.createEntity();
//        powerUp.add(TransformComponent.create(engine)
//                .setPosition(x, y, Z.powerUp)
//                .setScale(1f, 1f));
//        powerUp.add(TweenComponent.create(engine)
//                .addTween(Tween.to(powerUp, K2EntityTweenAccessor.SCALE, 0.5f)
//                        .target(1.25f, 1.25f)
//                        .ease(TweenEquations.easeInOutBounce)
//                        .repeatYoyo(Tween.INFINITY, 0)));
//        powerUp.add(VelocityComponent.create(engine)
//                .setSpeed(0f, healthPackSpeed));
//        powerUp.add(TextureComponent.create(engine));
//
//        powerUp.add(AnimationComponent.create(engine)
//                .addAnimation("DEFAULT", Animations.getUpgrade()));
//        powerUp.add(StateComponent.create(engine)
//                .set("DEFAULT")
//                .setLooping(true));
//        powerUp.add(PowerUpComponent.create(engine)
//                .setPowerUpType(PowerUpComponent.PowerUpType.UPGRADE));
//        powerUp.add(BoundsComponent.create(engine)
//                .setBounds(0f, 0f, 1f, 1f));
//        engine.addEntity(powerUp);
//    }
//
//    private void attachTreeCover(Entity enemy, Animation ani) {
//
//        PooledEngine engine = (PooledEngine) getEngine();
//        TransformComponent tc = tm.get(enemy);
//
//        Entity treeCover = engine.createEntity();
//        treeCover.add(TransformComponent.create(engine)
//                .setPosition(tc.position.x, tc.position.y, Z.treeCover)
//                .setRotation(tc.rotation));
//        treeCover.add(TextureComponent.create(engine));
//        treeCover.add(StateComponent.create(engine)
//                .set("DEFAULT")
//                .setLooping(false));
//
//        treeCover.add(FollowerComponent.create(engine)
//                .setOffset(0f, 0f)
//                .setTarget(enemy)
//                .setBaseRotation(0f));
//
//        treeCover.add(AnimationComponent.create(engine)
//            .addAnimation("DEFAULT", ani));
//        getEngine().addEntity(treeCover);
//    }

    private void applyHealthChange(Entity bullet, HealthComponent hc) {
        DamageComponent dmg = K2ComponentMappers.damage.get(bullet);
        hc.health = Math.max(0f, (hc.health - dmg.dps));
    }

//    private void attachPlant(Entity bullet, Entity enemy, boolean isComet) {
//
//        PooledEngine engine = (PooledEngine)getEngine();
//
//        CircleBoundsComponent bb = K2ComponentMappers.circleBounds.get(bullet);
//        CircleBoundsComponent eb = K2ComponentMappers.circleBounds.get(enemy);
//        TransformComponent et = K2ComponentMappers.transform.get(enemy);
//        bulletPos.set(bb.circle.x, bb.circle.y);
//        enemyPos.set(eb.circle.x, eb.circle.y);
//
//        Vector2 outVec;
//        if(isComet) {
//            outVec = enemyPos.cpy();
//        }else {
//            outVec = bulletPos.sub(enemyPos).nor();
//            outVec = outVec.scl(eb.circle.radius);
//        }
//
//        Vector2 offsetVec;
//        float baseRotation;
//        if(isComet){
//            offsetVec = eb.offset.cpy().add(0.1f, -0.5f);
//        } else{
//            offsetVec =VectorUtils.rotateVector(outVec.cpy(), -et.rotation).add(eb.offset);
//        }
//        baseRotation = offsetVec.angle() - 90f;
//
//
//        Entity plant = (engine).createEntity();
//        plant.add(TransformComponent.create(engine)
//                .setPosition(outVec.x, outVec.y, Z.plant)
//                .setRotation(et.rotation + baseRotation)
//                .setScale(1f, 1f));
//
//        plant.add(TextureComponent.create(engine));
//        plant.add(StateComponent.create(engine)
//                .set("DEFAULT")
//                .setLooping(false));
//        float rnd = r.nextFloat();
//        Animation ani = rnd < 0.3f ? Animations.getGreenTree() :
//                        rnd < 0.6f ? Animations.getPinkTree() :
//                                     Animations.getPineTree();
//        plant.add(AnimationComponent.create(engine)
//                .addAnimation("DEFAULT", ani));
//        plant.add(FollowerComponent.create(engine)
//                .setOffset(offsetVec.x, offsetVec.y)
//                .setTarget(enemy)
//                .setBaseRotation(baseRotation));
//
//        plant.add(ParticleEmitterComponent.create(engine)
//                .setParticleImages(Assets.getLeafFrames())
//                .setParticleLifespans(0.1f, 0.2f)
//                .setSpawnRate(100f)
//                .setAngleRange(0f, 360f)
//                .setParticleMinMaxScale(0.5f, 0.5f)
//                .setSpeed(2f, 3f)
//                .setParticleMinMaxScale(0.5f, 0.5f)
//                .setShouldFade(true)
//                .setZIndex(Z.leaves)
//                .setDuration(0.3f));
//
//        getEngine().addEntity(plant);
//    }
}
