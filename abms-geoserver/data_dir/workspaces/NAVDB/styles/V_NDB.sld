<?xml version="1.0" encoding="UTF-8"?>
<StyledLayerDescriptor version="1.0.0"
  xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd" xmlns="http://www.opengis.net/sld"
  xmlns:ogc="http://www.opengis.net/ogc" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <NamedLayer>
    <Name>vor</Name>
    <UserStyle>
      <Name>ndb</Name>
      <Title>ndb style</Title>
      <FeatureTypeStyle>
        <Rule>
          <PointSymbolizer>

            <Graphic>
              <Mark>
                <WellKnownName>circle</WellKnownName>
                <Fill>
                  <CssParameter name="fill">#535AFC</CssParameter>
                  <CssParameter name="fill-opacity">0.2</CssParameter>
                </Fill>
                <Stroke>
                  <CssParameter name="stroke">#38190D</CssParameter>
                  <CssParameter name="stroke-width">0.5</CssParameter>
                </Stroke>
              </Mark>
              <Size>8</Size>
            </Graphic>            
          </PointSymbolizer>
        </Rule>

    <Rule>
      <MaxScaleDenominator>5000000</MaxScaleDenominator>
      <TextSymbolizer>
        <Label>
          <ogc:PropertyName>CODEID</ogc:PropertyName>
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
        
        <!--Rule>
          <MaxScaleDenominator>80000000</MaxScaleDenominator>
          <TextSymbolizer>
            <Label>
              <ogc:PropertyName>codeid</ogc:PropertyName>
            </Label>
            <Font/>
            <LabelPlacement>
              <PointPlacement>
                <AnchorPoint>
                  <AnchorPointX>0.5</AnchorPointX>
                  <AnchorPointY>0.0</AnchorPointY>
                </AnchorPoint>
                <Displacement>
                  <DisplacementX>0</DisplacementX>
                  <DisplacementY>10</DisplacementY>
                </Displacement>
              </PointPlacement>
            </LabelPlacement>
            <Fill>
              <CssParameter name="fill">#38190D</CssParameter>
            </Fill>
          </TextSymbolizer>
        </Rule-->
       </Rule> 
      </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>