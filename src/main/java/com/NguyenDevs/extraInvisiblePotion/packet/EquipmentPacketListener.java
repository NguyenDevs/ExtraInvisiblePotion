package com.NguyenDevs.extraInvisiblePotion.packet;

import com.NguyenDevs.extraInvisiblePotion.util.ItemDataUtil;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class EquipmentPacketListener extends PacketAdapter {
    public EquipmentPacketListener(Plugin plugin) {
        super(plugin, new PacketType[]{Server.ENTITY_EQUIPMENT});
    }

    public void onPacketSending(PacketEvent event) {
        if (event.getPacketType() == Server.ENTITY_EQUIPMENT) {
            PacketContainer packet = event.getPacket();
            Entity entity = (Entity)packet.getEntityModifier(event).read(0);
            if (entity instanceof Player) {
                Player player = (Player)entity;
                if (event.getPlayer().getEntityId() != player.getEntityId()) {
                    if (!event.getPlayer().hasPermission("extrainvisiblepotion.bypass")) {
                        List<Pair<ItemSlot, ItemStack>> equipmentList = (List)packet.getSlotStackPairLists().read(0);
                        List<Pair<ItemSlot, ItemStack>> newEquipmentList = new ArrayList();
                        boolean modified = false;
                        Iterator var8 = equipmentList.iterator();

                        while(var8.hasNext()) {
                            Pair<ItemSlot, ItemStack> pair = (Pair)var8.next();
                            ItemStack item = (ItemStack)pair.getSecond();
                            if (ItemDataUtil.isInvisible(item)) {
                                newEquipmentList.add(new Pair((ItemSlot)pair.getFirst(), new ItemStack(Material.AIR)));
                                modified = true;
                            } else {
                                newEquipmentList.add(pair);
                            }
                        }

                        if (modified) {
                            packet.getSlotStackPairLists().write(0, newEquipmentList);
                        }

                    }
                }
            }
        }
    }
}