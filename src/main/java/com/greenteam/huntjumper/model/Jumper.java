package com.greenteam.huntjumper.model;

import com.greenteam.huntjumper.Camera;
import com.greenteam.huntjumper.IVisibleObject;
import com.greenteam.huntjumper.utils.GameConstants;
import com.greenteam.huntjumper.utils.Point;
import net.phys2d.math.ROVector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * Created by IntelliJ IDEA. User: GreenTea Date: 14.01.12 Time: 21:11 To change this template use
 * File | Settings | File Templates.
 */
public class Jumper implements IVisibleObject
{
   private String playerName;
   private Color color;

   private Body body;
   private JumperRole jumperRole;

   public Jumper(String playerName, Color color, ROVector2f startPos)
   {
      this.playerName = playerName;
      this.color = color;
      body = new Body(new Circle(GameConstants.JUMPER_RADIUS), GameConstants.JUMPER_MASS);
      body.setPosition(startPos.getX(), startPos.getY());
   }

   public Body getBody()
   {
      return body;
   }

   public JumperRole getJumperRole()
   {
      return jumperRole;
   }

   public void setJumperRole(JumperRole jumperRole)
   {
      this.jumperRole = jumperRole;
   }

   public String getPlayerName()
   {
      return playerName;
   }

   @Override
   public void draw(Graphics g, Camera camera)
   {
      Point viewCenter = camera.toView(body.getPosition());


      //To change body of implemented methods use File | Settings | File Templates.
   }
}
