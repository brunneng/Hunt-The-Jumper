package com.greenteam.huntjumper.model;

import com.greenteam.huntjumper.utils.Point;
import net.phys2d.math.ROVector2f;

/**
 * User: GreenTea Date: 24.06.12 Time: 1:03
 */
public class PreviousPosition
{
   private long time;
   private Point pos;

   public PreviousPosition(long time, Point pos)
   {
      this.time = time;
      this.pos = pos;
   }

   public long getTime()
   {
      return time;
   }

   public void setTime(long time)
   {
      this.time = time;
   }

   public Point getPos()
   {
      return pos;
   }

   public void setPos(Point pos)
   {
      this.pos = pos;
   }
}
