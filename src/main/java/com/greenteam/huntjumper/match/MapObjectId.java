package com.greenteam.huntjumper.match;

import java.io.Serializable;

/**
 * User: GreenTea Date: 22.09.12 Time: 9:49
 */
public class MapObjectId implements Serializable
{
   public final int id;
   public final MapObjectType type;

   public MapObjectId(int id, MapObjectType type)
   {
      this.id = id;
      this.type = type;
   }
}
