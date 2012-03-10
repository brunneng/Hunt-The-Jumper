package com.greenteam.huntjumper.effects.particles;

import com.greenteam.huntjumper.utils.Vector2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA. User: Ivan Date: 05.02.2011 Time: 17:56:43 To change this template use
 * File | Settings | File Templates.
 */
public class CompositeParticleGenerator extends ParticleGenerator
{
   Collection<ParticleGenerator> generators;

   public CompositeParticleGenerator()
   {
      generators = new LinkedList<ParticleGenerator>();
   }

   public CompositeParticleGenerator add(ParticleGenerator generator)
   {
      generators.add(generator);
      return this;
   }

   @Override
   public void setDirection(Vector2D direction)
   {
      for (ParticleGenerator generator : generators)
         generator.setDirection(direction);
   }

   @Override
   public Collection<ParticleEntity> update(int delta)
   {
      Collection<ParticleEntity> res = new ArrayList<ParticleEntity>();
      for (ParticleGenerator generator : generators)
      {
         res.addAll(generator.update(delta));
      }
      return res;
   }

   @Override
   public CompositeParticleGenerator copy()
   {
      CompositeParticleGenerator res = new CompositeParticleGenerator();
      for (ParticleGenerator generator : generators)
      {
         res.add(generator.copy());
      }
      return res;
   }
}
