<?xml version="1.0" encoding="UTF-8"?><sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>AIRSPACE_PROTECT</sld:Name>
    <sld:UserStyle>
      <sld:Name>AIRSPACE_PROTECT</sld:Name>
      <sld:Title>AIRSPACE_PROTECT</sld:Title>
      <sld:Abstract>PROTECT AIRSPACE STYLE</sld:Abstract>
      <sld:FeatureTypeStyle>
        <sld:Name>PROTECT</sld:Name>
        <sld:Rule>
          <sld:Title>PROTECT_AIRSPACE</sld:Title>
          <sld:MinScaleDenominator>100000.0</sld:MinScaleDenominator>
          
          <sld:PolygonSymbolizer>
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://vertline</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#65ED5A</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>1</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#65ED5A</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>         
            
          </sld:PolygonSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>