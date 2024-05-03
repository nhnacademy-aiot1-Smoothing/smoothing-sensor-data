package live.smoothing.sensordata.dto;

public class ThreadLocalUserId {

    private ThreadLocalUserId() {}

    private static final ThreadLocal<String> userId = new ThreadLocal<>();

    public static void setUserId(String userId) {
        ThreadLocalUserId.userId.set(userId);
    }

    public static String getUserId() {
        return userId.get();
    }

    public static void clear() {
        userId.remove();
    }
}
