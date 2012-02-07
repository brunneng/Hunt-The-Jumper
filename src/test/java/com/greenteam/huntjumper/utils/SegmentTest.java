package com.greenteam.huntjumper.utils;

import junit.framework.Assert;
import org.junit.Test;

/**
 * User: GreenTea Date: 29.01.12 Time: 18:33
 */
public class SegmentTest
{
   @Test
   public void testIntersection1()
   {
      Segment s1 = new Segment(new Point(1, 0), new Point(1, 3));
      Segment s2 = new Segment(new Point(0, 1), new Point(3, 1));
      
      Assert.assertEquals(new Point(1, 1), s1.intersectionWith(s2));
   }

   @Test
   public void testIntersection2()
   {
      Segment s1 = new Segment(new Point(1, 0), new Point(1, 3));
      Segment s2 = new Segment(new Point(0, 0), new Point(2, 2));

      Assert.assertEquals(new Point(1, 1), s1.intersectionWith(s2));
   }

   @Test
   public void testIntersection3()
   {
      Segment s1 = new Segment(new Point(0, 0), new Point(5, 6));
      Segment s2 = new Segment(new Point(0, 0), new Point(-2, 5));

      Assert.assertEquals(new Point(0, 0), s1.intersectionWith(s2));
   }

   @Test
   public void testIntersection4()
   {
      Segment s1 = new Segment(new Point(4,  2), new Point(-2, 0));
      Segment s2 = new Segment(new Point(0, -2), new Point(2, 4));

      Assert.assertEquals(new Point(1, 1), s1.intersectionWith(s2));
   }


   @Test
   public void testIntersection5()
   {
      Segment s1 = new Segment(new Point(0,  3750.5f), new Point(3800, 3750.5f));
      Segment s2 = new Segment(new Point(1700.61f, 0), new Point(1700.61f, 3800));

      Assert.assertEquals(new Point(1700.61f, 3750.5f), s1.intersectionWith(s2));
   }

   @Test
   public void testNoIntersection1()
   {
      Segment s1 = new Segment(new Point(1, 0), new Point(1, 3));
      Segment s2 = new Segment(new Point(1, 0), new Point(1, 3));

      Assert.assertEquals(null, s1.intersectionWith(s2));
   }

   @Test
   public void testNoIntersection2()
   {
      Segment s1 = new Segment(new Point(1, 0), new Point(1, 3));
      Segment s2 = new Segment(new Point(2, 0), new Point(2, 3));

      Assert.assertEquals(null, s1.intersectionWith(s2));
   }

   @Test
   public void testNoIntersection3()
   {
      Segment s1 = new Segment(new Point(0, 0), new Point(3, 3));
      Segment s2 = new Segment(new Point(0, -2), new Point(1, 0));

      Assert.assertEquals(null, s1.intersectionWith(s2));
   }
   
   @Test
   public void testDistanceInSegment1()
   {
      Segment s1 = new Segment(new Point(0, 0), new Point(5, 0));
      float len = s1.distanceTo(new Point(1, 3));
      
      Assert.assertTrue(Utils.equals(len, 3));
   }

   @Test
   public void testDistanceInSegment2()
   {
      Segment s1 = new Segment(new Point(0, 0), new Point(0, 5));
      float len = s1.distanceTo(new Point(3, 1));

      Assert.assertTrue(Utils.equals(len, 3));
   }

   @Test
   public void testDistanceInSegment3()
   {
      Segment s1 = new Segment(new Point(0, 0), new Point(4, 2));
      float len = s1.distanceTo(new Point(1, 3));

      Assert.assertTrue(Utils.equals(len, Math.sqrt(5)));
   }

   @Test
   public void testDistanceInSegment4()
   {
      Segment s1 = new Segment(new Point(1245.5f, 3769.5f), new Point(1265.5f, 3780.5f));
      float len = s1.distanceTo(new Point(1255.5f,3774.5f));

      Assert.assertTrue(len < 10);
   }

   @Test
   public void testDistanceOutSegment1()
   {
      Segment s1 = new Segment(new Point(0, 0), new Point(5, 0));
      float len = s1.distanceTo(new Point(-1, 3));

      Assert.assertTrue(Utils.equals(len, Math.sqrt(10)));
   }

   @Test
   public void testDistanceOutSegment2()
   {
      Segment s1 = new Segment(new Point(0, 0), new Point(0, 5));
      float len = s1.distanceTo(new Point(3, -1));

      Assert.assertTrue(Utils.equals(len, Math.sqrt(10)));
   }

   @Test
   public void testDistanceOutSegment3()
   {
      Segment s1 = new Segment(new Point(0, 0), new Point(4, 2));
      float len = s1.distanceTo(new Point(4, 4));

      Assert.assertTrue(Utils.equals(len, 2));
   }

   @Test
   public void testDistanceInBorderSegment1()
   {
      Segment s1 = new Segment(new Point(0, 0), new Point(5, 0));
      float len = s1.distanceTo(new Point(0, 3));

      Assert.assertTrue(Utils.equals(len, 3));
   }

   @Test
   public void testDistanceInBorderSegment2()
   {
      Segment s1 = new Segment(new Point(0, 0), new Point(5, 0));
      float len = s1.distanceTo(new Point(5, -3));

      Assert.assertTrue(Utils.equals(len, 3));
   }

   @Test
   public void testDistanceInBorderSegment3()
   {
      Segment s1 = new Segment(new Point(0, 0), new Point(5, 0));
      float len = s1.distanceTo(new Point(-1, 0));

      Assert.assertTrue(Utils.equals(len, 1));
   }

   @Test
   public void testPerpendicular1()
   {
      Segment s1 = new Segment(new Point(0, 0), new Point(5, 0));
      Vector2D res = s1.perpendicularToLine(new Point(1, 1));

      Assert.assertEquals(new Vector2D(0f, -1f), res);
   }

   @Test
   public void testPerpendicular2()
   {
      Segment s1 = new Segment(new Point(0, 0), new Point(0, 5));
      Vector2D res = s1.perpendicularToLine(new Point(1, 1));

      Assert.assertEquals(new Vector2D(-1f, 0f), res);
   }

   @Test
   public void testPerpendicular3()
   {
      Segment s1 = new Segment(new Point(0, 0), new Point(0, 5));
      Vector2D res = s1.perpendicularToLine(new Point(1, 10));

      Assert.assertEquals(new Vector2D(-1f, 0f), res);
   }

   @Test
   public void testPerpendicular4()
   {
      Segment s1 = new Segment(new Point(0, 3), new Point(3, 2));
      Vector2D res = s1.perpendicularToLine(new Point(2, -1));

      Assert.assertEquals(new Vector2D(1f, 3f), res);
   }
}
