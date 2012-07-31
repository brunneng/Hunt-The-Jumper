package com.greenteam.huntjumper.model.bonuses.gravity;

import com.greenteam.huntjumper.match.Camera;
import com.greenteam.huntjumper.match.TimeAccumulator;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.bonuses.AbstractBonusEffect;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;

/**
 * User: GreenTea Date: 27.07.12 Time: 22:26
 */
public class GravityBonusEffect extends AbstractBonusEffect
{
   private TimeAccumulator ringsMoveAccumulator = new TimeAccumulator(10);
   private Point bonusPos;

   public GravityBonusEffect(Point bonusPos)
   {
      this.bonusPos = bonusPos;
   }

   @Override
   public void onStartEffect()
   {
   }

   @Override
   public int getDuration()
   {
      return GameConstants.GRAVITY_BONUS_EFFECT_DURATION;
   }

   @Override
   public void onEndEffect()
   {
   }

   @Override
   public void update(int delta)
   {
      ringsMoveAccumulator.update(delta);
      for (Jumper other : otherJumpers)
      {
         Vector2D vectorToMe = new Vector2D(other.getBody().getPosition(),
                 jumper.getBody().getPosition());
         float dist = vectorToMe.length();
         float forceFactor = GameConstants.GRAVITY_BONUS_FORCE_FACTOR;
         float forceValue = forceFactor*other.getBody().getMass()*jumper.getBody().getMass() /
                 (dist*dist);

         Vector2D forceOnMe = vectorToMe.negate().unit().multiply(forceValue);
         Vector2D forceOnOther = vectorToMe.unit().multiply(forceValue);

         jumper.getBody().addForce(forceOnMe.toVector2f());
         other.getBody().addForce(forceOnOther.toVector2f());
      }
   }

   @Override
   public void draw(Graphics g)
   {
      float baseRadius = GameConstants.JUMPER_RADIUS;
      Point viewPos = Camera.getCamera().toView(jumper.getBody().getPosition());
      float moveToJumperFactor = ringsMoveAccumulator.getTotalTimeInMilliseconds() /
              ViewConstants.GRAVITY_BONUS_MOVE_TO_JUMPER_TIME;
      if (moveToJumperFactor < 1f)
      {
         baseRadius *= moveToJumperFactor;
         Point viewBonusPos = Camera.getCamera().toView(bonusPos);
         viewPos = viewBonusPos.plus(new Vector2D(viewBonusPos, viewPos).multiply(
                 (float)Math.sqrt(moveToJumperFactor)));
      }

      float distBetweenRings = ViewConstants.GRAVITY_BONUS_DIST_BETWEEN_RINGS;

      float baseAlpha = 1f;
      float timeLeftPercent = timeLeft / (float)getDuration();
      if (timeLeftPercent < ViewConstants.BONUS_TIME_PERCENT_TO_START_HIDE)
      {
         baseAlpha = timeLeftPercent/ViewConstants.BONUS_TIME_PERCENT_TO_START_HIDE;
      }

      g.setLineWidth(ViewConstants.GRAVITY_BONUS_RING_WIDTH);
      float timeBetween2Rings = ViewConstants.GRAVITY_BONUS_TIME_2_RINGS;
      float dr = distBetweenRings*(1f -(ringsMoveAccumulator.getTotalTimeInMilliseconds()%
              (int)timeBetween2Rings)/timeBetween2Rings);
      float maxR = baseRadius + ViewConstants.GRAVITY_BONUS_VIEW_RADIUS;
      float currR = baseRadius + dr;
      while (currR < maxR)
      {
         float a = baseAlpha * (1f - currR / maxR);
         g.setColor(Utils.toColorWithAlpha(ViewConstants.GRAVITY_BONUS_RING_COLOR, a));
         g.draw(new Circle(viewPos.getX(), viewPos.getY(), currR));
         currR += distBetweenRings;
      }
   }
}
