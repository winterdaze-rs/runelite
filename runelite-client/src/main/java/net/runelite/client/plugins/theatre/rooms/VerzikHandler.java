package net.runelite.client.plugins.theatre.rooms;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.theatre.*;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerzikHandler extends RoomHandler {

    @Getter(AccessLevel.PUBLIC)
    private final List<Projectile> rangeProjectiles = new ArrayList<>();

    @Getter(AccessLevel.PUBLIC)
    private int versikCounter = 0;
    private int attacksLeft = 0;

    @Getter(AccessLevel.PUBLIC)
    private NPC npc;

    private int lastId = -1;

    private int autosSinceYellows;
    private int yellows;

    private boolean tornados;

    private int attackTick = -1;

    private long startTime = 0;


    //My variables
    int redCrabsTimer;
    boolean foundTornado;
    boolean timerFlag = false;

    private final Map<Projectile, WorldPoint> verzikRangeProjectiles = new HashMap<>();
    private MenuEntry[] entries;

    @Inject
    public VerzikHandler(Client client, TheatrePlugin plugin, TheatreConfig config) {
        super(client, plugin, config);
    }

    @Override
    public void onStart() {
        if (this.plugin.getRoom() == TheatreRoom.VERSIK)
            return;

        this.reset();
        entries = client.getMenuEntries();
        this.plugin.setRoom(TheatreRoom.VERSIK);
        System.out.println("Starting Verzik Room");
    }

    @Override
    public void onStop() {
        this.reset();
        this.timerFlag = false;
        this.plugin.setRoom(TheatreRoom.UNKNOWN);
        System.out.println("Stopping Verzik Room");
    }

    public void reset() {
        this.foundTornado = false;
        this.redCrabsTimer = 13;
        this.rangeProjectiles.clear();
        this.versikCounter = 19;
        this.attacksLeft = 0;
        this.npc = null;
        this.yellows = 0;
        this.autosSinceYellows = 0;
        this.lastId = -1;
        this.tornados = false;
        this.startTime = 0;
    }

    public void render(Graphics2D graphics) {
        if (npc == null) {
            return;
        }

        int id = npc.getId();

        if (config.showVerzikP1Timer()) {
            if (id == TheatreConstant.VERZIK_ID_P1) {
                if (this.versikCounter >= 0) {
                    String str = Integer.toString(versikCounter);

                    LocalPoint lp = npc.getLocalLocation();
                    Point point = Perspective.getCanvasTextLocation(client, graphics, lp, str, 0);

                    renderTextLocation(graphics, str, 15, Font.BOLD, Color.WHITE, point);
                }

            }
        }

        if (config.showVerzikAttacks()) {

            if (npc.getAnimation() == 8117) {
                if (this.redCrabsTimer > 0) {
                    String str = Integer.toString(redCrabsTimer);

                    LocalPoint lp = npc.getLocalLocation();
                    Point point = Perspective.getCanvasTextLocation(client, graphics, lp, str, 60);
                    renderTextLocation(graphics, str, 15, Font.BOLD, Color.WHITE, point);
                }


            } else if (id == TheatreConstant.VERZIK_ID_P3) {

                Model model = npc.getModel();
//                String str = versikCounter + "";// + " | " + model.getModelHeight();// + " | " + model.getRadius();
                if (versikCounter > 0 && versikCounter < 8) {
                    String str = Math.max(versikCounter, 0) + "";// + " | " + model.getModelHeight();// + " | " + model.getRadius();

                    LocalPoint lp = npc.getLocalLocation();
                    Point point = Perspective.getCanvasTextLocation(client, graphics, lp, str, 0);

                    renderTextLocation(graphics, str, 15, Font.BOLD, Color.WHITE, point);
                }
            }
        }

        if (config.VerzikTankTile()) {
            if (id == TheatreConstant.VERZIK_ID_P3) {
                WorldPoint wp = new WorldPoint(npc.getWorldLocation().getX() + 3, npc.getWorldLocation().getY() + 3, client.getPlane());
                drawTile2(graphics, wp, new Color(75, 0, 130), 2, 255, 0);
                //renderNpcOverlay(graphics, boss, new Color(75, 0, 130), 1, 255, 0);
            }

        }

        if (config.showVerzikYellows()) {
            if (this.yellows > 0) {
                String text = Integer.toString(this.yellows);

                for (GraphicsObject object : client.getGraphicsObjects()) {
                    if (object.getId() == TheatreConstant.GRAPHIC_ID_YELLOWS) {
                        drawTile(graphics, WorldPoint.fromLocal(client, object.getLocation()), Color.YELLOW, 3, 255, 0);
                        LocalPoint lp = object.getLocation();
                        Point point = Perspective.getCanvasTextLocation(client, graphics, lp, text, 0);
                        renderTextLocation(graphics, text, 12, Font.BOLD, Color.WHITE, point);
                    }
                }
            }
        }

        if (config.showCrabTargets()) {
            Player local = client.getLocalPlayer();
            if (local != null && local.getName() != null) {
                for (NPC npc : client.getNpcs()) {
                    if (npc.getName() == null)
                        continue;

                    Pattern p = Pattern.compile("Nylocas (Hagios|Toxobolos|Ischyros)");
                    Matcher m = p.matcher(npc.getName());
                    if (!m.matches())
                        continue;

                    Actor target = npc.getInteracting();
                    if (target == null || target.getName() == null)
                        continue;

                    LocalPoint lp = npc.getLocalLocation();
                    Color color = local.getName().equals(target.getName()) ? Color.RED : Color.GREEN;

                    Point point = Perspective.getCanvasTextLocation(client, graphics, lp, target.getName(), 0);
                    renderTextLocation(graphics, target.getName(), 14, Font.BOLD, color, point);
                }
            }
        }


        if (config.showVerzikRangeAttack()) {
            for (WorldPoint p : verzikRangeProjectiles.values()) {

                LocalPoint point = LocalPoint.fromWorld(client, p);
                Polygon poly = Perspective.getCanvasTilePoly(client, point);
                graphics.setColor(new Color(255, 0, 0, 255));
                graphics.drawPolygon(poly);
                graphics.setColor(new Color(255, 0, 0, 50));
                graphics.fillPolygon(poly);
            }
        }

        /**
         if(config.showVerzikTickEat()) {
         for (Projectile projectile : client.getProjectiles()) {
         int p_id = projectile.getId();

         String name = null;
         Color color = null;

         if (p_id == 1598) {
         double millis = projectile.getRemainingCycles();
         double ticks = millis / 60; // 10 millis per cycle, 0.6 ticks per second, 10/0.6 = 60
         double round = Math.round(ticks * 10d) / 10d;
         name = "Bomb: " + round;
         color = Color.PINK;
         }

         if (name != null) {
         int x = (int) projectile.getX();
         int y = (int) projectile.getY();

         LocalPoint point = new LocalPoint(x, y);
         Point loc = Perspective.getCanvasTextLocation(client, graphics, point, name, 0);

         if (loc != null) {
         OverlayUtil.renderTextLocation(graphics, loc, name, color);
         }
         }
         }
         }**/
    }

    public void onNpcSpawned(NpcSpawned event) {
        NPC npc = event.getNpc();
        int id = npc.getId();

        if (npc.getName() != null && npc.getName().equals("Verzik Vitur")) {
            this.npc = npc;
            if (id == TheatreConstant.VERZIK_ID_P3_BAT) {
                this.onStop();
            } else {
                this.onStart();

                if (id == TheatreConstant.VERZIK_ID_P1) {
                    this.versikCounter = 19;
                } else if (id == TheatreConstant.VERZIK_ID_P2) {
                    this.versikCounter = 3;
                } else if (id == TheatreConstant.VERZIK_ID_P3) {
                    this.versikCounter = -1;
                    this.attacksLeft = 9;
                }
            }
        }
    }

    public void onAnimationChanged(AnimationChanged event) {
        if (plugin.getRoom() != TheatreRoom.VERSIK) {
            return;
        }

        Actor actor = event.getActor();
        if (!(actor instanceof NPC))
            return;

        NPC npc = (NPC) actor;
        int id = npc.getId();

        if (event.getActor().getAnimation() == 8117) {
            redCrabsTimer = 11;
        }

        if (id == TheatreConstant.VERZIK_ID_P1) {
            int animation = npc.getAnimation();
            if (animation == TheatreConstant.ANIMATION_ID_P1_ATTACK) {
//                System.out.println("Verzik is shooting her attack on P1.");
                versikCounter = 15;
            }
        } else if (id == TheatreConstant.VERZIK_ID_P2) {
            int animation = npc.getAnimation();
            if (animation == TheatreConstant.ANIMATION_ID_P2_ATTACK_RANGE || animation == TheatreConstant.ANIMATION_ID_P2_ATTACK_MELEE) {
//                System.out.println("Verzik is shooting her attack on P2.");
                versikCounter = 5;
            } else if (animation == TheatreConstant.ANIMATION_ID_P2_SHIELD) {
//                System.out.println("Verzik is healing on P2.");
                versikCounter = 13;
            }
        }
    }


    public void onProjectileMoved(ProjectileMoved event) {
        int id = event.getProjectile().getId();
        if (id == TheatreConstant.PROJECTILE_ID_P2RANGE) {
            WorldPoint p = WorldPoint.fromLocal(client, event.getPosition());
            verzikRangeProjectiles.put(event.getProjectile(), p);

        }
    }

    public void onGameTick() {
        if (plugin.getRoom() != TheatreRoom.VERSIK) {
            return;
        }

        if (!verzikRangeProjectiles.isEmpty()) {
            for (Iterator<Projectile> it = verzikRangeProjectiles.keySet().iterator(); it.hasNext(); ) {
                Projectile projectile = it.next();
                if (projectile.getRemainingCycles() < 1) {
                    it.remove();
                }
            }
        }

        if (this.yellows == 0) {
            //if (this.autosSinceYellows > 0) {
            for (GraphicsObject object : client.getGraphicsObjects()) {
                if (object.getId() == TheatreConstant.GRAPHIC_ID_YELLOWS) {
                    this.yellows = 14;
//                        this.versikCounter = 22;
                    this.autosSinceYellows = 0;
                    System.out.println("Yellows have spawned.");
                    break;
                }
            }
            // }
        } else {
            this.yellows--;
        }

        if (npc != null) {
            if (npc.getAnimation() == 8117) {
                redCrabsTimer = redCrabsTimer - 1;
            }
        }


        boolean foundVerzik = false;
        foundTornado = false;

        for (NPC npc : client.getNpcs()) {
            if (npc.getName() != null && npc.getName().equals("Verzik Vitur")) {
                foundVerzik = true;
                this.npc = npc;
            } else if (npc.getId() == TheatreConstant.NPC_ID_TORNADO) {
                foundTornado = true;
            }

            if (foundTornado && foundVerzik)
                break;
        }

        if (!foundVerzik) {
            this.onStop();
            return;
        }

        if (npc == null)
            return;

        int id = npc.getId();

        if (this.lastId != id) {
            this.lastId = id;

            if (id == TheatreConstant.VERZIK_ID_P1) {
                this.startTime = System.currentTimeMillis();
            } else if (id == TheatreConstant.VERZIK_ID_P1_WALK && this.startTime != 0) {
                long elapsedTime = System.currentTimeMillis() - this.startTime;
                long seconds = elapsedTime / 1000L;

                long minutes = seconds / 60L;
                seconds = seconds % 60;
                this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Wave 'The Final Challenge - Part 1' completed! Duration: <col=ff0000>" + minutes + ":" + twoDigitString(seconds), null);
            } else if (id == TheatreConstant.VERZIK_ID_P2_TRANSFORM && this.startTime != 0) {
                long elapsedTime = System.currentTimeMillis() - this.startTime;
                long seconds = elapsedTime / 1000L;

                long minutes = seconds / 60L;
                seconds = seconds % 60;

                this.attackTick = this.client.getTickCount() - 4;
                this.versikCounter = -1;
                this.attacksLeft = 9;
                this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Wave 'The Final Challenge - Part 2' completed! Duration: <col=ff0000>" + minutes + ":" + twoDigitString(seconds), null);
            }
        }

        if (id == TheatreConstant.VERZIK_ID_P3_BAT) {
            this.onStop();
            return;
        } else if (id == TheatreConstant.VERZIK_ID_P1_WALK) {
            versikCounter = 4;
            return;
        }

        if (id == TheatreConstant.VERZIK_ID_P1 || id == TheatreConstant.VERZIK_ID_P2) {
            versikCounter--;
            if (versikCounter < 0)
                versikCounter = 0;
            if (npc.getAnimation() == TheatreConstant.ANIMATION_ID_P2_SHIELD) {
                if (!timerFlag) {
                    long elapsedTime = System.currentTimeMillis() - this.startTime;
                    long seconds = elapsedTime / 1000L;

                    long minutes = seconds / 60L;
                    seconds = seconds % 60;
                    this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Wave 'P1 -> Red Crabs' completed! Duration: <col=ff0000>" + minutes + ":" + twoDigitString(seconds), null);


                    timerFlag = true;
                }
            }
        } else if (id == TheatreConstant.VERZIK_ID_P3) {
//            Model model = npc.getModel();
//            if (model.getModelHeight() != 556) {
//                int currentTick = this.client.getTickCount();
//                int lastTick = this.attackTick;
//                int diff = Math.abs(currentTick - lastTick);
//                if (diff > 3) {
//                    this.attackTick = currentTick;
//                }
//            }
//
//            int attackSpeed = 7;
//            if (foundTornado) {
//                attackSpeed = 5;
//            }
//
//            versikCounter = (this.client.getTickCount() - this.attackTick);
//            versikCounter += 1;
//            versikCounter %= attackSpeed;
//            versikCounter = attackSpeed - versikCounter;
//
//            if (versikCounter == 0)
//                versikCounter = attackSpeed;

            if (foundTornado && !this.tornados) {
                this.tornados = true;
            }

            boolean isGreenBall = false;
            for (Projectile projectile : client.getProjectiles()) {

                if (projectile.getId() == TheatreConstant.PROJECTILE_ID_P3_GREEN) {
                    isGreenBall = projectile.getRemainingCycles() > 210;
                    break;
                }
            }

            versikCounter--;

            int animation = npc.getAnimation();

            switch (animation) {
                case TheatreConstant.ANIMATION_ID_P3_MELEE:
                case TheatreConstant.ANIMATION_ID_P3_MAGE:
                    if (versikCounter < 2) {
                        this.attacksLeft--;
                        if (this.tornados) {
                            versikCounter = 5;
                        } else {
                            versikCounter = 7;
                        }

                        if (attacksLeft < 1) {
                            versikCounter = 24;
                        }
                    }
                    break;
                case TheatreConstant.ANIMATION_ID_P3_RANGE:
                    if (versikCounter < 2) {
                        attacksLeft--;
                        if (this.tornados) {
                            versikCounter = 5;
                        } else {
                            versikCounter = 7;
                        }

                        if (attacksLeft < 1) {
                            versikCounter = 30;
                        }

                        if (isGreenBall) {
                            versikCounter = 12;
                        }
                    }
                    break;
                case TheatreConstant.ANIMATION_ID_P3_WEB:
                    attacksLeft = 4;
                    versikCounter = 11;
                    break;
                case TheatreConstant.ANIMATION_ID_P3_YELLOW:
                    attacksLeft = 14;
                    versikCounter = 11;
                    break;
            }
        }
    }


    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (this.client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        final Widget loginScreenOne = this.client.getWidget(WidgetInfo.LOGIN_CLICK_TO_PLAY_SCREEN);
        final Widget loginScreenTwo = this.client.getWidget(WidgetInfo.LOGIN_CLICK_TO_PLAY_SCREEN_MESSAGE_OF_THE_DAY);
        if (loginScreenOne != null || loginScreenTwo != null) {
            return;
        }
        final String option = Text.removeTags(event.getOption()).toLowerCase();
        final String target = Text.removeTags(event.getTarget()).toLowerCase();
        this.entries = this.client.getMenuEntries();
        MenuEntry[] newEntries = this.client.getMenuEntries();
        if (!plugin.isHotKeyPressed() && config.shiftClickPurple()) {
            if (target.contains("nylocas athanatos")) {
                for (int i = this.entries.length - 1; i >= 0; --i) {
                    if(option.contains("examine") || option.contains("attack")){
                        this.entries = ArrayUtils.remove(this.entries, i);
                        --i;
                    }
                }
                this.client.setMenuEntries(this.entries);
            }
        }
    }

    private void delete(final int target) {
        for (int i = this.entries.length - 1; i >= 0; --i) {
            if (this.entries[i].getIdentifier() == target) {
                this.entries = ArrayUtils.remove(this.entries, i);
                --i;
            }
        }
        this.client.setMenuEntries(this.entries);
    }

}
