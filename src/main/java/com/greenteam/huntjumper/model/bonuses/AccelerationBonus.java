package com.greenteam.huntjumper.model.bonuses;

import com.greenteam.huntjumper.IMatch;
import com.greenteam.huntjumper.audio.AudioSystem;
import com.greenteam.huntjumper.effects.particles.ParticleEntity;
import com.greenteam.huntjumper.match.Camera;
import com.greenteam.huntjumper.match.EffectsContainer;
import com.greenteam.huntjumper.match.TimeAccumulator;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;

/**
 * User: GreenTea Date: 19.07.12 Time: 23:02
 */
public class AccelerationBonus extends AbstractPositiveBonus
{
   List<TimeAccumulator> particlesAccumulators = new ArrayList<>();

   public AccelerationBonus(WorldInformationSource world, Point pos)
   {
      super(world, GameConstants.BONUS_ACCELERATION_FACTOR);
      body = new Body(new Circle(GameConstants.ACCELERATION_BONUS_RADIUS),
              GameConstants.DEFAULT_BONUS_MASS);
      body.setPosition(pos.getX(), pos.getY());
      body.setUserData(this);
      body.setRestitution(1.0f);

      for (int i = 0; i < ViewConstants.ACC_BONUS_PARTICLES_COUNT; ++i)
      {
         particlesAccumulators.add(new TimeAccumulator(300 + Utils.rand.nextInt(400)));
      }
   }

   @Override
   public void onBonusTaken(IMatch match, Jumper jumper)
   {
      jumper.addBonusEffect(new AccelerationBonusEffect());
      Point pos = new Point(getBody().getPosition());

      float dr = GameConstants.ACCELERATION_BONUS_RADIUS / particlesAccumulators.size();
      for (int i = 0, particlesAccumulatorsSize = particlesAccumulators.size();
           i < particlesAccumulatorsSize; i++)
      {
         TimeAccumulator acc = particlesAccumulators.get(i);

         float angle = 360f * acc.getAccumulatorValue() / acc.getCycleLength();
         float particleRadius = ViewConstants.ACC_BONUS_SPLASH_PARTICLE_RADIUS;
         float radiusToCenter = dr*i;

         Vector2D vectorFromCenter = Vector2D.fromAngleAndLength(angle, radiusToCenter);
         Point particlePos = pos.plus(vectorFromCenter);

         Vector2D particleVelocity = vectorFromCenter.rotate(90);
         Vector2D vectorFromJumper = new Vector2D(new Point(jumper.getBody().getPosition()),
                 particlePos);
         particleVelocity = particleVelocity.plus(vectorFromJumper);

         float w = 360f/acc.getCycleLength();
         float v = ViewConstants.ACC_BONUS_SPLASH_VELOCITY_FACTOR * w * radiusToCenter;
         particleVelocity.setLength(v);

         ParticleEntity pe = new ParticleEntity();
         pe.setPosition(particlePos);
         pe.setColor(ViewConstants.ACC_BONUS_PARTICLE_COLOR);
         pe.setDrawShadow(true);
         pe.setDuration(ViewConstants.ACC_BONUS_SPLASH_DURATION);
         pe.setStartRadius(particleRadius);
         pe.setEndRadius(particleRadius);
         pe.setVelocity(particleVelocity);

         EffectsContainer.getInstance().addEffect(pe);
      }

      AudioSystem.getInstance().playFarSound(AudioSystem.TAKE_ACC_BONUS_SOUND,
              match.getMyJumper().getBody().getPosition(), jumper.getBody().getPosition());
   }

   @Override
   public void update(int delta)
   {
      super.update(delta);

      for (TimeAccumulator acc : particlesAccumulators)
      {
         acc.update(delta);
      }
   }

   @Override
   public void draw(Graphics g)
   {
      Point pos = Camera.getCamera().toView(getBody().getPosition());

      float dr = GameConstants.ACCELERATION_BONUS_RADIUS / particlesAccumulators.size();
      for (int i = 0, particlesAccumulatorsSize = particlesAccumulators.size();
              i < particlesAccumulatorsSize; i++)
      {
         TimeAccumulator acc = particlesAccumulators.get(i);

         float angle = 360f * acc.getAccumulatorValue() / acc.getCycleLength();
         float radiusToCenter = dr*i;
         float particleRadius = 3*(1f - i/(float)particlesAccumulators.size());

         float fadeAngleLength = ViewConstants.ACC_BONUS_PARTICLE_ANGLE_LENGTH;
         float fadeAngleStep = fadeAngleLength / 5;
         Color fadeColor = ViewConstants.ACC_BONUS_PARTICLE_COLOR;
         g.setLineWidth(1f);

         Vector2D drawVector = Vector2D.fromAngleAndLength(angle, radiusToCenter);
         Point prevPoint = pos.plus(drawVector);
         Point currPoint;

         g.setLineWidth(particleRadius);
         for (int da = 0; da <= fadeAngleLength + Float.MIN_VALUE; da += fadeAngleStep)
         {
            drawVector = drawVector.rotate(-fadeAngleStep);
            currPoint = pos.plus(drawVector);

            float alpha = (1f - da / fadeAngleLength);
            Color c = Utils.toColorWithAlpha(fadeColor, alpha);
            g.setColor(c);
            g.drawLine(currPoint.getX(), currPoint.getY(), prevPoint.getX(), prevPoint.getY());
            prevPoint = currPoint;
         }
      }
      g.setLineWidth(1f);

   }
}
