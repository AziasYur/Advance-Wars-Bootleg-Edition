package com.azias.advancewarsbootleg.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import com.azias.advancewarsbootleg.AdvanceWarsBootleg;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		String[] a = new String[] {"Black","Blue","Green","Red","Yellow"};
		Random rand = new Random();
		int b = rand.nextInt(a.length);
        config.addIcon("assets-desktop/icons/icon"+a[b]+".png", FileType.Internal);
		try {
			Scanner sc = new Scanner(new File("assets-desktop/titleMessages.txt")).useDelimiter("\\Z");
			String messages[] = sc.next().replace("\n", "").replace("\r", "").split(";");
			sc.close();
			int c = rand.nextInt(messages.length);
			config.title = "Advance Wars Bootleg Edition - "+messages[c];
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			config.title = "Advance Wars Bootleg Edition";
		}
		config.width = 1280;
		config.height = 720;
		config.resizable = false;
		new LwjglApplication(new AdvanceWarsBootleg(), config);
	}
}
