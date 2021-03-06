package com.greenteam.huntjumper.model;

import com.greenteam.huntjumper.*;
import com.greenteam.huntjumper.commands.Command;
import com.greenteam.huntjumper.contoller.AbstractJumperController;
import com.greenteam.huntjumper.match.*;
import com.greenteam.huntjumper.model.bonuses.IJumperBonusEffect;
import com.greenteam.huntjumper.model.parameters.IParametersUser;
import com.greenteam.huntjumper.model.parameters.ParameterType;
import com.greenteam.huntjumper.model.parameters.ParametersHolder;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.utils.*;
import net.phys2d.math.ROVector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;
import org.newdawn.slick.*;

import java.util.*;

import static com.greenteam.huntjumper.parameters.GameConstants.*;
import static com.greenteam.huntjumper.parameters.ViewConstants.*;
import static com.greenteam.huntjumper.utils.Vector2D.fromRadianAngleAndLength;

/**
 * User: GreenTea Date: 14.01.12 Time: 21:11
 */
public class Jumper extends AbstractMapObject implements IParametersUser, IMapObject, ILightproof, ILightSource
{
   private String playerName;

   private Color color;
   private Color paintColor;
   private Circle bodyCircle;
   private Body body;

   private org.newdawn.slick.geom.Circle[] viewCirclesCache = new org.newdawn.slick.geom.Circle[2];
   private int currentCircleIndex = 0;
   private org.newdawn.slick.geom.Circle viewCircle;

   private JumperRole jumperRole;
   private AbstractJumperController controller;
   private TimeAccumulator currentRoleTimeAccumulator = new TimeAccumulator();
   private List<IRoleChangedListener> roleChangedListeners = new ArrayList<IRoleChangedListener>();

   private ParametersHolder parameters = new ParametersHolder();

   private LinkedList<PreviousFadePosition> fadePositions = new LinkedList<PreviousFadePosition>();
   private TimeAccumulator fadePositionsTimer = new TimeAccumulator(FADE_POSITIONS_TIME_INTERVAL);

   private Map<IJumperBonusEffect, TimeAccumulator> bonusEffects = new HashMap<>();
   private List<Jumper> otherJumpers;

   public Jumper(String playerName, Color color, ROVector2f startPos,
                 AbstractJumperController controller, JumperRole jumperRole)
   {
      super(MapObjectIdFactory.getInstance().getNextId(MapObjectType.JUMPER));
      this.playerName = playerName;
      this.color = color;
      this.paintColor = Utils.isBright(color) ? Color.black : Color.white;
      
      bodyCircle = new Circle(JUMPER_RADIUS);
      body = new Body(playerName, bodyCircle, JUMPER_MASS);
      body.setMaxVelocity(MAX_VELOCITY, MAX_VELOCITY);
      body.setPosition(startPos.getX(), startPos.getY());
      body.setRestitution(1.0f);
      body.setUserData(this);

      this.jumperRole = jumperRole;
      this.controller = controller;
      prepareParameters(parameters);
   }

   @Override
   public void prepareParameters(ParametersHolder parametersHolder)
   {
      controller.prepareParameters(parametersHolder);
   }

   public Vector2D vectorTo(Jumper j)
   {
      return new Vector2D(getBody().getPosition(), j.getBody().getPosition());
   }

   public <T> T getParameterValue(ParameterType type)
   {
      return (T)parameters.getParameter(type).getValue();
   }

   public ParametersHolder getParameters()
   {
      return parameters;
   }

   public void addBonusEffect(IJumperBonusEffect effect)
   {
      TimeAccumulator ta = new TimeAccumulator(effect.getDuration());
      bonusEffects.put(effect, ta);
      effect.onStartEffect(this, otherJumpers);
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

   public List<Jumper> getOtherJumpers()
   {
      return otherJumpers;
   }

   public void setOtherJumpers(List<Jumper> otherJumpers)
   {
      this.otherJumpers = otherJumpers;
   }

   @Override
   public Point getPosition()
   {
      return new Point(getBody().getPosition());
   }

   public List<? extends Command> update(int delta)
   {
      List<? extends Command> commands = controller.update(this, delta);
      currentRoleTimeAccumulator.update(delta);
      commands = Utils.add(commands, (List) updateBonusEffects(delta));

      updateFadePositions(delta);
      return commands;
   }

   public List<? extends Command> updateBonusEffects(int delta)
   {
      List<Command> commands = null;
      Iterator<Map.Entry<IJumperBonusEffect, TimeAccumulator>> i =
              bonusEffects.entrySet().iterator();
      while (i.hasNext())
      {
         Map.Entry<IJumperBonusEffect, TimeAccumulator> entry = i.next();
         IJumperBonusEffect effect = entry.getKey();
         TimeAccumulator ta = entry.getValue();
         if (ta.update(delta) == 0)
         {
            commands = Utils.add(commands, (List) effect.update(delta));
            effect.signalTimeLeft(ta.getCycleLength() - ta.getAccumulatorValue());
         }
         else
         {
            effect.onEndEffect();
            i.remove();
         }
      }
      return commands;
   }

   public void removeBonusEffect(IJumperBonusEffect bonusEffect)
   {
      if (bonusEffects.containsKey(bonusEffect))
      {
         bonusEffect.onEndEffect();
         bonusEffects.remove(bonusEffect);
      }
   }

   private void updateFadePositions(int delta)
   {
      if (fadePositionsTimer.update(delta) > 0)
      {
         PreviousFadePosition prevPos = new PreviousFadePosition(System.currentTimeMillis(),
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

         Iterator<PreviousFadePosition> i = fadePositions.iterator();
         while (i.hasNext())
         {
            PreviousFadePosition pp = i.next();
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
         Iterator<PreviousFadePosition> i = fadePositions.descendingIterator();
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

   @Override
   public void drawLightProofBody(Graphics g)
   {
      g.setColor(Color.black);
      g.setLineWidth(2f);

      Point pos = Camera.getCamera().toView(getBody().getPosition());
      if (!Camera.getCamera().inViewScreenWithReserve(pos))
      {
         return;
      }

      updateViewCircle(pos);
      g.fill(viewCircle);

      g.setLineWidth(1f);
   }

   public void drawBody(Graphics g, Point pos, float alpha)
   {
      float radius = getBodyCircle().getRadius();

      updateViewCircle(pos);

      g.setColor(Utils.toColorWithAlpha(getColor(), alpha));
      g.fill(viewCircle);
      g.setColor(Utils.toColorWithAlpha(ViewConstants.JUMPER_BORDER_COLOR, alpha));
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

   private void updateViewCircle(Point pos)
   {
      float radius = getBodyCircle().getRadius();
      boolean circleFound = false;
      for (int i = 0; i < viewCirclesCache.length; ++i)
      {
         org.newdawn.slick.geom.Circle currCircle = viewCirclesCache[i];
         if (currCircle != null &&
                 Utils.equals(currCircle.getRadius(), radius) &&
                 Utils.equals(currCircle.getCenterX(), pos.getX()) &&
                 Utils.equals(currCircle.getCenterY(), pos.getY()))
         {
            viewCircle = currCircle;
            circleFound = true;
            break;
         }
      }
      if (!circleFound)
      {
         if (viewCirclesCache[currentCircleIndex] == null)
         {
            viewCirclesCache[currentCircleIndex] = new org.newdawn.slick.geom.Circle(
                    pos.getX(), pos.getY(), radius);
            viewCircle = viewCirclesCache[currentCircleIndex];
         }
         else
         {
            viewCircle = viewCirclesCache[currentCircleIndex];
            viewCircle.setCenterX(pos.getX());
            viewCircle.setCenterY(pos.getY());
            viewCircle.setRadius(radius);
         }

         currentCircleIndex = (currentCircleIndex + 1) % viewCirclesCache.length;
      }
   }

   public void draw(Graphics g)
   {
      drawFade(g);

      Point viewCenter = Camera.getCamera().toView(getBody().getPosition());
      if (!Camera.getCamera().inViewScreenWithReserve(viewCenter))
      {
         return;
      }

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
      drawBonusEffects(g);
   }

   private void drawBonusEffects(Graphics g)
   {
      for (IJumperBonusEffect effect : bonusEffects.keySet())
      {
         effect.draw(g);
      }
   }

   private void drawName(Graphics g, Point viewCenter, float radius)
   {
      Input input = HuntJumperGame.getInstance().getGameContainer().getInput();
      Point cursorPos = new Point(input.getMouseX(), input.getMouseY());
      float distToCursor = Camera.getCamera().getPhysVectorToCursor(getBody(), cursorPos).length();
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

   @Override
   public Color getLightColor()
   {
      return Color.white;//getColor().darker(0.6f);
   }

   @Override
   public float getLightCircle()
   {
      return getBodyCircle().getRadius();
   }

   @Override
   public float getLightMaxRadius()
   {
      return JUMPER_LIGHT_MAX_RADIUS;
   }
}
