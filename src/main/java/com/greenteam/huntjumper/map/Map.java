package com.greenteam.huntjumper.map;

import com.greenteam.huntjumper.Camera;
import com.greenteam.huntjumper.IVisibleObject;
import com.greenteam.huntjumper.utils.*;
import com.greenteam.huntjumper.utils.pathfinding.PathFinder;
import net.phys2d.math.ROVector2f;
import net.phys2d.raw.StaticBody;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * User: GreenTea Date: 05.02.12 Time: 19:36
 */
public class Map implements IVisibleObject
{
   private List<StaticBody> allPolygons;
   private Image mapImage;
   private AvailabilityMap map;

   private int cellSize;
   private PathFinder pathFinder;
   
   public Map(AvailabilityMap map)
   {
      this.map = map;
      List<Polygon> polygons = map.splitOnPolygons();
      allPolygons = new ArrayList<StaticBody>();

      Random rand = Utils.rand;
      
      for (Polygon p : polygons)
      {
         ROVector2f[] physPoints = new ROVector2f[p.getSegments().size()];
         if (physPoints.length < 3)
         {
            continue;
         }

         for (int i = 0; i < physPoints.length; ++i)
         {
            physPoints[i] = p.getSegments().get(i).getEnd1().toVector2f();
         }
         net.phys2d.raw.shapes.Polygon physP = new net.phys2d.raw.shapes.Polygon(physPoints);
         StaticBody body = new StaticBody(physP);
         body.setRestitution(0.95f);
         allPolygons.add(body);
      }

      try
      {
         Image pixel = new Image(1, 1);
         pixel.getGraphics().setColor(Color.white);
         pixel.getGraphics().draw(new Rectangle(0, 0, 2, 2));
         pixel.getGraphics().flush();
         
         mapImage = new Image(map.countX, map.countY);
         Graphics g = mapImage.getGraphics();

         g.setColor(ViewConstants.defaultGroundColor);
         g.fill(new Rectangle(0, 0, map.countX, map.countY));

         for (int x = 0; x < map.countX; ++x)
         {
            for (int y = 0; y < map.countY; ++y)
            {
               byte value = map.getValue(x, y);
               if (value != AvailabilityMap.FREE)
               {
                  float scale = 0.2f * (rand.nextFloat() - 0.5f);
                  Color c = ViewConstants.defaultMapColor.brighter(scale);

                  int count = map.getCountOfFreeNearPoints(x, y);
                  if (count > 0)
                  {
                     c = c.brighter(0.08f * count);
                  }

                  g.setColor(c);
                  g.drawImage(pixel, x, y, c);
               }
            }
         }

         g.flush();
      }
      catch (SlickException e)
      {
         throw new RuntimeException(e);
      }

      initPathFinder();
   }

   private void initPathFinder()
   {
      cellSize = GameConstants.PATH_FINDING_MAP_CELL_SIZE;
      int countX = (map.countX / cellSize) + 1;
      int countY = (map.countY / cellSize) + 1;

      byte[][] pathFindingMap = new byte[countY][countX];
      for (int x = 0; x < countX; ++x)
      {
         for (int y = 0; y < countX; ++y)
         {
            byte cell = PathFinder.FREE;
            if (!isCellFree(x*cellSize, y*cellSize, cellSize))
            {
               cell = PathFinder.WALL;
            }
            pathFindingMap[y][x] = cell;
         }
      }

      pathFinder = new PathFinder(pathFindingMap);
   }

   private boolean isNotFree(IntPoint p)
   {
      return map.isValid(p) && map.getValue(p) != AvailabilityMap.FREE;
   }
   
   private boolean isCellFree(int x, int y, int size)
   {
      int centerX = x + size/2;
      int centerY = y + size/2;
      int radius = size/2 - 3;
      List<Point> points = Utils.getRotationPoints(new Point(centerX, centerY), radius, 0, 
              GameConstants.PATH_FINDING_MAP_TEST_POINTS_IN_CELL);
      boolean free = true;
      for (Point p : points)
      {
         IntPoint testPoint = new IntPoint((int)p.getX(), (int)p.getY());
         if (isNotFree(testPoint))
         {
            free = false;
            break;
         }
      }
      
      return free;
   }
   
   private IntPoint toPathFindingCell(Point objectPos)
   {
      Point tPos = objectPos.plus(map.getTranslationVector());
      IntPoint p = new IntPoint((int)(tPos.getX() / cellSize), (int)(tPos.getY() / cellSize));
      return p;
   }

   private Point toMapPoint(IntPoint pathFindingCell)
   {
      Point p = new Point(
              pathFindingCell.x * cellSize + cellSize/2,
              pathFindingCell.y * cellSize + cellSize/2);
      
      return p.plus(map.getTranslationVector().negate());
   }

   public List<Point> findShortestPath(Point start, Point end)
   {
      List<Point> res = null;

      IntPoint startCell = toPathFindingCell(start);
      List<Direction> shortestPath = pathFinder.findShortestPath(
              startCell, toPathFindingCell(end));
      if (shortestPath != null)
      {
         res = new ArrayList<Point>();
         
         IntPoint currentCell = startCell;
         for (Direction d : shortestPath)
         {
            currentCell = currentCell.plus(d);
            res.add(toMapPoint(currentCell));
         }
      }

      return res;
   }

   public boolean isPointFree(Point p)
   {
      Point tp = p.plus(map.getTranslationVector());
      return map.isValid((int)tp.getX(), (int)tp.getY()) &&
              map.getValue((int)tp.getX(), (int)tp.getY()) == AvailabilityMap.FREE;
   }

   @Override
   public void draw(Graphics g)
   {
      Vector2D tv = map.getTranslationVector();
      Point p = new Point(-tv.getX(), -tv.getY());
      Point viewPoint = Camera.instance().toView(p);
      g.drawImage(mapImage, viewPoint.getX(), viewPoint.getY(), Color.white);

      g.setColor(ViewConstants.defaultMapColor);
      g.setAntiAlias(true);
      for (StaticBody b : allPolygons)
      {
         org.newdawn.slick.geom.Polygon viewPolygon = Utils.toViewPolygon(b);
         g.draw(viewPolygon);
      }
      g.setAntiAlias(false);
   }

   public List<StaticBody> getAllPolygons()
   {
      return allPolygons;
   }
}
