package com.greenteam.huntjumper.match;

import com.greenteam.huntjumper.AbstractGameState;
import com.greenteam.huntjumper.HuntJumperGame;
import com.greenteam.huntjumper.audio.AudioSystem;
import com.greenteam.huntjumper.contoller.AbstractJumperController;
import com.greenteam.huntjumper.contoller.BotController;
import com.greenteam.huntjumper.contoller.MouseController;
import com.greenteam.huntjumper.effects.Effect;
import com.greenteam.huntjumper.effects.particles.ParticleEntity;
import com.greenteam.huntjumper.effects.particles.ParticleGenerator;
import com.greenteam.huntjumper.effects.particles.ParticleType;
import com.greenteam.huntjumper.map.AvailabilityMap;
import com.greenteam.huntjumper.map.Map;
import com.greenteam.huntjumper.model.IRoleChangedListener;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.JumperInfo;
import com.greenteam.huntjumper.model.JumperRole;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.parameters.ViewConstants;
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
import java.util.*;

import static com.greenteam.huntjumper.parameters.GameConstants.DEFAULT_GAME_TIME;
import static com.greenteam.huntjumper.parameters.GameConstants.TIME_TO_BECOME_SUPER_HUNTER;
import static com.greenteam.huntjumper.parameters.ViewConstants.*;
import static com.greenteam.huntjumper.parameters.ViewConstants.timerEllipseAlpha;
import static com.greenteam.huntjumper.parameters.ViewConstants.timerEllipseIndentFromText;

/**
 * User: GreenTea Date: 03.06.12 Time: 16:33
 */
public class SinglePlayerMatchState extends AbstractGameState
{
   private World world;
   private File mapFile;
   private Map map;
   private List<Jumper> jumpers = new ArrayList<Jumper>();
   private HashMap<Body, Jumper> bodyToJumpers = new HashMap<Body, Jumper>();

   private Jumper myJumper;
   private TimeAccumulator updateTimeAccumulator = new TimeAccumulator();
   private InitializationScreen initializationScreen;
   private ArrowsVisualizer arrowsVisualizer;
   private GameContainer gameContainer;
   private ScoresManager scoresManager;
   private LinkedList<Effect> effects = new LinkedList<Effect>();
   private LinkedList<Integer> beforeEndNotifications = new LinkedList<Integer>();
   private boolean gameFinished = false;

   public SinglePlayerMatchState(File mapFile)
   {
      this.mapFile = mapFile;
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
      bodyToJumpers.put(jumper.getBody(), jumper);
      return jumper;
   }

   private boolean isStartPointFree(Point p, List<Point> resultJumperPositions,
                                    int currentJumperIndex)
   {
      if (!map.isPointFree(p))
      {
         return false;
      }

      List<Point> rotationPoints = Utils.getRotationPoints(p, GameConstants.JUMPER_RADIUS, 0, 4);
      for (Point rp : rotationPoints)
      {
         if (!map.isPointFree(rp))
         {
            return false;
         }
      }

      return p.inRange(resultJumperPositions.subList(0, currentJumperIndex),
              GameConstants.JUMPER_RADIUS*2).size() == 0;
   }

   private List<Point> getJumperPositionsOnFreePoints(List<Point> initialJumperPositions)
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
      addRoleChangeEffect();
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
                 }), JumperRole.Hunting);
      }

      arrowsVisualizer = new ArrowsVisualizer(myJumper, jumpers);
      scoresManager = new ScoresManager(jumpers);
   }

   private void addRoleChangeEffect()
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

            addEffect(new Effect()
            {
               @Override
               public int getDuration()
               {
                  return ViewConstants.roleChangeEffectDuration;
               }

               @Override
               public void draw(Graphics g)
               {
                  float ep = getExecutionPercent();

                  Point pos = Camera.getCamera().toView(myJumper.getBody().getPosition());
                  pos = pos.plus(new Vector2D(0,
                          -(GameConstants.JUMPER_RADIUS*3 +
                                  ViewConstants.roleChangeEffectHeight*ep)));

                  Color c = new Color(0, 0, 0, 1 - ep);
                  TextUtils.drawTextInCenter(pos, text, c, ViewConstants.roleChangeEffectFont, g);

                  c = Utils.toColorWithAlpha(newRole.getRoleColor().brighter(), 1 - ep);
                  pos = pos.plus(new Vector2D(1, 1));
                  TextUtils.drawTextInCenter(pos, text, c, ViewConstants.roleChangeEffectFont, g);
               }
            });
         }
      });
   }

   private void initCamera()
   {
      initializationScreen.setStatus("Init camera", null);
      Camera.instance = new Camera(this, new Point(myJumper.getBody().getPosition()),
              ViewConstants.VIEW_WIDTH, ViewConstants.VIEW_HEIGHT);
   }

   public void addEffect(Effect effect)
   {
      effects.add(effect);
   }

   private void updateEffects(int dt)
   {
      Iterator<Effect> i = effects.iterator();
      while (i.hasNext())
      {
         Effect effect = i.next();
         effect.update(dt);
         if (effect.isFinished())
         {
            i.remove();
         }
      }
   }

   private void drawEffects(Graphics g)
   {
      for (Effect effect : effects)
      {
         effect.draw(g);
      }
   }

   @Override
   public void init()
   {
      gameContainer = HuntJumperGame.getInstance().getGameContainer();
      initializationScreen = InitializationScreen.getInstance();
      beforeEndNotifications.addAll(GameConstants.NOTIFY_TIMES_BEFORE_END);

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
         updateEffects(dt);
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

         updateCollisions();
         updateRolesByTimer();
         scoresManager.update(dt);
         checkGameIsFinished();
      }
      AudioSystem.getInstance().update(delta);
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

   public void checkGameIsFinished()
   {
      int totalTime = updateTimeAccumulator.getTotalTimeInMilliseconds();
      if (!gameFinished && totalTime > GameConstants.DEFAULT_GAME_TIME)
      {
         gameFinished = true;

         final String text1 = "GAME OVER";
         final String text2 = makeWinnersString(scoresManager.calcWinners());

         final Point pos = new Point(gameContainer.getWidth() / 2, gameContainer.getHeight() / 2);
         addEffect(new Effect()
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
               Point text2Pos1 = pos.plus(new Vector2D(0, indentFactor*font.getHeight(text1)));
               Point text2Pos2 = text2Pos1.plus(new Vector2D(1, 1));

               int maxWidth = Math.max(font.getWidth(text1), font.getWidth(text2));
               int maxHeight = Math.max(font.getHeight(text1), font.getHeight(text2));
               Point boxPos = new Point(text1Pos1.getX() - maxWidth/2 - maxHeight*indentFactor/2,
                       text1Pos1.getY() - maxHeight*indentFactor);
               float boxWidth = maxWidth + maxHeight*indentFactor;
               float boxHeight = maxHeight*3*indentFactor;
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

      int x = gameContainer.getWidth() / 2;
      int y = ViewConstants.timerIndentFromTop + (int)(timerEllipseVerticalRadius*2.5f);
      final Point pos = new Point(x, y);

      Iterator<Integer> i = beforeEndNotifications.iterator();
      final int timeToEnd = GameConstants.DEFAULT_GAME_TIME - totalTime;
      while (i.hasNext())
      {
         int notificationTime = i.next();
         if (timeToEnd < notificationTime)
         {
            i.remove();
            addEffect(new Effect()
            {
               @Override
               public int getDuration()
               {
                  return beforeEndNotificationDuration;
               }

               @Override
               public void draw(Graphics g)
               {
                  float ep = getExecutionPercent();
                  float angle = ep * 2 * (float) Math.PI *
                          beforeEndNotificationBlinksPerSec * beforeEndNotificationDuration/1000;
                  float alpha = (1 + (float) Math.cos(angle)) / 2;

                  int endAfterTime = GameConstants.DEFAULT_GAME_TIME -
                          updateTimeAccumulator.getTotalTimeInMilliseconds();
                  String text = "End after " + Utils.getTimeString(endAfterTime);

                  float green = (float)timeToEnd / DEFAULT_GAME_TIME;
                  Color c = new Color(0, 0, 0, alpha);
                  TextUtils.drawTextInCenter(pos, text, c, ViewConstants.beforeEndNotificationFont,
                          g);

                  c = new Color(1, green, 0, alpha);
                  Point pos2 = pos.plus(new Vector2D(1, 1));
                  TextUtils.drawTextInCenter(pos2, text, c, ViewConstants.beforeEndNotificationFont,
                          g);
               }
            });
         }
      }
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

   public void updateCollisions()
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

               Jumper jumperA = bodyToJumpers.get(bodyA);
               Jumper jumperB = bodyToJumpers.get(bodyB);

               float collisionDist = myJumper.getBody().getPosition().distance(e.getPoint());
               boolean hasChangeRole = false;

               if (jumperA != null && jumperB != null)
               {
                  executedJumpers.add(jumperA);
                  executedJumpers.add(jumperB);

                  JumperRole roleA = jumperA.getJumperRole();
                  JumperRole roleB = jumperB.getJumperRole();

                  if (roleA.equals(JumperRole.Hunting) &&
                          roleB.equals(JumperRole.Escaping))
                  {
                     jumperA.setJumperRole(JumperRole.Escaping);
                     jumperB.setJumperRole(JumperRole.Hunting);
                     hasChangeRole = true;
                     myJumperEscaping = myJumper.equals(jumperA);
                  }
                  else if (roleB.equals(JumperRole.Hunting) &&
                          roleA.equals(JumperRole.Escaping))
                  {
                     jumperB.setJumperRole(JumperRole.Escaping);
                     jumperA.setJumperRole(JumperRole.Hunting);
                     hasChangeRole = true;
                     myJumperEscaping = myJumper.equals(jumperB);
                  }
                  else if (roleA.equals(JumperRole.HuntingForEveryone) &&
                          roleB.equals(JumperRole.EscapingFromHunter))
                  {
                     jumperA.setJumperRole(JumperRole.Escaping);
                     for (Jumper otherJumper : jumpers)
                     {
                        if (!otherJumper.equals(jumperA))
                        {
                           otherJumper.setJumperRole(JumperRole.Hunting);
                        }
                     }
                     hasChangeRole = true;
                     myJumperEscaping = myJumper.equals(jumperA);
                  }
                  else if (roleB.equals(JumperRole.HuntingForEveryone) &&
                          roleA.equals(JumperRole.EscapingFromHunter))
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
                 new ParticleGenerator(ParticleType.SPARK, 0, particlesCount).update(0);
         Random rand = Utils.rand;
         float currAngle = rand.nextFloat()*360f;
         float dAngle = 360f / particlesCount;
         for (ParticleEntity p : particles)
         {
            p.setPosition(new Point(e.getPoint()));
            p.setDeviation(particlesDeviation);

            p.setVelocity(Vector2D.fromAngleAndLength(currAngle,
                    ViewConstants.COLLISIONS_PARTICLES_VELOCITY_FACTOR *
                            GameConstants.JUMPER_RADIUS * 1000 / p.getDuration()));
            currAngle += dAngle;
         }
         effects.addAll(particles);
      }
   }

   public void render(Graphics g) throws SlickException
   {
      if (!initialized)
      {
         initializationScreen.draw(g);
         return;
      }

      map.draw(g);
      for (Jumper j : jumpers)
      {
         j.draw(g);
      }

      arrowsVisualizer.draw(g);
      drawInterface(g);
      drawEffects(g);
   }

   private void drawInterface(Graphics g)
   {
      scoresManager.draw(g);
      drawTimer(g);
   }

   private void drawTimer(Graphics g)
   {
      Font font = TextUtils.Arial30Font;
      String timeStr = Utils.getTimeString(
              Math.min(updateTimeAccumulator.getTotalTimeInMilliseconds(),
                      GameConstants.DEFAULT_GAME_TIME));
      int timerIndentFromTop = ViewConstants.timerIndentFromTop;

      int textHeight = font.getHeight(timeStr);
      int width = gameContainer.getWidth();
      Point timerPos = new Point(width / 2, timerIndentFromTop + textHeight / 2);

      float ellipseVRadius =  timerEllipseVerticalRadius;
      float ellipseHRadius = timerEllipseHorizontalRadius;
      Color ellipseColor = new Color(1f, 1f, 1f, timerEllipseAlpha);
      g.setColor(ellipseColor);
      g.fill(new Ellipse(timerPos.getX(), timerPos.getY() + timerEllipseIndentFromText,
              ellipseHRadius, ellipseVRadius));

      TextUtils.drawTextInCenter(timerPos, timeStr, Color.black, font, g);
   }

   public boolean closeRequested()
   {
      return true;
   }

   public String getTitle()
   {
      return ViewConstants.GAME_NAME;
   }

   public GameContainer getGameContainer()
   {
      return gameContainer;
   }

   public Jumper getMyJumper()
   {
      return myJumper;
   }

   public List<Jumper> getJumpers()
   {
      return jumpers;
   }

}
