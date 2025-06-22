package FloorPlan;

import Functions.*;

public interface FunctionSelectedObserver {
    //DrawingPanel will know which function was selected from BarFunction
    void onFunctionSelected(ManipulationFunction function);
}
