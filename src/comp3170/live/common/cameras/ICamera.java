package comp3170.live.common.cameras;

import org.joml.Matrix4f;

import comp3170.InputManager;

/**
 * A standard interface for all cameras
 */

public interface ICamera {
	/**
	 * Get the view matrix for this camera in 'dest'
	 * 
	 * @param dest A pre-allocated matrix. 
	 * @return 
	 */
	public Matrix4f getViewMatrix(Matrix4f dest);
	
	/**
	 * Get the projection matrix for this camera in 'dest'
	 * 
	 * @param dest A pre-allocated matrix. 
	 * @return
	 */
	public Matrix4f getProjectionMatrix(Matrix4f dest);

	/**
	 * Get the model to world matrix for this camera
	 * 
	 * @param dest A pre-allocated matrix. 
	 * @return
	 */

	public Matrix4f getModelMatrix(Matrix4f dest);

	/**
	 * Handle any per-frame updates & input
	 * 
	 * @param dest A pre-allocated matrix. 
	 * @return
	 */

	public void update(float deltaTime, InputManager input);

}
