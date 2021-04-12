<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version="1.0.0" xmlns="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc"
  xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd">
  <NamedLayer>
    <Name>V_AIRWAY_SEG</Name>
    <UserStyle>
      <Title>3px blue line</Title>
      <Abstract>Default line style for Airways, 3 pixel wide blue</Abstract>

      <FeatureTypeStyle>
        <Rule>
          <Title>Blue Line</Title>
          <Abstract>A 3 pixel wide blue line</Abstract>
          
          <LineSymbolizer>        
          <Stroke>
           <CssParameter name="stroke">#333333</CssParameter>
           <CssParameter name="stroke-width">3</CssParameter>
           <CssParameter name="stroke-linecap">round</CssParameter>
          </Stroke>        
      </LineSymbolizer>
      <LineSymbolizer>
       <Stroke>
           <CssParameter name="stroke">#6699FF</CssParameter>
           <CssParameter name="stroke-width">1</CssParameter>
           <CssParameter name="stroke-linecap">round</CssParameter>
         </Stroke>
      </LineSymbolizer>
        </Rule>
        
    <Rule>
      <Title>CONDITIONAL_AIRWAYS</Title>
      <Abstract>A RED LINE</Abstract>
      <ogc:Filter>
        <ogc:PropertyIsEqualTo>
          <ogc:PropertyName>AVBL</ogc:PropertyName>
          <ogc:Literal>COND</ogc:Literal>
        </ogc:PropertyIsEqualTo>  
      </ogc:Filter>  
      <LineSymbolizer>
        <Stroke>
          <CssParameter name="stroke">#DB6161</CssParameter>
        </Stroke>
      </LineSymbolizer>
    </Rule>        
      
    <Rule>
      <Title>CONDITIONAL,OPEN_AIRWAYS</Title>
      <Abstract>A RED LINE</Abstract>
      <ogc:Filter>
        <ogc:PropertyIsEqualTo>
          <ogc:PropertyName>AVBL</ogc:PropertyName>
          <ogc:Literal>COND,OPEN</ogc:Literal>
        </ogc:PropertyIsEqualTo>  
      </ogc:Filter>  
      <LineSymbolizer>
        <Stroke>
          <CssParameter name="stroke">#DB6161</CssParameter>
        </Stroke>
      </LineSymbolizer>
    </Rule>
    
<!-- some steps that need to be created for separation-->          
  
  <Rule>
    <Title>AIRWAY TXTDESIG LABEL WITH BACK_FORWARD ARROW</Title>    
    <Abstract>AIRWAY TXTDESIG LABEL</Abstract>
    
    <ogc:Filter>
      <ogc:PropertyIsEqualTo>    
        <ogc:PropertyName>DIRECTION</ogc:PropertyName>
        <ogc:Literal>BACKWARD,FORWARD</ogc:Literal>   
      </ogc:PropertyIsEqualTo>    
    </ogc:Filter>        
    
    <MaxScaleDenominator>6000000</MaxScaleDenominator>    
    
    <TextSymbolizer>   
  
      <Label>      
      <ogc:Literal>&#8592;</ogc:Literal> <![CDATA[ ]]>  
        <ogc:PropertyName>AIRWAY</ogc:PropertyName> <![CDATA[ ]]>
    <ogc:Literal>&#8594;</ogc:Literal>
        <!--<ogc:Literal>&#8596;</ogc:Literal>   B/F-->  
      </Label>      
      <LabelPlacement>
        <LinePlacement>
          <PerpendicularOffset>0</PerpendicularOffset>
        </LinePlacement>        
      </LabelPlacement>    
      <Halo>
        <Radius>3</Radius>    
        <Fill>
          <CssParameter name="fill">#0036a3</CssParameter>
        </Fill>        
      </Halo>
      <Fill>
        <CssParameter name="fill">#FFFFFF</CssParameter>
      </Fill>
      <VendorOption name="followLine">true</VendorOption>
    </TextSymbolizer>
  </Rule>

  <Rule>
    <Title>AIRWAY TXTDESIG LABEL WITH FORWARD ARROW</Title>    
    <Abstract>AIRWAY TXTDESIG LABEL</Abstract>
    
    <ogc:Filter>
      <ogc:PropertyIsEqualTo>    
        <ogc:PropertyName>DIRECTION</ogc:PropertyName>
        <ogc:Literal>FORWARD</ogc:Literal>   
      </ogc:PropertyIsEqualTo>    
    </ogc:Filter>        
    
    <MaxScaleDenominator>6000000</MaxScaleDenominator>    
    
    <TextSymbolizer>      
      <Label>      
        <ogc:PropertyName>AIRWAY</ogc:PropertyName> <![CDATA[  ]]>
        <ogc:Literal>&#8594;</ogc:Literal>     
      </Label>      
      <LabelPlacement>
        <LinePlacement>
          <PerpendicularOffset>0</PerpendicularOffset>
        </LinePlacement>        
      </LabelPlacement>    
      <Halo>
        <Radius>3</Radius>    
        <Fill>
          <CssParameter name="fill">#0036a3</CssParameter>
        </Fill>        
      </Halo>
      <Fill>
        <CssParameter name="fill">#FFFFFF</CssParameter>
      </Fill>
      <VendorOption name="followLine">true</VendorOption>     
    </TextSymbolizer>
  </Rule>        
        
  <Rule>
    <Title>AIRWAY TXTDESIG LABEL WITH BACKWARD ARROW</Title>    
    <Abstract>AIRWAY TXTDESIG LABEL</Abstract>
    
    <ogc:Filter>
      <ogc:PropertyIsEqualTo>    
        <ogc:PropertyName>DIRECTION</ogc:PropertyName>
        <ogc:Literal>BACKWARD</ogc:Literal>   
      </ogc:PropertyIsEqualTo>    
    </ogc:Filter>        
    
    <MaxScaleDenominator>6000000</MaxScaleDenominator>    
    
    <TextSymbolizer>      
      <Label>        
        <ogc:Literal>&#8592;</ogc:Literal> <![CDATA[  ]]>  
    <ogc:PropertyName>AIRWAY</ogc:PropertyName>    
      </Label>      
      <LabelPlacement>
        <LinePlacement>
          <PerpendicularOffset>0</PerpendicularOffset>
        </LinePlacement>        
      </LabelPlacement>    
      <Halo>
        <Radius>3</Radius>    
        <Fill>
          <CssParameter name="fill">#0036a3</CssParameter>
        </Fill>        
      </Halo>
      <Fill>
        <CssParameter name="fill">#FFFFFF</CssParameter>
      </Fill>
      <VendorOption name="followLine">true</VendorOption>   
    </TextSymbolizer>
       
  </Rule>        
             
<!-- some steps that need to be created for separation-->  
  <Rule>
    <Title>A CIRCLE STARTING POINT</Title>    
    <Abstract>A CIRCLE STARTING POINT</Abstract>            
          
    <MaxScaleDenominator>6000000</MaxScaleDenominator>
    
    <PointSymbolizer>            
      <Geometry>      
        <ogc:Function name="startPoint">        
          <ogc:PropertyName>GEOM</ogc:PropertyName>  
        </ogc:Function>
      </Geometry>

      <Graphic>
        <Mark>
          <WellKnownName>circle</WellKnownName>
          <Fill>
            <CssParameter name="fill">0xFF0000</CssParameter>
          </Fill>
        </Mark>
        <Size>3</Size>
      </Graphic>
    </PointSymbolizer>
  </Rule>
        
  <Rule>
    <Title>A CIRCLE ENDING POINT</Title>    
    <Abstract>A CIRCLE ENDING POINT</Abstract>            
              
    <MaxScaleDenominator>6000000</MaxScaleDenominator>
    
    <PointSymbolizer>
      <Geometry>
        <ogc:Function name="endPoint">
          <ogc:PropertyName>GEOM</ogc:PropertyName>
        </ogc:Function>
      </Geometry>
      <Graphic>
        <Mark>
          <WellKnownName>circle</WellKnownName>
          <Fill>
            <CssParameter name="fill">0xFF0000</CssParameter>
          </Fill>
        </Mark>
        <Size>4</Size>
      </Graphic>
    </PointSymbolizer>
  </Rule>
 

      </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>