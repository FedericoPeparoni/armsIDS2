<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version="1.0.0" xmlns="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc"
  xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd">
  <NamedLayer>
    <Name>FIR Airspace Style</Name>
    <UserStyle>
      <Name>V_FIR</Name>
      <Title>AERODB FIR AIRSPACE</Title>
      <FeatureTypeStyle>
        <Name>FIR</Name>        
        
    <Rule>
          <Title>FIR AIRSPACE</Title>      
          <Abstract>A BLUE SLASH LINE WITH FILL COLOR</Abstract>
      <PolygonSymbolizer>  
            <Fill>
              <CssParameter name="fill">#006dff</CssParameter>
              <CssParameter name="fill-opacity">0.10</CssParameter>
            </Fill>
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://slash</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#006dff</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#006dff</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>           
          </PolygonSymbolizer>
        </Rule>
        
        <Rule>
          <TextSymbolizer>
            <Label>
              <ogc:PropertyName>IDENT</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">Lucida</CssParameter>
              <CssParameter name="font-size">08</CssParameter>
              <CssParameter name="font-style">Normal</CssParameter>
              <CssParameter name="font-weight">bold</CssParameter>
            </Font>
            <LabelPlacement>
              <PointPlacement>
                <AnchorPoint>
                  <AnchorPointX>0.5</AnchorPointX>
                  <AnchorPointY>0.0</AnchorPointY>
                </AnchorPoint>
                <Displacement>
                  <DisplacementX>5</DisplacementX>
                  <DisplacementY>5</DisplacementY>
                </Displacement>
              </PointPlacement>
            </LabelPlacement>
            <Halo>
              <Radius>1</Radius>
              <Fill>
                <CssParameter name="fill">#ffffff</CssParameter>
              </Fill>
            </Halo>
            <Fill>
              <CssParameter name="fill">#000000</CssParameter>
            </Fill>
          </TextSymbolizer>
        </Rule>
      </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>