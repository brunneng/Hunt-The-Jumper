package com.greenteam.huntjumper.commands;

import com.greenteam.huntjumper.match.MapObjectId;
import com.greenteam.huntjumper.model.IMapObject;

/**
 * User: GreenTea Date: 23.09.12 Time: 14:06
 */
public class MapObjectRemoveCommand extends Command
{
   private IMapObject removedObject;

   public MapObjectRemoveCommand(MapObjectId objectId, int commandTime)
   {
      super(objectId, CommandType.MAP_OBJECT_REMOVED, commandTime);
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
