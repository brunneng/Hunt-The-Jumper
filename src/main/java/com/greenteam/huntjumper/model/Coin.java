package com.greenteam.huntjumper.model;

import com.greenteam.huntjumper.match.Camera;
import com.greenteam.huntjumper.match.IGameObject;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Vector2D;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * User: GreenTea Date: 10.07.12 Time: 0:16
 */
public class Coin implements IGameObject
{
   private static Image coinImage;

   private Vector2D rotationVector = Vector2D.fromAngleAndLength(90, 2);

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
   public void update(int delta)
   {
      rotationVector = rotationVector.rotate(delta * -0.250f);
   }

   @Override
   public void draw(Graphics g)
   {
      init();

      Point viewPos = Camera.getCamera().toView(pos);

      Vector2D dir = new Vector2D(rotationVector);
      g.drawImage(coinImage, viewPos.getX()+dir.getX(), viewPos.getY()+dir.getY(), Color.red);

      dir = dir.rotate(120);
      g.drawImage(coinImage, viewPos.getX()+dir.getX(), viewPos.getY()+dir.getY(), Color.green);

      dir = dir.rotate(120);
      g.drawImage(coinImage, viewPos.getX()+dir.getX(), viewPos.getY()+dir.getY(), Color.blue);

      g.drawImage(coinImage, viewPos.getX(), viewPos.getY(), Color.white);
   }

   public Point getPos()
   {
      return pos;
   }

}
