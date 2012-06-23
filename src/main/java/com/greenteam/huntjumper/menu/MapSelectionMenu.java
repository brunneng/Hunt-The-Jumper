package com.greenteam.huntjumper.menu;

import com.greenteam.huntjumper.IGameState;
import com.greenteam.huntjumper.match.SinglePlayerMatchState;

import java.io.File;

/**
 * User: GreenTea Date: 04.06.12 Time: 22:07
 */
public class MapSelectionMenu extends ScreenMenu
{
   private String pathToMap;

   public MapSelectionMenu(String name, String mapsDir)
   {
      super(name);
      File mapsDirectory = new File(mapsDir);
      File[] files = mapsDirectory.listFiles();
      for (final File file : files)
      {
         if (file.getName().endsWith("png"))
         {
            ScreenMenu item = new ScreenMenu(file.getName(), new INextStateProvider<ScreenMenu>()
            {
               @Override
               public IGameState getNextState(ScreenMenu parent)
               {
                  return new SinglePlayerMatchState(file);
               }
            });
            getItems().add(item);
         }
      }
   }

   public String getPathToMap()
   {
      return pathToMap;
   }
}
