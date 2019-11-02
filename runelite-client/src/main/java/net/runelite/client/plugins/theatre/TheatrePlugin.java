/*
 * THIS SOFTWARE WRITTEN BY A KEYBOARD-WIELDING MONKEY BOI
 * No rights reserved. Use, redistribute, and modify at your own discretion,
 * and in accordance with Yagex and RuneLite guidelines.
 * However, aforementioned monkey would prefer if you don't sell this plugin for profit.
 * Good luck on your raids!
 */

package net.runelite.client.plugins.theatre;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.party.messages.TilePing;
import net.runelite.client.plugins.theatre.rooms.*;
//import net.runelite.client.plugins.theatre.rooms.SotetsegHandler;
import net.runelite.client.plugins.theatre.rooms.xarpus.XarpusHandler;
import net.runelite.client.plugins.theatre.rooms.nylocas.NyloHandler;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ws.PartyService;
import net.runelite.client.ws.WSClient;

import javax.inject.Inject;
import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.util.ArrayList;

@PluginDescriptor(
        name = "[S] Theatre",
        description = "All-in-one plugin for Theatre of Blood.",
        tags = {"theatre, raids"},
        enabledByDefault = false
)

public class TheatrePlugin extends Plugin {

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private TheatreRoom room;

    @Getter(AccessLevel.PUBLIC)
    private MaidenHandler maidenHandler;

    @Getter(AccessLevel.PUBLIC)
    private BloatHandler bloatHandler;

    @Getter(AccessLevel.PUBLIC)
    private NyloHandler nyloHandler;

    @Getter(AccessLevel.PUBLIC)
    private Sotetseg sotetseg;

    //@Getter(AccessLevel.PUBLIC)
    //private SotetsegHandler sotetsegHandler;

    @Getter(AccessLevel.PUBLIC)
    private XarpusHandler xarpusHandler;

    @Getter(AccessLevel.PUBLIC)
    private VerzikHandler verzikHandler;

    @Inject
    private Client client;

    @Getter(AccessLevel.PUBLIC)
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private TheatreOverlay overlay;

    @Inject
    private TimerOverlay timerOverlay;

    @Inject
    private TheatreConfig config;

    @Getter(AccessLevel.PACKAGE)
    private int tickCount;

    @Getter(AccessLevel.PACKAGE)
    private boolean inRaid;

    @Inject
    private PartyService party;
    @Inject
    private WSClient wsClient;

    @Inject
    private ShiftWalkerInputListener inputListener;
    @Inject
    private KeyManager keyManager;

    private static Clip clip;
    @Getter
    private boolean hotKeyPressed;
    private boolean soundPlayed;

    @Provides
    TheatreConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(TheatreConfig.class);
    }

    @Override
    protected void startUp() {
        room = TheatreRoom.UNKNOWN;


        maidenHandler = new MaidenHandler(client, this, config);
        bloatHandler = new BloatHandler(client, this, config);
        nyloHandler = new NyloHandler(client, this, config);
        sotetseg = new Sotetseg(client, this, config, party, wsClient);
        //sotetsegHandler = new SotetsegHandler(client, this, config);
        xarpusHandler = new XarpusHandler(client, this, config);
        verzikHandler = new VerzikHandler(client, this, config);
        tickCount = 1;
        soundPlayed = false;
        this.keyManager.registerKeyListener(this.inputListener);
        overlayManager.add(overlay);
        overlayManager.add(timerOverlay);
    }

    @Override
    protected void shutDown() {


        maidenHandler.onStop();
        maidenHandler = null;

        bloatHandler.onStop();
        bloatHandler = null;

        nyloHandler.startTime = 0L;
        nyloHandler.onStop();
        nyloHandler = null;

        sotetseg.onStop();
        sotetseg = null;
        //sotetsegHandler.onStop();
        //sotetsegHandler = null;

        xarpusHandler.onStop();
        xarpusHandler = null;

        verzikHandler.onStop();
        verzikHandler = null;

        room = TheatreRoom.UNKNOWN;
        tickCount = 1;
        inRaid = false;
        soundPlayed = false;
        this.keyManager.unregisterKeyListener(this.inputListener);
        overlayManager.remove(overlay);
        overlayManager.remove(timerOverlay);
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        if (maidenHandler != null)
            maidenHandler.onNpcSpawned(event);

        if (bloatHandler != null)
            bloatHandler.onNpcSpawned(event);

        if (nyloHandler != null)
            nyloHandler.onNpcSpawned(event);

        if (sotetseg != null) {
            sotetseg.onNpcSpawned(event);
        }
        //if (sotetsegHandler != null)
        //sotetsegHandler.onNpcSpawned(event);

        if (xarpusHandler != null)
            xarpusHandler.onNpcSpawned(event);

        if (verzikHandler != null)
            verzikHandler.onNpcSpawned(event);

    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        if (maidenHandler != null)
            maidenHandler.onNpcDespawned(event);

        if (bloatHandler != null)
            bloatHandler.onNpcDespawned(event);

        if (nyloHandler != null)
            nyloHandler.onNpcDespawned(event);

        if (sotetseg != null) {
            sotetseg.onNpcDespawned(event);
        }
        //if (sotetsegHandler != null)
        // sotetsegHandler.onNpcDespawned(event);

        if (xarpusHandler != null)
            xarpusHandler.onNpcDespawned(event);

    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {

        if (verzikHandler != null)
            verzikHandler.onAnimationChanged(event);
    }


    @Subscribe
    public void onGameTick(GameTick event) {
        tickCount++;

        if (maidenHandler != null)
            maidenHandler.onGameTick();

        if (bloatHandler != null)
            bloatHandler.onGameTick();

        if (nyloHandler != null)
            nyloHandler.onGameTick();

        if (sotetseg != null) {
            sotetseg.onGameTick(event);
        }
        //if (sotetsegHandler != null)
        //sotetsegHandler.onGameTick();

        if (xarpusHandler != null)
            xarpusHandler.onGameTick();

        if (verzikHandler != null)
            verzikHandler.onGameTick();
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event) {

        if (sotetseg != null) {
            sotetseg.onGroundObjectSpawned(event);
        }
        //if (sotetsegHandler != null)
        //sotetsegHandler.onGroundObjectSpawned(event);

        if (xarpusHandler != null)
            xarpusHandler.onGroundObjectSpawned(event);
    }


    @Subscribe
    public void onFocusChanged(final FocusChanged event) {
        if (!event.isFocused()) {
            this.hotKeyPressed = false;
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (nyloHandler != null)
            nyloHandler.onConfigChanged();
        if (sotetseg != null) {
            sotetseg.onConfigChanged(event);
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (client.getVar(Varbits.THEATRE_OF_BLOOD) == 2) {
            inRaid = true;
        } else {
            inRaid = false;
        }
        if (bloatHandler != null)
            bloatHandler.onVarbitChanged(event);

        if (xarpusHandler != null)
            xarpusHandler.onVarbitChanged(event);
    }

    @Subscribe
    public void onProjectileMoved(ProjectileMoved event) {
        if (sotetseg != null) {
            sotetseg.onProjectileMoved(event);
        }
        //if(sotetsegHandler != null){
        //sotetsegHandler.onProjectileMoved(event);
        //}
        if (verzikHandler != null) {
            verzikHandler.onProjectileMoved(event);
        }
    }

    @Subscribe
    public void onClientTick(ClientTick event) {
        if (sotetseg != null) {
            sotetseg.onClientTick(event);
        }
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event){
        if(xarpusHandler != null){
            xarpusHandler.onHitsplatApplied(event);
        }
    }
    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event){
        if(verzikHandler != null){
            verzikHandler.onMenuEntryAdded(event);
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (event.getMenuTarget().toLowerCase().contains("formidable passage") || event.getMenuTarget().toLowerCase().contains("theatre")) {
            if (event.getMenuOption().toLowerCase().contains("quick-enter") || event.getMenuOption().toLowerCase().contains("enter")) {
                tickCount = 1;
            }
        } else if (event.getMenuTarget().toLowerCase().contains("treasure room") && event.getMenuOption().toLowerCase().contains("enter")){
            if(config.dudash()){
                loadSound();
                try {
                    if (clip != null && !soundPlayed) {
                        clip.setFramePosition(0);
                        clip.start();
                        soundPlayed = true;
                    }
                } catch (Exception e){
                    soundPlayed = false;
                    //swallow
                }
            }
        }
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event) {
        if (bloatHandler != null) {
            bloatHandler.onGraphicsObjectCreated(event);
        }
    }
    @Subscribe
    private void onTilePing(TilePing ping){
        if (sotetseg != null){
            sotetseg.onTilePing(ping);
        }
    }

    public void loadSound(){
        try {
            AudioInputStream stream;
            AudioFormat format;
            DataLine.Info info;

            stream = AudioSystem.getAudioInputStream(new BufferedInputStream(TheatrePlugin.class.getResourceAsStream("dudash.wav")));
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (control != null) {
                control.setValue(20f * (float) Math.log10(config.dudashSoundVolume() / 100.0f));

            }

        } catch (Exception e) {
            e.printStackTrace();
            clip = null;

        }

    }

    @Subscribe
    public void onWidgetHiddenChanged(WidgetHiddenChanged event) {
        //how to hide widgets
        /**
         Widget widget = client.getWidget(WidgetInfo.THEATRE_PARTY_CONTAINER);
         widget.setHidden(true);
         Widget widget2 = client.getWidget(WidgetInfo.THEATRE_PARTY_MEMBER_FIRST);
         widget2.setHidden(true);
         System.out.println(widget.isHidden() + " " + widget.isSelfHidden());
         System.out.println(widget2.isHidden() + " " + widget2.isSelfHidden());
         System.out.println("here");
         **/
    }
    public void setHotKeyPressed(final boolean hotKeyPressed) {
        this.hotKeyPressed = hotKeyPressed;
    }
}
