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


   public static final float DEFAULT_MAP_RING_RADIUS = 600;
   public static final int DEFAULT_MAP_RING_ANGLES_COUNT = 36;

   public static final float MAX_VELOCITY = 600;

   public static final float CAMERA_MAX_DIST = 100;
}
