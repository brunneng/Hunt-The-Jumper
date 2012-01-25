package com.greenteam.huntjumper;

import com.greenteam.huntjumper.contoller.MouseController;
import com.greenteam.huntjumper.map.Map;
import com.greenteam.huntjumper.map.MapGenerator;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.utils.*;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import org.newdawn.slick.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: GreenTea Date: 13.01.12 Time: 22:44
 */
public class HuntJumperGame implements Game
{
   boolean exitGame = false;
   
   private World world;
   private Map map;
   private List<Jumper> jumpers;
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

   private void initJumpers(GameContainer container)
   {
      float maxRandomRadius = GameConstants.DEFAULT_MAP_RING_RADIUS - GameConstants.JUMPER_RADIUS;
      Vector2D v = new Vector2D(Utils.rand.nextFloat()*maxRandomRadius, 0);
      v.rotate(Utils.rand.nextInt(360));
      float x = v.getX();
      float y = v.getY();

      myJumper = new Jumper("GreenTea", Color.green, new Point(x, y).toVector2f(),
              new MouseController(container));

      jumpers = new ArrayList<Jumper>();
      jumpers.add(myJumper);

      world.add(myJumper.getBody());
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
      Set<Body> executedJumpers = new HashSet<Body>();

      CollisionEvent[] collisions = world.getContacts(myJumper.getBody());
      if (collisions != null && collisions.length > 0)
      {
         for (CollisionEvent e : collisions)
         {
            System.out.println(e);
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
