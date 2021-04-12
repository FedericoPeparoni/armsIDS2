<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version="1.0.0" xmlns="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc"
  xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd">
  <NamedLayer>
    <Name>AIRSPACEs</Name>
    <UserStyle>
      <Name>V_AIRSPACE</Name>
      <Title>AERODB AIRSPACE</Title>
      <FeatureTypeStyle>
        <Name>AIRSPACE</Name>

<!--comment>AIRSPACE TYPE A</comment-->        
    <Rule>
          <Title>A_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>A</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>      
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#D168D2</CssParameter>
              <CssParameter name="fill-opacity">0.40</CssParameter>
            </Fill>
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://vertline</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#D168D2</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#D168D2</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>             
          </PolygonSymbolizer>
      <TextSymbolizer>
            <Label>
              <ogc:PropertyName>IDENT</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">Serif</CssParameter>
              <CssParameter name="font-size">8</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
              <CssParameter name="font-weight">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <PointPlacement>
                <AnchorPoint>
                  <AnchorPointX>0.0</AnchorPointX>
                  <AnchorPointY>0.5</AnchorPointY>
                </AnchorPoint>
              </PointPlacement>
            </LabelPlacement>
            <VendorOption name="graphic-resize">stretch</VendorOption>
            <VendorOption name="graphic-margin">2</VendorOption>
          </TextSymbolizer>
    </Rule>

<!--comment>AIRSPACE TYPE ADIZ_AWY</comment-->  
        <Rule>
          <Title>ADIZ_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>ADIZ</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#f80000</CssParameter>
              <CssParameter name="fill-opacity">0.35</CssParameter>
            </Fill>       
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://vertline</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#f80000</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>1</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#f80000</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>         
          </PolygonSymbolizer>     
          <TextSymbolizer>
            <Label>
              <ogc:PropertyName>IDENT</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">Serif</CssParameter>
              <CssParameter name="font-size">8</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
              <CssParameter name="font-weight">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <PointPlacement>
                <AnchorPoint>
                  <AnchorPointX>0.0</AnchorPointX>
                  <AnchorPointY>0.5</AnchorPointY>
                </AnchorPoint>
              </PointPlacement>
            </LabelPlacement>
            <VendorOption name="graphic-resize">stretch</VendorOption>
            <VendorOption name="graphic-margin">2</VendorOption>
          </TextSymbolizer>
        </Rule>

<!--comment>AIRSPACE TYPE ADIZ_AWY</comment-->     
    <Rule>
          <Title>AWY_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>AWY</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#f80000</CssParameter>
              <CssParameter name="fill-opacity">0.35</CssParameter>
            </Fill>       
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://vertline</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#f80000</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>1</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#f80000</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>         
          </PolygonSymbolizer>     
          <TextSymbolizer>
            <Label>
              <ogc:PropertyName>IDENT</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">Serif</CssParameter>
              <CssParameter name="font-size">8</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
              <CssParameter name="font-weight">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <PointPlacement>
                <AnchorPoint>
                  <AnchorPointX>0.0</AnchorPointX>
                  <AnchorPointY>0.5</AnchorPointY>
                </AnchorPoint>
              </PointPlacement>
            </LabelPlacement>
            <VendorOption name="graphic-resize">stretch</VendorOption>
            <VendorOption name="graphic-margin">2</VendorOption>
          </TextSymbolizer>
        </Rule>
    
    
<!--comment>AIRSPACE TYPE ASR</comment-->
        <Rule>
          <Title>ASR_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>ASR</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>          
          <PolygonSymbolizer>
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://slash</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#D27961</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#D27961</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>
          </PolygonSymbolizer>
        </Rule>    

<!--comment>AIRSPACE TYPE CTA,CTA-P</comment--> 
        <Rule>
          <Title>CTA_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>CTA</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>
            <Stroke>           
              <CssParameter name="stroke">#966300</CssParameter>                                         
              <CssParameter name="stroke-width">0.5</CssParameter>                   
            </Stroke>                                      
          </PolygonSymbolizer>
        </Rule>
        <Rule>
          <Title>CTA-P_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>CTA-P</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>
            <Stroke>           
              <CssParameter name="stroke">#966300</CssParameter>                                         
              <CssParameter name="stroke-width">0.5</CssParameter>                   
            </Stroke>                                      
          </PolygonSymbolizer>
        </Rule>
    
<!--comment>AIRSPACE TYPE CTR,CTR-P</comment-->     
        <Rule>
          <Title>CTR_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>CTR</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>          
          <PolygonSymbolizer>
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://vertline</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#119DE8</CssParameter>                   
                      <CssParameter name="stroke-width">1</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#119DE8</CssParameter>
           <CssParameter name="stroke-width">1</CssParameter>   
           </Stroke>
          </PolygonSymbolizer>
        </Rule>
        <Rule>
          <Title>CTR-P_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>CTR-P</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>          
          <PolygonSymbolizer>
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://vertline</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#119DE8</CssParameter>                   
                      <CssParameter name="stroke-width">1</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#119DE8</CssParameter>
           <CssParameter name="stroke-width">1</CssParameter>   
           </Stroke>
          </PolygonSymbolizer>
        </Rule>
    
<!--comment>AIRSPACE TYPE D,D-OTHER,D-AMC</comment-->
        <Rule>
          <Title>DANGER_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>D</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://slash</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#DF1D04</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#DF1D04</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>
          </PolygonSymbolizer>
        </Rule>
        <Rule>
          <Title>DANGER-OTHER_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>D-OTHER</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://slash</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#DF1D04</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#DF1D04</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>
          </PolygonSymbolizer>
        </Rule>
        <Rule>
          <Title>DANGER-AMC_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>D-AMC</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://slash</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#DF1D04</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#DF1D04</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>
          </PolygonSymbolizer>
        </Rule>    

<!--comment>AIRSPACE TYPE NAS</comment-->    
        <Rule>
          <Title>NAS_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>NAS</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://vertline</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#0150AD</CssParameter>                   
                      <CssParameter name="stroke-width">1</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#0150AD</CssParameter>
           <CssParameter name="stroke-width">1</CssParameter>   
           </Stroke>
          </PolygonSymbolizer>
        </Rule>

    
<!--comment>AIRSPACE TYPE P, POLITICAL, W</comment-->
        <Rule>
          <Title>PAPA_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>P</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://slash</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#CD410C</CssParameter>                   
                      <CssParameter name="stroke-width">0.25</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#CD410C</CssParameter>
           <CssParameter name="stroke-width">0.25</CssParameter>   
           </Stroke>
          </PolygonSymbolizer>
        </Rule>
        <Rule>
          <Title>POLITICAL_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>POLITICAL</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://slash</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#CD410C</CssParameter>                   
                      <CssParameter name="stroke-width">0.25</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#CD410C</CssParameter>
           <CssParameter name="stroke-width">0.25</CssParameter>   
           </Stroke>
          </PolygonSymbolizer>
        </Rule>
        <Rule>
          <Title>W_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>W</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://slash</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#CD410C</CssParameter>                   
                      <CssParameter name="stroke-width">0.25</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#CD410C</CssParameter>
           <CssParameter name="stroke-width">0.25</CssParameter>   
           </Stroke>
          </PolygonSymbolizer>
        </Rule>    
    
<!--comment>AIRSPACE TYPE PROTECT, RAS, RCA</comment-->    
        <Rule>
          <Title>PROTECT_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>PROTECT</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>
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
          </PolygonSymbolizer>
        </Rule>
        <Rule>
          <Title>RAS_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>RAS</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>
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
          </PolygonSymbolizer>
        </Rule>
        <Rule>
          <Title>RCA_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>RCA</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>
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
          </PolygonSymbolizer>
        </Rule>

<!--comment>AIRSPACE TYPE R, R-AMC,TRA, TSA</comment-->    
        <Rule>
          <Title>ROMEO_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>R</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>            
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://slash</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#F16DE3</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#F16DE3</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>
          </PolygonSymbolizer>
        </Rule>
    <Rule>
          <Title>R-AMC_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>R-AMC</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>            
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://slash</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#F16DE3</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#F16DE3</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>
          </PolygonSymbolizer>
        </Rule>    
        <Rule>
          <Title>TRA_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>TRA</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>            
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://slash</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#F16DE3</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#F16DE3</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>
          </PolygonSymbolizer>
        </Rule>
        <Rule>
          <Title>TSA_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>TSA</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>            
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://slash</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#F16DE3</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#F16DE3</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>
          </PolygonSymbolizer>
        </Rule>    
    
<!--comment>AIRSPACE TYPE SECTOR, SECTOR-C</comment-->    
        <Rule>
          <Title>SECTOR_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>SECTOR</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>          
          <PolygonSymbolizer>
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://vertline</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#990000</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>
                      <CssParameter name="stroke-opacity">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>1</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#D8D8D8</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>
          </PolygonSymbolizer>
        </Rule>
        <Rule>
          <Title>SECTOR-C_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>SECTOR-C</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>          
          <PolygonSymbolizer>
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://vertline</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#990000</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>
                      <CssParameter name="stroke-opacity">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>1</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#D8D8D8</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>
          </PolygonSymbolizer>
        </Rule>
    
<!--comment>AIRSPACE TYPE TMA</comment-->    
        <Rule>
          <Title>TMA_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>TMA</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#FDE95F</CssParameter>
              <CssParameter name="fill-opacity">0.50</CssParameter>
            </Fill>       
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
          </PolygonSymbolizer>          
          <TextSymbolizer>
            <Label>
              <ogc:PropertyName>IDENT</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">Serif</CssParameter>
              <CssParameter name="font-size">10</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
              <CssParameter name="font-weight">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <PointPlacement>
                <AnchorPoint>
                  <AnchorPointX>0.0</AnchorPointX>
                  <AnchorPointY>0.5</AnchorPointY>
                </AnchorPoint>
              </PointPlacement>
            </LabelPlacement>
            <VendorOption name="graphic-resize">stretch</VendorOption>
              <VendorOption name="graphic-margin">3</VendorOption>
          </TextSymbolizer>
        </Rule>
        <Rule>
          <Title>TMA-P_AIRSPACE</Title>
          <Abstract>Airspace Shape</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>TMA-P</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>
          <MinScaleDenominator>100000.0</MinScaleDenominator>
          <PolygonSymbolizer>
            <Fill>
              <CssParameter name="fill">#FDE95F</CssParameter>
              <CssParameter name="fill-opacity">0.50</CssParameter>
            </Fill>       
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
          </PolygonSymbolizer>          
          <TextSymbolizer>
            <Label>
              <ogc:PropertyName>IDENT</ogc:PropertyName>
            </Label>
            <Font>
              <CssParameter name="font-family">Serif</CssParameter>
              <CssParameter name="font-size">10</CssParameter>
              <CssParameter name="font-style">normal</CssParameter>
              <CssParameter name="font-weight">normal</CssParameter>
            </Font>
            <LabelPlacement>
              <PointPlacement>
                <AnchorPoint>
                  <AnchorPointX>0.0</AnchorPointX>
                  <AnchorPointY>0.5</AnchorPointY>
                </AnchorPoint>
              </PointPlacement>
            </LabelPlacement>
            <VendorOption name="graphic-resize">stretch</VendorOption>
              <VendorOption name="graphic-margin">3</VendorOption>
          </TextSymbolizer>
        </Rule>    

      </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>