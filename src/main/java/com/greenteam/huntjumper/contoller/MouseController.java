package com.greenteam.huntjumper.contoller;

import com.greenteam.huntjumper.Camera;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.utils.Point;
import net.phys2d.math.Vector2f;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

import static com.greenteam.huntjumper.utils.GameConstants.*;
import static java.lang.String.format;

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
         float mouseX = container.getInput().getMouseX();
         float mouseY = container.getInput().getMouseY();

         float scale = DEFAULT_FORCE_SCALE;
         if (releasing()) {
            scale *= accumulatedScale > MAX_FORCE_SCALE ? MAX_FORCE_SCALE : accumulatedScale;
            accumulatedScale = MIN_IMPULSE;
         }

         if (scale > DEFAULT_FORCE_SCALE) System.out.println(format("Force scale is %s", scale));
         
         jumper.getBody().addForce(createForce(jumper, scale, mouseX, mouseY));

      }
   }

   private boolean releasing()
   {
      boolean result = false;
      if (!container.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) && accumulatedScale > MIN_IMPULSE)
      {
         result = true;
      }
      return result;
   }

   private boolean accumulating() {
      if (container.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON))
      {
         accumulatedScale += SCALE_INC;
         return true;
      }
      return false;
   }

   private Vector2f createForce(Jumper jumper, float scale, float mouseX, float mouseY) {
      Vector2f velocity = new Vector2f();
      Point realPoint = Camera.instance().toPhys(new Vector2f(mouseX, mouseY));
      velocity.set((realPoint.getX() - jumper.getBody().getPosition().getX()),
              (realPoint.getY() - jumper.getBody().getPosition().getY()));
      velocity.negate();
      velocity.scale(scale);
      return velocity;
   }
}
