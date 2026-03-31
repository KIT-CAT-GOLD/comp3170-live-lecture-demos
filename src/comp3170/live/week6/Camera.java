package comp3170.live.week6;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import comp3170.SceneObject;
import comp3170.InputManager;

import static comp3170.Math.TAU;

public class Camera extends SceneObject {

	private float zoom = 2.0f;
	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f viewMatrix = new Matrix4f();

	public Camera() {
	}
	
	
	public Matrix4f GetViewMatrix(Matrix4f dest) {
		viewMatrix = getMatrix();
		return viewMatrix.invert(dest);
	}
	
	public Matrix4f GetProjectionMatrix(Matrix4f dest) {
		return projectionMatrix.invert(dest);
	}

	private final float ZOOM_SPEED = 1.0f;
	private float yAngle = 0f;
	private float xAngle = 0f;
	
	public void update(InputManager input, float deltaTime) {
		if (input.isKeyDown(GLFW_KEY_UP)) {
			zoom -= ZOOM_SPEED * deltaTime;
		}
		
		if (input.isKeyDown(GLFW_KEY_DOWN)) {
			zoom += ZOOM_SPEED * deltaTime;
		}
		
		getMatrix().identity().rotateY(yAngle).rotateX(xAngle);
		projectionMatrix.scaling(zoom,zoom,1.0f); // Doesn't work for non-uniform screens - why? Check Week 4 slides!
	}
}