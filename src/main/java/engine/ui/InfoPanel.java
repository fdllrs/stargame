package engine.ui;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import game.info.PlanetInfo;
import game.info.StarInfo;
import game.objects.GameObject;
import game.objects.Planet;
import game.objects.Star;
import org.joml.Vector4f;

import java.util.ArrayList;

public class InfoPanel extends UIElement {

    private GameObject currentTarget;
    private ArrayList<UIElement> elements = new ArrayList<>();
    private FontAtlas font;

    public InfoPanel(float x, float y, float width, float height, Vector4f color, FontAtlas font) {
        super(x, y, width, height, color);
        this.font = font;

    }

    private void loadElementInfo() {
        elements.clear();

        if (currentTarget instanceof Planet planet) {
            loadPlanetInfo(planet);
        } else if (currentTarget instanceof Star star) {
            loadStarInfo(star);

        }

        calculateElementsSpacing();
    }

    private void loadStarInfo(Star star) {
        String starNameText = "Star Name: " + star.getName();
        StarInfo info = star.getStarInfo();
        String[] starInfoText = {"Star type: " + info.type(), "Star radius: " + info.radius(), "Star mass: " + info.mass()};

        setTitle(starNameText);
        setInfo(starInfoText);
    }

    private void setTitle(String starNameText) {
        elements.add(new UIText(starNameText, this, UIText.Alignment.CENTER, new Vector4f(1, 1, 1, 1), 24, 5, font));
    }

    private void setInfo(String[] info) {
        for (String infoText : info) {
            elements.add(new UIText(infoText, this, UIText.Alignment.LEFT, new Vector4f(1, 1, 1, 1), 20, 15, font));
        }
    }

    private void loadPlanetInfo(Planet planet) {
        String planetNameText = "Planet Name: " + planet.getName();
        PlanetInfo info = planet.getPlanetInfo();
        String[] planetInfoText = {"home Star: " + info.homeStar().getName(), "Planet Radius: " + info.planetRadius(), "Planet Orbit: " + info.orbitDistance(), "Planet Speed: " + info.orbitSpeed()};
        setTitle(planetNameText);
        setInfo(planetInfoText);
    }

    private void calculateElementsSpacing() {
        float spacing = 10;
        float currentY = this.y + this.height;
        for (UIElement element : elements) {
            element.y = currentY;
            currentY -= element.getBoundingHeight() + spacing;
        }

    }

    @Override
    public void render(ShaderProgram shader, Mesh uiQuad) {
        if (currentTarget == null)
            return;
        shader.setUniform("useTexture", 0);
        shader.setUniform("uiColor", this.color);
        shader.setUniform("model", this.modelMatrix);

        uiQuad.render();
        for (UIElement text : this.elements) {
            text.render(shader, uiQuad);
        }
    }

    @Override
    public float getBoundingHeight() {
        return this.height;
    }

    public void setTarget(GameObject objectClicked) {
        this.currentTarget = objectClicked;

        if (objectClicked == null)
            return;

        loadElementInfo();

    }

    @Override
    public void setSize(float newWidth, float newHeight) {
        if (this.width == newWidth && this.height == newHeight)
            return;

        super.setSize(newWidth, newHeight);

        for (UIElement child : elements) {
            if (child instanceof UIText textChild) {
                textChild.setMaxWidth(this.width);
            }
        }

        calculateElementsSpacing();
    }

}
