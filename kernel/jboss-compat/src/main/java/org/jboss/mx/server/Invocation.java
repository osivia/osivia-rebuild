package org.jboss.mx.server;

import org.jboss.mx.interceptor.AbstractInterceptor;

public class Invocation extends InvocationContext {
	private Object[] args;
	private InvocationContext ctx;
	int ic_counter = 0;
	Object retVal = null;

	public Invocation() {
	}

	public Invocation(InvocationContext ic) {
		this.addContext(ic);
	}

	public void addContext(InvocationContext ctx) {
		super.copy(ctx);
		this.ctx = ctx;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Object[] getArgs() {
		return this.args;
	}

	public AbstractInterceptor nextInterceptor() {
		if (this.interceptors == null) {
			return null;
		} else {
			return this.ic_counter < this.interceptors.size()
					? (AbstractInterceptor) this.interceptors.get(this.ic_counter++)
					: null;
		}
	}

	public Object invoke() throws Throwable {
		AbstractInterceptor ic = this.nextInterceptor();
		return ic == null ? this.dispatch() : ic.invoke(this);
	}

	public Object dispatch() throws Throwable {
		return this.dispatcher.invoke(this);
	}

	public String toString() {
		return this.getName() + " " + this.getType();
	}

	public Class getAttributeTypeClass() throws ClassNotFoundException {
		return this.ctx.getAttributeTypeClass();
	}

	public Class getReturnTypeClass() throws ClassNotFoundException {
		return this.ctx.getReturnTypeClass();
	}

	public Class[] getSignatureClasses() throws ClassNotFoundException {
		return this.ctx.getSignatureClasses();
	}
}