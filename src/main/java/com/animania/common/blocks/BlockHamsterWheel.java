package com.animania.common.blocks;

import java.util.List;

import com.animania.Animania;
import com.animania.common.capabilities.CapabilityRefs;
import com.animania.common.entities.rodents.EntityHamster;
import com.animania.common.tileentities.TileEntityHamsterWheel;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockHamsterWheel extends BlockContainer
{

	private String name = "block_hamster_wheel";

	public BlockHamsterWheel()
	{
		super(Material.IRON, MapColor.GRAY);
		this.setRegistryName(new ResourceLocation(Animania.MODID, this.name));
		this.setDefaultState(this.blockState.getBaseState());
		GameRegistry.register(this);
		this.setUnlocalizedName(Animania.MODID + "_" + this.name);
		this.setCreativeTab(Animania.TabAnimaniaResources);
		this.setTickRandomly(true);
		this.setHardness(1.4f);
		this.setResistance(3.4F);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntityHamsterWheel();
	}


	@Override
	public boolean isFullyOpaque(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}


	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		TileEntityHamsterWheel te = (TileEntityHamsterWheel) world.getTileEntity(pos);

		if(player.isBeingRidden() && !te.isRunning())
		{
			List<Entity> passengers = player.getPassengers();
			if(passengers.get(0) instanceof EntityHamster && ((EntityHamster)passengers.get(0)).getFed())
			{
				if(player.hasCapability(CapabilityRefs.CAPS, null))
				{
					player.getCapability(CapabilityRefs.CAPS, null).setMounted(false);
				}
				EntityHamster hamster = (EntityHamster) passengers.get(0);
				NBTTagCompound hamsternbt = new NBTTagCompound();
				hamster.writeToNBT(hamsternbt);
				
				EntityHamster clone = new EntityHamster(world);
				clone.readFromNBT(hamsternbt);
				
				player.removePassengers();
				te.setHamster(clone);
				hamster.setDead();
				te.markDirty();
				
				return true;

			}
		}


		if(!world.isRemote)
		player.sendMessage(new TextComponentString(te.getEnergy() + "/" + te.getMaxEnergyStored(null) + " RF"));



		return false;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		TileEntityHamsterWheel te = (TileEntityHamsterWheel) worldIn.getTileEntity(pos);
		te.ejectHamster();

		super.breakBlock(worldIn, pos, state);
	}



}