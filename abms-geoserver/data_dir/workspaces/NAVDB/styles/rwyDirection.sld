<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version="1.0.0" xmlns="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc"
  xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd">
  <NamedLayer>
    <Name>Default rwy Direction</Name>
    <UserStyle>
      <Title>Default rwy Direction</Title>
      <Abstract>A sample style for rwy Direction</Abstract>
      <FeatureTypeStyle>
         <Rule>
             <Name>Medium</Name>
             <MaxScaleDenominator>150000</MaxScaleDenominator>
             <MinScaleDenominator>40000</MinScaleDenominator>
             <PointSymbolizer>
               <Graphic>
                 <Mark>
                   <WellKnownName>triangle</WellKnownName>
                   <Fill>
                     <CssParameter name="fill">#FD6A08</CssParameter>
                   </Fill>
                 </Mark>
                <Size>14</Size>
                 <Rotation>180</Rotation>
               </Graphic>
            </PointSymbolizer>
            <TextSymbolizer>
                <Label>
                  <ogc:PropertyName>RWYENDID</ogc:PropertyName>
                </Label>
                <Font>
                  <CssParameter name="font-family">Lucida</CssParameter>
                  <CssParameter name="font-size">14</CssParameter>
                  <CssParameter name="font-style">Normal</CssParameter>
                </Font>  
                 <LabelPlacement>
                   <PointPlacement>
                       <AnchorPoint>
                         <AnchorPointX>0.0</AnchorPointX>
                         <AnchorPointY>0.0</AnchorPointY>
                       </AnchorPoint>
                       <Displacement>
                           <DisplacementX>-10.0</DisplacementX>
                           <DisplacementY>15.0</DisplacementY>
                         </Displacement>
                   </PointPlacement>
                 </LabelPlacement>      
                <Fill>
                  <CssParameter name="fill">#333333</CssParameter>
                </Fill>
            </TextSymbolizer>
       </Rule>
       <Rule>
           <Name>Large</Name>
           <MaxScaleDenominator>40000</MaxScaleDenominator>
           <PointSymbolizer>
             <Graphic>
                 <Mark>
                   <WellKnownName>triangle</WellKnownName>
                   <Fill>
                     <CssParameter name="fill">#FD6A08</CssParameter>
                   </Fill>
                 </Mark>
                <Size>30</Size>
                 <Rotation>180</Rotation>
               </Graphic>
            </PointSymbolizer>
            <TextSymbolizer>
                <Label>
                  <ogc:PropertyName>RWYENDID</ogc:PropertyName>
                </Label>
                <Font>
                  <CssParameter name="font-family">Lucida</CssParameter>
                  <CssParameter name="font-size">14</CssParameter>
                  <CssParameter name="font-style">Normal</CssParameter>
                </Font>  
                 <LabelPlacement>
                   <PointPlacement>
                       <AnchorPoint>
                         <AnchorPointX>0.0</AnchorPointX>
                         <AnchorPointY>0.0</AnchorPointY>
                       </AnchorPoint>
                       <Displacement>
                           <DisplacementX>-10.0</DisplacementX>
                           <DisplacementY>15.0</DisplacementY>
                         </Displacement>
                   </PointPlacement>
                 </LabelPlacement>      
                  <Fill>
                    <CssParameter name="fill">#333333</CssParameter>
                  </Fill>
           </TextSymbolizer>
         </Rule>
      </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>