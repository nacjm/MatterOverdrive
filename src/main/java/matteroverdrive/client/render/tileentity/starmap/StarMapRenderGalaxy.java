package matteroverdrive.client.render.tileentity.starmap;

import cofh.lib.gui.GuiColor;
import cofh.lib.render.RenderHelper;
import matteroverdrive.Reference;
import matteroverdrive.proxy.ClientProxy;
import matteroverdrive.starmap.GalaxyClient;
import matteroverdrive.starmap.data.*;
import matteroverdrive.tile.TileEntityMachineStarMap;
import matteroverdrive.util.MOStringHelper;
import matteroverdrive.util.RenderUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Vec3;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Simeon on 6/17/2015.
 */
@SideOnly(Side.CLIENT)
public class StarMapRenderGalaxy extends StarMapRendererStars
{
    @Override
    public void renderBody(Galaxy galaxy, SpaceBody spaceBody, TileEntityMachineStarMap starMap, float partialTicks,float viewerDistance)
    {
        double distanceMultiply = 2;

        for (Quadrant quadrant : galaxy.getQuadrants())
        {
            renderStars(quadrant, starMap,distanceMultiply,2);
        }

        glDisable(GL_TEXTURE_2D);

        //glLineStipple(1, (short)0xFF);
        //glEnable(GL_LINE_STIPPLE);

        for (int i = 0;i < galaxy.getTravelEvents().size();i++)
        {
            TravelEvent travelEvent = galaxy.getTravelEvents().get(i);
            if (travelEvent.isValid(GalaxyClient.getInstance().getTheGalaxy()))
            {

                Vec3 from = GalaxyClient.getInstance().getTheGalaxy().getPlanet(travelEvent.getFrom()).getStar().getPosition(2);
                Vec3 to = GalaxyClient.getInstance().getTheGalaxy().getPlanet(travelEvent.getTo()).getStar().getPosition(2);
                Vec3 dir = from.subtract(to);
                double percent = travelEvent.getPercent(starMap.getWorldObj());

                RenderUtils.applyColorWithMultipy(Reference.COLOR_HOLO, 0.5f);
                glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                glPushMatrix();
                glTranslated(from.xCoord + dir.xCoord * percent, from.yCoord + dir.yCoord * percent, from.zCoord + dir.zCoord * percent);
                RenderUtils.rotateTowards(Vec3.createVectorHelper(-1,0,0.0),dir.normalize(),Vec3.createVectorHelper(0,1,0));
                RenderUtils.drawShip(0, 0, 0, 0.02);
                glPopMatrix();

                glPolygonMode(GL_FRONT_AND_BACK,GL_FILL);
                RenderUtils.applyColorWithMultipy(Reference.COLOR_HOLO_PURPLE, 0.5f);
                glBegin(GL_LINE_STRIP);
                glVertex3d(from.xCoord, from.yCoord, from.zCoord);
                glVertex3d(to.xCoord,to.yCoord,to.zCoord);
                glEnd();
            }
        }
        glEnable(GL_TEXTURE_2D);
        //glDisable(GL_LINE_STIPPLE);
    }

    @Override
    public void renderGUIInfo(Galaxy galaxy, SpaceBody spaceBody,TileEntityMachineStarMap starMap, float partialTicks, float opacity)
    {
        glEnable(GL_ALPHA_TEST);
        int ownedSystemCount = galaxy.getOwnedSystemCount(Minecraft.getMinecraft().thePlayer);
        int enemySystemCount = galaxy.getEnemySystemCount(Minecraft.getMinecraft().thePlayer);
        int freeSystemCount = galaxy.getStarCount() - ownedSystemCount - enemySystemCount;
        GuiColor color = Reference.COLOR_HOLO_GREEN;
        RenderUtils.applyColorWithMultipy(color, opacity);
        ClientProxy.holoIcons.bindSheet();
        RenderHelper.renderIcon(0, -30, 0, ClientProxy.holoIcons.getIcon("page_icon_star"), 20, 20);
        RenderUtils.drawString(String.format("x%s",ownedSystemCount),24,-23,color,opacity);

        color = Reference.COLOR_HOLO_RED;
        RenderUtils.applyColorWithMultipy(color, opacity);
        ClientProxy.holoIcons.bindSheet();
        RenderHelper.renderIcon(64, -30, 0, ClientProxy.holoIcons.getIcon("page_icon_star"), 20, 20);
        RenderUtils.drawString(String.format("x%s",enemySystemCount),88,-23,color,opacity);

        color = Reference.COLOR_HOLO;
        RenderUtils.applyColorWithMultipy(color, opacity);
        ClientProxy.holoIcons.bindSheet();
        RenderHelper.renderIcon(128, -30, 0, ClientProxy.holoIcons.getIcon("page_icon_star"), 20, 20);
        RenderUtils.drawString(String.format("x%s", freeSystemCount), 152, -23, color, opacity);

        for (int i = 0;i < galaxy.getTravelEvents().size();i++)
        {
            TravelEvent travelEvent = galaxy.getTravelEvents().get(i);
            if (travelEvent.isValid(GalaxyClient.getInstance().getTheGalaxy())) {
                Planet from = GalaxyClient.getInstance().getTheGalaxy().getPlanet(travelEvent.getFrom());
                Planet to = GalaxyClient.getInstance().getTheGalaxy().getPlanet(travelEvent.getTo());

                RenderUtils.drawString(String.format("%s -> %s : %s", from.getName(), to.getName(), MOStringHelper.formatRemainingTime(galaxy.getTravelEvents().get(i).getTimeRemainning(starMap.getWorldObj()) / 20)), 0, -48 - i * 10, Reference.COLOR_HOLO,opacity);
            }
        }
        glDisable(GL_ALPHA_TEST);
    }

    @Override
    public double getHologramHeight() {
        return 2.5;
    }

}