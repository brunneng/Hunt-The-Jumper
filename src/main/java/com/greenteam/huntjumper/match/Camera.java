package com.greenteam.huntjumper.match;

import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Range;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;
import net.phys2d.math.ROVector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.shapes.Polygon;

import java.util.Deque;
import java.util.LinkedList;

/**
 * User: GreenTea Date: 14.01.12 Time: 22:29
 */
public class Camera implements IUpdateable
{
   static Camera instance;
   
   private Point viewCenter;
   private int viewWidth;
   private int viewHeight;

   private float minX;
   private float minY;

   private TimeAccumulator samplesTimer = new TimeAccumulator(
           ViewConstants.CAMERA_SAMPLES_TIMER_INTERVAL);
   private Deque<Float> velocitySamples = new LinkedList<Float>();

   private AbstractMatchState matchState;

   public static Camera getCamera() {
      return instance;
   }

   Camera(AbstractMatchState matchState, Point pos, int viewWidth, int viewHeight)
   {
      this.matchState = matchState;
      this.viewWidth = viewWidth;
      this.viewHeight = viewHeight;
      setViewCenter(pos);

      velocitySamples.add(0f);
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

   public boolean inViewScreenWithReserve(Point viewPoint)
   {
      final float distReserve = 100f;
      return viewPoint.getX() > -distReserve &&
             viewPoint.getY() > -distReserve &&
             viewPoint.getX() < getViewWidth() + distReserve &&
             viewPoint.getY() < getViewHeight() + distReserve;
   }

   public boolean contains(ROVector2f p)
   {
      Range xRange = new Range(minX, minX + getViewWidth());
      Range yRange = new Range(minY, minY + getViewHeight());
      Point point = new Point(p);
      return xRange.contains(point.getX()) && yRange.contains(point.getY());
   }

   @Override
   public void update(int delta)
   {
      Jumper myJumper = matchState.getMyJumper();
      if (samplesTimer.update(delta) > 0)
      {
         velocitySamples.add(myJumper.getBody().getVelocity().length());
         if (velocitySamples.size() > ViewConstants.CAMERA_MAX_SAMPLES_COUNT)
         {
            velocitySamples.pollFirst();
         }
      }

      Point myJumperPos = new Point(myJumper.getBody().getPosition());
      Vector2D jumperToCamera = new Vector2D(myJumperPos, Camera.getCamera().getViewCenter());

      float cameraMaxDist = ViewConstants.CAMERA_MAX_DIST;
      float averageVelocity = Utils.average(velocitySamples);
      cameraMaxDist *= Math.min(averageVelocity / ViewConstants.CAMERA_MAX_VELOCITY_OF_JUMPER, 1);

      if (jumperToCamera.length() > cameraMaxDist)
      {
         jumperToCamera.setLength(cameraMaxDist);
         Point newCameraPos = myJumperPos.plus(jumperToCamera);
         Camera.getCamera().setViewCenter(newCameraPos);
      }
   }

   public Vector2D getPhysVectorToCursor(Body body, Point cursor)
   {
      Point physPoint = toPhys(new Point(cursor.getX(), cursor.getY())) ;
      return new Vector2D(new Point(body.getPosition()), physPoint);
   }

   public static org.newdawn.slick.geom.Polygon toViewPolygon(StaticBody b)
   {
      Polygon p = (Polygon)b.getShape();

      ROVector2f[] vertices = p.getVertices();
      float[] viewVertices = new float[vertices.length * 2];
      for (int i = 0; i < vertices.length; ++i)
      {
         ROVector2f v = vertices[i];
         Point viewPoint = getCamera().toView(v);
         viewVertices[2*i] = viewPoint.getX();
         viewVertices[2*i + 1] = viewPoint.getY();
      }

      return new org.newdawn.slick.geom.Polygon(
              viewVertices);
   }
}
