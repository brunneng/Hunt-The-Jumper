package com.greenteam.huntjumper.model;

import com.greenteam.huntjumper.*;
import com.greenteam.huntjumper.contoller.AbstractJumperController;
import com.greenteam.huntjumper.match.Camera;
import com.greenteam.huntjumper.match.IGameObject;
import com.greenteam.huntjumper.match.TimeAccumulator;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.utils.*;
import net.phys2d.math.ROVector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;
import org.newdawn.slick.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.greenteam.huntjumper.parameters.GameConstants.*;
import static com.greenteam.huntjumper.parameters.ViewConstants.*;
import static com.greenteam.huntjumper.utils.Vector2D.fromRadianAngleAndLength;

/**
 * User: GreenTea Date: 14.01.12 Time: 21:11
 */
public class Jumper implements IGameObject
{
   private String playerName;

   private Color color;
   private Color paintColor;
   private Circle bodyCircle;
   private Body body;

   private JumperRole jumperRole;
   private AbstractJumperController controller;
   private TimeAccumulator currentRoleTimeAccumulator = new TimeAccumulator();
   private List<IRoleChangedListener> roleChangedListeners = new ArrayList<IRoleChangedListener>();

   private LinkedList<PreviousPosition> fadePositions = new LinkedList<PreviousPosition>();
   private TimeAccumulator fadePositionsTimer = new TimeAccumulator(FADE_POSITIONS_TIME_INTERVAL);


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

   public Body getBody()
   {
      return body;
   }

   public JumperRole getJumperRole()
   {
      return jumperRole;
   }

   public List<IRoleChangedListener> getRoleChangedListeners()
   {
      return roleChangedListeners;
   }

   public void setJumperRole(JumperRole jumperRole)
   {
      if (!jumperRole.equals(this.jumperRole))
      {
         currentRoleTimeAccumulator.reset();
      }

      for (IRoleChangedListener listener : roleChangedListeners)
      {
         listener.signalRoleIsChanged(this.jumperRole, jumperRole);
      }

      this.jumperRole = jumperRole;
   }
   
   public int getTimeInCurrentRole()
   {
      return currentRoleTimeAccumulator.getTotalTimeInMilliseconds();
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

   public Color getPaintColor()
   {
      return paintColor;
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
      currentRoleTimeAccumulator.update(delta);

      updateFadePositions(delta);
   }

   private void updateFadePositions(int delta)
   {
      if (fadePositionsTimer.update(delta) > 0)
      {
         PreviousPosition prevPos = new PreviousPosition(System.currentTimeMillis(),
                 new Point(body.getPosition()));
         if (fadePositions.size() == 0)
         {
            fadePositions.add(prevPos);
         }
         else
         {
            if (fadePositions.getLast().getPos().distanceTo(prevPos.getPos()) > MIN_FADE_LENGTH)
            {
               fadePositions.addLast(prevPos);
            }
         }

         Iterator<PreviousPosition> i = fadePositions.iterator();
         while (i.hasNext())
         {
            PreviousPosition pp = i.next();
            if (pp.getTime() < prevPos.getTime() - MAX_FADE_TIME)
            {
               i.remove();
            }
            else
            {
               break;
            }
         }
      }
   }

   private void drawFade(Graphics g)
   {
      if (fadePositions.size() > 0)
      {
         g.setLineWidth(2);

         float currAlpha = START_FADE_ALPHA;
         float alphaStep = currAlpha / fadePositions.size();

         Point prevFadePos = null;
         Iterator<PreviousPosition> i = fadePositions.descendingIterator();
         while (i.hasNext())
         {
            Point currFadePos = Camera.getCamera().toView(i.next().getPos());
            if (prevFadePos == null)
            {
               prevFadePos = currFadePos;
               continue;
            }

            g.setColor(Utils.toColorWithAlpha(jumperRole.getRoleColor(), currAlpha));
            g.drawLine(prevFadePos.getX(), prevFadePos.getY(),
                    currFadePos.getX(), currFadePos.getY());
            currAlpha -= alphaStep;

            prevFadePos = currFadePos;
         }

         g.setLineWidth(1);
      }
   }

   public void drawBody(Graphics g, Point pos, float alpha)
   {
      float radius = getBodyCircle().getRadius();

      org.newdawn.slick.geom.Circle viewCircle = new org.newdawn.slick.geom.Circle(
              pos.getX(), pos.getY(), radius);

      g.setColor(Utils.toColorWithAlpha(getColor(), alpha));
      g.fill(viewCircle);
      g.setColor(Utils.toColorWithAlpha(ViewConstants.jumperBorderColor, alpha));
      g.draw(viewCircle);

      g.setColor(paintColor);
      final int segmentsCount = 6;
      final float anglePerSegment = 360 / segmentsCount;
      Vector2D rotationDirection = fromRadianAngleAndLength(getBody().getRotation(), 0.9f*radius);
      for (int i = 0; i < segmentsCount; ++i)
      {
         Vector2D vectorFromCenter = rotationDirection.plus(pos.getX(), pos.getY());

         g.drawLine(pos.getX(), pos.getY(),
                 vectorFromCenter.getX(), vectorFromCenter.getY());
         rotationDirection = rotationDirection.rotate(anglePerSegment);
      }
   }
   
   public void draw(Graphics g)
   {
      drawFade(g);
      Point viewCenter = Camera.getCamera().toView(getBody().getPosition());
      drawBody(g, viewCenter, 1f);

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

               Point tP1 = Camera.getCamera().toView(p1);
               Point tP2 = Camera.getCamera().toView(p2);

               g.drawLine(tP1.getX(), tP1.getY(),
                       tP2.getX(), tP2.getY());
            }
         }
      }

      renderAccelerationBar(g);
      drawName(g, viewCenter, getBodyCircle().getRadius());
   }

   private void drawName(Graphics g, Point viewCenter, float radius)
   {
      Input input = HuntJumperGame.getInstance().getGameContainer().getInput();
      Point cursorPos = new Point(input.getMouseX(), input.getMouseY());
      float distToCursor = Utils.getPhysVectorToCursor(getBody(), cursorPos,
              Camera.getCamera()).length();
      if (distToCursor < ViewConstants.DRAW_NAME_MAX_RADIUS)
      {
         float a = 1f - distToCursor/ ViewConstants.DRAW_NAME_MAX_RADIUS;
         Color c = new Color(1f, 1f, 1f, a);
         TextUtils.drawTextInCenter(viewCenter.plus(new Vector2D(0, radius * 3)), playerName, c,
                 TextUtils.Arial20Font, g);

         c = new Color(0f, 0f, 0f, a);
         TextUtils.drawTextInCenter(viewCenter.plus(new Vector2D(-1, radius * 3 - 1)), playerName,
                 c, TextUtils.Arial20Font, g);
      }
   }

   private void renderAccelerationBar(Graphics g) {
      Point point = Camera.getCamera().toView(getBody().getPosition());

      g.setLineWidth(2);

      float barLen = (2f*JUMPER_RADIUS * (controller.getAccumulatedImpulseTime() /
              MAX_IMPULSE_ACCUMULATION_TIME));
      if (barLen > 1)
      {
         float leftX = point.getX() - JUMPER_RADIUS;
         float rightX = leftX + barLen;
         float leftY, rightY;
         leftY = rightY = point.getY() - JUMPER_RADIUS - JUMPER_RADIUS / 2;
         g.drawGradientLine(leftX, leftY, Color.yellow, rightX, rightY, Color.red);
      }

      g.setLineWidth(1);
   }

}
