package comp3170.live.week6;

import org.joml.Vector3f;

import comp3170.InputManager;
import comp3170.SceneObject;
import static comp3170.Math.TAU;

public class Scene extends SceneObject {
	
	public static Scene theScene;

	private Camera camera;
	private Gem gem;
	
	private Vector3f gemColour = new Vector3f(1.0f,1.0f,1.0f);
	private float gemSize = 1.0f;
	private int gemSides = 8;

	public Scene () {		
		theScene = this;
		gem = new Gem(gemColour, gemSize, gemSides);
		gem.setParent(this);
		
		camera = new Camera();
		camera.setParent(this);
	}
	
	public Camera GetCamera() {
		return camera;
	}

	public void update(InputManager input, float deltaTime) {
		camera.update(input, deltaTime);
		gem.update(input, deltaTime);
	}
}
