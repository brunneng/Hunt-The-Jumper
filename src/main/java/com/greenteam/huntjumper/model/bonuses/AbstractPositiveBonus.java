package com.greenteam.huntjumper.model.bonuses;

import com.greenteam.huntjumper.model.JumperInfo;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;

/**
 * User: GreenTea Date: 19.07.12 Time: 23:12
 */
public abstract class AbstractPositiveBonus extends AbstractPhysBonus
{
   protected AbstractPositiveBonus(WorldInformationSource world,
                                   float acceleration)
   {
      super(world, acceleration);
   }

   @Override
   public void update(int delta)
   {
      JumperInfo nearest = Utils.findNearest(this, world.getJumpers());
      float distToNearest = getPosition().distanceTo(nearest.getPosition());

      float maxDistToNearest = GameConstants.JUMPER_RADIUS*GameConstants.POSITIVE_BONUS_DIST_FACTOR;
      Vector2D force;
      if (distToNearest < maxDistToNearest)
      {
         force = new Vector2D(nearest.position.toVector2f(), getBody().getPosition());
      }
      else
      {
         JumperInfo mostFar = Utils.findMostFar(this, world.getJumpers());
         force = new Vector2D(getBody().getPosition(), mostFar.position.toVector2f());
      }

      force.setLength(acceleration *getBody().getMass());
      getBody().addForce(force.toVector2f());
   }
}
