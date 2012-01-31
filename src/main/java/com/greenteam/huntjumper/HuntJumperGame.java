package com.greenteam.huntjumper;

import com.greenteam.huntjumper.contoller.AbstractJumperController;
import com.greenteam.huntjumper.contoller.BotController;
import com.greenteam.huntjumper.contoller.IJumperController;
import com.greenteam.huntjumper.contoller.MouseController;
import com.greenteam.huntjumper.map.Map;
import com.greenteam.huntjumper.map.MapGenerator;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.JumperInfo;
import com.greenteam.huntjumper.model.JumperRole;
import com.greenteam.huntjumper.utils.*;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import org.newdawn.slick.*;

import java.util.*;

/**
 * User: GreenTea Date: 13.01.12 Time: 22:44
 */
public class HuntJumperGame implements Game
{
   boolean exitGame = false;
   
   private World world;
   private Map map;
   private List<Jumper> jumpers = new ArrayList<Jumper>();
   private HashMap<Body, Jumper> bodyToJumpers = new HashMap<Body, Jumper>();
   
   private Jumper myJumper;
   private TimeAccumulator timeAccumulator = new TimeAccumulator();

   private void initWorld()
   {
      world = new World(new Vector2f(0f, 0f), 5);
   }
   
   private void initMap()
   {
      map = MapGenerator.generateRingMap(GameConstants.DEFAULT_MAP_RING_RADIUS);
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
   
   private void initJumpers(GameContainer container)
   {
      float maxRandomRadius = GameConstants.DEFAULT_MAP_RING_RADIUS - GameConstants.JUMPER_RADIUS;

      List<Point> jumperPositions = Utils.getRotationPoints(
              new Point(0, 0), Utils.rand.nextFloat()*maxRandomRadius, Utils.rand.nextInt(360), 5);
      
      myJumper = addJumper(jumperPositions.get(0), "GreenTea", Utils.randomColor(),
              new MouseController(container), JumperRole.Escaping);
      
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
   }

   private void initCamera()
   {
      Camera.instance = new Camera(new Point(myJumper.getBody().getPosition()),
              ViewConstants.VIEW_WIDTH, ViewConstants.VIEW_HEIGHT);
   }

   public void init(GameContainer container) throws SlickException
   {
      initWorld();
      initMap();
      initJumpers(container);
      initCamera();
   }

   private void updateCamera()
   {
      Point myJumperPos = new Point(myJumper.getBody().getPosition());
      Vector2D jumperToCamera = new Vector2D(myJumperPos, Camera.instance().getViewCenter());

      if (jumperToCamera.length() > GameConstants.CAMERA_MAX_DIST)
      {
         jumperToCamera.setLength(GameConstants.CAMERA_MAX_DIST);
         Point newCameraPos = myJumperPos.plus(jumperToCamera);
         Camera.instance().setViewCenter(newCameraPos);
      }
   }

   public void update(GameContainer container, int delta) throws SlickException
   {
      int cycles = timeAccumulator.cycles(delta);
      for (int i = 0; i < cycles; i++) 
      {
         world.step(0.001f * TimeAccumulator.CYCLE_LENGTH);
         updateCamera();
         for (Jumper j : jumpers)
         {
            j.update(TimeAccumulator.CYCLE_LENGTH);
         }

         updateCollisions();
      }
   }

   public void updateCollisions()
   {
      Set<Jumper> executedJumpers = new HashSet<Jumper>();
      
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

               Jumper jumperA = bodyToJumpers.get(bodyA);
               Jumper jumperB = bodyToJumpers.get(bodyB);

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
                  }
                  else if (roleB.equals(JumperRole.Hunting) &&
                          roleA.equals(JumperRole.Escaping))
                  {
                     jumperB.setJumperRole(JumperRole.Escaping);
                     jumperA.setJumperRole(JumperRole.Hunting);
                  }
               }
            }
         }
      }

   }

   public void render(GameContainer container, Graphics g) throws SlickException
   {
      map.draw(g);
      for (Jumper j : jumpers)
      {
         j.draw(g);
      }

   }

   public boolean closeRequested()
   {
      return exitGame;
   }

   public String getTitle()
   {
      return ViewConstants.GAME_NAME;
   }
}
