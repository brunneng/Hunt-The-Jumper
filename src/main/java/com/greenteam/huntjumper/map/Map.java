package com.greenteam.huntjumper.map;

import com.greenteam.huntjumper.match.Camera;
import com.greenteam.huntjumper.model.ILightproof;
import com.greenteam.huntjumper.match.IVisibleObject;
import com.greenteam.huntjumper.match.InitializationScreen;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.shaders.ShadersSystem;
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
public class Map implements IVisibleObject, ILightproof
{
   private static Image wallImage;

   private static void init()
   {
      if (wallImage != null)
      {
         return;
      }

      try
      {
         wallImage = new Image("images/wall_texture4.png", Color.white);
      }
      catch (SlickException e)
      {
      }
   }

   private List<StaticBody> allPolygons;

   private final int SMALL_IMAGE_SIZE = 128;
   private Image[][] mapImages;

   private AvailabilityMap map;

   private PathFinder smallMapPathFinder;
   private PathFinder detailMapPathFinder;
   
   public Map(AvailabilityMap map)
   {
      this.map = map;
      InitializationScreen.getInstance().setStatus("Prepare physics polygons");
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

      InitializationScreen.getInstance().setStatus("Init path finders");
      smallMapPathFinder = createPathFinder(GameConstants.PATH_FINDING_MAP_CELL_SIZE);

      detailMapPathFinder = createPathFinder((int)GameConstants.JUMPER_RADIUS);
      detailMapPathFinder.setMaxSearchDepth(GameConstants.PATH_FINDING_DETAIL_SEARCH_MAX_DEPTH);
   }

   private Image createSmallImage(int sx, int sy)
   {
      init();

      Random rand = Utils.rand;
      ImageBuffer imageBuffer = new ImageBuffer(SMALL_IMAGE_SIZE, SMALL_IMAGE_SIZE);

      int startX = Math.max(sx * SMALL_IMAGE_SIZE, 0);
      int startY = Math.max(sy * SMALL_IMAGE_SIZE, 0);
      int endX = Math.min(startX + SMALL_IMAGE_SIZE, map.countX);
      int endY = Math.min(startY + SMALL_IMAGE_SIZE, map.countY);

      int dx = rand.nextInt(wallImage.getWidth());
      int dy = rand.nextInt(wallImage.getHeight());

      for (int x = startX; x < endX; ++x)
      {
         for (int y = startY; y < endY; ++y)
         {
            boolean value = map.isFree(x, y);
            if (!value)
            {
               Color c = wallImage.getColor((x + dx) % wallImage.getWidth(),
                       (y + dy) % wallImage.getHeight());

               imageBuffer.setRGBA(x - startX, y - startY,
                       c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
            }
         }
      }

      return imageBuffer.getImage();
   }

   private PathFinder createPathFinder(int cellSize)
   {
      int countX = (map.countX / cellSize) + 1;
      int countY = (map.countY / cellSize) + 1;

      byte[][] pathFindingMap = new byte[countY][countX];
      for (int x = 0; x < countX; ++x)
      {
         for (int y = 0; y < countY; ++y)
         {
            byte cell = PathFinder.FREE;
            if (!isCellFree(x* cellSize, y* cellSize, cellSize))
            {
               cell = PathFinder.WALL;
            }
            pathFindingMap[y][x] = cell;
         }
      }

      return new PathFinder(pathFindingMap, cellSize);
   }

   private boolean isNotFree(IntPoint p)
   {
      return map.isValid(p) && !map.isFree(p);
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
   
   private IntPoint toPathFindingCell(Point objectPos, int cellSize)
   {
      Point tPos = objectPos.plus(map.getTranslationVector());
      IntPoint p = new IntPoint((int)(tPos.getX() / cellSize), (int)(tPos.getY() /
              cellSize));
      return p;
   }

   private Point toMapPoint(IntPoint pathFindingCell, int cellSize)
   {
      Point p = new Point(
              pathFindingCell.x * cellSize + cellSize /2,
              pathFindingCell.y * cellSize + cellSize /2);
      
      return p.plus(map.getTranslationVector().negate());
   }

   public List<Point> findApproximateShortestPath(Point start, Point end)
   {
      return findShortestPath(start, end, smallMapPathFinder);
   }

   public List<Point> findDetailShortestPath(Point start, Point end)
   {
      return findShortestPath(start, end, detailMapPathFinder);
   }

   private List<Point> findShortestPath(Point start, Point end, PathFinder pathFinder)
   {
      List<Point> res = null;

      IntPoint startCell = toPathFindingCell(start, pathFinder.getCellSize());
      List<Direction> shortestPath = pathFinder.findShortestPath(
              startCell, toPathFindingCell(end, pathFinder.getCellSize()));
      if (shortestPath != null)
      {
         res = new ArrayList<Point>();
         
         IntPoint currentCell = startCell;
         for (Direction d : shortestPath)
         {
            currentCell = currentCell.plus(d);
            res.add(toMapPoint(currentCell, pathFinder.getCellSize()));
         }
      }

      return res;
   }

   public boolean isPointFree(Point p)
   {
      Point tp = p.plus(map.getTranslationVector());
      return map.isValid((int)tp.getX(), (int)tp.getY()) &&
              map.isFree((int) tp.getX(), (int) tp.getY());
   }

   public boolean isCircleFree(Point p, float radius)
   {
      if (!isPointFree(p))
      {
         return false;
      }

      List<Point> rotationPoints = Utils.getRotationPoints(p, GameConstants.JUMPER_RADIUS, 0, 4);
      for (Point rp : rotationPoints)
      {
         if (!isPointFree(rp))
         {
            return false;
         }
      }

      return true;
   }

   private Rectangle viewRect; // memory optimization

   @Override
   public void draw(Graphics g)
   {
      Camera c = Camera.getCamera();
      g.setColor(ShadersSystem.getInstance().isSupported() ?
              ViewConstants.DEFAULT_GROUND_COLOR_WITH_SHADERS :
              ViewConstants.DEFAULT_GROUND_COLOR_NO_SHADERS);

      if (viewRect == null)
      {
         viewRect = new Rectangle(0, 0, c.getViewWidth(), c.getViewHeight());
      }
      g.fill(viewRect);

      drawLightProofBody(g);
   }

   @Override
   public void drawLightProofBody(Graphics g)
   {
      Camera c = Camera.getCamera();
      Vector2D tv = map.getTranslationVector();
      Point p = new Point(-tv.getX(), -tv.getY());
      Point viewPoint = c.toView(p);

      prepareBackImages(c, viewPoint);
      drawBackImages(viewPoint);

      g.setColor(ViewConstants.DEFAULT_MAP_COLOR);
      g.setAntiAlias(true);
      g.setLineWidth(2);
      for (StaticBody b : allPolygons)
      {
         drawContours(g, b);
      }
      g.setLineWidth(1);
   }

   private void drawContours(Graphics g, StaticBody b)
   {
      net.phys2d.raw.shapes.Polygon poly = (net.phys2d.raw.shapes.Polygon)b.getShape();
      ROVector2f[] vertices = poly.getVertices();

      Camera c = Camera.getCamera();
      Point firstPoint = null;
      Point currPoint = null;
      for (int i = 0; i < vertices.length; ++i)
      {
         ROVector2f v = vertices[i];
         Point nextPoint = c.toView(v);
         if (currPoint != null &&
                 (c.inViewScreenWithReserve(currPoint) || c.inViewScreenWithReserve(nextPoint)))
         {
            g.drawLine(currPoint.getX(), currPoint.getY(), nextPoint.getX(), nextPoint.getY());
         }
         currPoint = nextPoint;
         if (firstPoint == null)
         {
            firstPoint = currPoint;
         }
      }
      if (currPoint != null &&
              (c.inViewScreenWithReserve(currPoint) || c.inViewScreenWithReserve(firstPoint)))
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

   public int getWidth()
   {
      return map.getCountX();
   }

   public int getHeight()
   {
      return map.getCountY();
   }
}
