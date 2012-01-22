package com.greenteam.huntjumper.utils;

/**
 * User: GreenTea Date: 14.01.12 Time: 21:17
 */
public final class GameConstants
{
   private GameConstants()
   {
      
   }
   
   public static final float JUMPER_RADIUS = 10;
   public static final float JUMPER_MASS = 10;
   public static final float DEFAULT_FORCE_SCALE = 1000;

   public static final float MIN_IMPULSE = 1.f;
   public static final float IMPULSE_INC = 0.1f;
   public static final float MAX_IMPULSE = 10f;

   public static final float DEFAULT_MAP_RING_RADIUS = 600;
   public static final int DEFAULT_MAP_RING_ANGLES_COUNT = 36;

   public static final float MAX_VELOCITY = 600;

   public static final float CAMERA_MAX_DIST = 100;
}
