package com.greenteam.huntjumper;

import com.greenteam.huntjumper.contoller.MouseController;
import com.greenteam.huntjumper.map.Map;
import com.greenteam.huntjumper.map.MapGenerator;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.utils.*;
import com.greenteam.huntjumper.view.JumperView;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import org.newdawn.slick.*;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.geom.Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: GreenTea Date: 13.01.12 Time: 22:44 To change this template use
 * File | Settings | File Templates.
 */
public class HuntJumperGame implements Game
{
   boolean exitGame = false;
   
   private World world;
   private Map map;
   private List<JumperView> jumpers;
   private Jumper myJumper;
   private TimeAccumulator timeAccumulator = new TimeAccumulator();

   private void initWorld()
   {
      world = new World(new Vector2f(0f, 0f), 5);
   }
   
   private void initMap()
   {
      map = MapGenerator.generateRingMap(GameConstants.DEFAULT_MAP_RING_RADIUS);
      for (StaticBody body : map.getMapPolygons())
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

      myJumper = new Jumper("GreenTea", Color.red, new Point(x, y).toPhysVector());

      jumpers = new ArrayList<JumperView>();
      jumpers.add(new JumperView(myJumper).addController(new MouseController(container)));
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
         for (JumperView j : jumpers) j.update(container, delta);
      }
   }

   public void render(GameContainer container, Graphics g) throws SlickException
   {
      map.draw(g);
      for (JumperView j : jumpers)
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
