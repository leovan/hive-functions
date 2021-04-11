package me.leovan.hive.utils;

public class TextUtils {
    public static String camelCaseToSnakeCase(String camelCase) {
        StringBuilder sb = new StringBuilder();

        for (char ch : camelCase.toCharArray()) {
            if (ch >= 'A' && ch <= 'Z') {
                sb.append('_');
                sb.append((char) (ch - 'A' + 'a'));
            } else {
                sb.append(ch);
            }
        }

        return sb.toString();
    }

    public static String snakeCaseToCamelCase(String snakeCase) {
        StringBuilder sb = new StringBuilder();

        String[] words = snakeCase.toLowerCase().split("_");
        sb.append(words[0]);

        for (int i = 1; i < words.length; ++i) {
            String word = words[i];

            if (word.length() == 0) {
                continue;
            }

            char firstChar = word.charAt(0);

            if (firstChar >= 'a' && firstChar <= 'z') {
                sb.append((char) (word.charAt(0) + 'A' - 'a'));
                sb.append(word.substring(1));
            } else {
                sb.append(word);
            }
        }

        return sb.toString();
    }
}
