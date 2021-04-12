<?xml version="1.0" encoding="UTF-8"?><sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>AIRSPACE_NAS</sld:Name>
    <sld:UserStyle>
      <sld:Name>AIRSPACE_NAS</sld:Name>
      <sld:Title>AIRSPACE_NAS</sld:Title>
      <sld:Abstract>NAS AIRSPACE STYLE</sld:Abstract>
      <sld:FeatureTypeStyle>
        <sld:Name>NAS</sld:Name>
        <sld:Rule>
          <sld:Title>NAS_AIRSPACE</sld:Title>
          <sld:MinScaleDenominator>100000.0</sld:MinScaleDenominator>
          
          <sld:PolygonSymbolizer>
            <sld:Stroke>           
              <sld:GraphicStroke>            
                <sld:Graphic>               
                  <sld:Mark>               
                    <sld:WellKnownName>shape://vertline</sld:WellKnownName>             
                    <sld:Stroke>            
                      <sld:CssParameter name="stroke">#0150AD</sld:CssParameter>                   
                      <sld:CssParameter name="stroke-width">1</sld:CssParameter>               
                    </sld:Stroke>             
                  </sld:Mark>             
                  <sld:Size>2</sld:Size>           
                </sld:Graphic>         
              </sld:GraphicStroke>
           <sld:CssParameter name="stroke">#0150AD</sld:CssParameter>
           <sld:CssParameter name="stroke-width">1</sld:CssParameter>   
           </sld:Stroke>
          </sld:PolygonSymbolizer>

        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>