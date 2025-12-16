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
        if (client.getLocalPlayer() == null)
        {
            return false;
        }

        WorldPoint wp = client.getLocalPlayer().getWorldLocation();
        int region = wp.getRegionID();

        return region == TOA_TOMB_REGION;
    }

    @Override
    public void handleGameTick(final GameTick gameTick, final Room room)
    {
        if (!doesOverride(room))
        {
            activatedBefore = false;
            return;
        }

        if(!activatedBefore)
        {
            final int sarcValue = client.getVarbitValue(VARBIT_ID_SARCOPHAGUS);
            final boolean sarcophagusIsPurple = sarcValue % 2 != 0;

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

                activatedBefore = true;
                if (purpleIsMine)
                {
                    hueHelper.setColor(room, config.toaColor());
                } else if(config.toaShowOthersPurple())
                {
                    hueHelper.setColor(room, config.toaColor());
                }
            }
        }
    }
}
