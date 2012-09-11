package com.greenteam.huntjumper.parameters;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * User: GreenTea Date: 11.09.12 Time: 22:05
 */
public class ParametersUtils
{
   private static final Map<Class, Class> primitiveToCover = new HashMap<>();
   static
   {
      primitiveToCover.put(Integer.TYPE, Integer.class);
      primitiveToCover.put(Long.TYPE, Long.class);
      primitiveToCover.put(Float.TYPE, Float.class);
      primitiveToCover.put(Double.TYPE, Double.class);
      primitiveToCover.put(Boolean.TYPE, Boolean.class);
   }
   private static Class getCoverClass(Class primitive)
   {
      return primitiveToCover.get(primitive);
   }

   private static Class getSupportedType(Field field)
   {
      Class res = null;
      Class type = field.getType();
      if (!type.isPrimitive())
      {
         if (primitiveToCover.values().contains(type))
         {
            res = type;
         }
      }
      else
      {
         res = getCoverClass(type);
      }
      return res;
   }

   public static void overrideParameters(Class parametersClass, String pathToPropertiesFile)
   {
      File file = new File(pathToPropertiesFile);
      if (!file.exists())
      {
         System.out.println("Properties file " + pathToPropertiesFile + " is not exists.");
         return;
      }

      Properties props = new Properties();
      try (FileInputStream in = new FileInputStream(file))
      {
         props.load(in);

         for (Object nameObj : props.keySet())
         {
            String name = (String)nameObj;
            Field field;
            try
            {
               field = parametersClass.getField(name);
            }
            catch (NoSuchFieldException e)
            {
               System.out.println("Field " + name + " is not found!");
               continue;
            }

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);

            boolean finalRemoved = false;
            if ((field.getModifiers() & ~Modifier.FINAL) != field.getModifiers())
            {
               modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
               finalRemoved = true;
            }

            Class supportedType = getSupportedType(field);
            if (supportedType == null)
            {
               System.out.println("Type " + field.getType() + " of field " + name +
                       " is not supported");
               continue;
            }
            Method valueOf = supportedType.getMethod("valueOf", String.class);
            Object value = valueOf.invoke(null, props.get(name));
            field.set(null, value);

            if (finalRemoved)
            {
               modifiersField.setInt(field, field.getModifiers() | Modifier.FINAL);
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
