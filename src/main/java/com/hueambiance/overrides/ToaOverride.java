package com.hueambiance.overrides;

import com.hueambiance.AmbianceOverride;
import com.hueambiance.HueAmbianceConfig;
import com.hueambiance.helpers.HueHelper;
import io.github.zeroone3010.yahueapi.Room;
import net.runelite.api.Client;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;

@Singleton
public class ToaOverride implements AmbianceOverride {

    private static final int TOA_TOMB_REGION = 14672;
    private static final int[] VARBIT_MULTILOC_IDS_CHEST = new int[]{
            14356, 14357, 14358, 14359, 14360, 14370, 14371, 14372
    };

    private static final int VARBIT_VALUE_CHEST_KEY = 2;
    private static final int VARBIT_ID_SARCOPHAGUS = 14373;
    private boolean active = false;
    private boolean activatedBefore = false;

    @Inject
    private Client client;

    @Inject
    private HueAmbianceConfig config;

    @Inject
    private HueHelper hueHelper;

    @Override
    public boolean doesOverride(final Room room)
    {
        return active;
    }

    @Override
    public void handleGameTick(final GameTick gameTick, final Room room)
    {
        final LocalPoint lp = client.getLocalPlayer().getLocalLocation();
        final int region = lp == null ? -1 : WorldPoint.fromLocalInstance(client, lp).getRegionID();
        if(region == TOA_TOMB_REGION && !activatedBefore)
        {
            final boolean sarcophagusIsPurple = client.getVarbitValue(VARBIT_ID_SARCOPHAGUS) % 2 != 0;
            if(sarcophagusIsPurple)
            {
                boolean purpleIsMine = true;
                for (final int varbitId : VARBIT_MULTILOC_IDS_CHEST)
                {
                    if (client.getVarbitValue(varbitId) == VARBIT_VALUE_CHEST_KEY)
                    {
                        purpleIsMine = false;
                        break;
                    }
                }

                if (purpleIsMine)
                {
                    active = true;
                    activatedBefore = true;
                    hueHelper.setColorForDuration(room, config.toaColor(), Duration.ofSeconds(15), () -> active = false);
                } else
                {
                    if(config.toaShowOthersPurple())
                    {
                        active = true;
                        activatedBefore = true;
                        hueHelper.setColorForDuration(room, config.toaOthersColor(), Duration.ofSeconds(15), () -> active = false);
                    }
                }
            }
        } else if(region != TOA_TOMB_REGION && activatedBefore)
        {
            activatedBefore = false;
        }
    }
}
