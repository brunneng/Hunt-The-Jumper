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
   Double minPathToEndLength;
   List<Direction> directions;
   int currentDirection;
   Integer number;

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

      Double bestPathLength1 = pathFromStart1 + minPathToEndLength;
      Double bestPathLength2 = pathFromStart2 + o.minPathToEndLength;

      int res = bestPathLength1.compareTo(bestPathLength2);
      if (res == 0)
      {
         res = minPathToEndLength.compareTo(o.minPathToEndLength);

         if (res == 0)
         {
            res = number.compareTo(o.number);
         }
      }

      return res;
   }
}