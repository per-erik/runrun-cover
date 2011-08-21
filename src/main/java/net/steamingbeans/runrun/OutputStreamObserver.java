/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.steamingbeans.runrun;

/**
 *
 * @author Per-Erik
 */
public interface OutputStreamObserver<T> {
    public void flushing(T t);
}
