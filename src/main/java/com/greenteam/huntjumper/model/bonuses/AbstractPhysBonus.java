package com.greenteam.huntjumper.model.bonuses;

import com.greenteam.huntjumper.model.ILightSource;
import com.greenteam.huntjumper.model.JumperInfo;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.utils.Point;
import net.phys2d.raw.Body;
import org.newdawn.slick.Color;

import java.util.List;

/**
 * User: GreenTea Date: 18.07.12 Time: 0:00
 */
public abstract class AbstractPhysBonus implements IBonus, ILightSource
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

   public Point getPosition()
   {
      return new Point(body.getPosition());
   }

   @Override
   public Color getLightColor()
   {
      return Color.white;
   }

   @Override
   public float getLightCircle()
   {
      return 0f;
   }

   @Override
   public float getLightMaxRadius()
   {
      return ViewConstants.PHYS_BONUS_MAX_RADIUS;
   }
}
