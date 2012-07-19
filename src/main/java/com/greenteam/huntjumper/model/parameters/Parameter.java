package com.greenteam.huntjumper.model.parameters;

import java.util.ArrayList;
import java.util.List;

/**
 * User: GreenTea Date: 19.07.12 Time: 22:10
 */
public class Parameter<T>
{
   private ParameterType type;
   private T value;

   private List<IParameterEffect<T>> effects = new ArrayList<>();

   public Parameter(ParameterType type, T value)
   {
      this.type = type;
      this.value = value;
   }

   public void addEffect(IParameterEffect<T> effect)
   {
      effects.add(effect);
   }

   public void removeEffect(IParameterEffect<T> effect)
   {
      effects.remove(effect);
   }

   public void removeEffect(String effectName)
   {
      for (int i = 0; i < effects.size(); ++i)
      {
         IParameterEffect<T> effect = effects.get(i);
         if (effect.getEffectName().equals(effectName))
         {
            effects.remove(i);
            break;
         }
      }
   }

   public ParameterType getType()
   {
      return type;
   }

   public T getValue()
   {
      T currValue = value;
      for (IParameterEffect<T> effect : effects)
      {
         currValue = effect.transformParameter(value, currValue);
      }

      return currValue;
   }
}
