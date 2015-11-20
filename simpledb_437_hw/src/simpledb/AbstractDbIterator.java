package simpledb;

import java.io.IOException;
import java.util.NoSuchElementException;

/** Helper for implementing DbIterators. Handles hasNext()/next() logic and
 *  throwing exceptions if open()/close() are abused. */
public abstract class AbstractDbIterator implements DbIterator {
    @Override
    public boolean hasNext() throws DbException, TransactionAbortedException,IOException {
        if (next == null) next = readNext();
        return next != null;
    }

    @Override
    public Tuple next() throws
            DbException, TransactionAbortedException, NoSuchElementException {
        if (next == null) {
            try {
                next = readNext();
            } catch (IOException e) {
                throw new DbException("Could not read next");
            }
            if (next == null) throw new NoSuchElementException();
        }

        Tuple result = next;
        next = null;
        return result;
    }

    /** @return the next Tuple in the iterator, null if the iteration is finished. */
    protected abstract Tuple readNext() throws DbException, TransactionAbortedException,IOException;

    /** If subclasses override this, they should call super.close(). */
    //    @Override
    public void close() {
        // Ensures that a future call to next() will fail
        next = null;
    }

    private Tuple next = null;
}
