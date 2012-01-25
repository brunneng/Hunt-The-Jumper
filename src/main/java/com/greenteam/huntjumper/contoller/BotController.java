package com.greenteam.huntjumper.contoller;

import com.greenteam.huntjumper.map.Map;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.JumperInfo;
import com.greenteam.huntjumper.model.JumperRole;
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
   
   @Override
   protected Move makeMove(Jumper jumper)
   {
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
               res = new Move(new Vector2D(current.position, info.position), false);
               break;
            }
         }
      }
      
      return res;
   }
}
