package comp3170.live.week12;

import static comp3170.Math.TAU;
import static comp3170.Math.cross;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_POINT;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import java.io.IOException;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

import comp3170.GLBuffers;
import comp3170.OpenGLException;
import comp3170.SceneObject;
import comp3170.Shader;
import comp3170.ShaderLibrary;
import comp3170.TextureLibrary;
import comp3170.live.week8.Week8;

public class Torus extends SceneObject {

	final private String VERTEX_SHADER = "lightVertex.glsl";
	final private String FRAGMENT_SHADER = "lightFragmentPerspective.glsl";
	final private String WIREFRAME_VERTEX_SHADER = "simpleVertex.glsl";
	final private String WIREFRAME_FRAGMENT_SHADER = "simpleFragment.glsl";

	final private String DIFFUSE_TEXTURE = "128_oak fine wood texture-seamless.jpg";
	final private String SPECULAR_TEXTURE = "brick_wall2-spec-1024.tga";

	private static final Vector2i NSIDES = new Vector2i(20, 80);
	private static final Vector2f RADIUS = new Vector2f(1, 4);
	private static final Vector2f MAX_UV = new Vector2f(1, 4);

	private Vector4f[] crossSectionVertices; 
	private Vector4f[] crossSectionNormals; 

	private Vector4f[] vertices; 
	private int vertexBuffer;
	private Vector4f[] normals; 
	private int normalBuffer;
	private Vector2f[] uvs; 
	private int uvBuffer;
	private int[] indices;
	private int indexBuffer;

	private int diffuseTextureID;
	private int specularTextureID;

	private Vector4f wireColour = new Vector4f(1,1,1,1);
	
	private Shader litShader;
	private Shader wireframeShader;
	
	public Torus() {
		loadShaders();
		createBuffers();
		loadTextures();
	}

	private void loadShaders() {
		litShader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);
		wireframeShader = ShaderLibrary.instance.compileShader(WIREFRAME_VERTEX_SHADER, WIREFRAME_FRAGMENT_SHADER);
		litShader.setStrict(false);
		wireframeShader.setStrict(false);
	}

	private void createBuffers() {
		createCrossSection();
		createAttributeBuffers();				
		createIndexBuffer();
	}

	private void createCrossSection() {
		crossSectionVertices = new Vector4f[NSIDES.x + 1];
		crossSectionNormals = new Vector4f[NSIDES.x + 1];
		
		// cross section is a circle
		
		for (int i = 0; i <= NSIDES.x; i++) {
			float angle = i * TAU / NSIDES.x;
			
			crossSectionVertices[i] = new Vector4f(RADIUS.x,0,0,1).rotateZ(angle);
			crossSectionNormals[i] = new Vector4f(1,0,0,1).rotateZ(angle);			
		}
	}

	private void createAttributeBuffers() {
		int nPoints = (NSIDES.x+1) * (NSIDES.y+1);	// duplicate points at beginning & end in each direction
		vertices = new Vector4f[nPoints];	
		normals = new Vector4f[nPoints];	
		uvs = new Vector2f[nPoints];	

		int k = 0;
		Matrix4f m = new Matrix4f();
		Matrix4f n = new Matrix4f();
		
		Vector4f origin = new Vector4f();
		Vector4f iVec = new Vector4f();
		Vector4f jVec = new Vector4f();
		Vector4f kVec = new Vector4f();
		Vector4f up = new Vector4f(0,1,0,0);
		
		for (int i = 0; i <= NSIDES.y; i++) {
			float angle = i * TAU / NSIDES.y;
			origin.set(RADIUS.y, 0, 0, 1).rotateY(angle);
			
			kVec.set(0,0,1,0).rotateY(angle);
			cross(up, kVec, iVec);
			cross(kVec, iVec, jVec);
			
			iVec.normalize();
			jVec.normalize();
			kVec.normalize();
			
			m.setColumn(0, iVec);
			m.setColumn(1, jVec);
			m.setColumn(2, kVec);
			m.setColumn(3, origin);

			m.normal(n);
			
			for (int j = 0; j <= NSIDES.x; j++) {
				vertices[k] = crossSectionVertices[j].mul(m, new Vector4f());
				normals[k] = crossSectionNormals[j].mul(n, new Vector4f());
				uvs[k] = new Vector2f(MAX_UV.x * j / NSIDES.x, MAX_UV.y * i / NSIDES.y);
				
				k++;
			}			
		}
				
		vertexBuffer = GLBuffers.createBuffer(vertices);		
		normalBuffer = GLBuffers.createBuffer(normals);		
		uvBuffer = GLBuffers.createBuffer(uvs);		
	}
	
	private void createIndexBuffer() {

		//   2---5---8
		//   |  /|  /|
		//   | / | / |
		//   |/  |/  |
		//   1---4---7 ... 
		//   |  /|  /|     
		//   | / | / |     
		//   |/  |/  |     
		// j 0---3---6 ... 
		//    i
		
		int nTriangles = NSIDES.x * NSIDES.y * 2; // 2 triangles per quad
		indices = new int[nTriangles * 3];	// 3 vertices per triangle  
		
		int n = NSIDES.x + 1;
		int k = 0;		
		for (int i = 0; i < NSIDES.y; i++) {
			for (int j = 0; j < NSIDES.x; j++) {
				
				indices[k++] = i * n + j;
				indices[k++] = i * n + j + 1;
				indices[k++] = (i+1) * n + j + 1;

				indices[k++] = (i+1) * n + j + 1;
				indices[k++] = (i+1) * n + j;
				indices[k++] = i * n + j;
			}
		}
		
		indexBuffer = GLBuffers.createIndexBuffer(indices);
	}
	
	private void loadTextures() {
		diffuseTextureID = loadTexture(DIFFUSE_TEXTURE);
		specularTextureID = loadTexture(SPECULAR_TEXTURE);
	}
	
	private int loadTexture(String textureFile) {
		int textureID = -1;
		try {
			textureID = TextureLibrary.instance.loadTexture(textureFile);
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
		
		return textureID;
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
//			drawSelfWireframe(mvpMatrix);
			break;
		}
	}

	private void drawSelfWireframe(Matrix4f mvpMatrix) {
		wireframeShader.enable();
		wireframeShader.setUniform("u_mvpMatrix", mvpMatrix);
		wireframeShader.setAttribute("a_position", vertexBuffer);
		wireframeShader.setUniform("u_colour", wireColour);
		
		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
	}

	private void drawSelfOpaque(Matrix4f mvpMatrix) {
		// Opaque pass: draw the faces

		getModelToWorldMatrix(modelMatrix);
		modelMatrix.normal(normalMatrix);		// convert model matrix to normal matrix
		
		litShader.enable();
		
		// coordinate frames
		litShader.setUniform("u_mvpMatrix", mvpMatrix);
		litShader.setUniform("u_modelMatrix", modelMatrix);
		litShader.setUniform("u_normalMatrix", normalMatrix);

		// geometry
		litShader.setAttribute("a_position", vertexBuffer);
		litShader.setAttribute("a_normal", normalBuffer);


		// light
		litShader.setUniform("u_ambientIntensity", ambientIntensity);
		litShader.setUniform("u_lightIntensity", lightIntensity);
		litShader.setUniform("u_lightDirection", lightDirection);

		// material
		litShader.setUniform("u_shininess", shininess);
		litShader.setAttribute("a_uv", uvBuffer);
		
		glActiveTexture(GL_TEXTURE0);				// we are loading into texture slot 0
		glBindTexture(GL_TEXTURE_2D, diffuseTextureID);	// load the texture into this slot
		litShader.setUniform("u_diffuseTexture", 0);			// tell GLSL to use this slot

		glActiveTexture(GL_TEXTURE1);				// we are loading into texture slot 0
		glBindTexture(GL_TEXTURE_2D, specularTextureID);	// load the texture into this slot
		litShader.setUniform("u_specularTexture", 1);			// tell GLSL to use this slot

		
		// camera
		Scene.theScene.getCamera().getModelMatrix(cameraMatrix);
//			cameraMatrix.getColumn(2, camera);	// k axis - orthorgaphic
//			shader.setUniform("u_camera", cameraDirection);			
		cameraMatrix.getColumn(3, camera);	// origin - perspecitce
		litShader.setUniform("u_cameraPosition", camera);			
					
		// draw
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
	}

	
}
