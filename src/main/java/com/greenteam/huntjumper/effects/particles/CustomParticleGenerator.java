package com.greenteam.huntjumper.effects.particles;

/**
 * User: GreenTea Date: 23.06.12 Time: 19:13
 */
public class CustomParticleGenerator extends AbstractParticleGenerator
{
   protected IParticleCreator creator;

   public CustomParticleGenerator(int cooldown, IParticleCreator creator)
   {
      super(cooldown, INF);
      this.creator = creator;
   }

   public CustomParticleGenerator(int cooldown, int count, IParticleCreator creator)
   {
      super(cooldown, count);
      this.creator = creator;
   }

   @Override
   protected ParticleEntity createParticle()
   {
      return creator.createParticle();
   }

   @Override
   public AbstractParticleGenerator copy()
   {
      return new CustomParticleGenerator(cooldown, count, creator);
   }
}
