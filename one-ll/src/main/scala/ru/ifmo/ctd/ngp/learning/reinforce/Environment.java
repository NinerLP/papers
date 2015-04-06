package ru.ifmo.ctd.ngp.learning.reinforce;

import java.util.List;


/**
 * The environment which interacts with the {@link Agent}.
 * It rewards the agent for his actions.
 * After each agent's action the state of the environment changes.
 * 
 * @author Arina Buzdalova
 * @param <S> type of a state
 * @param <A> type of an action
 */
public interface Environment<S, A> {
	
	/**
	 * Applies the specified action, moves to a new state and returns reward
	 * based on the action and the previous state of this environment
	 * @param action the action to be applied
	 * @return the reward for the <code>action</code>
	 */
	public double applyAction(A action);
	
	/**
	 * Returns the current state of this environment
	 * @return the current state of this environment
	 */
	public S getCurrentState();
	
	/**
	 * Gets the set of actions that can be performed over this environment
	 * @return the set of actions that can be performed
	 */
	public List<A> getActions();
	
	/**
	 * Returns the number of actions that can be performed over this environment
	 * @return the number of actions
	 */
	public int actionsCount();
	
	/**
	 * <p>
	 * Gets the action that is recommended to be applied first
	 * </p><p>
	 * TODO: Is this method needed?
	 * </p>
	 * @return the action that is recommended to be applied first
	 */
	public A firstAction();
	
	/**
	 * Returns <code>true</code> if this environment is in terminal state, 
	 * <code>false</code> if not. 
	 * Terminal state signals that {@link Agent} can stop acting.
	 * @return <code>true</code> if this environment is in terminal state, 
	 * <code>false</code> if not
	 */
	public boolean isInTerminalState();
	
	/**
	 * Subscribes printer that allows to observe some properties of this environment
	 * @param printer the printer to be subscribed
	 */
	public void addPrinter(EnvironmentPrinter<S, A> printer);
	
	/**
	 * Gets the last reward returned to an agent
	 * @return the last reward returned to an agent
	 */
	public double getLastReward();
	
}
