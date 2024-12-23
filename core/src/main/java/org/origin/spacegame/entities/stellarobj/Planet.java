package org.origin.spacegame.entities.stellarobj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.origin.spacegame.data.PlanetClass;
import org.origin.spacegame.entities.galaxy.StarSystem;
import org.origin.spacegame.game.GameInstance;
import org.origin.spacegame.generation.OrbitalZone;
import org.origin.spacegame.map.hex.HexCell;
import org.origin.spacegame.map.hex.HexMap;
import org.origin.spacegame.utilities.RandomNumberUtility;

public class Planet
{
    public int id;
    private Vector2 position;
    private float orbitalRadius;
    private PlanetClass planetClass;
    private Rectangle rect;

    private int size = 0;
    private float habitability = 0f;
    private OrbitalZone orbitalZone; //This is mostly for debug purposes.
    private StarSystem starSystem;

    private HexMap map;

    /*
    *
    * To Do: In order to add zones (habitable zone, freezing zone, melting zone), we need to be able
    * to get a list of each planet class that can spawn in each zone, and then randomly pick a planet from said
    * list of planet classes.
    *
    * */
    public Planet(int id, Vector2 position, float orbitalRadius, StarSystem system)
    {
        this(id, position, orbitalRadius, GameInstance.getInstance().getPlanetClass(null), system);
    }

    public Planet(int id, Vector2 position, float orbitalRadius, String planetClassTag, StarSystem system)
    {
        this(id, position, orbitalRadius, GameInstance.getInstance().getPlanetClass(planetClassTag), system);
    }

    public Planet(int id, Vector2 position, float orbitalRadius, PlanetClass planetClass, StarSystem system)
    {
        this.id = id;
        this.position = position;
        this.orbitalRadius = orbitalRadius;
        this.planetClass = planetClass;
        this.orbitalZone = OrbitalZone.ANY;
        /*
        *
        * if(orbitalRadius <= Constants.MELTINGZONE)
        *   planetClass = GameInstance.getInstance().getPlanetClass("pc_molten");
        *
        * */
        if(planetClass.getMaxHabitability() <= planetClass.getMinHabitability())
            this.habitability = planetClass.getMinHabitability();
        else
            this.habitability = RandomNumberUtility.nextFloat(planetClass.getMinHabitability(),
                planetClass.getMaxHabitability());
        if(planetClass.getMaxSize() <= planetClass.getMinSize())
            this.size = planetClass.getMinSize();
        else
            this.size = RandomNumberUtility.nextInt(planetClass.getMinSize(),
                planetClass.getMaxSize());
        this.rect = new Rectangle(position.x, position.y, size, size);

        int cellMapWidth = getSize() * 4;
        int cellMapHeight = getSize() * 2;

        this.map = new HexMap(cellMapWidth, cellMapHeight);
    }

    public void renderPlanetToSystemMap(SpriteBatch batch)
    {
        /*batch.draw(planetClass.getGfx(), position.x, position.y,
            Constants.StarSystemConstants.PLANET_RENDER_SIZE,
            Constants.StarSystemConstants.PLANET_RENDER_SIZE);*/
        batch.draw(planetClass.getGfx(), position.x, position.y,
            size,
            size);
    }

    //Draws the surface of this planet with the passed spritebatch.
    public void renderPlanetToSurfaceMap(SpriteBatch batch)
    {
        map.draw(batch);
        Gdx.app.log("Planet Debug", "Rendering planet map...");
    }

    public float distanceToPlanet(Planet other)
    {
        return position.dst(other.position);
    }

    public StarSystem getSystem()
    {
        return this.starSystem;
    }

    public void update()
    {

    }

    public boolean isHabitable()
    {
        return this.habitability > 0;
    }

    public boolean isTouched(float x, float y)
    {
        if(rect.contains(x, y))
            return true;
        else
            return false;
    }

    public void setPlanetClass(String tag, boolean resize, boolean regenerateHabitability)
    {
        this.planetClass = GameInstance.getInstance().getPlanetClass(tag);
        if(regenerateHabitability)
            this.habitability = RandomNumberUtility.nextFloat(planetClass.getMinHabitability(), planetClass.getMaxHabitability());
        if(resize)
            this.size = RandomNumberUtility.nextInt(planetClass.getMinSize(), planetClass.getMaxSize());
        this.rect = new Rectangle(position.x, position.y, size, size);
    }

    public void setOrbitalZone(OrbitalZone zone)
    {
        this.orbitalZone = zone;
    }

    public OrbitalZone getOrbitalZone()
    {
        return this.orbitalZone;
    }

    public int getHabitabilityRounded()
    {
        return Math.round(getHabitability()*100);
    }

    public int getSize()
    {
        return size;
    }

    public Vector2 getPosition()
    {
        return position;
    }

    public float getOrbitalRadius()
    {
        return orbitalRadius;
    }

    public PlanetClass getPlanetClass()
    {
        return planetClass;
    }

    public float getHabitability()
    {
        return this.habitability;
    }

    public int getID()
    {
        return this.id;
    }
}
