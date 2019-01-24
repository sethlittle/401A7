package a7;

import java.util.ArrayList;
import java.util.List;

public class DecoratorROIObserverImpl implements DecoratorROIObserver {

	/*
	 * This class is a decorated implementation of the ROIObserver Interface,
	 * and this will be the object used in the ObservablePictureImpl as the main
	 * List
	 */

	private Region _region;
	private ROIObserver _observer;

	public DecoratorROIObserverImpl(ROIObserver observer, Region r) {
		if (r == null) {
			throw new IllegalArgumentException("The region parameter cannot be null");
		}
		_region = r;
		_observer = observer;
	}

	/*
	 * This method calls the notify method on the ROIObserver object that the
	 * DecoratorROIObserver is a reference to
	 * 
	 */
	@Override
	public void notify(ObservablePicture picture, Region changed_region) {
		_observer.notify(picture, changed_region);
	}

	@Override
	public Region getRegion() {
		return _region;
	}

	public ROIObserver getObserver() {
		return _observer;
	}

}
