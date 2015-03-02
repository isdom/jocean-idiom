package org.jocean.idiom.stats;

import org.jocean.idiom.StopWatch;



public interface BizMemo<STEP extends Enum<STEP>, RESULT extends Enum<RESULT>> {
    public void beginBizStep(final STEP step);
    public void endBizStep(final STEP step, final long ttl);
    
    public void incBizResult(final RESULT result, final long ttl);
    
    public interface StepMemo<STEP extends Enum<STEP>> {
        public void beginBizStep(final STEP step);
        public void endBizStep(final long ttl);
        public void endBizStep();
    }
    
    public static class Util {
	    public static <STEP extends Enum<STEP>> StepMemo<STEP> buildStepMemo(
	    		final BizMemo<STEP,?> memo, final StopWatch stopWatch) {
	    	return new StepMemo<STEP>() {
				@Override
				public void beginBizStep(final STEP step) {
			    	endBizStep();
			    	this._current = step;
			        memo.beginBizStep(step);
			    }
				@Override
			    public void endBizStep(final long ttl) {
			    	if (null != this._current) {
				    	memo.endBizStep(this._current, ttl);
				    	this._current = null;
			    	}
			    }
				@Override
			    public void endBizStep() {
			    	if (null != this._current) {
			    		endBizStep(stopWatch.stopAndRestart());
			    	}
				}
			    private STEP _current = null;
			};
	    }
    }
}
