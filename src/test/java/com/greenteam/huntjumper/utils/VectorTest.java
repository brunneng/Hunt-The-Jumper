package com.greenteam.huntjumper.utils;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA. User: GreenTea Date: 15.01.12 Time: 16:35 To change this template use
 * File | Settings | File Templates.
 */
public class VectorTest
{
   @Test
   public void testRotation()
   {
      Vector2D v = new Vector2D(100, 0);

      v = v.rotate(90);
      Assert.assertTrue(Utils.equals(v.angle(), 90.0f));

      v = v.rotate(90);
      Assert.assertTrue(Utils.equals(v.angle(), 180.0f));

      v = v.rotate(90);
      Assert.assertTrue(Utils.equals(v.angle(), 270.0f));

      v = v.rotate(90);
      Assert.assertTrue(Utils.equals(v.angle(), 0.0f));
   }
}
