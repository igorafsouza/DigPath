import qupath.ext.stardist.StarDist2D

// Specify the model .pb file (you will NEED to change this!)
def pathModel = '/path/to/QuPath Extensions/Stardist/he_heavy_augment.pb'

def stardist = StarDist2D.builder(pathModel)

.threshold(0.25) // Probability (detection) threshold - 0.03 is OK

//.channels('Hematoxylin') // Select detection channel

.normalizePercentiles(1, 99) // Percentile normalization

.pixelSize(0.3) // Resolution for detection - adjust to 0.5 if ROI

.cellExpansion(5.0) // Approximate cells based upon nucleus expansion

.cellConstrainScale(3.0) // Constrain cell expansion using nucleus size

.measureShape() // Add shape measurements

.measureIntensity() // Add cell measurements (in all compartments)

.includeProbability(true) // Add probability as a measurement (enables later filtering)

.build()

// Run detection for the selected objects

def imageData = getCurrentImageData()

def pathObjects = getSelectedObjects()

if (pathObjects.isEmpty()) {

Dialogs.showErrorMessage("StarDist", "Please select a parent object!")

return

}

stardist.detectObjects(imageData, pathObjects)

println 'Done!