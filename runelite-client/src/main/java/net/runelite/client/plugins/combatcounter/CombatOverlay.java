/*
 * Copyright (c) 2018, Seth <http://github.com/sethtroll>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.combatcounter;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

class CombatOverlay extends Overlay {

    private final Client client;

    private final CombatCounter plugin;

    private final PanelComponent panelComponent = new PanelComponent();

    private final CombatCounterConfig config;


    private String longestName = "";

    private HashMap<String, Long> ticks = new HashMap<>();

    @Inject
    public CombatOverlay(Client client, CombatCounter plugin, CombatCounterConfig config) {
        super(plugin);

        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setPriority(OverlayPriority.HIGH);

        this.config = config;
        this.client = client;
        this.plugin = plugin;

        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Combat Counter Overlay"));
    }



    @Override
    public Dimension render(Graphics2D graphics) {
        if(config.showTickCounter()){
            panelComponent.getChildren().clear();

            Player local = client.getLocalPlayer();
            if(local == null || local.getName() == null)
                return null;


                panelComponent.getChildren().add(TitleComponent.builder().text("Tick Counter").color(Color.WHITE).build());



            if(plugin.getCounter().isEmpty()) {
                panelComponent.getChildren().add(LineComponent.builder().left(local.getName()).right("0").build());

                //Used to decide width of counter
                longestName = client.getLocalPlayer().getName();
            } else {
                Map<String, Long> map = this.plugin.getCounter();
                if(map == null)
                    return null;

                for(String name : map.keySet()) {
                    if(name.length() > longestName.length()) { longestName = name; }
                    panelComponent.getChildren().add(1, LineComponent.builder().left(name).right(Long.toString(map.get(name))).build());
                }

                if(!map.containsKey(local.getName())) {
                    if(client.getLocalPlayer().getName().length() > longestName.length()) { longestName = client.getLocalPlayer().getName(); }
                    panelComponent.getChildren().add(LineComponent.builder().left(local.getName()).right("0").build());
                }
            }

            boolean size = graphics.getFontMetrics().stringWidth(longestName + " 77777.5") + 10 > ComponentConstants.STANDARD_WIDTH;
            panelComponent.setPreferredSize(size ? new Dimension(graphics.getFontMetrics().stringWidth(longestName + " 77777") + 25, 0) : new Dimension(ComponentConstants.STANDARD_WIDTH, 0));

            return panelComponent.render(graphics);
        } else {
            return null;
        }
    }
}
