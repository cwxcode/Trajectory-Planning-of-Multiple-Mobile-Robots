package tt.util;

public class Args {
	
	

    public static String getArgumentValue(String[] args, String argName, boolean required) {
        return getArgumentValue(args, argName, required, null);
    }

    public static String getArgumentValue(String[] args, String argName, boolean required, String defaultValue) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(argName)) {
                if (i + 1 < args.length) {
                    return args[i + 1];
                } else {
                    throw new RuntimeException("The value for argument " + argName + " is not provided.");
                }
            }
        }

        if (required) {
            throw new RuntimeException("The required argument " + argName + " is not provided.");
        } else {
            return defaultValue;
        }
    }

    public static boolean isArgumentSet(String[] args, String argName) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(argName)) {
                return true;
            }
        }
        return false;
    }
}
