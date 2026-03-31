#version 410

in vec3 v_colour; // colour from vertex shader as a 3D vector (r,g,b)
uniform vec3 u_baseColour; // colour from java code - base colour for object

layout(location = 0) out vec4 o_colour; // (r,g,b,a)

void main() {
	o_colour = vec4(u_baseColour + v_colour,1);
}  