package com.greenteam.huntjumper.match;

import com.greenteam.huntjumper.audio.AudioSystem;
import com.greenteam.huntjumper.contoller.AbstractJumperController;
import com.greenteam.huntjumper.contoller.BotController;
import com.greenteam.huntjumper.contoller.MouseController;
import com.greenteam.huntjumper.map.Map;
import com.greenteam.huntjumper.model.*;
import com.greenteam.huntjumper.model.bonuses.*;
import com.greenteam.huntjumper.model.bonuses.coin.Coin;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Utils;
import org.newdawn.slick.*;

import java.io.File;
import java.util.*;

/**
 * User: GreenTea Date: 03.06.12 Time: 16:33
 */
public class SinglePlayerMatchState extends BaseMatchState
{
   private boolean gameFinished = false;

   public SinglePlayerMatchState(File mapFile)
   {
      super(mapFile);
   }

   private Jumper addJumper(Point startPos, String name, Color color,
                            AbstractJumperController jumperController, JumperRole role)
   {
      Jumper jumper = new Jumper(name, color, startPos.toVector2f(), jumperController, role);

      addJumper(jumper);
      return jumper;
   }

   private void initJumpers()
   {
      initializationScreen.setStatus("Init jumpers", null);

      gameContainer.setForceExit(false);
      float maxRandomRadius = GameConstants.JUMPERS_START_RADIUS - GameConstants.JUMPER_RADIUS;

      List<Point> jumperPositions = Utils.getRotationPoints(
              new Point(0, 0), Utils.rand.nextFloat()*maxRandomRadius, Utils.rand.nextInt(360), 5);
      jumperPositions = getJumperPositionsOnFreePoints(jumperPositions);

      myJumper = addJumper(jumperPositions.get(0), "GreenTea", Utils.randomColor(),
              new MouseController(), JumperRole.Escaping);
      initRoleChangeEffect();
      myJumper.setJumperRole(JumperRole.Escaping);

      for (int i = 1; i < jumperPositions.size(); ++i)
      {
         addJumper(jumperPositions.get(i), "bot" + i, Utils.randomColor(),
                 new BotController(new BotController.WorldInformationSource() {
                    @Override
                    public List<JumperInfo> getOpponents(Jumper jumper)
                    {
                       List<JumperInfo> jumperInfos = new ArrayList<JumperInfo>();
                       for (Jumper j : getJumpers())
                       {
                          if (!j.equals(jumper))
                          {
                             jumperInfos.add(new JumperInfo(j));
                          }
                       }
                       return jumperInfos;
                    }

                    @Override
                    public Map getMap()
                    {
                       return map;
                    }

                    @Override
                    public java.util.Map<Class<? extends IBonus>, List<Point>> getBonuses()
                    {
                       java.util.Map<Class<? extends IBonus>, List<Point>> res = new HashMap<>();
                       List<Point> positions = new ArrayList<>();
                       res.put(Coin.class, positions);
                       for (Coin c : coins)
                       {
                          positions.add(c.getPosition());
                       }

                       for (IBonus bonus : physBonuses)
                       {
                          positions = res.get(bonus.getClass());
                          if (positions == null)
                          {
                             positions = new ArrayList<>();
                             res.put(bonus.getClass(), positions);
                          }
                          positions.add(bonus.getPosition());
                       }

                       return res;
                    }
                 }), JumperRole.Hunting);
      }

      initOtherJumpers();

      arrowsVisualizer = new ArrowsVisualizer(myJumper, getJumpers());
      scoresManager = new ScoresManager(getJumpers());
   }

   private void initOtherJumpers()
   {
      for (Jumper j : getJumpers())
      {
         List<Jumper> other = new ArrayList<>(getJumpers());
         other.remove(j);
         j.setOtherJumpers(other);
      }
   }

   @Override
   public void init()
   {
      super.init();

      new Thread(new Runnable()
      {
         @Override
         public void run()
         {
            initWorld(5);
            initMap();
            initJumpers();
            initCamera();
            initialized = true;
         }
      }).start();
   }

   public void update(int delta) throws SlickException
   {
      if (!initialized)
      {
         initializationScreen.update(delta);
         return;
      }

      int cycles = updateTimeAccumulator.update(delta);
      for (int i = 0; i < cycles; i++)
      {
         int dt = updateTimeAccumulator.getCycleLength();
         EffectsContainer.getInstance().updateEffects(dt);
         if (gameFinished)
         {
            continue;
         }

         stepWorld(dt);
         Camera.getCamera().update(dt);
         updateJumpers(dt);

         updateCoins(dt);
         processTakingCoins();
         createNewCoin(dt);

         updateBonuses(dt);
         processTakingBonuses();
         createBonuses(dt);

         updateJumperToJumperCollisions();

         updateRolesByTimer();

         scoresManager.update(dt);

         checkGameIsFinished();
         showBeforeEndNotification();

         processCommands();
         executedCommands.clear();

         needUpdateLightPassability(true);
      }
      AudioSystem.getInstance().update(delta);
   }

   public void checkGameIsFinished()
   {
      int totalTime = updateTimeAccumulator.getTotalTimeInMilliseconds();
      if (!gameFinished && totalTime > GameConstants.DEFAULT_GAME_TIME)
      {
         gameFinished = true;

         showFinishGameEffect();
      }
   }

   public boolean closeRequested()
   {
      return true;
   }

   public String getTitle()
   {
      return ViewConstants.GAME_NAME;
   }

   public Jumper getMyJumper()
   {
      return myJumper;
   }
}
