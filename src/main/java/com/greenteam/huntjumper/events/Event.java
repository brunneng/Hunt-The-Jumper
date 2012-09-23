package com.greenteam.huntjumper.events;

import com.greenteam.huntjumper.match.MapObjectId;

import java.io.Serializable;

/**
 * User: GreenTea Date: 23.09.12 Time: 11:33
 */
public abstract class Event implements Serializable
{
   private MapObjectId objectId;
   private int eventTime;
   private EventType type;

   protected Event(MapObjectId objectId, EventType type, int eventTime)
   {
      this.objectId = objectId;
      this.type = type;
      this.eventTime = eventTime;
   }

   public MapObjectId getObjectId()
   {
      return objectId;
   }
   public void setObjectId(MapObjectId objectId)
   {
      this.objectId = objectId;
   }

   public int getEventTime()
   {
      return eventTime;
   }
   public void setEventTime(int eventTime)
   {
      this.eventTime = eventTime;
   }

   public EventType getType()
   {
      return type;
   }
   public void setType(EventType type)
   {
      this.type = type;
   }

   protected void validateSameTime(IEventExecutionContext context)
   {
      if (getEventTime() != context.getCurrentGameTime())
      {
         throw new RuntimeException("Execution of event from different time is not supported");
      }
   }

   public abstract void execute(IEventExecutionContext context);

   public abstract boolean isRollbackSupported();
   public abstract void rollback(IEventExecutionContext context);
}
