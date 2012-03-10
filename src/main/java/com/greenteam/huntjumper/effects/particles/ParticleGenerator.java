package com.greenteam.huntjumper.effects.particles;

import com.greenteam.huntjumper.utils.Vector2D;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA. User: Ivan Date: 15.01.2011 Time: 14:55:23 To change this template use
 * File | Settings | File Templates.
 */
public class ParticleGenerator
{
   static public final int INF = Integer.MAX_VALUE;

   protected ParticleType type;
   protected int cooldown;
   protected int next;
   protected int count;
   protected Vector2D direction;

   protected ParticleGenerator()
   {
   }

   public ParticleGenerator(ParticleType type, int cooldown)
   {
      this(type, cooldown, INF);
   }

   public ParticleGenerator(ParticleType type, int cooldown, int count)
   {
      this.type = type;
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
         res.add(ParticleEntity.Builder.createEntity(type));
         next += cooldown;
         if (count != INF)
         {
            count--;
         }
      }
      return res;
   }

   public ParticleGenerator copy()
   {
      return new ParticleGenerator(this.type, this.cooldown, this.count);
   }
}
