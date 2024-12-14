package cn.travellerr.aronaTools.wordle;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.travellerr.aronaTools.AronaTools;
import cn.travellerr.aronaTools.shareTools.Log;
import cn.travellerr.aronaTools.shareTools.MessageUtil;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.utils.ExternalResource;
import wordle.entity.Words;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WordleManager {
    public static final ExecutorService executorService = Executors.newFixedThreadPool(20);
    private static final int[] X_COORDINATES = {103, 308, 511, 712, 915};
    private static final int[] Y_COORDINATES = {170, 373, 575, 778, 980, 1183, 1388, 1590, 1791, 1994};
    private static final int BLOCK_SIZE = 191;
    public static CopyOnWriteArrayList<User> users = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<Group> groups = new CopyOnWriteArrayList<>();


    public static void wordle(Contact sender, Contact subject, MessageChain chain) {
        executorService.submit(() -> {
            Date startTime = new Date();
            ArrayList<Character> usedLetters = new ArrayList<>();

            if (sender instanceof Group) {
                groups.add((Group) sender);
            } else {
                users.add((User) sender);
            }

            subject.sendMessage(new QuoteReply(chain).plus("""
                    欢迎来到 Wordle！
                    规则很简单：
                    1. 你有规定次机会猜出单词。
                    2. 你只能猜出 5 个字符长的单词。
                    3. 你将收到有关您的猜测的反馈：
                        - 单词中的字母正确且位置正确：绿色
                        - 单词中的字母是正确的，但位置错误：黄色
                        - 单词中没有字母：红色
                    4. 如果你在规定次机会内猜中单词，您就赢了。
                    5. 你只有120秒的时间发送一个单词
                    6. 若群聊中多人游玩，请在猜测前加上 "#" 符号
                    7. 玩得开心！
                """));

            try (InputStreamReader stream = new InputStreamReader(Objects.requireNonNull(AronaTools.INSTANCE.getResourceAsStream("wordle/words.json")))) {
                subject.sendMessage(MessageUtil.quoteReply(chain, "请选择难度：\n1. 简单(5个字母，10次机会)\n2. 普通(5个字母，6次机会)(默认)\n3. 困难(5个字母，6次机会，不允许重复字母)"));
                int difficulty = parseDifficulty(MessageUtil.getNextMessage(sender, subject, chain, 60, TimeUnit.SECONDS));
                Words words = Words.Companion.parse(stream);
                String word = words.getRandomWord();
                subject.sendMessage(MessageUtil.quoteReply(chain, "游戏开始！请输入您的猜测："));

                BufferedImage picture = chooseBackground(difficulty);
                if (picture == null) {
                    subject.sendMessage(MessageUtil.quoteReply(chain, "出错啦~ 问题原因: 无法加载背景图片"));
                    throw new IllegalArgumentException("Invalid difficulty: " + difficulty);
                }

                String lastFeedback = ".....";

                int times = difficulty == 1 ? 10 : 6;

                for (int i = 0; i < times; i++) {
                    String guess = MessageUtil.getNextMessage(sender, subject, chain, 120, TimeUnit.SECONDS).toLowerCase(Locale.ROOT);
                    if (guess.isBlank() || guess.isEmpty()) break;

                    if (guess.length() != 5 || !words.getValid().contains(guess)) {
                        subject.sendMessage(MessageUtil.quoteReply(chain, "猜测无效。请输入一个有效的 5 个字母的单词。"));
                        i--;
                        continue;
                    }

                    if (difficulty == 3 && guess.chars().anyMatch(c -> usedLetters.contains((char) c))) {
                        subject.sendMessage(MessageUtil.quoteReply(chain, "猜测无效。请不要重复使用字母。"));
                        i--;
                        continue;
                    }

                    String feedback = getFeedback(word, guess, usedLetters);
                    Log.info("Wordle: " + guess + " Feedback: " + feedback);
                    picture = pictureBuilder(guess, feedback, picture, i);
                    BufferedImage sendPic = trimPicture(picture);
                    ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
                    ImageIO.write(sendPic, "png", imageStream);
                    try (imageStream; ExternalResource externalResource = ExternalResource.create(imageStream.toByteArray())) {
                        Image image = sender.uploadImage(externalResource);
                        if (guess.equals(word)) {
                            lastFeedback = feedback;
                            Image addStamp = addStamp(sendPic, lastFeedback, subject);
                            String time = DateUtil.formatBetween(startTime, new Date(), BetweenFormatter.Level.SECOND);

                            subject.sendMessage(MessageUtil.quoteReply(chain, (addStamp == null ? image.plus("祝贺！你猜对了这个词！单词为: " + word + "\n 用时: " + time) : addStamp.plus("祝贺！你猜对了这个词！单词为: " + word + "\n 用时: " + time))));
                            return;
                        }
                        subject.sendMessage(MessageUtil.quoteReply(chain, image));
                    }
                    lastFeedback = feedback;
                }
                int score = getScore(lastFeedback);
                String time = DateUtil.formatBetween(startTime, new Date(), BetweenFormatter.Level.SECOND);
                subject.sendMessage(MessageUtil.quoteReply(chain, "你的机会已经用完了。这个词是：" + word + "\n你的分数是：" + score + "\n 用时: " + time));
            } catch (Exception e) {
                Log.error("出错啦~", e);
                subject.sendMessage(new QuoteReply(chain).plus("出错啦~ 问题原因: " + e.getMessage()));
            } finally {
                if (sender instanceof Group) {
                    groups.add((Group) sender);
                } else {
                    users.add((User) sender);
                }
            }
        });
    }

    private static int parseDifficulty(String difficultyInput) {
        try {
            int difficulty = Integer.parseInt(difficultyInput);
            if (difficulty < 1 || difficulty > 3) throw new IllegalArgumentException();
            return difficulty;
        } catch (Exception ignored) {
            return 2; // Default to normal difficulty
        }
    }

    private static String getFeedback(String word, String guess, ArrayList<Character> usedLetters) {
        StringBuilder feedback = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (word.charAt(i) == guess.charAt(i)) feedback.append("+");
            else if (word.contains(String.valueOf(guess.charAt(i)))) feedback.append("-");
            else { usedLetters.add(guess.charAt(i)); feedback.append("."); }
        }
        return feedback.toString();
    }

    private static BufferedImage pictureBuilder(String guess, String feedback, BufferedImage oldPicture, int frequency) {
        BufferedImage newPicture = new BufferedImage(oldPicture.getWidth(), oldPicture.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D pen = newPicture.createGraphics();
        ArrayList<Color> guessFeedback = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            LetterColor color = LetterColor.getLetterColor(feedback.charAt(i));
            if (color == null) throw new IllegalArgumentException("Invalid feedback character: " + feedback.charAt(i));
            guessFeedback.add(color.getColor());
        }
        pen.setFont(pen.getFont().deriveFont(70f).deriveFont(Font.BOLD));
        for (int i = 0; i < 5; i++) {
            pen.setColor(guessFeedback.get(i));
            pen.fillRect(X_COORDINATES[i], Y_COORDINATES[frequency], BLOCK_SIZE, BLOCK_SIZE);
            pen.setColor(Color.white);
            pen.drawString(String.valueOf(guess.charAt(i)), X_COORDINATES[i] + 70, Y_COORDINATES[frequency] + 120);
        }
        pen.drawImage(oldPicture, 0, 0, null);
        pen.dispose();
        return newPicture;
    }

    private static BufferedImage trimPicture(BufferedImage image) {
        BufferedImage newPicture = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D pen = newPicture.createGraphics();
        pen.setColor(Color.WHITE);
        pen.fillRect(0, 0, image.getWidth(), image.getHeight());
        pen.drawImage(image, 0, 0, null);
        pen.dispose();
        return newPicture;
    }

    private static BufferedImage chooseBackground(int difficulty) {
        try (InputStream stream = AronaTools.INSTANCE.getResourceAsStream("wordle/backgrounds/" + difficulty + ".png")) {
            if (stream == null) throw new IllegalArgumentException("Invalid difficulty: " + difficulty);
            return ImageIO.read(stream);
        } catch (Exception e) {
            Log.error("出错啦~", e);
            return null;
        }
    }

    private static Image addStamp(BufferedImage image, String feedback, Contact subject) {
        Graphics2D pen = image.createGraphics();
        int score = getScore(feedback);
        try (InputStream stream = AronaTools.INSTANCE.getResourceAsStream("wordle/stamp/" + score + ".png")) {
            if (stream == null) throw new IllegalArgumentException("Invalid score: " + score);
            BufferedImage stamp = ImageIO.read(stream);
            pen.drawImage(stamp, image.getWidth() / 2 - stamp.getWidth() / 2, image.getHeight() / 2 - stamp.getHeight() / 2, null);
            pen.dispose();
            ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", imageStream);
            try (imageStream; ExternalResource externalResource = ExternalResource.create(imageStream.toByteArray())) {
                return subject.uploadImage(externalResource);
            }
        } catch (Exception e) {
            Log.error("出错啦~", e);
            return null;
        }
    }

    private static int getScore(String feedback) {
        int score = feedback.replace("+", "").length() * -20 + 100;
        Log.info("Wordle: Score: " + score);
        if (score < 30) score = 0;
        else if (score < 59) score = 59;
        else if (score < 90) score = 60;
        else if (score < 100) score = 100;
        return score;
    }
}