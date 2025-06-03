package org.lwjgl;

import imgui.ImGui;
import org.lwjgl.combatMap.CombatHexagon;
import org.lwjgl.data.CombatFileManager;
import org.lwjgl.data.ImageGeneration;
import org.lwjgl.data.MapSaveLoad;
import org.lwjgl.continentMap.ContinentHexagon;
import org.lwjgl.input.InputHandler;
import org.lwjgl.input.ObjectSelection;
import org.lwjgl.objects.Grid;
import org.lwjgl.objects.SceneObject;
import org.lwjgl.objects.Trap;
import org.lwjgl.objects.entities.Creature;
import org.lwjgl.objects.entities.Player;
import org.lwjgl.shaders.ShaderProgramCache;
import org.lwjgl.textures.TextureCache;

import java.util.ArrayList;
import java.util.List;

public class Scene extends SceneObject {
    private Camera camera;
    private List<SceneObject> rootObjects;
    private List<SceneObject> allObjects;
    private Grid grid;
    private int screenWidth;
    private int screenHeight;
    private long window;
    private InputHandler inputHandler;
    private SceneObject hoveredObject;
    private SceneObject selectedObject;
    private TextureCache textureCache;
    private ShaderProgramCache shaderCache;
    private MapSaveLoad mapSaveLoad;
    private CombatFileManager combatFileManager;

    public Scene(int width, int height, InputHandler inputHandler, ShaderProgramCache shaderCache, long window) {
        //CopyOnWriteArrayList
        rootObjects = new ArrayList<>();
        allObjects = new ArrayList<>();
        this.screenWidth = width;
        this.screenHeight = height;
        this.window = window;
        this.inputHandler = inputHandler;
        this.hoveredObject = null;
        this.textureCache = new TextureCache();
        this.shaderCache = shaderCache;

        this.mapSaveLoad = new MapSaveLoad();
        this.combatFileManager = new CombatFileManager();

        setupScene(width, height);
    }

    private void setupScene(int width, int height) {
        camera = new Camera(width, height);
        camera.setPosition(60f, 50.0f, 40f);
        //camera.setPosition(0, 10f, 0);
        camera.setRotation(1.5f, 0.0f);
        camera.resize(width, height);
    }

    public void initContinentScene() {
        this.grid = new Grid(this, 80, 40);
        grid.makeContinentGrid(this);
        addObject(grid);
    }

    public void initCityScene() {
        this.grid = new Grid(this, 80, 40);
        grid.makeCityGrid(this);
        addObject(grid);

        Trap trap = new Trap(2, grid.getHexagonAt(20, 40).getOffsetPos());
        trap.setShaderProgram(this.getShaderCache().getShader("trap"));
        trap.setId("trap");
        trap.setParent(grid.getHexagonAt(20, 40));
        trap.setPosition(0.0f, 0.1f, 0.0f);
        trap.setTexture(this.getTextureCache().getTexture("sandvich"));
        trap.setIsHidden(false);

        Player player = new Player(grid.getHexagonAt(20, 45).getOffsetPos());
        player.setId("player");
        player.setShaderProgram(this.getShaderCache().getShader("creature"));
        player.setParent(grid.getHexagonAt(20, 45));
        player.setTexture(this.getTextureCache().getTexture("soda"));
        player.setPosition(0.0f, 0.02f, 0.0f);
    }

    public void initCombatScene() {
        this.grid = new Grid(this, 80, 40);
        grid.makeCombatGrid(this);
        addObject(grid);
    }

    public void addObject(SceneObject object) {
        rootObjects.add(object);
        allObjects.add(object);
    }

    public void removeObject(SceneObject removeObject) {
        for (SceneObject rootObject : rootObjects) {
            if (rootObject == removeObject) {
                rootObjects.remove(removeObject);
            }
            else {
                removeObjectChild(rootObject, removeObject);
            }
        }


//        for (SceneObject root : rootObjects) {
//            if (root instanceof ContinentHexagon) {
//                root.cleanup();
//            }
//            // Recursively clean up children if needed
//            cleanupChildren(root);
//        }
    }

    private void removeObjectChild(SceneObject object, SceneObject removeObject) {
        for (SceneObject childObject : object.children) {
            if (childObject == removeObject) {
                System.out.println("Removing " + childObject.getId());
                object.children.remove(childObject);
            }
            else {
                removeObjectChild(childObject, removeObject);
            }
        }
    }

    public SceneObject getObject(String id) {
        for (SceneObject object : rootObjects) {
            if (object.getId().equals(id)) {
                return object;
            }
        }
        return null;
    }

    public void removeAllObjects() {
        rootObjects.clear();
    }

    public void update(float deltaTime) {
        for (SceneObject root : rootObjects) {
            root.update();
            root.update(this, deltaTime, inputHandler);
        }
        if (hoveredObject != null) {
            ObjectSelection.resetHoveredObject(hoveredObject);
            hoveredObject = null;
        }
        if (grid != null) {
            grid.clearHoveredHexagons();
        }

        //Only get a new hovered object when not hovering over UI
        if (!ImGui.getIO().getWantCaptureMouse()) {
            ObjectSelection.hoverObject(this, inputHandler, rootObjects);
        }

//        if (hoveredObject != null) {
//            hoveredObject.update(this, deltaTime, inputHandler);
//        }
    }

    public void render() {
        for (SceneObject root : rootObjects) {
            root.render();
        }
    }

    @Override
    public void update(Scene scene, float deltaTime, InputHandler inputHandler) {

    }

    public void cleanup() {
        for (SceneObject root : rootObjects) {
            if (root instanceof ContinentHexagon) {
                root.cleanup();
            }
            // Recursively clean up children if needed
            cleanupChildren(root);
        }
    }

    private void cleanupChildren(SceneObject object) {
        for (SceneObject child : object.children) {
            child.cleanup();
            cleanupChildren(child);
        }
    }

    public List<SceneObject> getRootObjects() {
        return rootObjects;
    }

    //Recursively get all objects
    public List<SceneObject> getAllObjects() {
        List<SceneObject> objects = new ArrayList<>();

        for (SceneObject object : rootObjects) {
            objects.add(search(object, objects));
        }
        return objects;
    }

    public SceneObject search(SceneObject object, List<SceneObject> objects) {
        if (object.children.isEmpty()) {
            objects.add(object);
            return object;
        }

        for (SceneObject child : object.children) {
            search(child, objects);
        }
        return object;
    }

    public Camera getCamera() {
        return camera;
    }

    public int getScreenWidth() {
        return screenWidth;
    }
    public int getScreenHeight() {
        return screenHeight;
    }

    public SceneObject getHoveredObject() {
        return hoveredObject;
    }
    public void setHoveredObject(SceneObject hoveredObject) {
        this.hoveredObject = hoveredObject;
    }

    public TextureCache getTextureCache() {
        return textureCache;
    }
    public ShaderProgramCache getShaderCache() {
        return shaderCache;
    }
    public Grid getGrid() {
        return grid;
    }

    public void saveContinentMap() {
        mapSaveLoad.saveFile(grid);
    }

    public void saveCombatMap() {
        combatFileManager.saveMapFile(grid);
    }

    public void loadContinentMap() {
        Grid temp = mapSaveLoad.loadFile();
        if (temp != null) {
            grid.setGridFromLoad(temp.getGrid(), temp.rows, temp.columns);
        }
    }

    public boolean loadCombatMap() {
        Grid temp = combatFileManager.loadMapFile();
        if (temp == null) {
            return false;
        }

        grid.makeGridFromLoadedGrid(temp);

        return true;
    }

    public void saveImage() {
        ImageGeneration.saveImageAsFile(window, screenWidth, screenHeight);
    }
}