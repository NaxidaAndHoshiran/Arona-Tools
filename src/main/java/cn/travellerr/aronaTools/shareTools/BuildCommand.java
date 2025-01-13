package cn.travellerr.aronaTools.shareTools;

import kotlin.sequences.SequencesKt;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import net.mamoe.mirai.console.command.CommandManager;

import java.util.List;

public class BuildCommand {


    static final String COMMAND_PREFIX = CommandManager.INSTANCE.getCommandPrefix();

//    static final String COMMAND_PREFIX = "/";

    public static Regex createCommand(String commandStart, Class<?>... args) {
        StringBuilder regexBuilder = new StringBuilder("^" + COMMAND_PREFIX + "(" + commandStart + ")");
        for (Class<?> arg : args) {
            if (arg == Integer.class || arg == Long.class) {
                regexBuilder.append(" ?(\\d+)");
            } else if (arg == String.class) {
                regexBuilder.append(" ?(\\S+)");
            } else if (arg == Double.class || arg == Float.class) {
                regexBuilder.append(" ?(\\d+\\.\\d+)");
            } else if (arg == Boolean.class) {
                regexBuilder.append(" ?(true|false)");
            } else {
                throw new IllegalArgumentException("Unsupported argument type: " + arg.getName());
            }
        }
        regexBuilder.append(" ?$");
        return new Regex(regexBuilder.toString());
    }

    /**
     * 获取指令(正则)中的每个参数，去掉了指令本身
     * <br>
     * 例如指令为"/test 1 2 3"，则返回[1, 2, 3]
     * @param regex 指令的正则
     * @param input 输入的指令
     * @return 指令中的每个参数
     */
    public static List<String> getEveryValue(Regex regex, String input) {
        List<MatchResult> matches = SequencesKt.toList(regex.findAll(input, 0));
        if (matches.isEmpty()) return List.of();
        return matches.get(0).getGroupValues().subList(2, matches.get(0).getGroupValues().size());
    }
}