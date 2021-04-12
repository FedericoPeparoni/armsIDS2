<?xml version="1.0" encoding="UTF-8"?><sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>AIRSPACE_TMA</sld:Name>
    <sld:UserStyle>
      <sld:Name>AIRSPACE_TMA</sld:Name>
      <sld:Title>AIRSPACE_TMA</sld:Title>
      <sld:Abstract>TMA AIRSPACE STYLE</sld:Abstract>
      <sld:FeatureTypeStyle>
        <sld:Name>TMA</sld:Name>
        <sld:Rule>
          <sld:Title>TMA_AIRSPACE</sld:Title>
          <sld:MinScaleDenominator>100000.0</sld:MinScaleDenominator>
          <sld:PolygonSymbolizer>
            <sld:Fill>
              <sld:CssParameter name="fill">#FDE95F</sld:CssParameter>
              <sld:CssParameter name="fill-opacity">0.50</sld:CssParameter>
            </sld:Fill>       
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://vertline</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#FDE95F</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>1</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#FDE95F</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>         
            
          </sld:PolygonSymbolizer>
          
          <sld:TextSymbolizer>
            <sld:Label>
              <ogc:PropertyName>IDENT</ogc:PropertyName>
            </sld:Label>
            <sld:Font>
              <sld:CssParameter name="font-family">Serif</sld:CssParameter>
              <sld:CssParameter name="font-size">10</sld:CssParameter>
              <sld:CssParameter name="font-style">normal</sld:CssParameter>
              <sld:CssParameter name="font-weight">normal</sld:CssParameter>
            </sld:Font>
            <sld:LabelPlacement>
              <sld:PointPlacement>
                <sld:AnchorPoint>
                  <sld:AnchorPointX>0.0</sld:AnchorPointX>
                  <sld:AnchorPointY>0.5</sld:AnchorPointY>
                </sld:AnchorPoint>
              </sld:PointPlacement>
            </sld:LabelPlacement>
            <sld:VendorOption name="graphic-resize">stretch</sld:VendorOption>
              <sld:VendorOption name="graphic-margin">3</sld:VendorOption>
          </sld:TextSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>