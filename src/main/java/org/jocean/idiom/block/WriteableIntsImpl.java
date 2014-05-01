/**
 * 
 */
package org.jocean.idiom.block;

import java.util.List;

import org.jocean.idiom.ConcurrentInvokeGuard;
import org.jocean.idiom.Pair;
import org.jocean.idiom.pool.IntsPool;
import org.jocean.idiom.pool.ObjectPool.Ref;


/**
 * @author isdom
 *
 */
final class WriteableIntsImpl implements WriteableInts {
    
    public WriteableIntsImpl(final IntsPool pool) {
        this._support = new BlocksWriteableSupport<int[]>(pool);
    }
    
    public IntsPool pool() {
        return this._support.pool();
    }
    
    @Override
    public IntsBlob drainToIntsBlob() {
        this._guard.enter(null);
        
        try {
            final Pair<List<Ref<int[]>>,Integer> blocks = this._support.exportBlocks();
            if ( null == blocks) {
                return null;
            }
            final IntsBlobImpl blob = new IntsBlobImpl(blocks.getFirst(), blocks.getSecond(), this.pool());
            this._support.clear();
            return blob;
        }
        finally {
            this._guard.leave(null);
        }
    }
    
    @Override
    public void write(final int data) {
        this._guard.enter(null);
        
        try {
            final int[] currentInts = this._support.currentBlock();
            currentInts[this._support.getWritePositionInBlockAndIncrement()] = data;
        }
        finally {
            this._guard.leave(null);
        }
    }
    
    @Override
    public void close() {
        this._guard.enter(null);
        
        try {
            this._support.clear();
        }
        finally {
            this._guard.leave(null);
        }
    }
    
    private final BlocksWriteableSupport<int[]> _support;
    private final ConcurrentInvokeGuard _guard = new ConcurrentInvokeGuard();
}
