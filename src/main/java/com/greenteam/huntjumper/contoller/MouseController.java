package com.greenteam.huntjumper.contoller;

import com.greenteam.huntjumper.match.Camera;
import com.greenteam.huntjumper.HuntJumperGame;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Vector2D;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

import static java.lang.String.format;
import static org.newdawn.slick.Input.*;

/**
 * Listen and react for mouse and/or keyboard commands
 */
public class MouseController extends AbstractJumperController
{
   public MouseController()
   {
      resetImpulse(0);
   }

   @Override
   protected Move makeMove(Jumper jumper, int delta)
   {
      GameContainer container = HuntJumperGame.getInstance().getGameContainer();
      Input input = container.getInput();
      Vector2D forceDirection = Camera.getCamera().getPhysVectorToCursor(jumper.getBody(),
              new Point(input.getMouseX(), input.getMouseY()));

      boolean accumulating = container.getInput().isMouseButtonDown(MOUSE_LEFT_BUTTON);
      return new Move(forceDirection, accumulating);
   }
}
