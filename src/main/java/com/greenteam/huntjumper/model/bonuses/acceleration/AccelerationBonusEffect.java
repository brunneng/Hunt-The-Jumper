package com.greenteam.huntjumper.model.bonuses.acceleration;

import com.greenteam.huntjumper.match.Camera;
import com.greenteam.huntjumper.match.TimeAccumulator;
import com.greenteam.huntjumper.model.bonuses.AbstractBonusEffect;
import com.greenteam.huntjumper.model.parameters.MultiplicationParameterEffect;
import com.greenteam.huntjumper.model.parameters.Parameter;
import com.greenteam.huntjumper.model.parameters.ParameterType;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * User: GreenTea Date: 21.07.12 Time: 15:56
 */
public class AccelerationBonusEffect extends AbstractBonusEffect
{
   private TimeAccumulator rotationAccumulator;

   @Override
   public int getDuration()
   {
      return GameConstants.ACCELERATION_BONUS_EFFECT_DURATION;
   }

   @Override
   public void onStartEffect()
   {
      Parameter<Float> p = jumper.getParameters().getParameter(
              ParameterType.ACCELERATION_COMMON_FACTOR);
      p.addEffect(new MultiplicationParameterEffect(getEffectName(),
              GameConstants.ACCELERATION_BONUS_EFFECT_MULTIPLIER));
      rotationAccumulator = new TimeAccumulator(500);
   }

   @Override
   public void onEndEffect()
   {
      jumper.getParameters().getParameter(ParameterType.ACCELERATION_COMMON_FACTOR).removeEffect(
              getEffectName());
   }

   @Override
   public void update(int delta)
   {
      rotationAccumulator.update(delta);
   }

   @Override
   public void draw(Graphics g)
   {
      Point pos = Camera.getCamera().toView(jumper.getBody().getPosition());
      if (!Camera.getCamera().inViewScreenWithReserve(pos))
      {
         return;
      }

      float angle = 360f*rotationAccumulator.getAccumulatorValue() /
              rotationAccumulator.getCycleLength();

      float fadeAngleLength = ViewConstants.ACC_BONUS_FADE_ANGLE_LENGTH;
      float fadeAngleStep = fadeAngleLength / ViewConstants.ACC_BONUS_FADE_SEGMENTS_COUNT;
      Color fadeColor = Color.gray;
      g.setLineWidth(2f);

      float fadeRadius = GameConstants.JUMPER_RADIUS*(1 +
              ViewConstants.ACC_BONUS_FADE_DIST_FROM_JUMPER_FACTOR);

      Vector2D drawVector = Vector2D.fromAngleAndLength(angle, fadeRadius);
      Point prevPoint = pos.plus(drawVector);
      Point currPoint;

      float baseAlpha = 1f;
      float timeLeftPercent = timeLeft / (float)getDuration();
      if (timeLeftPercent < ViewConstants.BONUS_TIME_PERCENT_TO_START_HIDE)
      {
         baseAlpha = timeLeftPercent/ViewConstants.BONUS_TIME_PERCENT_TO_START_HIDE;
      }

      for (int da = 0; da <= fadeAngleLength + Float.MIN_VALUE; da += fadeAngleStep)
      {
         drawVector = drawVector.rotate(-fadeAngleStep);
         currPoint = pos.plus(drawVector);

         float alpha = baseAlpha * (1f - da/fadeAngleLength);
         Color c = Utils.toColorWithAlpha(fadeColor, alpha);
         g.setColor(c);
         g.drawLine(currPoint.getX(), currPoint.getY(), prevPoint.getX(), prevPoint.getY());
         prevPoint = currPoint;
      }
   }
}
