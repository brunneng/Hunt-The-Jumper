package com.greenteam.huntjumper.match;

import com.greenteam.huntjumper.effects.Effect;
import org.newdawn.slick.Graphics;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * User: GreenTea Date: 23.06.12 Time: 19:01
 */
public class EffectsContainer
{
   private static EffectsContainer instance;

   public static EffectsContainer getInstance()
   {
      if (instance == null)
      {
         instance = new EffectsContainer();
      }

      return instance;
   }

   private LinkedList<Effect> effects = new LinkedList<Effect>();

   private EffectsContainer()
   {

   }

   public void addEffect(Effect effect)
   {
      effects.add(effect);
   }

   public void addAllEffects(Collection<? extends Effect> newEffects)
   {
      this.effects.addAll(newEffects);
   }

   void clearEffects()
   {
      effects.clear();
   }

   void updateEffects(int dt)
   {
      Iterator<Effect> i = effects.iterator();
      while (i.hasNext())
      {
         Effect effect = i.next();
         effect.update(dt);
         if (effect.isFinished())
         {
            i.remove();
         }
      }
   }

   void drawEffects(Graphics g)
   {
      for (Effect effect : effects)
      {
         effect.draw(g);
      }
   }
}
