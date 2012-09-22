package com.greenteam.huntjumper.model;

import com.greenteam.huntjumper.match.MapObjectId;

/**
 * User: GreenTea Date: 22.09.12 Time: 10:03
 */
public abstract class AbstractMapObject implements IMapObject
{
   private MapObjectId identifier;

   public AbstractMapObject(MapObjectId identifier)
   {
      this.identifier = identifier;
   }

   public MapObjectId getIdentifier()
   {
      return identifier;
   }
}
