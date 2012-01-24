package com.greenteam.huntjumper.contoller;

import com.greenteam.huntjumper.Camera;
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
   private GameContainer container;

   public MouseController(GameContainer container)
   {
      this.container = container;
      resetImpulse();
   }

   public Point getCursorPosition()
   {
      Input input = container.getInput();
      return new Point(input.getMouseX(), input.getMouseY());
   }

   protected boolean releasing()
   {
      boolean result = false;
      if (!container.getInput().isMouseButtonDown(MOUSE_LEFT_BUTTON))
      {
         result = true;
      }
      return result;
   }

   protected boolean accumulating()
   {
      if (container.getInput().isMouseButtonDown(MOUSE_LEFT_BUTTON))
      {
         return true;
      }
      return false;
   }
}
