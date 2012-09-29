package com.greenteam.huntjumper.model.bonuses;

import com.greenteam.huntjumper.model.IMapObject;
import com.greenteam.huntjumper.model.IMapObjectCreator;
import com.greenteam.huntjumper.utils.Point;

import java.lang.reflect.InvocationTargetException;

/**
 * User: GreenTea Date: 29.09.12 Time: 14:38
 */
public class BonusCreator implements IMapObjectCreator
{
   private String bonusClassName;
   private Point pos;

   public BonusCreator(String bonusClassName, Point pos)
   {
      this.bonusClassName = bonusClassName;
      this.pos = pos;
   }

   @Override
   public IMapObject create()
   {
      try
      {
         Class bonusClass = Class.forName(bonusClassName);
         return (IMapObject)bonusClass.getConstructor(Point.class).newInstance(pos);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
