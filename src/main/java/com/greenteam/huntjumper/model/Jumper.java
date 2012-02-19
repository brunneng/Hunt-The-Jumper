package com.greenteam.huntjumper.model;

import com.greenteam.huntjumper.Camera;
import com.greenteam.huntjumper.HuntJumperGame;
import com.greenteam.huntjumper.IVisibleObject;
import com.greenteam.huntjumper.contoller.AbstractJumperController;
import com.greenteam.huntjumper.contoller.IJumperController;
import com.greenteam.huntjumper.contoller.MouseController;
import com.greenteam.huntjumper.utils.*;
import net.phys2d.math.ROVector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;
import org.newdawn.slick.*;

import java.util.List;

import static com.greenteam.huntjumper.utils.GameConstants.*;
import static com.greenteam.huntjumper.utils.Vector2D.fromRadianAngleAndLength;

/**
 * User: GreenTea Date: 14.01.12 Time: 21:11
 */
public class Jumper implements IVisibleObject
{
   private static Image lighting;

   static
   {
      try
      {
         lighting = new Image("lighting.png").getScaledCopy(1.12f);
      }
      catch (SlickException e)
      {
         e.printStackTrace();
      }
   }

   private String playerName;

   private Color color;
   private Color paintColor;
   private Circle bodyCircle;
   private Body body;

   private JumperRole jumperRole;
   private AbstractJumperController controller;

   public Jumper(String playerName, Color color, ROVector2f startPos,
                 AbstractJumperController controller, JumperRole jumperRole)
   {
      this.playerName = playerName;
      this.color = color;
      this.paintColor = Utils.isBright(color) ? Color.black : Color.white;
      
      bodyCircle = new Circle(JUMPER_RADIUS);
      body = new Body(playerName, bodyCircle, JUMPER_MASS);
      body.setMaxVelocity(MAX_VELOCITY, MAX_VELOCITY);
      body.setPosition(startPos.getX(), startPos.getY());
      body.setRestitution(1.0f);
      this.jumperRole = jumperRole;
      this.controller = controller;
   }

   public Vector2D vectorTo(Jumper j)
   {
      return new Vector2D(getBody().getPosition(), j.getBody().getPosition());
   }

   public boolean locallyControlled()
   {
      return controller instanceof MouseController;  
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

   public Color getColor()
   {
      return color;
   }

   public void setColor(Color color)
   {
      this.color = color;
   }

   public Circle getBodyCircle()
   {
      return bodyCircle;
   }

   public void setBodyCircle(Circle bodyCircle)
   {
      this.bodyCircle = bodyCircle;
   }

   public void update(int delta)
   {
      controller.update(this, delta);
   }

   public void draw(Graphics g)
   {
      Point viewCenter = Camera.instance().toView(getBody().getPosition());
      float radius = getBodyCircle().getRadius();

      org.newdawn.slick.geom.Circle viewCircle = new org.newdawn.slick.geom.Circle(
              viewCenter.getX(), viewCenter.getY(), radius);

      g.drawImage(lighting, viewCenter.getX() - lighting.getWidth()/2 + 0.6f,
              viewCenter.getY() - lighting.getHeight()/2 + 0.6f, jumperRole.getRoleColor());

      g.setColor(getColor());
      g.fill(viewCircle);
      g.setColor(ViewConstants.jumperBorderColor);
      g.draw(viewCircle);

      g.setColor(paintColor);
      final int segmentsCount = 6;
      final float anglePerSegment = 360 / segmentsCount;
      Vector2D rotationDirection = fromRadianAngleAndLength(getBody().getRotation(), 0.9f*radius);
      for (int i = 0; i < segmentsCount; ++i)
      {
         Vector2D vectorFromCenter = new Vector2D(rotationDirection);
         vectorFromCenter = vectorFromCenter.plus(new Vector2D(viewCenter));

         g.drawLine(viewCenter.getX(), viewCenter.getY(),
                 vectorFromCenter.getX(), vectorFromCenter.getY());
         rotationDirection = rotationDirection.rotate(anglePerSegment);
      }

      if (GameConstants.PATH_FINDING_DEBUG)
      {
         Color c = new Color(1f, 0f, 0f, 0.2f);
         g.setColor(c);
         List<Point> lastShortestPath = controller.lastShortestPath;
         if (lastShortestPath != null)
         {
            for (int i = 0; i < lastShortestPath.size() - 1; ++i)
            {
               Point p1 = lastShortestPath.get(i);
               Point p2 = lastShortestPath.get(i + 1);
               
               Point tP1 = Camera.instance().toView(p1);
               Point tP2 = Camera.instance().toView(p2);
   
               g.drawLine(tP1.getX(), tP1.getY(),
                       tP2.getX(), tP2.getY());
            }
         }
      }

      renderAccelerationBar(g);
      drawName(g, viewCenter, radius);
   }

   private void drawName(Graphics g, Point viewCenter, float radius)
   {
      Input input = HuntJumperGame.getInstance().getGameContainer().getInput();
      Point cursorPos = new Point(input.getMouseX(), input.getMouseY());
      float distToCursor = Utils.getPhysVectorToCursor(getBody(), cursorPos, Camera.instance()).length();
      if (distToCursor < ViewConstants.DRAW_NAME_MAX_RADIUS)
      {
         float a = 1f - distToCursor/ViewConstants.DRAW_NAME_MAX_RADIUS;
         Color c = new Color(1f, 1f, 1f, a);
         TextUtils.drawText(viewCenter.plus(new Vector2D(0, radius*3)), playerName, c,
                 TextUtils.ArialFont, g);

         c = new Color(0f, 0f, 0f, a);
         TextUtils.drawText(viewCenter.plus(new Vector2D(-1, radius*3 - 1)), playerName, c,
                 TextUtils.ArialFont, g);
      }
   }

   private void renderAccelerationBar(Graphics g) {
      Point point = Camera.instance().toView(getBody().getPosition());

      g.setLineWidth(2);

      float leftX = point.getX() - JUMPER_RADIUS;
      float rightX = leftX - (JUMPER_RADIUS) *
              ((MAX_IMPULSE) / MAX_IMPULSE - controller.getAccumulatedImpulse());
      float leftY, rightY;
      leftY = rightY = point.getY() - JUMPER_RADIUS - JUMPER_RADIUS / 2;
      g.drawGradientLine(leftX, leftY, Color.yellow, rightX, rightY, Color.red);
   }

}
