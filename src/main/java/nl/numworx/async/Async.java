package nl.numworx.async;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import org.osgi.util.promise.Promise;

public class Async {

	private ThreadLocal<AsyncRunner> runner = new ThreadLocal<AsyncRunner>();
	Executor executor = ForkJoinPool.commonPool();
	
	public Executor getExecutor() {
		return executor;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	@SuppressWarnings("unchecked")
	<T> T mediate(T object, Class<T> clz) {		
		ClassLoader loader = object.getClass().getClassLoader(); // ???
		InvocationHandler h = new AsyncMediator(this, object);
		Class<?>[] interfaces = new Class<?>[] { clz };
		return (T) Proxy.newProxyInstance(loader, interfaces, h);
	}

	public void setRunner(AsyncRunner asyncRunner) {
		runner.set(asyncRunner);
	}
	
	@SuppressWarnings("unchecked")
	public <T> Promise<T> call(T noresult) {
		executor.execute(getRunner());
		return (Promise<T>) getRunner().getPromise();
	}

	@SuppressWarnings("unchecked")
	public Promise<Integer> call(int noresult) {
		AsyncRunner run = getRunner();
		executor.execute(run);
		return (Promise<Integer>) run.getPromise();
	}
	@SuppressWarnings("unchecked")
	public Promise<Character> call(char noresult) {
		AsyncRunner run = getRunner();
		executor.execute(run);
		return (Promise<Character>) run.getPromise();
	}
	@SuppressWarnings("unchecked")
	public Promise<Short> call(short noresult) {
		AsyncRunner run = getRunner();
		executor.execute(run);
		return (Promise<Short>) run.getPromise();
	}
	@SuppressWarnings("unchecked")
	public Promise<Double> call(double noresult) {
		AsyncRunner run = getRunner();
		executor.execute(run);
		return (Promise<Double>) run.getPromise();
	}
	@SuppressWarnings("unchecked")
	public Promise<Float> call(float noresult) {
		AsyncRunner run = getRunner();
		executor.execute(run);
		return (Promise<Float>) run.getPromise();
	}
	@SuppressWarnings("unchecked")
	public Promise<Long> call(long noresult) {
		AsyncRunner run = getRunner();
		executor.execute(run);
		return (Promise<Long>) run.getPromise();
	}

	@SuppressWarnings("unchecked")
	public Promise<Boolean> call(boolean noresult) {
		AsyncRunner run = getRunner();
		executor.execute(run);
		return (Promise<Boolean>) run.getPromise();
	}

	public Promise<?> call() {
		AsyncRunner run = getRunner();
		executor.execute(run);
		return run.getPromise();
	}
	
	private AsyncRunner getRunner() {
		try {
			return runner.get();
		} finally {
			setRunner(null);
		}
	}
	
}
