package xyz.gnarbot.gnar.commands.executors.admin;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import xyz.gnarbot.gnar.Shard;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Command(
        aliases = "update",
        administrator = true)
public class UpdateCommand extends CommandExecutor {

    @Override
    public void execute(Context context, String[] args) {

        for(Shard s : context.getBot().getShards()) {
            s.getPresence().setGame(Game.of("Updating bot..."));
        }

        try {
            Runtime rt = Runtime.getRuntime();
            Message msg;

            msg = context.getMessage().getChannel().sendMessage("*Now updating...*\n\nRunning `git clone`... ").complete(true);

            String branch = "master";
            if (args.length > 1) {
                branch = args[1];
            }
            String githubUser = "Gnar-Team";
            if (args.length > 2) {
                githubUser = args[2];
            }

            //Clear any old update folder if it is still present
            try {
                Process rm = rt.exec("rm -rf Gnar-git/");
                rm.waitFor(5, TimeUnit.SECONDS);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            Process gitClone = rt.exec("git clone https://github.com/" + githubUser + "/Gnar-Bot.git --branch " + branch + " --recursive --single-branch Gnar-git");

            if (!gitClone.waitFor(120, TimeUnit.SECONDS)) {
                msg = msg.editMessage(msg.getRawContent() + "[:anger: timed out]\n\n").complete(true);
                throw new RuntimeException("Operation timed out: git clone");
            } else if (gitClone.exitValue() != 0) {
                msg = msg.editMessage(msg.getRawContent() + "[:anger: returned code " + gitClone.exitValue() + "]\n\n").complete(true);
                throw new RuntimeException("Bad response code");
            }

            msg = msg.editMessage(msg.getRawContent() + "👌🏽\n\nRunning `gradlew`... ").complete(true);
            File updateDir = new File("Gnar-git/");

            Process mvnBuild = rt.exec("./Gnar-git/gradlew");

            if (!mvnBuild.waitFor(600, TimeUnit.SECONDS)) {
                msg = msg.editMessage(msg.getRawContent() + "[:anger: timed out]\n\n").complete(true);
                throw new RuntimeException("Operation timed out: gradlew");
            } else if (mvnBuild.exitValue() != 0) {
                msg = msg.editMessage(msg.getRawContent() + "[:anger: returned code " + mvnBuild.exitValue() + "]\n\n").complete(true);
                throw new RuntimeException("Bad response code");
            }

            msg.editMessage(msg.getRawContent() + "👌🏽").queue();

            if(!new File("./Gnar-git/build/libs/Gnar-bot-1.0.jar").renameTo(new File("/home/Gnar-bot/Gnar-bot-1.0.jar"))){
                throw new RuntimeException("Failed to move jar to home");
            }

            for(Shard s : context.getBot().getShards()) {
                s.getPresence().setGame(Game.of("Restarting bot..."));
            }

            System.exit(20);
        } catch (InterruptedException | IOException | RateLimitedException ex) {
            throw new RuntimeException(ex);
        }

    }

}
