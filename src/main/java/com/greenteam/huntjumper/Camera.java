package com.greenteam.huntjumper;

import com.greenteam.huntjumper.utils.Point;
import net.phys2d.math.ROVector2f;

/**
 * User: GreenTea Date: 14.01.12 Time: 22:29
 */
public class Camera
{
   static Camera instance;

   private Point viewCenter;
   private int viewWidth;
   private int viewHeight;

   private float minX;
   private float minY;

   public static Camera instance() {
      return instance;
   }

   Camera(Point pos, int viewWidth, int viewHeight)
   {
      this.viewWidth = viewWidth;
      this.viewHeight = viewHeight;
      setViewCenter(pos);
   }

   public Point getViewCenter()
   {
      return viewCenter;
   }

   public void setViewCenter(Point viewCenter)
   {
      this.viewCenter = viewCenter;
      minX = viewCenter.getX() - viewWidth/2;
      minY = viewCenter.getY() - viewHeight/2;
   }

   public int getViewWidth()
   {
      return viewWidth;
   }

   public void setViewWidth(int viewWidth)
   {
      this.viewWidth = viewWidth;
   }

   public int getViewHeight()
   {
      return viewHeight;
   }

   public void setViewHeight(int viewHeight)
   {
      this.viewHeight = viewHeight;
   }
   
   public Point toView(ROVector2f physPoint)
   {
      return new Point(physPoint.getX() - minX, physPoint.getY() - minY);
   }

   public Point toPhys(ROVector2f viewPoint)
   {
      return new Point(viewPoint.getX() + minX, viewPoint.getY() + minY);
   }

   public Point toView(Point physPoint)
   {
      return new Point(physPoint.getX() - minX, physPoint.getY() - minY);
   }

   public Point toPhys(Point viewPoint)
   {
      return new Point(viewPoint.getX() + minX, viewPoint.getY() + minY);
   }

   public boolean contains(ROVector2f p)
   {
      Point point = new Point(p);
      return !((point.getX() < (viewCenter.getX() + viewWidth / 2) || point.getX() > (viewCenter.getX() - viewWidth / 2))
              && (point.getY() < (viewCenter.getY() - viewHeight / 2) || point.getY() > (viewCenter.getY() + viewHeight / 2)));
   }
}
