package comp3170.live.week12;

import static comp3170.Math.TAU;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_POINT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import java.io.IOException;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import comp3170.GLBuffers;
import comp3170.InputManager;
import comp3170.OpenGLException;
import comp3170.SceneObject;
import comp3170.Shader;
import comp3170.ShaderLibrary;
import comp3170.TextureLibrary;
import comp3170.live.week8.Week8;

public class Gem extends SceneObject {
	
//	final private String VERTEX_SHADER = "normalVertex.glsl";
//	final private String FRAGMENT_SHADER = "normalFragment.glsl";
	final private String VERTEX_SHADER = "lightVertex.glsl";
	final private String FRAGMENT_SHADER = "lightFragmentPerspective.glsl";
	final private String WIREFRAME_VERTEX_SHADER = "simpleVertex.glsl";
	final private String WIREFRAME_FRAGMENT_SHADER = "simpleFragment.glsl";
	final private String DIFFUSE_TEXTURE = "brick_wall2-diff-1024.tga";
	final private String SPECULAR_TEXTURE = "brick_wall2-spec-1024.tga";
	
	private Vector4f[] vertices; 
	private int vertexBuffer;
	private Vector4f[] normals; 
	private int normalBuffer;
	private Vector2f[] uvs; 
	private int uvBuffer;

	private Vector4f wireColour = new Vector4f(0,0,0,1);
	
	private static final int NSIDES = 10;
	
	private Shader shader;
	private Shader wireframeShader;
	private int diffuseTextureID;
	private int specularTextureID;
	
	public Gem() {
		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);
		wireframeShader = ShaderLibrary.instance.compileShader(WIREFRAME_VERTEX_SHADER, WIREFRAME_FRAGMENT_SHADER);
		shader.setStrict(false);
		wireframeShader.setStrict(false);
		
		createBuffers();
		loadTextures();
	}

	private void loadTextures() {
		try {
			diffuseTextureID = TextureLibrary.instance.loadTexture(DIFFUSE_TEXTURE);
			specularTextureID = TextureLibrary.instance.loadTexture(SPECULAR_TEXTURE);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (OpenGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Wrap modes
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); // S is U
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT); // T is V

		// Filtering
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		// MipMaps
		glGenerateMipmap(GL_TEXTURE_2D);
		
	}

	private void createBuffers() {
		createAttributeBuffers();				
	}

	private void createAttributeBuffers() {
		int nVertices = 3 * 2 * NSIDES; // 2 facets per side, 3 verts per tri 
		vertices = new Vector4f[nVertices];	
		normals = new Vector4f[nVertices];	
		uvs = new Vector2f[nVertices];	

		int k = 0;
		
		float[] rgb = new float[3];
		for (int i = 0; i < NSIDES; i++) {
			float angle0 = TAU * i / NSIDES;
			float angle1 = TAU * (i+1) / NSIDES;

			Vector4f p0 = new Vector4f(0,1,0,1);	// top
			Vector4f p1 = new Vector4f(1,0,0,1).rotateY(angle0);
			Vector4f p2 = new Vector4f(1,0,0,1).rotateY(angle1);
			
			Vector4f n = new Vector4f(-1,1,0,0).rotateZ(-TAU/4);
			Vector4f n0 = new Vector4f(0,0,0,0);
			Vector4f n1 = n.rotateY(angle0, new Vector4f());
			Vector4f n2 = n.rotateY(angle1, new Vector4f());
			
			vertices[k] = p0;
			normals[k] = n0;
			uvs[k] = new Vector2f(0.5f, 1f);
			k++;
			
			vertices[k] = p1;
			normals[k] = n1;
			uvs[k] = new Vector2f(0f, 0f);
			k++;

			vertices[k] = p2;
			normals[k] = n2;
			uvs[k] = new Vector2f(1f, 0f);
			k++;
				
			p0 = new Vector4f(0,-1,0,1); // bottom
			p1 = new Vector4f(1,0,0,1).rotateY(angle1);
			p2 = new Vector4f(1,0,0,1).rotateY(angle0);
			
			n = new Vector4f(-1,-1,0,0).rotateZ(TAU/4);
			n0 = new Vector4f(0,0,0,0);
			n1 = n.rotateY(angle1, new Vector4f());
			n2 = n.rotateY(angle0, new Vector4f());
			
			vertices[k] = p0;
			normals[k] = n0;
			uvs[k] = new Vector2f(0.5f, 1f);
			k++;
			
			vertices[k] = p1;
			normals[k] = n1;
			uvs[k] = new Vector2f(1f, 0f);
			k++;

			vertices[k] = p2;
			normals[k] = n2;
			uvs[k] = new Vector2f(0f, 0f);
			k++;
		}
		
		vertexBuffer = GLBuffers.createBuffer(vertices);		
		normalBuffer = GLBuffers.createBuffer(normals);		
		uvBuffer = GLBuffers.createBuffer(uvs);		
	}
	
	private float shininess = 1000f;

	private Vector3f ambientIntensity = new Vector3f(0.1f,0.1f,0.1f);
	private Vector3f lightIntensity = new Vector3f(1,1,1);
	private Vector4f lightDirection = new Vector4f(1,0,0,0); 

	private Matrix4f cameraMatrix = new Matrix4f();
	private Vector4f camera = new Vector4f(0,0,0,0); 

	private Matrix4f modelMatrix = new Matrix4f();
	private Matrix4f normalMatrix = new Matrix4f();
	
	public void drawSelf(Matrix4f mvpMatrix, int pass) {
		
		switch (pass) {
		
		case Week8.OPAQUE_PASS:
			drawSelfOpaque(mvpMatrix);
			break;
	
		case Week8.WIREFRAME_PASS:
			drawSelfWireframe(mvpMatrix);
			break;
		}
	}

	private void drawSelfWireframe(Matrix4f mvpMatrix) {
		wireframeShader.enable();
		wireframeShader.setUniform("u_mvpMatrix", mvpMatrix);
		wireframeShader.setAttribute("a_position", vertexBuffer);
		wireframeShader.setUniform("u_colour", wireColour);
		
		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		glDrawArrays(GL_TRIANGLES, 0, vertices.length);
	}

	private void drawSelfOpaque(Matrix4f mvpMatrix) {
		// Opaque pass: draw the faces

		getModelToWorldMatrix(modelMatrix);
		modelMatrix.normal(normalMatrix);		// convert model matrix to normal matrix
		
		shader.enable();
		
		// coordinate frames
		shader.setUniform("u_mvpMatrix", mvpMatrix);
		shader.setUniform("u_modelMatrix", modelMatrix);
		shader.setUniform("u_normalMatrix", normalMatrix);

		// geometry
		shader.setAttribute("a_position", vertexBuffer);
		shader.setAttribute("a_normal", normalBuffer);


		// light
		shader.setUniform("u_ambientIntensity", ambientIntensity);
		shader.setUniform("u_lightIntensity", lightIntensity);
		shader.setUniform("u_lightDirection", lightDirection);

		// material
		shader.setUniform("u_shininess", shininess);
		shader.setAttribute("a_uv", uvBuffer);
		
		glActiveTexture(GL_TEXTURE0);				// we are loading into texture slot 0
		glBindTexture(GL_TEXTURE_2D, diffuseTextureID);	// load the texture into this slot
		shader.setUniform("u_diffuseTexture", 0);			// tell GLSL to use this slot

		glActiveTexture(GL_TEXTURE1);				// we are loading into texture slot 0
		glBindTexture(GL_TEXTURE_2D, specularTextureID);	// load the texture into this slot
		shader.setUniform("u_specularTexture", 1);			// tell GLSL to use this slot

		
		// camera
		Scene.theScene.getCamera().getModelMatrix(cameraMatrix);
//			cameraMatrix.getColumn(2, camera);	// k axis - orthorgaphic
//			shader.setUniform("u_camera", cameraDirection);			
		cameraMatrix.getColumn(3, camera);	// origin - perspecitce
		shader.setUniform("u_cameraPosition", camera);			
					
		// draw
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glDrawArrays(GL_TRIANGLES, 0, vertices.length);
	}

	private static final float ROTATION_SPEED = TAU / 10;
	
	public void update(float deltaTime, InputManager input) {
		float angle = deltaTime * ROTATION_SPEED;
//		getMatrix().rotateY(angle);
	}
}
