package com.greenteam.huntjumper.commands;

import com.greenteam.huntjumper.match.MapObjectId;
import com.greenteam.huntjumper.model.IMapObject;
import com.greenteam.huntjumper.utils.Vector2D;

/**
 * User: GreenTea Date: 30.09.12 Time: 15:17
 */
public class MoveCommand extends Command
{
   private MapObjectId movedObjectId;
   private Vector2D force;

   public MoveCommand(MapObjectId movedObjectId, Vector2D force)
   {
      super(CommandType.MOVE);
      this.movedObjectId = movedObjectId;
      this.force = force;
   }

   @Override
   public MapObjectId[] getObjectIds()
   {
      return new MapObjectId[] {movedObjectId};  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public void execute(ICommandExecutionContext context)
   {
      IMapObject object = context.getMapObject(movedObjectId);
      if (object != null)
      {
         object.getBody().addForce(force.toVector2f());
      }
   }

   @Override
   public boolean isRollbackSupported()
   {
      return false;
   }

   @Override
   public void rollback(ICommandExecutionContext context)
   {
      throw new RuntimeException("Operation not supported!");
   }
}
