package com.greenteam.huntjumper.model.bonuses;

import com.greenteam.huntjumper.model.JumperInfo;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.utils.Vector2D;

/**
 * User: GreenTea Date: 19.07.12 Time: 23:12
 */
public abstract class AbstractPositiveBonus extends AbstractPhysBonus
{
   private float acceleration;

   protected AbstractPositiveBonus(WorldInformationSource world,
                                   float acceleration)
   {
      super(world);
      this.acceleration = acceleration;
   }

   @Override
   public void update(int delta)
   {
      JumperInfo nearest = null;
      float distToNearest = Integer.MAX_VALUE;

      JumperInfo mostFar = null;
      float distToMostFar = Integer.MIN_VALUE;
      for (JumperInfo j : world.getJumpers())
      {
         float dist = getBody().getPosition().distance(j.position.toVector2f());
         if (dist < distToNearest)
         {
            distToNearest = dist;
            nearest = j;
         }
         if (dist > distToMostFar)
         {
            distToMostFar = dist;
            mostFar = j;
         }
      }

      assert nearest != null;
      assert mostFar != null;

      float maxDistToNearest = GameConstants.JUMPER_RADIUS*GameConstants.POSITIVE_BONUS_DIST_FACTOR;
      Vector2D force = distToNearest < maxDistToNearest ?
              new Vector2D(nearest.position.toVector2f(), getBody().getPosition()) :
              new Vector2D(getBody().getPosition(), mostFar.position.toVector2f());

      force.setLength(acceleration *getBody().getMass());
      getBody().addForce(force.toVector2f());
   }
}
