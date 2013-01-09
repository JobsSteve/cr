package com.gts.cr.models;

import org.anddev.andengine.entity.sprite.Sprite;



public class Car {
	int id;
	int topSpeed;
	int accelerationSpeed;
	
	public float initialAngle;
	public Sprite sprite;
	public int currentSpeed;
	public float distance_traveled;
	public boolean hasCrashed;
	public Car(int id, int topSpeed, int accelerationSpeed) {
		super();
		this.id = id;
		this.topSpeed = topSpeed;
		this.accelerationSpeed = accelerationSpeed;
	}
	public Car() {
		super();
	}
	public Sprite getSprite() {
		return sprite;
	}
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTopSpeed() {
		return topSpeed;
	}
	public void setTopSpeed(int topSpeed) {
		this.topSpeed = topSpeed;
	}
	public int getAccelerationSpeed() {
		return accelerationSpeed;
	}
	public void setAccelerationSpeed(int accelerationSpeed) {
		this.accelerationSpeed = accelerationSpeed;
	}
	@Override
	public String toString() {
		return "Car id:"+this.id+",Car topspeed:"+this.topSpeed;
	}
}
