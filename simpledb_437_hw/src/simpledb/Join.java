package simpledb;
import java.util.*;
import java.io.*;

/**
 * The Join operator implements the relational join operation.
 */
public class Join extends AbstractDbIterator {

    private JoinPredicate _predicate;
    private DbIterator _outerRelation;
    private DbIterator _innerRelation;
    private Iterator<Tuple> _outerPage=null;
    private Iterator<Tuple> _innerPage=null;
    private ArrayList<Tuple> store = null; // keep all the prev matching cases
    private Tuple _outerRecent=null;
    private Tuple _innerRecent=null;

    private int _joinType = 0;
    private int _numMatches =0;
    private int _numComp=0;
  
    public static final int SNL = 0;
    public static final int PNL = 1;    
    public static final int BNL = 2;    
    public static final int SMJ = 3;    
    public static final int HJ = 4;    
    /**
     * Constructor.  Accepts to children to join and the predicate
     * to join them on
     *
     * @param p The predicate to use to join the children
     * @param child1 Iterator for the left(outer) relation to join
     * @param child2 Iterator for the right(inner) relation to join
     */
    public Join(JoinPredicate p, DbIterator child1, DbIterator child2) {
	//IMPLEMENT THIS
        _predicate = p;
        _outerRelation = child1;
        _innerRelation = child2;

    }

    public void setJoinAlgorithm(int joinAlgo){
	_joinType = joinAlgo;
    }
    /**
     * @see simpledb.TupleDesc#combine(TupleDesc, TupleDesc) for possible implementation logic.
     */
    public TupleDesc getTupleDesc() {
	//IMPLEMENT THIS
        return TupleDesc.combine(_outerRelation.getTupleDesc(),_innerRelation.getTupleDesc());
	//return null;
    }

    public void open()
        throws DbException, NoSuchElementException, TransactionAbortedException, IOException {
		//IMPLEMENT THIS

        _outerRelation.open();
        _innerRelation.open();
    }

    public void close() {
//IMPLEMENT THIS
        _innerRelation.close();
        _outerRelation.close();

    }

    public void rewind() throws DbException, TransactionAbortedException, IOException {
//IMPLEMENT THIS
        _innerRelation.rewind();
        _outerRelation.rewind();

    }

    /**
     * Returns the next tuple generated by the join, or null if there are no more tuples.
     * Logically, this is the next tuple in r1 cross r2 that satisfies the join
     * predicate.  There are many possible implementations; the simplest is a
     * nested loops join.
     * <p>
     * Note that the tuples returned from this particular implementation of
     * Join are simply the concatenation of joining tuples from the left and
     * right relation. Therefore, there will be two copies of the join attribute
     * in the results.  (Removing such duplicate columns can be done with an
     * additional projection operator if needed.)
     * <p>
     * For example, if one tuple is {1,2,3} and the other tuple is {1,5,6},
     * joined on equality of the first column, then this returns {1,2,3,1,5,6}.
     *
     * @return The next matching tuple.
     * @see JoinPredicate#filter
     */
    protected Tuple readNext() throws TransactionAbortedException, DbException, IOException {
	switch(_joinType){
	case SNL: return SNL_readNext();
	case PNL: return PNL_readNext();
	case BNL: return BNL_readNext();
	case SMJ: return SMJ_readNext();
	case HJ: return HJ_readNext();
	default: return SNL_readNext();
	}
    }

    protected Tuple SNL_readNext() throws TransactionAbortedException, DbException {
	//IMPLEMENT THIS

        try {
//            if (_outerRecent == null) {
//                if (!_outerRelation.hasNext()) _outerRelation.rewind();
//                _outerRecent = _outerRelation.next();
//            }
//            if (_innerRecent == null) {
//                if (!_innerRelation.hasNext()) _innerRelation.rewind();
//                _innerRecent = _innerRelation.next();
//            }
//            Tuple joined = null;
//
//            do {
//                _numComp++;
//                if(_predicate.filter(_outerRecent, _innerRecent)) {
//                    _numMatches++;
//                    joined = joinTuple(_outerRecent, _innerRecent, this.getTupleDesc());
//                }
//
//                if(_innerRelation.hasNext()) {
//                    _innerRecent = _innerRelation.next();
//                } else {
//                    _innerRelation.rewind();
//                    if(_outerRelation.hasNext()) {
//                        _outerRelation.next();
//                    } else break;
//                    _innerRecent = _innerRelation.next();
//                }
//
//                if (joined != null) return joined;
//
//            } while (_outerRelation.hasNext());
//            _outerRecent = null;
//            _innerRecent = null;

            while (_outerRecent != null || _outerRelation.hasNext()){
                Tuple left;
                if(_outerRecent !=null){
                    left = _outerRecent;
                } else{
                    _outerRecent = _outerRelation.next();
                    left = _outerRecent;
                }
                while (_innerRelation.hasNext()){

                    Tuple right = _innerRelation.next();
                    _numComp++;
                    if(_predicate.filter(left,right)){

                        //return SetMergeField(left, right);
                        _numMatches++;
                        return joinTuple(left,right,this.getTupleDesc());
                    }

                }
                _outerRecent = null;
                _innerRelation.rewind();
            }
        } catch (IOException e) {
            throw new DbException("IOException " + e.getMessage());
        }
        return null;
    }

    protected Tuple PNL_readNext() throws TransactionAbortedException, DbException {
	//IMPLEMENT THIS (EXTRA CREDIT ONLY)
	return null;
    }


    protected Tuple BNL_readNext() throws TransactionAbortedException, DbException {
	//no need to implement this
	return null;
    }


    protected Tuple SMJ_readNext() throws TransactionAbortedException, DbException {
	
	//IMPLEMENT THIS. YOU CAN ASSUME THE JOIN PREDICATE IS ALWAYS =
        Predicate.Op op;
        store = new ArrayList<Tuple>();

        try {
            while (!_innerRelation.hasNext() && !_outerRelation.hasNext()){

                _predicate.filter(_outerRelation.next(),_innerRelation.next());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    protected Tuple HJ_readNext() throws TransactionAbortedException, DbException {
	//no need to implement this
	return null;
    }


    private Tuple joinTuple(Tuple outer, Tuple inner, TupleDesc tupledesc){
	//IMPLEMENT THIS
        int totalSize = (outer.getTupleDesc().numFields()) + (inner.getTupleDesc().numFields());
        Tuple ret = new Tuple(this.getTupleDesc());
        int j = 0;
        for(int i = 0; i < totalSize;i++){
            if(i < outer.getTupleDesc().numFields()) {
                ret.setField(i, outer.getField(i));
            }
            else {
                ret.setField(i,inner.getField(j));
                j++;
            }
        }
        return ret;
    }

    public int getNumMatches(){
	return _numMatches;
    }
    public int getNumComp(){
	return _numComp;
    }
}
