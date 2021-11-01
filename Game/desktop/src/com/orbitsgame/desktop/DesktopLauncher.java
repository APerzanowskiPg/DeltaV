package com.orbitsgame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.orbitsgame.DeltaVGame;

/*
Used libGdx, source: http://libgdx.com/
*/

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
                config.useGL30 = true;
                config.resizable = false;
                config.width = 1280;
                config.height = 720;
                //config.gles30ContextMajorVersion = ;
		new LwjglApplication(new DeltaVGame(), config);
	}
}
