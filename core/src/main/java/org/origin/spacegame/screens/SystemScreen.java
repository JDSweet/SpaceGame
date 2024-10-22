package org.origin.spacegame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.origin.spacegame.SpaceGame;
import org.origin.spacegame.entities.Planet;
import org.origin.spacegame.entities.StarSystem;
import org.origin.spacegame.game.GameInstance;
import org.origin.spacegame.input.InputUtilities;
import org.origin.spacegame.utilities.CameraManager;

public class SystemScreen implements Screen, InputProcessor
{
    private SpaceGame game;
    boolean enterPositionSet = false;

    //This is used for the System GUI
    //A button to take you back to the galaxy view
    //is at the bottom center of the screen.
    private Stage stage;
    private TextButton goToGalaxyScreenButton;

    public SystemScreen(SpaceGame game)
    {
        this.game = game;
        stage = new Stage();
        InputUtilities.getInputMultiplexer().addProcessor(stage);
        goToGalaxyScreenButton = new TextButton("Galaxy Screen", GameInstance.getInstance().getGuiSkin());
        goToGalaxyScreenButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getCameraManager().setRenderView(CameraManager.RenderView.GALACTIC_VIEW);
                Gdx.app.log("SystemScreenDebug", "Galaxy Button Clicked!");
            }
        });



        goToGalaxyScreenButton.setX(Gdx.graphics.getWidth()/2f);
        //goToGalaxyScreenButton.setY(Gdx.graphics.getHeight()/2f);
        stage.addActor(goToGalaxyScreenButton);
        //goToGalaxyScreenButton.
    }

    /**
     *
     */
    @Override
    public void show()
    {
        Gdx.app.log("SystemScreenDebug", "Showing System View...");
        //InputUtilities.getInputMultiplexer().addProcessor(this);
    }

    /**
     * @param delta The time in seconds since the last render.
     */
    String debugTxt = "";
    @Override
    public void render(float delta)
    {
        Gdx.app.log("SystemScreen", "Render");
        StarSystem selectedSystem = GameInstance.getInstance().getSelectedStarSystem();
        InputUtilities.detectCameraMovement(game.getCameraManager());
        if(!enterPositionSet)
        {
            game.getCameraManager().getCurrentCamera().position.x = selectedSystem.getCenter().x;
            game.getCameraManager().getCurrentCamera().position.y = selectedSystem.getCenter().y;
            this.enterPositionSet = true;
        }
        onPlanetClicked(null);
        game.getCameraManager().update();
        game.getBatch().setProjectionMatrix(game.getCameraManager().getCurrentCamera().combined);
        GameInstance.getInstance().getState().renderSystemView(game.getBatch(), GameInstance.getInstance().getSelectedStarSystem());
        debugTxt += ("SystemScreen Render");
        stage.draw();
        stage.act();
    }

    private void onPlanetClicked(Planet planet)
    {
        debugTxt += ("\nSystemScreen: Planet " + planet.id + " clicked.");
    }

    /**
     * @param width
     * @param height
     */
    @Override
    public void resize(int width, int height)
    {

    }

    /**
     *
     */
    @Override
    public void pause()
    {

    }

    /**
     *
     */
    @Override
    public void resume()
    {

    }

    /**
     *
     */
    @Override
    public void hide()
    {
        Gdx.app.log("Debug ", debugTxt);
    }

    /**
     *
     */
    @Override
    public void dispose()
    {

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        Gdx.app.log("SystemScreen", "Click detected!");
        for(Planet planet : GameInstance.getInstance().getSelectedStarSystem().getPlanets())
        {
            Vector3 touchPos = game.getCameraManager().getCurrentCamera().unproject(new Vector3((float)screenX,
                (float)screenY, 0f));
            float tx = touchPos.x;
            float ty = touchPos.y;
            float x = planet.getPosition().x;
            float y = planet.getPosition().y;

            if(planet.isTouched(tx, ty))
                onPlanetClicked(planet);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
