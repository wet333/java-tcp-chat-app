package online.awet.commons;

import com.google.gson.Gson;

/**
 * Utility for serializing and deserializing {@link Command} objects to/from JSON.
 * Uses newline-delimited JSON (one compact JSON object per line) so it pairs naturally
 * with {@code BufferedReader.readLine()} on both ends of the TCP connection.
 *
 * <p>{@code Gson} is thread-safe for concurrent calls to {@code toJson}/{@code fromJson},
 * so the single static instance is safe to share across threads.
 */
public class CommandSerializer {

    private static final Gson GSON = new Gson();

    private CommandSerializer() {}

    public static String toJson(Command command) {
        return GSON.toJson(command);
    }

    public static Command fromJson(String json) {
        return GSON.fromJson(json, Command.class);
    }
}
