package com.greenteam.huntjumper.model.bonuses.inelastic;

import com.greenteam.huntjumper.match.Camera;
import com.greenteam.huntjumper.match.TimeAccumulator;
import com.greenteam.huntjumper.model.bonuses.AbstractBonusEffect;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * User: GreenTea Date: 31.07.12 Time: 23:11
 */
public class InelasticBonusEffect extends AbstractBonusEffect
{
   private TimeAccumulator effectMoveToJumperAccumulator = new TimeAccumulator((int)
           ViewConstants.INELASTIC_BONUS_EFFECT_MOVE_TO_JUMPER);
   private float[] waveLengths;
   private float[] waveSpeed;
   private Point bonusPos;
   private float restitutionChange;

   public InelasticBonusEffect(Point bonusPos)
   {
      this.bonusPos = bonusPos;

      waveLengths = new float[ViewConstants.INELASTIC_BONUS_WAVE_POINTS_COUNT];
      waveSpeed = new float[waveLengths.length];
      InelasticBonus.initWaves(waveLengths, waveSpeed);
   }

   @Override
   public void onStartEffect()
   {
      float restitution = jumper.getBody().getRestitution();
      restitutionChange = restitution *GameConstants.INELASTIC_BONUS_EFFECT_FACTOR;
      jumper.getBody().setRestitution(restitution - restitutionChange);
   }

   @Override
   public int getDuration()
   {
      return GameConstants.INELASTIC_BONUS_EFFECT_DURATION;
   }

   @Override
   public void onEndEffect()
   {
      jumper.getBody().setRestitution(jumper.getBody().getRestitution() + restitutionChange);
   }

   @Override
   public void update(int delta)
   {
      if (effectMoveToJumperAccumulator != null && effectMoveToJumperAccumulator.update(delta) != 0)
      {
         effectMoveToJumperAccumulator = null;
      }
      InelasticBonus.updateWaves(delta, waveLengths, waveSpeed);
   }

   @Override
   public void draw(Graphics g)
   {
      float baseRadius = GameConstants.JUMPER_RADIUS *
              ViewConstants.INELASTIC_BONUS_BASE_RADIUS_FACTOR;
      Point viewPos = Camera.getCamera().toView(jumper.getBody().getPosition());
      if (!Camera.getCamera().inViewScreenWithReserve(viewPos))
      {
         return;
      }

      if (effectMoveToJumperAccumulator != null)
      {
         float moveToJumperFactor = effectMoveToJumperAccumulator.getTotalTimeInMilliseconds() /
                 (float)effectMoveToJumperAccumulator.getCycleLength();

         baseRadius = GameConstants.INELASTIC_BONUS_RADIUS +
                 moveToJumperFactor*(baseRadius - GameConstants.INELASTIC_BONUS_RADIUS);
         Point viewBonusPos = Camera.getCamera().toView(bonusPos);
         viewPos = viewBonusPos.plus(new Vector2D(viewBonusPos, viewPos).multiply(
                 (float)Math.sqrt(moveToJumperFactor)));
      }

      float a = 1f;
      float timeLeftPercent = timeLeft / (float)getDuration();
      if (timeLeftPercent < ViewConstants.BONUS_TIME_PERCENT_TO_START_HIDE)
      {
         a = timeLeftPercent/ViewConstants.BONUS_TIME_PERCENT_TO_START_HIDE;
      }
      a *= 0.8f;

      g.setColor(Utils.toColorWithAlpha(Color.gray, a));
      g.setLineWidth(1);
      InelasticBonus.drawWaves(g, viewPos, waveLengths, baseRadius - 2);

      g.setColor(Utils.toColorWithAlpha(Color.lightGray, a));
      g.setLineWidth(ViewConstants.INELASTIC_BONUS_SECOND_LINE_WIDTH);
      InelasticBonus.drawWaves(g, viewPos, waveLengths, baseRadius);
   }
}
