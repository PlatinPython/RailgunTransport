package platinpython.railguntransport.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class CapsuleBlockItem extends BlockItem {
    public CapsuleBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }
}
