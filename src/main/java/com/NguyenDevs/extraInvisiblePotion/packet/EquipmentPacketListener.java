package com.NguyenDevs.extraInvisiblePotion.packet;

import com.NguyenDevs.extraInvisiblePotion.util.ItemDataUtil;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class EquipmentPacketListener extends PacketAdapter {

    public EquipmentPacketListener(Plugin plugin) {
        super(plugin, PacketType.Play.Server.ENTITY_EQUIPMENT);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.ENTITY_EQUIPMENT) {
            return;
        }

        PacketContainer packet = event.getPacket();
        Entity entity = packet.getEntityModifier(event).read(0);

        if (!(entity instanceof Player player)) {
            return;
        }

        // If the receiver is the player themselves, do nothing (they should see their
        // own armor)
        // Use Entity ID or UUID comparison to be safe
        if (event.getPlayer().getEntityId() == player.getEntityId()) {
            return;
        }

        List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipmentList = packet.getSlotStackPairLists().read(0);
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> newEquipmentList = new ArrayList<>();
        boolean modified = false;

        for (Pair<EnumWrappers.ItemSlot, ItemStack> pair : equipmentList) {
            ItemStack item = pair.getSecond();
            if (ItemDataUtil.isInvisible(item)) {
                newEquipmentList.add(new Pair<>(pair.getFirst(), new ItemStack(Material.AIR)));
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
