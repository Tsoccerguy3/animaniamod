package com.animania.common.entities.rodents.ai;

import com.animania.common.entities.rodents.rabbits.EntityAnimaniaRabbit;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityAIPanicRabbits extends EntityAIBase
{
	private final EntityAnimaniaRabbit theEntityCreature;
	protected double             speed;
	private double               randPosX;
	private double               randPosY;
	private double               randPosZ;
	private int                  duration;
	private boolean              hitFlag;

	public EntityAIPanicRabbits(EntityAnimaniaRabbit creature, double speedIn) {
		this.theEntityCreature = creature;
		this.speed = speedIn;
		this.setMutexBits(1);
		this.duration = 0;
		this.hitFlag = false;
	}

	@Override
	public boolean shouldExecute() {
		
		EntityPlayer checkPlayer = this.theEntityCreature.world.getClosestPlayer(this.theEntityCreature.posX, this.theEntityCreature.posY,
				this.theEntityCreature.posZ, 20, false);
		
		if (this.theEntityCreature.getAttackTarget() == null && !this.theEntityCreature.isBurning() && this.duration == 0) {
		
			this.hitFlag = false;
			return false;
		}
		else if (!this.theEntityCreature.isBurning()) {
			
			Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.theEntityCreature, 40, 4);

			EntityAnimaniaRabbit entityRabbit = this.theEntityCreature;
			if (entityRabbit.getSleeping()) {
				entityRabbit.setSleeping(false);
			}
			
			if (this.hitFlag == false) {
				this.hitFlag = true;
				this.duration = 60;
			}

			this.duration--;

			if (vec3d == null) {
				this.hitFlag = false;
				return false;
			}
			else {
				this.randPosX = vec3d.x;
				this.randPosY = vec3d.y;
				this.randPosZ = vec3d.z;

				return true;
			}
		}
		else {
			
			EntityAnimaniaRabbit entityRabbit = (EntityAnimaniaRabbit) this.theEntityCreature;
			if (entityRabbit.getSleeping()) {
				entityRabbit.setSleeping(false);
			}
			
			BlockPos blockpos = this.getRandPos(this.theEntityCreature.world, this.theEntityCreature, 40, 4);

			if (blockpos == null) {
				this.hitFlag = false;
				return false;
			}
			else {
				this.randPosX = blockpos.getX();
				this.randPosY = blockpos.getY();
				this.randPosZ = blockpos.getZ();

				return true;
			}
		}
	}

	 @Override
	 public void startExecuting() {
		this.theEntityCreature.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed + 1);
	}

	 @Override
	 public boolean shouldContinueExecuting() {
		return !this.theEntityCreature.getNavigator().noPath();
	 }

	 private BlockPos getRandPos(World worldIn, Entity entityIn, int horizontalRange, int verticalRange) {
		 BlockPos blockpos = new BlockPos(entityIn);
		 BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
		 horizontalRange = horizontalRange + 10;
		 verticalRange = verticalRange + 10;
		 int i = blockpos.getX();
		 int j = blockpos.getY();
		 int k = blockpos.getZ();
		 float f = horizontalRange * horizontalRange * verticalRange * 5;
		 BlockPos blockpos1 = null;

		 for (int l = i - horizontalRange; l <= i + horizontalRange; ++l)
			 for (int i1 = j - verticalRange; i1 <= j + verticalRange; ++i1)
				 for (int j1 = k - horizontalRange; j1 <= k + horizontalRange; ++j1) {
					 blockpos$mutableblockpos.setPos(l, i1, j1);
					 IBlockState iblockstate = worldIn.getBlockState(blockpos$mutableblockpos);
					 Block block = iblockstate.getBlock();

					 if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
						 float f1 = (l - i) * (l - i) + (i1 - j) * (i1 - j) + (j1 - k) * (j1 - k);

						 if (f1 < f) {
							 f = f1;
							 blockpos1 = new BlockPos(blockpos$mutableblockpos);
						 }
					 }
				 }

		 return blockpos1;
	 }
}