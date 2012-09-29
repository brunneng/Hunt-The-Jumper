package com.greenteam.huntjumper.model.bonuses;

import com.greenteam.huntjumper.IMatch;
import com.greenteam.huntjumper.match.MapObjectIdFactory;
import com.greenteam.huntjumper.match.MapObjectType;
import com.greenteam.huntjumper.model.AbstractMapObject;
import com.greenteam.huntjumper.model.ILightSource;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.JumperInfo;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.utils.Point;
import net.phys2d.raw.Body;
import org.newdawn.slick.Color;

import java.util.List;

/**
 * User: GreenTea Date: 18.07.12 Time: 0:00
 */
public abstract class AbstractPhysBonus extends AbstractMapObject implements IBonus, ILightSource
{
   public static interface WorldInformationSource
   {
      List<JumperInfo> getJumpers();
   }

   protected Body body;
   protected WorldInformationSource world;
   protected float acceleration;
   protected AbstractBonusEffect appliedEffect;

   public AbstractPhysBonus(float acceleration)
   {
      super(MapObjectIdFactory.getInstance().getNextId(MapObjectType.BONUS));
      this.acceleration = acceleration;
   }

   public void setWorld(WorldInformationSource world)
   {
      this.world = world;
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

   public AbstractBonusEffect getAppliedEffect()
   {
      return appliedEffect;
   }

   public void setAppliedEffect(AbstractBonusEffect appliedEffect)
   {
      this.appliedEffect = appliedEffect;
   }

   @Override
   public void revertTakingBonus(IMatch match, Jumper jumper)
   {
      jumper.removeBonusEffect(getAppliedEffect());
      setAppliedEffect(null);
   }
}
