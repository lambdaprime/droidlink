package id.droidlink;

public class DroidLinkUtils {
    /**
     * Converts first character to lowercase or do nothing if second character is in uppercase
     */
    public static String decapitalize(String str) {
        if (str == null || str.isEmpty())
            return str;
        if (str.length() == 1)
            return str.toUpperCase();
        if (Character.isUpperCase(str.charAt(1)))
                return str;
        return str.toLowerCase().charAt(0) + str.substring(1);
    }

    public static void main(String[] args) {
        System.out.println(decapitalize("xxx"));
        System.out.println(decapitalize("XXX"));
        System.out.println(decapitalize("XxX"));
    }
}
