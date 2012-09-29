package com.greenteam.huntjumper.commands;

import com.greenteam.huntjumper.match.MapObjectId;
import com.greenteam.huntjumper.model.IMapObject;
import com.greenteam.huntjumper.model.IMapObjectCreator;

/**
 * User: GreenTea Date: 23.09.12 Time: 14:54
 */
public class MapObjectAddCommand extends Command
{
   transient private IMapObject addedObject;
   private IMapObjectCreator mapObjectCreator;

   public MapObjectAddCommand(IMapObjectCreator mapObjectCreator, int commandTime)
   {
      super(CommandType.MAP_OBJECT_ADDED, commandTime);
      this.mapObjectCreator = mapObjectCreator;
   }

   @Override
   public MapObjectId[] getObjectIds()
   {
      return new MapObjectId[0];
   }

   @Override
   public void execute(ICommandExecutionContext context)
   {
      validateSameTime(context);

      addedObject = mapObjectCreator.create();
      context.addMapObject(addedObject);
   }

   @Override
   public boolean isRollbackSupported()
   {
      return true;
   }

   @Override
   public void rollback(ICommandExecutionContext context)
   {
      context.removeMapObject(addedObject.getIdentifier());
   }
}
