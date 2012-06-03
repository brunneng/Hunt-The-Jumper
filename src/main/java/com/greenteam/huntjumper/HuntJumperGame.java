package com.greenteam.huntjumper;

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
import com.greenteam.huntjumper.utils.*;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.RoundedRectangle;

import java.io.IOException;
import java.util.*;

import static com.greenteam.huntjumper.parameters.GameConstants.DEFAULT_GAME_TIME;
import static com.greenteam.huntjumper.parameters.GameConstants.TIME_TO_BECOME_SUPER_HUNTER;
import static com.greenteam.huntjumper.parameters.ViewConstants.*;

/**
 * User: GreenTea Date: 13.01.12 Time: 22:44
 */
public class HuntJumperGame implements Game
{
   private static HuntJumperGame game;
   public static final HuntJumperGame getInstance()
   {
      return game;
   }

   private GameContainer gameContainer;
   private IGameState state = new SinglePlayerMatchState();

   public GameContainer getGameContainer()
   {
      return gameContainer;
   }

   @Override
   public void init(GameContainer container) throws SlickException
   {
      game = this;
      gameContainer = container;
      container.setShowFPS(false);
      container.setAlwaysRender(true);
      state.init();
   }

   public void update(GameContainer container, int delta) throws SlickException
   {
      state.update(delta);
   }

   public void render(GameContainer container, Graphics g) throws SlickException
   {
      state.render(g);
   }
   
   public boolean closeRequested()
   {
      return true;
   }

   public String getTitle()
   {
      return ViewConstants.GAME_NAME;
   }
}
