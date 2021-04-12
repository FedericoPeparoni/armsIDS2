<?xml version="1.0" encoding="UTF-8"?><sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>AIRSPACE_SECTOR</sld:Name>
    <sld:UserStyle>
      <sld:Name>AIRSPACE_SECTOR</sld:Name>
      <sld:Title>AIRSPACE_SECTOR</sld:Title>
      <sld:Abstract>SECTOR AIRSPACE STYLE</sld:Abstract>
      <sld:FeatureTypeStyle>
        <sld:Name>SECTOR</sld:Name>
        <sld:Rule>
          <sld:Title>SECTOR_AIRSPACE</sld:Title>
          <sld:MinScaleDenominator>100000.0</sld:MinScaleDenominator>
          
          <sld:PolygonSymbolizer>
            <sld:Stroke>           
              <sld:GraphicStroke>            
                <sld:Graphic>               
                  <sld:Mark>               
                    <sld:WellKnownName>shape://vertline</sld:WellKnownName>             
                    <sld:Stroke>            
                      <sld:CssParameter name="stroke">#990000</sld:CssParameter>                   
                      <sld:CssParameter name="stroke-width">0.5</sld:CssParameter>
                      <CssParameter name="stroke-opacity">0.5</CssParameter>               
                    </sld:Stroke>             
                  </sld:Mark>             
                  <sld:Size>1</sld:Size>           
                </sld:Graphic>         
              </sld:GraphicStroke>
           <sld:CssParameter name="stroke">#D8D8D8</sld:CssParameter>
           <sld:CssParameter name="stroke-width">0.5</sld:CssParameter>   
           </sld:Stroke>
          </sld:PolygonSymbolizer>

        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>