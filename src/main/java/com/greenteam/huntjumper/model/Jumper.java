package com.greenteam.huntjumper.model;

import com.greenteam.huntjumper.Camera;
import com.greenteam.huntjumper.IVisibleObject;
import com.greenteam.huntjumper.utils.GameConstants;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;
import net.phys2d.math.ROVector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.ShapeFill;
import org.newdawn.slick.fills.GradientFill;

/**
 * Created by IntelliJ IDEA. User: GreenTea Date: 14.01.12 Time: 21:11 To change this template use
 * File | Settings | File Templates.
 */
public class Jumper implements IVisibleObject
{
   private String playerName;
   private Color color;

   private Circle bodyCircle;
   private Body body;
   private JumperRole jumperRole;

   public Jumper(String playerName, Color color, ROVector2f startPos)
   {
      this.playerName = playerName;
      this.color = color;
      bodyCircle = new Circle(GameConstants.JUMPER_RADIUS);
      body = new Body(bodyCircle, GameConstants.JUMPER_MASS);
      body.setPosition(startPos.getX(), startPos.getY());
      body.setRestitution(1.0f);
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
      float radius = bodyCircle.getRadius();

      org.newdawn.slick.geom.Circle viewCircle = new org.newdawn.slick.geom.Circle(
              viewCenter.getX(), viewCenter.getY(), radius);

      Vector2D rotationDirection = Vector2D.fromRadianAngleAndLength(body.getRotation(), radius);
      rotationDirection.plus(new Vector2D(viewCenter));

      g.setColor(color);
      g.draw(viewCircle);
      g.drawLine(viewCenter.getX(), viewCenter.getY(),
              rotationDirection.getX(), rotationDirection.getY());
   }
}
