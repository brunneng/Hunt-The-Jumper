package com.greenteam.huntjumper.effects.particles;

import com.greenteam.huntjumper.utils.Vector2D;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA. User: Ivan Date: 27.02.2011 Time: 20:00:43 To change this template use
 * File | Settings | File Templates.
 */
public class ThrustGenerator extends BlastGenerator
{
   public ThrustGenerator(ParticleType type, int count, float blastSpeed)
   {
      super(type, count, blastSpeed);
      this.direction = null;
   }

   @Override
   public Collection<ParticleEntity> update(int delta)
   {
      Collection<ParticleEntity> res = new ArrayList<ParticleEntity>();
      for (int i = 0; i < count; i++)
      {
         ParticleEntity particle = ParticleEntity.Builder.createEntity(type);
         Vector2D dir = direction.clone();
         dir.unit().multiply((rand.nextFloat() + 1) * blastSpeed / 2);
         dir.rotate((rand.nextFloat() - 0.5f) * 40);
         particle.setVelocity(dir);
         res.add(particle);
      }

      return res;
   }

   @Override
   public ThrustGenerator copy()
   {
      return new ThrustGenerator(type, count, blastSpeed);
   }
}
