package net.runelite.client.plugins.theatre;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;

public class TimerOverlay extends Overlay{

        private final Client client;
        private final TheatrePlugin plugin;
        private final TheatreConfig config;
        private PanelComponent panelComponent = new PanelComponent();


        @Inject
        private TimerOverlay(Client client, TheatrePlugin plugin, TheatreConfig config) {
            this.client = client;
            this.plugin = plugin;
            this.config = config;
            setPriority(OverlayPriority.HIGH);
            setLayer(OverlayLayer.ABOVE_SCENE);
        }


    @Override
    public Dimension render(Graphics2D graphics) {
            panelComponent.getChildren().clear();


            panelComponent.setPreferredSize(new Dimension(
                    graphics.getFontMetrics().stringWidth("Tick:   ") + 30,0
            ));

            panelComponent.getChildren().add(LineComponent.builder()
            .left("Tick: ")
            .right(Integer.toString(plugin.getTickCount()))
            .build());

            if(config.showTickTimer() && plugin.isInRaid()){
                    return panelComponent.render(graphics);

            } else {
                return null;
            }


    }
}
