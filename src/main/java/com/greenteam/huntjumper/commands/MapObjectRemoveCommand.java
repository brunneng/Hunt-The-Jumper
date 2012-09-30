package com.greenteam.huntjumper.commands;

import com.greenteam.huntjumper.match.MapObjectId;
import com.greenteam.huntjumper.model.IMapObject;

/**
 * User: GreenTea Date: 23.09.12 Time: 14:06
 */
public class MapObjectRemoveCommand extends Command
{
   private MapObjectId removedObjectId;
   private IMapObject removedObject;

   public MapObjectRemoveCommand(MapObjectId removedObjectId)
   {
      super(CommandType.MAP_OBJECT_REMOVED);
      this.removedObjectId = removedObjectId;
   }


   @Override
   public void execute(ICommandExecutionContext context)
   {
      validateSameTime(context);
      removedObject = context.getMapObject(removedObjectId);
      context.removeMapObject(removedObjectId);
   }

   @Override
   public MapObjectId[] getObjectIds()
   {
      return new MapObjectId[] {removedObjectId};
   }

   @Override
   public boolean isRollbackSupported()
   {
      return true;
   }

   @Override
   public void rollback(ICommandExecutionContext context)
   {
      context.addMapObject(removedObject);
   }
}
