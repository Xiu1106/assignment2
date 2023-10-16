package nuber.students;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NuberRegion {
	private NuberDispatch dispatch;
	private String regionName;
	private int maxSimultaneousJobs;
	private ExecutorService executor;
	private LinkedBlockingQueue<Booking> bookingsQueue;
	private boolean isShutdown = false;

	public NuberRegion(NuberDispatch dispatch, String regionName, int maxSimultaneousJobs) {
		this.dispatch = dispatch;
		this.regionName = regionName;
		this.maxSimultaneousJobs = maxSimultaneousJobs;
		this.executor = Executors.newFixedThreadPool(maxSimultaneousJobs);
		this.bookingsQueue = new LinkedBlockingQueue<>();
	}

	public Future<BookingResult> bookPassenger(Passenger waitingPassenger) {
		if (isShutdown) {
			dispatch.logEvent(null, "Booking was rejected.");
			return null;
		}

		Booking booking = new Booking(dispatch, waitingPassenger);
		bookingsQueue.offer(booking);

		// Submit the booking for execution and return the Future object
		return executor.submit(booking::call);
	}

	public void shutdown() {
		isShutdown = true;
		executor.shutdown(); // This will not accept new tasks
	}
}
