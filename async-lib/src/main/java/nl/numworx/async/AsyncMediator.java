package nl.numworx.async;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.osgi.util.promise.Deferred;

public class AsyncMediator implements InvocationHandler {

	static final Integer NUL = Integer.valueOf(0);
	static final Long NULL = Long.valueOf(0);
	static final Double NULD = Double.valueOf(0);
	static final Character NULC = Character.valueOf((char) 0);
	static final Short NULS = Short.valueOf((short) 0);
	static final Float NULF = Float.valueOf(0);
	private Async async;
	private Object org;
	
	private Object nul(Class<?> cls) {
// zeven primitieve classen
		if(cls == Integer.TYPE) return NUL;
		if(cls == Long.TYPE) return NULL;
		if(cls == Double.TYPE) return NULD;
		if(cls == Boolean.TYPE) return Boolean.FALSE;
		if(cls == Short.TYPE) return NULS;
		if(cls == Float.TYPE) return NULF;
		if(cls == Character.TYPE) return NULC;
		return null;
	}
	
	
	
	public AsyncMediator(Async async, Object org) {
		this.async = async;
		this.org = org;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if("toString".equals(method.getName()) && (args == null || args.length == 0))
		{
			return org.toString();
		}
		if("hashCode".equals(method.getName()) && (args == null || args.length == 0))
		{
			return System.identityHashCode(proxy); //org.hashCode();
		}
		if("equals".equals(method.getName()) && args != null && args.length == 1 && method.getParameterTypes()[0] == Object.class) {
			return Boolean.valueOf(args[0] == proxy);
		}	
		@SuppressWarnings("rawtypes")
		Deferred<?> result = new Deferred();
		async.setRunner(new AsyncRunner(result, org, method, args));
		return nul(method.getReturnType());
	}

}
