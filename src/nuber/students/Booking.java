package nuber.students;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.Date;

public class Booking {
	private static final AtomicInteger nextId = new AtomicInteger(1); // For unique, sequential job IDs

	private final int jobId;
	private final NuberDispatch dispatch;
	private final Passenger passenger;
	private Driver driver;
	private final Date createTime;

	public Booking(NuberDispatch dispatch, Passenger passenger) {
		this.jobId = nextId.getAndIncrement();
		this.dispatch = dispatch;
		this.passenger = passenger;
		this.createTime = new Date();
	}

	public BookingResult call() throws InterruptedException {
		driver = dispatch.getDriver(); // This method should be implemented in NuberDispatch
		while(driver == null) {
			// Wait and retry to get an available driver
			Thread.sleep(100);
			driver = dispatch.getDriver();
		}

		driver.pickUpPassenger(passenger);
		driver.driveToDestination();

		Date endTime = new Date();
		long tripDuration = endTime.getTime() - createTime.getTime();

		dispatch.addDriver(driver); // This method should be implemented in NuberDispatch

		return new BookingResult(jobId, passenger, driver, tripDuration);
	}

	@Override
	public String toString() {
		String driverName = (driver == null) ? "null" : driver.name;
		String passengerName = (passenger == null) ? "null" : passenger.name;
		return jobId + ":" + driverName + ":" + passengerName;
	}
}
