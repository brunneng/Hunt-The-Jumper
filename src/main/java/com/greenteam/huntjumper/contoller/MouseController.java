package com.greenteam.huntjumper.contoller;

import com.greenteam.huntjumper.Camera;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.utils.GameConstants;
import com.greenteam.huntjumper.utils.Point;
import net.phys2d.math.Vector2f;
import org.newdawn.slick.GameContainer;

/**
 * Listen and react for mouse and/or keyboard events
 */

public class MouseController implements IJumperController
{
   private GameContainer container;

   public MouseController(GameContainer container)
   {
      this.container = container;
   }

   public void update(Jumper jumper, int delta)
   {
      float mouseX = container.getInput().getMouseX();
      float mouseY = container.getInput().getMouseY();
      Vector2f velocity = new Vector2f();
      Point realPoint = Camera.instance().toPhys(new Vector2f(mouseX, mouseY));
      velocity.set((realPoint.getX() - jumper.getBody().getPosition().getX()),
              (realPoint.getY() - jumper.getBody().getPosition().getY()));
      velocity.scale(GameConstants.JUMPER_FORCE_MULTIPLIER);
      jumper.getBody().addForce(velocity);
   }
}
