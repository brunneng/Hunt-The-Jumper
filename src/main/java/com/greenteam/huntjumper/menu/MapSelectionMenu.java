package com.greenteam.huntjumper.menu;

import com.greenteam.huntjumper.HuntJumperGame;
import com.greenteam.huntjumper.IGameState;
import com.greenteam.huntjumper.map.CompressedMap;
import com.greenteam.huntjumper.match.SinglePlayerMatchState;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.utils.ImageLoader;
import com.greenteam.huntjumper.utils.Utils;
import org.newdawn.slick.*;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.greenteam.huntjumper.parameters.ViewConstants.PREVIEW_IMAGE_HEIGHT;
import static com.greenteam.huntjumper.parameters.ViewConstants.PREVIEW_IMAGE_WIDTH;

/**
 * User: GreenTea Date: 04.06.12 Time: 22:07
 */
public class MapSelectionMenu extends ScreenMenu
{
   private Map<String, File> nameToFile = new HashMap<String, File>();
   private File selectedFile;
   private Map<File, ImageBuffer> mapPreviewBuffers = new HashMap<File, ImageBuffer>();
   private Map<File, Image> mapPreviews = new HashMap<File, Image>();

   public MapSelectionMenu(String name, String mapsDir)
   {
      super(name);
      File mapsDirectory = new File(mapsDir);
      File[] files = mapsDirectory.listFiles();
      for (final File file : files)
      {
         if (file.getName().endsWith("map"))
         {
            ScreenMenu item = new ScreenMenu(file.getName(), new INextStateProvider<ScreenMenu>()
            {
               @Override
               public IGameState getNextState(ScreenMenu parent)
               {
                  return new SinglePlayerMatchState(file);
               }
            });
            nameToFile.put(item.getName(), file);
            getItems().add(item);
         }
      }
   }

   @Override
   protected void onSelection(ScreenMenu selectedItem)
   {
      selectedFile = nameToFile.get(selectedItem.getName());
      if (mapPreviewBuffers.containsKey(selectedFile))
      {
         return;
      }

      new Thread(new Runnable()
      {
         @Override
         public void run()
         {
            try
            {
               loadPreviewImage();
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
         }
      }).start();
   }

   private void loadPreviewImage() throws IOException
   {
      File file = selectedFile;

      CompressedMap compressedMap = Utils.loadMap(file);
      float sizeFactor = compressedMap.getWidth() / (float)(PREVIEW_IMAGE_WIDTH);

      int lineNum = 0;
      int lineCounter = 0;
      int[] whiteBlackLines = compressedMap.getWhiteBlackLines();

      boolean white = true;

      int[][] accumulator = new int[PREVIEW_IMAGE_WIDTH][PREVIEW_IMAGE_HEIGHT];
      for (int i = 0; i < compressedMap.getWidth(); ++i)
      {
         for (int j = 0; j < compressedMap.getHeight(); ++j)
         {
            int x = (int)(i / sizeFactor);
            int y = (int)(j / sizeFactor);

            if (!white)
            {
               accumulator[x][y]++;
            }

            lineCounter++;
            if (lineCounter >= whiteBlackLines[lineNum])
            {
               lineCounter = 0;
               white = !white;
               lineNum++;
            }
         }
      }

      ImageBuffer buffer = new ImageBuffer(
              PREVIEW_IMAGE_WIDTH, PREVIEW_IMAGE_HEIGHT);
      float maxAcc = sizeFactor*sizeFactor;
      for (int i = 0; i < PREVIEW_IMAGE_WIDTH; ++i)
      {
         for (int j = 0; j < PREVIEW_IMAGE_HEIGHT; ++j)
         {
            Color c = Color.getHSBColor(0f, 0f, 1f - accumulator[i][j] / maxAcc);
            buffer.setRGBA(i, j, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
         }
      }

      synchronized (this)
      {
         mapPreviewBuffers.put(file, buffer);
      }
   }

   @Override
   public void render(Graphics g) throws SlickException
   {
      super.render(g);

      if (!isShouldRenderMenu())
      {
         return;
      }

      ImageBuffer previewImageBuffer = null;
      synchronized (this)
      {
         previewImageBuffer = mapPreviewBuffers.get(selectedFile);
      }

      if (previewImageBuffer != null)
      {
         Image image = mapPreviews.get(selectedFile);
         if (image == null)
         {
            image = new Image(previewImageBuffer);
            mapPreviews.put(selectedFile, image);
         }

         GameContainer container = HuntJumperGame.getInstance().getGameContainer();
         float x = container.getWidth() - PREVIEW_IMAGE_WIDTH*1.2f;
         float y = (container.getHeight() - PREVIEW_IMAGE_HEIGHT) / 2f;

         float margin = PREVIEW_IMAGE_WIDTH / 20f;

         float margin2 = margin + 3;
         g.setColor(ViewConstants.PREVIEW_IMAGE_BORDER_COLOR);
         g.fillRoundRect(x - margin2, y - margin2,
                 PREVIEW_IMAGE_WIDTH + 2*margin2, PREVIEW_IMAGE_HEIGHT + 2*margin2, 6);

         g.setColor(org.newdawn.slick.Color.white);
         g.fillRoundRect(x - margin, y - margin,
                 PREVIEW_IMAGE_WIDTH + 2*margin, PREVIEW_IMAGE_HEIGHT + 2*margin, 6);

         g.drawImage(image, x, y);
      }
   }
}
