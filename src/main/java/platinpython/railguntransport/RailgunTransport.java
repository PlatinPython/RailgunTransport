package platinpython.railguntransport;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import platinpython.railguntransport.util.network.NetworkHandler;
import platinpython.railguntransport.util.RegistryHandler;

@Mod(RailgunTransport.MOD_ID)
public class RailgunTransport {
    public static final String MOD_ID = "railguntransport";

    public static final Logger LOGGER = LogUtils.getLogger();

    public RailgunTransport() {
        RegistryHandler.register();

        NetworkHandler.register();
    }
}
