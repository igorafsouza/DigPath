# StarDist Nuclei Segmentation in QuPath (v0.5.1)

This workflow shows how to use the [StarDist](https://github.com/stardist/stardist) plugin with [QuPath v0.5.1+](https://qupath.github.io/) to segment nuclei from immunofluorescence images containing DAPI (in the blue channel / Channel 3).

---

## Requirements

- QuPath v0.5.1 or later
- StarDist model: `dsb2018_heavy_augment.pb`

---

## Installation Steps

### 1. Install the StarDist Extension via Extension Manager

- Open QuPath
- Go to:
  `Extensions → Manage extensions → Download extension from GitHub`
- In the popup window, enter:
  - **Owner**: `qupath`
  - **Repository**: `qupath-extension-stardist`
- Click **Download**
- **Restart QuPath** when prompted

---

### 2. Download a Pretrained StarDist Model

- Go to the model repository:  
  [https://github.com/qupath/models/tree/main/stardist](https://github.com/qupath/models/tree/main/stardist)
- Download the file for single channel segmentation (i.e. immunofluorescence images):  
  **`dsb2018_heavy_augment.pb`**
- Download the file for HE segmentation (i.e. immunohistochemistry images):  
  **`he_heavy_augment.pb`**
- Save it somewhere accessible (e.g., project folder or desktop)

---

### 4. Drawing the ROI (Region of Interest)

Before running the StarDist script, you need to select the region of the image where nuclei should be detected.

- Use the rectangle tool (🟦) in the QuPath toolbar to draw a square or rectangular ROI to fit your ROI image.

## Run StarDist in QuPath

### 5. Open StarDist Fluorescence Tool

- Go to:  
  `Extensions → StarDist → StarDist Fluorescence Cell Detection Script`

---

### 6. Edit Script Parameters

In the script window:

- **Set the path to your downloaded model** (use double backslashes `\\` on Windows):

```groovy
// Example for macOS/Linux:
def modelPath = '/Users/yourname/Desktop/dsb2018_heavy_augment.pb'

// Example for Windows (use double backslashes):
// def modelPath = 'C:\\Users\\yourname\\Desktop\\dsb2018_heavy_augment.pb'
```

- **Set the channel of your nuclei (default: `'DAPI'`) and change if needed the parameters highlighted below**:

  > 💡 **Tip:** Start by running the script with the default parameters to see how well the model performs on your image.  
  > If needed, you can fine-tune the values using trial and error:
  > - Lower `threshold` if the model misses true nuclei; raise it to reduce false positives.  
  > - Adjust `pixelSize` to match your image resolution.  
  > - Change `cellExpansion` to better approximate full cell boundaries beyond the nucleus.

```groovy
def stardist = StarDist2D
    .builder(modelPath)
    .channels('DAPI')     // Change for your Channel name
    .normalizePercentiles(1, 99) 
    .threshold(0.5)       // Sets how confident the model should be when calling a nucleus (0.5 = balanced)
    .pixelSize(0.5)       // Defines the resolution for detection (in microns per pixel)
    .cellExpansion(5)     // Expands detected nuclei by 5 µm to estimate full cell size  
    .measureShape()              
    .measureIntensity()          
    .build()```
