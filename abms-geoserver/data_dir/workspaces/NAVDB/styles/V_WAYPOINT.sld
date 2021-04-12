<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version="1.0.0" xmlns="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc"
  xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd">
  <NamedLayer>
    <Name>Waypoint style</Name>
    <UserStyle>
      <Title>Waypoiny</Title>
      <Abstract>Sample Style for Waypoint</Abstract>
      <FeatureTypeStyle>
        <Rule>
          <PointSymbolizer>
            <Graphic>
              <ExternalGraphic>
                <OnlineResource
                xlink:type="simple"
                xlink:href="Waypoint.png" />
                <Format>image/png</Format>
              </ExternalGraphic>
              <Size>7</Size>
            </Graphic>
          </PointSymbolizer>
        </Rule>
        
    <Rule>
      <MaxScaleDenominator>5000000</MaxScaleDenominator>
      <TextSymbolizer>
        <Label>
          <ogc:PropertyName>IDENT</ogc:PropertyName>
        </Label>
        <Font>
          <CssParameter name="font-family">Lucida</CssParameter>
          <CssParameter name="font-size">10</CssParameter>
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
        <!--Halo>
          <Radius>1</Radius>
          <Fill>
            <CssParameter name="fill">#ffffff</CssParameter>
          </Fill>
        </Halo-->
        <Fill>
          <CssParameter name="fill">#000000</CssParameter>
        </Fill>
      </TextSymbolizer>
        </Rule>
      </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>