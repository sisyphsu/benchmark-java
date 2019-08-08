package com.date.parser.regexp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class Pattern {

    /*
     * Regular expression modifier values.  Instead of being passed as
     * arguments, they can also be passed as inline modifiers.
     * For example, the following statements have the same effect.
     * <pre>
     * RegExp r1 = RegExp.compile("abc", Pattern.I|Pattern.M);
     * RegExp r2 = RegExp.compile("(?im)abc", 0);
     * </pre>
     *
     * The flags are duplicated so that the familiar Perl match flag names are available.
     */

    /**
     * 换行符采用Unix标准即'\n'，换行符只会在[.^$]的模式下被识别并匹配，此模式也可以通过内嵌表达式`(?d)`来启用
     */
    public static final int UNIX_LINES = 0x01;

    /**
     * 启用dotall模式，此模式下，表达式`.`会匹配任何字符包括换行符，而默认情况下它不会匹配到换行符。
     * <p>
     * 可以通过内嵌表达式`(?s)`启用，s等价于single-line的缩写。
     */
    public static final int DOTALL = 0x20;

    private String pattern;
    private int flags;
    /**
     * 规范化的模板字符串，用于支持特殊的模式，一般情况下与pattern相同
     */
    private transient String normalizedPattern;
    /**
     * 标明当前模板是否已编译，用于懒加载的case
     */
    private transient volatile boolean compiled = false;
    /**
     * 匹配操作状态机的起点
     */
    transient Node root;
    /**
     * The root of object tree for a match operation.
     * The pattern is matched at the beginning.
     * This may include a find that uses BnM or a First node.
     */
    transient Node matchRoot;
    /**
     * 解析Pattern片段时使用的临时存储
     */
    transient int[] buffer;
    /**
     * 映射groupName与它的位置，用于组的捕捉
     */
    transient volatile Map<String, Integer> namedGroups;
    /**
     * 解析Group时的临时存储
     */
    transient GroupHead[] groupNodes;
    /**
     * Temporary null terminated code point array used by pattern compiling.
     */
    private transient int[] temp;
    /**
     * The number of capturing groups in this Pattern. Used by matchers to
     * allocate storage needed to perform a match.
     */
    transient int capturingGroupCount;
    /**
     * The local variable count used by parsing tree. Used by matchers to allocate storage needed to perform a match.
     */
    transient int localCount;
    /**
     * Index into the pattern string that keeps track of how much has been parsed.
     */
    private transient int cursor;
    /**
     * Holds the length of the pattern string.
     */
    private transient int patternLength;
    /**
     * If the Start node might possibly match supplementary characters.
     * It is set to true during compiling if
     * (1) There is supplementary char in pattern, or
     * (2) There is complement node of Category or Block
     */
    private transient boolean hasSupplementary;

    /**
     * 采用默认的模式编译给定的正则表达式，并生成Pattern实例。
     *
     * @param regex The expression to be compiled
     * @return the given regular expression compiled into a pattern
     * @throws PatternSyntaxException If the expression's syntax is invalid
     */
    public static Pattern compile(String regex) {
        return new Pattern(regex, 0);
    }

    public String toString() {
        return pattern;
    }

    /**
     * 根据给定的字符串创建Matcher
     *
     * @param input The character sequence to be matched
     * @return A new matcher for this pattern
     */
    public Matcher matcher(CharSequence input) {
        if (!compiled) {
            synchronized (this) {
                if (!compiled)
                    compile();
            }
        }
        return new Matcher(this, input);
    }

    /**
     * 编译给定的正则表达式并针对指定输入字符串进行正则匹配，返回匹配结果。
     *
     * @param regex The expression to be compiled
     * @param input The character sequence to be matched
     * @return whether or not the regular expression matches on the input
     * @throws PatternSyntaxException If the expression's syntax is invalid
     */
    public static boolean matches(String regex, CharSequence input) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        return m.matches();
    }

    /**
     * 用于创建Pattern的私有构造方法。
     */
    private Pattern(String p, int f) {
        pattern = p;
        flags = f;

        // Reset group index count
        capturingGroupCount = 1;
        localCount = 0;

        if (pattern.length() > 0) {
            compile();
        } else {
            root = new Start(lastAccept);
            matchRoot = lastAccept;
        }
    }

    /**
     * Preprocess any \Q...\E sequences in `temp', meta-quoting them.
     * See the description of `quotemeta' in perlfunc(1).
     */
    private void RemoveQEQuoting() {
        final int pLen = patternLength;
        int i = 0;
        while (i < pLen - 1) {
            if (temp[i] != '\\')
                i += 1;
            else if (temp[i + 1] != 'Q')
                i += 2;
            else
                break;
        }
        if (i >= pLen - 1)    // No \Q sequence found
            return;
        int j = i;
        i += 2;
        int[] newtemp = new int[j + 3 * (pLen - i) + 2];
        System.arraycopy(temp, 0, newtemp, 0, j);

        boolean inQuote = true;
        boolean beginQuote = true;
        while (i < pLen) {
            int c = temp[i++];
            if (!ASCII.isAscii(c) || ASCII.isAlpha(c)) {
                newtemp[j++] = c;
            } else if (ASCII.isDigit(c)) {
                if (beginQuote) {
                    /*
                     * A unicode escape \[0xu] could be before this quote,
                     * and we don't want this numeric char to processed as
                     * part of the escape.
                     */
                    newtemp[j++] = '\\';
                    newtemp[j++] = 'x';
                    newtemp[j++] = '3';
                }
                newtemp[j++] = c;
            } else if (c != '\\') {
                if (inQuote) newtemp[j++] = '\\';
                newtemp[j++] = c;
            } else if (inQuote) {
                if (temp[i] == 'E') {
                    i++;
                    inQuote = false;
                } else {
                    newtemp[j++] = '\\';
                    newtemp[j++] = '\\';
                }
            } else {
                if (temp[i] == 'Q') {
                    i++;
                    inQuote = true;
                    beginQuote = true;
                    continue;
                } else {
                    newtemp[j++] = c;
                    if (i != pLen)
                        newtemp[j++] = temp[i++];
                }
            }

            beginQuote = false;
        }

        patternLength = j;
        temp = Arrays.copyOf(newtemp, j + 2); // double zero termination
    }

    /**
     * Copies regular expression to an int array and invokes the parsing
     * of the expression which will create the object tree.
     */
    private void compile() {
        normalizedPattern = pattern;
        patternLength = normalizedPattern.length();

        // Copy pattern to int array for convenience
        // Use double zero to terminate pattern
        temp = new int[patternLength + 2];

        hasSupplementary = false;
        int c, count = 0;
        // Convert all chars into code points
        for (int x = 0; x < patternLength; x += Character.charCount(c)) {
            c = normalizedPattern.codePointAt(x);
            if (isSupplementary(c)) {
                hasSupplementary = true;
            }
            temp[count++] = c;
        }

        patternLength = count;   // patternLength now in code points

        RemoveQEQuoting();

        // Allocate all temporary objects here.
        buffer = new int[32];
        groupNodes = new GroupHead[10];
        namedGroups = null;

        // Start recursive descent parsing
        matchRoot = expr(lastAccept);
        // Check extra pattern characters
        if (patternLength != cursor) {
            if (peek() == ')') {
                throw error("Unmatched closing ')'");
            } else {
                throw error("Unexpected internal error");
            }
        }

        // Peephole optimization
        if (matchRoot instanceof Slice) {
            root = BnM.optimize(matchRoot);
            if (root == matchRoot) {
                root = hasSupplementary ? new StartS(matchRoot) : new Start(matchRoot);
            }
        } else if (matchRoot instanceof Begin || matchRoot instanceof First) {
            root = matchRoot;
        } else {
            root = hasSupplementary ? new StartS(matchRoot) : new Start(matchRoot);
        }

        // Release temporary storage
        temp = null;
        buffer = null;
        groupNodes = null;
        patternLength = 0;
        compiled = true;
    }

    Map<String, Integer> namedGroups() {
        if (namedGroups == null)
            namedGroups = new HashMap<>(2);
        return namedGroups;
    }

    /**
     * Used to print out a subtree of the Pattern to help with debugging.
     */
    public static void printObjectTree(Node node) {
        while (node != null) {
            if (node instanceof Prolog) {
                System.out.println(node);
                printObjectTree(((Prolog) node).loop);
                System.out.println("**** end contents prolog loop");
            } else if (node instanceof Loop) {
                System.out.println(node);
                printObjectTree(((Loop) node).body);
                System.out.println("**** end contents Loop body");
            } else if (node instanceof Curly) {
                System.out.println(node);
                printObjectTree(((Curly) node).atom);
                System.out.println("**** end contents Curly body");
            } else if (node instanceof GroupCurly) {
                System.out.println(node);
                printObjectTree(((GroupCurly) node).atom);
                System.out.println("**** end contents GroupCurly body");
            } else if (node instanceof GroupTail) {
                System.out.println(node);
                System.out.println("Tail next is " + node.next);
                return;
            } else {
                System.out.println(node);
            }
            node = node.next;
            if (node != null)
                System.out.println("->next:");
            if (node == Pattern.accept) {
                System.out.println("Accept Node");
                node = null;
            }
        }
    }

    /**
     * Used to accumulate information about a subtree of the object graph so that optimizations can be applied to the subtree.
     */
    static final class TreeInfo {
        int minLength;
        int maxLength;
        boolean maxValid;
        boolean deterministic;

        TreeInfo() {
            reset();
        }

        void reset() {
            minLength = 0;
            maxLength = 0;
            maxValid = true;
            deterministic = true;
        }
    }

    /*
     * The following private methods are mainly used to improve the
     * readability of the code. In order to let the Java compiler easily
     * inline them, we should not put many assertions or error checks in them.
     */

    /**
     * Indicates whether a particular flag is set or not.
     */
    private boolean has(int f) {
        return (flags & f) != 0;
    }

    /**
     * Match next character, signal error if failed.
     */
    private void accept(int ch, String s) {
        int testChar = temp[cursor++];
        if (ch != testChar) {
            throw error(s);
        }
    }

    /**
     * Mark the end of pattern with a specific character.
     */
    private void mark(int c) {
        temp[patternLength] = c;
    }

    /**
     * Peek the next character, and do not advance the cursor.
     */
    private int peek() {
        return temp[cursor];
    }

    /**
     * Read the next character, and advance the cursor by one.
     */
    private int read() {
        return temp[cursor++];
    }

    /**
     * Advance the cursor by one, and peek the next character.
     */
    private int next() {
        return temp[++cursor];
    }

    /**
     * Advance the cursor by one, and peek the next character,
     * ignoring the COMMENTS setting
     */
    private int nextEscaped() {
        return temp[++cursor];
    }

    /**
     * Read the character after the next one, and advance the cursor by two.
     */
    private int skip() {
        int i = cursor;
        int ch = temp[i + 1];
        cursor = i + 2;
        return ch;
    }

    /**
     * Unread one next character, and retreat cursor by one.
     */
    private void unread() {
        cursor--;
    }

    /**
     * Internal method used for handling all syntax errors. The pattern is
     * displayed with a pointer to aid in locating the syntax error.
     */
    private PatternSyntaxException error(String s) {
        return new PatternSyntaxException(s, normalizedPattern, cursor - 1);
    }

    /**
     * Determines if there is any supplementary character or unpaired
     * surrogate in the specified range.
     */
    private boolean findSupplementary(int start, int end) {
        for (int i = start; i < end; i++) {
            if (isSupplementary(temp[i]))
                return true;
        }
        return false;
    }

    /**
     * Determines if the specified code point is a supplementary
     * character or unpaired surrogate.
     */
    private static boolean isSupplementary(int ch) {
        return ch >= Character.MIN_SUPPLEMENTARY_CODE_POINT || Character.isSurrogate((char) ch);
    }

    /*
     *  The following methods handle the main parsing. They are sorted
     *  according to their precedence order, the lowest one first.
     */

    /**
     * The expression is parsed with branch nodes added for alternations.
     * This may be called recursively to parse sub expressions that may
     * contain alternations.
     */
    private Node expr(Node end) {
        Node prev = null;
        Node firstTail = null;
        Branch branch = null;
        Node branchConn = null;

        for (; ; ) {
            Node node = sequence(end);
            Node nodeTail = root;      //double return
            if (prev == null) {
                prev = node;
                firstTail = nodeTail;
            } else {
                // Branch
                if (branchConn == null) {
                    branchConn = new BranchConn();
                    branchConn.next = end;
                }
                if (node == end) {
                    // if the node returned from sequence() is "end"
                    // we have an empty expr, set a null atom into
                    // the branch to indicate to go "next" directly.
                    node = null;
                } else {
                    // the "tail.next" of each atom goes to branchConn
                    nodeTail.next = branchConn;
                }
                if (prev == branch) {
                    branch.add(node);
                } else {
                    if (prev == end) {
                        prev = null;
                    } else {
                        // replace the "end" with "branchConn" at its tail.next
                        // when put the "prev" into the branch as the first atom.
                        firstTail.next = branchConn;
                    }
                    prev = branch = new Branch(prev, node, branchConn);
                }
            }
            if (peek() != '|') {
                return prev;
            }
            next();
        }
    }

    /**
     * Parsing of sequences between alternations.
     */
    private Node sequence(Node end) {
        Node head = null;
        Node tail = null;
        Node node;
        LOOP:
        for (; ; ) {
            int ch = peek();
            switch (ch) {
                case '(':
                    // Because group handles its own closure,
                    // we need to treat it differently
                    node = group0();
                    // Check for comment or flag group
                    if (node == null)
                        continue;
                    if (head == null)
                        head = node;
                    else
                        tail.next = node;
                    // Double return: Tail was returned in root
                    tail = root;
                    continue;
                case '[':
                    node = clazz(true);
                    break;
                case '\\':
                    ch = nextEscaped();
                    if (ch == 'p' || ch == 'P') {
                        boolean oneLetter = true;
                        boolean comp = (ch == 'P');
                        ch = next(); // Consume { if present
                        if (ch != '{') {
                            unread();
                        } else {
                            oneLetter = false;
                        }
                        node = family(oneLetter, comp);
                    } else {
                        unread();
                        node = atom();
                    }
                    break;
                case '^':
                    next();
                    node = new Begin();
                    break;
                case '$':
                    next();
                    if (has(UNIX_LINES))
                        node = new UnixDollar(false);
                    else
                        node = new Dollar(false);
                    break;
                case '.':
                    next();
                    if (has(DOTALL)) {
                        node = new All();
                    } else {
                        if (has(UNIX_LINES))
                            node = new UnixDot();
                        else {
                            node = new Dot();
                        }
                    }
                    break;
                case '|':
                case ')':
                    break LOOP;
                case ']': // Now interpreting dangling ] and } as literals
                case '}':
                    node = atom();
                    break;
                case '?':
                case '*':
                case '+':
                    next();
                    throw error("Dangling meta character '" + ((char) ch) + "'");
                case 0:
                    if (cursor >= patternLength) {
                        break LOOP;
                    }
                    // Fall through
                default:
                    node = atom();
                    break;
            }

            node = closure(node);

            if (head == null) {
                head = tail = node;
            } else {
                tail.next = node;
                tail = node;
            }
        }
        if (head == null) {
            return end;
        }
        tail.next = end;
        root = tail;      //double return
        return head;
    }

    /**
     * Parse and add a new Single or Slice.
     */
    private Node atom() {
        int first = 0;
        int prev = -1;
        boolean hasSupplementary = false;
        int ch = peek();
        for (; ; ) {
            switch (ch) {
                case '*':
                case '+':
                case '?':
                case '{':
                    if (first > 1) {
                        cursor = prev;    // Unwind one character
                        first--;
                    }
                    break;
                case '$':
                case '.':
                case '^':
                case '(':
                case '[':
                case '|':
                case ')':
                    break;
                case '\\':
                    ch = nextEscaped();
                    if (ch == 'p' || ch == 'P') { // Property
                        if (first > 0) { // Slice is waiting; handle it first
                            unread();
                            break;
                        } else { // No slice; just return the family node
                            boolean comp = (ch == 'P');
                            boolean oneLetter = true;
                            ch = next(); // Consume { if present
                            if (ch != '{')
                                unread();
                            else
                                oneLetter = false;
                            return family(oneLetter, comp);
                        }
                    }
                    unread();
                    prev = cursor;
                    ch = escape(false, first == 0, false);
                    if (ch >= 0) {
                        append(ch, first);
                        first++;
                        if (isSupplementary(ch)) {
                            hasSupplementary = true;
                        }
                        ch = peek();
                        continue;
                    } else if (first == 0) {
                        return root;
                    }
                    // Unwind meta escape sequence
                    cursor = prev;
                    break;
                case 0:
                    if (cursor >= patternLength) {
                        break;
                    }
                    // Fall through
                default:
                    prev = cursor;
                    append(ch, first);
                    first++;
                    if (isSupplementary(ch)) {
                        hasSupplementary = true;
                    }
                    ch = next();
                    continue;
            }
            break;
        }
        if (first == 1) {
            return newSingle(buffer[0]);
        } else {
            return newSlice(buffer, first, hasSupplementary);
        }
    }

    private void append(int ch, int len) {
        if (len >= buffer.length) {
            int[] tmp = new int[len + len];
            System.arraycopy(buffer, 0, tmp, 0, len);
            buffer = tmp;
        }
        buffer[len] = ch;
    }

    /**
     * Parses a backref greedily, taking as many numbers as it
     * can. The first digit is always treated as a backref, but
     * multi digit numbers are only treated as a backref if at
     * least that many backrefs exist at this point in the regex.
     */
    private Node ref(int refNum) {
        boolean done = false;
        while (!done) {
            int ch = peek();
            switch (ch) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    int newRefNum = (refNum * 10) + (ch - '0');
                    // Add another number if it doesn't make a group
                    // that doesn't exist
                    if (capturingGroupCount - 1 < newRefNum) {
                        done = true;
                        break;
                    }
                    refNum = newRefNum;
                    read();
                    break;
                default:
                    done = true;
                    break;
            }
        }
        return new BackRef(refNum);
    }

    /**
     * Parses an escape sequence to determine the actual value that needs
     * to be matched.
     * If -1 is returned and create was true a new object was added to the tree
     * to handle the escape sequence.
     * If the returned value is greater than zero, it is the value that
     * matches the escape sequence.
     */
    private int escape(boolean inclass, boolean create, boolean isrange) {
        int ch = skip();
        switch (ch) {
            case '0':
                return o();
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                if (inclass) break;
                if (create) {
                    root = ref((ch - '0'));
                }
                return -1;
            case 'A':
                if (inclass) break;
                if (create) root = new Begin();
                return -1;
            case 'B':
                if (inclass) break;
                if (create) root = new Bound(Bound.NONE, false);
                return -1;
            case 'C':
                break;
            case 'D':
                if (create) root = new Ctype(ASCII.DIGIT).complement();
                return -1;
            case 'E':
            case 'F':
                break;
            case 'G':
                if (inclass) break;
                if (create) root = new LastMatch();
                return -1;
            case 'H':
                if (create) root = new HorizWS().complement();
                return -1;
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
                break;
            case 'R':
                if (inclass) break;
                if (create) root = new LineEnding();
                return -1;
            case 'S':
                if (create) root = new Ctype(ASCII.SPACE).complement();
                return -1;
            case 'T':
            case 'U':
                break;
            case 'V':
                if (create) root = new VertWS().complement();
                return -1;
            case 'W':
                if (create) root = new Ctype(ASCII.WORD).complement();
                return -1;
            case 'X':
            case 'Y':
                break;
            case 'Z':
                if (inclass) break;
                if (create) {
                    if (has(UNIX_LINES))
                        root = new UnixDollar(false);
                    else
                        root = new Dollar(false);
                }
                return -1;
            case 'a':
                return '\007';
            case 'b':
                if (inclass) break;
                if (create) root = new Bound(Bound.BOTH, false);
                return -1;
            case 'c':
                return c();
            case 'd':
                if (create) root = new Ctype(ASCII.DIGIT);
                return -1;
            case 'e':
                return '\033';
            case 'f':
                return '\f';
            case 'g':
                break;
            case 'h':
                if (create) root = new HorizWS();
                return -1;
            case 'i':
            case 'j':
                break;
            case 'k':
                if (inclass)
                    break;
                if (read() != '<')
                    throw error("\\k is not followed by '<' for named capturing group");
                String name = groupname(read());
                if (!namedGroups().containsKey(name))
                    throw error("(named capturing group <" + name + "> does not exit");
                if (create) {
                    root = new BackRef(namedGroups().get(name));
                }
                return -1;
            case 'l':
            case 'm':
                break;
            case 'n':
                return '\n';
            case 'o':
            case 'p':
            case 'q':
                break;
            case 'r':
                return '\r';
            case 's':
                if (create) root = new Ctype(ASCII.SPACE);
                return -1;
            case 't':
                return '\t';
            case 'u':
                return u();
            case 'v':
                // '\v' was implemented as VT/0x0B in releases < 1.8 (though
                // undocumented). In JDK8 '\v' is specified as a predefined
                // character class for all vertical whitespace characters.
                // So [-1, root=VertWS node] pair is returned (instead of a
                // single 0x0B). This breaks the range if '\v' is used as
                // the start or end value, such as [\v-...] or [...-\v], in
                // which a single definite value (0x0B) is expected. For
                // compatibility concern '\013'/0x0B is returned if isrange.
                if (isrange)
                    return '\013';
                if (create) root = new VertWS();
                return -1;
            case 'w':
                if (create) root = new Ctype(ASCII.WORD);
                return -1;
            case 'x':
                return x();
            case 'y':
                break;
            case 'z':
                if (inclass) break;
                if (create) root = new End();
                return -1;
            default:
                return ch;
        }
        throw error("Illegal/unsupported escape sequence");
    }

    /**
     * Parse a character class, and return the node that matches it.
     * <p>
     * Consumes a ] on the way out if consume is true. Usually consume
     * is true except for the case of [abc&&def] where def is a separate
     * right hand node with "understood" brackets.
     */
    private CharProperty clazz(boolean consume) {
        CharProperty prev = null;
        CharProperty node = null;
        BitClass bits = new BitClass();
        boolean include = true;
        boolean firstInClass = true;
        int ch = next();
        for (; ; ) {
            switch (ch) {
                case '^':
                    // Negates if first char in a class, otherwise literal
                    if (firstInClass) {
                        if (temp[cursor - 1] != '[')
                            break;
                        ch = next();
                        include = !include;
                        continue;
                    } else {
                        // ^ not first in class, treat as literal
                        break;
                    }
                case '[':
                    firstInClass = false;
                    node = clazz(true);
                    if (prev == null)
                        prev = node;
                    else
                        prev = union(prev, node);
                    ch = peek();
                    continue;
                case '&':
                    firstInClass = false;
                    ch = next();
                    if (ch == '&') {
                        ch = next();
                        CharProperty rightNode = null;
                        while (ch != ']' && ch != '&') {
                            if (ch == '[') {
                                if (rightNode == null)
                                    rightNode = clazz(true);
                                else
                                    rightNode = union(rightNode, clazz(true));
                            } else { // abc&&def
                                unread();
                                rightNode = clazz(false);
                            }
                            ch = peek();
                        }
                        if (rightNode != null)
                            node = rightNode;
                        if (prev == null) {
                            if (rightNode == null)
                                throw error("Bad class syntax");
                            else
                                prev = rightNode;
                        } else {
                            prev = intersection(prev, node);
                        }
                    } else {
                        // treat as a literal &
                        unread();
                        break;
                    }
                    continue;
                case 0:
                    firstInClass = false;
                    if (cursor >= patternLength)
                        throw error("Unclosed character class");
                    break;
                case ']':
                    firstInClass = false;
                    if (prev != null) {
                        if (consume)
                            next();
                        return prev;
                    }
                    break;
                default:
                    firstInClass = false;
                    break;
            }
            node = range(bits);
            if (include) {
                if (prev == null) {
                    prev = node;
                } else {
                    if (prev != node)
                        prev = union(prev, node);
                }
            } else {
                if (prev == null) {
                    prev = node.complement();
                } else {
                    if (prev != node)
                        prev = setDifference(prev, node);
                }
            }
            ch = peek();
        }
    }

    private CharProperty bitsOrSingle(BitClass bits, int ch) {
        /* Bits can only handle codepoints in [u+0000-u+00ff] range.
           Use "single" node instead of bits when dealing with unicode
           case folding for codepoints listed below.
           (1)Uppercase out of range: u+00ff, u+00b5
              toUpperCase(u+00ff) -> u+0178
              toUpperCase(u+00b5) -> u+039c
           (2)LatinSmallLetterLongS u+17f
              toUpperCase(u+017f) -> u+0053
           (3)LatinSmallLetterDotlessI u+131
              toUpperCase(u+0131) -> u+0049
           (4)LatinCapitalLetterIWithDotAbove u+0130
              toLowerCase(u+0130) -> u+0069
           (5)KelvinSign u+212a
              toLowerCase(u+212a) ==> u+006B
           (6)AngstromSign u+212b
              toLowerCase(u+212b) ==> u+00e5
        */
        if (ch < 256) {
            return bits.add(ch);
        }
        return newSingle(ch);
    }

    /**
     * Parse a single character or a character range in a character class
     * and return its representative node.
     */
    private CharProperty range(BitClass bits) {
        int ch = peek();
        if (ch == '\\') {
            ch = nextEscaped();
            if (ch == 'p' || ch == 'P') { // A property
                boolean comp = (ch == 'P');
                boolean oneLetter = true;
                // Consume { if present
                ch = next();
                if (ch != '{')
                    unread();
                else
                    oneLetter = false;
                return family(oneLetter, comp);
            } else { // ordinary escape
                boolean isrange = temp[cursor + 1] == '-';
                unread();
                ch = escape(true, true, isrange);
                if (ch == -1)
                    return (CharProperty) root;
            }
        } else {
            next();
        }
        if (ch >= 0) {
            if (peek() == '-') {
                int endRange = temp[cursor + 1];
                if (endRange == '[') {
                    return bitsOrSingle(bits, ch);
                }
                if (endRange != ']') {
                    next();
                    int m = peek();
                    if (m == '\\') {
                        m = escape(true, false, true);
                    } else {
                        next();
                    }
                    if (m < ch) {
                        throw error("Illegal character range");
                    }
                    return rangeFor(ch, m);
                }
            }
            return bitsOrSingle(bits, ch);
        }
        throw error("Unexpected character '" + ((char) ch) + "'");
    }

    /**
     * Parses a Unicode character family and returns its representative node.
     */
    private CharProperty family(boolean singleLetter, boolean maybeComplement) {
        next();
        String name;
        CharProperty node = null;

        if (singleLetter) {
            int c = temp[cursor];
            if (!Character.isSupplementaryCodePoint(c)) {
                name = String.valueOf((char) c);
            } else {
                name = new String(temp, cursor, 1);
            }
            read();
        } else {
            int i = cursor;
            mark('}');
            while (read() != '}') ;
            mark('\000');
            int j = cursor;
            if (j > patternLength)
                throw error("Unclosed character family");
            if (i + 1 >= j)
                throw error("Empty character family");
            name = new String(temp, i, j - i - 1);
        }

        int i = name.indexOf('=');
        if (i != -1) {
            // property construct \p{name=value}
            String value = name.substring(i + 1);
            name = name.substring(0, i).toLowerCase(Locale.ENGLISH);
            switch (name) {
                case "sc":
                case "script":
                    node = unicodeScriptPropertyFor(value);
                    break;
                case "blk":
                case "block":
                    node = unicodeBlockPropertyFor(value);
                    break;
                case "gc":
                case "general_category":
                    node = charPropertyNodeFor(value);
                    break;
                default:
                    throw error("Unknown Unicode property {name=<" + name + ">, value=<" + value + ">}");
            }
        } else {
            if (name.startsWith("In")) {
                // \p{inBlockName}
                node = unicodeBlockPropertyFor(name.substring(2));
            } else if (name.startsWith("Is")) {
                // \p{isGeneralCategory} and \p{isScriptName}
                name = name.substring(2);
                UnicodeProp uprop = UnicodeProp.forName(name);
                if (uprop != null)
                    node = new Utype(uprop);
                if (node == null)
                    node = CharPropertyNames.charPropertyFor(name);
                if (node == null)
                    node = unicodeScriptPropertyFor(name);
            } else {
                node = charPropertyNodeFor(name);
            }
        }
        if (maybeComplement) {
            if (node instanceof Category || node instanceof Block)
                hasSupplementary = true;
            node = node.complement();
        }
        return node;
    }


    /**
     * Returns a CharProperty matching all characters belong to
     * a UnicodeScript.
     */
    private CharProperty unicodeScriptPropertyFor(String name) {
        final Character.UnicodeScript script;
        try {
            script = Character.UnicodeScript.forName(name);
        } catch (IllegalArgumentException iae) {
            throw error("Unknown character script name {" + name + "}");
        }
        return new Script(script);
    }

    /**
     * Returns a CharProperty matching all characters in a UnicodeBlock.
     */
    private CharProperty unicodeBlockPropertyFor(String name) {
        final Character.UnicodeBlock block;
        try {
            block = Character.UnicodeBlock.forName(name);
        } catch (IllegalArgumentException iae) {
            throw error("Unknown character block name {" + name + "}");
        }
        return new Block(block);
    }

    /**
     * Returns a CharProperty matching all characters in a named property.
     */
    private CharProperty charPropertyNodeFor(String name) {
        CharProperty p = CharPropertyNames.charPropertyFor(name);
        if (p == null)
            throw error("Unknown character property name {" + name + "}");
        return p;
    }

    /**
     * Parses and returns the name of a "named capturing group", the trailing
     * ">" is consumed after parsing.
     */
    private String groupname(int ch) {
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toChars(ch));
        while (ASCII.isLower(ch = read()) || ASCII.isUpper(ch) || ASCII.isDigit(ch)) {
            sb.append(Character.toChars(ch));
        }
        if (sb.length() == 0)
            throw error("named capturing group has 0 length name");
        if (ch != '>')
            throw error("named capturing group is missing trailing '>'");
        return sb.toString();
    }

    /**
     * Parses a group and returns the head node of a set of nodes that process
     * the group. Sometimes a double return system is used where the tail is
     * returned in root.
     */
    private Node group0() {
        boolean capturingGroup = false;
        Node head;
        Node tail;
        int save = flags;
        root = null;
        int ch = next();
        if (ch == '?') {
            ch = skip();
            switch (ch) {
                case ':':   //  (?:xxx) pure group
                    head = createGroup(true);
                    tail = root;
                    head.next = expr(tail);
                    break;
                case '=':   // (?=xxx) and (?!xxx) lookahead
                case '!':
                    head = createGroup(true);
                    tail = root;
                    head.next = expr(tail);
                    if (ch == '=') {
                        head = tail = new Pos(head);
                    } else {
                        head = tail = new Neg(head);
                    }
                    break;
                case '>':   // (?>xxx)  independent group
                    head = createGroup(true);
                    tail = root;
                    head.next = expr(tail);
                    head = tail = new Ques(head, INDEPENDENT);
                    break;
                case '<':   // (?<xxx)  look behind
                    ch = read();
                    if (ASCII.isLower(ch) || ASCII.isUpper(ch)) {
                        // named captured group
                        String name = groupname(ch);
                        if (namedGroups().containsKey(name))
                            throw error("Named capturing group <" + name + "> is already defined");
                        capturingGroup = true;
                        head = createGroup(false);
                        tail = root;
                        namedGroups().put(name, capturingGroupCount - 1);
                        head.next = expr(tail);
                        break;
                    }
                    int start = cursor;
                    head = createGroup(true);
                    tail = root;
                    head.next = expr(tail);
                    tail.next = lookbehindEnd;
                    TreeInfo info = new TreeInfo();
                    head.study(info);
                    if (!info.maxValid) {
                        throw error("Look-behind group does not have an obvious maximum length");
                    }
                    boolean hasSupplementary = findSupplementary(start, patternLength);
                    if (ch == '=') {
                        head = tail = (hasSupplementary ?
                                new BehindS(head, info.maxLength, info.minLength) :
                                new Behind(head, info.maxLength, info.minLength));
                    } else if (ch == '!') {
                        head = tail = (hasSupplementary ? new NotBehindS(head, info.maxLength, info.minLength) :
                                new NotBehind(head, info.maxLength, info.minLength));
                    } else {
                        throw error("Unknown look-behind group");
                    }
                    break;
                case '$':
                case '@':
                    throw error("Unknown group type");
                default:    // (?xxx:) inlined match flags
                    unread();
                    addFlag();
                    ch = read();
                    if (ch == ')') {
                        return null;    // Inline modifier only
                    }
                    if (ch != ':') {
                        throw error("Unknown inline modifier");
                    }
                    head = createGroup(true);
                    tail = root;
                    head.next = expr(tail);
                    break;
            }
        } else { // (xxx) a regular group
            capturingGroup = true;
            head = createGroup(false);
            tail = root;
            head.next = expr(tail);
        }

        accept(')', "Unclosed group");
        flags = save;

        // Check for quantifiers
        Node node = closure(head);
        if (node == head) { // No closure
            root = tail;
            return node;    // Dual return
        }
        if (head == tail) { // Zero length assertion
            root = node;
            return node;    // Dual return
        }

        if (node instanceof Ques) {
            Ques ques = (Ques) node;
            if (ques.type == POSSESSIVE) {
                root = node;
                return node;
            }
            tail.next = new BranchConn();
            tail = tail.next;
            if (ques.type == GREEDY) {
                head = new Branch(head, null, tail);
            } else { // Reluctant quantifier
                head = new Branch(null, head, tail);
            }
            root = tail;
            return head;
        } else if (node instanceof Curly) {
            Curly curly = (Curly) node;
            if (curly.type == POSSESSIVE) {
                root = node;
                return node;
            }
            // Discover if the group is deterministic
            TreeInfo info = new TreeInfo();
            if (head.study(info)) { // Deterministic
                head = root = new GroupCurly(head.next, curly.cmin,
                        curly.cmax, curly.type,
                        ((GroupTail) tail).localIndex,
                        ((GroupTail) tail).groupIndex,
                        capturingGroup);
                return head;
            } else { // Non-deterministic
                int temp = ((GroupHead) head).localIndex;
                Loop loop;
                if (curly.type == GREEDY)
                    loop = new Loop(this.localCount, temp);
                else  // Reluctant Curly
                    loop = new LazyLoop(this.localCount, temp);
                Prolog prolog = new Prolog(loop);
                this.localCount += 1;
                loop.cmin = curly.cmin;
                loop.cmax = curly.cmax;
                loop.body = head;
                tail.next = loop;
                root = loop;
                return prolog; // Dual return
            }
        }
        throw error("Internal logic error");
    }

    /**
     * Create group head and tail nodes using double return. If the group is
     * created with anonymous true then it is a pure group and should not
     * affect group counting.
     */
    private Node createGroup(boolean anonymous) {
        int localIndex = localCount++;
        int groupIndex = 0;
        if (!anonymous)
            groupIndex = capturingGroupCount++;
        GroupHead head = new GroupHead(localIndex);
        root = new GroupTail(localIndex, groupIndex);
        if (!anonymous && groupIndex < 10)
            groupNodes[groupIndex] = head;
        return head;
    }

    /**
     * Parses inlined match flags and set them appropriately.
     */
    private void addFlag() {
        int ch = peek();
        for (; ; ) {
            switch (ch) {
                case 's':
                    flags |= DOTALL;
                    break;
                case 'd':
                    flags |= UNIX_LINES;
                    break;
                case '-': // subFlag then fall through
                    next();
                    subFlag();
                default:
                    return;
            }
            ch = next();
        }
    }

    /**
     * Parses the second part of inlined match flags and turns off
     * flags appropriately.
     */
    private void subFlag() {
        int ch = peek();
        for (; ; ) {
            switch (ch) {
                case 's':
                    flags &= ~DOTALL;
                    break;
                case 'd':
                    flags &= ~UNIX_LINES;
                    break;
                default:
                    return;
            }
            ch = next();
        }
    }

    static final int MAX_REPS = 0x7FFFFFFF;

    static final int GREEDY = 0;

    static final int LAZY = 1;

    static final int POSSESSIVE = 2;

    static final int INDEPENDENT = 3;

    /**
     * Processes repetition. If the next character peeked is a quantifier
     * then new nodes must be appended to handle the repetition.
     * Prev could be a single or a group, so it could be a chain of nodes.
     */
    private Node closure(Node prev) {
        int ch = peek();
        switch (ch) {
            case '?':
                ch = next();
                if (ch == '?') {
                    next();
                    return new Ques(prev, LAZY);
                } else if (ch == '+') {
                    next();
                    return new Ques(prev, POSSESSIVE);
                }
                return new Ques(prev, GREEDY);
            case '*':
                ch = next();
                if (ch == '?') {
                    next();
                    return new Curly(prev, 0, MAX_REPS, LAZY);
                } else if (ch == '+') {
                    next();
                    return new Curly(prev, 0, MAX_REPS, POSSESSIVE);
                }
                return new Curly(prev, 0, MAX_REPS, GREEDY);
            case '+':
                ch = next();
                if (ch == '?') {
                    next();
                    return new Curly(prev, 1, MAX_REPS, LAZY);
                } else if (ch == '+') {
                    next();
                    return new Curly(prev, 1, MAX_REPS, POSSESSIVE);
                }
                return new Curly(prev, 1, MAX_REPS, GREEDY);
            case '{':
                ch = temp[cursor + 1];
                if (ASCII.isDigit(ch)) {
                    skip();
                    int cmin = 0;
                    do {
                        cmin = cmin * 10 + (ch - '0');
                    } while (ASCII.isDigit(ch = read()));
                    int cmax = cmin;
                    if (ch == ',') {
                        ch = read();
                        cmax = MAX_REPS;
                        if (ch != '}') {
                            cmax = 0;
                            while (ASCII.isDigit(ch)) {
                                cmax = cmax * 10 + (ch - '0');
                                ch = read();
                            }
                        }
                    }
                    if (ch != '}')
                        throw error("Unclosed counted closure");
                    if (((cmin) | (cmax) | (cmax - cmin)) < 0)
                        throw error("Illegal repetition range");
                    Curly curly;
                    ch = peek();
                    if (ch == '?') {
                        next();
                        curly = new Curly(prev, cmin, cmax, LAZY);
                    } else if (ch == '+') {
                        next();
                        curly = new Curly(prev, cmin, cmax, POSSESSIVE);
                    } else {
                        curly = new Curly(prev, cmin, cmax, GREEDY);
                    }
                    return curly;
                } else {
                    throw error("Illegal repetition");
                }
            default:
                return prev;
        }
    }

    /**
     * Utility method for parsing control escape sequences.
     */
    private int c() {
        if (cursor < patternLength) {
            return read() ^ 64;
        }
        throw error("Illegal control escape sequence");
    }

    /**
     * Utility method for parsing octal escape sequences.
     */
    private int o() {
        int n = read();
        if (((n - '0') | ('7' - n)) >= 0) {
            int m = read();
            if (((m - '0') | ('7' - m)) >= 0) {
                int o = read();
                if ((((o - '0') | ('7' - o)) >= 0) && (((n - '0') | ('3' - n)) >= 0)) {
                    return (n - '0') * 64 + (m - '0') * 8 + (o - '0');
                }
                unread();
                return (n - '0') * 8 + (m - '0');
            }
            unread();
            return (n - '0');
        }
        throw error("Illegal octal escape sequence");
    }

    /**
     * Utility method for parsing hexadecimal escape sequences.
     */
    private int x() {
        int n = read();
        if (ASCII.isHexDigit(n)) {
            int m = read();
            if (ASCII.isHexDigit(m)) {
                return ASCII.toDigit(n) * 16 + ASCII.toDigit(m);
            }
        } else if (n == '{' && ASCII.isHexDigit(peek())) {
            int ch = 0;
            while (ASCII.isHexDigit(n = read())) {
                ch = (ch << 4) + ASCII.toDigit(n);
                if (ch > Character.MAX_CODE_POINT)
                    throw error("Hexadecimal codepoint is too big");
            }
            if (n != '}')
                throw error("Unclosed hexadecimal escape sequence");
            return ch;
        }
        throw error("Illegal hexadecimal escape sequence");
    }

    /**
     * Utility method for parsing unicode escape sequences.
     */
    private int cursor() {
        return cursor;
    }

    private void setcursor(int pos) {
        cursor = pos;
    }

    private int uxxxx() {
        int n = 0;
        for (int i = 0; i < 4; i++) {
            int ch = read();
            if (!ASCII.isHexDigit(ch)) {
                throw error("Illegal Unicode escape sequence");
            }
            n = n * 16 + ASCII.toDigit(ch);
        }
        return n;
    }

    private int u() {
        int n = uxxxx();
        if (Character.isHighSurrogate((char) n)) {
            int cur = cursor();
            if (read() == '\\' && read() == 'u') {
                int n2 = uxxxx();
                if (Character.isLowSurrogate((char) n2))
                    return Character.toCodePoint((char) n, (char) n2);
            }
            setcursor(cur);
        }
        return n;
    }

    //
    // Utility methods for code point support
    //

    private static int countChars(CharSequence seq, int index, int lengthInCodePoints) {
        // optimization
        if (lengthInCodePoints == 1 && !Character.isHighSurrogate(seq.charAt(index))) {
            assert (index >= 0 && index < seq.length());
            return 1;
        }
        int length = seq.length();
        int x = index;
        if (lengthInCodePoints >= 0) {
            assert (index >= 0 && index < length);
            for (int i = 0; x < length && i < lengthInCodePoints; i++) {
                if (Character.isHighSurrogate(seq.charAt(x++))) {
                    if (x < length && Character.isLowSurrogate(seq.charAt(x))) {
                        x++;
                    }
                }
            }
            return x - index;
        }

        assert (index >= 0 && index <= length);
        if (index == 0) {
            return 0;
        }
        int len = -lengthInCodePoints;
        for (int i = 0; x > 0 && i < len; i++) {
            if (Character.isLowSurrogate(seq.charAt(--x))) {
                if (x > 0 && Character.isHighSurrogate(seq.charAt(x - 1))) {
                    x--;
                }
            }
        }
        return index - x;
    }

    /**
     * Creates a bit vector for matching Latin-1 values. A normal BitClass
     * never matches values above Latin-1, and a complemented BitClass always
     * matches values above Latin-1.
     */
    private static final class BitClass extends BmpCharProperty {
        final boolean[] bits;

        BitClass() {
            bits = new boolean[256];
        }

        BitClass add(int c) {
            assert c >= 0 && c <= 255;
            bits[c] = true;
            return this;
        }

        boolean isSatisfiedBy(int ch) {
            return ch < 256 && bits[ch];
        }
    }

    /**
     * Returns a suitably optimized, single character matcher.
     */
    private CharProperty newSingle(final int ch) {
        if (isSupplementary(ch))
            return new SingleS(ch);    // Match a given Unicode character
        return new Single(ch);         // Match a given BMP character
    }

    /**
     * Utility method for creating a string slice matcher.
     */
    private Node newSlice(int[] buf, int count, boolean hasSupplementary) {
        int[] tmp = new int[count];
        System.arraycopy(buf, 0, tmp, 0, count);
        return hasSupplementary ? new SliceS(tmp) : new Slice(tmp);
    }

    /*
     * The following classes are the building components of the object
     * tree that represents a compiled regular expression. The object tree
     * is made of individual elements that handle constructs in the Pattern.
     * Each type of object knows how to match its equivalent construct with
     * the match() method.
     */

    /**
     * Base class for all node classes. Subclasses should override the match()
     * method as appropriate. This class is an accepting node, so its match()
     * always returns true.
     */
    static class Node {
        Node next;

        Node() {
            next = Pattern.accept;
        }

        /**
         * This method implements the classic accept node.
         */
        boolean match(Matcher matcher, int i, CharSequence seq) {
            matcher.last = i;
            matcher.groups[0] = matcher.first;
            matcher.groups[1] = matcher.last;
            return true;
        }

        /**
         * This method is good for all zero length assertions.
         */
        boolean study(TreeInfo info) {
            if (next != null) {
                return next.study(info);
            } else {
                return info.deterministic;
            }
        }
    }

    static class LastNode extends Node {
        /**
         * This method implements the classic accept node with
         * the addition of a check to see if the match occurred
         * using all of the input.
         */
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (matcher.acceptMode == Matcher.ENDANCHOR && i != matcher.to)
                return false;
            matcher.last = i;
            matcher.groups[0] = matcher.first;
            matcher.groups[1] = matcher.last;
            return true;
        }
    }

    /**
     * Used for REs that can start anywhere within the input string.
     * This basically tries to match repeatedly at each spot in the
     * input string, moving forward after each try. An anchored search
     * or a BnM will bypass this node completely.
     */
    static class Start extends Node {
        int minLength;

        Start(Node node) {
            this.next = node;
            TreeInfo info = new TreeInfo();
            next.study(info);
            minLength = info.minLength;
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i > matcher.to - minLength) {
                matcher.hitEnd = true;
                return false;
            }
            int guard = matcher.to - minLength;
            for (; i <= guard; i++) {
                if (next.match(matcher, i, seq)) {
                    matcher.first = i;
                    matcher.groups[0] = matcher.first;
                    matcher.groups[1] = matcher.last;
                    return true;
                }
            }
            matcher.hitEnd = true;
            return false;
        }

        boolean study(TreeInfo info) {
            next.study(info);
            info.maxValid = false;
            info.deterministic = false;
            return false;
        }
    }

    /*
     * StartS supports supplementary characters, including unpaired surrogates.
     */
    static final class StartS extends Start {
        StartS(Node node) {
            super(node);
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i > matcher.to - minLength) {
                matcher.hitEnd = true;
                return false;
            }
            int guard = matcher.to - minLength;
            while (i <= guard) {
                //if ((ret = next.match(matcher, i, seq)) || i == guard)
                if (next.match(matcher, i, seq)) {
                    matcher.first = i;
                    matcher.groups[0] = matcher.first;
                    matcher.groups[1] = matcher.last;
                    return true;
                }
                if (i == guard)
                    break;
                // Optimization to move to the next character. This is faster than countChars(seq, i, 1).
                if (Character.isHighSurrogate(seq.charAt(i++))) {
                    if (i < seq.length() && Character.isLowSurrogate(seq.charAt(i))) {
                        i++;
                    }
                }
            }
            matcher.hitEnd = true;
            return false;
        }
    }

    /**
     * Node to anchor at the beginning of input. This object implements the
     * match for a \A sequence, and the caret anchor will use this if not in
     * multiline mode.
     */
    static final class Begin extends Node {
        boolean match(Matcher matcher, int i, CharSequence seq) {
            int fromIndex = (matcher.anchoringBounds) ? matcher.from : 0;
            if (i == fromIndex && next.match(matcher, i, seq)) {
                matcher.first = i;
                matcher.groups[0] = i;
                matcher.groups[1] = matcher.last;
                return true;
            }
            return false;
        }
    }

    /**
     * Node to anchor at the end of input. This is the absolute end, so this
     * should not match at the last newline before the end as $ will.
     */
    static final class End extends Node {
        boolean match(Matcher matcher, int i, CharSequence seq) {
            int endIndex = (matcher.anchoringBounds) ? matcher.to : matcher.getTextLength();
            if (i == endIndex) {
                matcher.hitEnd = true;
                return next.match(matcher, i, seq);
            }
            return false;
        }
    }

    /**
     * Node to match the location where the last match ended.
     * This is used for the \G construct.
     */
    static final class LastMatch extends Node {
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i != matcher.oldLast)
                return false;
            return next.match(matcher, i, seq);
        }
    }

    /**
     * Node to anchor at the end of a line or the end of input based on the
     * multiline mode.
     * <p>
     * When not in multiline mode, the $ can only match at the very end
     * of the input, unless the input ends in a line terminator in which
     * it matches right before the last line terminator.
     * <p>
     * Note that \r\n is considered an atomic line terminator.
     * <p>
     * Like ^ the $ operator matches at a position, it does not match the
     * line terminators themselves.
     */
    static final class Dollar extends Node {
        boolean multiline;

        Dollar(boolean mul) {
            multiline = mul;
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            int endIndex = (matcher.anchoringBounds) ?
                    matcher.to : matcher.getTextLength();
            if (!multiline) {
                if (i < endIndex - 2)
                    return false;
                if (i == endIndex - 2) {
                    char ch = seq.charAt(i);
                    if (ch != '\r')
                        return false;
                    ch = seq.charAt(i + 1);
                    if (ch != '\n')
                        return false;
                }
            }
            // Matches before any line terminator; also matches at the
            // end of input
            // Before line terminator:
            // If multiline, we match here no matter what
            // If not multiline, fall through so that the end
            // is marked as hit; this must be a /r/n or a /n
            // at the very end so the end was hit; more input
            // could make this not match here
            if (i < endIndex) {
                char ch = seq.charAt(i);
                if (ch == '\n') {
                    // No match between \r\n
                    if (i > 0 && seq.charAt(i - 1) == '\r')
                        return false;
                    if (multiline)
                        return next.match(matcher, i, seq);
                } else if (ch == '\r' || ch == '\u0085' || (ch | 1) == '\u2029') {
                    if (multiline)
                        return next.match(matcher, i, seq);
                } else { // No line terminator, no match
                    return false;
                }
            }
            // Matched at current end so hit end
            matcher.hitEnd = true;
            // If a $ matches because of end of input, then more input
            // could cause it to fail!
            matcher.requireEnd = true;
            return next.match(matcher, i, seq);
        }

        boolean study(TreeInfo info) {
            next.study(info);
            return info.deterministic;
        }
    }

    /**
     * Node to anchor at the end of a line or the end of input based on the
     * multiline mode when in unix lines mode.
     */
    static final class UnixDollar extends Node {
        boolean multiline;

        UnixDollar(boolean mul) {
            multiline = mul;
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            int endIndex = (matcher.anchoringBounds) ? matcher.to : matcher.getTextLength();
            if (i < endIndex) {
                char ch = seq.charAt(i);
                if (ch == '\n') {
                    // If not multiline, then only possible to
                    // match at very end or one before end
                    if (!multiline && i != endIndex - 1)
                        return false;
                    // If multiline return next.match without setting
                    // matcher.hitEnd
                    if (multiline)
                        return next.match(matcher, i, seq);
                } else {
                    return false;
                }
            }
            // Matching because at the end or 1 before the end;
            // more input could change this so set hitEnd
            matcher.hitEnd = true;
            // If a $ matches because of end of input, then more input
            // could cause it to fail!
            matcher.requireEnd = true;
            return next.match(matcher, i, seq);
        }

        boolean study(TreeInfo info) {
            next.study(info);
            return info.deterministic;
        }
    }

    /**
     * Node class that matches a Unicode line ending '\R'
     */
    static final class LineEnding extends Node {
        boolean match(Matcher matcher, int i, CharSequence seq) {
            // (u+000Du+000A|[u+000Au+000Bu+000Cu+000Du+0085u+2028u+2029])
            if (i < matcher.to) {
                int ch = seq.charAt(i);
                if (ch == 0x0A || ch == 0x0B || ch == 0x0C || ch == 0x85 || ch == 0x2028 || ch == 0x2029)
                    return next.match(matcher, i + 1, seq);
                if (ch == 0x0D) {
                    i++;
                    if (i < matcher.to && seq.charAt(i) == 0x0A)
                        i++;
                    return next.match(matcher, i, seq);
                }
            } else {
                matcher.hitEnd = true;
            }
            return false;
        }

        boolean study(TreeInfo info) {
            info.minLength++;
            info.maxLength += 2;
            return next.study(info);
        }
    }

    /**
     * Abstract node class to match one character satisfying some
     * boolean property.
     */
    private static abstract class CharProperty extends Node {
        abstract boolean isSatisfiedBy(int ch);

        CharProperty complement() {
            return new Pattern.CharProperty() {
                boolean isSatisfiedBy(int ch) {
                    return !Pattern.CharProperty.this.isSatisfiedBy(ch);
                }
            };
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i < matcher.to) {
                int ch = Character.codePointAt(seq, i);
                return isSatisfiedBy(ch) && next.match(matcher, i + Character.charCount(ch), seq);
            } else {
                matcher.hitEnd = true;
                return false;
            }
        }

        boolean study(TreeInfo info) {
            info.minLength++;
            info.maxLength++;
            return next.study(info);
        }
    }

    /**
     * Optimized version of CharProperty that works only for
     * properties never satisfied by Supplementary characters.
     */
    private static abstract class BmpCharProperty extends CharProperty {
        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (i < matcher.to) {
                return isSatisfiedBy(seq.charAt(i)) && next.match(matcher, i + 1, seq);
            } else {
                matcher.hitEnd = true;
                return false;
            }
        }
    }

    /**
     * Node class that matches a Supplementary Unicode character
     */
    static final class SingleS extends CharProperty {
        final int c;

        SingleS(int c) {
            this.c = c;
        }

        boolean isSatisfiedBy(int ch) {
            return ch == c;
        }
    }

    /**
     * Optimization -- matches a given BMP character
     */
    static final class Single extends BmpCharProperty {
        final int c;

        Single(int c) {
            this.c = c;
        }

        boolean isSatisfiedBy(int ch) {
            return ch == c;
        }
    }

    /**
     * Node class that matches a Unicode block.
     */
    static final class Block extends CharProperty {
        final Character.UnicodeBlock block;

        Block(Character.UnicodeBlock block) {
            this.block = block;
        }

        boolean isSatisfiedBy(int ch) {
            return block == Character.UnicodeBlock.of(ch);
        }
    }

    /**
     * Node class that matches a Unicode script
     */
    static final class Script extends CharProperty {
        final Character.UnicodeScript script;

        Script(Character.UnicodeScript script) {
            this.script = script;
        }

        boolean isSatisfiedBy(int ch) {
            return script == Character.UnicodeScript.of(ch);
        }
    }

    /**
     * Node class that matches a Unicode category.
     */
    static final class Category extends CharProperty {
        final int typeMask;

        Category(int typeMask) {
            this.typeMask = typeMask;
        }

        boolean isSatisfiedBy(int ch) {
            return (typeMask & (1 << Character.getType(ch))) != 0;
        }
    }

    /**
     * Node class that matches a Unicode "type"
     */
    static final class Utype extends CharProperty {
        final UnicodeProp uprop;

        Utype(UnicodeProp uprop) {
            this.uprop = uprop;
        }

        boolean isSatisfiedBy(int ch) {
            return uprop.is(ch);
        }
    }

    /**
     * Node class that matches a POSIX type.
     */
    static final class Ctype extends BmpCharProperty {
        final int ctype;

        Ctype(int ctype) {
            this.ctype = ctype;
        }

        boolean isSatisfiedBy(int ch) {
            return ch < 128 && ASCII.isType(ch, ctype);
        }
    }

    /**
     * Node class that matches a Perl vertical whitespace
     */
    static final class VertWS extends BmpCharProperty {
        boolean isSatisfiedBy(int cp) {
            return (cp >= 0x0A && cp <= 0x0D) || cp == 0x85 || cp == 0x2028 || cp == 0x2029;
        }
    }

    /**
     * Node class that matches a Perl horizontal whitespace
     */
    static final class HorizWS extends BmpCharProperty {
        boolean isSatisfiedBy(int cp) {
            return cp == 0x09 || cp == 0x20 || cp == 0xa0 ||
                    cp == 0x1680 || cp == 0x180e ||
                    cp >= 0x2000 && cp <= 0x200a ||
                    cp == 0x202f || cp == 0x205f || cp == 0x3000;
        }
    }

    /**
     * Base class for all Slice nodes
     */
    static class SliceNode extends Node {
        int[] buffer;

        SliceNode(int[] buf) {
            buffer = buf;
        }

        boolean study(TreeInfo info) {
            info.minLength += buffer.length;
            info.maxLength += buffer.length;
            return next.study(info);
        }
    }

    /**
     * Node class for a case sensitive/BMP-only sequence of literal
     * characters.
     */
    static final class Slice extends SliceNode {
        Slice(int[] buf) {
            super(buf);
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            int[] buf = buffer;
            int len = buf.length;
            for (int j = 0; j < len; j++) {
                if ((i + j) >= matcher.to) {
                    matcher.hitEnd = true;
                    return false;
                }
                if (buf[j] != seq.charAt(i + j))
                    return false;
            }
            return next.match(matcher, i + len, seq);
        }
    }

    /**
     * Node class for a case sensitive sequence of literal characters
     * including supplementary characters.
     */
    static final class SliceS extends SliceNode {
        SliceS(int[] buf) {
            super(buf);
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            int[] buf = buffer;
            int x = i;
            for (int value : buf) {
                if (x >= matcher.to) {
                    matcher.hitEnd = true;
                    return false;
                }
                int c = Character.codePointAt(seq, x);
                if (value != c)
                    return false;
                x += Character.charCount(c);
                if (x > matcher.to) {
                    matcher.hitEnd = true;
                    return false;
                }
            }
            return next.match(matcher, x, seq);
        }
    }

    private static boolean inRange(int lower, int ch, int upper) {
        return lower <= ch && ch <= upper;
    }

    /**
     * Returns node for matching characters within an explicit value range.
     */
    private static CharProperty rangeFor(final int lower, final int upper) {
        return new CharProperty() {
            boolean isSatisfiedBy(int ch) {
                return inRange(lower, ch, upper);
            }
        };
    }

    /**
     * Implements the Unicode category ALL and the dot metacharacter when
     * in dotall mode.
     */
    static final class All extends CharProperty {
        boolean isSatisfiedBy(int ch) {
            return true;
        }
    }

    /**
     * Node class for the dot metacharacter when dotall is not enabled.
     */
    static final class Dot extends CharProperty {
        boolean isSatisfiedBy(int ch) {
            return (ch != '\n' && ch != '\r' && (ch | 1) != '\u2029' && ch != '\u0085');
        }
    }

    /**
     * Node class for the dot metacharacter when dotall is not enabled
     * but UNIX_LINES is enabled.
     */
    static final class UnixDot extends CharProperty {
        boolean isSatisfiedBy(int ch) {
            return ch != '\n';
        }
    }

    /**
     * The 0 or 1 quantifier. This one class implements all three types.
     */
    static final class Ques extends Node {
        Node atom;
        int type;

        Ques(Node node, int type) {
            this.atom = node;
            this.type = type;
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            switch (type) {
                case GREEDY:
                    return (atom.match(matcher, i, seq) && next.match(matcher, matcher.last, seq)) || next.match(matcher, i, seq);
                case LAZY:
                    return next.match(matcher, i, seq) || (atom.match(matcher, i, seq) && next.match(matcher, matcher.last, seq));
                case POSSESSIVE:
                    if (atom.match(matcher, i, seq)) i = matcher.last;
                    return next.match(matcher, i, seq);
                default:
                    return atom.match(matcher, i, seq) && next.match(matcher, matcher.last, seq);
            }
        }

        boolean study(TreeInfo info) {
            if (type != INDEPENDENT) {
                int minL = info.minLength;
                atom.study(info);
                info.minLength = minL;
                info.deterministic = false;
                return next.study(info);
            } else {
                atom.study(info);
                return next.study(info);
            }
        }
    }

    /**
     * Handles the curly-brace style repetition with a specified minimum and
     * maximum occurrences. The * quantifier is handled as a special case.
     * This class handles the three types.
     */
    static final class Curly extends Node {
        Node atom;
        int type;
        int cmin;
        int cmax;

        Curly(Node node, int cmin, int cmax, int type) {
            this.atom = node;
            this.type = type;
            this.cmin = cmin;
            this.cmax = cmax;
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            int j;
            for (j = 0; j < cmin; j++) {
                if (atom.match(matcher, i, seq)) {
                    i = matcher.last;
                    continue;
                }
                return false;
            }
            if (type == GREEDY)
                return match0(matcher, i, j, seq);
            else if (type == LAZY)
                return match1(matcher, i, j, seq);
            else
                return match2(matcher, i, j, seq);
        }

        // Greedy match.
        // i is the index to start matching at
        // j is the number of atoms that have matched
        boolean match0(Matcher matcher, int i, int j, CharSequence seq) {
            if (j >= cmax) {
                // We have matched the maximum... continue with the rest of
                // the regular expression
                return next.match(matcher, i, seq);
            }
            int backLimit = j;
            while (atom.match(matcher, i, seq)) {
                // k is the length of this match
                int k = matcher.last - i;
                if (k == 0) // Zero length match
                    break;
                // Move up index and number matched
                i = matcher.last;
                j++;
                // We are greedy so match as many as we can
                while (j < cmax) {
                    if (!atom.match(matcher, i, seq))
                        break;
                    if (i + k != matcher.last) {
                        if (match0(matcher, matcher.last, j + 1, seq))
                            return true;
                        break;
                    }
                    i += k;
                    j++;
                }
                // Handle backing off if match fails
                while (j >= backLimit) {
                    if (next.match(matcher, i, seq))
                        return true;
                    i -= k;
                    j--;
                }
                return false;
            }
            return next.match(matcher, i, seq);
        }

        // Reluctant match. At this point, the minimum has been satisfied.
        // i is the index to start matching at
        // j is the number of atoms that have matched
        boolean match1(Matcher matcher, int i, int j, CharSequence seq) {
            for (; ; ) {
                // Try finishing match without consuming any more
                if (next.match(matcher, i, seq))
                    return true;
                // At the maximum, no match found
                if (j >= cmax)
                    return false;
                // Okay, must try one more atom
                if (!atom.match(matcher, i, seq))
                    return false;
                // If we haven't moved forward then must break out
                if (i == matcher.last)
                    return false;
                // Move up index and number matched
                i = matcher.last;
                j++;
            }
        }

        boolean match2(Matcher matcher, int i, int j, CharSequence seq) {
            for (; j < cmax; j++) {
                if (!atom.match(matcher, i, seq))
                    break;
                if (i == matcher.last)
                    break;
                i = matcher.last;
            }
            return next.match(matcher, i, seq);
        }

        boolean study(TreeInfo info) {
            // Save original info
            int minL = info.minLength;
            int maxL = info.maxLength;
            boolean maxV = info.maxValid;
            boolean detm = info.deterministic;
            info.reset();

            atom.study(info);

            int temp = info.minLength * cmin + minL;
            if (temp < minL) {
                temp = 0xFFFFFFF; // arbitrary large number
            }
            info.minLength = temp;

            if (maxV & info.maxValid) {
                temp = info.maxLength * cmax + maxL;
                info.maxLength = temp;
                if (temp < maxL) {
                    info.maxValid = false;
                }
            } else {
                info.maxValid = false;
            }

            if (info.deterministic && cmin == cmax)
                info.deterministic = detm;
            else
                info.deterministic = false;
            return next.study(info);
        }
    }

    /**
     * Handles the curly-brace style repetition with a specified minimum and
     * maximum occurrences in deterministic cases. This is an iterative
     * optimization over the Prolog and Loop system which would handle this
     * in a recursive way. The * quantifier is handled as a special case.
     * If capture is true then this class saves group settings and ensures
     * that groups are unset when backing off of a group match.
     */
    static final class GroupCurly extends Node {
        Node atom;
        int type;
        int cmin;
        int cmax;
        int localIndex;
        int groupIndex;
        boolean capture;

        GroupCurly(Node node, int cmin, int cmax, int type, int local, int group, boolean capture) {
            this.atom = node;
            this.type = type;
            this.cmin = cmin;
            this.cmax = cmax;
            this.localIndex = local;
            this.groupIndex = group;
            this.capture = capture;
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            int[] groups = matcher.groups;
            int[] locals = matcher.locals;
            int save0 = locals[localIndex];
            int save1 = 0;
            int save2 = 0;

            if (capture) {
                save1 = groups[groupIndex];
                save2 = groups[groupIndex + 1];
            }

            // Notify GroupTail there is no need to setup group info
            // because it will be set here
            locals[localIndex] = -1;

            boolean ret = true;
            for (int j = 0; j < cmin; j++) {
                if (atom.match(matcher, i, seq)) {
                    if (capture) {
                        groups[groupIndex] = i;
                        groups[groupIndex + 1] = matcher.last;
                    }
                    i = matcher.last;
                } else {
                    ret = false;
                    break;
                }
            }
            if (ret) {
                if (type == GREEDY) {
                    ret = match0(matcher, i, cmin, seq);
                } else if (type == LAZY) {
                    ret = match1(matcher, i, cmin, seq);
                } else {
                    ret = match2(matcher, i, cmin, seq);
                }
            }
            if (!ret) {
                locals[localIndex] = save0;
                if (capture) {
                    groups[groupIndex] = save1;
                    groups[groupIndex + 1] = save2;
                }
            }
            return ret;
        }

        // Aggressive group match
        boolean match0(Matcher matcher, int i, int j, CharSequence seq) {
            // don't back off passing the starting "j"
            int min = j;
            int[] groups = matcher.groups;
            int save0 = 0;
            int save1 = 0;
            if (capture) {
                save0 = groups[groupIndex];
                save1 = groups[groupIndex + 1];
            }
            for (; ; ) {
                if (j >= cmax)
                    break;
                if (!atom.match(matcher, i, seq))
                    break;
                int k = matcher.last - i;
                if (k <= 0) {
                    if (capture) {
                        groups[groupIndex] = i;
                        groups[groupIndex + 1] = i + k;
                    }
                    i = i + k;
                    break;
                }
                for (; ; ) {
                    if (capture) {
                        groups[groupIndex] = i;
                        groups[groupIndex + 1] = i + k;
                    }
                    i = i + k;
                    if (++j >= cmax)
                        break;
                    if (!atom.match(matcher, i, seq))
                        break;
                    if (i + k != matcher.last) {
                        if (match0(matcher, i, j, seq))
                            return true;
                        break;
                    }
                }
                while (j > min) {
                    if (next.match(matcher, i, seq)) {
                        if (capture) {
                            groups[groupIndex + 1] = i;
                            groups[groupIndex] = i - k;
                        }
                        return true;
                    }
                    // backing off
                    i = i - k;
                    if (capture) {
                        groups[groupIndex + 1] = i;
                        groups[groupIndex] = i - k;
                    }
                    j--;

                }
                break;
            }
            if (capture) {
                groups[groupIndex] = save0;
                groups[groupIndex + 1] = save1;
            }
            return next.match(matcher, i, seq);
        }

        // Reluctant matching
        boolean match1(Matcher matcher, int i, int j, CharSequence seq) {
            for (; ; ) {
                if (next.match(matcher, i, seq))
                    return true;
                if (j >= cmax)
                    return false;
                if (!atom.match(matcher, i, seq))
                    return false;
                if (i == matcher.last)
                    return false;
                if (capture) {
                    matcher.groups[groupIndex] = i;
                    matcher.groups[groupIndex + 1] = matcher.last;
                }
                i = matcher.last;
                j++;
            }
        }

        // Possessive matching
        boolean match2(Matcher matcher, int i, int j, CharSequence seq) {
            for (; j < cmax; j++) {
                if (!atom.match(matcher, i, seq)) {
                    break;
                }
                if (capture) {
                    matcher.groups[groupIndex] = i;
                    matcher.groups[groupIndex + 1] = matcher.last;
                }
                if (i == matcher.last) {
                    break;
                }
                i = matcher.last;
            }
            return next.match(matcher, i, seq);
        }

        boolean study(TreeInfo info) {
            // Save original info
            int minL = info.minLength;
            int maxL = info.maxLength;
            boolean maxV = info.maxValid;
            boolean detm = info.deterministic;
            info.reset();

            atom.study(info);

            int temp = info.minLength * cmin + minL;
            if (temp < minL) {
                temp = 0xFFFFFFF; // Arbitrary large number
            }
            info.minLength = temp;

            if (maxV & info.maxValid) {
                temp = info.maxLength * cmax + maxL;
                info.maxLength = temp;
                if (temp < maxL) {
                    info.maxValid = false;
                }
            } else {
                info.maxValid = false;
            }

            if (info.deterministic && cmin == cmax) {
                info.deterministic = detm;
            } else {
                info.deterministic = false;
            }
            return next.study(info);
        }
    }

    /**
     * A Guard node at the end of each atom node in a Branch. It
     * serves the purpose of chaining the "match" operation to
     * "next" but not the "study", so we can collect the TreeInfo
     * of each atom node without including the TreeInfo of the
     * "next".
     */
    static final class BranchConn extends Node {
        boolean match(Matcher matcher, int i, CharSequence seq) {
            return next.match(matcher, i, seq);
        }

        boolean study(TreeInfo info) {
            return info.deterministic;
        }
    }

    /**
     * Handles the branching of alternations. Note this is also used for
     * the ? quantifier to branch between the case where it matches once
     * and where it does not occur.
     */
    static final class Branch extends Node {
        Node[] atoms = new Node[2];
        int size = 2;
        Node conn;

        Branch(Node first, Node second, Node branchConn) {
            conn = branchConn;
            atoms[0] = first;
            atoms[1] = second;
        }

        void add(Node node) {
            if (size >= atoms.length) {
                Node[] tmp = new Node[atoms.length * 2];
                System.arraycopy(atoms, 0, tmp, 0, atoms.length);
                atoms = tmp;
            }
            atoms[size++] = node;
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            for (int n = 0; n < size; n++) {
                if (atoms[n] == null) {
                    if (conn.next.match(matcher, i, seq))
                        return true;
                } else if (atoms[n].match(matcher, i, seq)) {
                    return true;
                }
            }
            return false;
        }

        boolean study(TreeInfo info) {
            int minL = info.minLength;
            int maxL = info.maxLength;
            boolean maxV = info.maxValid;

            int minL2 = Integer.MAX_VALUE; //arbitrary large enough num
            int maxL2 = -1;
            for (int n = 0; n < size; n++) {
                info.reset();
                if (atoms[n] != null)
                    atoms[n].study(info);
                minL2 = Math.min(minL2, info.minLength);
                maxL2 = Math.max(maxL2, info.maxLength);
                maxV = (maxV & info.maxValid);
            }

            minL += minL2;
            maxL += maxL2;

            info.reset();
            conn.next.study(info);

            info.minLength += minL;
            info.maxLength += maxL;
            info.maxValid &= maxV;
            info.deterministic = false;
            return false;
        }
    }

    /**
     * The GroupHead saves the location where the group begins in the locals
     * and restores them when the match is done.
     * <p>
     * The matchRef is used when a reference to this group is accessed later
     * in the expression. The locals will have a negative value in them to
     * indicate that we do not want to unset the group if the reference
     * doesn't match.
     */
    static final class GroupHead extends Node {
        int localIndex;

        GroupHead(int localCount) {
            localIndex = localCount;
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            int save = matcher.locals[localIndex];
            matcher.locals[localIndex] = i;
            boolean ret = next.match(matcher, i, seq);
            matcher.locals[localIndex] = save;
            return ret;
        }

    }

    /**
     * The GroupTail handles the setting of group beginning and ending
     * locations when groups are successfully matched. It must also be able to
     * unset groups that have to be backed off of.
     * <p>
     * The GroupTail node is also used when a previous group is referenced,
     * and in that case no group information needs to be set.
     */
    static final class GroupTail extends Node {
        int localIndex;
        int groupIndex;

        GroupTail(int localCount, int groupCount) {
            localIndex = localCount;
            groupIndex = groupCount + groupCount;
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            int tmp = matcher.locals[localIndex];
            if (tmp >= 0) { // This is the normal group case.
                // Save the group so we can unset it if it
                // backs off of a match.
                int groupStart = matcher.groups[groupIndex];
                int groupEnd = matcher.groups[groupIndex + 1];

                matcher.groups[groupIndex] = tmp;
                matcher.groups[groupIndex + 1] = i;
                if (next.match(matcher, i, seq)) {
                    return true;
                }
                matcher.groups[groupIndex] = groupStart;
                matcher.groups[groupIndex + 1] = groupEnd;
                return false;
            } else {
                // This is a group reference case. We don't need to save any
                // group info because it isn't really a group.
                matcher.last = i;
                return true;
            }
        }
    }

    /**
     * This sets up a loop to handle a recursive quantifier structure.
     */
    static final class Prolog extends Node {
        Loop loop;

        Prolog(Loop loop) {
            this.loop = loop;
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            return loop.matchInit(matcher, i, seq);
        }

        boolean study(TreeInfo info) {
            return loop.study(info);
        }
    }

    /**
     * Handles the repetition count for a greedy Curly. The matchInit
     * is called from the Prolog to save the index of where the group
     * beginning is stored. A zero length group check occurs in the
     * normal match but is skipped in the matchInit.
     */
    static class Loop extends Node {
        Node body;
        int countIndex; // local count index in matcher locals
        int beginIndex; // group beginning index
        int cmin, cmax;

        Loop(int countIndex, int beginIndex) {
            this.countIndex = countIndex;
            this.beginIndex = beginIndex;
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            // Avoid infinite loop in zero-length case.
            if (i > matcher.locals[beginIndex]) {
                int count = matcher.locals[countIndex];

                // This block is for before we reach the minimum
                // iterations required for the loop to match
                if (count < cmin) {
                    matcher.locals[countIndex] = count + 1;
                    boolean b = body.match(matcher, i, seq);
                    // If match failed we must backtrack, so
                    // the loop count should NOT be incremented
                    if (!b)
                        matcher.locals[countIndex] = count;
                    // Return success or failure since we are under
                    // minimum
                    return b;
                }
                // This block is for after we have the minimum
                // iterations required for the loop to match
                if (count < cmax) {
                    matcher.locals[countIndex] = count + 1;
                    boolean b = body.match(matcher, i, seq);
                    // If match failed we must backtrack, so
                    // the loop count should NOT be incremented
                    if (!b)
                        matcher.locals[countIndex] = count;
                    else
                        return true;
                }
            }
            return next.match(matcher, i, seq);
        }

        boolean matchInit(Matcher matcher, int i, CharSequence seq) {
            int save = matcher.locals[countIndex];
            boolean ret;
            if (0 < cmin) {
                matcher.locals[countIndex] = 1;
                ret = body.match(matcher, i, seq);
            } else if (0 < cmax) {
                matcher.locals[countIndex] = 1;
                ret = body.match(matcher, i, seq);
                if (!ret)
                    ret = next.match(matcher, i, seq);
            } else {
                ret = next.match(matcher, i, seq);
            }
            matcher.locals[countIndex] = save;
            return ret;
        }

        boolean study(TreeInfo info) {
            info.maxValid = false;
            info.deterministic = false;
            return false;
        }
    }

    /**
     * Handles the repetition count for a reluctant Curly. The matchInit
     * is called from the Prolog to save the index of where the group
     * beginning is stored. A zero length group check occurs in the
     * normal match but is skipped in the matchInit.
     */
    static final class LazyLoop extends Loop {
        LazyLoop(int countIndex, int beginIndex) {
            super(countIndex, beginIndex);
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            // Check for zero length group
            if (i > matcher.locals[beginIndex]) {
                int count = matcher.locals[countIndex];
                if (count < cmin) {
                    matcher.locals[countIndex] = count + 1;
                    boolean result = body.match(matcher, i, seq);
                    // If match failed we must backtrack, so
                    // the loop count should NOT be incremented
                    if (!result)
                        matcher.locals[countIndex] = count;
                    return result;
                }
                if (next.match(matcher, i, seq))
                    return true;
                if (count < cmax) {
                    matcher.locals[countIndex] = count + 1;
                    boolean result = body.match(matcher, i, seq);
                    // If match failed we must backtrack, so
                    // the loop count should NOT be incremented
                    if (!result)
                        matcher.locals[countIndex] = count;
                    return result;
                }
                return false;
            }
            return next.match(matcher, i, seq);
        }

        boolean matchInit(Matcher matcher, int i, CharSequence seq) {
            int save = matcher.locals[countIndex];
            boolean ret = false;
            if (0 < cmin) {
                matcher.locals[countIndex] = 1;
                ret = body.match(matcher, i, seq);
            } else if (next.match(matcher, i, seq)) {
                ret = true;
            } else if (0 < cmax) {
                matcher.locals[countIndex] = 1;
                ret = body.match(matcher, i, seq);
            }
            matcher.locals[countIndex] = save;
            return ret;
        }

        boolean study(TreeInfo info) {
            info.maxValid = false;
            info.deterministic = false;
            return false;
        }
    }

    /**
     * Refers to a group in the regular expression. Attempts to match
     * whatever the group referred to last matched.
     */
    static class BackRef extends Node {
        int groupIndex;

        BackRef(int groupCount) {
            super();
            groupIndex = groupCount + groupCount;
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            int j = matcher.groups[groupIndex];
            int k = matcher.groups[groupIndex + 1];

            int groupSize = k - j;
            // If the referenced group didn't match, neither can this
            if (j < 0)
                return false;

            // If there isn't enough input left no match
            if (i + groupSize > matcher.to) {
                matcher.hitEnd = true;
                return false;
            }
            // Check each new char to make sure it matches what the group
            // referenced matched last time around
            for (int index = 0; index < groupSize; index++)
                if (seq.charAt(i + index) != seq.charAt(j + index))
                    return false;

            return next.match(matcher, i + groupSize, seq);
        }

        boolean study(TreeInfo info) {
            info.maxValid = false;
            return next.study(info);
        }
    }

    /**
     * Searches until the next instance of its atom. This is useful for
     * finding the atom efficiently without passing an instance of it
     * (greedy problem) and without a lot of wasted search time (reluctant
     * problem).
     */
    static final class First extends Node {
        Node atom;

        First(Node node) {
            this.atom = BnM.optimize(node);
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            if (atom instanceof BnM) {
                return atom.match(matcher, i, seq) && next.match(matcher, matcher.last, seq);
            }
            for (; ; ) {
                if (i > matcher.to) {
                    matcher.hitEnd = true;
                    return false;
                }
                if (atom.match(matcher, i, seq)) {
                    return next.match(matcher, matcher.last, seq);
                }
                i += countChars(seq, i, 1);
                matcher.first++;
            }
        }

        boolean study(TreeInfo info) {
            atom.study(info);
            info.maxValid = false;
            info.deterministic = false;
            return next.study(info);
        }
    }

    /**
     * Zero width positive lookahead.
     */
    static final class Pos extends Node {
        Node cond;

        Pos(Node cond) {
            this.cond = cond;
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            int savedTo = matcher.to;
            boolean conditionMatched;

            // Relax transparent region boundaries for lookahead
            if (matcher.transparentBounds)
                matcher.to = matcher.getTextLength();
            try {
                conditionMatched = cond.match(matcher, i, seq);
            } finally {
                // Reinstate region boundaries
                matcher.to = savedTo;
            }
            return conditionMatched && next.match(matcher, i, seq);
        }
    }

    /**
     * Zero width negative lookahead.
     */
    static final class Neg extends Node {
        Node cond;

        Neg(Node cond) {
            this.cond = cond;
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            int savedTo = matcher.to;
            boolean conditionMatched;

            // Relax transparent region boundaries for lookahead
            if (matcher.transparentBounds)
                matcher.to = matcher.getTextLength();
            try {
                if (i < matcher.to) {
                    conditionMatched = !cond.match(matcher, i, seq);
                } else {
                    // If a negative lookahead succeeds then more input could cause it to fail!
                    matcher.requireEnd = true;
                    conditionMatched = !cond.match(matcher, i, seq);
                }
            } finally {
                // Reinstate region boundaries
                matcher.to = savedTo;
            }
            return conditionMatched && next.match(matcher, i, seq);
        }
    }

    /**
     * For use with lookbehinds; matches the position where the lookbehind
     * was encountered.
     */
    static Node lookbehindEnd = new Node() {
        boolean match(Matcher matcher, int i, CharSequence seq) {
            return i == matcher.lookbehindTo;
        }
    };

    /**
     * Zero width positive lookbehind.
     */
    static class Behind extends Node {
        Node cond;
        int rmax, rmin;

        Behind(Node cond, int rmax, int rmin) {
            this.cond = cond;
            this.rmax = rmax;
            this.rmin = rmin;
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            int savedFrom = matcher.from;
            boolean conditionMatched = false;
            int startIndex = (!matcher.transparentBounds) ?
                    matcher.from : 0;
            int from = Math.max(i - rmax, startIndex);
            // Set end boundary
            int savedLBT = matcher.lookbehindTo;
            matcher.lookbehindTo = i;
            // Relax transparent region boundaries for lookbehind
            if (matcher.transparentBounds)
                matcher.from = 0;
            for (int j = i - rmin; !conditionMatched && j >= from; j--) {
                conditionMatched = cond.match(matcher, j, seq);
            }
            matcher.from = savedFrom;
            matcher.lookbehindTo = savedLBT;
            return conditionMatched && next.match(matcher, i, seq);
        }
    }

    /**
     * Zero width positive lookbehind, including supplementary
     * characters or unpaired surrogates.
     */
    static final class BehindS extends Behind {
        BehindS(Node cond, int rmax, int rmin) {
            super(cond, rmax, rmin);
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            int rmaxChars = countChars(seq, i, -rmax);
            int rminChars = countChars(seq, i, -rmin);
            int savedFrom = matcher.from;
            int startIndex = (!matcher.transparentBounds) ?
                    matcher.from : 0;
            boolean conditionMatched = false;
            int from = Math.max(i - rmaxChars, startIndex);
            // Set end boundary
            int savedLBT = matcher.lookbehindTo;
            matcher.lookbehindTo = i;
            // Relax transparent region boundaries for lookbehind
            if (matcher.transparentBounds)
                matcher.from = 0;

            for (int j = i - rminChars;
                 !conditionMatched && j >= from;
                 j -= j > from ? countChars(seq, j, -1) : 1) {
                conditionMatched = cond.match(matcher, j, seq);
            }
            matcher.from = savedFrom;
            matcher.lookbehindTo = savedLBT;
            return conditionMatched && next.match(matcher, i, seq);
        }
    }

    /**
     * Zero width negative lookbehind.
     */
    static class NotBehind extends Node {
        Node cond;
        int rmax, rmin;

        NotBehind(Node cond, int rmax, int rmin) {
            this.cond = cond;
            this.rmax = rmax;
            this.rmin = rmin;
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            int savedLBT = matcher.lookbehindTo;
            int savedFrom = matcher.from;
            boolean conditionMatched = false;
            int startIndex = (!matcher.transparentBounds) ?
                    matcher.from : 0;
            int from = Math.max(i - rmax, startIndex);
            matcher.lookbehindTo = i;
            // Relax transparent region boundaries for lookbehind
            if (matcher.transparentBounds)
                matcher.from = 0;
            for (int j = i - rmin; !conditionMatched && j >= from; j--) {
                conditionMatched = cond.match(matcher, j, seq);
            }
            // Reinstate region boundaries
            matcher.from = savedFrom;
            matcher.lookbehindTo = savedLBT;
            return !conditionMatched && next.match(matcher, i, seq);
        }
    }

    /**
     * Zero width negative lookbehind, including supplementary
     * characters or unpaired surrogates.
     */
    static final class NotBehindS extends NotBehind {
        NotBehindS(Node cond, int rmax, int rmin) {
            super(cond, rmax, rmin);
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            int rmaxChars = countChars(seq, i, -rmax);
            int rminChars = countChars(seq, i, -rmin);
            int savedFrom = matcher.from;
            int savedLBT = matcher.lookbehindTo;
            boolean conditionMatched = false;
            int startIndex = (!matcher.transparentBounds) ?
                    matcher.from : 0;
            int from = Math.max(i - rmaxChars, startIndex);
            matcher.lookbehindTo = i;
            // Relax transparent region boundaries for lookbehind
            if (matcher.transparentBounds)
                matcher.from = 0;
            for (int j = i - rminChars;
                 !conditionMatched && j >= from;
                 j -= j > from ? countChars(seq, j, -1) : 1) {
                conditionMatched = cond.match(matcher, j, seq);
            }
            //Reinstate region boundaries
            matcher.from = savedFrom;
            matcher.lookbehindTo = savedLBT;
            return !conditionMatched && next.match(matcher, i, seq);
        }
    }

    /**
     * Returns the set union of two CharProperty nodes.
     */
    private static CharProperty union(final CharProperty lhs, final CharProperty rhs) {
        return new CharProperty() {
            boolean isSatisfiedBy(int ch) {
                return lhs.isSatisfiedBy(ch) || rhs.isSatisfiedBy(ch);
            }
        };
    }

    /**
     * Returns the set intersection of two CharProperty nodes.
     */
    private static CharProperty intersection(final CharProperty lhs, final CharProperty rhs) {
        return new CharProperty() {
            boolean isSatisfiedBy(int ch) {
                return lhs.isSatisfiedBy(ch) && rhs.isSatisfiedBy(ch);
            }
        };
    }

    /**
     * Returns the set difference of two CharProperty nodes.
     */
    private static CharProperty setDifference(final CharProperty lhs, final CharProperty rhs) {
        return new CharProperty() {
            boolean isSatisfiedBy(int ch) {
                return !rhs.isSatisfiedBy(ch) && lhs.isSatisfiedBy(ch);
            }
        };
    }

    /**
     * Handles word boundaries. Includes a field to allow this one class to
     * deal with the different types of word boundaries we can match. The word
     * characters include underscores, letters, and digits. Non spacing marks
     * can are also part of a word if they have a base character, otherwise
     * they are ignored for purposes of finding word boundaries.
     */
    static final class Bound extends Node {
        static int LEFT = 0x1;
        static int RIGHT = 0x2;
        static int BOTH = 0x3;
        static int NONE = 0x4;
        int type;
        boolean useUWORD;

        Bound(int n, boolean useUWORD) {
            type = n;
            this.useUWORD = useUWORD;
        }

        boolean isWord(int ch) {
            return useUWORD ? UnicodeProp.WORD.is(ch)
                    : (ch == '_' || Character.isLetterOrDigit(ch));
        }

        int check(Matcher matcher, int i, CharSequence seq) {
            int ch;
            boolean left = false;
            int startIndex = matcher.from;
            int endIndex = matcher.to;
            if (matcher.transparentBounds) {
                startIndex = 0;
                endIndex = matcher.getTextLength();
            }
            if (i > startIndex) {
                ch = Character.codePointBefore(seq, i);
                left = (isWord(ch) ||
                        ((Character.getType(ch) == Character.NON_SPACING_MARK)
                                && hasBaseCharacter(matcher, i - 1, seq)));
            }
            boolean right = false;
            if (i < endIndex) {
                ch = Character.codePointAt(seq, i);
                right = (isWord(ch) ||
                        ((Character.getType(ch) == Character.NON_SPACING_MARK)
                                && hasBaseCharacter(matcher, i, seq)));
            } else {
                // Tried to access char past the end
                matcher.hitEnd = true;
                // The addition of another char could wreck a boundary
                matcher.requireEnd = true;
            }
            return ((left ^ right) ? (right ? LEFT : RIGHT) : NONE);
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            return (check(matcher, i, seq) & type) > 0 && next.match(matcher, i, seq);
        }
    }

    /**
     * Non spacing marks only count as word characters in bounds calculations
     * if they have a base character.
     */
    private static boolean hasBaseCharacter(Matcher matcher, int i,
                                            CharSequence seq) {
        int start = (!matcher.transparentBounds) ?
                matcher.from : 0;
        for (int x = i; x >= start; x--) {
            int ch = Character.codePointAt(seq, x);
            if (Character.isLetterOrDigit(ch))
                return true;
            if (Character.getType(ch) == Character.NON_SPACING_MARK)
                continue;
            return false;
        }
        return false;
    }

    /**
     * Attempts to match a slice in the input using the Boyer-Moore string
     * matching algorithm. The algorithm is based on the idea that the
     * pattern can be shifted farther ahead in the search text if it is
     * matched right to left.
     * <p>
     * The pattern is compared to the input one character at a time, from
     * the rightmost character in the pattern to the left. If the characters
     * all match the pattern has been found. If a character does not match,
     * the pattern is shifted right a distance that is the maximum of two
     * functions, the bad character shift and the good suffix shift. This
     * shift moves the attempted match position through the input more
     * quickly than a naive one position at a time check.
     * <p>
     * The bad character shift is based on the character from the text that
     * did not match. If the character does not appear in the pattern, the
     * pattern can be shifted completely beyond the bad character. If the
     * character does occur in the pattern, the pattern can be shifted to
     * line the pattern up with the next occurrence of that character.
     * <p>
     * The good suffix shift is based on the idea that some subset on the right
     * side of the pattern has matched. When a bad character is found, the
     * pattern can be shifted right by the pattern length if the subset does
     * not occur again in pattern, or by the amount of distance to the
     * next occurrence of the subset in the pattern.
     * <p>
     * Boyer-Moore search methods adapted from code by Amy Yu.
     */
    static class BnM extends Node {
        int[] buffer;
        int[] lastOcc;
        int[] optoSft;

        /**
         * Pre calculates arrays needed to generate the bad character
         * shift and the good suffix shift. Only the last seven bits
         * are used to see if chars match; This keeps the tables small
         * and covers the heavily used ASCII range, but occasionally
         * results in an aliased match for the bad character shift.
         */
        static Node optimize(Node node) {
            if (!(node instanceof Slice)) {
                return node;
            }

            int[] src = ((Slice) node).buffer;
            int patternLength = src.length;
            // The BM algorithm requires a bit of overhead;
            // If the pattern is short don't use it, since
            // a shift larger than the pattern length cannot
            // be used anyway.
            if (patternLength < 4) {
                return node;
            }
            int i, j;
            int[] lastOcc = new int[128];
            int[] optoSft = new int[patternLength];
            // Precalculate part of the bad character shift
            // It is a table for where in the pattern each
            // lower 7-bit value occurs
            for (i = 0; i < patternLength; i++) {
                lastOcc[src[i] & 0x7F] = i + 1;
            }
            // Precalculate the good suffix shift
            // i is the shift amount being considered
            NEXT:
            for (i = patternLength; i > 0; i--) {
                // j is the beginning index of suffix being considered
                for (j = patternLength - 1; j >= i; j--) {
                    // Testing for good suffix
                    if (src[j] == src[j - i]) {
                        // src[j..len] is a good suffix
                        optoSft[j - 1] = i;
                    } else {
                        // No match. The array has already been
                        // filled up with correct values before.
                        continue NEXT;
                    }
                }
                // This fills up the remaining of optoSft
                // any suffix can not have larger shift amount
                // then its sub-suffix. Why???
                while (j > 0) {
                    optoSft[--j] = i;
                }
            }
            // Set the guard value because of unicode compression
            optoSft[patternLength - 1] = 1;
            return new BnM(src, lastOcc, optoSft, node.next);
        }

        BnM(int[] src, int[] lastOcc, int[] optoSft, Node next) {
            this.buffer = src;
            this.lastOcc = lastOcc;
            this.optoSft = optoSft;
            this.next = next;
        }

        boolean match(Matcher matcher, int i, CharSequence seq) {
            int[] src = buffer;
            int patternLength = src.length;
            int last = matcher.to - patternLength;

            // Loop over all possible match positions in text
            NEXT:
            while (i <= last) {
                // Loop over pattern from right to left
                for (int j = patternLength - 1; j >= 0; j--) {
                    int ch = seq.charAt(i + j);
                    if (ch != src[j]) {
                        // Shift search to the right by the maximum of the
                        // bad character shift and the good suffix shift
                        i += Math.max(j + 1 - lastOcc[ch & 0x7F], optoSft[j]);
                        continue NEXT;
                    }
                }
                // Entire pattern matched starting at i
                matcher.first = i;
                boolean ret = next.match(matcher, i + patternLength, seq);
                if (ret) {
                    matcher.first = i;
                    matcher.groups[0] = matcher.first;
                    matcher.groups[1] = matcher.last;
                    return true;
                }
                i++;
            }
            // BnM is only used as the leading node in the unanchored case,
            // and it replaced its Start() which always searches to the end
            // if it doesn't find what it's looking for, so hitEnd is true.
            matcher.hitEnd = true;
            return false;
        }

        boolean study(TreeInfo info) {
            info.minLength += buffer.length;
            info.maxValid = false;
            return next.study(info);
        }
    }

    /**
     * This must be the very first initializer.
     */
    static Node accept = new Node();

    static Node lastAccept = new LastNode();

    private static class CharPropertyNames {

        static CharProperty charPropertyFor(String name) {
            CharPropertyFactory m = map.get(name);
            return m == null ? null : m.make();
        }

        private static abstract class CharPropertyFactory {
            abstract CharProperty make();
        }

        private static void defCategory(String name, final int typeMask) {
            map.put(name, new CharPropertyFactory() {
                CharProperty make() {
                    return new Category(typeMask);
                }
            });
        }

        private static void defRange(String name, final int lower, final int upper) {
            map.put(name, new CharPropertyFactory() {
                CharProperty make() {
                    return rangeFor(lower, upper);
                }
            });
        }

        private static void defCtype(String name, final int ctype) {
            map.put(name, new CharPropertyFactory() {
                CharProperty make() {
                    return new Ctype(ctype);
                }
            });
        }

        private static abstract class CloneableProperty extends CharProperty implements Cloneable {
            public CloneableProperty clone() {
                try {
                    return (CloneableProperty) super.clone();
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }
        }

        private static void defClone(String name, final CloneableProperty p) {
            map.put(name, new CharPropertyFactory() {
                CharProperty make() {
                    return p.clone();
                }
            });
        }

        private static final HashMap<String, CharPropertyFactory> map = new HashMap<>();

        static {
            // Unicode character property aliases, defined in
            // http://www.unicode.org/Public/UNIDATA/PropertyValueAliases.txt
            defCategory("Cn", 1 << Character.UNASSIGNED);
            defCategory("Lu", 1 << Character.UPPERCASE_LETTER);
            defCategory("Ll", 1 << Character.LOWERCASE_LETTER);
            defCategory("Lt", 1 << Character.TITLECASE_LETTER);
            defCategory("Lm", 1 << Character.MODIFIER_LETTER);
            defCategory("Lo", 1 << Character.OTHER_LETTER);
            defCategory("Mn", 1 << Character.NON_SPACING_MARK);
            defCategory("Me", 1 << Character.ENCLOSING_MARK);
            defCategory("Mc", 1 << Character.COMBINING_SPACING_MARK);
            defCategory("Nd", 1 << Character.DECIMAL_DIGIT_NUMBER);
            defCategory("Nl", 1 << Character.LETTER_NUMBER);
            defCategory("No", 1 << Character.OTHER_NUMBER);
            defCategory("Zs", 1 << Character.SPACE_SEPARATOR);
            defCategory("Zl", 1 << Character.LINE_SEPARATOR);
            defCategory("Zp", 1 << Character.PARAGRAPH_SEPARATOR);
            defCategory("Cc", 1 << Character.CONTROL);
            defCategory("Cf", 1 << Character.FORMAT);
            defCategory("Co", 1 << Character.PRIVATE_USE);
            defCategory("Cs", 1 << Character.SURROGATE);
            defCategory("Pd", 1 << Character.DASH_PUNCTUATION);
            defCategory("Ps", 1 << Character.START_PUNCTUATION);
            defCategory("Pe", 1 << Character.END_PUNCTUATION);
            defCategory("Pc", 1 << Character.CONNECTOR_PUNCTUATION);
            defCategory("Po", 1 << Character.OTHER_PUNCTUATION);
            defCategory("Sm", 1 << Character.MATH_SYMBOL);
            defCategory("Sc", 1 << Character.CURRENCY_SYMBOL);
            defCategory("Sk", 1 << Character.MODIFIER_SYMBOL);
            defCategory("So", 1 << Character.OTHER_SYMBOL);
            defCategory("Pi", 1 << Character.INITIAL_QUOTE_PUNCTUATION);
            defCategory("Pf", 1 << Character.FINAL_QUOTE_PUNCTUATION);
            defCategory("L", ((1 << Character.UPPERCASE_LETTER) |
                    (1 << Character.LOWERCASE_LETTER) |
                    (1 << Character.TITLECASE_LETTER) |
                    (1 << Character.MODIFIER_LETTER) |
                    (1 << Character.OTHER_LETTER)));
            defCategory("M", ((1 << Character.NON_SPACING_MARK) |
                    (1 << Character.ENCLOSING_MARK) |
                    (1 << Character.COMBINING_SPACING_MARK)));
            defCategory("N", ((1 << Character.DECIMAL_DIGIT_NUMBER) |
                    (1 << Character.LETTER_NUMBER) |
                    (1 << Character.OTHER_NUMBER)));
            defCategory("Z", ((1 << Character.SPACE_SEPARATOR) |
                    (1 << Character.LINE_SEPARATOR) |
                    (1 << Character.PARAGRAPH_SEPARATOR)));
            defCategory("C", ((1 << Character.CONTROL) |
                    (1 << Character.FORMAT) |
                    (1 << Character.PRIVATE_USE) |
                    (1 << Character.SURROGATE))); // Other
            defCategory("P", ((1 << Character.DASH_PUNCTUATION) |
                    (1 << Character.START_PUNCTUATION) |
                    (1 << Character.END_PUNCTUATION) |
                    (1 << Character.CONNECTOR_PUNCTUATION) |
                    (1 << Character.OTHER_PUNCTUATION) |
                    (1 << Character.INITIAL_QUOTE_PUNCTUATION) |
                    (1 << Character.FINAL_QUOTE_PUNCTUATION)));
            defCategory("S", ((1 << Character.MATH_SYMBOL) |
                    (1 << Character.CURRENCY_SYMBOL) |
                    (1 << Character.MODIFIER_SYMBOL) |
                    (1 << Character.OTHER_SYMBOL)));
            defCategory("LC", ((1 << Character.UPPERCASE_LETTER) |
                    (1 << Character.LOWERCASE_LETTER) |
                    (1 << Character.TITLECASE_LETTER)));
            defCategory("LD", ((1 << Character.UPPERCASE_LETTER) |
                    (1 << Character.LOWERCASE_LETTER) |
                    (1 << Character.TITLECASE_LETTER) |
                    (1 << Character.MODIFIER_LETTER) |
                    (1 << Character.OTHER_LETTER) |
                    (1 << Character.DECIMAL_DIGIT_NUMBER)));
            defRange("L1", 0x00, 0xFF); // Latin-1
            map.put("all", new CharPropertyFactory() {
                CharProperty make() {
                    return new All();
                }
            });

            // Posix regular expression character classes, defined in
            // http://www.unix.org/onlinepubs/009695399/basedefs/xbd_chap09.html
            defRange("ASCII", 0x00, 0x7F);   // ASCII
            defCtype("Alnum", ASCII.ALNUM);  // Alphanumeric characters
            defCtype("Alpha", ASCII.ALPHA);  // Alphabetic characters
            defCtype("Blank", ASCII.BLANK);  // Space and tab characters
            defCtype("Cntrl", ASCII.CNTRL);  // Control characters
            defRange("Digit", '0', '9');     // Numeric characters
            defCtype("Graph", ASCII.GRAPH);  // printable and visible
            defRange("Lower", 'a', 'z');     // Lower-case alphabetic
            defRange("Print", 0x20, 0x7E);   // Printable characters
            defCtype("Punct", ASCII.PUNCT);  // Punctuation characters
            defCtype("Space", ASCII.SPACE);  // Space characters
            defRange("Upper", 'A', 'Z');     // Upper-case alphabetic
            defCtype("XDigit", ASCII.XDIGIT); // hexadecimal digits

            // Java character properties, defined by methods in Character.java
            defClone("javaLowerCase", new CloneableProperty() {
                boolean isSatisfiedBy(int ch) {
                    return Character.isLowerCase(ch);
                }
            });
            defClone("javaUpperCase", new CloneableProperty() {
                boolean isSatisfiedBy(int ch) {
                    return Character.isUpperCase(ch);
                }
            });
            defClone("javaAlphabetic", new CloneableProperty() {
                boolean isSatisfiedBy(int ch) {
                    return Character.isAlphabetic(ch);
                }
            });
            defClone("javaIdeographic", new CloneableProperty() {
                boolean isSatisfiedBy(int ch) {
                    return Character.isIdeographic(ch);
                }
            });
            defClone("javaTitleCase", new CloneableProperty() {
                boolean isSatisfiedBy(int ch) {
                    return Character.isTitleCase(ch);
                }
            });
            defClone("javaDigit", new CloneableProperty() {
                boolean isSatisfiedBy(int ch) {
                    return Character.isDigit(ch);
                }
            });
            defClone("javaDefined", new CloneableProperty() {
                boolean isSatisfiedBy(int ch) {
                    return Character.isDefined(ch);
                }
            });
            defClone("javaLetter", new CloneableProperty() {
                boolean isSatisfiedBy(int ch) {
                    return Character.isLetter(ch);
                }
            });
            defClone("javaLetterOrDigit", new CloneableProperty() {
                boolean isSatisfiedBy(int ch) {
                    return Character.isLetterOrDigit(ch);
                }
            });
            defClone("javaJavaIdentifierStart", new CloneableProperty() {
                boolean isSatisfiedBy(int ch) {
                    return Character.isJavaIdentifierStart(ch);
                }
            });
            defClone("javaJavaIdentifierPart", new CloneableProperty() {
                boolean isSatisfiedBy(int ch) {
                    return Character.isJavaIdentifierPart(ch);
                }
            });
            defClone("javaUnicodeIdentifierStart", new CloneableProperty() {
                boolean isSatisfiedBy(int ch) {
                    return Character.isUnicodeIdentifierStart(ch);
                }
            });
            defClone("javaUnicodeIdentifierPart", new CloneableProperty() {
                boolean isSatisfiedBy(int ch) {
                    return Character.isUnicodeIdentifierPart(ch);
                }
            });
            defClone("javaIdentifierIgnorable", new CloneableProperty() {
                boolean isSatisfiedBy(int ch) {
                    return Character.isIdentifierIgnorable(ch);
                }
            });
            defClone("javaSpaceChar", new CloneableProperty() {
                boolean isSatisfiedBy(int ch) {
                    return Character.isSpaceChar(ch);
                }
            });
            defClone("javaWhitespace", new CloneableProperty() {
                boolean isSatisfiedBy(int ch) {
                    return Character.isWhitespace(ch);
                }
            });
            defClone("javaISOControl", new CloneableProperty() {
                boolean isSatisfiedBy(int ch) {
                    return Character.isISOControl(ch);
                }
            });
            defClone("javaMirrored", new CloneableProperty() {
                boolean isSatisfiedBy(int ch) {
                    return Character.isMirrored(ch);
                }
            });
        }
    }

}
