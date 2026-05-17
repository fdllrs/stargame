package engine.ui;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import game.objects.GameObject;
import game.objects.Planet;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

import java.util.Vector;

public class InfoPanel extends UIElement{

    private GameObject currentTarget;
    private String title;
    private String location;

    public InfoPanel(float x, float y, float width, float height, Vector4f color) {
        super(x, y, width, height, color);
    }

    @Override
    public void render(ShaderProgram shader, Mesh uiQuad) {
        if(currentTarget == null) return;
        shader.setUniform("useTexture", 0);
        shader.setUniform("uiColor", this.color);
        shader.setUniform("model", this.modelMatrix);
        uiQuad.render();

        UIText title = makeTitle();

        UIText location = new UIText(
                this.location,
                x,
                y,
                10,
                12,
                new Vector4f(1,1,1,1));

        title.render(shader, uiQuad);
        location.render(shader, uiQuad);
    }

    @NotNull
    private UIText makeTitle() {
        return new UIText(
                this.title,
                (width - 20* this.title.length()) /2  ,
                height - 30 ,
                20,
                30,
                new Vector4f(1,1,1,1));
    }

    public void setTarget(Planet planetClicked) {
        this.currentTarget = planetClicked;

        if (planetClicked == null) return;

        title = planetClicked.getClass().getSimpleName();
        location = planetClicked.getPosition().toString();
    }
}
