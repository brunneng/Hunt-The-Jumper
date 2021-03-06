package com.greenteam.huntjumper.model.bonuses.inelastic;

import com.greenteam.huntjumper.IMatch;
import com.greenteam.huntjumper.audio.AudioSystem;
import com.greenteam.huntjumper.commands.Command;
import com.greenteam.huntjumper.match.Camera;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.bonuses.AbstractNegativeBonus;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.util.List;
import java.util.Random;

/**
 * User: GreenTea Date: 31.07.12 Time: 23:05
 */
public class InelasticBonus extends AbstractNegativeBonus
{
   private static final float MAX_WAVE_LENGTH = ViewConstants.INELASTIC_BONUS_MAX_WAVE_LENGTH;
   private float[] waveLengths;
   private float[] waveSpeed;

   public InelasticBonus(Point pos)
   {
      super(GameConstants.BONUS_ACCELERATION_FACTOR);

      body = new Body(new Circle(GameConstants.INELASTIC_BONUS_RADIUS),
              GameConstants.DEFAULT_BONUS_MASS);
      body.setPosition(pos.getX(), pos.getY());
      body.setUserData(this);
      body.setRestitution(0.5f);

      waveLengths = new float[ViewConstants.INELASTIC_BONUS_WAVE_POINTS_COUNT];
      waveSpeed = new float[waveLengths.length];
      initWaves(waveLengths, waveSpeed);
   }

   static void initWaves(float[] waveLengths, float[] waveSpeed)
   {
      for (int i = 0; i < waveLengths.length; ++i)
      {
         waveLengths[i] = ViewConstants.INELASTIC_BONUS_MAX_WAVE_LENGTH / 2f;
         waveSpeed[i] = (i % 2 == 0 ? 1 : -1) * Utils.rand.nextFloat()*2*
                 ViewConstants.INELASTIC_BONUS_WAVE_AVERAGE_MOVE_SPEED;
      }
   }

   @Override
   public void onBonusTaken(IMatch match, Jumper jumper, int timePassed)
   {
      AudioSystem.getInstance().playFarSound(AudioSystem.TAKE_INELASTIC_BONUS_SOUND,
              match.getMyJumper().getBody().getPosition(), jumper.getBody().getPosition());
      setAppliedEffect(new InelasticBonusEffect(new Point(getBody().getLastPosition()), timePassed));
      jumper.addBonusEffect(getAppliedEffect());
   }

   @Override
   public List<? extends Command> update(int delta)
   {
      updateWaves(delta, waveLengths, waveSpeed);
      return super.update(delta);
   }

   static void updateWaves(int delta, float[] waveLengths, float[] waveSpeed)
   {
      Random rand = Utils.rand;
      for (int i = 0; i < waveLengths.length; ++i)
      {
         float dWave = waveSpeed[i] * 2*ViewConstants.INELASTIC_BONUS_WAVE_AVERAGE_MOVE_SPEED *
                 rand.nextFloat() * delta * 0.001f;

         waveLengths[i] += dWave;
         if (waveLengths[i] > MAX_WAVE_LENGTH)
         {
            waveLengths[i] -= (waveLengths[i] - MAX_WAVE_LENGTH);
            waveSpeed[i] = -waveSpeed[i];
         }
         else if (waveLengths[i] < 0)
         {
            waveLengths[i] = -waveLengths[i];
            waveSpeed[i] = -waveSpeed[i];
         }

         if (waveLengths[i] > MAX_WAVE_LENGTH)
         {
            waveLengths[i] = MAX_WAVE_LENGTH;
         }
         else if (waveLengths[i] < 0)
         {
            waveLengths[i] = 0;
         }
      }
   }

   @Override
   public void draw(Graphics g)
   {
      Point viewPos = Camera.getCamera().toView(body.getPosition());
      if (!Camera.getCamera().inViewScreenWithReserve(viewPos))
      {
         return;
      }

      g.setColor(Color.gray);
      g.setLineWidth(1);
      drawWaves(g, viewPos, waveLengths, GameConstants.INELASTIC_BONUS_RADIUS-2);

      g.setColor(Color.lightGray);
      g.setLineWidth(ViewConstants.INELASTIC_BONUS_SECOND_LINE_WIDTH);
      drawWaves(g, viewPos, waveLengths, GameConstants.INELASTIC_BONUS_RADIUS);
   }

   static void drawWaves(Graphics g, Point viewPos, float[] waveLengths, float baseRadius)
   {
      final int pointsCount = waveLengths.length;
      final float dAngle = 360f / pointsCount;
      Point firstPoint = null;
      Point prevPointPos = null;

      for (int i = 0; i < pointsCount; ++i)
      {
         float rotateAngle = i*dAngle;
         float r = baseRadius - MAX_WAVE_LENGTH*0.5f + waveLengths[i];
         Vector2D pointVector = Vector2D.fromAngleAndLength(rotateAngle, r);
         Point currPointPos = viewPos.plus(pointVector);

         if (prevPointPos != null)
         {
            g.drawLine(prevPointPos.getX(), prevPointPos.getY(), currPointPos.getX(),
                    currPointPos.getY());
         }
         else
         {
            firstPoint = currPointPos;
         }

         prevPointPos = currPointPos;
      }
      g.drawLine(prevPointPos.getX(), prevPointPos.getY(), firstPoint.getX(),
              firstPoint.getY());
   }
}
