/**
 * CesiumJS draw polyline tool
 */
(function() {
    // Constructor
    this.DrawToolPolyline = function() {
        // Create global element references
        this.viewerRef = null;
        this.globeElip = null;
        //track whether the polyline is currently being edited
        this.editingActive = false;
        //the main reference to the currently drawing / edit polyline
        this.pickedPolylinePrimitive = undefined;
        //create the position array
        this.positionArray = [];

        // Define option defaults
        var defaults = {
          _editPointSize: 8,
          _editColor: Cesium.Color.YELLOW.withAlpha(0.5),
          _finishColor: Cesium.Color.LIME.withAlpha(0.5),
          _editPointColor: Cesium.Color.GREENYELLOW,
          _editHalfwayPointColor: Cesium.Color.GOLD
        }

        if(arguments[0] && typeof arguments[0] === "object" && arguments[0].scene){
            this.viewerRef = arguments[0];
            this.globeElip = arguments[0].scene.globe.ellipsoid;
        }

        // Create options by extending defaults with the passed in arugments
        // Use the second argument as the first is the viewer instance
        if (arguments[1] && typeof arguments[1] === "object") {
          this.options = extendDefaults(defaults, arguments[1]);
        }

        return this;
    }

    DrawToolPolyline.viewerCesiumDrawPoly = function (viewer, options) {
        if (!viewer) {
            throw new DeveloperError('viewer is required.');
        }

        var drawToolPolyline = init(viewer, options);

        /*drawToolPolyline.addOnDestroyListener((function (viewer) {
            return function () {
                delete viewer.drawToolPolyline;
            };
        })(viewer));*/

        Cesium.defineProperties(viewer, {
            drawToolPolyline: {
                configurable: true,
                get: function () {
                    return viewer.cesiumWidget.drawToolPolyline;
                }
            }
        });
    }
    var init = function (viewerCesiumWidget, options) {
        var drawToolPolyline = new DrawToolPolyline(viewerCesiumWidget, options);

        var cesiumWidget = viewerCesiumWidget.cesiumWidget ? viewerCesiumWidget.cesiumWidget : viewerCesiumWidget;

        Cesium.defineProperties(cesiumWidget, {
            drawToolPolyline: {
                configurable: true,
                get: function () {
                    return drawToolPolyline;
                }
            }
        });

        /*drawToolPolyline.addOnDestroyListener((function (cesiumWidget) {
            return function () {
                delete cesiumWidget.drawToolPolyline;
            };
        })(cesiumWidget));*/

        return drawToolPolyline;
    };

    /**
     * Remove any primitives on the map, destroy mouse event handlers and destory the view.
     */
    DrawToolPolyline.prototype.destroy = function(){
        //remove all primitives on the map
        this.clearPrimitives({final: true});
        //remove the label from the viewer
        this.viewerRef.entities.remove(this.positionLabel);
        //if the mousehandler is active, destroy it
        this.mouseHandler = this.mouseHandler && this.mouseHandler.destroy();
        //re-enable the default mouse inputs
        this.enableMouseInputs();
        //destroy the tool
        delete this.viewerRef.drawToolPolyline;
    }

    /**
     * The method to enable the mouse handler that allows for the click down.
     * Once clicked, the initial position is set and move and stop mouse
     * handlers are enabled after at least 2 clicks (positions) to allow 
     * for drag and stop events of the mouse.
     */
    DrawToolPolyline.prototype.startDrawing = function(){
        var self = this;

        //Application.Utils.Alert.newAlert("info","Left click to start drawing polyline", 3000);

        //add position label that will be updated on mouse movement with the current position
        self.positionLabel = self.viewerRef.entities.add({
            label : {
                show : false,
                showBackground : true,
                font : '14px monospace',
                horizontalOrigin : Cesium.HorizontalOrigin.LEFT,
                verticalOrigin : Cesium.VerticalOrigin.TOP,
                pixelOffset : new Cesium.Cartesian2(15, 0)
            }
        });
        //create the points collection and add the points collection to the viewers primitives
        this.polyPointsCollection = this.viewerRef.scene.primitives.add(
            new Cesium.PointPrimitiveCollection()
        );
        //track whether the polyline is currently being edited
        this.editingActive = false;
        //the main reference to the currently drawing / edit polyline
        this.pickedPolylinePrimitive = undefined;
        //create the position array
        this.positionArray = [];
        //disable default mouse events on the map
        this.disableMouseInputs();
        //ensure any previous primitives are removed
        this.clearPrimitives();
        //The mouse handler used to click and drag events
        self.mouseHandler = new Cesium.ScreenSpaceEventHandler(this.viewerRef.scene.canvas);
        //the mouse down event
        self.mouseHandler.setInputAction(function(position) {
            //set the start position based on the current mouse position
           self.setPosition(position);
        }, Cesium.ScreenSpaceEventType.LEFT_DOWN);

        self.mouseHandler.setInputAction(function(movement) {
            var cartesian = self.cartesianChecker(movement.endPosition);
            if(cartesian){
                //update the label entity position
                self.updatePositionLabel(cartesian, self.positionArray.length-1);
                //set the current position based on the mouse position to be used as the temp
                //end position which will draw the polyline on the map.
                self.setCurrentDrawingPosition(cartesian); 
            }
        }, Cesium.ScreenSpaceEventType.MOUSE_MOVE); 

        self.mouseHandler.setInputAction(function (movement) {
            //set the stop position based on the current final mouse position
            self.setStopPosition(movement);
            //remove the mouse handlers
            self.mouseHandler = self.mouseHandler && self.mouseHandler.destroy();
            //re-enable the default mouse inputs
            self.enableMouseInputs();
            //enable the mouse events to allow for editing of the polyline
            self.setHoverToEditMode();
        }, Cesium.ScreenSpaceEventType.LEFT_DOUBLE_CLICK );
    }

    /**
     * Calculates the geodesic length based off of two cartographic positions.
     * @param  {object} firstPos Cartesian 3 position.
     * @param  {object} lastPos  Cartesian 3 position.
     * @return {result}          The calculated distance between the two points on the 
     *                           ellipsoid (earth).
     */
    DrawToolPolyline.prototype.getGeodesicDistance = function(firstPos, lastPos) {
        var self = this;
        //convert the positions to cartographic
        var pickedPointCartographic = Cesium.Cartographic.fromCartesian(firstPos);
        var lastPointCartographic = Cesium.Cartographic.fromCartesian(lastPos);
        //create a new ellipsoid geodesic between the two points.
        var geodesic = new Cesium.EllipsoidGeodesic(
            pickedPointCartographic, 
            lastPointCartographic,
            self.globeElip
        );
        //return the distance between the two points
        return geodesic.surfaceDistance;
    }
    /**
     * Calculate the total length based off of the passed positions and optional current
     * position passed.
     * @param  {array} positions   Array containing all Cartesian3 positions to be used 
     *                             to calculate the geodesic length.
     * @param  {object} position   Cartesian3 position to be calculated after the initial
     *                             geodesic length. Typically a current mouse position.
     * @param  {number} pointIndex The conditional index of a position in the passed positions
     *                             array. Passed to signify an edit point is being moved.
     * @return {number}            The total length in meters of the passed positions.
     */
    DrawToolPolyline.prototype.distanceTotal = function(positions, position, pointIndex){
        //initial distance is 0
        var distance = 0;
        //the reference to the next position index to be used for the geodesic calculation.
        var nextPos = 0;
        //iterate over all the positions in the array to add the geodesic distances 
        //from each intersection of points
        for (var i = positions.length - 1; i >= 0; i--) {
            //the next position index is one less than the current
            nextPos = i -1;
            // if the next position index is less than zero, we are done
            if(nextPos < 0){
                break;
            }
            //continuously add the geodesic distance between the current point and the next
            distance += this.getGeodesicDistance(positions[i], positions[nextPos]);
        }

        //if a current position is being passed (mouse pos)
        //and we have a point index that is the same as the last in the positions array
        //we can then assume mouse movement and add the additional mouse position distance
        if(position && pointIndex !== undefined && pointIndex === positions.length - 1){
            distance += this.getGeodesicDistance(positions[positions.length -1], position);
        }
        //return the total geodesic distance in meters
        return distance;
    }
    /**
     * Update the position of the label based on the pased position param.
     * @param  {object} position  The Cesium Cartesian2 position based on the mouse position or 
     *                            the Cesium Cartesian3 position based on the entities position.
     * @param {number} lastPoint  The index of the position for the last added position point.                                
     */
    DrawToolPolyline.prototype.updatePositionLabel = function(position, editPoint){
        var self = this;
        var curEditPoint = editPoint;
        var cartesian = position;
        //verify we attempting to display on the map
        if (cartesian) {
            var padText = function(width, string, padding) { 
              return (width <= string.length) ? string : padText(width, padding + string, padding);
            };
            //helper to calculate the distance from the last added point to the current position
            var distanceCalc = function(){
                var string = "";
                //if a last point position index is passed and we have at least one position
                //in the positions array, we will show the distance on the label
                if (self.positionArray.length >= 1){
                    //get the total distance of all of the primitive based on the
                    //current set of positions, and either the current mouse position or
                    //current edit point position
                    var distance = self.distanceTotal(self.positionArray, position, curEditPoint);
                    //calculate the length in nautical miles
                    var distanceNM = (distance * 0.00054).toFixed(2);
                    var distanceKM = (distance / 1000).toFixed(2);
                    
                    //update the string to contain the distance to be returned and displayed
                    string = "\nNM: "+ padText(10, distanceNM, ' ') + "\nKM: "+ padText(10, distanceKM, ' ');
                }
                return string;
            }
            //calculate the cartographic position for display purposes (lat, lon)
            var cartographic = Cesium.Cartographic.fromCartesian(cartesian);
            var longitudeString = Cesium.Math.toDegrees(cartographic.longitude).toFixed(4);
            var latitudeString = Cesium.Math.toDegrees(cartographic.latitude).toFixed(4);
            //set the position of the label entity
            self.positionLabel.position = cartesian;
            //calculate the label entity z-index value so that the label will
            //always appear on top of other entities 
            //(setting its z-index hight but not too high)
            var getZIndex = function(){ 
                var zIndex = self.viewerRef.camera.frustum.near * 100 - 
                    Cesium.Cartesian3.distance( 
                        self.positionLabel.position.getValue(), 
                        self.viewerRef.camera.position
                    );
                //if the map mode is not in 2d mode
                if(self.viewerRef.sceneModePicker.viewModel.sceneMode == Cesium.SceneMode.SCENE3D
                ){
                    return zIndex;
                }
                //for 2d mode only a small number is necessary
                return -100;
            }
            //update the z-index of the label using the eyeOffset property
            self.positionLabel.label.eyeOffset = new Cesium.Cartesian3(0.0, 0.0, getZIndex());
            //display the label entity
            self.positionLabel.label.show = true;
            
            //update the label text of the entity
            self.positionLabel.label.text =
                'Lon: ' + padText(9, longitudeString, ' ') + '\u00B0' +
                '\nLat: ' + padText(9, latitudeString, ' ') + '\u00B0'+
                distanceCalc();
        }else{
            //we are not on the map, hide the label
            self.positionLabel.label.show = false;
        }
    }
    /**
     * Set / store the position of a segment of a polyline based on the mouse left click event.
     * @param {object} position The passed position object of the polyline from the mouse event.
     */
    DrawToolPolyline.prototype.setPosition = function(position){
        var cartesian = this.cartesianChecker(position.position);
        if(cartesian){
            //add the position to position array
            this.positionArray.push(cartesian);
            //add the edit point to the map (visual purposes only)
            this.setPolyPoint(cartesian);
        }
    }
    /**
     * Set the final end position of the point double left clicked on the mouse.
     * to create/close the polyline and display it on the map.
     * @param {object} movement The passed position object of the polyline from the mouse event.
     */
    DrawToolPolyline.prototype.setStopPosition = function(position){
        var self = this;
        var cartesian = this.cartesianChecker(position.position);
        if (cartesian) {
            this.positionLabel.label.show = false;
            //remove previous primitive from the map
            this.clearPrimitives({final: true});
            //create the new primitive to be show on the map with a green color
            this.createGeometryInstance(
                self.createPositionsArray(cartesian, {final: true}),
                {   
                    attributes: {
                        color : new Cesium.ColorGeometryInstanceAttribute.fromColor(
                            self.options._finishColor
                        )
                    }
                }
            );
        }
    }
    /**
     * Set the current position of the last point based on the mouse position
     * to create/close the polyline and display it on the map.
     * @param {object} position The passed position object of the polyline from the mouse event.
     *                          in Cartesian3.
     */
    DrawToolPolyline.prototype.setCurrentDrawingPosition = function(position) {
        //remove previous primitive from the map
        this.clearPrimitives();
        //create the new primitive to be show on the map based on the passed position
        this.createGeometryInstance(this.createPositionsArray(position));
    }
    /**
     * Create the polyline coordinates to be used in creating the primitive instance.
     * @param  {object} lastPosition The position of the last point in the polyline as 
     *                               a cartographic (radians) object. 
     * @param {object} options The object containing optional display properties.
     *                         ex: { final: false }
     * @return {object}              The current array of positions.
     */
    DrawToolPolyline.prototype.createPositionsArray = function(lastPosition, options){
        var currentPositionsArr = this.positionArray.slice(0);
        //add the last point to the temp arr.
        currentPositionsArr.push(lastPosition);

        //if this is the final point, set it the position array 
        if(options && options.final){
            currentPositionsArr.splice(currentPositionsArr.length - 3, 2);
            this.positionArray = currentPositionsArr;
        }
        //the current array of positions
        return currentPositionsArr;
    }
    /**
     * Create the polyline primitive instance to be then displayed on the map.
     * @param  {array} positionArray The array of Cartesian3 coordinates.
     * @param {object} options The object containing optional display properties.
     *                          ex: { attributes: { color: Cesium.Color }}
     * @returns {result} The result of the createPrimitive method.
     *                   The return result is a Cesium polyline primitive.                        
     */
    DrawToolPolyline.prototype.createGeometryInstance = function(positionArray, options){
        var self = this;
        //use the optional passed color attribute other wise, otherwise the default color
        var instanceAttr = options && options.attributes ?  options.attributes : {
            color : new Cesium.ColorGeometryInstanceAttribute.fromColor(
                self.options._editColor
            )
        };
        //use the optional passed width attribute, otherwise the default width
        var instanceWidth = options && options.width ? options.width : 3;

        //create the polyline instance which contains the polyline geometry
        var polylineInstance = new Cesium.GeometryInstance({
            id: 'polyline_' + Cesium.createGuid(),
            geometry : new Cesium.PolylineGeometry({
                positions : positionArray,
                width: instanceWidth,
                ellipsoid : self.globeElip,
                vertexFormat : Cesium.PerInstanceColorAppearance.VERTEX_FORMAT/*,
                extrudedHeight: 300000*/
            }), 
            attributes : instanceAttr
        });
        //create the polyline from the new instance and returns the results (primitive).
        return this.createPrimitive(polylineInstance);
    }
    /**
     * Create the polyline primitive and add it to the map.
     * @param  {object} polylineInstance Cesium GeometryInstance object used to create the 
     *                                  primitive instance.
     * @returns {object} The Cesium polyline primitive reference.                                                
     */
    DrawToolPolyline.prototype.createPrimitive = function(polylineInstance){
        //create the polyline primitive which contains the geometry instance which
        //will be displayed on the map
        this.polylinePrimitive = new Cesium.Primitive({
            geometryInstances : polylineInstance,
              appearance : new Cesium.PolylineColorAppearance({
                translucent : false
              }),
            //removes flicker from the constant destroy calls on the primivites
            asynchronous: false
        });
        //return & add the primitive to the viewer scene to be displayed on the map
        return this.viewerRef.scene.primitives.add(this.polylinePrimitive);
    }
    /**
     * Remove the created primitives from the scene.
     * @param {object} options Optional params for clearing primitives.
     */
    DrawToolPolyline.prototype.clearPrimitives = function(options){
        if(this.polylinePrimitive){
            this.polylinePrimitive = this.polylinePrimitive.destroy();
        }
        if(options && options.final && this.polyPointsCollection){
            this.polyPointsCollection.removeAll();
        }
    }
    /**
     * Controls the display of the primitive based on the passed hover attribute state.
     * @param  {boolean} hover True of False state if primitive is being hovered on.
     */
    DrawToolPolyline.prototype.toggleEditInnactiveHover = function(hover){
        //default width of primitive
        var width = 3;
        //default color is edit color
        var color = this.options._editColor;
        //if we are not editing
        if(!this._editingActive){
            //large size if hovering over primitive
            if(hover){
                width = 5;
            }else{
                width = 3;
            }
            //assume finished color
            color = this.options._finishColor;
        }
        //clear the previous primitive and draw new primitive with passed option attributes
        this.clearAndRedrawPolylinePrimitive(this.positionArray, {
            width: width,
            attributes: {
                color : new Cesium.ColorGeometryInstanceAttribute.fromColor(
                    color
                )
            }
        });
    }
    /**
     * Enable the mouse events for polyline editing on hover.
     */
    DrawToolPolyline.prototype.setHoverToEditMode = function() {
        var self = this;
        //track whether the polyline can be edited
        var allowEditing = false;
        //track the current edited polyline primitive
        var currentPickedPrimitive;
        //create new mouse handler.
        self.mouseHandler = new Cesium.ScreenSpaceEventHandler(self.viewerRef.scene.canvas);
        //set the mouse move event to allow for polyline editing on it the mouse cursor
        //is over a primitive which can be edited.
        self.mouseHandler.setInputAction(function(movement) {
            //update local reference to the current mouse over primitive
            currentPickedPrimitive = self.viewerRef.scene.pick(movement.endPosition);
            //if mouse is over a primitive, allow for editing enabled
            if(currentPickedPrimitive && typeof currentPickedPrimitive.id === 'string' && 
                currentPickedPrimitive.id.includes('polyline')
            ){
                //editing is allowed
                allowEditing = true;
                //update reference to current picked primitive for potential editing
                self._pickedPolylinePrimitive = currentPickedPrimitive.primitive;
               
                //update the label entity position
                self.updatePositionLabel(self.cartesianChecker(movement.endPosition));


                //updated the display of the primitive to be back hovered state
                return self.toggleEditInnactiveHover(true);
            }
            //editing is dissallowed
            allowEditing = false;
            self.updatePositionLabel(false);
            //updated the display of the primitive to be back to default finish
            return self.toggleEditInnactiveHover(false);

        }, Cesium.ScreenSpaceEventType.MOUSE_MOVE); 

        //set the mouse btn up event to either enable or disbale editing of the primitive
        self.mouseHandler.setInputAction(function(position) {
            //if editing hasnt started yet, and if its allowable (cursor on top of primitive)
            if(allowEditing){
                //update the selected primitive property reference
                self._pickedPolylinePrimitive = currentPickedPrimitive.primitive;
                //remove these hover to edit mouse events
                self.mouseHandler.destroy();
                //update the track editing property
                self._editingActive = true;
                //updated the display of the primitive to be back to default finish
                self.toggleEditInnactiveHover(false);
                //enable the edit display points which in turn starts the 
                //mouse edit event listeners
                return self.startEditPolyline();
            }
        }, Cesium.ScreenSpaceEventType.LEFT_DOWN);
    }
    /**
     * Update the display of the primitive to edit mode, disable Cesium 
     * default mouse inputs and trigger enabling the edit points.
     * @param  {object} primitive The Cesium primitive reference.
     */
    DrawToolPolyline.prototype.startEditPolyline = function(){
        //enable the edit points on the primitive which in turn starts the 
        //mouse edit event listeners
        this.addEditPoints();
        //add the potential edit points to the polyline if a new point will be clicked 
        //and dragged. 'Halfway Edit Points' (points between a pair of real points)
        this.addHalfwayEditPoints();
    }
    /**
     * Add points to each primitive point position on top of the polyline primitive 
     * to act as draggable anchor points for position updating of the primitive.
     */
    DrawToolPolyline.prototype.addEditPoints = function(){
        //clones the polyline positions list
        var arrPos = this.positionArray.slice(0);
        //add a editable point for each position in the list of polyline positions array
        for (var i = arrPos.length - 1; i >= 0; i--) {
            this.setPolyPoint(arrPos[i], i);
        }
        //add the mouse event handlers for the editable points
        this.setPointEventHandlers();
    }
    /**
     * Create the temp array to be used for creating the halfway edit points.
     */
    DrawToolPolyline.prototype.addHalfwayEditPoints = function(){
        //clones the polyline positions list
        var arrPos = this.positionArray.slice(0);
                   
        for (var i = arrPos.length - 2; i >= 0; i--) {
            //calculate the halfway edit point position to be then added on the map
            this.setHalfwayPolyPoint(this.calculateHalfMarkerPosition(i), i)
        }
    }

    /**
     * Set the position of polyline point on the map based on the mouse click event.
     * @param {object} position Cartographic (radians) position object.
     * @param {number} posIndex The index of the point positions in the array being updated.
     */
    DrawToolPolyline.prototype.setPolyPoint = function(position, posIndex){
        //add the new point based on the passed position to the points collection
        return this.polyPointsCollection.add({
            id: posIndex,
            position : position,
            pixelSize: this.options._editPointSize,
            color : this.options._editPointColor
        });
    }
    /**
     * Create and add the new halfway point on the map.
     * @param {object} position Cesium cartographic position of the halfway edit point.
     * @param {number} posIndex The index of the preceeding point.
     * @returns {object} The result of adding the new point to the collection of points.
     *                   (Cesium point primitive)
     */
    DrawToolPolyline.prototype.setHalfwayPolyPoint = function(position, posIndex){
        //update the index to include '.5' to be used later with helping to identify
        //which kind of point it is
        var point = {
            id: posIndex + 0.5,
            position : position,
            pixelSize: this.options._editPointSize -1,
            color : this.options._editHalfwayPointColor
        };

        //add the new point based on the passed position to the points collection
        return this.polyPointsCollection.add(point);
    }
    /**
     * Calculate the halfway points position.
     * @param  {number} index The index of the preceeding point in the array of positions
     *                        for the polyline.
     * @return {object}       The Cesium Cartographic position of the new halfway point.
     */
    DrawToolPolyline.prototype.calculateHalfMarkerPosition = function(index) {
        //clones the polyline positions list
        var arrPos = this.positionArray.slice(0);

        //return the position of the new halfway edit point
        return this.globeElip.cartographicToCartesian(
            new Cesium.EllipsoidGeodesic(
                this.globeElip.cartesianToCartographic(arrPos[index]),
                this.globeElip.cartesianToCartographic(
                    arrPos[index < arrPos.length - 1 ? index + 1 : 0]
                )
            ).interpolateUsingFraction(0.5)
        );
    }
    /**
     * Remove the collection of editable points and the associated mouse event handlers.
     */
    DrawToolPolyline.prototype.removeAllEditPoints = function(){
        //remove all edit points
        this.polyPointsCollection.removeAll();
        //remove edit points mouse event handlers
        this.mouseHandler.destroy();
    }
    /**
     * Remove a single point position from the polyline positions array used for drawing
     * the polyline on the map. Redraw the edit points and polyline after removal.
     * @param  {object} pointPrimitive The Cesium point primitive currently being removed.
     * @return {boolean}               Return true or false if a point has been removed.
     */
    DrawToolPolyline.prototype.removeSingleEditPoint = function(pointPrimitive){
        //cancel the removal of a point if we are the minimum amount of points for a polyline
        if(this.positionArray.length -1 === 1){
            return false;
        }
   
        //remove the point by index of the current points id
        this.positionArray.splice(pointPrimitive.id, 1);
        //re-update the edit points on the polyline
        this.updateEditPointsPosition()
        //remove old polylinee primitive from map and redraw with new updated positions
        this.clearAndRedrawPolylinePrimitive(this.positionArray);
        return true;
    }
    /**
     * Verify that the point clicked is in fact on the elipsoid (map).  
     * @param  {object} position The Cesium Cartesian2 position from the mouse event.
     * @return {result}          The result position in Cartesian3 if a position is 
     *                           correct.
     */
    DrawToolPolyline.prototype.cartesianChecker = function(position){
        return this.viewerRef.camera.pickEllipsoid(
            position, this.globeElip
        );
    }

    /**
     * Add the mouse event handlers for the edit points and methods for update the 
     * associated polyline primitive positions.
     */
    DrawToolPolyline.prototype.setPointEventHandlers = function() {
        var self = this;
        //default editing tracker property
        var allowEditing = false;
        //create new mouse handler.
        self.mouseHandler = new Cesium.ScreenSpaceEventHandler(self.viewerRef.scene.canvas);
        //picked point primitive tracker property
        var pickedPoint;
        //the last edit point
        var lastPickedPointPrimitive;

        //search all the primitives that the mouse is currently on against
        //the point primitives collection and return the match if found
        var pickPointPrimitive = function(position){
            var matches = self.viewerRef.scene.drillPick(position);
            for (var i = matches.length - 1; i >= 0; i--) {
                //ensure the point is a match and that it is a edit point
                if(self.polyPointsCollection.contains(matches[i].primitive) && 
                   typeof matches[i].id === "number"
                ){
                    return matches[i];
                }
            }
            return false;
        };
        //helper for updating the edit point primitive size
        var updatePointPrimitiveSize = function(picked){
            //if there is a picked edit point primitive passed
            if(picked){
                //increase the edit point primitive size
                return picked.primitive.pixelSize = 12;
            }
            //otherwise, reset the last mouse over edit point to default size
            if(lastPickedPointPrimitive && !picked){
                return lastPickedPointPrimitive.primitive.pixelSize = self.options._editPointSize;
            }
        };
        //set the mouse event for mouse btn down to allow for mouse click drag on point.
        self.mouseHandler.setInputAction(function(position) {
            //find a match for the primitive that we are editing
            var pickedPrimitive = self.viewerRef.scene.pick(position.position);
            //the matched primitive on mouse btn down if found in the points collection
            var primitiveSearch = pickPointPrimitive(position.position);
            //if a primitive is in fact click down on and that primitive exisits in 
            //the collection of store point primitives allow for editing
            if(primitiveSearch && !allowEditing){
                //update the point primitive property reference with the current picked primitive
                pickedPoint = primitiveSearch;
                //disable the defaults Cesium mouse inputs
                self.disableMouseInputs();
                //allow for editing of the associated primitive
                allowEditing = true;
            }
            //if clicked off of the currently editing primitive (no match on mouse position)
            if(!primitiveSearch && !pickedPrimitive){
                //re-enable the mouse hover events and update the editing primitive 
                //to not be in edit mode
                self.reenableHoverToEdit();
            }
                
        }, Cesium.ScreenSpaceEventType.LEFT_DOWN);

        //set the mouse event for moving the mouse
        self.mouseHandler.setInputAction(function(movement) {
            //check if our current mouse position is on a edit point
            var primitiveSearch = pickPointPrimitive(movement.endPosition);
            //the current position of the mouse
            var cartesian = self.cartesianChecker(movement.endPosition);
            //if editing is allowed (current mouse down event is on a point primitive)
            if(allowEditing){
                //update the point primitives location to the 
                //new one based on the mouse movement
                pickedPoint.primitive.position = cartesian;
                //hide the adjacent half edit points, when moving a real
                //point on the polyline
                self.hideAdjacentEditHalwaysPoints(pickedPoint);
                //udpate the associated polyline primitive position point to reflect the 
                //new position of this point primitive
                self.updatePolylinePrimitivePosition(pickedPoint);
                //update the label entity position
                return self.updatePositionLabel(cartesian, pickedPoint.id);
            }else{
                //if the mouse position is on a edit point
                if(primitiveSearch){
                    //update the picked primitive reference
                    lastPickedPointPrimitive = primitiveSearch;
                    //update the edit point primitive size (large)
                    updatePointPrimitiveSize(primitiveSearch);
                    //update the label entity position
                    return self.updatePositionLabel(
                        lastPickedPointPrimitive.primitive.position
                    );
                }
                //update the edit point primitive size (large)
                updatePointPrimitiveSize(pickedPoint);
                //remove the edit point reference
                pickedPoint = false;
                //hide the label
                return self.updatePositionLabel(false);
            }

        }, Cesium.ScreenSpaceEventType.MOUSE_MOVE);

        //set the edit mode tracker property to false to finish the drag movement
        self.mouseHandler.setInputAction(function(position) {
            //if editing is allowed (current mouse down event is on a point primitive)
            if(allowEditing){
                var cartesian = self.cartesianChecker(position.position);
                if(cartesian){
                    dragging = false;
                    //update the point primitives location to the 
                    //new one based on the mouse movement
                    pickedPoint.primitive.position = cartesian;
                    //update the polyline primitive position based on the 
                    //current position of the edit point that was moved
                    self.updatePrimitivePosition(pickedPoint, true);
                    //recalculate the position of the edit points now that one of 
                    //the polyline points have been updated
                    self.updateEditPointsPosition();
                    //hide the position label
                    self.positionLabel.label.show = false;
                    //dissallow editing
                    allowEditing = false;
                }
            }
            //dragging has finished, re-enable the default Cesium mouse events
            self.enableMouseInputs();
        }, Cesium.ScreenSpaceEventType.LEFT_UP);

        self.mouseHandler.setInputAction(function(position) {
            var primitiveSearch = pickPointPrimitive(position.position);
            //if there is a permanent point primitive on the double click area
            if(primitiveSearch.id){
                //remove the point primitive and the point position from the array of positions
                self.removeSingleEditPoint(primitiveSearch);
            }
        }, Cesium.ScreenSpaceEventType.LEFT_DOUBLE_CLICK);

    }
    /**
     *  Remove the edit mode mouse events and re-enable the hover to edit mode mouse events.
     */
    DrawToolPolyline.prototype.reenableHoverToEdit = function(){
        //update the editing track reference
        this._editingActive = false;
        //remove the old edit points
        this.removeAllEditPoints();
        //remove all mouse events that were used for editing the primitive
        this.mouseHandler.destroy();
        //re-enable Cesium default mouse events
        this.enableMouseInputs();
        //enable the hover to edit mouse events on the primitive
        this.setHoverToEditMode();
    }
    /**
     * Shorcut method for updating the edit point positions.
     */
    DrawToolPolyline.prototype.updateEditPointsPosition = function(){
        //remove the old edit points
        this.removeAllEditPoints();
        //add the new edits
        this.addEditPoints();
        //calculate and add the new halfway edit points
        this.addHalfwayEditPoints();
    }
    /**
     * Hide the adjacent halway edit points (left, right).
     * @param  {object} pointPrimitive The Cesium point primitive that is currently being moved.
     */
    DrawToolPolyline.prototype.hideAdjacentEditHalwaysPoints = function(pointPrimitive){
        //if it is not a halfway edit point. (only do this if its a permanent edit point)
        //check the point id to see if it is a halfway edit point or not 
        if(pointPrimitive.id % 1 !== 0.5){
            //remove two for the position array length because 
            //one point is the same as the first and must be removed
            //the array length is always an index longer than the 'edit' point primitive index 
            var pointLength = this.positionArray.length;
            //determine if the point is the first point in the list or not
            var left = pointPrimitive.id - 0.5 < 0 ? pointLength-1 + 0.5 : pointPrimitive.id - 0.5;
            var right = pointPrimitive.id + 0.5;

            //check all the points in the collection. Find the point which ids match either the 
            //left edit point or the right and then hide them
            for (var i = this.polyPointsCollection.length - 1; i >= 0; i--) {
                var point = this.polyPointsCollection.get([i]);
                if(point.id === left || point.id === right){
                    point.show = false;
                }
            }
        }
    }
    /**
     * Update the position of the polyline primitive point based on the update edit point 
     * primitives new position from the mouse events.
     * @param  {object} pointPrimitive The Cesium point primitive reference.
     */
    DrawToolPolyline.prototype.updatePolylinePrimitivePosition = function(pointPrimitive){
        var newPosValue = pointPrimitive.primitive.position;

        //if we are edit/dragging a point that is a halfway edit point, 
        //we need to add two new halfway points and add a new permanent point 
        //to the polyline position array based on the currently dragging halfway edit point
        if(pointPrimitive.id % 1 === 0.5){
            return this.updatePrimitivePosition(pointPrimitive);
        }

        //update the position of the polyline primitive point to that of the 
        //new point position
        this.positionArray[pointPrimitive.id] = newPosValue;
        //remove old polylinee primitive from map and redraw with new updated positions
        this.clearAndRedrawPolylinePrimitive(this.positionArray);
    }
    /**
     * Update polyline position based on the current position of the edit point or 
     * halfway edit point.
     * @param  {object} pointPrimitive The Cesium point primitive reference of the 
     *                                 permanent point being moved.
     * @param  {boolean} finalUpdate   If this is the last update to the movement on the 
     *                                 point primitive.
     */
    DrawToolPolyline.prototype.updatePrimitivePosition = function(pointPrimitive, finalUpdate){
        //the potential index of the point to be added in the array of permanent positions
        var insertAtIndex = pointPrimitive.id - 0.5;
        //clone the permanent positions array
        var positionArray = this.positionArray.slice(0);
        //the new position of the point being edited/moved
        var newPosValue = pointPrimitive.primitive.position;
       
        //we need to insert the point into the correct index in the permanent position array.
        positionArray.splice(
            insertAtIndex + 1, 
            0, 
            newPosValue
        );
        //remove old polylinee primitive from map and redraw with new updated positions
        this.clearAndRedrawPolylinePrimitive(positionArray);
        //only update the permanent positions array if the mouse drag event has finished
        //and that there is a new point being added (because we were editing a halfway point)
        if(finalUpdate && pointPrimitive.id % 1 === 0.5){
            this.positionArray = positionArray;
        }
    }
    /**
     * Remove the old polyline primitive from the mafp and redraw a new one with new positions.
     * @param  {array} positionArray The array of positions used to draw the polyline.
     */
    DrawToolPolyline.prototype.clearAndRedrawPolylinePrimitive = function(positionArray, options){
        //remove the old polyline and edit points
        this.clearPrimitives();
        //create a new polyline instance based on the temp position points and show on the map
        this._pickedPolylinePrimitive = this.createGeometryInstance(positionArray, options);
    }
    /**
     * Enable the default mouse inputs on the map (scroll, zoom, click drag..)
     */
    DrawToolPolyline.prototype.enableMouseInputs = function() {
        var controller = this.viewerRef.scene.screenSpaceCameraController;
        controller.enableTranslate = true;
        controller.enableZoom = true;
        controller.enableRotate = true;
        controller.enableTilt = true;
        controller.enableLook = true;
    }
    /**
     * Disable the default mouse inputs on the map (scroll, zoom, click drag..)
     */
    DrawToolPolyline.prototype.disableMouseInputs = function() {
        var controller = this.viewerRef.scene.screenSpaceCameraController;
        controller.enableTranslate = false;
        controller.enableZoom = false;
        controller.enableRotate = false;
        controller.enableTilt = false;
        controller.enableLook = false;
    }


    // Private Methods

    // Utility method to extend defaults with user options
    function extendDefaults(source, properties) {
        var property;
        for (property in properties) {
          if (properties.hasOwnProperty(property)) {
            source[property] = properties[property];
          }
        }
        return source;
    }

}());
