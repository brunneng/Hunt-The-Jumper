package com.greenteam.huntjumper.utils.pathfinding;

import com.greenteam.huntjumper.utils.Direction;
import com.greenteam.huntjumper.utils.IntPoint;

import java.util.*;

/**
 * User: GreenTea Date: 18.02.12 Time: 14:03
 */
public class PathFinder
{
   public static final int DEFAULT_MAX_SEARCH_DEPTH = 400;

   public static final byte FREE = 0;
   public static final byte WALL = 1;
   public static final byte START = 2;
   public static final byte END = 3;
   public static final byte PATH = 10;
   
   private static Map<Character, Byte> symbolToByte;
   private static Map<Byte, Character> byteToSymbol;
   static
   {
      symbolToByte = new HashMap<Character, Byte>();
      symbolToByte.put('.', FREE);
      symbolToByte.put('%', WALL);
      symbolToByte.put('A', START);
      symbolToByte.put('*', END);
      symbolToByte.put('x', PATH);

      byteToSymbol = new HashMap<Byte, Character>();
      byteToSymbol.put(FREE, '.');
      byteToSymbol.put(WALL, '%');
      byteToSymbol.put(START, 'A');
      byteToSymbol.put(END, '*');
      byteToSymbol.put(PATH, 'x');
   }

   private static IntPoint findObject(byte[][] map, byte object)
   {
      IntPoint res = null;
      for (int i = 0; i < map.length; ++i)
      {
         for (int j = 0; j < map[i].length; ++j)
         {
            if (map[i][j] == object)
            {
               res = new IntPoint(j, i);
               break;
            }
         }
      }
      return res;
   }

   private static void drawPath(byte[][] map, IntPoint start, List<Direction> path)
   {
      IntPoint currentPos = start;
      for (Direction d : path)
      {
         currentPos = currentPos.plus(d);
         map[currentPos.y][currentPos.x] = PATH;
      }
   }

   public static byte[][] parseMap(String[] mapStr)
   {
      byte[][] res = new byte[mapStr.length][(mapStr[0].length() + 1)/2];
      for (int i = 0; i < mapStr.length; ++i)
      {
         String line = mapStr[i];
         String[] cells = line.split(" ");
         for (int j = 0; j < cells.length; ++j)
         {
            Byte value = symbolToByte.get(cells[j].charAt(0));
            res[i][j] = value;
         }
      }
      return res;
   }

   public static void printMap(byte[][] map)
   {
      for (int i = 0; i < map.length; ++i)
      {
         StringBuilder line = new StringBuilder();
         byte[] row = map[i];
         for (int j = 0; j < row.length; ++j)
         {
            line.append(byteToSymbol.get(row[j])).append(" ");
         }
         System.out.println(line);
      }
   }

   public static void main(String[] args)
   {
      String[] mapStr = new String[]
                     {". . . . . . . . . .",
                      ". . % . . . . % . .",
                      ". % . . . . . * % .",
                      "% % % % % % % % . .",
                      "% . . . . . . . . .",
                      "% . . . . . . . . .",
                      "% . . . . . . . . .",
                      "% . . . . . . . . .",
                      "% . . . . . . . . .",
                      "% A . . . . . . . .",
                      "% % % % % % % % % %",};

      byte[][] map = parseMap(mapStr);

      IntPoint start = findObject(map, START);
      IntPoint end = findObject(map, END);

      List<Direction> shortestPath = null;
      Date startTime = new Date();
      PathFinder pathFinder = new PathFinder(map);
      int count = 5;
      for (int i = 0; i < count; ++i)
      {
         shortestPath = pathFinder.findShortestPath(start, end);
      }
      long time = new Date().getTime() - startTime.getTime();

      if (shortestPath != null)
      {
         drawPath(map, start, shortestPath);
      }

      printMap(map);
      System.out.println("Time of " + count + " executions: " + time);
   }

   private byte[][] map;
   private int maxX;
   private int maxY;
   private IntPoint start;
   private IntPoint end;
   private CellInfo[] cellInfos;

   private List<Direction> shortestPath;

   private int currentCellInfoNumber = 0;
   private TreeSet<CellInfo> cellsPool = new TreeSet<CellInfo>();

   public PathFinder(byte[][] map)
   {
      this.map = map;
      maxY = map.length;
      maxX = map[0].length;
   }

   private CellInfo getNearestCellFromPool()
   {
      return cellsPool.first();
   }

   public List<Direction> findShortestPath(IntPoint start, IntPoint end)
   {
      if (start.distanceToInCells(end) > DEFAULT_MAX_SEARCH_DEPTH)
      {
         return null;
      }

      this.start = start;
      this.end = end;
      this.cellInfos = new CellInfo[maxY*maxX];
      this.shortestPath = null;
      this.cellsPool.clear();

      CellInfo c = new CellInfo(start, minDistanceToEnd(start), currentCellInfoNumber++);
      cellsPool.add(c);

      if (start.equals(end))
      {
         return new ArrayList<Direction>();
      }

      while (cellsPool.size() > 0 && shortestPath == null)
      {
         searchInternal(getNearestCellFromPool());
      }

      return shortestPath;
   }

   private void searchInternal(CellInfo cellInfo)
   {
      IntPoint pos = cellInfo.pos;

      if (cellInfo.directions == null)
      {
         cellInfo.directions = Direction.values;
      }

      for (int i = cellInfo.currentDirection; i < cellInfo.directions.size(); ++i)
      {
         cellInfo.currentDirection = i + 1;
         Direction d = cellInfo.directions.get(i);
         IntPoint next = pos.plus(d);
         if (!isValid(next))
         {
            continue;
         }

         int dist = start.distanceToInCells(next);

         byte nextPointType = getValue(next);
         if (next.equals(end) || nextPointType == FREE)
         {
            CellInfo nextCell = getCellInfo(next);
            if (nextCell.currentDirection != 0)
            {
               continue;
            }

            int nextPathFromStartLen = cellInfo.pathFromStartLen + 1;

            if (next.equals(end))
            {
               if (shortestPath == null)
               {
                  nextCell.pathFromStartLen = nextPathFromStartLen;
                  nextCell.previousCell = d.behind();
                  setNewPathToEnd();
                  return;
               }
               break;
            }

            if (nextCell.pathFromStartLen == 0 && !next.equals(start))
            {
               int minPathToEndLen = nextPathFromStartLen + minDistanceToEndInCells(next);
               if (minPathToEndLen > DEFAULT_MAX_SEARCH_DEPTH)
               {
                  continue;
               }

               nextCell.pathFromStartLen = nextPathFromStartLen;
               nextCell.previousCell = d.behind();
               cellsPool.add(nextCell);
            }
         }
      }

      if (cellInfo.isAnalyzed())
      {
         cellsPool.remove(cellInfo);
      }
   }

   private void setNewPathToEnd()
   {
      shortestPath = new ArrayList<Direction>();
      IntPoint currPos = end;
      CellInfo cell = getCellInfo(currPos);

      while (cell.previousCell != null)
      {
         shortestPath.add(cell.previousCell.behind());
         currPos = currPos.plus(cell.previousCell);
         cell = getCellInfo(currPos);
      }
      Collections.reverse(shortestPath);
   }

   private int minDistanceToEndInCells(IntPoint pos)
   {
      return pos.distanceToInCells(end);
   }

   private double minDistanceToEnd(IntPoint pos)
   {
      return pos.distanceTo(end);
   }

   public boolean isValid(IntPoint p)
   {
      return p.x >= 0 && p.x < maxX && p.y >= 0 && p.y < maxY;
   }

   public byte getValue(IntPoint p)
   {
      return map[p.y][p.x];
   }

   private CellInfo getCellInfo(IntPoint pos)
   {
      CellInfo cell = cellInfos[maxX * pos.y + pos.x];
      if (cell == null)
      {
         cell = new CellInfo(pos, minDistanceToEnd(pos), currentCellInfoNumber++);
         cellInfos[maxX * pos.y + pos.x] = cell;
      }

      return cell;
   }
}
