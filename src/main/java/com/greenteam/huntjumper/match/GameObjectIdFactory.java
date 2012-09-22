package com.greenteam.huntjumper.match;


import java.util.EnumMap;
import java.util.Map;

/**
 * User: GreenTea Date: 22.09.12 Time: 9:52
 */
public class GameObjectIdFactory
{
   private static GameObjectIdFactory factory = new GameObjectIdFactory();

   private Map<MapObjectType, Integer> idCounters = new EnumMap<>(MapObjectType.class);

   public static GameObjectIdFactory getInstance()
   {
      return factory;
   }

   private GameObjectIdFactory()
   {
   }

   public MapObjectId getNextId(MapObjectType type)
   {
      Integer currId = idCounters.get(type);
      if (currId == null)
      {
         currId = 0;
      }

      Integer newId = currId + 1;
      idCounters.put(type, newId);

      return new MapObjectId(newId, type);
   }
}
