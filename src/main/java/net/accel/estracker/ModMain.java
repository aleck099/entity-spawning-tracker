package net.accel.estracker;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ModMain implements ClientModInitializer {
    public static final String ID = "estracker";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    public static List<String> trackingList = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        var addSubCmd = ClientCommandManager.literal("add").then(ClientCommandManager.argument("key", StringArgumentType.string()).executes(context -> {
            var k = StringArgumentType.getString(context, "key");
            trackingList.add(k);
            context.getSource().sendFeedback(new TranslatableText("commands.estracker.add.success", k));
            return 0;
        }));
        var removeSubCmd = ClientCommandManager.literal("remove").then(ClientCommandManager.argument("key", StringArgumentType.string()).executes(context -> {
            var k = StringArgumentType.getString(context, "key");
            var result = trackingList.remove(k);
            Text t;
            if (result) t = new TranslatableText("commands.estracker.remove.success", k);
            else t = new TranslatableText("commands.estracker.remove.failure", k);
            context.getSource().sendFeedback(t);
            return 0;
        }));
        var showSubCmd = ClientCommandManager.literal("list").executes(context -> {
            var s = context.getSource();
            for (var e : trackingList) {
                s.sendFeedback(new LiteralText(e));
            }
            s.sendFeedback(new TranslatableText("commands.estracker.total", trackingList.size()));
            return 0;
        });
        var c = ClientCommandManager.literal("estracker").then(addSubCmd).then(removeSubCmd).then(showSubCmd);
        ClientCommandManager.DISPATCHER.register(c);
    }
}
