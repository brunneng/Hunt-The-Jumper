package com.greenteam.huntjumper.contoller;

import com.greenteam.huntjumper.map.Map;
import com.greenteam.huntjumper.match.TimeAccumulator;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.JumperInfo;
import com.greenteam.huntjumper.model.JumperRole;
import com.greenteam.huntjumper.model.bonuses.AbstractPositiveBonus;
import com.greenteam.huntjumper.model.bonuses.coin.Coin;
import com.greenteam.huntjumper.model.bonuses.gravity.GravityBonus;
import com.greenteam.huntjumper.model.bonuses.IBonus;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: GreenTea Date: 25.01.12 Time: 22:25
 */
public class BotController extends AbstractJumperController
{
   public static interface WorldInformationSource
   {
      List<JumperInfo> getOpponents(Jumper jumper);
      public java.util.Map<Class<? extends IBonus>, List<Point>> getBonuses();
      Map getMap();
   }

   private WorldInformationSource infoSource;
   private TimeAccumulator pathFindingTimer;
   private Point previousTarget;

   public BotController(WorldInformationSource infoSource)
   {
      this.infoSource = infoSource;
      int t = GameConstants.PATH_FINDING_AVERAGE_TIME_INTERVAL;
      int dt = GameConstants.PATH_FINDING_TIME_DISPERSION;
      pathFindingTimer = new TimeAccumulator(t + Utils.rand.nextInt(dt) - dt/2);
      pathFindingTimer.update(Utils.rand.nextInt(pathFindingTimer.getCycleLength()));
   }
   
   private boolean isLineFree(Point start, Point end, float step)
   {
      float dist = end.distanceTo(start);
      if (dist < step)
      {
         return true;
      }

      Vector2D stepVector = new Vector2D(start, end).setLength(step);
      float currLen = step;

      boolean res = true;
      Point testPoint = start;
      while (currLen < dist)
      {
         testPoint = testPoint.plus(stepVector);
         if (!infoSource.getMap().isPointFree(testPoint))
         {
            res = false;
            break;
         }

         currLen += step;
      }
      
      return res;
   }
   
   private Point findMostFarPointInFreeLine(Point current, List<Point> shortestPath, Point target)
   {
      Point mostFarPoint = null;
      boolean hasNotFreePoint = false;
      for (Point p : shortestPath)
      {
         if (!isLineFree(current, p, GameConstants.FREE_LINE_TEST_STEP))
         {
            hasNotFreePoint = true;
            break;
         }
         mostFarPoint = p;
      }
      if (!hasNotFreePoint)
      {
         mostFarPoint = target;
      }

      return mostFarPoint;
   }
   
   @Override
   protected Move makeMove(Jumper jumper, int delta)
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
         if (current.position.distanceTo(escapeTarget.position) >
                 GameConstants.MIN_DIST_TO_ESCAPE_TO_FAR_JUMPER)
         {
            Point target = moveByShortestPath(current, escapeTarget, delta,
                    jumperRole.equals(JumperRole.EscapingFromHunter));
            res = new Move(vectorToTargetConsideringVelocity(current, target), false);
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
         Point target = moveByShortestPath(current, targetJumperInfo, delta, true);
         res = new Move(vectorToTargetConsideringVelocity(current, target), false);
      }

      return res;
   }

   private Vector2D vectorToTargetConsideringVelocity(JumperInfo current, Point target)
   {
      Vector2D straightVector = new Vector2D(current.position, target);
      if (current.velocity.length() < Float.MIN_VALUE)
      {
         return straightVector;
      }

      float angle = current.velocity.angleToVector(straightVector);

      return Math.abs(angle) < 90 ? straightVector.rotate(angle) : straightVector;
   }

   private Point moveByShortestPath(JumperInfo current, JumperInfo targetInfo, int delta,
                                    boolean huntForBonus)
   {
      if (pathFindingTimer.update(delta) == 0 && previousTarget != null)
      {
         return previousTarget;
      }

      Point currPos = current.position;
      Point target = targetInfo.position;
      if (huntForBonus)
      {
         List<Point> goodBonusesPos = new ArrayList<>();
         java.util.Map<Class<? extends IBonus>, List<Point>> bonuses = infoSource.getBonuses();
         for (java.util.Map.Entry<Class<? extends IBonus>, List<Point>> entry : bonuses.entrySet())
         {
            if (AbstractPositiveBonus.class.isAssignableFrom(entry.getKey()))
            {
               goodBonusesPos.addAll(entry.getValue());
            }
            else if (Coin.class.isAssignableFrom((entry.getKey())))
            {
               goodBonusesPos.addAll(entry.getValue());
            }
            else if (GravityBonus.class.isAssignableFrom((entry.getKey())) &&
                        current.jumperRole.isHuntingRole())
            {
               goodBonusesPos.addAll(entry.getValue());
            }
         }

         Point nearestBonusPos = currPos.findNearestPoint(goodBonusesPos);
         if (nearestBonusPos != null &&
                 currPos.distanceTo(nearestBonusPos) < currPos.distanceTo(targetInfo.position) &&
                 currPos.distanceTo(nearestBonusPos) < GameConstants.MIN_DIST_FOR_BOT_TO_TAKE_COIN)
         {
            target = nearestBonusPos;
         }
      }

      float dist = currPos.distanceTo(target);
      boolean isShortDist = dist < GameConstants.JUMPER_RADIUS*
              GameConstants.PATH_FINDING_ENABLE_DETAIL_SEARCH_FACTOR;
      List<Point> shortestPath = isShortDist ?
              infoSource.getMap().findDetailShortestPath(target, currPos) :
              infoSource.getMap().findApproximateShortestPath(target, currPos);

      if (isShortDist && (shortestPath == null || shortestPath.size() == 0))
      {
         shortestPath = infoSource.getMap().findApproximateShortestPath(target, currPos);
      }

      if (shortestPath != null && shortestPath.size() > 0)
      {
         Collections.reverse(shortestPath);
         Point newTarget = findMostFarPointInFreeLine(currPos, shortestPath, target);
         if (newTarget != null)
         {
            target = newTarget;
         }
         lastShortestPath = shortestPath;
      }
      previousTarget = target;

      return target;
   }


}
