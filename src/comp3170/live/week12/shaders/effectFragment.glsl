
#version 410

uniform sampler2D u_texture;
uniform ivec2 u_screenSize;

uniform float u_focusDepth;	// between 0 = near, 1 = far

in vec2 v_texcoord;	// UV 

layout(location = 0) out vec4 o_colour;

const vec3 GAMMA = vec3(2.2);

void main() {
	
	vec2 dUV = 1. / u_screenSize;
	
	vec3 c = vec3(0);
	float total = 0;
	
	float fragDepth = texture(u_texture, v_texcoord).a;  // between 0 = near, 1 = far
	
	float diff = abs(u_focusDepth - fragDepth);
	
	float sigma = 20. * diff + 0.1;
	
	for (int i = -10; i <= 10; i++) {
		for (int j = -10; j <= 10; j++) {
			vec2 offset = vec2(i,j);
			
			float distance = length(offset);
			float weight = exp(-0.5 * (distance * distance) / (sigma * sigma));
						
			c = c + weight * texture(u_texture, v_texcoord + dUV * offset).rgb;
			total = total + weight;
		}		
	}
	
	c = c / total;

    o_colour = vec4(c, 1);
}

