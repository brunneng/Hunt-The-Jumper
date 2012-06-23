package com.greenteam.huntjumper.effects.particles;

/**
 * User: GreenTea Date: 23.06.12 Time: 18:52
 */
public class TypedParticleGenerator extends AbstractParticleGenerator
{
   protected ParticleType type;

   protected TypedParticleGenerator()
   {
   }

   public TypedParticleGenerator(ParticleType type, int cooldown)
   {
      super(cooldown, INF);
      this.type = type;
   }

   public TypedParticleGenerator(ParticleType type, int cooldown, int count)
   {
      super(cooldown, count);
      this.type = type;
   }

   @Override
   protected ParticleEntity createParticle()
   {
      return ParticleEntity.Builder.createEntity(type);
   }

   @Override
   public AbstractParticleGenerator copy()
   {
      return null;
   }
}
