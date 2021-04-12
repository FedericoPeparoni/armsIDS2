<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version="1.0.0" xmlns="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc"
  xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd">
  <NamedLayer>
    <Name>RUNWAY</Name>
    <UserStyle>
      <Title> RUNWAY POLYGON</Title>
      <Abstract>A Runway style that just draws out a solid red interior with a red 1px outline</Abstract>
      <FeatureTypeStyle>
        <Rule>
          <Title>RUNWAY</Title>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#fd0000</CssParameter>
              <!--CssParameter name="fill">#AAAAAA</CssParameter-->
            </Fill>
            <Stroke>
              <CssParameter name="stroke">#fd0000</CssParameter>
              <CssParameter name="stroke-width">0.5</CssParameter>
            </Stroke>
          </PolygonSymbolizer>
        </Rule>

      </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>