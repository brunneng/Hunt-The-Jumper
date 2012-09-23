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

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (o == null || getClass() != o.getClass())
      {
         return false;
      }

      MapObjectId that = (MapObjectId) o;

      if (id != that.id)
      {
         return false;
      }
      if (type != that.type)
      {
         return false;
      }

      return true;
   }

   @Override
   public int hashCode()
   {
      int result = id;
      result = 31 * result + (type != null ? type.hashCode() : 0);
      return result;
   }
}
