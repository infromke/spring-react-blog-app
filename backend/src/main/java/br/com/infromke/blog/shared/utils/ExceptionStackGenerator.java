package br.com.infromke.blog.shared.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class ExceptionStackGenerator {

    private ExceptionStackGenerator() {
        throw new UnsupportedOperationException("ExceptionStackGenerator is an utility class and " +
                "cannot be instantiated");
    }

    // extrai a stacktrace (se o SPRING_ENV for "development")
    public static String getStackTrace(Exception ex, String env) {
        if ("development".equalsIgnoreCase(env)) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            return sw.toString();
        }
        return null;
    }
}
