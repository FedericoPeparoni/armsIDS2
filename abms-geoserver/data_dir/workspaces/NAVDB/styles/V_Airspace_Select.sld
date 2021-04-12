<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version="1.0.0" xmlns="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc"
  xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd">
  <NamedLayer>
    <Name>V_Airspace_Select</Name>
    <UserStyle>
      <Name>V_Airspace_Select</Name>
      <Title>V_Airspace_Select</Title>
      <FeatureTypeStyle>
        <Name>V_Airspace_Select</Name>

		<!--comment>AIRSPACE TYPE FIR</comment-->        
		<Rule>
          <Title>FIR_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsLike wildCard="*" singleChar="?" escape="\">
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>FIR*</ogc:Literal>
            </ogc:PropertyIsLike>
          </ogc:Filter>      
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#734388</CssParameter>
              <CssParameter name="fill-opacity">0.8</CssParameter>
            </Fill>
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://vertline</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#734388</CssParameter>                   
                      <CssParameter name="stroke-width">0.8</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>1</Size>           
                </Graphic>         
              </GraphicStroke>  
           </Stroke>                 
          </PolygonSymbolizer>
		</Rule>

		<!--comment>AIRSPACE TYPE TMA</comment-->  
        <Rule>
          <Title>TMA_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>TMA</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#734388</CssParameter>
              <CssParameter name="fill-opacity">0.8</CssParameter>
            </Fill>       
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://vertline</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#734388</CssParameter>                   
                      <CssParameter name="stroke-width">0.8</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>1</Size>           
                </Graphic>         
              </GraphicStroke>  
           </Stroke>         
          </PolygonSymbolizer>
        </Rule>

        <!--comment> {AttributeName} {Direction} </comment--> 
        <VendorOption name="sortBy">TYP A</VendorOption>

      </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>