package com.date.parser.regexp;

/**
 * An engine that performs match operations on a {@linkplain CharSequence
 * character sequence} by interpreting a {@link Pattern}.
 *
 * <p> A matcher is created from a pattern by invoking the pattern's {@link
 * Pattern#matcher matcher} method.  Once created, a matcher can be used to
 * perform three different kinds of match operations:
 *
 * <p> Instances of this class are not safe for use by multiple concurrent
 * threads. </p>
 *
 * @author Mike McCloskey
 * @author Mark Reinhold
 * @author JSR-51 Expert Group
 * @since 1.4
 */
public final class Matcher {

    /**
     * The Pattern object that created this Matcher.
     */
    Pattern parentPattern;

    /**
     * The storage used by groups. They may contain invalid values if
     * a group was skipped during the matching.
     */
    int[] groups;

    /**
     * The range within the sequence that is to be matched. Anchors
     * will match at these "hard" boundaries. Changing the region
     * changes these values.
     */
    int from, to;

    /**
     * Lookbehind uses this value to ensure that the subexpression
     * match ends at the point where the lookbehind was encountered.
     */
    int lookbehindTo;

    /**
     * The original string being matched.
     */
    CharSequence text;

    /**
     * Matcher state used by the last node. NOANCHOR is used when a
     * match does not have to consume all of the input. ENDANCHOR is
     * the mode used for matching all the input.
     */
    static final int ENDANCHOR = 1;
    static final int NOANCHOR = 0;
    int acceptMode = NOANCHOR;

    /**
     * The range of string that last matched the pattern. If the last
     * match failed then first is -1; last initially holds 0 then it
     * holds the index of the end of the last match (which is where the
     * next search starts).
     */
    int first = -1, last = 0;

    /**
     * The end index of what matched in the last match operation.
     */
    int oldLast = -1;

    /**
     * The index of the last position appended in a substitution.
     */
    int lastAppendPosition = 0;

    /**
     * Storage used by nodes to tell what repetition they are on in
     * a pattern, and where groups begin. The nodes themselves are stateless,
     * so they rely on this field to hold state during a match.
     */
    int[] locals;

    /**
     * Boolean indicating whether or not more input could change
     * the results of the last match.
     * <p>
     * If hitEnd is true, and a match was found, then more input
     * might cause a different match to be found.
     * If hitEnd is true and a match was not found, then more
     * input could cause a match to be found.
     * If hitEnd is false and a match was found, then more input
     * will not change the match.
     * If hitEnd is false and a match was not found, then more
     * input will not cause a match to be found.
     */
    boolean hitEnd;

    /**
     * Boolean indicating whether or not more input could change
     * a positive match into a negative one.
     * <p>
     * If requireEnd is true, and a match was found, then more
     * input could cause the match to be lost.
     * If requireEnd is false and a match was found, then more
     * input might change the match but the match won't be lost.
     * If a match was not found, then requireEnd has no meaning.
     */
    boolean requireEnd;

    /**
     * If transparentBounds is true then the boundaries of this
     * matcher's region are transparent to lookahead, lookbehind,
     * and boundary matching constructs that try to see beyond them.
     */
    boolean transparentBounds = false;

    /**
     * If anchoringBounds is true then the boundaries of this
     * matcher's region match anchors such as ^ and $.
     */
    boolean anchoringBounds = true;

    /**
     * All matchers have the state used by Pattern during a match.
     */
    Matcher(Pattern parent, CharSequence text) {
        this.parentPattern = parent;
        this.text = text;

        // Allocate state storage
        int parentGroupCount = Math.max(parent.capturingGroupCount, 10);
        groups = new int[parentGroupCount * 2];
        locals = new int[parent.localCount];

        // Put fields into initial states
        reset();
    }

    /**
     * Resets this matcher.
     *
     * <p> Resetting a matcher discards all of its explicit state information
     * and sets its append position to zero. The matcher's region is set to the
     * default region, which is its entire character sequence. The anchoring
     * and transparency of this matcher's region boundaries are unaffected.
     *
     * @return This matcher
     */
    public Matcher reset() {
        first = -1;
        last = 0;
        oldLast = -1;
        for (int i = 0; i < groups.length; i++)
            groups[i] = -1;
        for (int i = 0; i < locals.length; i++)
            locals[i] = -1;
        lastAppendPosition = 0;
        from = 0;
        to = getTextLength();
        return this;
    }

    /**
     * Returns the start index of the previous match.
     *
     * @return The index of the first character matched
     * @throws IllegalStateException If no match has yet been attempted,
     *                               or if the previous match operation failed
     */
    public int start() {
        if (first < 0)
            throw new IllegalStateException("No match available");
        return first;
    }

    /**
     * Returns the offset after the last character matched.
     *
     * @return The offset after the last character matched
     * @throws IllegalStateException If no match has yet been attempted,
     *                               or if the previous match operation failed
     */
    public int end() {
        if (first < 0)
            throw new IllegalStateException("No match available");
        return last;
    }

    /**
     * Attempts to match the entire region against the pattern.
     *
     * <p> If the match succeeds then more information can be obtained via the
     * <tt>start</tt>, <tt>end</tt>, and <tt>group</tt> methods.  </p>
     *
     * @return <tt>true</tt> if, and only if, the entire region sequence
     * matches this matcher's pattern
     */
    public boolean matches() {
        return match(from, ENDANCHOR);
    }

    /**
     * Attempts to find the next subsequence of the input sequence that matches
     * the pattern.
     *
     * <p> This method starts at the beginning of this matcher's region, or, if
     * a previous invocation of the method was successful and the matcher has
     * not since been reset, at the first character not matched by the previous
     * match.
     *
     * <p> If the match succeeds then more information can be obtained via the
     * <tt>start</tt>, <tt>end</tt>, and <tt>group</tt> methods.  </p>
     *
     * @return <tt>true</tt> if, and only if, a subsequence of the input
     * sequence matches this matcher's pattern
     */
    public boolean find() {
        int nextSearchIndex = last;
        if (nextSearchIndex == first)
            nextSearchIndex++;

        // If next search starts before region, start it at region
        if (nextSearchIndex < from)
            nextSearchIndex = from;

        // If next search starts beyond region then it fails
        if (nextSearchIndex > to) {
            for (int i = 0; i < groups.length; i++)
                groups[i] = -1;
            return false;
        }
        return search(nextSearchIndex);
    }

    /**
     * Resets this matcher and then attempts to find the next subsequence of
     * the input sequence that matches the pattern, starting at the specified
     * index.
     *
     * <p> If the match succeeds then more information can be obtained via the
     * <tt>start</tt>, <tt>end</tt>, and <tt>group</tt> methods, and subsequent
     * invocations of the {@link #find()} method will start at the first
     * character not matched by this match.  </p>
     *
     * @param start the index to start searching for a match
     * @return <tt>true</tt> if, and only if, a subsequence of the input
     * sequence starting at the given index matches this matcher's
     * pattern
     * @throws IndexOutOfBoundsException If start is less than zero or if start is greater than the
     *                                   length of the input sequence.
     */
    public boolean find(int start) {
        int limit = getTextLength();
        if ((start < 0) || (start > limit))
            throw new IndexOutOfBoundsException("Illegal start index");
        reset();
        return search(start);
    }

    /**
     * Initiates a search to find a Pattern within the given bounds.
     * The groups are filled with default values and the match of the root
     * of the state machine is called. The state machine will hold the state
     * of the match as it proceeds in this matcher.
     * <p>
     * Matcher.from is not set here, because it is the "hard" boundary
     * of the start of the search which anchors will set to. The from param
     * is the "soft" boundary of the start of the search, meaning that the
     * regex tries to match at that index but ^ won't match there. Subsequent
     * calls to the search methods start at a new "soft" boundary which is
     * the end of the previous match.
     */
    boolean search(int from) {
        this.hitEnd = false;
        this.requireEnd = false;
        from = from < 0 ? 0 : from;
        this.first = from;
        this.oldLast = oldLast < 0 ? from : oldLast;
        for (int i = 0; i < groups.length; i++)
            groups[i] = -1;
        acceptMode = NOANCHOR;
        boolean result = parentPattern.root.match(this, from, text);
        if (!result)
            this.first = -1;
        this.oldLast = this.last;
        return result;
    }

    /**
     * Initiates a search for an anchored match to a Pattern within the given
     * bounds. The groups are filled with default values and the match of the
     * root of the state machine is called. The state machine will hold the
     * state of the match as it proceeds in this matcher.
     */
    boolean match(int from, int anchor) {
        this.hitEnd = false;
        this.requireEnd = false;
        from = from < 0 ? 0 : from;
        this.first = from;
        this.oldLast = oldLast < 0 ? from : oldLast;
        for (int i = 0; i < groups.length; i++)
            groups[i] = -1;
        acceptMode = anchor;
        boolean result = parentPattern.matchRoot.match(this, from, text);
        if (!result)
            this.first = -1;
        this.oldLast = this.last;
        return result;
    }

    /**
     * Returns the end index of the text.
     *
     * @return the index after the last character in the text
     */
    int getTextLength() {
        return text.length();
    }
}
