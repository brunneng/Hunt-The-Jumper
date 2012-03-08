package com.greenteam.huntjumper.parameters;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * User: GreenTea Date: 14.01.12 Time: 21:17
 */
public final class GameConstants
{
   private GameConstants()
   {
      
   }
   
   public static final float JUMPER_RADIUS = 10f;
   public static final float JUMPER_MASS = 10;
   public static final float DEFAULT_FORCE_SCALE = 200;


   public static final float IMPULSE_MULTIPLIER = 0.001f;

   /**
    * Max time for impulse accumulating
    */
   public static final int MAX_IMPULSE_TIME = 3000;
   public static final float SPEED_DIVISOR = 1500.0f;


   public static final float JUMPERS_START_RADIUS = 600;

   public static final float MAX_VELOCITY = 600;

   public static final float CAMERA_MAX_DIST = 100;
   public static final float MAX_SOUNDS_DIST = JUMPER_RADIUS*50;

   public static final int PATH_FINDING_MAP_TEST_POINTS_IN_CELL = 12;
   public static final int PATH_FINDING_MAP_CELL_SIZE = 40;
   public static final boolean PATH_FINDING_DEBUG = false;
   public static final float FREE_LINE_TEST_STEP = 5f;

   public static final float SCORES_GROWTH_MULTIPLIER_PER_MINUTE = 0.2f;
   public static final float SCORES_FOR_ESCAPING_PER_SEC = 5f;
   public static final float SCORES_FOR_HUNTING_FOR_EVERYONE_PER_SEC = -3f;
   
   public static final int TIME_TO_BECOME_SUPER_HUNTER = 30000;
   public static final int DEFAULT_GAME_TIME = 10*60*1000;
   public static final List<Integer> NOTIFY_TIMES_BEFORE_END =
           Arrays.asList(2* DEFAULT_GAME_TIME / 3, DEFAULT_GAME_TIME / 3,
                   ViewConstants.beforeEndNotificationDuration);
}
