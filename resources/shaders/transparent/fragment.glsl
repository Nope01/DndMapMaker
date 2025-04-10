#version 330 core

in vec2 texCoords;
out vec4 FragColor;

uniform vec3 color;
uniform int selected;
uniform int inLine;

uniform sampler2D textureSampler;

void main() {
    vec4 textureColor = texture(textureSampler, texCoords);
    vec3 selectedColour = vec3(173.0f/255.0f, 62.0f/255.0f, 62.0f/255.0f);
    vec3 lineColour = vec3(0.0f, 1.0f, 0.3f);

    FragColor = vec4(color, 1.0f) + textureColor;
    FragColor = vec4(FragColor.xyz, 0.5f);

    if (selected > 0) {
        FragColor = vec4(selectedColour, 1.0f);
    }

    if (inLine > 0) {
        FragColor = vec4(lineColour, 1.0f);
    }
}