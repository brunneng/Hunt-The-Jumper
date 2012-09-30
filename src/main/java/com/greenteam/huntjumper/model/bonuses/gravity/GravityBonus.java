package com.greenteam.huntjumper.model.bonuses.gravity;

import com.greenteam.huntjumper.IMatch;
import com.greenteam.huntjumper.audio.AudioSystem;
import com.greenteam.huntjumper.match.Camera;
import com.greenteam.huntjumper.match.TimeAccumulator;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.bonuses.AbstractNeutralBonus;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Utils;
import net.phys2d.raw.Body;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;

/**
 * User: GreenTea Date: 24.07.12 Time: 21:07
 */
public class GravityBonus extends AbstractNeutralBonus
{
   TimeAccumulator ringsMoveAccumulator = new TimeAccumulator(10);

   public GravityBonus(Point pos)
   {
      super(GameConstants.BONUS_ACCELERATION_FACTOR);
      body = new Body(new net.phys2d.raw.shapes.Circle(10),
              GameConstants.DEFAULT_BONUS_MASS);
      body.setPosition(pos.getX(), pos.getY());
      body.setUserData(this);
      body.setRestitution(1.0f);
   }

   @Override
   public void update(int delta)
   {
      super.update(delta);
      ringsMoveAccumulator.update(delta);
   }

   @Override
   public void onBonusTaken(IMatch match, Jumper jumper, int timePassed)
   {
      setAppliedEffect(new GravityBonusEffect(new Point(body.getLastPosition()), timePassed));
      jumper.addBonusEffect(getAppliedEffect());
      AudioSystem.getInstance().playFarSound(AudioSystem.TAKE_GRAVITY_BONUS_SOUND,
              match.getMyJumper().getBody().getPosition(), jumper.getBody().getPosition());
   }

   @Override
   public void draw(Graphics g)
   {
      Point viewPos = Camera.getCamera().toView(getPosition());
      if (!Camera.getCamera().inViewScreenWithReserve(viewPos))
      {
         return;
      }

      float distBetweenRings = ViewConstants.GRAVITY_BONUS_DIST_BETWEEN_RINGS;

      g.setLineWidth(ViewConstants.GRAVITY_BONUS_RING_WIDTH);
      float timeBetween2Rings = ViewConstants.GRAVITY_BONUS_TIME_2_RINGS;
      float dr = distBetweenRings*(1f -(ringsMoveAccumulator.getTotalTimeInMilliseconds()%
              (int)timeBetween2Rings)/timeBetween2Rings);
      float maxR = ViewConstants.GRAVITY_BONUS_VIEW_RADIUS;
      float currR = dr;
      while (currR < maxR)
      {
         float a = 1f - currR / maxR;
         g.setColor(Utils.toColorWithAlpha(ViewConstants.GRAVITY_BONUS_RING_COLOR, a));
         g.draw(new Circle(viewPos.getX(), viewPos.getY(), currR));
         currR += distBetweenRings;
      }
   }
}
