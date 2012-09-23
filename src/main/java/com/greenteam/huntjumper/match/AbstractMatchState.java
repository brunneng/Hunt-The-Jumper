package com.greenteam.huntjumper.match;

import com.greenteam.huntjumper.AbstractGameState;
import com.greenteam.huntjumper.HuntJumperGame;
import com.greenteam.huntjumper.IMatch;
import com.greenteam.huntjumper.audio.AudioSystem;
import com.greenteam.huntjumper.effects.Effect;
import com.greenteam.huntjumper.effects.particles.ParticleEntity;
import com.greenteam.huntjumper.effects.particles.ParticleType;
import com.greenteam.huntjumper.effects.particles.TypedParticleGenerator;
import com.greenteam.huntjumper.events.Event;
import com.greenteam.huntjumper.events.IEventExecutionContext;
import com.greenteam.huntjumper.events.MapObjectAddEvent;
import com.greenteam.huntjumper.events.MapObjectRemoveEvent;
import com.greenteam.huntjumper.map.AvailabilityMap;
import com.greenteam.huntjumper.map.Map;
import com.greenteam.huntjumper.model.*;
import com.greenteam.huntjumper.model.bonuses.AbstractNegativeBonus;
import com.greenteam.huntjumper.model.bonuses.AbstractNeutralBonus;
import com.greenteam.huntjumper.model.bonuses.AbstractPhysBonus;
import com.greenteam.huntjumper.model.bonuses.AbstractPositiveBonus;
import com.greenteam.huntjumper.model.bonuses.acceleration.AccelerationBonus;
import com.greenteam.huntjumper.model.bonuses.coin.Coin;
import com.greenteam.huntjumper.model.bonuses.gravity.GravityBonus;
import com.greenteam.huntjumper.model.bonuses.inelastic.InelasticBonus;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.shaders.ShadersSystem;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.TextUtils;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.RoundedRectangle;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;

import static com.greenteam.huntjumper.parameters.GameConstants.COIN_RADIUS;
import static com.greenteam.huntjumper.parameters.GameConstants.DEFAULT_GAME_TIME;
import static com.greenteam.huntjumper.parameters.GameConstants.TIME_TO_BECOME_SUPER_HUNTER;
import static com.greenteam.huntjumper.parameters.ViewConstants.*;
import static com.greenteam.huntjumper.parameters.ViewConstants.BEFORE_END_NOTIFICATION_BLINKS_PER_SEC;
import static com.greenteam.huntjumper.parameters.ViewConstants.BEFORE_END_NOTIFICATION_DURATION;

/**
 * User: GreenTea Date: 17.07.12 Time: 22:22
 */
public abstract class AbstractMatchState extends AbstractGameState implements IMatch,
        IEventExecutionContext
{
   private static List<Class<? extends AbstractPhysBonus>> allBonusClasses = new ArrayList<>();
   static
   {
      allBonusClasses.add(AccelerationBonus.class);
      allBonusClasses.add(GravityBonus.class);
      allBonusClasses.add(InelasticBonus.class);
   }

   protected TimeAccumulator updateTimeAccumulator = new TimeAccumulator(10);
   protected GameContainer gameContainer;

   private World world;
   protected File mapFile;
   protected Map map;
   private List<Jumper> jumpers = new ArrayList<Jumper>();
   private List<Jumper> unmodifiableJumpersList = Collections.unmodifiableList(jumpers);

   protected Jumper myJumper;
   protected LinkedList<Integer> beforeEndNotifications = new LinkedList<Integer>();

   protected InitializationScreen initializationScreen;
   protected ArrowsVisualizer arrowsVisualizer;

   protected Set<Coin> coins = new HashSet<>();
   protected Set<AbstractPhysBonus> physBonuses = new HashSet<>();
   private int positiveBonusesCount;
   private int neutralBonusesCount;
   private int negativeBonusesCount;

   private TimeAccumulator createCoinsAccumulator = new TimeAccumulator(
           GameConstants.COIN_APPEAR_INTERVAL);

   private TimeAccumulator createPositiveBonusesAccumulator = new TimeAccumulator(
           GameConstants.BONUS_APPEAR_INTERVAL);

   private TimeAccumulator createNeutralBonusesAccumulator = new TimeAccumulator(
           GameConstants.BONUS_APPEAR_INTERVAL);
   private TimeAccumulator createNegativeBonusesAccumulator = new TimeAccumulator(
           GameConstants.BONUS_APPEAR_INTERVAL);

   private java.util.Map<MapObjectId, IMapObject> mapObjects = new HashMap<>();

   private LightDrawer lightDrawer;

   protected List<Event> executedEvents = new ArrayList<>();
   protected List<Event> eventsForExecute = new ArrayList<>();
   protected List<Event> eventsForRollback = new ArrayList<>();

   protected ScoresManager scoresManager;

   protected AbstractMatchState(File mapFile)
   {
      this.mapFile = mapFile;
   }

   @Override
   public void init()
   {
      EffectsContainer.getInstance().clearEffects();
      gameContainer = HuntJumperGame.getInstance().getGameContainer();
      initializationScreen = InitializationScreen.getInstance();
      beforeEndNotifications.addAll(GameConstants.NOTIFY_TIMES_BEFORE_END);
   }

   protected void initWorld(int iterationsCount)
   {
      world = new World(new Vector2f(0f, 0f), iterationsCount);
   }

   protected void stepWorld(int dt)
   {
      world.step(0.001f * dt);
   }

   protected void initMap()
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

   protected void initCamera()
   {
      initializationScreen.setStatus("Init camera", null);
      Camera.instance = new Camera(this, new Point(myJumper.getBody().getPosition()),
              ViewConstants.VIEW_WIDTH, ViewConstants.VIEW_HEIGHT);
   }

   protected void initRoleChangeEffect()
   {
      final HashMap<JumperRole, List<String>> messages = new HashMap<JumperRole, List<String>>();
      messages.put(JumperRole.Escaping,
              Arrays.asList("Run!", "Escape!"));
      messages.put(JumperRole.Hunting,
              Arrays.asList("Catch him!", "Run down him!"));
      messages.put(JumperRole.HuntingForEveryone,
              Arrays.asList("Time for hunting!", "Go to papa..."));
      messages.put(JumperRole.EscapingFromHunter,
              Arrays.asList("Hunter appeared!", "Danger!"));

      myJumper.getRoleChangedListeners().add(new IRoleChangedListener()
      {
         @Override
         public void signalRoleIsChanged(JumperRole oldRole, final JumperRole newRole)
         {
            List<String> possibleMessages = messages.get(newRole);
            final String text = possibleMessages.get(Utils.rand.nextInt(possibleMessages.size()));

            EffectsContainer.getInstance().addEffect(
                    new FlyUpTextEffect(myJumper, text, ViewConstants.ROLE_CHANGE_EFFECT_DURATION,
                            newRole.getRoleColor().brighter(),
                            ViewConstants.ROLE_CHANGE_EFFECT_FONT,
                            ViewConstants.ROLE_CHANGE_EFFECT_HEIGHT));
         }
      });
   }

   private AbstractPhysBonus.WorldInformationSource createWorldInfo()
   {
      return new AbstractPhysBonus.WorldInformationSource()
      {
         @Override
         public List<JumperInfo> getJumpers()
         {
            List<JumperInfo> res = new ArrayList<>();
            for (Jumper j : jumpers)
            {
               res.add(new JumperInfo(j));
            }

            return res;
         }
      };
   }

   private boolean isStartPointFree(Point p, List<Point> resultJumperPositions,
                                    int currentJumperIndex)
   {
      return map.isCircleFree(p, GameConstants.JUMPER_RADIUS) &&
              p.inRange(resultJumperPositions.subList(0, currentJumperIndex),
                      GameConstants.JUMPER_RADIUS * 2).size() == 0;
   }

   protected void addJumper(Jumper jumper)
   {
      jumpers.add(jumper);
      world.add(jumper.getBody());
      mapObjects.put(jumper.getIdentifier(), jumper);
   }

   protected List<Jumper> getJumpers()
   {
      return unmodifiableJumpersList;
   }

   public List<Point> getJumperPositionsOnFreePoints(List<Point> initialJumperPositions)
   {
      List<Point> res = new ArrayList<Point>();

      float randomStep = GameConstants.JUMPER_RADIUS*5;

      Random rand = Utils.rand;
      for (int i = 0; i < initialJumperPositions.size(); ++i)
      {
         Point p = initialJumperPositions.get(i);
         while(!isStartPointFree(p, res, i))
         {
            Vector2D tv = new Vector2D(rand.nextFloat()*randomStep, rand.nextFloat()* randomStep);
            p = p.plus(tv);

         }

         res.add(p);
      }

      return res;
   }

   public void updateRolesByTimer()
   {
      boolean makeHunterForEveryone = false;
      for (Jumper j : jumpers)
      {
         if (j.getJumperRole().equals(JumperRole.Escaping) &&
                 j.getTimeInCurrentRole() > TIME_TO_BECOME_SUPER_HUNTER)
         {
            makeHunterForEveryone = true;
            break;
         }
      }

      if (makeHunterForEveryone)
      {
         for (Jumper j : jumpers)
         {
            if (j.getJumperRole().equals(JumperRole.Escaping))
            {
               j.setJumperRole(JumperRole.HuntingForEveryone);
            }
            else
            {
               j.setJumperRole(JumperRole.EscapingFromHunter);
            }
         }
      }
   }

   public void updateJumperToJumperCollisions()
   {
      Set<Jumper> executedJumpers = new HashSet<Jumper>();
      boolean myJumperEscaping = false;

      for (Jumper j : jumpers)
      {
         if (executedJumpers.contains(j))
         {
            continue;
         }

         CollisionEvent[] collisions = world.getContacts(j.getBody());
         if (collisions != null && collisions.length > 0)
         {
            for (CollisionEvent e : collisions)
            {
               Body bodyA = e.getBodyA();
               Body bodyB = e.getBodyB();
               Vector2D collisionVelocity = new Vector2D(bodyA.getVelocity()).minus(
                       new Vector2D(bodyB.getVelocity()));

               addCollisionEffect(e, collisionVelocity);

               Jumper jumperA = Utils.getUserDataOfClass(bodyA, Jumper.class);
               Jumper jumperB = Utils.getUserDataOfClass(bodyB, Jumper.class);

               float collisionDist = myJumper.getBody().getPosition().distance(e.getPoint());
               boolean hasChangeRole = false;

               if (jumperA != null && jumperB != null)
               {
                  executedJumpers.add(jumperA);
                  executedJumpers.add(jumperB);

                  JumperRole roleA = jumperA.getJumperRole();
                  JumperRole roleB = jumperB.getJumperRole();
                  if (roleA.ordinal() > roleB.ordinal())
                  {
                     JumperRole tmpRole = roleB;
                     roleB = roleA;
                     roleA = tmpRole;

                     Jumper tmpJumper = jumperB;
                     jumperB = jumperA;
                     jumperA = tmpJumper;
                  }

                  if (roleA.equals(JumperRole.Escaping) &&
                          roleB.equals(JumperRole.Hunting))
                  {
                     jumperB.setJumperRole(JumperRole.Escaping);
                     jumperA.setJumperRole(JumperRole.Hunting);
                     hasChangeRole = true;
                     myJumperEscaping = myJumper.equals(jumperB);
                  }
                  else if (roleA.equals(JumperRole.EscapingFromHunter) &&
                          roleB.equals(JumperRole.HuntingForEveryone))
                  {
                     jumperB.setJumperRole(JumperRole.Escaping);
                     for (Jumper otherJumper : jumpers)
                     {
                        if (!otherJumper.equals(jumperB))
                        {
                           otherJumper.setJumperRole(JumperRole.Hunting);
                        }
                     }
                     hasChangeRole = true;
                     myJumperEscaping = myJumper.equals(jumperB);
                  }
               }

               float volumePercent = Math.max(
                       1 - collisionDist / GameConstants.MAX_SOUNDS_DIST, 0f);

               String sound = AudioSystem.COLLISION_SOUND;
               if (hasChangeRole)
               {
                  sound = myJumperEscaping ? AudioSystem.ESCAPING_SOUND :
                          AudioSystem.HUNTING_SOUND;
               }
               else
               {
                  float powerModifier = Math.min(
                          collisionVelocity.length() / ViewConstants.collisionVelocityOfMaxVolume, 1);
                  volumePercent *= powerModifier;
               }

               AudioSystem.getInstance().playSound(sound, volumePercent);
            }
         }
      }
   }

   private void addCollisionEffect(CollisionEvent e, Vector2D collisionVelocity)
   {
      int particlesCount = (int) (ViewConstants.COLLISIONS_PARTICLES_MAX_COUNT *
              (collisionVelocity.length() / GameConstants.MAX_VELOCITY));
      int particlesDeviation = (int) (ViewConstants.COLLISIONS_PARTICLES_MAX_DEVIATION *
              (collisionVelocity.length() / GameConstants.MAX_VELOCITY));

      if (particlesCount > 0)
      {
         Collection<ParticleEntity> particles =
                 new TypedParticleGenerator(ParticleType.SPARK, 0, particlesCount).update(0);
         Random rand = Utils.rand;
         float currAngle = rand.nextFloat()*360f;
         float dAngle = 360f / particlesCount;

         EffectsContainer effectsContainer = EffectsContainer.getInstance();
         for (ParticleEntity p : particles)
         {
            p.setPosition(new Point(e.getPoint()));
            p.setDeviation(particlesDeviation);

            p.setVelocity(Vector2D.fromAngleAndLength(currAngle,
                    ViewConstants.COLLISIONS_PARTICLES_VELOCITY_FACTOR *
                            GameConstants.JUMPER_RADIUS * 1000 / p.getDuration()));
            currAngle += dAngle;
            effectsContainer.addEffect(p);
         }
      }
   }

   protected void processEvents()
   {
      for (Event e : eventsForExecute)
      {
         e.execute(this);
      }

      for (Event e : eventsForRollback)
      {
         if (e.isRollbackSupported())
         {
            e.rollback(this);
         }
      }

      executedEvents.addAll(eventsForExecute);
      eventsForExecute.clear();

      executedEvents.removeAll(eventsForRollback);
      eventsForRollback.clear();
   }

   protected void updateCoins(int dt)
   {
      for (Coin c : coins)
      {
         c.update(dt);
      }
   }

   protected void updateBonuses(int dt)
   {
      for (AbstractPhysBonus b : physBonuses)
      {
         b.update(dt);
      }
   }

   protected void processTakingCoins()
   {
      Iterator<Coin> i = coins.iterator();
      A: while (i.hasNext())
      {
         final Coin c = i.next();

         for (Jumper j : jumpers)
         {
            if (c.getPosition().distanceTo(new Point(j.getBody().getPosition())) <
                    GameConstants.JUMPER_RADIUS + GameConstants.COIN_RADIUS)
            {
               i.remove();
               eventsForExecute.add(new MapObjectRemoveEvent(c.getIdentifier(), getCurrentGameTime()));
               c.onBonusTaken(this, j);

               continue A;
            }
         }
      }
   }

   protected void processTakingBonuses()
   {
      for (Jumper j : jumpers)
      {
         CollisionEvent[] collisions = world.getContacts(j.getBody());
         if (collisions != null && collisions.length > 0)
         {
            for (CollisionEvent e : collisions)
            {
               Body bodyA = e.getBodyA();
               Body bodyB = e.getBodyB();

               AbstractPhysBonus bonus = Utils.getUserDataOfClass(bodyA, AbstractPhysBonus.class);
               if (bonus == null)
               {
                  bonus = Utils.getUserDataOfClass(bodyB, AbstractPhysBonus.class);
               }
               if (bonus != null)
               {
                  bonus.onBonusTaken(this, j);
                  eventsForExecute.add(new MapObjectRemoveEvent(bonus.getIdentifier(), getCurrentGameTime()));
               }
            }
         }
      }
   }

   protected void createBonuses(int dt)
   {
      createNewBonus(createPositiveBonusesAccumulator,
              dt, AbstractPositiveBonus.class);
      createNewBonus(createNeutralBonusesAccumulator,
              dt, AbstractNeutralBonus.class);
      createNewBonus(createNegativeBonusesAccumulator,
              dt, AbstractNegativeBonus.class);
   }

   protected void createNewCoin(int dt)
   {
      if (createCoinsAccumulator.update(dt) == 0 || coins.size() >= GameConstants.MAX_COINS_ON_MAP)
      {
         return;
      }

      Point pos = getRandomBonusPos(COIN_RADIUS);
      Coin c = new Coin(pos);
      eventsForExecute.add(new MapObjectAddEvent(c, getCurrentGameTime()));
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

   private <T extends AbstractPhysBonus> void createNewBonus(TimeAccumulator timeAccumulator,
                                                             int dt, Class<T> bonusClazz)
   {
      if (timeAccumulator.update(dt) == 0 ||
              getBonusesCount(bonusClazz) >= GameConstants.MAX_BONUSES_OF_1_TYPE_ON_MAP)
      {
         return;
      }

      Point pos = getRandomBonusPos(GameConstants.MAX_BONUS_RADIUS);
      createRandomBonus(pos, bonusClazz);
   }

   protected <T extends AbstractPhysBonus> T createRandomBonus(Point pos, Class<T> bonusClass)
   {
      List<Class<T>> bonusClasses = new ArrayList<>();
      for (Class c : allBonusClasses)
      {
         if (bonusClass.isAssignableFrom(c))
         {
            bonusClasses.add(c);
         }
      }

      Class<? extends AbstractPhysBonus> concreteBonusClass = bonusClasses.get(
              Utils.rand.nextInt(bonusClasses.size()));

      T res;
      try
      {
         Constructor c = concreteBonusClass.getConstructor(
                 AbstractPhysBonus.WorldInformationSource.class, Point.class);
         res = (T)c.newInstance(createWorldInfo(), pos);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }

      addPhysBonus(res);
      return res;
   }

   protected void addPhysBonus(AbstractPhysBonus bonus)
   {
      eventsForExecute.add(new MapObjectAddEvent(bonus, getCurrentGameTime()));
   }

   protected <T extends AbstractPhysBonus> int getBonusesCount(Class<T> bonusClazz)
   {
      if (AbstractPositiveBonus.class.isAssignableFrom(bonusClazz))
      {
         return positiveBonusesCount;
      }
      else if (AbstractNeutralBonus.class.isAssignableFrom(bonusClazz))
      {
         return neutralBonusesCount;
      }
      else if (AbstractNegativeBonus.class.isAssignableFrom(bonusClazz))
      {
         return negativeBonusesCount;
      }

      throw new IllegalArgumentException("Bonus class " + bonusClazz + " is not supported");
   }

   protected <T extends AbstractPhysBonus> void changeBonusesCount(Class<T> bonusClazz, int dCount)
   {
      if (AbstractPositiveBonus.class.isAssignableFrom(bonusClazz))
      {
         positiveBonusesCount += dCount;
      }
      else if (AbstractNeutralBonus.class.isAssignableFrom(bonusClazz))
      {
         neutralBonusesCount += dCount;
      }
      else if (AbstractNegativeBonus.class.isAssignableFrom(bonusClazz))
      {
         negativeBonusesCount += dCount;
      }
      else
      {
         throw new IllegalArgumentException("Bonus class " + bonusClazz + " is not supported");
      }
   }

   private String makeWinnersString(List<Jumper> winners)
   {
      StringBuilder sb = new StringBuilder(winners.size() == 1 ? "Winner is " : "Winners are ");
      for (int i = 0; i < winners.size(); ++i)
      {
         sb.append(winners.get(i).getPlayerName()).append(", ");
      }
      if (winners.size() > 0)
      {
         sb.delete(sb.length() - 2, sb.length());
      }

      return sb.toString();
   }

   protected void showFinishGameEffect()
   {
      final String text1 = "GAME OVER";
      final String text2 = makeWinnersString(scoresManager.calcWinners());

      final Point pos = new Point(gameContainer.getWidth() / 2, gameContainer.getHeight() / 2);
      EffectsContainer.getInstance().addEffect(new Effect()
      {
         @Override
         public int getDuration()
         {
            return Integer.MAX_VALUE;
         }

         @Override
         public void draw(Graphics g)
         {
            Font font = ViewConstants.WINNER_BOX_FONT;
            final float indentFactor = ViewConstants.WINNER_BOX_INDENT_FACTOR;

            Point text1Pos1 = new Point(pos);
            Point text1Pos2 = pos.plus(new Vector2D(1, 1));
            Point text2Pos1 = pos.plus(new Vector2D(0, indentFactor * font.getHeight(text1)));
            Point text2Pos2 = text2Pos1.plus(new Vector2D(1, 1));

            int maxWidth = Math.max(font.getWidth(text1), font.getWidth(text2));
            int maxHeight = Math.max(font.getHeight(text1), font.getHeight(text2));
            Point boxPos = new Point(text1Pos1.getX() - maxWidth / 2 - maxHeight * indentFactor / 2,
                    text1Pos1.getY() - maxHeight * indentFactor);
            float boxWidth = maxWidth + maxHeight * indentFactor;
            float boxHeight = maxHeight * 3 * indentFactor;
            g.setColor(Utils.toColorWithAlpha(ViewConstants.WINNER_BOX_COLOR,
                    ViewConstants.WINNER_BOX_RECTANGLE_ALPHA));
            g.fill(new RoundedRectangle(boxPos.getX(), boxPos.getY(), boxWidth, boxHeight,
                    ViewConstants.WINNER_BOX_RECTANGLE_CORNER_RADIUS));

            Color c = ViewConstants.WINNER_BOX_FONT_BACK_COLOR;
            TextUtils.drawTextInCenter(text1Pos1, text1, c, font, g);

            c = ViewConstants.WINNER_BOX_FONT_FRONT_COLOR;
            TextUtils.drawTextInCenter(text1Pos2, text1, c, font, g);

            c = ViewConstants.WINNER_BOX_FONT_BACK_COLOR;
            TextUtils.drawTextInCenter(text2Pos1, text2, c, font, g);

            c = ViewConstants.WINNER_BOX_FONT_FRONT_COLOR;
            TextUtils.drawTextInCenter(text2Pos2, text2, c, font, g);
         }
      });
   }

   protected void showBeforeEndNotification()
   {
      int totalTime = getCurrentGameTime();
      Iterator<Integer> i = beforeEndNotifications.iterator();
      final int timeToEnd = GameConstants.DEFAULT_GAME_TIME - totalTime;
      while (i.hasNext())
      {
         int notificationTime = i.next();
         if (timeToEnd < notificationTime)
         {
            int x = gameContainer.getWidth() / 2;
            int y = ViewConstants.TIMER_INDENT_FROM_TOP + (int)(TIMER_ELLIPSE_VERTICAL_RADIUS *2.5f);
            final Point pos = new Point(x, y);

            i.remove();
            EffectsContainer.getInstance().addEffect(new Effect()
            {
               @Override
               public int getDuration()
               {
                  return BEFORE_END_NOTIFICATION_DURATION;
               }

               @Override
               public void draw(Graphics g)
               {
                  float ep = getExecutionPercent();
                  float angle = ep * 2 * (float) Math.PI *
                          BEFORE_END_NOTIFICATION_BLINKS_PER_SEC *
                          BEFORE_END_NOTIFICATION_DURATION / 1000;
                  float alpha = (1 + (float) Math.cos(angle)) / 2;

                  int endAfterTime = GameConstants.DEFAULT_GAME_TIME - getCurrentGameTime();
                  String text = "End after " + Utils.getTimeString(endAfterTime);

                  float green = (float) timeToEnd / DEFAULT_GAME_TIME;
                  Color c = new Color(0, 0, 0, alpha);
                  TextUtils.drawTextInCenter(pos, text, c,
                          ViewConstants.BEFORE_END_NOTIFICATION_FONT, g);

                  c = new Color(1, green, 0, alpha);
                  Point pos2 = pos.plus(new Vector2D(1, 1));
                  TextUtils.drawTextInCenter(pos2, text, c,
                          ViewConstants.BEFORE_END_NOTIFICATION_FONT, g);
               }
            });
         }
      }
   }

   private void drawJumpers(Graphics g)
   {
      for (Jumper j : jumpers)
      {
         j.draw(g);
      }
   }

   public void drawCoins(Graphics g)
   {
      for (Coin c : coins)
      {
         c.draw(g);
      }
   }

   public void drawBonuses(Graphics g)
   {
      for (AbstractPhysBonus b : physBonuses)
      {
         b.draw(g);
      }
   }

   private void drawTimer(Graphics g)
   {
      Font font = TextUtils.Arial30Font;
      String timeStr = Utils.getTimeString(
              Math.min(getCurrentGameTime(),
                      GameConstants.DEFAULT_GAME_TIME));
      int timerIndentFromTop = ViewConstants.TIMER_INDENT_FROM_TOP;

      int textHeight = font.getHeight(timeStr);
      int width = gameContainer.getWidth();
      Point timerPos = new Point(width / 2, timerIndentFromTop + textHeight / 2);

      float ellipseVRadius = TIMER_ELLIPSE_VERTICAL_RADIUS;
      float ellipseHRadius = TIMER_ELLIPSE_HORIZONTAL_RADIUS;
      Color ellipseColor = new Color(1f, 1f, 1f, TIMER_ELLIPSE_ALPHA);
      g.setColor(ellipseColor);
      g.fill(new Ellipse(timerPos.getX(), timerPos.getY() + TIMER_ELLIPSE_INDENT_FROM_TEXT,
              ellipseHRadius, ellipseVRadius));

      TextUtils.drawTextInCenter(timerPos, timeStr, Color.black, font, g);
   }

   private void drawInterface(Graphics g)
   {
      scoresManager.draw(g);
      drawTimer(g);
   }

   private void drawLight(Graphics g)
   {
      if (lightDrawer == null && ShadersSystem.getInstance().isReady())
      {
         lightDrawer = new LightDrawer();
      }

      if (lightDrawer != null)
      {
         List<ILightproof> lightproofObjects = lightDrawer.getLightproofObjects();
         lightproofObjects.clear();
         lightproofObjects.addAll(jumpers);
         lightproofObjects.add(map);
         lightDrawer.setLightproofObjects(lightproofObjects);

         List<ILightSource> lightSources = lightDrawer.getLightSources();
         lightSources.clear();
         lightSources.addAll(jumpers);
         lightSources.addAll(coins);
         lightSources.addAll(physBonuses);
         lightDrawer.setLightSources(lightSources);

         lightDrawer.draw(g);
      }
   }

   protected void needUpdateLightPassability(boolean need)
   {
      if (lightDrawer != null)
      {
         lightDrawer.setNeedUpdateLightPassibility(need);
      }
   }

   @Override
   public void removeMapObject(MapObjectId identifier)
   {
      IMapObject removedObject = mapObjects.remove(identifier);
      if (removedObject != null)
      {
         if (removedObject instanceof Coin)
         {
            coins.remove(removedObject);
         }
         else if (removedObject instanceof AbstractPhysBonus)
         {
            physBonuses.remove(removedObject);
            changeBonusesCount(((AbstractPhysBonus)removedObject).getClass(), -1);
         }

         Body body = removedObject.getBody();
         if (body != null)
         {
            world.remove(body);
         }
      }
   }

   @Override
   public <T extends IMapObject> T getMapObject(MapObjectId identifier)
   {
      return (T)mapObjects.get(identifier);
   }

   @Override
   public void addMapObject(IMapObject mapObject)
   {
      mapObjects.put(mapObject.getIdentifier(), mapObject);
      Body body = mapObject.getBody();
      if (body != null)
      {
         world.add(body);
      }

      if (mapObject instanceof AbstractPhysBonus)
      {
         AbstractPhysBonus bonus = (AbstractPhysBonus)mapObject;
         physBonuses.add(bonus);
         changeBonusesCount(bonus.getClass(), 1);
      }
      else if (mapObject instanceof Coin)
      {
         coins.add((Coin)mapObject);
      }
   }

   @Override
   public int getCurrentGameTime()
   {
      return updateTimeAccumulator.getTotalTimeInMilliseconds();
   }

   public void render(Graphics g) throws SlickException
   {
      if (!initialized)
      {
         initializationScreen.draw(g);
         return;
      }

//      long time = System.currentTimeMillis();
      map.draw(g);
      drawLight(g);

      drawJumpers(g);
      drawCoins(g);
      drawBonuses(g);

      arrowsVisualizer.draw(g);
      drawInterface(g);

      EffectsContainer.getInstance().drawEffects(g);
//      time = System.currentTimeMillis() - time;
//      sumTime += time;
//      System.out.println(sumTime);
   }

   public ScoresManager getScoresManager()
   {
      return scoresManager;
   }
}
