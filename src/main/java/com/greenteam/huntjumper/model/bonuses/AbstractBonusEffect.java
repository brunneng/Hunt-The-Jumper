package com.greenteam.huntjumper.model.bonuses;

import com.greenteam.huntjumper.model.Jumper;

/**
 * User: GreenTea Date: 21.07.12 Time: 16:18
 */
public abstract class AbstractBonusEffect implements IJumperBonusEffect
{
   protected int timeLeft;
   protected Jumper jumper;

   protected String getEffectName()
   {
      return getClass().getSimpleName();
   }

   @Override
   public void signalTimeLeft(int timeLeft)
   {
      this.timeLeft = timeLeft;
   }

   @Override
   public final void onStartEffect(Jumper jumper)
   {
      this.jumper = jumper;
      timeLeft = getDuration();
      onStartEffect();
   }

   public abstract void onStartEffect();
}
