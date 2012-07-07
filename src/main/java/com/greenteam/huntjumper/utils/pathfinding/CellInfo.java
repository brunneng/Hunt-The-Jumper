package com.greenteam.huntjumper.utils.pathfinding;

import com.greenteam.huntjumper.utils.Direction;
import com.greenteam.huntjumper.utils.IntPoint;

import java.util.List;

/**
* Created by IntelliJ IDEA. User: GreenTea Date: 02.12.11 Time: 19:43 To change this template use
* File | Settings | File Templates.
*/
class CellInfo implements Comparable<CellInfo>
{
   IntPoint pos;
   Direction previousCell;
   int pathFromStartLen;
   double minPathToEndLength;
   List<Direction> directions;
   int currentDirection;
   int number;

   public CellInfo(IntPoint pos, Double minPathToEndLength, int number)
   {
      this.pos = pos;
      this.minPathToEndLength = minPathToEndLength;
      this.number = number;
   }

   public boolean isAnalyzed()
   {
      return (directions != null && currentDirection >= 4) ||
              minPathToEndLength == 0;
   }

   public int compareTo(CellInfo o)
   {
      int pathFromStart1 = pathFromStartLen;
      int pathFromStart2 = o.pathFromStartLen;

      double bestPathLength1 = pathFromStart1 + minPathToEndLength;
      double bestPathLength2 = pathFromStart2 + o.minPathToEndLength;

      int res = Double.compare(bestPathLength1, bestPathLength2);
      if (res == 0)
      {
         res = Double.compare(minPathToEndLength, o.minPathToEndLength);

         if (res == 0)
         {
            res = Integer.compare(number, o.number);
         }
      }

      return res;
   }
}