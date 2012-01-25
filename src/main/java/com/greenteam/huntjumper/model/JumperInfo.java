package com.greenteam.huntjumper.model;

import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Vector2D;
import net.phys2d.raw.Body;

/**
 * User: GreenTea Date: 25.01.12 Time: 20:49
 */
public class JumperInfo
{
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
