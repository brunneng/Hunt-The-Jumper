package com.greenteam.huntjumper.effects.particles;

import com.greenteam.huntjumper.utils.Vector2D;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA. User: Ivan Date: 15.01.2011 Time: 14:55:23 To change this template use
 * File | Settings | File Templates.
 */
public abstract class AbstractParticleGenerator
{
   static public final int INF = Integer.MAX_VALUE;

   protected int cooldown;
   protected int next;
   protected int count;
   protected Vector2D direction;

   protected AbstractParticleGenerator()
   {
   }

   public AbstractParticleGenerator(int cooldown)
   {
      this(cooldown, INF);
   }

   public AbstractParticleGenerator(int cooldown, int count)
   {
      this.cooldown = cooldown;
      this.next = cooldown;
      this.count = count;
   }

   public void setDirection(Vector2D direction)
   {
      this.direction = direction;
   }

   public Collection<ParticleEntity> update(int delta)
   {
      Collection<ParticleEntity> res = new ArrayList<ParticleEntity>();
      next -= delta;
      while (count > 0 && next <= 0)
      {
         res.add(createParticle());
         next += cooldown;
         if (count != INF)
         {
            count--;
         }
      }
      return res;
   }

   protected abstract ParticleEntity createParticle();
   public abstract AbstractParticleGenerator copy();
}
