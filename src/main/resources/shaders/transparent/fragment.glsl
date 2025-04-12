#version 330 core

in vec2 texCoords;
out vec4 FragColor;

uniform vec3 color;
uniform int selected;
uniform int inLine;

uniform sampler2D textureSampler;

void main() {
    vec4 textureColor = texture(textureSampler, texCoords);
    vec3 selectedColour = vec3(173.0/255.0, 62.0/255.0, 62.0/255.0);
    vec3 lineColour = vec3(0.0, 1.0, 0.3);

    FragColor = vec4(color, 1.0) + textureColor;
    FragColor = vec4(FragColor.xyz, 0.5);

    if (selected > 0) {
        FragColor = vec4(selectedColour, 1.0);
    }

    if (inLine > 0) {
        FragColor = vec4(lineColour, 1.0);
    }
}