package com.greenteam.huntjumper.model.parameters;

/**
 * User: GreenTea Date: 21.07.12 Time: 16:02
 */
public class MultiplicationParameterEffect implements IParameterEffect<Float>
{
   private String name;
   private Float multiplier;

   public MultiplicationParameterEffect(String name, Float multiplier)
   {
      this.name = name;
      this.multiplier = multiplier;
   }

   @Override
   public String getEffectName()
   {
      return name;
   }

   @Override
   public Float transformParameter(Float defaultValue, Float currentValue)
   {
      return currentValue * multiplier;
   }
}
