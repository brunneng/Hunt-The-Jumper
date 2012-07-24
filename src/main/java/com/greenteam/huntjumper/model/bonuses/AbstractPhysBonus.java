package com.greenteam.huntjumper.model.bonuses;

import com.greenteam.huntjumper.model.JumperInfo;
import com.greenteam.huntjumper.utils.Point;
import net.phys2d.raw.Body;

import java.util.List;

/**
 * User: GreenTea Date: 18.07.12 Time: 0:00
 */
public abstract class AbstractPhysBonus implements IBonus
{
   public static interface WorldInformationSource
   {
      List<JumperInfo> getJumpers();
   }

   protected Body body;
   protected WorldInformationSource world;
   protected float acceleration;

   public AbstractPhysBonus(WorldInformationSource worldInformationSource, float acceleration)
   {
      this.world = worldInformationSource;
      this.acceleration = acceleration;
   }

   public Body getBody()
   {
      return body;
   }

   public Point getPos()
   {
      return new Point(body.getPosition());
   }
}
