#version 330 core

in vec2 texCoords;
out vec4 FragColor;

uniform int hovered;
uniform int selected;
uniform int isHidden;

uniform sampler2D iconTexture;

void main() {
    vec4 iconTextureColour = texture(iconTexture, texCoords);


    if (iconTextureColour.a > 0.001f) {
        if (isHidden > 0) {
            FragColor = vec4(iconTextureColour);
            FragColor.a = 0.2f;
        }
        else {
            FragColor = vec4(iconTextureColour);
        }

    }
    else {
        FragColor = vec4(0.0f, 0.0f, 0.0f, 0.0f);
    }

    if (hovered > 0) {
        FragColor = iconTextureColour + 0.2f;
    }

    if (selected > 0) {
        FragColor = iconTextureColour + 0.2f;
    }

}