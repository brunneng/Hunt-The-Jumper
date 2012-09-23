package com.greenteam.huntjumper.model.bonuses.coin;

import com.greenteam.huntjumper.IMatch;
import com.greenteam.huntjumper.audio.AudioSystem;
import com.greenteam.huntjumper.effects.particles.ParticleEntity;
import com.greenteam.huntjumper.match.*;
import com.greenteam.huntjumper.model.AbstractMapObject;
import com.greenteam.huntjumper.model.ILightSource;
import com.greenteam.huntjumper.model.Jumper;
import com.greenteam.huntjumper.model.bonuses.IBonus;
import com.greenteam.huntjumper.parameters.GameConstants;
import com.greenteam.huntjumper.parameters.ViewConstants;
import com.greenteam.huntjumper.shaders.Shader;
import com.greenteam.huntjumper.shaders.ShadersSystem;
import com.greenteam.huntjumper.utils.Point;
import com.greenteam.huntjumper.utils.Utils;
import com.greenteam.huntjumper.utils.Vector2D;
import net.phys2d.math.ROVector2f;
import net.phys2d.raw.Body;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.shader.ShaderProgram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: GreenTea Date: 10.07.12 Time: 0:16
 */
public class Coin extends AbstractMapObject implements IBonus, ILightSource
{
   private static ShaderProgram program;
   private static Image coinImage;

   private static final float width = ViewConstants.COIN_SPHERE_RECT_WIDTH;
   private static List<Color> colors = Arrays.asList(Color.red, Color.green, Color.blue);
   private Vector2D rotationVector = Vector2D.fromAngleAndLength(90, 2);

   private static void init()
   {
      if (program != null || coinImage != null)
      {
         return;
      }

      if (ShadersSystem.getInstance().isReady())
      {
         program = ShadersSystem.getInstance().getProgram(Shader.COIN);
      }
      else
      {
         try
         {
            coinImage = new Image("images/coin.png");
         }
         catch (SlickException e)
         {
            e.printStackTrace();
         }
      }
   }

   private Point pos;

   public Coin(Point pos)
   {
      super(MapObjectIdFactory.getInstance().getNextId(MapObjectType.BONUS));
      this.pos = pos;
   }

   @Override
   public void update(int delta)
   {
      rotationVector = rotationVector.rotate(delta * -0.250f);
   }

   @Override
   public void draw(Graphics g)
   {
      init();

      Point viewPos = Camera.getCamera().toView(pos);
      if (!Camera.getCamera().inViewScreenWithReserve(viewPos))
      {
         return;
      }

      ShadersSystem shadersSystem = ShadersSystem.getInstance();
      if (shadersSystem.isReady())
      {
         program.bind();
         shadersSystem.setPosition(program, viewPos.getX(), viewPos.getY());
         program.setUniform1f("sphereRadius", width/2f);
         program.setUniform1f("angle", rotationVector.angle());
         program.setUniform1f("len", rotationVector.length());

         float dxy = width/2f + rotationVector.length();
         g.fillRect(viewPos.getX() - dxy, viewPos.getY() - dxy, 2*dxy, 2*dxy);

         ShaderProgram.unbind();
      }
      else
      {
         float dxy = coinImage.getWidth() / 2f;

         Vector2D dir = new Vector2D(rotationVector);
         float rotationAngle = 360 / colors.size();
         for (Color c : colors)
         {
            g.drawImage(
                    coinImage, viewPos.getX()+dir.getX() - dxy,
                    viewPos.getY()+dir.getY() - dxy,
                    c);
            dir = dir.rotate(rotationAngle);
         }

         g.drawImage(coinImage, viewPos.getX() - dxy, viewPos.getY() - dxy, Color.white);
      }
   }

   private List<ParticleEntity> createTakeCoinParticles()
   {
      List<ParticleEntity> res = new ArrayList<>();

      float startRadius = width * 0.6f;
      Vector2D dir = new Vector2D(rotationVector);
      float rotationAngle = 360 / colors.size();
      for (Color c : colors)
      {
         res.add(createParticleEntity(startRadius, dir, c));
         dir = dir.rotate(rotationAngle);
      }

      ParticleEntity pe = createParticleEntity(startRadius*0.6f, new Vector2D(), Color.white);
      pe.setDrawShadow(false);
      res.add(pe);

      return res;
   }

   private ParticleEntity createParticleEntity(float startRadius, Vector2D dir, Color c)
   {
      c = Utils.toColorWithAlpha(c, 0.9f);
      ParticleEntity pe = new ParticleEntity();
      pe.setPosition(pos.plus(dir));
      pe.setColor(c);
      pe.setDuration(200);
      pe.setStartRadius(startRadius);
      pe.setEndRadius(startRadius/3);
      return pe;
   }

   public Point getPosition()
   {
      return pos;
   }

   @Override
   public Body getBody()
   {
      return null;
   }

   @Override
   public void onBonusTaken(IMatch match, Jumper jumper)
   {
      match.getScoresManager().signalCoinTaken(jumper);

      EffectsContainer.getInstance()
              .addEffect(new FlyUpTextEffect(new FlyUpTextEffect.IGetPositionCallback()
              {
                 @Override
                 public ROVector2f getPosition()
                 {
                    return Coin.this.getPosition().toVector2f();
                 }
              }, "+" + (int) GameConstants.COIN_SCORES, ViewConstants.TAKE_COIN_EFFECT_DURATION,
                      ViewConstants.TAKE_COIN_EFFECT_COLOR, ViewConstants.TAKE_COIN_EFFECT_FONT,
                      ViewConstants.TAKE_COIN_EFFECT_HEIGHT));

      AudioSystem.getInstance().playFarSound(AudioSystem.TAKE_COIN_SOUND,
              match.getMyJumper().getBody().getPosition(), jumper.getBody().getPosition());

      EffectsContainer.getInstance().addAllEffects(createTakeCoinParticles());
   }

   @Override
   public Color getLightColor()
   {
      return Color.white;
   }

   @Override
   public float getLightCircle()
   {
      return 0;
   }

   @Override
   public float getLightMaxRadius()
   {
      return ViewConstants.COIN_LIGHT_MAX_RADIUS;
   }
}
