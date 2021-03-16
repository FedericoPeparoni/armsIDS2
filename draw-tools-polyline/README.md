# Map Draw Tools - Polyline

A simple tool to be used with [CesiumJS](https://cesiumjs.org/) to draw and edit a single polyline on the map.

--------------
**How to use it**

```javascript
var viewer = new Cesium.Viewer('cesiumContainer');

viewer.extend(DrawToolPolyline.viewerCesiumDrawPoly, {
	//options
});
```

**Available options with defaults**

```javascript
{
  _editPointSize: 8,
  _editColor: Cesium.Color.YELLOW.withAlpha(0.5),
  _finishColor: Cesium.Color.LIME.withAlpha(0.5),
  _editPointColor: Cesium.Color.GREENYELLOW,
  _editHalfwayPointColor: Cesium.Color.GOLD
}
```

**Example usage**

```javascript
var viewer = new Cesium.Viewer('cesiumContainer');

document.getElementById("MyToggleBtn").addEventListener(
	"click", function(){
        if(!viewer.drawToolPolyline){
          viewer.extend(
	          DrawToolPolyline.viewerCesiumDrawPoly,  
	          {
		          _editPointSize: 8
			  }
		  );
          viewer.drawToolPolyline.startDrawing();
        }else{
          viewer.drawToolPolyline.destroy();
        }
      });
```
