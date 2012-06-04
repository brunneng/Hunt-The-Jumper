package com.greenteam.huntjumper.menu;

import java.io.File;

/**
 * User: GreenTea Date: 04.06.12 Time: 22:07
 */
public class MapSelectionMenu extends ScreenMenu
{
   private String pathToMap;

   public MapSelectionMenu(String name, String mapsDir,
                           INextStateProvider<MapSelectionMenu> nextStateProvider)
   {
      super(name, (INextStateProvider)nextStateProvider);
      File mapsDirectory = new File(mapsDir);
      File[] files = mapsDirectory.listFiles();
      for (File file : files)
      {
         if (file.getName().endsWith("png"))
         {
            ScreenMenu item = new ScreenMenu(file.getName());
            getItems().add(item);
         }
      }
   }

   public String getPathToMap()
   {
      return pathToMap;
   }
}
