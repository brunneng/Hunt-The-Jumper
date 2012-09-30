package com.greenteam.huntjumper.commands;

import com.greenteam.huntjumper.match.MapObjectId;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.JumperRole;

/**
 * User: GreenTea Date: 28.09.12 Time: 1:35
 */
public class HuntingForEveryOneCollisionCommand extends Command
{
   private MapObjectId hunterForEveryOneId;

   public HuntingForEveryOneCollisionCommand(MapObjectId hunterForEveryOneId)
   {
      super(CommandType.HUNTING_FOR_EVERYONE_COLLISION);
      this.hunterForEveryOneId = hunterForEveryOneId;
   }

   @Override
   public MapObjectId[] getObjectIds()
   {
      return new MapObjectId[] {hunterForEveryOneId};
   }

   @Override
   public void execute(ICommandExecutionContext context)
   {
      Jumper hunter = context.getMapObject(hunterForEveryOneId);
      hunter.setJumperRole(JumperRole.Escaping);
      for (Jumper otherJumper : hunter.getOtherJumpers())
      {
         otherJumper.setJumperRole(JumperRole.Hunting);
      }
   }

   @Override
   public boolean isRollbackSupported()
   {
      return true;
   }

   @Override
   public void rollback(ICommandExecutionContext context)
   {
      Jumper hunter = context.getMapObject(hunterForEveryOneId);
      hunter.setJumperRole(JumperRole.HuntingForEveryone);
      for (Jumper otherJumper : hunter.getOtherJumpers())
      {
         otherJumper.setJumperRole(JumperRole.EscapingFromHunter);
      }
   }
}
