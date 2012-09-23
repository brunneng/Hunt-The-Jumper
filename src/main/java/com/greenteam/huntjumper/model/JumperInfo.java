package com.greenteam.huntjumper.model;

import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Vector2D;
import net.phys2d.raw.Body;

import java.util.List;

/**
 * User: GreenTea Date: 25.01.12 Time: 20:49
 */
public class JumperInfo extends AbstractMapObject
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

   public static JumperInfo getMostFar(List<JumperInfo> infos, JumperRole jumperRole, Point p)
   {
      float maxDist = 0;
      JumperInfo nearest = null;

      for (JumperInfo info : infos)
      {
         if (jumperRole != null && !info.jumperRole.equals(jumperRole))
         {
            continue;
         }

         float dist = p.distanceTo(info.position);
         if (dist > maxDist)
         {
            maxDist = dist;
            nearest = info;
         }
      }
      return nearest;
   }
   
   public final Point position;
   public final Vector2D velocity;
   public final float angularVelocity;
   public final JumperRole jumperRole;

   @Override
   public Point getPosition()
   {
      return position;
   }

   @Override
   public Body getBody()
   {
      throw new RuntimeException("Operation not supported");
   }

   public JumperInfo(Jumper jumper)
   {
      super(jumper.getIdentifier());
      Body body = jumper.getBody();
      position = new Point(body.getPosition());
      velocity = new Vector2D(body.getVelocity());
      angularVelocity = body.getAngularVelocity();
      jumperRole = jumper.getJumperRole();
   }
}
