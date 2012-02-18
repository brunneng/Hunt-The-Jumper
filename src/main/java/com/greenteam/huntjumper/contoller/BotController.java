package com.greenteam.huntjumper.contoller;

import com.greenteam.huntjumper.map.Map;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.JumperInfo;
import com.greenteam.huntjumper.model.JumperRole;
import com.greenteam.huntjumper.utils.GameConstants;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Vector2D;

import java.util.List;

/**
 * User: GreenTea Date: 25.01.12 Time: 22:25
 */
public class BotController extends AbstractJumperController
{
   public static interface WorldInformationSource
   {
      List<JumperInfo> getOpponents(Jumper jumper);
      Map getMap();
   }

   private WorldInformationSource infoSource;

   public BotController(WorldInformationSource infoSource)
   {
      this.infoSource = infoSource;
   }
   
   private boolean isLineFree(Point start, Point end, float step)
   {
      float dist = end.distanceTo(start);
      if (dist < step)
      {
         return true;
      }

      Vector2D v = new Vector2D(start, end);
      float currLen = step;

      boolean res = true;
      while (currLen < dist)
      {
         v.setLength(currLen);
         Point testPoint = start.plus(v);
         if (!infoSource.getMap().isPointFree(testPoint))
         {
            res = false;
            break;
         }

         currLen += step;
      }
      
      return res;
   }
   
   private Point findMostFarPointInFreeLine(Point current, List<Point> shortestPath)
   {
      Point mostFarPoint = null;
      for (Point p : shortestPath)
      {
         if (!isLineFree(current, p, GameConstants.FREE_LINE_TEST_STEP))
         {
            break;
         }
         mostFarPoint = p;
      }

      return mostFarPoint;
   }
   
   @Override
   protected Move makeMove(Jumper jumper)
   {
      lastShortestPath = null;
      JumperInfo current = new JumperInfo(jumper);
      List<JumperInfo> jumperInfos = infoSource.getOpponents(jumper);
      Move res = null;
      if (jumper.getJumperRole().equals(JumperRole.Escaping))
      {
         JumperInfo nearest = JumperInfo.getNearest(jumperInfos, current.position);
         res = new Move(new Vector2D(nearest.position, current.position), false);
      }
      else if (jumper.getJumperRole().equals(JumperRole.Hunting))
      {
         for (JumperInfo info : jumperInfos)
         {
            if (info.jumperRole.equals(JumperRole.Escaping))
            {
               List<Point> shortestPath = infoSource.getMap().findShortestPath(
                       current.position, info.position);
               Point target = info.position;
               if (shortestPath != null && shortestPath.size() > 0)
               {
                  Point newTarget = findMostFarPointInFreeLine(current.position, shortestPath);
                  if (newTarget != null)
                  {
                     target = newTarget;
                  }
                  lastShortestPath = shortestPath;
               }

               res = new Move(new Vector2D(current.position, target), false);
               break;
            }
         }
      }
      
      return res;
   }
}
