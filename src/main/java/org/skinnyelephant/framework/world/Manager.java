package org.skinnyelephant.framework.world;

/** Interface for managers in this framework. */
public interface Manager extends Disposable {
    /**
     * <p>Initializes manager.</p>
     * <p> This method is called by {@link World} class after the manager is added to system and its not recommended to call it externally.</p>
     */
    void initialize();
}
