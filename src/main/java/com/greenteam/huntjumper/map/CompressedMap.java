package com.greenteam.huntjumper.map;

import java.io.*;
import java.util.List;

/**
 * User: GreenTea Date: 05.07.12 Time: 21:36
 */
public class CompressedMap implements Serializable
{
   private static final long serialVersionUID = -7291604090886208018L;

   public static CompressedMap loadMap(File mapFile) throws IOException
   {
      CompressedMap compressedMap;
      try (FileInputStream fis = new FileInputStream(mapFile))
      {
         ObjectInputStream ois = new ObjectInputStream(fis);
         compressedMap = (CompressedMap)ois.readObject();
      }
      catch (ClassNotFoundException e)
      {
         e.printStackTrace();
         throw new RuntimeException(e);
      }
      return compressedMap;
   }

   private int width;
   private int height;

   private int[] whiteBlackLines;

   public CompressedMap(int width, int height, List<Integer> whiteBlackLines)
   {
      this.width = width;
      this.height = height;
      this.whiteBlackLines = new int[whiteBlackLines.size()];
      for (int i = 0; i < whiteBlackLines.size(); ++i)
      {
         this.whiteBlackLines[i] = whiteBlackLines.get(i);
      }
   }

   public int getWidth()
   {
      return width;
   }

   public int getHeight()
   {
      return height;
   }

   public int[] getWhiteBlackLines()
   {
      return whiteBlackLines;
   }
}
