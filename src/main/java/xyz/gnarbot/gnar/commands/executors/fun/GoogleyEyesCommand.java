//package xyz.gnarbot.gnar.commands.executors.fun;
//
//import com.mashape.unirest.http.HttpResponse;
//import com.mashape.unirest.http.JsonNode;
//import com.mashape.unirest.http.Unirest;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import xyz.gnarbot.gnar.commands.Category;
//import xyz.gnarbot.gnar.commands.Command;
//import xyz.gnarbot.gnar.commands.CommandExecutor;
//import xyz.gnarbot.gnar.utils.Context;
//import xyz.gnarbot.gnar.utils.Utils;
//
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//@Command(
//        aliases = {"eyes", "googleyeyes"},
//        usage = "-image_url",
//        description = "Put weird eyes on people!",
//        category = Category.FUN)
//public class GoogleyEyesCommand extends CommandExecutor {
//    private static BufferedImage resize(BufferedImage img, int newW, int newH) {
//        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
//        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
//
//        Graphics2D g2d = dimg.createGraphics();
//        g2d.drawImage(tmp, 0, 0, null);
//        g2d.dispose();
//
//        return dimg;
//    }
//
//    @Override
//    public void execute(Context context, String[] args) {
//        if (args.length == 0) {
//            context.send().error("Please provide an image link.").queue();
//            return;
//        }
//
//        try {
//            String urlStr = args[0];
//            String encodedStr = URLEncoder.encode(urlStr, StandardCharsets.UTF_8.displayName());
//
//            HttpResponse<JsonNode> response = Unirest.get("https://apicloud-facerect.p.mashape.com/process-url.json")
//                    .queryString("features", true)
//                    .queryString("url", encodedStr)
//                    .header("X-Mashape-Key", context.getBot().getKeys().getMashape())
//                    .header("Accept", "application/json")
//                    .asJson();
//
//            JSONObject j = new JSONObject(response.getBody().toString());
//
//            List<JSONObject> eyesJSON = new ArrayList<>();
//
//            JSONArray j2 = (JSONArray) j.get("faces");
//
//            try {
//                for (int in = 0; in < j2.length(); in++) {
//                    JSONObject j3 = (JSONObject) j2.get(in);
//                    JSONObject j4 = (JSONObject) j3.get("features");
//                    JSONArray j5 = (JSONArray) j4.get("eyes");
//
//                    for (int i = 0; i < j5.length(); i++) {
//                        eyesJSON.add(new JSONObject(j5.get(i).toString()));
//                    }
//                }
//            } catch (Exception e) {
//                if (eyesJSON.isEmpty()) {
//                    context.send().error("The API did not detect any eyes/facial features.").queue();
//                    return;
//                }
//            }
//
//            BufferedImage targetImg = ImageIO.read(new URL(urlStr));
//            BufferedImage eye, resizedEye;
//
//            Graphics graphics = targetImg.getGraphics();
//            for (JSONObject json : eyesJSON) {
//                eye = ImageIO.read(new File(Utils.DATA_FOLDER, "resources/eye" + new Random().nextInt(2) + ".png"));
//                resizedEye = resize(eye, (int) json.get("width"), (int) json.get("height"));
//                graphics.drawImage(resizedEye, (int) json.get("x"), (int) json.get("y"), null);
//            }
//
//            //writing the file
//            File outputFile = new File(Utils.DATA_FOLDER, "output_image.png");
//            try {
//                if (ImageIO.write(targetImg, "jpg", outputFile)) {
//                    //send if success
//                    context.getMessage().getChannel().sendFile(outputFile, null).queue();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                //noinspection ResultOfMethodCallIgnored
//                outputFile.delete();
//            }
//
//            //event.getChannel().sendMessage(mb.build());
//        } catch (Exception e) {
//            context.send().error("An unexpected error occurred, did you provide a proper link?").queue();
//            e.printStackTrace();
//        }
//    }
//}
