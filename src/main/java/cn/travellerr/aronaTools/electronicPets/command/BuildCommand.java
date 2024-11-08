package cn.travellerr.aronaTools.electronicPets.command;

import kotlin.sequences.SequencesKt;
import kotlin.text.MatchResult;
import kotlin.text.Regex;

import java.util.List;

public class BuildCommand {


    static final String COMMAND_PREFIX = "#";//CommandManager.INSTANCE.getCommandPrefix();

    public static Regex createCommand(String commandStart, Class<?>... args) {
        StringBuilder regexBuilder = new StringBuilder("^" + COMMAND_PREFIX + "(" + commandStart + ")");
        for (Class<?> arg : args) {
            if (arg == Integer.class) {
                regexBuilder.append(" ?(\\d+)");
            } else if (arg == String.class) {
                regexBuilder.append(" ?(\\S+)");
            } else if (arg == Double.class) {
                regexBuilder.append(" ?(\\d+\\.\\d+)");
            } else if (arg == Float.class) {
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

    public static List<String> getEveryValue(Regex regex, String input) {
        Iterable<MatchResult> matches = SequencesKt.toList(regex.findAll(input, 0));

        List<String> groups = null;
        for (MatchResult match : matches) {
            groups = match.getGroupValues();
        }
        assert groups != null;
        return groups.subList(2, groups.size());
    }


}
