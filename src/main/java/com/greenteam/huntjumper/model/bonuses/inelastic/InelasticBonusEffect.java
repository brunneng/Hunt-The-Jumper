package com.greenteam.huntjumper.model.bonuses.inelastic;

import com.greenteam.huntjumper.model.bonuses.AbstractBonusEffect;
import com.greenteam.huntjumper.parameters.GameConstants;
import org.newdawn.slick.Graphics;

/**
 * User: GreenTea Date: 31.07.12 Time: 23:11
 */
public class InelasticBonusEffect extends AbstractBonusEffect
{
   float restitutionChange;

   @Override
   public void onStartEffect()
   {
      float restitution = jumper.getBody().getRestitution();
      restitutionChange = restitution *GameConstants.INELASTIC_BONUS_EFFECT_FACTOR;
      jumper.getBody().setRestitution(restitution - restitutionChange);
   }

   @Override
   public int getDuration()
   {
      return GameConstants.INELASTIC_BONUS_EFFECT_DURATION;
   }

   @Override
   public void onEndEffect()
   {
      jumper.getBody().setRestitution(jumper.getBody().getRestitution() + restitutionChange);
   }

   @Override
   public void update(int delta)
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public void draw(Graphics g)
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }
}
