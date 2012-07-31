package com.greenteam.huntjumper.model.bonuses;

import com.greenteam.huntjumper.IMatch;
import com.greenteam.huntjumper.model.IMapObject;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.JumperInfo;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;
import org.newdawn.slick.Graphics;

/**
 * User: GreenTea Date: 24.07.12 Time: 21:02
 */
public abstract class AbstractNeutralBonus extends AbstractPhysBonus
{
   public AbstractNeutralBonus(WorldInformationSource worldInformationSource,
                               float acceleration)
   {
      super(worldInformationSource, acceleration);
   }

   @Override
   public void update(int delta)
   {
      JumperInfo mostFar = Utils.findMostFar(this, world.getJumpers());
      Vector2D force = new Vector2D(getBody().getPosition(), mostFar.position.toVector2f());

      force.setLength(acceleration *getBody().getMass());
      getBody().addForce(force.toVector2f());
   }
}
