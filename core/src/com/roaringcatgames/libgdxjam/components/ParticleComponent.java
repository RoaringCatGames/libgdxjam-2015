package com.roaringcatgames.libgdxjam.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by barry on 3/13/16 @ 4:30 PM.
 */
public class ParticleComponent implements Component, Pool.Poolable {

    public float lifespan = 1f;
    public float timeAlive = 0f;

    public static ParticleComponent create(PooledEngine engine){
        return engine.createComponent(ParticleComponent.class);
    }

    public ParticleComponent setLifespan(float life){
        this.lifespan = life;
        return this;
    }

    @Override
    public void reset() {
        this.lifespan = 1f;
        this.timeAlive = 0f;
    }
}