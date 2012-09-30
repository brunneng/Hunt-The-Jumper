package com.greenteam.huntjumper.commands;

import com.greenteam.huntjumper.match.MapObjectId;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.bonuses.IBonus;
import org.apache.commons.lang.NotImplementedException;

/**
 * User: GreenTea Date: 28.09.12 Time: 0:54
 */
public class BonusTakeCommand extends Command
{
   private MapObjectId jumperId;
   private MapObjectId bonusId;

   public BonusTakeCommand(MapObjectId bonusId, MapObjectId jumperId)
   {
      super(CommandType.BONUS_TAKEN);
      this.jumperId = jumperId;
      this.bonusId = bonusId;
   }

   @Override
   public MapObjectId[] getObjectIds()
   {
      return new MapObjectId[] {jumperId, bonusId};
   }

   @Override
   public void execute(ICommandExecutionContext context)
   {
      IBonus bonus = context.getMapObject(bonusId);
      Jumper jumper = context.getMapObject(jumperId);
      bonus.onBonusTaken(context.getMatch(), jumper,
              context.getCurrentGameTime() - getCommandTime());
   }

   @Override
   public boolean isRollbackSupported()
   {
      return true;
   }

   @Override
   public void rollback(ICommandExecutionContext context)
   {
      IBonus bonus = context.getMapObject(bonusId);
      Jumper jumper = context.getMapObject(jumperId);
      bonus.revertTakingBonus(context.getMatch(), jumper);
   }
}
