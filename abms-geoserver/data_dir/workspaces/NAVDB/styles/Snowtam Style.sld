<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version="1.0.0" xmlns="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc"
  xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd">
  <NamedLayer>
    <Name>XNS</Name>
    <UserStyle>
      <Title>Red</Title>
      <Abstract>A style for Snotwam</Abstract>

    <FeatureTypeStyle>
      <Rule>
         <Name>Large</Name>
         <MaxScaleDenominator>500000</MaxScaleDenominator>
         <PolygonSymbolizer>
          <Fill>
            <CssParameter name="fill">#FFFFFF</CssParameter>
            <CssParameter name="fill-opacity">1</CssParameter>
          </Fill>
          <Stroke>
            <CssParameter name="stroke">#000000</CssParameter>
            <CssParameter name="stroke-width">2</CssParameter>
          </Stroke>
          </PolygonSymbolizer>
        
          <PointSymbolizer>
            <Graphic>
              <Mark>
                <WellKnownName>circle</WellKnownName>
                <Fill>
                  <CssParameter name="fill">#fb0303</CssParameter>
                </Fill>
              </Mark>
              <Size>6</Size>
            </Graphic>
          </PointSymbolizer>

       </Rule>
       <Rule>
         <Name>Small</Name>
         <MinScaleDenominator>500000</MinScaleDenominator>
         
         <PolygonSymbolizer>
          <Fill>
            <CssParameter name="fill">#f9fd00</CssParameter>
            <!--CssParameter name="fill">#FFFFFF</CssParameter-->
            <CssParameter name="fill-opacity">1</CssParameter>
          </Fill>
          <Stroke>
            <CssParameter name="stroke">#fb0303</CssParameter>
            <CssParameter name="stroke-width">2</CssParameter>
          </Stroke>
        </PolygonSymbolizer>
        
        <PointSymbolizer>
         <Graphic>
          <ExternalGraphic>
            <OnlineResource
              xlink:type="simple"
              xlink:href="snow.png" />
            <Format>image/png</Format>
          </ExternalGraphic>
          <Size>18</Size>
          </Graphic>
         </PointSymbolizer>
        
      </Rule>
    </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>