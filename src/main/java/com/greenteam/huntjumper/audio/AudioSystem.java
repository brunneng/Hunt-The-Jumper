package com.greenteam.huntjumper.audio;

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
         InputStream in = new BufferedInputStream(
                 ClassLoader.getSystemResourceAsStream("sounds/sound60.wav"));
         wavEffects.put(COLLISION_SOUND, AudioLoader.getAudio("WAV", in));
         wavEffectsVolume.put(COLLISION_SOUND, NORMAL_SOUND_VOLUME);
         
         in = new BufferedInputStream(
                 ClassLoader.getSystemResourceAsStream("sounds/sound98.wav"));
         wavEffects.put(HUNTING_SOUND, AudioLoader.getAudio("WAV", in));
         wavEffectsVolume.put(HUNTING_SOUND, 2.5f*NORMAL_SOUND_VOLUME);

         in = new BufferedInputStream(
                 ClassLoader.getSystemResourceAsStream("sounds/beep21.wav"));
         wavEffects.put(ESCAPING_SOUND, AudioLoader.getAudio("WAV", in));
         wavEffectsVolume.put(ESCAPING_SOUND, NORMAL_SOUND_VOLUME);

         SoundStore.get().setMaxSources(5);
         SoundStore.get().setSoundVolume(NORMAL_SOUND_VOLUME);

      }
      catch (IOException e)
      {
         throw new RuntimeException("Can't initialize sound system", e);
      }

   }

   public void update(int delta)
   {
      SoundStore.get().poll(delta);
   }

   public void playSound(String name, float volumePercent)
   {
      float volume = wavEffectsVolume.get(name) * volumePercent;
      SoundStore.get().setSoundVolume(volume);
      Audio s = wavEffects.get(name);
      s.playAsSoundEffect(1.0f, 1.0f, false);
   }
}
