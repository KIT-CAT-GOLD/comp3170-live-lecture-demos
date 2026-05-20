#version 410

layout(location = 0) out vec4 o_colour;	// BRIGHTNESS

uniform float u_shininess;			// Phong exponent

uniform vec3 u_ambientIntensity;	// RGB colour of ambient light	- INTENSITY
uniform vec3 u_lightIntensity;		// RGB colour of direct light	- INTENSITY
uniform vec4 u_lightDirection; 		// direction to light source vector WORLD

uniform sampler2D u_diffuseTexture;

in vec4 v_position; 				// WORLD
in vec4 v_normal; 					// WORLD
in vec2 v_uv;						// UV

const vec3 GAMMA = vec3(2.2);

void main() {
	vec4 s = normalize(u_lightDirection);
	vec4 n = normalize(v_normal);

	vec3 diffuseMaterial = texture(u_diffuseTexture, v_uv).rgb;		// BRIGHTNESS
	diffuseMaterial = pow(diffuseMaterial, GAMMA);				// INTENSITY 

	vec3 ambient = u_ambientIntensity * diffuseMaterial;
	vec3 diffuse = u_lightIntensity * diffuseMaterial * max(0, dot(s, n));
	
	vec3 intensity = ambient + diffuse; 
	vec3 brightness = pow(intensity, 1. / GAMMA);
	
    o_colour = vec4(brightness,1);
}

