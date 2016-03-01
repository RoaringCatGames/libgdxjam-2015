package com.roaringcatgames.libgdxjam.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.roaringcatgames.kitten2d.ashley.components.*;
import com.roaringcatgames.libgdxjam.App;
import com.roaringcatgames.libgdxjam.Assets;
import com.roaringcatgames.libgdxjam.values.Z;
import com.roaringcatgames.libgdxjam.components.*;

/**
 * Created by barry on 12/29/15 @ 8:07 PM.
 */
public class PlayerSystem extends IteratingSystem implements InputProcessor {

    private boolean isInitialized = false;
    private Entity player;
    private Entity flames;
    private Entity touchIndicator;
    private Vector3 initialPosition;
    private float initialScale;

    private float deceleration = 100f;
    private float maxVelocity = 25f;
    private float ACCEL_RATE = 40f;
    private float accelerationX = 0f;
    private float accelerationY = 0f;

    private Vector2 controlOrigin;

    private Vector2 idleFlameOffset = new Vector2(0f, -2.6f);
    private Vector2 flyingFlameOffset = new Vector2(0f, -3.25f);

    private ComponentMapper<VelocityComponent> vm;
    private ComponentMapper<StateComponent> sm;
    private ComponentMapper<BoundsComponent> bm;
    private ComponentMapper<TransformComponent> tm;
    private ComponentMapper<PlayerComponent> pm;

    private OrthographicCamera cam;

    public PlayerSystem(Vector3 initialPosition, float initialScale, OrthographicCamera cam){
        super(Family.all(PlayerComponent.class).get());
        this.initialPosition = initialPosition;
        this.initialScale = initialScale;
        this.vm = ComponentMapper.getFor(VelocityComponent.class);
        this.sm = ComponentMapper.getFor(StateComponent.class);
        this.bm = ComponentMapper.getFor(BoundsComponent.class);
        this.tm = ComponentMapper.getFor(TransformComponent.class);
        this.pm = ComponentMapper.getFor(PlayerComponent.class);
        this.cam = cam;

        this.controlOrigin = new Vector2();
    }

    private void init(){
        if(player == null) {

            if (getEngine() instanceof PooledEngine) {
                player = ((PooledEngine) getEngine()).createEntity();
                flames = ((PooledEngine) getEngine()).createEntity();
                touchIndicator = ((PooledEngine) getEngine()).createEntity();
            } else {
                player = new Entity();
                flames = new Entity();
                touchIndicator = new Entity();
            }

            player.add(KinematicComponent.create());
            player.add(PlayerComponent.create());
            player.add(HealthComponent.create()
                .setHealth(App.getPlayerHealth())
                .setMaxHealth(App.getPlayerHealth()));
            player.add(TransformComponent.create()
                    .setPosition(initialPosition.x, initialPosition.y, initialPosition.z)
                    .setScale(initialScale, initialScale));

            Vector2 p0 = new Vector2(0f, 0f);
            Vector2 p1 = new Vector2(0f, 25f);
            Vector2 p2 = new Vector2(20f, 30f);

            player.add(PathFollowComponent.create()
                .setFacingPath(true)
                .setTotalPathTime(10f)
                .setPath(new Bezier<>(p0, p1, p2)));

            player.add(BoundsComponent.create()
                    .setBounds(0f, 0f, 2f, 3f));

            player.add(TextureComponent.create());
            player.add(AnimationComponent.create()
                    .addAnimation("DEFAULT", new Animation(1f / 9f, Assets.getShipIdleFrames()))
                    .addAnimation("FLYING", new Animation(1f / 12f, Assets.getShipFlyingFrames()))
                    .addAnimation("FLYING_LEFT", new Animation(1f / 6f, Assets.getShipFlyingLeftFrames()))
                    .addAnimation("FLYING_RIGHT", new Animation(1f / 6f, Assets.getShipFlyingRightFrames())));
            player.add(RemainInBoundsComponent.create()
                .setMode(BoundMode.CONTAINED));
            player.add(StateComponent.create()
                .set("DEFAULT")
                .setLooping(true));

            player.add(VelocityComponent.create()
                    .setSpeed(0f, 0f));

            getEngine().addEntity(player);

            flames.add(FollowerComponent.create()
                    .setOffset(idleFlameOffset.x * initialScale, idleFlameOffset.y * initialScale)
                    .setTarget(player)
                    .setMode(FollowMode.STICKY));
            flames.add(TextureComponent.create());
            flames.add(TransformComponent.create()
                    .setPosition(initialPosition.x, initialPosition.y - ((3.25f * initialScale) * initialScale), Z.flames)
                    .setScale(initialScale, initialScale));
            flames.add(AnimationComponent.create()
                    .addAnimation("DEFAULT", new Animation(1f / 9f, Assets.getIdleFlamesFrames()))
                    .addAnimation("FLYING", new Animation(1f / 9f, Assets.getFlamesFrames())));
            flames.add(StateComponent.create()
                    .set("DEFAULT")
                    .setLooping(true));
            getEngine().addEntity(flames);

            touchIndicator.add(TextureComponent.create()
                    .setRegion(Assets.getTouchPoint()));
            touchIndicator.add(TransformComponent.create()
                .setPosition(0f, 0f, Z.touchIndicator)
                .setHidden(true));

            getEngine().addEntity(touchIndicator);


        }
        isInitialized = true;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        App.game.multiplexer.addProcessor(this);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        App.game.multiplexer.removeProcessor(this);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if(!isInitialized){
            init();
        }

        StateComponent sc = sm.get(player);
        TransformComponent tc = tm.get(player);
        tc.position.add(currentPositionChange);
//        VelocityComponent vc = vm.get(player);

//
//        float newX = vc.speed.x;
//        float newY = vc.speed.y;
//        if(accelerationX != 0f && Math.abs(newX) < maxVelocity){
//            newX = applyAcceleration(deltaTime, newX, accelerationX);
//        }else if(newX != 0f){
//            newX = applyDeceleration(deltaTime, newX);
//        }
//
//        //Y Accel
//        if(accelerationY != 0f && Math.abs(newY) < maxVelocity){
//            newY = applyAcceleration(deltaTime, newY, accelerationY);
//        }else if(newY != 0f){
//            newY = applyDeceleration(deltaTime, newY);
//        }
//
//        vc.speed.set(newX, newY);
//
        /**********************
         * Set Animation State
         **********************/
        String state = "DEFAULT";
        String flameState;
        boolean isLooping = true;
        //right
        if(currentPositionChange.x > 0f) {
            //if(accelerationX > 0f){
            state = "FLYING_RIGHT";
            isLooping = false;
        }else if(currentPositionChange.x < 0f) {
            //}else if(accelerationX < 0f){
            state = "FLYING_LEFT";
            isLooping = false;
        }else if(currentPositionChange.y != 0f){
        //}else if(accelerationY != 0f){
            state = "FLYING";
        }

        FollowerComponent fc = flames.getComponent(FollowerComponent.class);
        if(state != "DEFAULT"){
            flameState = "FLYING";
            fc.setOffset(flyingFlameOffset.x*initialScale, flyingFlameOffset.y * initialScale);
        }else{
            flameState = "DEFAULT";
            fc.setOffset(idleFlameOffset.x * initialScale, idleFlameOffset.y * initialScale);
        }

        if(sc.get() != state) {
            sc.set(state).setLooping(isLooping);
            StateComponent fsc = sm.get(flames);
            if(fsc.get() != flameState){
                fsc.set(flameState);
            }
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.RIGHT || keycode == Input.Keys.D){
            currentPositionChange.add(0.5f, 0f, 0f);
        }else if(keycode == Input.Keys.LEFT || keycode == Input.Keys.A){
            currentPositionChange.add(-0.5f, 0f, 0f);
        }

        if(keycode == Input.Keys.UP || keycode == Input.Keys.W){
            currentPositionChange.add(0f, 0.5f, 0f);
        }else if(keycode == Input.Keys.DOWN || keycode == Input.Keys.S){
            currentPositionChange.add(0f, -0.5f, 0f);
        }
        return false;
    }



    @Override
    public boolean keyUp(int keycode) {
        if(!Gdx.input.isKeyPressed(Input.Keys.RIGHT) ||
            !Gdx.input.isKeyPressed(Input.Keys.D) ||
            !Gdx.input.isKeyPressed(Input.Keys.LEFT) ||
            !Gdx.input.isKeyPressed(Input.Keys.A)
         ) {
            if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D ||
                    keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
                currentPositionChange.set(0f, currentPositionChange.y, currentPositionChange.z);
            }
        }

        if(!Gdx.input.isKeyPressed(Input.Keys.DOWN) ||
           !Gdx.input.isKeyPressed(Input.Keys.S) ||
           !Gdx.input.isKeyPressed(Input.Keys.UP) ||
           !Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (keycode == Input.Keys.UP || keycode == Input.Keys.W ||
                    keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
                currentPositionChange.set(currentPositionChange.x, 0f, currentPositionChange.z);
            }
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    Vector3 touchPoint = new Vector3();
    Vector3 dragPoint = new Vector3();

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        touchPoint.set(screenX, screenY, 0f);
        touchPoint = cam.unproject(touchPoint);
        controlOrigin.set(touchPoint.x, touchPoint.y);
        touchIndicator.getComponent(TransformComponent.class)
            .setPosition(touchPoint.x, touchPoint.y)
            .setHidden(false);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        accelerationX = 0f;
        accelerationY = 0f;
        touchIndicator.getComponent(TransformComponent.class).isHidden = true;
        currentPositionChange.set(0f, 0f, 0f);
        return false;
    }

    Vector3 currentPositionChange = new Vector3();
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        currentPositionChange.set(screenX, screenY, 0f);
        currentPositionChange = cam.unproject(currentPositionChange);
        Vector3 newTouchPosition = currentPositionChange.cpy();

        currentPositionChange.sub(touchPoint);
        touchPoint.set(newTouchPosition);



//        dragPoint.set(screenX, screenY, 0f);
//        dragPoint = cam.unproject(dragPoint);
//
//        if(dragPoint.x > controlOrigin.x){
//            accelerationX = ACCEL_RATE;
//        }else if(dragPoint.x < controlOrigin.x){
//            accelerationX = -ACCEL_RATE;
//        }
//
//        if(dragPoint.y > controlOrigin.y){
//            accelerationY = ACCEL_RATE;
//        }else if(dragPoint.y < controlOrigin.y){
//            accelerationY = -ACCEL_RATE;
//        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }


    /************************
     * Private Methods
     ************************/
    private float applyDeceleration(float deltaTime, float inputSpeed) {
        return 0f;
//        float newSpeed;
//        boolean isReverse = inputSpeed < 0f;
//        float adjust = !isReverse ?  -deceleration *deltaTime : deceleration *deltaTime;
//        newSpeed = inputSpeed + adjust;
//        newSpeed =  isReverse ? Math.min(0f, newSpeed) : Math.max(0f, newSpeed);
//        return newSpeed;
    }

    private float applyAcceleration(float deltaTime, float inputSpeed, float acceleration) {
        return acceleration > 0f ? maxVelocity : -maxVelocity;

//        float newSpeed;
//        float adjust = acceleration *deltaTime;
//        newSpeed = inputSpeed + adjust;
//        newSpeed = newSpeed > 0 ? Math.min(maxVelocity, newSpeed) : Math.max(-maxVelocity, newSpeed);
//        return newSpeed;
    }
}
