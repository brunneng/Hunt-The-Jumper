package com.greenteam.huntjumper.commands;

import com.greenteam.huntjumper.match.MapObjectId;

import java.io.Serializable;

/**
 * User: GreenTea Date: 23.09.12 Time: 11:33
 */
public abstract class Command implements Serializable
{
   private MapObjectId objectId;
   private int commandTime;
   private CommandType type;

   public Command(MapObjectId objectId, CommandType type, int commandTime)
   {
      this.objectId = objectId;
      this.type = type;
      this.commandTime = commandTime;
   }

   public MapObjectId getObjectId()
   {
      return objectId;
   }
   public void setObjectId(MapObjectId objectId)
   {
      this.objectId = objectId;
   }

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

   protected void validateSameTime(IEventExecutionContext context)
   {
      if (getCommandTime() != context.getCurrentGameTime())
      {
         throw new RuntimeException("Execution of event from different time is not supported");
      }
   }

   public abstract void execute(IEventExecutionContext context);

   public abstract boolean isRollbackSupported();
   public abstract void rollback(IEventExecutionContext context);
}
