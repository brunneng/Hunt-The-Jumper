package com.greenteam.huntjumper.match;

import com.greenteam.huntjumper.HuntJumperGame;
import com.greenteam.huntjumper.audio.AudioSystem;
import com.greenteam.huntjumper.contoller.AbstractJumperController;
import com.greenteam.huntjumper.contoller.BotController;
import com.greenteam.huntjumper.contoller.MouseController;
import com.greenteam.huntjumper.effects.Effect;
import com.greenteam.huntjumper.map.AvailabilityMap;
import com.greenteam.huntjumper.map.Map;
import com.greenteam.huntjumper.model.*;
import com.greenteam.huntjumper.model.bonuses.*;
import com.greenteam.huntjumper.model.bonuses.coin.Coin;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.TextUtils;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.RoundedRectangle;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.greenteam.huntjumper.parameters.GameConstants.COIN_RADIUS;
import static com.greenteam.huntjumper.parameters.GameConstants.DEFAULT_GAME_TIME;
import static com.greenteam.huntjumper.parameters.GameConstants.TIME_TO_BECOME_SUPER_HUNTER;
import static com.greenteam.huntjumper.parameters.ViewConstants.*;

/**
 * User: GreenTea Date: 03.06.12 Time: 16:33
 */
public class SinglePlayerMatchState extends AbstractMatchState
{
   private boolean gameFinished = false;

   private TimeAccumulator createCoinsAccumulator = new TimeAccumulator(
           GameConstants.COIN_APPEAR_INTERVAL);

   private TimeAccumulator createPositiveBonusesAccumulator = new TimeAccumulator(
           GameConstants.BONUS_APPEAR_INTERVAL);
   private TimeAccumulator createNeutralBonusesAccumulator = new TimeAccumulator(
           GameConstants.BONUS_APPEAR_INTERVAL);
   private TimeAccumulator createNegativeBonusesAccumulator = new TimeAccumulator(
           GameConstants.BONUS_APPEAR_INTERVAL);

   public SinglePlayerMatchState(File mapFile)
   {
      super(mapFile);
   }

   private void initWorld()
   {
      world = new World(new Vector2f(0f, 0f), 5);
   }

   private void initMap()
   {
      try
      {
         initializationScreen.setStatus("Loading map: " + mapFile.getName(), null);
         AvailabilityMap availabilityMap = new AvailabilityMap(mapFile);

         map = new Map(availabilityMap);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }

      for (StaticBody body : map.getAllPolygons())
      {
         world.add(body);
      }
   }

   private Jumper addJumper(Point startPos, String name, Color color,
                            AbstractJumperController jumperController, JumperRole role)
   {
      Jumper jumper = new Jumper(name, color, startPos.toVector2f(), jumperController, role);

      jumpers.add(jumper);
      world.add(jumper.getBody());
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
                       for (Jumper j : jumpers)
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

                       for (IBonus bonus : allPhysBonuses)
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

      arrowsVisualizer = new ArrowsVisualizer(myJumper, jumpers);
      scoresManager = new ScoresManager(jumpers);
   }

   private void initOtherJumpers()
   {
      for (Jumper j : jumpers)
      {
         List<Jumper> other = new ArrayList<>(jumpers);
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
            initWorld();
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

         world.step(0.001f * dt);
         Camera.getCamera().update(dt);
         for (Jumper j : jumpers)
         {
            j.update(dt);
         }

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

         needUpdateLightPassibility = true;
      }
      AudioSystem.getInstance().update(delta);
   }

   private void createBonuses(int dt)
   {
      createNewBonus(createPositiveBonusesAccumulator,
              dt, AbstractPositiveBonus.class, positiveBonuses);
      createNewBonus(createNeutralBonusesAccumulator,
              dt, AbstractNeutralBonus.class, neutralBonuses);
      createNewBonus(createNegativeBonusesAccumulator,
              dt, AbstractNegativeBonus.class, negativeBonuses);
      allPhysBonuses = collectAllPhysBonuses();
   }

   private void createNewCoin(int dt)
   {
      if (createCoinsAccumulator.update(dt) == 0 || coins.size() >= GameConstants.MAX_COINS_ON_MAP)
      {
         return;
      }

      Point pos = getRandomBonusPos(COIN_RADIUS);
      Coin c = new Coin(pos);
      coins.add(c);
   }

   private <T extends AbstractPhysBonus> void createNewBonus(TimeAccumulator timeAccumulator,
                                                             int dt, Class<T> bonusClazz,
                                                             Set<T> bonuses)
   {
      if (timeAccumulator.update(dt) == 0 ||
              bonuses.size() >= GameConstants.MAX_BONUSES_OF_1_TYPE_ON_MAP)
      {
         return;
      }

      Point pos = getRandomBonusPos(GameConstants.MAX_BONUS_RADIUS);
      T bonus = createRandomBonus(pos, bonusClazz);
      world.add(bonus.getBody());
      bonuses.add(bonus);
   }

   private Point getRandomBonusPos(float bonusRadius)
   {
      Random rand = Utils.rand;
      int appearRadius = (int)(0.9 * map.getWidth() / 2);

      Point pos;
      do
      {
         Vector2D createVector = Vector2D.fromAngleAndLength(rand.nextFloat() * 360,
                 rand.nextFloat()*appearRadius);
         pos = new Point(createVector.getX(), createVector.getY());
      }
      while (!map.isCircleFree(pos, bonusRadius));
      return pos;
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
