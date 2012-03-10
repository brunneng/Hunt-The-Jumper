package com.greenteam.huntjumper.effects.particles;

import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Created by IntelliJ IDEA. User: Ivan Date: 16.01.2011 Time: 12:13:11 To change this template use
 * File | Settings | File Templates.
 */
public class BlastGenerator extends ParticleGenerator
{
   protected static final Random rand = Utils.rand;

   protected float blastSpeed;

   public BlastGenerator(ParticleType type, int count, float blastSpeed)
   {
      super(type, 0, count);

      this.blastSpeed = blastSpeed;
   }

   @Override
   public Collection<ParticleEntity> update(int delta)
   {
      Collection<ParticleEntity> res = new ArrayList<ParticleEntity>();
      for (int i = 0; i < count; i++)
      {
         ParticleEntity particle = ParticleEntity.Builder.createEntity(type);
         particle.setVelocity(
                 Vector2D.fromAngleAndLength(rand.nextFloat() * 360, rand.nextFloat() * blastSpeed));
         res.add(particle);
      }

      return res;
   }

   @Override
   public BlastGenerator copy()
   {
      return new BlastGenerator(type, count, blastSpeed);
   }
}
