package org.origin.spacegame.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.XmlReader;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.origin.spacegame.game.GameInstance;

//This button is defined in XML and receives a reference to the GUI script engine for callback referencing.
//It gets skins from the GameInstance's list of skins.
/*
*
* The ScriptedTextButton has several attributes in XML:
*   text - The default string of text that is going to be displayed.
*   on_click - The function that is going to be called by this button when it is pressed.
*   skin - The GUI skin this button will use.
*   x - The optional X coordinate of this button on the screen (as a fraction of the screen width).
*       This is overridden by any container this element is in that controls/manipulates its position.
*   y - The optional y coordinate of this button on the screen (as a fraction of the screen's width).
*       This is overridden by any container this element is in that controls/manipulates its position.
*   width - See above.
*   height - See above.
*   padding_up - If this widget is part of a layer or group, then this controls the up padding.
*   padding_down - If this widget is part of a layer or group, then this controls the down padding.
*   padding_left - If this widget is part of a layer or group, then this controls the left padding.
*   padding_right - If this widget is part of a layer or group, then this controls the right padding.
* */
public class ScriptedTextButton extends TextButton implements ScriptableGUIComponent
{
    //private String callbackFunctionName;
    private LuaValue callbackFunction;
    private String debugTag;

    public ScriptedTextButton(XmlReader.Element self, LuaValue ctxt)
    {
        super(self.getAttribute("text"), GameInstance.getInstance().getSkin(self.getAttribute("skin")));
        this.debugTag = getClass().getTypeName() + " Debug";

        if(ctxt == null)
            Gdx.app.log(debugTag, "The Lua Context is null.");

        if(self.hasAttribute("on_click") && ctxt != null)
            this.callbackFunction = ctxt.get(self.getAttribute("on_click"));
        else
            Gdx.app.log(debugTag, "Either the Lua context has not been set, or on_click has not been defined.");
        if(self.hasAttribute("x"))
            setX(Gdx.graphics.getWidth() * Float.parseFloat(self.getAttribute("x")));
        if(self.hasAttribute("X"))
            setX(Gdx.graphics.getWidth() * Float.parseFloat(self.getAttribute("X")));
        if(self.hasAttribute("y"))
            setY(Gdx.graphics.getHeight() * Float.parseFloat(self.getAttribute("y")));
        if(self.hasAttribute("Y"))
            setY(Gdx.graphics.getHeight() * Float.parseFloat(self.getAttribute("y")));
        if(self.hasAttribute("width"))
            setWidth(Gdx.graphics.getWidth() * Float.parseFloat(self.getAttribute("width")));
        if(self.hasAttribute("height"))
            setHeight(Gdx.graphics.getHeight() * Float.parseFloat(self.getAttribute("height")));
        if(self.hasAttribute("padding_up"))
            padTop(Gdx.graphics.getHeight() * Float.parseFloat(self.getAttribute("padding_up")));
        if(self.hasAttribute("padding_down"))
            padBottom(Gdx.graphics.getHeight() * Float.parseFloat(self.getAttribute("padding_down")));
        if(self.hasAttribute("padding_left"))
            padLeft(Gdx.graphics.getWidth() * Float.parseFloat(self.getAttribute("padding_left")));
        if(self.hasAttribute("padding_right"))
            padRight(Gdx.graphics.getWidth() * Float.parseFloat(self.getAttribute("padding_right")));

        addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                //The widget, this GameInstance, and the GameState are passed to the callback.
                if(ctxt != null && callbackFunction != null)
                {
                    callbackFunction.invoke(
                        CoerceJavaToLua.coerce(this),
                        CoerceJavaToLua.coerce(GameInstance.getInstance()),
                        CoerceJavaToLua.coerce(GameInstance.getInstance().getState())
                    );
                }
            }
        });
    }

    @Override
    public float getPaddingUp()
    {
        return 0;
    }

    @Override
    public float getPaddingDown()
    {
        return 0;
    }

    @Override
    public float getPaddingLeft()
    {
        return 0;
    }

    @Override
    public float getPaddingRight()
    {
        return 0;
    }

    @Override
    public void setPaddingUp(float padding)
    {

    }

    @Override
    public void setPaddingDown(float padding)
    {

    }

    @Override
    public void setPaddingLeft(float padding)
    {

    }

    @Override
    public void setPaddingRight(float padding)
    {

    }
}