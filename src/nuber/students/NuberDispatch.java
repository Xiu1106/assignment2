package nuber.students;

import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class NuberDispatch {
	private final int MAX_DRIVERS = 999;
	private boolean logEvents;
	private LinkedBlockingQueue<Driver> idleDrivers;
	private HashMap<String, Integer> regions;
	private ConcurrentHashMap<String, NuberRegion> regionObjects;
	private AtomicInteger bookingsAwaitingDriver = new AtomicInteger(0);

	public NuberDispatch(HashMap<String, Integer> regionInfo, boolean logEvents) {
		this.logEvents = logEvents;
		this.idleDrivers = new LinkedBlockingQueue<Driver>(MAX_DRIVERS);
		this.regions = regionInfo;
		this.regionObjects = new ConcurrentHashMap<>();
		for (String region : regions.keySet()) {
			regionObjects.put(region, new NuberRegion(this, region, regions.get(region)));
		}
	}

	public boolean addDriver(Driver newDriver) {
		try {
			idleDrivers.put(newDriver);
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Driver getDriver() {
		try {
			return idleDrivers.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void logEvent(Booking booking, String message) {
		if (!logEvents) return;
		System.out.println(booking + ": " + message);
	}

	public Future<BookingResult> bookPassenger(Passenger passenger, String region) {
		bookingsAwaitingDriver.incrementAndGet();
		NuberRegion regionObj = regionObjects.get(region);
		if (regionObj != null) {
			return regionObj.bookPassenger(passenger);
		}
		return null;
	}

	public int getBookingsAwaitingDriver() {
		return bookingsAwaitingDriver.get();
	}

	public void decrementBookingsAwaitingDriver() {
		bookingsAwaitingDriver.decrementAndGet();
	}

	public void shutdown() {
		for (NuberRegion region : regionObjects.values()) {
			region.shutdown();
		}
	}
}
