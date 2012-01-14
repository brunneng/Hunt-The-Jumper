package com.greenteam.huntjumper.map;

import com.greenteam.huntjumper.Camera;
import com.greenteam.huntjumper.IVisibleObject;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.shapes.Shape;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: GreenTea Date: 14.01.12 Time: 22:21 To change this template use
 * File | Settings | File Templates.
 */
public class Map implements IVisibleObject
{
   private List<StaticBody> mapPolygons;

   public Map(List<StaticBody> mapPolygons)
   {
      this.mapPolygons = mapPolygons;
   }

   public List<Shape> toGraphicsShapes()
   {
      List<Shape> res = new ArrayList<Shape>();

      return res;
   }

   @Override
   public void draw(Graphics g, Camera camera)
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }
}
