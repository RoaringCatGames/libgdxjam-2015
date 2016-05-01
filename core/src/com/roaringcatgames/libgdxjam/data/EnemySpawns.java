package com.roaringcatgames.libgdxjam.data;

import com.badlogic.gdx.utils.Array;
import com.roaringcatgames.libgdxjam.components.EnemyType;

/**
 * Created by barry on 5/1/16 @ 3:49 PM.
 */
public class EnemySpawns {

    private static Array<EnemySpawn> levelOneSpawns;
    public static Array<EnemySpawn> getLevelOneSpawns(){
        if(levelOneSpawns == null){
            levelOneSpawns = new Array<>();

            for(int i=0;i<360;i++){
                if(i >= 20 && i%20 == 0){
                    levelOneSpawns.add(new EnemySpawn((i * 6f) + 1f, EnemyType.ASTEROID_C, 25f, 25f, -2f, -3f));
                    levelOneSpawns.add(new EnemySpawn((i * 6f) + 5f, EnemyType.ASTEROID_C, -5f, 25f, 2f, -3f));
                }else if(i >= 8 && i%8 == 0){
                    levelOneSpawns.add(new EnemySpawn((i * 6f) + 1f, EnemyType.ASTEROID_B, 25f, 25f, -2f, -3f));
                    levelOneSpawns.add(new EnemySpawn((i * 6f) + 5f, EnemyType.ASTEROID_B, -5f, 25f, 2f, -3f));
                }else {
                    levelOneSpawns.add(new EnemySpawn((i * 6f) + 1f, EnemyType.ASTEROID_A, 25f, 25f, -2f, -3f));
                    levelOneSpawns.add(new EnemySpawn((i * 6f) + 5f, EnemyType.ASTEROID_A, -5f, 25f, 2f, -3f));
                }
            }
//            levelOneSpawns.add(new EnemySpawn(3f, EnemyType.ASTEROID_A, 25f, 25f, -2f, -3f));
//            levelOneSpawns.add(new EnemySpawn(5f, EnemyType.ASTEROID_A, -5f, 25f, 2f, 3f));
//            levelOneSpawns.add(new EnemySpawn(3f, EnemyType.ASTEROID_A, 25f, 25f, -2f, -3f));
//            levelOneSpawns.add(new EnemySpawn(3f, EnemyType.ASTEROID_A, 25f, 25f, -2f, -3f));
//            levelOneSpawns.add(new EnemySpawn(3f, EnemyType.ASTEROID_A, 25f, 25f, -2f, -3f));
//            levelOneSpawns.add(new EnemySpawn(3f, EnemyType.ASTEROID_A, 25f, 25f, -2f, -3f));
//            levelOneSpawns.add(new EnemySpawn(3f, EnemyType.ASTEROID_A, 25f, 25f, -2f, -3f));
//            levelOneSpawns.add(new EnemySpawn(3f, EnemyType.ASTEROID_A, 25f, 25f, -2f, -3f));
//            levelOneSpawns.add(new EnemySpawn(3f, EnemyType.ASTEROID_A, 25f, 25f, -2f, -3f));
//            levelOneSpawns.add(new EnemySpawn(3f, EnemyType.ASTEROID_A, 25f, 25f, -2f, -3f));
//            levelOneSpawns.add(new EnemySpawn(3f, EnemyType.ASTEROID_A, 25f, 25f, -2f, -3f));
//            levelOneSpawns.add(new EnemySpawn(3f, EnemyType.ASTEROID_A, 25f, 25f, -2f, -3f));
//            levelOneSpawns.add(new EnemySpawn(3f, EnemyType.ASTEROID_A, 25f, 25f, -2f, -3f));
//            levelOneSpawns.add(new EnemySpawn(3f, EnemyType.ASTEROID_A, 25f, 25f, -2f, -3f));
//            levelOneSpawns.add(new EnemySpawn(3f, EnemyType.ASTEROID_A, 25f, 25f, -2f, -3f));
//            levelOneSpawns.add(new EnemySpawn(3f, EnemyType.ASTEROID_A, 25f, 25f, -2f, -3f));
        }

        return levelOneSpawns;
    }
}
