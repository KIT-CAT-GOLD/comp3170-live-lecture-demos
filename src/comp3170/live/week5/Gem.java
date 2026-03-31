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
import static org.lwjgl.opengl.GL15.glDrawArrays;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glPolygonMode;
import static org.lwjgl.opengl.GL15.GL_POINT;
import static org.lwjgl.opengl.GL15.GL_POINTS;
import static org.lwjgl.opengl.GL15.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL15.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_UNSIGNED_INT;

public class Gem extends SceneObject {
	
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
	// parameters
	private float height = 1.0f;
	private int nSides = 4;
	

	public Gem(Vector3f baseColour) {
		this.baseColour = baseColour;
		
		// compile shader 
		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);
		
		createMesh();

//		eye = new Eye();
//		eye.setParent(this);
//		eye.getMatrix().translate(eyePosition).scale(eyeScale);
	}
	
	private void createMesh() {
		vertices = new Vector4f[2 + nSides];
		vertices[0] = new Vector4f(0.0f, height / 2, 0.0f, 1.0f);
		vertices[1] = new Vector4f(0.0f, -height / 2, 0.0f, 1.0f);

		// start at vertex 1
		// first loop around the top
		for(int i=2; i<=nSides+1; i++) {
			double angle = (i * TAU / nSides);
			float x = (float) (Math.cos(angle));
			float z = (float) (Math.sin(angle));
			float y = 0.0f;
			Vector4f vert = new Vector4f(x, y, z, 1.0f);
			vertices[i] = vert;
		}

		indices = new int[nSides*2*3];
		int j=0;
		for(int i=1; i<nSides; i++) {
			indices[j++] = 0;
			indices[j++] = (i % nSides) + 1;
			indices[j++] = i;
		}

		int k=nSides*3;
		for(int i=1; i<=(nSides); i++) {
			indices[k++] = vertices.length;
			indices[k++] = i;
			indices[k++] = (i % nSides) + 1;
			
		}
		// second loop around the bottom
		// @formatter:off
//		vertices = new Vector4f[] {
//				new Vector4f(-0.8f, -1.0f, 0.0f, 1.0f), // P0
//				new Vector4f(0.8f, -1.0f, 0.0f, 1.0f),  // P1
//
//				new Vector4f(0.1f, -0.3f, 0.0f, 1.0f),   // P2
//				new Vector4f(-0.1f, -0.3f, 0.0f, 1.0f),  // P3
//				
//				new Vector4f(0.8f, 0.2f, 0.0f, 1.0f),   // P4
//				new Vector4f(-0.8f, 0.2f, 0.0f, 1.0f),  // P5
//				new Vector4f(0.f, 1.0f, 0.0f, 1.0f),    // P6	
//			
//		};
		
//		indices = new int[] {
//				0, 1, 2,
//				2, 3, 0,
//				
//				2, 4, 3,
//				3, 4, 5,
//				4, 5, 6,
//		};
		
//		colours = new Vector3f[] {
//				new Vector3f(0.4f, 0.0f, 0.1f),
//				new Vector3f(0.0f, 0.4f, 0.15f),
//				
//				new Vector3f(0.4f, 0.0f, 0.1f),
//				new Vector3f(0.0f, 0.4f, 0.15f),
//				
//				new Vector3f(0.4f, 0.0f, 0.1f),
//				new Vector3f(0.0f, 0.4f, 0.15f),
//				new Vector3f(0.4f, 0.4f, 0.15f),
//		};
		// @formatter:on

		vertexBuffer = GLBuffers.createBuffer(vertices);
		indexBuffer = GLBuffers.createIndexBuffer(indices);
//		colourBuffer = GLBuffers.createBuffer(colours);
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
//		shader.setAttribute("a_colour",colourBuffer);

//		glPolygonMode(GL_FRONT_AND_BACK, GL_POINT);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
//		glDrawArrays(GL_POINTS, 0, vertices.length);
	}
}
