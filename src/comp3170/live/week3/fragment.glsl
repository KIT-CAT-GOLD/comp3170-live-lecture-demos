#version 410

uniform vec3 u_colour;			// colour as a 3D vector (r,g,b)
uniform float u_phase;			// how far around the clock face am I?

layout(location = 0) out vec4 o_colour;	// output to colour buffer

void main() {
	int segment = int(u_phase*7.0);
	if(segment > gl_PrimitiveID) {
		o_colour = vec4(u_colour, 1);
	} else {
		o_colour = vec4(0, 0, 0, 1);
	}
}
