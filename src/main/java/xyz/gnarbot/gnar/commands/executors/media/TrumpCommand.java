package xyz.gnarbot.gnar.commands.executors.media;

import org.apache.commons.lang3.StringUtils;
import xyz.gnarbot.gnar.Bot;
import xyz.gnarbot.gnar.commands.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Arrays;

@Command(
        aliases = {"trump", "executiveorder"},
        usage = "(words...)",
        description = "Generate a picture of our president."
)
@BotInfo(
        id = 29,
        category = Category.MEDIA
)
public class TrumpCommand extends CommandExecutor {
    @Override
    public void execute(Context context, String label, String[] args) {
        if (args.length == 0) {
            Bot.getCommandDispatcher().sendHelp(context, getInfo());
            return;
        }

        try (InputStream is = Bot.class.getClassLoader().getResourceAsStream("trump.jpg")) {
            BufferedImage image = ImageIO.read(is);

            Graphics2D g2 = image.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            String query = StringUtils.join(Arrays.asList(args), " ");

            if (query.length() > 153) {
                query = query.substring(0, 150) + "...";
            }

            if (query.isEmpty()) {
                query = "Try putting in some text into the arguments, ie. \"_trump Pepe\"";
            }

            double fontSize = 65.0 / (0.05 * query.length() + 1.0) + 20;

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
            int breakWidth = 230;
            LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, g2.getFontRenderContext());
            lineMeasurer.setPosition(paragraphStart);

            while (lineMeasurer.getPosition() < paragraphEnd) {
                TextLayout layout = lineMeasurer.nextLayout(breakWidth);

                drawPosY += layout.getAscent();

                layout.draw(g2, drawPosX, drawPosY);

                drawPosY += layout.getDescent() + layout.getLeading();
                drawPosX -= 1;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            byte[] imageInByte = baos.toByteArray();

            g2.dispose();

            context.getTextChannel().sendFile(imageInByte, "attachment.jpg", null).queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

