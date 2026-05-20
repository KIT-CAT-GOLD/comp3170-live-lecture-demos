package comp3170.live.week12;

import static comp3170.Math.TAU;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;

import java.awt.Color;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import comp3170.GLBuffers;
import comp3170.SceneObject;
import comp3170.Shader;
import comp3170.ShaderLibrary;
import comp3170.live.week8.Week8;

public class Cylinder extends SceneObject {
	
	final private String VERTEX_SHADER = "colourVertex.glsl";
	final private String FRAGMENT_SHADER = "colourFragment.glsl";
	final private String WIREFRAME_VERTEX_SHADER = "simpleVertex.glsl";
	final private String WIREFRAME_FRAGMENT_SHADER = "simpleFragment.glsl";
	
	private Vector4f[] vertices; 
	private int vertexBuffer;
	
	private Vector4f[] colours; 
	private int colourBuffer;

	private Vector4f wireColour = new Vector4f(0,0,0,1);
	
	private int[] indices;
	private int indexBuffer;

	private static final int NSIDES = 8;
	
	private Shader shader;
	private Shader wireframeShader;
	
	public Cylinder() {
		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);
		wireframeShader = ShaderLibrary.instance.compileShader(WIREFRAME_VERTEX_SHADER, WIREFRAME_FRAGMENT_SHADER);
		shader.setStrict(false);
		wireframeShader.setStrict(false);
		
		createBuffers();
	}

	private void createBuffers() {
		createAttributeBuffers();				
		createIndexBuffer();
	}

	private void createAttributeBuffers() {
		vertices = new Vector4f[2 + NSIDES * 2];	// top + bottom + 2 * sides
		colours = new Vector4f[2 + NSIDES * 2];

		vertices[0] = new Vector4f(0,0,0,1);	// bottom
		colours[0] = new Vector4f(1,1,1,1);
		
		vertices[1] = new Vector4f(0,1,0,1);	// top
		colours[1] = new Vector4f(1,1,1,1);
			
		int k = 2;
		
		float[] rgba = new float[4];
		for (int i = 0; i < NSIDES; i++) {
			float angle = TAU * i / NSIDES;
			float hue = (float)i / NSIDES;
			Color c = Color.getHSBColor(hue, 1, 1);
			c.getColorComponents(rgba);
			rgba[3] = 1; // for some reason getHSBColor sets alpha = 0

			// bottom
			vertices[k] = new Vector4f(1,0,0,1).rotateY(angle);
			colours[k] = new Vector4f(rgba);			
			k++;

			// top 
			vertices[k] = new Vector4f(1,1,0,1).rotateY(angle);
			colours[k] = new Vector4f(rgba);			
			k++;
		}
		
		vertexBuffer = GLBuffers.createBuffer(vertices);		
		colourBuffer = GLBuffers.createBuffer(colours);
	}
	
	private void createIndexBuffer() {
		
		// example for 6 sides 
		//   1   1          1
		//  / \ / \        / \
		// 3---5---7 ... 13---3  <- wrap around to start
		// |  /|  /|      |  /|
		// | / | / |      | / |
		// |/  |/  |      |/  |
		// 2---4---6 ... 12---2
		//  \ / \ /        \ /
		//   0   0          0
		
		int nTriangles = 4 * NSIDES; // 2 triangles per side panel + top triangles + bottom triangles
		indices = new int[nTriangles * 3];	// 3 vertices per triangle  
		
		int k = 0;		
		for (int i0 = 0; i0 < NSIDES; i0++) {
			
			int i1 = (i0+1) % NSIDES;
			
			// top
			indices[k++] = 0; 			
			indices[k++] = 2 + 2 * i1;
			indices[k++] = 2 + 2 * i0;	

			// bottom 
			indices[k++] = 1; 			
			indices[k++] = 2 + 2 * i0 + 1;	
			indices[k++] = 2 + 2 * i1 + 1;

			// side			
			indices[k++] = 2 + 2 * i0;	
			indices[k++] = 2 + 2 * i1;
			indices[k++] = 2 + 2 * i0 + 1;
			
			indices[k++] = 2 + 2 * i1 + 1;
			indices[k++] = 2 + 2 * i0 + 1;			
			indices[k++] = 2 + 2 * i1;
		}		
		
		indexBuffer = GLBuffers.createIndexBuffer(indices);
	}
	
	public void drawSelf(Matrix4f mvpMatrix, int pass) {
		
		switch (pass) {
		
		case Week8.OPAQUE_PASS:
			// Opaque pass: draw the faces
	
			shader.enable();		
			shader.setUniform("u_mvpMatrix", mvpMatrix);
			shader.setAttribute("a_position", vertexBuffer);
			shader.setAttribute("a_colour", colourBuffer);
			
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
			glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
			break;
	
		case Week8.WIREFRAME_PASS:
			// draw the wireframe
			
			wireframeShader.enable();
			wireframeShader.setUniform("u_mvpMatrix", mvpMatrix);
			wireframeShader.setAttribute("a_position", vertexBuffer);
			wireframeShader.setUniform("u_colour", wireColour);
			
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
			glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
			break;
		}
	}
}
