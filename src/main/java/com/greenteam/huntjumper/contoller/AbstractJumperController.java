package com.greenteam.huntjumper.contoller;

import com.greenteam.huntjumper.Camera;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.utils.GameConstants;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;
import net.phys2d.raw.Body;
import org.newdawn.slick.Input;

import static com.greenteam.huntjumper.utils.GameConstants.*;
import static java.lang.String.format;
import static org.newdawn.slick.Input.MOUSE_LEFT_BUTTON;

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

   private void incrementImpulse()
   {
      accumulatedImpulse += IMPULSE_INC;

      if (accumulatedImpulse > MAX_IMPULSE)
      {
         accumulatedImpulse = MAX_IMPULSE;
      }
   }

   protected void resetImpulse()
   {
      accumulatedImpulse = GameConstants.MIN_IMPULSE;
   }

   protected abstract Point getCursorPosition();
   protected abstract boolean releasing();
   protected abstract boolean accumulating();

   public void update(Jumper jumper)
   {
      if (accumulating())
      {
         incrementImpulse();
         return;
      }

      final Body body = jumper.getBody();
      Vector2D mouseVector = Utils.getPhysVectorToCursor(body, getCursorPosition(),
              Camera.instance());

      float scale = DEFAULT_FORCE_SCALE;
      if (releasing())
      {
         if (isImpulseSufficient())
         {
            scale *= getAccumulatedImpulse();
            System.out.println(format("Accumulated impulse is %s", getAccumulatedImpulse()));
            Vector2D velocity = Vector2D.fromVector2f(jumper.getBody().getVelocity());
            float angle = Math.abs(velocity.angleToVector(mouseVector));
            scale *= angle;
         }

         resetImpulse();
      }

      mouseVector.setLength(scale);
      body.addForce(mouseVector.toVector2f());
   }
}
