package com.greenteam.huntjumper.view;

import com.greenteam.huntjumper.Camera;
import com.greenteam.huntjumper.IVisibleObject;
import com.greenteam.huntjumper.contoller.IJumperController;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Vector2D;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;

import java.util.ArrayList;
import java.util.List;

import static com.greenteam.huntjumper.utils.Vector2D.fromRadianAngleAndLength;

/**
 * Provides methods and properties for drawing Jumper on the screen
 * @author blindbutcher
 */

public class JumperView implements IVisibleObject
{
   private Jumper model;
   private List<IJumperController> controllers = new ArrayList<IJumperController>();


   public JumperView(Jumper jumper)
   {
      this.model = jumper;
   }

   public void draw(Graphics g)
   {
      Point viewCenter = Camera.instance().toView(model.getBody().getPosition());
      float radius = model.getBodyCircle().getRadius();

      Circle viewCircle = new Circle(viewCenter.getX(), viewCenter.getY(), radius);

      Vector2D rotationDirection = fromRadianAngleAndLength(model.getBody().getRotation(), radius);
      rotationDirection.plus(new Vector2D(viewCenter));

      g.setColor(model.getColor());
      g.draw(viewCircle);
      g.drawLine(viewCenter.getX(), viewCenter.getY(),
              rotationDirection.getX(), rotationDirection.getY());
   }

   public void update(GameContainer container, int delta) {
      for(IJumperController c : controllers) c.update(model, delta);
   }

   public JumperView addController(IJumperController controller)
   {
      controllers.add(controller);
      return this;
   }

   public Jumper getModel()
   {
      return model;
   }

   public void setModel(Jumper model)
   {
      this.model = model;
   }
}
