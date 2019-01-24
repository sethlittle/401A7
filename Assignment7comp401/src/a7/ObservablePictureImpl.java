package a7;

import java.util.ArrayList;
import java.util.List;

public class ObservablePictureImpl implements ObservablePicture {

	/*
	 * This is an implementation of the Observable class
	 * 
	 * This class has a picture instance field and contains a List of the
	 * Decorated objects, DecoratorROIObserver, the suspend instance field will
	 * be used to keep track of the status of the suspend characteristic
	 * 
	 * The _changed_region instance field will be used to keep track of the
	 * regions that are changed when the class is suspended, and then it will be
	 * used to notify the observers of all the changes once suspend is resumed,
	 * it will then be cleared to null
	 */

	private Picture _source;
	private List<DecoratorROIObserver> _observers;
	private boolean _suspend;
	private Region _changed_region;
	// changed region uses region when suspend is off

	public ObservablePictureImpl(Picture p) {
		if (p == null) {
			throw new IllegalArgumentException("Picture p cannot be null");
		}
		_source = p;
		_observers = new ArrayList<DecoratorROIObserver>();
		_suspend = false;
	}

	// all of these methods should have a conditional statement - if suspend
	// then they should perform the action rn
	// then they notify the observers, for each loop, using intersect and union

	@Override
	public int getWidth() {
		return _source.getWidth();
	}

	@Override
	public int getHeight() {
		return _source.getHeight();
	}

	@Override
	public Pixel getPixel(int x, int y) {
		return _source.getPixel(x, y);
	}

	@Override
	public Pixel getPixel(Coordinate c) {
		return getPixel(c.getX(), c.getY());
	}

	/*
	 * setPixel must determine the status of the suspend instance field. it
	 * calls the union of the _changed_region field to continue to add to the
	 * changes, then is it is not suspended, it notifies all of the observers
	 * that fall within the _changed_region
	 * 
	 * The NoIntersectionException must be handled within a try catch block -
	 * this is done in the hleper method trynotify
	 */

	@Override
	public void setPixel(int x, int y, Pixel p) {
		_source.setPixel(x, y, p);
		if (_changed_region == null) {
			_changed_region = (new RegionImpl(new Coordinate(x, y), new Coordinate(x, y)));
		} else {
			_changed_region = _changed_region.union(new RegionImpl(new Coordinate(x, y), new Coordinate(x, y)));
		}

		if (!_suspend) {
			for (DecoratorROIObserver o : _observers) {
				trynotify(o, _changed_region);
			}
			_changed_region = null;
		}

	}

	@Override
	public void setPixel(Coordinate c, Pixel p) {
		setPixel(c.getX(), c.getY(), p);
	}

	@Override
	public SubPicture extract(int xoff, int yoff, int width, int height) {
		return _source.extract(xoff, yoff, width, height);
	}

	@Override
	public SubPicture extract(Coordinate a, Coordinate b) {
		return _source.extract(a, b);
	}

	/*
	 * This method creates a Decorated object from the parameters and adds it to
	 * the _observers List
	 */

	@Override
	public void registerROIObserver(ROIObserver observer, Region r) {
		if (observer == null || r == null) {
			throw new IllegalArgumentException("observer and r cannot be null");
		}
		_observers.add(new DecoratorROIObserverImpl(observer, r));
	}

	/*
	 * This method takes in a region r as a parameter and iterates through the
	 * observers in the _observers List and if any of the regions of them
	 * intersect with the parameter region, it removes the observer from the
	 * list
	 */

	@Override
	public void unregisterROIObservers(Region r) {
		if (r == null) {
			return;
		}
		List<DecoratorROIObserver> modifiedList = new ArrayList<DecoratorROIObserver>();
		for (DecoratorROIObserver o : _observers) {
			if (!(intersects(o.getRegion(), r))) {
				modifiedList.add(o);
			}
		}
		_observers = modifiedList;
	}

	/*
	 * This method does the same as above except for the ROIObserver instead of
	 * the Region
	 */

	@Override
	public void unregisterROIObserver(ROIObserver observer) {
		if (observer == null) {
			return;
		}
		List<DecoratorROIObserver> modifiedList = new ArrayList<DecoratorROIObserver>();
		for (DecoratorROIObserver o : _observers) {
			if (o.getObserver() != observer) {
				modifiedList.add(o);
			}
		}
		_observers = modifiedList;
	}

	/*
	 * This method creates an array of observers that intersect with the region
	 * r parameter, this is done by creating a new array and adding the objects
	 * from the _observers List to it so the indexing of the List is not ruined,
	 * then the _observers list is reference to the new List and it is
	 * transformed into an array using the toArray method
	 */

	@Override
	public ROIObserver[] findROIObservers(Region r) {
		if (r == null) {
			throw new IllegalArgumentException("Region r cannot be null");
		}

		List<ROIObserver> ob = new ArrayList<ROIObserver>();
		for (DecoratorROIObserver o : _observers) {
			if (intersects(o.getRegion(), r)) {
				ob.add(o.getObserver());
			}
		}
		return ob.toArray(new ROIObserver[0]);
		// 0 as a length and the compiler knows to give this a variable length
		// and keep adding to it
	}

	public boolean getSuspend() {
		return _suspend;
	}

	@Override
	public void suspendObservable() {
		if (_suspend == false) {
			switchSuspend();
		}
	}

	// The resume method must notify all the observers of the changes that
	// occurred when the observable was suspended, then it clears the changes

	@Override
	public void resumeObservable() {
		if (_suspend == true) {
			switchSuspend();
			for (DecoratorROIObserver o : _observers) {
				trynotify(o, _changed_region);
			}
			_changed_region = null;
		}
	}

	// helper methods

	private boolean intersects(Region r1, Region r2) {
		try {
			r1.intersect(r2);
		} catch (NoIntersectionException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private void trynotify(DecoratorROIObserver o, Region r) {
		if (intersects(o.getRegion(), r)) {
			try {
				o.notify(this, r.intersect(o.getRegion()));
			} catch (NoIntersectionException e) {
			}
		}
	}

	private void switchSuspend() {
		_suspend = !_suspend;
	}

}
