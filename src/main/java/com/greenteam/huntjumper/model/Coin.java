package com.greenteam.huntjumper.model;

import com.greenteam.huntjumper.match.Camera;
import com.greenteam.huntjumper.match.IVisibleObject;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.utils.Point;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * User: GreenTea Date: 10.07.12 Time: 0:16
 */
public class Coin implements IVisibleObject
{
   private static Image coinImage;

   private static void init()
   {
      if (coinImage != null)
      {
         return;
      }

      try
      {
         coinImage = new Image("images/coin.png");
      }
      catch (SlickException e)
      {
         e.printStackTrace();
      }
   }

   private Point pos;

   public Coin(Point pos)
   {
      this.pos = pos;
   }

   @Override
   public void draw(Graphics g)
   {
      init();

      Point viewPos = Camera.getCamera().toView(pos);

      g.drawImage(coinImage, viewPos.getX()+1, viewPos.getY(), ViewConstants.COIN_COLOR2);
      g.drawImage(coinImage, viewPos.getX(), viewPos.getY());
   }

   public Point getPos()
   {
      return pos;
   }
}
