#version 410

in vec4 a_position;		// vertex in 3D homogenous coordinates (MODEL)
in vec4 a_normal;		// normal vector in 3D homogenous coordinates (MODEL)
in vec2 a_uv;			// UV coordinates

uniform mat4 u_mvpMatrix;	// MODEL -> NDC
uniform mat4 u_modelMatrix;	// MODEL -> WORLD 
uniform mat4 u_normalMatrix;	// MODEL -> WORLD (for normals)

out vec4 v_position; 	// WORLD
out vec4 v_normal;		// WORLD
out vec2 v_uv;			// UV

void main() {
	v_position = u_modelMatrix * a_position;
	v_normal = u_normalMatrix * a_normal;
	v_uv = a_uv;
	
    gl_Position = u_mvpMatrix * a_position;
}

