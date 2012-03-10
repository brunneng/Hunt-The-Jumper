package com.greenteam.huntjumper.effects;

import com.greenteam.huntjumper.IVisibleObject;
import com.greenteam.huntjumper.TimeAccumulator;

/**
 * User: GreenTea Date: 04.03.12 Time: 21:49
 */
public abstract class Effect implements IVisibleObject
{
   private TimeAccumulator effectTime = new TimeAccumulator();

   public void update(int dt)
   {
      effectTime.update(dt);
   }

   public final float getExecutionPercent()
   {
      return (float)effectTime.getTotalTimeInMilliseconds() / getDuration();
   }
   
   public final boolean isFinished()
   {
      return getExecutionPercent() > 1;
   }

   public abstract int getDuration();

}
