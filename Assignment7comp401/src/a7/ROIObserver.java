package a7;

public interface ROIObserver {

	// This the interface for the Observer

	void notify(ObservablePicture picture, Region changed_region);

}
