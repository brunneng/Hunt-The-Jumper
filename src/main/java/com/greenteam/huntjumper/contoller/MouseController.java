package com.greenteam.huntjumper.contoller;

import com.greenteam.huntjumper.Camera;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

import static com.greenteam.huntjumper.utils.GameConstants.*;
import static com.greenteam.huntjumper.utils.Vector2D.fromPhys;
import static java.lang.String.format;
import static org.newdawn.slick.Input.*;

/**
 * Listen and react for mouse and/or keyboard events
 */

public class MouseController implements IJumperController
{
   private static float MIN_IMPULSE = 1.f;
   private static final float SCALE_INC = 2;

   private GameContainer container;
   private float accumulatedScale = MIN_IMPULSE;

   public MouseController(GameContainer container)
   {
      this.container = container;
   }

   public void update(Jumper jumper, int delta)
   {
      if (accumulating())
      {
         return;
      }
      else {
         final Body body = jumper.getBody();
         Vector2D mouseVector = Utils.mouseVector(container.getInput(), body, Camera.instance());

         float scale = DEFAULT_FORCE_SCALE;
         if (releasing()) {
            scale *= accumulatedScale > MAX_FORCE_SCALE ? MAX_FORCE_SCALE : accumulatedScale;
            accumulatedScale = MIN_IMPULSE;

            Vector2D velocity = fromPhys((Vector2f) jumper.getBody().getVelocity());

            float angel = velocity.angleToVector(mouseVector);
            Vector2D rotated = velocity.rotate(angel);

            body.adjustVelocity(((Vector2f) body.getVelocity()).negate());
            body.adjustVelocity(rotated.toPhysVector());
         }

         if (scale > DEFAULT_FORCE_SCALE) System.out.println(format("Force scale is %s", scale));

         Vector2f force = mouseVector.toPhysVector();
         force.scale(scale);

         body.addForce(force);

      }
   }

   private boolean releasing()
   {
      boolean result = false;
      if (!container.getInput().isMouseButtonDown(MOUSE_LEFT_BUTTON) && accumulatedScale > MIN_IMPULSE)
      {
         result = true;
      }
      return result;
   }

   private boolean accumulating()
   {
      if (container.getInput().isMouseButtonDown(MOUSE_LEFT_BUTTON))
      {
         accumulatedScale += SCALE_INC;
         return true;
      }
      return false;
   }
}
