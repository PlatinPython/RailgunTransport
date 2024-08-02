package platinpython.railgun_transport.util;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import platinpython.railgun_transport.RailgunTransport;

public class ModelLocations {
    public static final ModelResourceLocation BASE =
        ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(RailgunTransport.MOD_ID, "block/base"));

    public static class Railgun {
        public static final ModelResourceLocation MOUNT = ModelResourceLocation
            .standalone(ResourceLocation.fromNamespaceAndPath(RailgunTransport.MOD_ID, "block/railgun/gun_mount"));
        public static final ModelResourceLocation BODY = ModelResourceLocation
            .standalone(ResourceLocation.fromNamespaceAndPath(RailgunTransport.MOD_ID, "block/railgun/gun_body"));
        public static final ModelResourceLocation HOLD = ModelResourceLocation
            .standalone(ResourceLocation.fromNamespaceAndPath(RailgunTransport.MOD_ID, "block/railgun/gun_hold"));
    }

    public static class Target {
        public static final ModelResourceLocation MOUNT = ModelResourceLocation
            .standalone(ResourceLocation.fromNamespaceAndPath(RailgunTransport.MOD_ID, "block/target/target_mount"));
        public static final ModelResourceLocation SHAFT_FRONT = ModelResourceLocation.standalone(
            ResourceLocation.fromNamespaceAndPath(RailgunTransport.MOD_ID, "block/target/hydraulic_shaft_front")
        );
        public static final ModelResourceLocation SHAFT_MIDDLE = ModelResourceLocation.standalone(
            ResourceLocation.fromNamespaceAndPath(RailgunTransport.MOD_ID, "block/target/hydraulic_shaft_middle")
        );
        public static final ModelResourceLocation SHAFT_BACK = ModelResourceLocation.standalone(
            ResourceLocation.fromNamespaceAndPath(RailgunTransport.MOD_ID, "block/target/hydraulic_shaft_back")
        );
        public static final ModelResourceLocation CLAW_UP = ModelResourceLocation
            .standalone(ResourceLocation.fromNamespaceAndPath(RailgunTransport.MOD_ID, "block/target/claw_upper"));
        public static final ModelResourceLocation CLAW_DOWN = ModelResourceLocation
            .standalone(ResourceLocation.fromNamespaceAndPath(RailgunTransport.MOD_ID, "block/target/claw_lower"));
        public static final ModelResourceLocation CLAW_LEFT = ModelResourceLocation
            .standalone(ResourceLocation.fromNamespaceAndPath(RailgunTransport.MOD_ID, "block/target/claw_left"));
        public static final ModelResourceLocation CLAW_RIGHT = ModelResourceLocation
            .standalone(ResourceLocation.fromNamespaceAndPath(RailgunTransport.MOD_ID, "block/target/claw_right"));
    }
}
