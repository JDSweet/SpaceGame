package org.origin.spacegame.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader.Element;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.origin.spacegame.game.GameInstance;

public class ScriptableWidgetContainer  implements ScriptableGUIComponent
{
    protected LuaValue onShowCallbackFunc;
    protected LuaValue onHideCallbackFunc;
    protected LuaValue onCreateCallbackFunc;
    protected LuaValue onClickCallbackFunc;

    public Actor widget;

    protected ScriptedGUIScene scene;
    protected String debugTag;
    protected String debugID;

    protected Array<ScriptableGUIComponent> children;

    public ScriptableWidgetContainer(Element self, LuaValue ctxt, Actor widget, ScriptedGUIScene scene)
    {
        this.debugID = self.getAttribute("id");
        this.debugTag = getClass().getSimpleName();
        this.widget = widget;
        this.scene = scene;
        this.children = new Array<ScriptableGUIComponent>();
        if(this.scene != null)
            scene.registerWidgetByID(debugID, this);
        else
            Gdx.app.log(debugTag, "Scene is null!");


        if(self.hasAttribute("visible"))
        {
            String attribVal = self.getAttribute("visible");
            if(attribVal.equalsIgnoreCase("yes"))
                attribVal = "true";
            else if(attribVal.equalsIgnoreCase("no"))
                attribVal = "false";
            boolean visibility = Boolean.parseBoolean(attribVal);
            widget.setVisible(visibility);
        }
        //scene.registerWidgetByID(debugID, this);

        if(ctxt == null)
            Gdx.app.log(debugTag, "The Lua Context is null.");
        if(self.hasAttribute("on_create") && ctxt != null)
            this.onCreateCallbackFunc = ctxt.get(self.getAttribute("on_create"));
        else
            Gdx.app.log(debugTag, "Either the Lua context has not been set, or on_click has not been defined.");
        if(self.hasAttribute("on_show") && ctxt != null)
            this.onShowCallbackFunc = ctxt.get(self.getAttribute("on_show"));
        else
            Gdx.app.log(debugTag, "Either the Lua context has not been set, or on_show has not been defined.");
        if(self.hasAttribute("on_hide") && ctxt != null)
            this.onHideCallbackFunc = ctxt.get(self.getAttribute("on_hide"));
        if(self.hasAttribute("on_click") && ctxt != null)
            this.onClickCallbackFunc = ctxt.get(self.getAttribute("on_click"));
        else
            Gdx.app.log(debugTag, "Either the Lua context has not been set, or on_hide has not been defined.");

        if(self.hasAttribute("x"))
            widget.setX(Gdx.graphics.getWidth() * Float.parseFloat(self.getAttribute("x")));
        if(self.hasAttribute("X"))
            widget.setX(Gdx.graphics.getWidth() * Float.parseFloat(self.getAttribute("X")));
        if(self.hasAttribute("y"))
            widget.setY(Gdx.graphics.getHeight() * Float.parseFloat(self.getAttribute("y")));
        if(self.hasAttribute("Y"))
            widget.setY(Gdx.graphics.getHeight() * Float.parseFloat(self.getAttribute("y")));
        if(self.hasAttribute("width"))
            widget.setWidth(Gdx.graphics.getWidth() * Float.parseFloat(self.getAttribute("width")));
        if(self.hasAttribute("height"))
            widget.setHeight(Gdx.graphics.getHeight() * Float.parseFloat(self.getAttribute("height")));
    }

    public Actor getWidget()
    {
        return widget;
    }

    /**
     *
     */
    @Override
    public void show()
    {
        if(this.onShowCallbackFunc != null)
            onShowCallbackFunc.invoke(CoerceJavaToLua.coerce(widget),
                CoerceJavaToLua.coerce(GameInstance.getInstance()),
                CoerceJavaToLua.coerce(GameInstance.getInstance().getState()));
        else
            Gdx.app.log(debugTag, "No on_show callback function defined for " + debugTag);
        enable();
    }

    /**
     *
     */
    @Override
    public void hide()
    {
        if(this.onHideCallbackFunc != null)
            onHideCallbackFunc.invoke(CoerceJavaToLua.coerce(widget),
                CoerceJavaToLua.coerce(GameInstance.getInstance()),
                CoerceJavaToLua.coerce(GameInstance.getInstance().getState()));
        else
            Gdx.app.log(debugTag, "No on_hide callback function defined for " + debugTag);
        disable();
    }

    @Override
    public void readChild(Element child, ScriptedGUIScene scene, LuaValue ctxt)
    {

    }

    @Override
    public String getDebugID() {
        return debugID;
    }

    private boolean enabled = true;
    @Override
    public void enable() {
        enabled = true;
    }

    @Override
    public void disable() {
        enabled = false;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
