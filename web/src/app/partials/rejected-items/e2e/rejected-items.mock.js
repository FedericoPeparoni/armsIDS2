let rejectedItem = {
      id: 1,
      record_type: 'ATS_MOV',
      rejected_date_time: '2017-02-16T21:02:33Z',
      error_message: 'Data not valid',
      error_details: 'AtcMovementLog.flightId:may not be null; ',
      rejected_reason: 'The object is not validated',
      raw_text: '16-Feb-17,AAAA,BRITISH AIRWAYS,UG863/UM002,,B744,BAW,FAOR,EGLL,RUDAS,0105,320,MNV,1255,320,BUGRO,1615,320,H,sch,I',
      header: 'dateOfContact,registration,,route,flightId,aircraftType,operatorIcaoCode,departureAerodrome,destinationAerodrome,firEntryPoint,firEntryTime,,firMidPoint,firMidTime,,firExitPoint,firExitTime,,wakeTurbulence,flightCategory',
      json_text: null,
      status: 'uncorrected',
      originator: null,
      file_name: 'atc.txt'
    }

module.exports = [
    {
      request: {
          path: '/rejected-items',
          method: 'GET'
      },
      response: {
          data: { content: [rejectedItem] }
      }
    },
    {
      request: {
        path: '/rejected-items',
        method: 'POST',
      },
      response: {
        data: {content: [rejectedItem]}
      }
    },
    {
      request: {
        path: '/rejected-items',
        method: 'PUT',
      },
      response: {
        data: {content: [rejectedItem]}
      }
    },
    {
      request: {
        path: '/rejected-items/1',
        method: 'DELETE'
      },
      response: ''
    }
  ];
