package com.date.parser.regexp;

import org.junit.Test;

/**
 * @author sulin
 * @since 2019-08-07 22:11:19
 */
public class PatternTest {

    @Test
    public void test() {
        Pattern ptn = Pattern.compile("^\\d+(\\D\\w+)$");
        System.out.println(ptn);
        Pattern.printObjectTree(ptn.root);

        System.out.println(ptn.matcher("").matches());
        System.out.println(ptn.matcher("123afsdfsdsdf").matches());
    }

}