package com.greenteam.huntjumper.contoller;

import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.utils.GameConstants;
import com.greenteam.huntjumper.utils.Vector2D;
import net.phys2d.raw.Body;

import static com.greenteam.huntjumper.utils.GameConstants.*;
import static java.lang.String.format;

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

   private void incrementImpulse(int delta)
   {
      accumulatedImpulse += delta * IMPULSE_MULTIPIER;

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
         scale *= getAccumulatedImpulse();
         System.out.println(format("Accumulated impulse is %s", getAccumulatedImpulse()));
         Vector2D velocity = Vector2D.fromVector2f(jumper.getBody().getVelocity());
         float angle = Math.abs(velocity.angleToVector(forceDirection));
         scale *= angle;
      }

      resetImpulse();

      forceDirection.setLength(scale);
      body.addForce(forceDirection.toVector2f());
   }
}
