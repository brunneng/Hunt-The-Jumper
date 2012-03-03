package com.greenteam.huntjumper.contoller;

import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Vector2D;
import net.phys2d.raw.Body;

import java.util.List;

import static com.greenteam.huntjumper.parameters.GameConstants.*;
import static com.greenteam.huntjumper.utils.Vector2D.fromVector2f;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.String.format;

/**
 * User: GreenTea Date: 22.01.12 Time: 12:07
 */
public abstract class AbstractJumperController implements IJumperController
{
   private int accumulatedImpulseTime;
   public List<Point> lastShortestPath = null;

   public float getAccumulatedImpulseTime()
   {
      return accumulatedImpulseTime;
   }

   private void incrementImpulseTime(int delta)
   {
      accumulatedImpulseTime += delta;

      if (accumulatedImpulseTime > MAX_IMPULSE_TIME)
      {
         accumulatedImpulseTime = MAX_IMPULSE_TIME;
      }
   }

   protected void resetImpulse(int delta)
   {
      accumulatedImpulseTime = delta;
   }

   protected abstract Move makeMove(Jumper jumper);

   public void update(Jumper jumper, int delta)
   {
      Move move = makeMove(jumper);
      Vector2D forceDirection = new Vector2D(move.forceDirection);

      if (move.accumulating)
      {
         incrementImpulseTime(delta);
         return;
      }

      final Body body = jumper.getBody();

      float scale = DEFAULT_FORCE_SCALE * accumulatedImpulseTime;
      Vector2D velocity = fromVector2f(body.getVelocity());

      float angleCoef = 1f;
      float speedCoef = 1f;
      if (velocity.length() > 0 && forceDirection.length() > 0)
      {
         angleCoef = 1 + 0.5f*(abs(velocity.angleToVector(forceDirection)) / 180f);
         angleCoef = angleCoef*angleCoef;

         speedCoef = 1 + Math.min(velocity.length() / SPEED_DIVISOR, 1f);
         speedCoef = speedCoef*speedCoef;
      }
      scale *= angleCoef*speedCoef;

      resetImpulse(delta);

      forceDirection.setLength(scale);
      body.addForce(forceDirection.toVector2f());
   }
}
