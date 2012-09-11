package com.greenteam.huntjumper;

import com.greenteam.huntjumper.match.SinglePlayerMatchState;
import com.greenteam.huntjumper.menu.INextStateProvider;
import com.greenteam.huntjumper.menu.MapSelectionMenu;
import com.greenteam.huntjumper.menu.ScreenMenu;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.parameters.ParametersUtils;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.shaders.ShadersSystem;
import org.newdawn.slick.*;

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
   private IGameState state;

   public GameContainer getGameContainer()
   {
      return gameContainer;
   }

   @Override
   public void init(GameContainer container) throws SlickException
   {
      ParametersUtils.overrideParameters(GameConstants.class, "game.properties");

      game = this;
      gameContainer = container;
      container.setShowFPS(true);
      container.setAlwaysRender(true);
      ShadersSystem.getInstance().init();

      ScreenMenu mainMenu = new ScreenMenu();
      MapSelectionMenu singlePlayer = new MapSelectionMenu("single player", "maps/");
      ScreenMenu exit = new ScreenMenu("exit", new INextStateProvider<ScreenMenu>()
      {
         @Override
         public IGameState getNextState(ScreenMenu parent)
         {
            System.exit(0);
            return null;
         }
      });
      mainMenu.getItems().add(singlePlayer);
      mainMenu.getItems().add(exit);
      state = mainMenu;

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
