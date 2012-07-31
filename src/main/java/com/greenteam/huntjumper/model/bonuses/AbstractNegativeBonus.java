package com.greenteam.huntjumper.model.bonuses;

import com.greenteam.huntjumper.model.JumperInfo;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;

/**
 * User: GreenTea Date: 31.07.12 Time: 22:17
 */
public abstract class AbstractNegativeBonus extends AbstractPhysBonus
{
   public AbstractNegativeBonus(WorldInformationSource worldInformationSource, float acceleration)
   {
      super(worldInformationSource, acceleration);
   }

   @Override
   public void update(int delta)
   {
      JumperInfo nearest = Utils.findNearest(this, world.getJumpers());
      Vector2D force = new Vector2D(getBody().getPosition(), nearest.getPosition().toVector2f());

      force.setLength(acceleration * getBody().getMass());
      getBody().addForce(force.toVector2f());
   }
}
