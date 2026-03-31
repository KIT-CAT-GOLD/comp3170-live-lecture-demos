package comp3170.live.week5;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_FRONT_AND_BACK;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import comp3170.SceneObject;
import comp3170.Shader;
import comp3170.ShaderLibrary;
import comp3170.GLBuffers;
import static comp3170.Math.TAU;

public class Eye extends SceneObject {
	
	private String VERTEX_SHADER = "vertex_simple.glsl";
	private String FRAGMENT_SHADER = "fragment_simple.glsl";
	
	private Vector4f[] vertices;
	private int vertexBuffer;
	private int[] indices;
	private int indexBuffer;
	
	private Vector3f solidColour = new Vector3f(0.0f,0.0f,0.0f); // SOLID BLACK RGB
	private int nSides = 50;

	private Shader shader;
		
	public Eye() {
		createMesh();
		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);	
	}
	
	private void createMesh() {
		vertices = new Vector4f[nSides+1];
		
		vertices[0] = new Vector4f(0,0,0,1);	
		for (int i = 1; i <= nSides; i ++) {
			double angle = (i * (TAU) / (nSides));
			float x = (float) (Math.cos(angle));
			float y = (float) (Math.sin(angle));
			Vector4f vert = new Vector4f(x, y, 0, 1);
			vertices[i] = vert;
		}
		
		vertexBuffer = GLBuffers.createBuffer(vertices);	
		
		indices = new int[nSides*3];
		                   
		int j = 0; 
		for (int i = 1; i <= nSides; i++) { 
		indices[j++] = 0;
		indices[j++] = i;
		indices[j++] = (i % nSides) + 1; // Wrap around
		}
		
		indexBuffer = GLBuffers.createIndexBuffer(indices);
	}
	
	public void drawSelf(Matrix4f mvpMatrix)
	{
		shader.enable();
		shader.setAttribute("a_position", vertexBuffer);
		shader.setUniform("u_mvpMatrix", mvpMatrix);

		shader.setUniform("u_colour", solidColour);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		glPolygonMode(GL_FRONT_AND_BACK, GL_LINES);
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
	}
}
