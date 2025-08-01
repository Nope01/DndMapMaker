#version 330 core

in vec2 texCoords;
in vec2 iconTexCoords;
out vec4 FragColor;

uniform vec3 color;
uniform int hovered;
uniform int inLine;
uniform int selected;
uniform int highlighted;
uniform int isVisible;
uniform int movementHighlighted;
uniform int spellHighlighted;

uniform sampler2D terrainTexture;
uniform sampler2D iconTexture;

void main() {
    vec4 terrainTextureColour = texture(terrainTexture, texCoords);
    vec4 iconTextureColour = texture(iconTexture, iconTexCoords);
    vec3 selectedColour = vec3(173.0/255.0, 62.0/255.0, 62.0/255.0);
    vec3 fogColour = vec3(0.1f, 0.1f, 0.15f);
    vec3 lineColour = vec3(0.0, 1.0, 0.3);

    if (iconTextureColour.a > 0.001f) {
        FragColor = vec4(iconTextureColour);
        FragColor.rgb = FragColor.rgb - 0.2f;
    }
    else {
        FragColor = vec4(terrainTextureColour);
    }

    if (inLine > 0) {
        FragColor = vec4(lineColour, 1.0);
    }

    if (selected > 0) {
        FragColor = FragColor + 0.2f;
    }


    if (isVisible > 0) {
        FragColor.a = 1.0f;
    }
    else {
        FragColor = vec4(fogColour, 1.0f);
    }

    if (movementHighlighted > 0) {
        FragColor.b = FragColor.b + 0.2f;
    }

    if (spellHighlighted > 0) {
        FragColor.r = FragColor.r + 0.2f;
    }

    if (highlighted > 0) {
        FragColor.g = FragColor.g + 0.2f;
    }

    if (hovered > 0) {
        FragColor = vec4(selectedColour, 1.0);
    }
}