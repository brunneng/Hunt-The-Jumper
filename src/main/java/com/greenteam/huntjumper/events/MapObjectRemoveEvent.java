package com.greenteam.huntjumper.events;

import com.greenteam.huntjumper.match.MapObjectId;
import com.greenteam.huntjumper.model.IMapObject;

/**
 * User: GreenTea Date: 23.09.12 Time: 14:06
 */
public class MapObjectRemoveEvent extends Event
{
   private IMapObject removedObject;

   public MapObjectRemoveEvent(MapObjectId objectId, int eventTime)
   {
      super(objectId, EventType.MAP_OBJECT_REMOVED, eventTime);
   }


   @Override
   public void execute(IEventExecutionContext context)
   {
      validateSameTime(context);
      removedObject = context.getMapObject(getObjectId());
      context.removeMapObject(getObjectId());
   }

   @Override
   public boolean isRollbackSupported()
   {
      return true;
   }

   @Override
   public void rollback(IEventExecutionContext context)
   {
      context.addMapObject(removedObject);
   }
}
