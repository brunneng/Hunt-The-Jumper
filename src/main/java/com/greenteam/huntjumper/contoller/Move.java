package com.greenteam.huntjumper.contoller;

import com.greenteam.huntjumper.utils.Vector2D;

/**
 * User: GreenTea Date: 24.01.12 Time: 22:11
 */
public class Move
{
   public final Vector2D forceDirection;
   public final boolean accumulating;

   public Move(Vector2D forceDirection, boolean accumulating)
   {
      this.forceDirection = forceDirection;
      this.accumulating = accumulating;
   }
}
