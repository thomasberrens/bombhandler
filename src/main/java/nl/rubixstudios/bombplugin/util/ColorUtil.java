package nl.rubixstudios.bombplugin.util;

import java.util.List;
import java.util.stream.Collectors;

public class ColorUtil {
        public static String translate(String message) {
            return message.replace("&", "§");
        }

        public static List<String> translate(List<String> messages) {
            return messages.stream().map(ColorUtil::translate).collect(Collectors.toList());
        }
}
