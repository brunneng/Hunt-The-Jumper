package com.greenteam.huntjumper.commands;

import com.greenteam.huntjumper.match.MapObjectId;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.bonuses.AbstractPhysBonus;
import com.greenteam.huntjumper.model.bonuses.IBonus;
import org.apache.commons.lang.NotImplementedException;

/**
 * User: GreenTea Date: 28.09.12 Time: 0:54
 */
public class BonusTakeCommand extends Command
{
   private MapObjectId jumperId;

   public BonusTakeCommand(MapObjectId bonusId, MapObjectId jumperId, int commandTime)
   {
      super(bonusId, CommandType.BONUS_TAKEN, commandTime);
      this.jumperId = jumperId;
   }

   @Override
   public void execute(IEventExecutionContext context)
   {
      IBonus bonus = context.getMapObject(getObjectId());
      Jumper jumper = context.getMapObject(jumperId);
      bonus.onBonusTaken(context.getMatch(), jumper);
   }

   @Override
   public boolean isRollbackSupported()
   {
      return true;
   }

   @Override
   public void rollback(IEventExecutionContext context)
   {
      throw new NotImplementedException();
   }
}
