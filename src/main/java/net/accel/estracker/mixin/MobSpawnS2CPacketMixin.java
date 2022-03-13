package net.accel.estracker.mixin;

import net.accel.estracker.ModMain;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobSpawnS2CPacket.class)
public class MobSpawnS2CPacketMixin {
    @Inject(method = "apply(Lnet/minecraft/network/listener/ClientPlayPacketListener;)V", at = @At("HEAD"))
    private void injected(CallbackInfo ci) {
        var this2 = (MobSpawnS2CPacket) (Object) this;
        var etype = Registry.ENTITY_TYPE.get(this2.getEntityTypeId());
        var key = etype.getTranslationKey();
        var pos = new BlockPos(this2.getX(), this2.getY(), this2.getZ());
        if (ModMain.trackingList.contains(key)) {
            var client = MinecraftClient.getInstance();
            client.execute(() -> {
                var coordtext = Texts.bracketed(new TranslatableText("chat.coordinates", pos.getX(), pos.getY(), pos.getZ()))
                        .styled(style -> style
                                .withColor(Formatting.GREEN)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + pos.getX() + ' ' + pos.getY() + ' ' + pos.getZ()))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.coordinates.tooltip"))));
                var p = client.player;
                if (p != null)
                    p.sendMessage(new TranslatableText("notification.estracker.coordinate", new TranslatableText(key), coordtext), false);
            });
        }
    }
}
