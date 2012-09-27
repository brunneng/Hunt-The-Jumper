package com.greenteam.huntjumper.commands;

import com.greenteam.huntjumper.match.MapObjectId;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.JumperRole;

/**
 * User: GreenTea Date: 28.09.12 Time: 1:26
 */
public class HuntingWithEscapingCollisionCommand extends Command
{
   private MapObjectId escapingId;

   public HuntingWithEscapingCollisionCommand(MapObjectId huntingId, MapObjectId escapingId,
                                               int commandTime)
   {
      super(huntingId, CommandType.HUNTING_WITH_ESCAPING_COLLISION, commandTime);
      this.escapingId = escapingId;
   }

   @Override
   public void execute(IEventExecutionContext context)
   {
      Jumper hunting = context.getMapObject(getObjectId());
      Jumper escaping = context.getMapObject(escapingId);

      hunting.setJumperRole(JumperRole.Escaping);
      escaping.setJumperRole(JumperRole.Hunting);
   }

   @Override
   public boolean isRollbackSupported()
   {
      return true;
   }

   @Override
   public void rollback(IEventExecutionContext context)
   {
      Jumper hunting = context.getMapObject(getObjectId());
      Jumper escaping = context.getMapObject(escapingId);

      hunting.setJumperRole(JumperRole.Hunting);
      escaping.setJumperRole(JumperRole.Escaping);
   }
}
