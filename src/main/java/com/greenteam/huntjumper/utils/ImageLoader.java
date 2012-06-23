package com.greenteam.huntjumper.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: GreenTea Date: 23.06.12 Time: 21:16
 */
public class ImageLoader
{
   private static ImageLoader instance;

   public static ImageLoader getInstance()
   {
      if (instance == null)
      {
         instance = new ImageLoader();
      }

      return instance;
   }

   private Map<File, BufferedImage> imagesCache = new HashMap<File, BufferedImage>();

   private ImageLoader()
   {

   }

   public synchronized BufferedImage load(File file) throws IOException
   {
      if (imagesCache.containsKey(file))
      {
         return imagesCache.get(file);
      }

      BufferedImage image = ImageIO.read(file);
      imagesCache.put(file, image);
      return image;
   }

}
