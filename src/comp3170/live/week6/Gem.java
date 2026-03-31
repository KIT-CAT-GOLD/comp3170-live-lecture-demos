package comp3170.live.week6;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Matrix4f;

import comp3170.GLBuffers;
import comp3170.InputManager;
import comp3170.SceneObject;
import comp3170.Shader;
import comp3170.ShaderLibrary;
import static comp3170.Math.TAU;

import static org.lwjgl.opengl.GL15.glDrawElements;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glPolygonMode;

import static org.lwjgl.opengl.GL15.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL15.GL_LINE;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_UNSIGNED_INT;

public class Gem extends SceneObject {
	
	final private String VERTEX_SHADER = "vertex_simple.glsl";
	final private String FRAGMENT_SHADER = "fragment_simple.glsl";
	
	private Vector4f[] vertices; // An array of points that make up the house
	private int vertexBuffer;
	private int[] indices;
	private int indexBuffer;

	// Paramaters
	private Vector3f baseColour = new Vector3f(1.0f,1.0f,1.0f);
	private float height = 1f;
	private int nSides = 4;
	
	private Shader shader;
	
	public Gem(Vector3f baseColour, float height, int nSides) {
		this.baseColour = baseColour;
		this.height = height;
		this.nSides = nSides;
		
		// compile shader 
		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

		// @formatter:off
		
		vertices = new Vector4f[2 + nSides];
		
		vertices[0] = new Vector4f(0.0f, height/2,0.0f, 1.0f);
		vertices[vertices.length - 1] = new Vector4f(0.0f, -height/2,0.0f, 1.0f);
		
		// Starting at vertex 1
		// first loop doing top
		// second loop doing bottom
		
		for (int i = 1; i <= nSides; i ++)
		{
			double angle = (i * (TAU / nSides));
			float x = (float) (Math.cos(angle));
			float z = (float) (Math.sin(angle));
			float y = 0.0f;
			Vector4f vert = new Vector4f(x,y,z,1);
			vertices[i] = vert;
		}
		
		indices = new int[(nSides*2)*3];
		
		int j = 0;
		for (int i = 1; i <= (nSides); i++) {
			indices[j++] = 0;
			indices[j++] = (i%nSides) + 1; // Wrap around
			indices[j++] = i;
		}
		
		int k = nSides*3;
		for (int i = 1; i <= (nSides); i++) {
			indices[k++] = vertices.length - 1;
			indices[k++] = i;
			indices[k++] = (i%nSides) + 1; // Wrap around
			System.out.println(i);
			System.out.println(i+1);
		}
		// @formatter:on
		
		vertexBuffer = GLBuffers.createBuffer(vertices);
		indexBuffer = GLBuffers.createIndexBuffer(indices);
	}
	
	private final float ROTATE_RATE = TAU/3;
	private float yAngle = 0f;
	private float xAngle = 0f;
	
	public void update(InputManager input, float deltaTime) {
		if (input.isKeyDown(GLFW_KEY_W)) {
			xAngle += ROTATE_RATE * deltaTime;
		}
		if (input.isKeyDown(GLFW_KEY_S)) {
			xAngle -= ROTATE_RATE * deltaTime;
		}
		if (input.isKeyDown(GLFW_KEY_A)) {
			yAngle -= ROTATE_RATE * deltaTime;
		}
		if (input.isKeyDown(GLFW_KEY_D)) {
			yAngle += ROTATE_RATE * deltaTime;
		}
		getMatrix().identity().rotateY(yAngle).rotateX(xAngle);
	}
	
	public void drawSelf(Matrix4f mvpMatrix) {
		shader.enable();
		shader.setAttribute("a_position", vertexBuffer);
		shader.setUniform("u_mvpMatrix", mvpMatrix);
		shader.setUniform("u_colour", baseColour);
		
		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
	}
}
