package com.greenteam.huntjumper.model;

import com.greenteam.huntjumper.match.Camera;
import com.greenteam.huntjumper.match.IVisibleObject;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.utils.Point;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Line;

/**
 * User: GreenTea Date: 10.07.12 Time: 0:16
 */
public class Coin implements IVisibleObject
{
   private Point pos;

   public Coin(Point pos)
   {
      this.pos = pos;
   }

   @Override
   public void draw(Graphics g)
   {
      Point viewPos = Camera.getCamera().toView(pos);

      g.setColor(ViewConstants.COIN_COLOR2);
      g.fill(new Circle(viewPos.getX(), viewPos.getY(), GameConstants.COIN_RADIUS));

      g.setColor(ViewConstants.COIN_COLOR1);
      g.draw(new Circle(viewPos.getX(), viewPos.getY(), GameConstants.COIN_RADIUS));
   }

   public Point getPos()
   {
      return pos;
   }
}
