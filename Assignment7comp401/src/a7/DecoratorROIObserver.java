package a7;

import java.util.List;

public interface DecoratorROIObserver extends ROIObserver {

	/*
	 * This interface is the Decorated interface of ROIObserver
	 */

	Region getRegion();

	ROIObserver getObserver();
}
