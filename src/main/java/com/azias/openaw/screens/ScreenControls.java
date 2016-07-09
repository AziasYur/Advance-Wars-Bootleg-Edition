package com.azias.openaw.screens;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azias.openaw.OpenAW;
import com.azias.openaw.mod.ModLoader;
import com.azias.openaw.tests.*;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.math.Vector3;

public class ScreenControls extends ScreenAdapter implements ApplicationListener, InputProcessor, ControllerListener {
	private final static Logger logger = LoggerFactory.getLogger(ModLoader.class);
	private OpenAW game;

	public ScreenControls(OpenAW game) {
		this.game = game;
		Controllers.addListener(this);
		Gdx.input.setInputProcessor(this);
		this.create();
	}
	
	//Why isn't it called by default...
	@Override
	public void create() {
		logger.info("- - - - - - - - - - - - - - - - - - - - - -");
		for (Controller controller : Controllers.getControllers()) {
			logger.info("Controller detected: {}", controller.getName());
		}/**/
	}

	public void update () {
		
	}

	public void draw () {
		Gdx.gl30.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
		Gdx.gl30.glClear(GL30.GL_COLOR_BUFFER_BIT);
		
		this.game.batch.begin();
		
		this.game.batch.end();
	}

	@Override
	public void render() {
		draw();
	}

	@Override
	public void render(float delta) {
		render();
		update();
	}

	@Override
	public void pause() {
		
	}

	@Override
	public boolean keyDown(int keycode) {
		logger.info("keyDown: {}", keycode);
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		logger.info("keyTyped: {}", character);
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		logger.info("touchDown: {}", button);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
	
	/* Controller Inputs */
	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		//Puts too much data in the logs
		//logger.info("axisMoved: {} - {} - {}", controller.getName(), axisCode, value);
		return false;
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		logger.info("buttonDown: {} - {}", controller.getName(), buttonCode);
		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void connected(Controller controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnected(Controller controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		// TODO Auto-generated method stub
		return false;
	}
}
