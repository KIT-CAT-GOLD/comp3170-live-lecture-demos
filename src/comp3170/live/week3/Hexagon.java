package comp3170.live.week3;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import comp3170.GLBuffers;
import comp3170.Shader;
import comp3170.ShaderLibrary;

public class Hexagon {

	final private String VERTEX_SHADER = "vertex.glsl";
	final private String FRAGMENT_SHADER = "fragment.glsl";

	private Vector4f[] vertices;
	private int vertexBuffer;

	private int[] indices;
	private int indexBuffer;
	
	private Shader shader;
	private int screenWidth;
	private int screenHeight;
    private long duration = 5000; // 5 seconds in millis

	public Hexagon() {
		
		// compile shader
		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

        // calculate vertices based upon a radius of 'r'
        float r = 0.8f;
        float r3d2 = (float) (r * Math.sqrt(3.0f))/2.0f;
        
		// @formatter:off
            vertices = new Vector4f[] {
                new Vector4f(0.0f, 0.0f, 0.0f, 1.0f),   // centre
                new Vector4f(-r, 0.0f, 0.0f, 1.0f),   // left middle
                new Vector4f(-r/2.0f, -r3d2, 0.0f, 1.0f),  // bottom left
                new Vector4f(r/2.0f, -r3d2, 0.0f, 1.0f),   // bottom right
                new Vector4f(r, 0.0f, 0.0f, 1.0f),    // right middle
                new Vector4f(r/2.0f, r3d2, 0.0f, 1.0f),    // top right
                new Vector4f(-r/2.0f, r3d2, 0.0f, 1.0f),   // top left
        };

		indices = new int[] {
				0, 1, 2,   // Triangle 1
				0, 2, 3,   // Triangle 2
				0, 3, 4,   // Triangle 3
				0, 4, 5,   // Triangle 4
				0, 5, 6,   // Triangle 5
				0, 6, 1,   // Triangle 6
		};

		// @formatter:on
		vertexBuffer = GLBuffers.createBuffer(vertices);
		indexBuffer = GLBuffers.createIndexBuffer(indices);

	}

	public void draw() {
		// activate the shader
		shader.enable();

		// connect the vertex buffer to the a_position attribute
		shader.setAttribute("a_position", vertexBuffer);

		// write the colour value into the u_colour uniform
		Vector3f colour = new Vector3f(1.0f, 0.0f, 0.0f);
		shader.setUniform("u_colour", colour);

		// query clock
		long currTime = System.currentTimeMillis();
		// phase ranges between 0.0 and 1.0
		float phase = ((float) (currTime % duration)) / ((float) duration);
		shader.setUniform("u_phase", phase);
		
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);		
	}

}
