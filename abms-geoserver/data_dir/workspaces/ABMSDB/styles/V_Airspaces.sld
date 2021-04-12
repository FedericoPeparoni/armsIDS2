<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version="1.0.0" xmlns="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc"
  xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd">
  <NamedLayer>
    <Name>AIRSPACEs</Name>
    <UserStyle>
      <Name>V_AIRSPACE</Name>
      <Title>AERODB AIRSPACE</Title>
      <FeatureTypeStyle>
        <Name>AIRSPACE</Name>

		<!--comment>AIRSPACE TYPE FIR</comment-->        
		<Rule>
          <Title>FIR_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsLike wildCard="*" singleChar="?" escapeChar="\">
              <ogc:PropertyName>Type</ogc:PropertyName>
              <ogc:Literal>FIR*</ogc:Literal>
            </ogc:PropertyIsLike>
          </ogc:Filter>      
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#d4d4d4</CssParameter>
              <CssParameter name="fill-opacity">0.8</CssParameter>
            </Fill>
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://vertline</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#797979</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>1</Size>           
                </Graphic>         
              </GraphicStroke>  
           </Stroke>                 
          </PolygonSymbolizer>
		   <!--
			<TextSymbolizer>
				<Label>
				  <ogc:PropertyName>Name</ogc:PropertyName>
				</Label>
				<Font>
				  <CssParameter name="font-family">Serif</CssParameter>
				  <CssParameter name="font-size">8</CssParameter>
				  <CssParameter name="font-style">normal</CssParameter>
				  <CssParameter name="font-weight">normal</CssParameter>
				</Font>
				<LabelPlacement>
				  <PointPlacement>
					<AnchorPoint>
					  <AnchorPointX>0.0</AnchorPointX>
					  <AnchorPointY>0.5</AnchorPointY>
					</AnchorPoint>
				  </PointPlacement>
				</LabelPlacement>
				<VendorOption name="graphic-resize">stretch</VendorOption>
				<VendorOption name="graphic-margin">2</VendorOption>
			</TextSymbolizer>
			-->
		</Rule>

		<!--comment>AIRSPACE TYPE TMA</comment-->  
        <Rule>
          <Title>TMA_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>Type</ogc:PropertyName>
              <ogc:Literal>TMA</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#ff174b</CssParameter>
              <CssParameter name="fill-opacity">0.45</CssParameter>
            </Fill>       
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://vertline</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#f90038</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>1</Size>           
                </Graphic>         
              </GraphicStroke>  
           </Stroke>         
          </PolygonSymbolizer>
          <!--
          <TextSymbolizer>
            <Label>
              <ogc:PropertyName>Name</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">Serif</CssParameter>
              <CssParameter name="font-size">8</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
              <CssParameter name="font-weight">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <PointPlacement>
                <AnchorPoint>
                  <AnchorPointX>0.0</AnchorPointX>
                  <AnchorPointY>0.5</AnchorPointY>
                </AnchorPoint>
              </PointPlacement>
            </LabelPlacement>
            <VendorOption name="graphic-resize">stretch</VendorOption>
            <VendorOption name="graphic-margin">2</VendorOption>
          </TextSymbolizer>
		  -->
        </Rule>

        <!--comment> {AttributeName} {Direction} </comment--> 
        <VendorOption name="sortBy">Type A</VendorOption>

      </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>
