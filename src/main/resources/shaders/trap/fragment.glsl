#version 330 core

in vec2 texCoords;
out vec4 FragColor;

uniform int selected;
uniform int isHidden;

uniform sampler2D iconTexture;

void main() {
    vec4 iconTextureColour = texture(iconTexture, texCoords);
    vec3 selectedColour = vec3(173.0/255.0, 62.0/255.0, 62.0/255.0);

    FragColor = vec4(iconTextureColour);

    if (selected > 0) {
        FragColor = vec4(selectedColour, 1.0);
    }

    if (isHidden > 0) {
        FragColor.r = 1.0f;
    }
}