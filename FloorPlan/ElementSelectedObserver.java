package FloorPlan;

import Assets.*;

public interface ElementSelectedObserver {
    //DrawingPanel will know which element was selected from BarElement
    void onElementSelected(DesignElement element);
}
