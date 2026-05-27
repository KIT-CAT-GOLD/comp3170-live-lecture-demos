#version 410

layout(location = 0) out vec4 o_colour;	// BRIGHTNESS

uniform vec3 u_ambientIntensity;	// RGB colour of ambient light	- INTENSITY
uniform vec3 u_lightIntensity;		// RGB colour of direct light	- INTENSITY
uniform vec4 u_lightDirection; 		// direction to light source vector WORLD
uniform vec4 u_cameraPosition; 		// position to of camera WORLD

uniform sampler2D u_diffuseTexture;
uniform sampler2D u_specularTexture;
uniform float u_shininess;

uniform float u_near;
uniform float u_far;

in vec4 v_position; 				// WORLD
in vec4 v_viewPosition;				// VIEW
in vec4 v_normal; 					// WORLD
in vec2 v_uv;						// UV

const vec3 GAMMA = vec3(2.2);

void main() {
	vec4 s = normalize(u_lightDirection);
	vec4 n = normalize(v_normal);	
	vec4 r = -reflect(s,n);
	vec4 v = normalize(u_cameraPosition - v_position);

	vec3 diffuseMaterial = texture(u_diffuseTexture, v_uv).rgb;		// BRIGHTNESS
	diffuseMaterial = pow(diffuseMaterial, GAMMA);				// INTENSITY 

	vec3 specularMaterial = texture(u_specularTexture, v_uv).rgb;		// INTENSITY

	vec3 ambient = u_ambientIntensity * diffuseMaterial;
	vec3 diffuse = u_lightIntensity * diffuseMaterial * max(0, dot(s, n));	
	vec3 specular = vec3(0);
	
	if (dot(n,s) > 0) {
		specular = u_lightIntensity * specularMaterial * pow(max(0, dot(r, v)), u_shininess);		
	}
	
	vec3 intensity = ambient + diffuse + specular; 
	vec3 brightness = pow(intensity, 1. / GAMMA);
	
	float depth = (-v_viewPosition.z - u_near) / (u_far - u_near);
	
    o_colour = vec4(brightness, depth);
}

