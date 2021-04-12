<?xml version="1.0" encoding="UTF-8"?>
<StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0.0" xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd">
   <NamedLayer>
      <Name>V_RouteSegments</Name>
      <UserStyle>
         <Title>3px blue line</Title>
         <Abstract>Default line style for Route Segment, 3 pixel wide blue</Abstract>
         <FeatureTypeStyle>
           
            <Rule>
               <Title>Blue Line</Title>
               <Abstract>A 3 pixel wide blue line</Abstract>
               <LineSymbolizer>
                  <Stroke>
                     <CssParameter name="stroke">#333333</CssParameter>
                     <CssParameter name="stroke-width">3</CssParameter>
                     <CssParameter name="stroke-linecap">round</CssParameter>
                  </Stroke>
               </LineSymbolizer>
               <LineSymbolizer>
                  <Stroke>
                     <CssParameter name="stroke">#6699FF</CssParameter>
                     <CssParameter name="stroke-width">1</CssParameter>
                     <CssParameter name="stroke-linecap">round</CssParameter>
                  </Stroke>
               </LineSymbolizer>
               
            </Rule>
           
            <!-- some steps that need to be created for separation-->
          
           
            <Rule>
               <Title>A SMALL CIRCLE STARTING POINT</Title>
               <Abstract>A SMALL CIRCLE STARTING POINT</Abstract>
               <MaxScaleDenominator>10000000</MaxScaleDenominator>
              
              <TextSymbolizer>
                <Geometry>
                  <ogc:Function name="startPoint">
                    <ogc:PropertyName>geom</ogc:PropertyName>
                  </ogc:Function>
                </Geometry>
                <LabelPlacement>
                  <PointPlacement>
                    <Displacement>
                     <DisplacementX>7</DisplacementX>
                     <DisplacementY>-5</DisplacementY>
                   </Displacement>
                  </PointPlacement>
                </LabelPlacement>
                <Label>
                  <ogc:PropertyName>startlabel</ogc:PropertyName>
                </Label>
              </TextSymbolizer>
              
              
               <PointSymbolizer>
                  <Geometry>
                     <ogc:Function name="startPoint">
                        <ogc:PropertyName>geom</ogc:PropertyName>
                     </ogc:Function>
                  </Geometry>
                  <Graphic>
                     <Mark>
                        <WellKnownName>circle</WellKnownName>
                        <Fill>
                           <CssParameter name="fill">#CC3300</CssParameter>
                        </Fill>
                     </Mark>
                     <Size>7</Size>
                  </Graphic>
               </PointSymbolizer>
            </Rule>
           	
            
           
            <Rule>
               <Title>A SMALL CIRCLE ENDING POINT</Title>
               <Abstract>A SMALL CIRCLE ENDING POINT</Abstract>
               <MaxScaleDenominator>10000000</MaxScaleDenominator>
                <TextSymbolizer>
                  <Geometry>
                    <ogc:Function name="endPoint">
                      <ogc:PropertyName>geom</ogc:PropertyName>
                    </ogc:Function>
                  </Geometry>
                  <LabelPlacement>
                    <PointPlacement>
                      <Displacement>
                       <DisplacementX>7</DisplacementX>
                       <DisplacementY>-5</DisplacementY>
                     </Displacement>
                    </PointPlacement>
                  </LabelPlacement>
                  <Label>
                    <ogc:PropertyName>endlabel</ogc:PropertyName>
                  </Label>
                </TextSymbolizer>
              
               <PointSymbolizer>
                  <Geometry>
                     <ogc:Function name="endPoint">
                        <ogc:PropertyName>geom</ogc:PropertyName>
                     </ogc:Function>
                  </Geometry>
                  <Graphic>
                     <Mark>
                        <WellKnownName>circle</WellKnownName>
                        <Fill>
                           <CssParameter name="fill">#CC3300</CssParameter>
                        </Fill>
                     </Mark>
                     <Size>7</Size>
                  </Graphic>
               </PointSymbolizer>
            </Rule>
           
         </FeatureTypeStyle>
      </UserStyle>
   </NamedLayer>
</StyledLayerDescriptor>