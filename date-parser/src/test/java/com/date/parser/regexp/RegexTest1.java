package com.date.parser.regexp;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author sulin
 * @since 2019-08-09 10:45:29
 */
@Slf4j
public class RegexTest1 {

    private static void testCase(String regex, String text) {
        testCase(regex, text, true);
    }

    private static void testCase(String regex, String text, boolean isMatch) {
        assert Pattern.matches(regex, text) == isMatch;
    }

    @Test
    public void testSingleAtom() {
        testCase("a", "a");
        testCase("a", "b", false);
        testCase("b", "b");
    }

    @Test
    public void testZeroOrMore() {
        testCase("a*", "");
        testCase("a*", "a");
        testCase("a*", "aaaaaa");
        testCase("a*", "aaaabaaa", false);
    }

    @Test
    public void testOneOrMore() {
        testCase("a+", "", false);
        testCase("a+", "a");
        testCase("a+", "aaaaaaaaa");
        testCase("a+", "aaaabaaa", false);
    }

    @Test
    public void testZeroOrOne() {
        testCase("a?", "");
        testCase("a?", "a");
        testCase("a?", "aa", false);
        testCase("a?", "ba", false);
    }

    @Test
    public void testEscapeAtom() {
        testCase("\\n", "\n");
        testCase("\\n", "n", false);
        testCase("\\t", "\t");
        testCase("\\t", "t", false);
        testCase("\\(", "(");
        testCase("\\)", ")");
        testCase("\\*", "*");
        testCase("\\+", "+");
        testCase("\\?", "?");
        testCase("\\\\", "\\");
    }

    @Test
    public void testConcatenation() {
        testCase("abc", "abc");
        testCase("abc", "acb", false);
        testCase("a\\nb", "a\nb");
        testCase("abcdef", "abcde", false);
        testCase("abcdef", "abcdefg", false);
    }

    @Test
    public void testNestedExpressions() {
        testCase("ab(cd)e", "abcde");
        // with star
        testCase("a(bc)*d", "ad");
        testCase("a(bc)*d", "abcd");
        testCase("a(bc)*d", "abcbcbcd");
        testCase("a(bc)*d", "abcbcbd", false);
        // with plus
        testCase("a(bc)+d", "ad", false);
        testCase("a(bc)+d", "abcd");
        testCase("a(bc)+d", "abcbcbcd");
        testCase("a(bc)+d", "abcbcbd", false);
        // with optional
        testCase("a(bc)?d", "ad");
        testCase("a(bc)?d", "abcd");
        testCase("a(bc)?d", "abcbcd", false);
    }

    @Test
    public void testWorstCasePerformance() {
        String regex = "";
        String input = "";
        for (int n = 5; n <= 10; n += 5) {
            log.info("start {}", n);
            regex = "a?a?a?a?a?a?a?a?a?a?" + regex + "aaaaaaaaaa";
            input += "aaaaaaaaaa";
            testCase(regex, input);
            testCase(regex, input + input);
            testCase(regex, input + input + "a", false);
            log.info("done {}", n);
        }
    }

}
