package com.greenteam.huntjumper;

import com.greenteam.huntjumper.utils.ViewConstants;
import org.newdawn.slick.*;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;

/**
 * Created by IntelliJ IDEA. User: GreenTea Date: 13.01.12 Time: 22:44 To change this template use
 * File | Settings | File Templates.
 */
public class HuntJumperGame implements Game
{
   boolean exitGame = false;

   public void initMap()
   {

   }

   public void initBalls()
   {

   }

   public void init(GameContainer container) throws SlickException
   {

   }

   public void update(GameContainer container, int delta) throws SlickException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public void render(GameContainer container, Graphics g) throws SlickException
   {
      float[] points = new float[] {50, 50, 50, 90, 60, 60, 60, 40};
      g.fill(new Polygon(points), new GradientFill(0,0, Color.red, 0, 1, Color.red));
      //To change body of implemented methods use File | Settings | File Templates.
   }

   public boolean closeRequested()
   {
      return exitGame;
   }

   public String getTitle()
   {
      return ViewConstants.GAME_NAME;
   }
}
