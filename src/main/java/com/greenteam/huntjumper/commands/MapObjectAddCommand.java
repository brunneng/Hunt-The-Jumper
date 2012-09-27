package com.greenteam.huntjumper.commands;

import com.greenteam.huntjumper.model.IMapObject;

/**
 * User: GreenTea Date: 23.09.12 Time: 14:54
 */
public class MapObjectAddCommand extends Command
{
   private IMapObject addedObject;
   public MapObjectAddCommand(IMapObject addedObject, int commandTime)
   {
      super(addedObject.getIdentifier(), CommandType.MAP_OBJECT_ADDED, commandTime);
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
