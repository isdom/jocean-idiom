/**
 * 
 */
package org.jocean.idiom.stats;

import java.util.concurrent.atomic.AtomicInteger;

import org.jocean.idiom.ReflectUtils;

import rx.functions.Func1;

/**
 * @author isdom
 *
 */
public abstract class BizMemoImpl<IMPL extends BizMemoImpl<IMPL,STEP,RESULT>, 
    STEP extends Enum<STEP>, RESULT extends Enum<RESULT>> 
    implements BizMemo<STEP,RESULT> {
    
    public BizMemoImpl(final Class<STEP> clsStep, final Class<RESULT> clsResult) {
        this._clsStep = clsStep;
        this._clsResult = clsResult;
        this._steps = ReflectUtils.getValuesOf(clsStep);
        this._stepCounters = new AtomicInteger[this._steps.length];
        this._stepMemos = new TimeIntervalMemo[this._steps.length];
        this._results = ReflectUtils.getValuesOf(clsResult);
        this._resultCounters = new AtomicInteger[this._results.length];
        this._resultMemos = new TimeIntervalMemo[this._results.length];
        initCountersAndMemos(this._stepCounters, this._stepMemos);
        initCountersAndMemos(this._resultCounters, this._resultMemos);
    }

    private static void initCountersAndMemos(
            final AtomicInteger[] counters, 
            final TimeIntervalMemo[] memos) {
        for ( int idx = 0; idx < counters.length; idx++) {
            counters[idx] = new AtomicInteger(0);
            memos[idx] = TimeIntervalMemo.NOP;
        }
    }

    @Override
    public void beginBizStep(final STEP step) {
        this._stepCounters[step.ordinal()].incrementAndGet();
    }

    @Override
    public void endBizStep(final STEP step, final long ttl) {
        this._stepCounters[step.ordinal()].decrementAndGet();
        this._stepMemos[step.ordinal()].recordInterval(ttl);
    }

    @Override
    public void incBizResult(final RESULT result, final long ttl) {
        this._resultCounters[result.ordinal()].incrementAndGet();
        this._resultMemos[result.ordinal()].recordInterval(ttl);
    }
    
    protected AtomicInteger step2Counter(final STEP step) {
        return this._stepCounters[step.ordinal()];
    }
    
    protected AtomicInteger result2Counter(final RESULT result) {
        return this._resultCounters[result.ordinal()];
    }
    
    @SuppressWarnings("unchecked")
    public IMPL setTimeIntervalMemoOfStep(final STEP step, final TimeIntervalMemo memo) {
        this._stepMemos[step.ordinal()] = memo;
        return (IMPL)this;
    }
    
    @SuppressWarnings("unchecked")
    public IMPL setTimeIntervalMemoOfResult(final RESULT result, final TimeIntervalMemo memo) {
        this._resultMemos[result.ordinal()] = memo;
        return (IMPL)this;
    }
    
    @SuppressWarnings("unchecked")
    public IMPL fillTimeIntervalMemoWith(final Func1<Enum<?>, TimeIntervalMemo> enum2memo) {
        fillTTLMemosWith(this._steps, this._stepMemos, enum2memo);
        fillTTLMemosWith(this._results, this._resultMemos, enum2memo);
        return (IMPL)this;
    }

    /**
     * @param enum2memo
     */
    private static <E extends Enum<E>> void fillTTLMemosWith(
            final E[] enums,
            final TimeIntervalMemo[] memos,
            final Func1<Enum<?>, TimeIntervalMemo> enum2memo) {
        for ( E e : enums ) {
            final TimeIntervalMemo memo = enum2memo.call(e);
            if (null != memo) {
                memos[e.ordinal()] = memo;
            }
        }
    }
    
    protected AtomicInteger name2Integer(final String type, final String name) {
        final STEP step = enumOf( this._clsStep, type, name);
        return ( null != step ) ? this._stepCounters[step.ordinal()] 
                : this._resultCounters[enumOf(this._clsResult, type, name).ordinal()];
    }
    
    private <E extends Enum<E>> E enumOf(
            final Class<E> cls, 
            final String type,
             final String name ) {
        return cls.getSimpleName().equals(type) ? E.valueOf(cls, name) : null;
    }
    
    protected final Class<STEP> _clsStep;
    protected final Class<RESULT> _clsResult;
    protected final STEP[] _steps;
    protected final RESULT[] _results;
    protected final AtomicInteger[] _stepCounters;
    protected final TimeIntervalMemo[] _stepMemos;
    protected final AtomicInteger[] _resultCounters;
    protected final TimeIntervalMemo[] _resultMemos;
}
