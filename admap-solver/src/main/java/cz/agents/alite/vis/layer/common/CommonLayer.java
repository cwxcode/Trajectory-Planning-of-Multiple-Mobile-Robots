package cz.agents.alite.vis.layer.common;

import cz.agents.alite.vis.layer.AbstractLayer;
import cz.agents.alite.vis.layer.terminal.TerminalLayer;
import cz.agents.alite.vis.layer.toggle.KeyToggleLayer;

/**
 * A CommonLayer is typically a wrapper or composition of several
 * {@link TerminalLayer}s.
 *
 * Each non-terminal layer, should be a CommonLayer. The common layers typically
 * have various create() factory methods, to cover the needs of the layer users.
 * The create() method(s) of a common layer typically do not return a object of
 * the type the particular layer, but rather a toggle layer, which turns on and off the layer
 * e.g., by a key. For example, the create() method of the {@link FpsLayer}
 * do not return <code>new {@link FpsLayer}(...)</code>, but a new instance of
 * the {@link KeyToggleLayer}, that shows the layer by pressing the 'f' key.
 *
 *
 * @author Antonin Komenda
 */
public abstract class CommonLayer extends AbstractLayer {
}
