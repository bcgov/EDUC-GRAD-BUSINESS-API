package ca.bc.gov.educ.api.gradbusiness.util;

public class ThreadLocalStateUtil {
    private static InheritableThreadLocal<String> transaction = new InheritableThreadLocal<>();
    private static final InheritableThreadLocal<String> user = new InheritableThreadLocal<String>();
    private static final InheritableThreadLocal<String> requestSource = new InheritableThreadLocal<String>();

    public static void setCorrelationID(String correlationID){
        transaction.set(correlationID);
    }
    public static String getCorrelationID() {
        return transaction.get();
    }

    public static String getCurrentUser() {
        return user.get();
    }

    public static void setCurrentUser(String username) {
        user.set(username);
    }

    public static void setRequestSource(String reqSource){
        requestSource.set(reqSource);
    }
    public static String getRequestSource() {
        return requestSource.get();
    }

    public static void clear() {
        transaction.remove();
        user.remove();
        requestSource.remove();
    }

}
