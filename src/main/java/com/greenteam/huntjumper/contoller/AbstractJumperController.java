package com.greenteam.huntjumper.contoller;

import static com.greenteam.huntjumper.utils.GameConstants.IMPULSE_INC;
import static com.greenteam.huntjumper.utils.GameConstants.MAX_IMPULSE;
import static com.greenteam.huntjumper.utils.GameConstants.MIN_IMPULSE;

/**
 * User: GreenTea Date: 22.01.12 Time: 12:07
 */
public abstract class AbstractJumperController implements IJumperController
{
   private float accumulatedImpulse;

   public float getAccumulatedImpulse()
   {
      return accumulatedImpulse;
   }

   protected boolean isImpulseSufficient()
   {
      return accumulatedImpulse > MIN_IMPULSE;
   }

   protected void incrementImpulse()
   {
      accumulatedImpulse += IMPULSE_INC;

      if (accumulatedImpulse > MAX_IMPULSE)
      {
         accumulatedImpulse = MAX_IMPULSE;
      }
   }

   protected void resetImpulse()
   {
      accumulatedImpulse = MIN_IMPULSE;
   }
}
