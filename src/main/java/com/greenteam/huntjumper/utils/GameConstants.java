package com.greenteam.huntjumper.utils;

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
   public static final float DEFAULT_FORCE_SCALE = 100;


   public static final float IMPULSE_MULTIPLIER = 0.001f;

   /**
    * Max time for impulse accumulating
    */
   public static final float MAX_IMPULSE = 3.0f;
   public static final float MIN_IMPULSE = 1.0f;
   public static final float SPEED_DIVISOR = 50.0f;
   public static final float BASE_SPEED_MODIFIER = 3.0f;


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
}
