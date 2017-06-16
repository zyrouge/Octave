package xyz.gnarbot.gnar.commands.executors.media;

import com.sun.deploy.util.StringUtils;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;
import xyz.gnarbot.gnar.utils.Context;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Arrays;

@Command(
        aliases = {"trump", "executiveorder"},
        description = "Our president.",
        category = Category.FUN
)
public class ExecutiveOrderCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String[] args) {
        InputStream is = Bot.class.getClassLoader().getResourceAsStream("trump.jpg");

        long start = System.currentTimeMillis();

        try {
            BufferedImage image = ImageIO.read(is);

            Graphics2D g2 = image.createGraphics();

            String query = StringUtils.join(Arrays.asList(args), " ");

            if (query.length() > 175) {
                query = query.substring(0, 172) + "...";
            }

            if (query.isEmpty()) {
                query = "Try putting in some text into the arguments, ie. \"_trump Pepe\"";
            }

            double fontSize = 60.0 / (0.1 * query.length() + 1.0) + 25;

            Font font = new Font("Times New Roman", Font.PLAIN, (int) fontSize);

            AffineTransform tx = new AffineTransform();
            tx.rotate(Math.toRadians(3));
            tx.shear(0, Math.toRadians(5.5));
            font = font.deriveFont(tx);

            g2.setFont(font);
            g2.setColor(Color.BLACK);

            //String lines = WordUtils.wrap(, 25, null, true);

            float drawPosX = 380;
            float drawPosY = 230;

            AttributedString string = new AttributedString(query);
            string.addAttribute(TextAttribute.FONT, font);

            AttributedCharacterIterator paragraph = string.getIterator();
            int paragraphStart = paragraph.getBeginIndex();
            int paragraphEnd = paragraph.getEndIndex();
            int breakWidth = 250;
            LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, g2.getFontRenderContext());
            lineMeasurer.setPosition(paragraphStart);

            while (lineMeasurer.getPosition() < paragraphEnd) {
                TextLayout layout = lineMeasurer.nextLayout(breakWidth);

                drawPosY += layout.getAscent();

                layout.draw(g2, drawPosX, drawPosY);

                drawPosY += layout.getDescent() + layout.getLeading();
                drawPosX -= 1;
            }


            //layout.draw(g2, 380, 290);

            File file = new File("saved.png");

            ImageIO.write(image, "jpg", file);

            g2.dispose();

            context.getChannel().sendFile(file, null).queue();

            if (file.delete()) {
                file.deleteOnExit();
            }

            long end = System.currentTimeMillis();

            System.out.println(end - start + "ms");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

