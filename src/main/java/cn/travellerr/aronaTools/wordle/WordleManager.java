package cn.travellerr.aronaTools.wordle;

import cn.chahuyun.hibernateplus.HibernateFactory;
import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.travellerr.aronaTools.AronaTools;
import cn.travellerr.aronaTools.entity.WordleInfo;
import cn.travellerr.aronaTools.shareTools.Log;
import cn.travellerr.aronaTools.shareTools.MessageUtil;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import wordle.entity.WordList;
import wordle.entity.Words;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Wordle游戏管理类
 * 提供Wordle游戏的主要逻辑和功能
 *
 * @author Travellerr
 */
public class WordleManager {
    public static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(20, 20, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    private static final int[] X_COORDINATES = {103, 308, 511, 712, 915};
    private static final int[] Y_COORDINATES = {170, 373, 575, 778, 980, 1183, 1388, 1590, 1791, 1994};
    private static final int BLOCK_SIZE = 191;
    public static CopyOnWriteArrayList<User> users = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<Group> groups = new CopyOnWriteArrayList<>();

    /**
     * 启动Wordle游戏
     *
     * @param sender 发送者，可以是群组或用户
     * @param subject 接收消息的对象
     * @param chain 消息链
     */
    public static void wordle(Contact sender, Contact subject, MessageChain chain) {
        try {
            // 提交任务到线程池
            threadPoolExecutor.submit(() -> {
                Date startTime = new Date(); // 记录开始时间
                ArrayList<Character> usedLetters = new ArrayList<>(); // 用于存储已使用的字母

                // 判断发送者是群组还是用户，并添加到相应的列表
                if (sender instanceof Group) {
                    groups.add((Group) sender);
                } else {
                    users.add((User) sender);
                }

                // 发送游戏规则消息
                subject.sendMessage(new QuoteReply(chain).plus("""
                            欢迎来到 Wordle！
                            规则很简单：
                            1. 你有规定次机会猜出单词。
                            2. 你只能猜出 5 个字符长的单词。
                            3. 你将收到有关您的猜测的反馈：
                                - 单词中的字母正确且位置正确：绿色
                                - 单词中的字母是正确的，但位置错误：黄色
                                - 单词中没有字母：灰色
                            4. 如果你在规定次机会内猜中单词，您就赢了。
                            5. 你只有120秒的时间发送一个单词
                            6. 若群聊中多人游玩，请在猜测前加上 "#" 号
                            7. 玩得开心！
                        """));

                int score = 0; // 初始化分数
                int difficulty = 2; // 难度
                try (InputStreamReader stream = new InputStreamReader(Objects.requireNonNull(AronaTools.INSTANCE.getResourceAsStream("wordle/CET4-6.json")))) {
                    // 发送难度选择消息
                    subject.sendMessage(MessageUtil.quoteReply(chain, "请选择难度：\n1. 简单(5个字母，10次机会)\n2. 普通(5个字母，6次机会)(默认)\n3. 困难(5个字母，6次机会，不允许重复字母)"));
                    difficulty = parseDifficulty(MessageUtil.getNextMessage(sender, subject, chain, 60, TimeUnit.SECONDS)); // 解析难度
                    WordList wordList = WordList.Companion.parse(stream); // 解析单词列表
                    Words wordObject = wordList.getRandomWord(); // 获取随机单词
                    String word = wordObject.getWord().toLowerCase(Locale.ROOT); // 获取单词
                    subject.sendMessage(MessageUtil.quoteReply(chain, "游戏开始！请输入您的猜测："));

                    BufferedImage picture = chooseBackground(difficulty); // 选择背景图片
                    if (picture == null) {
                        subject.sendMessage(MessageUtil.quoteReply(chain, "出错啦~ 问题原因: 无法加载背景图片"));
                        throw new IllegalArgumentException("Invalid difficulty: " + difficulty);
                    }

                    String lastFeedback = "....."; // 初始化反馈
                    int times = difficulty == 1 ? 10 : 6; // 根据难度设置猜测次数

                    for (int i = 0; i < times; i++) {
                        // 获取用户的猜测
                        String guess = MessageUtil.getNextMessage(sender, subject, chain, 120, TimeUnit.SECONDS).toLowerCase(Locale.ROOT);
                        if (guess.isBlank() || guess.isEmpty()) break;

                        // 验证猜测的有效性
                        if (guess.length() != 5 || !wordList.getValid().contains(guess)) {
                            subject.sendMessage(MessageUtil.quoteReply(chain, "猜测无效。请输入一个有效的 5 个字母的单词。"));
                            i--;
                            continue;
                        }

                        // 检查困难模式下是否有重复字母
                        if (difficulty == 3 && guess.chars().anyMatch(c -> usedLetters.contains((char) c))) {
                            subject.sendMessage(MessageUtil.quoteReply(chain, "猜测无效。请不要重复使用字母。"));
                            i--;
                            continue;
                        }

                        // 获取反馈并更新图片
                        String feedback = getFeedback(word, guess, usedLetters);
                        Log.info("Wordle: " + guess + " Feedback: " + feedback);
                        picture = pictureBuilder(guess, feedback, picture, i);
                        BufferedImage sendPic = trimPicture(picture);
                        ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
                        ImageIO.write(sendPic, "png", imageStream);
                        try (imageStream; ExternalResource externalResource = ExternalResource.create(imageStream.toByteArray())) {
                            Image image = sender.uploadImage(externalResource);
                            if (guess.equals(word)) {
                                score = 100; // 猜对单词，得分100
                                lastFeedback = feedback;
                                Image addStamp = addStamp(sendPic, lastFeedback, subject);
                                String time = DateUtil.formatBetween(startTime, new Date(), BetweenFormatter.Level.SECOND);
                                subject.sendMessage(MessageUtil.quoteReply(chain, (addStamp == null ? image.plus("祝贺！你猜对了这个词！单词为: " + wordObject.getMessage() + "\n 用时: " + time) : addStamp.plus("祝贺！你猜对了这个词！单词为: " + wordObject.getMessage() + "\n 用时: " + time))));
                                return;
                            }
                            subject.sendMessage(MessageUtil.quoteReply(chain, image));
                        }
                        lastFeedback = feedback;
                    }
                    score = getScore(lastFeedback); // 计算最终得分
                    String time = DateUtil.formatBetween(startTime, new Date(), BetweenFormatter.Level.SECOND);
                    subject.sendMessage(MessageUtil.quoteReply(chain, "你的机会已经用完了。这个词是：" + wordObject.getMessage() + "\n你的分数是：" + score + "\n 用时: " + time));
                } catch (Exception e) {
                    Log.error("出错啦~", e);
                    subject.sendMessage(new QuoteReply(chain).plus("出错啦~ 问题原因: " + e.getMessage()));
                } finally {
                    // 更新用户信息
                    WordleInfo userInfo = getWordleInfo(sender, difficulty);
                    if (userInfo.getWordleScore() < score ||
                            (userInfo.getWordleScore() == score && userInfo.getWordleTime() > System.currentTimeMillis() - startTime.getTime()) || !DateUtil.isSameDay(userInfo.getCompleteDate(), new Date())) {
                        userInfo.setWordleScore(score);
                        userInfo.setWordleTime(System.currentTimeMillis() - startTime.getTime());
                        HibernateFactory.merge(userInfo);
                        subject.sendMessage(new QuoteReply(chain).plus("恭喜你打破了自己的记录！"));
                    }
                    // 从列表中移除用户或群组
                    if (sender instanceof Group) {
                        groups.remove((Group) sender);
                    } else {
                        users.remove((User) sender);
                    }
                }
            });
        } catch (RejectedExecutionException e) {
            Log.error("出错啦~", e);
            subject.sendMessage(new QuoteReply(chain).plus("出错啦~ 游戏无法启动，可能是因为游戏线程已满("+threadPoolExecutor.getPoolSize()+"/"+threadPoolExecutor.getActiveCount()+")，请稍后再试"));
        }
    }

    /**
     * 解析难度输入
     *
     * @param difficultyInput 难度输入字符串
     * @return 解析后的难度值
     */
    private static int parseDifficulty(String difficultyInput) {
        try {
            int difficulty = Integer.parseInt(difficultyInput);
            if (difficulty < 1 || difficulty > 3) throw new IllegalArgumentException();
            return difficulty;
        } catch (Exception ignored) {
            return 2; // 默认普通难度
        }
    }

        /**
         * 获取猜测的反馈
         *
         * @param word 正确的单词
         * @param guess 用户的猜测
         * @param usedLetters 已使用的字母列表
         * @return 反馈字符串
         */
        private static String getFeedback(String word, String guess, ArrayList<Character> usedLetters) {
            StringBuilder feedback = new StringBuilder();
            boolean[] matched = new boolean[5];
            for (int i = 0; i < 5; i++) {
                if (word.charAt(i) == guess.charAt(i)) {
                    feedback.append("+");
                    matched[i] = true;
                } else {
                    feedback.append(".");
                }
            }
            for (int i = 0; i < 5; i++) {
                boolean containsSameLetter = word.contains(String.valueOf(guess.charAt(i)));
                if (feedback.charAt(i) == '.' && containsSameLetter) {
                    for (int j = 0; j < 5; j++) {
                        if (!matched[j] && word.charAt(j) == guess.charAt(i)) {
                            feedback.setCharAt(i, '-');
                            matched[j] = true;
                            break;
                        }
                    }
                }
                if (feedback.charAt(i) == '.' && !containsSameLetter) {
                    usedLetters.add(guess.charAt(i));
                }
            }
            return feedback.toString();
        }
    /**
     * 构建反馈图片
     *
     * @param guess 用户的猜测
     * @param feedback 反馈字符串
     * @param oldPicture 旧的图片
     * @param frequency 当前猜测次数
     * @return 更新后的图片
     */
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

    /**
     * 修剪图片
     *
     * @param image 原始图片
     * @return 修剪后的图片
     */
    private static BufferedImage trimPicture(BufferedImage image) {
        BufferedImage newPicture = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D pen = newPicture.createGraphics();
        pen.setColor(Color.WHITE);
        pen.fillRect(0, 0, image.getWidth(), image.getHeight());
        pen.drawImage(image, 0, 0, null);
        pen.dispose();
        return newPicture;
    }

    /**
     * 选择背景图片
     *
     * @param difficulty 游戏难度
     * @return 背景图片
     */
    private static BufferedImage chooseBackground(int difficulty) {
        try (InputStream stream = AronaTools.INSTANCE.getResourceAsStream("wordle/backgrounds/" + difficulty + ".png")) {
            if (stream == null) throw new IllegalArgumentException("Invalid difficulty: " + difficulty);
            return ImageIO.read(stream);
        } catch (Exception e) {
            Log.error("出错啦~", e);
            return null;
        }
    }

    /**
     * 添加印章到图片
     *
     * @param image 原始图片
     * @param feedback 反馈字符串
     * @param subject 接收消息的对象
     * @return 添加印章后的图片
     */
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

    /**
     * 根据反馈计算分数
     *
     * @param feedback 反馈字符串
     * @return 计算后的分数
     */
    private static int getScore(String feedback) {
        int score = feedback.replace("+", "").length() * -20 + 100;
        if (score > 80 && score < 100) score = 100;
        return score;
    }

    /**
     * 显示排行榜
     *
     * @param subject 接收消息的对象
     * @param isGroup 是否为群组
     * @param chronoUnit 时间单位
     */
    public static void rank(Contact subject, boolean isGroup, ChronoUnit chronoUnit) {
        List<WordleInfo> ranks = HibernateFactory.selectList(WordleInfo.class).stream()
            .filter(wordleInfo -> wordleInfo.isGroup() == isGroup)
            .filter(wordleInfo -> isSame(wordleInfo.getCompleteDate(), new Date(), chronoUnit))
            .sorted(Comparator.comparingInt(WordleInfo::getWordleScore).reversed()
                    .thenComparingLong(WordleInfo::getWordleTime))
            .toList();

        if (ranks.isEmpty()) {
            subject.sendMessage(new PlainText("暂无" + (isGroup ? "群聊" : "用户") + "参与游戏"));
            return;
        }

        ForwardMessageBuilder forwardMessageBuilder = new ForwardMessageBuilder(subject);
        AtomicInteger index = new AtomicInteger(1);

        forwardMessageBuilder.add(subject.getBot(), new PlainText("Wordle 排行榜-" + (isGroup ? "群聊" : "用户") + " (更新时间：" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + ")\n"));
        forwardMessageBuilder.add(subject.getBot(), new PlainText("共有 " + ranks.size() + " 位" + (isGroup ? "群聊" : "用户") + "参与了游戏\n"));

        ranks.forEach(wordleInfo -> forwardMessageBuilder.add(subject.getBot(), new PlainText("第 " + index.getAndIncrement() + " 名\n" + wordleInfo.getWordleRank())));

        subject.sendMessage(forwardMessageBuilder.build());
    }

    /**
     * 获取用户信息
     *
     * @param user 用户或群组
     * @return 用户信息
     */
    private static WordleInfo getWordleInfo(Contact user, int difficulty) {
        // 按照分数降序排列，如果分数相同则按照用时升序排列
        return HibernateFactory.selectList(WordleInfo.class, "userId", user.getId())
                .stream()
                .filter(info -> info.getDifficulty() == difficulty)
                .min(Comparator.comparingInt(WordleInfo::getWordleScore).reversed()
                        .thenComparingLong(WordleInfo::getWordleTime))
                .orElseGet(() -> {
                    Log.info("Wordle：为用户创建新的 WordleInfo：" + user.getId());
                    int id = HibernateFactory.selectList(WordleInfo.class).stream()
                            .max(Comparator.comparingInt(WordleInfo::getId))
                            .map(info -> info.getId() + 1)
                            .orElse(1);
                    return WordleInfo.builder()
                            .id(id)
                            .userId(user.getId())
                            .difficulty(difficulty)
                            .name(user instanceof Group ? "暂时匿名" : ((User) user).getNick())
                            .wordleScore(0)
                            .wordleTime(0L)
                            .isGroup(user instanceof Group)
                            .build();
                });
    }

    private static boolean isSame (Date date1, Date date2, ChronoUnit chronoUnit) {
        return switch (chronoUnit) {
            case DAYS -> DateUtil.isSameDay(date1, date2);
            case WEEKS -> DateUtil.isSameWeek(date1, date2, true);
            case MONTHS -> DateUtil.isSameMonth(date1, date2);
            case YEARS -> DateUtil.year(date1) == DateUtil.year(date2);
            case FOREVER -> true;
            default -> false;
        };
    }
}