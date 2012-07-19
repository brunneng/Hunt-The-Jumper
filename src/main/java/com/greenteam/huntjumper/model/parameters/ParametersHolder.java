package com.greenteam.huntjumper.model.parameters;

import java.util.EnumMap;

/**
 * User: GreenTea Date: 19.07.12 Time: 22:09
 */
public class ParametersHolder
{
   private EnumMap<ParameterType, Parameter> parameters = new EnumMap<>(ParameterType.class);

   public void addParameter(Parameter parameter)
   {
      parameters.put(parameter.getType(), parameter);
   }

   public Parameter getParameter(ParameterType type)
   {
      return parameters.get(type);
   }

}
