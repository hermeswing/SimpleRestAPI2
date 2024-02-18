package octopus.base.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {
    public static String getDateFormatBeforV8(Date date) {
        return getDateFormatBeforV8(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * <pre>
     *     Date Type 날짜 Format 으로 변경
     *     JDK 8 버전 이하에서 사용.
     * </pre>
     * @param currentDate
     * @return
     */
    public static String getDateFormatBeforV8(Date currentDate, String format) {
        // SimpleDateFormat을 사용하여 날짜 형식화
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(currentDate);
    }

    public static String getDateFormat(Date date) {
        // java.util.Date를 java.time.LocalDateTime으로 변환
        LocalDateTime dateTime = date.toInstant().atZone( ZoneId.systemDefault()).toLocalDateTime();

        return getDateFormat(dateTime, "yyyy-MM-dd HH:mm:ss");
    }

    public static String getDateFormat(LocalDateTime dateTime) {
        return getDateFormat(dateTime, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * <pre>
     *     Date Type 날짜 Format 으로 변경
     * </pre>
     * @param dateTime LocalDateTime
     * @param dateFormat 날짜 Format
     * @return
     */
    public static String getDateFormat(LocalDateTime dateTime, String dateFormat) {
        // DateTimeFormatter를 사용하여 날짜와 시간 형식화
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        return dateTime.format(formatter);
    }

}
