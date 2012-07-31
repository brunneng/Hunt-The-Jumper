package com.greenteam.huntjumper.model.bonuses.inelastic;

import com.greenteam.huntjumper.IMatch;
import com.greenteam.huntjumper.match.Camera;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.bonuses.AbstractNegativeBonus;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.utils.Point;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * User: GreenTea Date: 31.07.12 Time: 23:05
 */
public class InelasticBonus extends AbstractNegativeBonus
{
   public InelasticBonus(WorldInformationSource worldInformationSource, Point pos)
   {
      super(worldInformationSource, GameConstants.BONUS_ACCELERATION_FACTOR);

      body = new Body(new Circle(GameConstants.INELASTIC_BONUS_RADIUS),
              GameConstants.DEFAULT_BONUS_MASS);
      body.setPosition(pos.getX(), pos.getY());
      body.setUserData(this);
      body.setRestitution(1.0f);
   }

   @Override
   public void onBonusTaken(IMatch match, Jumper jumper)
   {
      jumper.addBonusEffect(new InelasticBonusEffect());
   }

   @Override
   public void draw(Graphics g)
   {
      Point viewPos = Camera.getCamera().toView(body.getPosition());

      g.setColor(Color.green);
      g.fill(new org.newdawn.slick.geom.Circle(viewPos.getX(), viewPos.getY(),
              GameConstants.INELASTIC_BONUS_RADIUS));
   }
}
