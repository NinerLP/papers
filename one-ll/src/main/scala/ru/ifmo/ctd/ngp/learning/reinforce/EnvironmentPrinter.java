package ru.ifmo.ctd.ngp.learning.reinforce;

import java.io.IOException;

public interface EnvironmentPrinter<S, A> {
	
	public void print(Environment<S, A> env) throws IOException;

}
