package com.greenteam.huntjumper.commands;

import com.greenteam.huntjumper.match.MapObjectId;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.JumperRole;

/**
 * User: GreenTea Date: 28.09.12 Time: 1:35
 */
public class HuntingForEveryOneCollisionCommand extends Command
{

   public HuntingForEveryOneCollisionCommand(MapObjectId hunterForEveryOneId,
                                                int commandTime)
   {
      super(hunterForEveryOneId, CommandType.HUNTING_FOR_EVERYONE_COLLISION, commandTime);
   }

   @Override
   public void execute(IEventExecutionContext context)
   {
      Jumper hunter = context.getMapObject(getObjectId());
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
   public void rollback(IEventExecutionContext context)
   {
      Jumper hunter = context.getMapObject(getObjectId());
      hunter.setJumperRole(JumperRole.HuntingForEveryone);
      for (Jumper otherJumper : hunter.getOtherJumpers())
      {
         otherJumper.setJumperRole(JumperRole.EscapingFromHunter);
      }
   }
}
