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
        
        <Rule>
          <Title>RESTRICTED AIRSPACE</Title>      
          <Abstract>A SLASH LINE</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>typ</ogc:PropertyName>
              <ogc:Literal>R</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>           
          <PolygonSymbolizer>  
            <Fill>
              <CssParameter name="fill">#c333cc</CssParameter>
              <CssParameter name="fill-opacity">0.10</CssParameter>
            </Fill>
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://slash</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#c333cc</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#c333cc</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>           
          </PolygonSymbolizer>
        </Rule>
                
        <Rule>
          <Title>PROHIBITED AIRSPACE</Title>      
          <Abstract>A SLASH LINE</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>typ</ogc:PropertyName>
              <ogc:Literal>P</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>           
          <PolygonSymbolizer>  
            <Fill>
              <CssParameter name="fill">#c333cc</CssParameter>
              <CssParameter name="fill-opacity">0.10</CssParameter>
            </Fill>
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://slash</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#c333cc</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#c333cc</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>           
          </PolygonSymbolizer>
        </Rule>
           
        <Rule>
          <Title>DANGER AIRSPACE</Title>      
          <Abstract>A CYAN LINE</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>typ</ogc:PropertyName>
              <ogc:Literal>D</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>           
          <PolygonSymbolizer>  
            <Fill>
              <CssParameter name="fill">#c333cc</CssParameter>
              <CssParameter name="fill-opacity">0.10</CssParameter>
            </Fill>
            <Stroke>           
              <CssParameter name="stroke">#c333cc</CssParameter>                                         
              <CssParameter name="stroke-width">0.5</CssParameter>                   
            </Stroke>                                      
          </PolygonSymbolizer>
        </Rule>    

        <Rule>
          <Title>CTR BOUNDARY AIRSPACE</Title>      
          <Abstract>A BLUE LINE</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>typ</ogc:PropertyName>
              <ogc:Literal>CTR</ogc:Literal>
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>           
          <PolygonSymbolizer>  
            <Fill>
              <CssParameter name="fill">#2c7af8</CssParameter>
              <CssParameter name="fill-opacity">0.10</CssParameter>
            </Fill>
            <Stroke>           
              <CssParameter name="stroke">#2c7af8</CssParameter>                                         
              <CssParameter name="stroke-width">0.8</CssParameter>                   
            </Stroke>                                      
          </PolygonSymbolizer>
        </Rule>    

        <Rule>
          <Title>TMA BOUNDARY AIRSPACE</Title>      
          <Abstract>A YELLOW LINE</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>typ</ogc:PropertyName>
              <ogc:Literal>TMA</ogc:Literal>
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>           
          <PolygonSymbolizer>  
            <Fill>
              <CssParameter name="fill">#fddf00</CssParameter>
              <CssParameter name="fill-opacity">0.10</CssParameter>
            </Fill>
            <Stroke>           
              <CssParameter name="stroke">#fddf00</CssParameter>                                         
              <CssParameter name="stroke-width">0.8</CssParameter>                   
            </Stroke>                                      
          </PolygonSymbolizer>
        </Rule>    

        <Rule>
          <Title>TSA AIRSPACE</Title>      
          <Abstract>A VERTICAL BOUNDARY LINE</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>typ</ogc:PropertyName>
              <ogc:Literal>TSA</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>           
          <PolygonSymbolizer>  
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://slash</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#fd0000</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#fd0000</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>           
          </PolygonSymbolizer>
        </Rule>
       
        <Rule>
          <Title>CTA BOUNDARY AIRSPACE</Title>      
          <Abstract>A BROWN LINE</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>typ</ogc:PropertyName>
              <ogc:Literal>CTA</ogc:Literal>
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>           
          <PolygonSymbolizer>  
            <Fill>
              <CssParameter name="fill">#966300</CssParameter>
              <CssParameter name="fill-opacity">0.10</CssParameter>
            </Fill>
            <Stroke>           
              <CssParameter name="stroke">#966300</CssParameter>                                         
              <CssParameter name="stroke-width">0.8</CssParameter>                   
            </Stroke>                                      
          </PolygonSymbolizer>
        </Rule>    

        <Rule>
          <Title>CDA AIRSPACE</Title>      
          <Abstract>A GREEN LINE</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>typ</ogc:PropertyName>
              <ogc:Literal>CDA</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>           
          <PolygonSymbolizer>  
            <Stroke>           
              <CssParameter name="stroke">#2ff800</CssParameter>                                         
              <CssParameter name="stroke-width">0.5</CssParameter>                   
            </Stroke>                                      
          </PolygonSymbolizer>
        </Rule>        

        <Rule>
          <Title>OCA AIRSPACE</Title>      
          <Abstract>A SLASH LINE</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>typ</ogc:PropertyName>
              <ogc:Literal>OCA</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>           
          <PolygonSymbolizer>  
            <Fill>
              <CssParameter name="fill">#0054f8</CssParameter>
              <CssParameter name="fill-opacity">0.10</CssParameter>
            </Fill>
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://slash</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#0054f8</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#c333cc</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>           
          </PolygonSymbolizer>
        </Rule>   

         <Rule>
          <Title>ADIZ AIRSPACE</Title>      
          <Abstract>AIR DEFENSE IDENTIFICATION ZONE</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>typ</ogc:PropertyName>
              <ogc:Literal>ADIZ</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>           
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
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#f80000</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>           
          </PolygonSymbolizer>
        </Rule>   
        
         <Rule>
          <Title>POLITICAL AIRSPACE</Title>      
          <Abstract>POLITICAL</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>typ</ogc:PropertyName>
              <ogc:Literal>POLITICAL</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>           
          <PolygonSymbolizer>  
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://vertline</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#0b0505</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#0b0505</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>           
          </PolygonSymbolizer>
        </Rule>           
                 
        <Rule>
          <Title>SECTOR AIRSPACE</Title>      
          <Abstract>SECTOR</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>typ</ogc:PropertyName>
              <ogc:Literal>SECTOR</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>           
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
                  <Size>2</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#f80000</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>           
          </PolygonSymbolizer>
        </Rule>

           
        <Rule>
          <Title>UTA AIRSPACE</Title>      
          <Abstract>UTA</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>typ</ogc:PropertyName>
              <ogc:Literal>SECTOR</ogc:Literal>    
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>           
          <PolygonSymbolizer>  
            <Fill>
              <CssParameter name="fill">#7d5cdc</CssParameter>
              <CssParameter name="fill-opacity">0.35</CssParameter>
            </Fill>
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>shape://dot</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#7d5cdc</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>5</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#7d5cdc</CssParameter>
           <CssParameter name="stroke-width">0.5</CssParameter>   
           </Stroke>           
          </PolygonSymbolizer>
        </Rule>     

                
        <Rule>
          <Title>ATZ BOUNDARY AIRSPACE</Title>      
          <Abstract>A YELLOW LINE</Abstract>
          <ogc:Filter>        
            <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>typ</ogc:PropertyName>
              <ogc:Literal>ATZ</ogc:Literal>
            </ogc:PropertyIsEqualTo>       
          </ogc:Filter>           
          <PolygonSymbolizer>  
            <Fill>
              <CssParameter name="fill">#eefd00</CssParameter>
              <CssParameter name="fill-opacity">0.10</CssParameter>
            </Fill>
            <Stroke>           
              <CssParameter name="stroke">#eefd00</CssParameter>                                         
              <CssParameter name="stroke-width">0.8</CssParameter>                   
            </Stroke>                                      
          </PolygonSymbolizer>
        </Rule>    

        <!--Rule>
          <Title>RAS MILOPS AIRSPACE</Title>      
          <Abstract> RAS </Abstract>
         
          <ogc:Filter>     
            <ogc:And>
              <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>RAS</ogc:Literal>    
              </ogc:PropertyIsEqualTo>
              
              <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>CODEACTIVITY</ogc:PropertyName>
              <ogc:Literal>MILOPS</ogc:Literal>    
              </ogc:PropertyIsEqualTo>             
            </ogc:And>
          </ogc:Filter>
              
          <PolygonSymbolizer>  
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>circle</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#c333cc</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>1.5</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#c333cc</CssParameter>
           <CssParameter name="stroke-width">1</CssParameter>   
           </Stroke>           
          </PolygonSymbolizer>
        </Rule>     

        <Rule>
          <Title>RAS MILOPS AIRSPACE</Title>      
          <Abstract> RAS </Abstract>
         
          <ogc:Filter>     
            <ogc:And>
              <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>TYP</ogc:PropertyName>
              <ogc:Literal>RAS</ogc:Literal>    
              </ogc:PropertyIsEqualTo>
              
              <ogc:PropertyIsEqualTo>
              <ogc:PropertyName>CODEACTIVITY</ogc:PropertyName>
              <ogc:Literal>NATURE</ogc:Literal>    
              </ogc:PropertyIsEqualTo>             
            </ogc:And>
          </ogc:Filter>
              
          <PolygonSymbolizer>  
            <Stroke>           
              <GraphicStroke>            
                <Graphic>               
                  <Mark>               
                    <WellKnownName>circle</WellKnownName>             
                    <Stroke>            
                      <CssParameter name="stroke">#1eb429</CssParameter>                   
                      <CssParameter name="stroke-width">0.5</CssParameter>               
                    </Stroke>             
                  </Mark>             
                  <Size>1.5</Size>           
                </Graphic>         
              </GraphicStroke>
           <CssParameter name="stroke">#1eb429</CssParameter>
           <CssParameter name="stroke-width">1</CssParameter>   
           </Stroke>           
          </PolygonSymbolizer>
        </Rule-->     

      </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>