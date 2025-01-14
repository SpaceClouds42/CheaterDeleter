package io.github.coolmineman.cheaterdeleter.checks;

import io.github.coolmineman.cheaterdeleter.duck.PlayerMoveC2SPacketView;
import io.github.coolmineman.cheaterdeleter.events.MovementPacketCallback;
import io.github.coolmineman.cheaterdeleter.objects.CDPlayer;
import io.github.coolmineman.cheaterdeleter.util.CollisionUtil;
import net.minecraft.util.ActionResult;

public class InventoryMoveCheck extends Check implements MovementPacketCallback {
    public InventoryMoveCheck() {
        MovementPacketCallback.EVENT.register(this);
    }

    //TODO Damage
    @Override
    public ActionResult onMovementPacket(CDPlayer player, PlayerMoveC2SPacketView packet) {
        if (player.shouldBypassAnticheat() || CollisionUtil.isNearby(player, 2, 4, CollisionUtil.NON_SOLID_COLLISION)) return ActionResult.PASS;
        if (packet.isChangePosition() && player.getCurrentScreenHandler() != null && invalidateMove(player, packet)) {
            player.mcPlayer.closeHandledScreen();
            flag(player, FlagSeverity.MINOR, "Inventory Move");
        } else if (player.getCurrentScreenHandler() == null) {
            InventoryMoveCheckData data = player.getOrCreateData(InventoryMoveCheckData.class, InventoryMoveCheckData::new);
            data.lastDeltaX = Double.MAX_VALUE;
            data.lastDeltaZ = Double.MAX_VALUE;
        }
        return ActionResult.PASS;
    }

    private boolean invalidateMove(CDPlayer player, PlayerMoveC2SPacketView packet) {
        if (player.getVelocity().getY() < 0.0 && packet.getY() > player.getY()) return true;
        InventoryMoveCheckData data = player.getOrCreateData(InventoryMoveCheckData.class, InventoryMoveCheckData::new);
        double delaX = Math.abs(packet.getX() - player.getX());
        double delaZ = Math.abs(packet.getZ() - player.getZ());
        if (player.getVelocity().getX() == 0.0 && delaX > 0.0 && delaX > data.lastDeltaX) return true;
        if (player.getVelocity().getX() != 0.0) {
            data.lastDeltaX = Double.MAX_VALUE;
        } else {
            data.lastDeltaX = delaX;
        }
        if (player.getVelocity().getZ() == 0.0 && delaZ > 0.0 && delaZ > data.lastDeltaZ) return true;
        if (player.getVelocity().getZ() != 0.0) {
            data.lastDeltaZ = Double.MAX_VALUE;
        } else {
            data.lastDeltaZ = delaZ;
        }
        return false;
    }

    public static class InventoryMoveCheckData {
        double lastDeltaX = Double.MAX_VALUE;
        double lastDeltaZ = Double.MAX_VALUE;
    } 
    
}
