#version 330 core

in vec2 texCoords;
out vec4 FragColor;

uniform int hovered;
uniform int isHidden;

uniform sampler2D iconTexture;

void main() {
    vec4 iconTextureColour = texture(iconTexture, texCoords);

    FragColor = vec4(iconTextureColour);

    if (isHidden > 0) {
        FragColor.a = 0.0f;
    }

    if (hovered > 0) {
        FragColor = iconTextureColour + 0.2f;
    }
}