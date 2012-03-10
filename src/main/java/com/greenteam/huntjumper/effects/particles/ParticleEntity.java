package com.greenteam.huntjumper.effects.particles;

import com.greenteam.huntjumper.Camera;
import com.greenteam.huntjumper.effects.Effect;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA. User: Ivan Date: 15.01.2011 Time: 14:31:45 To change this template use
 * File | Settings | File Templates.
 */
public class ParticleEntity extends Effect
{
   private Color color;
   private float startRadius, endRadius;
   private boolean friction;
   private float deviation;
   private int duration;

   protected Point position;
   protected Vector2D velocity = new Vector2D();

   private ParticleEntity()
   {
   }

   public Color getColor()
   {
      return color;
   }

   public void setColor(Color color)
   {
      this.color = color;
   }

   public float getStartRadius()
   {
      return startRadius;
   }

   public void setStartRadius(float startRadius)
   {
      this.startRadius = startRadius;
   }

   public float getEndRadius()
   {
      return endRadius;
   }

   public void setEndRadius(float endRadius)
   {
      this.endRadius = endRadius;
   }

   public boolean isFriction()
   {
      return friction;
   }

   public void setFriction(boolean friction)
   {
      this.friction = friction;
   }

   public float getDeviation()
   {
      return deviation;
   }

   public void setDeviation(float dev)
   {
      this.deviation = dev;
   }

   @Override
   public void update(int delta)
   {
      super.update(delta);
      position = position.plus(velocity.multiply(delta / 1000f));

      if (deviation > 0)
      {
         position = position.plus(new Vector2D(
                 (Utils.rand.nextFloat() - 0.5f) * deviation * delta / 1000f,
                 (Utils.rand.nextFloat() - 0.5f) * deviation * delta / 1000f));
      }

      if (friction)
      {
         velocity = velocity.multiply((float) Math.pow(0.99, delta));
      }
   }

//   @Override
//   public void renderShadow(GameContainer gc, BasicGame sb, Graphics gr)
//   {
//      if (getShadow() == null)
//      {
//         return;
//      }
//
//      getShadow().getColor().setA(getLifeTimeRatio() / 2);
//      float curWidth = getCurrentRadius() * 2;
//      Builder.template.draw(position.x + ViewUtils.lengthToView(getShadow().getDx()) - curWidth / 2,
//              position.y + ViewUtils.lengthToView(getShadow().getDy()) - curWidth / 2, curWidth, curWidth,
//              getShadow().getColor().toColor());
//   }

   public float getCurrentRadius()
   {
      float ep = getExecutionPercent();
      return startRadius * ep + (1 - ep) * endRadius;
   }

   @Override
   public int getDuration()
   {
      return duration;
   }

   public void setDuration(int duration)
   {
      this.duration = duration;
   }

   public Vector2D getVelocity()
   {
      return velocity;
   }

   public void setVelocity(Vector2D velocity)
   {
      this.velocity = velocity;
   }

   public Point getPosition()
   {
      return position;
   }

   public void setPosition(Point position)
   {
      this.position = position;
   }

   @Override
   public void draw(Graphics g)
   {
      Color c = Utils.toColorWithAlpha(color, getExecutionPercent());
      float curRadius = getCurrentRadius();
      
      Point pos = Camera.getCamera().toView(position);
      float x = pos.getX() - curRadius;
      float y = pos.getY() - curRadius;

      g.setColor(Utils.toColorWithAlpha(Color.gray, getExecutionPercent()));
      g.fill(new Circle(x, y, curRadius));

      float d = Math.min(1, curRadius/5);
      g.setColor(Utils.toColorWithAlpha(color, getExecutionPercent()));
      g.fill(new Circle(x+d, y+d, curRadius));
   }

   static public class Builder
   {
      static private final Map<ParticleType, Properties> propertiesMap =
              new HashMap<ParticleType, Properties>();
      static public final int TEMPLATE_WIDTH = 100;
      static public final int TEMPLATE_HEIGHT = 100;
      static
      {
         for (ParticleType type : ParticleType.values())
         {
            getProperties(type);
         }
      }

      static private Image createTemplate()
      {
         Image res = null;
         try
         {
            res = new Image(TEMPLATE_WIDTH, TEMPLATE_HEIGHT);
            res.getGraphics()
                    .fill(new Circle(TEMPLATE_WIDTH / 2, TEMPLATE_WIDTH / 2, TEMPLATE_WIDTH / 2));
            res.getGraphics().flush();
         }
         catch (SlickException e)
         {
            e.printStackTrace();
         }

         return res;
      }

      static public ParticleEntity createEntity(ParticleType type)
      {
         ParticleEntity res = new ParticleEntity();
         Properties p = getProperties(type);

         res.setDuration(Integer.valueOf(p.getProperty("lifeTime", "0")));
         res.setFriction(Boolean.valueOf(p.getProperty("friction", "false")));
         res.setDeviation(Float.valueOf(p.getProperty("deviation", "0")));
         res.setStartRadius(Float.valueOf(p.getProperty("startRadius", "0")));
         res.setEndRadius(Float.valueOf(p.getProperty("endRadius", "0")));
         res.setColor(new Color(Float.valueOf(p.getProperty("red", "0")),
                 Float.valueOf(p.getProperty("green", "0")),
                 Float.valueOf(p.getProperty("blue", "0")),
                 Float.valueOf(p.getProperty("alpha", "0"))));
         
         return res;
      }

      static public Properties getProperties(ParticleType type)
      {
         if (!propertiesMap.containsKey(type))
         {
            Properties p = new Properties();
            try
            {
               p.load(ResourceLoader.getResourceAsStream(
                       "particles/" + type.name() + ".properties"));
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
            propertiesMap.put(type, p);
         }

         return propertiesMap.get(type);
      }
   }
}
