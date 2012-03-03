package com.greenteam.huntjumper.contoller;

import com.greenteam.huntjumper.map.Map;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.JumperInfo;
import com.greenteam.huntjumper.model.JumperRole;
import com.greenteam.huntjumper.parameters.GameConstants;
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
      List<JumperInfo> opponentsInfos = infoSource.getOpponents(jumper);
      Move res = null;
      JumperRole jumperRole = jumper.getJumperRole();
      if (jumperRole.equals(JumperRole.Escaping) ||
          jumperRole.equals(JumperRole.EscapingFromHunter))
      {
         JumperInfo nearest = JumperInfo.getNearest(opponentsInfos,
                 jumperRole.equals(JumperRole.Escaping) ? null : JumperRole.HuntingForEveryone,
                 current.position);
         JumperInfo escapeTarget = JumperInfo.getMostFar(opponentsInfos, null, current.position);
         if (current.position.distanceTo(escapeTarget.position) > 1500)
         {
            Point target = moveByShortestPath(current, escapeTarget);
            res = new Move(new Vector2D(current.position, target), false);
         }
         else
         {
            res = new Move(new Vector2D(nearest.position, current.position), false);
         }
      }
      else if (jumperRole.equals(JumperRole.Hunting) ||
              jumperRole.equals(JumperRole.HuntingForEveryone))
      {
         JumperInfo targetJumperInfo = JumperInfo.getNearest(opponentsInfos,
                 jumperRole.equals(JumperRole.Hunting) ? JumperRole.Escaping : null,
                 current.position);
         Point target = moveByShortestPath(current, targetJumperInfo);
         res = new Move(new Vector2D(current.position, target), false);
      }

      return res;
   }

   private Point moveByShortestPath(JumperInfo current, JumperInfo info)
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
      return target;
   }
}
