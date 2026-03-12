package comp3170.live.week3;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;

import java.io.File;
import java.io.IOException;

import org.joml.Vector4f;

import comp3170.IWindowListener;
import comp3170.OpenGLException;
import comp3170.ShaderLibrary;
import comp3170.Window;

public class Week3 implements IWindowListener {

	final private File DIRECTORY = new File("src/comp3170/live/week3");

	private int screenWidth = 1000;
	private int screenHeight = 1000;
	private Vector4f clearColour = new Vector4f(1.0f, 0.86f, 0.35f, 1.0f); // RGBA

	private Scene scene;

	public Week3() throws OpenGLException {
		// create a window with title, size, and a listener (this)
		Window window = new Window("The Clock of Doom", screenWidth, screenHeight, this);
		// start running the window
		window.run();
	}
	
	public static void main(String[] args) throws OpenGLException {
		new Week3();
	}

	@Override
	public void init() {
		new ShaderLibrary(DIRECTORY); // Singleton
		glClearColor(clearColour.x, clearColour.y, clearColour.z, clearColour.w);
		scene = new Scene();
	}

	@Override
	public void draw() {
		// clear the colour buffer
		glClear(GL_COLOR_BUFFER_BIT);		

		// draw the scene
		scene.draw();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
