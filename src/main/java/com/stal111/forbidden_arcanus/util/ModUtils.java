package com.stal111.forbidden_arcanus.util;

import com.google.common.collect.Maps;
import com.stal111.forbidden_arcanus.Main;

import net.minecraft.block.*;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.Random;

public class ModUtils {

	protected static final Random random = new Random();
	
	public static ResourceLocation location(String path) {
		return new ResourceLocation(Main.MOD_ID, path);
	}

	public static boolean isShiftDown() {
		return InputMappings.isKeyDown(GLFW.glfwGetCurrentContext(), GLFW.GLFW_KEY_LEFT_SHIFT) || InputMappings.isKeyDown(GLFW.glfwGetCurrentContext(), GLFW.GLFW_KEY_RIGHT_SHIFT);
	}

	public static boolean applyBonemeal(ItemStack stack, World worldIn, BlockPos pos, net.minecraft.entity.player.PlayerEntity player) {
		BlockState blockstate = worldIn.getBlockState(pos);
		int hook = net.minecraftforge.event.ForgeEventFactory.onApplyBonemeal(player, worldIn, pos, blockstate, stack);
		if (hook != 0) return hook > 0;
		if (blockstate.getBlock() instanceof IGrowable) {
			IGrowable igrowable = (IGrowable)blockstate.getBlock();
			if (igrowable.canGrow(worldIn, pos, blockstate, worldIn.isRemote)) {
				if (!worldIn.isRemote) {
					if (igrowable.canUseBonemeal(worldIn, worldIn.rand, pos, blockstate)) {
						igrowable.grow(worldIn, worldIn.rand, pos, blockstate);
					}
				}
				return true;
			}
		}
		return false;
	}

	public static boolean growSeagrass(ItemStack stack, World worldIn, BlockPos pos, @Nullable Direction side) {
		if (worldIn.getBlockState(pos).getBlock() == Blocks.WATER && worldIn.getFluidState(pos).getLevel() == 8) {
			if (!worldIn.isRemote) {
				label79:
				for(int i = 0; i < 128; ++i) {
					BlockPos blockpos = pos;
					Biome biome = worldIn.getBiome(pos);
					BlockState blockstate = Blocks.SEAGRASS.getDefaultState();

					for(int j = 0; j < i / 16; ++j) {
						blockpos = blockpos.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
						biome = worldIn.getBiome(blockpos);
						if (worldIn.getBlockState(blockpos).func_224756_o(worldIn, blockpos)) {
							continue label79;
						}
					}

					if (biome == Biomes.WARM_OCEAN || biome == Biomes.DEEP_WARM_OCEAN) {
						if (i == 0 && side != null && side.getAxis().isHorizontal()) {
							blockstate = BlockTags.WALL_CORALS.getRandomElement(worldIn.rand).getDefaultState().with(DeadCoralWallFanBlock.FACING, side);
						} else if (random.nextInt(4) == 0) {
							blockstate = BlockTags.UNDERWATER_BONEMEALS.getRandomElement(random).getDefaultState();
						}
					}

					if (blockstate.getBlock().isIn(BlockTags.WALL_CORALS)) {
						for(int k = 0; !blockstate.isValidPosition(worldIn, blockpos) && k < 4; ++k) {
							blockstate = blockstate.with(DeadCoralWallFanBlock.FACING, Direction.Plane.HORIZONTAL.random(random));
						}
					}

					if (blockstate.isValidPosition(worldIn, blockpos)) {
						BlockState blockstate1 = worldIn.getBlockState(blockpos);
						if (blockstate1.getBlock() == Blocks.WATER && worldIn.getFluidState(blockpos).getLevel() == 8) {
							worldIn.setBlockState(blockpos, blockstate, 3);
						} else if (blockstate1.getBlock() == Blocks.SEAGRASS && random.nextInt(10) == 0) {
							((IGrowable)Blocks.SEAGRASS).grow(worldIn, random, blockpos, blockstate1);
						}
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public static void addStrippable(Block block, Block strippedBlock) {
		AxeItem.BLOCK_STRIPPING_MAP = Maps.newHashMap(AxeItem.BLOCK_STRIPPING_MAP);
		AxeItem.BLOCK_STRIPPING_MAP.put(block, strippedBlock);
	}
}
