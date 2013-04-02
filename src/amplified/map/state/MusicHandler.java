package amplified.map.state;

import amplified.resources.SoundCache;

public class MusicHandler {
	
	private static boolean oldPlaying, newPlaying;
	
	public static void startPlayingOld(){
		if (newPlaying){
			SoundCache.getSound("newMusic").stop();
			newPlaying = false;
		}
		if (!oldPlaying){
			SoundCache.getSound("oldMusic").playAsMusic(1, 1, true);
			oldPlaying = true;
		}
	}
	
	public static void startPlayingNew(){
		if (oldPlaying){
			SoundCache.getSound("oldMusic").stop();
			oldPlaying = false;
		}
		if (!newPlaying){
			SoundCache.getSound("newMusic").playAsMusic(1, 1, true);
			newPlaying = true;
		}
	}
	
	
}
