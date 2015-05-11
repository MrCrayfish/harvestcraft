package com.pam.harvestcraft;
 
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
 
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
 
public class GuiMarket extends GuiContainer
{
        private static final ResourceLocation gui = new ResourceLocation("harvestcraft:textures/gui/market.png");
 
        private GuiButton left, right;
        private GuiButton button_buy;
 
        private int itemNum;
 
        private ItemStack buySlot;
        private TileEntityMarket tileEntityMarket;
        private EntityPlayer player;
 
        public GuiMarket(InventoryPlayer inventoryplayer, TileEntityMarket tileEntityMarket)
        {
                super(new ContainerMarket(inventoryplayer, tileEntityMarket));
                this.tileEntityMarket = tileEntityMarket;
        }
 
        @Override
        public void initGui()
        {
                super.initGui();
                Keyboard.enableRepeatEvents(false);
               
                buttonList.clear();
               
                int posX = width / 2 - 48;
                int posY = height / 2 - 48;
               
                left = new GuiButton(0, posX, posY - 21, 15, 20, "<");
                right = new GuiButton(1, posX + 16, posY - 21, 15, 20, ">");
                button_buy = new GuiButton(2, posX, posY + 1, 55, 20, "Buy");
               
                buttonList.add(left);
                buttonList.add(right);
                buttonList.add(button_buy);
               
                this.itemNum = tileEntityMarket.getBrowsingInfo();
        }
 
        @Override
        protected void actionPerformed(GuiButton guibutton)
        {
                if (!guibutton.enabled)
                {
                        return;
                }
                if (guibutton.id == 0)
                {
                        itemNum--;
                        if (itemNum < 0)
                        {
                                itemNum = MarketItems.getSize() - 1;
                        }
                        this.tileEntityMarket.setBrowsingInfo(itemNum);
                }
                if (guibutton.id == 1)
                {
                        itemNum++;
                        if (itemNum > MarketItems.getSize() - 1)
                        {
                                itemNum = 0;
                        }
                        this.tileEntityMarket.setBrowsingInfo(itemNum);
                }
                if (guibutton.id == 2)
                {
                        this.buySlot = this.tileEntityMarket.getStackInSlot(0);
                        if (buySlot != null)
                        {
                                MarketData data = MarketItems.getData(itemNum);
                                if( buySlot.getItem() == data.getCurrency().getItem())
                                {
                                        if (buySlot.getItemDamage() == data.getCurrency().getItemDamage())
                                        {
                                                int price = data.getPrice();
                                                if (buySlot.stackSize == price)
                                                {
                     
                                                        PacketHandler.INSTANCE.sendToServer(new MessageMarketBuy(this.itemNum, this.tileEntityMarket.xCoord, this.tileEntityMarket.yCoord, this.tileEntityMarket.zCoord, true));
                                                }
                                                else if (buySlot.stackSize > price && buySlot.stackSize > 1)
                                                {
                                                        PacketHandler.INSTANCE.sendToServer(new MessageMarketBuy(this.itemNum, this.tileEntityMarket.xCoord, this.tileEntityMarket.yCoord, this.tileEntityMarket.zCoord, false));
                                                }
                                                if (buySlot.stackSize == 0 && price == 1)
                                                {
                                                        PacketHandler.INSTANCE.sendToServer(new MessageMarketBuy(this.itemNum, this.tileEntityMarket.xCoord, this.tileEntityMarket.yCoord, this.tileEntityMarket.zCoord, true));
                                                }
                                        }
                                }
                        }
                }
        }
 
        @Override
        public void onGuiClosed()
        {
                PacketHandler.INSTANCE.sendToServer(new MessageMarketClosed(this.tileEntityMarket.xCoord, this.tileEntityMarket.yCoord, this.tileEntityMarket.zCoord));
                super.onGuiClosed();
        }
 
        protected void drawGuiContainerForegroundLayer(int par1, int par2)
        {
                this.fontRendererObj.drawString("Inventory", 8, (ySize - 96) + 13, 4210752);
 
                GL11.glPushMatrix();
                RenderHelper.enableGUIStandardItemLighting();
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                GL11.glEnable(GL11.GL_COLOR_MATERIAL);
                GL11.glEnable(GL11.GL_LIGHTING);
                itemRender.zLevel = 100.0F;
 
                MarketData data = MarketItems.getData(itemNum);
               
                ItemStack item = data.getItem();
                itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), item, 73, 16);
                itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), item, 73, 16);
 
                ItemStack currency = data.getCurrency();
                itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), currency, 100, 16);
                itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), currency, 100, 16);
                itemRender.zLevel = 0.0F;
                GL11.glDisable(GL11.GL_LIGHTING);
 
                int price = data.getPrice();
                this.fontRendererObj.drawString("x" + Integer.toString(price), 116, 20, 0);
 
                GL11.glPopMatrix();
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                RenderHelper.enableStandardItemLighting();
        }
 
        public void drawScreen(int par1, int par2, float par3)
        {
                super.drawScreen(par1, par2, par3);
                ItemStack item = MarketItems.getData(itemNum).getItem();
                if (this.func_146978_c(73, 16, 16, 16, par1, par2))
                {
                        this.renderToolTip(item, par1, par2);
                }
        }
 
        protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
        {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.getTextureManager().bindTexture(gui);
                int l = (width - xSize) / 2;
                int i1 = (height - ySize) / 2;
                this.drawTexturedModalRect(l, i1 - 10, 0, 0, xSize, ySize + 21);
        }
}
