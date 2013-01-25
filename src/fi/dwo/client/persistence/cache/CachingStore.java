package fi.dwo.client.persistence.cache;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

import fi.dwo.client.persistence.DbAccessIF;
import fi.dwo.client.system.PersistenceException;

public class CachingStore implements IStore, Runnable {

	private static final long MAX_BACKOFF = 2 * 60 * 1000L; // 2 min maximum voor exponential backoff
	private static final long INI_BACKOFF = 200L; 
	Thread worker;
	Map work, cache;
	Iterator iterator;
	IStore delegate;
	private PersistenceException last;
	
	public synchronized Bucket getWork() throws InterruptedException {
		if(iterator != null)
		{
			try {
				if(iterator.hasNext())
					return (Bucket) iterator.next();
			} catch (ConcurrentModificationException cme) {
				System.err.println("Expected: " + cme + ", niets aan de hand");
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
System.out.println("putWork " + b.getKey());
		work.put(b,b);
		cache.put(b, b.getValue());
		notifyAll();
	}
	
	public void run() {
		long backoff = INI_BACKOFF;
		while(!Thread.interrupted())
		{
			Bucket b = null;
			try {
				b = getWork();
System.out.println("try setValue " + b.getKey());
				delegate.setValue(b.getUid(), b.getScoid(), b.getKey(), b.getValue());
				/// sleep(1000)
				iterator_remove();
				backoff = INI_BACKOFF;
			} catch (InterruptedException e) {
				break;
			} catch (PersistenceException e) {
				if(e.getCode() == PersistenceException.EX_IO) {
					try {
						Thread.sleep(backoff);
						backoff = Math.min(MAX_BACKOFF, backoff * 2);
						iterator = null; // start at head of queue, wel of niet?
					} catch (InterruptedException e1) {
						break;
					}
					
				} else {
					System.out.println(b.getKey() + " exception " + e.getCode());
					last = e;
				}
			}
		}
		clr();
	}

	private synchronized void iterator_remove() {
		try {
			iterator.remove();
		} catch (ConcurrentModificationException cme) {
			iterator = null;
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
			Bucket v = (Bucket) work.get(b);
			if(v == null)
			{	
				System.out.println("cache miss " + key + " " + cache.size());
				String value = (String) cache.get(b);
				if(value != null) {
					System.out.println("2nd cache hit " + key);
					return value;
				}
				try {
					return delegate.getValue(uid, scoid, key);
				} catch (PersistenceException e) {
					e.printStackTrace();
					throw e;
				}
			}
			System.out.println("cache hit " + key);
			return v.getValue();
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
		cache.clear();
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
		cache = new WeakHashMap();
		worker = new Thread(this);
		worker.start();
	}
	
}
