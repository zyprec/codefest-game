package com.lhs.bird.desktop;

import org.mini2Dx.desktop.DesktopMini2DxConfig;

import com.badlogic.gdx.backends.lwjgl.DesktopMini2DxGame;
import com.lhs.bird.MainGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		DesktopMini2DxConfig config = new DesktopMini2DxConfig(MainGame.GAME_IDENTIFIER);
		config.vSyncEnabled = true;
		config.width = 1280;
		config.height = 720;
		new DesktopMini2DxGame(new MainGame(), config);
	}
}
