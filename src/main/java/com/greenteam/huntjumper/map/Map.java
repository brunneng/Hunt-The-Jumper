package com.greenteam.huntjumper.map;

import com.greenteam.huntjumper.match.Camera;
import com.greenteam.huntjumper.match.IVisibleObject;
import com.greenteam.huntjumper.match.InitializationScreen;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.utils.*;
import com.greenteam.huntjumper.utils.pathfinding.PathFinder;
import net.phys2d.math.ROVector2f;
import net.phys2d.raw.StaticBody;
import org.newdawn.slick.*;
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

   private final int SMALL_IMAGE_SIZE = 128;
   private Image[][] mapImages;

   private AvailabilityMap map;

   private int cellSize;
   private PathFinder pathFinder;
   
   public Map(AvailabilityMap map)
   {
      this.map = map;
      InitializationScreen.getInstance().setStatus("Initialize physics polygons");
      List<Polygon> polygons = map.splitOnPolygons();
      allPolygons = new ArrayList<StaticBody>();

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

      int imagesX = 1 + (map.countX / SMALL_IMAGE_SIZE);
      int imagesY = 1 + (map.countX / SMALL_IMAGE_SIZE);
      mapImages = new Image[imagesX][imagesY];

      InitializationScreen.getInstance().setStatus("Init path finder");
      initPathFinder();
   }

   private Image createSmallImage(int sx, int sy)
   {
      Random rand = Utils.rand;
      ImageBuffer imageBuffer = new ImageBuffer(SMALL_IMAGE_SIZE, SMALL_IMAGE_SIZE);

      int startX = Math.max(sx * SMALL_IMAGE_SIZE, 0);
      int startY = Math.max(sy * SMALL_IMAGE_SIZE, 0);
      int endX = Math.min(startX + SMALL_IMAGE_SIZE, map.countX);
      int endY = Math.min(startY + SMALL_IMAGE_SIZE, map.countY);

      for (int x = startX; x < endX; ++x)
      {
         for (int y = startY; y < endY; ++y)
         {
            boolean value = map.getBoolValue(x, y);
            if (!value)
            {
               float scale = 0.2f * (rand.nextFloat() - 0.5f);
               Color c = ViewConstants.defaultMapColor.brighter(scale);

               int count = map.getCountOfFreeNearPoints(x, y);
               if (count > 0)
               {
                  c = c.brighter(0.08f * count);
               }

               imageBuffer.setRGBA(x - startX, y - startY,
                       c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
            }
         }
      }

      return imageBuffer.getImage();
   }

   private void initPathFinder()
   {
      cellSize = GameConstants.PATH_FINDING_MAP_CELL_SIZE;
      int countX = (map.countX / cellSize) + 1;
      int countY = (map.countY / cellSize) + 1;

      byte[][] pathFindingMap = new byte[countY][countX];
      for (int x = 0; x < countX; ++x)
      {
         for (int y = 0; y < countY; ++y)
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
      return map.isValid(p) && !map.getBoolValue(p);
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
              map.getBoolValue((int)tp.getX(), (int)tp.getY());
   }

   private Rectangle viewRect; // memory optimization

   @Override
   public void draw(Graphics g)
   {
      Camera c = Camera.getCamera();
      g.setColor(ViewConstants.defaultGroundColor);

      if (viewRect == null)
      {
         viewRect = new Rectangle(0, 0, c.getViewWidth(), c.getViewHeight());
      }
      g.fill(viewRect);
      
      Vector2D tv = map.getTranslationVector();
      Point p = new Point(-tv.getX(), -tv.getY());
      Point viewPoint = c.toView(p);

      prepareBackImages(c, viewPoint);
      drawBackImages(viewPoint);

      g.setColor(ViewConstants.defaultMapColor);
      g.setAntiAlias(true);
      for (StaticBody b : allPolygons)
      {
         drawContours(g, b);
      }
      g.setAntiAlias(false);
   }

   private void drawContours(Graphics g, StaticBody b)
   {
      net.phys2d.raw.shapes.Polygon poly = (net.phys2d.raw.shapes.Polygon)b.getShape();
      ROVector2f[] vertices = poly.getVertices();

      Point firstPoint = null;
      Point currPoint = null;
      for (int i = 0; i < vertices.length; ++i)
      {
         ROVector2f v = vertices[i];
         Point nextPoint = Camera.getCamera().toView(v);
         if (currPoint != null)
         {
            g.drawLine(currPoint.getX(), currPoint.getY(), nextPoint.getX(), nextPoint.getY());
         }
         currPoint = nextPoint;
         if (firstPoint == null)
         {
            firstPoint = currPoint;
         }
      }
      if (currPoint != null)
      {
         g.drawLine(currPoint.getX(), currPoint.getY(), firstPoint.getX(), firstPoint.getY());
      }
   }

   //static int imagesCount = 0;
   private void prepareBackImages(Camera c, Point viewPoint)
   {
      int topLeftSx = -(int)viewPoint.getX() / SMALL_IMAGE_SIZE;
      int topLeftSy = -(int)viewPoint.getY() / SMALL_IMAGE_SIZE;

      final int border = 1;
      topLeftSx = Math.max(topLeftSx-border, 0);
      topLeftSy = Math.max(topLeftSy-border, 0);
      int sXLen = (c.getViewWidth() / SMALL_IMAGE_SIZE) + border*2;
      int sYLen = (c.getViewHeight() / SMALL_IMAGE_SIZE) + border*2;
      int bottomRightSx = topLeftSx + sXLen;
      int bottomRightSy = topLeftSy + sYLen;
      for (int sx = 0; sx < mapImages.length; ++sx)
      {
         for (int sy = 0; sy < mapImages[sx].length; ++sy)
         {
            if (sx < topLeftSx || sy < topLeftSy || sx > bottomRightSx || sy > bottomRightSy)
            {
               mapImages[sx][sy] = null;
            }
            else if (mapImages[sx][sy] == null)
            {
               //System.out.println("image created " + imagesCount++);
               mapImages[sx][sy] = createSmallImage(sx, sy);
            }
         }
      }
   }

   private void drawBackImages(Point viewPoint)
   {
      for (int sx = 0; sx < mapImages.length; ++sx)
      {
         for (int sy = 0; sy < mapImages[sx].length; ++sy)
         {
            if (mapImages[sx][sy] != null)
            {
               mapImages[sx][sy].draw(
                       (int)(viewPoint.getX() + sx*SMALL_IMAGE_SIZE),
                       (int)(viewPoint.getY() + sy*SMALL_IMAGE_SIZE));
            }
         }
      }
   }

   public List<StaticBody> getAllPolygons()
   {
      return allPolygons;
   }
}
