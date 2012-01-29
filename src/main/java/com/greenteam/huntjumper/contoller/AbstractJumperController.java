package com.greenteam.huntjumper.contoller;

import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.utils.GameConstants;
import com.greenteam.huntjumper.utils.Vector2D;
import net.phys2d.raw.Body;

import static com.greenteam.huntjumper.utils.GameConstants.*;
import static com.greenteam.huntjumper.utils.Vector2D.fromVector2f;
import static java.lang.Math.*;
import static java.lang.String.format;

/**
 * User: GreenTea Date: 22.01.12 Time: 12:07
 */
public abstract class AbstractJumperController implements IJumperController
{

   private static final String INFO ="Acceleration info \n Acceleration time - %s \n Angel coef - %s \n Speed coef - %s \n Scale - %s";

   private float accumulatedImpulse;

   public float getAccumulatedImpulse()
   {
      return accumulatedImpulse;
   }

   protected boolean isImpulseSufficient()
   {
      return accumulatedImpulse > MIN_IMPULSE;
   }

   private void incrementImpulse(int delta)
   {
      accumulatedImpulse += delta * IMPULSE_MULTIPLIER;

      if (accumulatedImpulse > MAX_IMPULSE)
      {
         accumulatedImpulse = MAX_IMPULSE;
      }
   }

   protected void resetImpulse()
   {
      accumulatedImpulse = GameConstants.MIN_IMPULSE;
   }

   protected abstract Move makeMove(Jumper jumper);

   public void update(Jumper jumper, int delta)
   {
      Move move = makeMove(jumper);
      Vector2D forceDirection = new Vector2D(move.forceDirection);

      if (move.accumulating)
      {
         incrementImpulse(delta);
         return;
      }

      final Body body = jumper.getBody();

      float scale = DEFAULT_FORCE_SCALE * delta;
      if (isImpulseSufficient())
      {
         Vector2D velocity = fromVector2f(body.getVelocity());
         float angleCoef = 1 + (max(30 , abs(velocity.angleToVector(forceDirection))) / 360);
         float accumulatedTime = getAccumulatedImpulse();
         float speedCoef = BASE_SPEED_MODIFIER + velocity.length() / SPEED_DIVISOR;
         scale *= speedCoef * body.getMass() * accumulatedTime * accumulatedTime * angleCoef;      
         System.out.println(format(INFO, accumulatedTime,  angleCoef, speedCoef, scale));
      }

      resetImpulse();

      forceDirection.setLength(scale);
      body.addForce(forceDirection.toVector2f());
   }
}
