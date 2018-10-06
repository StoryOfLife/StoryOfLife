package net.nolifers.storyoflife.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.nolifers.storyoflife.StoryofLife;

import javax.annotation.Nullable;

public class EntityWildebeast extends EntityAnimal {


    EntityAIEatGrass grassEatAI;
    int grassEatTimer;
    public EntityWildebeast(World worldIn) {
        super(worldIn);
        this.setSize(0.9F, 1.4F);

    }

    @Override
    public void initEntityAI(){
        grassEatAI = new EntityAIEatGrass(this);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 2));
        this.tasks.addTask(2, new EntityAIMate(this, 1));
        this.tasks.addTask(3, new EntityAITempt(this, 1.25, Items.WHEAT, false));
        this.tasks.addTask(3, new EntityAITempt(this, 1, new ItemStack(Blocks.TALLGRASS,1).getItem(), false));
        EntityAIFollowParent ai = new EntityAIFollowParent(this, 1.1);
        ai.setMutexBits(1);
        this.tasks.addTask(4, ai);
        this.tasks.addTask(5, this.grassEatAI);
        this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 1));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
    }

    @Override
    protected void updateAITasks()
    {
        this.grassEatTimer = this.grassEatAI.getEatingGrassTimer();
        super.updateAITasks();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float f){
        boolean result = super.attackEntityFrom(source,f);
        if(!result) return false;
        Entity ent = source.getTrueSource();
        if(ent instanceof EntityLivingBase){
            for(Entity b : this.world.getEntitiesInAABBexcluding(this,new AxisAlignedBB(this.posX-5,this.posY,this.posZ-5,this.posX+5,this.posY+2,this.posZ+5),x->x instanceof EntityWildebeast)){
                ((EntityLivingBase)b).setRevengeTarget(this.getRevengeTarget());
            }
        }

        return true;
    }
    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.19000000417232513D);
    }

    @Override
    public void onLivingUpdate(){
        if (this.world.isRemote)
        {
            this.grassEatTimer = Math.max(0, this.grassEatTimer - 1);
        }


        super.onLivingUpdate();
    }

    @Override
    public boolean isBreedingItem(ItemStack item){
        return item.getItem()==Items.WHEAT||item.getItem()== ItemBlock.getItemFromBlock(Blocks.TALLGRASS);
    }

    @Override
    public ResourceLocation getLootTable(){
        return LootTableList.ENTITIES_COW;
    }


    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        return new EntityWildebeast(this.world);
    }

    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id)
    {
        if (id == 10)
        {
            this.grassEatTimer = 40;
        }
        else
        {
            super.handleStatusUpdate(id);
        }
    }

    @SideOnly(Side.CLIENT)
    public float getHeadRotationAngleX(float p_70890_1_)
    {
        if (this.grassEatTimer > 4 && this.grassEatTimer <= 36)
        {
            float f = ((float)(this.grassEatTimer - 4) - p_70890_1_) / 32.0F;
            return -0.12217304763960307F + ((float)Math.PI * 7F / 100F) * MathHelper.sin(f * 28.7F);
        }
        else
        {
            return this.grassEatTimer > 0 ? -0.12217304763960307F : this.rotationPitch * 0.017453292F;
        }
    }
    @SideOnly(Side.CLIENT)
    public float getHeadRotationPointY(float p_70894_1_)
    {
        if (this.grassEatTimer <= 0)
        {
            return 0.0F;
        }
        else if (this.grassEatTimer >= 4 && this.grassEatTimer<= 36)
        {
            return 1.0F;
        }
        else
        {
            return this.grassEatTimer < 4 ? ((float)this.grassEatTimer - p_70894_1_) / 4.0F : -((float)(this.grassEatTimer - 40) - p_70894_1_) / 4.0F;
        }
    }

}