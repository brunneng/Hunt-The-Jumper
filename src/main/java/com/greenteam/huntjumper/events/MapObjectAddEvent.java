package com.greenteam.huntjumper.events;

import com.greenteam.huntjumper.match.MapObjectId;
import com.greenteam.huntjumper.model.IMapObject;

/**
 * User: GreenTea Date: 23.09.12 Time: 14:54
 */
public class MapObjectAddEvent extends Event
{
   private IMapObject addedObject;
   public MapObjectAddEvent(IMapObject addedObject, int eventTime)
   {
      super(addedObject.getIdentifier(), EventType.MAP_OBJECT_ADDED, eventTime);
      this.addedObject = addedObject;
   }

   @Override
   public void execute(IEventExecutionContext context)
   {
      validateSameTime(context);
      context.addMapObject(addedObject);
   }

   @Override
   public boolean isRollbackSupported()
   {
      return true;
   }

   @Override
   public void rollback(IEventExecutionContext context)
   {
      context.removeMapObject(getObjectId());
   }
}
