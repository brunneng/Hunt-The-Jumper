package com.greenteam.huntjumper.model;

import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Vector2D;
import net.phys2d.raw.Body;

import java.util.List;

/**
 * User: GreenTea Date: 25.01.12 Time: 20:49
 */
public class JumperInfo
{
   public static JumperInfo getNearest(List<JumperInfo> infos, JumperRole jumperRole, Point p)
   {
      float minDist = Float.MAX_VALUE;
      JumperInfo nearest = null;

      for (JumperInfo info : infos)
      {
         if (jumperRole != null && !info.jumperRole.equals(jumperRole))
         {
            continue;
         }

         float dist = p.distanceTo(info.position);
         if (dist < minDist)
         {
            minDist = dist;
            nearest = info;
         }
      }
      return nearest;
   }
   
   public final Point position;
   public final Vector2D velocity;
   public final float angularVelocity;
   public final JumperRole jumperRole;

   public JumperInfo(Jumper jumper)
   {
      Body body = jumper.getBody();
      position = new Point(body.getPosition());
      velocity = new Vector2D(body.getVelocity());
      angularVelocity = body.getAngularVelocity();
      jumperRole = jumper.getJumperRole();
   }
}
