<?xml version="1.0" encoding="UTF-8"?><sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>AIRSPACE_PAPA</sld:Name>
    <sld:UserStyle>
      <sld:Name>AIRSPACE_PAPA</sld:Name>
      <sld:Title>AIRSPACE_PAPA</sld:Title>
      <sld:Abstract>PAPA AIRSPACE STYLE</sld:Abstract>
      <sld:FeatureTypeStyle>
        <sld:Name>PAPA</sld:Name>
        <sld:Rule>
          <sld:Title>PAPA_AIRSPACE</sld:Title>
          <sld:MinScaleDenominator>100000.0</sld:MinScaleDenominator>
          <sld:PolygonSymbolizer>
            
            <sld:Stroke>           
              <sld:GraphicStroke>            
                <sld:Graphic>               
                  <sld:Mark>               
                    <sld:WellKnownName>shape://slash</sld:WellKnownName>             
                    <sld:Stroke>            
                      <sld:CssParameter name="stroke">#CD410C</sld:CssParameter>                   
                      <sld:CssParameter name="stroke-width">0.25</sld:CssParameter>               
                    </sld:Stroke>             
                  </sld:Mark>             
                  <sld:Size>2</sld:Size>           
                </sld:Graphic>         
              </sld:GraphicStroke>
           <sld:CssParameter name="stroke">#CD410C</sld:CssParameter>
           <sld:CssParameter name="stroke-width">0.25</sld:CssParameter>   
           </sld:Stroke>
            
          </sld:PolygonSymbolizer>
          <!--sld:TextSymbolizer>
            <sld:Label>
              <ogc:PropertyName>ident</ogc:PropertyName>
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
            <sld:VendorOption name="graphic-margin">2</sld:VendorOption>
          </sld:TextSymbolizer-->
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>