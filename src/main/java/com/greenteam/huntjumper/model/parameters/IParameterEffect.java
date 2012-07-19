package com.greenteam.huntjumper.model.parameters;

/**
 * User: GreenTea Date: 19.07.12 Time: 22:12
 */
public interface IParameterEffect<T>
{
   String getEffectName();
   T transformParameter(T defaultValue, T currentValue);
}
