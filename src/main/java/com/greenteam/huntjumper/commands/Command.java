package com.greenteam.huntjumper.commands;

import com.greenteam.huntjumper.match.MapObjectId;

import java.io.Serializable;

/**
 * User: GreenTea Date: 23.09.12 Time: 11:33
 */
public abstract class Command implements Serializable
{
   private int commandTime;
   private CommandType type;

   public Command(CommandType type)
   {
      this.type = type;
   }

   public abstract MapObjectId[] getObjectIds();

   public int getCommandTime()
   {
      return commandTime;
   }
   public void setCommandTime(int commandTime)
   {
      this.commandTime = commandTime;
   }

   public CommandType getType()
   {
      return type;
   }
   public void setType(CommandType type)
   {
      this.type = type;
   }

   protected void validateSameTime(ICommandExecutionContext context)
   {
      if (getCommandTime() != context.getCurrentGameTime())
      {
         throw new RuntimeException("Execution of event from different time is not supported");
      }
   }

   public abstract void execute(ICommandExecutionContext context);

   public abstract boolean isRollbackSupported();
   public abstract void rollback(ICommandExecutionContext context);
}
