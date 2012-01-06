package fi.dwo.client.persistence.cache;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import fi.dwo.client.persistence.DbAccessIF;
import fi.dwo.client.system.PersistenceException;

public class CachingStore implements IStore, Runnable {

	Thread worker;
	Map work;
	Iterator iterator;
	IStore delegate;
	private PersistenceException last;
	
	public synchronized Bucket getWork() throws InterruptedException {
		if(iterator != null)
		{
			try {
				if(iterator.hasNext())
					return (Bucket) iterator.next();
			} catch (Exception e) { // expect ConcurrentModificationException
				e.printStackTrace();
			}
		}
		
		while(work.isEmpty())
		{
			notifyAll();
			wait();
		}
		iterator = work.values().iterator();
		Bucket next = (Bucket) iterator.next();
		return next;
	}
	/**
	 * FIXME synchronisatie probleem met iterator.
	 * @param b
	 */
	public synchronized void putWork(Bucket b) {
		work.put(b,b);
		notifyAll();
	}
	
	public void run() {
		while(!Thread.interrupted())
		{
			Bucket b = null;
			try {
				b = getWork();
				delegate.setValue(b.getUid(), b.getScoid(), b.getKey(), b.getValue());
				/// sleep(1000)
				iterator_remove();
			} catch (InterruptedException e) {
				clr();
				return;
			} catch (PersistenceException e) {
				last = e;
			}
		}
		clr();
	}

	private synchronized void iterator_remove() {
		try {
			iterator.remove();
		} catch (Exception e) {
			System.err.println(e);
			iterator = null;
		}
		
	}

	private synchronized void clr() {
		worker = null;
		notifyAll();
	}

	public String getValue(int uid, int scoid, String key)
			throws PersistenceException {
		Bucket b = new Bucket(uid, scoid, key, "");
		synchronized(this) {
			b = (Bucket) work.get(b);
			if(b == null)
				return delegate.getValue(uid, scoid, key);
			return b.getValue();
		}
	}

	public String setValue(int uid, int scoid, String key, String value)
			throws PersistenceException {
		putWork(new Bucket(uid, scoid, key, value));
		if(last != null) {
			PersistenceException e = last; last = null;
			throw e;
		}
		return "true";
	}

	public String commit(int uid, int scoid, String param)
			throws PersistenceException {
		commit(true);
		return "true";
	}

	private synchronized void commit(boolean x) throws PersistenceException {
		while(!work.isEmpty())
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				if(x)
					throw new PersistenceException(PersistenceException.EX_IO, e);
			}
			if(last != null && x) {
				PersistenceException e = last; last = null; throw e;
			}
	}

	public void destroy() {
		try {
			commit(false);
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
		synchronized(this) {
			while(worker != null) {
				worker.interrupt();
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	CachingStore(DbAccessIF dba) {
		delegate = new NoCache(dba);
		work = new LinkedHashMap();
		worker = new Thread(this);
		worker.start();
	}
	
}
