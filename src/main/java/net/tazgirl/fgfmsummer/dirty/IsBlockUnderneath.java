package net.tazgirl.fgfmsummer.dirty;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

public class IsBlockUnderneath
{
    public static boolean IsBlockUnder(ServerPlayer player, Block blockToCheck)
    {
        AABB feetBox = player.getBoundingBox().inflate(-0.001, -player.getBbHeight() + 0.001, -0.001);
        int minX = Mth.floor(feetBox.minX);
        int maxX = Mth.floor(feetBox.maxX);
        int minZ = Mth.floor(feetBox.minZ);
        int maxZ = Mth.floor(feetBox.maxZ);
        int y = Mth.floor(feetBox.minY - 0.001);

        boolean onBlock = false;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                Block blockBeneathPlayer = player.level().getBlockState(new BlockPos(x, y, z)).getBlock();
                if (blockBeneathPlayer == blockToCheck)
                {
                    return true;
                }
            }
        }

        return false;
    }


}
