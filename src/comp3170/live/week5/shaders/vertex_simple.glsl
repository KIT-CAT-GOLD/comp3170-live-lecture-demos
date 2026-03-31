#version 410

in vec4 a_position; // vertex position - (X,Y,Z,W)
uniform mat4 u_mvpMatrix; // model Matrix

void main() {
	gl_Position = u_mvpMatrix * a_position;
}