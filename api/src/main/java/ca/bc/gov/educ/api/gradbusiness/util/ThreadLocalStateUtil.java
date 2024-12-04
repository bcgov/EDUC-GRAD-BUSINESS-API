package ca.bc.gov.educ.api.gradbusiness.util;

public class ThreadLocalStateUtil {
    private static ThreadLocal<String> transaction = new ThreadLocal<>();
    public static void setCorrelationID(String correlationID){
        transaction.set(correlationID);
    }
    public static String getCorrelationID() {
        return transaction.get();
    }
    public static void clear() {
        transaction.remove();
    }
}
