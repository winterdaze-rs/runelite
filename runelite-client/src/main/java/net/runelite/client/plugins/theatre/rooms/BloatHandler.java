package net.runelite.client.plugins.theatre.rooms;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.theatre.TheatreConfig;
import net.runelite.client.plugins.theatre.TheatrePlugin;
import net.runelite.client.plugins.theatre.TheatreRoom;
import net.runelite.client.plugins.theatre.RoomHandler;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class BloatHandler extends RoomHandler {

    public static enum BloatState {
        DOWN, UP, WARN;
    }

    @Getter(AccessLevel.PUBLIC)
    private NPC bloat;

    private int counter;

    //My variables
    private boolean bloatFlag;
    int bloatTimer;
    private Color color;
    private ConcurrentHashMap<WorldPoint, Integer> bloatHands = new ConcurrentHashMap<>();


    @Getter(AccessLevel.PUBLIC)
    private BloatState bloatState;

    public BloatHandler(Client client, TheatrePlugin plugin, TheatreConfig config) {
        super(client, plugin, config);
    }

    @Override
    public void onStart() {
        if (this.plugin.getRoom() == TheatreRoom.BLOAT)
            return;

        this.reset();
        this.plugin.setRoom(TheatreRoom.BLOAT);
        System.out.println("Starting Bloat Room");
    }

    @Override
    public void onStop() {
        this.reset();
        this.plugin.setRoom(TheatreRoom.UNKNOWN);
        System.out.println("Stopping Bloat Room");
    }

    public void reset() {
        bloat = null;
        bloatFlag = false;
        bloatTimer = 0;
        counter = 0;
        bloatState = BloatState.UP;
    }

    public void render(Graphics2D graphics) {
        if (bloat == null) {
            return;
        }

        if (config.showBloatIndicator()) {
            switch (bloatState) {
                case DOWN:
                    renderNpcOverlay(graphics, bloat, Color.GREEN, 3, 150, 0);
                    break;
                case WARN:
                    renderNpcOverlay(graphics, bloat, Color.YELLOW, 3, 150, 0);
                    break;
                case UP:
                    renderNpcOverlay(graphics, bloat, new Color(223, 109, 255), 3, 150, 0);
                    break;
            }
        }

        if(config.showBloatHands()){
            Iterator<Map.Entry<WorldPoint, Integer>> it = bloatHands.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry<WorldPoint, Integer> entry = it.next();
                if (!config.BloatFeetIndicatorRaveEdition()) {
                    drawTile(graphics, entry.getKey(), new Color(config.bloatColor().getRed(), config.bloatColor().getGreen(), config.bloatColor().getBlue()), 2, config.bloatColor().getAlpha(), 10);
                } else {
                    drawTile(graphics, entry.getKey(), color, 2, 255, 10);
                }
                if(entry != null){
                    //LocalPoint lp = LocalPoint.fromWorld(client, entry.getKey());
                    //String str = Integer.toString(entry.getValue());
                    //Point point = Perspective.getCanvasTextLocation(client, graphics, lp, str, 0); // here

                   // renderTextLocation(graphics, str, 15, Font.BOLD, Color.WHITE, point);
                }

                //Point p = new Point(entry.getKey().getX(), entry.getKey().getY());
                //renderTextLocation(graphics, String.valueOf(entry.getValue()),15, Font.BOLD, Color.WHITE, p);
            }
        }
/**
        if (config.showBloatHands()) {
            for (GraphicsObject object : client.getGraphicsObjects()) {
                int id = object.getId();
                if (id >= 1560 && id <= 1590) {
                    WorldPoint point = WorldPoint.fromLocal(client, object.getLocation());
                    if (!config.BloatFeetIndicatorRaveEdition()) {
                        drawTile(graphics, point, new Color(config.bloatColor().getRed(), config.bloatColor().getGreen(), config.bloatColor().getBlue()), 2, config.bloatColor().getAlpha(), 10);
                    } else {
                        drawTile(graphics, point, color, 2, 255, 10);
                    }

                }
            }
        }**/

        if (config.showBloatTimer()) {
            final String tickCounter = String.valueOf(bloatTimer);
            int secondConversion = (int) (bloatTimer * .6);
            if (bloat != null) {
                Point canvasPoint = bloat.getCanvasTextLocation(graphics, tickCounter, 60);
                if (bloatTimer <= 37) {
                    renderTextLocation(graphics, tickCounter + "( " + secondConversion + " )", 15, Font.BOLD, Color.WHITE, canvasPoint);
                } else {
                    renderTextLocation(graphics, tickCounter + "( " + secondConversion + " )", 15, Font.BOLD, Color.RED, canvasPoint);
                }
            }
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (client.getVar(Varbits.BLOAT_DOOR) == 1) {
            if (!bloatFlag) {
                bloatTimer = 0;
                bloatFlag = true;
            }
        }
    }

    public void onGraphicsObjectCreated(GraphicsObjectCreated event) {

        WorldPoint wp = WorldPoint.fromLocal(client, event.getGraphicsObject().getLocation());
        int id = event.getGraphicsObject().getId();
        if (id >= 1560 && id <= 1590) {
            Iterator<Map.Entry<WorldPoint, Integer>> it = bloatHands.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<WorldPoint, Integer> entry = it.next();
                if(entry != null){
                    if(entry.getKey().getX() == wp.getX() && entry.getKey().getY() == wp.getY()){
                        return;
                    }
                }
            }
                bloatHands.put(wp, 4);
        }


    }

    public void onNpcSpawned(NpcSpawned event) {
        NPC npc = event.getNpc();
        int id = npc.getId();

        if (id == NpcID.PESTILENT_BLOAT) {
            this.onStart();
            bloatTimer = 0;
            bloat = npc;
        }
    }

    public void onNpcDespawned(NpcDespawned event) {
        NPC npc = event.getNpc();
        int id = npc.getId();

        if (id == NpcID.PESTILENT_BLOAT) {
            this.onStop();
            bloatTimer = 0;
            bloat = null;
        }
    }

    public void onGameTick() {
        if (plugin.getRoom() != TheatreRoom.BLOAT) {
            return;
        }

       Iterator<Map.Entry<WorldPoint, Integer>> it = bloatHands.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<WorldPoint, Integer> entry = it.next();
            if(entry.getValue() <= 0){
                bloatHands.remove(entry.getKey(), entry.getValue());
            } else {
                bloatHands.put(entry.getKey(), entry.getValue()-1);
            }
        }

        //Color generating code for bloat feet rave edition
        int R = (int) (Math.random() * 256);
        int G = (int) (Math.random() * 256);
        int B = (int) (Math.random() * 256);
        color = new Color(R, G, B); //random color, but can be bright or dull
        Random random = new Random();
        final float hue = random.nextFloat();
        final float saturation = 0.9f;//1.0 for brilliant, 0.0 for dull
        final float luminance = 1.0f; //1.0 for brighter, 0.0 for black
        color = Color.getHSBColor(hue, saturation, luminance);


        counter++;

        if (bloat.getAnimation() == -1) {
            bloatTimer++;
            counter = 0;
            if (bloat.getHealth() == 0) {
                bloatState = BloatState.DOWN;
            } else {
                bloatState = BloatState.UP;
            }
        } else {
            if (25 < counter && counter < 35) {
                bloatState = BloatState.WARN;
            } else if (counter < 26) {
                bloatTimer = 0;
                bloatState = BloatState.DOWN;
            } else if (bloat.getModelHeight() == 568) {
                bloatTimer = 0;
                bloatState = BloatState.DOWN;
            } else {
                bloatState = BloatState.UP;
            }
        }
    }
}
