package com.greenteam.huntjumper.model.bonuses;

import com.greenteam.huntjumper.model.JumperInfo;
import com.greenteam.huntjumper.utils.Vector2D;

/**
 * User: GreenTea Date: 19.07.12 Time: 23:12
 */
public abstract class AbstractPositiveBonus extends AbstractPhysBonus
{
   private float escapeAcceleration;

   protected AbstractPositiveBonus(WorldInformationSource world,
                                   float escapeAcceleration)
   {
      super(world);
      this.escapeAcceleration = escapeAcceleration;
   }

   @Override
   public void update(int delta)
   {
      JumperInfo nearest = null;
      float distToNearest = Integer.MAX_VALUE;
      for (JumperInfo j : world.getJumpers())
      {
         float dist = getBody().getPosition().distance(j.position.toVector2f());
         if (dist < distToNearest)
         {
            distToNearest = dist;
            nearest = j;
         }
      }

      assert nearest != null;
      Vector2D force = new Vector2D(nearest.position.toVector2f(), getBody().getPosition());
      force.setLength(escapeAcceleration*getBody().getMass());
      getBody().addForce(force.toVector2f());
   }
}
