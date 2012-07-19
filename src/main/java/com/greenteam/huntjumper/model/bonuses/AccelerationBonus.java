package com.greenteam.huntjumper.model.bonuses;

import com.greenteam.huntjumper.IMatch;
import com.greenteam.huntjumper.match.Camera;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.utils.Point;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * User: GreenTea Date: 19.07.12 Time: 23:02
 */
public class AccelerationBonus extends AbstractPositiveBonus
{
   public AccelerationBonus(WorldInformationSource world, Point pos)
   {
      super(world, GameConstants.ACCELERATION_BONUS_ESCAPE_ACCELERATION);
      body = new Body(new Circle(GameConstants.ACCELERATION_BONUS_RADIUS),
              GameConstants.ACCELERATION_BONUS_MASS);
      body.setPosition(pos.getX(), pos.getY());
      body.setUserData(this);
      body.setRestitution(1.0f);
   }

   @Override
   public void onBonusTaken(IMatch match, Jumper jumper)
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public void draw(Graphics g)
   {
      Point viewPoint = Camera.getCamera().toView(getBody().getPosition());
      org.newdawn.slick.geom.Circle c = new org.newdawn.slick.geom.Circle(
              viewPoint.getX(), viewPoint.getY(), GameConstants.ACCELERATION_BONUS_RADIUS);
      g.setColor(Color.yellow);
      g.fill(c);
   }
}
