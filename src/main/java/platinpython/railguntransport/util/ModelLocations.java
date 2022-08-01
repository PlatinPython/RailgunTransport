package platinpython.railguntransport.util;

import net.minecraft.resources.ResourceLocation;
import platinpython.railguntransport.RailgunTransport;

public class ModelLocations {
    public static final ResourceLocation BASE = new ResourceLocation(RailgunTransport.MOD_ID, "block/base");

    public static class Railgun {
        public static final ResourceLocation MOUNT = new ResourceLocation(RailgunTransport.MOD_ID,
                                                                          "block/railgun/gun_mount"
        );
        public static final ResourceLocation BODY = new ResourceLocation(RailgunTransport.MOD_ID,
                                                                         "block/railgun/gun_body"
        );
        public static final ResourceLocation HOLD = new ResourceLocation(RailgunTransport.MOD_ID,
                                                                         "block/railgun/gun_hold"
        );
    }

    public static class Target {
        public static final ResourceLocation MOUNT = new ResourceLocation(RailgunTransport.MOD_ID,
                                                                          "block/target/target_mount"
        );
        public static final ResourceLocation SHAFT_FRONT = new ResourceLocation(RailgunTransport.MOD_ID,
                                                                                "block/target/hydraulic_shaft_front"
        );
        public static final ResourceLocation SHAFT_MIDDLE = new ResourceLocation(RailgunTransport.MOD_ID,
                                                                                 "block/target/hydraulic_shaft_middle"
        );
        public static final ResourceLocation SHAFT_BACK = new ResourceLocation(RailgunTransport.MOD_ID,
                                                                               "block/target/hydraulic_shaft_back"
        );
        public static final ResourceLocation CLAW_UP = new ResourceLocation(RailgunTransport.MOD_ID,
                                                                            "block/target/claw_upper"
        );
        public static final ResourceLocation CLAW_DOWN = new ResourceLocation(RailgunTransport.MOD_ID,
                                                                              "block/target/claw_lower"
        );
        public static final ResourceLocation CLAW_LEFT = new ResourceLocation(RailgunTransport.MOD_ID,
                                                                              "block/target/claw_left"
        );
        public static final ResourceLocation CLAW_RIGHT = new ResourceLocation(RailgunTransport.MOD_ID,
                                                                               "block/target/claw_right"
        );
    }
}
