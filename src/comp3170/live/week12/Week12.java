package comp3170.live.week12;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;

import java.io.File;

import org.joml.Matrix4f;

import comp3170.IWindowListener;
import comp3170.InputManager;
import comp3170.OpenGLException;
import comp3170.ShaderLibrary;
import comp3170.TextureLibrary;
import comp3170.Window;
import comp3170.live.common.cameras.ICamera;

/**
 * Week 10 Demo
 * 
 * 
 */

public class Week12 implements IWindowListener{

	private static final File COMMON_DIR = new File("src/comp3170/live/common/shaders");
	private static final File SHADER_DIR = new File("src/comp3170/live/week12/shaders");
	private static final File TEXTURE_DIR = new File("src/comp3170/live/week12/textures");

	private Window window;
	private int screenWidth = 1000;
	private int screenHeight = 1000;

	private InputManager input;
	private long oldTime;

	private Scene scene;

	private boolean drawWireframes = true;
	
	public static final int OPAQUE_PASS = 0; 
	public static final int WIREFRAME_PASS = 1; 
	public static final int TRANSPARENT_PASS = 2; 
	
	/**
	 * @throws OpenGLException
	 */
	
	public Week12() throws OpenGLException {
		window = new Window("Gem Demo", screenWidth, screenHeight, this);
//		window.setResizable(true);	// make the window resizable
		window.setSamples(4); // use anti-aliasing
		window.run();
	}

	/**
	 * Initialise OpenGL and create the scene
	 */
	@Override
	public void init() {
		
		// Enable required GL features & configure
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glEnable(GL_DEPTH_TEST);	// enable depth testing
		glDepthFunc(GL_LEQUAL);			
//		glEnable(GL_CULL_FACE);		// enable backface culling
		
		// Create ShaderLibrary and TextureLibrary singletons and 
		// configure the paths they will use to find files
		
		new ShaderLibrary(COMMON_DIR).addPath(SHADER_DIR);
		new TextureLibrary(TEXTURE_DIR);
		
		scene = new Scene();

		input = new InputManager(window);		
		oldTime = System.currentTimeMillis();

	}

	/**
	 * Update the world (e.g. moving objects in the scene)
	 * This is called from draw() below at the beginning of every frame 
	 */
	
	private void update() {
		// calculate the time that has passed since the last update
		long time = System.currentTimeMillis();
		float deltaTime = (time - oldTime) / 1000.0f;
		oldTime = time;

		// update the scene
		scene.update(deltaTime, input);
		
		// input needs to be cleared at the end of every frame
		input.clear();
	}

	// To avoid garbage collection, we typically allocate matrices once
	// and re-use them, rather than allocating them on every frame.
	
	private Matrix4f viewMatrix = new Matrix4f();
	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f mvpMatrix = new Matrix4f();
	
	/**
	 * Redraw the scene.
	 * 
	 * This is called repeatedly at a high frame rate.
	 */

	@Override
	public void draw() {
		// update the scene before drawing
		update();
		
		// clear buffers before drawing
		glViewport(0, 0, screenWidth, screenHeight);
		glClear(GL_COLOR_BUFFER_BIT);

		glClearDepth(1f);
		glClear(GL_DEPTH_BUFFER_BIT);

		ICamera camera = scene.getCamera();
		camera.getViewMatrix(viewMatrix);
		camera.getProjectionMatrix(projectionMatrix);
		mvpMatrix.set(projectionMatrix).mul(viewMatrix);

		// Pass 0: Draw opaque objects
		
		scene.draw(mvpMatrix, OPAQUE_PASS);

		// Pass 1: Draw wireframes
		
		if (drawWireframes) {
			scene.draw(mvpMatrix, WIREFRAME_PASS);
		}

	}

	/**
	 * The window has been resized.
	 * This is always called between init() and the first call to draw()
	 */
	
	@Override
	public void resize(int width, int height) {
		screenWidth = width;
		screenHeight = height;
	}

	@Override
	public void close() {
		// We generally don't do anything here
	}

	public static void main(String[] args) throws OpenGLException {
		// just call the constructor, it will take care of the rest
		new Week12();
	}
}
