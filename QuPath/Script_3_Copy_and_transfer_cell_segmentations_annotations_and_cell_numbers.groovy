/**
* Script to transfer QuPath objects from one image to another, applying an AffineTransform to any ROIs.
*/

// SET ME! Define transformation matrix
// Get this from 'Interactive image alignment (experimental)
def matrix = [
    1.0000, 0.0000, 0.0000,
    0.0000, 1.0000, 0.0000
]

// SET ME! Define image containing the original objects (must be in the current project)
def otherImageName = ‘Please insert the name of the root image here’

// SET ME! Delete existing objects
def deleteExisting = false

// SET ME! Change this if things end up in the wrong place
def createInverse = true

import qupath.lib.objects.PathCellObject
import qupath.lib.objects.PathDetectionObject
import qupath.lib.objects.PathObject
import qupath.lib.objects.PathObjects
import qupath.lib.objects.PathTileObject
import qupath.lib.roi.RoiTools
import qupath.lib.roi.interfaces.ROI
import java.awt.geom.AffineTransform
import static qupath.lib.gui.scripting.QPEx.*

if (otherImageName == null) {

DisplayHelpers.showErrorNotification("Transform objects", "Please insert the name of the root image here")
return

}

// Get the project & the requested image name
def project = getProject()

def entry = project.getImageList().find {it.getImageName() == otherImageName}

if (entry == null) {

print 'Could not find image with name ' + otherImageName
return

}

def otherHierarchy = entry.readHierarchy()

def pathObjects = otherHierarchy.getRootObject().getChildObjects()

// Define the transformation matrix
def transform = new AffineTransform(

    matrix[0], matrix[3], matrix[1],
    matrix[4], matrix[2], matrix[5]

)

if (createInverse)

transform = transform.createInverse()

if (deleteExisting)

clearAllObjects()

def newObjects = []

for (pathObject in pathObjects) {

newObjects << transformObject(pathObject, transform)

}

addObjects(newObjects)
print 'Done!'

/**
* Transform object, recursively transforming all child objects
*
* @param pathObject
* @param transform
* @return
*/

PathObject transformObject(PathObject pathObject, AffineTransform transform) {

// Create a new object with the converted ROI

def roi = pathObject.getROI()

def roi2 = transformROI(roi, transform)

def newObject = null

if (pathObject instanceof PathCellObject) {

    def nucleusROI = pathObject.getNucleusROI()

    if (nucleusROI == null)

        newObject = PathObjects.createCellObject(roi2, pathObject.getPathClass(), pathObject.getMeasurementList())

    else

        name = pathObject.getName()

        newObject = PathObjects.createCellObject(roi2, transformROI(nucleusROI, transform), pathObject.getPathClass(), pathObject.getMeasurementList())

        newObject.setName(name.toString())

}

else if (pathObject instanceof PathTileObject) {

    newObject = PathObjects.createTileObject(roi2, pathObject.getPathClass(), pathObject.getMeasurementList())

} 

else if (pathObject instanceof PathDetectionObject) {

    newObject = PathObjects.createDetectionObject(roi2, pathObject.getPathClass(), pathObject.getMeasurementList())

} 

else {

    newObject = PathObjects.createAnnotationObject(roi2, pathObject.getPathClass(), pathObject.getMeasurementList())

}

// Handle child objects

if (pathObject.hasChildObjects()) {

    newObject.addPathObjects(pathObject.getChildObjects().collect({transformObject(it, transform)}))

}

return newObject

}

/**
* Transform ROI (via conversion to Java AWT shape)
*
* @param roi
* @param transform
* @return
*/

ROI transformROI(ROI roi, AffineTransform transform) {

def shape = RoiTools.getShape(roi) // Should be able to use roi.getShape() - but there's currently a bug in it for rectangles/ellipses!

shape2 = transform.createTransformedShape(shape)

return RoiTools.getShapeROI(shape2, roi.getImagePlane(), 0.5)

}