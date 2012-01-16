package com.greenteam.huntjumper.contoller;

import com.greenteam.huntjumper.model.Jumper;
import net.phys2d.math.Vector2f;
import org.newdawn.slick.GameContainer;

/**
 * Listen and react for mouse and/or keyboard events
 */

public class MouseController implements IJumperController
{

   public void update(Jumper jumper, GameContainer container, int delta)
   {
      float mouseX = container.getInput().getMouseX();
      float mouseY = container.getInput().getMouseY();
      Vector2f velocity = new Vector2f();
      velocity.set((mouseX - jumper.getBody().getPosition().getX()),
              (mouseY - jumper.getBody().getPosition().getY()));
      velocity.normalise();
      jumper.getBody().adjustVelocity(velocity);
   }
}
