package com.greenteam.huntjumper.contoller;

import com.greenteam.huntjumper.Camera;
import com.greenteam.huntjumper.HuntJumperGame;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;
import net.phys2d.raw.Body;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

import static com.greenteam.huntjumper.utils.GameConstants.*;
import static java.lang.String.format;
import static org.newdawn.slick.Input.*;

/**
 * Listen and react for mouse and/or keyboard events
 */
public class MouseController extends AbstractJumperController
{
   public MouseController()
   {
      resetImpulse();
   }

   @Override
   protected Move makeMove(Jumper jumper)
   {
      GameContainer container = HuntJumperGame.getInstance().getGameContainer();
      Input input = container.getInput();
      Vector2D forceDirection = Utils.getPhysVectorToCursor(jumper.getBody(),
              new Point(input.getMouseX(), input.getMouseY()), Camera.instance());

      boolean accumulating = container.getInput().isMouseButtonDown(MOUSE_LEFT_BUTTON);
      return new Move(forceDirection, accumulating);
   }
}
