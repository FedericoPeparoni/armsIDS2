<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version="1.0.0"
  xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd"
  xmlns="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc"
  xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

  <NamedLayer>
    <Name>V_FlightMovement_SP</Name>
    <UserStyle>
      <Title>A Flight Movement style</Title>
      
      <FeatureTypeStyle>
     <Rule>
       <Name>FPL</Name>
       <ogc:Filter>
         <ogc:PropertyIsNull>
           <ogc:PropertyName>fpl_route_geom</ogc:PropertyName>
           
         </ogc:PropertyIsNull>
       </ogc:Filter>
       <LineSymbolizer>
         <Stroke>
           <CssParameter name="stroke">#ffff00</CssParameter>
           <CssParameter name="stroke-width">2</CssParameter>
         </Stroke>
       </LineSymbolizer>
     </Rule>
   </FeatureTypeStyle>
      
   <FeatureTypeStyle>
     <Rule>
       <Name>Billable</Name>
       <ogc:Filter>
         <ogc:PropertyIsEqualTo>
           <ogc:PropertyName>billable_route</ogc:PropertyName>
           <ogc:Literal></ogc:Literal>
         </ogc:PropertyIsEqualTo>
       </ogc:Filter>
       <LineSymbolizer>
         <Stroke>
           <CssParameter name="stroke">#ff9933</CssParameter>
           <CssParameter name="stroke-width">3</CssParameter>
         </Stroke>
       </LineSymbolizer>
     </Rule>
   </FeatureTypeStyle>
      
   <FeatureTypeStyle>
     <Rule>
     <Name>Radar</Name>
       <ogc:Filter>
         <ogc:PropertyIsEqualTo>
           <ogc:PropertyName>radar_route</ogc:PropertyName>
           <ogc:Literal></ogc:Literal>
         </ogc:PropertyIsEqualTo>
       </ogc:Filter>
       <LineSymbolizer>
         <Stroke>
           <CssParameter name="stroke">#FF0000</CssParameter>
           <CssParameter name="stroke-width">3</CssParameter>
         </Stroke>
       </LineSymbolizer>
     </Rule>
   </FeatureTypeStyle>
      
      
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>