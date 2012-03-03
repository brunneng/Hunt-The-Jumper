package com.greenteam.huntjumper.contoller;

import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.utils.GameConstants;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Vector2D;
import net.phys2d.raw.Body;

import java.util.List;

import static com.greenteam.huntjumper.utils.GameConstants.*;
import static com.greenteam.huntjumper.utils.Vector2D.fromVector2f;
import static java.lang.Math.*;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.String.format;

/**
 * User: GreenTea Date: 22.01.12 Time: 12:07
 */
public abstract class AbstractJumperController implements IJumperController
{

   private static final String INFO ="Acceleration info \n Acceleration time - %s \n Angel coef - %s \n Speed coef - %s \n Scale - %s";

   private float accumulatedImpulse;
   public List<Point> lastShortestPath = null;

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
      accumulatedImpulse += delta;

      if (accumulatedImpulse > MAX_IMPULSE)
      {
         accumulatedImpulse = MAX_IMPULSE;
      }
   }

   protected void resetImpulse(int delta)
   {
      accumulatedImpulse = delta;
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

      float scale = DEFAULT_FORCE_SCALE * accumulatedImpulse;
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
