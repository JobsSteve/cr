package com.gts.cr.models;

import java.util.ArrayList;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

public class Barrier {
	public static final int BARRIER_HIT = 1;
	public static final int BARRIER_TYPE_SANDBAG = 2;
	public static final int BARRIER_TYPE_CONES = 3;
	public static final int BARRIER_TYPE_POLICE = 4;
	public static final int BARRIER_TYPE_TYRE = 5;
	public static final int BARRIER_TYPE_NITROUS = 6;
	public static final int BARRIER_POSITION_LEFT = 7;
	public static final int BARRIER_POSITION_RIGHT = 8;
	public int status;
	public int type;
	public int position;
	public ArrayList<Sprite> components;
	float currentYPosition;
	float startXPosition;
	public float getStartXPosition() {
		return startXPosition;
	}
	public void setStartXPosition(float startXPosition) {
		this.startXPosition = startXPosition;
	}
	public float getCurrentYPosition() {
		return currentYPosition;
	}
	public void setCurrentYPosition(float currentYPosition) {
		this.currentYPosition = currentYPosition;
	}
}
