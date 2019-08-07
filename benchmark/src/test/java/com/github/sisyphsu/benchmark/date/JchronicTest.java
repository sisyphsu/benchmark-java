package com.github.sisyphsu.benchmark.date;

import com.mdimension.jchronic.Chronic;
import com.mdimension.jchronic.utils.Span;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * @author sulin
 * @since 2019-08-07 17:13:04
 */
@Slf4j
public class JchronicTest {

    @Test
    public void testDate() {
        Date date = new Date();
        String fmts[] = {
                "yyyy/MM/dd HH:mm:ss",
                "yyyy年M月d日 HH:mm:ss",
                "yyyy年M月d日 H时m分s秒",
                "yyyy년M월d일 HH:mm:ss",
                "MM/dd/yyyy HH:mm:ss",
                "dd/MM/yyyy HH:mm:ss",
                "dd.MM.yyyy HH:mm:ss",
                "dd-MM-yyyy HH:mm:ss",
                "yyyyMMdd",
                "yyyy/MM/dd",
                "yyyy年M月d日",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS"
        };

        for (String fmt : fmts) {
            String dateStr = DateFormatUtils.format(date, fmt);

            Span span = Chronic.parse(dateStr);
            if (span == null) {
                System.out.println("err: " + fmt);
                continue;
            }
            Calendar calendar = span.getBeginCalendar();
            if (calendar == null) {
                System.out.println("err: " + fmt);
                continue;
            }
            Date newDate = calendar.getTime();
            log.info("{}: {} \t => {}", fmt, dateStr, newDate);
//            assert newDate.getYear() == date.getYear();
//            assert newDate.getMonth() == date.getMonth();
//            assert newDate.getDay() == date.getDay();

//            System.out.println("success: " + fmt);
        }

    }

}
