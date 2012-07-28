package com.greenteam.huntjumper.audio;

import com.greenteam.huntjumper.parameters.GameConstants;
import net.phys2d.math.ROVector2f;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: Алексей Date: 30.01.2011 Time: 16:15:16 To change this template
 * use File | Settings | File Templates.
 */
public final class AudioSystem
{
   private static final float NORMAL_SOUND_VOLUME = 0.08f;
   public static final String COLLISION_SOUND = "COLLISION_SOUND";
   public static final String HUNTING_SOUND = "HUNTING_SOUND";
   public static final String ESCAPING_SOUND = "ESCAPING_SOUND";
   public static final String TAKE_COIN_SOUND = "TAKE_BONUS_SOUND";
   public static final String TAKE_ACC_BONUS_SOUND = "TAKE_ACC_BONUS_SOUND";
   public static final String TAKE_GRAVITY_BONUS_SOUND = "TAKE_GRAVITY_BONUS_SOUND";

   private static AudioSystem system;

   private Map<String, Audio> wavEffects = new HashMap<String, Audio>();
   private Map<String, Float> wavEffectsVolume = new HashMap<String, Float>();

   public static AudioSystem getInstance()
   {
      if (system == null)
      {
         system = new AudioSystem();
      }
      return system;
   }

   private AudioSystem()
   {
      init();
   }

   private void init()
   {
      try
      {
         createSound("sound60.wav", COLLISION_SOUND, 1f);
         createSound("sound98.wav", HUNTING_SOUND, 2.5f);
         createSound("beep21.wav", ESCAPING_SOUND, 1f);
         createSound("rattle_high_01.wav", TAKE_COIN_SOUND, 0.4f);
         createSound("wind1.wav", TAKE_ACC_BONUS_SOUND, 1.5f);
         createSound("Hit_Deep_Bass.wav", TAKE_GRAVITY_BONUS_SOUND, 1.5f);

         SoundStore.get().setMaxSources(5);
         SoundStore.get().setSoundVolume(NORMAL_SOUND_VOLUME);
      }
      catch (IOException e)
      {
         throw new RuntimeException("Can't initialize sound system", e);
      }

   }

   private void createSound(String fileName, String soundAlias, float volume) throws IOException
   {
      InputStream in;
      in = new BufferedInputStream(
              ClassLoader.getSystemResourceAsStream("sounds/" + fileName));
      wavEffects.put(soundAlias, AudioLoader.getAudio("WAV", in));
      wavEffectsVolume.put(soundAlias, volume);
   }

   public void update(int delta)
   {
      SoundStore.get().poll(delta);
   }

   public void playFarSound(String name, ROVector2f from, ROVector2f to)
   {
      float collisionDist = from.distance(to);
      float volumePercent = Math.max(
              1 - collisionDist / GameConstants.MAX_SOUNDS_DIST, 0f);
      AudioSystem.getInstance().playSound(name, volumePercent);
   }

   public void playSound(String name, float volumePercent)
   {
      float volume = wavEffectsVolume.get(name) * volumePercent;
      Audio s = wavEffects.get(name);
      s.playAsSoundEffect(1.0f, volume, false);
   }
}
