package com.date.parser.regexp;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author sulin
 * @since 2019-08-07 22:11:19
 */
@Slf4j
public class PatternTest {

    @Test
    public void test() {
        Pattern ptn = Pattern.compile("^\\d+(\\D\\w+)$");
        System.out.println(ptn);

        System.out.println(ptn.matcher("").matches());
        System.out.println(ptn.matcher("123a1fsdfsdsdf").matches());

        System.out.println(Pattern.matches("\\d+", "123456"));
    }

    @Test
    public void testNormal() {
        String[][] matches = {
                {
                        "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?",
                        "zhangsan@sina.com",
                        "zhangsan@xxx.com.cn"
                },
                {
                        "[1-9]\\d{16}[a-zA-Z0-9]{1}",
                        "110110199001183311"
                },
                {
                        "(\\+\\d+)?1[3458]\\d{9}$",
                        "+8613533333333", "13533333333"
                },
                {
                        "(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$",
                        "+8602085588447", "02085588447"
                },
                {
                        "\\-?[1-9]\\d+",
                        "129312", "-1235253400003243"
                },
                {
                        "\\-?[1-9]\\d+(\\.\\d+)?",
                        "1231414", "-1231414",
                        "123.54324", "-123.54324"
                },
                {
                        "\\s+",
                        " ", "\t", "\n", "\r", "\f", " \t\n\r\f"
                },
                {
                        "^[\u4E00-\u9FA5]+$",
                        "哈喽", "当当"
                },
                {
                        "[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}",
                        "1992-09-03", "1992.09.03"
                },
                {
                        "(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?",
                        "http://blog.csdn.net:80/xyang81/article/details/7705960?",
                        "http://www.csdn.net:80"
                },
                {
                        "[1-9]\\d{5}",
                        "471300"
                },
                {
                        "[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))",
                        "192.168.1.1", "127.0.0.1"
                }
        };
        for (String[] match : matches) {
            String ptn = match[0];
            for (int i = 1; i < match.length; i++) {
                if (!Pattern.matches(ptn, match[i])) {
                    log.error("ERR: {} -> {}", ptn, match[i]);
                }
            }
        }
    }

}