package comp3170.live.week5;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Matrix4f;

import comp3170.GLBuffers;
import comp3170.SceneObject;
import comp3170.Shader;
import comp3170.ShaderLibrary;
import static comp3170.Math.TAU;

import static org.lwjgl.opengl.GL15.glDrawElements;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_UNSIGNED_INT;

public class Fish extends SceneObject {
	
	final private String VERTEX_SHADER = "vertex_vertColouring.glsl";
	final private String FRAGMENT_SHADER = "fragment_vertColouring.glsl";
	
	private Vector4f[] vertices;
	private int vertexBuffer;
	private int[] indices;
	private int indexBuffer;
	
	private Vector3f[] colours;
	private int colourBuffer;
	private Vector3f baseColour = new Vector3f(1.0f,1.0f,1.0f);
	
	private Shader shader;
			
	final private float MOVEMENT_SPEED = 1f;
	final private float ROTATION_RATE = TAU/12;
	
	// Eye variables
	private Eye eye;
	private Vector3f eyePosition = new Vector3f(0.2f,0.3f,0.0f);
	private float eyeScale = 0.2f;
	private float pulse = 1.0f;
	private int direction = 1;

	public Fish(Vector3f baseColour) {
		this.baseColour = baseColour;
		
		// compile shader 
		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);
		
		createMesh();

		eye = new Eye();
		eye.setParent(this);
		eye.getMatrix().translate(eyePosition).scale(eyeScale);
	}
	
	private void createMesh() {
		// @formatter:off
		vertices = new Vector4f[] {
				new Vector4f(-0.8f, -1.0f, 0.0f, 1.0f), // P0
				new Vector4f(0.8f, -1.0f, 0.0f, 1.0f),  // P1

				new Vector4f(0.1f, -0.3f, 0.0f, 1.0f),   // P2
				new Vector4f(-0.1f, -0.3f, 0.0f, 1.0f),  // P3
				
				new Vector4f(0.8f, 0.2f, 0.0f, 1.0f),   // P4
				new Vector4f(-0.8f, 0.2f, 0.0f, 1.0f),  // P5
				new Vector4f(0.f, 1.0f, 0.0f, 1.0f),    // P6	
			
		};
		
		indices = new int[] {
				0, 1, 2,
				2, 3, 0,
				
				2, 4, 3,
				3, 4, 5,
				4, 5, 6,
		};
		
		colours = new Vector3f[] {
				new Vector3f(0.4f, 0.0f, 0.1f),
				new Vector3f(0.0f, 0.4f, 0.15f),
				
				new Vector3f(0.4f, 0.0f, 0.1f),
				new Vector3f(0.0f, 0.4f, 0.15f),
				
				new Vector3f(0.4f, 0.0f, 0.1f),
				new Vector3f(0.0f, 0.4f, 0.15f),
				new Vector3f(0.4f, 0.4f, 0.15f),
		};
		// @formatter:on

		vertexBuffer = GLBuffers.createBuffer(vertices);
		indexBuffer = GLBuffers.createIndexBuffer(indices);
		colourBuffer = GLBuffers.createBuffer(colours);
	}
	
	public void update(float deltaTime) {
		float movement = MOVEMENT_SPEED * deltaTime;
		float rotation = ROTATION_RATE * deltaTime;
//		System.out.println(deltaTime);
		getMatrix().translate(0.0f,movement,0.0f).rotateZ(rotation); //.scale(1.001f);
		if(direction > 0 && pulse < 2.0f) {
			pulse += 0.1f;
		} else if (direction > 0 && pulse >= 2.0f) {
			direction = -1;
		} else if (direction < 0 && pulse > 1.0f) {
			pulse -= 0.1f;
		} else if (direction < 0 && pulse <= 1.0f) {
			direction = 1;
		} else {
			// should not happen
		}
//		getMatrix().rotateZ(rotation).scaleLocal(1.001f);
	}
	
	public void drawSelf(Matrix4f mvpMatrix) {
		shader.enable();
		shader.setAttribute("a_position", vertexBuffer);
		shader.setUniform("u_mvpMatrix", mvpMatrix);
		shader.setUniform("u_baseColour", baseColour);
		shader.setAttribute("a_colour",colourBuffer);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
	}
}
