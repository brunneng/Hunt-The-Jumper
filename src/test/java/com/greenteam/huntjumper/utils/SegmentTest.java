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
      
      Assert.assertEquals(new Point(1, 1), s1.findIntersection(s2));
   }

   @Test
   public void testIntersection2()
   {
      Segment s1 = new Segment(new Point(1, 0), new Point(1, 3));
      Segment s2 = new Segment(new Point(0, 0), new Point(2, 2));

      Assert.assertEquals(new Point(1, 1), s1.findIntersection(s2));
   }

   @Test
   public void testIntersection3()
   {
      Segment s1 = new Segment(new Point(0, 0), new Point(5, 6));
      Segment s2 = new Segment(new Point(0, 0), new Point(-2, 5));

      Assert.assertEquals(new Point(0, 0), s1.findIntersection(s2));
   }

   @Test
   public void testIntersection4()
   {
      Segment s1 = new Segment(new Point(4,  2), new Point(-2, 0));
      Segment s2 = new Segment(new Point(0, -2), new Point(2, 4));

      Assert.assertEquals(new Point(1, 1), s1.findIntersection(s2));
   }

   @Test
   public void testNoIntersection1()
   {
      Segment s1 = new Segment(new Point(1, 0), new Point(1, 3));
      Segment s2 = new Segment(new Point(1, 0), new Point(1, 3));

      Assert.assertEquals(null, s1.findIntersection(s2));
   }

   @Test
   public void testNoIntersection2()
   {
      Segment s1 = new Segment(new Point(1, 0), new Point(1, 3));
      Segment s2 = new Segment(new Point(2, 0), new Point(2, 3));

      Assert.assertEquals(null, s1.findIntersection(s2));
   }

   @Test
   public void testNoIntersection3()
   {
      Segment s1 = new Segment(new Point(0, 0), new Point(3, 3));
      Segment s2 = new Segment(new Point(0, -2), new Point(1, 0));

      Assert.assertEquals(null, s1.findIntersection(s2));
   }
}
