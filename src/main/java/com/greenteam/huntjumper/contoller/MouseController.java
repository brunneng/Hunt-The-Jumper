package com.greenteam.huntjumper.contoller;

import com.greenteam.huntjumper.Camera;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;
import net.phys2d.raw.Body;
import org.newdawn.slick.GameContainer;

import static com.greenteam.huntjumper.utils.GameConstants.*;
import static java.lang.String.format;
import static org.newdawn.slick.Input.*;

/**
 * Listen and react for mouse and/or keyboard events
 */
public class MouseController extends AbstractJumperController
{
   private GameContainer container;

   public MouseController(GameContainer container)
   {
      this.container = container;
      resetImpulse();
   }

   public void update(Jumper jumper, int delta)
   {
      if (accumulating())
      {
         return;
      }

      final Body body = jumper.getBody();
      Vector2D mouseVector = Utils.getPhysVectorToCursor(body, container.getInput(),
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

   private boolean releasing()
   {
      boolean result = false;
      if (!container.getInput().isMouseButtonDown(MOUSE_LEFT_BUTTON))
      {
         result = true;
      }
      return result;
   }

   private boolean accumulating()
   {
      if (container.getInput().isMouseButtonDown(MOUSE_LEFT_BUTTON))
      {
         incrementImpulse();
         return true;
      }
      return false;
   }
}
