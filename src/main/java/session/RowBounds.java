package session;

/**
 * @author Bridge-cut
 * @version 1.0
 * @date 2020/12/10 上午10:33
 */
public class RowBounds {

    public static final int NO_ROW_OFFSET = 0;
    public static final int NO_ROW_LIMIT = Integer.MAX_VALUE;
    public static final RowBounds DEFAULT = new RowBounds();


    private final int offset;
    private final int limit;

    public RowBounds() {
        this.limit = NO_ROW_LIMIT;
        this.offset =NO_ROW_OFFSET;
    }

    public RowBounds(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }
}
