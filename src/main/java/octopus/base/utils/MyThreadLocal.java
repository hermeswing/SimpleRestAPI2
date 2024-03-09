package octopus.base.utils;


import lombok.extern.slf4j.Slf4j;
import octopus.base.WebConst;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.List;

@Slf4j
public class MyThreadLocal {
    private static final ThreadLocal<HashMap<String, Object>> threadLocal = ThreadLocal.withInitial(HashMap::new);

    /**
     * private 생성자로 외부에서 직접 인스턴스 생성을 방지
     */
    private MyThreadLocal() {}

    /**
     * ThreadLocal 의 Instance 를 생성한다.
     */
    public static MyThreadLocal getInstance() {
        return MyThreadLocal.Holder.INSTANCE;
    }

    /**
     * ThreadLocal에 Tracking Log 를 생성할 수 있는 환경을 구성한다.
     */
    public static void createTracking() {
        String threadId = "ThreadId-" + Thread.currentThread().getId();
        MDC.put( WebConst.THREAD_ID, threadId );
        // ThreadLocal 을 초기화 한다.
        MyThreadLocal.clearContext();
        MyThreadLocal.setContext( WebConst.THREAD_ID, threadId );

        log.debug( "Thread ID 를 Log에 사용하기 시작합니다." );
    }

    /**
     * ThreadLocal에 Context 를 생성한다.
     * @param key Context ID
     * @param value Context Value
     */
    public static void setContext(String key, Object value) {
        // 데이터 설정
        getInstance().setAttribute(key, value);
    }

    /**
     * ThreadLocal에 Context 를 리턴한다.
     * @param key Context ID
     */
    public static Object getContext(String key) {
        return getInstance().getAttribute(key);
    }

    /**
     * ThreadLocal에 Tracking 로그를 셋팅한다.
     * @param path Tracking을 위한 Path
     */
    public static void setTrackingLog(String path) {
        // 현재 스레드의 데이터를 가져오거나 생성
        List<String> trackingLog = getTrackingList();
        trackingLog.add(path);
        // 데이터 설정
        threadLocal.get().put(WebConst.TRACKING_LOGGER, trackingLog);
    }

    /**
     * ThreadLocal에 Tracking 중 사용자의 임의 로그를 셋팅한다.
     * @param logger Tracking 중간에 삽입될 임의 로그
     */
    public static void setDevTrackingLog(String logger) {
        // 현재 스레드의 데이터를 가져오거나 생성
        List<String> trackingLog = getTrackingList();
        trackingLog.add("[개발자 로그] >> " + logger);
        // 데이터 설정
        threadLocal.get().put(WebConst.TRACKING_LOGGER, trackingLog);
    }

    /**
     * ThreadLocal에 저장된 Tracking 로그를 리턴한다.
     */
    public static List<String> getTrackingList() {
        // 현재 스레드의 데이터를 가져오거나 생성
        HashMap<String, Object> threadData = threadLocal.get();

        List<String> trackingLog = (List<String>) threadData.get(WebConst.TRACKING_LOGGER);
        if (trackingLog == null) {
            trackingLog = new java.util.ArrayList<>();
        }

        return trackingLog;
    }

    /**
     * ThreadLocal에 Tracking 로그를 초기화 한다.
     */
    public static void clearContext() {
        log.debug("ThreadLocal 을 초기화 합니다.");
        threadLocal.remove();
    }

    /**
     * ThreadLocal에 Tracking 로그를 출력 한다.
     */
    public static void printStackLog() {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("\n\n********************** [ " + getContext(WebConst.THREAD_ID) + " ] Tracking Logging 시작 **********************\n\n");
        List<String> trackingList = MyThreadLocal.getTrackingList();
        for (String element : trackingList) {
            logBuilder.append(element + "\n");
        }
        logBuilder.append("\n********************** [ " + getContext(WebConst.THREAD_ID) + " ] Tracking Logging 종료 **********************\n\n");
        log.debug(logBuilder.toString() + "\n");
    }

    private void setAttribute(String key, Object value) {
        threadLocal.get().put(key, value);
    }

    private Object getAttribute(String key) {
        return threadLocal.get().get(key);
    }

    private void removeAttribute(String key) {
        threadLocal.get().remove(key);
    }

    // 실제 싱글톤 인스턴스를 보유하는 정적 내부 클래스
    private static class Holder {
        private static final MyThreadLocal INSTANCE = new MyThreadLocal();
    }
}
