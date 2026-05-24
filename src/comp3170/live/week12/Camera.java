package comp3170.live.week12;

import static comp3170.Math.TAU;

import comp3170.live.common.cameras.PerspectiveOrbittingCamera;

public class Camera extends PerspectiveOrbittingCamera {

	private static final float DISTANCE = 11;
	private static final float WIDTH = 4;
	private static final float HEIGHT = 4;
	private static final float NEAR = 0.1f;
	private static final float FAR = 20;
	private static final float FOVY = TAU / 6; 
	private static final float ASPECT = 1;
	
	public Camera() {
//		super(DISTANCE, WIDTH, HEIGHT, NEAR, FAR);	// orthographic
		super(DISTANCE, FOVY, ASPECT, NEAR, FAR);	// perspective
	}

}
